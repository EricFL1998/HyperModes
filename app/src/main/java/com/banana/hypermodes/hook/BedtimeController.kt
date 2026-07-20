package com.banana.hypermodes.hook

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

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
    private val classLoader: ClassLoader
) {
    companion object {
        private const val TAG = "HyperModes"

        private const val CLS_BEDTIME_UTIL = "com.android.deskclock.alarm.bedtime.BedtimeUtil"
        private const val CLS_HEALTH_DATA_UTIL = "com.android.deskclock.alarm.bedtime.HealthDataUtil"
        private const val CLS_MI_HOME_HELPER = "com.android.deskclock.alarm.bedtime.MiHomeHelper"
        private const val CLS_ALARM_HELPER = "com.android.deskclock.util.AlarmHelper"
        private const val CLS_DAYS_OF_WEEK = "com.android.deskclock.Alarm\$DaysOfWeek"

        // SleepModeUtil.exitSleepMode equivalent — sent raw, no reflection.
        private const val POWERKEEPER_PACKAGE = "com.miui.powerkeeper"
        private const val ACTION_REQUEST_WAKE = "com.miui.powerkeeper_request_wake"
        private const val EXTRA_REASON = "reason"
        private const val REASON_DESK_CLOCK = 1

        // SleepModeUtil.inSleepMode equivalent — direct provider call.
        private const val POWERKEEPER_PROVIDER = "content://com.miui.powerkeeper.configure"
        private const val METHOD_GET_SLEEP_MODE_STATE = "getSleepModeState"
        private const val KEY_IS_IN_SLEEP = "isInSleep"
    }

    private val bedtimeUtil by lazy { XposedHelpers.findClass(CLS_BEDTIME_UTIL, classLoader) }
    private val healthDataUtil by lazy { XposedHelpers.findClass(CLS_HEALTH_DATA_UTIL, classLoader) }
    private val miHomeHelper by lazy { XposedHelpers.findClass(CLS_MI_HOME_HELPER, classLoader) }
    private val alarmHelper by lazy { XposedHelpers.findClass(CLS_ALARM_HELPER, classLoader) }

    /** Current powerkeeper sleep-mode state; false on any error. */
    fun querySleepModeState(): Boolean = try {
        val bundle = context.contentResolver.call(
            Uri.parse(POWERKEEPER_PROVIDER), METHOD_GET_SLEEP_MODE_STATE, null, null
        )
        bundle?.getBoolean(KEY_IS_IN_SLEEP, false) ?: false
    } catch (t: Throwable) {
        XposedBridge.log("$TAG: querySleepModeState error: ${t.message}")
        false
    }

    /** Edit the persisted bedtime schedule (official getSleepAlarm/saveSleepAlarm flow). */
    fun applySchedule(
        sleepHour: Int, sleepMin: Int,
        wakeHour: Int, wakeMin: Int,
        repeatDays: Int
    ): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // 1. Fetch the existing sleep alarm. Never construct a new one —
        //    that would lose its database id and custom flags.
        val alarm: Any? = try {
            val a = XposedHelpers.callStaticMethod(bedtimeUtil, "getSleepAlarm", context)
            if (a == null) {
                results += StepResult.fail(
                    "getSleepAlarm", "returned null - create a bedtime in the Clock app first"
                )
            } else {
                results += StepResult.ok("getSleepAlarm")
            }
            a
        } catch (t: Throwable) {
            results += StepResult.fail("getSleepAlarm", t)
            null
        }

        // 2+3. Mutate and persist. Skipped entirely if there is no alarm;
        //      save is skipped if mutation failed (never persist a
        //      half-mutated Alarm).
        if (alarm != null) {
            if (mutateAlarm(alarm, sleepHour, sleepMin, repeatDays, results)) {
                runStep(results, "saveSleepAlarm") {
                    XposedHelpers.callStaticMethod(bedtimeUtil, "saveSleepAlarm", context, alarm)
                }
            } else {
                results += StepResult.fail("saveSleepAlarm", "skipped: alarm mutation failed")
            }
        }

        // 4+5. Mi Health sync — independent of the Alarm object, always runs.
        runStep(results, "updateSleepSchedule") {
            XposedHelpers.callStaticMethod(
                healthDataUtil, "updateSleepSchedule", context, sleepHour, sleepMin
            )
        }
        runStep(results, "updateWakeSchedule") {
            XposedHelpers.callStaticMethod(
                healthDataUtil, "updateWakeSchedule", context, wakeHour, wakeMin
            )
        }

        // 6. Reschedule the bedtime reminder notification.
        runStep(results, "setSleepNotification") {
            XposedHelpers.callStaticMethod(alarmHelper, "setSleepNotification", context)
        }

        // 7. Mi Home IoT ecosystem.
        notifyMiHome(results)

        return results
    }

    /** Manually enter bedtime mode, mimicking the official DeskClock sequence. */
    fun startBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        runStep(results, "setSleepNotification") {
            XposedHelpers.callStaticMethod(alarmHelper, "setSleepNotification", context)
        }

        // Zen Mode only when the user enabled DND integration in Bedtime settings.
        val dndEnabled: Boolean? = try {
            XposedHelpers.callStaticMethod(bedtimeUtil, "getDisturbanceState", context) as Boolean
        } catch (t: Throwable) {
            results += StepResult.fail("getDisturbanceState", t)
            null
        }
        when (dndEnabled) {
            true -> runStep(results, "setZenMode") {
                XposedHelpers.callStaticMethod(alarmHelper, "setZenMode", context)
            }
            false -> results += StepResult.ok("setZenMode (skipped: DND integration off in Clock settings)")
            null -> results += StepResult.fail("setZenMode", "skipped: disturbance state unknown")
        }

        notifyMiHome(results)
        return results
    }

    /** Exit bedtime mode: official powerkeeper wake broadcast + best-effort Zen exit. */
    fun stopBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // 1. Exit powerkeeper sleep mode — the exact Intent
        //    SleepModeUtil.exitSleepMode sends (no reflection dependency).
        runStep(results, "exitSleepMode (powerkeeper broadcast)") {
            context.sendBroadcast(Intent(ACTION_REQUEST_WAKE).apply {
                setPackage(POWERKEEPER_PACKAGE)
                putExtra(EXTRA_REASON, REASON_DESK_CLOCK)
            })
        }

        // 2. Exit Zen Mode — best-effort, with standard DND fallback.
        try {
            XposedHelpers.callStaticMethod(alarmHelper, "exitZenMode", context)
            results += StepResult.ok("exitZenMode")
        } catch (t: Throwable) {
            try {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                results += StepResult.ok("exitZenMode (via NotificationManager fallback)")
            } catch (t2: Throwable) {
                results += StepResult.fail("exitZenMode", "${t.message}; fallback: ${t2.message}")
            }
        }

        // 3. Mi Home wake signal.
        notifyMiHome(results)
        return results
    }

    private fun notifyMiHome(results: MutableList<StepResult>) {
        runStep(results, "MiHomeHelper.notifyBedtimeChanged") {
            val helper = XposedHelpers.newInstance(miHomeHelper, context)
            XposedHelpers.callMethod(helper, "notifyBedtimeChanged")
        }
    }

    private fun runStep(results: MutableList<StepResult>, name: String, block: () -> Any?) {
        try {
            block()
            results += StepResult.ok(name)
        } catch (t: Throwable) {
            results += StepResult.fail(name, t)
            XposedBridge.log("$TAG: step $name failed: ${t.message}")
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
            XposedHelpers.setIntField(alarm, "hour", sleepHour)
            XposedHelpers.setIntField(alarm, "minutes", sleepMin)
            tried += "fields hour/minutes"
        } catch (t: Throwable) {
            try {
                XposedHelpers.callMethod(alarm, "setHour", sleepHour)
                XposedHelpers.callMethod(alarm, "setMinutes", sleepMin)
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
            XposedHelpers.setIntField(alarm, "daysOfWeek", repeatDays)
            tried += "daysOfWeek as int field"
            return
        } catch (t: Throwable) {
            tried += "int field failed"
        }

        // Variant 2: AOSP Alarm.DaysOfWeek(int) wrapper assigned to the field.
        try {
            val dowClass = XposedHelpers.findClass(CLS_DAYS_OF_WEEK, alarm.javaClass.classLoader)
            val dow = XposedHelpers.newInstance(dowClass, repeatDays)
            XposedHelpers.setObjectField(alarm, "daysOfWeek", dow)
            tried += "DaysOfWeek wrapper field"
            return
        } catch (t: Throwable) {
            tried += "wrapper field failed"
        }

        // Variant 3: setter taking the wrapper.
        val dowClass = XposedHelpers.findClass(CLS_DAYS_OF_WEEK, alarm.javaClass.classLoader)
        val dow = XposedHelpers.newInstance(dowClass, repeatDays)
        XposedHelpers.callMethod(alarm, "setDaysOfWeek", dow)
        tried += "setDaysOfWeek(wrapper)"
    }
}
