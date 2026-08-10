package com.banana.hypermodes.automation

import androidx.compose.ui.graphics.Color
import com.banana.hypermodes.ui.AutomationAction
import java.util.UUID

/**
 * 自动化控制块的类型。包含系统控制、显示、设备、模式、应用、控制流和条件判断。
 * 每个类型的 [id] 与 [AutomationCatalog] 中的操作 id 一一对应。
 */
sealed class BlockType(val id: String) {
    // ==================== 触发条件 ====================
    data object TriggerTime : BlockType("trigger_time")
    data object TriggerWifi : BlockType("trigger_wifi")
    data object TriggerWifiOn : BlockType("trigger_wifi_on")
    data object TriggerWifiOff : BlockType("trigger_wifi_off")
    data object TriggerBluetooth : BlockType("trigger_bluetooth")
    data object TriggerBluetoothOn : BlockType("trigger_bluetooth_on")
    data object TriggerBluetoothOff : BlockType("trigger_bluetooth_off")
    data object TriggerBattery : BlockType("trigger_battery")
    data object TriggerChargingStart : BlockType("trigger_charging_start")
    data object TriggerChargingStop : BlockType("trigger_charging_stop")
    data object TriggerNetwork : BlockType("trigger_network")
    data object TriggerMusicStart : BlockType("trigger_music_start")
    data object TriggerMusicStop : BlockType("trigger_music_stop")
    data object TriggerApp : BlockType("trigger_app")
    data object TriggerDayOfWeek : BlockType("trigger_day_of_week")

    // ==================== 系统控制 ====================
    data object ToggleWifiOn : BlockType("toggle_wifi_on")
    data object ToggleWifiOff : BlockType("toggle_wifi_off")
    data object ToggleBluetoothOn : BlockType("toggle_bluetooth_on")
    data object ToggleBluetoothOff : BlockType("toggle_bluetooth_off")
    data object ToggleMobileDataOn : BlockType("toggle_mobile_data_on")
    data object ToggleMobileDataOff : BlockType("toggle_mobile_data_off")
    data object ToggleAirplaneOn : BlockType("toggle_airplane_on")
    data object ToggleAirplaneOff : BlockType("toggle_airplane_off")
    data object ToggleHotspotOn : BlockType("toggle_hotspot_on")
    data object ToggleHotspotOff : BlockType("toggle_hotspot_off")
    data object ToggleNfcOn : BlockType("toggle_nfc_on")
    data object ToggleNfcOff : BlockType("toggle_nfc_off")
    data object ToggleGpsOn : BlockType("toggle_gps_on")
    data object ToggleGpsOff : BlockType("toggle_gps_off")
    data object ToggleFlashlightOn : BlockType("toggle_flashlight_on")
    data object ToggleFlashlightOff : BlockType("toggle_flashlight_off")
    data object ToggleAutoRotateOn : BlockType("toggle_auto_rotate_on")
    data object ToggleAutoRotateOff : BlockType("toggle_auto_rotate_off")
    data object ToggleBatterySaverOn : BlockType("toggle_battery_saver_on")
    data object ToggleBatterySaverOff : BlockType("toggle_battery_saver_off")
    data object SetSilentModeOn : BlockType("set_silent_mode_on")
    data object SetSilentModeOff : BlockType("set_silent_mode_off")
    data object SetDnd : BlockType("set_dnd")
    data object AdjustVolume : BlockType("adjust_volume")
    data object AdjustBrightness : BlockType("adjust_brightness")
    data object SetAutoBrightnessOn : BlockType("set_auto_brightness_on")
    data object SetAutoBrightnessOff : BlockType("set_auto_brightness_off")

    // ==================== 显示 ====================
    data object SetGrayscaleOn : BlockType("set_grayscale_on")
    data object SetGrayscaleOff : BlockType("set_grayscale_off")
    data object SetRaiseToWakeOn : BlockType("set_raise_to_wake_on")
    data object SetRaiseToWakeOff : BlockType("set_raise_to_wake_off")
    data object SetWakeForNotificationsOn : BlockType("set_wake_for_notifications_on")
    data object SetWakeForNotificationsOff : BlockType("set_wake_for_notifications_off")
    data object SetEyeCareOn : BlockType("set_eye_care_on")
    data object SetEyeCareOff : BlockType("set_eye_care_off")
    data object SetRefreshRate : BlockType("set_refresh_rate")
    data object SetAdaptiveRefreshRateProOn : BlockType("set_adaptive_refresh_rate_pro_on")
    data object SetAdaptiveRefreshRateProOff : BlockType("set_adaptive_refresh_rate_pro_off")

    // ==================== 设备 ====================
    data object SetPerformanceMode : BlockType("set_performance_mode")
    data object Set5gOn : BlockType("set_5g_on")
    data object Set5gOff : BlockType("set_5g_off")
    data object SetPreferredSim : BlockType("set_preferred_sim")
    data object SetMotionSicknessReliefOn : BlockType("set_motion_sickness_relief_on")
    data object SetMotionSicknessReliefOff : BlockType("set_motion_sickness_relief_off")

    // ==================== 模式 ====================
    data object EnableMode : BlockType("enable_mode")
    data object DisableMode : BlockType("disable_mode")

    // ==================== 应用 ====================
    data object OpenApp : BlockType("open_app")
    data object SuspendApps : BlockType("suspend_apps")
    data object UnsuspendApps : BlockType("unsuspend_apps")

    // ==================== 控制流 ====================
    data object IfCondition : BlockType("if_condition")
    data object Repeat : BlockType("repeat")
    data object RepeatCount : BlockType("repeat_count")
    data object Wait : BlockType("wait")
    data object Comment : BlockType("comment")

    // ==================== 逻辑运算 ====================
    data object AndCondition : BlockType("and_condition")
    data object OrCondition : BlockType("or_condition")

    // ==================== 条件判断 ====================
    data object CheckWifiOn : BlockType("check_wifi_on")
    data object CheckWifiOff : BlockType("check_wifi_off")
    data object CheckBluetoothOn : BlockType("check_bluetooth_on")
    data object CheckBluetoothOff : BlockType("check_bluetooth_off")
    data object CheckBatteryLevel : BlockType("check_battery")
    data object CheckChargingOn : BlockType("check_charging_on")
    data object CheckChargingOff : BlockType("check_charging_off")
    data object CheckTimeRange : BlockType("check_time")
    data object CheckDayOfWeek : BlockType("check_day_of_week")
    data object CheckScreenOn : BlockType("check_screen_on")
    data object CheckScreenOff : BlockType("check_screen_off")
    data object CheckAirplaneOn : BlockType("check_airplane_on")
    data object CheckAirplaneOff : BlockType("check_airplane_off")
    data object CheckDndOn : BlockType("check_dnd_state_on")
    data object CheckDndOff : BlockType("check_dnd_state_off")
    data object CheckSilentOn : BlockType("check_silent_on")
    data object CheckSilentOff : BlockType("check_silent_off")
    data object CheckMobileDataOn : BlockType("check_mobile_data_on")
    data object CheckMobileDataOff : BlockType("check_mobile_data_off")
    data object CheckNetworkType : BlockType("check_network_type")
    data object CheckMusicPlayingOn : BlockType("check_music_playing_on")
    data object CheckMusicPlayingOff : BlockType("check_music_playing_off")
    data object CheckAppForeground : BlockType("check_app_foreground")
    data object CheckAutoRotateOn : BlockType("check_auto_rotate_on")
    data object CheckAutoRotateOff : BlockType("check_auto_rotate_off")
    data object CheckHotspotOn : BlockType("check_hotspot_on")
    data object CheckHotspotOff : BlockType("check_hotspot_off")
    data object CheckNfcOn : BlockType("check_nfc_on")
    data object CheckNfcOff : BlockType("check_nfc_off")
    data object CheckGpsOn : BlockType("check_gps_on")
    data object CheckGpsOff : BlockType("check_gps_off")
    data object CheckVolumeLevel : BlockType("check_volume")
    data object CheckBrightnessLevel : BlockType("check_brightness")

    companion object {
        /** 所有类型，按目录顺序排列，用于 id -> BlockType 反查。 */
        val all: List<BlockType> by lazy {
            listOf(
            TriggerTime, TriggerWifi, TriggerWifiOn, TriggerWifiOff,
            TriggerBluetooth, TriggerBluetoothOn, TriggerBluetoothOff,
            TriggerBattery, TriggerChargingStart, TriggerChargingStop,
            TriggerNetwork, TriggerMusicStart, TriggerMusicStop,
            TriggerApp, TriggerDayOfWeek,
            ToggleWifiOn, ToggleWifiOff, ToggleBluetoothOn, ToggleBluetoothOff,
            ToggleMobileDataOn, ToggleMobileDataOff,
            ToggleAirplaneOn, ToggleAirplaneOff,
            ToggleHotspotOn, ToggleHotspotOff,
            ToggleNfcOn, ToggleNfcOff,
            ToggleGpsOn, ToggleGpsOff,
            ToggleFlashlightOn, ToggleFlashlightOff,
            ToggleAutoRotateOn, ToggleAutoRotateOff,
            ToggleBatterySaverOn, ToggleBatterySaverOff,
            SetSilentModeOn, SetSilentModeOff, SetDnd,
            AdjustVolume, AdjustBrightness,
            SetAutoBrightnessOn, SetAutoBrightnessOff,
            SetGrayscaleOn, SetGrayscaleOff,
            SetRaiseToWakeOn, SetRaiseToWakeOff,
            SetWakeForNotificationsOn, SetWakeForNotificationsOff,
            SetEyeCareOn, SetEyeCareOff,
            SetRefreshRate,
            SetAdaptiveRefreshRateProOn, SetAdaptiveRefreshRateProOff,
            SetPerformanceMode, Set5gOn, Set5gOff,
            SetPreferredSim,
            SetMotionSicknessReliefOn, SetMotionSicknessReliefOff,
            EnableMode, DisableMode,
            OpenApp, SuspendApps, UnsuspendApps,
            IfCondition, Repeat, RepeatCount, Wait, Comment,
            AndCondition, OrCondition,
            CheckWifiOn, CheckWifiOff, CheckBluetoothOn, CheckBluetoothOff,
            CheckBatteryLevel, CheckChargingOn, CheckChargingOff,
            CheckTimeRange, CheckDayOfWeek,
            CheckScreenOn, CheckScreenOff,
            CheckAirplaneOn, CheckAirplaneOff,
            CheckDndOn, CheckDndOff,
            CheckSilentOn, CheckSilentOff,
            CheckMobileDataOn, CheckMobileDataOff,
            CheckNetworkType, CheckMusicPlayingOn, CheckMusicPlayingOff,
            CheckAppForeground,
            CheckAutoRotateOn, CheckAutoRotateOff,
            CheckHotspotOn, CheckHotspotOff,
            CheckNfcOn, CheckNfcOff,
            CheckGpsOn, CheckGpsOff,
            CheckVolumeLevel, CheckBrightnessLevel
            )
        }

        private val byIdMap: Map<String, BlockType> by lazy { all.associateBy { it.id } }

        fun fromId(id: String): BlockType? = byIdMap[id]
    }
}

/**
 * Block 的可配置参数。
 * Boolean 用于开关，Int 用于音量/亮度等数值，Choice 用于固定选项，
 * String 用于包名/SSID 等自由文本。
 */
sealed class BlockParameter {
    abstract val key: String
    abstract val label: String

    data class BooleanParam(
        override val key: String,
        override val label: String,
        val value: Boolean
    ) : BlockParameter()

    data class IntParam(
        override val key: String,
        override val label: String,
        val value: Int,
        val min: Int = 0,
        val max: Int = 100
    ) : BlockParameter()

    data class ChoiceParam(
        override val key: String,
        override val label: String,
        val value: String,
        val options: List<String>
    ) : BlockParameter()

    data class StringParam(
        override val key: String,
        override val label: String,
        val value: String
    ) : BlockParameter()
}

/**
 * 自动化工作流中的一个执行块。支持嵌套子块（用于 IF、REPEAT 等）。
 */
data class AutomationBlock(
    val id: String,
    val type: BlockType,
    val label: String,
    val icon: String,
    val iconColor: Color,
    val parameters: List<BlockParameter> = emptyList(),
    val children: List<AutomationBlock> = emptyList(), // 用于 IF/REPEAT 的嵌套子块
    val elseChildren: List<AutomationBlock> = emptyList() // 用于 IF 的 ELSE 分支
)

/**
 * 将选择操作弹窗中的 [AutomationAction] 转换为带默认参数的 [AutomationBlock]。
 */
fun AutomationAction.toAutomationBlock(): AutomationBlock {
    val type = BlockType.fromId(id) ?: BlockType.OpenApp
    return AutomationBlock(
        id = UUID.randomUUID().toString(),
        type = type,
        label = name,
        icon = icon,
        iconColor = iconColor,
        parameters = defaultParametersFor(type)
    )
}

private fun defaultParametersFor(type: BlockType): List<BlockParameter> = when (type) {
    // ==================== 触发条件 ====================
    is BlockType.TriggerTime -> listOf(
        BlockParameter.StringParam("start", "开始时间 (HH:mm)", ""),
        BlockParameter.ChoiceParam(
            "repeat", "重复", "每天",
            listOf("每天", "工作日", "周末")
        )
    )
    is BlockType.TriggerWifi -> listOf(
        BlockParameter.StringParam("ssid", "WiFi 名称 (SSID)", ""),
        BlockParameter.ChoiceParam(
            "connect", "触发条件", "已加入",
            listOf("已加入", "已断开连接", "已加入或断开连接")
        )
    )
    is BlockType.TriggerWifiOn,
    is BlockType.TriggerWifiOff,
    is BlockType.TriggerBluetooth -> listOf(
        BlockParameter.StringParam("device", "蓝牙设备", ""),
        BlockParameter.ChoiceParam(
            "connect", "触发条件", "已连接",
            listOf("已连接", "已断开连接", "已连接或断开连接")
        )
    )
    is BlockType.TriggerBluetoothOn,
    is BlockType.TriggerBluetoothOff -> emptyList()
    is BlockType.TriggerBattery -> listOf(
        BlockParameter.ChoiceParam("operator", "比较运算符", "低于", listOf("高于", "低于")),
        BlockParameter.IntParam("level", "电量百分比", 20, 0, 100)
    )
    is BlockType.TriggerChargingStart,
    is BlockType.TriggerChargingStop -> emptyList()
    is BlockType.TriggerNetwork -> listOf(
        BlockParameter.ChoiceParam(
            "type", "网络类型", "WiFi",
            listOf("WiFi", "移动数据", "无网络")
        )
    )
    is BlockType.TriggerMusicStart,
    is BlockType.TriggerMusicStop -> emptyList()
    is BlockType.TriggerApp -> listOf(
        BlockParameter.StringParam("package", "应用包名", "")
    )
    is BlockType.TriggerDayOfWeek -> listOf(
        BlockParameter.ChoiceParam(
            "days", "星期", "周一至周五",
            listOf("周一至周五", "周末", "每天")
        )
    )

    // ==================== 系统控制 ====================
    is BlockType.ToggleWifiOn,
    is BlockType.ToggleWifiOff,
    is BlockType.ToggleBluetoothOn,
    is BlockType.ToggleBluetoothOff,
    is BlockType.ToggleMobileDataOn,
    is BlockType.ToggleMobileDataOff,
    is BlockType.ToggleAirplaneOn,
    is BlockType.ToggleAirplaneOff,
    is BlockType.ToggleHotspotOn,
    is BlockType.ToggleHotspotOff,
    is BlockType.ToggleNfcOn,
    is BlockType.ToggleNfcOff,
    is BlockType.ToggleGpsOn,
    is BlockType.ToggleGpsOff,
    is BlockType.ToggleFlashlightOn,
    is BlockType.ToggleFlashlightOff,
    is BlockType.ToggleAutoRotateOn,
    is BlockType.ToggleAutoRotateOff,
    is BlockType.ToggleBatterySaverOn,
    is BlockType.ToggleBatterySaverOff,
    is BlockType.SetSilentModeOn,
    is BlockType.SetSilentModeOff,
    is BlockType.SetAutoBrightnessOn,
    is BlockType.SetAutoBrightnessOff,
    is BlockType.SetGrayscaleOn,
    is BlockType.SetGrayscaleOff,
    is BlockType.SetRaiseToWakeOn,
    is BlockType.SetRaiseToWakeOff,
    is BlockType.SetWakeForNotificationsOn,
    is BlockType.SetWakeForNotificationsOff,
    is BlockType.SetEyeCareOn,
    is BlockType.SetEyeCareOff,
    is BlockType.SetAdaptiveRefreshRateProOn,
    is BlockType.SetAdaptiveRefreshRateProOff,
    is BlockType.Set5gOn,
    is BlockType.Set5gOff,
    is BlockType.SetMotionSicknessReliefOn,
    is BlockType.SetMotionSicknessReliefOff -> emptyList()

    is BlockType.SetDnd -> listOf(
        BlockParameter.ChoiceParam(
            "level", "勿扰级别", "仅优先",
            listOf("关闭", "仅闹钟", "仅优先", "完全静音")
        )
    )

    is BlockType.AdjustVolume -> listOf(
        BlockParameter.IntParam("level", "音量", 50, 0, 100),
        BlockParameter.ChoiceParam("stream", "音量类型", "媒体", listOf("媒体", "铃声", "通知", "闹钟"))
    )

    is BlockType.AdjustBrightness -> listOf(
        BlockParameter.IntParam("level", "亮度", 50, 0, 100)
    )

    // ==================== 显示 ====================
    is BlockType.SetRefreshRate -> listOf(
        BlockParameter.ChoiceParam("rate", "刷新率", "60", listOf("60", "90", "120", "144"))
    )

    // ==================== 设备 ====================
    is BlockType.SetPerformanceMode -> listOf(
        BlockParameter.ChoiceParam("mode", "性能模式", "均衡", listOf("均衡", "性能", "省电"))
    )

    is BlockType.SetPreferredSim -> listOf(
        BlockParameter.ChoiceParam("slot", "数据卡", "SIM 1", listOf("SIM 1", "SIM 2"))
    )

    // ==================== 模式 ====================
    is BlockType.EnableMode,
    is BlockType.DisableMode -> listOf(
        BlockParameter.StringParam("modeId", "模式 ID", "")
    )

    // ==================== 应用 ====================
    is BlockType.OpenApp,
    is BlockType.SuspendApps,
    is BlockType.UnsuspendApps -> listOf(
        BlockParameter.StringParam("packages", "应用包名（逗号分隔）", "")
    )

    // ==================== 控制流 ====================
    is BlockType.IfCondition -> emptyList() // 条件由子块决定
    is BlockType.Repeat -> listOf(
        BlockParameter.IntParam("count", "重复次数", 3, 1, 100)
    )
    is BlockType.RepeatCount -> listOf(
        BlockParameter.IntParam("count", "重复次数", 3, 1, 100)
    )
    is BlockType.Wait -> listOf(
        BlockParameter.IntParam("seconds", "等待秒数", 1, 1, 3600)
    )
    is BlockType.Comment -> emptyList()

    // ==================== 逻辑运算 ====================
    is BlockType.AndCondition,
    is BlockType.OrCondition -> emptyList()

    // ==================== 条件判断 ====================
    is BlockType.CheckWifiOn,
    is BlockType.CheckWifiOff,
    is BlockType.CheckBluetoothOn,
    is BlockType.CheckBluetoothOff,
    is BlockType.CheckChargingOn,
    is BlockType.CheckChargingOff,
    is BlockType.CheckScreenOn,
    is BlockType.CheckScreenOff,
    is BlockType.CheckAirplaneOn,
    is BlockType.CheckAirplaneOff,
    is BlockType.CheckDndOn,
    is BlockType.CheckDndOff,
    is BlockType.CheckSilentOn,
    is BlockType.CheckSilentOff,
    is BlockType.CheckMobileDataOn,
    is BlockType.CheckMobileDataOff,
    is BlockType.CheckMusicPlayingOn,
    is BlockType.CheckMusicPlayingOff,
    is BlockType.CheckAutoRotateOn,
    is BlockType.CheckAutoRotateOff,
    is BlockType.CheckHotspotOn,
    is BlockType.CheckHotspotOff,
    is BlockType.CheckNfcOn,
    is BlockType.CheckNfcOff,
    is BlockType.CheckGpsOn,
    is BlockType.CheckGpsOff -> emptyList()

    is BlockType.CheckBatteryLevel -> listOf(
        BlockParameter.ChoiceParam("operator", "比较运算符", "大于", listOf("大于", "小于", "等于")),
        BlockParameter.IntParam("level", "电量百分比", 50, 0, 100)
    )

    is BlockType.CheckTimeRange -> listOf(
        BlockParameter.StringParam("start", "开始时间 (HH:mm)", "00:00"),
        BlockParameter.StringParam("end", "结束时间 (HH:mm)", "23:59")
    )

    is BlockType.CheckDayOfWeek -> listOf(
        BlockParameter.ChoiceParam(
            "days", "星期", "周一至周五",
            listOf("周一至周五", "周末", "每天")
        )
    )

    is BlockType.CheckNetworkType -> listOf(
        BlockParameter.ChoiceParam(
            "type", "网络类型", "WiFi",
            listOf("WiFi", "移动数据", "无网络")
        )
    )

    is BlockType.CheckAppForeground -> listOf(
        BlockParameter.StringParam("package", "应用包名", "")
    )

    is BlockType.CheckVolumeLevel -> listOf(
        BlockParameter.ChoiceParam("operator", "比较运算符", "大于", listOf("大于", "小于", "等于")),
        BlockParameter.IntParam("level", "音量百分比", 50, 0, 100),
        BlockParameter.ChoiceParam("stream", "音量类型", "媒体", listOf("媒体", "铃声", "通知", "闹钟"))
    )

    is BlockType.CheckBrightnessLevel -> listOf(
        BlockParameter.ChoiceParam("operator", "比较运算符", "大于", listOf("大于", "小于", "等于")),
        BlockParameter.IntParam("level", "亮度百分比", 50, 0, 100)
    )
}
