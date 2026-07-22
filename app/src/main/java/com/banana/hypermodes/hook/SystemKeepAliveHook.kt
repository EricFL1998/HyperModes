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
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
        skipForOurPackage(ams, "forceStopPackage")
        skipForOurPackage(ams, "killBackgroundProcesses")
        allowAutoStart(classLoader)
        surviveSwipeFromRecents(classLoader)
        exemptFromStandbyBuckets(classLoader)
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
        private const val TASK_SUPERVISOR = "com.android.server.wm.ActivityTaskSupervisor"
        private const val TASK = "com.android.server.wm.Task"
        private const val APP_STANDBY_CONTROLLER = "com.android.server.usage.AppStandbyController"
    }
}
