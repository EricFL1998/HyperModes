package com.banana.hypermodes.hook

import android.app.AutomaticZenRule
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.service.notification.Condition

/**
 * Typed façade over HyperOS DeskClock's internal bedtime APIs.
 * Runs INSIDE the com.android.deskclock process with its Context/ClassLoader.
 *
 * Class/method names per finding.md (decompiled HyperOS DeskClock).
 * Every step is individually caught and recorded — one renamed method must
 * never abort the remaining steps.
 */
class BedtimeController(
    private val context: Context,
    private val classLoader: ClassLoader,
    private val log: (String) -> Unit
) {
    companion object {
        private const val CLS_BEDTIME_UTIL = "com.android.deskclock.alarm.bedtime.BedtimeUtil"
        private const val CLS_HEALTH_DATA_UTIL = "com.android.deskclock.alarm.bedtime.HealthDataUtil"
        private const val CLS_MI_HOME_HELPER = "com.android.deskclock.alarm.bedtime.MiHomeHelper"
        private const val CLS_ALARM_HELPER = "com.android.deskclock.util.AlarmHelper"
        private const val CLS_DAYS_OF_WEEK = "com.android.deskclock.Alarm\$DaysOfWeek"
        private const val CLS_ZEN_MODE_UTIL = "com.android.deskclock.alarm.bedtime.ZenModeUtil"
        private const val CLS_FBE_UTIL = "com.android.deskclock.util.FBEUtil"

        // AlarmCheckboxLayout persists the bedtime screen's wake-alert checkbox
        // here (default SharedPreferences). If this value disagrees with the
        // alarm DB row, BedtimeManageActivity.setStatus() sees a transition on
        // next open and pops the 关闭一次/永久 dialog unprompted.
        private const val KEY_WAKE_UP_ALERT = "key_wake_up_alert"

        // SleepModeUtil.exitSleepMode equivalent — sent raw, no reflection.
        private const val POWERKEEPER_PACKAGE = "com.miui.powerkeeper"
        private const val ACTION_REQUEST_WAKE = "com.miui.powerkeeper_request_wake"
        private const val EXTRA_REASON = "reason"
        private const val REASON_DESK_CLOCK = 1

        // SleepModeUtil.inSleepMode equivalent — direct provider call.
        private const val POWERKEEPER_PROVIDER = "content://com.miui.powerkeeper.configure"
        private const val METHOD_GET_SLEEP_MODE_STATE = "getSleepModeState"
        private const val KEY_IS_IN_SLEEP = "isInSleep"

        // Android ZenMode integration
        private const val BEDTIME_RULE_NAME = "HyperModes Bedtime"
        private const val BEDTIME_RULE_ID = "hypermodes_bedtime"
    }

    private val bedtimeUtil by lazy { Reflect.findClass(CLS_BEDTIME_UTIL, classLoader) }
    private val healthDataUtil by lazy { Reflect.findClass(CLS_HEALTH_DATA_UTIL, classLoader) }
    private val miHomeHelper by lazy { Reflect.findClass(CLS_MI_HOME_HELPER, classLoader) }
    private val alarmHelper by lazy { Reflect.findClass(CLS_ALARM_HELPER, classLoader) }
    private val zenModeUtil by lazy { Reflect.findClass(CLS_ZEN_MODE_UTIL, classLoader) }
    private val fbeUtil by lazy { Reflect.findClass(CLS_FBE_UTIL, classLoader) }
    private val notificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /** Current powerkeeper sleep-mode state; false on any error. */
    fun querySleepModeState(): Boolean = try {
        val bundle = context.contentResolver.call(
            Uri.parse(POWERKEEPER_PROVIDER), METHOD_GET_SLEEP_MODE_STATE, null, null
        )
        bundle?.getBoolean(KEY_IS_IN_SLEEP, false) ?: false
    } catch (t: Throwable) {
        log("querySleepModeState error: ${t.message}")
        false
    }

    /** Read the persisted bedtime schedule exactly as the Clock app reads it:
     * sleep time from SharedPreferences, wake alarm from ContentProvider. */
    fun querySchedule(): ScheduleInfo = try {
        val sleepHour = Reflect.callStatic(bedtimeUtil, "getSleepAlarmHour", context) as Int
        val sleepMin = Reflect.callStatic(bedtimeUtil, "getSleepAlarmMin", context) as Int
        val configured = Reflect.callStatic(bedtimeUtil, "bedTimeAlarmCompleted", context) as Boolean

        var wakeHour = 7
        var wakeMin = 0
        var wakeEnabled = false
        var repeatDays = 0x7F
        var isSkipped = false

        val wakeAlarm = Reflect.callStatic(bedtimeUtil, "getWakeAlarm", context)
        if (wakeAlarm != null) {
            wakeHour = (Reflect.getField(wakeAlarm, "hour") as? Int) ?: wakeHour
            wakeMin = (Reflect.getField(wakeAlarm, "minutes") as? Int) ?: wakeMin
            wakeEnabled = (Reflect.getField(wakeAlarm, "enabled") as? Boolean) ?: false
            val skipTime = (Reflect.getField(wakeAlarm, "skipTime") as? Long) ?: 0L
            val nextTime = (Reflect.getField(wakeAlarm, "time") as? Long) ?: 0L
            isSkipped = skipTime > 0 && skipTime == nextTime

            val dow = Reflect.getField(wakeAlarm, "daysOfWeek")
            if (dow != null) {
                repeatDays = (Reflect.call(dow, "getCoded") as? Int)
                    ?: (dow as? Int) ?: repeatDays
            }
        }

        ScheduleInfo(sleepHour, sleepMin, wakeHour, wakeMin, wakeEnabled, repeatDays, configured, isSkipped)
    } catch (t: Throwable) {
        log("querySchedule error: ${t.message}")
        ScheduleInfo(22, 30, 7, 0, false, 0x7F, false, false)
    }

    /** Edit the persisted bedtime schedule.
     * In HyperOS DeskClock: sleep alarm is in SharedPreferences, wake alarm is in ContentProvider.
     */
    fun applySchedule(
        sleepHour: Int, sleepMin: Int,
        wakeHour: Int, wakeMin: Int,
        repeatDays: Int
    ): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // 1. Save sleep time to SharedPreferences (where Clock UI reads it from)
        // Create a temporary Alarm object just for storing hour/minutes
        val sleepAlarm: Any? = try {
            val alarmClass = Reflect.findClass("com.android.deskclock.Alarm", classLoader)
            val alarm = Reflect.newInstance(alarmClass)
            Reflect.setIntField(alarm, "hour", sleepHour)
            Reflect.setIntField(alarm, "minutes", sleepMin)
            alarm
        } catch (t: Throwable) {
            results += StepResult.fail("createSleepAlarm", t)
            null
        }

        if (sleepAlarm != null) {
            runStep(results, "saveSleepAlarm") {
                Reflect.callStatic(bedtimeUtil, "saveSleepAlarm", context, sleepAlarm)
            }
        }

        // 2. Update sleep schedule in Mi Health
        runStep(results, "updateSleepSchedule") {
            Reflect.callStatic(
                healthDataUtil, "updateSleepSchedule", context, sleepHour, sleepMin
            )
        }

        // 3. Fetch the wake alarm from ContentProvider (id = Integer.MIN_VALUE)
        val wakeAlarm: Any? = try {
            val a = Reflect.callStatic(bedtimeUtil, "getWakeAlarm", context)
            if (a == null) {
                results += StepResult.fail(
                    "getWakeAlarm", "returned null - create a bedtime in the Clock app first"
                )
            } else {
                results += StepResult.ok("getWakeAlarm")
            }
            a
        } catch (t: Throwable) {
            results += StepResult.fail("getWakeAlarm", t)
            null
        }

        // 4. Mutate and persist wake alarm
        if (wakeAlarm != null) {
            if (mutateAlarm(wakeAlarm, wakeHour, wakeMin, repeatDays, results)) {
                runStep(results, "setWakeAlarm") {
                    // AlarmHelper.setWakeAlarm(context, alarm) persists to sleep_alarms table
                    Reflect.callStatic(alarmHelper, "setWakeAlarm", context, wakeAlarm)
                }
            } else {
                results += StepResult.fail("setWakeAlarm", "skipped: alarm mutation failed")
            }
        }

        // 5. Update wake schedule in Mi Health
        runStep(results, "updateWakeSchedule") {
            Reflect.callStatic(
                healthDataUtil, "updateWakeSchedule", context, wakeHour, wakeMin
            )
        }

        // 6. Reschedule the bedtime reminder notification.
        runStep(results, "setSleepNotification") {
            Reflect.callStatic(alarmHelper, "setSleepNotification", context)
        }

        // 7. Mi Home IoT ecosystem.
        notifyMiHome(results)

        return results
    }

    /** Manually enter bedtime mode, mimicking the official DeskClock sequence. */
    fun startBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // Official sequence from BedtimeUtil.doInWakeTime + manual override for immediate DND

        runStep(results, "setSleepNotification") {
            Reflect.callStatic(alarmHelper, "setSleepNotification", context)
        }

        // Force DND on immediately, bypassing time checks
        // (AlarmHelper.setZenMode would exit if not in sleep window)
        runStep(results, "enterZenMode") {
            Reflect.callStatic(zenModeUtil, "enterZenMode", context)
        }

        // Sync with Android's AutomaticZenRule
        enableAndroidBedtimeMode(results)

        notifyMiHome(results)
        return results
    }

    /** Exit bedtime mode: official sequence from doInWakeTime when wake time arrives. */
    fun stopBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // Official sequence from BedtimeUtil.doInWakeTime (when wake time arrives):
        // 1. Exit powerkeeper sleep mode first
        // 2. setSleepNotification - reschedule for next day
        // 3. setZenMode - exits DND if not in sleep time window

        runStep(results, "exitSleepMode (powerkeeper broadcast)") {
            context.sendBroadcast(Intent(ACTION_REQUEST_WAKE).apply {
                setPackage(POWERKEEPER_PACKAGE)
                putExtra(EXTRA_REASON, REASON_DESK_CLOCK)
            })
        }

        runStep(results, "setSleepNotification") {
            Reflect.callStatic(alarmHelper, "setSleepNotification", context)
        }

        runStep(results, "setZenMode") {
            Reflect.callStatic(alarmHelper, "setZenMode", context)
        }

        // Sync with Android's AutomaticZenRule
        disableAndroidBedtimeMode(results)

        notifyMiHome(results)
        return results
    }

    /**
     * Force-exit the CURRENTLY ACTIVE bedtime (user ended it mid-period, e.g.
     * "skip once" while sleep mode is on). Unlike stopBedtime this must NOT call
     * AlarmHelper.setZenMode: that helper re-ENTERS zen when still inside the
     * sleep window. Here we exit zen unconditionally, wake powerkeeper, and
     * re-arm tomorrow's reminder (skip is once-only, the schedule survives).
     */
    fun exitActiveBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        runStep(results, "exitSleepMode (powerkeeper broadcast)") {
            context.sendBroadcast(Intent(ACTION_REQUEST_WAKE).apply {
                setPackage(POWERKEEPER_PACKAGE)
                putExtra(EXTRA_REASON, REASON_DESK_CLOCK)
            })
        }

        runStep(results, "exitZenMode") {
            Reflect.callStatic(zenModeUtil, "exitZenMode", context)
        }

        runStep(results, "setSleepNotification") {
            Reflect.callStatic(alarmHelper, "setSleepNotification", context)
        }

        // Sync with Android's AutomaticZenRule
        disableAndroidBedtimeMode(results)

        notifyMiHome(results)
        return results
    }

    /**
     * Enable Android's native bedtime mode by activating AutomaticZenRule.
     * This makes other apps (Google Pixel Bedtime, Samsung Modes) aware of bedtime state.
     */
    private fun enableAndroidBedtimeMode(results: MutableList<StepResult>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        runStep(results, "enableAndroidBedtimeMode") {
            val ruleId = findOrCreateBedtimeRule()
            if (ruleId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val condition = Condition(
                    Uri.parse("condition://$BEDTIME_RULE_ID"),
                    "",
                    Condition.STATE_TRUE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)
            }
        }
    }

    /**
     * Disable Android's native bedtime mode.
     */
    private fun disableAndroidBedtimeMode(results: MutableList<StepResult>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        runStep(results, "disableAndroidBedtimeMode") {
            val ruleId = findOrCreateBedtimeRule()
            if (ruleId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val condition = Condition(
                    Uri.parse("condition://$BEDTIME_RULE_ID"),
                    "",
                    Condition.STATE_FALSE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)
            }
        }
    }

    /**
     * Find existing HyperModes bedtime rule or create a new one.
     * Returns the rule ID.
     */
    private fun findOrCreateBedtimeRule(): String? {
        try {
            // Check if rule already exists
            val rules = notificationManager.automaticZenRules
            val existingRule = rules.entries.find { it.value.name == BEDTIME_RULE_NAME }
            if (existingRule != null) {
                return existingRule.key
            }

            // Create new rule
            val conditionId = Uri.parse("condition://$BEDTIME_RULE_ID")
            val rule = AutomaticZenRule.Builder(BEDTIME_RULE_NAME, conditionId)
                .setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
                .setEnabled(true)
                .build()

            return notificationManager.addAutomaticZenRule(rule)
        } catch (t: Throwable) {
            log("findOrCreateBedtimeRule error: ${t.message}")
            return null
        }
    }

    /** Show the sleep notification (normally shown 15 minutes before bedtime). */
    fun showSleepNotification(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // Find NotificationUtil class and show the sleep notification
        runStep(results, "showSleepNotification") {
            val notificationUtil = Reflect.findClass(
                "com.android.deskclock.util.NotificationUtil", classLoader
            )
            Reflect.callStatic(notificationUtil, "showSleepNotification", context)
        }

        return results
    }

    /**
     * Enable the wake alarm (toggle ON), per BedtimeManageActivity.onPreferenceChange:
     * the checkbox first writes key_wake_up_alert=true to the default prefs,
     * then AlarmHelper.enableAlarm(context, Integer.MIN_VALUE, true).
     * Writing the pref ourselves is what stops the official app from seeing a
     * checkbox transition (and popping the 关闭一次/永久 dialog) on next open.
     */
    fun enableWakeAlarm(): List<StepResult> {
        val results = mutableListOf<StepResult>()
        setWakeAlertPref(results, true)
        runStep(results, "enableAlarm(true)") {
            Reflect.callStatic(alarmHelper, "enableAlarm", context, Int.MIN_VALUE, true)
        }
        return results
    }

    /**
     * Disable the wake alarm permanently (toggle OFF -> "turn off always"),
     * per BedtimeManageActivity.showRepeatAlarmTurnOffDialog option 1:
     * key_wake_up_alert=false, then AlarmHelper.enableAlarm(context, MIN_VALUE,
     * false) + registerWakeAlarm(context).
     */
    fun disableWakeAlarm(): List<StepResult> {
        val results = mutableListOf<StepResult>()
        setWakeAlertPref(results, false)
        runStep(results, "enableAlarm(false)") {
            Reflect.callStatic(alarmHelper, "enableAlarm", context, Int.MIN_VALUE, false)
        }
        runStep(results, "registerWakeAlarm") {
            Reflect.callStatic(alarmHelper, "registerWakeAlarm", context)
        }
        return results
    }

    /** Keep AlarmCheckboxLayout's persisted checkbox state in sync with the
     * alarm DB so BedtimeManageActivity never sees a spurious transition. */
    private fun setWakeAlertPref(results: MutableList<StepResult>, enabled: Boolean) {
        runStep(results, "key_wake_up_alert=$enabled") {
            val prefs = Reflect.callStatic(fbeUtil, "getDefaultSharedPreferences", context)
                as android.content.SharedPreferences
            prefs.edit().putBoolean(KEY_WAKE_UP_ALERT, enabled).apply()
        }
    }

    /**
     * Skip the wake alarm for one occurrence only (toggle OFF -> "skip once"),
     * per BedtimeManageActivity.showRepeatAlarmTurnOffDialog option 0:
     * AlarmHelper.skipAlarmForOnce(context, MIN_VALUE) + registerWakeAlarm(context)
     */
    fun skipWakeAlarmOnce(): List<StepResult> {
        val results = mutableListOf<StepResult>()
        runStep(results, "skipAlarmForOnce") {
            Reflect.callStatic(alarmHelper, "skipAlarmForOnce", context, Int.MIN_VALUE)
        }
        runStep(results, "registerWakeAlarm") {
            Reflect.callStatic(alarmHelper, "registerWakeAlarm", context)
        }
        return results
    }

    /**
     * Set the 就寝提醒 lead time (minutes before sleep time; -1 = none),
     * per BedtimeSettingsFragment: BedtimeUtil.setNotificationAdvTime + reschedule.
     */
    fun setSleepReminder(minutes: Int): List<StepResult> {
        val results = mutableListOf<StepResult>()
        runStep(results, "setNotificationAdvTime($minutes)") {
            Reflect.callStatic(bedtimeUtil, "setNotificationAdvTime", context, minutes)
        }
        runStep(results, "setSleepNotification") {
            Reflect.callStatic(alarmHelper, "setSleepNotification", context)
        }
        return results
    }

    /** Current 就寝提醒 lead time in minutes (default 15, -1 = none). */
    fun querySleepReminder(): Int = try {
        Reflect.callStatic(bedtimeUtil, "getNotificationAdvTime", context) as Int
    } catch (t: Throwable) {
        log("querySleepReminder error: ${t.message}")
        15
    }

    /**
     * Fully disable the bedtime feature (used when the user deletes the
     * bedtime mode in HyperModes): master switch off, wake alarm off,
     * exit sleep mode. Every step is best-effort and individually reported.
     */
    fun disableBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()
        // Master bedtime switch (KEY_OPEN_BEDTIME) — confirmed name in
        // BedtimeUtil.java (decompiled HyperOS DeskClock).
        runStep(results, "setBedtimeOpenState(false)") {
            Reflect.callStatic(bedtimeUtil, "setBedtimeOpenState", context, false)
        }
        // Wake-alert checkbox pref must match the DB row (see KEY_WAKE_UP_ALERT).
        setWakeAlertPref(results, false)
        // Wake alarm off permanently (official 永久关闭 sequence).
        runStep(results, "enableAlarm(false)") {
            Reflect.callStatic(alarmHelper, "enableAlarm", context, Int.MIN_VALUE, false)
        }
        runStep(results, "registerWakeAlarm") {
            Reflect.callStatic(alarmHelper, "registerWakeAlarm", context)
        }
        // Exit powerkeeper sleep mode in case it is active right now.
        runStep(results, "exitSleepMode (powerkeeper broadcast)") {
            context.sendBroadcast(Intent(ACTION_REQUEST_WAKE).apply {
                setPackage(POWERKEEPER_PACKAGE)
                putExtra(EXTRA_REASON, REASON_DESK_CLOCK)
            })
        }
        // Reschedule (with bedtime closed this clears the reminder).
        runStep(results, "setSleepNotification") {
            Reflect.callStatic(alarmHelper, "setSleepNotification", context)
        }
        return results
    }

    private fun notifyMiHome(results: MutableList<StepResult>) {
        runStep(results, "MiHomeHelper.notifyBedtimeChanged") {
            val helper = Reflect.newInstance(miHomeHelper, context)
            Reflect.call(helper, "notifyBedtimeChanged")
        }
    }

    private fun runStep(results: MutableList<StepResult>, name: String, block: () -> Any?) {
        try {
            block()
            results += StepResult.ok(name)
        } catch (t: Throwable) {
            // Log full exception details for debugging
            val cause = t.cause
            val fullMessage = if (cause != null) {
                "${t.javaClass.simpleName}: ${t.message} caused by ${cause.javaClass.simpleName}: ${cause.message}"
            } else {
                "${t.javaClass.simpleName}: ${t.message}"
            }
            results += StepResult.fail(name, fullMessage)
            log("step $name failed: $fullMessage")
        }
    }

    /**
     * finding.md does not document Alarm's member names, so try AOSP DeskClock
     * names first (public int fields hour/minutes, daysOfWeek wrapper), then
     * setter methods. Every attempt is logged into the result for on-device
     * diagnosis without recompiling.
     */
    private fun mutateAlarm(
        alarm: Any, sleepHour: Int, sleepMin: Int, repeatDays: Int,
        results: MutableList<StepResult>
    ): Boolean {
        val tried = mutableListOf<String>()

        // Time: fields first, setters as fallback.
        try {
            Reflect.setIntField(alarm, "hour", sleepHour)
            Reflect.setIntField(alarm, "minutes", sleepMin)
            tried += "fields hour/minutes"
        } catch (t: Throwable) {
            try {
                Reflect.call(alarm, "setHour", sleepHour)
                Reflect.call(alarm, "setMinutes", sleepMin)
                tried += "setters setHour/setMinutes"
            } catch (t2: Throwable) {
                results += StepResult.fail("mutateAlarm", "time: ${t2.message}")
                return false
            }
        }

        // Days.
        try {
            setAlarmDays(alarm, repeatDays, tried)
        } catch (t: Throwable) {
            results += StepResult.fail("mutateAlarm", "days [${tried.joinToString()}]: ${t.message}")
            return false
        }

        results += StepResult.ok("mutateAlarm [${tried.joinToString()}]")
        return true
    }

    private fun setAlarmDays(alarm: Any, repeatDays: Int, tried: MutableList<String>) {
        // Variant 1: plain int field (Xiaomi may have flattened the AOSP wrapper).
        try {
            Reflect.setIntField(alarm, "daysOfWeek", repeatDays)
            tried += "daysOfWeek as int field"
            return
        } catch (t: Throwable) {
            tried += "int field failed"
        }

        // Variant 2: AOSP Alarm.DaysOfWeek(int) wrapper assigned to the field.
        try {
            val dowClass = Reflect.findClass(CLS_DAYS_OF_WEEK, alarm.javaClass.classLoader!!)
            val dow = Reflect.newInstance(dowClass, repeatDays)
            Reflect.setObjectField(alarm, "daysOfWeek", dow)
            tried += "DaysOfWeek wrapper field"
            return
        } catch (t: Throwable) {
            tried += "wrapper field failed"
        }

        // Variant 3: setter taking the wrapper.
        val dowClass = Reflect.findClass(CLS_DAYS_OF_WEEK, alarm.javaClass.classLoader!!)
        val dow = Reflect.newInstance(dowClass, repeatDays)
        Reflect.call(alarm, "setDaysOfWeek", dow)
        tried += "setDaysOfWeek(wrapper)"
    }
}
