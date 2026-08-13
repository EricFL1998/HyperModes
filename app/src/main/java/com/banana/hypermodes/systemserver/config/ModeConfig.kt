package com.banana.hypermodes.systemserver.config

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

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

    // Trigger Groups (v2.0) - replaces complexTriggers for new modes
    val triggerGroups: List<TriggerGroup> = emptyList(),

    // 壁纸 set：锁屏 + 桌面可单独配置；null 子项 = 未编辑（不进入备份/回滚）
    val wallpaper: WallpaperConfig? = null,

    // Contact filter
    val contactFilter: ContactFilter = ContactFilter.ALL
)

@Serializable
data class WallpaperConfig(
    val lock: WallpaperItemConfig? = null,
    val desktop: WallpaperItemConfig? = null
)

@Serializable
data class WallpaperItemConfig(
    val imagePath: String? = null,
    /** system_server 落盘的可读路径（应用/复原时从此复制）。 */
    val sysImagePath: String? = null,
    val sysSubjectMaskPath: String? = null,
    val lockscreenJson: String? = null,
    val templateEditorJson: String? = null,
    val subjectMaskPath: String? = null,
    val scrollEnabled: Boolean? = null,
    val effectType: Int? = null,
    val which: Int = 3
)

@Serializable
sealed class ComplexTrigger {
    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Time")
    data class Time(
        val startTime: String,
        val endTime: String,
        val repeatDays: List<Int>
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.App")
    data class App(
        val packageNames: List<String>
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Wifi")
    data class Wifi(
        val ssids: List<String>
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Bluetooth")
    data class Bluetooth(
        val deviceAddresses: List<String>,
        val matchAnyCarAudio: Boolean = false
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Music")
    object Music : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Intent")
    data class Intent(
        val activateAction: String? = null,
        val deactivateAction: String? = null,
        val packageName: String? = null
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Location")
    data class Location(
        val id: String,
        val latitude: Double,
        val longitude: Double,
        val radius: Int = 500,
        val addressName: String? = null,
        val cityName: String? = null,
        val provinceName: String? = null,
        val transition: String // "ARRIVE" or "LEAVE"
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Battery")
    data class Battery(
        val threshold: Int = 20,
        val operator: String = "below"
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Nfc")
    data class Nfc(
        val tagId: String = ""
    ) : ComplexTrigger()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.ComplexTrigger.Holiday")
    data class Holiday(
        val kind: String = "节假日"
    ) : ComplexTrigger()
}


/**
 * Trigger Group for v2.0 - supports both single and compound (AND) triggers
 * Multiple TriggerGroups are OR'd together
 */
@Serializable
sealed class TriggerGroup {
    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.TriggerGroup.Single")
    data class Single(
        val trigger: ComplexTrigger
    ) : TriggerGroup()

    @Serializable
    @SerialName("com.banana.hypermodes.systemserver.config.TriggerGroup.Compound")
    data class Compound(
        val triggers: List<ComplexTrigger>,
        val name: String? = null  // Optional user-defined name for this group
    ) : TriggerGroup()
}

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
    val adaptiveRefreshRatePro: Boolean? = null,
    val eyeCare: Boolean? = null,
    val enableRefreshRate: Boolean? = null,
    val refreshRate: Int = 60,
    val enableAod: Boolean? = null,
    val enableRaiseToWake: Boolean? = null,
    val enableWakeForNotifications: Boolean? = null
)

@Serializable
data class DeviceConfig(
    val performanceMode: Int? = null,
    val enable5g: Boolean? = null,
    val enableWifi: Boolean? = null,
    val enableBluetooth: Boolean? = null,
    val enableHotspot: Boolean? = null,
    val silentMode: Boolean? = null,
    val airplaneMode: Boolean? = null,
    val enableMotionSicknessRelief: Boolean? = null,
    /** Preferred data SIM slot (0 = SIM 1, 1 = SIM 2); null = don't switch. */
    val preferredSimSlot: Int? = null
)

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
