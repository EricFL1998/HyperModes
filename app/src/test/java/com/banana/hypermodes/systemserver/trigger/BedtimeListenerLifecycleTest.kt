package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class BedtimeListenerLifecycleTest {

    @Test
    fun firstModeLoadInitializesListener() {
        val lifecycle = BedtimeListenerLifecycle()
        val calls = mutableListOf<String>()

        lifecycle.onModesLoaded(
            modes = listOf(bedtimeMode("bedtime")),
            initialize = { calls += "init:${it.single().id}" },
            update = { calls += "update:${it.single().id}" }
        )

        assertEquals(listOf("init:bedtime"), calls)
    }

    @Test
    fun laterModeLoadsUpdateListenerWithoutReinitializing() {
        val lifecycle = BedtimeListenerLifecycle()
        val calls = mutableListOf<String>()
        val initialize: (List<ModeConfig>) -> Unit = { calls += "init:${it.single().id}" }
        val update: (List<ModeConfig>) -> Unit = { calls += "update:${it.single().id}" }

        lifecycle.onModesLoaded(listOf(bedtimeMode("bedtime-1")), initialize, update)
        lifecycle.onModesLoaded(listOf(bedtimeMode("bedtime-2")), initialize, update)

        assertEquals(listOf("init:bedtime-1", "update:bedtime-2"), calls)
    }

    @Test
    fun failedInitializationIsRetriedOnNextModeLoad() {
        val lifecycle = BedtimeListenerLifecycle()
        val calls = mutableListOf<String>()

        assertThrows(IllegalStateException::class.java) {
            lifecycle.onModesLoaded(
                modes = listOf(bedtimeMode("first")),
                initialize = {
                    calls += "init:${it.single().id}"
                    throw IllegalStateException("boom")
                },
                update = { calls += "update:${it.single().id}" }
            )
        }

        lifecycle.onModesLoaded(
            modes = listOf(bedtimeMode("second")),
            initialize = { calls += "init:${it.single().id}" },
            update = { calls += "update:${it.single().id}" }
        )

        assertEquals(listOf("init:first", "init:second"), calls)
    }

    @Test
    fun broadcastStateSurvivesUnrelatedModeListUpdates() {
        val lifecycle = BedtimeListenerLifecycle()

        lifecycle.onBedtimeStateChanged(true)
        lifecycle.onModesLoaded(emptyList(), initialize = {}, update = {})

        assertTrue(lifecycle.resolveBedtimeState { false })
    }

    @Test
    fun persistedStateIsUsedUntilAStateBroadcastArrives() {
        val lifecycle = BedtimeListenerLifecycle()

        assertFalse(lifecycle.resolveBedtimeState { false })
        assertTrue(lifecycle.resolveBedtimeState { true })
    }

    @Test
    fun persistedStateCannotOverwriteBroadcastBeforeModesLoad() {
        val lifecycle = BedtimeListenerLifecycle()
        lifecycle.onBedtimeStateChanged(true)

        assertTrue(lifecycle.onPersistedStateChanged { false })
        assertTrue(lifecycle.resolveBedtimeState { false })
    }

    @Test
    fun persistedStateChangeTakesOverAfterModesLoad() {
        val lifecycle = BedtimeListenerLifecycle()
        lifecycle.onBedtimeStateChanged(true)
        lifecycle.onModesLoaded(emptyList(), initialize = {}, update = {})

        assertFalse(lifecycle.onPersistedStateChanged { false })
        assertFalse(lifecycle.resolveBedtimeState { true })
    }

    private fun bedtimeMode(id: String) = ModeConfig(
        id = id,
        name = id,
        icon = "bedtime",
        type = ModeType.BEDTIME,
        notification = NotificationConfig(dndLevel = DndLevel.PRIORITY),
        display = DisplayConfig(),
        pausedApps = emptyList()
    )
}
