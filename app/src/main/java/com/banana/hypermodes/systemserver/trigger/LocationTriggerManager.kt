package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.geofence.PolarisGeofenceAdapter
import com.banana.hypermodes.systemserver.geofence.PolarisCallbackBridge

/**
 * Manages location triggers via Polaris geofencing.
 * Translates Polaris enter/exit events into continuous ARRIVE/LEAVE state.
 * State starts unknown after restart; config load never synthesizes enter/leave.
 */
class LocationTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val polarisAdapter = PolarisGeofenceAdapter(context, ::onGeofenceEvent)
    private var configs: Map<String, List<Pair<String, ComplexTrigger.Location>>> = emptyMap()

    // Track current state per trigger: modeId:triggerId -> isInside
    // null = unknown (initial state after restart/config load)
    private val triggerStates = mutableMapOf<String, Boolean?>()

    init {
        // Register adapter to receive callbacks from PolarisCallbackReceiver
        if (polarisAdapter.isSupported()) {
            PolarisCallbackBridge.register(polarisAdapter)
        }
    }

    fun updateConfigs(newConfigs: Map<String, List<Pair<String, ComplexTrigger.Location>>>) {
        // Report modes that dropped out as inactive
        val oldKeys = configs.keys
        val newKeys = newConfigs.keys
        (oldKeys - newKeys).forEach { modeId ->
            configs[modeId]?.forEach { (triggerId, _) ->
                val key = "$modeId:$triggerId"
                triggerStates.remove(key)
                callback(modeId, "location:$triggerId", false)
            }
        }

        configs = newConfigs

        // Check capability before registering
        if (!polarisAdapter.isSupported()) {
            log("Polaris geofencing not supported on this device")
            return
        }

        // Reconcile geofences with Polaris
        val allTriggers = mutableListOf<Triple<String, String, ComplexTrigger.Location>>()
        configs.forEach { (modeId, triggers) ->
            triggers.forEach { (triggerId, location) ->
                allTriggers.add(Triple(modeId, triggerId, location))
            }
        }

        polarisAdapter.reconcile(allTriggers)

        // Initialize state for new triggers as unknown
        allTriggers.forEach { (modeId, triggerId, _) ->
            val key = "$modeId:$triggerId"
            if (!triggerStates.containsKey(key)) {
                triggerStates[key] = null // unknown
            }
        }
    }

    private fun onGeofenceEvent(modeId: String, triggerId: String, event: GeofenceEvent) {
        val key = "$modeId:$triggerId"
        val trigger = configs[modeId]?.find { it.first == triggerId }?.second ?: return

        val wasInside = triggerStates[key]
        val isInside = when (event) {
            GeofenceEvent.ENTER -> true
            GeofenceEvent.EXIT -> false
        }

        triggerStates[key] = isInside

        // Determine if trigger should be active based on transition type
        val shouldActivate = when (trigger.transition) {
            "ARRIVE" -> isInside
            "LEAVE" -> !isInside
            else -> false
        }

        log("Geofence event for $modeId:$triggerId: $event, inside=$isInside, transition=${trigger.transition}, activate=$shouldActivate")

        // Only fire callback if state actually changed (or was unknown)
        if (wasInside == null || wasInside != isInside) {
            callback(modeId, "location:$triggerId", shouldActivate)
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "LocationTriggerManager"
    }
}

enum class GeofenceEvent {
    ENTER,
    EXIT
}
