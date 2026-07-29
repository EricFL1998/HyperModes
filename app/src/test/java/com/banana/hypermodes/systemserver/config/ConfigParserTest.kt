package com.banana.hypermodes.systemserver.config

import org.junit.Assert.*
import org.junit.Test

class ConfigParserTest {

    @Test
    fun testParseBasicConfig() {
        val json = """
        {
            "activeModeId": "work_mode",
            "modes": [
                {
                    "id": "work_mode",
                    "name": "Work",
                    "icon": "work",
                    "type": "SCHEDULED",
                    "startTime": "09:00",
                    "endTime": "17:00",
                    "repeatDays": [1, 2, 3, 4, 5],
                    "notification": {
                        "dndLevel": "PRIORITY",
                        "allowedApps": ["com.android.messaging", "com.google.android.apps.messaging"]
                    },
                    "display": {
                        "darkMode": 0,
                        "grayscale": true
                    },
                    "pausedApps": ["com.facebook.katana", "com.instagram.android"],
                    "contactFilter": "STARRED"
                }
            ]
        }
        """.trimIndent()

        val config = ConfigParser.parseConfig(json)

        assertNotNull(config)
        assertEquals("work_mode", config.activeModeId)
        assertEquals(1, config.modes.size)

        val mode = config.modes[0]
        assertEquals("work_mode", mode.id)
        assertEquals("Work", mode.name)
        assertEquals("work", mode.icon)
        assertEquals(ModeType.SCHEDULED, mode.type)
        assertEquals("09:00", mode.startTime)
        assertEquals("17:00", mode.endTime)
        assertNotNull(mode.repeatDays)
        assertEquals(listOf(1, 2, 3, 4, 5), mode.repeatDays)

        assertEquals(DndLevel.PRIORITY, mode.notification.dndLevel)
        assertEquals(2, mode.notification.allowedApps.size)

        assertEquals(0, mode.display.darkMode)
        assertEquals(true, mode.display.grayscale)

        assertEquals(2, mode.pausedApps.size)
        assertEquals(ContactFilter.STARRED, mode.contactFilter)
    }

    @Test
    fun testUpdateActiveModeId() {
        val json = """
        {
            "activeModeId": "work_mode",
            "modes": [
                {
                    "id": "work_mode",
                    "name": "Work",
                    "icon": "work",
                    "type": "SCHEDULED",
                    "notification": {
                        "dndLevel": "PRIORITY",
                        "allowedApps": []
                    },
                    "display": {
                        "darkMode": 0,
                        "grayscale": false
                    },
                    "pausedApps": [],
                    "contactFilter": "ALL"
                }
            ]
        }
        """.trimIndent()

        val dismissals = mapOf("custom_1" to 123456789L)
        val updated = ConfigParser.updateActiveModeId(json, "driving_mode", dismissals)
        val config = ConfigParser.parseConfig(updated)

        assertEquals("driving_mode", config.activeModeId)
        assertEquals(1, config.modes.size) // modes should be unchanged
        assertEquals(123456789L, config.dismissedModes["custom_1"])
    }

    @Test
    fun testDismissedModesRoundTrip() {
        val config = FullConfig(
            modes = emptyList(),
            dismissedModes = mapOf("mode1" to 1000L, "mode2" to 2000L)
        )
        val json = ConfigParser.serializeConfig(config)
        val parsed = ConfigParser.parseConfig(json)
        
        assertEquals(2, parsed.dismissedModes.size)
        assertEquals(1000L, parsed.dismissedModes["mode1"])
        assertEquals(2000L, parsed.dismissedModes["mode2"])
    }

    @Test
    fun testSerializeConfig() {
        val config = FullConfig(
            activeModeId = "bedtime",
            modes = listOf(
                ModeConfig(
                    id = "bedtime",
                    name = "Bedtime",
                    icon = "bedtime",
                    type = ModeType.SCHEDULED,
                    startTime = "22:00",
                    endTime = "07:00",
                    repeatDays = listOf(1, 2, 3, 4, 5, 6, 7),
                    triggers = null,
                    notification = NotificationConfig(
                        dndLevel = DndLevel.PRIORITY,
                        allowedApps = emptyList()
                    ),
                    display = DisplayConfig(
                        darkMode = 1,
                        grayscale = true
                    ),
                    pausedApps = emptyList(),
                    contactFilter = ContactFilter.STARRED
                )
            )
        )

        val json = ConfigParser.serializeConfig(config)
        val parsed = ConfigParser.parseConfig(json)

        assertEquals(config.activeModeId, parsed.activeModeId)
        assertEquals(config.modes.size, parsed.modes.size)
        assertEquals(config.modes[0].id, parsed.modes[0].id)
        assertEquals(config.modes[0].name, parsed.modes[0].name)
    }

    @Test
    fun testScheduleEnabledRoundTrip() {
        val config = FullConfig(
            activeModeId = null,
            modes = listOf(
                ModeConfig(
                    id = "custom_work",
                    name = "Work",
                    icon = "work",
                    type = ModeType.SCHEDULED,
                    startTime = "09:30",
                    endTime = "17:45",
                    repeatDays = listOf(1, 2, 3, 4, 5),
                    scheduleEnabled = false,
                    notification = NotificationConfig(DndLevel.PRIORITY),
                    display = DisplayConfig(),
                    pausedApps = emptyList()
                )
            )
        )

        val parsed = ConfigParser.parseConfig(ConfigParser.serializeConfig(config))
        val mode = parsed.modes.single()

        assertEquals("09:30", mode.startTime)
        assertEquals("17:45", mode.endTime)
        assertEquals(listOf(1, 2, 3, 4, 5), mode.repeatDays)
        assertEquals(false, mode.scheduleEnabled)
    }

    @Test
    fun testLegacyConfigWithoutScheduleEnabled() {
        val json = """
        {
            "activeModeId": null,
            "modes": [{
                "id": "custom_legacy",
                "name": "Legacy",
                "icon": "work",
                "type": "SCHEDULED",
                "startTime": "08:10",
                "endTime": "12:20",
                "repeatDays": [1, 3, 5],
                "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
                "display": {},
                "pausedApps": []
            }]
        }
        """.trimIndent()

        val mode = ConfigParser.parseConfig(json).modes.single()

        assertNull(mode.scheduleEnabled)
        assertEquals("08:10", mode.startTime)
        assertEquals("12:20", mode.endTime)
    }

    @Test
    fun testBedtimeModeType() {
        val json = """
        {
            "activeModeId": "bedtime_mode",
            "modes": [
                {
                    "id": "bedtime_mode",
                    "name": "Bedtime",
                    "icon": "bedtime",
                    "type": "BEDTIME",
                    "startTime": "22:00",
                    "endTime": "07:00",
                    "repeatDays": [1, 2, 3, 4, 5, 6, 7],
                    "notification": {
                        "dndLevel": "PRIORITY",
                        "allowedApps": []
                    },
                    "display": {
                        "darkMode": 1,
                        "grayscale": true,
                        "keepScreenOff": false
                    },
                    "pausedApps": [],
                    "contactFilter": "STARRED"
                }
            ]
        }
        """.trimIndent()

        val config = ConfigParser.parseConfig(json)

        assertNotNull(config)
        assertEquals("bedtime_mode", config.activeModeId)
        assertEquals(1, config.modes.size)

        val mode = config.modes[0]
        assertEquals("bedtime_mode", mode.id)
        assertEquals("Bedtime", mode.name)
        assertEquals(ModeType.BEDTIME, mode.type)
        assertEquals("22:00", mode.startTime)
        assertEquals("07:00", mode.endTime)
        assertEquals(ContactFilter.STARRED, mode.contactFilter)
    }

    @Test
    fun testLegacyBooleanDisplayConfigMigrates() {
        // Configs written before tri-state display overrides (pre-v1.2) always
        // encoded every display toggle as a boolean, where "false" meant "this
        // mode does not touch the setting" — not "force it off".
        val json = """
        {
            "activeModeId": "bedtime",
            "modes": [
                {
                    "id": "bedtime",
                    "name": "Bedtime",
                    "icon": "bedtime",
                    "type": "BEDTIME",
                    "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
                    "display": {
                        "darkMode": true,
                        "grayscale": true,
                        "dimWallpaper": false,
                        "keepScreenOff": false,
                        "eyeCare": false,
                        "enableRefreshRate": false,
                        "refreshRate": 60
                    },
                    "pausedApps": [],
                    "contactFilter": "ALL"
                },
                {
                    "id": "driving",
                    "name": "Driving",
                    "icon": "driving",
                    "type": "DYNAMIC_TRIGGER",
                    "notification": { "dndLevel": "NONE", "allowedApps": [] },
                    "display": {
                        "darkMode": false,
                        "grayscale": false,
                        "dimWallpaper": false,
                        "keepScreenOff": false,
                        "eyeCare": false,
                        "enableRefreshRate": false,
                        "refreshRate": 60
                    },
                    "pausedApps": [],
                    "contactFilter": "ALL"
                }
            ]
        }
        """.trimIndent()

        val config = ConfigParser.parseConfig(json)

        val bedtime = config.modes[0]
        // darkMode was a plain on/off: "on" maps to Dark (1)
        assertEquals(1, bedtime.display.darkMode)
        assertEquals(true, bedtime.display.grayscale)
        // "off" toggles meant "ignore", not "force off"
        assertNull(bedtime.display.eyeCare)
        assertNull(bedtime.display.enableRefreshRate)

        val driving = config.modes[1]
        // darkMode "off" meant "ignore", not "force light"
        assertNull(driving.display.darkMode)
        assertNull(driving.display.grayscale)
    }

    @Test
    fun testNewFormatExplicitFalsePreserved() {
        // Tri-state configs deliberately store false ("force off"); the legacy
        // migration must not rewrite them.
        val json = """
        {
            "activeModeId": null,
            "modes": [{
                "id": "work",
                "name": "Work",
                "icon": "work",
                "type": "SCHEDULED",
                "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
                "display": {
                    "darkMode": 0,
                    "grayscale": false,
                    "eyeCare": false,
                    "enableRefreshRate": false,
                    "enableAod": false,
                    "refreshRate": 120
                },
                "pausedApps": [],
                "contactFilter": "ALL"
            }]
        }
        """.trimIndent()

        val display = ConfigParser.parseConfig(json).modes.single().display

        assertEquals(0, display.darkMode)
        assertEquals(false, display.grayscale)
        assertEquals(false, display.eyeCare)
        assertEquals(false, display.enableRefreshRate)
        assertEquals(false, display.enableAod)
        assertEquals(120, display.refreshRate)
    }

    @Test
    fun testV12DeviceWakeKeysMoveToDisplay() {
        // v1.2 stored raise-to-wake / wake-for-notifications under "device";
        // v1.3 moved them into "display". The values must follow the move,
        // including explicit false ("force off").
        val json = """
        {
            "activeModeId": null,
            "modes": [{
                "id": "work",
                "name": "Work",
                "icon": "work",
                "type": "SCHEDULED",
                "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
                "display": {
                    "darkMode": 1,
                    "grayscale": true
                },
                "device": {
                    "performanceMode": 1,
                    "enable5g": false,
                    "enableRaiseToWake": true,
                    "enableWakeForNotifications": false
                },
                "pausedApps": [],
                "contactFilter": "ALL"
            }]
        }
        """.trimIndent()

        val mode = ConfigParser.parseConfig(json).modes.single()

        assertEquals(true, mode.display.enableRaiseToWake)
        assertEquals(false, mode.display.enableWakeForNotifications)
        // keys that belong to device stay in device
        assertEquals(1, mode.device?.performanceMode)
        assertEquals(false, mode.device?.enable5g)
    }
}
