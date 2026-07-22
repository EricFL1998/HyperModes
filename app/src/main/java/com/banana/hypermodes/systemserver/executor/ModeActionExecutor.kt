package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.config.ModeConfig

/**
 * Stub executor for applying and reverting mode actions.
 * This is a placeholder implementation that logs actions without actually executing them.
 *
 * Actual implementation will be added in later tasks to:
 * - Apply DND settings
 * - Pause/unpause apps
 * - Apply display settings (dark mode, grayscale)
 * - Configure notification filters
 *
 * @param context System context from system_server
 */
class ModeActionExecutor(
    private val context: Context
) {
    /**
     * Apply all actions configured in the mode.
     * Currently a stub that only logs the action.
     *
     * @param mode Mode configuration to apply
     */
    fun applyMode(mode: ModeConfig) {
        log("applyMode: ${mode.name} (id=${mode.id})")
        log("  - DND level: ${mode.notification.dndLevel}")
        log("  - Display: darkMode=${mode.display.darkMode}, grayscale=${mode.display.grayscale}, dimWallpaper=${mode.display.dimWallpaper}, keepScreenOff=${mode.display.keepScreenOff}")
        log("  - Paused apps: ${mode.pausedApps.size} apps")
        log("  - Contact filter: ${mode.contactFilter}")
        log("  - Notification: allowed=${mode.notification.allowedApps.size} apps")
        // TODO: Implement actual mode application in later tasks
    }

    /**
     * Revert all actions that were applied by this mode.
     * Currently a stub that only logs the action.
     *
     * @param mode Mode configuration to revert
     */
    fun revertMode(mode: ModeConfig) {
        log("revertMode: ${mode.name} (id=${mode.id})")
        log("  - Reverting DND settings")
        log("  - Reverting display settings")
        log("  - Unpausing apps: ${mode.pausedApps.size} apps")
        log("  - Reverting notification filters")
        // TODO: Implement actual mode reversion in later tasks
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "ModeActionExecutor"
    }
}
