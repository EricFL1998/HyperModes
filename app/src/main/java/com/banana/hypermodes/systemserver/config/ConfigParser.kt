package com.banana.hypermodes.systemserver.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

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
        return json.decodeFromString<FullConfig>(jsonString)
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
     * Update only the activeModeId in existing JSON config.
     * This is more efficient than parsing/modifying/serializing the entire config.
     *
     * @param jsonString Existing JSON config
     * @param modeId New active mode ID (null to clear)
     * @return Updated JSON string
     */
    fun updateActiveModeId(jsonString: String, modeId: String?): String {
        val config = parseConfig(jsonString)
        val updated = config.copy(activeModeId = modeId)
        return serializeConfig(updated)
    }
}
