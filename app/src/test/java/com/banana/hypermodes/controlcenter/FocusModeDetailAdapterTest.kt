package com.banana.hypermodes.controlcenter

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.LinearLayout
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
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
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusModeDetailAdapterTest {

    @Test
    fun `proxy exposes native detail contract values`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val detail = fixture.createDetail()

        assertEquals("Focus modes", detail.getTitle())
        assertFalse(detail.getToggleVisible())
        assertNull(detail.getToggleState())
        detail.setToggleState(true)
        assertFalse(detail.getToggleEnabled())
        assertEquals(118, detail.getMetricsCategory())
        assertEquals(-1, detail.getContainerHeight())
        assertTrue(detail.shouldAnimate())
        assertTrue(detail.hasHeader())
    }

    @Test
    fun `settings intent is null so native detail footer is hidden`() {
        val detail = fixture(configJson(activeModeId = null, lastModeId = "work")).createDetail()

        assertNull(detail.getSettingsIntent())
        assertTrue(detail.hasHeader())
    }

    @Test
    fun `native detail event methods return reflected invalid event when available`() {
        val detail = fixture(configJson(activeModeId = null, lastModeId = "work")).createDetail()

        assertSame(FakeDetailAdapter.INVALID, detail.openDetailEvent())
        assertSame(FakeDetailAdapter.INVALID, detail.closeDetailEvent())
        assertSame(FakeDetailAdapter.INVALID, detail.moreSettingsEvent())
    }

    @Test
    fun `native detail event methods return null without invalid event field`() {
        val detail = FocusModeDetailAdapter(
            pluginContext = TestContext("plugin", "Focus modes", "No modes configured", "Open HyperModes", "On", "Off"),
            moduleContext = TestContext("module", "Module title", "Empty", "Open", "Active", "Inactive"),
            detailAdapterInterface = FakeDetailAdapterWithoutInvalid::class.java,
            repository = FocusCardStateRepository(RecordingStore(configJson(activeModeId = null, lastModeId = "work")), ModeIndexSelector { 0 }),
            onDismiss = {}
        ).create() as FakeDetailAdapterWithoutInvalid

        assertNull(detail.openDetailEvent())
        assertNull(detail.closeDetailEvent())
        assertNull(detail.moreSettingsEvent())
    }

    @Test
    fun `detail view context selection uses host context before plugin fallback`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val hostContext = TestContext("host", "Host title", "Host empty", "Host open", "Host on", "Host off")

        assertSame(hostContext, FocusModeDetailViewContextSelector.select(hostContext, fixture.pluginContext))
        assertSame(fixture.pluginContext, FocusModeDetailViewContextSelector.select("not a context", fixture.pluginContext))
        assertSame(fixture.pluginContext, FocusModeDetailViewContextSelector.select(null, fixture.pluginContext))
    }

    @Test
    fun `manual fallback uses only standard Android view classes`() {
        assertEquals(
            listOf(
                android.widget.ScrollView::class.java,
                android.widget.LinearLayout::class.java,
                android.widget.ImageView::class.java
            ),
            FocusModeManualViewClasses.productionClasses
        )
    }

    @Test
    fun `row descriptors map mode identity status and selection`() {
        val rows = buildFocusModeRows(activeSnapshot("work"))

        assertEquals(listOf("dnd", "work"), rows.map { it.id })
        assertEquals(listOf(false, true), rows.map { it.selected })
        assertEquals("Work, On", rows.single { it.id == "work" }.contentDescription)
    }

    @Test
    fun `native QSDetailContent path is preferred and maps selectable rows`() {
        FakeNativeDetailContent.reset()
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val fixture = fixture(
            json = configJson(activeModeId = "focus", lastModeId = "work"),
            nativeDetailContentApi = nativeApi
        )
        val hostContext = TestContext("host", "Host title", "Host empty", "Host open", "Host on", "Host off")
        val convertView = FakeNativeDetailContent(hostContext)
        val detail = fixture.createDetail()

        val view = detail.createDetailView(hostContext, convertView, null)

        assertSame(convertView, view)
        val content = view as FakeNativeDetailContent
        assertSame(hostContext, FakeNativeDetailContent.lastConvertContext)
        assertSame(convertView, FakeNativeDetailContent.lastConvertView)
        assertEquals("HyperModesFocus", content.suffixValue)
        assertEquals(2, content.itemValues.size)

        val work = content.itemValues[0] as FakeNativeDetailContent.SelectableItem
        assertEquals("work", work.tag)
        assertEquals("Work", work.title)
        assertEquals("Off", work.summary)
        assertEquals("Work, Off", work.contentDescription)
        assertTrue(work.selectable)
        assertFalse(work.selected)
        assertTrue(work.isForceSingle())
        assertNotNull(work.iconDrawable)

        val focus = content.itemValues[1] as FakeNativeDetailContent.SelectableItem
        assertEquals("focus", focus.tag)
        assertEquals("Focus", focus.title)
        assertEquals("On", focus.summary)
        assertEquals("Focus, On", focus.contentDescription)
        assertTrue(focus.selectable)
        assertTrue(focus.selected)
        assertTrue(focus.isForceSingle())
        assertNotNull(focus.iconDrawable)
    }

    @Test
    fun `selection controller dismisses once and refreshes after successful activation`() {
        val repository = RecordingSelectionRepository(result = true)
        var dismissCalls = 0
        var refreshCalls = 0
        val controller = FocusModeSelectionController(
            repository = repository,
            dismiss = { dismissCalls += 1 },
            refreshState = { refreshCalls += 1 }
        )

        controller.select("focus")

        assertEquals(listOf("focus"), repository.activateCalls)
        assertEquals(1, dismissCalls)
        assertEquals(1, refreshCalls)
    }

    @Test
    fun `selection controller dismisses once and refreshes when activation returns false`() {
        val repository = RecordingSelectionRepository(result = false)
        var dismissCalls = 0
        var refreshCalls = 0
        val controller = FocusModeSelectionController(
            repository = repository,
            dismiss = { dismissCalls += 1 },
            refreshState = { refreshCalls += 1 }
        )

        controller.select("missing")

        assertEquals(listOf("missing"), repository.activateCalls)
        assertEquals(1, dismissCalls)
        assertEquals(1, refreshCalls)
    }

    @Test
    fun `selection controller dismisses once and refreshes when activation throws`() {
        val repository = RecordingSelectionRepository(error = IllegalStateException("write failed"))
        var dismissCalls = 0
        var refreshCalls = 0
        val controller = FocusModeSelectionController(
            repository = repository,
            dismiss = { dismissCalls += 1 },
            refreshState = { refreshCalls += 1 }
        )

        controller.select("focus")

        assertEquals(listOf("focus"), repository.activateCalls)
        assertEquals(1, dismissCalls)
        assertEquals(1, refreshCalls)
    }

    @Test
    fun `selection controller ignores double tap while close is terminal`() {
        val repository = RecordingSelectionRepository(result = true)
        var dismissCalls = 0
        var refreshCalls = 0
        val controller = FocusModeSelectionController(
            repository = repository,
            dismiss = { dismissCalls += 1 },
            refreshState = { refreshCalls += 1 }
        )

        controller.select("focus")
        controller.select("work")

        assertEquals(listOf("focus"), repository.activateCalls)
        assertEquals(1, dismissCalls)
        assertEquals(1, refreshCalls)
    }

    @Test
    fun `main activity launcher catches every failed candidate and returns false`() {
        val parentContext = ThrowingStartActivityContext("parent")
        val rowContext = ThrowingStartActivityContext("row")
        val pluginContext = ThrowingStartActivityContext("plugin")
        val moduleContext = ThrowingStartActivityContext("module")

        val result = FocusModeAppLauncher.launchMainActivity(
            parentContext = parentContext,
            rowContext = rowContext,
            pluginContext = pluginContext,
            moduleContext = moduleContext
        )

        assertFalse(result)
        assertEquals(1, parentContext.startActivityCalls)
        assertEquals(1, rowContext.startActivityCalls)
        assertEquals(1, pluginContext.startActivityCalls)
        assertEquals(1, moduleContext.startActivityCalls)
    }

    @Test
    fun `native callback uses selection controller for false result and double click`() {
        FakeNativeDetailContent.reset()
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            writeResult = false
        )
        val detail = fixture.createDetail()
        val content = detail.createDetailView(fixture.pluginContext, null, null) as FakeNativeDetailContent
        val focus = content.itemValues[1]

        content.callbackValue!!.onDetailItemClick(focus)
        content.callbackValue!!.onDetailItemClick(focus)

        assertEquals(1, fixture.store.writes.size)
        assertEquals(1, fixture.dismissCalls)
        assertEquals(1, fixture.refreshCalls)
    }

    @Test
    fun `native icons remain non null when resources are missing`() {
        FakeNativeDetailContent.reset()
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val nativeFixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            pluginContext = MissingResourceContext(),
            moduleContext = MissingResourceContext()
        )
        val nativeDetail = nativeFixture.createDetail()
        val content = nativeDetail.createDetailView(nativeFixture.pluginContext, null, null) as FakeNativeDetailContent

        content.itemValues.forEach { item ->
            val selectable = item as FakeNativeDetailContent.SelectableItem
            assertNotNull(selectable.iconDrawable)
        }

        val rows = buildFocusModeRows(activeSnapshot("work"))

        assertEquals(R.drawable.ic_stat_zen, rows.single { it.id == "dnd" }.iconResId)
        assertEquals(R.drawable.ic_stat_work, rows.single { it.id == "work" }.iconResId)
    }

    @Test
    fun `native signature failure resolves to null without preventing manual fallback`() {
        val brokenApi = FocusNativeDetailContentResolver.fromContentClass(BrokenNativeDetailContent::class.java)
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val detail = fixture.createDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertNull(brokenApi)
        assertTrue(view is ScrollView)
    }

    @Test
    fun `native setup failure falls back to manual detail view`() {
        FakeNativeDetailContent.reset()
        FakeNativeDetailContent.throwFromSetItems = true
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi
        )
        val detail = fixture.createDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertTrue(view is ScrollView)
        assertFalse(view is FakeNativeDetailContent)
    }

    @Test
    fun `native failure records exact stage and uses manual builder`() {
        listOf(
            FocusDetailFallbackStage.NATIVE_CONVERT,
            FocusDetailFallbackStage.NATIVE_ITEMS,
            FocusDetailFallbackStage.NATIVE_CALLBACK
        ).forEach { stage ->
            val diagnostic = RecordingDiagnostic()
            val nativeError = IllegalStateException("native $stage")
            var manualCalls = 0
            var safeCalls = 0
            val coordinator = FocusDetailViewCoordinator(
                nativeBuilder = { FocusDetailBuildResult.Failed(stage, nativeError) },
                manualBuilder = {
                    manualCalls += 1
                    "manual-$stage"
                },
                safeBuilder = {
                    safeCalls += 1
                    "safe"
                },
                lastResortBuilder = { "last-resort" },
                diagnostic = diagnostic
            )

            val view = coordinator.createDetailView()

            assertEquals("manual-$stage", view)
            assertEquals(listOf(DiagnosticFailure(stage, nativeError)), diagnostic.failures)
            assertEquals(1, manualCalls)
            assertEquals(0, safeCalls)
        }
    }

    @Test
    fun `manual failure returns safe empty view instead of throwing`() {
        val diagnostic = RecordingDiagnostic()
        val manualError = IllegalStateException("manual failed")
        var safeCalls = 0
        val coordinator = FocusDetailViewCoordinator<String>(
            nativeBuilder = null,
            manualBuilder = { throw manualError },
            safeBuilder = {
                safeCalls += 1
                "safe-empty"
            },
            lastResortBuilder = { "last-resort" },
            diagnostic = diagnostic
        )

        val view = coordinator.createDetailView()

        assertEquals("safe-empty", view)
        assertEquals(
            listOf(
                DiagnosticFailure(FocusDetailFallbackStage.NATIVE_API_UNAVAILABLE, null),
                DiagnosticFailure(FocusDetailFallbackStage.MANUAL_BUILD, manualError)
            ),
            diagnostic.failures
        )
        assertEquals(1, safeCalls)
    }

    @Test
    fun `safe builder failure is contained by deterministic last resort`() {
        val diagnostic = RecordingDiagnostic()
        val nativeError = IllegalStateException("native failed")
        val manualError = IllegalStateException("manual failed")
        val safeError = IllegalStateException("safe failed")
        val coordinator = FocusDetailViewCoordinator<String>(
            nativeBuilder = { FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CONVERT, nativeError) },
            manualBuilder = { throw manualError },
            safeBuilder = { throw safeError },
            lastResortBuilder = { "existing-view" },
            diagnostic = diagnostic
        )

        val view = coordinator.createDetailView()

        assertEquals("existing-view", view)
        assertEquals(
            listOf(
                DiagnosticFailure(FocusDetailFallbackStage.NATIVE_CONVERT, nativeError),
                DiagnosticFailure(FocusDetailFallbackStage.MANUAL_BUILD, manualError),
                DiagnosticFailure(FocusDetailFallbackStage.SAFE_BUILD, safeError)
            ),
            diagnostic.failures
        )
    }

    @Test
    fun `createDetailView returns convertView when native manual and safe fallback fail`() {
        FakeNativeDetailContent.reset()
        FakeNativeDetailContent.throwFromConvert = true
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val diagnostic = RecordingDiagnostic()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            diagnostic = diagnostic,
            readError = IllegalStateException("read failed"),
            safeViewFactory = { throw IllegalStateException("safe failed") }
        )
        val detail = fixture.createDetail()
        val convertView = ScrollView(fixture.pluginContext)

        val view = detail.createDetailView(fixture.pluginContext, convertView, null)

        assertSame(convertView, view)
        assertEquals(
            listOf(
                FocusDetailFallbackStage.NATIVE_CONVERT,
                FocusDetailFallbackStage.MANUAL_BUILD,
                FocusDetailFallbackStage.SAFE_BUILD
            ),
            diagnostic.failures.map { it.stage }
        )
    }

    @Test
    fun `createDetailView contains impossible all constructor failure without existing view`() {
        FakeNativeDetailContent.reset()
        FakeNativeDetailContent.throwFromConvert = true
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val diagnostic = RecordingDiagnostic()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            diagnostic = diagnostic,
            readError = IllegalStateException("read failed"),
            safeViewFactory = { throw IllegalStateException("safe failed") }
        )
        val detail = fixture.createNullableDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertNull(view)
        assertEquals(
            listOf(
                FocusDetailFallbackStage.NATIVE_CONVERT,
                FocusDetailFallbackStage.MANUAL_BUILD,
                FocusDetailFallbackStage.SAFE_BUILD
            ),
            diagnostic.failures.map { it.stage }
        )
    }

    @Test
    fun `native api with empty rows falls back to manual empty state without native failure diagnostic`() {
        FakeNativeDetailContent.reset()
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val diagnostic = RecordingDiagnostic()
        val fixture = fixture(
            json = emptyConfigJson(),
            nativeDetailContentApi = nativeApi,
            diagnostic = diagnostic
        )
        val detail = fixture.createDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertTrue(view is ScrollView)
        assertFalse(view is FakeNativeDetailContent)
        assertTrue(diagnostic.failures.isEmpty())
    }

    @Test
    fun `createDetailView never leaks native reflection exception`() {
        FakeNativeDetailContent.reset()
        FakeNativeDetailContent.throwFromConvert = true
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val diagnostic = RecordingDiagnostic()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            diagnostic = diagnostic
        )
        val detail = fixture.createDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertTrue(view is ScrollView)
        assertFalse(view is FakeNativeDetailContent)
        assertEquals(FocusDetailFallbackStage.NATIVE_CONVERT, diagnostic.failures.single().stage)
        assertNotNull(diagnostic.failures.single().throwable)
    }

    @Test
    fun `native items failure diagnoses items stage and falls back to manual`() {
        FakeNativeDetailContent.reset()
        FakeNativeDetailContent.throwFromSetItems = true
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val diagnostic = RecordingDiagnostic()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            diagnostic = diagnostic
        )
        val detail = fixture.createDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertTrue(view is ScrollView)
        assertFalse(view is FakeNativeDetailContent)
        assertEquals(FocusDetailFallbackStage.NATIVE_ITEMS, diagnostic.failures.single().stage)
        assertNotNull(diagnostic.failures.single().throwable)
    }

    @Test
    fun `native callback failure diagnoses callback stage and falls back to manual`() {
        FakeNativeDetailContent.reset()
        FakeNativeDetailContent.throwFromSetCallback = true
        val nativeApi = requireNotNull(FocusNativeDetailContentResolver.fromContentClass(FakeNativeDetailContent::class.java))
        val diagnostic = RecordingDiagnostic()
        val fixture = fixture(
            json = configJson(activeModeId = null, lastModeId = "work"),
            nativeDetailContentApi = nativeApi,
            diagnostic = diagnostic
        )
        val detail = fixture.createDetail()

        val view = detail.createDetailView(fixture.pluginContext, null, null)

        assertTrue(view is ScrollView)
        assertFalse(view is FakeNativeDetailContent)
        assertEquals(FocusDetailFallbackStage.NATIVE_CALLBACK, diagnostic.failures.single().stage)
        assertNotNull(diagnostic.failures.single().throwable)
    }

    @Test
    fun `tile class resolution keeps required classes when native detail API is absent`() {
        val loader = RequiredOnlyClassLoader()

        val classes = FocusCardTileClasses.resolve(loader)

        assertSame(FakeResolvedTile::class.java, classes.tileInterface)
        assertSame(FakeResolvedBooleanState::class.java, classes.booleanStateClass)
        assertSame(FakeResolvedDrawableIcon::class.java, classes.drawableIconClass)
        assertSame(FakeResolvedDetailAdapter::class.java, classes.detailAdapterInterface)
        assertNull(classes.nativeDetailContentApi)
    }

    @Test
    fun `unsupported primitive returning methods use safe defaults`() {
        val detail = fixture(configJson(activeModeId = null, lastModeId = "work")).createDetail()

        assertFalse(detail.unknownBoolean())
        assertEquals(0, detail.unknownInt())
        assertEquals(0L, detail.unknownLong())
        assertEquals(0.toByte(), detail.unknownByte())
        assertEquals(0.toShort(), detail.unknownShort())
        assertEquals(0f, detail.unknownFloat())
        assertEquals(0.0, detail.unknownDouble(), 0.0)
        assertEquals(' ', detail.unknownChar())
        assertNull(detail.unknownReference())
    }

    @Test
    fun `equals hashCode and toString behave safely`() {
        val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
        val detail = fixture.createDetail()
        val same = detail
        val other = fixture.createDetail()

        assertTrue(detail == same)
        assertFalse(detail == other)
        assertFalse(detail.equals("not detail"))
        assertEquals(System.identityHashCode(detail), detail.hashCode())
        assertNotEquals("", detail.toString())
    }

    private fun fixture(
        json: String,
        nativeDetailContentApi: FocusNativeDetailContentApi? = null,
        writeResult: Boolean = true,
        pluginContext: TestContext = TestContext("plugin", "Focus modes", "No modes configured", "Open HyperModes", "On", "Off"),
        moduleContext: TestContext = TestContext("module", "Module title", "Empty", "Open", "Active", "Inactive"),
        diagnostic: FocusDetailDiagnostic = FocusDetailDiagnostic { _, _ -> },
        readError: Throwable? = null,
        safeViewFactory: ((Context) -> View)? = null
    ): Fixture {
        val store = RecordingStore(json, writeResult, readError)
        return Fixture(
            pluginContext = pluginContext,
            moduleContext = moduleContext,
            store = store,
            repository = FocusCardStateRepository(store, ModeIndexSelector { 0 }),
            nativeDetailContentApi = nativeDetailContentApi,
            diagnostic = diagnostic,
            safeViewFactory = safeViewFactory
        )
    }

    private fun activeSnapshot(activeModeId: String?): FocusCardSnapshot {
        val modes = listOf(mode("dnd", "DND", ""), mode("work", "Work", "💼"))
        return FocusCardSnapshot(
            modes = modes,
            displayedMode = modes.firstOrNull { it.id == activeModeId },
            activeModeId = activeModeId,
            isActive = activeModeId != null,
            configValid = true
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

    private fun emptyConfigJson(): String {
        return ConfigParser.serializeConfig(
            FullConfig(
                activeModeId = null,
                lastModeId = null,
                modes = emptyList()
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
        val nativeDetailContentApi: FocusNativeDetailContentApi?,
        val diagnostic: FocusDetailDiagnostic,
        val safeViewFactory: ((Context) -> View)?
    ) {
        var dismissCalls = 0
        var refreshCalls = 0

        fun createDetail(): FakeDetailAdapter {
            return FocusModeDetailAdapter(
                pluginContext = pluginContext,
                moduleContext = moduleContext,
                detailAdapterInterface = FakeDetailAdapter::class.java,
                repository = repository,
                onDismiss = { dismissCalls += 1 },
                onStateRefresh = { refreshCalls += 1 },
                nativeDetailContentApi = nativeDetailContentApi,
                diagnostic = diagnostic,
                safeViewFactory = safeViewFactory ?: { context -> View(context) }
            ).create() as FakeDetailAdapter
        }

        fun createNullableDetail(): FakeNullableDetailAdapter {
            return FocusModeDetailAdapter(
                pluginContext = pluginContext,
                moduleContext = moduleContext,
                detailAdapterInterface = FakeNullableDetailAdapter::class.java,
                repository = repository,
                onDismiss = { dismissCalls += 1 },
                onStateRefresh = { refreshCalls += 1 },
                nativeDetailContentApi = nativeDetailContentApi,
                diagnostic = diagnostic,
                safeViewFactory = safeViewFactory ?: { context -> View(context) }
            ).create() as FakeNullableDetailAdapter
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

    private class MissingResourceContext : TestContext(
        "missing.resources",
        "Focus modes",
        "No modes configured",
        "Open HyperModes",
        "On",
        "Off"
    )

    private class ThrowingStartActivityContext(
        name: String
    ) : TestContext(name, "Focus modes", "No modes configured", "Open HyperModes", "On", "Off") {
        var startActivityCalls = 0
            private set

        override fun startActivity(intent: Intent?) {
            startActivityCalls += 1
            throw IllegalStateException("cannot launch $packageName")
        }
    }

    private class RecordingSelectionRepository(
        private val result: Boolean = true,
        private val error: Throwable? = null
    ) : FocusModeActivator {
        val activateCalls = mutableListOf<String>()

        override fun activate(modeId: String): Boolean {
            activateCalls += modeId
            error?.let { throw it }
            return result
        }
    }

    private data class DiagnosticFailure(
        val stage: FocusDetailFallbackStage,
        val throwable: Throwable?
    )

    private class RecordingDiagnostic : FocusDetailDiagnostic {
        val failures = mutableListOf<DiagnosticFailure>()

        override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {
            failures += DiagnosticFailure(stage, throwable)
        }
    }

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
        fun unknownBoolean(): Boolean
        fun unknownInt(): Int
        fun unknownLong(): Long
        fun unknownByte(): Byte
        fun unknownShort(): Short
        fun unknownFloat(): Float
        fun unknownDouble(): Double
        fun unknownChar(): Char
        fun unknownReference(): Any?

        companion object {
            @JvmField
            val INVALID: Any = Any()
        }
    }

    private interface FakeDetailAdapterWithoutInvalid {
        fun openDetailEvent(): Any?
        fun closeDetailEvent(): Any?
        fun moreSettingsEvent(): Any?
    }

    private interface FakeNullableDetailAdapter {
        fun createDetailView(context: Context, convertView: View?, parent: ViewGroup?): View?
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

    private class BrokenNativeDetailContent(context: Context) : View(context) {
        companion object {
            fun convertOrInflate(context: Context, view: View?, parent: ViewGroup?): BrokenNativeDetailContent {
                return view as? BrokenNativeDetailContent ?: BrokenNativeDetailContent(context)
            }
        }
    }

    private class RequiredOnlyClassLoader : ClassLoader(FocusModeDetailAdapterTest::class.java.classLoader) {
        override fun loadClass(name: String, resolve: Boolean): Class<*> {
            val mapped = when (name) {
                "com.android.systemui.plugins.qs.QSTile" -> FakeResolvedTile::class.java
                "com.android.systemui.plugins.qs.QSTile\$BooleanState" -> FakeResolvedBooleanState::class.java
                "miui.systemui.controlcenter.qs.DrawableIcon" -> FakeResolvedDrawableIcon::class.java
                "com.android.systemui.plugins.qs.DetailAdapter" -> FakeResolvedDetailAdapter::class.java
                "com.android.systemui.qs.QSDetailContent" -> throw ClassNotFoundException(name)
                else -> null
            }
            if (mapped != null) return mapped
            return super.loadClass(name, resolve)
        }
    }

    private interface FakeResolvedTile
    private class FakeResolvedBooleanState
    private class FakeResolvedDrawableIcon(val drawable: Drawable?)
    private interface FakeResolvedDetailAdapter
}
