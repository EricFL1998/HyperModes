package com.banana.hypermodes.controlcenter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import com.banana.hypermodes.R
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.DisplayConfig
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.FullConfig
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType
import com.banana.hypermodes.systemserver.config.NotificationConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusModeDetailAdapterTest {

    @Test
    fun `adapter uses session for detail view`() {
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java)
        val repository = FocusCardStateRepository(
            RecordingStore(configJson(activeModeId = null, lastModeId = "work")),
            ModeIndexSelector { 0 }
        )
        val adapter = FocusModeDetailAdapter(
            pluginContext = TestContext("plugin", "Focus modes", "No modes configured", "Open HyperModes", "On", "Off"),
            moduleContext = TestContext("module", "Module title", "Empty", "Open", "Active", "Inactive"),
            detailAdapterInterface = FakeDetailAdapter::class.java,
            repository = repository,
            onDismiss = {},
            onStateRefresh = {},
            nativeDetailContentApi = nativeApi
        )

        val session = adapter.session
        assertNotNull(session)
        assertTrue(FocusNativeDetailRegistry.isFocusAdapter(session.adapter))
    }

    @Test
    fun `adapter delegates setDetailListening to session`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val adapter = fixture.createAdapter()

        adapter.setDetailListening(true)
        assertEquals(DetailLifecycleState.OPEN, adapter.session.state)

        adapter.setDetailListening(false)
        assertEquals(DetailLifecycleState.CLOSING, adapter.session.state)
    }

    @Test
    fun `adapter defers state refresh until native panel hidden completes`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val adapter = fixture.createAdapter()

        adapter.setDetailListening(true)
        adapter.setDetailListening(false)

        assertEquals(0, fixture.refreshCalls)
        assertTrue(adapter.session.hasPendingCardRefresh())

        adapter.onPanelHidden()

        assertEquals(1, fixture.refreshCalls)
        assertFalse(adapter.session.hasPendingCardRefresh())
        assertEquals(DetailLifecycleState.CLOSED, adapter.session.state)
    }

    @Test
    fun `native adapter omits settings intent`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val adapter = fixture.createAdapter()

        val settingsIntent = (adapter.adapter as FakeDetailAdapter).getSettingsIntent()

        assertNull(settingsIntent)
    }

    @Test
    fun `native rows omit visual selection while preserving row content`() {
        FakeNativeDetailContent.reset()
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(
            FakeNativeDetailContent::class.java
        )!!
        val fixture = fixture(
            json = configJson(activeModeId = "work", lastModeId = "work"),
            nativeApiOverride = nativeApi
        )
        val activeIcon = ColorDrawable(0x10)
        val inactiveIcon = ColorDrawable(0x20)
        val adapter = fixture.createAdapter(
            modeIconProvider = { mode ->
                when (mode.id) {
                    "work" -> activeIcon
                    "focus" -> inactiveIcon
                    else -> error("unexpected mode ${mode.id}")
                }
            }
        )
        val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")

        val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent
        val rows = content.itemValues.map { it as FakeNativeDetailContent.SelectableItem }

        assertEquals(listOf("Work", "Focus"), rows.map { it.title })
        assertEquals(listOf("work", "focus"), rows.map { it.tag })
        assertSame(activeIcon, rows[0].iconDrawable)
        assertSame(inactiveIcon, rows[1].iconDrawable)
        assertTrue(rows.all { it.summary == null })
        assertTrue(rows.all { it.secondarySummary == null })
        assertTrue(rows.all { !it.selected })
        assertTrue(rows.all { !it.isForceSingle() })
    }

    @Test
    fun `native rows receive drawable icons from mode provider`() {
        FakeNativeDetailContent.reset()
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(
            FakeNativeDetailContent::class.java
        )!!
        val workDrawable = ColorDrawable(0x10)
        val focusDrawable = ColorDrawable(0x20)
        val seenModes = mutableListOf<ModeConfig>()
        val fixture = fixture(
            json = configJson(activeModeId = "work", lastModeId = "work"),
            nativeApiOverride = nativeApi
        )
        val adapter = fixture.createAdapter(
            modeIconProvider = { mode ->
                seenModes += mode
                when (mode.id) {
                    "work" -> workDrawable
                    "focus" -> focusDrawable
                    else -> error("unexpected mode ${mode.id}")
                }
            }
        )
        val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")

        val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent
        val rows = content.itemValues.map { it as FakeNativeDetailContent.SelectableItem }

        assertEquals(listOf("work", "focus"), seenModes.map { it.id })
        assertSame(workDrawable, rows[0].iconDrawable)
        assertSame(focusDrawable, rows[1].iconDrawable)
    }

    @Test
    fun `native rows use injected display names while preserving native row behavior`() {
        FakeNativeDetailContent.reset()
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(
            FakeNativeDetailContent::class.java
        )!!
        val workDrawable = ColorDrawable(0x10)
        val focusDrawable = ColorDrawable(0x20)
        val displayedModes = mutableListOf<ModeConfig>()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeApiOverride = nativeApi
        )
        val adapter = fixture.createAdapter(
            modeIconProvider = { mode ->
                when (mode.id) {
                    "work" -> workDrawable
                    "focus" -> focusDrawable
                    else -> error("unexpected icon mode ${mode.id}")
                }
            },
            modeDisplayNameProvider = { mode ->
                displayedModes += mode
                "Display ${mode.id}"
            }
        )
        val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")

        val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent
        val rows = content.itemValues.map { it as FakeNativeDetailContent.SelectableItem }
        content.callbackValue!!.onDetailItemClick(rows[1])

        assertEquals(listOf("work", "focus"), displayedModes.map { it.id })
        assertEquals(listOf("Display work", "Display focus"), rows.map { it.title })
        assertEquals(listOf("work", "focus"), rows.map { it.tag })
        assertSame(workDrawable, rows[0].iconDrawable)
        assertSame(focusDrawable, rows[1].iconDrawable)
        assertTrue(rows.all { !it.selected })
        assertTrue(rows.all { it.selectable })
        val config = ConfigParser.parseConfig(fixture.store.json!!)
        assertEquals("focus", config.activeModeId)
        assertEquals(1, fixture.dismissCalls)
    }

    @Test
    fun `first native bind populates items and installs callback`() {
        FakeNativeDetailContent.reset()
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java)!!
        val fixture = fixture(
            json = configJson(activeModeId = "work", lastModeId = "work"),
            nativeApiOverride = nativeApi
        )
        val adapter = fixture.createAdapter()
        val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")

        val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent

        assertEquals("HyperModesFocus", content.suffixValue)
        assertEquals(2, content.itemValues.size)
        assertNotNull(content.callbackValue)
        val work = content.itemValues[0] as FakeNativeDetailContent.SelectableItem
        assertEquals("work", work.tag)
        assertFalse(work.selected)
        assertFalse(work.isForceSingle())
        val outerField = work.javaClass.declaredFields.single {
            FakeNativeDetailContent::class.java.isAssignableFrom(it.type)
        }.apply { isAccessible = true }
        assertEquals(content, outerField.get(work))
    }

    @Test
    fun `native item click activates selected mode and requests close once`() {
        FakeNativeDetailContent.reset()
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java)!!
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeApiOverride = nativeApi
        )
        val adapter = fixture.createAdapter()
        val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")
        val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent
        val focus = content.itemValues[1]

        content.callbackValue!!.onDetailItemClick(focus)
        content.callbackValue!!.onDetailItemClick(focus)

        val config = ConfigParser.parseConfig(fixture.store.json!!)
        assertEquals("focus", config.activeModeId)
        assertEquals(1, fixture.dismissCalls)
    }

    @Test
    fun `adapter delegates refreshItems to session`() {
        FakeNativeDetailContent.reset()
        val nativeApi = FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java)!!
        val fixture = fixture(
            json = configJson(activeModeId = "work", lastModeId = "work"),
            nativeApiOverride = nativeApi
        )
        val adapter = fixture.createAdapter()

        adapter.session.setDetailListening(true)
        val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")
        val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent

        adapter.refreshItems()

        assertNotNull(content.itemValues)
        assertEquals(2, content.itemValues.size)
    }

    @Test
    fun `adapter delegates destroy to session`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val adapter = fixture.createAdapter()

        adapter.destroy()

        assertEquals(DetailLifecycleState.CLOSED, adapter.session.state)
        assertFalse(FocusNativeDetailRegistry.isFocusAdapter(adapter.adapter))
    }

    @Test
    fun `adapter registers session on construction`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val adapter = fixture.createAdapter()

        assertTrue(FocusNativeDetailRegistry.isFocusAdapter(adapter.adapter))
        assertEquals(adapter.session, FocusNativeDetailRegistry.adapterSession(adapter.adapter))
    }

    @Test
    fun `onPanelHidden delegates to session and posts refresh if pending`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val adapter = fixture.createAdapter()

        adapter.setDetailListening(true)
        adapter.setDetailListening(false)
        // First setDetailListening(false) already called onStateRefresh once
        fixture.refreshCalls = 0

        adapter.onPanelHidden()

        assertEquals(DetailLifecycleState.CLOSED, adapter.session.state)
        assertFalse(adapter.session.hasPendingCardRefresh())
    }

    private fun fixture(
        json: String,
        nativeApiOverride: FocusNativeDetailContentApi? = null
    ): Fixture {
        val store = RecordingStore(json, writeResult = true, readError = null)
        return Fixture(
            pluginContext = TestContext("plugin", "Focus modes", "No modes configured", "Open HyperModes", "On", "Off"),
            moduleContext = TestContext("module", "Module title", "Empty", "Open", "Active", "Inactive"),
            store = store,
            repository = FocusCardStateRepository(store, ModeIndexSelector { 0 }),
            nativeApiOverride = nativeApiOverride
        )
    }

    private fun configJson(activeModeId: String?, lastModeId: String?): String {
        return ConfigParser.serializeConfig(
            FullConfig(
                activeModeId = activeModeId,
                lastModeId = lastModeId,
                modes = listOf(mode("work", "Work", "💼"), mode("focus", "Focus", "🧘"))
            )
        )
    }

    private fun mode(id: String, name: String, icon: String): ModeConfig {
        return ModeConfig(
            id = id,
            name = name,
            icon = icon,
            type = ModeType.SCHEDULED,
            notification = NotificationConfig(DndLevel.PRIORITY),
            display = DisplayConfig(),
            pausedApps = emptyList()
        )
    }

    private data class Fixture(
        val pluginContext: TestContext,
        val moduleContext: TestContext,
        val store: RecordingStore,
        val repository: FocusCardStateRepository,
        val nativeApiOverride: FocusNativeDetailContentApi?
    ) {
        var dismissCalls = 0
        var refreshCalls = 0

        fun createAdapter(
            modeIconProvider: ((ModeConfig) -> Drawable)? = null,
            modeDisplayNameProvider: ((ModeConfig) -> String)? = null
        ): FocusModeDetailAdapter {
            val nativeApi = nativeApiOverride ?: FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java)
            return FocusModeDetailAdapter(
                pluginContext = pluginContext,
                moduleContext = moduleContext,
                detailAdapterInterface = FakeDetailAdapter::class.java,
                repository = repository,
                onDismiss = { dismissCalls += 1 },
                onStateRefresh = { refreshCalls += 1 },
                nativeDetailContentApi = nativeApi,
                modeIconProvider = modeIconProvider,
                modeDisplayNameProvider = modeDisplayNameProvider
            )
        }
    }

    private class RecordingStore(
        var json: String?,
        private val writeResult: Boolean = true,
        private val readError: Throwable? = null
    ) : FocusCardConfigStore {
        val writes = mutableListOf<String>()

        override fun read(): String? {
            readError?.let { throw it }
            return json
        }

        override fun write(json: String): Boolean {
            writes += json
            if (writeResult) this.json = json
            return writeResult
        }
    }

    private open class TestContext(
        private val packageNameValue: String,
        title: String,
        empty: String,
        openApp: String,
        active: String,
        inactive: String
    ) : ContextWrapper(null) {
        private val resources = TestResources(
            mapOf(
                R.string.focus_card_title to title,
                R.string.focus_card_empty to empty,
                R.string.focus_card_open_app to openApp,
                R.string.focus_card_active to active,
                R.string.focus_card_inactive to inactive,
                R.string.focus_card_fallback to "Focus mode"
            )
        )

        override fun getApplicationContext(): Context = this
        override fun getPackageName(): String = packageNameValue
        override fun getResources(): Resources = resources
        override fun startActivity(intent: Intent?) = Unit
    }

    @Suppress("DEPRECATION")
    private class TestResources(
        private val strings: Map<Int, String>
    ) : Resources(newAssetManager(), DisplayMetrics().apply { density = 1f }, Configuration()) {
        override fun getString(id: Int): String = strings[id] ?: "string-$id"
        override fun getIdentifier(name: String?, defType: String?, defPackage: String?): Int = 0

        companion object {
            private fun newAssetManager(): AssetManager {
                val constructor = AssetManager::class.java.getDeclaredConstructor()
                constructor.isAccessible = true
                return constructor.newInstance()
            }
        }
    }

    private interface FakeDetailAdapter {
        fun getTitle(): CharSequence
        fun getToggleVisible(): Boolean
        fun getToggleState(): Boolean?
        fun setToggleState(enabled: Boolean)
        fun getToggleEnabled(): Boolean
        fun getMetricsCategory(): Int
        fun getSettingsIntent(): Intent?
        fun getContainerHeight(): Int
        fun createDetailView(context: Context, convertView: View?, parent: ViewGroup?): View
        fun shouldAnimate(): Boolean
        fun hasHeader(): Boolean
        fun openDetailEvent(): Any?
        fun closeDetailEvent(): Any?
        fun moreSettingsEvent(): Any?

        companion object {
            @JvmField
            val INVALID: Any = Any()
        }
    }

    private open class FakeNativeDetailContent(context: Context) : View(context) {
        var suffixValue: String? = null
            private set
        var itemValues: Array<Item> = emptyArray()
            private set
        var callbackValue: Callback? = null
            private set

        fun setSuffix(suffix: String) {
            suffixValue = suffix
        }

        fun setItems(vararg items: Item) {
            if (throwFromSetItems) throw IllegalStateException("setItems failed")
            itemValues = Array(items.size) { index -> items[index] }
        }

        fun setCallback(callback: Callback) {
            if (throwFromSetCallback) throw IllegalStateException("setCallback failed")
            callbackValue = callback
        }

        interface Item {
            fun getTag(): Any? = null
            fun getType(): Int
            fun isForceSingle(): Boolean = false
        }

        inner class SelectableItem : Item {
            @JvmField var clickToDisconnect: Boolean = false
            @JvmField var contentDescription: CharSequence? = null
            @JvmField var icon2Res: Int = 0
            @JvmField var isForceSingle: Boolean = false
            @JvmField var secondarySummary: CharSequence? = null
            @JvmField var selectable: Boolean = false
            @JvmField var selected: Boolean = false
            @JvmField var summary: CharSequence? = null
            @JvmField var tag: Any? = null
            @JvmField var title: CharSequence? = null
            @JvmField var iconRes: Int = 0
            @JvmField var iconDrawable: Drawable? = null
            @JvmField val activated: Boolean = true

            override fun getTag(): Any? = tag
            override fun getType(): Int = 233
            override fun isForceSingle(): Boolean = selected || isForceSingle
        }

        interface Callback {
            fun onDetailItemClick(item: Item)
            fun onDetailItemDisconnect(item: Item) = Unit
        }

        companion object {
            var lastConvertContext: Context? = null
                private set
            var lastConvertView: View? = null
                private set
            var throwFromSetItems: Boolean = false
            var throwFromSetCallback: Boolean = false
            var throwFromConvert: Boolean = false

            fun reset() {
                lastConvertContext = null
                lastConvertView = null
                throwFromSetItems = false
                throwFromSetCallback = false
                throwFromConvert = false
            }

            fun convertOrInflate(context: Context, view: View?, parent: ViewGroup?): FakeNativeDetailContent {
                if (throwFromConvert) throw IllegalStateException("convert failed")
                lastConvertContext = context
                lastConvertView = view
                return view as? FakeNativeDetailContent ?: FakeNativeDetailContent(context)
            }
        }
    }
}
