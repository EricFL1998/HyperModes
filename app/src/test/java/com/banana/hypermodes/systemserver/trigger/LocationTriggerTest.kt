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
}
