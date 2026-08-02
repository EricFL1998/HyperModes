package com.banana.hypermodes.systemserver.geofence

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.trigger.GeofenceEvent
import java.security.MessageDigest
import java.util.UUID

/**
 * Adapter for Xiaomi Polaris geofencing service.
 * Package-scoped HyperModes IDs, 500m radius, enter+exit transition, confidence 3.
 * Only deletes HyperModes-namespace fences.
 */
class PolarisGeofenceAdapter(
    private val context: Context,
    private val callback: (String, String, GeofenceEvent) -> Unit
) {
    private val packageManager = context.packageManager
    private val registeredFences = mutableMapOf<String, FenceInfo>() // hypermodesId -> FenceInfo

    companion object {
        private const val TAG = "PolarisGeofenceAdapter"
        private const val POLARIS_PACKAGE = "com.xiaomi.gnss.polaris"
        private const val POLARIS_SERVICE = "com.xiaomi.gnss.polaris.PolarisService"

        // Polaris action strings (from decompiled SecurityAdd)
        private const val ACTION_ADD_GEOFENCE = "com.xiaomi.gnss.polaris.action.ADD_GEOFENCE"
        private const val ACTION_REMOVE_GEOFENCE = "com.xiaomi.gnss.polaris.action.REMOVE_GEOFENCE"
        private const val ACTION_LIST_GEOFENCES = "com.xiaomi.gnss.polaris.action.LIST_GEOFENCES"

        // Bundle keys (reverse-engineered from SecurityAdd)
        private const val KEY_FENCE_ID = "fence_id"
        private const val KEY_LATITUDE = "latitude"
        private const val KEY_LONGITUDE = "longitude"
        private const val KEY_RADIUS = "radius"
        private const val KEY_TRANSITION = "transition"
        private const val KEY_CONFIDENCE = "confidence"
        private const val KEY_PACKAGE_NAME = "package_name"
        private const val KEY_COMPONENT = "component"

        // Transition types
        private const val TRANSITION_ENTER = 1
        private const val TRANSITION_EXIT = 2
        private const val TRANSITION_BOTH = 3

        // Confidence level (1=low, 2=medium, 3=high)
        private const val CONFIDENCE_HIGH = 3

        // Namespace prefix for HyperModes fences
        private const val FENCE_PREFIX = "hypermodes_"
    }

    data class FenceInfo(
        val polarisId: String,
        val modeId: String,
        val triggerId: String,
        val latitude: Double,
        val longitude: Double,
        val radius: Int,
        val transition: String
    )

    fun isSupported(): Boolean {
        return try {
            packageManager.getPackageInfo(POLARIS_PACKAGE, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            log("Polaris package not found")
            false
        }
    }

    /**
     * Reconcile HyperModes geofences with Polaris.
     * Add missing fences, update changed ones, delete obsolete ones.
     */
    fun reconcile(triggers: List<Triple<String, String, ComplexTrigger.Location>>) {
        if (!isSupported()) {
            log("Polaris not supported, skipping reconcile")
            return
        }

        // Build desired state
        val desiredFences = mutableMapOf<String, FenceInfo>()
        triggers.forEach { (modeId, triggerId, location) ->
            val hypermodesId = "$modeId:$triggerId"
            val polarisId = generatePolarisId(hypermodesId)
            desiredFences[hypermodesId] = FenceInfo(
                polarisId = polarisId,
                modeId = modeId,
                triggerId = triggerId,
                latitude = location.latitude,
                longitude = location.longitude,
                radius = location.radius,
                transition = location.transition
            )
        }

        // Delete obsolete fences
        val toDelete = registeredFences.keys - desiredFences.keys
        toDelete.forEach { hypermodesId ->
            val fence = registeredFences[hypermodesId]
            if (fence != null) {
                deleteFence(fence.polarisId)
                registeredFences.remove(hypermodesId)
            }
        }

        // Add or update fences
        desiredFences.forEach { (hypermodesId, fence) ->
            val existing = registeredFences[hypermodesId]
            if (existing == null || needsUpdate(existing, fence)) {
                // Delete old if exists
                if (existing != null) {
                    deleteFence(existing.polarisId)
                }
                // Add new
                addFence(fence)
                registeredFences[hypermodesId] = fence
            }
        }

        log("Reconcile complete: ${registeredFences.size} fences registered")
    }

    private fun needsUpdate(existing: FenceInfo, desired: FenceInfo): Boolean {
        return existing.latitude != desired.latitude ||
                existing.longitude != desired.longitude ||
                existing.radius != desired.radius ||
                existing.transition != desired.transition
    }

    private fun addFence(fence: FenceInfo) {
        try {
            val intent = Intent(ACTION_ADD_GEOFENCE).apply {
                setPackage(POLARIS_PACKAGE)
                component = ComponentName(POLARIS_PACKAGE, POLARIS_SERVICE)
                putExtra(KEY_FENCE_ID, fence.polarisId)
                putExtra(KEY_LATITUDE, fence.latitude)
                putExtra(KEY_LONGITUDE, fence.longitude)
                putExtra(KEY_RADIUS, fence.radius.toFloat())
                putExtra(KEY_TRANSITION, TRANSITION_BOTH) // Always monitor both enter and exit
                putExtra(KEY_CONFIDENCE, CONFIDENCE_HIGH)
                putExtra(KEY_PACKAGE_NAME, context.packageName)
                putExtra(KEY_COMPONENT, PolarisCallbackReceiver::class.java.name)
            }

            context.startService(intent)
            log("Added fence: ${fence.polarisId} at (${fence.latitude}, ${fence.longitude}) r=${fence.radius}m")
        } catch (e: Exception) {
            log("Failed to add fence: ${e.message}")
        }
    }

    private fun deleteFence(polarisId: String) {
        try {
            val intent = Intent(ACTION_REMOVE_GEOFENCE).apply {
                setPackage(POLARIS_PACKAGE)
                component = ComponentName(POLARIS_PACKAGE, POLARIS_SERVICE)
                putExtra(KEY_FENCE_ID, polarisId)
            }

            context.startService(intent)
            log("Deleted fence: $polarisId")
        } catch (e: Exception) {
            log("Failed to delete fence: ${e.message}")
        }
    }

    /**
     * Generate a stable Polaris fence ID from HyperModes modeId:triggerId.
     * Uses UUID v5 (namespace-based SHA-1) for consistent regeneration.
     */
    private fun generatePolarisId(hypermodesId: String): String {
        val namespace = "com.banana.hypermodes.geofence"
        val data = "$namespace:$hypermodesId"
        val bytes = MessageDigest.getInstance("SHA-1").digest(data.toByteArray())
        // Use first 16 bytes to create UUID
        val uuid = UUID.nameUUIDFromBytes(bytes)
        return "${FENCE_PREFIX}${uuid.toString().replace("-", "")}"
    }

    /**
     * Handle geofence event from PolarisCallbackReceiver.
     * Verifies fence ID belongs to HyperModes namespace before processing.
     */
    fun handleGeofenceEvent(fenceId: String, eventType: Int) {
        if (!fenceId.startsWith(FENCE_PREFIX)) {
            log("Ignoring non-HyperModes fence: $fenceId")
            return
        }

        val fence = registeredFences.values.find { it.polarisId == fenceId }
        if (fence == null) {
            log("Unknown fence: $fenceId")
            return
        }

        val event = when (eventType) {
            11 -> GeofenceEvent.ENTER // Polaris enter event
            12 -> GeofenceEvent.EXIT  // Polaris exit event
            else -> {
                log("Unknown event type: $eventType")
                return
            }
        }

        log("Geofence event: ${fence.modeId}:${fence.triggerId} -> $event")
        callback(fence.modeId, fence.triggerId, event)
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }
}
