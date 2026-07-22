package com.banana.hypermodes.data

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
