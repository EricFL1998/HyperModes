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

    /** 进程内缓存：避免每次读取都解析 SharedPreferences JSON。 */
    @Volatile
    private var cache: List<IntentConfig>? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    /** 带进程级缓存的读取。 */
    fun loadAllCached(context: Context): List<IntentConfig> {
        cache?.let { return it }
        return loadAll(context).also { cache = it }
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
        // 更新缓存，保持与存储一致
        cache = existing
    }

    /** 删除整个 app 类别的意图配置。 */
    fun deleteApp(context: Context, packageName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = loadAll(context).filterNot { it.packageName == packageName }
        val raw = json.encodeToString<List<IntentConfig>>(existing)
        prefs.edit().putString(KEY_IMPORTED_INTENTS, raw).apply()
        cache = existing
    }

    /** 删除指定 app 类别中的单个意图；删除后为空则移除整个类别。 */
    fun deleteIntent(context: Context, packageName: String, intentName: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val existing = loadAll(context).toMutableList()
        val idx = existing.indexOfFirst { it.packageName == packageName }
        if (idx >= 0) {
            val config = existing[idx]
            val remaining = config.intents.filterNot { it.name == intentName }
            if (remaining.isEmpty()) {
                existing.removeAt(idx)
            } else {
                existing[idx] = config.copy(intents = remaining)
            }
            val raw = json.encodeToString<List<IntentConfig>>(existing)
            prefs.edit().putString(KEY_IMPORTED_INTENTS, raw).apply()
            cache = existing
        }
    }
}
