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
                        "allowAll": false,
                        "allowedApps": ["com.android.messaging", "com.google.android.apps.messaging"]
                    },
                    "display": {
                        "darkMode": false,
                        "grayscale": true
                    },
                    "pausedApps": ["com.facebook.katana", "com.instagram.android"],
                    "contactFilter": "STARRED_ONLY",
                    "dndEnabled": true
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

        assertEquals(false, mode.notification.allowAll)
        assertEquals(2, mode.notification.allowedApps.size)

        assertEquals(false, mode.display.darkMode)
        assertEquals(true, mode.display.grayscale)

        assertEquals(2, mode.pausedApps.size)
        assertEquals(ContactFilter.STARRED_ONLY, mode.contactFilter)
        assertEquals(true, mode.dndEnabled)
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
                        "allowAll": false,
                        "allowedApps": []
                    },
                    "display": {
                        "darkMode": false,
                        "grayscale": false
                    },
                    "pausedApps": [],
                    "contactFilter": "ALL"
                }
            ]
        }
        """.trimIndent()

        val updated = ConfigParser.updateActiveModeId(json, "driving_mode")
        val config = ConfigParser.parseConfig(updated)

        assertEquals("driving_mode", config.activeModeId)
        assertEquals(1, config.modes.size) // modes should be unchanged
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
                        allowAll = false,
                        allowedApps = emptyList()
                    ),
                    display = DisplayConfig(
                        darkMode = true,
                        grayscale = true
                    ),
                    pausedApps = emptyList(),
                    contactFilter = ContactFilter.STARRED_ONLY,
                    dndEnabled = true
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
}
