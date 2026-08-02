package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import com.banana.hypermodes.protocol.Protocol

/**
 * On-device test utility for Polaris geofencing probe.
 *
 * This utility invokes the system_server bridge to run the PolarisGeofenceAdapter
 * capability probe and captures diagnostic results. Use this to validate whether
 * Xiaomi Polaris geofencing is available on the target device before implementing
 * location trigger UI/persistence.
 *
 * Usage:
 * ```
 * PolarisProbeTestUtil.runProbe(context) { result ->
 *     Log.d(TAG, "Probe result: $result")
 *     // Display result to user or save to log file
 * }
 * ```
 */
object PolarisProbeTestUtil {

    private const val TAG = "PolarisProbeTest"
    private const val PROBE_TIMEOUT_MS = 10000L

    /**
     * Run the Polaris capability probe via system_server bridge.
     * Results are delivered asynchronously via callback.
     *
     * @param context Application context
     * @param onResult Callback invoked with probe result (on main thread)
     */
    fun runProbe(context: Context, onResult: (ProbeResult) -> Unit) {
        Log.d(TAG, "Initiating Polaris capability probe")

        val resultReceiver = object : ResultReceiver(Handler(Looper.getMainLooper())) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (resultData == null) {
                    Log.e(TAG, "Probe returned null result bundle")
                    onResult(ProbeResult.Error("Null result bundle"))
                    return
                }

                val result = parseProbeResult(resultData)
                Log.d(TAG, "Probe completed: $result")
                onResult(result)
            }
        }

        try {
            val intent = Intent(Protocol.ACTION_PROBE_POLARIS).apply {
                putExtra(Protocol.EXTRA_RESULT_RECEIVER, resultReceiver)
            }

            context.sendBroadcast(intent, Protocol.PERMISSION_CONTROL)
            Log.d(TAG, "Probe request sent to system_server")

            // Timeout handler
            Handler(Looper.getMainLooper()).postDelayed({
                Log.w(TAG, "Probe timeout (${PROBE_TIMEOUT_MS}ms)")
                onResult(ProbeResult.Error("Probe timeout - system_server bridge may not be active"))
            }, PROBE_TIMEOUT_MS)

        } catch (e: Exception) {
            Log.e(TAG, "Failed to send probe request: ${e.message}", e)
            onResult(ProbeResult.Error("Failed to send probe request: ${e.message}"))
        }
    }

    /**
     * Parse Bundle result from PolarisGeofenceAdapter.getCapabilityReport()
     */
    private fun parseProbeResult(bundle: Bundle): ProbeResult {
        val resultType = bundle.getString("result_type", "Unknown")
        val supported = bundle.getBoolean("supported", false)
        val message = bundle.getString("message", "No message")
        val failureReason = bundle.getString("failure_reason")

        return if (supported) {
            ProbeResult.Supported(message)
        } else {
            ProbeResult.Unsupported(
                resultType = resultType,
                failureReason = failureReason,
                message = message
            )
        }
    }

    /**
     * Structured probe result for on-device testing.
     */
    sealed class ProbeResult {
        /** Polaris is available and accessible. Location triggers can proceed. */
        data class Supported(val message: String) : ProbeResult() {
            override fun toString() = "✓ SUPPORTED: $message"
        }

        /** Polaris is unavailable or refuses non-SecurityCenter callers.
         *  Location triggers MUST NOT be implemented. */
        data class Unsupported(
            val resultType: String,
            val failureReason: String?,
            val message: String
        ) : ProbeResult() {
            override fun toString() = "✗ UNSUPPORTED ($resultType): $message"
        }

        /** Probe execution error (bridge not responding, exception, etc.). */
        data class Error(val message: String) : ProbeResult() {
            override fun toString() = "⚠ ERROR: $message"
        }
    }

    /**
     * Generate a detailed diagnostic report string suitable for logging or display.
     */
    fun formatDiagnosticReport(result: ProbeResult): String {
        return buildString {
            appendLine("═══════════════════════════════════════════════════")
            appendLine("    Polaris Geofencing Capability Probe Report")
            appendLine("═══════════════════════════════════════════════════")
            appendLine()

            when (result) {
                is ProbeResult.Supported -> {
                    appendLine("Status: ✓ SUPPORTED")
                    appendLine("Message: ${result.message}")
                    appendLine()
                    appendLine("Decision: Location triggers MAY proceed to implementation.")
                    appendLine("Next steps:")
                    appendLine("  1. Implement location trigger UI")
                    appendLine("  2. Add geofence registration logic")
                    appendLine("  3. Test actual fence activation on device")
                }

                is ProbeResult.Unsupported -> {
                    appendLine("Status: ✗ UNSUPPORTED")
                    appendLine("Result Type: ${result.resultType}")
                    if (result.failureReason != null) {
                        appendLine("Failure Reason: ${result.failureReason}")
                    }
                    appendLine("Message: ${result.message}")
                    appendLine()
                    appendLine("Decision: Location triggers MUST NOT be implemented.")
                    appendLine("Blocker: Polaris service is unavailable or refuses")
                    appendLine("         non-SecurityCenter callers.")
                    appendLine()
                    appendLine("Recommended action:")
                    appendLine("  - Mark location triggers as 'unsupported' in UI")
                    appendLine("  - Document device/ROM incompatibility")
                    appendLine("  - Consider alternative location APIs if critical")
                }

                is ProbeResult.Error -> {
                    appendLine("Status: ⚠ PROBE ERROR")
                    appendLine("Message: ${result.message}")
                    appendLine()
                    appendLine("Decision: Cannot determine support status.")
                    appendLine("Troubleshooting:")
                    appendLine("  1. Verify LSPosed module is active")
                    appendLine("  2. Check system_server hook installation")
                    appendLine("  3. Review system_server logs for bridge errors")
                    appendLine("  4. Retry probe after module restart")
                }
            }

            appendLine()
            appendLine("═══════════════════════════════════════════════════")
        }
    }
}
