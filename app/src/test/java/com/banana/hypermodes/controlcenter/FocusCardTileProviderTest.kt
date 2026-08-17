package com.banana.hypermodes.controlcenter

import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.FullConfig
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusCardTileProviderTest {

    @Test
    fun `active snapshot maps to active boolean state`() {
        val fixture = fixture(configJson(activeModeId = "focus", lastModeId = "work"))
        val tile = fixture.createTile()

        val state = tile.getState()

        assertEquals("hypermodes_focus", state.spec)
        assertEquals("Focus", state.label)
        assertEquals(2, state.state)
        assertTrue(state.value)
        assertFalse(state.dualTarget)
        assertTrue(state.handlesLongClick)
        assertFalse(state.handlesSecondaryClick)
        assertNotNull(state.contentDescription)
        assertNotNull(state.icon)
        assertTrue(tile.isAvailable())
    }

    @Test
    fun `remembered inactive snapshot maps to inactive boolean state`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()

        val state = tile.getState()

        assertEquals("Work", state.label)
        assertEquals(1, state.state)
        assertFalse(state.value)
        assertTrue(tile.isAvailable())
    }

    @Test
    fun `empty mode list maps to unavailable state`() {
        val fixture = fixture(ConfigParser.serializeConfig(FullConfig(modes = emptyList())))
        val tile = fixture.createTile()

        val state = tile.getState()

        assertEquals("hypermodes_focus", state.spec)
        assertEquals(0, state.state)
        assertFalse(state.value)
        assertFalse(tile.isAvailable())
    }

    @Test
    fun `callback receives state after refresh`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        tile.refreshState()

        assertEquals(1, callback.states.size)
        assertEquals("Work", callback.states.single().label)
    }

    @Test
    fun `multiple listener tokens share one observer and close after final token stops`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val tokenA = Any()
        val tokenB = Any()

        tile.setListening(tokenA, true)
        tile.setListening(tokenA, true)
        tile.setListening(tokenB, true)

        assertTrue(tile.isListening())
        assertEquals(1, fixture.store.observeCalls)
        assertEquals(0, fixture.store.closeCalls)

        tile.setListening(tokenA, false)

        assertTrue(tile.isListening())
        assertEquals(1, fixture.store.observeCalls)
        assertEquals(0, fixture.store.closeCalls)

        tile.setListening(tokenB, false)

        assertFalse(tile.isListening())
        assertEquals(1, fixture.store.observeCalls)
        assertEquals(1, fixture.store.closeCalls)
    }

    @Test
    fun `store observer refreshes callbacks`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)
        tile.setListening(Any(), true)

        // Clear the initial refresh from setListening
        callback.states.clear()

        fixture.store.fireChange()

        assertEquals(1, callback.states.size)
        assertEquals("Work", callback.states.single().label)
    }

    @Test
    fun `click deactivates active mode`() {
        val fixture = fixture(configJson(activeModeId = "focus", lastModeId = "work"))
        val tile = fixture.createTile()

        tile.click()

        val config = ConfigParser.parseConfig(fixture.store.json!!)
        assertNull(config.activeModeId)
        assertEquals("focus", config.lastModeId)
        assertFalse(tile.getState().value)
    }

    @Test
    fun `click activates inactive displayed mode`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()

        tile.click()

        val config = ConfigParser.parseConfig(fixture.store.json!!)
        assertEquals("work", config.activeModeId)
        assertEquals("work", config.lastModeId)
        assertTrue(tile.getState().value)
    }

    @Test
    fun `long click shows detail immediately when already on main thread`() {
        val queuedUiTasks = mutableListOf<() -> Unit>()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { _, _ -> FakeDetailAdapter },
            postToUi = { task -> queuedUiTasks += task },
            isMainThread = { true }
        )
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        tile.longClick()

        assertEquals(listOf(true), callback.detailEvents)
        assertEquals(0, queuedUiTasks.size)
    }

    @Test
    fun `long click posts detail notify when off main thread`() {
        val queuedUiTasks = mutableListOf<() -> Unit>()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { _, _ -> FakeDetailAdapter },
            postToUi = { task -> queuedUiTasks += task },
            isMainThread = { false }
        )
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        tile.longClick()

        assertEquals(emptyList<Boolean>(), callback.detailEvents)
        assertEquals(1, queuedUiTasks.size)

        queuedUiTasks.single().invoke()

        assertEquals(listOf(true), callback.detailEvents)
    }

    @Test
    fun `long click does not show detail without adapter`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        tile.longClick()

        assertEquals(emptyList<Boolean>(), callback.detailEvents)
    }

    @Test
    fun `detail dismiss closes through same callback identity used to open`() {
        lateinit var dismiss: () -> Unit
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { onDismiss, _ ->
                dismiss = onDismiss
                FakeDetailAdapter
            }
        )
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        tile.longClick()
        dismiss()

        assertEquals(listOf(true, false), callback.detailEvents)
        assertEquals(1, callback.identityHashes.distinct().size)
    }

    @Test
    fun `production detail factory path refreshes state before closing through stable callback identity`() {
        lateinit var dismiss: () -> Unit
        lateinit var refresh: () -> Unit
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { onDismiss, onStateRefresh ->
                dismiss = onDismiss
                refresh = onStateRefresh
                FakeDetailAdapter
            }
        )
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        tile.longClick()
        refresh()
        dismiss()

        assertEquals(listOf("detail:true", "state:Work", "detail:false"), callback.events)
        assertEquals(1, callback.identityHashes.distinct().size)
    }

    @Test
    fun `detail dismiss is safe after callback is removed`() {
        lateinit var dismiss: () -> Unit
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { onDismiss, _ ->
                dismiss = onDismiss
                FakeDetailAdapter
            }
        )
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)
        tile.longClick()
        tile.removeCallback(callback)

        dismiss()

        assertEquals(listOf(true), callback.detailEvents)
    }

    @Test
    fun `secondary clicks do not mutate config or show detail`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)
        val before = fixture.store.json

        tile.secondaryClick()
        tile.secondClick()

        assertEquals(before, fixture.store.json)
        assertEquals(emptyList<Boolean>(), callback.detailEvents)
    }

    @Test
    fun `missing resources still produce non null tile icon`() {
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            pluginContext = MissingResourceContext(),
            moduleContext = MissingResourceContext()
        )
        val tile = fixture.createTile()

        val state = tile.getState()

        assertNotNull(state.icon)
        val icon = state.icon as FakeDrawableIcon
        assertNotNull(icon.drawable)
    }

    @Test
    fun `destroy clears callbacks and detail so dismiss and long click stop working`() {
        lateinit var dismiss: () -> Unit
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { onDismiss, _ ->
                dismiss = onDismiss
                FakeDetailAdapter
            }
        )
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)
        tile.longClick()

        tile.destroy()
        dismiss()
        tile.longClick()
        tile.showDetail(true)

        assertEquals(listOf(true), callback.detailEvents)
    }

    @Test
    fun `destroy releases real detail session content and adapter registrations`() {
        lateinit var session: FocusModeDetailSession
        lateinit var content: Any
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { _, _ ->
                session = FocusModeDetailSession(
                    repository = fixtureRepository(configJson(activeModeId = null, lastModeId = "work")),
                    onDismiss = {},
                    nativeDetailContentApi = createFakeNativeApi(),
                    diagnostic = FakeDiagnostic(),
                    detailAdapterInterface = FakeDetailAdapterInterface::class.java
                )
                FocusNativeDetailRegistry.registerSession(session.adapter, session)
                content = session.bindDetailView(android.app.Application(), null, null)
                session.adapter
            }
        )
        val tile = fixture.createTile()

        tile.longClick()
        assertTrue(FocusNativeDetailRegistry.isFocusAdapter(session.adapter))
        assertTrue(FocusNativeDetailRegistry.isFocusContent(content))

        tile.destroy()

        assertFalse(FocusNativeDetailRegistry.isFocusAdapter(session.adapter))
        assertFalse(FocusNativeDetailRegistry.isFocusContent(content))
    }

    @Test
    fun `destroy closes observer clears callbacks and marks destroyed`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)
        tile.setListening(Any(), true)

        tile.destroy()
        tile.refreshState()

        assertTrue(tile.isDestroyed())
        assertFalse(tile.isListening())
        assertEquals(1, fixture.store.closeCalls)
        // After destroy, refreshState should not notify callbacks
        assertEquals(1, callback.states.size) // Only the initial state from setListening
    }

    @Test
    fun `primitive returning methods never return null`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()

        assertTrue(tile.isAvailable())
        assertFalse(tile.isListening())
        assertFalse(tile.isDestroyed())
        assertTrue(tile.isTileReady())
        assertFalse(tile.isConnected())
        assertEquals(0, tile.getCurrentTileUser())
        assertEquals(FocusNativeDetailRegistry.METRICS_CATEGORY, tile.getMetricsCategory())
        assertEquals(0L, tile.unknownLong())
        assertEquals(0.toByte(), tile.unknownByte())
        assertEquals(0.toShort(), tile.unknownShort())
        assertEquals(0f, tile.unknownFloat())
        assertEquals(0.0, tile.unknownDouble(), 0.0)
        assertEquals(' ', tile.unknownChar())
    }

    @Test
    fun `OS4 user switch updates current tile user`() {
        val tile = fixture(configJson(activeModeId = null, lastModeId = "work")).createTile()

        tile.userSwitch(12)

        assertEquals(12, tile.getCurrentTileUser())
    }

    @Test
    fun `OS4 QSTile v5 interface defaults remain callable`() {
        val tile = fixture(configJson(activeModeId = null, lastModeId = "work")).createTile()
        assertEquals("v5-default", tile.os4DefaultBehavior())
    }

    @Test
    fun `populate returns the same log maker instance`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val logMaker = FakeLogMaker()

        val returned = tile.populate(logMaker)

        assertSame(logMaker, returned)
    }

    @Test
    fun `equals hashCode and toString behave safely`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val tile = fixture.createTile()
        val same = tile
        val other = fixture.createTile()

        assertTrue(tile == same)
        assertFalse(tile == other)
        assertFalse(tile.equals("not a tile"))
        assertEquals(System.identityHashCode(tile), tile.hashCode())
        assertNotEquals("", tile.toString())
    }

    private fun fixture(
        json: String,
        detailFactory: FocusCardDetailFactory? = null,
        pluginContext: Context = TestContext(),
        moduleContext: Context = TestContext(),
        postToUi: ((() -> Unit) -> Unit) = { task -> task() },
        isMainThread: () -> Boolean = { true }
    ): Fixture {
        val store = RecordingObservableStore(json)
        val repository = FocusCardStateRepository(store, ModeIndexSelector { 0 })
        val classes = FocusCardTileClasses(
            tileInterface = FakeTile::class.java,
            booleanStateClass = FakeBooleanState::class.java,
            drawableIconClass = FakeDrawableIcon::class.java,
            detailAdapterInterface = FakeDetailAdapterInterface::class.java,
            nativeDetailContentApi = createFakeNativeApi()
        )
        return Fixture(
            provider = FocusCardTileProvider(
                pluginContext = pluginContext,
                moduleContext = moduleContext,
                classes = classes,
                repository = repository,
                observableStore = store,
                detailFactory = detailFactory,
                postToUi = postToUi,
                isMainThread = isMainThread
            ),
            store = store
        )
    }

    private fun configJson(activeModeId: String?, lastModeId: String?): String {
        return ConfigParser.serializeConfig(
            FullConfig(
                activeModeId = activeModeId,
                lastModeId = lastModeId,
                modes = listOf(mode("work", "Work"), mode("focus", "Focus"))
            )
        )
    }

    private fun fixtureRepository(json: String): FocusCardStateRepository {
        return FocusCardStateRepository(RecordingObservableStore(json), ModeIndexSelector { 0 })
    }

    private fun createFakeNativeApi(): FocusNativeDetailContentApi {
        val contentClass = ProviderNativeDetailContent::class.java
        val itemInterface = ProviderNativeDetailContent.Item::class.java
        val selectableItemClass = ProviderNativeDetailContent.SelectableItem::class.java
        val callbackInterface = ProviderNativeDetailContent.Callback::class.java
        val convertMethod = ProviderNativeDetailContent::class.java.getDeclaredMethod(
            "fakeConvert",
            android.content.Context::class.java,
            android.view.View::class.java,
            android.view.ViewGroup::class.java
        )
        return FocusNativeDetailContentApi(
            contentClass = contentClass,
            itemInterface = itemInterface,
            selectableItemClass = selectableItemClass,
            callbackInterface = callbackInterface,
            convertOrInflate = FocusNativeConvertOrInflate(ownerProvider = { null }, method = convertMethod),
            selectableItemConstructor = selectableItemClass.getDeclaredConstructor(),
            setSuffix = contentClass.getDeclaredMethod("setSuffix", String::class.java),
            setItems = contentClass.getDeclaredMethod("setItems", java.lang.reflect.Array.newInstance(itemInterface, 0).javaClass),
            setCallback = contentClass.getDeclaredMethod("setCallback", callbackInterface)
        )
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

    private data class Fixture(
        val provider: FocusCardTileProvider,
        val store: RecordingObservableStore
    ) {
        fun createTile(): FakeTile = provider.create() as FakeTile
    }

    private class RecordingObservableStore(var json: String?) : ObservableFocusCardConfigStore {
        val writes = mutableListOf<String>()
        var observeCalls = 0
        var closeCalls = 0
        private var observer: (() -> Unit)? = null

        override fun read(): String? = json

        override fun write(json: String): Boolean {
            writes += json
            this.json = json
            return true
        }

        override fun observe(onChanged: () -> Unit): AutoCloseable {
            observeCalls += 1
            observer = onChanged
            return AutoCloseable {
                closeCalls += 1
                if (observer === onChanged) observer = null
            }
        }

        fun fireChange() {
            observer?.invoke()
        }
    }

    private open class TestContext : ContextWrapper(null) {
        override fun getApplicationContext(): Context = this
    }

    private class MissingResourceContext : ContextWrapper(null) {
        override fun getApplicationContext(): Context = this
        override fun getPackageName(): String = "missing.resources"
    }

    private interface FakeCallback {
        fun onStateChanged(state: FakeBooleanState)
        fun onShowDetail(show: Boolean)
    }

    private class RecordingCallback : FakeCallback {
        val states = mutableListOf<FakeBooleanState>()
        val detailEvents = mutableListOf<Boolean>()
        val identityHashes = mutableListOf<Int>()
        val events = mutableListOf<String>()

        override fun onStateChanged(state: FakeBooleanState) {
            states += state
            events += "state:${state.label}"
        }

        override fun onShowDetail(show: Boolean) {
            identityHashes += System.identityHashCode(this)
            detailEvents += show
            events += "detail:$show"
        }
    }

    private class FakeBooleanState {
        @JvmField var spec: String? = null
        @JvmField var label: CharSequence? = null
        @JvmField var contentDescription: CharSequence? = null
        @JvmField var icon: Any? = null
        @JvmField var state: Int = 0
        @JvmField var value: Boolean = false
        @JvmField var dualTarget: Boolean = true
        @JvmField var handlesLongClick: Boolean = false
        @JvmField var handlesSecondaryClick: Boolean = true
    }

    private interface FakeTile {
        fun addCallback(callback: FakeCallback)
        fun removeCallback(callback: FakeCallback)
        fun removeCallbacks()
        fun removeCallbacksByType(type: Int)
        fun getState(): FakeBooleanState
        fun getTileSpec(): String
        fun setTileSpec(spec: String)
        fun getTileLabel(): CharSequence
        fun isAvailable(): Boolean
        fun isListening(): Boolean
        fun isDestroyed(): Boolean
        fun isTileReady(): Boolean
        fun isConnected(): Boolean
        fun getCurrentTileUser(): Int
        fun getMetricsCategory(): Int
        fun getMetricsSpec(): String?
        fun getInstanceId(): Any?
        fun setListening(token: Any, listening: Boolean)
        fun click()
        fun click(view: Any?)
        fun longClick()
        fun longClick(view: Any?)
        fun secondaryClick()
        fun secondaryClick(view: Any?)
        fun secondClick()
        fun secondClick(view: Any?)
        fun refreshState()
        fun destroy()
        fun getDetailAdapter(): FakeDetailAdapterInterface?
        fun setDetailListening(show: Boolean)
        fun showDetail(show: Boolean)
        fun userSwitch(user: Int)
        fun populate(logMaker: FakeLogMaker): FakeLogMaker
        fun unknownLong(): Long
        fun unknownByte(): Byte
        fun unknownShort(): Short
        fun unknownFloat(): Float
        fun unknownDouble(): Double
        fun unknownChar(): Char
        fun os4DefaultBehavior(): String = "v5-default"
    }

    private interface FakeDetailAdapterInterface

    private class ProviderNativeDetailContent : android.view.View(null) {
        fun setSuffix(suffix: String) = Unit
        fun setItems(vararg items: Item) = Unit
        fun setCallback(callback: Callback) = Unit

        interface Item {
            fun getType(): Int = 0
        }

        class SelectableItem : Item {
            @JvmField var tag: Any? = null
            @JvmField var title: CharSequence? = null
            @JvmField var selected: Boolean = false
            @JvmField var isForceSingle: Boolean = false
            @JvmField var selectable: Boolean = false
            @JvmField var iconDrawable: Drawable? = null
        }

        interface Callback {
            fun onDetailItemClick(item: Item)
            fun onDetailItemDisconnect(item: Item) = Unit
        }

        companion object {
            @JvmStatic
            fun fakeConvert(
                context: android.content.Context?,
                convertView: android.view.View?,
                parent: android.view.ViewGroup?
            ): ProviderNativeDetailContent {
                return ProviderNativeDetailContent()
            }
        }
    }

    private object FakeDetailAdapter : FakeDetailAdapterInterface

    private class FakeLogMaker

    private class FakeDrawableIcon(val drawable: Drawable) {
        init {
            require(drawable !is NullDrawable)
        }
    }

    private class NullDrawable : ColorDrawable()

    @Test
    fun `observer active when detail OPEN`() {
        val store = RecordingObservableStore(configJson(activeModeId = null, lastModeId = "work"))
        val repository = FocusCardStateRepository(store, ModeIndexSelector { 0 })
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { _, _ ->
                val adapter = FocusModeDetailAdapter(
                    pluginContext = TestContext(),
                    moduleContext = TestContext(),
                    detailAdapterInterface = FakeDetailAdapterInterface::class.java,
                    repository = repository,
                    onDismiss = {},
                    onStateRefresh = {},
                    nativeDetailContentApi = null
                )
                adapter.adapter
            }
        )
        val tile = fixture.createTile()

        val detailAdapter = tile.getDetailAdapter()
        assertNotNull(detailAdapter)

        tile.setDetailListening(true)

        assertTrue(fixture.store.observeCalls > 0)
    }

    @Test
    fun `config change refreshes items when OPEN`() {
        lateinit var fixtureStore: RecordingObservableStore
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { _, _ ->
                val adapter = FocusModeDetailAdapter(
                    pluginContext = TestContext(),
                    moduleContext = TestContext(),
                    detailAdapterInterface = FakeDetailAdapterInterface::class.java,
                    repository = FocusCardStateRepository(fixtureStore, ModeIndexSelector { 0 }),
                    onDismiss = {},
                    onStateRefresh = {},
                    nativeDetailContentApi = null
                )
                adapter.adapter
            }
        )
        fixtureStore = fixture.store
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        val detailAdapter = tile.getDetailAdapter()
        tile.setDetailListening(true)

        // Verify detail is OPEN
        val session = FocusNativeDetailRegistry.adapterSession(detailAdapter!!)
        assertEquals(DetailLifecycleState.OPEN, session?.state)

        // Verify observer was installed
        assertTrue("Observer should be installed when detail is OPEN", fixture.store.observeCalls > 0)

        val statesBefore = callback.states.size

        fixture.store.fireChange()

        // Verify refreshState was called (callbacks notified)
        assertTrue(callback.states.size > statesBefore)
    }

    @Test
    fun `config change during CLOSING defers card refresh`() {
        lateinit var fixtureStore: RecordingObservableStore
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            detailFactory = FocusCardDetailFactory { _, _ ->
                val adapter = FocusModeDetailAdapter(
                    pluginContext = TestContext(),
                    moduleContext = TestContext(),
                    detailAdapterInterface = FakeDetailAdapterInterface::class.java,
                    repository = FocusCardStateRepository(fixtureStore, ModeIndexSelector { 0 }),
                    onDismiss = {},
                    onStateRefresh = {},
                    nativeDetailContentApi = null
                )
                adapter.adapter
            }
        )
        fixtureStore = fixture.store
        val tile = fixture.createTile()
        val callback = RecordingCallback()
        tile.addCallback(callback)

        val detailAdapter = tile.getDetailAdapter()
        tile.setDetailListening(true)

        val session = FocusNativeDetailRegistry.adapterSession(detailAdapter!!)
        assertNotNull(session)
        assertEquals(DetailLifecycleState.OPEN, session?.state)

        // Trigger CLOSING state
        tile.setDetailListening(false)
        assertEquals(DetailLifecycleState.CLOSING, session?.state)

        // Verify pending refresh was set by setDetailListening
        assertTrue("Pending card refresh should be set when entering CLOSING", session?.hasPendingCardRefresh() == true)

        val statesBefore = callback.states.size

        fixture.store.fireChange()

        // Verify no immediate refreshState call
        assertEquals("No new states should be notified during CLOSING", statesBefore, callback.states.size)

        // Verify pending refresh flag is still set
        assertTrue("Pending card refresh should remain set during CLOSING after config change", session?.hasPendingCardRefresh() == true)
    }
}
