package com.banana.hypermodes.ui

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import android.graphics.Canvas
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.*
import com.banana.hypermodes.bridge.ModeControlBridge
import com.banana.hypermodes.data.WallpaperSnapshotBridge
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.components.TimePickerDialog
import com.banana.hypermodes.ui.components.TriggerSelectionDialog
import com.banana.hypermodes.ui.components.TriggerTypeSelectionDialog
import com.banana.hypermodes.ui.components.CompoundTriggerEditDialog
import com.banana.hypermodes.ui.components.TriggerGroupCard
import com.banana.hypermodes.ui.components.BatteryTriggerPickerDialog
import com.banana.hypermodes.ui.components.WallpaperOverviewCard
import java.io.File
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.basic.Close
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup

/** 锁屏子项是否被用户实际修改（JSON 或壁纸图任一变化）。 */
private fun lockItemChanged(before: WallpaperSet?, now: WallpaperSet?): Boolean {
    val a = before?.lock ?: return false
    val b = now?.lock ?: return true
    // 语义比较：系统可能对保存的 JSON 做字段重排/补默认值，字符串不同不代表
    // 用户改了样式；比较关键样式字段（templateId/颜色/信息行等）判断真实变化。
    if (!sameLockscreenJson(a.lockscreenJson, b.lockscreenJson)) return true
    return !sameImageFile(a.imagePath, b.imagePath)
}

/** 两个锁屏 JSON 是否表达同一样式（比较关键字段，忽略键顺序/补默认值）。 */
private fun sameLockscreenJson(j1: String?, j2: String?): Boolean {
    if (j1 == null || j2 == null) return j1 == j2
    if (j1 == j2) return true
    return runCatching {
        val o1 = org.json.JSONObject(j1)
        val o2 = org.json.JSONObject(j2)
        val c1 = o1.optJSONObject("clockInfo")
        val c2 = o2.optJSONObject("clockInfo")
        if (c1 == null || c2 == null) return@runCatching false
        val keys = listOf(
            "templateId", "primaryColor", "secondaryColor", "blendColor",
            "secondaryBlendColor", "infoAreaColor", "isAutoPrimaryColor",
            "isAutoSecondaryColor", "isDiffHourMinuteColor", "enableDiffusion",
            "style", "clockWeight", "clockEffect", "extraFlag",
            "classicLine1", "classicLine2", "classicLine3", "classicLine4", "classicLine5"
        )
        keys.all { key ->
            val v1 = c1.opt(key)
            val v2 = c2.opt(key)
            when {
                v1 == JSONObject.NULL && v2 == JSONObject.NULL -> true
                v1 == JSONObject.NULL || v2 == JSONObject.NULL -> false
                else -> v1 == v2
            }
        }
    }.getOrDefault(j1 == j2)
}

/**
 * 合并编辑后的快照与模式已有配置：快照缺失的字段（如系统锁屏无独立源图时
 * imagePath 为 null）保留原值，避免把已保存的壁纸路径覆盖成 null，
 * 导致预览全部丢失。快照有值的字段以快照为准。
 */
private fun mergeSnapshotItem(snap: WallpaperItem?, prev: WallpaperItem?): WallpaperItem? {
    if (snap == null) return prev
    if (prev == null) return snap
    return snap.copy(
        imagePath = snap.imagePath ?: prev.imagePath,
        sysImagePath = snap.sysImagePath ?: prev.sysImagePath,
        subjectMaskPath = snap.subjectMaskPath ?: prev.subjectMaskPath,
        sysSubjectMaskPath = snap.sysSubjectMaskPath ?: prev.sysSubjectMaskPath
    )
}

/**
 * 编辑会话结束后把真实系统恢复为编辑前的状态。
 * prepareEdit 是跨进程广播 + ResultReceiver，原本 fire-and-forget 不等待结果，
 * 导致 UI 在 restore 完成前就用编辑后的快照刷新预览，看起来“真实壁纸被改了”。
 * 这里在 lock/desktop 两个子项都完成后回调，再由 UI 同步刷新预览。
 */
private fun restoreEditSystem(
    context: Context,
    pre: WallpaperSet?,
    onDone: () -> Unit = {}
) {
    HyperLog.i("ModeDetailScreen", "restoreEditSystem: pre lock=${pre?.lock?.imagePath} desktop=${pre?.desktop?.imagePath}")
    if (pre == null) {
        onDone()
        return
    }
    var remaining = 0
    var failed = 0
    val checkDone = { ok: Boolean ->
        if (!ok) failed++
        remaining--
        if (remaining <= 0) {
            HyperLog.i("ModeDetailScreen", "restoreEditSystem: done failed=$failed")
            onDone()
        }
    }
    pre.lock?.let { remaining++ }
    pre.desktop?.let { remaining++ }
    if (remaining == 0) {
        onDone()
        return
    }
    pre.lock?.let { WallpaperSnapshotBridge.prepareEdit(context, it) { ok -> checkDone(ok) } }
    pre.desktop?.let { WallpaperSnapshotBridge.prepareEdit(context, it) { ok -> checkDone(ok) } }
}

/** 桌面子项是否被用户实际修改（壁纸图变化）。 */
private fun desktopItemChanged(before: WallpaperSet?, now: WallpaperSet?): Boolean {
    val a = before?.desktop ?: return false
    val b = now?.desktop ?: return true
    return !sameImageFile(a.imagePath, b.imagePath)
}

/**
 * 把模式已保存的壁纸图复制到 App 缓存目录作为"编辑前基线"。
 * 编辑后的捕获会覆盖模式目录里的同名文件（imagePath 不变、内容变），
 * 若基线直接引用原路径，编辑前后的比较会读到同一个新文件而永远判为"未修改"，
 * 导致只换壁纸不换样式时保存不了新壁纸、预览不更新。复制到临时文件后
 * 比较的是编辑前的旧内容。
 */
private fun copyBaselineItem(context: Context, item: WallpaperItem?): WallpaperItem? {
    val srcPath = item?.imagePath ?: return item
    val src = File(srcPath)
    if (!src.exists()) return item
    val tmp = File(context.cacheDir, "wp_baseline_" + src.name)
    return runCatching {
        src.inputStream().use { input ->
            tmp.outputStream().use { output -> input.copyTo(output) }
        }
        item.copy(imagePath = tmp.absolutePath)
    }.getOrDefault(item)
}

private fun sameImageFile(p1: String?, p2: String?): Boolean {
    if (p1 == null || p2 == null) return p1 == p2
    val f1 = File(p1)
    val f2 = File(p2)
    if (!f1.exists() || !f2.exists()) return false
    // 快速路径：字节完全一致
    if (runCatching { f1.readBytes().contentEquals(f2.readBytes()) }.getOrDefault(false)) {
        return true
    }
    // 容差路径：解码缩小后逐像素比较，容忍 JPEG 重压缩导致的字节差异
    // （预置保存的样式到系统后再捕获，图片内容相同但字节必然不同）。
    return runCatching {
        val b1 = decodeSampled(f1, 48)
        val b2 = decodeSampled(f2, 48)
        if (b1 == null || b2 == null) return@runCatching false
        if (b1.width != b2.width || b1.height != b2.height) return@runCatching false
        var diff = 0L
        var total = 0
        for (y in 0 until b1.height) {
            for (x in 0 until b1.width) {
                val c1 = b1.getPixel(x, y)
                val c2 = b2.getPixel(x, y)
                diff += Math.abs(((c1 shr 16) and 0xFF) - ((c2 shr 16) and 0xFF))
                diff += Math.abs(((c1 shr 8) and 0xFF) - ((c2 shr 8) and 0xFF))
                diff += Math.abs((c1 and 0xFF) - (c2 and 0xFF))
                total++
            }
        }
        // 平均每通道差 < 8/255 ≈ 3%，容错 JPEG 重压缩，区分真正的换图
        diff < total * 3L * 8L
    }.getOrDefault(false)
}

/** 按最大边长采样解码（缩小到 ~48px，像素级比较前先降采样）。 */
private fun decodeSampled(file: File, maxSide: Int): Bitmap? {
    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    BitmapFactory.decodeFile(file.absolutePath, bounds)
    if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null
    var sample = 1
    while (Math.max(bounds.outWidth, bounds.outHeight) / (sample * 2) >= maxSide) {
        sample *= 2
    }
    return BitmapFactory.decodeFile(
        file.absolutePath,
        BitmapFactory.Options().apply { inSampleSize = sample }
    )
}

@Composable
fun ModeDetailScreen(
    mode: Mode,
    onBack: () -> Unit,
    onOpenDisplayOptions: (Mode) -> Unit,
    onOpenRepeat: (Mode) -> Unit,
    onOpenApps: (Mode) -> Unit,
    onOpenPausedApps: (Mode) -> Unit,
    onOpenAppTriggerPicker: (Mode) -> Unit,
    onOpenWifiTriggerPicker: (Mode) -> Unit,
    onOpenBluetoothTriggerPicker: (Mode) -> Unit,
    onOpenLocationTriggerPicker: (Mode) -> Unit,
    onOpenIntentTriggerPicker: (Mode) -> Unit,
    onOpenDrivingBluetoothPicker: (Mode) -> Unit,
    onOpenDeviceControl: (Mode) -> Unit,
    onOpenDrivingDetect: (Mode) -> Unit,
    /** caller: "lock" 或 "desktop"，用于进入对应的自定义界面。 */
    onOpenWallpaper: (Mode, String) -> Unit,
    onRename: (Mode) -> Unit,
    onDelete: (Mode) -> Unit,
    onSave: (Mode) -> Unit,
    showEditDialog: Boolean,
    modeToEdit: Mode?,
    isCreatingNewMode: Boolean,
    onDismissEdit: () -> Unit,
    isAddingToCompound: Boolean,
    onIsAddingToCompoundChange: (Boolean) -> Unit,
    showCompoundTriggerDialog: Boolean,
    onShowCompoundTriggerDialogChange: (Boolean) -> Unit,
    pendingCompoundTrigger: ModeTrigger?,
    onPendingCompoundTriggerConsumed: () -> Unit,
    editingCompoundTriggers: List<ModeTrigger>,
    onEditingCompoundTriggersChange: (List<ModeTrigger>) -> Unit,

    onDoneEdit: (Mode) -> Unit
) {
    BackHandler(onBack = onBack)
    var editedMode by remember(mode) { mutableStateOf(mode) }
    val context = LocalContext.current

    // Resolve bonded-device names for display; falls back to the raw MAC when
    // unpaired or BLUETOOTH_CONNECT is denied.
    val deviceNames = remember(editedMode.settings.drivingTargetDevices) {
        val names = mutableMapOf<String, String>()
        try {
            val manager = context.getSystemService(BluetoothManager::class.java)
            manager?.adapter?.bondedDevices?.forEach { device ->
                device.name?.let { names[device.address] = it }
            }
        } catch (_: SecurityException) {
            // Permission denied: labels fall back to MAC addresses
        }
        names
    }
    var showContactDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // 点击壁纸卡片打开官方 UI 后，等待用户返回时捕获当前壁纸快照并保存
    var pendingWallpaperCapture by remember(mode.id) { mutableStateOf(false) }
    // 系统当前壁纸快照（详情页进入时拉取一次，未配置时作为预览底图/锁屏样式）
    var systemWallpaper by remember(mode.id) { mutableStateOf<WallpaperSet?>(null) }
    // 打开编辑器前的系统快照基线，用于返回后判断用户是否真的改过
    var beforeWallpaper by remember(mode.id) { mutableStateOf<WallpaperSet?>(null) }
    // 编辑会话开始前的真实系统壁纸状态，用于会话结束后恢复
    // （官方编辑器直接写真实系统，模式未激活时编辑完要还原，不改变真实锁屏/桌面）
    var preEditSystem by remember(mode.id) { mutableStateOf<WallpaperSet?>(null) }
    // 壁纸内容变化的刷新信号：捕获/恢复后递增，强制预览重新解码
    var wallpaperRefreshTick by remember(mode.id) { mutableIntStateOf(0) }
    // 预置已保存壁纸到系统（setStream 重新裁剪）耗时，期间显示加载遮罩并防止重复点击
    var isPreparingWallpaper by remember(mode.id) { mutableStateOf(false) }
    LaunchedEffect(mode.id) {
        // 先用上次缓存的预览立即显示，避免每次进详情页都等 1-2s 跨进程拉取
        systemWallpaper = WallpaperSnapshotBridge.readCachedCurrent(context)
        // 后台刷新最新快照
        WallpaperSnapshotBridge.captureCurrent(context) { snapshot ->
            if (snapshot != null) {
                systemWallpaper = snapshot
                wallpaperRefreshTick++
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mode.id) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                if (pendingWallpaperCapture) {
                    pendingWallpaperCapture = false
                    WallpaperSnapshotBridge.capture(context, editedMode.id) { snapshot ->
                        // 与打开前的系统快照逐子项对比：只保存用户实际修改的那一侧，
                        // 未修改的子项保留原配置（不误配置成系统当前值）。
                        val before = beforeWallpaper
                        val lockChanged = snapshot != null && lockItemChanged(before, snapshot)
                        val desktopChanged = snapshot != null && desktopItemChanged(before, snapshot)
                        val anyChanged = lockChanged || desktopChanged
                        if (snapshot != null && anyChanged) {
                            val prevWallpaper = editedMode.settings.wallpaper
                            val newWallpaper = WallpaperSet(
                                lock = if (lockChanged) {
                                    mergeSnapshotItem(snapshot.lock, prevWallpaper?.lock)
                                } else {
                                    prevWallpaper?.lock
                                },
                                desktop = if (desktopChanged) {
                                    mergeSnapshotItem(snapshot.desktop, prevWallpaper?.desktop)
                                } else {
                                    prevWallpaper?.desktop
                                }
                            )
                            val hasAny = newWallpaper.lock != null || newWallpaper.desktop != null
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(
                                    wallpaper = if (hasAny) newWallpaper else null
                                )
                            )
                            onSave(editedMode)
                            // 模式已激活：预览底图同步为编辑后的最新系统状态（激活时模式样式本就应生效）
                            if (editedMode.enabled) {
                                systemWallpaper = snapshot
                                wallpaperRefreshTick++
                            }
                        } else if (snapshot != null && editedMode.enabled) {
                            // 没改变壁纸：跳过保存，但激活模式下仍刷新预览
                            systemWallpaper = snapshot
                            wallpaperRefreshTick++
                        }
                        // 编辑会话结束：模式未激活时只有真正修改了壁纸才需要恢复真实系统；
                        // 没修改时官方编辑器不会改写系统壁纸，跳过恢复能避免不必要的 setStream。
                        if (!editedMode.enabled) {
                            if (anyChanged) {
                                HyperLog.i("ModeDetailScreen", "edit done: restore real system (snapshot=${snapshot != null})")
                                restoreEditSystem(context, preEditSystem) {
                                    // 恢复完成后重新捕获真实系统刷新预览，保证预览与真实壁纸一致，
                                    // 而不是直接信任 preEditSystem（恢复可能只成功了一半）。
                                    WallpaperSnapshotBridge.captureCurrent(context) { snap ->
                                        if (snap != null) systemWallpaper = snap
                                        wallpaperRefreshTick++
                                    }
                                }
                            } else {
                                // 没修改也要刷新预览
                                WallpaperSnapshotBridge.captureCurrent(context) { snap ->
                                    if (snap != null) systemWallpaper = snap
                                    wallpaperRefreshTick++
                                }
                            }
                        }
                    }
                } else {
                    // 非编辑会话返回（从系统设置/主题商店切回等）：刷新系统当前壁纸快照，
                    // 让“初始壁纸预览图”能反映外部对真实壁纸的修改。
                    WallpaperSnapshotBridge.captureCurrent(context) { snap ->
                        if (snap != null) {
                            systemWallpaper = snap
                            wallpaperRefreshTick++
                        }
                    }
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // v1.3 Trigger UI State - keyed by mode.id to reset when mode changes
    var showTriggerSelector by remember(mode.id) { mutableStateOf(false) }
    var showTimePicker by remember(mode.id) { mutableStateOf(false) }
    var showEndTimePicker by remember(mode.id) { mutableStateOf(false) }
    var showBatteryPicker by remember(mode.id) { mutableStateOf(false) }
    var pendingStartTime by remember(mode.id) { mutableStateOf<Pair<Int, Int>?>(null) }

    // v2.0 Complex Trigger State
    var showTriggerTypeDialog by remember(mode.id) { mutableStateOf(false) }
    var editingCompoundName by remember(mode.id) { mutableStateOf<String?>(null) }
    var editingGroupIndex by remember(mode.id) { mutableStateOf<Int?>(null) }

    // Monitor for new triggers added via picker screens and convert to v2.0 trigger groups


    // Handle pending compound trigger from picker
    LaunchedEffect(pendingCompoundTrigger) {
        pendingCompoundTrigger?.let { trigger ->
            onEditingCompoundTriggersChange(editingCompoundTriggers + trigger)
            onPendingCompoundTriggerConsumed()
            onShowCompoundTriggerDialogChange(true)
        }
    }



    // For bedtime: refresh the schedule from DeskClock when this screen opens,
    // and keep the UI in sync with whatever the Clock actually stores.
    if (mode.id == "bedtime") {
        LaunchedEffect(Unit) {
            context.sendBroadcast(android.content.Intent(Protocol.ACTION_QUERY_SCHEDULE).apply {
                setPackage(Protocol.TARGET_PACKAGE)
            })
        }
        val liveSchedule = DeskClockState.schedule
        LaunchedEffect(liveSchedule) {
            if (liveSchedule != null) {
                editedMode = editedMode.copy(
                    settings = editedMode.settings.copy(schedule = liveSchedule)
                )
            }
        }
    }

    val localizedName = when (editedMode.id) {
        "dnd" -> stringResource(R.string.mode_dnd)
        "bedtime" -> stringResource(R.string.mode_bedtime)
        "driving" -> stringResource(R.string.mode_driving)
        else -> editedMode.name
    }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = localizedName,
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = null
                        )
                    }
                    // miuix dropdown anchored to the ⋮ button (window popup —
                    // no Scaffold LocalDialogStates needed here).
                    WindowListPopup(
                        show = showOverflowMenu,
                        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                        alignment = PopupPositionProvider.Align.TopEnd,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        ListPopupColumn {
                            DropdownImpl(
                                text = stringResource(R.string.rename),
                                optionSize = 2,
                                isSelected = false,
                                index = 0,
                                onSelectedIndexChange = {
                                    showOverflowMenu = false
                                    onRename(editedMode)
                                }
                            )
                            DropdownImpl(
                                text = stringResource(R.string.delete),
                                optionSize = 2,
                                isSelected = false,
                                index = 1,
                                onSelectedIndexChange = {
                                    showOverflowMenu = false
                                    showDeleteConfirm = true
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding())
        ) {
            // Hero: big icon + description + 立即开启 button (Pixel layout)
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp, bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .squircleBackground(
                                color = MiuixTheme.colorScheme.secondaryContainer,
                                cornerRadius = 48.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = editedMode.icon,
                            fontSize = 44.sp,
                            // Emoji glyphs carry font padding that throws them
                            // off-center inside the squircle — strip it.
                            style = TextStyle(
                                textAlign = TextAlign.Center,
                                platformStyle = PlatformTextStyle(includeFontPadding = false)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    AdaptiveHeroDescription(
                        text = when (editedMode.id) {
                            "dnd" -> stringResource(R.string.mode_dnd_hero_desc)
                            "bedtime" -> stringResource(R.string.bedtime_hero_desc)
                            "driving" -> stringResource(R.string.driving_intro_subtitle)
                            else -> editedMode.description.ifEmpty {
                                stringResource(R.string.custom_mode_hero_desc)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    // Primary action button: MIUI Clock "Finish" style (Blue pill)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .padding(horizontal = 28.dp)
                            .squircleSurface(
                                color = MiuixTheme.colorScheme.primary,
                                cornerRadius = 28.dp
                            )
                            .clickable {
                                val enabled = !editedMode.enabled
                                editedMode = editedMode.copy(enabled = enabled)
                                if (enabled) {
                                    ModeControlBridge.activateMode(context, editedMode.id)
                                } else {
                                    ModeControlBridge.deactivateMode(context, editedMode.id)
                                }
                                // Bedtime's card mirrors the official DeskClock state,
                                // which only settles after an async round trip through
                                // system_server -> DeskClock -> back (seconds if the
                                // DeskClock process has to cold-start). Update the mirror
                                // optimistically; the query reply still reconciles truth.
                                if (editedMode.id == "bedtime") {
                                    DeskClockState.updateBedtimeActive(context, enabled)
                                }
                                onSave(editedMode)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (editedMode.enabled) stringResource(R.string.turn_off)
                            else stringResource(R.string.turn_on_now),
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Schedule section (for bedtime)
            if (editedMode.id == "bedtime") {
                item {
                    SmallTitle(
                        text = stringResource(R.string.when_to_turn_on),
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    ScheduleCard(
                        schedule = editedMode.settings.schedule ?: ModeSchedule(),
                        onScheduleChange = { newSchedule ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(schedule = newSchedule)
                            )
                            onSave(editedMode)
                        }
                    )
                }

                // Grouped options for bedtime
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(0.dp)
                    ) {
                        Column {
                            // 就寝提醒 row (图三): official options 准时/15/30/60分钟/无
                            ValueSettingItem(
                                title = stringResource(R.string.sleep_reminder),
                                subtitle = "",
                                value = reminderSummary(DeskClockState.reminderMinutes),
                                onClick = { showReminderDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // 重复周期 row (图四): opens the repeat picker sub-page
                            ValueSettingItem(
                                title = stringResource(R.string.repeat_cycle),
                                subtitle = stringResource(R.string.repeat_cycle_desc),
                                value = repeatSummary(
                                    (editedMode.settings.schedule ?: ModeSchedule()).repeatDays
                                ),
                                onClick = { onOpenRepeat(editedMode) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Driving: 何时自动开启 + 驾车时 row (opens 驾车勿扰 detection page)
            if (editedMode.id == "driving") {
                item {
                    SmallTitle(
                        text = stringResource(R.string.when_to_turn_on),
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        onClick = { onOpenDrivingDetect(editedMode) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.driving_when_driving),
                                    style = MiuixTheme.textStyles.body1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.mode_driving_desc),
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Icon(
                                imageVector = MiuixIcons.Basic.ArrowRight,
                                contentDescription = null,
                                tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Switch(
                                checked = editedMode.settings.drivingAutoDetect,
                                onCheckedChange = { on ->
                                    editedMode = editedMode.copy(
                                        settings = editedMode.settings.copy(drivingAutoDetect = on)
                                    )
                                    onSave(editedMode)
                                }
                            )
                        }
                    }
                }

                if (editedMode.settings.drivingAutoDetect) {
                    item {
                        SmallTitle(
                            text = stringResource(R.string.driving_devices_title),
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp, bottom = 8.dp)
                        )
                    }

                    editedMode.settings.drivingTargetDevices.forEach { address ->
                        item {
                            TriggerRowCard(
                                icon = "🎧",
                                label = deviceNames[address] ?: address,
                                onDelete = {
                                    editedMode = editedMode.copy(
                                        settings = editedMode.settings.copy(
                                            drivingTargetDevices =
                                                editedMode.settings.drivingTargetDevices - address
                                        )
                                    )
                                    onSave(editedMode)
                                }
                            )
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            onClick = { onOpenDrivingBluetoothPicker(editedMode) }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "＋",
                                    style = MiuixTheme.textStyles.title2,
                                    color = MiuixTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.add_device),
                                    style = MiuixTheme.textStyles.body1
                                )
                            }
                        }
                    }
                }
            }

            // Complex trigger UI (v1.3). Bedtime keeps its own Clock-driven
            // schedule UI and driving keeps its auto-detect section; every
            // other mode — built-in dnd included — gets complex triggers.
            if (editedMode.id != "bedtime" && editedMode.id != "driving") {
                item {
                    SmallTitle(
                        text = stringResource(R.string.when_to_turn_on),
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                // Display existing trigger groups (v2.0)
                editedMode.settings.triggerGroups.forEachIndexed { index, group ->
                    item(key = "trigger_group_$index") {
                        TriggerGroupCard(
                            group = group,
                            groupIndex = index,
                            onRemove = {
                                editedMode = editedMode.copy(
                                    settings = editedMode.settings.copy(
                                        triggerGroups = editedMode.settings.triggerGroups.filterIndexed { i, _ -> i != index }
                                    )
                                )
                                onSave(editedMode)
                            },
                            onLongClick = if (group is ModeTriggerGroup.Compound) {
                                {
                                    // Long press on compound trigger opens edit dialog
                                    editingGroupIndex = index
                                    onEditingCompoundTriggersChange(group.triggers)
                                    editingCompoundName = group.name
                                    onShowCompoundTriggerDialogChange(true)
                                }
                            } else null,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }

                // Add Schedule card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        onClick = { showTriggerTypeDialog = true }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "＋",
                                style = MiuixTheme.textStyles.title2,
                                color = MiuixTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.add_trigger),
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                }
            }

            // 壁纸 section: 标题 + 锁屏/桌面概览大卡片（在触发条件下面、通知过滤上面）
            item {
                SmallTitle(
                    text = stringResource(R.string.wallpaper),
                    modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            item {
                // 把点击逻辑提取成具名局部函数，才能在基线不完整时递归重试。
                fun openLockEditor() {
                    if (isPreparingWallpaper) return
                    pendingWallpaperCapture = true
                    // 基线/恢复依据直接用进入时已刷新的 systemWallpaper，点击时不再
                    // 同步跨进程捕获（这是点击后卡顿数秒的主因）。进入页与每次返回
                    // 都会刷新 systemWallpaper，因此它足够新。
                    val baseline = systemWallpaper
                        ?: WallpaperSnapshotBridge.readCachedCurrent(context)
                    // 官方编辑器打开时会同时读取锁屏 + 桌面两侧；
                    // 任一侧缺失都会在编辑后被重置为默认，因此基线必须完整。
                    if (baseline?.lock == null || baseline.desktop == null) {
                        isPreparingWallpaper = true
                        WallpaperSnapshotBridge.captureCurrent(context) { fresh ->
                            isPreparingWallpaper = false
                            if (fresh != null) {
                                systemWallpaper = fresh
                                wallpaperRefreshTick++
                                // 数据完整后自动重新触发点击
                                openLockEditor()
                            } else {
                                pendingWallpaperCapture = false
                            }
                        }
                        return
                    }
                    // 记录编辑前真实系统状态（会话结束后恢复）
                    preEditSystem = baseline
                    val saved = editedMode.settings.wallpaper?.lock
                    val hasSavedLock = saved != null && (
                        !saved.lockscreenJson.isNullOrEmpty() ||
                            !saved.imagePath.isNullOrEmpty()
                        )
                    val modeWallpaper = editedMode.settings.wallpaper
                    val savedDesktop = modeWallpaper?.desktop
                    val hasSavedDesktop = savedDesktop != null && !savedDesktop.imagePath.isNullOrEmpty()
                    val editSet = WallpaperSet(
                        lock = if (hasSavedLock) copyBaselineItem(context, saved) else baseline.lock,
                        desktop = if (hasSavedDesktop) copyBaselineItem(context, savedDesktop) else baseline.desktop
                    )
                    beforeWallpaper = WallpaperSet(
                        lock = if (hasSavedLock) copyBaselineItem(context, saved) else baseline.lock,
                        desktop = if (hasSavedDesktop) copyBaselineItem(context, savedDesktop) else baseline.desktop
                    )
                    // 预置需 setStream 重新裁剪（耗时），期间显示加载遮罩。
                    // 同时预置锁屏 + 桌面两侧，防止官方编辑器把另一侧重置为默认。
                    isPreparingWallpaper = true
                    WallpaperSnapshotBridge.prepareEditSet(context, editSet, which = 2) { ok ->
                        isPreparingWallpaper = false
                        if (ok) {
                            onOpenWallpaper(editedMode, "lock")
                        } else {
                            // 预置失败：不打开编辑器，也不在返回时误捕获保存
                            pendingWallpaperCapture = false
                        }
                    }
                }
                fun openDesktopEditor() {
                    if (isPreparingWallpaper) return
                    pendingWallpaperCapture = true
                    // 同锁屏：基线直接用已刷新的 systemWallpaper
                    val baseline = systemWallpaper
                        ?: WallpaperSnapshotBridge.readCachedCurrent(context)
                    if (baseline?.lock == null || baseline.desktop == null) {
                        isPreparingWallpaper = true
                        WallpaperSnapshotBridge.captureCurrent(context) { fresh ->
                            isPreparingWallpaper = false
                            if (fresh != null) {
                                systemWallpaper = fresh
                                wallpaperRefreshTick++
                                openDesktopEditor()
                            } else {
                                pendingWallpaperCapture = false
                            }
                        }
                        return
                    }
                    preEditSystem = baseline
                    val saved = editedMode.settings.wallpaper?.desktop
                    val hasSavedDesktop = saved != null && !saved.imagePath.isNullOrEmpty()
                    val modeWallpaper = editedMode.settings.wallpaper
                    val savedLock = modeWallpaper?.lock
                    val hasSavedLock = savedLock != null && (
                        !savedLock.lockscreenJson.isNullOrEmpty() ||
                            !savedLock.imagePath.isNullOrEmpty()
                        )
                    val editSet = WallpaperSet(
                        lock = if (hasSavedLock) copyBaselineItem(context, savedLock) else baseline.lock,
                        desktop = if (hasSavedDesktop) copyBaselineItem(context, saved) else baseline.desktop
                    )
                    beforeWallpaper = WallpaperSet(
                        lock = if (hasSavedLock) copyBaselineItem(context, savedLock) else baseline.lock,
                        desktop = if (hasSavedDesktop) copyBaselineItem(context, saved) else baseline.desktop
                    )
                    isPreparingWallpaper = true
                    WallpaperSnapshotBridge.prepareEditSet(context, editSet, which = 1) { ok ->
                        isPreparingWallpaper = false
                        if (ok) {
                            onOpenWallpaper(editedMode, "desktop")
                        } else {
                            pendingWallpaperCapture = false
                        }
                    }
                }
                Box {
                    WallpaperOverviewCard(
                        wallpaper = editedMode.settings.wallpaper,
                        systemWallpaper = systemWallpaper,
                        refreshTick = wallpaperRefreshTick,
                        onLockClick = ::openLockEditor,
                        onDesktopClick = ::openDesktopEditor,
                        onClear = {
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(wallpaper = null)
                            )
                            onSave(editedMode)
                            // 恢复初始后立即刷新系统当前壁纸，预览切到系统当前状态
                            WallpaperSnapshotBridge.captureCurrent(context) { snap ->
                                if (snap != null) systemWallpaper = snap
                            }
                            wallpaperRefreshTick++
                        },
                        modifier = Modifier
                    )
                    // 预置壁纸到系统期间的加载遮罩（setStream 重新裁剪耗时）
                    if (isPreparingWallpaper) {
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }
            }

            // 通知过滤条件 section
            item {
                SmallTitle(
                    text = stringResource(R.string.notif_filter),
                    modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    insideMargin = PaddingValues(0.dp)
                ) {
                    Column {
                        // Allow all notifications (inverse of DND)
                        SettingItem(
                            title = stringResource(R.string.allow_all_notifications),
                            subtitle = "",
                            checked = !editedMode.settings.enableDnd,
                            onCheckedChange = { allowAll ->
                                editedMode = editedMode.copy(
                                    settings = editedMode.settings.copy(enableDnd = !allowAll)
                                )
                                onSave(editedMode)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Contacts row: who may interrupt (none / all / starred).
                        // Official behavior: People/Apps are hidden while 允许所有通知 is on.
                        if (editedMode.settings.enableDnd) {
                            SettingItem(
                                title = stringResource(R.string.contacts),
                                subtitle = when (editedMode.settings.contactFilter) {
                                    CONTACT_FILTER_ALL -> stringResource(R.string.all_contacts)
                                    CONTACT_FILTER_STARRED -> stringResource(R.string.starred_contacts)
                                    else -> stringResource(R.string.no_contacts)
                                },
                                checked = false,
                                onCheckedChange = {},
                                onClick = { showContactDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Apps row: which apps may interrupt
                            SettingItem(
                                title = stringResource(R.string.apps),
                                subtitle = if (editedMode.settings.allowedApps.isEmpty()) {
                                    stringResource(R.string.no_apps_except)
                                } else {
                                    ""
                                },
                                checked = false,
                                onCheckedChange = {},
                                onClick = { onOpenApps(editedMode) },
                                modifier = Modifier.fillMaxWidth(),
                                subtitleContent = if (editedMode.settings.allowedApps.isEmpty()) {
                                    null
                                } else {
                                    {
                                        AppIconStack(
                                            packageNames = editedMode.settings.allowedApps,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                    }
                                }
                            )
                        }
                    }
                }
            }

            // Paused apps entry in its own Card immediately after notification filters
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    insideMargin = PaddingValues(0.dp)
                ) {
                    SettingItem(
                        title = stringResource(R.string.paused_apps),
                        subtitle = if (editedMode.settings.pausedApps.isEmpty()) {
                            stringResource(R.string.no_apps_paused)
                        } else {
                            ""
                        },
                        checked = false,
                        onCheckedChange = {},
                        onClick = { onOpenPausedApps(editedMode) },
                        modifier = Modifier.fillMaxWidth(),
                        subtitleContent = if (editedMode.settings.pausedApps.isEmpty()) {
                            null
                        } else {
                            {
                                AppIconStack(
                                    packageNames = editedMode.settings.pausedApps,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            }
                        }
                    )
                }
            }

            // 更多设置 section
            item {
                SmallTitle(
                    text = stringResource(R.string.more_settings),
                    modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    insideMargin = PaddingValues(0.dp)
                ) {
                    Column {
                        // Display settings row with dynamic summary
                        SettingItem(
                            title = stringResource(R.string.display_settings),
                            subtitle = displayOptionsSummary(editedMode.settings),
                            checked = false,
                            onCheckedChange = {},
                            onClick = { onOpenDisplayOptions(editedMode) },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Device Control entry in the More settings Card
                        SettingItem(
                            title = stringResource(R.string.device_control),
                            subtitle = deviceControlSummary(editedMode.settings),
                            checked = false,
                            onCheckedChange = {},
                            onClick = { onOpenDeviceControl(editedMode) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Bottom spacer with navigation bar padding
            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }

        // Dialogs

        // Trigger Selection Dialog
TriggerSelectionDialog(
            show = showTriggerSelector,
            onDismissRequest = { 
                showTriggerSelector = false
                if (showCompoundTriggerDialog) {
                    // If we were adding to compound trigger, go back
                    onShowCompoundTriggerDialogChange(true)
                }
            },
            onSelect = { type ->
                showTriggerSelector = false
                when (type) {
                    "time" -> showTimePicker = true
                    "app" -> {
                        onOpenAppTriggerPicker(editedMode)
                    }
                    "wifi" -> {
                        onOpenWifiTriggerPicker(editedMode)
                    }
                    "bluetooth" -> {
                        onOpenBluetoothTriggerPicker(editedMode)
                    }
                    "location" -> {
                        onOpenLocationTriggerPicker(editedMode)
                    }
                    "intent" -> {
                        onOpenIntentTriggerPicker(editedMode)
                    }
                    "battery" -> showBatteryPicker = true
                    "music" -> {
                        val trigger = ModeTrigger.Music
                        if (isAddingToCompound) {
                            // Adding to compound trigger
                            onEditingCompoundTriggersChange(editingCompoundTriggers + trigger)
                            onShowCompoundTriggerDialogChange(true)
                        } else if (editingGroupIndex != null) {
                            // Editing single trigger group
                            val newGroup = ModeTriggerGroup.Single(trigger)
                            editedMode = editedMode.copy(settings = editedMode.settings.copy(
                                triggerGroups = editedMode.settings.triggerGroups.mapIndexed { i, g ->
                                    if (i == editingGroupIndex) newGroup else g
                                }
                            ))
                            onSave(editedMode)
                            editingGroupIndex = null
                        } else {
                            // Adding new single trigger group (v2.0)
                            val newGroup = ModeTriggerGroup.Single(trigger)
                            editedMode = editedMode.copy(settings = editedMode.settings.copy(
                                triggerGroups = editedMode.settings.triggerGroups + newGroup
                            ))
                            onSave(editedMode)
                        }
                    }
                }
            }
        )

        // v2.0 Trigger Type Selection Dialog
        TriggerTypeSelectionDialog(
            show = showTriggerTypeDialog,
            onDismissRequest = { showTriggerTypeDialog = false },
            onSelectSingle = {
                showTriggerTypeDialog = false
                editingGroupIndex = null
                showTriggerSelector = true
            },
            onSelectCompound = {
                showTriggerTypeDialog = false
                editingGroupIndex = null
                // editingCompoundTriggers = emptyList() // Keep existing triggers when reopening
                editingCompoundName = null
                onShowCompoundTriggerDialogChange(true)
            }
        )

        // v2.0 Compound Trigger Edit Dialog
        CompoundTriggerEditDialog(
            show = showCompoundTriggerDialog,
            initialTriggers = editingCompoundTriggers,
            initialName = editingCompoundName,
            onDismissRequest = {
                onShowCompoundTriggerDialogChange(false)
                editingGroupIndex = null
                onEditingCompoundTriggersChange(emptyList())
                editingCompoundName = null
                onIsAddingToCompoundChange(false)
            },
            onConfirm = { triggers, name ->
                val newGroup = ModeTriggerGroup.Compound(triggers = triggers, name = name)
                editedMode = if (editingGroupIndex != null) {
                    editedMode.copy(settings = editedMode.settings.copy(
                        triggerGroups = editedMode.settings.triggerGroups.mapIndexed { i, g ->
                            if (i == editingGroupIndex) newGroup else g
                        }
                    ))
                } else {
                    editedMode.copy(settings = editedMode.settings.copy(
                        triggerGroups = editedMode.settings.triggerGroups + newGroup
                    ))
                }
                onSave(editedMode)
                onShowCompoundTriggerDialogChange(false)
                editingGroupIndex = null
                onEditingCompoundTriggersChange(emptyList())
                editingCompoundName = null
                onIsAddingToCompoundChange(false)
            },
            onAddTrigger = {
                // Keep dialog open - do not close it
                // onShowCompoundTriggerDialogChange(false)
                onIsAddingToCompoundChange(true)
                showTriggerSelector = true
            }

        )

        // Start-time picker for a new time trigger; confirming chains into
        // the end-time picker below.
        TimePickerDialog(
            title = stringResource(R.string.start_time),
            initialHour = ModeSchedule().startHour,
            initialMinute = ModeSchedule().startMinute,
            show = showTimePicker,
            onDismissRequest = {
                showTimePicker = false
                if (isAddingToCompound) {
                    onShowCompoundTriggerDialogChange(true)
                }
            },
            onConfirm = { h, m ->
                pendingStartTime = h to m
                showTimePicker = false
                showEndTimePicker = true
            }
        )

        // End-time picker, pre-filled with start + 1h. Overnight windows
        // (end earlier than start) are supported by the schedule engine.
        TimePickerDialog(
            title = stringResource(R.string.end_time),
            initialHour = pendingStartTime?.let { (it.first + 1) % 24 } ?: ModeSchedule().endHour,
            initialMinute = pendingStartTime?.second ?: ModeSchedule().endMinute,
            show = showEndTimePicker,
            onDismissRequest = {
                pendingStartTime = null
                showEndTimePicker = false
            },
            onConfirm = { endH, endM ->
                val start = pendingStartTime
                if (start != null) {
                    val newTrigger = ModeTrigger.Time(
                        ModeSchedule(
                            enabled = true,
                            startHour = start.first,
                            startMinute = start.second,
                            endHour = endH,
                            endMinute = endM
                        )
                    )
                    // v2.0: Add to trigger groups or compound trigger
                    if (isAddingToCompound) {
                        // Adding to compound trigger
                        onEditingCompoundTriggersChange(editingCompoundTriggers + newTrigger)
                        onShowCompoundTriggerDialogChange(true)
                    } else if (editingGroupIndex != null) {
                        // Editing single trigger group
                        val newGroup = ModeTriggerGroup.Single(newTrigger)
                        editedMode = editedMode.copy(settings = editedMode.settings.copy(
                            triggerGroups = editedMode.settings.triggerGroups.mapIndexed { i, g ->
                                if (i == editingGroupIndex) newGroup else g
                            }
                        ))
                        onSave(editedMode)
                        editingGroupIndex = null
                    } else {
                        // Adding new single trigger group (v2.0)
                        val newGroup = ModeTriggerGroup.Single(newTrigger)
                        editedMode = editedMode.copy(settings = editedMode.settings.copy(
                            triggerGroups = editedMode.settings.triggerGroups + newGroup
                        ))
                        onSave(editedMode)
                    }
                }
                pendingStartTime = null
                showEndTimePicker = false
            }
        )

        // Battery-level trigger picker
        BatteryTriggerPickerDialog(
            initialThreshold = 20,
            initialOperator = "below",
            show = showBatteryPicker,
            onDismissRequest = {
                showBatteryPicker = false
                if (isAddingToCompound) {
                    onShowCompoundTriggerDialogChange(true)
                }
            },
            onConfirm = { threshold, operator ->
                val newTrigger = ModeTrigger.Battery(
                    threshold = threshold,
                    operator = operator
                )
                // v2.0: Add to trigger groups or compound trigger
                if (isAddingToCompound) {
                    // Adding to compound trigger
                    onEditingCompoundTriggersChange(editingCompoundTriggers + newTrigger)
                    onShowCompoundTriggerDialogChange(true)
                } else if (editingGroupIndex != null) {
                    // Editing single trigger group
                    val newGroup = ModeTriggerGroup.Single(newTrigger)
                    editedMode = editedMode.copy(settings = editedMode.settings.copy(
                        triggerGroups = editedMode.settings.triggerGroups.mapIndexed { i, g ->
                            if (i == editingGroupIndex) newGroup else g
                        }
                    ))
                    onSave(editedMode)
                    editingGroupIndex = null
                } else {
                    // Adding new single trigger group (v2.0)
                    val newGroup = ModeTriggerGroup.Single(newTrigger)
                    editedMode = editedMode.copy(settings = editedMode.settings.copy(
                        triggerGroups = editedMode.settings.triggerGroups + newGroup
                    ))
                    onSave(editedMode)
                }
                showBatteryPicker = false
            }
        )

        // 就寝提醒 lead-time picker
        SleepReminderDialog(
            show = showReminderDialog,
            current = DeskClockState.reminderMinutes,
            onDismiss = { showReminderDialog = false },
            onSelect = { minutes ->
                context.sendBroadcast(android.content.Intent(Protocol.ACTION_SET_SLEEP_REMINDER).apply {
                    setPackage(Protocol.TARGET_PACKAGE)
                    putExtra(Protocol.EXTRA_REMINDER_MINUTES, minutes)
                })
                showReminderDialog = false
            }
        )

        // Contact filter picker
        ContactFilterDialog(
            show = showContactDialog,
            current = editedMode.settings.contactFilter,
            onDismiss = { showContactDialog = false },
            onSelect = { filter ->
                editedMode = editedMode.copy(
                    settings = editedMode.settings.copy(contactFilter = filter)
                )
                onSave(editedMode)
                showContactDialog = false
            }
        )

        // Delete confirmation dialog
        top.yukonga.miuix.kmp.overlay.OverlayDialog(
            show = showDeleteConfirm,
            onDismissRequest = { showDeleteConfirm = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete_mode_title),
                    style = MiuixTheme.textStyles.title3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = stringResource(R.string.delete_mode_confirm, localizedName),
                    style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        text = stringResource(R.string.cancel),
                        onClick = { showDeleteConfirm = false },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        text = stringResource(R.string.delete),
                        onClick = {
                            showDeleteConfirm = false
                            onDelete(editedMode)
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }

        // 修改模式 dialog
        modeToEdit?.let { mode ->
            EditModeDialog(
                show = showEditDialog,
                mode = mode,
                isNew = isCreatingNewMode,
                onDismissRequest = onDismissEdit,
                onDone = onDoneEdit
            )
        }
    }
}
