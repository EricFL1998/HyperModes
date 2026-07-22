package com.banana.hypermodes.systemserver

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.executor.ModeActionExecutor
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

    private var drivingTriggerManager: DrivingTriggerManager? = null
    private var scheduledModeManager: ScheduledModeManager? = null
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
        modeActionExecutor = ModeActionExecutor(context)
        drivingTriggerManager = DrivingTriggerManager(context, this)
        scheduledModeManager = ScheduledModeManager(context, this)

        // Watch for config changes in Settings.Global
        observeConfigChanges(context)

        // Load initial config
        loadConfigFromSettings()

        log("RoutineCoreEngine initialized successfully")
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

            // Restore active mode if one is specified
            config.activeModeId?.let { modeId ->
                val mode = allModes.find { it.id == modeId }
                if (mode != null) {
                    log("Restoring active mode: ${mode.name}")
                    currentActiveMode = mode
                    modeActionExecutor?.applyMode(mode)
                } else {
                    log("Active mode not found in config: $modeId")
                }
            }
        } catch (e: Exception) {
            log("Failed to load config: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Activate a mode by ID.
     * Deactivates current mode first if one is active.
     * Updates Settings.Global to persist active mode.
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

        // Persist active mode to Settings.Global
        updateActiveModeInSettings(modeId)
        log("Mode activated successfully: ${mode.name}")
    }

    /**
     * Deactivate a mode by ID.
     * Reverts mode actions and clears active mode from Settings.Global.
     *
     * @param modeId Mode identifier to deactivate
     */
    fun deactivateMode(modeId: String) {
        val mode = currentActiveMode
        if (mode == null || mode.id != modeId) {
            log("Cannot deactivate mode: mode not active: $modeId")
            return
        }

        log("Deactivating mode: ${mode.name} (id=$modeId)")

        // Revert mode actions
        modeActionExecutor?.revertMode(mode)

        currentActiveMode = null

        // Clear active mode from Settings.Global
        updateActiveModeInSettings(null)
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

    /**
     * Get the currently active mode, or null if no mode is active.
     */
    fun getCurrentActiveMode(): ModeConfig? = currentActiveMode

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
