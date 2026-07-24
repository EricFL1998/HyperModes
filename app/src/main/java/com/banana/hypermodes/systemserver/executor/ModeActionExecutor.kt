package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.StatusBarIconManager
import com.banana.hypermodes.systemserver.config.ModeConfig

/**
 * Executor for applying and reverting mode actions.
 *
 * This component orchestrates all mode controllers to apply complete mode configurations:
 * - DndController: Manages Do Not Disturb settings
 * - AppSuspendController: Suspends/unsuspends apps
 * - DisplayModeController: Applies dark mode and grayscale
 * - NotificationFilterHook: Filters notifications (integrated via Xposed hook)
 *
 * @param context System context from system_server
 * @param classLoader System_server ClassLoader for reflection-based operations
 */
class ModeActionExecutor(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    private val dndController = DndController(context)
    private val appSuspendController = AppSuspendController(context, classLoader)
    private val displayModeController = DisplayModeController(context)
    private val statusBarIconManager = StatusBarIconManager(context, classLoader)

    /**
     * Apply all actions configured in the mode.
     *
     * This method orchestrates all controllers to fully apply the mode configuration:
     * - Sets DND level via NotificationManager
     * - Suspends configured apps via PackageManagerService
     * - Applies display settings (dark mode, grayscale)
     *
     * @param mode Mode configuration to apply
     */
    fun applyMode(mode: ModeConfig) {
        log("applyMode: ${mode.name} (id=${mode.id})")

        // Apply DND settings
        dndController.setDndLevel(mode.notification.dndLevel)

        // Suspend apps
        if (mode.pausedApps.isNotEmpty()) {
            appSuspendController.suspendApps(mode.pausedApps)
        } else {
            log("applyMode: no apps to suspend")
        }

        // Apply display settings
        displayModeController.apply(mode.display)

        // Update status bar icon
        statusBarIconManager.setIcon(mode.statusIcon, mode.name)

        log("applyMode: completed for ${mode.name}")
    }

    /**
     * Revert all actions that were applied by this mode.
     *
     * This method orchestrates all controllers to fully revert the mode configuration:
     * - Restores normal notification behavior (disables DND)
     * - Unsuspends all suspended apps
     * - Restores default display settings
     *
     * @param mode Mode configuration to revert
     */
    fun revertMode(mode: ModeConfig) {
        log("revertMode: ${mode.name} (id=${mode.id})")

        // Restore DND settings
        dndController.restore()

        // Unsuspend apps
        appSuspendController.unsuspendApps()

        // Restore display settings
        displayModeController.restore()

        // Remove status bar icon
        statusBarIconManager.removeIcon()

        log("revertMode: completed for ${mode.name}")
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "ModeActionExecutor"
    }
}
