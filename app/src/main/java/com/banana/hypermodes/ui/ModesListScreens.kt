package com.banana.hypermodes.ui

import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.banana.hypermodes.R
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.data.ModeTrigger
import com.banana.hypermodes.data.WallpaperSnapshotBridge
import com.banana.hypermodes.automation.AutomationBlock
import com.banana.hypermodes.automation.SavedAutomation
import com.banana.hypermodes.automation.AutomationStore
import com.banana.hypermodes.bridge.ModeControlBridge
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.utils.UpdateManager
import com.banana.hypermodes.utils.UpdateInfo
import com.banana.hypermodes.utils.RefreshRateManager
import com.banana.hypermodes.ui.components.UpdateDialog
import com.banana.hypermodes.ui.components.BottomTabBar
import com.banana.hypermodes.ui.components.TabItem
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun MainTabsScreen(
    modes: List<Mode>,
    initialPage: Int = 0,
    onPageChange: (Int) -> Unit = {},
    onBack: () -> Unit,
    onModeClick: (Mode) -> Unit,
    onCreateCustom: () -> Unit,
    onRestoreBuiltIn: (Mode) -> Unit,
    showEditDialog: Boolean,
    modeToEdit: Mode?,
    isCreatingNewMode: Boolean,
    onDismissEdit: () -> Unit,
    onDoneEdit: (Mode) -> Unit,
    onCreateAutomation: (name: String, icon: String) -> Unit = { _, _ -> },
    onEditAutomation: (com.banana.hypermodes.automation.SavedAutomation) -> Unit = {},
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Local state for CreateModeDialog
    var showCreateDialog by remember { mutableStateOf(false) }
    var showCreateAutomationDialog by remember { mutableStateOf(false) }

    // Tab items - using MIUIX icons
    // 模式: Settings (represents switching/toggling modes)
    // 自动化: Refresh (represents automation/workflow/forward action)
    val tabs = remember {
        listOf(
            TabItem(icon = MiuixIcons.Settings, label = context.getString(R.string.modes)),
            TabItem(icon = MiuixIcons.Refresh, label = context.getString(R.string.automations))
        )
    }

    // Pager state for horizontal swiping
    val pagerState = rememberPagerState(initialPage = initialPage, pageCount = { tabs.size })

    // When returning from a detail screen (e.g. automation editor), rememberPagerState
    // restores the saved page instead of honoring the new initialPage. Force the pager
    // to the requested tab so back navigation lands on the correct tab.
    LaunchedEffect(initialPage) {
        if (pagerState.currentPage != initialPage) {
            pagerState.scrollToPage(initialPage)
        }
    }

    // 滑动 Pager 时同步 mainTabPage（底部 tab 点击已调用 onPageChange，
    // 但手指滑动不会，导致返回详情页时 initialPage 停留在旧页）。
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .collect { page -> onPageChange(page) }
    }

    // Full-screen root Box: pager/content layer, floating capsule overlay, FAB overlay
    // Wrap in Scaffold only for dialog support (no topBar/bottomBar)
    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0) // Don't consume system insets
    ) { scaffoldPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Content layer: pager with pages that have bottom padding for the floating capsule
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> {
                        // Modes tab
                        ModesListScreenContent(
                            modes = modes,
                            onBack = onBack,
                            onModeClick = onModeClick,
                            onCreateCustom = onCreateCustom,
                            onRestoreBuiltIn = onRestoreBuiltIn,
                            showEditDialog = showEditDialog,
                            modeToEdit = modeToEdit,
                            isCreatingNewMode = isCreatingNewMode,
                            onDismissEdit = onDismissEdit,
                            onDoneEdit = onDoneEdit,
                            showBackButton = true,
                            showFab = false, // FAB will be shown as overlay
                            showCreateDialog = false, // Dialog will be shown at root level
                            useFloatingLayout = true // Signal to use shared bottom padding
                        )
                    }
                    1 -> {
                        // Automations tab
                        AutomationsScreen(
                              onBack = onBack,
                              showBackButton = true,
                              showFab = false, // FAB will be shown as overlay
                              useFloatingLayout = true, // Signal to use shared bottom padding
                              onEditAutomation = onEditAutomation
                          )
                    }
                }
            }

            // Floating navigation capsule overlay - positioned absolutely at bottom
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = BottomLayoutGeometry.capsuleBottomOffset())
                    .padding(horizontal = 16.dp)
            ) {
                FloatingNavigationBar {
                    tabs.forEachIndexed { index, tab ->
                        FloatingNavigationBarItem(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                                onPageChange(index)
                            },
                            icon = tab.icon,
                            label = tab.label
                        )
                    }
                }
            }

            // Floating Action Button overlay - positioned absolutely above the capsule
            FloatingActionButton(
                onClick = {
                    if (pagerState.currentPage == 0) {
                        // Modes tab
                        val deleted = DefaultModes.get()
                            .filter { builtIn -> modes.none { it.id == builtIn.id } }
                        if (deleted.isNotEmpty()) {
                            showCreateDialog = true
                        } else {
                            onCreateCustom()
                        }
                    } else {
                        // Automations tab: 和模式一样，先选名字和图标
                        showCreateAutomationDialog = true
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp)
                    .padding(bottom = BottomLayoutGeometry.fabBottomOffset())
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    color = MiuixTheme.colorScheme.onPrimary
                )
            }

            // CreateModeDialog at the top level - will appear above bottom bar
            // CreateAutomationDialog - 与模式创建一致：选择名字和图标
            CreateAutomationDialog(
                show = showCreateAutomationDialog,
                onDismissRequest = { showCreateAutomationDialog = false },
                onDone = { name, icon ->
                    showCreateAutomationDialog = false
                    onCreateAutomation(name, icon)
                }
            )
            CreateModeDialog(
                show = showCreateDialog,
                deletedBuiltIns = DefaultModes.get()
                    .filter { builtIn -> modes.none { it.id == builtIn.id } },
                onDismiss = { showCreateDialog = false },
                onCreateCustom = {
                    showCreateDialog = false
                    onCreateCustom()
                },
                onRestoreBuiltIn = { builtIn ->
                    showCreateDialog = false
                    onRestoreBuiltIn(builtIn)
                }
            )

            // EditModeDialog at the top level
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
}

@Composable
fun ModesListScreen(
    modes: List<Mode>,
    onBack: () -> Unit,
    onModeClick: (Mode) -> Unit,
    onCreateCustom: () -> Unit,
    onRestoreBuiltIn: (Mode) -> Unit,
    showEditDialog: Boolean,
    modeToEdit: Mode?,
    isCreatingNewMode: Boolean,
    onDismissEdit: () -> Unit,
    onDoneEdit: (Mode) -> Unit,
) {
    ModesListScreenContent(
        modes = modes,
        onBack = onBack,
        onModeClick = onModeClick,
        onCreateCustom = onCreateCustom,
        onRestoreBuiltIn = onRestoreBuiltIn,
        showEditDialog = showEditDialog,
        modeToEdit = modeToEdit,
        isCreatingNewMode = isCreatingNewMode,
        onDismissEdit = onDismissEdit,
        onDoneEdit = onDoneEdit,
        showBackButton = true
    )
}

@Composable
fun ModesListScreenContent(
    modes: List<Mode>,
    onBack: () -> Unit,
    onModeClick: (Mode) -> Unit,
    onCreateCustom: () -> Unit,
    onRestoreBuiltIn: (Mode) -> Unit,
    showEditDialog: Boolean,
    modeToEdit: Mode?,
    isCreatingNewMode: Boolean,
    onDismissEdit: () -> Unit,
    onDoneEdit: (Mode) -> Unit,
    showBackButton: Boolean = true,
    showFab: Boolean = true,
    showCreateDialog: Boolean = true,
    useFloatingLayout: Boolean = false
) {
    val context = LocalContext.current
    var showCreateDialogLocal by remember { mutableStateOf(false) }
    
    // Long press delete state
    var menuMode by remember { mutableStateOf<Mode?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Ask the hook for the real schedule whenever this screen is shown.
    LaunchedEffect(Unit) {
        context.sendBroadcast(android.content.Intent(Protocol.ACTION_QUERY_SCHEDULE).apply {
            setPackage(Protocol.TARGET_PACKAGE)
        })
    }

    // Live schedule from DeskClock (null until the hook replies)
    val liveSchedule = DeskClockState.schedule

    val listPrefs = remember { context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    val drivingSetUp = listPrefs.getBoolean(KEY_DRIVING_SETUP, false)

    // Exact-alarm nudge: without it, scheduled modes may fire late.
    val alarmManager = remember {
        context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
    }
    val exactAlarmsMissing = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
            !alarmManager.canScheduleExactAlarms() &&
            modes.any {
                (it.settings.schedule?.enabled == true && it.id != "bedtime") ||
                        it.settings.triggerGroups.any { group ->
                            when (group) {
                                is com.banana.hypermodes.data.ModeTriggerGroup.Single ->
                                    group.trigger is ModeTrigger.Time
                                is com.banana.hypermodes.data.ModeTriggerGroup.Compound ->
                                    group.triggers.any { trigger -> trigger is ModeTrigger.Time }
                            }
                        }
            }

    // Format like "23:00" (24-hour)
    fun formatTime(hour: Int, minute: Int): String = "%02d:%02d".format(hour, minute)

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.modes),
                scrollBehavior = scrollBehavior,
                // Matches the official Settings detail page: a back arrow that
                // finishes the activity. Uses miuix's default navigation-icon /
                // title spacing so it lines up with the stock Settings UI.
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = MiuixIcons.Back,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                } else {
                    { }
                }
            )
        },
        floatingActionButton = if (showFab) {
            {
                FloatingActionButton(
                    onClick = {
                        val deleted = DefaultModes.get()
                            .filter { builtIn -> modes.none { it.id == builtIn.id } }
                        if (deleted.isNotEmpty()) {
                            showCreateDialogLocal = true
                        } else {
                            onCreateCustom()
                        }
                    }
                ) {
                    Text(
                        text = "+",
                        fontSize = 32.sp,
                        color = MiuixTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            { }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = if (useFloatingLayout) {
                // Floating layout: use shared geometry, but keep top padding from Scaffold
                PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = BottomLayoutGeometry.contentBottomPadding().calculateBottomPadding()
                )
            } else {
                // Standard layout: use Scaffold's top padding + navigationBarsPadding
                PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = 0.dp
                )
            }
        ) {
            // Description text
            item {
                Text(
                    text = stringResource(R.string.welcome_desc),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp)
                )
            }

            if (exactAlarmsMissing) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.exact_alarm_title),
                                style = MiuixTheme.textStyles.body1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.exact_alarm_desc),
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                text = stringResource(R.string.grant_permission),
                                colors = ButtonDefaults.textButtonColorsPrimary(),
                                onClick = {
                                    context.startActivity(
                                        Intent(
                                            android.provider.Settings
                                                .ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Top spacer for breathing room before first card
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Mode items
            items(modes.size) { index ->
                val mode = modes[index]
                ModeItem(
                    icon = mode.icon,
                    title = when (mode.id) {
                        "dnd" -> stringResource(R.string.mode_dnd)
                        "bedtime" -> stringResource(R.string.mode_bedtime)
                        "driving" -> stringResource(R.string.mode_driving)
                        else -> mode.name
                    },
                    subtitle = run {
                        // Official status-first summaries. Bedtime follows the
                        // Clock wake alarm: alarm on -> show the schedule time.
                        val schedule = if (mode.id == "bedtime") {
                            liveSchedule ?: mode.settings.schedule
                        } else {
                            mode.settings.schedule
                        }
                        // "每天 09:30 - 17:00" style schedule text.
                        @Composable
                        fun schedText(s: com.banana.hypermodes.data.ModeSchedule) =
                            repeatSummary(s.repeatDays) + " " +
                                    formatTime(s.startHour, s.startMinute) + " - " +
                                    formatTime(s.endHour, s.endMinute)
                        when {
                            mode.id == "driving" && !drivingSetUp ->
                                stringResource(R.string.not_set_up)
                            mode.enabled ->
                                // 进行中: "已启用 · 每天 09:30 - 17:00"
                                if (mode.id != "dnd" && mode.id != "driving" &&
                                    schedule?.enabled == true
                                ) {
                                    stringResource(R.string.mode_on) + " · " + schedText(schedule)
                                } else {
                                    stringResource(R.string.mode_on)
                                }
                            mode.id == "dnd" -> stringResource(R.string.mode_off)
                            mode.id == "driving" -> if (mode.settings.drivingAutoDetect) {
                                stringResource(R.string.mode_driving_desc)
                            } else {
                                stringResource(R.string.mode_off)
                            }
                            schedule?.enabled == true -> schedText(schedule)
                            else -> stringResource(R.string.mode_off)
                        }
                    },
                    onClick = {
                        // Inject the live schedule into the bedtime mode so the
                        // detail screen opens with the real Clock times.
                        val modeToOpen = if (mode.id == "bedtime" && liveSchedule != null) {
                            mode.copy(settings = mode.settings.copy(schedule = liveSchedule))
                        } else mode
                        onModeClick(modeToOpen)
                    },
                    onLongPress = {
                        menuMode = mode
                        showDeleteConfirm = true
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // Bottom spacer for navigation bar padding
            // In floating layout, contentPadding already reserves space,
            // but we need a final spacer to prevent scroll bounce
            item {
                if (!useFloatingLayout) {
                    Spacer(modifier = Modifier.height(if (showBackButton) 24.dp else 80.dp).navigationBarsPadding())
                } else {
                    // Minimal spacer to stabilize scroll in floating layout
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }

        // 创建模式 dialog: 自定义 + individually listed built-ins that were deleted.
        // Must live INSIDE the Scaffold content — OverlayDialog renders via
        // LocalDialogStates, which only the Scaffold provides.
        // Only show if showCreateDialog is true (for standalone ModesListScreen)
        if (showCreateDialog) {
            CreateModeDialog(
                show = showCreateDialogLocal,
                deletedBuiltIns = DefaultModes.get()
                    .filter { builtIn -> modes.none { it.id == builtIn.id } },
                onDismiss = { showCreateDialogLocal = false },
                onCreateCustom = {
                    showCreateDialogLocal = false
                    onCreateCustom()
                },
                onRestoreBuiltIn = { builtIn ->
                    showCreateDialogLocal = false
                    onRestoreBuiltIn(builtIn)
                }
            )
        }

        // 修改/创建模式 dialog - only show if showCreateDialog is true
        if (showCreateDialog) {
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
        
        // Delete confirmation dialog for modes
        menuMode?.let { mode ->
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
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    Text(
                        text = stringResource(
                            R.string.delete_mode_confirm,
                            when (mode.id) {
                                "dnd" -> stringResource(R.string.mode_dnd)
                                "bedtime" -> stringResource(R.string.mode_bedtime)
                                "driving" -> stringResource(R.string.mode_driving)
                                else -> mode.name
                            }
                        ),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            text = stringResource(android.R.string.cancel),
                            onClick = { showDeleteConfirm = false },
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            text = stringResource(R.string.delete),
                            onClick = {
                                showDeleteConfirm = false
                                // Delete mode by filtering it out (not calling onDoneEdit)
                                // This will be handled in the parent by not including it in the list
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeItem(
    icon: String,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp),
        insideMargin = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        cornerRadius = 36.dp,
        onClick = onClick,
        onLongPress = onLongPress
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                // Title using "Timer" style (headline1, medium weight)
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.headline1,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    // Subtitle using "Date/Repeat" style (body2)
                    Text(
                        text = subtitle,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
            // Icon placed on the right, mirroring MIUI style for auxiliary info
            Text(
                text = icon,
                style = MiuixTheme.textStyles.headline2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
