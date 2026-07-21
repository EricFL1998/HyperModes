package com.banana.hypermodes.hook

import android.app.AutomaticZenRule
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.service.notification.ZenModeConfig

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
     * Enable Android's native bedtime mode by activating AutomaticZenRule.
     * This makes other apps (Google Pixel Bedtime, Samsung Modes) aware of bedtime state.
     */
    private fun enableAndroidBedtimeMode(results: MutableList<StepResult>) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return

        runStep(results, "enableAndroidBedtimeMode") {
            val ruleId = findOrCreateBedtimeRule()
            if (ruleId != null) {
                notificationManager.setAutomaticZenRuleState(ruleId, android.service.notification.Condition.STATE_TRUE)
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
            if (ruleId != null) {
                notificationManager.setAutomaticZenRuleState(ruleId, android.service.notification.Condition.STATE_FALSE)
            }
        }
    }

    /**
     * Find existing HyperModes bedtime rule or create a new one.
     * Returns the rule ID.
     */
    private fun findOrCreateBedtimeRule(): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) return null

        try {
            // Check if rule already exists
            val rules = notificationManager.automaticZenRules
            val existingRule = rules.entries.find { it.value.name == BEDTIME_RULE_NAME }
            if (existingRule != null) {
                return existingRule.key
            }

            // Create new rule
            val conditionId = Uri.parse("condition://$BEDTIME_RULE_ID")
            val rule = AutomaticZenRule(
                BEDTIME_RULE_NAME,
                null, // No owner (we control it manually)
                conditionId,
                NotificationManager.INTERRUPTION_FILTER_ALARMS, // Allow only alarms
                true // Enabled
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                rule.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            }

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
