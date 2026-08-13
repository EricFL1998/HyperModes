package com.banana.hypermodes.systemserver.config

import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.encodeToString

/**
 * Parser for HyperModes configuration stored in Settings.Global.
 * Handles serialization/deserialization of FullConfig to/from JSON.
 */
object ConfigParser {

    private const val TAG = "ConfigParser"

    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "type"
    }

    /**
     * Parse JSON string into FullConfig object.
     * @param jsonString JSON configuration string
     * @return Parsed FullConfig
     * @throws kotlinx.serialization.SerializationException if JSON is invalid
     */
    fun parseConfig(jsonString: String): FullConfig {
        val tree = json.parseToJsonElement(jsonString)
        val migrated = migrateLegacyDisplayConfigs(tree)
        
        val config = json.decodeFromJsonElement(FullConfig.serializer(), migrated)
        
        return config
    }

    /**
     * Configs written before tri-state display overrides (pre-v1.2) always encoded
     * every display toggle as a boolean, where "false" meant "this mode does not
     * touch the setting". In the tri-state schema false actively forces the setting
     * off and null means "don't touch", so legacy booleans must be rewritten:
     * darkMode true -> 1, false -> absent; toggle false -> absent, true -> kept.
     * A boolean darkMode marks a display object as legacy (it is Int? since v1.2).
     */
    /**
     * Rewrite legacy config shapes so they decode into the current schema:
     * - pre-v1.2: display toggles were always-encoded booleans (see below)
     * - v1.2: raise-to-wake / wake-for-notifications lived under "device"
     */
    private fun migrateLegacyDisplayConfigs(root: JsonElement): JsonElement {
        val obj = root as? JsonObject ?: return root
        val modes = obj["modes"] as? JsonArray ?: return root
        val migratedModes = modes.map { modeElement ->
            (modeElement as? JsonObject)?.let { migrateMode(it) } ?: modeElement
        }
        return JsonObject(obj.toMutableMap().apply { put("modes", JsonArray(migratedModes)) })
    }

    private fun migrateMode(mode: JsonObject): JsonObject {
        val fields = mode.toMutableMap()
        (fields["display"] as? JsonObject)?.let { fields["display"] = migrateLegacyDisplay(it) }
        hoistV12DeviceWakeKeys(fields)
        normalizeBuiltInModeNames(fields)
        migrateLegacyTriggers(fields)
        return JsonObject(fields)
    }

    /**
     * Migrate v1.3 `complexTriggers` into the v2.0 `triggerGroups` schema.
     * Each legacy trigger becomes its own Single group (v1.3 semantics are OR,
     * and multiple Single groups are also OR'd together), so old configs keep
     * working without the engine retaining the legacy complexTriggers path.
     */
    private fun migrateLegacyTriggers(fields: MutableMap<String, JsonElement>) {
        val complexTriggers = fields["complexTriggers"] as? JsonArray ?: return
        val existingGroups = fields["triggerGroups"] as? JsonArray
        if (complexTriggers.isEmpty()) {
            fields.remove("complexTriggers")
            return
        }
        // Only migrate when the mode has no trigger groups yet; otherwise the
        // newer schema wins and the legacy list is simply dropped.
        if (existingGroups != null && existingGroups.isNotEmpty()) {
            fields.remove("complexTriggers")
            return
        }
        val groups = complexTriggers.map { trigger ->
            JsonObject(
                mapOf(
                    "type" to JsonPrimitive(
                        "com.banana.hypermodes.systemserver.config.TriggerGroup.Single"
                    ),
                    "trigger" to trigger
                )
            )
        }
        fields["triggerGroups"] = JsonArray(groups)
        fields.remove("complexTriggers")
        HyperLog.i(TAG, "Migrated ${groups.size} legacy complexTriggers to triggerGroups")
    }

    /**
     * Normalize built-in mode names to their canonical English defaults.
     * Legacy configs may have stored localized names (勿扰模式, 睡眠模式, 驾驶模式)
     * or old English names. Standardize to clean defaults so display resolution
     * works correctly and configs are cleaner.
     */
    private fun normalizeBuiltInModeNames(fields: MutableMap<String, JsonElement>) {
        val id = (fields["id"] as? JsonPrimitive)?.content ?: return
        val currentName = (fields["name"] as? JsonPrimitive)?.content ?: return

        val canonicalName = when (id) {
            "dnd" -> if (currentName in setOf("勿扰", "勿扰模式", "Do Not Disturb")) "Do Not Disturb" else null
            "bedtime" -> if (currentName in setOf("睡眠", "睡眠模式", "Bedtime")) "Bedtime" else null
            "driving" -> if (currentName in setOf("驾驶", "驾驶模式", "Driving")) "Driving" else null
            else -> null
        }

        if (canonicalName != null) {
            fields["name"] = JsonPrimitive(canonicalName)
        }
    }

    /**
     * v1.2 stored raise-to-wake and wake-for-notifications under "device"; v1.3
     * moved them into "display". Hoist non-null values so existing configs keep
     * them; a value already present in "display" wins.
     */
    private fun hoistV12DeviceWakeKeys(fields: MutableMap<String, JsonElement>) {
        val device = fields["device"] as? JsonObject ?: return
        val display = (fields["display"] as? JsonObject)?.toMutableMap() ?: return
        val deviceFields = device.toMutableMap()
        for (key in listOf("enableRaiseToWake", "enableWakeForNotifications")) {
            val value = deviceFields.remove(key) ?: continue
            if (value !is JsonNull) display.putIfAbsent(key, value)
        }
        fields["display"] = JsonObject(display)
        fields["device"] = JsonObject(deviceFields)
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
