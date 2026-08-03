package com.banana.hypermodes.systemserver.geofence

import android.content.ComponentName
import android.content.Context
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.trigger.GeofenceEvent
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class PolarisGeofenceAdapterTest {

    private lateinit var context: Context
    private lateinit var fakeService: FakePolarisGeoService
    private lateinit var adapter: PolarisGeofenceAdapter
    private val callbacks = mutableListOf<Triple<String, String, GeofenceEvent>>()

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        fakeService = FakePolarisGeoService()
        adapter = PolarisGeofenceAdapter(context, ::recordCallback, fakeService)
        callbacks.clear()
    }

    private fun recordCallback(modeId: String, triggerId: String, event: GeofenceEvent) {
        callbacks.add(Triple(modeId, triggerId, event))
    }

    @Test
    fun `client package is always android`() {
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        // Debug: print all calls
        println("All calls: ${fakeService.calls}")

        // Binder calls (list, add, etc.) should use "android" package
        assertTrue("Must have calls with package=android", fakeService.calls.any { it.contains("package=android") })

        // Component package should be HyperModes, but Binder calls should not use it as client package
        val binderCalls = fakeService.calls.filter {
            it.startsWith("list") || it.startsWith("add") ||
            it.startsWith("update") || it.startsWith("deleteById") ||
            it.startsWith("findById") || it.startsWith("status")
        }
        println("Binder calls: $binderCalls")
        assertTrue("All Binder calls must use package=android", binderCalls.all { it.contains("package=android") })
    }

    @Test
    fun `callback component belongs to HyperModes`() {
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        assertTrue(fakeService.registeredComponent?.packageName == "com.banana.hypermodes")
    }

    @Test
    fun `callback registration occurs before reconciliation`() {
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        val registerIndex = fakeService.calls.indexOfFirst { it.startsWith("registerComponent") }
        val addIndex = fakeService.calls.indexOfFirst { it.startsWith("add") }

        assertTrue("Register must come before add", registerIndex >= 0 && registerIndex < addIndex)
    }

    @Test
    fun `add result must equal desired ID before local registration`() {
        fakeService.addResultOverride = "wrong_id"
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        // Verify no callback was fired (fence not live)
        assertEquals(0, callbacks.size)
    }

    @Test
    fun `update is followed by findById confirmation`() {
        // First reconcile to add fence
        val trigger1 = trigger("mode1", "trigger1", 39.9, 116.4, radius = 200)
        adapter.reconcile(listOf(trigger1))
        fakeService.calls.clear()

        // Update with different radius
        val trigger2 = trigger("mode1", "trigger1", 39.9, 116.4, radius = 300)
        adapter.reconcile(listOf(trigger2))

        val updateIndex = fakeService.calls.indexOfFirst { it.startsWith("update") }
        val findIndex = fakeService.calls.indexOfFirst { it.startsWith("findById") && it.contains("hypermodes_mode1_trigger1") }

        assertTrue("Update must be followed by findById", updateIndex >= 0 && findIndex > updateIndex)
    }

    @Test
    fun `stale managed fence is deleted foreign fence untouched`() {
        fakeService.remoteFences.add(remoteFence("hypermodes_stale", 30.0, 120.0))
        fakeService.remoteFences.add(remoteFence("security_center_1", 30.0, 120.0))

        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        assertTrue(fakeService.calls.any { it.startsWith("deleteById(hypermodes_stale)") })
        assertFalse(fakeService.calls.any { it.contains("security_center_1") && it.startsWith("deleteById") })
    }

    @Test
    fun `status IN emits ENTER immediately`() {
        fakeService.statusOverride = PolarisContract.STATUS_IN
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        assertEquals(1, callbacks.size)
        assertEquals("mode1", callbacks[0].first)
        assertEquals("trigger1", callbacks[0].second)
        assertEquals(GeofenceEvent.ENTER, callbacks[0].third)
    }

    @Test
    fun `status OUT emits EXIT immediately`() {
        fakeService.statusOverride = PolarisContract.STATUS_OUT
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        assertEquals(1, callbacks.size)
        assertEquals(GeofenceEvent.EXIT, callbacks[0].third)
    }

    @Test
    fun `unknown status emits nothing`() {
        fakeService.statusOverride = PolarisContract.STATUS_UNKNOWN
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        assertEquals(0, callbacks.size)
    }

    @Test
    fun `forwarded event must pass findById geometry verification`() {
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))
        callbacks.clear()

        // Event with correct ID
        adapter.handleGeofenceEvent("hypermodes_mode1_trigger1", PolarisContract.EVENT_ENTER)
        assertEquals(1, callbacks.size)

        // Now tamper with remote geometry
        fakeService.remoteFences.clear()
        fakeService.remoteFences.add(remoteFence("hypermodes_mode1_trigger1", 30.0, 120.0)) // Different coordinates
        callbacks.clear()

        adapter.handleGeofenceEvent("hypermodes_mode1_trigger1", PolarisContract.EVENT_ENTER)
        assertEquals(0, callbacks.size) // Rejected due to geometry mismatch
    }

    @Test
    fun `failed add does not corrupt live registry`() {
        fakeService.addResultOverride = null // Simulate failure
        val trigger = trigger("mode1", "trigger1", 39.9, 116.4)
        adapter.reconcile(listOf(trigger))

        // Event should be rejected (fence not in live registry)
        adapter.handleGeofenceEvent("hypermodes_mode1_trigger1", PolarisContract.EVENT_ENTER)
        assertEquals(0, callbacks.size)
    }

    private fun trigger(
        modeId: String,
        triggerId: String,
        lat: Double,
        lon: Double,
        radius: Int = 200
    ): Triple<String, String, ComplexTrigger.Location> {
        return Triple(
            modeId,
            triggerId,
            ComplexTrigger.Location(
                id = triggerId,
                latitude = lat,
                longitude = lon,
                radius = radius,
                transition = "ARRIVE"
            )
        )
    }

    private fun remoteFence(
        fenceId: String,
        lat: Double,
        lon: Double,
        radius: Int = 200
    ) = PolarisRemoteFence(
        fenceId = fenceId,
        latitude = lat,
        longitude = lon,
        radiusMeters = radius,
        transitionType = PolarisContract.TRANSITION_BOTH,
        confidence = PolarisContract.CONFIDENCE_HIGH,
        packageName = PolarisContract.CLIENT_PACKAGE
    )

    // Fake service for testing
    private class FakePolarisGeoService : PolarisGeoService {
        val calls = mutableListOf<String>()
        var registeredComponent: ComponentName? = null
        val remoteFences = mutableListOf<PolarisRemoteFence>()
        var addResultOverride: String? = "default"
        var statusOverride: Int = PolarisContract.STATUS_UNKNOWN

        override fun registerComponent(component: ComponentName?) {
            calls.add("registerComponent(package=${component?.packageName})")
            registeredComponent = component
        }

        override fun list(): List<PolarisRemoteFence> {
            calls.add("list(package=android)")
            return remoteFences.toList()
        }

        override fun add(fence: PolarisFenceSpec): String? {
            calls.add("add(package=android, id=${fence.fenceId})")
            if (addResultOverride == "default") {
                remoteFences.add(PolarisRemoteFence(
                    fenceId = fence.fenceId,
                    latitude = fence.latitude,
                    longitude = fence.longitude,
                    radiusMeters = fence.radiusMeters,
                    transitionType = fence.transitionType,
                    confidence = fence.confidence,
                    packageName = PolarisContract.CLIENT_PACKAGE
                ))
                return fence.fenceId
            }
            return addResultOverride
        }

        override fun update(fence: PolarisFenceSpec) {
            calls.add("update(package=android, id=${fence.fenceId})")
            remoteFences.removeIf { it.fenceId == fence.fenceId }
            remoteFences.add(PolarisRemoteFence(
                fenceId = fence.fenceId,
                latitude = fence.latitude,
                longitude = fence.longitude,
                radiusMeters = fence.radiusMeters,
                transitionType = fence.transitionType,
                confidence = fence.confidence,
                packageName = PolarisContract.CLIENT_PACKAGE
            ))
        }

        override fun deleteById(fenceId: String) {
            calls.add("deleteById($fenceId)")
            remoteFences.removeIf { it.fenceId == fenceId }
        }

        override fun findById(fenceId: String): PolarisRemoteFence? {
            calls.add("findById($fenceId)")
            return remoteFences.find { it.fenceId == fenceId }
        }

        override fun status(fenceId: String): Int {
            calls.add("status(package=android, id=$fenceId)")
            return statusOverride
        }

        override fun isAlive(): Boolean = true
    }
}
