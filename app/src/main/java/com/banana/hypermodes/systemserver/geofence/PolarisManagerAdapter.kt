package com.banana.hypermodes.systemserver.geofence

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.systemserver.trigger.GeofenceEvent
import com.xiaomi.gnss.polaris.geofence.MiGeofence
import com.xiaomi.gnss.polaris.sdk.PolarisManager
import com.xiaomi.gnss.polaris.sdk.exception.PolarisException
import com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService

/**
 * PolarisManager SDK-based geofence adapter.
 * Uses the official Xiaomi SDK for reliable service connection and management.
 * Receives events via PolarisCallbackReceiver broadcast.
 */
class PolarisManagerAdapter(
    private val context: Context,
    private val callback: (String, String, GeofenceEvent) -> Unit
) {
    private var polarisManager: PolarisManager? = null
    private var geofenceService: PolarisGeofenceService? = null
    private var desiredById: Map<String, PolarisFenceSpec> = emptyMap()
    private val liveById = mutableMapOf<String, PolarisFenceSpec>()
    private var isConnected = false
    private var registeredCallback = false
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        private const val TAG = "PolarisManagerAdapter"
    }

    // Public method for PolarisCallbackReceiver to call
    fun onGeofenceEvent(modeId: String, triggerId: String, event: GeofenceEvent) {
        handler.post {
            callback(modeId, triggerId, event)
        }
    }

    fun init() {
        try {
            Log.i(TAG, "Initializing PolarisManager SDK")

            // CRITICAL: We're running in system_server, but need to start Polaris service
            // as our own app package. Create a package context to get the correct UID.
            val appContext = try {
                context.createPackageContext(
                    "com.banana.hypermodes",
                    Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
                )
            } catch (e: Exception) {
                Log.e(TAG, "Failed to create package context", e)
                return
            }

            polarisManager = PolarisManager.getInstance(appContext)

            // Check if Polaris is supported
            if (!polarisManager!!.isPolarisSupport()) {
                Log.e(TAG, "Polaris is not supported on this device")
                return
            }

            if (!polarisManager!!.isSubServiceSupport(PolarisManager.ServiceType.Geofence)) {
                Log.e(TAG, "Polaris Geofence service is not supported")
                return
            }

            // Connect to Polaris service synchronously
            Log.i(TAG, "Connecting to Polaris service...")
            polarisManager!!.connectPolarisServiceSync()

            // Get geofence sub-service
            geofenceService = polarisManager!!.getSubService(PolarisManager.ServiceType.Geofence) as? PolarisGeofenceService

            if (geofenceService == null) {
                Log.e(TAG, "Failed to get PolarisGeofenceService")
                return
            }

            Log.i(TAG, "PolarisManager connected successfully")
            isConnected = true

            // Register callback
            registerCallback()

            // Reconcile any pending fences
            reconcile()

        } catch (e: PolarisException) {
            Log.e(TAG, "PolarisException during init", e)
        } catch (e: Exception) {
            Log.e(TAG, "Exception during init", e)
        }
    }

    private fun registerCallback() {
        if (!isConnected || registeredCallback) {
            return
        }

        try {
            // Register our ComponentName for receiving broadcasts
            val componentName = android.content.ComponentName(
                context.packageName,
                "com.banana.hypermodes.systemserver.geofence.PolarisCallbackReceiver"
            )
            geofenceService?.registerComponent(componentName)
            registeredCallback = true
            Log.i(TAG, "Registered geofence callback component: $componentName")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register callback", e)
        }
    }

    fun updateTriggers(triggers: List<Triple<String, String, com.banana.hypermodes.systemserver.config.ComplexTrigger.Location>>) {
        Log.i(TAG, "updateTriggers called with ${triggers.size} triggers")

        desiredById = triggers.associate { (modeId, triggerId, config) ->
            val fenceId = "hypermodes_custom_${System.currentTimeMillis()}_$triggerId"
            fenceId to PolarisFenceSpec(
                fenceId = fenceId,
                modeId = modeId,
                triggerId = triggerId,
                latitude = config.latitude,
                longitude = config.longitude,
                radiusMeters = config.radius,
                transitionType = when (config.transition) {
                    "ARRIVE" -> MiGeofence.TRANSITION_TYPE_ENTER
                    "LEAVE" -> MiGeofence.TRANSITION_TYPE_EXIT
                    else -> MiGeofence.TRANSITION_TYPE_ENTER
                },
                confidence = MiGeofence.CONFIDENCE_HIGH
            )
        }

        reconcile()
    }

    private fun reconcile() {
        if (!isConnected) {
            Log.i(TAG, "Service not ready, deferring reconcile")
            return
        }

        try {
            val toAdd = desiredById.filterKeys { it !in liveById.keys }.values
            val toRemove = liveById.filterKeys { it !in desiredById.keys }.keys

            Log.i(TAG, "Reconciliation: ${toAdd.size} to add, ${toRemove.size} to remove")

            // Remove old fences
            toRemove.forEach { fenceId ->
                try {
                    geofenceService?.deleteGeofence(fenceId)
                    liveById.remove(fenceId)
                    Log.i(TAG, "Removed fence: $fenceId")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to remove fence: $fenceId", e)
                }
            }

            // Add new fences
            toAdd.forEach { spec ->
                try {
                    val miGeofence = spec.toMiGeofence()
                    geofenceService?.addGeofence(miGeofence)
                    liveById[spec.fenceId] = spec
                    Log.i(TAG, "Added fence: ${spec.fenceId}")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to add fence: ${spec.fenceId}", e)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Reconciliation failed", e)
        }
    }

    fun cleanup() {
        try {
            if (registeredCallback) {
                geofenceService?.unregisterComponent()
                registeredCallback = false
            }

            // Remove all fences
            liveById.keys.toList().forEach { fenceId ->
                try {
                    geofenceService?.deleteGeofence(fenceId)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to remove fence during cleanup: $fenceId", e)
                }
            }
            liveById.clear()

            // Note: PolarisManager SDK doesn't have a disconnect method
            // The service connection is managed by the SDK
            isConnected = false
            Log.i(TAG, "Cleanup completed")
        } catch (e: Exception) {
            Log.e(TAG, "Cleanup failed", e)
        }
    }

    private fun PolarisFenceSpec.toMiGeofence(): MiGeofence {
        return MiGeofence().apply {
            setId(fenceId)
            setLatitude(this@toMiGeofence.latitude)
            setLongitude(this@toMiGeofence.longitude)
            setRadius(radiusMeters)
            setTransitionType(this@toMiGeofence.transitionType)
            setConfidence(this@toMiGeofence.confidence)
        }
    }
}
