package com.banana.hypermodes.hook

import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.os.ResultReceiver
import android.os.Environment
import android.provider.Settings
import android.util.Log
import java.io.File
import com.banana.hypermodes.protocol.PackageLifecyclePolicy
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.hooks.UniversalPermissionHook
import com.banana.hypermodes.systemserver.trigger.PolarisGeofenceAdapter
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Privileged-operation bridge running INSIDE system_server.
 *
 * The mode engine (our app process) can't call setPackagesSuspended or edit
 * other apps' notification channels — both need system permissions. This
 * hook captures ActivityManagerService.systemReady (mContext is ready by
 * then) and registers a dynamic receiver guarded by our signature
 * permission, so only HyperModes can dispatch:
 *
 * - ACTION_SET_PACKAGES_SUSPENDED: suspend/unsuspend packages via the
 *   "package" binder (IPackageManager.setPackagesSuspendedAsUser).
 * - ACTION_SET_CHANNELS_BYPASS_DND: set/clear bypass-Dnd on every channel
 *   of the given packages via the "notification" binder. Original per-channel
 *   bypass flags are remembered in memory and restored on clear.
 * - ACTION_GET_CONFIGURED_WIFI: return saved WiFi SSIDs via ResultReceiver
 *   (apps lost getConfiguredNetworks in Android 10; system_server still
 *   qualifies).
 *
 * Also initializes RoutineCoreEngine which runs entirely in system_server.
 */
class SystemModeHook(private val module: XposedModule) {

    /** Kept so a package replace/reinstall can re-grant runtime permissions. */
    private var permissionHook: UniversalPermissionHook? = null

    fun install(classLoader: ClassLoader) {
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
        val systemReady = ams.declaredMethods.firstOrNull { it.name == "systemReady" }
        if (systemReady == null) {
            log("systemReady not found")
            return
        }
        module.hook(systemReady)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                        val thisObject = getThisObjectMethod.invoke(chain)

                        val context = ams.getDeclaredField("mContext")
                            .apply { isAccessible = true }[thisObject] as Context

                        // Install UniversalPermissionHook for automatic permission grant
                        permissionHook = UniversalPermissionHook(module)
                        permissionHook?.install(classLoader)

                        clearStoppedState(context)
                        registerBridge(context)
                        registerPackageLifecycleReceiver(context)
                        initRoutineCoreEngine(context, classLoader)
                    } catch (t: Throwable) {
                        log("bridge registration failed: $t")
                    }
                    return result
                }
            })
        log("systemReady hooked for mode bridge and RoutineCoreEngine")
    }

    /**
     * Clear the "stopped" state on boot so BOOT_COMPLETED and alarm broadcasts
     * can reach our manifest receivers. On MIUI, swiping from recents sets
     * stopped=true, which blocks ALL broadcasts until the user launches the app.
     */
    private fun clearStoppedState(context: Context) {
        try {
            val pm = context.packageManager
            val pmService = pm.javaClass.getDeclaredField("mPM")
                .apply { isAccessible = true }
                .get(pm)
            val method = pmService.javaClass.getDeclaredMethod(
                "setPackageStoppedState",
                String::class.java,
                Boolean::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
            method.invoke(pmService, Protocol.MODULE_PACKAGE, false, 0)
            log("cleared stopped state for ${Protocol.MODULE_PACKAGE}")
        } catch (t: Throwable) {
            log("failed to clear stopped state: ${t.message}")
        }
    }

    /**
     * Initialize RoutineCoreEngine in system_server context.
     * This engine will handle all mode logic independently of the app process.
     */
    private fun initRoutineCoreEngine(context: Context, classLoader: ClassLoader) {
        try {
            val engine = RoutineCoreEngine.getInstance()
            engine.init(context, classLoader)
            log("RoutineCoreEngine initialized")
        } catch (t: Throwable) {
            log("RoutineCoreEngine initialization failed: $t")
        }
    }

    private fun registerBridge(context: Context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                // Actions without EXTRA_PACKAGES — handle before the extraction below.
                when (intent.action) {
                    Protocol.ACTION_POLARIS_GEOFENCE_EVENT -> {
                        // Polaris geofence events are now handled by PolarisManagerAdapter
                        // via the SDK callback mechanism, not through this broadcast path.
                        // This case is kept for backward compatibility but should not be used.
                        log("Received legacy Polaris geofence broadcast - ignoring (handled by SDK)")
                        return
                    }
                    Protocol.ACTION_GET_CONFIGURED_WIFI -> {
                        sendConfiguredWifi(c, intent)
                        return
                    }
                    Protocol.ACTION_PROBE_POLARIS -> {
                        probePolaris(c, intent)
                        return
                    }
                    Protocol.ACTION_CAPTURE_WALLPAPER_SNAPSHOT -> {
                        captureWallpaperSnapshot(c, intent)
                        return
                    }
                }
                val packages = intent.getStringArrayExtra(Protocol.EXTRA_PACKAGES)
                    ?.toList() ?: return
                when (intent.action) {
                    Protocol.ACTION_SET_PACKAGES_SUSPENDED ->
                        setPackagesSuspended(
                            packages,
                            intent.getBooleanExtra(Protocol.EXTRA_SUSPENDED, false)
                        )
                    Protocol.ACTION_SET_CHANNELS_BYPASS_DND ->
                        setChannelsBypassDnd(
                            c, packages,
                            intent.getBooleanExtra(Protocol.EXTRA_BYPASS, false)
                        )
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_SET_PACKAGES_SUSPENDED)
            addAction(Protocol.ACTION_SET_CHANNELS_BYPASS_DND)
            addAction(Protocol.ACTION_GET_CONFIGURED_WIFI)
            addAction(Protocol.ACTION_PROBE_POLARIS)
            addAction(Protocol.ACTION_CAPTURE_WALLPAPER_SNAPSHOT)
            addAction(Protocol.ACTION_POLARIS_GEOFENCE_EVENT)
        }
        context.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        log("mode bridge receiver registered in system_server")
    }

    /**
     * Return the device's saved WiFi SSIDs to the picker via ResultReceiver.
     * Regular apps lost WifiManager.getConfiguredNetworks() in Android 10,
     * but system_server still qualifies.
     */
    private fun sendConfiguredWifi(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        val resultReceiver = intent.getParcelableExtra<ResultReceiver>(Protocol.EXTRA_RESULT_RECEIVER)
        if (resultReceiver == null) {
            log("GET_CONFIGURED_WIFI without ResultReceiver")
            return
        }
        val ssids = try {
            val wifi = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            wifi.configuredNetworks
                ?.mapNotNull { it.SSID?.removeSurrounding("\"") }
                ?.filter { it.isNotEmpty() && it != "<unknown ssid>" }
                ?.distinct()
                ?.sorted()
                ?: emptyList()
        } catch (t: Throwable) {
            log("getConfiguredNetworks failed: ${t.message}")
            emptyList()
        }
        log("GET_CONFIGURED_WIFI: returning ${ssids.size} saved networks")
        resultReceiver.send(0, android.os.Bundle().apply {
            putStringArray(Protocol.EXTRA_SSIDS, ssids.toTypedArray())
        })
    }

    /**
     * Probe Xiaomi Polaris geofencing capability and return structured result.
     * This is a fail-closed gate: location triggers are NOT implemented unless
     * the probe confirms Polaris is available and allows non-SecurityCenter callers.
     */
    private fun probePolaris(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        val resultReceiver = intent.getParcelableExtra<ResultReceiver>(Protocol.EXTRA_RESULT_RECEIVER)
        if (resultReceiver == null) {
            log("PROBE_POLARIS without ResultReceiver")
            return
        }

        log("PROBE_POLARIS: starting capability detection")
        val adapter = PolarisGeofenceAdapter(context)
        val report = adapter.getCapabilityReport()

        val supported = report.getBoolean("supported", false)
        val message = report.getString("message", "Unknown result")

        log("PROBE_POLARIS result: supported=$supported, message=$message")
        resultReceiver.send(0, report)
    }

    /**
     * Capture the current lock-screen style JSON + wallpaper files so the App can
     * store them as a mode's wallpaper set (used after the user edits wallpaper in
     * the official ThemeManager UI).
     *
     * Wallpaper files are copied into the App's external files dir (system_server
     * holds MANAGE_EXTERNAL_STORAGE), so the App can preview them and system_server
     * can re-apply them later via WallpaperController.
     */
    private fun captureWallpaperSnapshot(context: Context, intent: Intent) {
        @Suppress("DEPRECATION")
        val resultReceiver = intent.getParcelableExtra<ResultReceiver>(Protocol.EXTRA_RESULT_RECEIVER)
        if (resultReceiver == null) {
            log("CAPTURE_WALLPAPER_SNAPSHOT without ResultReceiver")
            return
        }
        val modeId = intent.getStringExtra(Protocol.EXTRA_MODE_ID) ?: "mode"
        // 预览快照写入共享目录，避免覆盖已保存的模式壁纸文件
        val previewOnly = intent.getBooleanExtra(Protocol.EXTRA_PREVIEW_ONLY, false)
        val userId = Process.myUid() / 100000
        val systemDir = File(Environment.getDataDirectory(), "system/users/$userId")

        // App external files dir: /sdcard/Android/data/com.banana.hypermodes/files/wallpapers/
        val appWallpaperRoot = File(
            File(Environment.getExternalStorageDirectory(), "Android/data"),
            Protocol.MODULE_PACKAGE + "/files/wallpapers"
        )
        val targetDir = if (previewOnly) {
            File(appWallpaperRoot, "preview")
        } else {
            File(appWallpaperRoot, modeId)
        }
        targetDir.mkdirs()

        val bundle = Bundle()
        try {
            // 1. 锁屏样式 JSON（Settings.Secure）
            val resolver = context.contentResolver
            bundle.putString(
                Protocol.EXTRA_LOCKSCREEN_JSON,
                Settings.Secure.getString(resolver, "constant_lockscreen_info")
            )
            bundle.putString(
                Protocol.EXTRA_TEMPLATE_EDITOR_JSON,
                Settings.Secure.getString(resolver, "constant_template_editor_info")
            )
            bundle.putString(
                Protocol.EXTRA_DEFAULT_LOCKSCREEN_JSON,
                Settings.Secure.getString(resolver, "miui_15_default_lockscreen_info")
            )
            bundle.putInt(
                Protocol.EXTRA_LOCKSCREEN_VERSION,
                Settings.Secure.getInt(resolver, "lockscreen_info_version", 3)
            )

            // 2. 锁屏壁纸源图
            val lockOrig = File(systemDir, "wallpaper_lock_orig")
            if (lockOrig.exists()) {
                val dest = File(targetDir, "lock_wallpaper")
                lockOrig.copyTo(dest, overwrite = true)
                bundle.putString(Protocol.EXTRA_LOCK_IMAGE_PATH, dest.absolutePath)
            }

            // 3. 桌面壁纸源图
            val desktopOrig = File(systemDir, "wallpaper_orig")
            if (desktopOrig.exists()) {
                val dest = File(targetDir, "desktop_wallpaper")
                desktopOrig.copyTo(dest, overwrite = true)
                bundle.putString(Protocol.EXTRA_DESKTOP_IMAGE_PATH, dest.absolutePath)
            }

            // 4. 桌面滚动/特效键
            bundle.putBoolean(
                Protocol.EXTRA_DESKTOP_SCROLL_ENABLED,
                Settings.Secure.getInt(resolver, "pref_key_wallpaper_screen_scrolled_span", -1) == 1
            )
            bundle.putInt(
                Protocol.EXTRA_WALLPAPER_EFFECT_TYPE,
                Settings.Secure.getInt(resolver, "wallpaper_effect_type_1", 0)
            )
            bundle.putString(
                Protocol.EXTRA_WALLPAPER_CHANGED,
                Settings.Secure.getString(resolver, "wallpaper_changed_2")
            )
            log("CAPTURE_WALLPAPER_SNAPSHOT: mode=$modeId preview=$previewOnly dir=${targetDir.absolutePath} lock=${lockOrig.exists()} desktop=${desktopOrig.exists()}")
        } catch (t: Throwable) {
            log("CAPTURE_WALLPAPER_SNAPSHOT failed: ${t.message}")
        }
        resultReceiver.send(0, bundle)
    }

    /** Original bypass flag per "pkg/channelId", captured before our first
     *  override so clearing restores rather than blindly writing false. */
    private val originalBypass = mutableMapOf<String, Boolean>()

    private fun setPackagesSuspended(packages: List<String>, suspended: Boolean) {
        try {
            val ipm = binder("package", "android.content.pm.IPackageManager\$Stub")
            if (ipm == null) {
                log("setPackagesSuspended: package binder not available")
                return
            }

            val method = ipm.javaClass.methods.first {
                it.name == "setPackagesSuspendedAsUser"
            }

            // Android 16 signature (verified):
            // setPackagesSuspendedAsUser(String[] packageNames, boolean suspended,
            //     PersistableBundle appExtras, PersistableBundle launcherExtras,
            //     SuspendDialogInfo dialogInfo, int flags, String suspendingPackage,
            //     int suspendingUserId, int targetUserId)
            val params = method.parameterTypes
            if (params.size < 9) {
                log("setPackagesSuspendedAsUser signature mismatch: expected >=9 params, got ${params.size}")
                return
            }

            val args = arrayOfNulls<Any>(params.size)
            args[0] = packages.toTypedArray()  // String[] packageNames
            args[1] = suspended                 // boolean suspended
            args[2] = null                      // PersistableBundle appExtras
            args[3] = null                      // PersistableBundle launcherExtras
            args[4] = null                      // SuspendDialogInfo dialogInfo
            args[5] = 0                         // int flags
            args[6] = Protocol.MODULE_PACKAGE   // String suspendingPackage
            args[7] = 0                         // int suspendingUserId
            args[8] = 0                         // int targetUserId

            method.invoke(ipm, *args)
            log("setPackagesSuspended($suspended): ${packages.joinToString()}")
        } catch (t: Throwable) {
            log("setPackagesSuspended failed: $t")
        }
    }

    private fun setChannelsBypassDnd(context: Context, packages: List<String>, bypass: Boolean) {
        val inm = try {
            binder("notification", "android.app.INotificationManager\$Stub")
        } catch (t: Throwable) {
            log("notification binder unavailable: $t")
            return
        }

        if (inm == null) {
            log("notification binder returned null")
            return
        }

        val getChannels = inm.javaClass.methods.firstOrNull {
            it.name == "getNotificationChannelsForPackage"
        }
        val updateChannel = inm.javaClass.methods.firstOrNull {
            it.name == "updateNotificationChannelForPackage"
        }
        if (getChannels == null || updateChannel == null) {
            log("channel methods not found on INotificationManager")
            return
        }
        for (pkg in packages) {
            try {
                val uid = context.packageManager.getPackageUid(pkg, 0)
                // (String pkg, int uid, boolean includeDeleted)
                val slice = getChannels.invoke(inm, pkg, uid, false)
                val channels = slice.javaClass.getMethod("getList")
                    .invoke(slice) as List<*>
                for (ch in channels) {
                    if (ch !is NotificationChannel) continue
                    val key = "$pkg/${ch.id}"
                    if (bypass) {
                        originalBypass.putIfAbsent(key, ch.canBypassDnd())
                        ch.setBypassDnd(true)
                    } else {
                        ch.setBypassDnd(originalBypass.remove(key) ?: false)
                    }
                    updateChannel.invoke(inm, pkg, uid, ch)
                }
                log("bypassDnd($bypass): $pkg (${channels.size} channels)")
            } catch (t: Throwable) {
                log("bypassDnd failed for $pkg: $t")
            }
        }
    }

    /** ServiceManager.getService(name) + Stub.asInterface(binder), reflectively. */
    private fun binder(service: String, stubClass: String): Any? {
        return try {
            val binder = Class.forName("android.os.ServiceManager")
                .getMethod("getService", String::class.java)
                .invoke(null, service) as? IBinder

            if (binder == null) {
                log("$service binder not found (service not started?)")
                return null
            }

            val result = Class.forName(stubClass)
                .getMethod("asInterface", IBinder::class.java)
                .invoke(null, binder)

            if (result == null) {
                log("$stubClass.asInterface returned null")
                return null
            }

            result
        } catch (t: Throwable) {
            log("binder($service, $stubClass) failed: ${t.message}")
            null
        }
    }

    private fun registerPackageLifecycleReceiver(context: Context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val action = PackageLifecyclePolicy.classify(
                    intent, Protocol.MODULE_PACKAGE
                )
                
                log("Package event: ${intent.action}, classified: $action")
                
                val engine = RoutineCoreEngine.getInstance()
                when (action) {
                    PackageLifecyclePolicy.Action.REMOVE -> {
                        log("Package removal detected, shutting down engine")
                        engine.shutdownForPackageRemoval()
                    }
                    PackageLifecyclePolicy.Action.REPLACEMENT_STARTED -> {
                        log("Package replacement started")
                        engine.setLifecycleState(RoutineCoreEngine.LifecycleState.REPLACING)
                    }
                    PackageLifecyclePolicy.Action.REPLACEMENT_FINISHED -> {
                        log("Package replacement finished")
                        engine.setLifecycleState(RoutineCoreEngine.LifecycleState.RUNNING)
                        // An update may declare new runtime permissions — re-grant.
                        permissionHook?.grantRuntimePermissions()
                    }
                    PackageLifecyclePolicy.Action.INSTALL -> {
                        log("Fresh installation detected, resetting engine to RUNNING")
                        // Use a transition that ensures we reload config
                        engine.setLifecycleState(RoutineCoreEngine.LifecycleState.RUNNING)
                        // A fresh install starts with every runtime permission
                        // denied — grant them without waiting for a reboot.
                        permissionHook?.grantRuntimePermissions()
                    }
                    else -> {}
                }
            }
        }
        
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_PACKAGE_REMOVED)
            addAction(Intent.ACTION_PACKAGE_FULLY_REMOVED)
            addAction(Intent.ACTION_PACKAGE_ADDED)
            addDataScheme("package")
        }
        
        context.registerReceiver(receiver, filter)
        log("Package lifecycle receiver registered in system_server")
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val AMS = "com.android.server.am.ActivityManagerService"
    }
}
