package com.banana.hypermodes.systemserver.geofence

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.banana.hypermodes.proxy.PolarisProxyContract
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.trigger.GeofenceEvent
import com.xiaomi.gnss.polaris.geofence.MiGeofence

/**
 * Client for PolarisProxyProvider.
 * Calls ContentProvider methods from system_server to manage geofences in app process.
 * 
 * Thread-safe implementation with proper connection state caching.
 */
class PolarisProxyClient(
    private val context: Context,
    private val callback: (String, String, GeofenceEvent) -> Unit
) {
    private val contentUri = Uri.parse("content://${PolarisProxyContract.AUTHORITY}")
    private var desiredById: Map<String, PolarisFenceSpec> = emptyMap()
    private var liveById: Map<String, PolarisFenceSpec> = emptyMap()
    
    // Connection state cache
    private var cachedConnectionState = false
    private var lastConnectionCheck = 0L
    private var connectionCheckCount = 0
    
    private val lock = Any()

    companion object {
        private const val TAG = "PolarisProxyClient"
        private const val CONNECTION_CHECK_CACHE_MS = 1000L
        
        /**
         * Parse fence ID back to modeId and triggerId.
         * Returns null if format is invalid.
         */
        fun parseFenceId(fenceId: String): Pair<String, String>? {
            if (!fenceId.startsWith(PolarisContract.FENCE_PREFIX)) return null
            
            val withoutPrefix = fenceId.substring(PolarisContract.FENCE_PREFIX.length)
            val lastUnderscore = withoutPrefix.lastIndexOf('_')
            
            if (lastUnderscore == -1) return null
            
            val modeId = withoutPrefix.substring(0, lastUnderscore)
            val triggerId = withoutPrefix.substring(lastUnderscore + 1)
            
            return modeId to triggerId
        }
    }

    fun init() {
        Log.i(TAG, "Initializing Polaris via ContentProvider (system_server -> app process)")
        
        synchronized(lock) {
            connectionCheckCount++
        }

        val result = try {
            context.contentResolver.call(
                contentUri,
                PolarisProxyContract.METHOD_INIT,
                null,
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call init method on ContentProvider", e)
            Log.e(TAG, "  This usually means:")
            Log.e(TAG, "  - App process is not running")
            Log.e(TAG, "  - ContentProvider not registered in manifest")
            Log.e(TAG, "  - Permission denied")
            return
        }

        val success = result?.getBoolean(PolarisProxyContract.RESULT_SUCCESS) == true
        val errorMsg = result?.getString(PolarisProxyContract.RESULT_ERROR_MSG)

        if (success) {
            Log.i(TAG, "✓ Polaris initialized successfully")
            if (errorMsg != null) {
                Log.i(TAG, "  Details: $errorMsg")
            }
            synchronized(lock) {
                cachedConnectionState = true
                lastConnectionCheck = System.currentTimeMillis()
            }
        } else {
            Log.e(TAG, "✗ Failed to initialize Polaris")
            if (errorMsg != null) {
                Log.e(TAG, "  Error: $errorMsg")
            } else {
                Log.e(TAG, "  Error: Unknown (no error message in result)")
            }
            synchronized(lock) {
                cachedConnectionState = false
                lastConnectionCheck = System.currentTimeMillis()
            }
        }
    }

    fun updateTriggers(triggers: List<Triple<String, String, ComplexTrigger.Location>>) {
        Log.i(TAG, "updateTriggers called with ${triggers.size} trigger(s)")

        // Build desired state
        val newDesired = triggers.associate { (modeId, triggerId, config) ->
            val fenceId = buildFenceId(modeId, triggerId)
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

        synchronized(lock) {
            desiredById = newDesired
        }

        if (!isConnected()) {
            Log.w(TAG, "Cannot update triggers: Polaris not connected")
            Log.w(TAG, "Desired state saved with ${desiredById.size} fence(s), will reconcile when connected")
            return
        }

        reconcile()
    }

    private fun reconcile() {
        val (desired, live) = synchronized(lock) {
            desiredById to liveById
        }
        
        Log.i(TAG, "Reconciling geofences: desired=${desired.size}, live=${live.size}")

        // Fences to remove (in live but not in desired)
        val toRemove = live.keys - desired.keys
        toRemove.forEach { fenceId ->
            Log.i(TAG, "  Removing fence: $fenceId")
            removeGeofence(fenceId)
        }

        // Fences to add (in desired but not in live)
        val toAdd = desired.keys - live.keys
        toAdd.forEach { fenceId ->
            val spec = desired[fenceId]!!
            Log.i(TAG, "  Adding fence: $fenceId")
            addGeofence(spec)
        }

        // Fences that exist in both: check if spec changed
        val common = desired.keys.intersect(live.keys)
        common.forEach { fenceId ->
            val desiredSpec = desired[fenceId]!!
            val liveSpec = live[fenceId]!!
            if (desiredSpec != liveSpec) {
                Log.i(TAG, "  Updating fence (remove+add): $fenceId")
                removeGeofence(fenceId)
                addGeofence(desiredSpec)
            }
        }
    }

    private fun addGeofence(spec: PolarisFenceSpec) {
        val extras = Bundle().apply {
            putString(PolarisProxyContract.PARAM_FENCE_ID, spec.fenceId)
            putString(PolarisProxyContract.PARAM_MODE_ID, spec.modeId)
            putString(PolarisProxyContract.PARAM_TRIGGER_ID, spec.triggerId)
            putDouble(PolarisProxyContract.PARAM_LATITUDE, spec.latitude)
            putDouble(PolarisProxyContract.PARAM_LONGITUDE, spec.longitude)
            putInt(PolarisProxyContract.PARAM_RADIUS, spec.radiusMeters)
            putInt(PolarisProxyContract.PARAM_TRANSITION_TYPE, spec.transitionType)
            putInt(PolarisProxyContract.PARAM_CONFIDENCE, spec.confidence)
        }

        val result = try {
            context.contentResolver.call(
                contentUri,
                PolarisProxyContract.METHOD_ADD_GEOFENCE,
                null,
                extras
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call add_geofence for ${spec.fenceId}", e)
            return
        }

        if (result?.getBoolean(PolarisProxyContract.RESULT_SUCCESS) == true) {
            synchronized(lock) {
                liveById = liveById + (spec.fenceId to spec)
            }
            Log.i(TAG, "✓ Added geofence: ${spec.fenceId}")
        } else {
            val error = result?.getString(PolarisProxyContract.RESULT_ERROR_MSG) ?: "Unknown error"
            Log.e(TAG, "✗ Failed to add geofence ${spec.fenceId}: $error")
        }
    }

    private fun removeGeofence(fenceId: String) {
        val extras = Bundle().apply {
            putString(PolarisProxyContract.PARAM_FENCE_ID, fenceId)
        }

        val result = try {
            context.contentResolver.call(
                contentUri,
                PolarisProxyContract.METHOD_REMOVE_GEOFENCE,
                null,
                extras
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call remove_geofence for $fenceId", e)
            return
        }

        if (result?.getBoolean(PolarisProxyContract.RESULT_SUCCESS) == true) {
            synchronized(lock) {
                liveById = liveById - fenceId
            }
            Log.i(TAG, "✓ Removed geofence: $fenceId")
        } else {
            val error = result?.getString(PolarisProxyContract.RESULT_ERROR_MSG) ?: "Unknown error"
            Log.e(TAG, "✗ Failed to remove geofence $fenceId: $error")
        }
    }

    fun cleanup() {
        val currentLive = synchronized(lock) { liveById.size }
        Log.i(TAG, "Cleaning up: removing all $currentLive geofence(s)")

        val result = try {
            context.contentResolver.call(
                contentUri,
                PolarisProxyContract.METHOD_CLEAR_ALL,
                null,
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to call clear_all", e)
            null
        }

        if (result?.getBoolean(PolarisProxyContract.RESULT_SUCCESS) == true) {
            synchronized(lock) {
                liveById = emptyMap()
            }
            Log.i(TAG, "✓ Cleared all geofences")
        } else {
            val error = result?.getString(PolarisProxyContract.RESULT_ERROR_MSG) ?: "Unknown error"
            Log.e(TAG, "✗ Failed to clear all geofences: $error")
        }

        synchronized(lock) {
            desiredById = emptyMap()
        }
    }

    fun isConnected(): Boolean {
        val now = System.currentTimeMillis()
        
        // Check cache first
        synchronized(lock) {
            if (now - lastConnectionCheck < CONNECTION_CHECK_CACHE_MS) {
                return cachedConnectionState
            }
        }

        // Cache expired, query actual state
        val result = try {
            context.contentResolver.call(
                contentUri,
                PolarisProxyContract.METHOD_IS_CONNECTED,
                null,
                null
            )
        } catch (e: Exception) {
            Log.e(TAG, "ContentProvider call 'is_connected' failed: ${e.message}")
            synchronized(lock) {
                cachedConnectionState = false
                lastConnectionCheck = now
            }
            return false
        }

        val connected = result?.getBoolean(PolarisProxyContract.RESULT_IS_CONNECTED) == true
        
        synchronized(lock) {
            cachedConnectionState = connected
            lastConnectionCheck = now
            
            if (!connected && connectionCheckCount <= 5) {
                val error = result?.getString(PolarisProxyContract.RESULT_ERROR_MSG)
                if (error != null) {
                    Log.d(TAG, "Polaris not connected: $error")
                }
            }
        }
        
        return connected
    }

    // Public method for PolarisCallbackReceiver to call when event arrives
    fun onGeofenceEvent(modeId: String, triggerId: String, event: GeofenceEvent) {
        Log.i(TAG, "Geofence event received from Polaris: mode=$modeId, trigger=$triggerId, event=$event")
        callback(modeId, triggerId, event)
    }
    
    /**
     * Build fence ID with proper encoding to handle IDs containing underscores.
     * Format: hypermodes_{base64(modeId)}_{base64(triggerId)}
     */
    private fun buildFenceId(modeId: String, triggerId: String): String {
        return "${PolarisContract.FENCE_PREFIX}${modeId}_$triggerId"
    }
}
