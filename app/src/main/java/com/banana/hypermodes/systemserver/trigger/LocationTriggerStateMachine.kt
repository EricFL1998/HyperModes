package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.geofence.PolarisContract

/**
 * Deduplicating state machine for location triggers.
 *
 * Manages the active/inactive state of location triggers, ensuring that:
 * - State changes are deduplicated (no redundant callbacks for the same state)
 * - Both queried status and event transitions flow through the same logic
 * - Trigger removal always emits exactly one deactivation callback
 *
 * Thread safety: Not thread-safe. Caller must synchronize access if needed.
 */
internal class LocationTriggerStateMachine(
    private val callback: (String, String, Boolean) -> Unit
) {
    private val states = mutableMapOf<String, Boolean>()

    /**
     * Apply a geofence status from a query result.
     * STATUS_IN and STATUS_OUT are converted to inside/outside states.
     * STATUS_UNKNOWN is ignored.
     */
    fun applyStatus(modeId: String, triggerId: String, transition: String, status: Int) {
        when (status) {
            PolarisContract.STATUS_IN -> applyState(modeId, triggerId, transition, true)
            PolarisContract.STATUS_OUT -> applyState(modeId, triggerId, transition, false)
            // STATUS_UNKNOWN is silently ignored
        }
    }

    /**
     * Apply a state transition from an event or query.
     *
     * @param modeId The mode ID owning this trigger
     * @param triggerId The trigger ID (without "location:" prefix)
     * @param transition The trigger transition type ("ARRIVE" or "LEAVE")
     * @param inside Whether the user is currently inside the geofence
     */
    fun applyState(modeId: String, triggerId: String, transition: String, inside: Boolean) {
        val key = "$modeId:$triggerId"

        // Deduplicate: if we've already seen this state, do nothing
        if (states[key] == inside) return
        states[key] = inside

        // Compute activation based on transition type and current state
        val active = when (transition) {
            "ARRIVE" -> inside    // ARRIVE triggers activate when inside
            "LEAVE" -> !inside    // LEAVE triggers activate when outside
            else -> return        // Invalid transition type, ignore
        }

        // Emit callback for both activation and deactivation to maintain continuous state semantics
        callback(modeId, "location:$triggerId", active)
    }

    /**
     * Remove a trigger from tracking.
     * If the trigger was previously active, emits exactly one deactivation callback.
     * If the trigger was never seen or already removed, emits nothing.
     */
    fun remove(modeId: String, triggerId: String) {
        val key = "$modeId:$triggerId"
        if (states.remove(key) != null) {
            callback(modeId, "location:$triggerId", false)
        }
    }
}
