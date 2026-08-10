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

    /** 目录条目：与 [BlockType.id] 一一对应。 */
    data class Entry(
        val id: String,
        val name: String,
        val icon: String,
        val iconColor: Color,
        val description: String,
        val category: Category
    )

    val entries: List<Entry> = listOf(
        // ==================== 触发条件 ====================
        Entry("trigger_time", "时间触发", "⏰", Color(0xFFFF9500), "当到达指定时间段时触发", Category.TRIGGER),
        Entry("trigger_wifi", "WiFi 触发", "📶", Color(0xFF007AFF), "当连接指定 WiFi 时触发", Category.TRIGGER),
        Entry("trigger_wifi_state", "WiFi 状态触发", "📶", Color(0xFF007AFF), "当 WiFi 开启/关闭时触发", Category.TRIGGER),
        Entry("trigger_bluetooth", "蓝牙触发", "🔵", Color(0xFF007AFF), "当蓝牙开启或连接设备时触发", Category.TRIGGER),
        Entry("trigger_battery", "电量触发", "🔋", Color(0xFF34C759), "当电量满足条件时触发", Category.TRIGGER),
        Entry("trigger_charging", "充电触发", "🔌", Color(0xFF34C759), "当开始/停止充电时触发", Category.TRIGGER),
        Entry("trigger_network", "网络触发", "🌐", Color(0xFF34C759), "当网络类型变化时触发", Category.TRIGGER),
        Entry("trigger_music", "音乐触发", "🎵", Color(0xFFFF2D55), "当音乐开始/停止播放时触发", Category.TRIGGER),
        Entry("trigger_app", "应用触发", "📱", Color(0xFF5E5CE6), "当指定应用打开时触发", Category.TRIGGER),
        Entry("trigger_day_of_week", "星期触发", "📅", Color(0xFFFF9500), "当到达指定星期时触发", Category.TRIGGER),

        // ==================== 系统控制 ====================
        Entry("toggle_wifi", "WiFi 开关", "📶", Color(0xFF007AFF), "切换 WiFi 开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_bluetooth", "蓝牙开关", "🔵", Color(0xFF007AFF), "切换蓝牙开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_mobile_data", "移动数据", "📡", Color(0xFF34C759), "切换移动数据开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_airplane", "飞行模式", "✈️", Color(0xFFFF9500), "切换飞行模式开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_hotspot", "个人热点", "📲", Color(0xFF5856D6), "切换个人热点开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_nfc", "NFC 开关", "📇", Color(0xFF30B0C7), "切换 NFC 开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_gps", "定位开关", "📍", Color(0xFF34C759), "切换定位服务开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_flashlight", "手电筒", "🔦", Color(0xFFFFCC00), "切换手电筒开关状态", Category.SYSTEM_CONTROL),
        Entry("toggle_auto_rotate", "自动旋转", "🔄", Color(0xFF8E8E93), "切换屏幕自动旋转状态", Category.SYSTEM_CONTROL),
        Entry("toggle_battery_saver", "省电模式", "🔋", Color(0xFF34C759), "切换省电模式开关状态", Category.SYSTEM_CONTROL),
        Entry("set_silent_mode", "静音模式", "🔇", Color(0xFFFF3B30), "开启或关闭静音模式", Category.SYSTEM_CONTROL),
        Entry("set_dnd", "勿扰模式", "🔕", Color(0xFFFF3B30), "设置勿扰模式级别", Category.SYSTEM_CONTROL),
        Entry("adjust_volume", "调节音量", "🔊", Color(0xFFFF9500), "调整设备音量", Category.SYSTEM_CONTROL),
        Entry("adjust_brightness", "调节亮度", "💡", Color(0xFFFFCC00), "调整屏幕亮度", Category.SYSTEM_CONTROL),
        Entry("set_auto_brightness", "自动亮度", "☀️", Color(0xFFFFCC00), "切换自动亮度开关状态", Category.SYSTEM_CONTROL),

        // ==================== 显示 ====================
        Entry("set_dark_mode", "深色模式", "🌙", Color(0xFF5E5CE6), "切换深色模式开关状态", Category.DISPLAY),
        Entry("set_grayscale", "灰度模式", "⬜", Color(0xFF8E8E93), "切换屏幕灰度显示", Category.DISPLAY),
        Entry("set_aod", "息屏显示", "🕐", Color(0xFF30B0C7), "切换息屏显示（AOD）开关", Category.DISPLAY),
        Entry("set_raise_to_wake", "抬腕亮屏", "🤚", Color(0xFF30B0C7), "切换抬腕亮屏开关", Category.DISPLAY),
        Entry("set_wake_for_notifications", "通知亮屏", "🔔", Color(0xFF30B0C7), "切换通知亮屏开关", Category.DISPLAY),
        Entry("set_eye_care", "纸质护眼", "📖", Color(0xFF34C759), "切换纸质护眼模式", Category.DISPLAY),
        Entry("set_refresh_rate", "屏幕刷新率", "⚡", Color(0xFFFF9500), "设置屏幕刷新率", Category.DISPLAY),
        Entry("set_adaptive_refresh_rate_pro", "自适应刷新率 Pro", "🚀", Color(0xFF5856D6), "切换自适应刷新率 Pro", Category.DISPLAY),

        // ==================== 设备 ====================
        Entry("set_performance_mode", "性能模式", "🏎️", Color(0xFFFF9500), "设置性能模式（均衡/性能/省电）", Category.DEVICE),
        Entry("set_5g", "5G 开关", "📶", Color(0xFF34C759), "切换 5G 网络开关", Category.DEVICE),
        Entry("set_preferred_sim", "默认数据卡", "📱", Color(0xFF007AFF), "切换默认数据 SIM 卡", Category.DEVICE),
        Entry("set_motion_sickness_relief", "防晕车", "🎢", Color(0xFF34C759), "切换防晕车辅助开关", Category.DEVICE),

        // ==================== 模式 ====================
        Entry("enable_mode", "启用模式", "🌙", Color(0xFF5E5CE6), "启用指定的 HyperMode", Category.MODE),
        Entry("disable_mode", "关闭模式", "☀️", Color(0xFFFF9500), "关闭指定的 HyperMode", Category.MODE),

        // ==================== 应用 ====================
        Entry("open_app", "打开 App", "📱", Color(0xFF5E5CE6), "启动指定应用程序", Category.APP),
        Entry("suspend_apps", "暂停应用", "⏸️", Color(0xFF5856D6), "暂停（置灰）指定应用", Category.APP),
        Entry("unsuspend_apps", "恢复应用", "▶️", Color(0xFF34C759), "恢复被暂停的应用", Category.APP),

        // ==================== 控制流 ====================
        Entry("if_condition", "If 条件判断", "🔀", Color(0xFF34C759), "根据条件执行不同操作", Category.CONTROL_FLOW),
        Entry("repeat_count", "重复 N 次", "🔁", Color(0xFF5856D6), "重复执行指定次数", Category.CONTROL_FLOW),
        Entry("wait", "等待", "⏱️", Color(0xFFFF9500), "暂停执行一段时间", Category.CONTROL_FLOW),
        Entry("comment", "注释", "💬", Color(0xFF8E8E93), "添加说明文字", Category.CONTROL_FLOW),

        // ==================== 逻辑 ====================
        Entry("and_condition", "AND 与运算", "➕", Color(0xFF007AFF), "所有条件都满足", Category.LOGIC),
        Entry("or_condition", "OR 或运算", "〰️", Color(0xFF007AFF), "任一条件满足", Category.LOGIC),

        // ==================== 条件判断 ====================
        Entry("check_wifi", "检查 WiFi 状态", "📶", Color(0xFF30B0C7), "判断 WiFi 是否开启", Category.CONDITION),
        Entry("check_bluetooth", "检查蓝牙状态", "🔵", Color(0xFF30B0C7), "判断蓝牙是否开启", Category.CONDITION),
        Entry("check_battery", "检查电量", "🔋", Color(0xFF30B0C7), "判断电量是否满足条件", Category.CONDITION),
        Entry("check_charging", "检查充电状态", "🔌", Color(0xFF34C759), "判断是否正在充电", Category.CONDITION),
        Entry("check_time", "检查时间范围", "🕐", Color(0xFF30B0C7), "判断当前时间是否在范围内", Category.CONDITION),
        Entry("check_day_of_week", "检查星期", "📅", Color(0xFFFF9500), "判断今天是星期几", Category.CONDITION),
        Entry("check_dark_mode", "检查深色模式", "🌙", Color(0xFF5E5CE6), "判断深色模式是否开启", Category.CONDITION),
        Entry("check_screen", "检查屏幕状态", "🖥️", Color(0xFF30B0C7), "判断屏幕是否亮着", Category.CONDITION),
        Entry("check_airplane", "检查飞行模式", "✈️", Color(0xFFFF9500), "判断飞行模式是否开启", Category.CONDITION),
        Entry("check_dnd_state", "检查勿扰状态", "🔕", Color(0xFFFF3B30), "判断勿扰模式是否开启", Category.CONDITION),
        Entry("check_silent", "检查静音状态", "🔇", Color(0xFFFF3B30), "判断静音模式是否开启", Category.CONDITION),
        Entry("check_mobile_data", "检查移动数据", "📡", Color(0xFF34C759), "判断移动数据是否开启", Category.CONDITION),
        Entry("check_network_type", "检查网络类型", "🌐", Color(0xFF34C759), "判断当前网络类型", Category.CONDITION),
        Entry("check_music_playing", "检查音乐播放", "🎵", Color(0xFFFF2D55), "判断是否有音乐正在播放", Category.CONDITION),
        Entry("check_app_foreground", "检查应用前台", "📱", Color(0xFF5E5CE6), "判断指定应用是否在前台", Category.CONDITION),
        Entry("check_auto_rotate", "检查自动旋转", "🔄", Color(0xFF8E8E93), "判断自动旋转是否开启", Category.CONDITION),
        Entry("check_hotspot", "检查热点", "📲", Color(0xFF5856D6), "判断个人热点是否开启", Category.CONDITION),
        Entry("check_nfc", "检查 NFC", "📇", Color(0xFF30B0C7), "判断 NFC 是否开启", Category.CONDITION),
        Entry("check_gps", "检查定位", "📍", Color(0xFF34C759), "判断定位服务是否开启", Category.CONDITION),
        Entry("check_volume", "检查音量", "🔊", Color(0xFFFF9500), "判断音量是否满足条件", Category.CONDITION),
        Entry("check_brightness", "检查亮度", "💡", Color(0xFFFFCC00), "判断亮度是否满足条件", Category.CONDITION)
    )

    private val byIdMap: Map<String, Entry> = entries.associateBy { it.id }

    fun byId(id: String): Entry? = byIdMap[id]

    /** 按分类分组的条目（保持目录顺序）。 */
    fun grouped(): Map<Category, List<Entry>> = entries.groupBy { it.category }
}
