package com.banana.hypermodes.data

import android.content.Context
import android.provider.Settings
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.FullConfig
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ModeStoreTest {

    private val context: Context = RuntimeEnvironment.getApplication()

    private fun workMode() = Mode(
        id = "custom_work",
        name = "Work",
        icon = "💼",
        description = "",
        settings = ModeSettings()
    )

    private fun storedConfig() = Settings.Global.getString(context.contentResolver, "pixel_routines_full_config")

    @Test
    fun `save overwrites unparseable stored config instead of aborting`() {
        // A stored config that can no longer be parsed (e.g. written by an
        // incompatible future/old schema) must not silently discard saves.
        Settings.Global.putString(context.contentResolver, "pixel_routines_full_config", "{ broken json")

        ModeStore.save(context, listOf(workMode()))

        val modes = ConfigParser.parseConfig(storedConfig()).modes
        assertEquals(1, modes.size)
        assertEquals("custom_work", modes[0].id)
    }

    @Test
    fun `save preserves active mode and dismissals from valid stored config`() {
        val existing = FullConfig(
            activeModeId = "bedtime",
            lastModeId = "bedtime",
            modes = emptyList(),
            dismissedModes = mapOf("custom_old" to 42L)
        )
        Settings.Global.putString(context.contentResolver, "pixel_routines_full_config", ConfigParser.serializeConfig(existing))

        ModeStore.save(context, listOf(workMode()))

        val parsed = ConfigParser.parseConfig(storedConfig())
        assertEquals("bedtime", parsed.activeModeId)
        assertEquals("bedtime", parsed.lastModeId)
        assertEquals(42L, parsed.dismissedModes["custom_old"])
        assertEquals("custom_work", parsed.modes.single().id)
    }
}
