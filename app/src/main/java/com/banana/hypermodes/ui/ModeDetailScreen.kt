package com.banana.hypermodes.ui

import android.bluetooth.BluetoothManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.components.TimePickerDialog
import com.banana.hypermodes.ui.components.TriggerSelectionDialog
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
    onOpenDrivingBluetoothPicker: (Mode) -> Unit,
    onOpenDeviceControl: (Mode) -> Unit,
    onOpenDrivingDetect: (Mode) -> Unit,
    onRename: (Mode) -> Unit,
    onDelete: (Mode) -> Unit,
    onSave: (Mode) -> Unit,
    showEditDialog: Boolean,
    modeToEdit: Mode?,
    isCreatingNewMode: Boolean,
    onDismissEdit: () -> Unit,
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

    // v1.3 Trigger UI State
    var showTriggerSelector by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

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

            // Complex trigger UI (v1.3)
            if (editedMode.id != "bedtime" && editedMode.id != "driving" && editedMode.id != "dnd") {
                item {
                    SmallTitle(
                        text = stringResource(R.string.when_to_turn_on),
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                // List of existing triggers
                editedMode.settings.triggers.forEach { trigger ->
                    item {
                        TriggerCard(
                            trigger = trigger,
                            onDelete = {
                                val newList = editedMode.settings.triggers.filter { it != trigger }
                                editedMode = editedMode.copy(
                                    settings = editedMode.settings.copy(triggers = newList)
                                )
                                onSave(editedMode)
                            }
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
                        onClick = { showTriggerSelector = true }
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
                                    stringResource(R.string.apps_except, editedMode.settings.allowedApps.size)
                                },
                                checked = false,
                                onCheckedChange = {},
                                onClick = { onOpenApps(editedMode) },
                                modifier = Modifier.fillMaxWidth()
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
                            stringResource(R.string.apps_paused, editedMode.settings.pausedApps.size)
                        },
                        checked = false,
                        onCheckedChange = {},
                        onClick = { onOpenPausedApps(editedMode) },
                        modifier = Modifier.fillMaxWidth()
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
            onDismissRequest = { showTriggerSelector = false },
            onSelect = { type ->
                showTriggerSelector = false
                when (type) {
                    "time" -> showTimePicker = true
                    "app" -> onOpenAppTriggerPicker(editedMode)
                    "wifi" -> onOpenWifiTriggerPicker(editedMode)
                    "bluetooth" -> onOpenBluetoothTriggerPicker(editedMode)
                    "music" -> {
                        if (!editedMode.settings.triggers.contains(ModeTrigger.Music)) {
                            val newTriggers = editedMode.settings.triggers + ModeTrigger.Music
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(triggers = newTriggers)
                            )
                            onSave(editedMode)
                        }
                    }
                }
            }
        )

        // Time Picker for adding a new time trigger
        TimePickerDialog(
            title = stringResource(R.string.trigger_time),
            initialHour = ModeSchedule().startHour,
            initialMinute = ModeSchedule().startMinute,
            show = showTimePicker,
            onDismissRequest = { showTimePicker = false },
            onConfirm = { h, m ->
                val newTrigger = ModeTrigger.Time(
                    ModeSchedule(
                        enabled = true,
                        startHour = h,
                        startMinute = m,
                        endHour = (h + 1) % 24,
                        endMinute = m
                    )
                )
                if (!editedMode.settings.triggers.contains(newTrigger)) {
                    val newTriggers = editedMode.settings.triggers + newTrigger
                    editedMode = editedMode.copy(
                        settings = editedMode.settings.copy(triggers = newTriggers)
                    )
                    onSave(editedMode)
                }
                showTimePicker = false
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
fun TriggerCard(
    trigger: ModeTrigger,
    onDelete: () -> Unit
) {
    val icon = when (trigger) {
        is ModeTrigger.Time -> "🕐"
        is ModeTrigger.App -> "📱"
        is ModeTrigger.Wifi -> "📶"
        is ModeTrigger.Bluetooth -> "🎧"
        is ModeTrigger.Music -> "🎵"
    }

    val label = when (trigger) {
        is ModeTrigger.Time -> stringResource(
            R.string.trigger_at_time,
            String.format("%02d:%02d", trigger.schedule.startHour, trigger.schedule.startMinute),
            String.format("%02d:%02d", trigger.schedule.endHour, trigger.schedule.endMinute)
        )
        is ModeTrigger.App -> {
            val appCount = trigger.packageNames.size
            if (appCount <= 1) {
                stringResource(R.string.trigger_on_app, trigger.packageNames.firstOrNull() ?: "")
            } else {
                stringResource(
                    R.string.trigger_on_app_multi,
                    trigger.packageNames.firstOrNull() ?: "",
                    appCount - 1
                )
            }
        }
        is ModeTrigger.Wifi -> stringResource(R.string.trigger_on_wifi, trigger.ssids.firstOrNull() ?: "")
        is ModeTrigger.Bluetooth -> stringResource(R.string.trigger_on_bluetooth, trigger.deviceAddresses.firstOrNull() ?: "")
        is ModeTrigger.Music -> stringResource(R.string.trigger_on_music)
    }

    TriggerRowCard(icon = icon, label = label, onDelete = onDelete)
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
