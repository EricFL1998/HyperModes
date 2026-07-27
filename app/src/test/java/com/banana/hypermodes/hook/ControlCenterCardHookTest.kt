package com.banana.hypermodes.hook

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.net.URLClassLoader
import java.nio.file.Files
import javax.tools.ToolProvider

class ControlCenterCardHookTest {

    @Test
    fun `appendFocusSpec preserves order and appends focus`() {
        val original = mutableListOf("wifi", "cell")

        val result = ControlCenterCardHook.appendFocusSpec(original)

        assertEquals(listOf("wifi", "cell", ControlCenterCardHook.FOCUS_CARD_SPEC), result)
        assertNotSame(original, result)
    }

    @Test
    fun `appendFocusSpec does not duplicate focus`() {
        val original = listOf("wifi", ControlCenterCardHook.FOCUS_CARD_SPEC, "cell")

        val result = ControlCenterCardHook.appendFocusSpec(original)

        assertEquals(original, result)
        assertNotSame(original, result)
    }

    @Test
    fun `appendFocusSpec accepts immutable input list`() {
        val original = listOf("wifi", "cell")

        val result = ControlCenterCardHook.appendFocusSpec(original)

        assertEquals(listOf("wifi", "cell", ControlCenterCardHook.FOCUS_CARD_SPEC), result)
        assertNotSame(original, result)
    }

    @Test
    fun `appendFocusSpec leaves non-list return unchanged`() {
        val original = "wifi,cell"

        val result = ControlCenterCardHook.appendFocusSpec(original)

        assertSame(original, result)
    }

    @Test
    fun `appendFocusSpec leaves null unchanged`() {
        val result = ControlCenterCardHook.appendFocusSpec(null)

        assertEquals(null, result)
    }

    @Test
    fun `filter removes only focus record and preserves native order`() {
        val wifi = RecordingListRecord("wifi")
        val focus = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val cell = RecordingListRecord("cell")
        val nearFocus = RecordingListRecord("hypermodes_focus_alt")
        val original = listOf(wifi, focus, cell, nearFocus)

        val result = ControlCenterCardHook.filterFocusRecord(original)

        assertEquals(listOf(wifi, cell, nearFocus), result)
        assertNotSame(original, result)
    }

    @Test
    fun `filter returns original non-list result unchanged`() {
        val original = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)

        val result = ControlCenterCardHook.filterFocusRecord(original)

        assertSame(original, result)
    }

    @Test
    fun `filter fails closed when a record spec cannot be read`() {
        val original = listOf(
            RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC),
            MissingSpecListRecord(),
            RecordingListRecord("wifi")
        )

        val result = ControlCenterCardHook.filterFocusRecord(original)

        assertSame(original, result)
    }

    @Test
    fun `filter does not mutate immutable source list`() {
        val focus = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val wifi = RecordingListRecord("wifi")
        val original = listOf(focus, wifi)

        val result = ControlCenterCardHook.filterFocusRecord(original)

        assertEquals(listOf(focus, wifi), original)
        assertEquals(listOf(wifi), result)
        assertNotSame(original, result)
    }

    @Test
    fun `tail is inserted immediately before footer`() {
        val device = Any()
        val footer = Any()
        val tail = Any()
        val content = mutableListOf<Any>(device, footer)

        val changed = ControlCenterCardHook.insertFocusTail(content, tail, footer)

        assertTrue(changed)
        assertSame(device, content[0])
        assertSame(tail, content[1])
        assertSame(footer, content[2])
    }

    @Test
    fun `tail appends when footer is absent`() {
        val device = Any()
        val footer = Any()
        val tail = Any()
        val content = mutableListOf<Any>(device)

        val changed = ControlCenterCardHook.insertFocusTail(content, tail, footer)

        assertTrue(changed)
        assertSame(device, content[0])
        assertSame(tail, content[1])
    }

    @Test
    fun `repeated insertion is idempotent`() {
        val device = Any()
        val footer = Any()
        val tail = Any()
        val content = mutableListOf<Any>(device, tail, footer)

        val changed = ControlCenterCardHook.insertFocusTail(content, tail, footer)

        assertFalse(changed)
        assertEquals(3, content.size)
        assertSame(device, content[0])
        assertSame(tail, content[1])
        assertSame(footer, content[2])
    }

    @Test
    fun `device content remains before tail`() {
        val deviceCenter = Any()
        val deviceControl = Any()
        val footer = Any()
        val tail = Any()
        val content = mutableListOf<Any>(deviceCenter, deviceControl, footer)

        val changed = ControlCenterCardHook.insertFocusTail(content, tail, footer)

        assertTrue(changed)
        assertSame(deviceCenter, content[0])
        assertSame(deviceControl, content[1])
        assertSame(tail, content[2])
        assertSame(footer, content[3])
    }

    @Test
    fun `tail moves from stale earlier position to footer boundary`() {
        val staleHeader = Any()
        val deviceCenter = Any()
        val deviceControl = Any()
        val footer = Any()
        val tail = Any()
        val content = mutableListOf<Any>(tail, staleHeader, deviceCenter, tail, deviceControl, footer)

        val changed = ControlCenterCardHook.insertFocusTail(content, tail, footer)

        assertTrue(changed)
        assertEquals(5, content.size)
        assertSame(staleHeader, content[0])
        assertSame(deviceCenter, content[1])
        assertSame(deviceControl, content[2])
        assertSame(tail, content[3])
        assertSame(footer, content[4])
    }

    @Test
    fun `validated feature set resolves filter and distributor methods together`() {
        val fixture = HyperOsControlCenterFixture.create()

        val featureSet = ControlCenterCardHook.validatedTailFeatureSet(fixture.classLoader)

        assertEquals(
            "miui.systemui.controlcenter.panel.main.qs.QSCardsController#getListItems",
            "${featureSet?.listItemsMethod?.declaringClass?.name}#${featureSet?.listItemsMethod?.name}"
        )
        assertEquals(
            "miui.systemui.controlcenter.panel.main.qs.QSListController#getListItems",
            "${featureSet?.listItemsMethodQsList?.declaringClass?.name}#${featureSet?.listItemsMethodQsList?.name}"
        )
        assertEquals(
            "miui.systemui.controlcenter.panel.main.MainPanelContentDistributor#distributePanels",
            "${featureSet?.distributePanelsMethod?.declaringClass?.name}#${featureSet?.distributePanelsMethod?.name}"
        )
    }

    @Test
    fun `validated feature set fails closed when distributor path is missing`() {
        val fixture = HyperOsControlCenterFixture.create(includeDistributor = false)

        val featureSet = ControlCenterCardHook.validatedTailFeatureSet(fixture.classLoader)

        assertEquals(null, featureSet)
    }

    @Test
    fun `validated feature set fails closed when distributor fields cannot carry panel lists`() {
        val fixture = HyperOsControlCenterFixture.create(useInvalidDistributorFields = true)

        val featureSet = ControlCenterCardHook.validatedTailFeatureSet(fixture.classLoader)

        assertEquals(null, featureSet)
    }

    @Test
    fun `distributor hook inserts focus tail after device content and before footer`() {
        val fixture = HyperOsControlCenterFixture.create()
        val focusRecord = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val cardsController = fixture.cardsController(focusRecord)
        val deviceCenter = fixture.mainPanelContent()
        val deviceControl = fixture.mainPanelContent()
        val footer = fixture.mainPanelContent()
        val distributor = fixture.distributor(
            rightPanelContent = mutableListOf(deviceCenter, deviceControl, footer),
            rightFooterSpace = footer,
            childControllers = mutableListOf(deviceCenter, cardsController, deviceControl)
        )

        val changed = ControlCenterCardHook.insertFocusTailFromDistributor(distributor, fixture.classLoader)

        assertTrue(changed)
        val content = fixture.rightPanelContent(distributor)
        assertEquals(4, content.size)
        assertSame(deviceCenter, content[0])
        assertSame(deviceControl, content[1])
        assertNotSame(cardsController, content[2])
        assertSame(footer, content[3])
        val tailItems = fixture.invokeMainPanelContent(content[2], "getListItems") as List<*>
        assertEquals(1, tailItems.size)
        assertSame(focusRecord, tailItems.single())
    }

    @Test
    fun `distributor hook leaves native panel list unchanged when focus record is unavailable`() {
        val fixture = HyperOsControlCenterFixture.create()
        val cardsController = fixture.cardsController(focusRecord = null)
        val device = fixture.mainPanelContent()
        val footer = fixture.mainPanelContent()
        val original = mutableListOf(device, footer)
        val distributor = fixture.distributor(
            rightPanelContent = original,
            rightFooterSpace = footer,
            childControllers = mutableListOf(cardsController)
        )

        val changed = ControlCenterCardHook.insertFocusTailFromDistributor(distributor, fixture.classLoader)

        assertFalse(changed)
        assertEquals(2, original.size)
        assertSame(device, original[0])
        assertSame(footer, original[1])
    }

    @Test
    fun `initializeFocusTile calls original post-create initialization in order and returns tile`() {
        val tile = RecordingTile()

        val result = ControlCenterCardHook.initializeFocusTile(tile, userId = 10)

        assertSame(tile, result)
        assertEquals(
            listOf(
                "setTileSpec:${ControlCenterCardHook.FOCUS_CARD_SPEC}",
                "userSwitch:10",
                "refreshState"
            ),
            tile.calls
        )
    }

    @Test
    fun `initializeFocusTile returns null and destroys tile when initialization fails`() {
        val tile = RecordingTile(failOnRefresh = true)

        val result = ControlCenterCardHook.initializeFocusTile(tile, userId = 11)

        assertEquals(null, result)
        assertEquals(
            listOf(
                "setTileSpec:${ControlCenterCardHook.FOCUS_CARD_SPEC}",
                "userSwitch:11",
                "refreshState",
                "destroy"
            ),
            tile.calls
        )
    }

    @Test
    fun `applyFocusCardSizing finds focus tile and updates bound card view to horizontal size`() {
        val view = RecordingCardView()
        val record = RecordingCardRecord(view)
        val controller = RecordingCardsController(record)

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertTrue(result)
        assertEquals(listOf(ControlCenterCardHook.FOCUS_CARD_SPEC), controller.requestedSpecs)
        assertFalse(record.currentShrink)
        assertFalse(view.currentShrink)
        assertEquals(listOf(false), record.shrinkUpdates)
        assertEquals(listOf(false), view.shrinkUpdates)
        assertEquals(listOf(false), view.backgroundUpdates)
    }

    @Test
    fun `applyFocusCardSizing updates record when focus tile is not bound to a view`() {
        val record = RecordingCardRecord(tileView = null)
        val controller = RecordingCardsController(record)

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertTrue(result)
        assertEquals(listOf(ControlCenterCardHook.FOCUS_CARD_SPEC), controller.requestedSpecs)
        assertFalse(record.currentShrink)
        assertEquals(listOf(false), record.shrinkUpdates)
    }

    @Test
    fun `applyFocusCardSizing fails closed when focus record is missing`() {
        val controller = RecordingCardsController(record = null)

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertFalse(result)
        assertEquals(listOf(ControlCenterCardHook.FOCUS_CARD_SPEC), controller.requestedSpecs)
    }

    @Test
    fun `applyFocusCardSizing fails closed on reflection failure`() {
        val controller = ThrowingCardsController()

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertFalse(result)
    }

    @Test
    fun `applyFocusCardSizing restores record and view shrink state when background update throws`() {
        val view = RecordingCardView(initialShrink = true, throwBackgroundOnCall = 1)
        val record = RecordingCardRecord(tileView = view, initialShrink = true)
        val controller = RecordingCardsController(record)

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertFalse(result)
        assertTrue(record.currentShrink)
        assertTrue(view.currentShrink)
        assertEquals(listOf(false, true), record.shrinkUpdates)
        assertEquals(listOf(false, true), view.shrinkUpdates)
        assertEquals(listOf(false, false), view.backgroundUpdates)
    }

    @Test
    fun `applyFocusCardSizing does not mutate when shrink getter is missing`() {
        val view = RecordingCardView(initialShrink = true)
        val record = MissingGetterCardRecord(tileView = view, initialShrink = true)
        val controller = MissingGetterCardsController(record)

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertFalse(result)
        assertTrue(record.currentShrink)
        assertTrue(view.currentShrink)
        assertEquals(emptyList<Boolean>(), record.shrinkUpdates)
        assertEquals(emptyList<Boolean>(), view.shrinkUpdates)
        assertEquals(emptyList<Boolean>(), view.backgroundUpdates)
    }

    @Test
    fun `applyFocusCardSizing does not mutate when bound view is missing required background method`() {
        val view = MissingBackgroundCardView(initialShrink = true)
        val record = RecordingCardRecord(tileView = view, initialShrink = true)
        val controller = RecordingCardsController(record)

        val result = ControlCenterCardHook.applyFocusCardSizing(controller)

        assertFalse(result)
        assertTrue(record.currentShrink)
        assertTrue(view.currentShrink)
        assertEquals(emptyList<Boolean>(), record.shrinkUpdates)
        assertEquals(emptyList<Boolean>(), view.shrinkUpdates)
    }

    @Test
    fun `focus tail getListItems returns only the latest native focus record`() {
        val firstFocus = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val secondFocus = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val delegate = RecordingMainPanelDelegate(focusRecord = firstFocus)
        val proxy = FocusTailContent(delegate, FakeMainPanelContent::class.java).proxy() as FakeMainPanelContent

        val firstResult = proxy.getListItems()
        delegate.focusRecord = secondFocus
        val secondResult = proxy.getListItems()

        assertEquals(1, firstResult.size)
        assertSame(firstFocus, firstResult.single())
        assertEquals(1, secondResult.size)
        assertSame(secondFocus, secondResult.single())
        assertEquals(
            listOf(
                "getTile:${ControlCenterCardHook.FOCUS_CARD_SPEC}",
                "getTile:${ControlCenterCardHook.FOCUS_CARD_SPEC}"
            ),
            delegate.calls
        )
    }

    @Test
    fun `focus tail available delegates and requires a native focus record`() {
        val focus = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val availableDelegate = RecordingMainPanelDelegate(focusRecord = focus, availableResult = true)
        val availableProxy = FocusTailContent(
            availableDelegate,
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent
        val missingFocusDelegate = RecordingMainPanelDelegate(focusRecord = null, availableResult = true)
        val missingFocusProxy = FocusTailContent(
            missingFocusDelegate,
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent
        val unavailableDelegate = RecordingMainPanelDelegate(focusRecord = focus, availableResult = false)
        val unavailableProxy = FocusTailContent(
            unavailableDelegate,
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent

        assertTrue(availableProxy.available())
        assertFalse(missingFocusProxy.available())
        assertFalse(unavailableProxy.available())

        assertEquals(
            listOf("available", "getTile:${ControlCenterCardHook.FOCUS_CARD_SPEC}"),
            availableDelegate.calls
        )
        assertEquals(
            listOf("available", "getTile:${ControlCenterCardHook.FOCUS_CARD_SPEC}"),
            missingFocusDelegate.calls
        )
        assertEquals(
            listOf("available", "getTile:${ControlCenterCardHook.FOCUS_CARD_SPEC}"),
            unavailableDelegate.calls
        )
    }

    @Test
    fun `focus tail fails closed when native focus lookup throws`() {
        val proxy = FocusTailContent(
            ThrowingTileLookupMainPanelDelegate(),
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent

        assertEquals(emptyList<Any>(), proxy.getListItems())
        assertFalse(proxy.available())
    }

    @Test
    fun `focus tail reports right-panel placement with bottom priority`() {
        val proxy = FocusTailContent(
            RecordingMainPanelDelegate(),
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent

        assertTrue(proxy.getRightOrLeft())
        assertEquals(60, proxy.getPriority())
    }

    @Test
    fun `focus tail createViewHolder delegates to native controller`() {
        val parent = "parent"
        val holder = Any()
        val delegate = RecordingMainPanelDelegate(viewHolder = holder)
        val proxy = FocusTailContent(delegate, FakeMainPanelContent::class.java).proxy() as FakeMainPanelContent

        val result = proxy.createViewHolder(parent)

        assertSame(holder, result)
        assertEquals(listOf("createViewHolder:parent"), delegate.calls)
    }

    @Test
    fun `focus tail known methods fail closed on delegate private api failures`() {
        val proxy = FocusTailContent(
            ThrowingKnownMethodsMainPanelDelegate(),
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent

        assertFalse(proxy.available())
        assertEquals(null, proxy.createViewHolder("parent"))
    }

    @Test
    fun `focus tail actual HyperOS lifecycle methods delegate exactly once`() {
        val delegate = RecordingMainPanelDelegate()
        val proxy = FocusTailContent(delegate, FakeMainPanelContent::class.java).proxy() as FakeMainPanelContent

        proxy.applyPayload("holder", "item", "payload")
        proxy.onBindViewHolder("holder", "item")
        proxy.onUnbindViewHolder("holder", "item")
        proxy.updateConfiguration("holder", "item", "config")
        proxy.updateMode("holder", "item", "mode", true)
        proxy.updateStyle("holder", "item", "style")
        proxy.updateSuperSaveMode("holder", "item")
        proxy.onSpreadChange("holder", 0.75f, 1.25f)
        proxy.onExpandChange("holder", 2.5f)
        proxy.onBrightnessChange(0.5f, true)

        assertEquals(
            listOf(
                "applyPayload:holder:item:payload",
                "onBindViewHolder:holder:item",
                "onUnbindViewHolder:holder:item",
                "updateConfiguration:holder:item:config",
                "updateMode:holder:item:mode:true",
                "updateStyle:holder:item:style",
                "updateSuperSaveMode:holder:item",
                "onSpreadChange:holder:0.75:1.25",
                "onExpandChange:holder:2.5",
                "onBrightnessChange:0.5:true"
            ),
            delegate.calls
        )
    }

    @Test
    fun `focus tail invokes actual HyperOS default lifecycle methods on delegate`() {
        val fixture = JavaDefaultMainPanelFixture.create()
        val proxy = FocusTailContent(
            fixture.delegate,
            fixture.mainPanelContentInterface
        ).proxy()

        fixture.invoke(proxy, "applyPayload", "holder", "item", "payload")
        fixture.invoke(proxy, "onBindViewHolder", "holder", "item")
        fixture.invoke(proxy, "onUnbindViewHolder", "holder", "item")
        fixture.invoke(proxy, "updateConfiguration", "holder", "item", "config")
        fixture.invoke(proxy, "updateMode", "holder", "item", "mode", true)
        fixture.invoke(proxy, "updateStyle", "holder", "item", "style")
        fixture.invoke(proxy, "updateSuperSaveMode", "holder", "item")
        fixture.invoke(proxy, "onSpreadChange", "holder", 0.75f, 1.25f)
        fixture.invoke(proxy, "onExpandChange", "holder", 2.5f)
        fixture.invoke(proxy, "onBrightnessChange", 0.5f, true)

        assertEquals(
            listOf(
                "default:applyPayload:holder:item:payload",
                "default:onBindViewHolder:holder:item",
                "default:onUnbindViewHolder:holder:item",
                "default:updateConfiguration:holder:item:config",
                "default:updateMode:holder:item:mode:true",
                "default:updateStyle:holder:item:style",
                "default:updateSuperSaveMode:holder:item",
                "default:onSpreadChange:holder:0.75:1.25",
                "default:onExpandChange:holder:2.5",
                "default:onBrightnessChange:0.5:true"
            ),
            fixture.events()
        )
    }

    @Test
    fun `focus tail moveElement fails closed without delegating`() {
        val delegate = RecordingMainPanelDelegate()
        val proxy = FocusTailContent(delegate, FakeMainPanelContent::class.java).proxy() as FakeMainPanelContent

        val result = proxy.moveElement(1, 2)

        assertFalse(result)
        assertEquals(emptyList<String>(), delegate.calls)
    }

    @Test
    fun `focus tail object methods are identity-safe`() {
        val proxy = FocusTailContent(
            ThrowingObjectMethodsDelegate(),
            FakeMainPanelContent::class.java
        ).proxy() as FakeMainPanelContent

        assertTrue(proxy.equals(proxy))
        assertFalse(proxy.equals(Any()))
        assertEquals(System.identityHashCode(proxy), proxy.hashCode())
        assertTrue(proxy.toString().contains("FocusTailContent"))
    }

    @Test
    fun `focus tail unknown primitive-returning methods use type defaults`() {
        val delegate = RecordingMainPanelDelegate()
        val proxy = FocusTailContent(delegate, FakeMainPanelContent::class.java).proxy() as FakeMainPanelContent

        assertFalse(proxy.unknownBoolean())
        assertEquals(0, proxy.unknownInt())
        assertEquals(0L, proxy.unknownLong())
        assertEquals(0f, proxy.unknownFloat())
        assertEquals(0.0, proxy.unknownDouble(), 0.0)
        assertEquals(0.toShort(), proxy.unknownShort())
        assertEquals(0.toByte(), proxy.unknownByte())
        assertEquals(' ', proxy.unknownChar())
        assertEquals(emptyList<String>(), delegate.calls)
    }

    @Test
    fun `focus tail cache reuses by controller identity without equal-object collisions`() {
        val focus = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC)
        val firstController = EqualMainPanelDelegate(focus)
        val equalController = EqualMainPanelDelegate(focus)

        val firstTail = ControlCenterCardHook.focusTailContent(
            firstController,
            FakeMainPanelContent::class.java
        )
        val firstTailAgain = ControlCenterCardHook.focusTailContent(
            firstController,
            FakeMainPanelContent::class.java
        )
        val equalControllerTail = ControlCenterCardHook.focusTailContent(
            equalController,
            FakeMainPanelContent::class.java
        )

        assertEquals(firstController, equalController)
        assertSame(firstTail, firstTailAgain)
        assertSame(firstTail.proxy(), firstTailAgain.proxy())
        assertNotSame(firstTail, equalControllerTail)
        assertNotSame(firstTail.proxy(), equalControllerTail.proxy())
    }

    private class RecordingListRecord(
        private val spec: String
    ) {
        fun getSpec(): String = spec
    }

    private class MissingSpecListRecord

    private class HyperOsControlCenterFixture(
        val classLoader: ClassLoader,
        private val qsCardsControllerClass: Class<*>,
        private val mainPanelContentClass: Class<*>?,
        private val deviceContentClass: Class<*>?,
        private val distributorClass: Class<*>?
    ) {
        fun cardsController(focusRecord: Any?): Any {
            return qsCardsControllerClass.getDeclaredConstructor(Any::class.java).newInstance(focusRecord)
        }

        fun mainPanelContent(): Any {
            return deviceContentClass?.getDeclaredConstructor()?.newInstance()
                ?: error("Device MainPanelContent fixture missing")
        }

        fun distributor(
            rightPanelContent: MutableList<Any>,
            rightFooterSpace: Any?,
            childControllers: MutableList<Any>
        ): Any {
            val instance = distributorClass?.getDeclaredConstructor()?.newInstance()
                ?: error("Distributor fixture missing")
            distributorClass.getField("rightPanelContent").set(instance, rightPanelContent)
            distributorClass.getField("rightFooterSpace").set(instance, rightFooterSpace)
            distributorClass.getField("childControllers").set(instance, childControllers)
            return instance
        }

        fun rightPanelContent(distributor: Any): MutableList<Any> {
            @Suppress("UNCHECKED_CAST")
            return distributor.javaClass.getField("rightPanelContent").get(distributor) as MutableList<Any>
        }

        fun invokeMainPanelContent(content: Any, methodName: String): Any? {
            return mainPanelContentClass?.getMethod(methodName)?.invoke(content)
        }

        companion object {
            fun create(
                includeDistributor: Boolean = true,
                useInvalidDistributorFields: Boolean = false
            ): HyperOsControlCenterFixture {
                val sourceDir = Files.createTempDirectory("focus-tail-hyperos-src")
                val outputDir = Files.createTempDirectory("focus-tail-hyperos-out")
                writeSource(
                    sourceDir,
                    "miui/systemui/controlcenter/qs/QSController.java",
                    """
                    package miui.systemui.controlcenter.qs;
                    public class QSController {
                        public java.util.List<String> getCardStyleTileSpecs() { return java.util.Collections.emptyList(); }
                        public Object createTile(String spec) { return null; }
                    }
                    """.trimIndent()
                )
                writeSource(
                    sourceDir,
                    "miui/systemui/controlcenter/panel/main/MainPanelContent.java",
                    """
                    package miui.systemui.controlcenter.panel.main;
                    public interface MainPanelContent {
                        default java.util.List<Object> getListItems() { return java.util.Collections.emptyList(); }
                        default boolean available() { return true; }
                        default boolean getRightOrLeft() { return true; }
                        default int getPriority() { return 0; }
                    }
                    """.trimIndent()
                )
                writeSource(
                    sourceDir,
                    "miui/systemui/controlcenter/panel/main/DevicePanelContent.java",
                    """
                    package miui.systemui.controlcenter.panel.main;
                    public class DevicePanelContent implements MainPanelContent {}
                    """.trimIndent()
                )
                writeSource(
                    sourceDir,
                    "miui/systemui/controlcenter/panel/main/qs/QSCardsController.java",
                    """
                    package miui.systemui.controlcenter.panel.main.qs;
                    import miui.systemui.controlcenter.panel.main.MainPanelContent;
                    public class QSCardsController implements MainPanelContent {
                        private final Object focusRecord;
                        public QSCardsController(Object focusRecord) { this.focusRecord = focusRecord; }
                        public void preparePanelUpdate() {}
                        public java.util.List<Object> getListItems() { return java.util.Collections.emptyList(); }
                        public Object getTile(String spec) { return focusRecord; }
                    }
                    """.trimIndent()
                )
                writeSource(
                    sourceDir,
                    "miui/systemui/controlcenter/panel/main/qs/QSListController.java",
                    """
                    package miui.systemui.controlcenter.panel.main.qs;
                    import miui.systemui.controlcenter.panel.main.MainPanelContent;
                    public class QSListController implements MainPanelContent {
                        public java.util.List<Object> getListItems() { return java.util.Collections.emptyList(); }
                    }
                    """.trimIndent()
                )
                if (includeDistributor) {
                    val distributorFields = if (useInvalidDistributorFields) {
                        """
                            public Object rightPanelContent;
                            public Object rightFooterSpace;
                            public Object childControllers;
                        """.trimIndent()
                    } else {
                        """
                            public java.util.ArrayList<Object> rightPanelContent = new java.util.ArrayList<>();
                            public Object rightFooterSpace;
                            public java.util.ArrayList<Object> childControllers = new java.util.ArrayList<>();
                        """.trimIndent()
                    }
                    writeSource(
                        sourceDir,
                        "miui/systemui/controlcenter/panel/main/MainPanelContentDistributor.java",
                        """
                        package miui.systemui.controlcenter.panel.main;
                        public class MainPanelContentDistributor {
                            $distributorFields
                            public void distributePanels(boolean expanded) {}
                        }
                        """.trimIndent()
                    )
                }
                compileJava(sourceDir, outputDir)
                val classLoader = URLClassLoader(arrayOf(outputDir.toUri().toURL()), null)
                val mainPanelContentClass = classLoader.loadClass(
                    "miui.systemui.controlcenter.panel.main.MainPanelContent"
                )
                val distributorClass = if (includeDistributor) {
                    classLoader.loadClass("miui.systemui.controlcenter.panel.main.MainPanelContentDistributor")
                } else {
                    null
                }
                return HyperOsControlCenterFixture(
                    classLoader = classLoader,
                    qsCardsControllerClass = classLoader.loadClass(
                        "miui.systemui.controlcenter.panel.main.qs.QSCardsController"
                    ),
                    mainPanelContentClass = mainPanelContentClass,
                    deviceContentClass = classLoader.loadClass(
                        "miui.systemui.controlcenter.panel.main.DevicePanelContent"
                    ),
                    distributorClass = distributorClass
                )
            }

            private fun writeSource(sourceDir: java.nio.file.Path, relativePath: String, source: String) {
                val sourceFile = sourceDir.resolve(relativePath)
                Files.createDirectories(sourceFile.parent)
                Files.writeString(sourceFile, source)
            }

            private fun compileJava(sourceDir: java.nio.file.Path, outputDir: java.nio.file.Path) {
                val sources = Files.walk(sourceDir)
                    .filter { path -> path.toString().endsWith(".java") }
                    .map { path -> path.toString() }
                    .toList()
                val compiler = ToolProvider.getSystemJavaCompiler()
                    ?: error("JDK compiler is required for HyperOS fixture")
                val errors = ByteArrayOutputStream()
                val args = mutableListOf("-d", outputDir.toString())
                args += sources
                val result = compiler.run(null, null, errors, *args.toTypedArray())
                check(result == 0) { errors.toString() }
            }
        }
    }

    private class RecordingTile(
        private val failOnRefresh: Boolean = false
    ) {
        val calls = mutableListOf<String>()

        fun setTileSpec(spec: String) {
            calls += "setTileSpec:$spec"
        }

        fun userSwitch(userId: Int) {
            calls += "userSwitch:$userId"
        }

        fun refreshState() {
            calls += "refreshState"
            if (failOnRefresh) throw IllegalStateException("refresh failed")
        }

        fun destroy() {
            calls += "destroy"
        }
    }

    private class RecordingCardsController(
        private val record: RecordingCardRecord?
    ) {
        val requestedSpecs = mutableListOf<String>()

        fun getTile(spec: String): RecordingCardRecord? {
            requestedSpecs += spec
            return record
        }
    }

    private class ThrowingCardsController {
        fun getTile(spec: String): Any? {
            throw IllegalStateException("boom for $spec")
        }
    }

    private class RecordingCardRecord(
        private val tileView: Any?,
        initialShrink: Boolean = true
    ) {
        var currentShrink = initialShrink
            private set
        val shrinkUpdates = mutableListOf<Boolean>()

        fun getShrinkCardStyle(): Boolean = currentShrink

        fun setShrinkCardStyle(shrink: Boolean) {
            shrinkUpdates += shrink
            currentShrink = shrink
        }

        fun getTileView(): Any? = tileView
    }

    private class MissingGetterCardsController(
        private val record: MissingGetterCardRecord?
    ) {
        fun getTile(spec: String): MissingGetterCardRecord? {
            require(spec == ControlCenterCardHook.FOCUS_CARD_SPEC)
            return record
        }
    }

    private class MissingGetterCardRecord(
        private val tileView: Any?,
        initialShrink: Boolean = true
    ) {
        var currentShrink = initialShrink
            private set
        val shrinkUpdates = mutableListOf<Boolean>()

        fun setShrinkCardStyle(shrink: Boolean) {
            shrinkUpdates += shrink
            currentShrink = shrink
        }

        fun getTileView(): Any? = tileView
    }

    private class RecordingCardView(
        initialShrink: Boolean = true,
        private val throwBackgroundOnCall: Int? = null
    ) {
        var currentShrink = initialShrink
            private set
        val shrinkUpdates = mutableListOf<Boolean>()
        val backgroundUpdates = mutableListOf<Boolean>()
        private var backgroundCalls = 0

        fun updateShrinkCardStyle(shrink: Boolean) {
            shrinkUpdates += shrink
            currentShrink = shrink
        }

        fun updateBackground(animated: Boolean) {
            backgroundCalls += 1
            backgroundUpdates += animated
            if (backgroundCalls == throwBackgroundOnCall) {
                throw IllegalStateException("background update failed")
            }
        }
    }

    private class MissingBackgroundCardView(
        initialShrink: Boolean = true
    ) {
        var currentShrink = initialShrink
            private set
        val shrinkUpdates = mutableListOf<Boolean>()

        fun updateShrinkCardStyle(shrink: Boolean) {
            shrinkUpdates += shrink
            currentShrink = shrink
        }
    }

    private interface FakeMainPanelContent {
        fun getListItems(): List<Any>
        fun available(): Boolean
        fun getRightOrLeft(): Boolean
        fun getPriority(): Int
        fun createViewHolder(parent: Any): Any?
        fun applyPayload(holder: Any, item: Any, payload: Any?)
        fun onBindViewHolder(holder: Any, item: Any)
        fun onUnbindViewHolder(holder: Any, item: Any)
        fun updateConfiguration(holder: Any, item: Any, config: Any)
        fun updateMode(holder: Any, item: Any, mode: Any, fromUser: Boolean)
        fun updateStyle(holder: Any, item: Any, style: Any)
        fun updateSuperSaveMode(holder: Any, item: Any)
        fun onSpreadChange(holder: Any, spread: Float, slide: Float)
        fun onExpandChange(holder: Any, translationY: Float)
        fun onBrightnessChange(brightness: Float, tracking: Boolean)
        fun moveElement(from: Int, to: Int): Boolean
        fun unknownBoolean(): Boolean
        fun unknownInt(): Int
        fun unknownLong(): Long
        fun unknownFloat(): Float
        fun unknownDouble(): Double
        fun unknownShort(): Short
        fun unknownByte(): Byte
        fun unknownChar(): Char
    }

    private open class RecordingMainPanelDelegate(
        var focusRecord: Any? = RecordingListRecord(ControlCenterCardHook.FOCUS_CARD_SPEC),
        private val availableResult: Boolean = true,
        private val viewHolder: Any? = null
    ) {
        val calls = mutableListOf<String>()

        fun getTile(spec: String): Any? {
            calls += "getTile:$spec"
            return focusRecord
        }

        fun available(): Boolean {
            calls += "available"
            return availableResult
        }

        fun createViewHolder(parent: Any): Any? {
            calls += "createViewHolder:$parent"
            return viewHolder
        }

        fun applyPayload(holder: Any, item: Any, payload: Any?) {
            calls += "applyPayload:$holder:$item:$payload"
        }

        fun onBindViewHolder(holder: Any, item: Any) {
            calls += "onBindViewHolder:$holder:$item"
        }

        fun onUnbindViewHolder(holder: Any, item: Any) {
            calls += "onUnbindViewHolder:$holder:$item"
        }

        fun updateConfiguration(holder: Any, item: Any, config: Any) {
            calls += "updateConfiguration:$holder:$item:$config"
        }

        fun updateMode(holder: Any, item: Any, mode: Any, fromUser: Boolean) {
            calls += "updateMode:$holder:$item:$mode:$fromUser"
        }

        fun updateStyle(holder: Any, item: Any, style: Any) {
            calls += "updateStyle:$holder:$item:$style"
        }

        fun updateSuperSaveMode(holder: Any, item: Any) {
            calls += "updateSuperSaveMode:$holder:$item"
        }

        fun onSpreadChange(holder: Any, spread: Float, slide: Float) {
            calls += "onSpreadChange:$holder:$spread:$slide"
        }

        fun onExpandChange(holder: Any, translationY: Float) {
            calls += "onExpandChange:$holder:$translationY"
        }

        fun onBrightnessChange(brightness: Float, tracking: Boolean) {
            calls += "onBrightnessChange:$brightness:$tracking"
        }
    }

    private class EqualMainPanelDelegate(
        focusRecord: Any?
    ) : RecordingMainPanelDelegate(focusRecord = focusRecord) {
        override fun equals(other: Any?): Boolean = other is EqualMainPanelDelegate
        override fun hashCode(): Int = 7
    }

    private class ThrowingObjectMethodsDelegate {
        fun getTile(spec: String): Any? = RecordingListRecord(spec)
        override fun equals(other: Any?): Boolean = throw AssertionError("delegate equals should not be used")
        override fun hashCode(): Int = throw AssertionError("delegate hashCode should not be used")
        override fun toString(): String = throw AssertionError("delegate toString should not be used")
    }

    private class ThrowingTileLookupMainPanelDelegate {
        fun getTile(spec: String): Any? {
            throw IllegalStateException("missing $spec")
        }

        fun available(): Boolean = true
    }

    private class ThrowingKnownMethodsMainPanelDelegate {
        fun getTile(spec: String): Any? = RecordingListRecord(spec)
        fun available(): Boolean = throw IllegalStateException("available missing")
        fun createViewHolder(parent: Any): Any? = throw IllegalStateException("create missing $parent")
    }

    private class JavaDefaultMainPanelFixture(
        val mainPanelContentInterface: Class<*>,
        val delegate: Any
    ) {
        fun invoke(target: Any, name: String, vararg args: Any?) {
            val method = mainPanelContentInterface.methods.single { method ->
                method.name == name && method.parameterTypes.size == args.size
            }
            method.isAccessible = true
            method.invoke(target, *args)
        }

        fun events(): List<String> {
            val field = delegate.javaClass.getField("events")
            @Suppress("UNCHECKED_CAST")
            return field.get(delegate) as List<String>
        }

        companion object {
            fun create(): JavaDefaultMainPanelFixture {
                val sourceDir = Files.createTempDirectory("focus-tail-defaults-src")
                val outputDir = Files.createTempDirectory("focus-tail-defaults-out")
                val sourceFile = sourceDir.resolve("DefaultMainPanelDelegate.java")
                Files.writeString(
                    sourceFile,
                    """
                    import java.util.ArrayList;
                    import java.util.List;

                    interface DefaultMainPanelContent {
                        default void applyPayload(Object holder, Object item, Object payload) {
                            ((DefaultMainPanelDelegate) this).events.add("default:applyPayload:" + holder + ":" + item + ":" + payload);
                        }
                        boolean available(boolean expanded);
                        Object createViewHolder(Object parent, int viewType);
                        List<Object> getListItems();
                        int getPriority();
                        boolean getRightOrLeft();
                        default boolean moveElement(Object from, Object to) { return false; }
                        default void onBindViewHolder(Object holder, Object item) {
                            ((DefaultMainPanelDelegate) this).events.add("default:onBindViewHolder:" + holder + ":" + item);
                        }
                        default void onBrightnessChange(float brightness, boolean tracking) {
                            ((DefaultMainPanelDelegate) this).events.add("default:onBrightnessChange:" + brightness + ":" + tracking);
                        }
                        default void onExpandChange(Object holder, float translationY) {
                            ((DefaultMainPanelDelegate) this).events.add("default:onExpandChange:" + holder + ":" + translationY);
                        }
                        default void onSpreadChange(Object holder, float spread, float slide) {
                            ((DefaultMainPanelDelegate) this).events.add("default:onSpreadChange:" + holder + ":" + spread + ":" + slide);
                        }
                        default void onUnbindViewHolder(Object holder, Object item) {
                            ((DefaultMainPanelDelegate) this).events.add("default:onUnbindViewHolder:" + holder + ":" + item);
                        }
                        default void updateConfiguration(Object holder, Object item, Object config) {
                            ((DefaultMainPanelDelegate) this).events.add("default:updateConfiguration:" + holder + ":" + item + ":" + config);
                        }
                        default void updateMode(Object holder, Object item, Object mode, boolean fromUser) {
                            ((DefaultMainPanelDelegate) this).events.add("default:updateMode:" + holder + ":" + item + ":" + mode + ":" + fromUser);
                        }
                        default void updateStyle(Object holder, Object item, Object style) {
                            ((DefaultMainPanelDelegate) this).events.add("default:updateStyle:" + holder + ":" + item + ":" + style);
                        }
                        default void updateSuperSaveMode(Object holder, Object item) {
                            ((DefaultMainPanelDelegate) this).events.add("default:updateSuperSaveMode:" + holder + ":" + item);
                        }
                    }

                    public class DefaultMainPanelDelegate implements DefaultMainPanelContent {
                        public final ArrayList<String> events = new ArrayList<>();
                        public Object getTile(String spec) { return new Object(); }
                        public boolean available(boolean expanded) { return true; }
                        public Object createViewHolder(Object parent, int viewType) { return new Object(); }
                        public List<Object> getListItems() { return java.util.Collections.emptyList(); }
                        public int getPriority() { return 20; }
                        public boolean getRightOrLeft() { return true; }
                    }
                    """.trimIndent()
                )
                val compiler = ToolProvider.getSystemJavaCompiler()
                    ?: error("JDK compiler is required for default-method fixture")
                val errors = ByteArrayOutputStream()
                val result = compiler.run(
                    null,
                    null,
                    errors,
                    "-d",
                    outputDir.toString(),
                    sourceFile.toString()
                )
                check(result == 0) { errors.toString() }
                val classLoader = URLClassLoader(arrayOf(outputDir.toUri().toURL()), null)
                val delegateClass = classLoader.loadClass("DefaultMainPanelDelegate")
                return JavaDefaultMainPanelFixture(
                    mainPanelContentInterface = classLoader.loadClass("DefaultMainPanelContent"),
                    delegate = delegateClass.getDeclaredConstructor().newInstance()
                )
            }
        }
    }
}
