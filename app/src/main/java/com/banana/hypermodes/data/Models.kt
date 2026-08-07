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
    val enableGrayscale: Boolean? = null,
    val darkMode: Int? = null, // null: Ignore, 0: Light, 1: Dark
    val enableAdaptiveRefreshRatePro: Boolean? = null,
    val enableEyeCare: Boolean? = null,
    val enableRefreshRate: Boolean? = null,
    val refreshRate: Int = 60,

    // Restrictions
    val pausedApps: Set<String> = emptySet(),
    val allowedContacts: Set<String> = emptySet(),

    // Exceptions (who/what can interrupt during the mode)
    val contactFilter: Int = CONTACT_FILTER_NONE,
    val allowedApps: Set<String> = emptySet(),

    // Screen settings
    val hideNotifications: Boolean = false,

    // Display settings (continued)
    val enableAod: Boolean? = null,

    // Device settings
    val performanceMode: Int? = null, // 0: Balanced, 1: Performance
    val enable5g: Boolean? = null,
    val enableWifi: Boolean? = null,
    val enableBluetooth: Boolean? = null,
    val silentMode: Boolean? = null,
    val airplaneMode: Boolean? = null,
    /** Preferred data SIM slot (0 = SIM 1, 1 = SIM 2); null = don't switch. */
    val preferredSimSlot: Int? = null,
    val enableRaiseToWake: Boolean? = null,
    val enableWakeForNotifications: Boolean? = null,
    val enableMotionSicknessRelief: Boolean? = null,

    // Driving auto-detection (何时自动开启). Only the built-in driving mode
    // enables this; custom modes must opt in explicitly.
    val drivingAutoDetect: Boolean = false,
    val drivingDetectMode: Int = DRIVING_DETECT_BLUETOOTH,

    // Specific Bluetooth devices that trigger driving detection in addition
    // to any car-audio device (MAC addresses).
    val drivingTargetDevices: Set<String> = emptySet(),

    // Trigger Groups (v2.0)
    val triggerGroups: List<ModeTriggerGroup> = emptyList(),

    // Schedule (Legacy/Bedtime)
    val schedule: ModeSchedule? = null
)

/**
 * Triggers for automatic mode activation
 */
sealed class ModeTrigger {
    data class Time(
        val schedule: ModeSchedule
    ) : ModeTrigger()

    data class App(
        val packageNames: Set<String>
    ) : ModeTrigger()

    data class Wifi(
        val ssids: Set<String>
    ) : ModeTrigger()

    data class Bluetooth(
        val deviceAddresses: Set<String>,
        val matchAnyCarAudio: Boolean = false
    ) : ModeTrigger()

    object Music : ModeTrigger()

    data class Intent(
        val activateAction: String? = null,
        val deactivateAction: String? = null,
        val packageName: String? = null
    ) : ModeTrigger()

    data class Location(
        val id: String,
        val target: LocationTarget,
        val transition: LocationTransition
    ) : ModeTrigger()

    /**
     * Battery level trigger: activates when the battery level crosses a threshold.
     * @param threshold The battery percentage threshold (0-100).
     * @param operator "above" (>= threshold), "below" (<= threshold) or "equal" (== threshold).
     */
    data class Battery(
        val threshold: Int = 20,
        val operator: String = "below"
    ) : ModeTrigger()
}

/**
 * Location target for geofencing triggers
 */
data class LocationTarget(
    val latitude: Double,
    val longitude: Double,
    val radius: Int = 500, // meters
    val addressName: String? = null,
    val cityName: String? = null,
    val provinceName: String? = null
)

/**
 * Location transition types
 */
/**
 * Trigger Group for v2.0
 */
sealed class ModeTriggerGroup {
    data class Single(
        val trigger: ModeTrigger
    ) : ModeTriggerGroup()

    data class Compound(
        val triggers: List<ModeTrigger>,
        val name: String? = null
    ) : ModeTriggerGroup()
}

enum class LocationTransition {
    ARRIVE,  // Enter the geofence
    LEAVE    // Exit the geofence
}

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

fun ModeTrigger.toComplexTrigger(): ComplexTrigger = when (this) {
    is ModeTrigger.Time -> ComplexTrigger.Time(
        startTime = "%02d:%02d".format(schedule.startHour, schedule.startMinute),
        endTime = "%02d:%02d".format(schedule.endHour, schedule.endMinute),
        repeatDays = (1..7).filter { day -> (schedule.repeatDays and (1 shl (day - 1))) != 0 }
    )
    is ModeTrigger.App -> ComplexTrigger.App(packageNames.toList())
    is ModeTrigger.Wifi -> ComplexTrigger.Wifi(ssids.toList())
    is ModeTrigger.Bluetooth -> ComplexTrigger.Bluetooth(deviceAddresses.toList(), matchAnyCarAudio)
    is ModeTrigger.Music -> ComplexTrigger.Music
    is ModeTrigger.Location -> ComplexTrigger.Location(
        id = id,
        latitude = target.latitude,
        longitude = target.longitude,
        radius = target.radius,
        addressName = target.addressName,
        cityName = target.cityName,
        provinceName = target.provinceName,
        transition = transition.name
    )
    is ModeTrigger.Intent -> ComplexTrigger.Intent(activateAction, deactivateAction, packageName)
    is ModeTrigger.Battery -> ComplexTrigger.Battery(threshold, operator)
}

fun ComplexTrigger.toModeTrigger(): ModeTrigger = when (this) {
    is ComplexTrigger.Time -> {
        val parts = startTime.split(":")
        val startH = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val startM = parts.getOrNull(1)?.toIntOrNull() ?: 0
        val endParts = endTime.split(":")
        val endH = endParts.getOrNull(0)?.toIntOrNull() ?: 0
        val endM = endParts.getOrNull(1)?.toIntOrNull() ?: 0
        val repeatMask = repeatDays.fold(0) { acc, day -> acc or (1 shl (day - 1)) }
        ModeTrigger.Time(ModeSchedule(true, startH, startM, endH, endM, repeatMask))
    }
    is ComplexTrigger.App -> ModeTrigger.App(packageNames.toSet())
    is ComplexTrigger.Wifi -> ModeTrigger.Wifi(ssids.toSet())
    is ComplexTrigger.Bluetooth -> ModeTrigger.Bluetooth(deviceAddresses.toSet(), matchAnyCarAudio)
    is ComplexTrigger.Music -> ModeTrigger.Music
    is ComplexTrigger.Location -> ModeTrigger.Location(
        id = id,
        target = LocationTarget(
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            addressName = addressName,
            cityName = cityName,
            provinceName = provinceName
        ),
        transition = try {
            LocationTransition.valueOf(transition)
        } catch (_: IllegalArgumentException) {
            LocationTransition.ARRIVE
        }
    )
    is ComplexTrigger.Intent -> ModeTrigger.Intent(activateAction, deactivateAction, packageName)
    is ComplexTrigger.Battery -> ModeTrigger.Battery(threshold, operator)
}

/**
 * Convert UI ModeTriggerGroup to system_server TriggerGroup
 */
fun ModeTriggerGroup.toTriggerGroup(): TriggerGroup = when (this) {
    is ModeTriggerGroup.Single -> TriggerGroup.Single(
        trigger = trigger.toComplexTrigger()
    )
    is ModeTriggerGroup.Compound -> TriggerGroup.Compound(
        triggers = triggers.map { it.toComplexTrigger() },
        name = name
    )
}

/**
 * Convert system_server TriggerGroup to UI ModeTriggerGroup
 */
fun TriggerGroup.toModeTriggerGroup(): ModeTriggerGroup = when (this) {
    is TriggerGroup.Single -> ModeTriggerGroup.Single(
        trigger = trigger.toModeTrigger()
    )
    is TriggerGroup.Compound -> ModeTriggerGroup.Compound(
        triggers = triggers.map { it.toModeTrigger() },
        name = name
    )
}

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

    // Build trigger config for the built-in driving mode. Written even when
    // auto-detection is off so the detect mode and picked devices survive
    // toggling — DrivingTriggerManager only reads this for DYNAMIC_TRIGGER
    // modes, so it stays inert while detection is disabled.
    val triggers = if (id == "driving") {
        TriggerConfig(
            bluetooth = BluetoothTrigger(
                enabled = s.drivingDetectMode == DRIVING_DETECT_BLUETOOTH ||
                         s.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH,
                matchAnyCarAudio = s.drivingTargetDevices.isEmpty(),
                targetMacs = s.drivingTargetDevices.toList()
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

    // Optimization: If user allowed many apps (heuristic for "All Apps"), disable system DND
    // Map ContactFilter
    val contactFilter = when (s.contactFilter) {
        CONTACT_FILTER_ALL -> ContactFilter.ALL
        CONTACT_FILTER_STARRED -> ContactFilter.STARRED
        CONTACT_FILTER_NONE -> ContactFilter.NONE
        else -> ContactFilter.NONE
    }

    val triggerGroups = s.triggerGroups.map { it.toTriggerGroup() }

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
        triggerGroups = triggerGroups,
        notification = NotificationConfig(
            dndLevel = dndLevel,
            allowedApps = s.allowedApps.toList()
        ),
        display = DisplayConfig(
            darkMode = s.darkMode,
            grayscale = s.enableGrayscale,
            adaptiveRefreshRatePro = s.enableAdaptiveRefreshRatePro,
            eyeCare = s.enableEyeCare,
            enableRefreshRate = s.enableRefreshRate,
            refreshRate = s.refreshRate,
            enableAod = s.enableAod,
            enableRaiseToWake = s.enableRaiseToWake,
            enableWakeForNotifications = s.enableWakeForNotifications
        ),
        device = DeviceConfig(
            performanceMode = s.performanceMode,
            enable5g = s.enable5g,
            enableWifi = s.enableWifi,
            enableBluetooth = s.enableBluetooth,
            silentMode = s.silentMode,
            airplaneMode = s.airplaneMode,
            enableMotionSicknessRelief = s.enableMotionSicknessRelief,
            preferredSimSlot = s.preferredSimSlot
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

    // A custom mode whose legacy schedule has no trigger groups is migrated
    // into a Time trigger group — the new schema's single representation for
    // schedules. The legacy schedule must NOT be kept in the UI model,
    // otherwise the next save writes both representations (double-scheduling)
    // and a deleted trigger card resurrects on the next load.
    val migratedLegacySchedule = triggerGroups.isEmpty() && hasStoredSchedule && id != "bedtime"

    val migratedTriggerGroups = if (id == "driving") {
        // Driving auto-detect stays on the DrivingTriggerManager path
        // (TriggerConfig + DYNAMIC_TRIGGER type). Surfacing trigger groups here
        // would double-manage the mode, so drop them — the mode has no
        // user-configured groups by design.
        emptyList()
    } else if (migratedLegacySchedule) {
        // Migrate legacy schedule to a Time trigger group for custom modes
        listOf(ModeTriggerGroup.Single(ModeTrigger.Time(schedule!!)))
    } else {
        triggerGroups.map { it.toModeTriggerGroup() }
    }

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
            darkMode = display.darkMode,
            enableAdaptiveRefreshRatePro = display.adaptiveRefreshRatePro,
            enableEyeCare = display.eyeCare,
            enableRefreshRate = display.enableRefreshRate,
            refreshRate = display.refreshRate,
            enableAod = display.enableAod,
            performanceMode = device?.performanceMode,
            enable5g = device?.enable5g,
            enableWifi = device?.enableWifi,
            enableBluetooth = device?.enableBluetooth,
            silentMode = device?.silentMode,
            airplaneMode = device?.airplaneMode,
            enableMotionSicknessRelief = device?.enableMotionSicknessRelief,
            preferredSimSlot = device?.preferredSimSlot,
            enableRaiseToWake = display.enableRaiseToWake,
            enableWakeForNotifications = display.enableWakeForNotifications,
            pausedApps = pausedApps.toSet(),
            allowedContacts = emptySet(),
            contactFilter = contactFilterValue,
            allowedApps = notification.allowedApps.toSet(),
            hideNotifications = false,
            drivingAutoDetect = drivingAutoDetect,
            drivingDetectMode = drivingDetectMode,
            drivingTargetDevices = triggers?.bluetooth?.targetMacs?.toSet() ?: emptySet(),
            triggerGroups = migratedTriggerGroups,
            schedule = if (migratedLegacySchedule) null else schedule
        )
    )
}
