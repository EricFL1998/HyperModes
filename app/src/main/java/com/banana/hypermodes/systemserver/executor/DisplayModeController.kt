package com.banana.hypermodes.systemserver.executor

import android.app.UiModeManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.DisplayConfig

/**
 * Controller for managing display-related settings like dark mode and grayscale.
 *
 * This component controls:
 * - Dark mode: Uses UiModeManager to toggle system-wide dark theme
 * - Grayscale: Uses accessibility daltonizer settings to convert display to grayscale
 *
 * Note: dimWallpaper and keepScreenOff are not implemented in this controller
 * as they require different mechanisms (WallpaperManager and PowerManager respectively).
 *
 * @param context System context from system_server
 */
class DisplayModeController(private val context: Context) {

    /**
     * Apply the specified display configuration.
     *
     * @param display Display settings to apply (dark mode, grayscale, etc.)
     */
    fun apply(display: DisplayConfig) {
        try {
            // Apply dark mode
            if (display.darkMode) {
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                log("apply: enabled dark mode")
            }

            // Apply grayscale
            if (display.grayscale) {
                Settings.Secure.putInt(
                    context.contentResolver,
                    "accessibility_display_daltonizer_enabled",
                    1
                )
                Settings.Secure.putInt(
                    context.contentResolver,
                    "accessibility_display_daltonizer",
                    0  // 0 = grayscale mode
                )
                log("apply: enabled grayscale")
            }

            // Note: dimWallpaper and keepScreenOff are not implemented yet
            if (display.dimWallpaper) {
                log("apply: dimWallpaper requested but not implemented")
            }
            if (display.keepScreenOff) {
                log("apply: keepScreenOff requested but not implemented")
            }

            // Apply Adaptive Refresh Rate Pro (mimotion_pwm_enable)
            display.adaptiveRefreshRatePro?.let { enabled ->
                if (isAdaptiveRefreshRateProSupported()) {
                    Settings.Secure.putInt(
                        context.contentResolver,
                        "mimotion_pwm_enable",
                        if (enabled) 2 else 1
                    )
                    log("apply: set adaptiveRefreshRatePro to $enabled")
                }
            }

        } catch (e: Exception) {
            log("apply: failed to apply display settings: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Restore default display settings by disabling dark mode and grayscale.
     */
    fun restore() {
        try {
            // Restore dark mode to auto
            val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            uiModeManager.nightMode = UiModeManager.MODE_NIGHT_AUTO
            log("restore: reset dark mode to auto")

            // Disable grayscale
            Settings.Secure.putInt(
                context.contentResolver,
                "accessibility_display_daltonizer_enabled",
                0
            )
            log("restore: disabled grayscale")

            // Restore Adaptive Refresh Rate Pro to default (disabled/1)
            if (isAdaptiveRefreshRateProSupported()) {
                Settings.Secure.putInt(
                    context.contentResolver,
                    "mimotion_pwm_enable",
                    1
                )
                log("restore: reset adaptiveRefreshRatePro to default")
            }

        } catch (e: Exception) {
            log("restore: failed to restore display settings: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun isAdaptiveRefreshRateProSupported(): Boolean {
        return try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "ro.display.enable_pwm_switch", false) as Boolean
        } catch (_: Exception) {
            false
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "DisplayModeController"
    }
}
