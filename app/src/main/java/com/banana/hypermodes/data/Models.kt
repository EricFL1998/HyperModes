package com.banana.hypermodes.data

import com.banana.hypermodes.systemserver.config.*
import java.util.Calendar

/**
 * Represents a mode (like Bedtime, Focus, Driving)
 */
data class Mode(
    val id: String,
    val name: String,
    val icon: String,
    val statusIcon: String? = null,
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
    val enableAdaptiveRefreshRatePro: Boolean = false,
    val enableEyeCare: Boolean = false,
    val enableRefreshRate: Boolean = false,
    val refreshRate: Int = 60,

    // Restrictions
    val pausedApps: Set<String> = emptySet(),
    val allowedContacts: Set<String> = emptySet(),

    // Exceptions (who/what can interrupt during the mode)
    val contactFilter: Int = CONTACT_FILTER_NONE,
    val allowedApps: Set<String> = emptySet(),

    // Screen settings
    val keepScreenOn: Boolean = false,
    val hideNotifications: Boolean = false,

    // Driving auto-detection (何时自动开启). Only the built-in driving mode
    // enables this; custom modes must opt in explicitly.
    val drivingAutoDetect: Boolean = false,
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
    val startHour: Int = currentHour(),
    val startMinute: Int = currentMinute(),
    val endHour: Int = (currentHour() + 1) % 24,
    val endMinute: Int = currentMinute(),
    val repeatDays: Int = 0x7F // All days by default
) {
    companion object {
        private fun currentHour(): Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        private fun currentMinute(): Int = Calendar.getInstance().get(Calendar.MINUTE)
    }
}

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

    // Driving detection is only meaningful for the built-in driving mode.
    // A custom mode must remain scheduled when it has a time table, even if
    // legacy data left drivingAutoDetect enabled by default.
    val type = when {
        id == "bedtime" -> ModeType.BEDTIME
        id == "driving" && s.drivingAutoDetect -> ModeType.DYNAMIC_TRIGGER
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

    // Build trigger config only for the built-in driving mode.
    val triggers = if (id == "driving" && s.drivingAutoDetect) {
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
    val dndLevel = if (!s.enableDnd) {
        com.banana.hypermodes.systemserver.config.DndLevel.DISABLED
    } else {
        when (s.dndLevel) {
            DndLevel.NONE -> com.banana.hypermodes.systemserver.config.DndLevel.NONE
            DndLevel.PRIORITY -> com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY
            DndLevel.ALARMS -> com.banana.hypermodes.systemserver.config.DndLevel.ALARMS
        }
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
        statusIcon = statusIcon ?: ModeIconMapper.getStatusBarIcon(icon),
        type = type,
        startTime = startTime,
        endTime = endTime,
        repeatDays = repeatDays,
        scheduleEnabled = s.schedule?.enabled ?: false,
        triggers = triggers,
        notification = NotificationConfig(
            dndLevel = dndLevel,
            allowedApps = s.allowedApps.toList()
        ),
        display = DisplayConfig(
            darkMode = s.enableDarkMode,
            grayscale = s.enableGrayscale,
            dimWallpaper = s.dimWallpaper,
            keepScreenOff = s.keepScreenOff,
            adaptiveRefreshRatePro = s.enableAdaptiveRefreshRatePro,
            eyeCare = s.enableEyeCare,
            enableRefreshRate = s.enableRefreshRate,
            refreshRate = s.refreshRate
        ),
        pausedApps = s.pausedApps.toList(),
        contactFilter = contactFilter
    )
}

/**
 * Convert system_server ModeConfig to UI Mode
 */
fun ModeConfig.toMode(isActive: Boolean = false): Mode {
    fun parseTime(value: String?, defaultHour: Int): Pair<Int, Int> {
        val parts = value?.split(":")
        val hour = parts?.getOrNull(0)?.toIntOrNull()
        val minute = parts?.getOrNull(1)?.toIntOrNull()
        return if (hour in 0..23 && minute in 0..59) {
            hour!! to minute!!
        } else {
            defaultHour to 0
        }
    }

    val (startHour, startMinute) = parseTime(startTime, 22)
    val (endHour, endMinute) = parseTime(endTime, 7)

    // Invalid legacy day values are ignored instead of shifting by a negative
    // or out-of-range amount.
    val repeatDaysBitmask = repeatDays
        ?.filter { it in 1..7 }
        ?.fold(0) { acc, day -> acc or (1 shl (day - 1)) }
        ?.takeIf { it != 0 }
        ?: 0x7F

    // A legacy custom mode may have been incorrectly serialized as
    // DYNAMIC_TRIGGER. Only the built-in driving mode owns that trigger type.
    val drivingAutoDetect = id == "driving" && type == ModeType.DYNAMIC_TRIGGER
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

    // Map DndLevel - extract from notification.dndLevel
    val dndLevel = when (notification.dndLevel) {
        com.banana.hypermodes.systemserver.config.DndLevel.DISABLED -> DndLevel.NONE
        com.banana.hypermodes.systemserver.config.DndLevel.NONE -> DndLevel.NONE
        com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY -> DndLevel.PRIORITY
        com.banana.hypermodes.systemserver.config.DndLevel.ALARMS -> DndLevel.ALARMS
    }

    // Preserve schedule data whenever it exists. This also repairs legacy
    // custom modes that were misclassified as DYNAMIC_TRIGGER.
    val hasStoredSchedule = startTime != null || endTime != null || repeatDays != null
    val hasConfiguredSchedule = hasStoredSchedule || scheduleEnabled == true || type == ModeType.BEDTIME
    val schedule = if (hasConfiguredSchedule) {
        ModeSchedule(
            enabled = scheduleEnabled ?: hasStoredSchedule,
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
        statusIcon = statusIcon,
        description = "",
        enabled = isActive,
        settings = ModeSettings(
            enableDnd = notification.dndLevel != com.banana.hypermodes.systemserver.config.DndLevel.DISABLED,
            dndLevel = dndLevel,
            enableGrayscale = display.grayscale,
            enableDarkMode = display.darkMode,
            dimWallpaper = display.dimWallpaper,
            keepScreenOff = display.keepScreenOff,
            enableAdaptiveRefreshRatePro = display.adaptiveRefreshRatePro ?: false,
            enableEyeCare = display.eyeCare,
            enableRefreshRate = display.enableRefreshRate,
            refreshRate = display.refreshRate,
            pausedApps = pausedApps.toSet(),
            allowedContacts = emptySet(),
            contactFilter = contactFilterValue,
            allowedApps = notification.allowedApps.toSet(),
            keepScreenOn = false,
            hideNotifications = false,
            drivingAutoDetect = drivingAutoDetect,
            drivingDetectMode = drivingDetectMode,
            schedule = schedule
        )
    )
}
