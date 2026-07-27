package com.banana.hypermodes.controlcenter

import com.banana.hypermodes.R
import com.banana.hypermodes.systemserver.config.ModeConfig

internal class FocusModeDisplayNameResolver(
    private val stringProvider: (Int) -> String
) {
    fun resolve(mode: ModeConfig): String {
        if (mode.name.isBlank()) return "Focus mode"
        val mapping = builtInMappings[mode.id] ?: return mode.name
        return if (mode.name in mapping.defaultNames) {
            stringProvider(mapping.stringResId)
        } else {
            mode.name
        }
    }

    private data class BuiltInModeNameMapping(
        val stringResId: Int,
        val defaultNames: Set<String>
    )

    private companion object {
        val builtInMappings = mapOf(
            "dnd" to BuiltInModeNameMapping(
                stringResId = R.string.mode_dnd,
                defaultNames = setOf("Do Not Disturb", "勿扰")
            ),
            "bedtime" to BuiltInModeNameMapping(
                stringResId = R.string.mode_bedtime,
                defaultNames = setOf("Bedtime", "睡眠")
            ),
            "driving" to BuiltInModeNameMapping(
                stringResId = R.string.mode_driving,
                defaultNames = setOf("Driving", "驾驶")
            )
        )
    }
}
