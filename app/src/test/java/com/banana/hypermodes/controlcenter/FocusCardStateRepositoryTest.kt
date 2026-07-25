package com.banana.hypermodes.controlcenter

import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.FullConfig
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusCardStateRepositoryTest {

    @Test
    fun `active mode wins and becomes last mode`() {
        val store = FakeStore(configJson(activeModeId = "focus", lastModeId = "work"))
        val repository = repository(store)

        val snapshot = repository.loadOrInitialize()

        assertEquals(listOf("work", "focus"), snapshot.modes.map { it.id })
        assertEquals("focus", snapshot.displayedMode?.id)
        assertEquals("focus", snapshot.activeModeId)
        assertTrue(snapshot.isActive)
        assertTrue(snapshot.configValid)
        assertEquals(1, store.writes.size)
        assertEquals("focus", ConfigParser.parseConfig(store.json!!).lastModeId)
    }

    @Test
    fun `valid last mode is shown while inactive`() {
        val store = FakeStore(configJson(activeModeId = null, lastModeId = "work"))
        val repository = repository(store)

        val snapshot = repository.loadOrInitialize()

        assertEquals("work", snapshot.displayedMode?.id)
        assertNull(snapshot.activeModeId)
        assertFalse(snapshot.isActive)
        assertTrue(snapshot.configValid)
        assertEquals(0, store.writes.size)
    }

    @Test
    fun `first load selects once and persists selection`() {
        val store = FakeStore(configJson(activeModeId = null, lastModeId = null))
        val repository = repository(store, ModeIndexSelector { size -> size - 1 })

        val first = repository.loadOrInitialize()
        val second = repository.loadOrInitialize()

        assertEquals("focus", first.displayedMode?.id)
        assertEquals(1, store.writes.size)
        assertEquals("focus", ConfigParser.parseConfig(store.json!!).lastModeId)
        assertEquals("focus", second.displayedMode?.id)
        assertEquals(1, store.writes.size)
    }

    @Test
    fun `persisted selection is not rerolled on later loads`() {
        val store = FakeStore(configJson(activeModeId = null, lastModeId = "work"))
        val repository = repository(store, ModeIndexSelector { size -> size - 1 })

        val snapshot = repository.loadOrInitialize()

        assertEquals("work", snapshot.displayedMode?.id)
        assertEquals(0, store.writes.size)
    }

    @Test
    fun `deleted last mode selects a valid replacement`() {
        val store = FakeStore(configJson(activeModeId = null, lastModeId = "deleted"))
        val repository = repository(store, ModeIndexSelector { 1 })

        val snapshot = repository.loadOrInitialize()

        assertEquals("focus", snapshot.displayedMode?.id)
        assertFalse(snapshot.isActive)
        assertTrue(snapshot.configValid)
        assertEquals(1, store.writes.size)
        assertEquals("focus", ConfigParser.parseConfig(store.json!!).lastModeId)
    }

    @Test
    fun `empty mode list produces unavailable snapshot`() {
        val store = FakeStore(ConfigParser.serializeConfig(FullConfig(modes = emptyList())))
        val repository = repository(store)

        val snapshot = repository.loadOrInitialize()

        assertEquals(emptyList<ModeConfig>(), snapshot.modes)
        assertNull(snapshot.displayedMode)
        assertNull(snapshot.activeModeId)
        assertFalse(snapshot.isActive)
        assertTrue(snapshot.configValid)
        assertEquals(0, store.writes.size)
    }

    @Test
    fun `malformed json is not overwritten`() {
        val store = FakeStore("{not json")
        val repository = repository(store)

        val snapshot = repository.loadOrInitialize()
        val activated = repository.activate("work")
        val deactivated = repository.deactivate()

        assertEquals(emptyList<ModeConfig>(), snapshot.modes)
        assertNull(snapshot.displayedMode)
        assertNull(snapshot.activeModeId)
        assertFalse(snapshot.isActive)
        assertFalse(snapshot.configValid)
        assertFalse(activated)
        assertFalse(deactivated)
        assertEquals("{not json", store.json)
        assertEquals(0, store.writes.size)
    }

    @Test
    fun `activate rejects unknown mode`() {
        val store = FakeStore(configJson(activeModeId = null, lastModeId = "work"))
        val repository = repository(store)

        val activated = repository.activate("missing")

        assertFalse(activated)
        assertEquals(0, store.writes.size)
        assertNull(ConfigParser.parseConfig(store.json!!).activeModeId)
        assertEquals("work", ConfigParser.parseConfig(store.json!!).lastModeId)
    }

    @Test
    fun `deactivate preserves history`() {
        val activeJson = ConfigParser.updateActiveModeId(
            configJson(activeModeId = null, lastModeId = "work"),
            "focus"
        )
        val store = FakeStore(activeJson)
        val repository = repository(store)

        val deactivated = repository.deactivate()

        assertTrue(deactivated)
        assertEquals(1, store.writes.size)
        val config = ConfigParser.parseConfig(store.json!!)
        assertNull(config.activeModeId)
        assertEquals("focus", config.lastModeId)
    }

    @Test
    fun `failed activate keeps persisted inactive snapshot authoritative`() {
        val store = FailingWriteStore(configJson(activeModeId = null, lastModeId = "work"))
        val repository = repository(store)

        val activated = repository.activate("focus")
        val snapshot = repository.loadOrInitialize()

        assertFalse(activated)
        assertEquals(1, store.writes.size)
        assertEquals("focus", ConfigParser.parseConfig(store.writes.single()).activeModeId)
        assertNull(ConfigParser.parseConfig(store.json!!).activeModeId)
        assertEquals("work", snapshot.displayedMode?.id)
        assertNull(snapshot.activeModeId)
        assertFalse(snapshot.isActive)
        assertTrue(snapshot.configValid)
    }

    @Test
    fun `failed deactivate keeps persisted active snapshot authoritative`() {
        val activeJson = ConfigParser.updateActiveModeId(
            configJson(activeModeId = null, lastModeId = "work"),
            "focus"
        )
        val store = FailingWriteStore(activeJson)
        val repository = repository(store)

        val deactivated = repository.deactivate()
        val snapshot = repository.loadOrInitialize()

        assertFalse(deactivated)
        assertEquals(1, store.writes.size)
        assertNull(ConfigParser.parseConfig(store.writes.single()).activeModeId)
        assertEquals("focus", snapshot.displayedMode?.id)
        assertEquals("focus", snapshot.activeModeId)
        assertTrue(snapshot.isActive)
        assertTrue(snapshot.configValid)
    }

    @Test
    fun `failed first-use initialization does not fabricate selected snapshot`() {
        val store = FailingWriteStore(configJson(activeModeId = null, lastModeId = null))
        val repository = repository(store, ModeIndexSelector { size -> size - 1 })

        val snapshot = repository.loadOrInitialize()

        assertEquals(listOf("work", "focus"), snapshot.modes.map { it.id })
        assertEquals(1, store.writes.size)
        assertEquals("focus", ConfigParser.parseConfig(store.writes.single()).lastModeId)
        assertNull(ConfigParser.parseConfig(store.json!!).lastModeId)
        assertNull(snapshot.displayedMode)
        assertNull(snapshot.activeModeId)
        assertFalse(snapshot.isActive)
        assertTrue(snapshot.configValid)
    }

    private fun repository(
        store: FocusCardConfigStore,
        selector: ModeIndexSelector = ModeIndexSelector { 0 }
    ): FocusCardStateRepository = FocusCardStateRepository(store, selector)

    private fun configJson(activeModeId: String?, lastModeId: String?): String {
        return ConfigParser.serializeConfig(
            FullConfig(
                activeModeId = activeModeId,
                lastModeId = lastModeId,
                modes = listOf(mode("work", "Work"), mode("focus", "Focus"))
            )
        )
    }

    private fun mode(id: String, name: String): ModeConfig {
        return ModeConfig(
            id = id,
            name = name,
            icon = id,
            type = ModeType.SCHEDULED,
            notification = NotificationConfig(DndLevel.PRIORITY),
            display = DisplayConfig(),
            pausedApps = emptyList()
        )
    }

    private open class FakeStore(var json: String?) : FocusCardConfigStore {
        val writes = mutableListOf<String>()

        override fun read(): String? = json

        override fun write(json: String): Boolean {
            writes += json
            this.json = json
            return true
        }
    }

    private class FailingWriteStore(json: String?) : FakeStore(json) {
        override fun write(json: String): Boolean {
            writes += json
            return false
        }
    }
}
