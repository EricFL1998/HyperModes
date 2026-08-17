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
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import com.banana.hypermodes.protocol.PackageLifecyclePolicy
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.SystemAutomationEngine
import com.banana.hypermodes.systemserver.config.WallpaperItemConfig
import com.banana.hypermodes.systemserver.executor.HotspotController
import com.banana.hypermodes.systemserver.executor.SystemOpsExecutor
import com.banana.hypermodes.systemserver.executor.WallpaperController
import com.banana.hypermodes.systemserver.hooks.UniversalPermissionHook
import com.banana.hypermodes.systemserver.trigger.PolarisGeofenceProbe
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

    /** 零进程自动化触发引擎（system_server 内运行）。 */
    private var automationEngine: SystemAutomationEngine? = null

    fun install(classLoader: ClassLoader) {
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
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

        // 零进程自动化触发引擎：应用被杀后触发依然生效
        try {
            automationEngine = SystemAutomationEngine(context, classLoader)
            automationEngine?.init()
            log("SystemAutomationEngine initialized")
        } catch (t: Throwable) {
            log("SystemAutomationEngine initialization failed: $t")
        }
    }

    private fun registerBridge(context: Context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                // Actions without EXTRA_PACKAGES — handle before the extraction below.
                when (intent.action) {
                    Protocol.ACTION_GET_CONFIGURED_WIFI -> {
                        sendConfiguredWifi(c, intent)
                        return
                    }
                    Protocol.ACTION_SET_HOTSPOT_ENABLED -> {
                        setHotspotEnabled(c, intent)
                        return
                    }
                    Protocol.ACTION_SYSTEM_OP -> {
                        handleSystemOp(c, intent)
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
                    Protocol.ACTION_PREPARE_WALLPAPER_EDIT -> {
                        prepareWallpaperForEdit(c, intent)
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
            addAction(Protocol.ACTION_SET_HOTSPOT_ENABLED)
            addAction(Protocol.ACTION_SYSTEM_OP)
            addAction(Protocol.ACTION_PROBE_POLARIS)
            addAction(Protocol.ACTION_CAPTURE_WALLPAPER_SNAPSHOT)
            addAction(Protocol.ACTION_PREPARE_WALLPAPER_EDIT)
        }
        context.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        log("mode bridge receiver registered in system_server")
    }

    /**
     * 把模式已保存的单个壁纸子项预置到系统，供官方编辑器作为起始样式。
     * setStream 会阻塞到裁剪重建完成，放到后台线程执行，避免卡 system_server 主线程；
     * 完成后通过 ResultReceiver 通知 App 再打开编辑器。
     */
    private fun prepareWallpaperForEdit(context: Context, intent: Intent) {
        val resultReceiver =
            intent.getParcelableExtra(Protocol.EXTRA_RESULT_RECEIVER, ResultReceiver::class.java)
        val which = intent.getIntExtra(Protocol.EXTRA_WHICH, 2)
        log("PREPARE_WALLPAPER_EDIT received: which=$which")
        Thread {
            try {
                val item = WallpaperItemConfig(
                    imagePath = when (which) {
                        2 -> intent.getStringExtra(Protocol.EXTRA_LOCK_IMAGE_PATH)
                        else -> intent.getStringExtra(Protocol.EXTRA_DESKTOP_IMAGE_PATH)
                    },
                    sysImagePath = when (which) {
                        2 -> intent.getStringExtra(Protocol.EXTRA_LOCK_SYS_IMAGE_PATH)
                        else -> intent.getStringExtra(Protocol.EXTRA_DESKTOP_SYS_IMAGE_PATH)
                    },
                    sysSubjectMaskPath = intent.getStringExtra(Protocol.EXTRA_SUBJECT_MASK_SYS_PATH),
                    lockscreenJson = intent.getStringExtra(Protocol.EXTRA_LOCKSCREEN_JSON),
                    templateEditorJson = intent.getStringExtra(Protocol.EXTRA_TEMPLATE_EDITOR_JSON),
                    scrollEnabled = if (intent.hasExtra(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED)) {
                        intent.getBooleanExtra(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED, false)
                    } else null,
                    effectType = if (intent.hasExtra(
                            if (which == 2) Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE
                            else Protocol.EXTRA_WALLPAPER_EFFECT_TYPE
                        )) {
                        intent.getIntExtra(
                            if (which == 2) Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE
                            else Protocol.EXTRA_WALLPAPER_EFFECT_TYPE,
                            0
                        )
                    } else null,
                    which = which
                )
                log("PREPARE_WALLPAPER_EDIT: calling prepareForEdit which=$which imagePath=${item.imagePath} sysImagePath=${item.sysImagePath}")
                WallpaperController(context).prepareForEdit(item)
                log("PREPARE_WALLPAPER_EDIT: prepareForEdit done which=$which")
                resultReceiver?.send(0, Bundle())
            } catch (t: Throwable) {
                log("PREPARE_WALLPAPER_EDIT failed: ${t.message}")
                resultReceiver?.send(1, Bundle())
            }
        }.start()
    }

    /**
     * Return the device's saved WiFi SSIDs to the picker via ResultReceiver.
     * Regular apps lost WifiManager.getConfiguredNetworks() in Android 10,
     * but system_server still qualifies.
     */
    private fun sendConfiguredWifi(context: Context, intent: Intent) {
        val resultReceiver =
            intent.getParcelableExtra(Protocol.EXTRA_RESULT_RECEIVER, ResultReceiver::class.java)
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
     * 开关个人热点：system_server 内直接调 WifiManager 的系统 API
     * （startTetheredHotspot / stopSoftAp），等价于系统设置里 flip switch。
     * 热点开启/关闭是异步的，这里拿到调用成功即返回。
     */
    private fun setHotspotEnabled(context: Context, intent: Intent) {
        val enabled = intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false)
        val ok = try {
            HotspotController(context).setHotspotEnabled(enabled)
        } catch (t: Throwable) {
            log("SET_HOTSPOT_ENABLED failed: $t")
            false
        }
        log("SET_HOTSPOT_ENABLED($enabled) -> $ok")
    }

    /**
     * 通用特权操作分发：把 ACTION_SYSTEM_OP 广播转给 SystemOpsExecutor，
     * 由 system_server 用系统 API 执行（写 Settings / 飞行模式 / 手电筒等）。
     */
    private fun handleSystemOp(context: Context, intent: Intent) {
        val executor = SystemOpsExecutor(context)
        val ok = try {
            when (intent.getStringExtra(Protocol.EXTRA_OP)) {
                Protocol.OP_WRITE_SETTING -> executor.writeSetting(
                    intent.getStringExtra(Protocol.EXTRA_NAMESPACE) ?: "",
                    intent.getStringExtra(Protocol.EXTRA_KEY) ?: "",
                    intent.getStringExtra(Protocol.EXTRA_VALUE) ?: ""
                )
                Protocol.OP_SET_AIRPLANE_ENABLED ->
                    executor.setAirplaneEnabled(intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false))
                Protocol.OP_SET_MOBILE_DATA_ENABLED ->
                    executor.setMobileDataEnabled(intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false))
                Protocol.OP_SET_FLASHLIGHT_ENABLED ->
                    executor.setFlashlightEnabled(intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false))
                Protocol.OP_SET_PREFERRED_SIM_SLOT ->
                    executor.setPreferredSimSlot(intent.getIntExtra(Protocol.EXTRA_SLOT, 0))
                Protocol.OP_SET_MOTION_SICKNESS_RELIEF ->
                    executor.setMotionSicknessReliefEnabled(intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false))
                Protocol.OP_SET_WIFI_ENABLED ->
                    executor.setWifiEnabled(intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false))
                Protocol.OP_SET_BLUETOOTH_ENABLED ->
                    executor.setBluetoothEnabled(intent.getBooleanExtra(Protocol.EXTRA_ENABLED, false))
                else -> {
                    log("SYSTEM_OP unknown op: ${intent.getStringExtra(Protocol.EXTRA_OP)}")
                    false
                }
            }
        } catch (t: Throwable) {
            log("SYSTEM_OP failed: $t")
            false
        }
        log("SYSTEM_OP(${intent.getStringExtra(Protocol.EXTRA_OP)}) -> $ok")
    }

    /**
     * Probe Xiaomi Polaris geofencing capability and return structured result.
     * This is a fail-closed gate: location triggers are NOT implemented unless
     * the probe confirms Polaris is available and allows non-SecurityCenter callers.
     */
    private fun probePolaris(context: Context, intent: Intent) {
        val resultReceiver =
            intent.getParcelableExtra(Protocol.EXTRA_RESULT_RECEIVER, ResultReceiver::class.java)
        if (resultReceiver == null) {
            log("PROBE_POLARIS without ResultReceiver")
            return
        }

        log("PROBE_POLARIS: starting capability detection")
        val adapter = PolarisGeofenceProbe(context)
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
        val resultReceiver =
            intent.getParcelableExtra(Protocol.EXTRA_RESULT_RECEIVER, ResultReceiver::class.java)
        if (resultReceiver == null) {
            log("CAPTURE_WALLPAPER_SNAPSHOT without ResultReceiver")
            return
        }
        val modeId = intent.getStringExtra(Protocol.EXTRA_MODE_ID) ?: "mode"
        // 预览快照写入共享目录，避免覆盖已保存的模式壁纸文件
        val previewOnly = intent.getBooleanExtra(Protocol.EXTRA_PREVIEW_ONLY, false)
        val userId = Process.myUid() / 100000
        val systemDir = File(Environment.getDataDirectory(), "system/users/$userId")

        val bundle = Bundle()
        try {
            // 1. 锁屏样式 JSON（Settings.Secure）。
            //    设备实测 constant_lockscreen_info 为 null，真实数据在
            //    constant_template_editor_info（{"homeInfo":...,"lockscreenInfo":{...}}），
            //    提取 lockscreenInfo 子对象作为锁屏样式返回。
            val resolver = context.contentResolver
            val editorInfo = Settings.Secure.getString(resolver, "constant_template_editor_info")
            val lockscreenInfo = editorInfo?.let { extractLockscreenInfo(it) }
            if (lockscreenInfo != null) {
                bundle.putString(Protocol.EXTRA_LOCKSCREEN_JSON, lockscreenInfo)
            } else {
                bundle.putString(
                    Protocol.EXTRA_LOCKSCREEN_JSON,
                    Settings.Secure.getString(resolver, "constant_lockscreen_info")
                )
            }
            bundle.putString(
                Protocol.EXTRA_TEMPLATE_EDITOR_JSON,
                editorInfo
            )
            bundle.putString(
                Protocol.EXTRA_DEFAULT_LOCKSCREEN_JSON,
                Settings.Secure.getString(resolver, "miui_15_default_lockscreen_info")
            )
            bundle.putInt(
                Protocol.EXTRA_LOCKSCREEN_VERSION,
                Settings.Secure.getInt(resolver, "lockscreen_info_version", 3)
            )

            // 2/3. 壁纸：预览走降采样 JPEG 返回 App 显示；恢复源则原样拷贝系统
            //      源图（全分辨率、保持原格式）到 system 可读目录
            //      （/data/system/hypermodes_backup/modes/），模式应用/复原时
            //      WallpaperController 从该路径 setStream，避免二次有损压缩。
            val sysModeDir = File("/data/system/hypermodes_backup/modes", modeId)
            sysModeDir.mkdirs()
            // 锁屏源图：无独立锁屏壁纸（跟随桌面）时回退到桌面源图，
            // 保证捕获始终有锁屏壁纸字节，保存后预览不会丢失锁屏图。
            val lockOrig = File(systemDir, "wallpaper_lock_orig")
                .takeIf { it.exists() } ?: File(systemDir, "wallpaper_orig")
            if (lockOrig.exists()) {
                bundle.putByteArray(Protocol.EXTRA_LOCK_IMAGE_BYTES, encodePreview(lockOrig))
                val sysFile = File(sysModeDir, "lock_wallpaper.jpg")
                copyFileIfChanged(lockOrig, sysFile)
                bundle.putString(Protocol.EXTRA_LOCK_SYS_IMAGE_PATH, sysFile.absolutePath)
            }
            val desktopOrig = File(systemDir, "wallpaper_orig")
            if (desktopOrig.exists()) {
                bundle.putByteArray(Protocol.EXTRA_DESKTOP_IMAGE_BYTES, encodePreview(desktopOrig))
                val sysFile = File(sysModeDir, "desktop_wallpaper.jpg")
                copyFileIfChanged(desktopOrig, sysFile)
                bundle.putString(Protocol.EXTRA_DESKTOP_SYS_IMAGE_PATH, sysFile.absolutePath)
            }

            // 2b. 锁屏壁纸主体蒙版（景深效果）。路径在 lockscreenInfo.wallpaperInfo.subject，
            //     从模板目录读取并压缩传回；不存在则跳过（预览无景深层）。
            val subjectMask = resolveSubjectMask(lockscreenInfo)
            if (subjectMask != null) {
                // 蒙版是灰度/alpha 图，JPEG 有损压缩会破坏景深边缘与灰度精度，
                // 必须用 PNG 无损编码；尺寸也保持原始（PNG 对黑白蒙版压缩率高）。
                val bytes = encodeMaskPng(subjectMask)
                bundle.putByteArray(Protocol.EXTRA_SUBJECT_MASK_BYTES, bytes)
                if (bytes != null) {
                    val sysFile = File(sysModeDir, "subject_mask.png")
                    writeIfChanged(sysFile, bytes)
                    bundle.putString(Protocol.EXTRA_SUBJECT_MASK_SYS_PATH, sysFile.absolutePath)
                }
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
            bundle.putInt(
                Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE,
                Settings.Secure.getInt(resolver, "wallpaper_effect_type_2", 0)
            )
            bundle.putString(
                Protocol.EXTRA_WALLPAPER_CHANGED,
                Settings.Secure.getString(resolver, "wallpaper_changed_2")
            )
            log("CAPTURE_WALLPAPER_SNAPSHOT: mode=$modeId preview=$previewOnly dir=${sysModeDir.absolutePath} lock=${lockOrig.exists()} desktop=${desktopOrig.exists()}")
        } catch (t: Throwable) {
            log("CAPTURE_WALLPAPER_SNAPSHOT failed: ${t.message}")
        }
        resultReceiver.send(0, bundle)
    }

    /** 从 lockscreenInfo JSON 解析 wallpaperInfo.subject 路径（主体蒙版文件）。 */
    private fun resolveSubjectMask(lockscreenInfo: String?): File? {
        if (lockscreenInfo.isNullOrEmpty()) return null
        return runCatching {
            val root = org.json.JSONObject(lockscreenInfo)
            val wallpaperInfo = root.optJSONObject("wallpaperInfo") ?: return null
            // 样式声明不支持主体（无景深）时不读取残留的 subject_mask，
            // 避免预览/保存把旧蒙版当成当前样式的景深。
            if (!wallpaperInfo.optBoolean("supportSubject", false)) return null
            val subject = wallpaperInfo.optString("subject").takeIf { it.isNotEmpty() }
                ?: return null
            val f = File(subject)
            f.takeIf { it.exists() && it.isFile }
        }.getOrNull()
    }

    /** 从 constant_template_editor_info 提取 lockscreenInfo 子对象 JSON。 */
    private fun extractLockscreenInfo(editorInfo: String): String? {
        return try {
            val root = org.json.JSONObject(editorInfo)
            root.optJSONObject("lockscreenInfo")?.toString()
        } catch (t: Throwable) {
            null
        }
    }

    /** 解码壁纸源图，等比缩放到预览尺寸，JPEG 压缩成字节。
     *  两张图 + JSON 一起走 ResultReceiver，Binder 单次事务约 1MB，
     *  所以尺寸/质量都要保守，避免 TransactionTooLargeException。 */
    private fun encodePreview(file: File): ByteArray? {
        return runCatching {
            val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(file.absolutePath, bounds)
            val maxSide = 640
            var sample = 1
            while (Math.max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) {
                sample *= 2
            }
            val opts = BitmapFactory.Options().apply { inSampleSize = sample }
            val bmp = BitmapFactory.decodeFile(file.absolutePath, opts) ?: return@runCatching null
            val out = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.JPEG, 75, out)
            bmp.recycle()
            out.toByteArray()
        }.getOrNull()
    }

    /** 蒙版无损 PNG 编码：不做 JPEG 压缩，避免破坏景深灰度/边缘。 */
    private fun encodeMaskPng(file: File): ByteArray? {
        return runCatching {
            val bmp = BitmapFactory.decodeFile(file.absolutePath) ?: return@runCatching null
            val out = ByteArrayOutputStream()
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out)
            bmp.recycle()
            out.toByteArray()
        }.getOrNull()
    }

    /**
     * 仅当内容变化时写文件。编辑会话只改样式不改壁纸时，重新编码的字节
     * 与上次捕获完全一致（同源图 + 同参数确定性压缩），跳过覆盖可保持
     * sysImagePath 内容不变，WallpaperController.apply 端据此跳过 setStream，
     * 避免"每次切换样式都算新壁纸"导致最近使用历史无限增长。
     */
    private fun writeIfChanged(file: File, bytes: ByteArray) {
        val same = file.exists() && runCatching {
            file.readBytes().contentEquals(bytes)
        }.getOrDefault(false)
        if (!same) {
            file.writeBytes(bytes)
        }
    }

    /** 原样拷贝系统源图到恢复源（保持原格式/原分辨率），内容一致则跳过写入。 */
    private fun copyFileIfChanged(src: File, dst: File) {
        val same = dst.exists() && runCatching {
            src.length() == dst.length() && src.readBytes().contentEquals(dst.readBytes())
        }.getOrDefault(false)
        if (same) return
        dst.parentFile?.mkdirs()
        src.inputStream().use { input ->
            dst.outputStream().use { output -> input.copyTo(output) }
        }
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

            val method = ipm.javaClass.getMethod(
                "setPackagesSuspendedAsUser",
                arrayOf<String>().javaClass,
                Boolean::class.javaPrimitiveType!!,
                android.os.PersistableBundle::class.java,
                android.os.PersistableBundle::class.java,
                Class.forName(SUSPEND_DIALOG_INFO, false, ipm.javaClass.classLoader),
                Int::class.javaPrimitiveType!!,
                String::class.java,
                Int::class.javaPrimitiveType!!,
                Int::class.javaPrimitiveType!!
            )

            // Android 17 signature (verified from the OS4 framework):
            // setPackagesSuspendedAsUser(String[] packageNames, boolean suspended,
            //     PersistableBundle appExtras, PersistableBundle launcherExtras,
            //     SuspendDialogInfo dialogInfo, int flags, String suspendingPackage,
            //     int suspendingUserId, int targetUserId)
            val args = arrayOfNulls<Any>(9)
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

        val getChannels: java.lang.reflect.Method
        val updateChannel: java.lang.reflect.Method
        try {
            getChannels = inm.javaClass.getMethod(
                "getNotificationChannelsForPackage",
                String::class.java,
                Int::class.javaPrimitiveType!!,
                Boolean::class.javaPrimitiveType!!
            )
            updateChannel = inm.javaClass.getMethod(
                "updateNotificationChannelForPackage",
                String::class.java,
                Int::class.javaPrimitiveType!!,
                NotificationChannel::class.java
            )
        } catch (t: Throwable) {
            log("OS4 notification channel signatures not found: $t")
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
        private const val TIMINGS_TRACE_AND_SLOG =
            "com.android.server.utils.TimingsTraceAndSlog"
        private const val SUSPEND_DIALOG_INFO =
            "android.content.pm.SuspendDialogInfo"
    }
}
