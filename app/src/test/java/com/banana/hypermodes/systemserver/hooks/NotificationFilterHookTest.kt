package com.banana.hypermodes.systemserver.hooks

import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class NotificationFilterHookTest {

    @Test
    fun `no active notification config preserves the OS4 native decision`() {
        assertNull(NotificationFilterHook.notificationMuteOverride(null, "com.example.app"))
    }

    @Test
    fun `whitelisted package preserves the OS4 native decision`() {
        val config = NotificationConfig(
            dndLevel = DndLevel.PRIORITY,
            allowedApps = listOf("com.example.allowed")
        )

        assertNull(
            NotificationFilterHook.notificationMuteOverride(config, "com.example.allowed")
        )
    }

    @Test
    fun `non whitelisted package uses the OS4 intercepted mute reason`() {
        val config = NotificationConfig(
            dndLevel = DndLevel.NONE,
            allowedApps = listOf("com.example.allowed")
        )

        assertEquals(
            NotificationFilterHook.MUTE_REASON_INTERCEPTED,
            NotificationFilterHook.notificationMuteOverride(config, "com.example.blocked")
        )
    }
}
