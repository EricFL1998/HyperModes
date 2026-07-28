package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.DeviceConfig

/**
 * Controller for managing device-related settings with optional overrides.
 */
class DeviceController(private val context: Context) {

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

    fun apply(device: DeviceConfig?) {
        if (device == null) return
        val cr = context.contentResolver

        try {
            // Performance Mode
            device.performanceMode?.let { mode ->
                saveOriginal(KEY_ORIG_PERFORMANCE_MODE, Settings.System.getInt(cr, POWER_PERFORMANCE_MODE_OPEN, 0))
                Settings.System.putInt(cr, POWER_PERFORMANCE_MODE_OPEN, mode)
                log("apply: set performanceMode to $mode")
            }

            // 5G Network
            device.enable5g?.let { enabled ->
                saveOriginal(KEY_ORIG_5G_MODE, Settings.Global.getInt(cr, ENABLED_5G_MODE, 1))
                Settings.Global.putInt(cr, ENABLED_5G_MODE, if (enabled) 1 else 0)
                log("apply: set 5G to $enabled")
            }

            // Raise to Wake
            device.enableRaiseToWake?.let { enabled ->
                saveOriginal(KEY_ORIG_GESTURE_WAKEUP, Settings.System.getInt(cr, GESTURE_WAKEUP, 0))
                Settings.System.putInt(cr, GESTURE_WAKEUP, if (enabled) 1 else 0)
                log("apply: set raiseToWake to $enabled")
            }

            // Wake for Notifications
            device.enableWakeForNotifications?.let { enabled ->
                saveOriginal(KEY_ORIG_WAKEUP_FOR_NOTIFICATION, Settings.System.getInt(cr, WAKEUP_FOR_KEYGUARD_NOTIFICATION, 0))
                Settings.System.putInt(cr, WAKEUP_FOR_KEYGUARD_NOTIFICATION, if (enabled) 1 else 0)
                log("apply: set wakeForNotifications to $enabled")
            }

        } catch (e: Exception) {
            log("apply: failed: ${e.message}")
        }
    }

    fun restore() {
        val cr = context.contentResolver
        try {
            takeOriginal(KEY_ORIG_PERFORMANCE_MODE)?.let { original ->
                Settings.System.putInt(cr, POWER_PERFORMANCE_MODE_OPEN, original)
            }

            takeOriginal(KEY_ORIG_5G_MODE)?.let { original ->
                Settings.Global.putInt(cr, ENABLED_5G_MODE, original)
            }

            takeOriginal(KEY_ORIG_GESTURE_WAKEUP)?.let { original ->
                Settings.System.putInt(cr, GESTURE_WAKEUP, original)
            }

            takeOriginal(KEY_ORIG_WAKEUP_FOR_NOTIFICATION)?.let { original ->
                Settings.System.putInt(cr, WAKEUP_FOR_KEYGUARD_NOTIFICATION, original)
            }
        } catch (e: Exception) {
            log("restore: failed: ${e.message}")
        }
    }

    private fun log(msg: String) = Log.i(TAG, msg)

    companion object {
        private const val TAG = "DeviceController"
        private const val POWER_PERFORMANCE_MODE_OPEN = "performance_mode"
        private const val ENABLED_5G_MODE = "enabled_5g_mode"
        private const val GESTURE_WAKEUP = "gesture_wakeup"
        private const val WAKEUP_FOR_KEYGUARD_NOTIFICATION = "wakeup_for_keyguard_notification"
        private const val KEY_ORIG_PERFORMANCE_MODE = "hypermodes_orig_performance_mode"
        private const val KEY_ORIG_5G_MODE = "hypermodes_orig_5g_mode"
        private const val KEY_ORIG_GESTURE_WAKEUP = "hypermodes_orig_gesture_wakeup"
        private const val KEY_ORIG_WAKEUP_FOR_NOTIFICATION = "hypermodes_orig_wakeup_for_notification"
    }
}
