package com.banana.hypermodes.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.protocol.Protocol
import java.io.File

/**
 * Ask the system_server bridge to capture the current lock-screen style JSON +
 * wallpaper bytes. system_server returns JPEG-compressed wallpaper bytes (writing
 * into the app's external dir from another process is blocked by scoped storage),
 * and the App persists them into its own files dir. Used after the user edits
 * wallpaper in the official ThemeManager UI, so HyperModes can store the set.
 *
 * onResult runs on the main thread exactly once; null means the bridge is
 * unavailable (module disabled or an older build).
 */
object WallpaperSnapshotBridge {

    private fun previewDir(context: Context): File =
        File(File(context.filesDir, "wallpapers"), "preview")

    /**
     * 同步读取上次缓存的系统壁纸预览（无跨进程等待），
     * 用于详情页进入时立即显示，随后再后台刷新。
     */
    fun readCachedCurrent(context: Context): WallpaperSet? {
        val dir = previewDir(context)
        val lockFile = File(dir, "lock_wallpaper.jpg")
        val desktopFile = File(dir, "desktop_wallpaper.jpg")
        val lockJsonFile = File(dir, "lockscreen.json")
        val templateEditorFile = File(dir, "template_editor.json")
        val subjectMaskFile = File(dir, "subject_mask.png")
        val lockEffectFile = File(dir, "effect_type_2.txt")
        val desktopEffectFile = File(dir, "effect_type_1.txt")
        if (!lockFile.exists() && !desktopFile.exists()) return null
        // system_server 侧预览目录（captureCurrent 时会同步落盘一份）。App 无法直接 stat
        // /data/system，但预先填上该路径即可：恢复/预置编辑时 system_server 会自行校验
        // 存在性。否则 sysImagePath 为 null 会回退到 App 私有目录，uid 1000 读不到，
        // 导致壁纸图恢复被静默跳过（真实壁纸残留的根因之一）。
        val sysPreviewDir = File("/data/system/hypermodes_backup/modes/preview")
        return WallpaperSet(
            lock = if (lockFile.exists()) {
                WallpaperItem(
                    imagePath = lockFile.absolutePath,
                    sysImagePath = File(sysPreviewDir, "lock_wallpaper.jpg").absolutePath,
                    sysSubjectMaskPath = if (subjectMaskFile.exists()) {
                        File(sysPreviewDir, "subject_mask.png").absolutePath
                    } else null,
                    lockscreenJson = runCatching { lockJsonFile.takeIf { it.exists() }?.readText() }.getOrNull(),
                    templateEditorJson = runCatching {
                        templateEditorFile.takeIf { it.exists() }?.readText()
                    }.getOrNull(),
                    subjectMaskPath = subjectMaskFile.takeIf { it.exists() }?.absolutePath,
                    effectType = lockEffectFile.takeIf { it.exists() }?.readText()?.toIntOrNull(),
                    which = 2
                )
            } else null,
            desktop = if (desktopFile.exists()) {
                WallpaperItem(
                    imagePath = desktopFile.absolutePath,
                    sysImagePath = File(sysPreviewDir, "desktop_wallpaper.jpg").absolutePath,
                    effectType = desktopEffectFile.takeIf { it.exists() }?.readText()?.toIntOrNull(),
                    which = 1
                )
            } else null
        )
    }

    /**
     * Capture the current lock-screen style JSON + wallpaper files into a shared
     * preview dir (never overwrites a mode's saved wallpaper). Used when entering
     * the detail page so unconfigured previews show the real system wallpaper
     * and lock-screen style.
     */
    fun captureCurrent(context: Context, onResult: (WallpaperSet?) -> Unit) {
        captureInternal(context, modeId = "preview", previewOnly = true, onResult)
    }

    /** Capture into a mode-specific dir; used after the user edits wallpaper in
     *  the official UI so the resulting set can be stored in the mode. */
    fun capture(context: Context, modeId: String, onResult: (WallpaperSet?) -> Unit) {
        captureInternal(context, modeId = modeId, previewOnly = false, onResult)
    }

    /**
     * 打开官方编辑器前，把模式已保存的单个子项（锁屏/桌面）预置到系统，
     * 让编辑器从保存的样式开始编辑而不是当前系统样式。
     *
     * @param item 模式已保存的 WallpaperItem（lock 或 desktop）
     * @param onDone 预置完成后回调（主线程）；true=成功（含超时失败）
     */
    fun prepareEdit(context: Context, item: WallpaperItem, onDone: (Boolean) -> Unit) {
        HyperLog.i("WallpaperSnapshotBridge", "prepareEdit: which=${item.which} imagePath=${item.imagePath} sysImagePath=${item.sysImagePath}")
        val handler = Handler(Looper.getMainLooper())
        var delivered = false
        val receiver = object : ResultReceiver(handler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                if (delivered) return
                delivered = true
                HyperLog.i("WallpaperSnapshotBridge", "prepareEdit result: which=${item.which} resultCode=$resultCode")
                onDone(resultCode == 0)
            }
        }
        val timeout = Runnable {
            if (delivered) return@Runnable
            delivered = true
            Log.w("WallpaperSnapshotBridge", "prepareEdit timeout: which=${item.which}")
            onDone(false)
        }
        handler.postDelayed(timeout, 10000)
        try {
            context.sendBroadcast(
                Intent(Protocol.ACTION_PREPARE_WALLPAPER_EDIT).apply {
                    setPackage(Protocol.FRAMEWORK_PACKAGE)
                    putExtra(Protocol.EXTRA_RESULT_RECEIVER, receiver)
                    putExtra(Protocol.EXTRA_WHICH, item.which)
                    when (item.which) {
                        2 -> putExtra(Protocol.EXTRA_LOCK_IMAGE_PATH, item.imagePath)
                        else -> putExtra(Protocol.EXTRA_DESKTOP_IMAGE_PATH, item.imagePath)
                    }
                    putExtra(
                        if (item.which == 2) Protocol.EXTRA_LOCK_SYS_IMAGE_PATH
                        else Protocol.EXTRA_DESKTOP_SYS_IMAGE_PATH,
                        item.sysImagePath
                    )
                    putExtra(Protocol.EXTRA_SUBJECT_MASK_SYS_PATH, item.sysSubjectMaskPath)
                    putExtra(Protocol.EXTRA_LOCKSCREEN_JSON, item.lockscreenJson)
                    putExtra(Protocol.EXTRA_TEMPLATE_EDITOR_JSON, item.templateEditorJson)
                    item.scrollEnabled?.let {
                        putExtra(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED, it)
                    }
                    item.effectType?.let {
                        putExtra(
                            if (item.which == 2) Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE
                            else Protocol.EXTRA_WALLPAPER_EFFECT_TYPE,
                            it
                        )
                    }
                }
            )
        } catch (t: Throwable) {
            handler.removeCallbacks(timeout)
            if (!delivered) {
                delivered = true
                onDone(false)
            }
        }
    }

    private fun captureInternal(
        context: Context,
        modeId: String,
        previewOnly: Boolean,
        onResult: (WallpaperSet?) -> Unit
    ) {
        HyperLog.i("WallpaperSnapshotBridge", "captureInternal: modeId=$modeId previewOnly=$previewOnly")
        val handler = Handler(Looper.getMainLooper())
        var delivered = false
        fun deliver(result: WallpaperSet?) {
            if (delivered) return
            delivered = true
            onResult(result)
        }
        val timeout = Runnable { deliver(null) }
        // 解码压缩壁纸 + 跨进程回传需要时间，放宽到 5s
        handler.postDelayed(timeout, 5000)

        val receiver = object : ResultReceiver(handler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                handler.removeCallbacks(timeout)
                deliver(parse(context, resultData, modeId, previewOnly))
            }
        }
        try {
            context.sendBroadcast(
                Intent(Protocol.ACTION_CAPTURE_WALLPAPER_SNAPSHOT).apply {
                    setPackage(Protocol.FRAMEWORK_PACKAGE)
                    putExtra(Protocol.EXTRA_MODE_ID, modeId)
                    putExtra(Protocol.EXTRA_PREVIEW_ONLY, previewOnly)
                    putExtra(Protocol.EXTRA_RESULT_RECEIVER, receiver)
                }
            )
        } catch (t: Throwable) {
            handler.removeCallbacks(timeout)
            deliver(null)
        }
    }

    private fun parse(
        context: Context,
        data: Bundle?,
        modeId: String,
        previewOnly: Boolean
    ): WallpaperSet? {
        if (data == null) return null
        if (previewOnly) {
            cacheLockscreenJson(context, data.getString(Protocol.EXTRA_LOCKSCREEN_JSON))
            cacheTemplateEditorJson(context, data.getString(Protocol.EXTRA_TEMPLATE_EDITOR_JSON))
            cacheEffectType(
                context,
                data.getInt(Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE, -1).takeIf { it != -1 },
                data.getInt(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE, -1).takeIf { it != -1 }
            )
        }
        val lockBytes = data.getByteArray(Protocol.EXTRA_LOCK_IMAGE_BYTES)
        val desktopBytes = data.getByteArray(Protocol.EXTRA_DESKTOP_IMAGE_BYTES)
        val subjectMaskBytes = data.getByteArray(Protocol.EXTRA_SUBJECT_MASK_BYTES)
        val subjectMaskPath = persistWallpaper(
            context,
            subjectMaskBytes,
            null,
            "subject_mask.png",
            modeId,
            previewOnly
        )
        val lockImage = persistWallpaper(
            context,
            lockBytes,
            data.getString(Protocol.EXTRA_LOCK_IMAGE_PATH),
            "lock_wallpaper.jpg",
            modeId,
            previewOnly
        )
        val desktopImage = persistWallpaper(
            context,
            desktopBytes,
            data.getString(Protocol.EXTRA_DESKTOP_IMAGE_PATH),
            "desktop_wallpaper.jpg",
            modeId,
            previewOnly
        )
        val lockJson = data.getString(Protocol.EXTRA_LOCKSCREEN_JSON)
        if (lockImage == null && desktopImage == null && lockJson == null) return null

        return WallpaperSet(
            lock = if (lockImage != null || lockJson != null) {
                WallpaperItem(
                    imagePath = lockImage,
                    sysImagePath = data.getString(Protocol.EXTRA_LOCK_SYS_IMAGE_PATH),
                    sysSubjectMaskPath = data.getString(Protocol.EXTRA_SUBJECT_MASK_SYS_PATH),
                    lockscreenJson = lockJson,
                    templateEditorJson = data.getString(Protocol.EXTRA_TEMPLATE_EDITOR_JSON),
                    subjectMaskPath = subjectMaskPath,
                    effectType = if (data.containsKey(Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE)) {
                        data.getInt(Protocol.EXTRA_LOCK_WALLPAPER_EFFECT_TYPE)
                    } else null,
                    which = 2
                )
            } else null,
            desktop = if (desktopImage != null) {
                WallpaperItem(
                    imagePath = desktopImage,
                    sysImagePath = data.getString(Protocol.EXTRA_DESKTOP_SYS_IMAGE_PATH),
                    scrollEnabled = if (data.containsKey(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED)) {
                        data.getBoolean(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED)
                    } else null,
                    effectType = if (data.containsKey(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE)) {
                        data.getInt(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE)
                    } else null,
                    which = 1
                )
            } else null
        )
    }

    /**
     * 编辑前预置锁屏 + 桌面两侧。官方编辑器打开时会同时读取两侧壁纸；
     * 只预置编辑侧会导致另一侧被重置为默认壁纸。
     *
     * @param which 用户要编辑的一侧：1 桌面 / 2 锁屏
     * @param set 包含当前系统两侧状态 + 编辑侧已保存配置的 WallpaperSet
     */
    fun prepareEditSet(
        context: Context,
        set: WallpaperSet,
        which: Int,
        onDone: (Boolean) -> Unit
    ) {
        HyperLog.i("WallpaperSnapshotBridge", "prepareEditSet: which=$which")
        val editedItem = if (which == 2) set.lock else set.desktop
        val otherItem = if (which == 2) set.desktop else set.lock
        var remaining = 0
        var failed = false
        val checkDone = { ok: Boolean ->
            if (!ok) failed = true
            remaining--
            if (remaining <= 0) {
                HyperLog.i("WallpaperSnapshotBridge", "prepareEditSet done: success=${!failed}")
                onDone(!failed)
            }
        }
        if (editedItem != null) {
            remaining++
            prepareEdit(context, editedItem) { ok -> checkDone(ok) }
        }
        // 另一侧即使未保存也要预置为当前系统值，防止编辑器把它重置为默认。
        if (otherItem != null) {
            remaining++
            prepareEdit(context, otherItem) { ok -> checkDone(ok) }
        }
        if (remaining == 0) {
            onDone(false)
        }
    }

    /** preview 模式缓存壁纸特效类型，供 readCachedCurrent 立即使用。 */
    private fun cacheEffectType(
        context: Context,
        lockEffectType: Int?,
        desktopEffectType: Int?
    ) {
        runCatching {
            val dir = previewDir(context)
            dir.mkdirs()
            lockEffectType?.let { File(dir, "effect_type_2.txt").writeText(it.toString()) }
            desktopEffectType?.let { File(dir, "effect_type_1.txt").writeText(it.toString()) }
        }
    }

    /** 优先用 system_server 返回的 JPEG 字节写入 App 自己的 files 目录；
     *  无字节时回退使用快照路径（旧版/兼容）。 */
    private fun persistWallpaper(
        context: Context,
        bytes: ByteArray?,
        fallbackPath: String?,
        fileName: String,
        modeId: String,
        previewOnly: Boolean
    ): String? {
        if (bytes != null && bytes.isNotEmpty()) {
            return runCatching {
                // 按 modeId 分目录：preview 走共享预览目录，模式保存走各自目录，
                // 避免不同模式的壁纸文件互相覆盖（加载时 imagePath 指向各自文件）。
                val dir = File(
                    File(context.filesDir, "wallpapers"),
                    if (previewOnly) "preview" else modeId
                )
                dir.mkdirs()
                val file = File(dir, fileName)
                file.writeBytes(bytes)
                file.absolutePath
            }.getOrNull()
        }
        return fallbackPath
    }

    /** preview 模式额外缓存锁屏 JSON，供 readCachedCurrent 立即恢复样式。 */
    private fun cacheLockscreenJson(context: Context, json: String?) {
        if (json.isNullOrEmpty()) return
        runCatching {
            val dir = previewDir(context)
            dir.mkdirs()
            File(dir, "lockscreen.json").writeText(json)
        }
    }

    /** preview 模式额外缓存完整 template_editor JSON（官方完整锁屏模板需要）。 */
    private fun cacheTemplateEditorJson(context: Context, json: String?) {
        if (json.isNullOrEmpty()) return
        runCatching {
            val dir = previewDir(context)
            dir.mkdirs()
            File(dir, "template_editor.json").writeText(json)
        }
    }
}
