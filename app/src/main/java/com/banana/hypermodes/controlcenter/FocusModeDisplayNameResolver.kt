package com.banana.hypermodes.controlcenter

import android.content.res.Resources
import com.banana.hypermodes.systemserver.config.ModeConfig

internal class FocusModeDisplayNameResolver(
    private val resources: Resources,
    private val packageName: String
) {
    fun resolve(mode: ModeConfig): String {
        if (mode.name.isBlank()) return "Focus mode"
        val mapping = builtInMappings[mode.id] ?: return mode.name
        return if (mode.name in mapping.defaultNames) {
            resolveStringByName(mapping.resourceEntryName) ?: mode.name
        } else {
            mode.name
        }
    }

    private fun resolveStringByName(entryName: String): String? {
        return runCatching {
            val resId = resources.getIdentifier(entryName, "string", packageName)
            if (resId != 0) resources.getString(resId) else null
        }.getOrNull()
    }

    private data class BuiltInModeNameMapping(
        val resourceEntryName: String,
        val defaultNames: Set<String>
    )

    private companion object {
        val builtInMappings = mapOf(
            "dnd" to BuiltInModeNameMapping(
                resourceEntryName = "mode_dnd",
                defaultNames = setOf("Do Not Disturb", "勿扰", "勿扰模式")
            ),
            "bedtime" to BuiltInModeNameMapping(
                resourceEntryName = "mode_bedtime",
                defaultNames = setOf("Bedtime", "睡眠", "睡眠模式")
            ),
            "driving" to BuiltInModeNameMapping(
                resourceEntryName = "mode_driving",
                defaultNames = setOf("Driving", "驾驶", "驾驶模式")
            )
        )
    }
}
