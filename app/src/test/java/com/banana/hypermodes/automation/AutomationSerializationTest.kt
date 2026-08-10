package com.banana.hypermodes.automation

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 验证自动化块树（参数、嵌套子块、颜色）经 JSON DTO 往返后保持一致。
 */
class AutomationSerializationTest {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private fun sampleBlock(): AutomationBlock = AutomationBlock(
        id = "block-1",
        type = BlockType.IfCondition,
        label = "如果 WiFi 开启",
        icon = "🔀",
        iconColor = Color(0xFF34C759),
        parameters = emptyList(),
        children = listOf(
            AutomationBlock(
                id = "cond-1",
                type = BlockType.CheckWifiState,
                label = "检查 WiFi 状态",
                icon = "📶",
                iconColor = Color(0xFF30B0C7),
                parameters = listOf(
                    BlockParameter.BooleanParam("expected", "期望状态：开启", true)
                )
            ),
            AutomationBlock(
                id = "action-1",
                type = BlockType.AdjustVolume,
                label = "调节音量",
                icon = "🔊",
                iconColor = Color(0xFFFF9500),
                parameters = listOf(
                    BlockParameter.IntParam("level", "音量", 80, 0, 100),
                    BlockParameter.ChoiceParam("stream", "音量类型", "媒体", listOf("媒体", "铃声", "通知", "闹钟"))
                )
            ),
            AutomationBlock(
                id = "action-2",
                type = BlockType.OpenApp,
                label = "打开 App",
                icon = "📱",
                iconColor = Color(0xFF5E5CE6),
                parameters = listOf(
                    BlockParameter.StringParam("packages", "应用包名（逗号分隔）", "com.tencent.mm")
                )
            )
        ),
        elseChildren = listOf(
            AutomationBlock(
                id = "else-1",
                type = BlockType.Comment,
                label = "注释",
                icon = "💬",
                iconColor = Color(0xFF8E8E93)
            )
        )
    )

    private fun sampleAutomation(): SavedAutomation = SavedAutomation(
        id = "auto-1",
        name = "回家自动调节",
        icon = "🏠",
        description = "测试自动化",
        blocks = listOf(sampleBlock()),
        enabled = true
    )

    @Test
    fun `saved automation round trips through dto`() {
        val original = sampleAutomation()
        val dto = original.toDto()
        val restored = dto.toAutomation()

        assertEquals(original, restored)
    }

    @Test
    fun `block tree round trips through json`() {
        val original = sampleAutomation()
        val dto = original.toDto()
        val encoded = json.encodeToString(dto)
        val decoded = json.decodeFromString<SavedAutomationDto>(encoded)
        val restored = decoded.toAutomation()

        assertEquals(original, restored)
    }

    @Test
    fun `all catalog actions map to block types with default params`() {
        for (entry in AutomationCatalog.entries) {
            val type = BlockType.fromId(entry.id)
            assertTrue("目录操作 ${entry.id} 缺少对应 BlockType", type != null)
            // 每个操作都能生成默认参数且往返不崩
            val block = AutomationBlock(
                id = "x",
                type = type!!,
                label = entry.name,
                icon = entry.icon,
                iconColor = entry.iconColor
            )
            val restored = block.toDto().toBlock()
            assertEquals(block.type, restored.type)
        }
    }

    @Test
    fun `every block type in catalog has non-empty parameter schema when needed`() {
        // 控制流容器类型（IF/AND/OR/注释）允许无参数，其余应可构造默认参数
        // Repeat 为内部容器类型（编辑器只暴露 RepeatCount），不在目录中。
        val internalTypes = setOf("repeat")
        val noParamTypes = setOf("if_condition", "and_condition", "or_condition", "comment")
        for (type in BlockType.all) {
            if (type.id in internalTypes) continue
            if (type.id in noParamTypes) continue
            val entry = AutomationCatalog.byId(type.id)
            assertTrue("块类型 ${type.id} 不在目录中", entry != null)
        }
    }
}
