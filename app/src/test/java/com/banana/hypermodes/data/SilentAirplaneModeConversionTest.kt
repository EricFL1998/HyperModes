package com.banana.hypermodes.data

import com.banana.hypermodes.systemserver.config.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for silent mode and airplane mode UI <-> config conversion.
 */
class SilentAirplaneModeConversionTest {

    @Test
    fun `silentMode and airplaneMode survive round trip`() {
        val mode = Mode(
            id = "test",
            name = "Test",
            icon = "test",
            description = "",
            settings = ModeSettings(
                silentMode = true,
                airplaneMode = false
            )
        )

        val config = mode.toModeConfig()
        val restored = config.toMode()

        assertEquals(true, config.device?.silentMode)
        assertEquals(false, config.device?.airplaneMode)
        assertEquals(true, restored.settings.silentMode)
        assertEquals(false, restored.settings.airplaneMode)
    }

    @Test
    fun `null silentMode and airplaneMode survive round trip`() {
        val mode = Mode(
            id = "test",
            name = "Test",
            icon = "test",
            description = "",
            settings = ModeSettings(
                silentMode = null,
                airplaneMode = null
            )
        )

        val config = mode.toModeConfig()
        val restored = config.toMode()

        assertNull(config.device?.silentMode)
        assertNull(config.device?.airplaneMode)
        assertNull(restored.settings.silentMode)
        assertNull(restored.settings.airplaneMode)
    }

    @Test
    fun `legacy mode without silentMode and airplaneMode converts to null`() {
        val config = ModeConfig(
            id = "legacy",
            name = "Legacy",
            icon = "test",
            type = ModeType.SCHEDULED,
            notification = NotificationConfig(com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY),
            display = DisplayConfig(),
            device = DeviceConfig(
                performanceMode = 1,
                enable5g = true,
                enableWifi = null,
                enableBluetooth = null
            ),
            pausedApps = emptyList()
        )

        val mode = config.toMode()

        assertNull(mode.settings.silentMode)
        assertNull(mode.settings.airplaneMode)
        assertEquals(1, mode.settings.performanceMode)
        assertEquals(true, mode.settings.enable5g)
    }

    @Test
    fun `mode with no device config has null silentMode and airplaneMode`() {
        val config = ModeConfig(
            id = "minimal",
            name = "Minimal",
            icon = "test",
            type = ModeType.SCHEDULED,
            notification = NotificationConfig(com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY),
            display = DisplayConfig(),
            device = null,
            pausedApps = emptyList()
        )

        val mode = config.toMode()

        assertNull(mode.settings.silentMode)
        assertNull(mode.settings.airplaneMode)
    }

    @Test
    fun `silentMode true with DND disabled is valid combination`() {
        val mode = Mode(
            id = "silent_no_dnd",
            name = "Silent No DND",
            icon = "test",
            description = "",
            settings = ModeSettings(
                enableDnd = false,
                silentMode = true
            )
        )

        val config = mode.toModeConfig()

        assertEquals(com.banana.hypermodes.systemserver.config.DndLevel.DISABLED, config.notification.dndLevel)
        assertEquals(true, config.device?.silentMode)
    }

    @Test
    fun `silentMode true with DND enabled is valid combination`() {
        val mode = Mode(
            id = "silent_with_dnd",
            name = "Silent With DND",
            icon = "test",
            description = "",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = com.banana.hypermodes.data.DndLevel.NONE,
                silentMode = true
            )
        )

        val config = mode.toModeConfig()

        assertEquals(com.banana.hypermodes.systemserver.config.DndLevel.NONE, config.notification.dndLevel)
        assertEquals(true, config.device?.silentMode)
    }
}
