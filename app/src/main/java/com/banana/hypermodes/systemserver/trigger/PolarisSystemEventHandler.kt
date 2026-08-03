package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.geofence.PolarisContract

/**
 * Internal helper that parses and validates Polaris geofence events before dispatching
 * them to the engine. Testable without constructing SystemModeHook.
 *
 * Validates fence ID format and event codes, rejecting invalid payloads silently.
 */
internal class PolarisSystemEventHandler(
    private val dispatch: (String, Int) -> Unit
) {
    fun handle(fenceId: String?, event: Int) {
        val callback = PolarisContract.parseCallback(fenceId, event) ?: return
        dispatch(callback.fenceId, callback.event)
    }
}
