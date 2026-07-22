package com.banana.hypermodes.engine

import com.banana.hypermodes.data.ModeSchedule
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

/**
 * Pure next-trigger calculation for a ModeSchedule. No Android dependencies —
 * unit-tested on the JVM (ModeSchedulerTest).
 */
object ModeScheduler {

    enum class Trigger { START, END }

    data class NextTrigger(val epochMillis: Long, val trigger: Trigger)

    /**
     * Next schedule boundary strictly after [nowMillis], or null when the
     * schedule is disabled. Overnight windows (end <= start) end the next day.
     * repeatDays: bit0 = Monday ... bit6 = Sunday (Protocol semantics).
     */
    fun nextTrigger(schedule: ModeSchedule, nowMillis: Long, zone: ZoneId): NextTrigger? {
        if (!schedule.enabled) return null
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val start = LocalTime.of(schedule.startHour, schedule.startMinute)
        val end = LocalTime.of(schedule.endHour, schedule.endMinute)
        val overnight = end <= start

        var best: NextTrigger? = null
        // Yesterday (an overnight window may have started then) through a week
        // ahead (guaranteed to contain the next active day).
        for (offset in -1..7) {
            val day = today.plusDays(offset.toLong())
            val bit = day.dayOfWeek.value - 1 // Monday = 0 ... Sunday = 6
            if (schedule.repeatDays and (1 shl bit) == 0) continue
            val startMillis = day.atTime(start).atZone(zone).toInstant().toEpochMilli()
            val endDay = if (overnight) day.plusDays(1) else day
            val endMillis = endDay.atTime(end).atZone(zone).toInstant().toEpochMilli()
            for (candidate in listOf(
                NextTrigger(startMillis, Trigger.START),
                NextTrigger(endMillis, Trigger.END)
            )) {
                if (candidate.epochMillis > nowMillis &&
                    (best == null || candidate.epochMillis < best.epochMillis)
                ) {
                    best = candidate
                }
            }
        }
        return best
    }
}
