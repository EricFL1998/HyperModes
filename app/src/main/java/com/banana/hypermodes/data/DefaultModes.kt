package com.banana.hypermodes.data

/** Factory for the three built-in modes (勿扰 / 睡眠 / 驾驶). */
object DefaultModes {
    fun get(): List<Mode> = listOf(
        Mode(
            id = "dnd",
            name = "勿扰模式",
            icon = "🔕",
            statusIcon = "ic_stat_mute",
            description = "静音通知和来电",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = DndLevel.PRIORITY
            )
        ),
        Mode(
            id = "bedtime",
            name = "睡眠模式",
            icon = "🌙",
            statusIcon = "ic_stat_moon",
            description = "每晚 23:00 - 次日 07:00",
            settings = ModeSettings(
                enableDnd = true,
                enableGrayscale = true,
                darkMode = 1, // Dark
                schedule = ModeSchedule(
                    enabled = true,
                    startHour = 23,
                    startMinute = 0,
                    endHour = 7,
                    endMinute = 0
                )
            )
        ),
        Mode(
            id = "driving",
            name = "驾驶模式",
            icon = "🚗",
            statusIcon = "ic_stat_car",
            description = "通过设备运动和蓝牙连接自动开启",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = DndLevel.PRIORITY,
                hideNotifications = true,
                drivingAutoDetect = true
            )
        )
    )
}
