package com.banana.hypermodes.ui

import org.junit.Assert.*
import org.junit.Test

class XiaomiLocationCoordinatesTest {

    @Test
    fun `outside China is unchanged`() {
        val result = XiaomiLocationCoordinates.toPolaris(51.5074, -0.1278)
        assertNotNull(result)
        assertEquals(51.5074, result!!.first, 0.0001)
        assertEquals(-0.1278, result.second, 0.0001)
    }

    @Test
    fun `Beijing GCJ coordinate subtracts rather than adds offset`() {
        val result = XiaomiLocationCoordinates.toPolaris(39.908823, 116.397470)
        assertNotNull(result)
        val converted = result!!
        assertEquals(39.9074, converted.first, 0.001)
        assertEquals(116.3912, converted.second, 0.001)
        assertTrue(converted.first < 39.908823)
        assertTrue(converted.second < 116.397470)
    }

    @Test
    fun `NaN coordinates return null`() {
        assertNull(XiaomiLocationCoordinates.toPolaris(Double.NaN, 116.4))
        assertNull(XiaomiLocationCoordinates.toPolaris(39.9, Double.NaN))
    }

    @Test
    fun `infinite coordinates return null`() {
        assertNull(XiaomiLocationCoordinates.toPolaris(Double.POSITIVE_INFINITY, 116.4))
        assertNull(XiaomiLocationCoordinates.toPolaris(39.9, Double.NEGATIVE_INFINITY))
    }

    @Test
    fun `zero coordinates return null`() {
        assertNull(XiaomiLocationCoordinates.toPolaris(0.0, 0.0))
    }

    @Test
    fun `out of range latitude returns null`() {
        assertNull(XiaomiLocationCoordinates.toPolaris(91.0, 116.4))
        assertNull(XiaomiLocationCoordinates.toPolaris(-91.0, 116.4))
    }

    @Test
    fun `out of range longitude returns null`() {
        assertNull(XiaomiLocationCoordinates.toPolaris(39.9, 181.0))
        assertNull(XiaomiLocationCoordinates.toPolaris(39.9, -181.0))
    }

    @Test
    fun `Shanghai GCJ coordinate is converted`() {
        val result = XiaomiLocationCoordinates.toPolaris(31.230416, 121.473701)
        assertNotNull(result)
        val converted = result!!
        // Verify conversion happened (coordinates changed)
        val latChanged = kotlin.math.abs(converted.first - 31.230416) > 0.0001
        val lonChanged = kotlin.math.abs(converted.second - 121.473701) > 0.0001
        assertTrue("Latitude should be converted", latChanged || converted.first < 31.230416)
        assertTrue("Longitude should be converted", lonChanged || converted.second < 121.473701)
    }

    @Test
    fun `valid non-zero coordinates outside China unchanged`() {
        val result = XiaomiLocationCoordinates.toPolaris(40.7128, -74.0060) // NYC
        assertNotNull(result)
        assertEquals(40.7128, result!!.first, 0.0001)
        assertEquals(-74.0060, result.second, 0.0001)
    }
}
