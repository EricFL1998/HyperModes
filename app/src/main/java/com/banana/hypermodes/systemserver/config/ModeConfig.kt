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
    val type: ModeType,

    // Schedule (for SCHEDULED type)
    val startTime: String? = null,  // "HH:mm"
    val endTime: String? = null,    // "HH:mm"
    val repeatDays: List<Int>? = null,  // [1,2,3,4,5] = Mon-Fri

    // Triggers (for DYNAMIC_TRIGGER type)
    val triggers: TriggerConfig? = null,

    // Notification settings
    val notification: NotificationConfig,

    // Display settings
    val display: DisplayConfig,

    // Paused apps
    val pausedApps: List<String>,

    // Contact filter
    val contactFilter: ContactFilter = ContactFilter.ALL,

    // DND settings
    val dndEnabled: Boolean = true
)

@Serializable
enum class ModeType {
    SCHEDULED,
    DYNAMIC_TRIGGER
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
    val allowAll: Boolean,
    val allowedApps: List<String>
)

@Serializable
data class DisplayConfig(
    val darkMode: Boolean,
    val grayscale: Boolean
)

@Serializable
enum class ContactFilter {
    ALL,
    STARRED_ONLY,
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
