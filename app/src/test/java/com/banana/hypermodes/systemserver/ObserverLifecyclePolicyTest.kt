package com.banana.hypermodes.systemserver

import org.junit.Assert.assertTrue
import org.junit.Test

class ObserverLifecyclePolicyTest {

    @Test
    fun `package removal unregisters config observer`() {
        assertTrue(ObserverLifecyclePolicy.unregisterConfigObserverOnPackageRemoval)
    }
}
