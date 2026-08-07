package com.banana.hypermodes.automation

import androidx.compose.ui.graphics.Color
import com.banana.hypermodes.ui.AutomationAction
import java.util.UUID

/**
 * 自动化控制块的类型。包含系统控制和控制流（IF、REPEAT、AND、OR 等）。
 */
sealed class BlockType(val id: String) {
    // 系统控制类
    data object ToggleWifi : BlockType("toggle_wifi")
    data object ToggleBluetooth : BlockType("toggle_bluetooth")
    data object SetDnd : BlockType("set_dnd")
    data object AdjustVolume : BlockType("adjust_volume")
    data object AdjustBrightness : BlockType("adjust_brightness")
    data object EnableMode : BlockType("enable_mode")
    data object DisableMode : BlockType("disable_mode")
    data object OpenApp : BlockType("open_app")
    
    // 控制流类
    data object IfCondition : BlockType("if_condition")
    data object Repeat : BlockType("repeat")
    data object RepeatCount : BlockType("repeat_count")
    data object Wait : BlockType("wait")
    data object Comment : BlockType("comment")
    
    // 逻辑运算
    data object AndCondition : BlockType("and_condition")
    data object OrCondition : BlockType("or_condition")
    
    // 条件判断
    data object CheckWifiState : BlockType("check_wifi_state")
    data object CheckBluetoothState : BlockType("check_bluetooth_state")
    data object CheckBatteryLevel : BlockType("check_battery_level")
    data object CheckTimeRange : BlockType("check_time_range")
}

/**
 * Block 的可配置参数。Boolean 用于开关，Int 用于音量/亮度等数值，
 * Choice 用于需要从固定选项中选择的场景（如音量流类型）。
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
    val type = when (id) {
        "toggle_wifi" -> BlockType.ToggleWifi
        "toggle_bluetooth" -> BlockType.ToggleBluetooth
        "set_dnd" -> BlockType.SetDnd
        "adjust_volume" -> BlockType.AdjustVolume
        "adjust_brightness" -> BlockType.AdjustBrightness
        "enable_mode" -> BlockType.EnableMode
        "disable_mode" -> BlockType.DisableMode
        "open_app" -> BlockType.OpenApp
        "if_condition" -> BlockType.IfCondition
        "repeat" -> BlockType.Repeat
        "repeat_count" -> BlockType.RepeatCount
        "wait" -> BlockType.Wait
        "comment" -> BlockType.Comment
        "and_condition" -> BlockType.AndCondition
        "or_condition" -> BlockType.OrCondition
        "check_wifi" -> BlockType.CheckWifiState
        "check_bluetooth" -> BlockType.CheckBluetoothState
        "check_battery" -> BlockType.CheckBatteryLevel
        "check_time" -> BlockType.CheckTimeRange
        else -> BlockType.OpenApp
    }
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
    // 系统控制
    is BlockType.ToggleWifi -> listOf(
        BlockParameter.BooleanParam("enabled", "开启 WiFi", true)
    )
    is BlockType.ToggleBluetooth -> listOf(
        BlockParameter.BooleanParam("enabled", "开启蓝牙", true)
    )
    is BlockType.SetDnd -> listOf(
        BlockParameter.BooleanParam("enabled", "开启勿扰", true)
    )
    is BlockType.AdjustVolume -> listOf(
        BlockParameter.IntParam("level", "音量", 50, 0, 100),
        BlockParameter.ChoiceParam("stream", "音量类型", "媒体", listOf("媒体", "铃声", "通知", "闹钟"))
    )
    is BlockType.AdjustBrightness -> listOf(
        BlockParameter.IntParam("level", "亮度", 50, 0, 100)
    )
    
    // 控制流
    is BlockType.IfCondition -> emptyList() // 条件由子块决定
    is BlockType.Repeat -> emptyList()
    is BlockType.RepeatCount -> listOf(
        BlockParameter.IntParam("count", "重复次数", 3, 1, 100)
    )
    is BlockType.Wait -> listOf(
        BlockParameter.IntParam("seconds", "等待秒数", 1, 1, 3600)
    )
    is BlockType.Comment -> emptyList()
    
    // 逻辑运算
    is BlockType.AndCondition -> emptyList()
    is BlockType.OrCondition -> emptyList()
    
    // 条件判断
    is BlockType.CheckWifiState -> listOf(
        BlockParameter.BooleanParam("expected", "期望状态：开启", true)
    )
    is BlockType.CheckBluetoothState -> listOf(
        BlockParameter.BooleanParam("expected", "期望状态：开启", true)
    )
    is BlockType.CheckBatteryLevel -> listOf(
        BlockParameter.ChoiceParam("operator", "比较运算符", "大于", listOf("大于", "小于", "等于")),
        BlockParameter.IntParam("level", "电量百分比", 50, 0, 100)
    )
    is BlockType.CheckTimeRange -> listOf(
        BlockParameter.ChoiceParam("start", "开始时间", "00:00", emptyList()),
        BlockParameter.ChoiceParam("end", "结束时间", "23:59", emptyList())
    )
    
    is BlockType.EnableMode,
    is BlockType.DisableMode,
    is BlockType.OpenApp -> emptyList()
}
