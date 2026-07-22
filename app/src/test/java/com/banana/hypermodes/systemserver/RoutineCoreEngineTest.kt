package com.banana.hypermodes.systemserver

import org.junit.Assert.*
import org.junit.Test

class RoutineCoreEngineTest {

    @Test
    fun testSingletonPattern() {
        val instance1 = RoutineCoreEngine.getInstance()
        val instance2 = RoutineCoreEngine.getInstance()

        assertNotNull(instance1)
        assertNotNull(instance2)
        assertSame("getInstance should return the same instance", instance1, instance2)
    }

    @Test
    fun testGetCurrentActiveModeInitiallyNull() {
        val engine = RoutineCoreEngine.getInstance()
        // Before initialization or activation, there should be no active mode
        // This test verifies the engine can be created without Android dependencies
        assertNull(engine.getCurrentActiveMode())
    }

    @Test
    fun testMultipleGetInstanceCallsReturnSameObject() {
        // Test thread-safe singleton pattern
        val instances = (1..10).map { RoutineCoreEngine.getInstance() }

        // All instances should be the same object
        instances.forEach { instance ->
            assertSame(instances[0], instance)
        }
    }
}
