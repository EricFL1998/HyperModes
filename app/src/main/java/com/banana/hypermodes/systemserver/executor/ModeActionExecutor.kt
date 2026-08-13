package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.StatusBarIconManager
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType

/**
 * Executor for applying and reverting mode actions.
 *
 * This component orchestrates all mode controllers to apply complete mode configurations:
 * - DndController: Manages Do Not Disturb settings
 * - AppSuspendController: Suspends/unsuspends apps
 * - DisplayModeController: Applies dark mode and grayscale
 * - DeviceController: Manages device settings (silent mode, etc.)
 * - StatusBarIconManager: Shows/hides status bar icon
 * - WallpaperController: Applies/reverts lock-screen style + wallpaper set
 *
 * Features rollback on failure to ensure atomic mode application.
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
    private val deviceController = DeviceController(context)
    private val statusBarIconManager = StatusBarIconManager(context, classLoader)
    private val wallpaperController = WallpaperController(context)

    // Track what was applied for rollback
    private data class AppliedState(
        var deviceApplied: Boolean = false,
        var dndApplied: Boolean = false,
        var appsApplied: Boolean = false,
        var displayApplied: Boolean = false,
        var iconApplied: Boolean = false,
        var wallpaperApplied: Boolean = false
    )

    /**
     * Apply all actions configured in the mode.
     *
     * This method orchestrates all controllers to fully apply the mode configuration.
     * If any step fails, all previous steps are rolled back to maintain consistency.
     *
     * @param mode Mode configuration to apply
     * @throws Exception if mode application fails (after rollback)
     */
    fun applyMode(mode: ModeConfig) {
        log("applyMode: ${mode.name} (id=${mode.id})")
        
        val applied = AppliedState()

        try {
            // Step 1: Apply device settings (includes silent mode)
            deviceController.apply(mode.device)
            applied.deviceApplied = true
            log("✓ Device settings applied")

            // Step 2: Apply DND settings (after silent mode)
            dndController.setDndLevel(mode.notification.dndLevel)
            applied.dndApplied = true
            log("✓ DND settings applied")

            // Step 3: Suspend apps
            if (mode.pausedApps.isNotEmpty()) {
                appSuspendController.suspendApps(mode.pausedApps)
                applied.appsApplied = true
                log("✓ ${mode.pausedApps.size} apps suspended")
            } else {
                log("No apps to suspend")
            }

            // Step 4: Apply display settings
            displayModeController.apply(mode.display)
            applied.displayApplied = true
            log("✓ Display settings applied")

            // Step 5: Update status bar icon
            statusBarIconManager.setIcon(mode.statusIcon, mode.name)
            applied.iconApplied = true
            log("✓ Status bar icon set")

            // Step 6: Apply wallpaper set (lock screen style + wallpaper)
            if (mode.wallpaper != null) {
                wallpaperController.apply(mode.wallpaper)
                applied.wallpaperApplied = true
                log("✓ Wallpaper set applied")
            } else {
                log("No wallpaper set to apply")
            }

            log("applyMode: completed successfully for ${mode.name}")

        } catch (e: Exception) {
            log("✗ Failed to apply mode ${mode.name}: ${e.message}")
            log("Rolling back partial changes...")
            
            // Rollback in reverse order
            try {
                if (applied.iconApplied) {
                    statusBarIconManager.removeIcon()
                    log("↩ Icon removed")
                }
                if (applied.wallpaperApplied) {
                    wallpaperController.restore()
                    log("↩ Wallpaper restored")
                }
                if (applied.displayApplied) {
                    displayModeController.restore()
                    log("↩ Display settings restored")
                }
                if (applied.appsApplied) {
                    appSuspendController.unsuspendApps()
                    log("↩ Apps unsuspended")
                }
                if (applied.dndApplied) {
                    dndController.restore()
                    log("↩ DND restored")
                }
                if (applied.deviceApplied) {
                    deviceController.restore()
                    log("↩ Device settings restored")
                }
                log("Rollback completed")
            } catch (rollbackException: Exception) {
                log("✗ Rollback failed: ${rollbackException.message}")
                // Log but don't throw - we're already handling an exception
            }

            // Re-throw original exception
            throw ModeApplicationException("Failed to apply mode ${mode.name}", e)
        }
    }

    /**
     * Revert all actions that were applied by this mode.
     *
     * This method orchestrates all controllers to fully revert the mode configuration.
     * Errors during revert are logged but not thrown to allow cleanup to continue.
     *
     * @param mode Mode configuration to revert
     */
    fun revertMode(mode: ModeConfig) {
        log("revertMode: ${mode.name} (id=${mode.id})")

        val errors = mutableListOf<String>()

        // Revert in reverse order of application
        // Continue even if steps fail to clean up as much as possible

        try {
            wallpaperController.restore()
            log("✓ Wallpaper restored")
        } catch (e: Exception) {
            val msg = "Failed to restore wallpaper: ${e.message}"
            log("✗ $msg")
            errors.add(msg)
        }

        try {
            statusBarIconManager.removeIcon()
            log("✓ Icon removed")
        } catch (e: Exception) {
            val msg = "Failed to remove icon: ${e.message}"
            log("✗ $msg")
            errors.add(msg)
        }

        try {
            displayModeController.restore()
            log("✓ Display settings restored")
        } catch (e: Exception) {
            val msg = "Failed to restore display: ${e.message}"
            log("✗ $msg")
            errors.add(msg)
        }

        try {
            appSuspendController.unsuspendApps()
            log("✓ Apps unsuspended")
        } catch (e: Exception) {
            val msg = "Failed to unsuspend apps: ${e.message}"
            log("✗ $msg")
            errors.add(msg)
        }

        try {
            deviceController.restore()
            log("✓ Device settings restored")
        } catch (e: Exception) {
            val msg = "Failed to restore device settings: ${e.message}"
            log("✗ $msg")
            errors.add(msg)
        }

        try {
            // Bedtime mode exits when the wake alarm is dismissed/turned off. The user expects
            // sleep mode and DND to turn off together. DND runs AFTER device restore so it wins
            // and clears both the interruption filter and MIUI silence_mode (勿扰).
            if (mode.type == ModeType.BEDTIME) {
                dndController.forceDisableAndClearOriginal()
            } else {
                dndController.restore()
            }
            log("✓ DND restored")
        } catch (e: Exception) {
            val msg = "Failed to restore DND: ${e.message}"
            log("✗ $msg")
            errors.add(msg)
        }

        if (errors.isEmpty()) {
            log("revertMode: completed successfully for ${mode.name}")
        } else {
            log("revertMode: completed with ${errors.size} error(s) for ${mode.name}")
            errors.forEach { log("  - $it") }
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "ModeActionExecutor"
    }
}

/**
 * Exception thrown when mode application fails.
 * Indicates that rollback has been attempted.
 */
class ModeApplicationException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)
