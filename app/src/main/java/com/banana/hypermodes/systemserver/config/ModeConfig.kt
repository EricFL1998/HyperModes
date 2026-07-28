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

    // Device settings
    val device: DeviceConfig? = null,

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
    DISABLED,
    NONE,
    PRIORITY,
    ALARMS
}

@Serializable
data class DisplayConfig(
    val darkMode: Int? = null, // null: Ignore, 0: Light, 1: Dark
    val grayscale: Boolean? = null,
    val keepScreenOff: Boolean? = null,
    val adaptiveRefreshRatePro: Boolean? = null,
    val eyeCare: Boolean? = null,
    val enableRefreshRate: Boolean? = null,
    val refreshRate: Int = 60,
    val enableAod: Boolean? = null
)

@Serializable
data class DeviceConfig(
    val performanceMode: Int? = null,
    val enable5g: Boolean? = null,
    val enableRaiseToWake: Boolean? = null,
    val enableWakeForNotifications: Boolean? = null
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
    val lastModeId: String? = null,
    val modes: List<ModeConfig>,
    /** Records manually dismissed scheduled modes: modeId -> dismissTimestamp. */
    val dismissedModes: Map<String, Long> = emptyMap()
)
