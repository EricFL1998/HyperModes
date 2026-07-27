package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
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
    private val engine: RoutineCoreEngine,
    private val lifecycle: BedtimeListenerLifecycle,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var allModes: List<ModeConfig> = emptyList()
    private var receiverRegistered = false
    private val registeredSecureKeys = mutableSetOf<String>()

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Protocol.ACTION_BEDTIME_ACTIVE) {
                val inSleepMode = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)
                lifecycle.onBedtimeStateChanged(inSleepMode)
                log("Received bedtime active broadcast: $inSleepMode")
                if (inSleepMode) {
                    activateBedtime()
                } else {
                    deactivateBedtime()
                }
            }
        }
    }

    private val bedtimeSettingsObserver = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            log("Bedtime settings changed: $uri")
            checkBedtimeState(lifecycle.onPersistedStateChanged(::isBedtimeActive))
        }
    }

    /**
     * Register bedtime state sources. Mode synchronization happens separately after
     * RoutineCoreEngine has restored the persisted active mode.
     */
    fun registerStateSources() {
        log("Registering BedtimeListener state sources...")
        registerReceiverIfNeeded()
        registerBedtimeObserver()
        log("BedtimeListener state sources registered")
    }

    /**
     * Initialize the bedtime listener with the first mode list and synchronize state.
     */
    fun init(modes: List<ModeConfig>) {
        registerStateSources()
        allModes = modes
        log("Initializing BedtimeListener with ${modes.size} modes")
        checkBedtimeState()
        log("BedtimeListener initialized")
    }

    /**
     * Update the mode list when configuration changes.
     * This is called when the engine reloads configuration from Settings.Global.
     */
    fun updateModes(modes: List<ModeConfig>) {
        registerStateSources()
        allModes = modes
        log("Mode list updated: ${modes.size} modes")

        // Re-check bedtime state in case BEDTIME mode was added/removed
        checkBedtimeState()
    }

    private fun registerReceiverIfNeeded() {
        if (receiverRegistered) return

        try {
            val filter = IntentFilter(Protocol.ACTION_BEDTIME_ACTIVE)
            context.registerReceiver(receiver, filter, null, handler, Context.RECEIVER_EXPORTED)
            receiverRegistered = true
            log("Bedtime active receiver registered")
        } catch (e: Exception) {
            log("Failed to register bedtime active receiver: ${e.message}")
        }
    }

    /**
     * Register ContentObserver to watch for bedtime mode changes.
     * Observes multiple possible Settings.Secure keys used by different bedtime implementations:
     * - "bedtime_mode": Generic Android bedtime state
     * - "sleep_mode_active": Some OEM implementations
     * - We also rely on broadcasts from DeskClockHook for HyperOS DeskClock changes
     */
    private fun registerBedtimeObserver() {
        registerSecureObserverIfNeeded("bedtime_mode")
        registerSecureObserverIfNeeded("sleep_mode_active")
    }

    private fun registerSecureObserverIfNeeded(key: String) {
        if (key in registeredSecureKeys) return

        try {
            context.contentResolver.registerContentObserver(
                Settings.Secure.getUriFor(key),
                false,
                bedtimeSettingsObserver
            )
            registeredSecureKeys += key
            log("Registered observer for $key")
        } catch (e: Exception) {
            log("Failed to register $key observer: ${e.message}")
        }
    }

    /**
     * Check current bedtime state and sync with RoutineCoreEngine.
     * Reads the persisted bedtime state from Settings.Secure and activates/deactivates
     * the BEDTIME mode accordingly.
     */
    private fun checkBedtimeState(
        bedtimeActive: Boolean = lifecycle.resolveBedtimeState(::isBedtimeActive)
    ) {
        try {
            val currentMode = engine.getCurrentActiveMode()
            val bedtimeMode = findBedtimeMode()

            // Check if this bedtime mode was recently manually dismissed
            val isManualDismissed = if (bedtimeMode != null) {
                // For BEDTIME modes, we consider them "dismissed" if the dismiss happened 
                // in the last few minutes and we haven't seen a new activation since then.
                // RoutineCoreEngine already tracks dismissedScheduledModes.
                // Since BEDTIME modes don't have a scheduled start time in the engine,
                // we use a recent threshold (e.g. 1 minute) to prevent immediate re-activation
                // by the ContentObserver before Settings.Secure has synced.
                engine.isDismissedInCurrentPeriod(bedtimeMode.id, System.currentTimeMillis() - 60000)
            } else false

            log("Bedtime state check: active=$bedtimeActive, dismissed=$isManualDismissed, currentMode=${currentMode?.name}, bedtimeMode=${bedtimeMode?.name}")

            if (bedtimeActive && !isManualDismissed) {
                // Bedtime should be active
                if (bedtimeMode != null && (currentMode?.id != bedtimeMode.id)) {
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
            if (bedtimeMode == 1) return true

            // Try alternative sleep_mode_active setting
            val sleepMode = Settings.Secure.getInt(
                context.contentResolver,
                "sleep_mode_active",
                0
            )
            return sleepMode == 1
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
            engine.clearDismissRecord(bedtimeMode.id)
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

    /**
     * Clean up resources.
     */
    fun cleanup() {
        if (receiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
                receiverRegistered = false
                log("Bedtime active receiver unregistered")
            } catch (e: Exception) {
                log("Failed to unregister bedtime active receiver: ${e.message}")
            }
        }

        registeredSecureKeys.forEach { key ->
            try {
                context.contentResolver.unregisterContentObserver(bedtimeSettingsObserver)
                log("Unregistered observer for $key")
            } catch (e: Exception) {
                log("Failed to unregister $key observer: ${e.message}")
            }
        }
        registeredSecureKeys.clear()
        allModes = emptyList()
    }

    /**
     * Clean up package-removal resources without normal mode deactivation.
     * The engine restores the active mode separately, and trigger callbacks
     * must not persist state after the removal gate has closed.
     */
    fun cleanupForPackageRemoval() {
        cleanup()
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "BedtimeListener"
    }
}
