package com.banana.hypermodes.controlcenter

import android.content.res.Resources
import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Test

class FocusModeDisplayNameResolverTest {

    @Test
    fun `dnd English default resolves to supplied localized dnd string`() {
        val resolver = resolver("mode_dnd" to "Localized DND")

        val resolved = resolver.resolve(mode(id = "dnd", name = "Do Not Disturb"))

        assertEquals("Localized DND", resolved)
    }

    @Test
    fun `dnd Chinese default resolves to supplied localized dnd string`() {
        val resolver = resolver("mode_dnd" to "Localized DND")

        val resolved = resolver.resolve(mode(id = "dnd", name = "勿扰"))

        assertEquals("Localized DND", resolved)
    }

    @Test
    fun `dnd legacy Chinese default resolves to supplied localized dnd string`() {
        val resolver = resolver("mode_dnd" to "Localized DND")

        val resolved = resolver.resolve(mode(id = "dnd", name = "勿扰模式"))

        assertEquals("Localized DND", resolved)
    }

    @Test
    fun `bedtime and driving default language variants resolve to supplied localized strings`() {
        val resolver = resolver(
            "mode_bedtime" to "Localized Bedtime",
            "mode_driving" to "Localized Driving"
        )

        val resolved = listOf(
            resolver.resolve(mode(id = "bedtime", name = "Bedtime")),
            resolver.resolve(mode(id = "bedtime", name = "睡眠")),
            resolver.resolve(mode(id = "driving", name = "Driving")),
            resolver.resolve(mode(id = "driving", name = "驾驶"))
        )

        assertEquals(
            listOf(
                "Localized Bedtime",
                "Localized Bedtime",
                "Localized Driving",
                "Localized Driving"
            ),
            resolved
        )
    }

    @Test
    fun `legacy Chinese defaults resolve to supplied localized strings`() {
        val resolver = resolver(
            "mode_bedtime" to "Localized Bedtime",
            "mode_driving" to "Localized Driving"
        )

        val resolved = listOf(
            resolver.resolve(mode(id = "bedtime", name = "睡眠模式")),
            resolver.resolve(mode(id = "driving", name = "驾驶模式"))
        )

        assertEquals(
            listOf(
                "Localized Bedtime",
                "Localized Driving"
            ),
            resolved
        )
    }

    @Test
    fun `built in user rename is preserved exactly`() {
        val resolver = resolver("mode_dnd" to "Localized DND")

        val resolved = resolver.resolve(mode(id = "dnd", name = "Do Not Disturb except family"))

        assertEquals("Do Not Disturb except family", resolved)
    }

    @Test
    fun `custom mode name is preserved exactly`() {
        val resolver = resolver("mode_dnd" to "Localized DND")

        val resolved = resolver.resolve(mode(id = "custom-id", name = "Deep Work"))

        assertEquals("Deep Work", resolved)
    }

    @Test
    fun `blank custom name returns Focus mode`() {
        val resolver = resolver("mode_dnd" to "Localized DND")

        val resolved = resolver.resolve(mode(id = "custom-id", name = "   "))

        assertEquals("Focus mode", resolved)
    }

    @Test
    fun `missing resource entry name returns mode name unchanged`() {
        val resolver = resolver() // No mappings

        val resolved = resolver.resolve(mode(id = "dnd", name = "Do Not Disturb"))

        assertEquals("Do Not Disturb", resolved)
    }

    private fun resolver(vararg strings: Pair<String, String>): FocusModeDisplayNameResolver {
        val values = strings.toMap()
        val mockResources = object : Resources(null, null, null) {
            override fun getIdentifier(name: String, defType: String?, defPackage: String?): Int {
                return if (values.containsKey(name)) name.hashCode() else 0
            }
            override fun getString(id: Int): String {
                return values.entries.find { it.key.hashCode() == id }?.value
                    ?: throw Resources.NotFoundException("string/$id")
            }
        }
        return FocusModeDisplayNameResolver(mockResources, "com.banana.hypermodes")
    }

    private fun mode(id: String, name: String): ModeConfig {
        return ModeConfig(
            id = id,
            name = name,
            icon = "🧘",
            type = ModeType.SCHEDULED,
            notification = NotificationConfig(DndLevel.PRIORITY),
            display = DisplayConfig(),
            pausedApps = emptyList()
        )
    }
}
