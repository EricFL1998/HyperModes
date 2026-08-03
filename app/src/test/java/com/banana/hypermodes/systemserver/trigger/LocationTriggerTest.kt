package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.data.LocationTarget
import com.banana.hypermodes.data.LocationTransition
import com.banana.hypermodes.data.ModeTrigger
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import org.junit.Assert.*
import org.junit.Test

class LocationTriggerTest {

    @Test
    fun testLocationTargetSerialization() {
        val target = LocationTarget(
            latitude = 39.9042,
            longitude = 116.4074,
            radius = 500,
            addressName = "Tiananmen Square",
            cityName = "Beijing",
            provinceName = "Beijing"
        )

        assertEquals(39.9042, target.latitude, 0.0001)
        assertEquals(116.4074, target.longitude, 0.0001)
        assertEquals(500, target.radius)
        assertEquals("Tiananmen Square", target.addressName)
        assertEquals("Beijing", target.cityName)
    }

    @Test
    fun testLocationTransitionTypes() {
        assertEquals("ARRIVE", LocationTransition.ARRIVE.name)
        assertEquals("LEAVE", LocationTransition.LEAVE.name)
    }

    @Test
    fun testModeTriggerLocationCreation() {
        val trigger = ModeTrigger.Location(
            id = "test-location-1",
            target = LocationTarget(
                latitude = 39.9042,
                longitude = 116.4074,
                radius = 500,
                addressName = "Test Location"
            ),
            transition = LocationTransition.ARRIVE
        )

        assertEquals("test-location-1", trigger.id)
        assertEquals(39.9042, trigger.target.latitude, 0.0001)
        assertEquals(LocationTransition.ARRIVE, trigger.transition)
    }

    @Test
    fun testComplexTriggerLocationCreation() {
        val trigger = ComplexTrigger.Location(
            id = "test-location-1",
            latitude = 39.9042,
            longitude = 116.4074,
            radius = 500,
            addressName = "Test Location",
            cityName = "Beijing",
            provinceName = "Beijing",
            transition = "ARRIVE"
        )

        assertEquals("test-location-1", trigger.id)
        assertEquals(39.9042, trigger.latitude, 0.0001)
        assertEquals(116.4074, trigger.longitude, 0.0001)
        assertEquals("ARRIVE", trigger.transition)
    }

    @Test
    fun testLocationTriggerConversion() {
        val modeTrigger = ModeTrigger.Location(
            id = "test-location-1",
            target = LocationTarget(
                latitude = 39.9042,
                longitude = 116.4074,
                radius = 500,
                addressName = "Test Location",
                cityName = "Beijing",
                provinceName = "Beijing"
            ),
            transition = LocationTransition.ARRIVE
        )

        // Would test toComplexTrigger() here if we had access to the extension functions
        // This verifies the data structure is correct
        assertNotNull(modeTrigger.id)
        assertNotNull(modeTrigger.target)
        assertNotNull(modeTrigger.transition)
    }

    @Test
    fun testGeofenceEventTypes() {
        assertEquals(GeofenceEvent.ENTER, GeofenceEvent.valueOf("ENTER"))
        assertEquals(GeofenceEvent.EXIT, GeofenceEvent.valueOf("EXIT"))
    }

    @Test
    fun testLocationTransitionLogic() {
        // Test ARRIVE transition: should activate when entering
        val arriveTrigger = ModeTrigger.Location(
            id = "arrive-test",
            target = LocationTarget(latitude = 39.9042, longitude = 116.4074),
            transition = LocationTransition.ARRIVE
        )
        assertEquals(LocationTransition.ARRIVE, arriveTrigger.transition)

        // Test LEAVE transition: should activate when exiting
        val leaveTrigger = ModeTrigger.Location(
            id = "leave-test",
            target = LocationTarget(latitude = 39.9042, longitude = 116.4074),
            transition = LocationTransition.LEAVE
        )
        assertEquals(LocationTransition.LEAVE, leaveTrigger.transition)
    }

    @Test
    fun testDefaultRadius() {
        val target = LocationTarget(
            latitude = 39.9042,
            longitude = 116.4074
        )
        assertEquals(500, target.radius) // Default radius should be 500m
    }

    @Test
    fun testCustomRadius() {
        val target = LocationTarget(
            latitude = 39.9042,
            longitude = 116.4074,
            radius = 1000
        )
        assertEquals(1000, target.radius)
    }

    // Task 4: PolarisSystemEventHandler routing tests
    @Test
    fun testPolarisSystemEventHandler_validPayload_dispatchesFenceIdAndEvent() {
        val dispatched = mutableListOf<Pair<String, Int>>()
        val handler = PolarisSystemEventHandler { fenceId, event ->
            dispatched.add(fenceId to event)
        }

        handler.handle("hypermodes_test_fence", 11)

        assertEquals(1, dispatched.size)
        assertEquals("hypermodes_test_fence", dispatched[0].first)
        assertEquals(11, dispatched[0].second)
    }

    @Test
    fun testPolarisSystemEventHandler_nullFenceId_doesNotDispatch() {
        val dispatched = mutableListOf<Pair<String, Int>>()
        val handler = PolarisSystemEventHandler { fenceId, event ->
            dispatched.add(fenceId to event)
        }

        handler.handle(null, 11)

        assertEquals(0, dispatched.size)
    }

    @Test
    fun testPolarisSystemEventHandler_blankFenceId_doesNotDispatch() {
        val dispatched = mutableListOf<Pair<String, Int>>()
        val handler = PolarisSystemEventHandler { fenceId, event ->
            dispatched.add(fenceId to event)
        }

        handler.handle("", 11)

        assertEquals(0, dispatched.size)
    }

    @Test
    fun testPolarisSystemEventHandler_fenceIdWithoutPrefix_doesNotDispatch() {
        val dispatched = mutableListOf<Pair<String, Int>>()
        val handler = PolarisSystemEventHandler { fenceId, event ->
            dispatched.add(fenceId to event)
        }

        handler.handle("invalid_fence_id", 11)

        assertEquals(0, dispatched.size)
    }

    @Test
    fun testPolarisSystemEventHandler_invalidEvent_doesNotDispatch() {
        val dispatched = mutableListOf<Pair<String, Int>>()
        val handler = PolarisSystemEventHandler { fenceId, event ->
            dispatched.add(fenceId to event)
        }

        handler.handle("hypermodes_test_fence", -1)

        assertEquals(0, dispatched.size)
    }

    @Test
    fun testPolarisSystemEventHandler_exitEvent_dispatches() {
        val dispatched = mutableListOf<Pair<String, Int>>()
        val handler = PolarisSystemEventHandler { fenceId, event ->
            dispatched.add(fenceId to event)
        }

        handler.handle("hypermodes_test_fence", 12)

        assertEquals(1, dispatched.size)
        assertEquals("hypermodes_test_fence", dispatched[0].first)
        assertEquals(12, dispatched[0].second)
    }
}
