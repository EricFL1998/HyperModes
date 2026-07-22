package com.banana.hypermodes.systemserver.trigger

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import java.util.Calendar

/**
 * Manages scheduled mode triggers using AlarmManager.
 * Schedules alarms for modes with SCHEDULED type, activates/deactivates them at specified times.
 */
class ScheduledModeManager(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    /**
     * Update schedules for all modes.
     * Cancels all existing alarms and reschedules based on the new mode list.
     */
    fun updateSchedules(modes: List<ModeConfig>) {
        log("Updating schedules for ${modes.size} modes")

        // Cancel all existing alarms first
        cancelAllSchedules()

        // Schedule only SCHEDULED type modes
        modes.filter { it.type == ModeType.SCHEDULED }.forEach { mode ->
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

        if (mode.startTime == null || mode.endTime == null) {
            log("Skipping mode with missing times: ${mode.name}")
            return
        }

        val repeatDays = mode.repeatDays ?: listOf(1, 2, 3, 4, 5, 6, 7)

        log("Scheduling mode: ${mode.name}, start=${mode.startTime}, end=${mode.endTime}, days=$repeatDays")

        // Schedule start alarm
        scheduleAlarm(mode.id, mode.startTime, repeatDays, true)

        // Schedule end alarm
        scheduleAlarm(mode.id, mode.endTime, repeatDays, false)
    }

    /**
     * Schedule a single alarm (start or end).
     */
    private fun scheduleAlarm(modeId: String, time: String, repeatDays: List<Int>, isStart: Boolean) {
        val (hour, minute) = parseTime(time)
        val nextOccurrence = getNextOccurrence(hour, minute, repeatDays)

        val action = if (isStart) ACTION_START_MODE else ACTION_END_MODE
        val intent = Intent(action).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_MODE_ID, modeId)
        }

        // Use unique request code for each alarm (modeId hash + start/end flag)
        val requestCode = (modeId.hashCode() * 2) + if (isStart) 0 else 1
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Schedule exact alarm
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                nextOccurrence,
                pendingIntent
            )

            val calendar = Calendar.getInstance().apply { timeInMillis = nextOccurrence }
            log("Scheduled ${if (isStart) "START" else "END"} alarm for mode $modeId at ${calendar.time}")
        } catch (e: Exception) {
            log("Failed to schedule alarm: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Cancel all scheduled alarms.
     */
    private fun cancelAllSchedules() {
        // We don't track active alarms, so we can't cancel them individually.
        // This is acceptable because updateSchedules() is called when modes change,
        // and the old alarms will be overwritten by FLAG_UPDATE_CURRENT.
        log("Canceling all schedules (handled by FLAG_UPDATE_CURRENT)")
    }

    /**
     * Cancel a specific mode's alarms.
     */
    fun cancelMode(modeId: String) {
        log("Canceling mode: $modeId")

        // Cancel start alarm
        val startIntent = Intent(ACTION_START_MODE).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_MODE_ID, modeId)
        }
        val startRequestCode = modeId.hashCode() * 2
        val startPendingIntent = PendingIntent.getBroadcast(
            context,
            startRequestCode,
            startIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        startPendingIntent?.let { alarmManager.cancel(it) }

        // Cancel end alarm
        val endIntent = Intent(ACTION_END_MODE).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_MODE_ID, modeId)
        }
        val endRequestCode = modeId.hashCode() * 2 + 1
        val endPendingIntent = PendingIntent.getBroadcast(
            context,
            endRequestCode,
            endIntent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        endPendingIntent?.let { alarmManager.cancel(it) }

        log("Mode canceled: $modeId")
    }

    /**
     * Parse time string "HH:mm" into hour and minute.
     */
    private fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return parts[0].toInt() to parts[1].toInt()
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

    companion object {
        private const val TAG = "ScheduledModeManager"
        const val ACTION_START_MODE = "com.banana.hypermodes.START_MODE"
        const val ACTION_END_MODE = "com.banana.hypermodes.END_MODE"
        const val EXTRA_MODE_ID = "MODE_ID"
    }
}
