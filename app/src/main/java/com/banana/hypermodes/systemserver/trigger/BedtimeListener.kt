package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType

/**
 * Listens for system bedtime mode activation and synchronizes with RoutineCoreEngine.
 *
 * This listener bridges the gap between the system's bedtime mode (triggered by DeskClock
 * or other bedtime providers) and HyperModes' RoutineCoreEngine.
 *
 * Integration points:
 * 1. Watches Settings.Secure for bedtime mode state changes
 * 2. Activates/deactivates the BEDTIME mode in RoutineCoreEngine
 * 3. Works alongside DeskClockHook which handles the app-side integration
 *
 * When system bedtime becomes active (through scheduled alarm or manual toggle in DeskClock),
 * this listener ensures the corresponding BEDTIME mode in RoutineCoreEngine is activated,
 * applying all configured restrictions (app suspension, DND, display settings, etc.).
 *
 * When bedtime ends (alarm dismissed, manual stop, or wake time reached), the BEDTIME mode
 * is deactivated and restrictions are reverted.
 */
class BedtimeListener(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private val handler = Handler(Looper.getMainLooper())
    private var allModes: List<ModeConfig> = emptyList()

    /**
     * Initialize the bedtime listener.
     * Registers ContentObserver for bedtime state changes and checks initial state.
     */
    fun init(modes: List<ModeConfig>) {
        log("Initializing BedtimeListener...")
        allModes = modes

        // Watch Settings.Secure for bedtime mode state
        registerBedtimeObserver()

        // Check initial bedtime state
        checkBedtimeState()

        log("BedtimeListener initialized")
    }

    /**
     * Update the mode list when configuration changes.
     * This is called when the engine reloads configuration from Settings.Global.
     */
    fun updateModes(modes: List<ModeConfig>) {
        allModes = modes
        log("Mode list updated: ${modes.size} modes")

        // Re-check bedtime state in case BEDTIME mode was added/removed
        checkBedtimeState()
    }

    /**
     * Register ContentObserver to watch for bedtime mode changes.
     * Observes multiple possible Settings.Secure keys used by different bedtime implementations:
     * - "bedtime_mode": Generic Android bedtime state
     * - "sleep_mode_active": Some OEM implementations
     * - We also rely on broadcasts from DeskClockHook for HyperOS DeskClock changes
     */
    private fun registerBedtimeObserver() {
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                log("Bedtime settings changed: $uri")
                checkBedtimeState()
            }
        }

        // Watch for generic bedtime mode setting
        try {
            context.contentResolver.registerContentObserver(
                Settings.Secure.getUriFor("bedtime_mode"),
                false,
                observer
            )
            log("Registered observer for bedtime_mode")
        } catch (e: Exception) {
            log("Failed to register bedtime_mode observer: ${e.message}")
        }

        // Watch for alternative sleep mode setting
        try {
            context.contentResolver.registerContentObserver(
                Settings.Secure.getUriFor("sleep_mode_active"),
                false,
                observer
            )
            log("Registered observer for sleep_mode_active")
        } catch (e: Exception) {
            log("Failed to register sleep_mode_active observer: ${e.message}")
        }
    }

    /**
     * Check current bedtime state and sync with RoutineCoreEngine.
     * Reads the persisted bedtime state from Settings.Secure and activates/deactivates
     * the BEDTIME mode accordingly.
     */
    private fun checkBedtimeState() {
        try {
            val bedtimeActive = isBedtimeActive()
            val currentMode = engine.getCurrentActiveMode()
            val bedtimeMode = findBedtimeMode()

            log("Bedtime state check: active=$bedtimeActive, currentMode=${currentMode?.name}, bedtimeMode=${bedtimeMode?.name}")

            if (bedtimeActive) {
                // Bedtime should be active
                if (bedtimeMode != null && currentMode?.id != bedtimeMode.id) {
                    log("Activating bedtime mode: ${bedtimeMode.name}")
                    engine.activateMode(bedtimeMode.id)
                } else if (bedtimeMode == null) {
                    log("Bedtime active but no BEDTIME mode configured")
                }
            } else {
                // Bedtime should be inactive
                if (currentMode != null && currentMode.type == ModeType.BEDTIME) {
                    log("Deactivating bedtime mode: ${currentMode.name}")
                    engine.deactivateMode(currentMode.id)
                }
            }
        } catch (e: Exception) {
            log("Failed to check bedtime state: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Check if bedtime mode is currently active according to system settings.
     * Tries multiple possible Settings.Secure keys used by different implementations.
     *
     * @return true if bedtime is active, false otherwise
     */
    private fun isBedtimeActive(): Boolean {
        try {
            // Try generic bedtime_mode setting (1 = active, 0 = inactive)
            val bedtimeMode = Settings.Secure.getInt(
                context.contentResolver,
                "bedtime_mode",
                0
            )
            if (bedtimeMode == 1) {
                return true
            }

            // Try alternative sleep_mode_active setting
            val sleepMode = Settings.Secure.getInt(
                context.contentResolver,
                "sleep_mode_active",
                0
            )
            if (sleepMode == 1) {
                return true
            }

            return false
        } catch (e: Exception) {
            log("Failed to read bedtime state from Settings: ${e.message}")
            return false
        }
    }

    /**
     * Find the BEDTIME mode in the current mode list.
     * Returns the first mode with type BEDTIME, or null if none exists.
     *
     * @return The BEDTIME mode config, or null if not found
     */
    private fun findBedtimeMode(): ModeConfig? {
        return allModes.firstOrNull { it.type == ModeType.BEDTIME }
    }

    /**
     * Manually trigger bedtime activation.
     * Called from external triggers (e.g., broadcast from DeskClockHook).
     */
    fun activateBedtime() {
        log("Manual bedtime activation requested")
        val bedtimeMode = findBedtimeMode()
        if (bedtimeMode != null) {
            engine.activateMode(bedtimeMode.id)
        } else {
            log("Cannot activate bedtime: no BEDTIME mode configured")
        }
    }

    /**
     * Manually trigger bedtime deactivation.
     * Called from external triggers (e.g., alarm dismissal from DeskClockHook).
     */
    fun deactivateBedtime() {
        log("Manual bedtime deactivation requested")
        val currentMode = engine.getCurrentActiveMode()
        if (currentMode != null && currentMode.type == ModeType.BEDTIME) {
            engine.deactivateMode(currentMode.id)
        } else {
            log("Cannot deactivate bedtime: no BEDTIME mode active")
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "BedtimeListener"
    }
}
