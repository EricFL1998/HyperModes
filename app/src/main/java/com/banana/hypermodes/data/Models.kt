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

    // Restrictions
    val pausedApps: Set<String> = emptySet(),
    val allowedContacts: Set<String> = emptySet(),

    // Screen settings
    val keepScreenOn: Boolean = false,
    val hideNotifications: Boolean = false,

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
