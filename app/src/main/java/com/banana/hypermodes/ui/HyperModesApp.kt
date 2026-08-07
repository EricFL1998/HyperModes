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

private const val PREF_NAME = "hypermodes_prefs"
private const val KEY_DRIVING_SETUP = "driving_setup_done"
private const val KEY_BEDTIME_DELETED = "bedtime_deleted"

sealed class Screen {
    object MainTabs : Screen()
    object ModesList : Screen()
    object BedtimeIntro : Screen()
    object DrivingIntro : Screen()
    data class ModeDetail(val mode: Mode) : Screen()
    data class DisplayOptions(val mode: Mode) : Screen()
    data class DeviceControl(val mode: Mode) : Screen()
    data class Repeat(val mode: Mode) : Screen()
    data class CustomRepeat(val mode: Mode) : Screen()
    data class DrivingDetect(val mode: Mode) : Screen()
    data class AppPicker(val mode: Mode, val paused: Boolean = false) : Screen()
    data class AppTriggerPicker(val mode: Mode) : Screen()
    data class WifiTriggerPicker(val mode: Mode) : Screen()
    data class BluetoothTriggerPicker(val mode: Mode) : Screen()
    data class LocationTriggerPicker(val mode: Mode) : Screen()
    data class IntentTriggerPicker(val mode: Mode) : Screen()
    data class DrivingBluetoothPicker(val mode: Mode) : Screen()
    data class EditAutomation(val automationId: String) : Screen()
    }

/** Official ordering: DND, Bedtime, Driving, then custom modes by name. */
private fun sortModes(list: List<Mode>): List<Mode> = list.sortedWith(
    compareBy(
        { when (it.id) { "dnd" -> 0; "bedtime" -> 1; "driving" -> 2; else -> 3 } },
        { it.name }
    )
)

@Composable
fun HyperModesApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    var currentScreen by remember {
        // No landing page: the module hooks system_server, so a reboot is
        // required anyway — go straight to the main tabs (modes/automations).
        mutableStateOf<Screen>(Screen.MainTabs)
    }
    // Remember which main tab the user is on so returning from a detail
    // screen (e.g. automation editor) lands back on the same tab.
    var mainTabPage by remember { mutableStateOf(0) }

    // Restore the last known schedule immediately so the UI never flashes
    // placeholder times while waiting for the hook's first reply.
    LaunchedEffect(Unit) {
        DeskClockState.restore(context)
        RefreshRateManager.initialize(context)
    }

    // Auto-update check on startup
    var updateInfo by remember { mutableStateOf<UpdateInfo?>(null) }
    var showUpdateDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        val currentVersion = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
        val info = UpdateManager.fetchIfNewer(currentVersion)
        if (info != null) {
            updateInfo = info
            showUpdateDialog = true
        }
    }

    // Listen for schedule/state updates broadcast back from the DeskClock hook.
    DisposableEffect(Unit) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(c: Context, intent: android.content.Intent) {
                if (intent.hasExtra(Protocol.EXTRA_SLEEP_HOUR)) {
                    DeskClockState.update(
                        sleepHour = intent.getIntExtra(Protocol.EXTRA_SLEEP_HOUR, 22),
                        sleepMin = intent.getIntExtra(Protocol.EXTRA_SLEEP_MIN, 30),
                        wakeHour = intent.getIntExtra(Protocol.EXTRA_WAKE_HOUR, 7),
                        wakeMin = intent.getIntExtra(Protocol.EXTRA_WAKE_MIN, 0),
                        wakeEnabled = intent.getBooleanExtra(Protocol.EXTRA_WAKE_ENABLED, false),
                        repeatDays = intent.getIntExtra(Protocol.EXTRA_REPEAT_DAYS, 0x7F),
                        configured = intent.getBooleanExtra(Protocol.EXTRA_BEDTIME_CONFIGURED, true),
                        reminderMinutes = intent.getIntExtra(Protocol.EXTRA_REMINDER_MINUTES, 15)
                    )
                    DeskClockState.persist(context)
                }
                if (intent.hasExtra(Protocol.EXTRA_IN_SLEEP_MODE)) {
                    DeskClockState.updateBedtimeActive(
                        context,
                        intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)
                    )
                }
            }
        }
        androidx.core.content.ContextCompat.registerReceiver(
            context, receiver,
            android.content.IntentFilter(Protocol.ACTION_RESULT),
            androidx.core.content.ContextCompat.RECEIVER_EXPORTED
        )
        onDispose { context.unregisterReceiver(receiver) }
    }

    // Re-query the schedule every time the app comes back to the foreground,
    // so changes made in the Clock app are reflected immediately.
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                context.sendBroadcast(Intent(Protocol.ACTION_QUERY_SCHEDULE).apply {
                    setPackage(Protocol.TARGET_PACKAGE)
                })
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Shared holder for the mode being edited. Sub-pages (display options,
    // repeat, apps) mutate this instead of navigating, so toggling an option
    // never pops back to the detail page.
    var editingMode by remember { mutableStateOf<Mode?>(null) }
    
    // State for the Edit/Create Mode popup dialog
    var showEditDialog by remember { mutableStateOf(false) }
    var modeToEditInDialog by remember { mutableStateOf<Mode?>(null) }
    var isCreatingNewModeInDialog by remember { mutableStateOf(false) }
    var isAddingToCompound by remember { mutableStateOf(false) }
    var pendingCompoundTrigger by remember { mutableStateOf<ModeTrigger?>(null) }
    var showCompoundTriggerDialog by remember { mutableStateOf(false) }
    var editingCompoundTriggers by remember { mutableStateOf<List<ModeTrigger>>(emptyList()) }
    var automationToRename by remember { mutableStateOf<SavedAutomation?>(null) }
    var showRenameAutomationDialog by remember { mutableStateOf(false) }
    var automationRefreshTrigger by remember { mutableStateOf(0) }


    // The user's mode list (built-ins minus deleted ones + custom modes),
    // persisted via ModeStore. Bedtime always sorts first.
    var modes by remember { mutableStateOf<List<Mode>>(emptyList()) }
    LaunchedEffect(Unit) {
        // Card state mirrors the engine (activeModeId via ModeStore.load) —
        // never DeskClock status directly, which races the command round trip
        // and flaps the card until the backend catches up.
        modes = sortModes(ModeStore.load(context) { DefaultModes.get() })
    }

    fun persistModes(updated: List<Mode>) {
        modes = updated
        ModeStore.save(context, updated)
        context.sendBroadcast(
            Intent(Protocol.ACTION_RESCHEDULE).setPackage(context.packageName)
        )
    }

    fun upsertMode(mode: Mode) {
        val idx = modes.indexOfFirst { it.id == mode.id }
        persistModes(
            if (idx >= 0) modes.toMutableList().apply { set(idx, mode) }
            else modes + mode
        )
    }

    // Refresh the mode list whenever the engine writes the config (scheduled
    // trigger, bedtime push, toggle from elsewhere) while the UI is alive.
    // The mode-state broadcast is not reliably delivered to the app process,
    // so observe the Settings.Global key the engine writes on every change.
    DisposableEffect(Unit) {
        val resolver = context.contentResolver
        val observer = object : android.database.ContentObserver(
            android.os.Handler(android.os.Looper.getMainLooper())
        ) {
            override fun onChange(selfChange: Boolean) {
                modes = sortModes(ModeStore.load(context) { DefaultModes.get() })
                editingMode?.let { current ->
                    val latest = modes.firstOrNull { it.id == current.id }
                    if (latest != null) {
                        editingMode = current.copy(enabled = latest.enabled)
                    }
                }
            }
        }
        resolver.registerContentObserver(
            android.provider.Settings.Global.getUriFor(ModeStore.CONFIG_KEY),
            false,
            observer
        )
        onDispose { resolver.unregisterContentObserver(observer) }
    }

    // Always follow the system's dark mode (no in-app toggle).
    MiuixTheme(
        colors = if (androidx.compose.foundation.isSystemInDarkTheme()) {
            top.yukonga.miuix.kmp.theme.darkColorScheme()
        } else {
            top.yukonga.miuix.kmp.theme.lightColorScheme()
        }
    ) {
        // Solid backdrop behind the sliding screens — without it the white
        // window background flashes through during enter/exit transitions.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MiuixTheme.colorScheme.background)
        ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                val forward = targetState.depth() > initialState.depth()
                val slide = (slideInHorizontally { if (forward) it else -it } + fadeIn())
                    .togetherWith(slideOutHorizontally { if (forward) -it else it } + fadeOut())
                slide.using(SizeTransform(clip = false))
            },
            label = "screen"
        ) { screen ->
            when (screen) {
                is Screen.MainTabs -> {
                    MainTabsScreen(
                        modes = modes,
                        initialPage = mainTabPage,
                        onPageChange = { page -> mainTabPage = page },
                        onBack = { (context as? android.app.Activity)?.finish() },
                        onModeClick = { mode ->
                            when {
                                // Bedtime shows the intro page when never set up in the
                                // Clock app, or right after the user deleted the mode.
                                mode.id == "bedtime" && (!DeskClockState.configured ||
                                        prefs.getBoolean(KEY_BEDTIME_DELETED, false)) -> {
                                    currentScreen = Screen.BedtimeIntro
                                }
                                // Driving intro until the user has set it up once
                                mode.id == "driving" && !prefs.getBoolean(KEY_DRIVING_SETUP, false) -> {
                                    currentScreen = Screen.DrivingIntro
                                }
                                else -> {
                                    editingMode = mode
                                    currentScreen = Screen.ModeDetail(mode)
                                }
                            }
                        },
                        onCreateCustom = {
                            val newMode = Mode(
                                id = "custom_${System.currentTimeMillis()}",
                                name = context.getString(R.string.custom_mode_default),
                                icon = "⭐",
                                description = "",
                                settings = com.banana.hypermodes.data.ModeSettings(
                                    drivingAutoDetect = false,
                                    schedule = com.banana.hypermodes.data.ModeSchedule(enabled = false)
                                )
                            )
                            modeToEditInDialog = newMode
                            isCreatingNewModeInDialog = true
                            showEditDialog = true
                        },
                        onRestoreBuiltIn = { builtIn ->
                            upsertMode(builtIn)
                        },
                        showEditDialog = showEditDialog,
                        modeToEdit = modeToEditInDialog,
                        isCreatingNewMode = isCreatingNewModeInDialog,
                        onDismissEdit = { showEditDialog = false },
                        onDoneEdit = { done ->
                            upsertMode(done)
                            if (isCreatingNewModeInDialog) {
                                editingMode = done
                                currentScreen = Screen.ModeDetail(done)
                            }
                        },
                        onCreateAutomation = { name, icon ->
                            val newAutomation = SavedAutomation(
                                name = name,
                                icon = icon,
                                blocks = emptyList()
                            )
                            AutomationStore.add(context, newAutomation)
                            automationRefreshTrigger++
                            currentScreen = Screen.EditAutomation(newAutomation.id)
                        },
                        onEditAutomation = { automation ->
                            currentScreen = Screen.EditAutomation(automation.id)
                        },
                    )
                }
                is Screen.ModesList -> {
                    ModesListScreen(
                        modes = modes,
                        // Mirror官方 SubSettings.onNavigateUp(): finish the activity.
                        onBack = { (context as? android.app.Activity)?.finish() },
                        onModeClick = { mode ->
                            when {
                                // Bedtime shows the intro page when never set up in the
                                // Clock app, or right after the user deleted the mode.
                                mode.id == "bedtime" && (!DeskClockState.configured ||
                                        prefs.getBoolean(KEY_BEDTIME_DELETED, false)) -> {
                                    currentScreen = Screen.BedtimeIntro
                                }
                                // Driving intro until the user has set it up once
                                mode.id == "driving" && !prefs.getBoolean(KEY_DRIVING_SETUP, false) -> {
                                    currentScreen = Screen.DrivingIntro
                                }
                                else -> {
                                    editingMode = mode
                                    currentScreen = Screen.ModeDetail(mode)
                                }
                            }
                        },
                        onCreateCustom = {
                            val newMode = Mode(
                                id = "custom_${System.currentTimeMillis()}",
                                name = context.getString(R.string.custom_mode_default),
                                icon = "⭐",
                                description = "",
                                settings = com.banana.hypermodes.data.ModeSettings(
                                    drivingAutoDetect = false,
                                    schedule = com.banana.hypermodes.data.ModeSchedule(enabled = false)
                                )
                            )
                            modeToEditInDialog = newMode
                            isCreatingNewModeInDialog = true
                            showEditDialog = true
                        },
                        onRestoreBuiltIn = { builtIn ->
                            upsertMode(builtIn)
                        },
                        showEditDialog = showEditDialog,
                        modeToEdit = modeToEditInDialog,
                        isCreatingNewMode = isCreatingNewModeInDialog,
                        onDismissEdit = { showEditDialog = false },
                        onDoneEdit = { done ->
                            upsertMode(done)
                            if (isCreatingNewModeInDialog) {
                                editingMode = done
                                currentScreen = Screen.ModeDetail(done)
                            }
                        },
                    )
                }
                is Screen.BedtimeIntro -> {
                    BedtimeIntroScreen(
                        onBack = { currentScreen = Screen.MainTabs },
                        onSetup = {
                            // User went through the landing page — don't gate on it again.
                            prefs.edit().putBoolean(KEY_BEDTIME_DELETED, false).apply()
                        }
                    )
                }
                is Screen.DrivingIntro -> {
                    DrivingIntroScreen(
                        onBack = { currentScreen = Screen.MainTabs },
                        onSetup = {
                            prefs.edit().putBoolean(KEY_DRIVING_SETUP, true).apply()
                            val driving = modes.firstOrNull { it.id == "driving" }
                                ?: DefaultModes.get().first { it.id == "driving" }
                            editingMode = driving
                            currentScreen = Screen.ModeDetail(driving)
                        }
                    )
                }
                is Screen.ModeDetail -> {
                    val currentMode = editingMode ?: screen.mode
                    val latestMode = modes.firstOrNull { it.id == currentMode.id }
                    if (latestMode != null && latestMode.enabled != currentMode.enabled) {
                        editingMode = currentMode.copy(enabled = latestMode.enabled)
                    }
                    ModeDetailScreen(
                        mode = editingMode ?: currentMode,
                        onBack = { currentScreen = Screen.MainTabs },
                        onOpenDisplayOptions = { updated ->
                            editingMode = updated
                            currentScreen = Screen.DisplayOptions(updated)
                        },
                        onOpenRepeat = { updated ->
                            editingMode = updated
                            currentScreen = Screen.Repeat(updated)
                        },
                        onOpenApps = { updated ->
                            editingMode = updated
                            currentScreen = Screen.AppPicker(updated)
                        },
                        onOpenPausedApps = { updated ->
                            editingMode = updated
                            currentScreen = Screen.AppPicker(updated, paused = true)
                        },
                        onOpenAppTriggerPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.AppTriggerPicker(updated)
                        },
                        onOpenWifiTriggerPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.WifiTriggerPicker(updated)
                        },
                        onOpenBluetoothTriggerPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.BluetoothTriggerPicker(updated)
                        },
                        onOpenLocationTriggerPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.LocationTriggerPicker(updated)
                        },
                        onOpenIntentTriggerPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.IntentTriggerPicker(updated)
                        },
                        onOpenDrivingBluetoothPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.DrivingBluetoothPicker(updated)
                        },
                        onOpenDeviceControl = { updated ->
                            editingMode = updated
                            currentScreen = Screen.DeviceControl(updated)
                        },
                        onOpenDrivingDetect = { updated ->
                            editingMode = updated
                            currentScreen = Screen.DrivingDetect(updated)
                        },
                        onRename = { updated ->
                            modeToEditInDialog = updated
                            isCreatingNewModeInDialog = false
                            showEditDialog = true
                        },
                        onDelete = { deleted ->
                            if (deleted.enabled) {
                                ModeControlBridge.deactivateMode(context, deleted.id)
                            }
                            persistModes(modes.filterNot { it.id == deleted.id })
                            when (deleted.id) {
                                // Re-adding driving shows the landing page again.
                                "driving" -> prefs.edit()
                                    .putBoolean(KEY_DRIVING_SETUP, false).apply()
                                // Deleting bedtime fully disables it in the Clock app
                                // and gates the re-added mode behind the landing page.
                                "bedtime" -> {
                                    prefs.edit().putBoolean(KEY_BEDTIME_DELETED, true).apply()
                                    context.sendBroadcast(Intent(Protocol.ACTION_DISABLE_BEDTIME).apply {
                                        setPackage(Protocol.TARGET_PACKAGE)
                                    })
                                }
                            }
                            editingMode = null
                            currentScreen = Screen.MainTabs
                        },
                        onSave = { updatedMode ->
                            editingMode = updatedMode
                            upsertMode(updatedMode)
                        },
                        showEditDialog = showEditDialog,
                        modeToEdit = modeToEditInDialog,
                        isCreatingNewMode = isCreatingNewModeInDialog,
                        onDismissEdit = { showEditDialog = false },
                        onDoneEdit = { done ->
                            upsertMode(done)
                            // If we were in the detail screen, update the local editing state
                            if (editingMode?.id == done.id) {
                                editingMode = done
                            }
                        },
                        isAddingToCompound = isAddingToCompound,
                        onIsAddingToCompoundChange = { isAddingToCompound = it },
                        showCompoundTriggerDialog = showCompoundTriggerDialog,
                        onShowCompoundTriggerDialogChange = { showCompoundTriggerDialog = it },
                        pendingCompoundTrigger = pendingCompoundTrigger,
                        onPendingCompoundTriggerConsumed = { pendingCompoundTrigger = null },
                        editingCompoundTriggers = editingCompoundTriggers,
                        onEditingCompoundTriggersChange = { editingCompoundTriggers = it },
                    )
                }
                is Screen.DisplayOptions -> {
                    DisplayOptionsScreen(
                        mode = editingMode ?: screen.mode,
                        onBack = { currentScreen = Screen.ModeDetail(editingMode ?: screen.mode) },
                        onSave = { updatedMode ->
                            editingMode = updatedMode
                            upsertMode(updatedMode)
                        }
                    )
                }
                is Screen.DeviceControl -> {
                    DeviceControlScreen(
                        mode = editingMode ?: screen.mode,
                        onBack = { currentScreen = Screen.ModeDetail(editingMode ?: screen.mode) },
                        onSave = { updatedMode ->
                            editingMode = updatedMode
                            upsertMode(updatedMode)
                        }
                    )
                }
                is Screen.Repeat -> {
                    val mode = editingMode ?: screen.mode
                    RepeatScreen(
                        schedule = mode.settings.schedule ?: com.banana.hypermodes.data.ModeSchedule(),
                        onBack = { currentScreen = Screen.ModeDetail(mode) },
                        onOpenCustom = { currentScreen = Screen.CustomRepeat(mode) },
                        onSelect = { newSchedule ->
                            val updated = mode.copy(
                                settings = mode.settings.copy(schedule = newSchedule)
                            )
                            editingMode = updated
                            // Only the bedtime schedule lives in the Clock app;
                            // custom-mode schedules are stored locally.
                            if (mode.id == "bedtime") {
                                sendScheduleToDeskClock(context, newSchedule)
                            } else {
                                upsertMode(updated)
                            }
                            currentScreen = Screen.ModeDetail(updated)
                        }
                    )
                }
                is Screen.CustomRepeat -> {
                    val mode = editingMode ?: screen.mode
                    CustomRepeatScreen(
                        schedule = mode.settings.schedule ?: com.banana.hypermodes.data.ModeSchedule(),
                        onBack = { currentScreen = Screen.Repeat(mode) },
                        onSelect = { newSchedule ->
                            val updated = mode.copy(
                                settings = mode.settings.copy(schedule = newSchedule)
                            )
                            editingMode = updated
                            if (mode.id == "bedtime") {
                                sendScheduleToDeskClock(context, newSchedule)
                            } else {
                                upsertMode(updated)
                            }
                        }
                    )
                }
                is Screen.AppPicker -> {
                    val mode = editingMode ?: screen.mode
                    AppPickerScreen(
                        title = stringResource(if (screen.paused) R.string.paused_apps else R.string.select_apps),
                        initialSelection = if (screen.paused) mode.settings.pausedApps else mode.settings.allowedApps,
                        onBack = { currentScreen = Screen.ModeDetail(editingMode ?: screen.mode) },
                        onSelectionChanged = { selectedApps ->
                            val updated = mode.copy(
                                settings = if (screen.paused) {
                                    mode.settings.copy(pausedApps = selectedApps)
                                } else {
                                    mode.settings.copy(allowedApps = selectedApps)
                                }
                            )
                            editingMode = updated
                            upsertMode(updated)
                        }
                    )
                }
                is Screen.AppTriggerPicker -> {
                    val mode = editingMode ?: screen.mode
                    AppPickerScreen(
                        title = stringResource(R.string.trigger_app),
                        initialSelection = emptySet(),
                        singleSelection = true,
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSelectionChanged = { selectedApps ->
                            if (selectedApps.isNotEmpty()) {
                                val trigger = ModeTrigger.App(selectedApps)
                                if (isAddingToCompound) {
                                    pendingCompoundTrigger = trigger
                                    currentScreen = Screen.ModeDetail(editingMode ?: mode)
                                } else if (!mode.settings.triggers.contains(trigger)) {
                                    val updated = mode.copy(
                                        settings = mode.settings.copy(
                                            triggers = mode.settings.triggers + trigger
                                        )
                                    )
                                    editingMode = updated
                                }
                                // Navigate back to ModeDetail
                            if (!isAddingToCompound) {
                                currentScreen = Screen.ModeDetail(editingMode ?: mode)
                            }
                            }
                        }
                    )
                }
                is Screen.WifiTriggerPicker -> {
                    val mode = editingMode ?: screen.mode
                    WifiPickerScreen(
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSelect = { ssid ->
                            val trigger = ModeTrigger.Wifi(setOf(ssid))
                            if (isAddingToCompound) {
                                pendingCompoundTrigger = trigger
                                currentScreen = Screen.ModeDetail(editingMode ?: mode)
                            } else if (!mode.settings.triggers.contains(trigger)) {
                                val updated = mode.copy(
                                    settings = mode.settings.copy(
                                        triggers = mode.settings.triggers + trigger
                                    )
                                )
                                editingMode = updated
                            }
                            // Navigate back to ModeDetail
                            if (!isAddingToCompound) {
                                currentScreen = Screen.ModeDetail(editingMode ?: mode)
                            }
                        }
                    )
                }
                is Screen.BluetoothTriggerPicker -> {
                    val mode = editingMode ?: screen.mode
                    BluetoothPickerScreen(
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSelect = { device ->
                            val trigger = ModeTrigger.Bluetooth(setOf(device.address))
                            if (isAddingToCompound) {
                                pendingCompoundTrigger = trigger
                                currentScreen = Screen.ModeDetail(editingMode ?: mode)
                            } else if (!mode.settings.triggers.contains(trigger)) {
                                val updated = mode.copy(
                                    settings = mode.settings.copy(
                                        triggers = mode.settings.triggers + trigger
                                    )
                                )
                                editingMode = updated
                                // Let ModeDetailScreen's LaunchedEffect handle saving for compound triggers
                                // upsertMode(updated)
                            }
                            // Navigate back to ModeDetail
                            if (!isAddingToCompound) {
                                currentScreen = Screen.ModeDetail(editingMode ?: mode)
                            }
                        }
                    )
                }
                is Screen.LocationTriggerPicker -> {
                    val mode = editingMode ?: screen.mode
                    LocationTriggerPickerScreen(
                        mode = mode,
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSave = { updated ->
                            editingMode = updated
                            // Let ModeDetailScreen's LaunchedEffect handle saving for compound triggers
                            // upsertMode(updated)
                            // Navigate back to ModeDetail
                            currentScreen = Screen.ModeDetail(updated)
                        }
                    )
                }
                is Screen.IntentTriggerPicker -> {
                    val mode = editingMode ?: screen.mode
                    IntentTriggerPickerScreen(
                        mode = mode,
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSave = { updated ->
                            editingMode = updated
                            // Let ModeDetailScreen's LaunchedEffect handle saving for compound triggers
                            // upsertMode(updated)
                            // Navigate back to ModeDetail
                            currentScreen = Screen.ModeDetail(updated)
                        }
                    )
                }
                is Screen.DrivingBluetoothPicker -> {
                    val mode = editingMode ?: screen.mode
                    BluetoothPickerScreen(
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSelect = { device ->
                            if (!mode.settings.drivingTargetDevices.contains(device.address)) {
                                val updated = mode.copy(
                                    settings = mode.settings.copy(
                                        drivingTargetDevices =
                                            mode.settings.drivingTargetDevices + device.address
                                    )
                                )
                                editingMode = updated
                                upsertMode(updated)
                            }
                        }
                    )
                }
                is Screen.DrivingDetect -> {
                    DrivingDetectScreen(
                        mode = editingMode ?: screen.mode,
                        onBack = { currentScreen = Screen.ModeDetail(editingMode ?: screen.mode) },
                        onSave = { updatedMode ->
                            editingMode = updatedMode
                            upsertMode(updatedMode)
                        }
                    )
                }
                is Screen.EditAutomation -> {
                    // 从存储加载自动化进行编辑
                    val loaded = remember(screen) {
                        AutomationStore.load(context).find { it.id == screen.automationId }
                    }
                    if (loaded != null) {
                        AutomationEditorScreen(
                            automation = loaded,
                            onBack = {
                                mainTabPage = 1 // 返回时回到自动化 tab
                                currentScreen = Screen.MainTabs
                            },
                            onSave = { blocks ->
                                AutomationStore.update(context, loaded.copy(blocks = blocks))
                                automationRefreshTrigger++
                                mainTabPage = 1
                                currentScreen = Screen.MainTabs
                            },
                            onRename = { auto ->
                                automationToRename = auto
                                showRenameAutomationDialog = true
                            },
                            onDelete = { auto ->
                                AutomationStore.delete(context, auto.id)
                                automationRefreshTrigger++
                                mainTabPage = 1
                                currentScreen = Screen.MainTabs
                            }
                        )
                    } else {
                        LaunchedEffect(Unit) {
                            mainTabPage = 1
                            currentScreen = Screen.MainTabs
                        }
                    }
                }
                
            }
        }
        }

        // 修改/创建模式 dialog removed from here
        modeToEditInDialog?.let { mode ->
            EditModeDialog(
                show = showEditDialog,
                mode = mode,
                isNew = isCreatingNewModeInDialog,
                onDismissRequest = { showEditDialog = false },
                onDone = { done ->
                    upsertMode(done)
                    if (isCreatingNewModeInDialog) {
                        editingMode = done
                        currentScreen = Screen.ModeDetail(done)
                    } else {
                        if (editingMode?.id == done.id) {
                            editingMode = done
                        }
                    }
                }
            )
        }


        // Automation rename dialog
        automationToRename?.let { automation ->
            CreateAutomationDialog(
                show = showRenameAutomationDialog,
                initialName = automation.name,
                initialIcon = automation.icon,
                onDismissRequest = {
                    showRenameAutomationDialog = false
                    automationToRename = null
                },
                onDone = { name, icon ->
                    AutomationStore.update(context, automation.copy(name = name, icon = icon))
                    showRenameAutomationDialog = false
                    automationToRename = null
                    automationRefreshTrigger++
                }
            )
        }
        // Auto-update dialog
        updateInfo?.let { info ->
            val currentVersion = try {
                context.packageManager.getPackageInfo(context.packageName, 0).versionName ?: "1.0"
            } catch (e: Exception) {
                "1.0"
            }
            UpdateDialog(
                show = showUpdateDialog,
                currentVersion = currentVersion,
                updateInfo = info,
                onDismiss = { showUpdateDialog = false }
            )

        }
    }
}


/**
 * 创建模式 dialog (图九): always shows 自定义; each deleted built-in mode
 * (勿扰/睡眠/开车) is listed individually so it can be restored.
 */
@Composable
fun CreateModeDialog(
    show: Boolean,
    deletedBuiltIns: List<Mode>,
    onDismiss: () -> Unit,
    onCreateCustom: () -> Unit,
    onRestoreBuiltIn: (Mode) -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayBottomSheet(

        show = show,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.create_mode)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Custom entry
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onCreateCustom)
                            .padding(horizontal = 28.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "😊",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = stringResource(R.string.custom),
                            style = MiuixTheme.textStyles.body1,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                        )
                    }
                }

                // Deleted built-in modes, listed individually
                items(deletedBuiltIns.size) { index ->
                    val builtIn = deletedBuiltIns[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRestoreBuiltIn(builtIn) }
                            .padding(horizontal = 28.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = builtIn.icon,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = when (builtIn.id) {
                                    "dnd" -> stringResource(R.string.mode_dnd)
                                    "bedtime" -> stringResource(R.string.mode_bedtime)
                                    "driving" -> stringResource(R.string.mode_driving)
                                    else -> builtIn.name
                                },
                                style = MiuixTheme.textStyles.body1,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                            )
                            Text(
                                text = when (builtIn.id) {
                                    "dnd" -> stringResource(R.string.mode_dnd_desc)
                                    "bedtime" -> stringResource(R.string.mode_bedtime_desc)
                                    "driving" -> stringResource(R.string.mode_driving_desc)
                                    else -> builtIn.description
                                },
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Push a full schedule (incl. repeat days) to the DeskClock hook. */
private fun sendScheduleToDeskClock(context: Context, schedule: com.banana.hypermodes.data.ModeSchedule) {
    context.sendBroadcast(Intent(Protocol.ACTION_APPLY_SCHEDULE).apply {
        setPackage(Protocol.TARGET_PACKAGE)
        putExtra(Protocol.EXTRA_SLEEP_HOUR, schedule.startHour)
        putExtra(Protocol.EXTRA_SLEEP_MIN, schedule.startMinute)
        putExtra(Protocol.EXTRA_WAKE_HOUR, schedule.endHour)
        putExtra(Protocol.EXTRA_WAKE_MIN, schedule.endMinute)
        putExtra(Protocol.EXTRA_REPEAT_DAYS, schedule.repeatDays)
    })
}

/** Navigation depth of a screen, used to pick the slide direction. */
private fun Screen.depth(): Int = when (this) {
    is Screen.MainTabs -> 0
    is Screen.ModesList -> 0
    is Screen.BedtimeIntro, is Screen.DrivingIntro, is Screen.ModeDetail -> 1
    is Screen.DisplayOptions, is Screen.DeviceControl, is Screen.Repeat, is Screen.AppPicker,
    is Screen.AppTriggerPicker, is Screen.WifiTriggerPicker, is Screen.BluetoothTriggerPicker,
    is Screen.LocationTriggerPicker, is Screen.IntentTriggerPicker,
    is Screen.DrivingBluetoothPicker,
    is Screen.EditAutomation,
    is Screen.DrivingDetect -> 2
    is Screen.CustomRepeat -> 3
}

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
                        it.settings.triggers.any { trigger -> trigger is ModeTrigger.Time }
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




