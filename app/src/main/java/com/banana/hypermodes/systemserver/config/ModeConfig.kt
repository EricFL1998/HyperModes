package com.banana.hypermodes.systemserver.config

import kotlinx.serialization.Serializable

/**
 * Mode configuration parsed from JSON stored in Settings.Global.
 * This is a pure data class with no Android dependencies.
 */
@Serializable
data class ModeConfig(
    val id: String,
    val name: String,
    val icon: String,
    val statusIcon: String? = null,
    val type: ModeType,

    // Schedule (for SCHEDULED/BEDTIME types). Nullable for legacy configs.
    val startTime: String? = null,  // "HH:mm"
    val endTime: String? = null,    // "HH:mm"
    val repeatDays: List<Int>? = null,  // [1,2,3,4,5] = Mon-Fri
    /** Whether this schedule is enabled; null means legacy config without the field. */
    val scheduleEnabled: Boolean? = null,

    // Triggers (for DYNAMIC_TRIGGER type)
    val triggers: TriggerConfig? = null,

    // Notification settings
    val notification: NotificationConfig,

    // Display settings
    val display: DisplayConfig,

    // Paused apps
    val pausedApps: List<String>,

    // Contact filter
    val contactFilter: ContactFilter = ContactFilter.ALL
)

@Serializable
enum class ModeType {
    SCHEDULED,
    DYNAMIC_TRIGGER,
    BEDTIME
}

@Serializable
data class TriggerConfig(
    val bluetooth: BluetoothTrigger? = null,
    val motion: MotionTrigger? = null
)

@Serializable
data class BluetoothTrigger(
    val enabled: Boolean,
    val matchAnyCarAudio: Boolean,
    val targetMacs: List<String>
)

@Serializable
data class MotionTrigger(
    val enabled: Boolean,
    val speedThresholdKmH: Float
)

@Serializable
data class NotificationConfig(
    val dndLevel: DndLevel,
    val allowedApps: List<String> = emptyList()
)

@Serializable
enum class DndLevel {
    NONE,
    PRIORITY,
    ALARMS
}

@Serializable
data class DisplayConfig(
    val darkMode: Boolean = false,
    val grayscale: Boolean = false,
    val dimWallpaper: Boolean = false,
    val keepScreenOff: Boolean = false
)

@Serializable
enum class ContactFilter {
    ALL,
    STARRED,
    NONE
}

/**
 * Root config object
 */
@Serializable
data class FullConfig(
    val activeModeId: String? = null,
    val modes: List<ModeConfig>
)
