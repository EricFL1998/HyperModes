package com.banana.hypermodes.systemserver.executor

import com.banana.hypermodes.systemserver.config.DeviceConfig
import org.junit.Assert.*
import org.junit.Test

/**
 * Tests for silent mode and airplane mode device controls.
 * Following TDD: these tests fail until implementation is complete.
 */
class DeviceControllerSilentAirplaneTest {

    @Test
    fun `silentMode null means no override`() {
        val config = DeviceConfig(
            performanceMode = null,
            enable5g = null,
            enableWifi = null,
            enableBluetooth = null,
            silentMode = null,
            airplaneMode = null
        )

        // Should not throw, should be no-op
        assertNotNull(config)
        assertNull(config.silentMode)
    }

    @Test
    fun `silentMode true means enable silent`() {
        val config = DeviceConfig(silentMode = true)
        assertEquals(true, config.silentMode)
    }

    @Test
    fun `silentMode false means disable silent`() {
        val config = DeviceConfig(silentMode = false)
        assertEquals(false, config.silentMode)
    }

    @Test
    fun `airplaneMode null means no override`() {
        val config = DeviceConfig(airplaneMode = null)
        assertNull(config.airplaneMode)
    }

    @Test
    fun `airplaneMode true means enable airplane`() {
        val config = DeviceConfig(airplaneMode = true)
        assertEquals(true, config.airplaneMode)
    }

    @Test
    fun `airplaneMode false means disable airplane`() {
        val config = DeviceConfig(airplaneMode = false)
        assertEquals(false, config.airplaneMode)
    }

    @Test
    fun `airplaneMode conflicts with enableWifi true`() {
        // This validation happens at UI/save time, not model construction
        val config = DeviceConfig(
            airplaneMode = true,
            enableWifi = true
        )

        // Model allows construction, UI must validate
        assertEquals(true, config.airplaneMode)
        assertEquals(true, config.enableWifi)
    }

    @Test
    fun `airplaneMode conflicts with enableBluetooth true`() {
        val config = DeviceConfig(
            airplaneMode = true,
            enableBluetooth = true
        )

        assertEquals(true, config.airplaneMode)
        assertEquals(true, config.enableBluetooth)
    }

    @Test
    fun `airplaneMode conflicts with enable5g true`() {
        val config = DeviceConfig(
            airplaneMode = true,
            enable5g = true
        )

        assertEquals(true, config.airplaneMode)
        assertEquals(true, config.enable5g)
    }

    @Test
    fun `airplaneMode true with all radios false is valid`() {
        val config = DeviceConfig(
            airplaneMode = true,
            enableWifi = false,
            enableBluetooth = false,
            enable5g = false
        )

        assertEquals(true, config.airplaneMode)
    }

    @Test
    fun `airplaneMode false with radios enabled is valid`() {
        val config = DeviceConfig(
            airplaneMode = false,
            enableWifi = true,
            enableBluetooth = true,
            enable5g = true
        )

        assertEquals(false, config.airplaneMode)
    }
}
