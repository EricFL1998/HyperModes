package com.banana.hypermodes.systemserver.trigger

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Unit tests for PolarisGeofenceAdapter result types.
 *
 * Note: Full integration testing requires on-device execution with actual
 * Polaris service. These tests validate result structure and classification.
 * Bundle structure validation is performed during on-device testing.
 */
class PolarisGeofenceAdapterTest {

    @Test
    fun `CapabilityResult Supported is correctly classified`() {
        val result = PolarisGeofenceAdapter.CapabilityResult.Supported

        assertNotNull(result)
        assertEquals("Supported", result.javaClass.simpleName)
    }

    @Test
    fun `CapabilityResult BindingFailed contains reason`() {
        val reason = "Service not found"
        val result = PolarisGeofenceAdapter.CapabilityResult.BindingFailed(reason)

        assertEquals("BindingFailed", result.javaClass.simpleName)
        assertEquals(reason, result.reason)
    }

    @Test
    fun `CapabilityResult TransactionFailed contains reason`() {
        val reason = "Permission denied"
        val result = PolarisGeofenceAdapter.CapabilityResult.TransactionFailed(reason)

        assertEquals("TransactionFailed", result.javaClass.simpleName)
        assertEquals(reason, result.reason)
    }

    @Test
    fun `CapabilityResult CallerRejected contains reason`() {
        val reason = "SecurityException: caller not allowed"
        val result = PolarisGeofenceAdapter.CapabilityResult.CallerRejected(reason)

        assertEquals("CallerRejected", result.javaClass.simpleName)
        assertEquals(reason, result.reason)
    }

    @Test
    fun `CapabilityResult ProbeError contains exception`() {
        val exception = RuntimeException("Test exception")
        val result = PolarisGeofenceAdapter.CapabilityResult.ProbeError(exception)

        assertEquals("ProbeError", result.javaClass.simpleName)
        assertEquals(exception, result.exception)
    }
}

