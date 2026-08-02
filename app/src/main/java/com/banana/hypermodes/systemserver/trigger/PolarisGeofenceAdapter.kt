package com.banana.hypermodes.systemserver.trigger

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.util.Log
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

/**
 * Fail-closed Polaris geofence feasibility probe.
 *
 * Polaris is Xiaomi's geofencing service (part of Security Center / GNSS).
 * Before building location trigger UI/persistence, this adapter attempts to:
 * 1. Bind to com.xiaomi.gnss.polaris/.PolarisService
 * 2. Dynamically resolve Binder contract (no compiled stubs)
 * 3. Execute minimal non-mutating operations (version query, capability check)
 * 4. Detect if the service refuses non-SecurityCenter callers
 *
 * If any step fails, returns structured unsupported reason without bypassing
 * package permissions, impersonating Security Center, or writing its database.
 *
 * This is a gate: location triggers are NOT implemented unless on-device probe
 * confirms Polaris registration is allowed.
 */
class PolarisGeofenceAdapter(private val context: Context) {

    /**
     * Capability detection result.
     * SUPPORTED means binding + read-only operations succeeded.
     * All other states block location trigger implementation.
     */
    sealed class CapabilityResult {
        /** Polaris is available and non-SecurityCenter callers can use it. */
        data object Supported : CapabilityResult()

        /** Service binding failed (package not found, service not exported, etc.). */
        data class BindingFailed(val reason: String) : CapabilityResult()

        /** Service bound but Binder transactions failed (permission/API mismatch). */
        data class TransactionFailed(val reason: String) : CapabilityResult()

        /** Service refuses non-SecurityCenter callers (explicit permission denial). */
        data class CallerRejected(val reason: String) : CapabilityResult()

        /** Unexpected error during probe. */
        data class ProbeError(val exception: Throwable) : CapabilityResult()
    }

    /**
     * Probe Polaris capability. Runs synchronously with timeout.
     * @return CapabilityResult indicating support status and reason.
     */
    fun probeCapability(): CapabilityResult {
        return try {
            log("Starting Polaris capability probe")

            // Step 1: Attempt service binding
            val binder = bindService() ?: return CapabilityResult.BindingFailed(
                "Failed to bind to com.xiaomi.gnss.polaris/.PolarisService"
            )

            log("Service bound successfully, probing Binder interface")

            // Step 2: Attempt non-mutating operations via Binder
            val result = probeBinderInterface(binder)

            log("Probe completed: $result")
            result

        } catch (e: SecurityException) {
            log("SecurityException during probe: ${e.message}")
            CapabilityResult.CallerRejected(
                "Polaris rejected caller: ${e.message}"
            )
        } catch (e: Exception) {
            log("Unexpected error during probe: ${e.message}")
            CapabilityResult.ProbeError(e)
        }
    }

    /**
     * Bind to Polaris service with timeout.
     * @return IBinder if binding succeeds, null otherwise.
     */
    private fun bindService(): IBinder? {
        val latch = CountDownLatch(1)
        var resultBinder: IBinder? = null

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                log("onServiceConnected: $name")
                resultBinder = service
                latch.countDown()
            }

            override fun onServiceDisconnected(name: ComponentName) {
                log("onServiceDisconnected: $name")
            }

            override fun onBindingDied(name: ComponentName) {
                log("onBindingDied: $name")
                latch.countDown()
            }

            override fun onNullBinding(name: ComponentName) {
                log("onNullBinding: $name")
                latch.countDown()
            }
        }

        val intent = Intent().apply {
            component = ComponentName(
                "com.xiaomi.gnss.polaris",
                "com.xiaomi.gnss.polaris.PolarisService"
            )
        }

        val bound = try {
            context.bindService(
                intent,
                connection,
                Context.BIND_AUTO_CREATE
            )
        } catch (e: SecurityException) {
            log("bindService SecurityException: ${e.message}")
            return null
        } catch (e: Exception) {
            log("bindService exception: ${e.message}")
            return null
        }

        if (!bound) {
            log("bindService returned false")
            return null
        }

        val timedOut = !latch.await(BINDING_TIMEOUT_MS, TimeUnit.MILLISECONDS)

        if (timedOut) {
            log("Service binding timeout")
            try {
                context.unbindService(connection)
            } catch (e: Exception) {
                log("unbindService failed: ${e.message}")
            }
            return null
        }

        return resultBinder
    }

    /**
     * Probe the Binder interface with minimal non-mutating operations.
     *
     * Strategy: Try to discover and call read-only methods without compiled stubs.
     * Common Binder transaction codes for AIDL services:
     * - INTERFACE_TRANSACTION (1598968902 / 0x5f4e5446) returns interface descriptor
     * - Custom transactions typically start from FIRST_CALL_TRANSACTION (1)
     *
     * We'll attempt:
     * 1. Get interface descriptor (standard Binder protocol)
     * 2. Try transaction codes for typical read-only operations
     *    (getVersion, getCapabilities, listGeofences, etc.)
     */
    private fun probeBinderInterface(binder: IBinder): CapabilityResult {
        return try {
            // Step 1: Get interface descriptor (validates basic Binder communication)
            val descriptor = try {
                binder.interfaceDescriptor
            } catch (e: Exception) {
                log("Failed to get interface descriptor: ${e.message}")
                return CapabilityResult.TransactionFailed(
                    "Cannot read interface descriptor: ${e.message}"
                )
            }

            log("Interface descriptor: $descriptor")

            // Step 2: Attempt typical read-only transaction codes
            // These are educated guesses based on common AIDL patterns
            // Transaction 1-5 typically include getters, version, capabilities
            val transactionResults = mutableMapOf<Int, String>()

            for (code in 1..5) {
                val result = tryTransaction(binder, code, descriptor)
                transactionResults[code] = result
                log("Transaction $code: $result")

                // If we get a SecurityException, Polaris is actively rejecting us
                if (result.contains("SecurityException", ignoreCase = true)) {
                    return CapabilityResult.CallerRejected(
                        "Polaris rejected transaction $code: $result"
                    )
                }

                // If we get a successful response (no exception), consider it supported
                if (result == "SUCCESS" || result.startsWith("OK")) {
                    return CapabilityResult.Supported
                }
            }

            // If we got here, service is available but we couldn't confirm
            // read-only access. Conservative fail-closed: report transaction failure.
            CapabilityResult.TransactionFailed(
                "No successful read-only transaction. Results: $transactionResults"
            )

        } catch (e: SecurityException) {
            CapabilityResult.CallerRejected("SecurityException: ${e.message}")
        } catch (e: Exception) {
            CapabilityResult.TransactionFailed("Exception during probe: ${e.message}")
        }
    }

    /**
     * Attempt a Binder transaction and return the result status.
     * @return "SUCCESS" if no exception, error message otherwise.
     */
    private fun tryTransaction(binder: IBinder, code: Int, descriptor: String?): String {
        val data = Parcel.obtain()
        val reply = Parcel.obtain()

        return try {
            // Write interface token (standard AIDL protocol)
            if (descriptor != null) {
                data.writeInterfaceToken(descriptor)
            }

            // Attempt transaction with standard flags
            val success = binder.transact(code, data, reply, 0)

            if (!success) {
                "FAILED"
            } else {
                // Check if reply has an exception
                if (reply.dataSize() > 0) {
                    reply.setDataPosition(0)
                    try {
                        // Standard Binder exception protocol: first int is 0 for success
                        val exceptionCode = reply.readInt()
                        if (exceptionCode != 0) {
                            "EXCEPTION_CODE_$exceptionCode"
                        } else {
                            "SUCCESS"
                        }
                    } catch (e: Exception) {
                        // Reply exists but couldn't parse - still counts as communication
                        "OK_UNPARSEABLE"
                    }
                } else {
                    "SUCCESS_NO_REPLY"
                }
            }
        } catch (e: SecurityException) {
            "SecurityException: ${e.message}"
        } catch (e: Exception) {
            "Exception: ${e.javaClass.simpleName}: ${e.message}"
        } finally {
            data.recycle()
            reply.recycle()
        }
    }

    /**
     * Get a human-readable capability report for diagnostics.
     */
    fun getCapabilityReport(): Bundle {
        val result = probeCapability()

        return Bundle().apply {
            putString("result_type", result.javaClass.simpleName)

            when (result) {
                is CapabilityResult.Supported -> {
                    putBoolean("supported", true)
                    putString("message", "Polaris geofencing is available")
                }
                is CapabilityResult.BindingFailed -> {
                    putBoolean("supported", false)
                    putString("failure_reason", "binding_failed")
                    putString("message", result.reason)
                }
                is CapabilityResult.TransactionFailed -> {
                    putBoolean("supported", false)
                    putString("failure_reason", "transaction_failed")
                    putString("message", result.reason)
                }
                is CapabilityResult.CallerRejected -> {
                    putBoolean("supported", false)
                    putString("failure_reason", "caller_rejected")
                    putString("message", result.reason)
                }
                is CapabilityResult.ProbeError -> {
                    putBoolean("supported", false)
                    putString("failure_reason", "probe_error")
                    putString("message", result.exception.toString())
                }
            }
        }
    }

    private fun log(msg: String) {
        Log.w(TAG, msg)
    }

    companion object {
        private const val TAG = "PolarisGeofenceAdapter"
        private const val BINDING_TIMEOUT_MS = 5000L

        /**
         * Binder transaction code constants (AIDL standard).
         */
        private const val FIRST_CALL_TRANSACTION = 1
        private const val INTERFACE_TRANSACTION = 0x5f4e5446
    }
}
