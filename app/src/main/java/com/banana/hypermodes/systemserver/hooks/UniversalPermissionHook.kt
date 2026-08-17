package com.banana.hypermodes.systemserver.hooks

import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Method

/**
 * OS4 universal permission hook - real grants plus targeted interception.
 *
 * Runtime (dangerous) permissions declared in our manifest are granted for
 * real from inside system_server (the same operation `pm grant` performs):
 * every check path returns GRANTED natively and the matching app-ops are
 * allowed. This runs at boot and again when our package is replaced or
 * reinstalled (see SystemModeHook's lifecycle receiver), so newly added
 * permissions never need a reboot or a user prompt.
 *
 * The interception hooks below remain for what real grants cannot cover
 * (WRITE_SECURE_SETTINGS is signature-level and not runtime-grantable):
 * - ContextImpl.checkPermission / enforcePermission
 * - IPackageManagerBase permission checks
 *
 * Only affects our app - surgical and safe.
 */
class UniversalPermissionHook(private val module: XposedModule) {

    private val targetPermissions = setOf(
        // Critical: Settings.Global write access
        "android.permission.WRITE_SECURE_SETTINGS",

        // Runtime permissions that may need auto-grant
        "android.permission.ACCESS_NOTIFICATION_POLICY",
        "android.permission.BLUETOOTH_CONNECT",
        "android.permission.ACTIVITY_RECOGNITION",
        "android.permission.SCHEDULE_EXACT_ALARM",
        "android.permission.READ_LOGS",
        // SIM card detection (enumerate active subscriptions)
        "android.permission.READ_PHONE_STATE",
        "android.permission.READ_PRECISE_PHONE_STATE"
    )

    private var systemContext: Context? = null

    fun install(classLoader: ClassLoader) {
        try {
            log("Installing UniversalPermissionHook for OS4 / Android 17...")

            // Cache system context for package name lookups
            cacheSystemContext()

            // Hook 1: ContextImpl permission checks (most common path)
            hookContextImpl(classLoader)

            // Hook 2: OS4 IPackageManager binder base permission checks
            hookPackageManagerBase(classLoader)

            // Real-grant every runtime permission declared in the manifest.
            // Real grants beat interception: checkSelfPermission() in the app
            // returns GRANTED natively and the matching app-ops are allowed.
            grantRuntimePermissions()

            log("UniversalPermissionHook installed successfully")
        } catch (t: Throwable) {
            log("Failed to install UniversalPermissionHook: ${t.message}")
            t.printStackTrace()
        }
    }

    private fun cacheSystemContext() {
        try {
            val activityThread = Class.forName("android.app.ActivityThread")
            val currentActivityThread = activityThread.getMethod("currentActivityThread")
            val thread = currentActivityThread.invoke(null)
            val getSystemContext = activityThread.getMethod("getSystemContext")
            systemContext = getSystemContext.invoke(thread) as? Context
            log("System context cached: ${systemContext != null}")
        } catch (t: Throwable) {
            log("Failed to cache system context: ${t.message}")
        }
    }

    /**
     * Hook ContextImpl permission checks - the main entry point for permission checks
     */
    private fun hookContextImpl(classLoader: ClassLoader) {
        try {
            val contextImpl = classLoader.loadClass("android.app.ContextImpl")

            // Hook checkPermission (returns PERMISSION_GRANTED or PERMISSION_DENIED)
            hookMethod(contextImpl, "checkPermission",
                arrayOf<Class<*>>(String::class.java, Int::class.javaPrimitiveType!!, Int::class.javaPrimitiveType!!),
                PackageManager.PERMISSION_GRANTED)

            // Hook enforcePermission (throws SecurityException if denied)
            hookEnforceMethod(contextImpl, "enforcePermission",
                arrayOf<Class<*>>(String::class.java, Int::class.javaPrimitiveType!!, Int::class.javaPrimitiveType!!, String::class.java))

            // Hook enforceCallingOrSelfPermission
            hookEnforceMethod(contextImpl, "enforceCallingOrSelfPermission",
                arrayOf<Class<*>>(String::class.java, String::class.java))

            log("ContextImpl permission checks hooked")
        } catch (t: Throwable) {
            log("Failed to hook ContextImpl: ${t.message}")
        }
    }

    /**
     * Hook the OS4 IPackageManager implementation base. PackageManagerService
     * itself no longer declares checkUidPermission(String, int).
     */
    private fun hookPackageManagerBase(classLoader: ClassLoader) {
        try {
            val packageManagerBase = classLoader.loadClass(
                "com.android.server.pm.IPackageManagerBase"
            )

            hookMethod(packageManagerBase, "checkUidPermission",
                arrayOf<Class<*>>(String::class.java, Int::class.javaPrimitiveType!!),
                PackageManager.PERMISSION_GRANTED)

            log("OS4 IPackageManagerBase.checkUidPermission hooked")
        } catch (t: Throwable) {
            log("Failed to hook OS4 IPackageManagerBase: ${t.message}")
        }
    }

    /**
     * Actually grant every dangerous (runtime) permission declared in our
     * manifest, using system_server's privileges (the same operation
     * `pm grant` performs). Called at boot from install() and again from
     * SystemModeHook's package lifecycle receiver, because a fresh install —
     * or an update that adds permissions — leaves them denied until granted.
     *
     * Real grants also put the matching app-ops (BLUETOOTH_CONNECT,
     * FINE_LOCATION, ACTIVITY_RECOGNITION) into their allowed state, which
     * pure check-interception does not.
     */
    fun grantRuntimePermissions() {
        try {
            val context = systemContext
            if (context == null) {
                log("grantRuntimePermissions: system context unavailable")
                return
            }
            val pm = context.packageManager

            @Suppress("DEPRECATION")
            val requested = pm.getPackageInfo(
                Protocol.MODULE_PACKAGE, PackageManager.GET_PERMISSIONS
            ).requestedPermissions
            if (requested.isNullOrEmpty()) {
                log("grantRuntimePermissions: no declared permissions")
                return
            }

            val (target, grantMethod) = resolveGrantTarget() ?: run {
                log("grantRuntimePermissions: no grant path available")
                return
            }
            val userId = getCurrentUserId()

            var grantedCount = 0
            for (permission in requested) {
                try {
                    if (!isDangerous(pm, permission)) continue
                    if (pm.checkPermission(permission, Protocol.MODULE_PACKAGE) ==
                        PackageManager.PERMISSION_GRANTED
                    ) continue

                    grantMethod.invoke(target, Protocol.MODULE_PACKAGE, permission, userId)
                    grantedCount++
                    log("Granted runtime permission: $permission")
                } catch (t: Throwable) {
                    log("Failed to grant $permission: $t")
                }
            }
            log("grantRuntimePermissions: $grantedCount newly granted " +
                "(${requested.size} declared, user $userId)")
        } catch (t: Throwable) {
            log("grantRuntimePermissions failed: $t")
        }
    }

    private fun isDangerous(pm: PackageManager, permission: String): Boolean {
        return try {
            @Suppress("DEPRECATION")
            val info = pm.getPermissionInfo(permission, 0)
            info.protection and PROTECTION_MASK_BASE == PROTECTION_DANGEROUS
        } catch (t: Throwable) {
            false
        }
    }

    /**
     * Resolve the OS4 IPackageManager grantRuntimePermission(String, String,
     * int) entry exposed through AppGlobals. The Android 17 permissionmgr
     * interface has a different four-argument signature and is intentionally
     * not retained as an older-version compatibility path.
     */
    private fun resolveGrantTarget(): Pair<Any, Method>? {
        try {
            val ipm = Class.forName("android.app.AppGlobals")
                .getMethod("getPackageManager")
                .invoke(null)
            if (ipm != null) {
                val method = ipm.javaClass.getMethod(
                    "grantRuntimePermission",
                    String::class.java,
                    String::class.java,
                    Int::class.javaPrimitiveType!!
                )
                return ipm to method
            }
        } catch (t: Throwable) {
            log("AppGlobals grant path unavailable: ${t.message}")
        }

        return null
    }

    private fun getCurrentUserId(): Int {
        return try {
            Class.forName("android.app.ActivityManager")
                .getMethod("getCurrentUser")
                .invoke(null) as? Int ?: 0
        } catch (t: Throwable) {
            0
        }
    }

    private fun hookMethod(clazz: Class<*>, methodName: String, paramTypes: Array<Class<*>>, grantedValue: Int) {
        try {
            val method = clazz.getDeclaredMethod(methodName, *paramTypes)
                .apply { isAccessible = true }

            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val permission = chain.getArg(0) as? String

                        // Check if it's our app and a target permission
                        if (isCallerModule() && permission in targetPermissions) {
                            log("Magic Grant: $methodName -> GRANTED for $permission")
                            return grantedValue
                        }

                        return chain.proceed()
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook $methodName: ${t.message}")
        }
    }

    private fun hookEnforceMethod(clazz: Class<*>, methodName: String, paramTypes: Array<Class<*>>) {
        try {
            val method = clazz.getDeclaredMethod(methodName, *paramTypes)
                .apply { isAccessible = true }

            module.hook(method)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val permission = chain.getArg(0) as? String

                        // Check if it's our app and a target permission
                        if (isCallerModule() && permission in targetPermissions) {
                            log("Magic Grant: $methodName -> bypassed for $permission")
                            return null // void method - skip enforcement
                        }

                        return chain.proceed()
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook $methodName: ${t.message}")
        }
    }

    /**
     * Resolve the Binder caller through the cached system context. The context is
     * cached during install(), before any interception can run, so a single lookup
     * path is sufficient. Shared UIDs are matched by membership, not position, so a
     * UID shared with other packages is still recognized as ours.
     */
    private fun isCallerModule(): Boolean {
        return try {
            val uid = Binder.getCallingUid()
            if (uid <= 0) return false
            val pm = systemContext?.packageManager ?: return false
            val packages = pm.getPackagesForUid(uid)
            packages != null && Protocol.MODULE_PACKAGE in packages
        } catch (t: Throwable) {
            log("isCallerModule failed: ${t.message}")
            false
        }
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes.UniversalPermission"
        // PermissionInfo.PROTECTION_MASK_BASE / PROTECTION_DANGEROUS (the
        // mask constant is @SystemApi, so mirror both values locally).
        private const val PROTECTION_MASK_BASE = 0xF
        private const val PROTECTION_DANGEROUS = 1
    }
}
