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
import com.banana.hypermodes.systemserver.trigger.BedtimeListenerLifecycle
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

    private var systemContext: Context? = null
    private var classLoader: ClassLoader? = null

    private var currentActiveMode: ModeConfig? = null
    private var allModes: List<ModeConfig> = emptyList()

    // Track manually dismissed scheduled modes: modeId -> dismiss timestamp
    // When user manually closes a mode during its scheduled period,
    // it won't auto-reopen until the next scheduled period starts
    private val dismissedScheduledModes = mutableMapOf<String, Long>()

    private var drivingTriggerManager: DrivingTriggerManager? = null
    private var scheduledModeManager: ScheduledModeManager? = null
    private var bedtimeListener: BedtimeListener? = null
    private var bedtimeListenerLifecycle = BedtimeListenerLifecycle()
    private var modeActionExecutor: ModeActionExecutor? = null

    private var mainHandler: Handler? = null

    /**
     * Initialize the engine in system_server context.
     * Must be called from AMS.systemReady() or similar system_server initialization point.
     *
     * @param context System context (from system_server)
     * @param loader System_server ClassLoader for reflection
     */
    fun init(context: Context, loader: ClassLoader) {
        log("Initializing RoutineCoreEngine...")
        systemContext = context
        classLoader = loader
        mainHandler = Handler(Looper.getMainLooper())

        // Initialize components
        modeActionExecutor = ModeActionExecutor(context, loader)
        drivingTriggerManager = DrivingTriggerManager(context, this)
        scheduledModeManager = ScheduledModeManager(context, this)
        bedtimeListenerLifecycle = BedtimeListenerLifecycle()
        bedtimeListener = BedtimeListener(context, this, bedtimeListenerLifecycle).also {
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
                }.get(this) as? StatusBarIconManager

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
                log("Config changed, reloading...")
                loadConfigFromSettings()
            }
        }

        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(CONFIG_KEY),
            false,
            observer
        )
        log("ContentObserver registered for $CONFIG_KEY")
    }

    /**
     * Load configuration from Settings.Global and update internal state.
     * Called on initialization and whenever config changes.
     */
    private fun loadConfigFromSettings() {
        val context = systemContext ?: return

        try {
            val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
            if (json.isNullOrBlank()) {
                log("No config found in Settings.Global[$CONFIG_KEY]")
                return
            }

            val config = ConfigParser.parseConfig(json)
            allModes = config.modes
            log("Config loaded: ${allModes.size} modes")

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
                        currentActiveMode = mode
                        modeActionExecutor?.applyMode(mode)
                    } else {
                        log("Active mode not found in config: ${config.activeModeId}")
                        currentActiveMode = null
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
                    if (activeMode.type == ModeType.SCHEDULED || activeMode.type == ModeType.BEDTIME) {
                        val now = System.currentTimeMillis()
                        dismissedScheduledModes[activeMode.id] = now
                        log("Recorded manual dismiss for mode ${activeMode.id} at timestamp $now (from config change)")
                    }
                    log("Deactivating current mode: ${activeMode.name}")
                    modeActionExecutor?.revertMode(activeMode)
                    currentActiveMode = null
                }
            }

            // Initialize the bedtime bridge on the first load, then synchronize its modes.
            // This runs after the persisted active mode is restored so external bedtime state wins.
            bedtimeListener?.let { listener ->
                bedtimeListenerLifecycle.onModesLoaded(
                    modes = config.modes,
                    initialize = listener::init,
                    update = listener::updateModes
                )
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

        // Deactivate current mode first
        currentActiveMode?.let {
            log("Deactivating current mode: ${it.name}")
            modeActionExecutor?.revertMode(it)
        }

        // Apply new mode
        currentActiveMode = mode
        modeActionExecutor?.applyMode(mode)

        // IF it's Bedtime mode, ensure DeskClock alarm is NOT skipped
        if (mode.type == ModeType.BEDTIME) {
            sendBedtimeCommand(com.banana.hypermodes.protocol.Protocol.ACTION_ENABLE_WAKE_ALARM)
        }

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
        val mode = currentActiveMode
        if (mode == null || mode.id != modeId) {
            log("Cannot deactivate mode: mode not active: $modeId")
            return
        }

        log("Deactivating mode: ${mode.name} (id=$modeId, manual=$isManualDismiss)")

        // Revert mode actions
        modeActionExecutor?.revertMode(mode)

        currentActiveMode = null

        // Record dismiss timestamp ONLY for manual dismissals of scheduled or bedtime modes
        if (isManualDismiss && (mode.type == ModeType.SCHEDULED || mode.type == ModeType.BEDTIME)) {
            val now = System.currentTimeMillis()
            dismissedScheduledModes[modeId] = now
            log("Recorded manual dismiss for mode $modeId at timestamp $now")

            // IF it's Bedtime mode, also tell DeskClock to skip the alarm
            if (mode.type == ModeType.BEDTIME) {
                sendBedtimeCommand(com.banana.hypermodes.protocol.Protocol.ACTION_SKIP_WAKE_ALARM_ONCE)
            }
        }

        // Clear active mode from Settings.Global
        updateActiveModeInSettings(null)
        broadcastModeState(null)

        // Reschedule alarms (next occurrence after deactivation)
        scheduledModeManager?.updateSchedules(allModes)

        log("Mode deactivated successfully: ${mode.name}")
    }

    /**
     * Update the active mode ID in Settings.Global.
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

            val updated = ConfigParser.updateActiveModeId(currentJson, modeId)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, updated)
            log("Updated active mode in Settings.Global: ${modeId ?: "null"}")
        } catch (e: Exception) {
            log("Failed to update active mode: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun broadcastModeState(modeId: String?) {
        val context = systemContext ?: return
        try {
            context.sendBroadcast(Intent(com.banana.hypermodes.protocol.Protocol.ACTION_MODE_STATE).apply {
                setPackage(com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)
                if (modeId != null) {
                    putExtra(com.banana.hypermodes.protocol.Protocol.EXTRA_MODE_ID, modeId)
                }
            })
            log("Broadcast mode state: ${modeId ?: "null"}")
        } catch (e: Exception) {
            log("Failed to broadcast mode state: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun sendBedtimeCommand(action: String) {
        val context = systemContext ?: return
        try {
            context.sendBroadcast(Intent(action).apply {
                setPackage(com.banana.hypermodes.protocol.Protocol.TARGET_PACKAGE)
            }, com.banana.hypermodes.protocol.Protocol.PERMISSION_CONTROL)
            log("Sent command to DeskClock: $action")
        } catch (e: Exception) {
            log("Failed to send command to DeskClock: ${e.message}")
        }
    }

    /**
     * Get the currently active mode, or null if no mode is active.
     */
    fun getCurrentActiveMode(): ModeConfig? = currentActiveMode

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
        }
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
