package com.banana.hypermodes.data

import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel as ConfigDndLevel
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ModeConversionTest {

    @Test
    fun `custom schedule survives config round trip`() {
        val mode = Mode(
            id = "custom_work",
            name = "Work",
            icon = "💼",
            description = "",
            settings = ModeSettings(
                drivingAutoDetect = false,
                schedule = ModeSchedule(
                    enabled = true,
                    startHour = 9,
                    startMinute = 30,
                    endHour = 17,
                    endMinute = 45,
                    repeatDays = 0b0011111
                ),
                enableAdaptiveRefreshRatePro = true
            )
        )

        val config = mode.toModeConfig()
        val restored = config.toMode()

        assertEquals(ModeType.SCHEDULED, config.type)
        assertEquals("09:30", config.startTime)
        assertEquals("17:45", config.endTime)
        assertEquals(listOf(1, 2, 3, 4, 5), config.repeatDays)
        assertEquals(true, config.scheduleEnabled)
        assertEquals(true, config.display.adaptiveRefreshRatePro)
        // v2.0: legacy schedule is migrated into a Time trigger group and the
        // legacy settings.schedule field is cleared to avoid double-scheduling.
        assertNull(restored.settings.schedule)
        val time = restored.settings.triggerGroups.single().let { group ->
            (group as ModeTriggerGroup.Single).trigger as ModeTrigger.Time
        }
        assertEquals(mode.settings.schedule, time.schedule)
        assertEquals(true, restored.settings.enableAdaptiveRefreshRatePro)
        assertFalse(restored.settings.drivingAutoDetect)
    }

    @Test
    fun `disabled schedule retains its times`() {
        val mode = Mode(
            id = "custom_focus",
            name = "Focus",
            icon = "⭐",
            description = "",
            settings = ModeSettings(
                schedule = ModeSchedule(
                    enabled = false,
                    startHour = 8,
                    startMinute = 15,
                    endHour = 11,
                    endMinute = 5,
                    repeatDays = 0b1000001
                )
            )
        )

        val restored = mode.toModeConfig().toMode()
        // v2.0: the schedule migrates into a Time trigger group.
        assertNull(restored.settings.schedule)
        val schedule = (restored.settings.triggerGroups.single()
            .let { group -> (group as ModeTriggerGroup.Single).trigger as ModeTrigger.Time }
            .schedule)
        assertFalse(schedule.enabled)
        assertEquals(8, schedule.startHour)
        assertEquals(15, schedule.startMinute)
        assertEquals(11, schedule.endHour)
        assertEquals(5, schedule.endMinute)
        assertEquals(0b1000001, schedule.repeatDays)
    }

    @Test
    fun `legacy dynamic custom mode with times restores as schedule`() {
        val legacy = config(
            id = "custom_legacy",
            type = ModeType.DYNAMIC_TRIGGER,
            startTime = "10:20",
            endTime = "18:40",
            repeatDays = listOf(2, 4, 6),
            scheduleEnabled = null
        )

        val restored = legacy.toMode()
        // v2.0: a legacy DYNAMIC_TRIGGER custom mode with times migrates into a
        // Time trigger group; the legacy settings.schedule field is cleared.
        assertNull(restored.settings.schedule)
        val schedule = (restored.settings.triggerGroups.single()
            .let { group -> (group as ModeTriggerGroup.Single).trigger as ModeTrigger.Time }
            .schedule)
        assertTrue(schedule.enabled)
        assertEquals(10, schedule.startHour)
        assertEquals(20, schedule.startMinute)
        assertEquals(18, schedule.endHour)
        assertEquals(40, schedule.endMinute)
        assertEquals(0b0101010, schedule.repeatDays)
        assertFalse(restored.settings.drivingAutoDetect)
    }

    @Test
    fun `manual mode without schedule does not invent one`() {
        val restored = config(
            id = "dnd",
            type = ModeType.SCHEDULED,
            startTime = null,
            endTime = null,
            repeatDays = null,
            scheduleEnabled = false
        ).toMode()

        assertNull(restored.settings.schedule)
    }

    @Test
    fun `active state comes from full config rather than mode type`() {
        val config = config(
            id = "custom_work",
            type = ModeType.SCHEDULED,
            startTime = "09:00",
            endTime = "17:00",
            repeatDays = listOf(1, 2, 3, 4, 5),
            scheduleEnabled = true
        )

        assertFalse(config.toMode().enabled)
        assertTrue(config.toMode(isActive = true).enabled)
    }

    @Test
    fun `statusIcon is populated from mapper if null`() {
        val mode = Mode(
            id = "custom",
            name = "Custom",
            icon = "🌙", // moon emoji
            statusIcon = null,
            description = ""
        )

        val config = mode.toModeConfig()
        assertEquals("ic_stat_moon", config.statusIcon)
    }

    @Test
    fun `explicit statusIcon is preserved`() {
        val mode = Mode(
            id = "custom",
            name = "Custom",
            icon = "🌙",
            statusIcon = "custom_icon",
            description = ""
        )

        val config = mode.toModeConfig()
        assertEquals("custom_icon", config.statusIcon)
    }

    private fun config(
        id: String,
        type: ModeType,
        startTime: String?,
        endTime: String?,
        repeatDays: List<Int>?,
        scheduleEnabled: Boolean?
    ) = ModeConfig(
        id = id,
        name = id,
        icon = "⭐",
        type = type,
        startTime = startTime,
        endTime = endTime,
        repeatDays = repeatDays,
        scheduleEnabled = scheduleEnabled,
        notification = NotificationConfig(ConfigDndLevel.PRIORITY),
        display = DisplayConfig(),
        pausedApps = emptyList()
    )
}
