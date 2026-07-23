package com.banana.hypermodes.systemserver.trigger

import android.app.AlarmManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import java.util.Calendar

/**
 * Manages scheduled mode triggers using AlarmManager with OnAlarmListener.
 * Runs entirely in system_server, no application process needed (zero-process architecture).
 */
class ScheduledModeManager(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val handler = Handler(Looper.getMainLooper())
    private val scheduledAlarms = mutableMapOf<String, AlarmManager.OnAlarmListener>()

    companion object {
        private const val TAG = "ScheduledModeManager"
    }

    /**
     * Update schedules for all modes.
     * Cancels all existing alarms and reschedules based on the new mode list.
     */
    fun updateSchedules(modes: List<ModeConfig>) {
        log("Updating schedules for ${modes.size} modes")

        // Cancel alarms from the previous configuration before rebuilding it.
        cancelAllSchedules()

        // Schedule only enabled SCHEDULED modes. Disabled schedules retain
        // their stored times but must not leave an old PendingIntent active.
        modes.filter {
            it.type == ModeType.SCHEDULED && it.scheduleEnabled != false
        }.forEach { mode ->
            scheduleMode(mode)
        }

        log("Schedules updated")
    }

    /**
     * Schedule a single mode.
     * Creates start and end alarms based on mode configuration.
     */
    private fun scheduleMode(mode: ModeConfig) {
        if (mode.type != ModeType.SCHEDULED) {
            log("Skipping non-scheduled mode: ${mode.name}")
            return
        }

        if (mode.scheduleEnabled == false) {
            log("Skipping disabled schedule: ${mode.name}")
            return
        }

        val startTime = mode.startTime
        val endTime = mode.endTime
        if (startTime == null || endTime == null) {
            log("Skipping mode with missing times: ${mode.name}")
            return
        }

        val repeatDays = mode.repeatDays
            ?.filter { it in 1..7 }
            ?.takeIf { it.isNotEmpty() }
            ?: listOf(1, 2, 3, 4, 5, 6, 7)

        log("Scheduling mode: ${mode.name}, start=$startTime, end=$endTime, days=$repeatDays")

        scheduleAlarm(mode.id, startTime, repeatDays, true)
        scheduleAlarm(mode.id, endTime, repeatDays, false)
    }

    /**
     * Schedule a single alarm (start or end) using OnAlarmListener.
     * Runs directly in system_server without requiring app process.
     */
    private fun scheduleAlarm(modeId: String, time: String, repeatDays: List<Int>, isStart: Boolean): Boolean {
        val parsed = parseTime(time)
        if (parsed == null) {
            log("Skipping invalid time '$time' for mode $modeId")
            return false
        }
        val (hour, minute) = parsed
        val nextOccurrence = getNextOccurrence(hour, minute, repeatDays)

        try {
            val tag = "${modeId}_${if (isStart) "start" else "end"}"

            // Create listener that runs in system_server
            val listener = AlarmManager.OnAlarmListener {
                log("Alarm triggered: $tag")
                try {
                    if (isStart) {
                        engine.activateMode(modeId)
                    } else {
                        engine.deactivateMode(modeId)
                    }
                } catch (e: Exception) {
                    log("Failed to ${if (isStart) "activate" else "deactivate"} mode $modeId: ${e.message}")
                    e.printStackTrace()
                }
            }

            // Schedule the alarm with listener (no PendingIntent needed)
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                nextOccurrence,
                tag,
                listener,
                handler
            )

            // Store listener for later cancellation
            scheduledAlarms[tag] = listener

            val calendar = Calendar.getInstance().apply { timeInMillis = nextOccurrence }
            log("Scheduled ${if (isStart) "START" else "END"} alarm for mode $modeId at ${calendar.time}")
            return true
        } catch (e: Exception) {
            log("Failed to schedule alarm: ${e.message}")
            e.printStackTrace()
            return false
        }
    }

    /**
     * Cancel all scheduled alarms.
     */
    private fun cancelAllSchedules() {
        scheduledAlarms.forEach { (tag, listener) ->
            alarmManager.cancel(listener)
            log("Canceled alarm: $tag")
        }
        scheduledAlarms.clear()
        log("Canceled all tracked schedules")
    }

    /**
     * Cancel a specific mode's alarms.
     */
    fun cancelMode(modeId: String) {
        log("Canceling mode: $modeId")

        // Cancel start alarm
        val startTag = "${modeId}_start"
        scheduledAlarms[startTag]?.let { listener ->
            alarmManager.cancel(listener)
            scheduledAlarms.remove(startTag)
            log("Canceled start alarm: $startTag")
        }

        // Cancel end alarm
        val endTag = "${modeId}_end"
        scheduledAlarms[endTag]?.let { listener ->
            alarmManager.cancel(listener)
            scheduledAlarms.remove(endTag)
            log("Canceled end alarm: $endTag")
        }
    }

    /**
     * Parse and validate a time string in strict "HH:mm" form.
     */
    private fun parseTime(time: String): Pair<Int, Int>? {
        val parts = time.split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null
        return hour to minute
    }

    /**
     * Calculate the next occurrence of the specified time on the specified days.
     *
     * @param hour Hour (0-23)
     * @param minute Minute (0-59)
     * @param days List of days (1=Monday, 7=Sunday)
     * @return Timestamp in milliseconds
     */
    private fun getNextOccurrence(hour: Int, minute: Int, days: List<Int>): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If the target time has passed today, start checking from tomorrow
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Find the next day that matches one of the repeat days
        for (i in 0..6) {
            val dayOfWeek = target.get(Calendar.DAY_OF_WEEK)
            // Convert Calendar.DAY_OF_WEEK (1=Sunday, 2=Monday) to our format (1=Monday, 7=Sunday)
            val ourDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

            if (days.contains(ourDayOfWeek)) {
                return target.timeInMillis
            }

            target.add(Calendar.DAY_OF_MONTH, 1)
        }

        // Should never reach here if days list is valid
        return target.timeInMillis
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }
}
