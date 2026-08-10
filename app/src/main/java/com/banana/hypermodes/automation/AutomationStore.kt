package com.banana.hypermodes.automation

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/** 持久化 DTO：与 Compose Color 解耦，便于序列化。 */
@Serializable
data class AutomationConfigDto(
    val version: Int = 2,
    val automations: List<SavedAutomationDto>
)

@Serializable
data class SavedAutomationDto(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val enabled: Boolean,
    val createdAt: Long,
    val lastModified: Long,
    val blocks: List<BlockDto>
)

@Serializable
data class BlockDto(
    val id: String,
    val typeId: String,
    val label: String,
    val icon: String,
    val iconColorArgb: Long,
    val parameters: List<ParameterDto> = emptyList(),
    val children: List<BlockDto> = emptyList(),
    val elseChildren: List<BlockDto> = emptyList()
)

@Serializable
data class ParameterDto(
    val kind: String, // bool / int / choice / string
    val key: String,
    val label: String,
    val boolValue: Boolean? = null,
    val intValue: Int? = null,
    val intMin: Int? = null,
    val intMax: Int? = null,
    val stringValue: String? = null,
    val options: List<String> = emptyList()
)

/**
 * 自动化持久化：以 JSON 写入 Settings.Global。
 * 块树（含参数、嵌套子块、图标颜色）完整序列化，修复此前"块丢失"的问题。
 */
object AutomationStore {
    private const val TAG = "AutomationStore"
    internal const val CONFIG_KEY = "hypermodes_automations_config"

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    fun load(context: Context): List<SavedAutomation> {
        return try {
            val jsonString = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
                ?: return emptyList()
            val config = json.decodeFromString<AutomationConfigDto>(jsonString)
            config.automations.map { it.toAutomation() }
        } catch (e: Exception) {
            Log.w(TAG, "load failed, returning empty", e)
            emptyList()
        }
    }

    fun save(context: Context, automations: List<SavedAutomation>) {
        try {
            val config = AutomationConfigDto(
                automations = automations.map { it.toDto() }
            )
            val jsonString = json.encodeToString(config)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "save failed", e)
        }
    }

    fun add(context: Context, automation: SavedAutomation) {
        val current = load(context)
        save(context, current + automation)
    }

    fun update(context: Context, automation: SavedAutomation) {
        val current = load(context)
        save(context, current.map { if (it.id == automation.id) automation else it })
    }

    fun delete(context: Context, automationId: String) {
        val current = load(context)
        save(context, current.filter { it.id != automationId })
    }
}

// ==================== DTO <-> 模型转换 ====================

internal fun SavedAutomation.toDto(): SavedAutomationDto = SavedAutomationDto(
    id = id,
    name = name,
    icon = icon,
    description = description,
    enabled = enabled,
    createdAt = createdAt,
    lastModified = lastModified,
    blocks = blocks.map { it.toDto() }
)

internal fun SavedAutomationDto.toAutomation(): SavedAutomation = SavedAutomation(
    id = id,
    name = name,
    icon = icon,
    description = description,
    enabled = enabled,
    createdAt = createdAt,
    lastModified = lastModified,
    blocks = blocks.map { it.toBlock() }
)

internal fun AutomationBlock.toDto(): BlockDto = BlockDto(
    id = id,
    typeId = type.id,
    label = label,
    icon = icon,
    iconColorArgb = iconColor.value.toLong(),
    parameters = parameters.map { it.toDto() },
    children = children.map { it.toDto() },
    elseChildren = elseChildren.map { it.toDto() }
)

internal fun BlockDto.toBlock(): AutomationBlock = AutomationBlock(
    id = id,
    type = BlockType.fromId(typeId) ?: BlockType.OpenApp,
    label = label,
    icon = icon,
    iconColor = Color(iconColorArgb.toULong()),
    parameters = parameters.map { it.toParameter() },
    children = children.map { it.toBlock() },
    elseChildren = elseChildren.map { it.toBlock() }
)

internal fun BlockParameter.toDto(): ParameterDto = when (this) {
    is BlockParameter.BooleanParam -> ParameterDto(
        kind = "bool", key = key, label = label, boolValue = value
    )
    is BlockParameter.IntParam -> ParameterDto(
        kind = "int", key = key, label = label,
        intValue = value, intMin = min, intMax = max
    )
    is BlockParameter.ChoiceParam -> ParameterDto(
        kind = "choice", key = key, label = label,
        stringValue = value, options = options
    )
    is BlockParameter.StringParam -> ParameterDto(
        kind = "string", key = key, label = label, stringValue = value
    )
}

internal fun ParameterDto.toParameter(): BlockParameter = when (kind) {
    "bool" -> BlockParameter.BooleanParam(key, label, boolValue ?: false)
    "int" -> BlockParameter.IntParam(
        key, label, intValue ?: 0, intMin ?: 0, intMax ?: 100
    )
    "choice" -> BlockParameter.ChoiceParam(key, label, stringValue ?: "", options)
    else -> BlockParameter.StringParam(key, label, stringValue ?: "")
}
