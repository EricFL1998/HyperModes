package com.banana.hypermodes.systemserver.geofence

import org.junit.Assert.*
import org.junit.Test

class PolarisGeofenceContractsTest {

    @Test
    fun `actual Polaris payload is accepted`() {
        assertEquals(
            PolarisCallback("hypermodes_abc", 11),
            PolarisContract.parseCallback("hypermodes_abc", 11)
        )
    }

    @Test
    fun `legacy extras cannot form a valid payload`() {
        assertNull(PolarisContract.parseCallback(null, -1))
        assertNull(PolarisContract.parseCallback("other_abc", 11))
        assertNull(PolarisContract.parseCallback("hypermodes_abc", 20))
    }

    @Test
    fun `parseCallback accepts EVENT_ENTER`() {
        val result = PolarisContract.parseCallback("hypermodes_test", 11)
        assertNotNull(result)
        assertEquals("hypermodes_test", result?.fenceId)
        assertEquals(11, result?.event)
    }

    @Test
    fun `parseCallback accepts EVENT_EXIT`() {
        val result = PolarisContract.parseCallback("hypermodes_test", 12)
        assertNotNull(result)
        assertEquals("hypermodes_test", result?.fenceId)
        assertEquals(12, result?.event)
    }

    @Test
    fun `parseCallback rejects blank fence ID`() {
        assertNull(PolarisContract.parseCallback("", 11))
        assertNull(PolarisContract.parseCallback("   ", 11))
    }

    @Test
    fun `parseCallback rejects fence ID without prefix`() {
        assertNull(PolarisContract.parseCallback("mymode_abc", 11))
        assertNull(PolarisContract.parseCallback("abc", 12))
    }

    @Test
    fun `parseCallback rejects invalid event codes`() {
        assertNull(PolarisContract.parseCallback("hypermodes_abc", 0))
        assertNull(PolarisContract.parseCallback("hypermodes_abc", 1))
        assertNull(PolarisContract.parseCallback("hypermodes_abc", 10))
        assertNull(PolarisContract.parseCallback("hypermodes_abc", 13))
    }

    @Test
    fun `senderIsPolaris accepts valid Polaris sender`() {
        val packages = arrayOf("com.xiaomi.gnss.polaris", "other.package")
        assertTrue(
            PolarisContract.senderIsPolaris("com.xiaomi.gnss.polaris", packages)
        )
    }

    @Test
    fun `senderIsPolaris rejects mismatched sender`() {
        val packages = arrayOf("com.xiaomi.gnss.polaris")
        assertFalse(PolarisContract.senderIsPolaris("other.package", packages))
    }

    @Test
    fun `senderIsPolaris rejects when package not in uid list`() {
        val packages = arrayOf("other.package")
        assertFalse(
            PolarisContract.senderIsPolaris("com.xiaomi.gnss.polaris", packages)
        )
    }

    @Test
    fun `senderIsPolaris rejects null inputs`() {
        assertFalse(PolarisContract.senderIsPolaris(null, arrayOf("com.xiaomi.gnss.polaris")))
        assertFalse(PolarisContract.senderIsPolaris("com.xiaomi.gnss.polaris", null))
        assertFalse(PolarisContract.senderIsPolaris(null, null))
    }
}
