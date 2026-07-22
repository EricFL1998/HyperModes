package com.banana.hypermodes.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.banana.hypermodes.data.ModeSchedule

/** Shared state of the schedule as reported by the DeskClock hook. */
object DeskClockState {
    /** Last schedule read from DeskClock, or null if never queried / module dead. */
    var schedule by mutableStateOf<ModeSchedule?>(null)
        private set

    /** Whether the wake alarm (起床响铃) is currently enabled in DeskClock. */
    var wakeEnabled by mutableStateOf(false)
        private set

    /** Whether bedtime was ever set up in the Clock app (bedTimeAlarmCompleted). */
    var configured by mutableStateOf(true)
        private set

    /** 就寝提醒 lead time in minutes before sleep time (-1 = none). */
    var reminderMinutes by mutableStateOf(15)
        private set

    /** Whether the official DeskClock bedtime is active RIGHT NOW
     *  (powerkeeper sleep mode / inZenMode), as pushed by the hook. */
    var bedtimeActive by mutableStateOf(false)
        private set

    /** Restore the last known schedule from disk so the UI never shows
     *  placeholders while waiting for the hook's first reply. */
    fun restore(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        if (!prefs.contains(KEY_SLEEP_HOUR)) return
        schedule = ModeSchedule(
            enabled = prefs.getBoolean(KEY_WAKE_ENABLED, false),
            startHour = prefs.getInt(KEY_SLEEP_HOUR, 22),
            startMinute = prefs.getInt(KEY_SLEEP_MIN, 30),
            endHour = prefs.getInt(KEY_WAKE_HOUR, 7),
            endMinute = prefs.getInt(KEY_WAKE_MIN, 0),
            repeatDays = prefs.getInt(KEY_REPEAT_DAYS, 0x7F)
        )
        wakeEnabled = prefs.getBoolean(KEY_WAKE_ENABLED, false)
        configured = prefs.getBoolean(KEY_CONFIGURED, true)
        reminderMinutes = prefs.getInt(KEY_REMINDER_MINUTES, 15)
        bedtimeActive = prefs.getBoolean(KEY_BEDTIME_ACTIVE, false)
    }

    /** Official bedtime active state pushed by the hook (scheduled
     *  activation, Clock-app toggle, or a state query reply). */
    fun updateBedtimeActive(context: Context, active: Boolean) {
        bedtimeActive = active
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_BEDTIME_ACTIVE, active)
            .apply()
    }

    fun update(
        sleepHour: Int, sleepMin: Int,
        wakeHour: Int, wakeMin: Int,
        wakeEnabled: Boolean,
        repeatDays: Int,
        configured: Boolean,
        reminderMinutes: Int = this.reminderMinutes
    ) {
        schedule = ModeSchedule(
            enabled = wakeEnabled,
            startHour = sleepHour,
            startMinute = sleepMin,
            endHour = wakeHour,
            endMinute = wakeMin,
            repeatDays = repeatDays
        )
        this.wakeEnabled = wakeEnabled
        this.configured = configured
        this.reminderMinutes = reminderMinutes
    }

    /** Persist the current schedule so the next cold start shows it instantly. */
    fun persist(context: Context) {
        val s = schedule ?: return
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit()
            .putInt(KEY_SLEEP_HOUR, s.startHour)
            .putInt(KEY_SLEEP_MIN, s.startMinute)
            .putInt(KEY_WAKE_HOUR, s.endHour)
            .putInt(KEY_WAKE_MIN, s.endMinute)
            .putBoolean(KEY_WAKE_ENABLED, wakeEnabled)
            .putInt(KEY_REPEAT_DAYS, s.repeatDays)
            .putBoolean(KEY_CONFIGURED, configured)
            .putInt(KEY_REMINDER_MINUTES, reminderMinutes)
            .apply()
    }

    private const val PREF_NAME = "deskclock_state"
    private const val KEY_SLEEP_HOUR = "sleepHour"
    private const val KEY_SLEEP_MIN = "sleepMin"
    private const val KEY_WAKE_HOUR = "wakeHour"
    private const val KEY_WAKE_MIN = "wakeMin"
    private const val KEY_WAKE_ENABLED = "wakeEnabled"
    private const val KEY_REPEAT_DAYS = "repeatDays"
    private const val KEY_CONFIGURED = "configured"
    private const val KEY_REMINDER_MINUTES = "reminderMinutes"
    private const val KEY_BEDTIME_ACTIVE = "bedtimeActive"
}
