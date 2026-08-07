package com.banana.hypermodes.automation

import java.util.UUID

/**
 * 已保存的自动化任务
 */
data class SavedAutomation(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val icon: String = "🤖",
    val description: String = "",
    val blocks: List<AutomationBlock>,
    val enabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis()
)
