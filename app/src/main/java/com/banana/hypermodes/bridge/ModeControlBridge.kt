package com.banana.hypermodes.bridge

import android.content.Context
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.systemserver.config.ConfigParser

/**
 * Bridge between UI and system_server for mode control.
 * UI calls this instead of ModeEngine.
 *
 * The bridge updates Settings.Global which triggers ContentObserver in RoutineCoreEngine,
 * which then applies the mode via ModeActionExecutor in system_server.
 */
object ModeControlBridge {
    private const val TAG = "ModeControlBridge"
    private const val CONFIG_KEY = "pixel_routines_full_config"

    /**
     * Activate a mode by updating activeModeId in Settings.Global.
     * RoutineCoreEngine will detect the change and apply the mode.
     */
    fun activateMode(context: Context, modeId: String) {
        try {
            val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: """{"activeModeId":null,"modes":[]}"""
            val updated = ConfigParser.updateActiveModeId(json, modeId)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, updated)
            HyperLog.i(TAG, "Activated mode: $modeId via Settings.Global")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to activate mode: $modeId", e)
        }
    }

    /**
     * Deactivate the current mode by clearing activeModeId in Settings.Global.
     * RoutineCoreEngine will detect the change and revert the mode.
     */
    fun deactivateMode(context: Context, modeId: String) {
        try {
            val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: """{"activeModeId":null,"modes":[]}"""
            val updated = ConfigParser.updateActiveModeId(json, null)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, updated)
            HyperLog.i(TAG, "Deactivated mode: $modeId via Settings.Global")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to deactivate mode: $modeId", e)
        }
    }
}
