package com.banana.hypermodes.data

import android.content.Context
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persists intent configs imported from the automations screen so the
 * intent trigger picker can reuse them.
 */
object ImportedIntentStore {
    private const val PREF_NAME = "hypermodes_prefs"
    private const val KEY_IMPORTED_INTENTS = "imported_intent_configs"

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    fun loadAll(context: Context): List<IntentConfig> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val raw = prefs.getString(KEY_IMPORTED_INTENTS, null) ?: return emptyList()
        return try {
            json.decodeFromString<List<IntentConfig>>(raw)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(context: Context, config: IntentConfig) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = loadAll(context).toMutableList()
        // Replace an existing config for the same package so re-imports update it.
        existing.removeAll { it.packageName == config.packageName }
        existing.add(0, config)
        val raw = json.encodeToString<List<IntentConfig>>(existing)
        prefs.edit().putString(KEY_IMPORTED_INTENTS, raw).apply()
    }
}
