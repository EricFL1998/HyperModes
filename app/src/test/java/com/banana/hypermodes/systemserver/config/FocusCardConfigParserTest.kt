package com.banana.hypermodes.systemserver.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FocusCardConfigParserTest {
    private val modeJson = """
        {
          "activeModeId": null,
          "modes": [{
            "id": "work",
            "name": "Work",
            "icon": "💼",
            "type": "SCHEDULED",
            "notification": {"dndLevel": "PRIORITY", "allowedApps": []},
            "display": {},
            "pausedApps": []
          }]
        }
    """.trimIndent()

    @Test
    fun `legacy config without lastModeId parses as null`() {
        assertNull(ConfigParser.parseConfig(modeJson).lastModeId)
    }

    @Test
    fun `activation records active and last mode`() {
        val config = ConfigParser.parseConfig(
            ConfigParser.updateActiveModeId(modeJson, "work")
        )
        assertEquals("work", config.activeModeId)
        assertEquals("work", config.lastModeId)
    }

    @Test
    fun `deactivation preserves last mode`() {
        val active = ConfigParser.updateActiveModeId(modeJson, "work")
        val config = ConfigParser.parseConfig(
            ConfigParser.updateActiveModeId(active, null)
        )
        assertNull(config.activeModeId)
        assertEquals("work", config.lastModeId)
    }

    @Test
    fun `last mode can be initialized without activation`() {
        val config = ConfigParser.parseConfig(
            ConfigParser.updateLastModeId(modeJson, "work")
        )
        assertNull(config.activeModeId)
        assertEquals("work", config.lastModeId)
    }
}
