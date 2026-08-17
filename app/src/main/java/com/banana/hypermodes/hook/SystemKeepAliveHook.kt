package com.banana.hypermodes.hook

import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Keep-alive for the HyperModes BroadcastReceivers, running INSIDE system_server.
 *
 * In zero-process architecture, RoutineCoreEngine runs in system_server and doesn't
 * need process keep-alive. However, certain triggers (Bluetooth, charging) still rely
 * on app BroadcastReceivers, which are disabled when the app is force-stopped.
 *
 * This hook prevents force-stop and ensures broadcasts reach the app even in stopped state:
 * - forceStopPackage(String, int)   — force stop (Settings app info page)
 * - killBackgroundProcesses(String, int) — background kill (cleaner apps)
 * - forceIncludeStoppedPackages — ensure broadcasts reach stopped apps
 * - allowAutoStart — bypass MIUI "自启动" restrictions
 * - alwaysAllowExactAlarm — allow scheduled modes to use exact alarms
 *
 * Calls targeting our package are swallowed or modified; everything else proceeds normally.
 */
class SystemKeepAliveHook(private val module: XposedModule) {

    @Volatile
    private var deferredHooksInstalled = false

    /** Install OS4 keep-alive hooks in system_server. */
    fun install(classLoader: ClassLoader) {
        log("SystemKeepAliveHook.install starting")
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
        log("AMS found, installing hooks")

        // Always install these critical hooks immediately
        skipForOurPackage(ams, "forceStopPackage")
        skipForOurPackage(ams, "killBackgroundProcesses")
        alwaysAllowExactAlarm(classLoader)

        // OS4 system_server startup is sensitive to heavy reflection during
        // onSystemServerStarting, so the remaining hooks are installed only
        // after ActivityManagerService.systemReady completes.
        installDeferredHooksOnSystemReady(classLoader, ams)

        log("SystemKeepAliveHook.install complete")
    }

    /**
     * Hook AMS.systemReady to install heavy hooks after system_server startup completes.
     * This avoids lspd Binder timeout during onSystemServerStarting.
     */
    private fun installDeferredHooksOnSystemReady(classLoader: ClassLoader, ams: Class<*>) {
        val systemReady = try {
            ams.getDeclaredMethod(
                "systemReady",
                Runnable::class.java,
                classLoader.loadClass(TIMINGS_TRACE_AND_SLOG)
            ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("OS4 ActivityManagerService.systemReady signature not found: ${t.message}")
            return
        }

        module.hook(systemReady)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (deferredHooksInstalled) return result
                    deferredHooksInstalled = true
                    try {
                        log("systemReady called, installing deferred hooks")
                        allowAutoStart(classLoader)
                        forceIncludeStoppedPackages(classLoader)
                        log("Deferred hooks installed successfully")
                    } catch (t: Throwable) {
                        deferredHooksInstalled = false
                        log("Failed to install deferred hooks: ${t.message}")
                        t.printStackTrace()
                    }
                    return result
                }
            })
        log("systemReady hooked for deferred hook installation")
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

            val broadcastController = classLoader.loadClass(BROADCAST_CONTROLLER)
            val method = broadcastController.declaredMethods.firstOrNull {
                it.name == "broadcastIntentLocked" &&
                    it.parameterTypes.map(Class<*>::getName) ==
                    OS4_BROADCAST_INTENT_LOCKED_PARAMETER_TYPES
            }
            if (method == null) {
                log("OS4 BroadcastController.broadcastIntentLocked signature not found")
                return
            }
            hookBroadcastMethod(method, 3)
            log("OS4 BroadcastController.broadcastIntentLocked hooked")
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
     * MIUI blocks cold-starting apps for manifest broadcasts unless the user
     * manually enables 自启动 ("process is not permitted to auto start").
     * System apps are exempt — make HyperModes exempt too by forcing
     * checkApplicationAutoStart to allow our package, so Bluetooth car events
     * and BOOT_COMPLETED always reach our receivers.
     */
    private fun allowAutoStart(classLoader: ClassLoader) {
        val method = try {
            val queue = classLoader.loadClass(BROADCAST_QUEUE)
            val record = classLoader.loadClass(BROADCAST_RECORD)
            val targetClass = classLoader.loadClass(BROADCAST_STUB)
            targetClass.getDeclaredMethod(
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

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val AMS = "com.android.server.am.ActivityManagerService"
        private const val TIMINGS_TRACE_AND_SLOG =
            "com.android.server.utils.TimingsTraceAndSlog"
        private const val BROADCAST_CONTROLLER = "com.android.server.am.BroadcastController"
        private const val BROADCAST_STUB = "com.android.server.am.BroadcastQueueModernStubImpl"
        private const val BROADCAST_QUEUE = "com.android.server.am.BroadcastQueue"
        private const val BROADCAST_RECORD = "com.android.server.am.BroadcastRecord"
        private const val ALARM_MANAGER_SERVICE = "com.android.server.alarm.AlarmManagerService"

        private val OS4_BROADCAST_INTENT_LOCKED_PARAMETER_TYPES = listOf(
            "com.android.server.am.ProcessRecord",
            "java.lang.String",
            "java.lang.String",
            "android.content.Intent",
            "java.lang.String",
            "com.android.server.am.ProcessRecord",
            "android.content.IIntentReceiver",
            "int",
            "java.lang.String",
            "android.os.Bundle",
            "[Ljava.lang.String;",
            "[Ljava.lang.String;",
            "[Ljava.lang.String;",
            "int",
            "android.os.Bundle",
            "boolean",
            "boolean",
            "int",
            "int",
            "int",
            "int",
            "int",
            "android.app.BackgroundStartPrivileges",
            "[I",
            "java.util.function.BiFunction"
        )
    }
}
