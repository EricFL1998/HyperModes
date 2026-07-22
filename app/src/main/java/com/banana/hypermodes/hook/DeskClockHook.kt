package com.banana.hypermodes.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hooks Application.attach(Context) inside DeskClock — the same capture point
 * the reference module (HyperCeiler) uses: attach is final, always called,
 * and after chain.proceed() the Application's base context is ready.
 * Registers the command receiver, then delegates to BedtimeController.
 *
 * The receiver must be RECEIVER_EXPORTED (sender is our app, a different uid)
 * and is guarded by our signature-level permission so only our app can
 * trigger it.
 */
class DeskClockHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val attach = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        module.hook(attach)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    val app = chain.thisObject as Application
                    try {
                        registerReceiver(app, classLoader)
                    } catch (t: Throwable) {
                        log("receiver registration failed: $t")
                    }
                    return result
                }
            })
        hookBedtimeStateSignals(classLoader)
        hookWakeAlarmDismissal(classLoader)
    }

    /**
     * Official HyperOS behavior ends bedtime AT the scheduled wake time
     * (BedtimeUtil.doInWakeTime -> AlarmHelper.setZenMode -> exitZenMode),
     * before the user even touches the ringing alarm. We change the end
     * condition to the official alarm-dismiss gesture instead:
     *
     *  - doInWakeTime: when a wake alarm is enabled, run only the
     *    setSleepNotification half and skip setZenMode, so bedtime survives
     *    the wake time itself. (No wake alarm enabled -> proceed normally,
     *    otherwise bedtime would have no end trigger at all.)
     *  - AlarmHelper.dismissAlarm: the single funnel every dismiss path
     *    (alert UI button, notification action, auto-dismiss timeout) goes
     *    through. The wake alarm's id is Integer.MIN_VALUE (see
     *    BedtimeUtil.queryWakeAlarm / AlarmReceiver.registerWakeAlarm). When
     *    it is dismissed, call ZenModeUtil.exitZenMode — our
     *    hookBedtimeStateSignals then pushes bedtime-inactive to the app.
     *    Snoozing does NOT call dismissAlarm, so snooze keeps bedtime on.
     */
    private fun hookWakeAlarmDismissal(classLoader: ClassLoader) {
        // 1) Delay the scheduled wake-time exit until the alarm is dismissed.
        try {
            val bedtimeUtil = classLoader.loadClass(CLS_BEDTIME_UTIL)
            val doInWakeTime = bedtimeUtil.getDeclaredMethod("doInWakeTime", Context::class.java)
            module.hook(doInWakeTime)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val context = chain.getArg(0) as? Context ?: return chain.proceed()
                        if (!isWakeAlarmEnabled(context, classLoader)) return chain.proceed()
                        try {
                            classLoader.loadClass(CLS_ALARM_HELPER)
                                .getDeclaredMethod("setSleepNotification", Context::class.java)
                                .invoke(null, context)
                            log("wake time reached: bedtime kept on until alarm dismiss")
                        } catch (t: Throwable) {
                            log("setSleepNotification failed, running full doInWakeTime: $t")
                            return chain.proceed()
                        }
                        return null
                    }
                })
            log("BedtimeUtil.doInWakeTime hooked")
        } catch (t: Throwable) {
            log("doInWakeTime not found: ${t.message}")
        }

        // 2) End bedtime when the wake alarm is dismissed.
        try {
            val alarmHelper = classLoader.loadClass(CLS_ALARM_HELPER)
            val alarm = classLoader.loadClass(CLS_ALARM)
            val dismissAlarm = alarmHelper.getDeclaredMethod("dismissAlarm", Context::class.java, alarm)
            module.hook(dismissAlarm)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val dismissed = chain.getArg(1) ?: return result
                            val id = dismissed.javaClass.getField("id").getInt(dismissed)
                            if (id == Int.MIN_VALUE) {
                                val context = chain.getArg(0) as Context
                                classLoader.loadClass(CLS_ZEN_MODE_UTIL)
                                    .getDeclaredMethod("exitZenMode", Context::class.java)
                                    .invoke(null, context)
                                log("wake alarm dismissed -> exitZenMode")
                            }
                        } catch (t: Throwable) {
                            log("dismiss-driven bedtime exit failed: $t")
                        }
                        return result
                    }
                })
            log("AlarmHelper.dismissAlarm hooked")
        } catch (t: Throwable) {
            log("dismissAlarm not found: ${t.message}")
        }
    }

    /** Wake alarm lives in the sleep_alarms provider with id Integer.MIN_VALUE. */
    private fun isWakeAlarmEnabled(context: Context, classLoader: ClassLoader): Boolean =
        try {
            val wakeAlarm = classLoader.loadClass(CLS_BEDTIME_UTIL)
                .getDeclaredMethod("getWakeAlarm", Context::class.java)
                .invoke(null, context) ?: return false
            wakeAlarm.javaClass.getField("enabled").getBoolean(wakeAlarm)
        } catch (t: Throwable) {
            false
        }

    /**
     * Every official bedtime transition funnels through
     * ZenModeUtil.enterZenMode/exitZenMode:
     *  - scheduled sleep time  -> AlarmReceiver ACTION_ENTER_ZENMODE -> enterZenMode
     *  - scheduled wake time   -> BedtimeUtil.doInWakeTime -> setZenMode -> exitZenMode
     *  - manual toggle in the Clock app -> same pair
     * After each call we read the persisted inZenMode flag (ground truth on
     * SDK 30+) and push it to our app so its 睡眠模式 UI tracks the official
     * state even when the change didn't come from us.
     */
    private fun hookBedtimeStateSignals(classLoader: ClassLoader) {
        val zenModeUtil = try {
            classLoader.loadClass(CLS_ZEN_MODE_UTIL)
        } catch (t: Throwable) {
            log("ZenModeUtil not found: ${t.message}")
            return
        }
        for ((name, fallback) in listOf("enterZenMode" to true, "exitZenMode" to false)) {
            val method = try {
                zenModeUtil.getDeclaredMethod(name, Context::class.java)
            } catch (t: Throwable) {
                log("$name not found: ${t.message}")
                continue
            }
            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val context = chain.getArg(0) as? Context ?: return result
                            val active = readInZenMode(context, classLoader, fallback)
                            context.sendBroadcast(Intent(Protocol.ACTION_BEDTIME_ACTIVE).apply {
                                setPackage(Protocol.MODULE_PACKAGE)
                                putExtra(Protocol.EXTRA_IN_SLEEP_MODE, active)
                            })
                            log("$name -> bedtime active=$active")
                        } catch (t: Throwable) {
                            log("bedtime state broadcast failed: $t")
                        }
                        return result
                    }
                })
        }
        log("ZenModeUtil enter/exit hooked")
    }

    /** BedtimeAlarm/inZenMode is what enter/exitZenMode persist on SDK 30+;
     * fall back to which method ran on older paths or reflection failure. */
    private fun readInZenMode(context: Context, classLoader: ClassLoader, fallback: Boolean): Boolean =
        try {
            val fbe = classLoader.loadClass(CLS_FBE_UTIL)
            val prefs = fbe.getDeclaredMethod(
                "getSharedPreferences", Context::class.java,
                String::class.java, Int::class.javaPrimitiveType
            ).invoke(null, context, "BedtimeAlarm", 0) as android.content.SharedPreferences
            prefs.getBoolean(KEY_IN_ZENMODE, fallback)
        } catch (t: Throwable) {
            fallback
        }

    private fun registerReceiver(app: Application, classLoader: ClassLoader) {
        val controller = BedtimeController(app, classLoader) { msg -> log(msg) }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val results: List<StepResult> = when (intent.action) {
                    Protocol.ACTION_APPLY_SCHEDULE -> controller.applySchedule(
                        sleepHour = intent.getIntExtra(Protocol.EXTRA_SLEEP_HOUR, 22),
                        sleepMin = intent.getIntExtra(Protocol.EXTRA_SLEEP_MIN, 30),
                        wakeHour = intent.getIntExtra(Protocol.EXTRA_WAKE_HOUR, 7),
                        wakeMin = intent.getIntExtra(Protocol.EXTRA_WAKE_MIN, 30),
                        repeatDays = intent.getIntExtra(Protocol.EXTRA_REPEAT_DAYS, Protocol.EVERY_DAY)
                    )
                    Protocol.ACTION_START_BEDTIME -> controller.startBedtime()
                    Protocol.ACTION_STOP_BEDTIME -> controller.stopBedtime()
                    Protocol.ACTION_SHOW_SLEEP_NOTIFICATION -> controller.showSleepNotification()
                    Protocol.ACTION_ENABLE_WAKE_ALARM -> controller.enableWakeAlarm()
                    Protocol.ACTION_DISABLE_WAKE_ALARM -> controller.disableWakeAlarm()
                    Protocol.ACTION_SKIP_WAKE_ALARM_ONCE -> controller.skipWakeAlarmOnce()
                    Protocol.ACTION_SET_SLEEP_REMINDER -> controller.setSleepReminder(
                        intent.getIntExtra(Protocol.EXTRA_REMINDER_MINUTES, 15)
                    )
                    Protocol.ACTION_DISABLE_BEDTIME -> controller.disableBedtime()
                    Protocol.ACTION_QUERY_STATE -> emptyList()
                    Protocol.ACTION_QUERY_SCHEDULE -> emptyList()
                    android.app.NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED -> {
                        handleZenModeChange(context, controller)
                    }
                    else -> return
                }
                log("${intent.action} -> ${results.joinToString { it.format() }}")
                // Report DeskClock's own bedtime-active flag (inZenMode pref),
                // falling back to the powerkeeper sleep state — the two can
                // disagree right after a manual start (zen only), and the UI
                // must track the flag that enter/exitZenMode actually set.
                sendResult(
                    app, results,
                    readInZenMode(app, classLoader, controller.querySleepModeState()),
                    controller.querySchedule(), controller.querySleepReminder()
                )
            }
        }

        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_APPLY_SCHEDULE)
            addAction(Protocol.ACTION_START_BEDTIME)
            addAction(Protocol.ACTION_STOP_BEDTIME)
            addAction(Protocol.ACTION_SHOW_SLEEP_NOTIFICATION)
            addAction(Protocol.ACTION_ENABLE_WAKE_ALARM)
            addAction(Protocol.ACTION_DISABLE_WAKE_ALARM)
            addAction(Protocol.ACTION_SKIP_WAKE_ALARM_ONCE)
            addAction(Protocol.ACTION_SET_SLEEP_REMINDER)
            addAction(Protocol.ACTION_DISABLE_BEDTIME)
            addAction(Protocol.ACTION_QUERY_STATE)
            addAction(Protocol.ACTION_QUERY_SCHEDULE)
            addAction(android.app.NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        }
        app.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        log("command receiver registered in DeskClock")
    }

    /**
     * Handle Android ZenMode changes from other apps (Google Bedtime, Samsung Modes, etc.)
     * When Android bedtime is activated externally, trigger DeskClock bedtime.
     */
    private fun handleZenModeChange(context: Context, controller: BedtimeController): List<StepResult> {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE)
            as? android.app.NotificationManager ?: return emptyList()

        // Check if any AutomaticZenRule with "bedtime" in name is active
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            val rules = notificationManager.automaticZenRules ?: return emptyList()
            val bedtimeActive = rules.values.any { rule ->
                rule.isEnabled &&
                rule.name.contains("bedtime", ignoreCase = true) &&
                rule.conditionId != null
            }

            val currentlyInSleep = controller.querySleepModeState()

            // Sync state: if Android bedtime is on but DeskClock is off, start it
            if (bedtimeActive && !currentlyInSleep) {
                log("Android bedtime activated externally, starting DeskClock bedtime")
                return controller.startBedtime()
            }
            // If Android bedtime is off but DeskClock is on, stop it
            else if (!bedtimeActive && currentlyInSleep) {
                log("Android bedtime deactivated externally, stopping DeskClock bedtime")
                return controller.stopBedtime()
            }
        }

        return emptyList()
    }

    private fun sendResult(
        context: Context, results: List<StepResult>, inSleepMode: Boolean,
        schedule: ScheduleInfo, reminderMinutes: Int
    ) {
        context.sendBroadcast(Intent(Protocol.ACTION_RESULT).apply {
            setPackage(Protocol.MODULE_PACKAGE)
            putExtra(Protocol.EXTRA_STEPS, results.map { it.format() }.toTypedArray())
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, inSleepMode)
            putExtra(Protocol.EXTRA_SLEEP_HOUR, schedule.sleepHour)
            putExtra(Protocol.EXTRA_SLEEP_MIN, schedule.sleepMin)
            putExtra(Protocol.EXTRA_WAKE_HOUR, schedule.wakeHour)
            putExtra(Protocol.EXTRA_WAKE_MIN, schedule.wakeMin)
            putExtra(Protocol.EXTRA_WAKE_ENABLED, schedule.wakeEnabled)
            putExtra(Protocol.EXTRA_REPEAT_DAYS, schedule.repeatDays)
            putExtra(Protocol.EXTRA_BEDTIME_CONFIGURED, schedule.bedtimeConfigured)
            putExtra(Protocol.EXTRA_REMINDER_MINUTES, reminderMinutes)
        })
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val CLS_ZEN_MODE_UTIL = "com.android.deskclock.alarm.bedtime.ZenModeUtil"
        private const val CLS_BEDTIME_UTIL = "com.android.deskclock.alarm.bedtime.BedtimeUtil"
        private const val CLS_ALARM_HELPER = "com.android.deskclock.util.AlarmHelper"
        private const val CLS_ALARM = "com.android.deskclock.Alarm"
        private const val CLS_FBE_UTIL = "com.android.deskclock.util.FBEUtil"
        private const val KEY_IN_ZENMODE = "inZenMode"
    }
}
