package com.banana.hypermodes.systemserver.geofence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class PolarisAidlContractTest {
    private val projectRoot = File(System.getProperty("user.dir"))
        .let { if (it.name == "app") it.parentFile else it }

    @Test
    fun `geo manager methods preserve the device transaction order`() {
        val source = File(
            projectRoot,
            "app/src/main/aidl/com/xiaomi/gnss/polaris/geofence/IMiGeoManagerService.aidl"
        ).readText()
        val methodNames = Regex("(?:String|void|int|Bundle|ComponentName|MiGeofence|List<MiGeofence>)\\s+(\\w+)\\s*\\(")
            .findAll(source)
            .map { it.groupValues[1] }
            .toList()

        assertEquals(
            listOf(
                "getVendorVersion",
                "addGeofenceWithFlag",
                "addGeofence",
                "deleteGeofence",
                "deleteGeofenceById",
                "updateGeofence",
                "listGeofence",
                "findGeofenceById",
                "registerComponent",
                "getComponent",
                "sendDebugEvent",
                "getAllGeofenceStatus",
                "getGeofenceStatus"
            ),
            methodNames
        )
        assertFalse(source.contains("unregisterComponent"))
    }
}
