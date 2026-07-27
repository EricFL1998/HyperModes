package com.banana.hypermodes.systemserver

import org.junit.Assert.assertEquals
import org.junit.Test

class EngineCleanupPolicyTest {

    @Test
    fun `config is removed after active mode restoration`() {
        val order = EngineCleanupPolicy.packageRemovalOrder

        val restoreIndex = order.indexOf(EngineCleanupPolicy.Step.REVERT_ACTIVE_MODE)
        val clearConfigIndex = order.indexOf(EngineCleanupPolicy.Step.REMOVE_GLOBAL_CONFIG)

        assertEquals(true, restoreIndex >= 0)
        assertEquals(true, clearConfigIndex > restoreIndex)
    }
}
