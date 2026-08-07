package com.banana.hypermodes.automation

import android.content.Context
import android.provider.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class AutomationConfig(
    val automations: List<SavedAutomationData>
)

@Serializable
data class SavedAutomationData(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val enabled: Boolean,
    val blocks: List<String> // Serialized block data
)

object AutomationStore {
    private const val CONFIG_KEY = "hypermodes_automations_config"
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun load(context: Context): List<SavedAutomation> {
        return try {
            val jsonString = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: return emptyList()
            
            val config = json.decodeFromString<AutomationConfig>(jsonString)
            config.automations.map { data ->
                SavedAutomation(
                    id = data.id,
                    name = data.name,
                    icon = data.icon,
                    description = data.description,
                    enabled = data.enabled,
                    blocks = emptyList() // TODO: Deserialize blocks
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun save(context: Context, automations: List<SavedAutomation>) {
        try {
            val data = automations.map { automation ->
                SavedAutomationData(
                    id = automation.id,
                    name = automation.name,
                    icon = automation.icon,
                    description = automation.description,
                    enabled = automation.enabled,
                    blocks = emptyList() // TODO: Serialize blocks
                )
            }
            
            val config = AutomationConfig(automations = data)
            val jsonString = json.encodeToString(config)
            
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, jsonString)
        } catch (e: Exception) {
            // Log error
        }
    }
    
    fun add(context: Context, automation: SavedAutomation) {
        val current = load(context)
        save(context, current + automation)
    }
    
    fun update(context: Context, automation: SavedAutomation) {
        val current = load(context)
        val updated = current.map { if (it.id == automation.id) automation else it }
        save(context, updated)
    }
    
    fun delete(context: Context, automationId: String) {
        val current = load(context)
        save(context, current.filter { it.id != automationId })
    }
}
