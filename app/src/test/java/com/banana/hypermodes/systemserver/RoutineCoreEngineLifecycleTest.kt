package com.banana.hypermodes.systemserver

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineCoreEngineLifecycleTest {

    @Test
    fun `beginPackageRemoval is atomic and terminal`() {
        val engine = RoutineCoreEngine.getInstance()
        val lifecycleField = RoutineCoreEngine::class.java.getDeclaredField("lifecycleState")
        lifecycleField.isAccessible = true
        lifecycleField.set(engine, RoutineCoreEngine.LifecycleState.RUNNING)

        assertTrue(engine.beginPackageRemoval())
        assertEquals(
            RoutineCoreEngine.LifecycleState.REMOVED,
            engine.getLifecycleState()
        )

        assertFalse(engine.beginPackageRemoval())
        assertEquals(
            RoutineCoreEngine.LifecycleState.REMOVED,
            engine.getLifecycleState()
        )
    }

    @Test
    fun `replacement cannot revive removed engine`() {
        val engine = RoutineCoreEngine.getInstance()
        val lifecycleField = RoutineCoreEngine::class.java.getDeclaredField("lifecycleState")
        lifecycleField.isAccessible = true
        lifecycleField.set(engine, RoutineCoreEngine.LifecycleState.REMOVED)

        engine.setLifecycleState(RoutineCoreEngine.LifecycleState.RUNNING)

        assertEquals(
            RoutineCoreEngine.LifecycleState.REMOVED,
            engine.getLifecycleState()
        )
    }
}
