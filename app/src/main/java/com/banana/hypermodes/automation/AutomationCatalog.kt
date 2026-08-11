package com.banana.hypermodes.automation

import androidx.compose.ui.graphics.Color

/**
 * 自动化操作目录 —— 应用所有可执行操作的单一事实来源。
 *
 * UI 操作选择器、块类型映射、默认参数都从这里派生，保证新增操作只需改这一处。
 */
object AutomationCatalog {

    /** 操作分组（对应选择器里的分类标签）。 */
    enum class Category(val label: String) {
        TRIGGER("触发条件"),
        SYSTEM_CONTROL("系统控制"),
        DISPLAY("显示"),
        DEVICE("设备"),
        MODE("模式"),
        APP("应用"),
        CONTROL_FLOW("控制流"),
        LOGIC("逻辑"),
        CONDITION("条件判断")
    }

    /**
     * 目录条目：与 [BlockType.id] 一一对应。
     * @param hidden 为 true 时不在 UI 选择器中显示（仅用于动态/内部类型，
     *             但仍需存在于目录保证 BlockType 与 catalog 的完备性一致）。
     */
    data class Entry(
        val id: String,
        val name: String,
        val icon: String,
        val iconColor: Color,
        val description: String,
        val category: Category,
        val hidden: Boolean = false
    )

    val entries: List<Entry> = listOf(
        // ==================== 触发条件 ====================
        Entry("trigger_time", "时间触发", "⏰", Color(0xFFFF9500), "当到达指定时间段时触发", Category.TRIGGER),
        Entry("trigger_wifi", "WiFi 触发", "📶", Color(0xFF007AFF), "当连接指定 WiFi 时触发", Category.TRIGGER),
        Entry("trigger_bluetooth", "蓝牙设备触发", "🔵", Color(0xFF007AFF), "当连接指定蓝牙设备时触发", Category.TRIGGER),
        Entry("trigger_battery", "电量触发", "🔋", Color(0xFF34C759), "当电量满足条件时触发", Category.TRIGGER),
        Entry("trigger_charging", "充电触发", "🔌", Color(0xFF34C759), "当充电状态变化时触发", Category.TRIGGER),
        Entry("trigger_network", "网络触发", "🌐", Color(0xFF34C759), "当网络类型变化时触发", Category.TRIGGER),
        Entry("trigger_music", "音乐触发", "🎵", Color(0xFFFF2D55), "当音乐播放状态变化时触发", Category.TRIGGER),
        Entry("trigger_app", "应用触发", "📱", Color(0xFF5E5CE6), "当指定应用打开时触发", Category.TRIGGER),
        Entry("trigger_day_of_week", "星期触发", "📅", Color(0xFFFF9500), "当到达指定星期时触发", Category.TRIGGER),

        // ==================== 系统控制 ====================
        Entry("toggle_wifi", "WiFi", "📶", Color(0xFF007AFF), "开启或关闭 WiFi", Category.SYSTEM_CONTROL),
        Entry("toggle_bluetooth", "蓝牙", "🔵", Color(0xFF007AFF), "开启或关闭蓝牙", Category.SYSTEM_CONTROL),
        Entry("toggle_mobile_data", "移动数据", "📡", Color(0xFF34C759), "开启或关闭移动数据", Category.SYSTEM_CONTROL),
        Entry("toggle_airplane", "飞行模式", "✈️", Color(0xFFFF9500), "开启或关闭飞行模式", Category.SYSTEM_CONTROL),
        Entry("toggle_hotspot", "个人热点", "📲", Color(0xFF5856D6), "开启或关闭个人热点", Category.SYSTEM_CONTROL),
        Entry("toggle_nfc", "NFC", "📇", Color(0xFF30B0C7), "开启或关闭 NFC", Category.SYSTEM_CONTROL),
        Entry("toggle_gps", "定位", "📍", Color(0xFF34C759), "开启或关闭定位服务", Category.SYSTEM_CONTROL),
        Entry("toggle_flashlight", "手电筒", "🔦", Color(0xFFFFCC00), "开启或关闭手电筒", Category.SYSTEM_CONTROL),
        Entry("toggle_auto_rotate", "自动旋转", "🔄", Color(0xFF8E8E93), "开启或关闭屏幕自动旋转", Category.SYSTEM_CONTROL),
        Entry("toggle_battery_saver", "省电模式", "🔋", Color(0xFF34C759), "开启或关闭省电模式", Category.SYSTEM_CONTROL),
        Entry("set_silent_mode", "静音", "🔇", Color(0xFFFF3B30), "开启或关闭静音模式", Category.SYSTEM_CONTROL),
        Entry("set_dnd", "勿扰模式", "🔕", Color(0xFFFF3B30), "设置勿扰模式级别", Category.SYSTEM_CONTROL),
        Entry("adjust_volume", "调节音量", "🔊", Color(0xFFFF9500), "调整设备音量", Category.SYSTEM_CONTROL),
        Entry("adjust_brightness", "调节亮度", "💡", Color(0xFFFFCC00), "调整屏幕亮度", Category.SYSTEM_CONTROL),
        Entry("set_auto_brightness", "自动亮度", "☀️", Color(0xFFFFCC00), "开启或关闭自动亮度", Category.SYSTEM_CONTROL),

        // ==================== 显示 ====================
        Entry("set_dark_mode", "深色模式", "🌙", Color(0xFF5E5CE6), "切换浅色/深色模式", Category.DISPLAY),
        Entry("set_grayscale", "灰度模式", "⬜", Color(0xFF8E8E93), "开启或关闭屏幕灰度显示", Category.DISPLAY),
        Entry("set_raise_to_wake", "抬腕亮屏", "🤚", Color(0xFF30B0C7), "开启或关闭抬腕亮屏", Category.DISPLAY),
        Entry("set_wake_for_notifications", "通知亮屏", "🔔", Color(0xFF30B0C7), "开启或关闭通知亮屏", Category.DISPLAY),
        Entry("set_eye_care", "纸质护眼", "📖", Color(0xFF34C759), "开启或关闭纸质护眼模式", Category.DISPLAY),
        Entry("set_refresh_rate", "屏幕刷新率", "⚡", Color(0xFFFF9500), "设置屏幕刷新率", Category.DISPLAY),
        Entry("set_adaptive_refresh_rate_pro", "自适应刷新率 Pro", "🚀", Color(0xFF5856D6), "开启或关闭自适应刷新率 Pro", Category.DISPLAY),
        Entry("set_aod", "息屏显示", "🕐", Color(0xFF30B0C7), "开启或关闭息屏显示（AOD）", Category.DISPLAY),

        // ==================== 设备 ====================
        Entry("set_performance_mode", "性能模式", "🏎️", Color(0xFFFF9500), "设置性能模式（均衡/性能/省电）", Category.DEVICE),
        Entry("set_5g", "5G", "📶", Color(0xFF34C759), "开启或关闭 5G 网络", Category.DEVICE),
        Entry("set_preferred_sim", "默认数据卡", "📱", Color(0xFF007AFF), "切换默认数据 SIM 卡", Category.DEVICE),
        Entry("set_motion_sickness_relief", "防晕车", "🎢", Color(0xFF34C759), "开启或关闭防晕车辅助", Category.DEVICE),

        // ==================== 模式 ====================
        Entry("set_mode", "切换模式", "🌙", Color(0xFF5E5CE6), "启用或关闭指定的 HyperMode", Category.MODE),

        // ==================== 应用 ====================
        Entry("open_app", "打开 App", "📱", Color(0xFF5E5CE6), "启动指定应用程序", Category.APP),
        Entry("suspend_apps", "暂停应用", "⏸️", Color(0xFF5856D6), "暂停（置灰）指定应用", Category.APP),
        Entry("unsuspend_apps", "恢复应用", "▶️", Color(0xFF34C759), "恢复被暂停的应用", Category.APP),
        // 发送意图由已导入的意图动态生成；保留隐藏条目保证 BlockType 完备性
        Entry("send_intent", "应用意图", "📨", Color(0xFF5856D6), "向已导入应用发送广播意图", Category.APP, hidden = true),

        // ==================== 控制流 ====================
        Entry("if_condition", "如果条件判断", "🔀", Color(0xFF34C759), "根据条件执行不同操作", Category.CONTROL_FLOW),
        Entry("repeat_count", "重复 N 次", "🔁", Color(0xFF5856D6), "重复执行指定次数", Category.CONTROL_FLOW),
        Entry("wait", "等待", "⏱️", Color(0xFFFF9500), "暂停执行一段时间", Category.CONTROL_FLOW),
        Entry("comment", "注释", "💬", Color(0xFF8E8E93), "添加说明文字", Category.CONTROL_FLOW),

        // ==================== 逻辑 ====================
        Entry("and_condition", "并且（全部满足）", "➕", Color(0xFF007AFF), "所有条件都满足", Category.LOGIC),
        Entry("or_condition", "或者（任一满足）", "〰️", Color(0xFF007AFF), "任一条件满足", Category.LOGIC),
        Entry("trigger_intent", "当收到意图时", "🔔", Color(0xFF5856D6), "监听广播意图，收到时执行 {} 内操作（拖入意图块绑定）", Category.LOGIC),

        // ==================== 条件判断 ====================
        Entry("check_wifi", "WiFi 状态", "📶", Color(0xFF30B0C7), "判断 WiFi 开关状态", Category.CONDITION),
        Entry("check_bluetooth", "蓝牙状态", "🔵", Color(0xFF30B0C7), "判断蓝牙开关状态", Category.CONDITION),
        Entry("check_battery", "检查电量", "🔋", Color(0xFF30B0C7), "判断电量是否满足条件", Category.CONDITION),
        Entry("check_charging", "充电状态", "🔌", Color(0xFF34C759), "判断充电状态", Category.CONDITION),
        Entry("check_time", "检查时间范围", "🕐", Color(0xFF30B0C7), "判断当前时间是否在范围内", Category.CONDITION),
        Entry("check_day_of_week", "检查星期", "📅", Color(0xFFFF9500), "判断今天是星期几", Category.CONDITION),
        Entry("check_screen", "屏幕状态", "🖥️", Color(0xFF30B0C7), "判断屏幕亮灭状态", Category.CONDITION),
        Entry("check_airplane", "飞行模式状态", "✈️", Color(0xFFFF9500), "判断飞行模式开关状态", Category.CONDITION),
        Entry("check_dnd_state", "勿扰模式状态", "🔕", Color(0xFFFF3B30), "判断勿扰模式开关状态", Category.CONDITION),
        Entry("check_silent", "静音模式状态", "🔇", Color(0xFFFF3B30), "判断静音模式开关状态", Category.CONDITION),
        Entry("check_mobile_data", "移动数据状态", "📡", Color(0xFF34C759), "判断移动数据开关状态", Category.CONDITION),
        Entry("check_network_type", "检查网络类型", "🌐", Color(0xFF34C759), "判断当前网络类型", Category.CONDITION),
        Entry("check_music_playing", "音乐播放状态", "🎵", Color(0xFFFF2D55), "判断音乐播放状态", Category.CONDITION),
        Entry("check_app_foreground", "检查应用前台", "📱", Color(0xFF5E5CE6), "判断指定应用是否在前台", Category.CONDITION),
        Entry("check_auto_rotate", "自动旋转状态", "🔄", Color(0xFF8E8E93), "判断自动旋转开关状态", Category.CONDITION),
        Entry("check_hotspot", "热点状态", "📲", Color(0xFF5856D6), "判断个人热点开关状态", Category.CONDITION),
        Entry("check_nfc", "NFC 状态", "📇", Color(0xFF30B0C7), "判断 NFC 开关状态", Category.CONDITION),
        Entry("check_gps", "定位状态", "📍", Color(0xFF34C759), "判断定位服务开关状态", Category.CONDITION),
        Entry("check_volume", "检查音量", "🔊", Color(0xFFFF9500), "判断音量是否满足条件", Category.CONDITION),
        Entry("check_brightness", "检查亮度", "💡", Color(0xFFFFCC00), "判断亮度是否满足条件", Category.CONDITION)
    )

    private val byIdMap: Map<String, Entry> = entries.associateBy { it.id }

    /** 非隐藏条目：用于 UI 选择器。 */
    val visibleEntries: List<Entry> get() = entries.filter { !it.hidden }

    /** 按分类分组的非隐藏条目（保持目录顺序）：用于 UI 选择器。 */
    fun grouped(): Map<Category, List<Entry>> = visibleEntries.groupBy { it.category }

    /** 按分类分组的全部条目（包含隐藏条目，用于测试/内部使用）。 */
    fun allGrouped(): Map<Category, List<Entry>> = entries.groupBy { it.category }

    fun byId(id: String): Entry? = byIdMap[id]
}

