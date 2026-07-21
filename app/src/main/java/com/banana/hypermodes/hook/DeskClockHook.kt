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
                    Protocol.ACTION_QUERY_STATE -> emptyList()
                    android.app.NotificationManager.ACTION_INTERRUPTION_FILTER_CHANGED -> {
                        handleZenModeChange(context, controller)
                    }
                    else -> return
                }
                log("${intent.action} -> ${results.joinToString { it.format() }}")
                sendResult(app, results, controller.querySleepModeState())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_APPLY_SCHEDULE)
            addAction(Protocol.ACTION_START_BEDTIME)
            addAction(Protocol.ACTION_STOP_BEDTIME)
            addAction(Protocol.ACTION_SHOW_SLEEP_NOTIFICATION)
            addAction(Protocol.ACTION_QUERY_STATE)
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

    private fun sendResult(context: Context, results: List<StepResult>, inSleepMode: Boolean) {
        context.sendBroadcast(Intent(Protocol.ACTION_RESULT).apply {
            setPackage(Protocol.MODULE_PACKAGE)
            putExtra(Protocol.EXTRA_STEPS, results.map { it.format() }.toTypedArray())
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, inSleepMode)
        })
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
    }
}
