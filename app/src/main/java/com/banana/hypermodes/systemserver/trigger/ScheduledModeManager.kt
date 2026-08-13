package com.banana.hypermodes.systemserver.trigger

import android.app.AlarmManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.systemserver.PackagePresencePolicy
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.TriggerGroup
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
        private val ALL_DAYS = listOf(1, 2, 3, 4, 5, 6, 7)
    }

    /**
     * Update schedules for all modes.
     * Cancels all existing alarms and reschedules based on the new mode list.
     */
    fun updateSchedules(modes: List<ModeConfig>, allowActivation: Boolean) {
        log("Updating schedules for ${modes.size} modes")

        // Cancel alarms from the previous configuration before rebuilding it.
        cancelAllSchedules()

        // Schedule only enabled SCHEDULED modes. Disabled schedules retain
        // their stored times but must not leave an old PendingIntent active.
        modes.filter {
            (it.type == ModeType.SCHEDULED) && (it.scheduleEnabled != false)
        }.forEach { mode ->
            scheduleMode(mode, allowActivation)
        }

        // Time triggers inside trigger groups: each mode may carry several
        // Time triggers (Single or inside a Compound group), independent of
        // the legacy single-schedule fields above.
        modes.forEach { mode ->
            mode.triggerGroups.forEachIndexed { groupIndex, group ->
                val triggers = when (group) {
                    is TriggerGroup.Single -> listOf(group.trigger)
                    is TriggerGroup.Compound -> group.triggers
                }
                triggers.forEachIndexed { triggerIndex, trigger ->
                    if (trigger is ComplexTrigger.Time) {
                        scheduleTimeTrigger(mode, groupIndex, triggerIndex, trigger, allowActivation)
                    }
                }
            }
        }

        log("Schedules updated")
    }

    /**
     * Check if the given mode should be active according to its schedule.
     * Covers both the schedule fields and Time triggers inside trigger groups.
     */
    fun isModeActive(mode: ModeConfig): Boolean {
        if (mode.type == ModeType.SCHEDULED && mode.scheduleEnabled != false) {
            val startTime = mode.startTime
            val endTime = mode.endTime
            if (startTime != null && endTime != null) {
                val repeatDays = mode.repeatDays ?: ALL_DAYS
                if (isCurrentlyInSchedule(startTime, endTime, repeatDays)) {
                    val periodStart = getCurrentPeriodStart(startTime, repeatDays)
                    if (!engine.isDismissedInCurrentPeriod(mode.id, periodStart)) {
                        return true
                    }
                }
            }
        }

        // Bedtime's window is owned by the bedtime listener/reconciler.
        if (mode.type == ModeType.BEDTIME) return false

        for (group in mode.triggerGroups) {
            val triggers = when (group) {
                is TriggerGroup.Single -> listOf(group.trigger)
                is TriggerGroup.Compound -> group.triggers
            }
            for (trigger in triggers) {
                if (trigger !is ComplexTrigger.Time) continue
                val repeatDays = normalizedDays(trigger.repeatDays)
                if (isCurrentlyInSchedule(trigger.startTime, trigger.endTime, repeatDays)) {
                    val periodStart = getCurrentPeriodStart(trigger.startTime, repeatDays)
                    if (!engine.isDismissedInCurrentPeriod(mode.id, periodStart)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun normalizedDays(repeatDays: List<Int>?): List<Int> =
        repeatDays?.filter { it in 1..7 }?.takeIf { it.isNotEmpty() } ?: ALL_DAYS

    /**
     * Schedule a single mode.
     * Creates start and end alarms based on mode configuration.
     * If current time is within the scheduled period, activate immediately.
     */
    private fun scheduleMode(mode: ModeConfig, allowActivation: Boolean) {
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

        // Check if current time is within the scheduled period
        if (allowActivation && isCurrentlyInSchedule(startTime, endTime, repeatDays)) {
            val periodStart = getCurrentPeriodStart(startTime, repeatDays)
            if (engine.isDismissedInCurrentPeriod(mode.id, periodStart)) {
                log("Current time is within schedule but mode was dismissed this period: ${mode.name}")
            } else {
                log("Current time is within schedule, activating immediately: ${mode.name}")
                engine.clearDismissRecord(mode.id)
                engine.activateMode(mode.id)
            }
        }

        scheduleAlarm(mode.id, startTime, repeatDays, isStart = true)
        scheduleAlarm(mode.id, endTime, repeatDays, isStart = false)
    }

    /**
     * Schedule one v1.3 complex Time trigger. Multiple Time triggers per mode
     * are supported; each gets its own alarm pair keyed by trigger index.
     * Activation/deactivation still goes through the mode ID, and the engine's
     * isAnyTriggerActive check keeps the mode on while another trigger holds it.
     */
    private fun scheduleTimeTrigger(
        mode: ModeConfig,
        groupIndex: Int,
        triggerIndex: Int,
        trigger: ComplexTrigger.Time,
        allowActivation: Boolean
    ) {
        if (mode.type == ModeType.BEDTIME) {
            // The bedtime window is owned by the bedtime listener/reconciler.
            log("Skipping time trigger on bedtime mode: ${mode.name}")
            return
        }

        val repeatDays = normalizedDays(trigger.repeatDays)
        val alarmKey = "${mode.id}_g${groupIndex}_t${triggerIndex}"

        log("Scheduling time trigger for mode: ${mode.name}, " +
                "start=${trigger.startTime}, end=${trigger.endTime}, days=$repeatDays")

        // If the window is open right now, activate immediately unless the
        // user dismissed this mode during the current period.
        if (allowActivation && isCurrentlyInSchedule(trigger.startTime, trigger.endTime, repeatDays)) {
            val periodStart = getCurrentPeriodStart(trigger.startTime, repeatDays)
            if (engine.isDismissedInCurrentPeriod(mode.id, periodStart)) {
                log("Within time trigger window but mode was dismissed this period: ${mode.name}")
            } else {
                log("Within time trigger window, activating immediately: ${mode.name}")
                engine.clearDismissRecord(mode.id)
                engine.activateMode(mode.id)
            }
        }

        scheduleAlarm(mode.id, trigger.startTime, repeatDays, isStart = true, alarmKey = alarmKey)
        scheduleAlarm(mode.id, trigger.endTime, repeatDays, isStart = false, alarmKey = alarmKey)
    }

    /**
     * Schedule a single alarm (start or end) using OnAlarmListener.
     * Runs directly in system_server without requiring app process.
     *
     * @param alarmKey Key used for the alarm tag/cancellation; defaults to the
     * mode ID but trigger group time triggers pass
     * "${modeId}_g<groupIndex>_t<index>" so several triggers of one mode can
     * coexist.
     */
    private fun scheduleAlarm(
        modeId: String,
        time: String,
        repeatDays: List<Int>,
        isStart: Boolean,
        alarmKey: String = modeId
    ): Boolean {
        val parsed = parseTime(time)
        if (parsed == null) {
            log("Skipping invalid time '$time' for mode $modeId")
            return false
        }
        val (hour, minute) = parsed
        val nextOccurrence = getNextOccurrence(hour, minute, repeatDays)

        try {
            val tag = "${alarmKey}_${if (isStart) "start" else "end"}"

            // Create listener that runs in system_server
            val listener = AlarmManager.OnAlarmListener {
                log("Alarm triggered: $tag")
                try {
                    // Safety check: is the engine still running and is the package still installed?
                    if (engine.getLifecycleState() == RoutineCoreEngine.LifecycleState.REMOVED) {
                        log("Skipping alarm: engine is REMOVED")
                        return@OnAlarmListener
                    }

                    if (!isPackageInstalled(context, com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)) {
                        when (PackagePresencePolicy.onMissingPackage(engine.getLifecycleState())) {
                            PackagePresencePolicy.MissingPackageAction.SHUTDOWN -> {
                                log("Skipping alarm: package not installed, requesting engine shutdown")
                                engine.shutdownForPackageRemoval()
                            }
                            PackagePresencePolicy.MissingPackageAction.SKIP_ONLY -> {
                                log("Skipping alarm: package temporarily unavailable during replacement")
                            }
                            PackagePresencePolicy.MissingPackageAction.ALLOW -> Unit
                        }
                        return@OnAlarmListener
                    }

                    if (isStart) {
                        // Check if mode was manually dismissed in this period
                        // periodStartTime is the time when this alarm fires (start of new period)
                        if (engine.isDismissedInCurrentPeriod(modeId, nextOccurrence)) {
                            log("Mode $modeId was dismissed in current period, skipping auto-activation")
                        } else {
                            // Clear any old dismiss record since this is a new period
                            engine.clearDismissRecord(modeId)
                            engine.activateMode(modeId)
                        }
                    } else {
                        // End alarm: automatic deactivation, not user dismiss
                        engine.deactivateMode(modeId, isManualDismiss = false)
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
    fun cancelAllSchedules() {
        scheduledAlarms.forEach { (tag, listener) ->
            alarmManager.cancel(listener)
            log("Canceled alarm: $tag")
        }
        scheduledAlarms.clear()
        log("Canceled all tracked schedules")
    }

    /**
     * Cancel a specific mode's alarms — the start/end pair plus any trigger
     * group time trigger alarms ("${modeId}_g<groupIndex>_t<index>_start/end").
     */
    fun cancelMode(modeId: String) {
        log("Canceling mode: $modeId")

        val tags = scheduledAlarms.keys.filter {
            it == "${modeId}_start" || it == "${modeId}_end" || it.startsWith("${modeId}_g")
        }
        tags.forEach { tag ->
            scheduledAlarms.remove(tag)?.let { listener ->
                alarmManager.cancel(listener)
                log("Canceled alarm: $tag")
            }
        }
    }

    /**
     * Parse and validate a time string in strict "HH:mm" form.
     */
    /**
     * Check if current time is within the scheduled period.
     * Supports cross-day schedules (e.g., 22:00-07:00).
     *
     * @param startTime Start time in HH:mm format
     * @param endTime End time in HH:mm format
     * @param repeatDays List of days (1=Monday, 7=Sunday)
     * @return true if current time is within the schedule
     */
    private fun isCurrentlyInSchedule(startTime: String, endTime: String, repeatDays: List<Int>): Boolean {
        val startParsed = parseTime(startTime) ?: return false
        val endParsed = parseTime(endTime) ?: return false
        val (startHour, startMinute) = startParsed
        val (endHour, endMinute) = endParsed

        val now = Calendar.getInstance()
        val currentMinutes = now[Calendar.HOUR_OF_DAY] * 60 + now[Calendar.MINUTE]
        val startMinutes = startHour * 60 + startMinute
        val endMinutes = endHour * 60 + endMinute

        // Convert current day to our format (1=Monday, 7=Sunday)
        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val currentDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1

        // Check if current day is in repeat days
        if (!repeatDays.contains(currentDay)) {
            // For cross-day schedules, also check if we're in the "end" portion from yesterday
            if (currentMinutes in 0..<endMinutes && endMinutes < startMinutes) {
                // We're in the early morning portion, check if yesterday is in repeat days
                val yesterday = if (currentDay == 1) 7 else currentDay - 1
                if (!repeatDays.contains(yesterday)) {
                    return false
                }
            } else {
                return false
            }
        }

        // Check if current time is within the range
        return if (endMinutes < startMinutes) {
            // Cross-day schedule (e.g., 22:00-07:00)
            currentMinutes >= startMinutes || currentMinutes < endMinutes
        } else {
            // Same-day schedule (e.g., 09:00-17:00)
            currentMinutes >= startMinutes && currentMinutes < endMinutes
        }
    }

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
        repeat(7) {
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

    private fun getCurrentPeriodStart(time: String, repeatDays: List<Int>): Long {
        val parsed = parseTime(time) ?: return System.currentTimeMillis()
        val (hour, minute) = parsed
        val now = Calendar.getInstance()
        val candidate = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (candidate.timeInMillis > now.timeInMillis) {
            candidate.add(Calendar.DAY_OF_MONTH, -1)
        }

        repeat(7) {
            val dayOfWeek = candidate.get(Calendar.DAY_OF_WEEK)
            val ourDayOfWeek = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
            if (repeatDays.contains(ourDayOfWeek)) {
                return candidate.timeInMillis
            }
            candidate.add(Calendar.DAY_OF_MONTH, -1)
        }

        return candidate.timeInMillis
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
    }
}
