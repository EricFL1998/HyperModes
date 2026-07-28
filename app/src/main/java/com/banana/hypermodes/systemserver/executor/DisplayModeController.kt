package com.banana.hypermodes.systemserver.executor

import android.app.UiModeManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.DisplayConfig

/**
 * Controller for managing display-related settings with optional overrides.
 * Only modifies settings that are non-null in the config.
 */
class DisplayModeController(private val context: Context) {

    private fun saveOriginal(key: String, value: Int) {
        if (Settings.Global.getInt(context.contentResolver, key, -1) == -1) {
            Settings.Global.putInt(context.contentResolver, key, value)
        }
    }

    private fun takeOriginal(key: String): Int? {
        val v = Settings.Global.getInt(context.contentResolver, key, -1)
        if (v == -1) return null
        Settings.Global.putString(context.contentResolver, key, null)
        return v
    }

    fun apply(display: DisplayConfig) {
        try {
            val cr = context.contentResolver

            // Theme Override (0: Light, 1: Dark)
            display.darkMode?.let { target ->
                saveOriginal(KEY_ORIG_DARK_ENABLE, Settings.System.getInt(cr, DARK_MODE_ENABLE, 0))
                Settings.System.putInt(cr, DARK_MODE_ENABLE, target)
                Settings.System.putInt(cr, DARK_MODE_SWITCH_NOW, 1)

                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                saveOriginal(KEY_ORIG_NIGHT_MODE, uiModeManager.nightMode)
                uiModeManager.nightMode = if (target == 1) UiModeManager.MODE_NIGHT_YES else UiModeManager.MODE_NIGHT_NO
                log("apply: set theme to ${if (target == 1) "Dark" else "Light"}")
            }

            // Grayscale
            display.grayscale?.let { enabled ->
                saveOriginal(KEY_ORIG_DALTONIZER_ENABLED, Settings.Secure.getInt(cr, "accessibility_display_daltonizer_enabled", 0))
                saveOriginal(KEY_ORIG_DALTONIZER, Settings.Secure.getInt(cr, "accessibility_display_daltonizer", 0))
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", if (enabled) 1 else 0)
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer", 0) // 0 = grayscale
                log("apply: set grayscale to $enabled")
            }

            // Always-on Display
            display.enableAod?.let { enabled ->
                saveOriginal(KEY_ORIG_AOD_MODE, Settings.Secure.getInt(cr, "aod_mode", 1))
                Settings.Secure.putInt(cr, "aod_mode", if (enabled) 1 else 0)
                log("apply: set AOD to $enabled")
            }

            // Adaptive Refresh Rate Pro
            display.adaptiveRefreshRatePro?.let { enabled ->
                if (isAdaptiveRefreshRateProSupported()) {
                    saveOriginal(KEY_ORIG_PWM, Settings.Secure.getInt(cr, "mimotion_pwm_enable", 1))
                    Settings.Secure.putInt(cr, "mimotion_pwm_enable", if (enabled) 2 else 1)
                    log("apply: set adaptiveRefreshRatePro to $enabled")
                }
            }

            // Eye Care
            display.eyeCare?.let { enabled ->
                saveOriginal(KEY_ORIG_PAPER, Settings.System.getInt(cr, "screen_paper_mode_enabled", 0))
                Settings.System.putInt(cr, "screen_paper_mode_enabled", if (enabled) 1 else 0)
                log("apply: set eyeCare to $enabled")
            }

            // Refresh Rate
            display.enableRefreshRate?.let { enabled ->
                if (enabled) {
                    saveOriginal(KEY_ORIG_REFRESH, Settings.Secure.getInt(cr, "user_refresh_rate", 60))
                    Settings.Secure.putInt(cr, "user_refresh_rate", display.refreshRate)
                    log("apply: set refresh rate to ${display.refreshRate}")
                }
            }

            if (display.darkMode != null) {
                forceConfigurationReapply()
            }
        } catch (e: Exception) {
            log("apply: failed: ${e.message}")
        }
    }

    fun restore() {
        try {
            val cr = context.contentResolver

            takeOriginal(KEY_ORIG_DARK_ENABLE)?.let { original ->
                Settings.System.putInt(cr, DARK_MODE_ENABLE, original)
                Settings.System.putInt(cr, DARK_MODE_SWITCH_NOW, 1)
            }
            takeOriginal(KEY_ORIG_NIGHT_MODE)?.let { original ->
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.nightMode = original
            }

            takeOriginal(KEY_ORIG_DALTONIZER_ENABLED)?.let { original ->
                Settings.Secure.putInt(cr, "accessibility_display_daltonizer_enabled", original)
                takeOriginal(KEY_ORIG_DALTONIZER)?.let { m -> Settings.Secure.putInt(cr, "accessibility_display_daltonizer", m) }
            }

            takeOriginal(KEY_ORIG_AOD_MODE)?.let { original ->
                Settings.Secure.putInt(cr, "aod_mode", original)
            }

            takeOriginal(KEY_ORIG_PWM)?.let { original ->
                Settings.Secure.putInt(cr, "mimotion_pwm_enable", original)
            }

            takeOriginal(KEY_ORIG_PAPER)?.let { original ->
                Settings.System.putInt(cr, "screen_paper_mode_enabled", original)
            }

            takeOriginal(KEY_ORIG_REFRESH)?.let { original ->
                Settings.Secure.putInt(cr, "user_refresh_rate", original)
            }

            forceConfigurationReapply()
        } catch (e: Exception) {
            log("restore: failed: ${e.message}")
        }
    }

    private fun forceConfigurationReapply() {
        try {
            val am = Class.forName("android.app.ActivityManager")
                .getDeclaredMethod("getService").apply { isAccessible = true }.invoke(null)
            Class.forName("android.app.IActivityManager")
                .getMethod("updateConfiguration", android.content.res.Configuration::class.java)
                .invoke(am, android.content.res.Configuration())
        } catch (_: Throwable) {}
    }

    private fun isAdaptiveRefreshRateProSupported(): Boolean {
        return try {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "ro.display.enable_pwm_switch", false) as Boolean
        } catch (_: Exception) { false }
    }

    private fun log(msg: String) = Log.i(TAG, msg)

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
        private const val KEY_ORIG_AOD_MODE = "hypermodes_orig_aod_mode"
    }
}
