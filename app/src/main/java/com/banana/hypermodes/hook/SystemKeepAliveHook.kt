package com.banana.hypermodes.hook

import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Keep-alive for the HyperModes app, running INSIDE system_server.
 *
 * System apps survive because the framework never force-stops or
 * background-kills them. We give HyperModes the same treatment by
 * intercepting the two ActivityManagerService entry points used by
 * Settings' "强制停止", MIUI Security's cleaner, and `am kill`:
 *
 * - forceStopPackage(String, int)   — force stop (Settings app info page)
 * - killBackgroundProcesses(String, int) — background kill (cleaner apps)
 * - killTaskProcessesIfPossible(Task) — process kill after swipe-from-recents
 *
 * Calls targeting our package are swallowed; everything else proceeds.
 */
class SystemKeepAliveHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        log("SystemKeepAliveHook.install starting")
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
        log("AMS found, installing hooks")
        skipForOurPackage(ams, "forceStopPackage")
        skipForOurPackage(ams, "killBackgroundProcesses")
        allowAutoStart(classLoader)
        surviveSwipeFromRecents(classLoader)
        exemptFromStandbyBuckets(classLoader)
        alwaysAllowExactAlarm(classLoader)
        exemptFromGreezer(classLoader)
        exemptFromCachedAlarm(classLoader)
        log("About to call forceIncludeStoppedPackages")
        forceIncludeStoppedPackages(classLoader)
        log("SystemKeepAliveHook.install complete")
    }

    /**
     * Force FLAG_INCLUDE_STOPPED_PACKAGES on broadcasts destined for our package.
     * This bypasses the PMS stopped-state check entirely — the system will deliver
     * the broadcast even if stopped=true, waking our process if needed.
     *
     * Theory 1 from theory.md: "降维打击" — give our broadcasts the exemption flag
     * that system broadcasts use to reach stopped apps.
     *
     * MIUI uses BroadcastController.broadcastIntentLocked instead of AMS directly.
     */
    private fun forceIncludeStoppedPackages(classLoader: ClassLoader) {
        try {
            log("forceIncludeStoppedPackages: starting")

            // Try BroadcastController first (MIUI)
            val broadcastController = try {
                classLoader.loadClass("com.android.server.am.BroadcastController")
            } catch (t: Throwable) {
                log("BroadcastController not found: ${t.message}")
                null
            }

            if (broadcastController != null) {
                log("BroadcastController found, searching for broadcastIntentLocked method")

                // Find the method - Intent is at parameter index 3, not 1!
                // Signature: broadcastIntentLocked(ProcessRecord, String, String, Intent, ...)
                val method = broadcastController.declaredMethods.firstOrNull {
                    it.name == "broadcastIntentLocked" &&
                    it.parameterTypes.size > 3 &&
                    it.parameterTypes[3] == android.content.Intent::class.java
                }

                if (method != null) {
                    log("Found broadcastIntentLocked with Intent at index 3")
                    hookBroadcastMethod(method, 3) // Pass the Intent parameter index
                    log("BroadcastController.broadcastIntentLocked hooked successfully")
                    return
                } else {
                    log("broadcastIntentLocked method not found with correct signature")
                }
            }

            // Fallback to AMS (unlikely on MIUI but try anyway)
            log("Trying AMS.broadcastIntentLocked as fallback")
            val ams = try {
                classLoader.loadClass(AMS)
            } catch (t: Throwable) {
                log("AMS not found for broadcast hook: ${t.message}")
                return
            }

            val method = ams.declaredMethods.firstOrNull {
                it.name == "broadcastIntentLocked" &&
                it.parameterTypes.any { param -> param == android.content.Intent::class.java }
            }

            if (method == null) {
                log("broadcastIntentLocked not found in AMS")
                return
            }

            // Find Intent parameter index in AMS method
            val intentIndex = method.parameterTypes.indexOfFirst { it == android.content.Intent::class.java }
            hookBroadcastMethod(method, intentIndex)
            log("AMS.broadcastIntentLocked hooked")
        } catch (t: Throwable) {
            log("forceIncludeStoppedPackages failed with exception: ${t.message}")
            t.printStackTrace()
        }
    }

    private fun hookBroadcastMethod(method: java.lang.reflect.Method, intentParamIndex: Int) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    // Get Intent from the correct parameter index
                    val intent = chain.args.getOrNull(intentParamIndex) as? android.content.Intent

                    if (intent != null) {
                        val targetPkg = intent.`package` ?: intent.component?.packageName

                        if (targetPkg == Protocol.MODULE_PACKAGE) {
                            // Force the flag that bypasses stopped-state filtering
                            val FLAG_INCLUDE_STOPPED_PACKAGES = 0x00000020
                            val oldFlags = intent.flags
                            intent.addFlags(FLAG_INCLUDE_STOPPED_PACKAGES)
                            log("forced FLAG_INCLUDE_STOPPED_PACKAGES for ${intent.action} -> $targetPkg (flags: 0x${oldFlags.toString(16)} -> 0x${intent.flags.toString(16)})")
                        }
                    }

                    return chain.proceed()
                }
            })
    }

    /**
     * MIUI "Aurogon" (Greezer v2) delays alarm delivery to frozen apps via
     * GreezeManagerService.isNeedCachedAlarmForAurogonInner — returning true
     * queues the alarm until the app is unfrozen. System apps and packages
     * containing "xiaomi"/"miui" or in mAurogonAlarmAllowList are exempt.
     * Force false for our package so alarms fire on time even when frozen.
     */
    private fun exemptFromCachedAlarm(classLoader: ClassLoader) {
        val method = try {
            classLoader.loadClass(GREEZE_MANAGER_SERVICE)
                .getDeclaredMethod("isNeedCachedAlarmForAurogonInner", Int::class.javaPrimitiveType)
                .apply { isAccessible = true }
        } catch (t: Throwable) {
            log("isNeedCachedAlarmForAurogonInner not found: ${t.message}")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val uid = chain.getArg(0) as? Int ?: return chain.proceed()
                    val myUid = android.os.Process.myUid()  // system_server uid
                    // Can't directly get our app's uid here, check package name instead
                    try {
                        val svc = chain.thisObject
                        val pkg = svc.javaClass.getDeclaredMethod("getPackageNameFromUid", Int::class.javaPrimitiveType)
                            .invoke(svc, uid) as? String
                        if (pkg == Protocol.MODULE_PACKAGE) {
                            log("cached alarm: exempting $pkg")
                            return false  // false = don't cache, send immediately
                        }
                    } catch (t: Throwable) {
                        // Fall through to original logic
                    }
                    return chain.proceed()
                }
            })
        log("isNeedCachedAlarmForAurogonInner hooked")
    }

    /**
     * MIUI Greezer freezes cached apps, and BroadcastQueueImpl.shouldSkipReceiver
     * then denies them ALL broadcast delivery ("Greezer Denial ... need cached
     * broadcast") — including our AlarmManager mode triggers. The check
     * funnels through BroadcastQueueModernStubImpl.checkReceiverIfRestricted
     * (miui-services.jar) -> GreezeManagerInternal.isRestrictReceiver.
     * Returning false for our package exempts it like a system app: the
     * broadcast is delivered, which wakes (unfreezes) our process to run the
     * engine. No process needs to stay resident — delivery itself is the wake.
     */
    private fun exemptFromGreezer(classLoader: ClassLoader) {
        val method = try {
            val stub = classLoader.loadClass(BROADCAST_STUB)
            val queue = classLoader.loadClass(BROADCAST_QUEUE)
            val record = classLoader.loadClass(BROADCAST_RECORD)
            val process = classLoader.loadClass(PROCESS_RECORD)
            stub.getDeclaredMethod(
                "checkReceiverIfRestricted", queue, record, process,
                Boolean::class.javaPrimitiveType
            ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("checkReceiverIfRestricted not found: ${t.message}")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val app = chain.getArg(2) ?: return chain.proceed()
                    val pkg = try {
                        val info = app.javaClass.getField("info").get(app)
                        (info?.javaClass?.getField("packageName")?.get(info) as? String)
                            ?: app.javaClass.getField("processName").get(app) as? String
                    } catch (t: Throwable) {
                        null
                    }
                    if (pkg == Protocol.MODULE_PACKAGE) {
                        log("Greezer: exempting broadcast delivery to $pkg")
                        return false // false = not restricted, deliver normally
                    }
                    return chain.proceed()
                }
            })
        log("checkReceiverIfRestricted hooked")
    }

    /**
     * Apps targeting S+ normally need the user-grantable SCHEDULE_EXACT_ALARM
     * app-op to call setExactAndAllowWhileIdle; AlarmManagerService throws
     * SecurityException otherwise (verified in services.jar:
     * AlarmManagerService.set -> hasScheduleExactAlarmInternal). System apps
     * are exempt via the DeviceIdle whitelist — give HyperModes the same
     * treatment by forcing the check to pass for our package, so the engine's
     * mode-schedule alarms work without the manual grant.
     */
    private fun alwaysAllowExactAlarm(classLoader: ClassLoader) {
        val method = try {
            classLoader.loadClass(ALARM_MANAGER_SERVICE)
                .getDeclaredMethod(
                    "hasScheduleExactAlarmInternal",
                    String::class.java, Int::class.javaPrimitiveType
                ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("hasScheduleExactAlarmInternal not found: ${t.message}")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (chain.getArg(0) == Protocol.MODULE_PACKAGE) return true
                    return chain.proceed()
                }
            })
        log("hasScheduleExactAlarmInternal hooked")
    }

    /**
     * Swiping the app card away in recents calls
     * ActivityTaskSupervisor.cleanUpRemovedTask, which funnels into
     * killTaskProcessesIfPossible(Task) — killing every process containing
     * the task's base package. That single private method is the ONLY kill
     * entry point for removed tasks (all 3 callers in services.jar funnel
     * into it), so skipping it for our package keeps the mode engine alive
     * after the card is swiped away. The task/card itself is still removed
     * normally — only the process kill is skipped.
     */
    private fun surviveSwipeFromRecents(classLoader: ClassLoader) {
        val method = try {
            val supervisor = classLoader.loadClass(TASK_SUPERVISOR)
            val task = classLoader.loadClass(TASK)
            supervisor.getDeclaredMethod("killTaskProcessesIfPossible", task)
                .apply { isAccessible = true }
        } catch (t: Throwable) {
            log("killTaskProcessesIfPossible not found: ${t.message}")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val task = chain.getArg(0) ?: return chain.proceed()
                    val pkg = try {
                        task.javaClass.getMethod("getBasePackageName")
                            .invoke(task) as? String
                    } catch (t: Throwable) {
                        null
                    }
                    if (pkg == Protocol.MODULE_PACKAGE) {
                        log("swipe from recents: keeping $pkg alive")
                        return null // void method — skip the process kill
                    }
                    return chain.proceed()
                }
            })
        log("killTaskProcessesIfPossible hooked")
    }

    /**
     * MIUI blocks cold-starting apps for manifest broadcasts unless the user
     * manually enables 自启动 ("process is not permitted to auto start").
     * System apps are exempt — make HyperModes exempt too by forcing
     * checkApplicationAutoStart to allow our package, so Bluetooth car events
     * and BOOT_COMPLETED always reach our receivers.
     */
    private fun allowAutoStart(classLoader: ClassLoader) {
        val method = try {
            val stub = classLoader.loadClass(BROADCAST_STUB)
            val queue = classLoader.loadClass(BROADCAST_QUEUE)
            val record = classLoader.loadClass(BROADCAST_RECORD)
            stub.getDeclaredMethod(
                "checkApplicationAutoStart", queue, record,
                android.content.pm.ResolveInfo::class.java
            ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("checkApplicationAutoStart not found: ${t.message}")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val info = chain.getArg(2) as? android.content.pm.ResolveInfo
                    val pkg = info?.activityInfo?.applicationInfo?.packageName
                    if (pkg == Protocol.MODULE_PACKAGE) {
                        return true // true = auto start permitted
                    }
                    return chain.proceed()
                }
            })
        log("checkApplicationAutoStart hooked")
    }

    /**
     * Exact alarms from background-restricted standby buckets are throttled.
     * System apps are exempt — force our bucket report to ACTIVE so the
     * engine's setExactAndAllowWhileIdle alarms fire on time, like a system
     * app's would. Best-effort: if MIUI renamed the method, the module log
     * says so and alarms still fire (allowWhileIdle bypasses most throttling).
     */
    private fun exemptFromStandbyBuckets(classLoader: ClassLoader) {
        val controller = try {
            classLoader.loadClass(APP_STANDBY_CONTROLLER)
        } catch (t: Throwable) {
            log("AppStandbyController not found: ${t.message}")
            return
        }
        // AOSP: getAppStandbyBucket(String packageName, int userId,
        // long elapsedRealtime, boolean shouldMinimizeUsage)
        val method = controller.declaredMethods.firstOrNull {
            it.name == "getAppStandbyBucket" &&
                    it.parameterTypes.firstOrNull() == String::class.java
        }
        if (method == null) {
            log("getAppStandbyBucket(String,...) not found")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (chain.getArg(0) == Protocol.MODULE_PACKAGE) {
                        return android.app.usage.UsageStatsManager.STANDBY_BUCKET_ACTIVE
                    }
                    return chain.proceed()
                }
            })
        log("getAppStandbyBucket hooked")
    }

    private fun skipForOurPackage(ams: Class<*>, name: String) {
        val method = try {
            ams.getDeclaredMethod(name, String::class.java, Int::class.javaPrimitiveType)
                .apply { isAccessible = true }
        } catch (t: Throwable) {
            log("$name not found: ${t.message}")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val pkg = chain.getArg(0) as? String
                    if (pkg == Protocol.MODULE_PACKAGE) {
                        log("blocked $name for $pkg")
                        return null // both methods return void
                    }
                    return chain.proceed()
                }
            })
        log("AMS.$name hooked")
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val AMS = "com.android.server.am.ActivityManagerService"
        private const val BROADCAST_STUB = "com.android.server.am.BroadcastQueueModernStubImpl"
        private const val BROADCAST_QUEUE = "com.android.server.am.BroadcastQueue"
        private const val BROADCAST_RECORD = "com.android.server.am.BroadcastRecord"
        private const val PROCESS_RECORD = "com.android.server.am.ProcessRecord"
        private const val TASK_SUPERVISOR = "com.android.server.wm.ActivityTaskSupervisor"
        private const val TASK = "com.android.server.wm.Task"
        private const val APP_STANDBY_CONTROLLER = "com.android.server.usage.AppStandbyController"
        private const val ALARM_MANAGER_SERVICE = "com.android.server.alarm.AlarmManagerService"
        private const val GREEZE_MANAGER_SERVICE = "com.miui.server.greeze.GreezeManagerService"
    }
}
