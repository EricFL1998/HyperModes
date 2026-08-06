package com.banana.hypermodes.ui

import android.bluetooth.BluetoothManager
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
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
import com.banana.hypermodes.ui.components.TriggerTypeSelectionDialog
import com.banana.hypermodes.ui.components.CompoundTriggerEditDialog
import com.banana.hypermodes.ui.components.TriggerGroupCard
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
    onOpenLocationTriggerPicker: (Mode) -> Unit,
    onOpenIntentTriggerPicker: (Mode) -> Unit,
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

    // v1.3 Trigger UI State - keyed by mode.id to reset when mode changes
    var showTriggerSelector by remember(mode.id) { mutableStateOf(false) }
    var showTimePicker by remember(mode.id) { mutableStateOf(false) }
    var showEndTimePicker by remember(mode.id) { mutableStateOf(false) }
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



    LaunchedEffect(editedMode.settings.triggers) {
        if (editedMode.settings.triggers.isNotEmpty()) {
            // Find newly added triggers (not in triggerGroups yet)
            val existingTriggers = editedMode.settings.triggerGroups.flatMap { group ->
                when (group) {
                    is ModeTriggerGroup.Single -> listOf(group.trigger)
                    is ModeTriggerGroup.Compound -> group.triggers
                }
            }
            val newTriggers = editedMode.settings.triggers.filterNot { it in existingTriggers }
            
            if (newTriggers.isNotEmpty()) {
                // Skip if adding to compound - handled by pendingCompoundTrigger LaunchedEffect
                if (isAddingToCompound) {
                    editedMode = editedMode.copy(settings = editedMode.settings.copy(
                        triggers = emptyList()
                    ))
                    return@LaunchedEffect
                }

                    val alreadyInCompound = newTriggers.any { trigger ->
                        editingCompoundTriggers.contains(trigger)
                    }
                    if (alreadyInCompound) {
                    val newGroups = newTriggers.map { trigger ->
                        if (editingGroupIndex != null) {
                            // Editing existing single group
                            ModeTriggerGroup.Single(trigger)
                        } else {
                            // Create new single group
                            ModeTriggerGroup.Single(trigger)
                        }
                    }
                    
                    editedMode = if (editingGroupIndex != null) {
                        // Replace existing group
                        editedMode.copy(settings = editedMode.settings.copy(
                            triggerGroups = editedMode.settings.triggerGroups.mapIndexed { i, g ->
                                if (i == editingGroupIndex) newGroups.first() else g
                            },
                            triggers = emptyList() // Clear old triggers
                        ))
                    } else {
                        // Add new groups
                        editedMode.copy(settings = editedMode.settings.copy(
                            triggerGroups = editedMode.settings.triggerGroups + newGroups,
                            triggers = emptyList() // Clear old triggers
                        ))
                    }
                    onSave(editedMode)
                    if (editingGroupIndex != null) {
                        editingGroupIndex = null
                    }
            }
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
    val context = LocalContext.current

    val icon = when (trigger) {
        is ModeTrigger.Time -> "🕐"
        is ModeTrigger.App -> "📱"
        is ModeTrigger.Wifi -> "📶"
        is ModeTrigger.Bluetooth -> "🎧"
        is ModeTrigger.Music -> "🎵"
        is ModeTrigger.Location -> "📍"
        is ModeTrigger.Intent -> "📨"
    }

    // Resolve display names for App triggers; fall back to the package name
    // if the app was uninstalled after the trigger was created.
    val appNames = remember(trigger) {
        if (trigger is ModeTrigger.App) {
            val pm = context.packageManager
            trigger.packageNames.map { pkg ->
                try {
                    pm.getApplicationInfo(pkg, PackageManager.ApplicationInfoFlags.of(0))
                        .loadLabel(pm).toString()
                } catch (_: PackageManager.NameNotFoundException) {
                    pkg
                }
            }
        } else {
            emptyList()
        }
    }

    // Resolve bonded-device names for Bluetooth triggers; fall back to the
    // raw MAC when the device was unpaired or BLUETOOTH_CONNECT is denied.
    val btNames = remember(trigger) {
        if (trigger is ModeTrigger.Bluetooth) {
            val bonded = try {
                val manager = context.getSystemService(BluetoothManager::class.java)
                manager?.adapter?.bondedDevices?.associate { it.address to it.name }
            } catch (_: SecurityException) {
                null
            }
            trigger.deviceAddresses.map { mac -> bonded?.get(mac) ?: mac }
        } else {
            emptyList()
        }
    }

    val label = when (trigger) {
        is ModeTrigger.Time -> stringResource(
            R.string.trigger_at_time,
            String.format("%02d:%02d", trigger.schedule.startHour, trigger.schedule.startMinute),
            String.format("%02d:%02d", trigger.schedule.endHour, trigger.schedule.endMinute)
        )
        is ModeTrigger.App -> {
            val first = appNames.firstOrNull() ?: ""
            if (appNames.size <= 1) {
                stringResource(R.string.trigger_on_app, first)
            } else {
                stringResource(
                    R.string.trigger_on_app_multi,
                    first,
                    appNames.size - 1
                )
            }
        }
        is ModeTrigger.Wifi -> stringResource(R.string.trigger_on_wifi, trigger.ssids.firstOrNull() ?: "")
        is ModeTrigger.Bluetooth -> stringResource(
            R.string.trigger_on_bluetooth,
            btNames.firstOrNull() ?: trigger.deviceAddresses.firstOrNull() ?: ""
        )
        is ModeTrigger.Music -> stringResource(R.string.trigger_on_music)
        is ModeTrigger.Location -> {
            val locationName = trigger.target.addressName
                ?: trigger.target.cityName
                ?: stringResource(R.string.location_picker_title)
            when (trigger.transition) {
                LocationTransition.ARRIVE -> stringResource(R.string.trigger_on_location_arrive, locationName)
                LocationTransition.LEAVE -> stringResource(R.string.trigger_on_location_leave, locationName)
            }
        }
        is ModeTrigger.Intent -> {
            val parts = mutableListOf<String>()
            trigger.activateAction?.let { parts.add(stringResource(R.string.intent_activate) + ": $it") }
            trigger.deactivateAction?.let { parts.add(stringResource(R.string.intent_deactivate) + ": $it") }
            parts.joinToString(" / ")
        }
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
