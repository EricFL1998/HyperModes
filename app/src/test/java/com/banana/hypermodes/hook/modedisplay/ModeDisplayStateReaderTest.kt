package com.banana.hypermodes.hook.modedisplay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModeDisplayStateReaderTest {

    @Test
    fun `active mode becomes display state and explicit status icon wins`() {
        val state = ModeDisplayStateReader.fromJson(configJson(
            activeModeId = "work",
            statusIcon = "ic_stat_star"
        ))

        assertEquals(ModeDisplayState("Work", "ic_stat_star"), state)
    }

    @Test
    fun `blank status icon falls back to mapped mode icon`() {
        val state = ModeDisplayStateReader.fromJson(configJson(
            activeModeId = "work",
            statusIcon = "   "
        ))

        assertEquals(ModeDisplayState("Work", "ic_stat_work"), state)
    }

    @Test
    fun `missing active mode returns null`() {
        assertNull(ModeDisplayStateReader.fromJson(configJson(
            activeModeId = "missing",
            statusIcon = "ic_stat_work"
        )))
    }

    @Test
    fun `blank and malformed configs return null`() {
        assertNull(ModeDisplayStateReader.fromJson(null))
        assertNull(ModeDisplayStateReader.fromJson("   "))
        assertNull(ModeDisplayStateReader.fromJson("{not-json"))
    }

    private fun configJson(activeModeId: String, statusIcon: String): String = """
        {
          "activeModeId": "$activeModeId",
          "modes": [
            {
              "id": "work",
              "name": "Work",
              "icon": "💼",
              "statusIcon": "$statusIcon",
              "type": "SCHEDULED",
              "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
              "display": {},
              "pausedApps": []
            }
          ]
        }
    """.trimIndent()
}
