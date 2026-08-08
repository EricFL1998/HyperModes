package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.os.Environment
import android.os.Process
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.WallpaperConfig
import com.banana.hypermodes.systemserver.config.WallpaperItemConfig
import java.io.File

/**
 * Controller for applying and reverting wallpaper + lockscreen-style sets.
 *
 * Runs inside system_server (uid 1000), so it can read/write
 * /data/system/users/{uid}/ files and Settings.Secure directly.
 *
 * 按子项惰性：lock / desktop 各自独立，哪个子项有数据就只处理哪个；
 * 无数据的子项不备份、不应用、不恢复，保持系统当前值。
 *
 * 机制（基于解包确认）：
 * - 锁屏样式 JSON 写回 Settings.Secure[constant_lockscreen_info] 与
 *   [constant_template_editor_info]，SystemUI 的观察者自动应用。
 * - 壁纸源图复制到 wallpaper_orig / wallpaper_lock_orig，服务端
 *   WallpaperObserver（FileObserver）自动重新生成裁剪并重绑组件。
 * - 进入模式前先备份当前值（"原始快照"），退出时用快照回写。
 */
class WallpaperController(private val context: Context) {

    private val userId: Int = Process.myUid() / 100000
    private val systemDir: File =
        File(Environment.getDataDirectory(), "system/users/$userId")

    // 快照存储根：/data/system/hypermodes_backup/{lock|desktop}/
    private val backupRoot: File = File("/data/system/hypermodes_backup")
    private val lockBackupDir: File get() = File(backupRoot, "lock")
    private val desktopBackupDir: File get() = File(backupRoot, "desktop")

    // 壁纸源图路径（WallpaperManagerService 实际监听的文件）
    private val lockOrigFile: File get() = File(systemDir, "wallpaper_lock_orig")
    private val desktopOrigFile: File get() = File(systemDir, "wallpaper_orig")

    /** 锁屏样式 JSON 键（SystemUI 观察者监听 constant_template_editor_info）。 */
    private val KEY_LOCKSCREEN_INFO = "constant_lockscreen_info"
    private val KEY_TEMPLATE_EDITOR_INFO = "constant_template_editor_info"
    private val KEY_DEFAULT_LOCKSCREEN_INFO = "miui_15_default_lockscreen_info"
    private val KEY_LOCKSCREEN_INFO_VERSION = "lockscreen_info_version"
    private val KEY_DESKTOP_SCROLL = "pref_key_wallpaper_screen_scrolled_span"
    private val KEY_WALLPAPER_EFFECT_1 = "wallpaper_effect_type_1"
    private val KEY_WALLPAPER_EFFECT_2 = "wallpaper_effect_type_2"
    private val KEY_WALLPAPER_CHANGED = "wallpaper_changed_2"

    /**
     * 应用模式的壁纸 set。无数据子项跳过（不备份、不应用）。
     */
    fun apply(wallpaper: WallpaperConfig?) {
        if (wallpaper == null) {
            log("apply: wallpaper config is null, skip")
            return
        }
        try {
            wallpaper.lock?.let { applyLock(it) }
            wallpaper.desktop?.let { applyDesktop(it) }
        } catch (e: Exception) {
            log("apply: failed: ${e.message}")
        }
    }

    /**
     * 恢复原始锁屏/桌面。仅回写有备份的子项（备份只在本控制器 apply 时创建）。
     */
    fun restore() {
        try {
            if (lockBackupDir.exists()) restoreLock()
            if (desktopBackupDir.exists()) restoreDesktop()
        } catch (e: Exception) {
            log("restore: failed: ${e.message}")
        }
    }

    /** 是否有任何子项被应用过（用于退出模式时判断是否需要 restore）。 */
    fun hasApplied(): Boolean = lockBackupDir.exists() || desktopBackupDir.exists()

    // ---- 锁屏子项 ----

    private fun applyLock(item: WallpaperItemConfig) {
        // 1. 备份当前锁屏样式 JSON（仅首次）
        backupLockJsonOnce()

        // 2. 写回锁屏样式 JSON（触发 SystemUI 观察者）
        if (!item.lockscreenJson.isNullOrEmpty()) {
            putSecure(KEY_LOCKSCREEN_INFO, item.lockscreenJson)
            putSecure(
                KEY_TEMPLATE_EDITOR_INFO,
                item.templateEditorJson
                    ?: "{\"lockscreenInfo\":${item.lockscreenJson}}"
            )
        }

        // 3. 复制锁屏壁纸源图（触发 FileObserver 重绑组件）
        if (!item.imagePath.isNullOrEmpty()) {
            backupFileOnce(lockOrigFile, lockBackupDir)
            copyFile(File(item.imagePath), lockOrigFile)
            log("apply lock: wrote wallpaper to $lockOrigFile")
        }
        log("apply lock: done (json=${!item.lockscreenJson.isNullOrEmpty()}, image=${!item.imagePath.isNullOrEmpty()})")
    }

    private fun restoreLock() {
        // 1. 恢复锁屏样式 JSON
        File(lockBackupDir, KEY_LOCKSCREEN_INFO).takeIf { it.exists() }?.let { f ->
            putSecure(KEY_LOCKSCREEN_INFO, f.readText())
        }
        File(lockBackupDir, KEY_TEMPLATE_EDITOR_INFO).takeIf { it.exists() }?.let { f ->
            putSecure(KEY_TEMPLATE_EDITOR_INFO, f.readText())
        }
        File(lockBackupDir, KEY_DEFAULT_LOCKSCREEN_INFO).takeIf { it.exists() }?.let { f ->
            putSecure(KEY_DEFAULT_LOCKSCREEN_INFO, f.readText())
        }
        File(lockBackupDir, KEY_LOCKSCREEN_INFO_VERSION).takeIf { it.exists() }?.let { f ->
            putSecureInt(KEY_LOCKSCREEN_INFO_VERSION, f.readText().toIntOrNull() ?: 3)
        }

        // 2. 恢复锁屏壁纸源图
        File(lockBackupDir, lockOrigFile.name).takeIf { it.exists() }?.let { backup ->
            copyFile(backup, lockOrigFile)
            log("restore lock: restored wallpaper from backup")
        }
        log("restore lock: done")
    }

    private fun backupLockJsonOnce() {
        lockBackupDir.mkdirs()
        if (!File(lockBackupDir, KEY_LOCKSCREEN_INFO).exists()) {
            getSecure(KEY_LOCKSCREEN_INFO)?.let { File(lockBackupDir, KEY_LOCKSCREEN_INFO).writeText(it) }
            getSecure(KEY_TEMPLATE_EDITOR_INFO)?.let { File(lockBackupDir, KEY_TEMPLATE_EDITOR_INFO).writeText(it) }
            getSecure(KEY_DEFAULT_LOCKSCREEN_INFO)?.let { File(lockBackupDir, KEY_DEFAULT_LOCKSCREEN_INFO).writeText(it) }
            getSecureInt(KEY_LOCKSCREEN_INFO_VERSION)?.let { File(lockBackupDir, KEY_LOCKSCREEN_INFO_VERSION).writeText(it.toString()) }
        }
    }

    // ---- 桌面子项 ----

    private fun applyDesktop(item: WallpaperItemConfig) {
        // 1. 备份桌面壁纸源图 + 滚动/特效键（仅首次）
        desktopBackupDir.mkdirs()
        if (!File(desktopBackupDir, desktopOrigFile.name).exists() && desktopOrigFile.exists()) {
            copyFile(desktopOrigFile, File(desktopBackupDir, desktopOrigFile.name))
        }
        if (!File(desktopBackupDir, KEY_DESKTOP_SCROLL).exists()) {
            getSecureInt(KEY_DESKTOP_SCROLL)?.let { File(desktopBackupDir, KEY_DESKTOP_SCROLL).writeText(it.toString()) }
        }
        if (!File(desktopBackupDir, KEY_WALLPAPER_EFFECT_1).exists()) {
            getSecureInt(KEY_WALLPAPER_EFFECT_1)?.let { File(desktopBackupDir, KEY_WALLPAPER_EFFECT_1).writeText(it.toString()) }
        }
        if (!File(desktopBackupDir, KEY_WALLPAPER_CHANGED).exists()) {
            getSecure(KEY_WALLPAPER_CHANGED)?.let { File(desktopBackupDir, KEY_WALLPAPER_CHANGED).writeText(it) }
        }

        // 2. 复制桌面壁纸源图
        if (!item.imagePath.isNullOrEmpty()) {
            copyFile(File(item.imagePath), desktopOrigFile)
            log("apply desktop: wrote wallpaper to $desktopOrigFile")
        }

        // 3. 写回滚动/特效键
        item.scrollEnabled?.let { putSecureInt(KEY_DESKTOP_SCROLL, if (it) 1 else 0) }
        item.effectType?.let { putSecureInt(KEY_WALLPAPER_EFFECT_1, it) }
        putSecure(KEY_WALLPAPER_CHANGED, "com.android.thememanager")
        log("apply desktop: done (image=${!item.imagePath.isNullOrEmpty()})")
    }

    private fun restoreDesktop() {
        File(desktopBackupDir, desktopOrigFile.name).takeIf { it.exists() }?.let { backup ->
            copyFile(backup, desktopOrigFile)
            log("restore desktop: restored wallpaper from backup")
        }
        File(desktopBackupDir, KEY_DESKTOP_SCROLL).takeIf { it.exists() }?.let { f ->
            putSecureInt(KEY_DESKTOP_SCROLL, f.readText().toIntOrNull() ?: 0)
        }
        File(desktopBackupDir, KEY_WALLPAPER_EFFECT_1).takeIf { it.exists() }?.let { f ->
            putSecureInt(KEY_WALLPAPER_EFFECT_1, f.readText().toIntOrNull() ?: 0)
        }
        File(desktopBackupDir, KEY_WALLPAPER_CHANGED).takeIf { it.exists() }?.let { f ->
            putSecure(KEY_WALLPAPER_CHANGED, f.readText())
        }
        log("restore desktop: done")
    }

    // ---- 工具 ----

    private fun backupFileOnce(source: File, dir: File) {
        dir.mkdirs()
        val dest = File(dir, source.name)
        if (!dest.exists() && source.exists()) {
            copyFile(source, dest)
        }
    }

    private fun copyFile(src: File, dst: File) {
        dst.parentFile?.mkdirs()
        src.inputStream().use { input ->
            dst.outputStream().use { output -> input.copyTo(output) }
        }
    }

    private fun getSecure(key: String): String? =
        Settings.Secure.getString(context.contentResolver, key)

    private fun getSecureInt(key: String): Int? =
        Settings.Secure.getInt(context.contentResolver, key, -1).takeIf { it != -1 }

    private fun putSecure(key: String, value: String) {
        Settings.Secure.putString(context.contentResolver, key, value)
    }

    private fun putSecureInt(key: String, value: Int) {
        Settings.Secure.putInt(context.contentResolver, key, value)
    }

    private fun log(msg: String) = Log.i(TAG, msg)

    companion object {
        private const val TAG = "WallpaperController"
    }
}
