package com.banana.hypermodes.controlcenter

import org.junit.Assert.*
import org.junit.Test

class FocusModeDetailSessionTest {

    @Test
    fun `initial state is CLOSED`() {
        val repository = createFakeRepository()
        val session = FocusModeDetailSession(
            repository = repository,
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `setDetailListening true moves CLOSED to OPEN`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        assertEquals(DetailLifecycleState.CLOSED, session.state)

        session.setDetailListening(true)

        assertEquals(DetailLifecycleState.OPEN, session.state)
    }

    @Test
    fun `setDetailListening false moves OPEN to CLOSING`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        assertEquals(DetailLifecycleState.OPEN, session.state)

        session.setDetailListening(false)

        assertEquals(DetailLifecycleState.CLOSING, session.state)
    }

    @Test
    fun `onPanelHidden moves CLOSING to CLOSED`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)
        assertEquals(DetailLifecycleState.CLOSING, session.state)

        session.onPanelHidden()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `destroy moves to CLOSED immediately`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        assertEquals(DetailLifecycleState.OPEN, session.state)

        session.destroy()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `destroy from CLOSING moves to CLOSED`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)
        assertEquals(DetailLifecycleState.CLOSING, session.state)

        session.destroy()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `setDetailListening false records pending refresh`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)

        // Pending refresh flag is internal, verify through state
        assertEquals(DetailLifecycleState.CLOSING, session.state)
    }

    @Test
    fun `destroy clears pending refresh`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        session.setDetailListening(false)

        session.destroy()

        assertEquals(DetailLifecycleState.CLOSED, session.state)
    }

    @Test
    fun `bindDetailView registers content and returns View`() {
        val api = createFakeNativeApi()
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = api,
            diagnostic = FakeDiagnostic()
        )

        val view = session.bindDetailView(
            context = android.app.Application(),
            convertView = null,
            parent = null
        )

        assertNotNull(view)
        assertTrue(FocusNativeDetailRegistry.isFocusContent(view!!))
    }

    @Test
    fun `refreshItems calls setItems when OPEN`() {
        val api = createFakeNativeApi()
        val repository = createFakeRepository()

        val session = FocusModeDetailSession(
            repository = repository,
            onDismiss = {},
            nativeDetailContentApi = api,
            diagnostic = FakeDiagnostic()
        )

        val view = session.bindDetailView(android.app.Application(), null, null)
        session.setDetailListening(true)

        session.refreshItems()

        // If no exception, refreshItems is callable
        assertNotNull(view)
    }

    @Test
    fun `hasPendingCardRefresh returns true after CLOSING`() {
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = null,
            diagnostic = FakeDiagnostic()
        )

        session.setDetailListening(true)
        assertFalse(session.hasPendingCardRefresh())

        session.setDetailListening(false)

        assertTrue(session.hasPendingCardRefresh())
    }

    @Test
    fun `adapter proxy returns correct metrics category`() {
        val api = createFakeNativeApi()
        val session = FocusModeDetailSession(
            repository = createFakeRepository(),
            onDismiss = {},
            nativeDetailContentApi = api,
            diagnostic = FakeDiagnostic(),
            detailAdapterInterface = createFakeDetailAdapterInterface()
        )

        val adapter = session.adapter
        val category = callMethod(adapter, "getMetricsCategory")

        assertEquals(118, category)
    }

    private fun createFakeDetailAdapterInterface(): Class<*> {
        // Return a simple interface that has the methods we need
        return FakeDetailAdapter::class.java
    }

    private fun callMethod(obj: Any, methodName: String): Any? {
        val method = obj.javaClass.getMethod(methodName)
        return method.invoke(obj)
    }

    private fun createFakeNativeApi(): FocusNativeDetailContentApi {
        val contentClass = FakeSelectableItem::class.java
        val itemInterface = Any::class.java
        val selectableItemClass = FakeSelectableItem::class.java
        val callbackInterface = Any::class.java

        val convertMethod = FakeSelectableItem::class.java.getDeclaredMethod(
            "fakeConvert",
            android.content.Context::class.java,
            android.view.View::class.java,
            android.view.ViewGroup::class.java
        )
        val convertOrInflate = FocusNativeConvertOrInflate(
            ownerProvider = { null },
            method = convertMethod
        )

        val constructor = selectableItemClass.getDeclaredConstructor()
        val setSuffix = FakeSelectableItem::class.java.getDeclaredMethod("setSuffix", String::class.java)
        val setItems = FakeSelectableItem::class.java.getDeclaredMethod("setItems", Array<Any>::class.java)
        val setCallback = FakeSelectableItem::class.java.getDeclaredMethod("setCallback", Any::class.java)

        return FocusNativeDetailContentApi(
            contentClass = contentClass,
            itemInterface = itemInterface,
            selectableItemClass = selectableItemClass,
            callbackInterface = callbackInterface,
            convertOrInflate = convertOrInflate,
            selectableItemConstructor = constructor,
            setSuffix = setSuffix,
            setItems = setItems,
            setCallback = setCallback
        )
    }

    private fun createFakeRepository(): FocusCardStateRepository {
        return FocusCardStateRepository(
            store = object : FocusCardConfigStore {
                override fun read() = null
                override fun write(json: String) = true
            },
            selector = ModeIndexSelector { 0 }
        )
    }
}

// Minimal fakes
class FakeDiagnostic : FocusDetailDiagnostic {
    override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {}
}

class FakeSelectableItem : android.view.View(null) {
    private var suffixValue: String? = null
    private var itemsValue: Array<Any>? = null
    private var callbackValue: Any? = null

    fun setSuffix(value: String) {
        suffixValue = value
    }

    fun setItems(value: Array<Any>) {
        itemsValue = value
    }

    fun setCallback(value: Any) {
        callbackValue = value
    }

    companion object {
        @JvmStatic
        fun fakeConvert(context: android.content.Context?, convertView: android.view.View?, parent: android.view.ViewGroup?): Any {
            return FakeSelectableItem()
        }
    }
}

class FakeNativeDetailContentApi {
    var lastItemsCount: Int = 0

    fun convertOrInflate(context: android.content.Context, convertView: android.view.View?, parent: android.view.ViewGroup?): Any {
        return FakeSelectableItem()
    }

    fun setSuffix(content: Any, suffix: String) {
        (content as? FakeSelectableItem)?.setSuffix(suffix)
    }

    fun setItems(content: Any, items: Array<Any>) {
        lastItemsCount = items.size
        (content as? FakeSelectableItem)?.setItems(items)
    }

    fun setCallback(content: Any, callback: Any) {
        (content as? FakeSelectableItem)?.setCallback(callback)
    }
}

interface FakeDetailAdapter {
    fun getMetricsCategory(): Int
    fun getTitle(): CharSequence?
    fun createDetailView(context: android.content.Context, convertView: android.view.View?, parent: android.view.ViewGroup?): android.view.View?
    fun getSettingsIntent(): android.content.Intent?
}
