package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.geofence.PolarisContract
import org.junit.Assert.*
import org.junit.Test

class LocationTriggerStateMachineTest {

    @Test
    fun `inside status immediately activates ARRIVE once`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "ARRIVE", true)
        machine.applyState("mode", "trigger", "ARRIVE", true)

        assertEquals(listOf(true), changes)
    }

    @Test
    fun `unknown status emits no state`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyStatus("mode", "trigger", "LEAVE", PolarisContract.STATUS_UNKNOWN)

        assertTrue(changes.isEmpty())
    }

    @Test
    fun `outside status immediately activates LEAVE once`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "LEAVE", false)
        machine.applyState("mode", "trigger", "LEAVE", false)

        assertEquals(listOf(true), changes)
    }

    @Test
    fun `STATUS_IN activates ARRIVE trigger`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyStatus("mode", "trigger", "ARRIVE", PolarisContract.STATUS_IN)

        assertEquals(listOf(true), changes)
    }

    @Test
    fun `STATUS_OUT activates LEAVE trigger`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyStatus("mode", "trigger", "LEAVE", PolarisContract.STATUS_OUT)

        assertEquals(listOf(true), changes)
    }

    @Test
    fun `STATUS_OUT followed by EVENT_EXIT deduplicates`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyStatus("mode", "trigger", "LEAVE", PolarisContract.STATUS_OUT)
        machine.applyState("mode", "trigger", "LEAVE", false)

        assertEquals(listOf(true), changes)
    }

    @Test
    fun `STATUS_IN followed by EVENT_ENTER deduplicates`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyStatus("mode", "trigger", "ARRIVE", PolarisContract.STATUS_IN)
        machine.applyState("mode", "trigger", "ARRIVE", true)

        assertEquals(listOf(true), changes)
    }

    @Test
    fun `invalid transition text emits nothing`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "INVALID", true)
        machine.applyState("mode", "trigger", "enter", true)
        machine.applyState("mode", "trigger", "", true)

        assertTrue(changes.isEmpty())
    }

    @Test
    fun `trigger removal emits false exactly once`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "ARRIVE", true)
        changes.clear()

        machine.remove("mode", "trigger")
        machine.remove("mode", "trigger")

        assertEquals(listOf(false), changes)
    }

    @Test
    fun `removal of never-active trigger emits nothing`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.remove("mode", "trigger")

        assertTrue(changes.isEmpty())
    }

    @Test
    fun `callback receives correct modeId and triggerId format`() {
        val callbacks = mutableListOf<Triple<String, String, Boolean>>()
        val machine = LocationTriggerStateMachine { mode, trigger, active ->
            callbacks += Triple(mode, trigger, active)
        }

        machine.applyState("testMode", "testTrigger", "ARRIVE", true)

        assertEquals(1, callbacks.size)
        assertEquals("testMode", callbacks[0].first)
        assertEquals("location:testTrigger", callbacks[0].second)
        assertEquals(true, callbacks[0].third)
    }

    @Test
    fun `different triggers maintain separate state`() {
        val changes = mutableListOf<Triple<String, String, Boolean>>()
        val machine = LocationTriggerStateMachine { mode, trigger, active ->
            changes += Triple(mode, trigger, active)
        }

        machine.applyState("mode", "trigger1", "ARRIVE", true)
        machine.applyState("mode", "trigger2", "ARRIVE", true)
        machine.applyState("mode", "trigger1", "ARRIVE", true)

        assertEquals(2, changes.size)
        assertEquals("location:trigger1", changes[0].second)
        assertEquals("location:trigger2", changes[1].second)
    }

    @Test
    fun `ARRIVE with inside false does not activate`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "ARRIVE", false)

        assertEquals(listOf(false), changes)
    }

    @Test
    fun `LEAVE with inside true does not activate`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "LEAVE", true)

        assertEquals(listOf(false), changes)
    }

    @Test
    fun `state transition from inside to outside with LEAVE`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "ARRIVE", true)
        machine.applyState("mode", "trigger", "LEAVE", false)

        assertEquals(listOf(true, true), changes)
    }

    @Test
    fun `state transition from outside to inside with ARRIVE`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        machine.applyState("mode", "trigger", "LEAVE", false)
        machine.applyState("mode", "trigger", "ARRIVE", true)

        assertEquals(listOf(true, true), changes)
    }

    @Test
    fun `full activation to deactivation cycle for ARRIVE trigger`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        // User enters geofence - ARRIVE trigger activates
        machine.applyState("mode", "trigger", "ARRIVE", true)
        // User exits geofence - ARRIVE trigger deactivates
        machine.applyState("mode", "trigger", "ARRIVE", false)

        assertEquals(listOf(true, false), changes)
    }

    @Test
    fun `full activation to deactivation cycle for LEAVE trigger`() {
        val changes = mutableListOf<Boolean>()
        val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

        // User exits geofence - LEAVE trigger activates
        machine.applyState("mode", "trigger", "LEAVE", false)
        // User re-enters geofence - LEAVE trigger deactivates
        machine.applyState("mode", "trigger", "LEAVE", true)

        assertEquals(listOf(true, false), changes)
    }
}
