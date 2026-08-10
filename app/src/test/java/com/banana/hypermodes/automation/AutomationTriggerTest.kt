package com.banana.hypermodes.automation

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * 验证触发块的识别与触发评估逻辑。
 */
class AutomationTriggerTest {

    private fun block(type: BlockType, params: List<BlockParameter> = emptyList()) = AutomationBlock(
        id = "b-${type.id}",
        type = type,
        label = type.id,
        icon = "⚡",
        iconColor = Color(0xFFFF9500),
        parameters = params
    )

    @Test
    fun `trigger block types are recognized`() {
        val triggerTypes = listOf(
            BlockType.TriggerTime,
            BlockType.TriggerWifi,
            BlockType.TriggerBluetooth,
            BlockType.TriggerBattery,
            BlockType.TriggerCharging,
            BlockType.TriggerNetwork,
            BlockType.TriggerMusic,
            BlockType.TriggerApp,
            BlockType.TriggerDayOfWeek
        )
        for (type in triggerTypes) {
            assertTrue("${type.id} 应被识别为触发块", block(type).isTriggerBlock())
        }
    }

    @Test
    fun `non trigger blocks are not recognized as triggers`() {
        val nonTrigger = listOf(
            BlockType.ToggleWifiOn,
            BlockType.ToggleWifiOff,
            BlockType.SetDnd,
            BlockType.AdjustVolume,
            BlockType.EnableMode,
            BlockType.OpenApp,
            BlockType.IfCondition,
            BlockType.CheckWifiState,
            BlockType.CheckBatteryLevel
        )
        for (type in nonTrigger) {
            assertFalse("${type.id} 不应被识别为触发块", block(type).isTriggerBlock())
        }
    }

    @Test
    fun `trigger catalog entries map to block types`() {
        val triggerEntries = AutomationCatalog.grouped()[AutomationCatalog.Category.TRIGGER]
            ?: emptyList()
        assertTrue("触发条件目录不应为空", triggerEntries.isNotEmpty())
        for (entry in triggerEntries) {
            val type = BlockType.fromId(entry.id)
            assertTrue("触发操作 ${entry.id} 缺少 BlockType", type != null)
            assertTrue("触发操作 ${entry.id} 应映射为触发块", type!!.let { block(it).isTriggerBlock() })
        }
    }

    @Test
    fun `global automation has no trigger blocks`() {
        val globalBlocks = listOf(
            block(BlockType.ToggleWifiOn),
            block(BlockType.SetDnd, listOf(BlockParameter.ChoiceParam("level", "勿扰级别", "仅优先", listOf("关闭"))))
        )
        assertTrue(globalBlocks.none { it.isTriggerBlock() })
    }

    @Test
    fun `trigger automation has leading trigger block`() {
        val triggered = listOf(
            block(BlockType.TriggerTime, listOf(
                BlockParameter.StringParam("start", "开始时间 (HH:mm)", "22:00"),
                BlockParameter.StringParam("end", "结束时间 (HH:mm)", "07:00")
            )),
            block(BlockType.SetDnd, listOf(BlockParameter.ChoiceParam("level", "勿扰级别", "仅优先", listOf("关闭"))))
        )
        assertEquals(1, triggered.count { it.isTriggerBlock() })
        assertTrue(triggered.first().isTriggerBlock())
    }
}
