package com.banana.hypermodes.engine

import com.banana.hypermodes.data.ModeSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class ModeSchedulerTest {
    private val zone: ZoneId = ZoneId.of("UTC")

    private fun at(iso: String) = Instant.parse(iso).toEpochMilli()

    private fun schedule(
        startH: Int, startM: Int, endH: Int, endM: Int,
        days: Int = 0x7F, enabled: Boolean = true
    ) = ModeSchedule(enabled, startH, startM, endH, endM, days)

    @Test
    fun `disabled schedule returns null`() {
        val s = schedule(9, 0, 17, 0, enabled = false)
        assertNull(ModeScheduler.nextTrigger(s, at("2026-07-22T08:00:00Z"), zone))
    }

    @Test
    fun `before window - next is START today`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T08:00:00Z"), zone)
        )
    }

    @Test
    fun `inside window - next is END today`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T17:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-22T12:00:00Z"), zone)
        )
    }

    @Test
    fun `after window - next is START tomorrow`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-23T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T18:00:00Z"), zone)
        )
    }

    @Test
    fun `overnight window - before start, next is START tonight`() {
        val s = schedule(23, 0, 7, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T23:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T22:00:00Z"), zone)
        )
    }

    @Test
    fun `overnight window - after midnight, next is END this morning`() {
        val s = schedule(23, 0, 7, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T07:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-22T02:00:00Z"), zone)
        )
    }

    @Test
    fun `weekdays only - friday evening rolls to monday`() {
        // Mon..Fri = bits 0..4 = 0b0011111 = 31; 2026-07-24 is a Friday
        val s = schedule(9, 0, 17, 0, days = 31)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-27T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-24T18:00:00Z"), zone)
        )
    }

    @Test
    fun `single weekday - waits a full week`() {
        // Tuesday only = bit 1 = 2; 2026-07-22 is a Wednesday
        val s = schedule(9, 0, 17, 0, days = 2)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-28T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T12:00:00Z"), zone)
        )
    }

    @Test
    fun `overnight weekday window - saturday early morning still ends`() {
        // Weekdays 23:00-07:00; Sat 2026-07-25 02:00 is inside Friday's window
        val s = schedule(23, 0, 7, 0, days = 31)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-25T07:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-25T02:00:00Z"), zone)
        )
    }

    @Test
    fun `exactly at start time counts as inside window`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T17:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-22T09:00:00Z"), zone)
        )
    }
}
