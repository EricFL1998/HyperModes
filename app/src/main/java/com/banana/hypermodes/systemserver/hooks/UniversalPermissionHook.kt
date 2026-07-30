package com.banana.hypermodes.systemserver.hooks

import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Method

/**
 * Universal Permission Hook - real grants + Magic Grant fallback.
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
 * - SettingsProvider permission checks for WRITE_SECURE_SETTINGS
 * - PackageManagerService permission checks
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
        "android.permission.READ_LOGS"
    )

    private var systemContext: Context? = null

    fun install(classLoader: ClassLoader) {
        try {
            log("Installing UniversalPermissionHook for Android 16...")

            // Cache system context for package name lookups
            cacheSystemContext()

            // Hook 1: ContextImpl permission checks (most common path)
            hookContextImpl(classLoader)

            // Hook 2: SettingsProvider for WRITE_SECURE_SETTINGS (critical for Settings.Global)
            hookSettingsProvider(classLoader)

            // Hook 3: PackageManagerService for runtime permission checks
            hookPackageManagerService(classLoader)

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
     * Hook SettingsProvider for WRITE_SECURE_SETTINGS
     *
     * Android 16 SettingsProvider permission checks:
     * - enforceWritePermission(String callingPackage)
     * - checkWritePermissions(String callingPackage)
     * - May also check via Context.enforceCallingOrSelfPermission
     */
    private fun hookSettingsProvider(classLoader: ClassLoader) {
        try {
            val settingsProvider = classLoader.loadClass(SETTINGS_PROVIDER)
            log("Found SettingsProvider class")

            var hookedCount = 0

            // Strategy 1: Hook common permission check method names
            val commonMethodNames = listOf(
                "enforceWritePermission",
                "checkWritePermissions",
                "enforceCallingOrSelfPermission",
                "checkCallingOrSelfPermission"
            )

            for (methodName in commonMethodNames) {
                try {
                    // Find method with String parameter (callingPackage or permission)
                    val method = settingsProvider.declaredMethods.firstOrNull {
                        it.name == methodName &&
                        it.parameterTypes.isNotEmpty() &&
                        it.parameterTypes[0] == String::class.java
                    }

                    if (method != null) {
                        method.isAccessible = true
                        module.hook(method)
                            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                            .intercept(object : XposedInterface.Hooker {
                                override fun intercept(chain: XposedInterface.Chain): Any? {
                                    val callingPackage = getCallingPackageAdvanced()

                                    if (callingPackage == Protocol.MODULE_PACKAGE) {
                                        log("SettingsProvider: bypassing $methodName for our app")
                                        // Return success - either null (void) or don't throw exception
                                        return null
                                    }

                                    return chain.proceed()
                                }
                            })
                        log("Hooked SettingsProvider.$methodName")
                        hookedCount++
                    }
                } catch (t: Throwable) {
                    // Continue with next method
                }
            }

            // Strategy 2: Hook all methods containing "enforce" or "check" + "permission"
            if (hookedCount == 0) {
                log("Common methods not found, trying pattern matching...")
                val methods = settingsProvider.declaredMethods.filter {
                    val name = it.name.lowercase()
                    (name.contains("enforce") || name.contains("check")) &&
                    name.contains("permission")
                }

                for (method in methods) {
                    try {
                        method.isAccessible = true
                        module.hook(method)
                            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                            .intercept(object : XposedInterface.Hooker {
                                override fun intercept(chain: XposedInterface.Chain): Any? {
                                    val callingPackage = getCallingPackageAdvanced()

                                    if (callingPackage == Protocol.MODULE_PACKAGE) {
                                        log("SettingsProvider: bypassing ${method.name} for our app")
                                        return null
                                    }

                                    return chain.proceed()
                                }
                            })
                        log("Hooked SettingsProvider.${method.name} (pattern match)")
                        hookedCount++
                    } catch (t: Throwable) {
                        // Continue with next method
                    }
                }
            }

            log("SettingsProvider: hooked $hookedCount methods")
        } catch (t: Throwable) {
            log("Failed to hook SettingsProvider: ${t.message}")
            t.printStackTrace()
        }
    }

    /**
     * Hook PackageManagerService for runtime permission checks
     */
    private fun hookPackageManagerService(classLoader: ClassLoader) {
        try {
            val pms = classLoader.loadClass("com.android.server.pm.PackageManagerService")

            // Hook checkUidPermission
            hookMethod(pms, "checkUidPermission",
                arrayOf<Class<*>>(String::class.java, Int::class.javaPrimitiveType!!),
                PackageManager.PERMISSION_GRANTED)

            log("PackageManagerService permission checks hooked")
        } catch (t: Throwable) {
            log("Failed to hook PackageManagerService: ${t.message}")
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
            info.protectionLevel and PROTECTION_MASK_BASE == PROTECTION_DANGEROUS
        } catch (t: Throwable) {
            false
        }
    }

    /**
     * Find a grantRuntimePermission(String packageName, String permission,
     * int userId) implementation. Primary: IPackageManager via AppGlobals
     * (local proxy inside system_server). Fallback: the "permissionmgr"
     * binder from the Android 13+ permission split. Same argument order on
     * both interfaces.
     */
    private fun resolveGrantTarget(): Pair<Any, Method>? {
        try {
            val ipm = Class.forName("android.app.AppGlobals")
                .getMethod("getPackageManager")
                .invoke(null)
            if (ipm != null) {
                val method = findGrantMethod(ipm)
                if (method != null) return ipm to method
            }
        } catch (t: Throwable) {
            log("AppGlobals grant path unavailable: ${t.message}")
        }

        try {
            val binder = Class.forName("android.os.ServiceManager")
                .getMethod("getService", String::class.java)
                .invoke(null, "permissionmgr") as? IBinder
            val mgr = Class.forName("android.permission.IPermissionManager\$Stub")
                .getMethod("asInterface", IBinder::class.java)
                .invoke(null, binder)
            if (mgr != null) {
                val method = findGrantMethod(mgr)
                if (method != null) return mgr to method
            }
        } catch (t: Throwable) {
            log("permissionmgr grant path unavailable: ${t.message}")
        }

        return null
    }

    private fun findGrantMethod(target: Any): Method? {
        return target.javaClass.methods.firstOrNull {
            it.name == "grantRuntimePermission" &&
                it.parameterTypes.size == 3 &&
                it.parameterTypes[0] == String::class.java &&
                it.parameterTypes[1] == String::class.java &&
                it.parameterTypes[2] == Int::class.javaPrimitiveType
        }
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
                        val callingPackage = getCallingPackageAdvanced()
                        if (callingPackage == Protocol.MODULE_PACKAGE && permission in targetPermissions) {
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
                        val callingPackage = getCallingPackageAdvanced()
                        if (callingPackage == Protocol.MODULE_PACKAGE && permission in targetPermissions) {
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
     * Advanced method to get calling package name with multiple fallback strategies.
     *
     * Android 16 may have changed Binder APIs, so we try multiple approaches:
     * 1. Binder.getCallingUid() -> PackageManager.getPackagesForUid()
     * 2. System context PackageManager lookup
     * 3. ActivityThread PackageManager lookup
     */
    private fun getCallingPackageAdvanced(): String? {
        // Strategy 1: Binder + cached system context
        try {
            val uid = Binder.getCallingUid()
            if (uid > 0) {
                val pm = systemContext?.packageManager
                val packages = pm?.getPackagesForUid(uid)
                if (!packages.isNullOrEmpty()) {
                    log("CallingPackage (cached): ${packages[0]} (uid=$uid)")
                    return packages[0]
                }
            }
        } catch (t: Throwable) {
            // Try next strategy
        }

        // Strategy 2: ActivityThread
        try {
            val uid = Binder.getCallingUid()
            if (uid > 0) {
                val activityThread = Class.forName("android.app.ActivityThread")
                val currentActivityThread = activityThread.getMethod("currentActivityThread")
                val thread = currentActivityThread.invoke(null)
                val getSystemContext = activityThread.getMethod("getSystemContext")
                val context = getSystemContext.invoke(thread) as? Context
                val pm = context?.packageManager
                val packages = pm?.getPackagesForUid(uid)
                if (!packages.isNullOrEmpty()) {
                    log("CallingPackage (ActivityThread): ${packages[0]} (uid=$uid)")
                    return packages[0]
                }
            }
        } catch (t: Throwable) {
            // Try next strategy
        }

        // Strategy 3: AppGlobals (Android internal)
        try {
            val uid = Binder.getCallingUid()
            if (uid > 0) {
                val appGlobals = Class.forName("android.app.AppGlobals")
                val getPackageManager = appGlobals.getMethod("getPackageManager")
                val pm = getPackageManager.invoke(null)
                val getPackagesForUid = pm.javaClass.getMethod("getPackagesForUid", Int::class.javaPrimitiveType)
                val packages = getPackagesForUid.invoke(pm, uid) as? Array<*>
                if (!packages.isNullOrEmpty()) {
                    log("CallingPackage (AppGlobals): ${packages[0]} (uid=$uid)")
                    return packages[0] as? String
                }
            }
        } catch (t: Throwable) {
            log("All CallingPackage strategies failed: ${t.message}")
        }

        return null
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes.UniversalPermission"
        private const val SETTINGS_PROVIDER = "com.android.providers.settings.SettingsProvider"

        // PermissionInfo.PROTECTION_MASK_BASE / PROTECTION_DANGEROUS (the
        // mask constant is @SystemApi, so mirror both values locally).
        private const val PROTECTION_MASK_BASE = 0xF
        private const val PROTECTION_DANGEROUS = 1
    }
}
