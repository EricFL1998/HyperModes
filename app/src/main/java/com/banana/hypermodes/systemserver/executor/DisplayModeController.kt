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
     * Original values are persisted in Settings.Global so they survive
     * system_server restarts and reboots — in-memory fields lost them, which
     * left dark mode stuck on until HyperOS re-evaluated at the next
     * screen-off. -1 = nothing saved.
     */
    private fun saveOriginal(key: String, value: Int) {
        if (Settings.Global.getInt(context.contentResolver, key, -1) == -1) {
            Settings.Global.putInt(context.contentResolver, key, value)
        }
    }

    /** Returns the saved original and clears it, or null if none. */
    private fun takeOriginal(key: String): Int? {
        val v = Settings.Global.getInt(context.contentResolver, key, -1)
        if (v == -1) return null
        Settings.Global.putString(context.contentResolver, key, null)
        return v
    }

    /**
     * Apply the specified display configuration.
     *
     * @param display Display settings to apply (dark mode, grayscale, etc.)
     */
    fun apply(display: DisplayConfig) {
        try {
            // Apply dark mode
            if (display.darkMode) {
                val cr = context.contentResolver
                // HyperOS tracks dark mode through its own switch (dark_mode_enable)
                // with DarkModeStatusTracker driving the visible flip. Writing it
                // directly triggers the tracker immediately — going through
                // UiModeManager alone leaves the flip deferred to the next screen-off.
                saveOriginal(KEY_ORIG_DARK_ENABLE, Settings.System.getInt(cr, DARK_MODE_ENABLE, 0))
                Settings.System.putInt(cr, DARK_MODE_ENABLE, 1)
                Settings.System.putInt(cr, DARK_MODE_SWITCH_NOW, 1)
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                saveOriginal(KEY_ORIG_NIGHT_MODE, uiModeManager.nightMode)
                uiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES
                log("apply: enabled dark mode")
            }

            // Apply grayscale
            if (display.grayscale) {
                val cr = context.contentResolver
                saveOriginal(KEY_ORIG_DALTONIZER_ENABLED, Settings.Secure.getInt(cr, "accessibility_display_daltonizer_enabled", 0))
                saveOriginal(KEY_ORIG_DALTONIZER, Settings.Secure.getInt(cr, "accessibility_display_daltonizer", 0))
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
                    saveOriginal(KEY_ORIG_PWM, Settings.Secure.getInt(cr, "mimotion_pwm_enable", 1))
                    Settings.Secure.putInt(cr, "mimotion_pwm_enable", if (enabled) 2 else 1)
                    log("apply: set adaptiveRefreshRatePro to $enabled")
                }
            }

            // Apply Eye Care (screen_paper_mode_enabled)
            if (display.eyeCare) {
                val cr = context.contentResolver
                saveOriginal(KEY_ORIG_PAPER, Settings.System.getInt(cr, "screen_paper_mode_enabled", 0))
                Settings.System.putInt(cr, "screen_paper_mode_enabled", 1)
                log("apply: enabled eyeCare")
            }

            // Apply custom Refresh Rate (user_refresh_rate)
            if (display.enableRefreshRate) {
                val cr = context.contentResolver
                saveOriginal(KEY_ORIG_REFRESH, Settings.Secure.getInt(cr, "user_refresh_rate", 60))
                Settings.Secure.putInt(cr, "user_refresh_rate", display.refreshRate)
                log("apply: set refresh rate to ${display.refreshRate}")
            }

        } catch (e: Exception) {
            log("apply: failed to apply display settings: ${e.message}")
            e.printStackTrace()
        }

        if (display.darkMode) {
            forceConfigurationReapply()
        }
    }

    /**
     * Restore default display settings by reverting to saved original values.
     */
    fun restore() {
        try {
            val cr = context.contentResolver

            // Restore dark mode
            takeOriginal(KEY_ORIG_DARK_ENABLE)?.let { original ->
                // Restore the HyperOS dark switch and poke dark_mode_switch_now so
                // DarkModeStatusTracker applies the flip NOW instead of at the
                // next screen-off (the lock-once-to-go-light symptom).
                Settings.System.putInt(cr, DARK_MODE_ENABLE, original)
                Settings.System.putInt(cr, DARK_MODE_SWITCH_NOW, 1)
            }
            takeOriginal(KEY_ORIG_NIGHT_MODE)?.let { original ->
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.nightMode = original
                log("restore: reverted dark mode to $original")
            }

            // Restore grayscale
            takeOriginal(KEY_ORIG_DALTONIZER_ENABLED)?.let { originalEnabled ->
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", originalEnabled)
                takeOriginal(KEY_ORIG_DALTONIZER)?.let { originalMode ->
                    Settings.Secure.putInt(cr, "accessibility_display_daltonizer", originalMode)
                }
                log("restore: reverted grayscale to enabled=$originalEnabled")
            }

            // Restore Adaptive Refresh Rate Pro
            takeOriginal(KEY_ORIG_PWM)?.let { original ->
                Settings.Secure.putInt(cr, "mimotion_pwm_enable", original)
                log("restore: reverted adaptiveRefreshRatePro to $original")
            }

            // Restore Eye Care
            takeOriginal(KEY_ORIG_PAPER)?.let { original ->
                Settings.System.putInt(cr, "screen_paper_mode_enabled", original)
                log("restore: reverted eyeCare to $original")
            }

            // Restore Refresh Rate
            takeOriginal(KEY_ORIG_REFRESH)?.let { original ->
                Settings.Secure.putInt(cr, "user_refresh_rate", original)
                log("restore: reverted refresh rate to $original")
            }

            forceConfigurationReapply()

        } catch (e: Exception) {
            log("restore: failed to restore display settings: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * HyperOS can defer a night-mode revert until the next screen-off (the
     * "lock once to go light" symptom). Force a global configuration refresh
     * from inside system_server so the theme flips immediately. Best-effort.
     */
    private fun forceConfigurationReapply() {
        try {
            // HyperOS strips ActivityManagerNative.updateConfiguration (proven on
            // device: NoSuchMethodException), so go through the live binder:
            // ActivityManager.getService() -> IActivityManager.updateConfiguration.
            val am = Class.forName("android.app.ActivityManager")
                .getDeclaredMethod("getService").apply { isAccessible = true }.invoke(null)
            Class.forName("android.app.IActivityManager")
                .getMethod("updateConfiguration", android.content.res.Configuration::class.java)
                .invoke(am, android.content.res.Configuration())
            log("forced configuration re-apply")
        } catch (t: Throwable) {
            log("forceConfigurationReapply failed: $t")
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

        private const val KEY_ORIG_NIGHT_MODE = "hypermodes_orig_night_mode"
        private const val KEY_ORIG_DARK_ENABLE = "hypermodes_orig_dark_enable"
        private const val DARK_MODE_ENABLE = "dark_mode_enable"
        private const val DARK_MODE_SWITCH_NOW = "dark_mode_switch_now"
        private const val KEY_ORIG_DALTONIZER_ENABLED = "hypermodes_orig_daltonizer_enabled"
        private const val KEY_ORIG_DALTONIZER = "hypermodes_orig_daltonizer"
        private const val KEY_ORIG_PWM = "hypermodes_orig_mimotion_pwm"
        private const val KEY_ORIG_PAPER = "hypermodes_orig_paper_mode"
        private const val KEY_ORIG_REFRESH = "hypermodes_orig_refresh_rate"
    }
}
