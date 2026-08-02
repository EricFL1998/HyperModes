package com.banana.hypermodes.systemserver.config

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for silent mode and airplane mode config serialization/deserialization.
 */
class SilentAirplaneModeConfigTest {

    @Test
    fun `silentMode and airplaneMode serialize and deserialize`() {
        val config = FullConfig(
            activeModeId = null,
            modes = listOf(
                ModeConfig(
                    id = "test",
                    name = "Test",
                    icon = "test",
                    type = ModeType.SCHEDULED,
                    notification = NotificationConfig(DndLevel.PRIORITY),
                    display = DisplayConfig(),
                    device = DeviceConfig(
                        silentMode = true,
                        airplaneMode = false
                    ),
                    pausedApps = emptyList()
                )
            )
        )

        val json = ConfigParser.serializeConfig(config)
        val parsed = ConfigParser.parseConfig(json)

        val device = parsed.modes[0].device
        assertNotNull(device)
        assertEquals(true, device?.silentMode)
        assertEquals(false, device?.airplaneMode)
    }

    @Test
    fun `legacy config without silentMode and airplaneMode parses as null`() {
        val json = """
        {
            "activeModeId": null,
            "modes": [{
                "id": "legacy",
                "name": "Legacy",
                "icon": "work",
                "type": "SCHEDULED",
                "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
                "display": {},
                "device": {
                    "performanceMode": 1,
                    "enable5g": true
                },
                "pausedApps": []
            }]
        }
        """.trimIndent()

        val mode = ConfigParser.parseConfig(json).modes.single()
        val device = mode.device

        assertNotNull(device)
        assertEquals(1, device?.performanceMode)
        assertEquals(true, device?.enable5g)
        assertNull(device?.silentMode)
        assertNull(device?.airplaneMode)
    }

    @Test
    fun `null device config parses correctly`() {
        val json = """
        {
            "activeModeId": null,
            "modes": [{
                "id": "minimal",
                "name": "Minimal",
                "icon": "work",
                "type": "SCHEDULED",
                "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
                "display": {},
                "pausedApps": []
            }]
        }
        """.trimIndent()

        val mode = ConfigParser.parseConfig(json).modes.single()
        assertNull(mode.device)
    }

    @Test
    fun `device config with only silentMode serializes`() {
        val config = FullConfig(
            activeModeId = null,
            modes = listOf(
                ModeConfig(
                    id = "silent_only",
                    name = "Silent Only",
                    icon = "test",
                    type = ModeType.SCHEDULED,
                    notification = NotificationConfig(DndLevel.DISABLED),
                    display = DisplayConfig(),
                    device = DeviceConfig(silentMode = true),
                    pausedApps = emptyList()
                )
            )
        )

        val parsed = ConfigParser.parseConfig(ConfigParser.serializeConfig(config))
        val device = parsed.modes[0].device

        assertNotNull(device)
        assertEquals(true, device?.silentMode)
        assertNull(device?.airplaneMode)
        assertNull(device?.performanceMode)
    }

    @Test
    fun `device config with only airplaneMode serializes`() {
        val config = FullConfig(
            activeModeId = null,
            modes = listOf(
                ModeConfig(
                    id = "airplane_only",
                    name = "Airplane Only",
                    icon = "test",
                    type = ModeType.SCHEDULED,
                    notification = NotificationConfig(DndLevel.DISABLED),
                    display = DisplayConfig(),
                    device = DeviceConfig(airplaneMode = true),
                    pausedApps = emptyList()
                )
            )
        )

        val parsed = ConfigParser.parseConfig(ConfigParser.serializeConfig(config))
        val device = parsed.modes[0].device

        assertNotNull(device)
        assertEquals(true, device?.airplaneMode)
        assertNull(device?.silentMode)
    }
}
