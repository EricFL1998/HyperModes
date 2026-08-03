package com.banana.hypermodes.data

import kotlinx.serialization.Serializable

/**
 * Data class for importing app intent configurations from JSON files.
 * Format matches the exported intent configuration from intent capture tools.
 */
@Serializable
data class IntentConfig(
    val appName: String,
    val packageName: String,
    val intents: List<IntentAction>
)

@Serializable
data class IntentAction(
    val name: String,
    val intents: List<String>
)
