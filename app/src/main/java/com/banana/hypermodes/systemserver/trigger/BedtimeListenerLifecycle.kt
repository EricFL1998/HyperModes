package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.config.ModeConfig

class BedtimeListenerLifecycle {
    private var initialized = false

    private var latestBedtimeState: Boolean? = null

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
    }

    fun resolveBedtimeState(readPersistedState: () -> Boolean): Boolean {
        return latestBedtimeState ?: readPersistedState()
    }

    fun onPersistedStateChanged(readPersistedState: () -> Boolean): Boolean {
        if (!initialized) {
            latestBedtimeState?.let { return it }
        }
        return readPersistedState().also { latestBedtimeState = it }
    }
}
