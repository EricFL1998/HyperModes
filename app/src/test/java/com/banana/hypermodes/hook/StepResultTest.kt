package com.banana.hypermodes.hook

import org.junit.Assert.assertEquals
import org.junit.Test

class StepResultTest {

    @Test
    fun `ok formats as name colon OK`() {
        assertEquals("setZenMode: OK", StepResult.ok("setZenMode").format())
    }

    @Test
    fun `fail with throwable formats message`() {
        val r = StepResult.fail("getSleepAlarm", IllegalStateException("no bedtime"))
        assertEquals("getSleepAlarm: FAIL: no bedtime", r.format())
    }

    @Test
    fun `fail with null message falls back to exception class name`() {
        val r = StepResult.fail("getSleepAlarm", IllegalStateException())
        assertEquals("getSleepAlarm: FAIL: IllegalStateException", r.format())
    }

    @Test
    fun `fail with plain detail string`() {
        val r = StepResult.fail("saveSleepAlarm", "skipped: alarm mutation failed")
        assertEquals("saveSleepAlarm: FAIL: skipped: alarm mutation failed", r.format())
    }
}
