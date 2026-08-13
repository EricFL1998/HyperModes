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

internal const val PREF_NAME = "hypermodes_prefs"
internal const val KEY_DRIVING_SETUP = "driving_setup_done"
internal const val KEY_BEDTIME_DELETED = "bedtime_deleted"

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

/**
 * 打开锁屏/桌面编辑器（com.miui.aod CommonEditorActivity）。
 * 与个性化顶部 SettingsTemplateView 的"自定义"按钮行为一致
 * （ContextUtilKt.toq -> CommonEditorActivity，action miui.keyguard.editor.common）：
 * caller=lock/desktop、argConfigPath=@MINE、whereFrom=homepage。
 * CommonEditorActivity 的 saveCallingSource 会用 isMiuiCall() 校验真实启动方是否在
 * CALL_PACKAGE_ALLOW 白名单（com.android.thememanager / com.miui.aod 等），非白名单
 * 调用会被立即 finish()。AodEditorHook 已 hook isMiuiCall()：系统记录的启动方为
 * com.banana.hypermodes 时放行，因此编辑器能正常停留（launched_from_package extra
 * 仅作为 saveCallingSource 里 launchFromPackage 的参考值，不影响白名单校验）。
 * 失败回退壁纸选择器 / WallpaperSettingsActivity / ThemeTabActivity。
 */
private fun openOfficialWallpaperUi(context: Context, caller: String = "lock") {
    val themePackage = "com.android.thememanager"

    // 1) 锁屏/桌面编辑器（与个性化顶部"自定义"按钮一致）。
    //    显式组件启动不依赖 resolveActivity，直接 try 顺序尝试。
    val attempts = listOf(
        Intent().apply {
            action = "miui.keyguard.editor.common"
            setClassName("com.miui.aod", "com.miui.keyguard.editor.CommonEditorActivity")
            putExtra("caller", caller)
            putExtra("argConfigPath", "@MINE")
            putExtra("argTemplateSource", -1L)
            putExtra("whereFrom", "homepage")
            // saveCallingSource 里 callingFromTheme() 用 launchFromPackage 判断，
            // 保持主题管家语义（仅影响追踪，白名单由 AodEditorHook 放行）
            putExtra("launched_from_package", "com.android.thememanager")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        },
        Intent().apply {
            action = "miui.intent.action.THEME_WALLPAPER_PICKER_PAGE_AOD"
            setClassName(themePackage, "com.android.thememanager.settings.ThemeAndWallpaperPickerSettingActivity")
            putExtra("openSource", 2)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        Intent().apply {
            setClassName(themePackage, "com.android.thememanager.settings.WallpaperSettingsActivity")
            putExtra("entrance", "homeEdit")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        Intent(Intent.ACTION_VIEW).apply {
            setClassName(themePackage, "com.android.thememanager.activity.ThemeTabActivity")
            putExtra("REQUEST_RESOURCE_CODE", "wallpaper")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    )
    for (intent in attempts) {
        try {
            context.startActivity(intent)
            return
        } catch (_: Exception) {
            // 继续尝试下一个兜底
        }
    }
}

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
        // 预热系统壁纸快照缓存：详情页进入时直接读缓存，零等待显示真实壁纸
        WallpaperSnapshotBridge.captureCurrent(context) { /* 缓存已落盘 */ }
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
                        onBack = {
                            mainTabPage = 0
                            currentScreen = Screen.MainTabs
                        },
                        onSetup = {
                            // User went through the landing page — don't gate on it again.
                            prefs.edit().putBoolean(KEY_BEDTIME_DELETED, false).apply()
                        }
                    )
                }
                is Screen.DrivingIntro -> {
                    DrivingIntroScreen(
                        onBack = {
                            mainTabPage = 0
                            currentScreen = Screen.MainTabs
                        },
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
                        onBack = {
                            mainTabPage = 0
                            currentScreen = Screen.MainTabs
                        },
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
                        onOpenWallpaper = { updated, caller ->
                            editingMode = updated
                            openOfficialWallpaperUi(context, caller)
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
                                } else if (mode.settings.triggerGroups.none {
                                        it is com.banana.hypermodes.data.ModeTriggerGroup.Single &&
                                            it.trigger == trigger
                                    }) {
                                    val updated = mode.copy(
                                        settings = mode.settings.copy(
                                            triggerGroups = mode.settings.triggerGroups +
                                                com.banana.hypermodes.data.ModeTriggerGroup.Single(trigger)
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
                            } else if (mode.settings.triggerGroups.none {
                                    it is com.banana.hypermodes.data.ModeTriggerGroup.Single &&
                                        it.trigger == trigger
                                }) {
                                val updated = mode.copy(
                                    settings = mode.settings.copy(
                                        triggerGroups = mode.settings.triggerGroups +
                                            com.banana.hypermodes.data.ModeTriggerGroup.Single(trigger)
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
                            } else if (mode.settings.triggerGroups.none {
                                    it is com.banana.hypermodes.data.ModeTriggerGroup.Single &&
                                        it.trigger == trigger
                                }) {
                                val updated = mode.copy(
                                    settings = mode.settings.copy(
                                        triggerGroups = mode.settings.triggerGroups +
                                            com.banana.hypermodes.data.ModeTriggerGroup.Single(trigger)
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
