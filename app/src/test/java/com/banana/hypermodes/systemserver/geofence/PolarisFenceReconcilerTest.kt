package com.banana.hypermodes.systemserver.geofence

import org.junit.Assert.*
import org.junit.Test

class PolarisFenceReconcilerTest {

    @Test
    fun `empty desired and remote produces no operations`() {
        val operations = PolarisFenceReconciler.plan(
            desired = emptyList(),
            remote = emptyList()
        )
        assertTrue(operations.isEmpty())
    }

    @Test
    fun `foreign remote fences are never changed`() {
        val operations = PolarisFenceReconciler.plan(
            desired = emptyList(),
            remote = listOf(remoteFence("security_center_1"))
        )
        assertTrue(operations.isEmpty())
    }

    @Test
    fun `missing desired fence produces Add`() {
        val desired = spec("hypermodes_a", latitude = 30.0, longitude = 120.0)
        val operations = PolarisFenceReconciler.plan(
            desired = listOf(desired),
            remote = emptyList()
        )
        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Add)
        assertEquals("hypermodes_a", (operations[0] as PolarisFenceOperation.Add).fence.fenceId)
    }

    @Test
    fun `stale remote fence produces Delete`() {
        val remote = remoteFence("hypermodes_stale")
        val operations = PolarisFenceReconciler.plan(
            desired = emptyList(),
            remote = listOf(remote)
        )
        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Delete)
        assertEquals("hypermodes_stale", (operations[0] as PolarisFenceOperation.Delete).fenceId)
    }

    @Test
    fun `unchanged fence produces Keep`() {
        val desired = spec("hypermodes_a", latitude = 30.0, longitude = 120.0, radius = 200)
        val remote = remoteFence("hypermodes_a", latitude = 30.0, longitude = 120.0, radius = 200)
        val operations = PolarisFenceReconciler.plan(
            desired = listOf(desired),
            remote = listOf(remote)
        )
        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Keep)
    }

    @Test
    fun `changed geometry produces Update`() {
        val desired = spec("hypermodes_a", latitude = 30.1)
        val remote = remoteFence("hypermodes_a", latitude = 30.2)
        val operations = PolarisFenceReconciler.plan(
            desired = listOf(desired),
            remote = listOf(remote)
        )
        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Update)
        assertEquals(30.1, (operations[0] as PolarisFenceOperation.Update).fence.latitude, 0.0)
    }

    @Test
    fun `operations are sorted by fence ID`() {
        val d1 = spec("hypermodes_c", latitude = 30.0)
        val d2 = spec("hypermodes_a", latitude = 31.0)
        val d3 = spec("hypermodes_b", latitude = 32.0)
        val remote = remoteFence("hypermodes_z")

        val operations = PolarisFenceReconciler.plan(
            desired = listOf(d1, d2, d3),
            remote = listOf(remote)
        )

        assertEquals(4, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Delete)
        assertEquals("hypermodes_z", (operations[0] as PolarisFenceOperation.Delete).fenceId)

        // Desired fences in sorted order: a, b, c
        assertTrue(operations[1] is PolarisFenceOperation.Add)
        assertEquals("hypermodes_a", (operations[1] as PolarisFenceOperation.Add).fence.fenceId)
        assertTrue(operations[2] is PolarisFenceOperation.Add)
        assertEquals("hypermodes_b", (operations[2] as PolarisFenceOperation.Add).fence.fenceId)
        assertTrue(operations[3] is PolarisFenceOperation.Add)
        assertEquals("hypermodes_c", (operations[3] as PolarisFenceOperation.Add).fence.fenceId)
    }

    @Test
    fun `mixed foreign and managed fences`() {
        val desired = spec("hypermodes_a", latitude = 30.0)
        val remote = listOf(
            remoteFence("security_1"),
            remoteFence("hypermodes_a", latitude = 30.0),
            remoteFence("other_app_fence")
        )
        val operations = PolarisFenceReconciler.plan(listOf(desired), remote)

        // Only hypermodes_a should produce Keep; foreign fences ignored
        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Keep)
    }

    @Test
    fun `changed radius produces Update`() {
        val desired = spec("hypermodes_a", radius = 300)
        val remote = remoteFence("hypermodes_a", radius = 200)
        val operations = PolarisFenceReconciler.plan(listOf(desired), listOf(remote))

        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Update)
    }

    @Test
    fun `changed longitude produces Update`() {
        val desired = spec("hypermodes_a", longitude = 116.5)
        val remote = remoteFence("hypermodes_a", longitude = 116.4)
        val operations = PolarisFenceReconciler.plan(listOf(desired), listOf(remote))

        assertEquals(1, operations.size)
        assertTrue(operations[0] is PolarisFenceOperation.Update)
    }

    private fun spec(
        fenceId: String,
        latitude: Double = 39.9,
        longitude: Double = 116.4,
        radius: Int = 200
    ) = PolarisFenceSpec(
        fenceId = fenceId,
        modeId = "test_mode",
        triggerId = "trigger_1",
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radius,
        transitionType = PolarisContract.TRANSITION_BOTH,
        confidence = PolarisContract.CONFIDENCE_HIGH
    )

    private fun remoteFence(
        fenceId: String,
        latitude: Double = 39.9,
        longitude: Double = 116.4,
        radius: Int = 200
    ) = PolarisRemoteFence(
        fenceId = fenceId,
        latitude = latitude,
        longitude = longitude,
        radiusMeters = radius,
        transitionType = PolarisContract.TRANSITION_BOTH,
        confidence = PolarisContract.CONFIDENCE_HIGH,
        packageName = PolarisContract.CLIENT_PACKAGE
    )
}
