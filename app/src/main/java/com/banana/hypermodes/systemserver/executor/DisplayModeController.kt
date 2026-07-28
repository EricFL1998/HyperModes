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

    private var originalDarkMode: Int? = null
    private var originalGrayscaleEnabled: Int? = null
    private var originalGrayscaleMode: Int? = null
    private var originalAdaptiveRefreshRatePro: Int? = null
    private var originalEyeCare: Int? = null
    private var originalRefreshRate: Int? = null

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
                if (originalDarkMode == null) {
                    originalDarkMode = uiModeManager.nightMode
                    log("apply: saved original dark mode: $originalDarkMode")
                }
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                log("apply: enabled dark mode")
            }

            // Apply grayscale
            if (display.grayscale) {
                val cr = context.contentResolver
                if (originalGrayscaleEnabled == null) {
                    originalGrayscaleEnabled = Settings.Secure.getInt(cr, "accessibility_display_daltonizer_enabled", 0)
                    originalGrayscaleMode = Settings.Secure.getInt(cr, "accessibility_display_daltonizer", 0)
                    log("apply: saved original grayscale: enabled=$originalGrayscaleEnabled, mode=$originalGrayscaleMode")
                }
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", 1)
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer", 0) // 0 = grayscale
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
                    val cr = context.contentResolver
                    if (originalAdaptiveRefreshRatePro == null) {
                        originalAdaptiveRefreshRatePro = Settings.Secure.getInt(cr, "mimotion_pwm_enable", 1)
                        log("apply: saved original adaptiveRefreshRatePro: $originalAdaptiveRefreshRatePro")
                    }
                    Settings.Secure.putInt(cr, "mimotion_pwm_enable", if (enabled) 2 else 1)
                    log("apply: set adaptiveRefreshRatePro to $enabled")
                }
            }

            // Apply Eye Care (screen_paper_mode_enabled)
            if (display.eyeCare) {
                val cr = context.contentResolver
                if (originalEyeCare == null) {
                    originalEyeCare = Settings.System.getInt(cr, "screen_paper_mode_enabled", 0)
                    log("apply: saved original eyeCare: $originalEyeCare")
                }
                Settings.System.putInt(cr, "screen_paper_mode_enabled", 1)
                log("apply: enabled eyeCare")
            }

            // Apply custom Refresh Rate (user_refresh_rate)
            if (display.enableRefreshRate) {
                val cr = context.contentResolver
                if (originalRefreshRate == null) {
                    originalRefreshRate = Settings.Secure.getInt(cr, "user_refresh_rate", 60)
                    log("apply: saved original refresh rate: $originalRefreshRate")
                }
                Settings.Secure.putInt(cr, "user_refresh_rate", display.refreshRate)
                log("apply: set refresh rate to ${display.refreshRate}")
            }

        } catch (e: Exception) {
            log("apply: failed to apply display settings: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Restore default display settings by reverting to saved original values.
     */
    fun restore() {
        try {
            val cr = context.contentResolver

            // Restore dark mode
            originalDarkMode?.let { original ->
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.nightMode = original
                log("restore: reverted dark mode to $original")
                originalDarkMode = null
            }

            // Restore grayscale
            if (originalGrayscaleEnabled != null) {
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", originalGrayscaleEnabled!!)
                if (originalGrayscaleMode != null) {
                    Settings.Secure.putInt(cr, "accessibility_display_daltonizer", originalGrayscaleMode!!)
                }
                log("restore: reverted grayscale to enabled=$originalGrayscaleEnabled, mode=$originalGrayscaleMode")
                originalGrayscaleEnabled = null
                originalGrayscaleMode = null
            }

            // Restore Adaptive Refresh Rate Pro
            originalAdaptiveRefreshRatePro?.let { original ->
                Settings.Secure.putInt(cr, "mimotion_pwm_enable", original)
                log("restore: reverted adaptiveRefreshRatePro to $original")
                originalAdaptiveRefreshRatePro = null
            }

            // Restore Eye Care
            originalEyeCare?.let { original ->
                Settings.System.putInt(cr, "screen_paper_mode_enabled", original)
                log("restore: reverted eyeCare to $original")
                originalEyeCare = null
            }

            // Restore Refresh Rate
            originalRefreshRate?.let { original ->
                Settings.Secure.putInt(cr, "user_refresh_rate", original)
                log("restore: reverted refresh rate to $original")
                originalRefreshRate = null
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
