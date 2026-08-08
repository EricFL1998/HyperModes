package com.banana.hypermodes.ui

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    if (a?.lockscreenJson != b?.lockscreenJson) return true
    return !sameImageFile(a.imagePath, b.imagePath)
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
 * 编辑会话结束后恢复真实系统壁纸/样式到编辑前状态。
 * 复用 prepareEdit 桥接（把 WallpaperItem 写回系统），
 * 模式未激活时调用，避免"在模式里编辑"改掉真实锁屏/桌面。
 */
private fun restoreEditSystem(context: Context, pre: WallpaperSet?) {
    if (pre == null) return
    pre.lock?.let { WallpaperSnapshotBridge.prepareEdit(context, it) { } }
    pre.desktop?.let { WallpaperSnapshotBridge.prepareEdit(context, it) { } }
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
    LaunchedEffect(mode.id) {
        // 先用上次缓存的预览立即显示，避免每次进详情页都等 1-2s 跨进程拉取
        systemWallpaper = WallpaperSnapshotBridge.readCachedCurrent(context)
        // 后台刷新最新快照
        WallpaperSnapshotBridge.captureCurrent(context) { snapshot ->
            if (snapshot != null) systemWallpaper = snapshot
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, mode.id) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && pendingWallpaperCapture) {
                pendingWallpaperCapture = false
                WallpaperSnapshotBridge.capture(context, editedMode.id) { snapshot ->
                    // 与打开前的系统快照逐子项对比：只保存用户实际修改的那一侧，
                    // 未修改的子项保留原配置（不误配置成系统当前值）。
                    val before = beforeWallpaper
                    if (snapshot != null) {
                        // 预览底图同步为编辑后的最新系统状态（未配置/恢复默认时立即生效）
                        systemWallpaper = snapshot
                        val prevWallpaper = editedMode.settings.wallpaper
                        val lockChanged = lockItemChanged(before, snapshot)
                        val desktopChanged = desktopItemChanged(before, snapshot)
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
                        // 编辑会话结束：模式未激活时恢复真实系统状态，
                        // 让"在模式里编辑"不改变真实锁屏/桌面（激活时模式样式本就应生效）
                        if (!editedMode.enabled) {
                            restoreEditSystem(context, preEditSystem)
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
                WallpaperOverviewCard(
                    wallpaper = editedMode.settings.wallpaper,
                    systemWallpaper = systemWallpaper,
                    onLockClick = {
                        pendingWallpaperCapture = true
                        // 记录编辑前真实系统状态（会话结束后恢复）
                        preEditSystem = systemWallpaper
                        val saved = editedMode.settings.wallpaper?.lock
                        val hasSavedLock = saved != null && (
                            !saved.lockscreenJson.isNullOrEmpty() ||
                                !saved.imagePath.isNullOrEmpty()
                            )
                        if (hasSavedLock) {
                            // 已有已保存样式：预置到系统，编辑器从它开始；
                            // 基线 = 保存的锁屏（壁纸图复制到临时文件，避免被
                            // 编辑后的捕获覆盖同一路径导致比较失效）+ 当前系统桌面。
                            beforeWallpaper = WallpaperSet(
                                lock = copyBaselineItem(context, editedMode.settings.wallpaper?.lock),
                                desktop = systemWallpaper?.desktop
                            )
                            WallpaperSnapshotBridge.prepareEdit(context, saved) { ok ->
                                if (ok) onOpenWallpaper(editedMode, "lock")
                            }
                        } else {
                            beforeWallpaper = systemWallpaper
                            onOpenWallpaper(editedMode, "lock")
                        }
                    },
                    onDesktopClick = {
                        pendingWallpaperCapture = true
                        // 记录编辑前真实系统状态（会话结束后恢复）
                        preEditSystem = systemWallpaper
                        val saved = editedMode.settings.wallpaper?.desktop
                        if (saved != null && !saved.imagePath.isNullOrEmpty()) {
                            // 基线 = 当前系统锁屏（锁屏未动）+ 保存的桌面。
                            beforeWallpaper = WallpaperSet(
                                lock = systemWallpaper?.lock,
                                desktop = copyBaselineItem(context, editedMode.settings.wallpaper?.desktop)
                            )
                            WallpaperSnapshotBridge.prepareEdit(context, saved) { ok ->
                                if (ok) onOpenWallpaper(editedMode, "desktop")
                            }
                        } else {
                            beforeWallpaper = systemWallpaper
                            onOpenWallpaper(editedMode, "desktop")
                        }
                    },
                    onClear = {
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(wallpaper = null)
                        )
                        onSave(editedMode)
                        // 恢复初始后立即刷新系统当前壁纸，预览切到系统当前状态
                        WallpaperSnapshotBridge.captureCurrent(context) { snap ->
                            if (snap != null) systemWallpaper = snap
                        }
                    },
                    modifier = Modifier
                )
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

@Composable
private fun AdaptiveHeroDescription(text: String) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val baseStyle = MiuixTheme.textStyles.body2
    val horizontalPadding = 32.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }
        val measuredWidth = textMeasurer.measure(
            text = text,
            style = baseStyle,
            maxLines = 1,
            softWrap = false,
            constraints = Constraints()
        ).size.width
        val scale = if (measuredWidth > maxWidthPx && measuredWidth > 0) {
            maxWidthPx.toFloat() / measuredWidth
        } else {
            1f
        }

        Text(
            text = text,
            style = baseStyle,
            fontSize = (baseStyle.fontSize.value * scale).coerceAtLeast(10f).sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun ContactFilterDialog(
    show: Boolean,
    current: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(R.string.select_contacts),
        show = show,
        onDismissRequest = onDismiss
    ) {
        Column {
            listOf(
                CONTACT_FILTER_NONE to stringResource(R.string.no_contacts),
                CONTACT_FILTER_ALL to stringResource(R.string.all_contacts),
                CONTACT_FILTER_STARRED to stringResource(R.string.starred_contacts)
            ).forEach { (filter, label) ->
                val selected = current == filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(filter) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selected) {
                        Text(
                            text = "✓",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                    Text(
                        text = label,
                        style = MiuixTheme.textStyles.body1,
                        color = if (selected) MiuixTheme.colorScheme.primary
                        else MiuixTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TriggerRowCard(
    icon: String,
    label: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MiuixTheme.textStyles.body1,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = MiuixIcons.Basic.Close,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

@Composable
fun displayOptionsSummary(settings: ModeSettings): String {
    val enabled = buildList {
        if (settings.hideNotifications) add(stringResource(R.string.setting_hide_notifications))
        settings.darkMode?.let { add(if (it == 1) stringResource(R.string.theme_dark) else stringResource(R.string.theme_light)) }
        if (settings.enableAod != null) add(stringResource(R.string.aod_option))
        if (settings.enableGrayscale != null) add(stringResource(R.string.grayscale_mode))
        if (settings.enableRefreshRate != null) add(stringResource(R.string.refresh_rate_option))
        if (settings.enableEyeCare != null) add(stringResource(R.string.eye_care_mode))
        if (settings.enableRaiseToWake != null) add(stringResource(R.string.raise_to_wake))
        if (settings.enableWakeForNotifications != null) add(stringResource(R.string.wake_for_notifications))
    }
    return when {
        enabled.isEmpty() -> stringResource(R.string.options_none)
        enabled.size <= 2 -> enabled.joinToString(stringResource(R.string.list_separator))
        else -> stringResource(R.string.and_n_more, enabled[0], enabled[1], enabled.size - 2)
    }
}

@Composable
fun deviceControlSummary(settings: ModeSettings): String {
    val enabled = buildList {
        settings.performanceMode?.let { mode ->
            val label = when (mode) {
                1 -> stringResource(R.string.performance_high)
                2 -> stringResource(R.string.performance_power_save)
                else -> stringResource(R.string.performance_balanced)
            }
            add(label)
        }
        if (settings.enable5g != null) add(stringResource(R.string.five_g_network))
        if (settings.enableWifi != null) add(stringResource(R.string.wifi_control))
        if (settings.enableBluetooth != null) add(stringResource(R.string.bluetooth_control))
    }
    return when {
        enabled.isEmpty() -> stringResource(R.string.options_none)
        enabled.size <= 2 -> enabled.joinToString(stringResource(R.string.list_separator))
        else -> stringResource(R.string.and_n_more, enabled[0], enabled[1], enabled.size - 2)
    }
}

/** Clickable settings row with a value label on the right (图三/图四 style). */
@Composable
fun ValueSettingItem(
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.body1
                )
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = MiuixIcons.Basic.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

/** 就寝提醒 value text: 准时 / 提前15分钟 / 提前30分钟 / 提前1小时 / 无. */
@Composable
fun reminderSummary(minutes: Int): String = when (minutes) {
    -1 -> stringResource(R.string.reminder_none)
    0 -> stringResource(R.string.reminder_on_time)
    60 -> stringResource(R.string.reminder_hour_before)
    else -> stringResource(R.string.reminder_minutes_before, minutes)
}

/** 就寝提醒 picker with the official DeskClock options (0/15/30/60/-1). */
@Composable
fun SleepReminderDialog(
    show: Boolean,
    current: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(R.string.sleep_reminder),
        show = show,
        onDismissRequest = onDismiss
    ) {
        Column {
            listOf(0, 15, 30, 60, -1).forEach { minutes ->
                val selected = current == minutes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(minutes) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selected) {
                        Text(
                            text = "✓",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                    Text(
                        text = reminderSummary(minutes),
                        style = MiuixTheme.textStyles.body1,
                        color = if (selected) MiuixTheme.colorScheme.primary
                        else MiuixTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    subtitleContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.body1
                )
                if (subtitleContent != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    subtitleContent()
                } else if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }
            if (onClick == null) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            } else {
                Icon(
                    imageVector = MiuixIcons.Basic.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

/**
 * Stacked app icons for the Apps / Paused apps rows: overlapping circular
 * icons with a small trailing gap instead of a "N apps" text summary.
 */
@Composable
fun AppIconStack(
    packageNames: Set<String>,
    modifier: Modifier = Modifier,
    maxIcons: Int = 5
) {
    val context = LocalContext.current
    val icons = remember(packageNames) {
        packageNames.take(maxIcons).mapNotNull { pkg ->
            runCatching {
                val pm = context.packageManager
                pm.getApplicationIcon(pkg)
                    .toStackIconBitmap()
            }.getOrNull()
        }
    }
    if (icons.isEmpty()) return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEachIndexed { index, bitmap ->
            val overlap = 12.dp
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = if (index == 0) 0.dp else -overlap * index)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MiuixTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            )
        }
        // Trailing whitespace after the stack
        Spacer(modifier = Modifier.width(8.dp))
    }
}

private fun android.graphics.drawable.Drawable.toStackIconBitmap(size: Int = 96): androidx.compose.ui.graphics.ImageBitmap =
    Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bmp ->
        val canvas = Canvas(bmp)
        setBounds(0, 0, size, size)
        draw(canvas)
    }.asImageBitmap()

@Composable
fun ScheduleCard(
    schedule: ModeSchedule,
    onScheduleChange: (ModeSchedule) -> Unit
) {
    val context = LocalContext.current
    var showSleepPicker by remember { mutableStateOf(false) }
    var showWakePicker by remember { mutableStateOf(false) }
    var showTurnOffDialog by remember { mutableStateOf(false) }

    fun sendToDeskClock(action: String, configure: android.content.Intent.() -> Unit = {}) {
        context.sendBroadcast(android.content.Intent(action).apply {
            setPackage(Protocol.TARGET_PACKAGE)
            configure()
        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            // Wake alarm toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.bedtime_alarm),
                        style = MiuixTheme.textStyles.body1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.bedtime_alarm_desc),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
                Switch(
                    checked = schedule.enabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            sendToDeskClock(
                                Protocol.ACTION_ENABLE_WAKE_ALARM
                            )
                            onScheduleChange(schedule.copy(enabled = true))
                        } else {
                            showTurnOffDialog = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sleep / Wake times side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeColumn(
                    icon = "🌙",
                    label = stringResource(R.string.sleep_time),
                    hour = schedule.startHour,
                    minute = schedule.startMinute,
                    onClick = { showSleepPicker = true },
                    modifier = Modifier.weight(1f)
                )
                TimeColumn(
                    icon = "☀️",
                    label = stringResource(R.string.wake_time),
                    hour = schedule.endHour,
                    minute = schedule.endMinute,
                    onClick = { showWakePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    TimePickerDialog(
        title = stringResource(R.string.sleep_time),
        initialHour = schedule.startHour,
        initialMinute = schedule.startMinute,
        show = showSleepPicker,
        onDismissRequest = { showSleepPicker = false },
        onConfirm = { hour, minute ->
            val newSchedule = schedule.copy(startHour = hour, startMinute = minute)
            onScheduleChange(newSchedule)
            applyScheduleToDeskClock(newSchedule) { action, h1, m1, h2, m2, days ->
                sendToDeskClock(action) {
                    putExtra(Protocol.EXTRA_SLEEP_HOUR, h1)
                    putExtra(Protocol.EXTRA_SLEEP_MIN, m1)
                    putExtra(Protocol.EXTRA_WAKE_HOUR, h2)
                    putExtra(Protocol.EXTRA_WAKE_MIN, m2)
                    putExtra(Protocol.EXTRA_REPEAT_DAYS, days)
                }
            }
        }
    )

    TimePickerDialog(
        title = stringResource(R.string.wake_time),
        initialHour = schedule.endHour,
        initialMinute = schedule.endMinute,
        show = showWakePicker,
        onDismissRequest = { showWakePicker = false },
        onConfirm = { hour, minute ->
            val newSchedule = schedule.copy(endHour = hour, endMinute = minute)
            onScheduleChange(newSchedule)
            applyScheduleToDeskClock(newSchedule) { action, h1, m1, h2, m2, days ->
                sendToDeskClock(action) {
                    putExtra(Protocol.EXTRA_SLEEP_HOUR, h1)
                    putExtra(Protocol.EXTRA_SLEEP_MIN, m1)
                    putExtra(Protocol.EXTRA_WAKE_HOUR, h2)
                    putExtra(Protocol.EXTRA_WAKE_MIN, m2)
                    putExtra(Protocol.EXTRA_REPEAT_DAYS, days)
                }
            }
        }
    )

    WakeAlarmTurnOffDialog(
        show = showTurnOffDialog,
        onDismiss = { showTurnOffDialog = false },
        onOnce = {
            sendToDeskClock(
                Protocol.ACTION_SKIP_WAKE_ALARM_ONCE
            )
            showTurnOffDialog = false
        },
        onAlways = {
            sendToDeskClock(
                Protocol.ACTION_DISABLE_WAKE_ALARM
            )
            showTurnOffDialog = false
            onScheduleChange(schedule.copy(enabled = false))
        }
    )
}

private fun applyScheduleToDeskClock(
    schedule: ModeSchedule,
    send: (action: String, h1: Int, m1: Int, h2: Int, m2: Int, days: Int) -> Unit
) {
    send(
        Protocol.ACTION_APPLY_SCHEDULE,
        schedule.startHour, schedule.startMinute,
        schedule.endHour, schedule.endMinute,
        schedule.repeatDays
    )
}

@Composable
fun WakeAlarmTurnOffDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onOnce: () -> Unit,
    onAlways: () -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(R.string.turn_off_alarm_title),
        show = show,
        onDismissRequest = onDismiss
    ) {
        Column {
            TextButton(
                text = stringResource(R.string.turn_off_once),
                onClick = onOnce,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.turn_off_always),
                onClick = onAlways,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TimeColumn(
    icon: String,
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MiuixTheme.textStyles.body2,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = label,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%02d:%02d", hour, minute),
            style = MiuixTheme.textStyles.headline1,
            fontSize = 44.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
