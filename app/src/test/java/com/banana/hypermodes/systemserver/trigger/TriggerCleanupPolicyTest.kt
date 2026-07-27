package com.banana.hypermodes.systemserver.trigger

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerCleanupPolicyTest {

    @Test
    fun `package removal cleanup never calls normal deactivation`() {
        assertFalse(TriggerCleanupPolicy.shouldDeactivateModeOnPackageRemoval)
    }

    @Test
    fun `normal cleanup still deactivates runtime trigger mode`() {
        assertTrue(TriggerCleanupPolicy.shouldDeactivateModeOnRuntimeCleanup)
    }
}
