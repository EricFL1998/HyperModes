package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.DeviceConfig

/**
 * Controller for managing device-related settings (Radios, Performance) with optional overrides.
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

            // WiFi
            device.enableWifi?.let { enabled ->
                saveOriginal(KEY_ORIG_WIFI_ON, Settings.Global.getInt(cr, Settings.Global.WIFI_ON, 1))
                Settings.Global.putInt(cr, Settings.Global.WIFI_ON, if (enabled) 1 else 0)
                log("apply: set WiFi to $enabled")
            }

            // Bluetooth
            device.enableBluetooth?.let { enabled ->
                saveOriginal(KEY_ORIG_BLUETOOTH_ON, Settings.Global.getInt(cr, Settings.Global.BLUETOOTH_ON, 1))
                Settings.Global.putInt(cr, Settings.Global.BLUETOOTH_ON, if (enabled) 1 else 0)
                log("apply: set Bluetooth to $enabled")
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

            takeOriginal(KEY_ORIG_WIFI_ON)?.let { original ->
                Settings.Global.putInt(cr, Settings.Global.WIFI_ON, original)
            }

            takeOriginal(KEY_ORIG_BLUETOOTH_ON)?.let { original ->
                Settings.Global.putInt(cr, Settings.Global.BLUETOOTH_ON, original)
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
        private const val KEY_ORIG_PERFORMANCE_MODE = "hypermodes_orig_performance_mode"
        private const val KEY_ORIG_5G_MODE = "hypermodes_orig_5g_mode"
        private const val KEY_ORIG_WIFI_ON = "hypermodes_orig_wifi_on"
        private const val KEY_ORIG_BLUETOOTH_ON = "hypermodes_orig_bluetooth_on"
    }
}
