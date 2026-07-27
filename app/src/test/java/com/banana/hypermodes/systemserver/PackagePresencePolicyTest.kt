package com.banana.hypermodes.systemserver

import org.junit.Assert.assertEquals
import org.junit.Test

class PackagePresencePolicyTest {

    @Test
    fun `missing package during replacement only skips callback`() {
        assertEquals(
            PackagePresencePolicy.MissingPackageAction.SKIP_ONLY,
            PackagePresencePolicy.onMissingPackage(
                RoutineCoreEngine.LifecycleState.REPLACING
            )
        )
    }

    @Test
    fun `missing package while running shuts down`() {
        assertEquals(
            PackagePresencePolicy.MissingPackageAction.SHUTDOWN,
            PackagePresencePolicy.onMissingPackage(
                RoutineCoreEngine.LifecycleState.RUNNING
            )
        )
    }

    @Test
    fun `removed engine never schedules cleanup again`() {
        assertEquals(
            PackagePresencePolicy.MissingPackageAction.ALLOW,
            PackagePresencePolicy.onMissingPackage(
                RoutineCoreEngine.LifecycleState.REMOVED
            )
        )
    }
}
