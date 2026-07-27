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
    fun testDismissalPersistenceLogic() {
        val engine = RoutineCoreEngine.getInstance()
        val modeId = "test_mode"
        val now = System.currentTimeMillis()
        
        // Use reflection to access private dismissedScheduledModes for testing
        val field = engine.javaClass.getDeclaredField("dismissedScheduledModes")
        field.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val dismissedMap = field.get(engine) as MutableMap<String, Long>
        
        dismissedMap.clear()
        
        // 1. Dismiss happens AFTER period starts -> should stay dismissed
        val periodStartToday = now - 3600000 // 1 hour ago
        val dismissTimeToday = now - 1800000 // 30 mins ago
        dismissedMap[modeId] = dismissTimeToday
        assertTrue("Should be dismissed if dismiss happened after period start",
            engine.isDismissedInCurrentPeriod(modeId, periodStartToday))
            
        // 2. Dismiss happened BEFORE current period starts (e.g. next day) -> should TRIGGER
        val nextPeriodStart = now + 3600000 // 1 hour in future (simulating check at that time)
        assertFalse("Should NOT be dismissed for a future period",
            engine.isDismissedInCurrentPeriod(modeId, nextPeriodStart))
            
        // 3. Dismiss is older than 24 hours -> should TRIGGER and be cleaned up
        val oldDismissTime = now - (25 * 60 * 60 * 1000L) // 25 hours ago
        dismissedMap[modeId] = oldDismissTime
        assertFalse("Old dismissal should expire and not block activation",
            engine.isDismissedInCurrentPeriod(modeId, now))
        assertFalse("Expired record should be removed from map", dismissedMap.containsKey(modeId))
    }
}
