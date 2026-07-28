package com.banana.hypermodes.systemserver.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.encodeToString

/**
 * Parser for HyperModes configuration stored in Settings.Global.
 * Handles serialization/deserialization of FullConfig to/from JSON.
 */
object ConfigParser {

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    /**
     * Parse JSON string into FullConfig object.
     * @param jsonString JSON configuration string
     * @return Parsed FullConfig
     * @throws kotlinx.serialization.SerializationException if JSON is invalid
     */
    fun parseConfig(jsonString: String): FullConfig {
        val tree = json.parseToJsonElement(jsonString)
        return json.decodeFromJsonElement(FullConfig.serializer(), migrateLegacyDisplayConfigs(tree))
    }

    /**
     * Configs written before tri-state display overrides (pre-v1.2) always encoded
     * every display toggle as a boolean, where "false" meant "this mode does not
     * touch the setting". In the tri-state schema false actively forces the setting
     * off and null means "don't touch", so legacy booleans must be rewritten:
     * darkMode true -> 1, false -> absent; toggle false -> absent, true -> kept.
     * A boolean darkMode marks a display object as legacy (it is Int? since v1.2).
     */
    private fun migrateLegacyDisplayConfigs(root: JsonElement): JsonElement {
        val obj = root as? JsonObject ?: return root
        val modes = obj["modes"] as? JsonArray ?: return root
        val migratedModes = modes.map { modeElement ->
            val mode = modeElement as? JsonObject ?: return@map modeElement
            val display = mode["display"] as? JsonObject ?: return@map modeElement
            JsonObject(mode.toMutableMap().apply { put("display", migrateLegacyDisplay(display)) })
        }
        return JsonObject(obj.toMutableMap().apply { put("modes", JsonArray(migratedModes)) })
    }

    private fun migrateLegacyDisplay(display: JsonObject): JsonObject {
        val legacyDarkMode = (display["darkMode"] as? JsonPrimitive)?.booleanOrNull
            ?: return display // new format: darkMode is a number or null
        val migrated = display.toMutableMap()
        if (legacyDarkMode) migrated["darkMode"] = JsonPrimitive(1) else migrated.remove("darkMode")
        for (key in listOf("grayscale", "keepScreenOff", "eyeCare", "enableRefreshRate")) {
            if ((migrated[key] as? JsonPrimitive)?.booleanOrNull == false) migrated.remove(key)
        }
        return JsonObject(migrated)
    }

    /**
     * Serialize FullConfig object to JSON string.
     * @param config FullConfig to serialize
     * @return JSON string representation
     */
    fun serializeConfig(config: FullConfig): String {
        return json.encodeToString(config)
    }

    /**
     * Update the activeModeId and dismissedModes in existing JSON config.
     * Non-null modeId also records lastModeId; null clears only activeModeId.
     *
     * @param jsonString Existing JSON config
     * @param modeId New active mode ID (null to clear)
     * @param dismissedModes Current dismissal records
     * @return Updated JSON string
     */
    fun updateActiveModeId(jsonString: String, modeId: String?, dismissedModes: Map<String, Long>? = null): String {
        val config = parseConfig(jsonString)
        val updated = if (modeId == null) {
            config.copy(activeModeId = null, dismissedModes = dismissedModes ?: config.dismissedModes)
        } else {
            config.copy(activeModeId = modeId, lastModeId = modeId, dismissedModes = dismissedModes ?: config.dismissedModes)
        }
        return serializeConfig(updated)
    }

    /**
     * Update only the lastModeId in existing JSON config.
     *
     * @param jsonString Existing JSON config
     * @param modeId New last mode ID (null to clear)
     * @return Updated JSON string
     */
    fun updateLastModeId(jsonString: String, modeId: String?): String {
        val config = parseConfig(jsonString)
        return serializeConfig(config.copy(lastModeId = modeId))
    }
}
