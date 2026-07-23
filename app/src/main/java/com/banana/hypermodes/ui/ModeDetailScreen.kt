package com.banana.hypermodes.ui

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
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.*
import com.banana.hypermodes.bridge.ModeControlBridge
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.components.TimePickerDialog
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleSurface
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
    onOpenDrivingDetect: (Mode) -> Unit,
    onRename: (Mode) -> Unit,
    onDelete: (Mode) -> Unit,
    onSave: (Mode) -> Unit,
    showEditDialog: Boolean,
    modeToEdit: Mode?,
    onDismissEdit: () -> Unit,
    onDoneEdit: (Mode) -> Unit
) {
    BackHandler(onBack = onBack)
    var editedMode by remember(mode) { mutableStateOf(mode) }
    val context = LocalContext.current
    var showContactDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showReminderDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
        // The 立即开启/关闭 button mirrors the OFFICIAL bedtime state: when
        // DeskClock activates bedtime on its own schedule (or from the Clock
        // app), the hook pushes it and this flips without user interaction.
        val bedtimeActive = DeskClockState.bedtimeActive
        LaunchedEffect(bedtimeActive) {
            if (editedMode.enabled != bedtimeActive) {
                editedMode = editedMode.copy(enabled = bedtimeActive)
                onSave(editedMode)
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
                                    // TODO: Activity Recognition now handled by DrivingTriggerManager in system_server
                                }
                            )
                        }
                    }
                }
            }

            // Custom modes: 何时自动开启 + time schedule (official "Set a schedule";
            // the manual DND mode has no trigger section in the official layout)
            if (editedMode.id != "bedtime" && editedMode.id != "driving" && editedMode.id != "dnd") {
                val schedule = editedMode.settings.schedule ?: ModeSchedule()
                item {
                    SmallTitle(
                        text = stringResource(R.string.when_to_turn_on),
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                if (!schedule.enabled) {
                    // Official default: a compact "＋ 设置时间表" row; tapping it
                    // enables the schedule and expands the full time editor.
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            onClick = {
                                editedMode = editedMode.copy(
                                    settings = editedMode.settings.copy(
                                        schedule = schedule.copy(enabled = true)
                                    )
                                )
                                onSave(editedMode)
                            }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "＋",
                                    style = MiuixTheme.textStyles.title2,
                                    color = MiuixTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.set_schedule),
                                    style = MiuixTheme.textStyles.body1
                                )
                            }
                        }
                    }
                } else {
                    item {
                        CustomScheduleCard(
                            schedule = schedule,
                            onScheduleChange = { newSchedule ->
                                editedMode = editedMode.copy(
                                    settings = editedMode.settings.copy(schedule = newSchedule)
                                )
                                onSave(editedMode)
                            }
                        )
                    }
                    item {
                        ValueSettingItem(
                            title = stringResource(R.string.repeat_cycle),
                            subtitle = "",
                            value = repeatSummary(schedule.repeatDays),
                            onClick = { onOpenRepeat(editedMode) },
                            modifier = Modifier
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp)
                        )
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

                        // Paused apps row: which apps get suspended while the mode is on
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
            }

            // Bottom spacer with navigation bar padding
            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }

        // Dialogs must live INSIDE the Scaffold content — OverlayDialog renders
        // via LocalDialogStates, which only the Scaffold provides.

        // 就寝提醒 lead-time picker: official options 0/15/30/60/-1
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

        // Contact filter picker: none / all / starred
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

        // Delete confirmation (official: 要删除“X”模式吗？)
        top.yukonga.miuix.kmp.overlay.OverlayDialog(
            show = showDeleteConfirm,
            onDismissRequest = { showDeleteConfirm = false }
        ) {
            Column {
                Text(
                    text = stringResource(R.string.delete_mode_confirm, localizedName),
                    style = MiuixTheme.textStyles.body1,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                TextButton(
                    text = stringResource(R.string.delete),
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(editedMode)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextButton(
                    text = stringResource(R.string.cancel),
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // 修改模式 dialog
        modeToEdit?.let { mode ->
            EditModeDialog(
                show = showEditDialog,
                mode = mode,
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
fun displayOptionsSummary(settings: ModeSettings): String {
    val enabled = buildList {
        if (settings.hideNotifications) add(stringResource(R.string.setting_hide_notifications))
        if (settings.enableGrayscale) add(stringResource(R.string.grayscale_mode))
        if (settings.keepScreenOff) add(stringResource(R.string.keep_screen_off))
        if (settings.dimWallpaper) add(stringResource(R.string.dim_wallpaper_option))
        if (settings.enableDarkMode) add(stringResource(R.string.dark_theme_option))
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
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = value,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantActions
            )
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
                            // Toggle ON: AlarmHelper.enableAlarm(context, MIN_VALUE, true)
                            sendToDeskClock(
                                Protocol.ACTION_ENABLE_WAKE_ALARM
                            )
                            onScheduleChange(schedule.copy(enabled = true))
                        } else {
                            // Toggle OFF: official app shows once/always popup
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

    // Turn-off dialog: once / always / cancel (matches official DeskClock ActionSheet)
    WakeAlarmTurnOffDialog(
        show = showTurnOffDialog,
        onDismiss = { showTurnOffDialog = false },
        onOnce = {
            // AlarmHelper.skipAlarmForOnce(context, MIN_VALUE) + registerWakeAlarm
            sendToDeskClock(
                Protocol.ACTION_SKIP_WAKE_ALARM_ONCE
            )
            showTurnOffDialog = false
            // Alarm stays enabled, only this occurrence is skipped
        },
        onAlways = {
            // AlarmHelper.enableAlarm(context, MIN_VALUE, false) + registerWakeAlarm
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

/**
 * Generic time-schedule card for custom modes (official "Set a schedule"):
 * enable switch + start/end times stored locally in the mode's settings.
 */
@Composable
fun CustomScheduleCard(
    schedule: ModeSchedule,
    onScheduleChange: (ModeSchedule) -> Unit
) {
    var showStartPicker by remember { mutableStateOf(false) }
    var showEndPicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.set_schedule),
                    style = MiuixTheme.textStyles.body1,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = schedule.enabled,
                    onCheckedChange = { onScheduleChange(schedule.copy(enabled = it)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeColumn(
                    icon = "🕐",
                    label = stringResource(R.string.start_time),
                    hour = schedule.startHour,
                    minute = schedule.startMinute,
                    onClick = { showStartPicker = true },
                    modifier = Modifier.weight(1f)
                )
                TimeColumn(
                    icon = "🕑",
                    label = stringResource(R.string.end_time),
                    hour = schedule.endHour,
                    minute = schedule.endMinute,
                    onClick = { showEndPicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    TimePickerDialog(
        title = stringResource(R.string.start_time),
        initialHour = schedule.startHour,
        initialMinute = schedule.startMinute,
        show = showStartPicker,
        onDismissRequest = { showStartPicker = false },
        onConfirm = { hour, minute ->
            onScheduleChange(schedule.copy(startHour = hour, startMinute = minute))
        }
    )

    TimePickerDialog(
        title = stringResource(R.string.end_time),
        initialHour = schedule.endHour,
        initialMinute = schedule.endMinute,
        show = showEndPicker,
        onDismissRequest = { showEndPicker = false },
        onConfirm = { hour, minute ->
            onScheduleChange(schedule.copy(endHour = hour, endMinute = minute))
        }
    )
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
