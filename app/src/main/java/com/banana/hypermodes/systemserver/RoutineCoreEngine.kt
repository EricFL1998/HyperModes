package com.banana.hypermodes.systemserver

import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.executor.ModeActionExecutor
import com.banana.hypermodes.systemserver.trigger.BedtimeListener
import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler
import com.banana.hypermodes.systemserver.trigger.DrivingTriggerManager
import com.banana.hypermodes.systemserver.trigger.ScheduledModeManager

/**
 * Core engine running inside system_server.
 * This is a singleton that manages all mode logic without any app process.
 *
 * Key responsibilities:
 * - Watch Settings.Global["pixel_routines_full_config"] for config changes
 * - Parse and store mode configurations
 * - Activate/deactivate modes on demand
 * - Maintain current active mode state
 */
class RoutineCoreEngine private constructor() {

    enum class LifecycleState {
        RUNNING,
        REPLACING,
        REMOVED
    }

    private var systemContext: Context? = null
    private var classLoader: ClassLoader? = null

    private var currentActiveMode: ModeConfig? = null

    /** When currentActiveMode was (re)activated (ms); 0 when no mode is active.
     * BedtimeListener compares this against bedtime-state broadcast times to
     * ignore stale state that predates the activation. */
    private var currentModeActivatedAt: Long = 0L

    private var allModes: List<ModeConfig> = emptyList()

    @Volatile
    private var lifecycleState = LifecycleState.RUNNING

    private val lifecycleLock = Any()

    // Track manually dismissed scheduled modes: modeId -> dismiss timestamp
    // When user manually closes a mode during its scheduled period,
    // it won't auto-reopen until the next scheduled period starts
    private val dismissedScheduledModes = mutableMapOf<String, Long>()

    private var drivingTriggerManager: DrivingTriggerManager? = null
    private var scheduledModeManager: ScheduledModeManager? = null
    private var bedtimeListener: BedtimeListener? = null
    private var modeActionExecutor: ModeActionExecutor? = null

    private var mainHandler: Handler? = null
    private var configObserverOwner: EngineObserverOwner? = null

    /**
     * Initialize the engine in system_server context.
     * Must be called from AMS.systemReady() or similar system_server initialization point.
     *
     * @param context System context (from system_server)
     * @param loader System_server ClassLoader for reflection
     */
    fun init(context: Context, loader: ClassLoader) {
        if (lifecycleState == LifecycleState.REMOVED) {
            // Check if it was reinstalled
            if (isPackageInstalled(context, com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)) {
                log("Engine was REMOVED but package is present, resetting state to RUNNING")
                lifecycleState = LifecycleState.RUNNING
            } else {
                log("Engine is REMOVED and package missing, skipping init")
                return
            }
        } else if (!isPackageInstalled(context, com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)) {
            log("Engine is RUNNING but package missing on boot, triggering shutdown")
            // Initialize mainHandler first so shutdown can post to it
            mainHandler = Handler(Looper.getMainLooper())
            shutdownForPackageRemoval()
            return
        }
        log("Initializing RoutineCoreEngine...")
        systemContext = context
        classLoader = loader
        mainHandler = Handler(Looper.getMainLooper())

        // Initialize components
        modeActionExecutor = ModeActionExecutor(context, loader)
        drivingTriggerManager = DrivingTriggerManager(context, this)
        scheduledModeManager = ScheduledModeManager(context, this)
        bedtimeListener = BedtimeListener(context, this).also {
            it.registerStateSources()
        }

        // Watch for config changes in Settings.Global
        observeConfigChanges(context)

        // Quick restore: show icon immediately if there's an active mode
        restoreActiveIcon(context)

        // Load full config asynchronously to avoid blocking systemReady
        mainHandler?.post {
            loadConfigFromSettings()
            log("RoutineCoreEngine initialized successfully")
        }
    }

    /**
     * Quickly restore the status bar icon if a mode was active before reboot.
     * This runs synchronously to show the icon as soon as possible.
     */
    private fun restoreActiveIcon(context: Context) {
        try {
            // Parse config to find the active mode's icon
            val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
            if (json.isNullOrBlank()) return

            val config = ConfigParser.parseConfig(json)
            val activeModeId = config.activeModeId
            if (activeModeId.isNullOrBlank()) return

            val activeMode = config.modes.find { it.id == activeModeId } ?: return

            // Show icon immediately
            modeActionExecutor?.run {
                val iconManager = javaClass.getDeclaredField("statusBarIconManager").apply {
                    isAccessible = true
                }[this] as? StatusBarIconManager

                // Use statusIcon if available, fallback to icon mapping
                val statusIcon = activeMode.statusIcon
                    ?: com.banana.hypermodes.data.ModeIconMapper.getStatusBarIcon(activeMode.icon)

                iconManager?.setIcon(statusIcon, activeMode.name)
                log("Quick restored icon for mode: ${activeMode.name} (icon=$statusIcon)")
            }
        } catch (e: Exception) {
            log("Failed to quick restore icon: ${e.message}")
        }
    }

    private fun observeConfigChanges(context: Context) {
        val handler = mainHandler ?: Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                if (lifecycleState == LifecycleState.REMOVED) return
                log("Config changed, reloading...")
                loadConfigFromSettings()
            }
        }

        val owner = EngineObserverOwner(
            registerAction = { registeredObserver ->
                context.contentResolver.registerContentObserver(
                    Settings.Global.getUriFor(CONFIG_KEY),
                    false,
                    registeredObserver
                )
            },
            unregisterAction = { registeredObserver ->
                context.contentResolver.unregisterContentObserver(registeredObserver)
            }
        )
        owner.register(observer)
        configObserverOwner = owner
        log("ContentObserver registered for $CONFIG_KEY")
    }

    /**
     * Load configuration from Settings.Global and update internal state.
     * Called on initialization and whenever config changes.
     */
    private fun loadConfigFromSettings() {
        val context = systemContext ?: return
        if (lifecycleState == LifecycleState.REMOVED) return

        if (!isPackageInstalled(context, com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)) {
            log("Package uninstalled but setting exists, triggering shutdown")
            shutdownForPackageRemoval()
            return
        }

        try {
            val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
            if (json.isNullOrBlank()) {
                log("No config found in Settings.Global[$CONFIG_KEY] - treating as empty")
                // treat as empty config: cancel schedules and revert current mode
                scheduledModeManager?.updateSchedules(emptyList())
                drivingTriggerManager?.init(emptyList())
                currentActiveMode?.let {
                    log("Reverting active mode due to missing config: ${it.name}")
                    modeActionExecutor?.revertMode(it)
                    currentActiveMode = null
                    currentModeActivatedAt = 0L
                }
                allModes = emptyList()
                return
            }

            val config = ConfigParser.parseConfig(json)
            allModes = config.modes
            log("Config loaded: ${allModes.size} modes")

            // Load persisted dismissal records
            dismissedScheduledModes.clear()
            dismissedScheduledModes.putAll(config.dismissedModes)
            if (dismissedScheduledModes.isNotEmpty()) {
                log("Loaded ${dismissedScheduledModes.size} dismissal records from config")
            }

            // Update schedulers with new mode list
            scheduledModeManager?.updateSchedules(config.modes)

            // Update driving trigger manager with new mode list
            drivingTriggerManager?.init(config.modes)

            // Handle active mode changes before synchronizing external trigger state.
            if (config.activeModeId != null) {
                if (currentActiveMode?.id != config.activeModeId) {
                    // Deactivate current mode if it's different from the new one
                    currentActiveMode?.let { oldMode ->
                        log("Deactivating current mode before switch: ${oldMode.name}")
                        modeActionExecutor?.revertMode(oldMode)
                    }

                    // Restore or switch to the specified active mode
                    val mode = allModes.find { it.id == config.activeModeId }
                    if (mode != null) {
                        log("Activating mode from config: ${mode.name}")
                        // Clear dismissal record when explicitly switching to this mode via config
                        clearDismissRecord(mode.id)
                        currentActiveMode = mode
                        currentModeActivatedAt = System.currentTimeMillis()
                        modeActionExecutor?.applyMode(mode)
                        // Manual UI activation comes through THIS config path, not
                        // activateMode() — route DeskClock sync through the reconciler.
                        if (mode.type == ModeType.BEDTIME) {
                            executeBedtimeDecisions(BedtimeReconciler.decide(
                                BedtimeReconciler.Event.ModeActivatedViaConfig(
                                    System.currentTimeMillis()
                                ),
                                bedtimeSnapshot()
                            ))
                        }
                    } else {
                        log("Active mode not found in config: ${config.activeModeId}")
                        currentActiveMode = null
                        currentModeActivatedAt = 0L
                    }
                } else {
                    log("Active mode unchanged: ${currentActiveMode?.name}")
                }
            } else {
                // activeModeId is null: deactivate current mode if one is active
                currentActiveMode?.let { activeMode ->
                    // When the user manually turns off a scheduled mode from the UI,
                    // ModeControlBridge writes activeModeId=null directly to
                    // Settings.Global, so RoutineCoreEngine.deactivateMode() is not
                    // invoked and no dismiss record is created. Record it here so
                    // the scheduler won't reactivate this mode later in the same
                    // period (e.g. when the user creates or deletes another mode).
                    if ((activeMode.type == ModeType.SCHEDULED) || (activeMode.type == ModeType.BEDTIME)) {
                        val now = System.currentTimeMillis()
                        dismissedScheduledModes[activeMode.id] = now
                        log("Recorded manual dismiss for mode ${activeMode.id} at timestamp $now (from config change)")

                        // Persist the dismissal record back to Settings.Global so it survives reboot.
                        // Only do this if the incoming config didn't already have it (to avoid loop).
                        if (config.dismissedModes[activeMode.id] != now) {
                            updateActiveModeInSettings(null)
                        }

                        // Manual bedtime dismiss via Settings.Global: command routing
                        // decided by the reconciler (skip-once in-window, exit outside).
                        if (activeMode.type == ModeType.BEDTIME) {
                            executeBedtimeDecisions(BedtimeReconciler.decide(
                                BedtimeReconciler.Event.ModeDeactivatedViaConfig(
                                    System.currentTimeMillis(),
                                    isInBedtimeWindow(activeMode)
                                ),
                                bedtimeSnapshot()
                            ))
                        }
                    }
                    log("Deactivating current mode: ${activeMode.name}")
                    modeActionExecutor?.revertMode(activeMode)
                    currentActiveMode = null
                    currentModeActivatedAt = 0L
                }
            }

            // Notify UI that state or config has changed
            broadcastModeState(config.activeModeId)
        } catch (e: Exception) {
            log("Failed to load config: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Activate a mode by ID.
     * Deactivates current mode first if one is active.
     * Updates Settings.Global to persist active mode.
     * Reschedules alarms after activation.
     *
     * @param modeId Mode identifier to activate
     */
    fun activateMode(modeId: String) {
        if (lifecycleState == LifecycleState.REMOVED) {
            log("Cannot activate mode: engine is REMOVED")
            return
        }
        val mode = allModes.find { it.id == modeId }
        if (mode == null) {
            log("Cannot activate mode: mode not found: $modeId")
            return
        }

        if (currentActiveMode?.id == modeId) {
            log("Mode already active: $modeId")
            return
        }

        log("Activating mode: ${mode.name} (id=$modeId)")

        // Clear any previous dismiss record when manually activating or reactivating
        clearDismissRecord(modeId)

        // Deactivate current mode first
        currentActiveMode?.let {
            log("Deactivating current mode: ${it.name}")
            modeActionExecutor?.revertMode(it)
        }

        // Apply new mode
        currentActiveMode = mode
        currentModeActivatedAt = System.currentTimeMillis()
        modeActionExecutor?.applyMode(mode)

        // Persist active mode to Settings.Global
        updateActiveModeInSettings(modeId)
        broadcastModeState(modeId)

        // Reschedule alarms (next occurrence after activation)
        scheduledModeManager?.updateSchedules(allModes)

        log("Mode activated successfully: ${mode.name}")
    }

    /**
     * Deactivate a mode by ID.
     * Reverts mode actions and clears active mode from Settings.Global.
     * Records dismiss timestamp for scheduled modes if manually dismissed.
     * Reschedules alarms after deactivation.
     *
     * @param modeId Mode identifier to deactivate
     * @param isManualDismiss true if user manually closed, false if automatic (e.g., end alarm)
     */
    fun deactivateMode(modeId: String, isManualDismiss: Boolean = true) {
        if (lifecycleState == LifecycleState.REMOVED) {
            log("Cannot deactivate mode: engine is REMOVED")
            return
        }
        val mode = currentActiveMode
        if (mode == null || mode.id != modeId) {
            log("Cannot deactivate mode: mode not active: $modeId")
            return
        }

        log("Deactivating mode: ${mode.name} (id=$modeId, manual=$isManualDismiss)")

        // Revert mode actions
        modeActionExecutor?.revertMode(mode)

        currentActiveMode = null
        currentModeActivatedAt = 0L

        // Record dismiss timestamp ONLY for manual dismissals of scheduled or bedtime modes
        if (isManualDismiss && (mode.type == ModeType.SCHEDULED || mode.type == ModeType.BEDTIME)) {
            val now = System.currentTimeMillis()
            dismissedScheduledModes[modeId] = now
            log("Recorded manual dismiss for mode $modeId at timestamp $now")
        }

        // Clear active mode from Settings.Global
        updateActiveModeInSettings(null)
        broadcastModeState(null)

        // Reschedule alarms (next occurrence after deactivation)
        scheduledModeManager?.updateSchedules(allModes)

        log("Mode deactivated successfully: ${mode.name}")
    }

    /**
     * Update the active mode ID and dismissal records in Settings.Global.
     * This keeps the persisted config in sync with runtime state.
     *
     * @param modeId Mode ID to set as active, or null to clear
     */
    private fun updateActiveModeInSettings(modeId: String?) {
        val context = systemContext ?: return

        try {
            val currentJson = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
            if (currentJson.isNullOrBlank()) {
                log("Cannot update active mode: no config in Settings.Global")
                return
            }

            val updated = ConfigParser.updateActiveModeId(currentJson, modeId, dismissedScheduledModes)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, updated)
            log("Updated state in Settings.Global (activeModeId=${modeId ?: "null"}, dismissals=${dismissedScheduledModes.size})")
        } catch (e: Exception) {
            log("Failed to update active mode: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun broadcastModeState(modeId: String?) {
        val context = systemContext ?: return
        try {
            val intent = Intent(com.banana.hypermodes.protocol.Protocol.ACTION_MODE_STATE).apply {
                setPackage(com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)
                if (modeId != null) {
                    putExtra(com.banana.hypermodes.protocol.Protocol.EXTRA_MODE_ID, modeId)
                }
            }
            context.sendBroadcast(intent)
            log("Broadcast mode state: ${modeId ?: "null"}")
        } catch (e: Exception) {
            log("Failed to broadcast mode state: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * True if now falls inside the bedtime mode's configured sleep window
     * ([startTime, endTime), overnight windows supported, repeatDays respected;
     * 1 = Monday .. 7 = Sunday). Unknown/unparseable schedule defaults to true,
     * preserving the legacy in-schedule behavior.
     */
    private fun isInBedtimeWindow(mode: ModeConfig): Boolean {
        val start = mode.startTime ?: return true
        val end = mode.endTime ?: return true
        return try {
            val startT = java.time.LocalTime.parse(start)
            val endT = java.time.LocalTime.parse(end)
            if (startT == endT) return true
            val now = java.time.LocalTime.now()
            val today = java.time.LocalDate.now()
            val repeat = mode.repeatDays
            fun isRepeatDay(date: java.time.LocalDate) =
                repeat == null || repeat.contains(date.dayOfWeek.value)
            if (startT < endT) {
                isRepeatDay(today) && !now.isBefore(startT) && now.isBefore(endT)
            } else {
                // Overnight window (e.g. 23:00 -> 07:00): after midnight the
                // period belongs to yesterday's schedule day.
                (!now.isBefore(startT) && isRepeatDay(today)) ||
                        (now.isBefore(endT) && isRepeatDay(today.minusDays(1)))
            }
        } catch (e: Exception) {
            log("isInBedtimeWindow parse failed ($start-$end): ${e.message}")
            true
        }
    }

    private fun sendBedtimeCommand(action: String) {
        val context = systemContext ?: return
        try {
            // No receiverPermission: PERMISSION_CONTROL is signature-level, so only
            // this app can hold it — neither system_server (sender) nor DeskClock
            // (receiver) can ever satisfy a permission check here. Passing it made
            // the system silently drop every engine->DeskClock command.
            context.sendBroadcast(Intent(action).apply {
                setPackage(com.banana.hypermodes.protocol.Protocol.TARGET_PACKAGE)
            })
            log("Sent command to DeskClock: $action")
        } catch (e: Exception) {
            log("Failed to send command to DeskClock: ${e.message}")
        }
    }

    /**
     * Single entry point for DeskClock bedtime-state signals (via BedtimeListener).
     * All policy lives in BedtimeReconciler; this only builds the snapshot and
     * executes the resulting decisions.
     */
    fun onBedtimeSignal(active: Boolean, reasonName: String?) {
        if (lifecycleState == LifecycleState.REMOVED) return
        val reason = BedtimeReconciler.Reason.fromString(reasonName, active)
        val decisions = BedtimeReconciler.decide(
            BedtimeReconciler.Event.DeskClockSignal(active, reason, System.currentTimeMillis()),
            bedtimeSnapshot()
        )
        log("Bedtime signal active=$active reason=$reason -> ${decisions.joinToString()}")
        executeBedtimeDecisions(decisions)
    }

    private fun bedtimeSnapshot(): BedtimeReconciler.Snapshot {
        val bedtime = allModes.firstOrNull { it.type == ModeType.BEDTIME }
        return BedtimeReconciler.Snapshot(
            bedtimeModeExists = bedtime != null,
            modeActive = currentActiveMode?.type == ModeType.BEDTIME,
            modeActivatedAt = currentModeActivatedAt,
            dismissedAt = bedtime?.let { dismissedScheduledModes[it.id] }
        )
    }

    private fun executeBedtimeDecisions(decisions: List<BedtimeReconciler.Decision>) {
        if (decisions.isEmpty()) return
        val bedtime = allModes.firstOrNull { it.type == ModeType.BEDTIME } ?: return
        for (d in decisions) when (d) {
            BedtimeReconciler.Decision.ActivateMode -> activateMode(bedtime.id)
            is BedtimeReconciler.Decision.DeactivateMode ->
                deactivateMode(bedtime.id, isManualDismiss = d.recordDismiss)
            is BedtimeReconciler.Decision.SendDeskClockCommand -> sendBedtimeCommand(
                when (d.command) {
                    BedtimeReconciler.Command.START_BEDTIME ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_START_BEDTIME
                    BedtimeReconciler.Command.SKIP_WAKE_ALARM_ONCE ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_SKIP_WAKE_ALARM_ONCE
                    BedtimeReconciler.Command.EXIT_BEDTIME ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_EXIT_BEDTIME
                    BedtimeReconciler.Command.ENABLE_WAKE_ALARM ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_ENABLE_WAKE_ALARM
                }
            )
        }
    }

    /**
     * Get the currently active mode, or null if no mode is active.
     */
    fun getCurrentActiveMode(): ModeConfig? = currentActiveMode

    /** When the current mode was activated (ms); 0 when no mode is active. */
    fun getCurrentModeActivatedAt(): Long = currentModeActivatedAt

    /**
     * Check if a mode was manually dismissed during the current scheduled period.
     * Used by scheduler to prevent re-activation after manual dismissal.
     *
     * @param modeId Mode identifier
     * @param periodStartTime Start time of the current scheduled period (milliseconds)
     * @return true if mode was dismissed after the period started
     */
    fun isDismissedInCurrentPeriod(modeId: String, periodStartTime: Long): Boolean {
        val dismissTime = dismissedScheduledModes[modeId] ?: return false

        // If dismiss happened after this period started, it's dismissed for this period
        val isDismissed = dismissTime >= periodStartTime

        // Clean up old dismiss records (older than 24 hours)
        if (System.currentTimeMillis() - dismissTime > 24 * 60 * 60 * 1000) {
            dismissedScheduledModes.remove(modeId)
            // Persist the removal
            updateActiveModeInSettings(currentActiveMode?.id)
            return false
        }

        return isDismissed
    }

    /**
     * Clear dismiss record when a new scheduled period starts.
     * Called by ScheduledModeManager when the mode is activated by schedule.
     */
    fun clearDismissRecord(modeId: String) {
        if (dismissedScheduledModes.remove(modeId) != null) {
            log("Cleared dismiss record for mode $modeId (new period started)")
            // Persist the removal
            updateActiveModeInSettings(currentActiveMode?.id)
        }
    }

    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Atomically closes the lifecycle gate before cleanup work or queued callbacks can run.
     * REMOVED is terminal for the current system_server process.
     */
    fun beginPackageRemoval(): Boolean = synchronized(lifecycleLock) {
        if (lifecycleState == LifecycleState.REMOVED) return false
        log("Engine lifecycle transition: $lifecycleState -> REMOVED")
        lifecycleState = LifecycleState.REMOVED
        true
    }

    /**
     * Set the lifecycle state of the engine.
     */
    fun setLifecycleState(state: LifecycleState) {
        if (lifecycleState == LifecycleState.REMOVED) {
            log("Engine is already REMOVED, ignoring transition to $state")
            return
        }
        log("Engine lifecycle transition: $lifecycleState -> $state")
        lifecycleState = state
        if (state == LifecycleState.RUNNING) {
            mainHandler?.post { loadConfigFromSettings() }
        }
    }

    /**
     * Get the current lifecycle state.
     */
    fun getLifecycleState(): LifecycleState = lifecycleState

    /**
     * Idempotent shutdown for package removal.
     * Clears all runtime state and reverts system changes.
     */
    fun shutdownForPackageRemoval() {
        if (!beginPackageRemoval()) {
            log("Engine already removed, skipping shutdown")
            return
        }

        val cleanup = {
            log("Starting engine shutdown for package removal...")
            performPackageRemovalCleanup()
        }

        mainHandler?.post(cleanup) ?: cleanup()
    }

    private fun performPackageRemovalCleanup() {
        // 1. Cancel all alarms
        scheduledModeManager?.cancelAllSchedules()

        // 2. Unregister engine configuration observer
        try {
            configObserverOwner?.release()
        } catch (e: Exception) {
            log("Failed to unregister config observer: ${e.message}")
        }
        configObserverOwner = null

        // 3. Unregister triggers and listeners without normal deactivation side effects
        drivingTriggerManager?.cleanupForPackageRemoval()
        bedtimeListener?.cleanupForPackageRemoval()

        // 4. Revert active mode
        currentActiveMode?.let {
            log("Reverting active mode for shutdown: ${it.name}")
            modeActionExecutor?.revertMode(it)
            currentActiveMode = null
            currentModeActivatedAt = 0L
        }

        // 4. Send DeskClock disable command while the injected bridge may still be alive
        sendBedtimeCommand(com.banana.hypermodes.protocol.Protocol.ACTION_DISABLE_BEDTIME)

        // 5. Clear state
        allModes = emptyList()
        dismissedScheduledModes.clear()

        // 6. Remove global config
        val context = systemContext
        if (context != null) {
            try {
                Settings.Global.putString(context.contentResolver, CONFIG_KEY, null)
                log("Removed global config from Settings.Global")
            } catch (e: Exception) {
                log("Failed to remove global config: ${e.message}")
            }
        }

        log("RoutineCoreEngine shutdown complete")
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "RoutineCoreEngine"
        private const val CONFIG_KEY = "pixel_routines_full_config"

        @Volatile
        private var instance: RoutineCoreEngine? = null

        /**
         * Get the singleton instance of RoutineCoreEngine.
         * Thread-safe double-checked locking pattern.
         */
        fun getInstance(): RoutineCoreEngine {
            return instance ?: synchronized(this) {
                instance ?: RoutineCoreEngine().also { instance = it }
            }
        }
    }
}
