package com.banana.hypermodes.data

import com.banana.hypermodes.systemserver.config.*

/**
 * Represents a mode (like Bedtime, Focus, Driving)
 */
data class Mode(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val enabled: Boolean = false,
    val settings: ModeSettings = ModeSettings()
)

/**
 * Settings for a mode
 */
data class ModeSettings(
    // DND settings
    val enableDnd: Boolean = true,
    val dndLevel: DndLevel = DndLevel.PRIORITY,

    // Display settings
    val enableGrayscale: Boolean = false,
    val enableDarkMode: Boolean = false,
    val dimWallpaper: Boolean = false,
    val keepScreenOff: Boolean = false,

    // Restrictions
    val pausedApps: Set<String> = emptySet(),
    val allowedContacts: Set<String> = emptySet(),

    // Exceptions (who/what can interrupt during the mode)
    val contactFilter: Int = CONTACT_FILTER_NONE,
    val allowedApps: Set<String> = emptySet(),

    // Screen settings
    val keepScreenOn: Boolean = false,
    val hideNotifications: Boolean = false,

    // Driving auto-detection (何时自动开启)
    val drivingAutoDetect: Boolean = true,
    val drivingDetectMode: Int = DRIVING_DETECT_BLUETOOTH,

    // Schedule
    val schedule: ModeSchedule? = null
)

/**
 * DND interruption filter levels
 */
enum class DndLevel {
    NONE,           // No interruptions
    PRIORITY,       // Priority only (alarms, starred contacts)
    ALARMS          // Alarms only
}

// contactFilter values (who may interrupt while the mode is on)
const val CONTACT_FILTER_NONE = 0
const val CONTACT_FILTER_ALL = 1
const val CONTACT_FILTER_STARRED = 2

// drivingDetectMode values (驾车勿扰 auto-activation source)
const val DRIVING_DETECT_BLUETOOTH = 0
const val DRIVING_DETECT_MOTION_BLUETOOTH = 1

/**
 * Schedule for automatic mode activation
 */
data class ModeSchedule(
    val enabled: Boolean = false,
    val startHour: Int = 22,
    val startMinute: Int = 0,
    val endHour: Int = 7,
    val endMinute: Int = 0,
    val repeatDays: Int = 0x7F // All days by default
)

/**
 * App info for pause list
 */
data class PausableApp(
    val packageName: String,
    val label: String,
    val icon: android.graphics.drawable.Drawable?,
    val isPaused: Boolean = false
)

// Extension functions for converting between UI models and system_server config models

/**
 * Convert UI Mode to system_server ModeConfig
 */
fun Mode.toModeConfig(): ModeConfig {
    val s = settings

    // Determine mode type based on id and settings
    val type = when {
        id == "bedtime" -> ModeType.BEDTIME
        s.drivingAutoDetect -> ModeType.DYNAMIC_TRIGGER
        else -> ModeType.SCHEDULED
    }

    // Format times as "HH:mm"
    val startTime = s.schedule?.let {
        "%02d:%02d".format(it.startHour, it.startMinute)
    }
    val endTime = s.schedule?.let {
        "%02d:%02d".format(it.endHour, it.endMinute)
    }

    // Convert repeatDays bitmask to list [1,2,3,4,5,6,7]
    val repeatDays = s.schedule?.let { schedule ->
        (1..7).filter { day ->
            (schedule.repeatDays and (1 shl (day - 1))) != 0
        }
    }

    // Build trigger config for DYNAMIC_TRIGGER type
    val triggers = if (s.drivingAutoDetect) {
        TriggerConfig(
            bluetooth = BluetoothTrigger(
                enabled = s.drivingDetectMode == DRIVING_DETECT_BLUETOOTH ||
                         s.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH,
                matchAnyCarAudio = true,
                targetMacs = emptyList()
            ),
            motion = if (s.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH) {
                MotionTrigger(
                    enabled = true,
                    speedThresholdKmH = 15f
                )
            } else null
        )
    } else null

    // Map DndLevel
    val dndLevel = when (s.dndLevel) {
        DndLevel.NONE -> com.banana.hypermodes.systemserver.config.DndLevel.NONE
        DndLevel.PRIORITY -> com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY
        DndLevel.ALARMS -> com.banana.hypermodes.systemserver.config.DndLevel.ALARMS
    }

    // Map ContactFilter
    val contactFilter = when (s.contactFilter) {
        CONTACT_FILTER_ALL -> ContactFilter.ALL
        CONTACT_FILTER_STARRED -> ContactFilter.STARRED
        CONTACT_FILTER_NONE -> ContactFilter.NONE
        else -> ContactFilter.NONE
    }

    return ModeConfig(
        id = id,
        name = name,
        icon = icon,
        type = type,
        startTime = startTime,
        endTime = endTime,
        repeatDays = repeatDays,
        triggers = triggers,
        notification = NotificationConfig(
            allowAll = !s.hideNotifications,
            allowedApps = s.allowedApps.toList()
        ),
        display = DisplayConfig(
            darkMode = s.enableDarkMode,
            grayscale = s.enableGrayscale
        ),
        pausedApps = s.pausedApps.toList(),
        contactFilter = contactFilter,
        dndEnabled = s.enableDnd
    )
}

/**
 * Convert system_server ModeConfig to UI Mode
 */
fun ModeConfig.toMode(): Mode {
    // Parse start/end times
    val (startHour, startMinute) = startTime?.split(":")?.map { it.toIntOrNull() ?: 0 } ?: listOf(22, 0)
    val (endHour, endMinute) = endTime?.split(":")?.map { it.toIntOrNull() ?: 0 } ?: listOf(7, 0)

    // Convert repeatDays list to bitmask
    val repeatDaysBitmask = repeatDays?.fold(0) { acc, day ->
        acc or (1 shl (day - 1))
    } ?: 0x7F

    // Determine drivingAutoDetect and drivingDetectMode from triggers
    val drivingAutoDetect = type == ModeType.DYNAMIC_TRIGGER
    val drivingDetectMode = when {
        triggers?.motion?.enabled == true -> DRIVING_DETECT_MOTION_BLUETOOTH
        triggers?.bluetooth?.enabled == true -> DRIVING_DETECT_BLUETOOTH
        else -> DRIVING_DETECT_BLUETOOTH
    }

    // Map ContactFilter
    val contactFilterValue = when (contactFilter) {
        ContactFilter.ALL -> CONTACT_FILTER_ALL
        ContactFilter.STARRED -> CONTACT_FILTER_STARRED
        ContactFilter.NONE -> CONTACT_FILTER_NONE
    }

    // Map DndLevel - need to infer from notification settings since DndLevel is not in ModeConfig
    // Default to PRIORITY as that's the most common setting
    val dndLevel = DndLevel.PRIORITY

    // Build schedule for SCHEDULED and BEDTIME types
    val schedule = if (type == ModeType.SCHEDULED || type == ModeType.BEDTIME) {
        ModeSchedule(
            enabled = true,
            startHour = startHour,
            startMinute = startMinute,
            endHour = endHour,
            endMinute = endMinute,
            repeatDays = repeatDaysBitmask
        )
    } else null

    return Mode(
        id = id,
        name = name,
        icon = icon,
        description = "",
        enabled = true,
        settings = ModeSettings(
            enableDnd = dndEnabled,
            dndLevel = dndLevel,
            enableGrayscale = display.grayscale,
            enableDarkMode = display.darkMode,
            dimWallpaper = false,
            keepScreenOff = false,
            pausedApps = pausedApps.toSet(),
            allowedContacts = emptySet(),
            contactFilter = contactFilterValue,
            allowedApps = notification.allowedApps.toSet(),
            keepScreenOn = false,
            hideNotifications = !notification.allowAll,
            drivingAutoDetect = drivingAutoDetect,
            drivingDetectMode = drivingDetectMode,
            schedule = schedule
        )
    )
}
