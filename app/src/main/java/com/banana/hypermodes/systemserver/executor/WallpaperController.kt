package com.banana.hypermodes.systemserver.executor

import android.app.WallpaperManager
import android.content.Context
import android.os.Environment
import android.os.Process
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
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
 * - 壁纸走官方 WallpaperManager.setStream()：服务端会先置
 *   imageWallpaperPending=true 再落盘，WallpaperObserver 的 CLOSE_WRITE
 *   门控（component==null || event!=8 || imageWallpaperPending）必然通过，
 *   每次都能重新生成裁剪图并重绑组件。直接写 wallpaper_orig 只有首次
 *   （组件未绑定）有效，组件绑定后后续写文件全部被忽略，导致
 *   restore / 重复 apply 失效。
 * - 进入模式前先备份当前值（"原始快照"），退出时用快照回写并清理备份，
 *   保证下一次 apply 备份的是"当时"的系统状态而不是首次的状态。
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
    /** AOD 锁屏模板目录（subject mask 实际读取位置）。 */
    private val aodTemplateDir: File =
        File("/data/user_de/0/com.miui.aod/files/templates/current")
    private val subjectMaskFile: File get() = File(aodTemplateDir, "subject_mask")
    /** 备份标记：原锁屏没有独立壁纸（跟随桌面），restore 时应 clear(FLAG_LOCK)。 */
    private val MARKER_LOCK_FOLLOWS_HOME = "lock_follows_home"

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
     * 息屏样式相关设置键（Settings.Secure）。应用涂鸦等锁屏样式后，系统会把
     * 息屏样式自动切成传统 AOD（预期行为）；退出模式恢复时需把这些键还原成
     * 进入前的值，否则息屏样式会一直停留在传统 AOD。
     */
    private val AOD_STYLE_KEYS = listOf(
        "aod_style_state",
        "full_screen_aod_on",
        "aod_category_name",
        "linkage_state",
        "aod_show_style",
        "aod_mode_user_set"
    )

    /** 备份文件中"原值不存在"的标记（恢复时删除该设置键）。 */
    private val VALUE_NULL = "__hypermodes_null__"

    private val wallpaperManager: WallpaperManager by lazy {
        WallpaperManager.getInstance(context)
    }

    /**
     * 应用模式的壁纸 set。无数据子项跳过（不备份、不应用）。
     */
    fun apply(wallpaper: WallpaperConfig?) {
        if (wallpaper == null) {
            log("apply: wallpaper config is null, skip")
            return
        }
        // 不吞异常：让 ModeActionExecutor 捕获并触发整体回滚，避免半应用状态。
        wallpaper.lock?.let { applyLock(it) }
        wallpaper.desktop?.let { applyDesktop(it) }
    }

    /**
     * 恢复原始锁屏/桌面。仅回写有备份的子项（备份只在本控制器 apply 时创建）。
     */
    fun restore() {
        // 依次尝试两侧，最后抛出首个失败，避免静默吞异常。
        var failure: Exception? = null
        if (lockBackupDir.exists()) {
            try {
                restoreLock()
            } catch (e: Exception) {
                failure = e
                logError("restore lock failed", e)
            }
        }
        if (desktopBackupDir.exists()) {
            try {
                restoreDesktop()
            } catch (e: Exception) {
                if (failure == null) failure = e
                logError("restore desktop failed", e)
            }
        }
        failure?.let { throw it }
    }

    /** 是否有任何子项被应用过（用于退出模式时判断是否需要 restore）。 */
    fun hasApplied(): Boolean = lockBackupDir.exists() || desktopBackupDir.exists()

    /**
     * 为官方编辑器预置起始样式：把模式已保存的单个子项（锁屏/桌面）写入系统，
     * 让编辑器从保存的样式开始编辑。不创建备份、不参与模式激活/复原，
     * 只是把系统状态临时切到该样式供编辑器加载。
     */
    fun prepareForEdit(item: WallpaperItemConfig) {
        try {
            log("prepareForEdit: which=${item.which} imagePath=${item.imagePath} sysImagePath=${item.sysImagePath} hasJson=${!item.lockscreenJson.isNullOrEmpty()}")
            if (item.which == 2) {
                // 锁屏：样式 JSON + 壁纸 + 主体蒙版
                if (!item.lockscreenJson.isNullOrEmpty()) {
                    // 预置后编辑器从"保存的样式"开始，必须把景深蒙版路径
                    // 指到模板目录，否则编辑器/系统读不到 subject_mask 而丢失景深。
                    val json = rewriteSubjectPath(item.lockscreenJson, subjectMaskFile)
                    putSecure(KEY_LOCKSCREEN_INFO, json)
                    putSecure(
                        KEY_TEMPLATE_EDITOR_INFO,
                        item.templateEditorJson
                            ?: "{\"lockscreenInfo\":$json}"
                    )
                }
                if (!item.imagePath.isNullOrEmpty()) {
                    val src = resolveReadableSource(item.sysImagePath, item.imagePath)
                    if (src != null) {
                        setWallpaperStream(src, WallpaperManager.FLAG_LOCK)
                    } else {
                        log("prepareForEdit lock: no readable wallpaper source")
                    }
                }
                if (!item.sysSubjectMaskPath.isNullOrEmpty()) {
                    val src = File(item.sysSubjectMaskPath)
                    if (src.exists()) {
                        copyFile(src, subjectMaskFile)
                        log("prepareForEdit lock: wrote subject mask to $subjectMaskFile")
                    }
                }
                item.effectType?.let { putSecureInt(KEY_WALLPAPER_EFFECT_2, it) }
                log("prepareForEdit: staged lock item for editor")
            } else {
                // 桌面：壁纸 + 滚动/特效键
                if (!item.imagePath.isNullOrEmpty()) {
                    val src = resolveReadableSource(item.sysImagePath, item.imagePath)
                    if (src != null) {
                        setWallpaperStream(src, WallpaperManager.FLAG_SYSTEM)
                    } else {
                        log("prepareForEdit desktop: no readable wallpaper source")
                    }
                }
                item.scrollEnabled?.let { putSecureInt(KEY_DESKTOP_SCROLL, if (it) 1 else 0) }
                item.effectType?.let { putSecureInt(KEY_WALLPAPER_EFFECT_1, it) }
                putSecure(KEY_WALLPAPER_CHANGED, "com.android.thememanager")
                log("prepareForEdit: staged desktop item for editor")
            }
        } catch (e: Exception) {
            log("prepareForEdit failed: ${e.message}")
        }
    }

    // ---- 锁屏子项 ----

    private fun applyLock(item: WallpaperItemConfig) {
        // 1. 备份当前锁屏样式 JSON（仅首次）
        backupLockJsonOnce()
        // 1.1 备份息屏样式设置（涂鸦等样式会切传统 AOD，恢复时需还原）
        backupAodStyleOnce()

        // 2. 写回锁屏样式 JSON（触发 SystemUI 观察者）；
        //    同步把 wallpaperInfo.subject 指向模板目录，保证景深蒙版可读。
        if (!item.lockscreenJson.isNullOrEmpty()) {
            val json = rewriteSubjectPath(item.lockscreenJson, subjectMaskFile)
            putSecure(KEY_LOCKSCREEN_INFO, json)
            putSecure(
                KEY_TEMPLATE_EDITOR_INFO,
                item.templateEditorJson
                    ?: "{\"lockscreenInfo\":$json}"
            )
        }

        // 3. 设置锁屏壁纸（官方 setStream，每次都能触发裁剪重建 + 组件重绑）
        //    优先用 system_server 落盘的 sys 路径（App 私有目录 uid 1000 读不了）。
        if (!item.imagePath.isNullOrEmpty()) {
            backupLockWallpaperOnce()
            val src = resolveReadableSource(item.sysImagePath, item.imagePath)
            if (src != null) {
                setWallpaperStream(src, WallpaperManager.FLAG_LOCK)
                log("apply lock: set lock wallpaper via setStream (src=$src)")
            } else {
                log("apply lock: no readable wallpaper source")
            }
        }
        // 4. 复制锁屏主体蒙版（景深），若有且 source 可读
        if (!item.sysSubjectMaskPath.isNullOrEmpty()) {
            backupFileOnce(subjectMaskFile, lockBackupDir)
            val src = File(item.sysSubjectMaskPath)
            if (src.exists()) {
                copyFile(src, subjectMaskFile)
                log("apply lock: wrote subject mask to $subjectMaskFile")
            }
        }
        // 5. 备份并设置锁屏景深特效类型
        backupSecureIntOnce(lockBackupDir, KEY_WALLPAPER_EFFECT_2)
        item.effectType?.let { putSecureInt(KEY_WALLPAPER_EFFECT_2, it) }
        log("apply lock: done (json=${!item.lockscreenJson.isNullOrEmpty()}, image=${!item.imagePath.isNullOrEmpty()}, mask=${!item.sysSubjectMaskPath.isNullOrEmpty()})")
    }

    /** 备份当前锁屏壁纸：有独立文件则存字节；没有（跟随桌面）则打标记。 */
    private fun backupLockWallpaperOnce() {
        lockBackupDir.mkdirs()
        val backup = File(lockBackupDir, lockOrigFile.name)
        if (backup.exists()) return
        if (lockOrigFile.exists()) {
            copyFile(lockOrigFile, backup)
        } else {
            File(lockBackupDir, MARKER_LOCK_FOLLOWS_HOME).writeText("1")
        }
    }

    private fun restoreLock() {
        // 0. 还原息屏样式设置（涂鸦样式应用后系统会切传统 AOD）
        restoreAodStyle()

        // 1. 恢复锁屏样式 JSON；把 wallpaperInfo.subject 指回恢复后的 subject_mask，
        //    保证景深蒙版路径与恢复后的文件一致（否则系统读不到蒙版，景深丢失）。
        restoreLockscreenJsonKey(KEY_LOCKSCREEN_INFO)
        restoreSecureOnce(lockBackupDir, KEY_TEMPLATE_EDITOR_INFO)
        restoreSecureOnce(lockBackupDir, KEY_DEFAULT_LOCKSCREEN_INFO)
        restoreSecureIntOnce(lockBackupDir, KEY_LOCKSCREEN_INFO_VERSION)

        // 2. 先恢复锁屏主体蒙版（景深）和特效类型，再 setStream，
        //    让 WallpaperManagerService 处理壁纸时景深配置已就位。
        File(lockBackupDir, subjectMaskFile.name).takeIf { it.exists() }?.let { backup ->
            copyFile(backup, subjectMaskFile)
            log("restore lock: restored subject mask from backup")
        }
        restoreSecureIntOnce(lockBackupDir, KEY_WALLPAPER_EFFECT_2)

        // 3. 恢复锁屏壁纸：有备份则流式写回；只有标记则清空锁屏（跟随桌面）
        val lockBackup = File(lockBackupDir, lockOrigFile.name)
        if (lockBackup.exists()) {
            setWallpaperStream(lockBackup, WallpaperManager.FLAG_LOCK)
            log("restore lock: restored lock wallpaper via setStream")
        } else if (File(lockBackupDir, MARKER_LOCK_FOLLOWS_HOME).exists()) {
            wallpaperManager.clear(WallpaperManager.FLAG_LOCK)
            log("restore lock: cleared lock wallpaper (was following home)")
        }
        // 4. 清理备份，保证下一次 apply 重新备份"当时"的系统状态
        lockBackupDir.deleteRecursively()
        log("restore lock: done")
    }

    private fun backupLockJsonOnce() {
        lockBackupDir.mkdirs()
        backupSecureOnce(lockBackupDir, KEY_LOCKSCREEN_INFO)
        backupSecureOnce(lockBackupDir, KEY_TEMPLATE_EDITOR_INFO)
        backupSecureOnce(lockBackupDir, KEY_DEFAULT_LOCKSCREEN_INFO)
        backupSecureIntOnce(lockBackupDir, KEY_LOCKSCREEN_INFO_VERSION)
    }

    /** 备份息屏样式设置键当前值（仅首次），key 存为文件名。 */
    private fun backupAodStyleOnce() {
        lockBackupDir.mkdirs()
        for (key in AOD_STYLE_KEYS) {
            val backup = File(lockBackupDir, "secure_$key")
            if (backup.exists()) continue
            val current = getSecure(key)
            backup.writeText(current ?: VALUE_NULL)
        }
    }

    /** 还原息屏样式设置键：有原值写回，原值不存在则删除该键。 */
    private fun restoreAodStyle() {
        for (key in AOD_STYLE_KEYS) {
            val backup = File(lockBackupDir, "secure_$key")
            if (!backup.exists()) continue
            val value = backup.readText()
            if (value == VALUE_NULL) {
                runCatching {
                    context.contentResolver.delete(Settings.Secure.getUriFor(key), null, null)
                }.onFailure { t -> log("restore AOD style $key: delete failed: ${t.message}") }
            } else {
                putSecure(key, value)
            }
            log("restore AOD style: $key -> ${if (value == VALUE_NULL) "<deleted>" else value}")
        }
    }

    // ---- 桌面子项 ----

    private fun applyDesktop(item: WallpaperItemConfig) {
        // 1. 备份桌面壁纸源图 + 滚动/特效/来源键（仅首次；原值不存在也记录 null 标记）
        desktopBackupDir.mkdirs()
        if (!File(desktopBackupDir, desktopOrigFile.name).exists() && desktopOrigFile.exists()) {
            copyFile(desktopOrigFile, File(desktopBackupDir, desktopOrigFile.name))
        }
        backupSecureIntOnce(desktopBackupDir, KEY_DESKTOP_SCROLL)
        backupSecureIntOnce(desktopBackupDir, KEY_WALLPAPER_EFFECT_1)
        backupSecureOnce(desktopBackupDir, KEY_WALLPAPER_CHANGED)
        // 桌面-only 应用会触发 MIUI 把"跟随桌面"的锁屏迁移成独立锁屏，记录该状态，
        // 退出时 clear(FLAG_LOCK) 恢复"跟随桌面"，避免锁屏残留被迁移后的壁纸。
        if (!lockOrigFile.exists() && !File(desktopBackupDir, MARKER_LOCK_FOLLOWS_HOME).exists()) {
            File(desktopBackupDir, MARKER_LOCK_FOLLOWS_HOME).writeText("1")
        }

        // 2. 设置桌面壁纸（官方 setStream，每次都能触发裁剪重建 + 组件重绑）
        //    优先 system_server 落盘的 sys 路径
        if (!item.imagePath.isNullOrEmpty()) {
            val src = resolveReadableSource(item.sysImagePath, item.imagePath)
            if (src != null) {
                setWallpaperStream(src, WallpaperManager.FLAG_SYSTEM)
                log("apply desktop: set desktop wallpaper via setStream (src=$src)")
            } else {
                log("apply desktop: no readable wallpaper source")
            }
        }

        // 3. 写回滚动/特效键
        item.scrollEnabled?.let { putSecureInt(KEY_DESKTOP_SCROLL, if (it) 1 else 0) }
        item.effectType?.let { putSecureInt(KEY_WALLPAPER_EFFECT_1, it) }
        putSecure(KEY_WALLPAPER_CHANGED, "com.android.thememanager")
        log("apply desktop: done (image=${!item.imagePath.isNullOrEmpty()})")
    }

    private fun restoreDesktop() {
        File(desktopBackupDir, desktopOrigFile.name).takeIf { it.exists() }?.let { backup ->
            setWallpaperStream(backup, WallpaperManager.FLAG_SYSTEM)
            log("restore desktop: restored desktop wallpaper via setStream")
        }
        // 原始锁屏跟随桌面：桌面-only 应用把它迁移成了独立锁屏，这里清掉恢复"跟随桌面"。
        if (File(desktopBackupDir, MARKER_LOCK_FOLLOWS_HOME).exists()) {
            wallpaperManager.clear(WallpaperManager.FLAG_LOCK)
            log("restore desktop: cleared migrated lock wallpaper (was following home)")
        }
        restoreSecureIntOnce(desktopBackupDir, KEY_DESKTOP_SCROLL)
        restoreSecureIntOnce(desktopBackupDir, KEY_WALLPAPER_EFFECT_1)
        restoreSecureOnce(desktopBackupDir, KEY_WALLPAPER_CHANGED)
        desktopBackupDir.deleteRecursively()
        log("restore desktop: done")
    }

    // ---- 工具 ----

    /**
     * 官方路径设置壁纸：原始字节流式写入。服务端内部先置
     * imageWallpaperPending=true 再落盘，CLOSE_WRITE 事件必然通过
     * needsUpdate 门控，触发裁剪重建与组件重绑；阻塞到完成。
     *
     * 若目标内容与系统当前壁纸一致则跳过写入，避免每次切换样式都
     * 重新 setStream 导致系统"最近使用"壁纸历史无限增长。
     */
    private fun setWallpaperStream(src: File, which: Int) {
        if (sameAsCurrent(src, which)) {
            log("setWallpaperStream: skip (content identical, which=$which)")
            return
        }
        log("setWallpaperStream: setting which=$which src=${src.absolutePath} size=${src.length()}")
        src.inputStream().use { input ->
            wallpaperManager.setStream(input, null, true, which)
        }
        log("setWallpaperStream: done which=$which")
    }

    /** 目标壁纸与系统当前壁纸内容是否一致（字节级，防重复 setStream）。 */
    private fun sameAsCurrent(src: File, which: Int): Boolean {
        val current = when (which) {
            WallpaperManager.FLAG_LOCK -> lockOrigFile
            else -> desktopOrigFile
        }
        if (!current.exists()) return false
        if (!src.exists()) return false
        // 快速路径：文件大小不同必不同
        if (src.length() != current.length()) return false
        return runCatching {
            var equal = true
            src.inputStream().use { a -> current.inputStream().use { b ->
                val bufA = ByteArray(64 * 1024)
                val bufB = ByteArray(64 * 1024)
                while (true) {
                    val nA = a.read(bufA)
                    val nB = b.read(bufB)
                    if (nA != nB) {
                        equal = false
                        break
                    }
                    if (nA < 0) break
                    if (!bufA.copyOf(nA).contentEquals(bufB.copyOf(nB))) {
                        equal = false
                        break
                    }
                }
            } }
            equal
        }.getOrDefault(false)
    }

    /**
     * 修正 lockscreenJson 中 wallpaperInfo.subject 指向模板目录的 subject_mask，
     * 确保系统/编辑器从正确位置读取景深蒙版（保存时捕获的可能是历史路径）。
     */
    private fun rewriteSubjectPath(lockscreenJson: String, maskFile: File): String {
        return runCatching {
            val root = org.json.JSONObject(lockscreenJson)
            val wallpaperInfo = root.optJSONObject("wallpaperInfo")
            if (wallpaperInfo != null && wallpaperInfo.has("subject")) {
                wallpaperInfo.put("subject", maskFile.absolutePath)
                root.toString()
            } else {
                lockscreenJson
            }
        }.getOrDefault(lockscreenJson)
    }

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

    /** 优先 system_server 可读的 sys 路径，回退 App 路径（仅当确实可读）。 */
    private fun resolveReadableSource(sysPath: String?, appPath: String?): File? {
        if (!sysPath.isNullOrEmpty()) {
            val f = File(sysPath)
            if (f.exists()) return f
        }
        if (!appPath.isNullOrEmpty()) {
            val f = File(appPath)
            if (f.exists() && f.canRead()) return f
        }
        return null
    }

    /** 备份 Settings.Secure 字符串键（仅首次）；原值不存在时写入 null 标记，恢复时删除该键。 */
    private fun backupSecureOnce(dir: File, key: String) {
        dir.mkdirs()
        val backup = File(dir, key)
        if (backup.exists()) return
        backup.writeText(getSecure(key) ?: VALUE_NULL)
    }

    /** 备份 Settings.Secure 整型键（仅首次）；原值不存在时写入 null 标记。 */
    private fun backupSecureIntOnce(dir: File, key: String) {
        dir.mkdirs()
        val backup = File(dir, key)
        if (backup.exists()) return
        backup.writeText(getSecureInt(key)?.toString() ?: VALUE_NULL)
    }

    /** 恢复 Settings.Secure 字符串键；null 标记表示删除，否则回写原值。 */
    private fun restoreSecureOnce(dir: File, key: String) {
        val backup = File(dir, key)
        if (!backup.exists()) return
        val value = backup.readText()
        if (value == VALUE_NULL) deleteSecure(key) else putSecure(key, value)
    }

    /** 恢复 Settings.Secure 整型键；null 标记表示删除，否则回写原值。 */
    private fun restoreSecureIntOnce(dir: File, key: String) {
        val backup = File(dir, key)
        if (!backup.exists()) return
        val value = backup.readText()
        if (value == VALUE_NULL) deleteSecure(key) else putSecureInt(key, value.toIntOrNull() ?: 0)
    }

    /** 恢复锁屏样式 JSON 键（额外把 subject 路径重写回模板目录）。 */
    private fun restoreLockscreenJsonKey(key: String) {
        val backup = File(lockBackupDir, key)
        if (!backup.exists()) return
        val value = backup.readText()
        if (value == VALUE_NULL) {
            deleteSecure(key)
        } else {
            putSecure(key, rewriteSubjectPath(value, subjectMaskFile))
        }
    }

    private fun deleteSecure(key: String) {
        runCatching {
            context.contentResolver.delete(Settings.Secure.getUriFor(key), null, null)
        }.onFailure { t -> logError("delete $key failed", t) }
    }

    private fun getSecure(key: String): String? =
        Settings.Secure.getString(context.contentResolver, key)

    private fun getSecureInt(key: String): Int? =
        runCatching { Settings.Secure.getInt(context.contentResolver, key) }.getOrNull()

    private fun putSecure(key: String, value: String) {
        Settings.Secure.putString(context.contentResolver, key, value)
    }

    private fun putSecureInt(key: String, value: Int) {
        Settings.Secure.putInt(context.contentResolver, key, value)
    }

    private fun log(msg: String) = HyperLog.i(TAG, msg)

    private fun logError(msg: String, t: Throwable? = null) {
        if (t != null) HyperLog.e(TAG, msg, t) else HyperLog.e(TAG, msg)
    }

    companion object {
        private const val TAG = "WallpaperController"
    }
}
