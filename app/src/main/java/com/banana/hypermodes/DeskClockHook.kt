package com.banana.hypermodes

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * DeskClockHook - LSPosed module for manual bedtime triggering
 *
 * This hook injects into com.android.deskclock to provide native manual
 * activation of the entire HyperOS Bedtime ecosystem (Mi Home + Mi Health).
 *
 * Based on HyperOS Bedtime Timer & Manual Trigger Implementation V2
 */
class DeskClockHook : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "DeskClockHook"
        private const val TARGET_PACKAGE = "com.android.deskclock"
        private const val MANUAL_BEDTIME_ACTION = "com.banana.hypermodes.MANUAL_BEDTIME"
        private const val UPDATE_BEDTIME_ACTION = "com.banana.hypermodes.UPDATE_BEDTIME"
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName != TARGET_PACKAGE) return

        XposedBridge.log("$TAG: ========================================")
        XposedBridge.log("$TAG: DESKCLOCK HOOK INITIALIZING")
        XposedBridge.log("$TAG: ========================================")

        try {
            // Hook into Application.onCreate to register our receiver
            XposedHelpers.findAndHookMethod(
                "android.app.Application",
                lpparam.classLoader,
                "onCreate",
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam) {
                        val context = param.thisObject as Context
                        XposedBridge.log("$TAG: DeskClock Application.onCreate called")

                        try {
                            // Register the receiver inside the DeskClock process
                            val filter = IntentFilter().apply {
                                addAction(MANUAL_BEDTIME_ACTION)
                                addAction(UPDATE_BEDTIME_ACTION)
                            }

                            // Android 16 compliance - RECEIVER_EXPORTED for system-level communication
                            val receiver = object : BroadcastReceiver() {
                                override fun onReceive(context: Context, intent: Intent) {
                                    when (intent.action) {
                                        MANUAL_BEDTIME_ACTION -> {
                                            XposedBridge.log("$TAG: Received manual bedtime broadcast")
                                            val isStarting = intent.getBooleanExtra("START_BEDTIME", true)
                                            invokeProprietaryApis(lpparam.classLoader, context, isStarting)
                                        }
                                        UPDATE_BEDTIME_ACTION -> {
                                            XposedBridge.log("$TAG: Received update bedtime schedule broadcast")
                                            val sleepHour = intent.getIntExtra("sleepHour", 22)
                                            val sleepMin = intent.getIntExtra("sleepMin", 30)
                                            val wakeHour = intent.getIntExtra("wakeHour", 7)
                                            val wakeMin = intent.getIntExtra("wakeMin", 30)
                                            val repeat = intent.getIntExtra("repeat", 127)
                                            updateBedtimeSchedule(context, sleepHour, sleepMin, wakeHour, wakeMin, repeat)
                                        }
                                    }
                                }
                            }

                            context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
                            XposedBridge.log("$TAG: Manual bedtime receiver registered successfully")

                        } catch (e: Throwable) {
                            XposedBridge.log("$TAG: Error registering receiver: ${e.message}")
                            e.printStackTrace()
                        }
                    }
                }
            )

        } catch (e: Throwable) {
            XposedBridge.log("$TAG: Error in handleLoadPackage: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Invoke proprietary Xiaomi APIs via reflection
     *
     * This method calls internal classes discovered via Logcat analysis:
     * - MiHomeHelper: Syncs with Mi Home IoT ecosystem
     * - AlarmHelper: Controls Zen Mode enforcement
     * - BedtimeUtil: Syncs with Mi Health sleep tracking
     */
    private fun invokeProprietaryApis(classLoader: ClassLoader, context: Context, isStarting: Boolean) {
        try {
            XposedBridge.log("$TAG: Invoking proprietary APIs - Starting: $isStarting")

            if (isStarting) {
                // === START BEDTIME MODE ===

                // 1. Notify Mi Home ecosystem
                try {
                    val miHomeHelperClass = XposedHelpers.findClass(
                        "com.android.deskclock.alarm.bedtime.MiHomeHelper",
                        classLoader
                    )
                    XposedHelpers.callStaticMethod(miHomeHelperClass, "notifySleepChange")
                    XposedBridge.log("$TAG: Mi Home notified of sleep change")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: MiHomeHelper not found or call failed: ${e.message}")
                    // Try instance method as fallback
                    tryInstanceMethod(classLoader, "com.android.deskclock.alarm.bedtime.MiHomeHelper", "notifySleepChange")
                }

                // 2. Set Zen Mode
                try {
                    val alarmHelperClass = XposedHelpers.findClass(
                        "com.android.deskclock.alarm.bedtime.AlarmHelper",
                        classLoader
                    )
                    XposedHelpers.callStaticMethod(alarmHelperClass, "setZenMode")
                    XposedBridge.log("$TAG: Zen Mode activated")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: AlarmHelper.setZenMode failed: ${e.message}")
                    tryInstanceMethod(classLoader, "com.android.deskclock.alarm.bedtime.AlarmHelper", "setZenMode")
                }

                // 3. Sync with Mi Health
                try {
                    val bedtimeUtilClass = XposedHelpers.findClass(
                        "com.android.deskclock.alarm.bedtime.BedtimeUtil",
                        classLoader
                    )
                    XposedHelpers.callStaticMethod(bedtimeUtilClass, "queryWakeAlarm")
                    XposedBridge.log("$TAG: Mi Health sync initiated")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: BedtimeUtil.queryWakeAlarm failed: ${e.message}")
                    tryInstanceMethod(classLoader, "com.android.deskclock.alarm.bedtime.BedtimeUtil", "queryWakeAlarm")
                }

                // 4. Broadcast to system
                try {
                    val intent = Intent("com.android.deskclock.ENTER_ZENMODE")
                    context.sendBroadcast(intent)
                    XposedBridge.log("$TAG: ENTER_ZENMODE broadcast sent")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: Failed to send ENTER_ZENMODE broadcast: ${e.message}")
                }

            } else {
                // === STOP BEDTIME MODE ===

                // 1. Exit Zen Mode
                try {
                    val alarmHelperClass = XposedHelpers.findClass(
                        "com.android.deskclock.alarm.bedtime.AlarmHelper",
                        classLoader
                    )
                    XposedHelpers.callStaticMethod(alarmHelperClass, "exitZenMode")
                    XposedBridge.log("$TAG: Zen Mode deactivated")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: AlarmHelper.exitZenMode failed: ${e.message}")
                    tryInstanceMethod(classLoader, "com.android.deskclock.alarm.bedtime.AlarmHelper", "exitZenMode")
                }

                // 2. Notify Mi Home of wake
                try {
                    val miHomeHelperClass = XposedHelpers.findClass(
                        "com.android.deskclock.alarm.bedtime.MiHomeHelper",
                        classLoader
                    )
                    // Try calling notifySleepChange again (it should handle wake state)
                    XposedHelpers.callStaticMethod(miHomeHelperClass, "notifySleepChange")
                    XposedBridge.log("$TAG: Mi Home notified of wake")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: MiHomeHelper wake notification failed: ${e.message}")
                }

                // 3. Broadcast to system
                try {
                    val intent = Intent("com.android.deskclock.EXIT_ZENMODE")
                    context.sendBroadcast(intent)
                    XposedBridge.log("$TAG: EXIT_ZENMODE broadcast sent")
                } catch (e: Throwable) {
                    XposedBridge.log("$TAG: Failed to send EXIT_ZENMODE broadcast: ${e.message}")
                }
            }

            XposedBridge.log("$TAG: Proprietary API invocation completed")

        } catch (e: Throwable) {
            XposedBridge.log("$TAG: Error invoking proprietary APIs: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Try to call method on singleton instance if static call fails
     * Many Xiaomi classes use getInstance() singleton pattern
     */
    private fun tryInstanceMethod(classLoader: ClassLoader, className: String, methodName: String) {
        try {
            val clazz = XposedHelpers.findClass(className, classLoader)
            val instance = XposedHelpers.callStaticMethod(clazz, "getInstance")
            XposedHelpers.callMethod(instance, methodName)
            XposedBridge.log("$TAG: Successfully called $className.$methodName via instance")
        } catch (e: Throwable) {
            XposedBridge.log("$TAG: Instance method call also failed for $className.$methodName: ${e.message}")
        }
    }

    /**
     * Update bedtime schedule using DeskClock's HealthDataUtil methods
     * This is the CORRECT way as discovered from decompiled DeskClock code
     */
    private fun updateBedtimeSchedule(
        context: Context,
        sleepHour: Int,
        sleepMin: Int,
        wakeHour: Int,
        wakeMin: Int,
        repeat: Int
    ) {
        XposedBridge.log("$TAG: Updating bedtime schedule: Sleep $sleepHour:$sleepMin, Wake $wakeHour:$wakeMin, Repeat $repeat")

        try {
            // Get the classLoader from context
            val classLoader = context.classLoader

            // Find HealthDataUtil class
            val healthDataUtilClass = XposedHelpers.findClass(
                "com.android.deskclock.alarm.bedtime.HealthDataUtil",
                classLoader
            )

            // Call updateSleepSchedule(Context, int hour, int min)
            XposedBridge.log("$TAG: Calling HealthDataUtil.updateSleepSchedule($sleepHour, $sleepMin)")
            val sleepResult = XposedHelpers.callStaticMethod(
                healthDataUtilClass,
                "updateSleepSchedule",
                context,
                sleepHour,
                sleepMin
            ) as Int
            XposedBridge.log("$TAG: updateSleepSchedule returned: $sleepResult")

            // Call updateWakeSchedule(Context, int hour, int min)
            XposedBridge.log("$TAG: Calling HealthDataUtil.updateWakeSchedule($wakeHour, $wakeMin)")
            val wakeResult = XposedHelpers.callStaticMethod(
                healthDataUtilClass,
                "updateWakeSchedule",
                context,
                wakeHour,
                wakeMin
            ) as Int
            XposedBridge.log("$TAG: updateWakeSchedule returned: $wakeResult")

            if (sleepResult > 0 && wakeResult > 0) {
                XposedBridge.log("$TAG: ✅ Bedtime schedule updated successfully!")
            } else {
                XposedBridge.log("$TAG: ⚠️ Update returned 0 rows - schedule may not exist yet")
            }

        } catch (e: Throwable) {
            XposedBridge.log("$TAG: ❌ Error updating bedtime schedule: ${e.message}")
            e.printStackTrace()
        }
    }
}
