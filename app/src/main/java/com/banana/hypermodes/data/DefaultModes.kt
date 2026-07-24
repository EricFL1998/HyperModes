package com.banana.hypermodes.data

/** Factory for the three built-in modes (勿扰 / 睡眠 / 驾驶). */
object DefaultModes {
    fun get(): List<Mode> = listOf(
        Mode(
            id = "dnd",
            name = "Do Not Disturb",
            icon = "🔕",
            statusIcon = "ic_stat_mute",
            description = "Silence notifications and calls",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = DndLevel.PRIORITY
            )
        ),
        Mode(
            id = "bedtime",
            name = "Bedtime",
            icon = "🌙",
            statusIcon = "ic_stat_moon",
            description = "From 11:00 pm - 7:00 am",
            settings = ModeSettings(
                enableDnd = true,
                enableGrayscale = true,
                dimWallpaper = true,
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
            name = "Driving",
            icon = "🚗",
            statusIcon = "ic_stat_car",
            description = "Using device's motion and Bluetooth connection",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = DndLevel.PRIORITY,
                hideNotifications = true,
                drivingAutoDetect = true
            )
        )
    )
}
