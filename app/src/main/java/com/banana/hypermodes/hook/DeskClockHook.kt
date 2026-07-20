package com.banana.hypermodes.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.banana.hypermodes.protocol.Protocol
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * Hooks DeskClock's Application.onCreate to register the command receiver
 * inside the DeskClock process, then delegates to BedtimeController.
 *
 * The receiver must be RECEIVER_EXPORTED (sender is our app, a different uid)
 * and is guarded by our signature-level permission so only our app can
 * trigger it.
 */
class DeskClockHook {

    fun install(lpparam: LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            Application::class.java.name, lpparam.classLoader, "onCreate",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val app = param.thisObject as Application
                    try {
                        registerReceiver(app, lpparam.classLoader)
                    } catch (t: Throwable) {
                        XposedBridge.log("HyperModes: receiver registration failed: $t")
                    }
                }
            }
        )
    }

    private fun registerReceiver(app: Application, classLoader: ClassLoader) {
        val controller = BedtimeController(app, classLoader)

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
                    Protocol.ACTION_QUERY_STATE -> emptyList()
                    else -> return
                }
                XposedBridge.log(
                    "HyperModes: ${intent.action} -> ${results.joinToString { it.format() }}"
                )
                sendResult(app, results, controller.querySleepModeState())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_APPLY_SCHEDULE)
            addAction(Protocol.ACTION_START_BEDTIME)
            addAction(Protocol.ACTION_STOP_BEDTIME)
            addAction(Protocol.ACTION_QUERY_STATE)
        }
        app.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        XposedBridge.log("HyperModes: command receiver registered in DeskClock")
    }

    private fun sendResult(context: Context, results: List<StepResult>, inSleepMode: Boolean) {
        context.sendBroadcast(Intent(Protocol.ACTION_RESULT).apply {
            setPackage(Protocol.MODULE_PACKAGE)
            putExtra(Protocol.EXTRA_STEPS, results.map { it.format() }.toTypedArray())
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, inSleepMode)
        })
    }
}
