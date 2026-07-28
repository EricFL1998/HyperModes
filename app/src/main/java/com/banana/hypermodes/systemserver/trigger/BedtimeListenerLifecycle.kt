package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.config.ModeConfig

class BedtimeListenerLifecycle {
    private var initialized = false

    private var latestBedtimeState: Boolean? = null

    /** When the last explicit DeskClock state broadcast arrived (ms); 0 = none.
     * Used to ignore stale broadcasts that predate a mode (de)activation. */
    private var latestBedtimeStateAt: Long = 0L

    fun onModesLoaded(
        modes: List<ModeConfig>,
        initialize: (List<ModeConfig>) -> Unit,
        update: (List<ModeConfig>) -> Unit
    ) {
        if (initialized) {
            update(modes)
            return
        }

        initialize(modes)
        initialized = true
    }

    fun onBedtimeStateChanged(active: Boolean) {
        latestBedtimeState = active
        latestBedtimeStateAt = System.currentTimeMillis()
    }

    fun resolveBedtimeState(readPersistedState: () -> Boolean): Boolean {
        return latestBedtimeState ?: readPersistedState()
    }

    /** Timestamp (ms) of the last explicit state broadcast; 0 if none. */
    fun explicitStateAt(): Long = latestBedtimeStateAt

    fun onPersistedStateChanged(readPersistedState: () -> Boolean): Boolean {
        if (!initialized) {
            latestBedtimeState?.let { return it }
        }
        return readPersistedState().also { latestBedtimeState = it }
    }
}
