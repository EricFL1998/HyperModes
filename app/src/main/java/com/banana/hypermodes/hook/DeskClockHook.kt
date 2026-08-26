package com.banana.hypermodes.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.protocol.PackageLifecyclePolicy
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hooks Application.attach(Context) inside DeskClock — the same capture point
 * the reference module (HyperCeiler) uses: attach is final, always called,
 * and after chain.proceed() the Application's base context is ready.
 * Registers the command receiver, then delegates to BedtimeController.
 *
 * The receiver must be RECEIVER_EXPORTED (senders are other uids: our app and
 * system_server's RoutineCoreEngine). It is intentionally NOT guarded by the
 * signature-level permission — system_server can never hold it, so the guard
 * silently blocked the engine's bedtime commands.
 */
class DeskClockHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val attach = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        module.hook(attach)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                    val app = getThisObjectMethod.invoke(chain) as Application
                    try {
                        registerReceiver(app, classLoader)
                        registerPackageLifecycleReceiver(app, classLoader)
                    } catch (t: Throwable) {
                        log("receiver registration failed: $t")
                    }
                    return result
                }
            })
        hookBedtimeStateSignals(classLoader)
        hookWakeAlarmDismissal(classLoader)
        hookAlarmSkip(classLoader)
        hookAlarmEnable(classLoader)
        hookAlarmRinging(classLoader)
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
                            log("dismissAlarm called with id=$id")
                            if (id == Int.MIN_VALUE) {
                                val context = chain.getArg(0) as Context
                                classLoader.loadClass(CLS_ZEN_MODE_UTIL)
                                    .getDeclaredMethod("exitZenMode", Context::class.java)
                                    .invoke(null, context)
                                log("wake alarm dismissed -> exitZenMode")
                                sendBedtimeState(context, false, "ALARM_DISMISSED")
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

        // 2b) OS4 alert UI dismiss no longer funnels through dismissAlarm.
        hookAlertUiDismiss(classLoader)

    }

    /**
     * OS4's AlarmAlertFullScreenActivity.dismiss() inlines the dismiss logic
     * (clearNotification/stopKlaxon/tryDeleteOneshot) and then nulls mAlarm,
     * so the alert-UI swipe path never reaches AlarmHelper.dismissAlarm.
     * Hook the dismiss overloads on the activity itself; the alarm must be
     * read BEFORE chain.proceed() because the original sets mAlarm=null.
     */
    private fun hookAlertUiDismiss(classLoader: ClassLoader) {
        try {
            val activityCls = classLoader.loadClass(CLS_ALERT_ACTIVITY)
            for (method in activityCls.declaredMethods.filter { it.name == "dismiss" }) {
                val params = method.parameterTypes
                if (params.size !in 2..3) continue
                if (!params.all { it == Boolean::class.javaPrimitiveType }) continue
                module.hook(method)
                    .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(object : XposedInterface.Hooker {
                        override fun intercept(chain: XposedInterface.Chain): Any? {
                            try {
                                val self = HookUtils.getThisObject(chain) ?: return chain.proceed()
                                val alarm = self.javaClass.getField("mAlarm").get(self)
                                val killed = chain.getArg(0) as? Boolean ?: false
                                if (alarm != null && !killed) {
                                    val id = alarm.javaClass.getField("id").getInt(alarm)
                                    if (id == Int.MIN_VALUE) {
                                        val context = self as Context
                                        classLoader.loadClass(CLS_ZEN_MODE_UTIL)
                                            .getDeclaredMethod("exitZenMode", Context::class.java)
                                            .invoke(null, context)
                                        log("wake alarm dismissed via alert UI -> exitZenMode")
                                        sendBedtimeState(context, false, "ALARM_DISMISSED")
                                    }
                                }
                            } catch (t: Throwable) {
                                log("alert-UI dismiss hook failed: " + t.message)
                            }
                            return chain.proceed()
                        }
                    })
                log("AlarmAlertFullScreenActivity.dismiss(" + params.size + " args) hooked")
            }
        } catch (t: Throwable) {
            log("alert activity dismiss hook not installed: " + t.message)
        }
    }

    /** Wake alarm lives in the sleep_alarms provider with id Integer.MIN_VALUE. */
    private fun isWakeAlarmEnabled(context: Context, classLoader: ClassLoader): Boolean =
        try {
            val wakeAlarm = classLoader.loadClass(CLS_BEDTIME_UTIL)
                .getDeclaredMethod("getWakeAlarm", Context::class.java)
                .invoke(null, context) ?: return false
            wakeAlarm.javaClass.getField("enabled").getBoolean(wakeAlarm)
        } catch (_: Throwable) {
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
                            sendBedtimeState(context, active, if (active) "ZEN_ENTERED" else "ZEN_EXITED")
                        } catch (t: Throwable) {
                            log("bedtime state broadcast failed: $t")
                        }
                        return result
                    }
                })
        }
        log("ZenModeUtil enter/exit hooked")

        // 自动睡眠进入走 AlarmHelper.setZenMode（time-aware），而非 ZenModeUtil.enterZenMode；
        // 不 hook 这里，到睡眠时间 DeskClock 自动进入勿扰时 HyperModes 收不到 ON 信号。
        hookAlarmHelperSetZenMode(classLoader)

    }

    /**
     * AlarmHelper.setZenMode is the time-aware entry used by the scheduled sleep
     * start/stop sequence. After it runs, read MIUI's actual 勿扰 (silence_mode)
     * state and push the result so bedtime mode follows the scheduled transition.
     */
    private fun hookAlarmHelperSetZenMode(classLoader: ClassLoader) {
        val alarmHelper = try {
            classLoader.loadClass(CLS_ALARM_HELPER)
        } catch (t: Throwable) {
            log("AlarmHelper not found for setZenMode: ${t.message}")
            return
        }
        val setZenMode = try {
            alarmHelper.getDeclaredMethod("setZenMode", Context::class.java)
        } catch (t: Throwable) {
            log("setZenMode not found: ${t.message}")
            return
        }
        module.hook(setZenMode)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val context = chain.getArg(0) as? Context ?: return result
                        val active = readMiuiZenMode(context)
                        sendBedtimeState(context, active, if (active) "ZEN_ENTERED" else "ZEN_EXITED")
                    } catch (t: Throwable) {
                        log("setZenMode broadcast failed: $t")
                    }
                    return result
                }
            })
        log("AlarmHelper.setZenMode hooked")
    }

    /** MIUI 勿扰状态：silence_mode == 4 表示勿扰开启（与 DeviceController 一致）。 */
    private fun readMiuiZenMode(context: Context): Boolean =
        try {
            Settings.System.getInt(context.contentResolver, "silence_mode", 0) == 4
        } catch (_: Throwable) {
            false
        }

    private fun hookAlarmSkip(classLoader: ClassLoader) {
        val alarmHelper = try {
            classLoader.loadClass(CLS_ALARM_HELPER)
        } catch (t: Throwable) {
            log("AlarmHelper not found: ${t.message}")
            return
        }

        val skipMethod = try {
            alarmHelper.getDeclaredMethod("skipAlarmForOnce", Context::class.java, Integer.TYPE)
        } catch (t: Throwable) {
            log("skipAlarmForOnce not found: ${t.message}")
            return
        }

        module.hook(skipMethod)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val context = chain.getArg(0) as? Context ?: return result
                        val alarmId = chain.getArg(1) as? Int ?: 0
                        if (alarmId == Int.MIN_VALUE) {
                            log("Bedtime alarm skipped manually in DeskClock")
                            if (readInZenMode(context, classLoader, false)) {
                                // Skip during an ACTIVE sleep period: report the skip
                                // FIRST so the engine records a manual dismiss (guards
                                // against a stale ON re-activating tonight). The
                                // ZEN_EXITED emitted by exitActiveBedtime below then
                                // arrives second and is a no-op.
                                sendBedtimeState(context, false, "SKIP_ONCE_ACTIVE")
                                val controller = BedtimeController(context, classLoader) { msg -> log(msg) }
                                val steps = controller.exitActiveBedtime()
                                log("skip during active bedtime -> exitActiveBedtime: ${steps.joinToString { it.format() }}")
                            } else {
                                // Idle pre-skip: DeskClock skips the next bedtime itself.
                                sendBedtimeState(context, false, "SKIP_ONCE_IDLE")
                            }
                        }
                    } catch (t: Throwable) {
                        log("skip hook broadcast failed: $t")
                    }
                    return result
                }
            })
        log("skipAlarmForOnce hooked")

    }

    private fun hookAlarmEnable(classLoader: ClassLoader) {
        val alarmHelper = try {
            classLoader.loadClass(CLS_ALARM_HELPER)
        } catch (t: Throwable) {
            log("AlarmHelper not found: ${t.message}")
            return
        }

        val enableMethod = try {
            alarmHelper.getDeclaredMethod("enableAlarm", Context::class.java, Integer.TYPE, java.lang.Boolean.TYPE)
        } catch (t: Throwable) {
            log("enableAlarm not found: ${t.message}")
            return
        }

        module.hook(enableMethod)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val context = chain.getArg(0) as? Context ?: return result
                        val alarmId = chain.getArg(1) as? Int ?: 0
                        val enabled = chain.getArg(2) as? Boolean ?: false
                        if (alarmId == Int.MIN_VALUE && !enabled) {
                            log("Bedtime wake alarm disabled permanently via AlarmHelper.enableAlarm")
                            
                            // OS4 routes permanent alarm disable through this helper.
                            classLoader.loadClass(CLS_ZEN_MODE_UTIL)
                                .getDeclaredMethod("exitZenMode", Context::class.java)
                                .invoke(null, context)
                            log("wake alarm disabled -> exitZenMode")
                            
                            // Then send the bedtime state signal
                            sendBedtimeState(context, false, "ALARM_DISABLED")
                        }
                    } catch (t: Throwable) {
                        log("enableAlarm hook failed: $t")
                    }
                    return result
                }
            })
        log("enableAlarm hooked")
    }

    /**
     * OS4 AlarmService only accepts AlarmHelper.ALARM_ALERT_ACTION and the timer
     * alert action in onStartCommand. The actual dismiss flow is handled by the
     * OS4 AlarmHelper.dismissAlarm(Context, Alarm) hook above.
     */
    private fun hookAlarmRinging(classLoader: ClassLoader) {
        val alarmService = try {
            classLoader.loadClass("com.android.deskclock.alarm.alert.AlarmService")
        } catch (t: Throwable) {
            log("AlarmService not found: ${t.message}")
            return
        }
        val onStartCommand = try {
            alarmService.getDeclaredMethod(
                "onStartCommand",
                Intent::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
        } catch (t: Throwable) {
            log("AlarmService.onStartCommand not found: ${t.message}")
            return
        }
        module.hook(onStartCommand)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val intent = chain.getArg(0) as? Intent
                        val action = intent?.action ?: ""
                        if (action == ACTION_DESKCLOCK_ALARM_ALERT) {
                            val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                            val service = getThisObjectMethod.invoke(chain) as? Context
                            service?.sendBroadcast(Intent(Protocol.ACTION_ALARM_RINGING))
                            log("alarm ringing detected: $action")
                        }
                    } catch (t: Throwable) {
                        log("alarm ringing hook failed: $t")
                    }
                    return result
                }
            })
        log("AlarmService.onStartCommand hooked")
    }

    /** OS4 ZenModeUtil persists its state in BedtimeAlarm/inZenMode. */
    private fun readInZenMode(context: Context, classLoader: ClassLoader, fallback: Boolean): Boolean =
        try {
            val fbe = classLoader.loadClass(CLS_FBE_UTIL)
            val prefs = fbe.getDeclaredMethod(
                "getSharedPreferences", Context::class.java,
                String::class.java, Int::class.javaPrimitiveType
            ).invoke(null, context, "BedtimeAlarm", 0) as android.content.SharedPreferences
            prefs.getBoolean(KEY_IN_ZENMODE, fallback)
        } catch (_: Throwable) {
            fallback
        }

    /** Single funnel for all bedtime-state pushes to system_server. */
    private fun sendBedtimeState(context: Context, active: Boolean, reason: String) {
        context.sendBroadcast(Intent(Protocol.ACTION_BEDTIME_ACTIVE).apply {
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, active)
            putExtra(Protocol.EXTRA_BEDTIME_REASON, reason)
            putExtra(Protocol.EXTRA_EVENT_TIME, System.currentTimeMillis())
        })
        log("bedtime state -> active=$active reason=$reason")
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
                        wakeMin = intent.getIntExtra(Protocol.EXTRA_WAKE_MIN, 0),
                        repeatDays = intent.getIntExtra(Protocol.EXTRA_REPEAT_DAYS, Protocol.EVERY_DAY)
                    )
                    Protocol.ACTION_START_BEDTIME -> controller.startBedtime()
                    Protocol.ACTION_STOP_BEDTIME -> controller.stopBedtime()
                    Protocol.ACTION_SHOW_SLEEP_NOTIFICATION -> controller.showSleepNotification()
                    Protocol.ACTION_ENABLE_WAKE_ALARM -> controller.enableWakeAlarm()
                    Protocol.ACTION_DISABLE_WAKE_ALARM -> controller.disableWakeAlarm()
                    Protocol.ACTION_SKIP_WAKE_ALARM_ONCE -> controller.skipWakeAlarmOnce()
                    Protocol.ACTION_EXIT_BEDTIME -> controller.exitActiveBedtime()
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
            addAction(Protocol.ACTION_EXIT_BEDTIME)
            addAction(Protocol.ACTION_SET_SLEEP_REMINDER)
            addAction(Protocol.ACTION_DISABLE_BEDTIME)
            addAction(Protocol.ACTION_QUERY_STATE)
            addAction(Protocol.ACTION_QUERY_SCHEDULE)
            addAction(android.app.NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED)
        }
        app.registerReceiver(
            receiver, filter,
            // No broadcastPermission: PERMISSION_CONTROL is signature-level and can
            // only be held by our own app — system_server (RoutineCoreEngine) could
            // never pass that check, which silently killed every engine->DeskClock
            // command. Exported is required since senders are other uids.
            null, null,
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

        // Check if any EXTERNAL AutomaticZenRule with "bedtime" in name is FIRING.
        // Two guards against self-triggering (this runs on every interruption-
        // filter change, including our own DND apply/revert):
        //  1. Exclude our own "HyperModes Bedtime" rule — it is created
        //     setEnabled(true) permanently, so an enabled-check alone always
        //     matched it and restarted bedtime right after every manual turn-off.
        //  2. Require the rule's condition to actually be STATE_TRUE (firing),
        //     not merely enabled.
        val rules = notificationManager.automaticZenRules ?: return emptyList()
        // 自触发 guard（关键）：若自己的 "HyperModes Bedtime" 规则正在 firing，
        // 说明本次过滤器变化来自我们自己 start/stop bedtime（enable/disableAndroidBedtimeMode
        // 会改变中断过滤器并触发本回调）。此时绝不能据此再 start/stop，否则 start 会
        // 刚启动就被 stopBedtime 打断、stop 会重复触发。
        val ownRuleFiring = rules.values.any { rule ->
            rule.name == BedtimeController.BEDTIME_RULE_NAME && isRuleFiring(rule)
        }
        if (ownRuleFiring) return emptyList()

        val bedtimeActive = rules.values.any { rule ->
            rule.isEnabled &&
                    rule.name.contains("bedtime", ignoreCase = true) &&
                    !rule.name.contains("hypermodes", ignoreCase = true) &&
                    isRuleFiring(rule)
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

        return emptyList()
    }

    /**
     * True if the rule's condition is currently STATE_TRUE (i.e. the rule is
     * firing, not just enabled). AutomaticZenRule.getCondition() is @SystemApi —
     * absent from the compile-time android.jar, but callable via reflection
     * inside DeskClock, which as a system app passes hidden-API enforcement.
     * Falls back to "has a conditionId" so external rules still register if the
     * lookup ever fails.
     */
    private fun isRuleFiring(rule: android.app.AutomaticZenRule): Boolean = try {
        val condition = rule.javaClass.getMethod("getCondition").invoke(rule)
            as? android.service.notification.Condition
        condition?.state == android.service.notification.Condition.STATE_TRUE
    } catch (t: Throwable) {
        rule.conditionId != null
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
            putExtra(Protocol.EXTRA_IS_SKIPPED, schedule.isSkipped)
        })
    }

    private fun registerPackageLifecycleReceiver(app: Application, classLoader: ClassLoader) {
        val controller = BedtimeController(app, classLoader) { msg -> log(msg) }
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = PackageLifecyclePolicy.classify(
                    intent, Protocol.MODULE_PACKAGE
                )
                
                if (action == PackageLifecyclePolicy.Action.REMOVE) {
                    log("HyperModes removal detected in DeskClock, disabling bedtime")
                    val results = controller.disableBedtime()
                    log("disableBedtime results: ${results.joinToString { it.format() }}")
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addDataScheme("package")
        }
        
        app.registerReceiver(receiver, filter)
        log("Package lifecycle receiver registered in DeskClock")
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val CLS_ZEN_MODE_UTIL = "com.android.deskclock.alarm.bedtime.ZenModeUtil"
        private const val CLS_BEDTIME_UTIL = "com.android.deskclock.alarm.bedtime.BedtimeUtil"
        private const val CLS_ALARM_HELPER = "com.android.deskclock.util.AlarmHelper"
        private const val CLS_ALARM = "com.android.deskclock.Alarm"
        private const val CLS_FBE_UTIL = "com.android.deskclock.util.FBEUtil"
        private const val KEY_IN_ZENMODE = "inZenMode"
        private const val ACTION_DESKCLOCK_ALARM_ALERT = "com.android.deskclock.ALARM_ALERT"
        private const val CLS_ALERT_ACTIVITY = "com.android.deskclock.alarm.alert.AlarmAlertFullScreenActivity"
    }
}
