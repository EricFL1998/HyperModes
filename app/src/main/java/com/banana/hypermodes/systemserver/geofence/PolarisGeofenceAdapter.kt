package com.banana.hypermodes.systemserver.geofence

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import com.banana.hypermodes.systemserver.trigger.GeofenceEvent
import com.xiaomi.gnss.polaris.IPolarisService

/**
 * Adapter for Xiaomi Polaris geofencing service.
 * Maintains desired and live fence state, performs reconciliation, and verifies callbacks.
 */
class PolarisGeofenceAdapter(
    private val context: Context,
    private val callback: (String, String, GeofenceEvent) -> Unit
) {
    private var geoService: PolarisGeoService? = null
    private var desiredById: Map<String, PolarisFenceSpec> = emptyMap()
    private val liveById = mutableMapOf<String, PolarisFenceSpec>()
    private var isBound = false
    private var registeredCallback = false
    private var pendingTriggers: List<Triple<String, String, com.banana.hypermodes.systemserver.config.ComplexTrigger.Location>>? = null

    companion object {
        private const val TAG = "PolarisGeofenceAdapter"
    }

    // Test constructor that injects PolarisGeoService
    internal constructor(
        context: Context,
        callback: (String, String, GeofenceEvent) -> Unit,
        service: PolarisGeoService
    ) : this(context, callback) {
        this.geoService = service
        this.isBound = true
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.i(TAG, "Polaris service connected")
            try {
                val polarisService = IPolarisService.Stub.asInterface(service)
                val geoManager = polarisService?.geoManagerService

                if (geoManager != null) {
                    geoService = AidlPolarisGeoService(geoManager)
                    isBound = true

                    // Register callback component first
                    registerCallbackComponent()

                    // Reconcile pending triggers
                    pendingTriggers?.let { triggers ->
                        Log.i(TAG, "Reconciling ${triggers.size} pending triggers")
                        reconcileFences(triggers)
                    }
                } else {
                    Log.e(TAG, "Failed to get GeoManagerService")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Exception in onServiceConnected", e)
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.i(TAG, "Polaris service disconnected")
            geoService = null
            isBound = false
        }

        override fun onBindingDied(name: ComponentName) {
            Log.e(TAG, "Polaris service binding died")
            geoService = null
            isBound = false
        }
    }

    init {
        if (isSupported()) {
            bindPolarisService()
        }
    }

    fun isSupported(): Boolean {
        return try {
            context.packageManager.getPackageInfo(
                PolarisContract.SERVICE_PACKAGE,
                0
            )
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun bindPolarisService() {
        try {
            // Create intent with ComponentName (Polaris requires explicit component)
            val intent = Intent().apply {
                component = ComponentName(
                    PolarisContract.SERVICE_PACKAGE,
                    PolarisContract.SERVICE_CLASS
                )
            }

            // CRITICAL: Must startService first, then bindService (same as SecurityCenter's AutoTask)
            // Use reflection to call startServiceAsUser
            val startServiceMethod = Context::class.java.getDeclaredMethod(
                "startServiceAsUser",
                Intent::class.java,
                android.os.UserHandle::class.java
            )

            val userHandleConstructor = android.os.UserHandle::class.java
                .getDeclaredConstructor(Int::class.javaPrimitiveType)
            val user0 = userHandleConstructor.newInstance(0) as android.os.UserHandle

            // Start service first
            val componentName = startServiceMethod.invoke(context, intent, user0) as? ComponentName
            Log.i(TAG, "startServiceAsUser result: $componentName")

            // Then bind to it
            val bindServiceMethod = Context::class.java.getDeclaredMethod(
                "bindServiceAsUser",
                Intent::class.java,
                ServiceConnection::class.java,
                Int::class.javaPrimitiveType,
                android.os.UserHandle::class.java
            )

            val bound = bindServiceMethod.invoke(
                context,
                intent,
                serviceConnection,
                Context.BIND_AUTO_CREATE,
                user0
            ) as Boolean

            Log.i(TAG, "bindServiceAsUser result: $bound (package=${PolarisContract.SERVICE_PACKAGE}, user=0)")
        } catch (e: Exception) {
            Log.e(TAG, "Exception binding service", e)
        }
    }

    private fun registerCallbackComponent() {
        try {
            val component = ComponentName(
                PolarisContract.CALLBACK_PACKAGE,
                PolarisCallbackReceiver::class.java.name
            )
            geoService?.registerComponent(component)
            Log.i(TAG, "Registered callback component")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register callback", e)
        }
    }

    fun reconcile(triggers: List<Triple<String, String, com.banana.hypermodes.systemserver.config.ComplexTrigger.Location>>) {
        if (!isBound || geoService == null) {
            Log.i(TAG, "Service not ready, deferring reconcile")
            pendingTriggers = triggers
            return
        }
        reconcileFences(triggers)
    }

    private fun reconcileFences(triggers: List<Triple<String, String, com.banana.hypermodes.systemserver.config.ComplexTrigger.Location>>) {
        val service = geoService ?: return

        // Register callback component first (once)
        if (!registeredCallback) {
            registerCallbackComponent()
            registeredCallback = true
        }

        // Build desired state
        val desired = triggers.map { (modeId, triggerId, location) ->
            PolarisFenceSpec(
                fenceId = "${PolarisContract.FENCE_PREFIX}${modeId}_$triggerId",
                modeId = modeId,
                triggerId = triggerId,
                latitude = location.latitude,
                longitude = location.longitude,
                radiusMeters = location.radius,
                transitionType = PolarisContract.TRANSITION_BOTH,
                confidence = PolarisContract.CONFIDENCE_HIGH
            )
        }

        desiredById = desired.associateBy { it.fenceId }

        // List remote fences
        val remote = service.list()

        // Compute operations
        val operations = PolarisFenceReconciler.plan(desired, remote)

        Log.i(TAG, "Reconciliation: ${operations.size} operations")

        // Execute operations
        operations.forEach { op ->
            when (op) {
                is PolarisFenceOperation.Add -> {
                    val result = service.add(op.fence)
                    if (result == op.fence.fenceId && result.isNotBlank()) {
                        liveById[op.fence.fenceId] = op.fence
                        Log.i(TAG, "Added fence: ${op.fence.fenceId}")
                    }
                }
                is PolarisFenceOperation.Update -> {
                    service.update(op.fence)
                    val remote = service.findById(op.fence.fenceId)
                    if (remote != null && remote.matches(op.fence)) {
                        liveById[op.fence.fenceId] = op.fence
                        Log.i(TAG, "Updated fence: ${op.fence.fenceId}")
                    }
                }
                is PolarisFenceOperation.Delete -> {
                    service.deleteById(op.fenceId)
                    liveById.remove(op.fenceId)
                    Log.i(TAG, "Deleted fence: ${op.fenceId}")
                }
                is PolarisFenceOperation.Keep -> {
                    liveById[op.fence.fenceId] = op.fence
                }
            }
        }

        // Query status for all live desired fences
        liveById.values.forEach { fence ->
            val status = service.status(fence.fenceId)
            when (status) {
                PolarisContract.STATUS_IN -> {
                    callback(fence.modeId, fence.triggerId, GeofenceEvent.ENTER)
                }
                PolarisContract.STATUS_OUT -> {
                    callback(fence.modeId, fence.triggerId, GeofenceEvent.EXIT)
                }
                // STATUS_UNKNOWN or error: emit nothing
            }
        }

        Log.i(TAG, "Reconcile complete: ${liveById.size} live fences")
    }

    fun handleGeofenceEvent(fenceId: String, event: Int) {
        val payload = PolarisContract.parseCallback(fenceId, event) ?: return
        val expected = liveById[payload.fenceId] ?: return
        val remote = geoService?.findById(payload.fenceId) ?: return
        if (!remote.matches(expected)) return

        callback(
            expected.modeId,
            expected.triggerId,
            if (event == PolarisContract.EVENT_ENTER) GeofenceEvent.ENTER else GeofenceEvent.EXIT
        )
    }

    fun release() {
        try {
            if (isBound) {
                geoService?.registerComponent(null)
                context.unbindService(serviceConnection)
                isBound = false
                Log.i(TAG, "Polaris service unbound")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to unbind service", e)
        }
        desiredById = emptyMap()
        liveById.clear()
    }

    private fun PolarisRemoteFence.matches(spec: PolarisFenceSpec): Boolean {
        return fenceId == spec.fenceId &&
            latitude == spec.latitude &&
            longitude == spec.longitude &&
            radiusMeters == spec.radiusMeters &&
            transitionType == spec.transitionType &&
            confidence == spec.confidence &&
            packageName == PolarisContract.CLIENT_PACKAGE
    }
}
