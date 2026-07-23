package com.banana.hypermodes.data

import android.content.Context
import android.provider.Settings
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.FullConfig

/**
 * Persists the user's mode list to Settings.Global using the system_server config format.
 * This replaces the previous SharedPreferences-based storage.
 */
object ModeStore {
    private const val CONFIG_KEY = "pixel_routines_full_config"

    val BUILT_IN_IDS = listOf("dnd", "bedtime", "driving")

    /**
     * Load the persisted mode list from Settings.Global, or the defaults on first run.
     */
    fun load(context: Context, defaults: () -> List<Mode>): List<Mode> {
        return try {
            val jsonString = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: return defaults()

            val fullConfig = ConfigParser.parseConfig(jsonString)
            fullConfig.modes.map { it.toMode(isActive = it.id == fullConfig.activeModeId) }
        } catch (e: Exception) {
            // Parse error or missing config - return defaults
            defaults()
        }
    }

    /**
     * Save the mode list to Settings.Global.
     * Preserves the existing activeModeId if present.
     */
    fun save(context: Context, modes: List<Mode>) {
        try {
            // Get existing activeModeId if present
            val existingActiveModeId = getCurrentActiveModeId(context)

            // Convert UI models to system_server config models
            val modeConfigs = modes.map { it.toModeConfig() }

            // Build FullConfig with preserved activeModeId
            val fullConfig = FullConfig(
                activeModeId = existingActiveModeId,
                modes = modeConfigs
            )

            // Serialize and write to Settings.Global
            val jsonString = ConfigParser.serializeConfig(fullConfig)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, jsonString)
        } catch (e: Exception) {
            // Serialization error - log but don't crash
            android.util.Log.e("ModeStore", "Failed to save modes", e)
        }
    }

    /**
     * Get the currently active mode ID from Settings.Global.
     * @return Active mode ID, or null if no mode is active
     */
    fun getCurrentActiveModeId(context: Context): String? {
        return try {
            val jsonString = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: return null

            val fullConfig = ConfigParser.parseConfig(jsonString)
            fullConfig.activeModeId
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Set the currently active mode ID in Settings.Global.
     * @param modeId Mode ID to activate, or null to deactivate
     */
    fun setActiveModeId(context: Context, modeId: String?) {
        try {
            val jsonString = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: return

            val updatedJson = ConfigParser.updateActiveModeId(jsonString, modeId)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, updatedJson)
        } catch (e: Exception) {
            android.util.Log.e("ModeStore", "Failed to set active mode ID", e)
        }
    }
}
