package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusCardTileClassesTest {

    @Test
    fun `resolve loads native detail content from the SystemUI class loader`() {
        val systemUiLoader = RecordingClassLoader(
            mapOf(
                QSTILE_CLASS to FakeTile::class.java,
                BOOLEAN_STATE_CLASS to FakeBooleanState::class.java,
                DETAIL_ADAPTER_CLASS to FakeDetailAdapter::class.java,
                QS_DETAIL_CONTENT_CLASS to FakeQsDetailContent::class.java,
                DRAWABLE_ICON_CLASS to FakeDrawableIcon::class.java
            )
        )

        val classes = FocusCardTileClasses.resolve(systemUiLoader)

        assertSame(FakeQsDetailContent::class.java, classes.nativeDetailContentApi?.contentClass)
        assertTrue(systemUiLoader.requests.contains(QS_DETAIL_CONTENT_CLASS))
        assertTrue(systemUiLoader.requests.contains(QSTILE_CLASS))
        assertTrue(systemUiLoader.requests.contains(DETAIL_ADAPTER_CLASS))
    }

    @Test
    fun `native detail resolution loads binary named nested classes when reflection does not enumerate them`() {
        val binaryName = BinaryNamedQsDetailContent::class.java.name
        val systemUiLoader = RecordingClassLoader(
            mapOf(
                QS_DETAIL_CONTENT_CLASS to BinaryNamedQsDetailContent::class.java,
                "$binaryName\$Item" to BinaryNamedItem::class.java,
                "$binaryName\$SelectableItem" to BinaryNamedSelectableItem::class.java,
                "$binaryName\$Callback" to BinaryNamedCallback::class.java,
                "$binaryName\$Companion" to BinaryNamedCompanion::class.java
            )
        )

        var failure: Throwable? = null
        val api = FocusNativeDetailContentResolver.fromClassLoader(systemUiLoader) { throwable ->
            failure = throwable
        }

        assertNotNull(failure?.stackTraceToString(), api)
        assertSame(BinaryNamedItem::class.java, api?.itemInterface)
        assertSame(BinaryNamedSelectableItem::class.java, api?.selectableItemClass)
        assertSame(BinaryNamedCallback::class.java, api?.callbackInterface)
        assertTrue(systemUiLoader.requests.contains("$binaryName\$Item"))
    }

    @Test
    fun `native detail resolution reports the concrete class loading failure`() {
        val missingLoader = RecordingClassLoader(emptyMap())
        var failure: Throwable? = null

        val api = FocusNativeDetailContentResolver.fromClassLoader(missingLoader) { throwable ->
            failure = throwable
        }

        assertEquals(null, api)
        assertNotNull(failure)
        assertTrue(failure is ClassNotFoundException)
        assertTrue(failure?.message.orEmpty().contains(QS_DETAIL_CONTENT_CLASS))
    }

    @Test
    fun `native detail resolution preserves the concrete incompatible API failure`() {
        val incompatibleLoader = RecordingClassLoader(
            mapOf(QS_DETAIL_CONTENT_CLASS to IncompatibleQsDetailContent::class.java)
        )
        var failure: Throwable? = null

        val api = FocusNativeDetailContentResolver.fromClassLoader(incompatibleLoader) { throwable ->
            failure = throwable
        }

        assertEquals(null, api)
        assertTrue(failure is ClassNotFoundException)
        assertTrue(failure?.message.orEmpty().contains("\$Item"))
    }

    private class RecordingClassLoader(
        private val classes: Map<String, Class<*>>
    ) : ClassLoader(null) {
        val requests = mutableListOf<String>()

        override fun loadClass(name: String): Class<*> {
            requests += name
            return classes[name] ?: throw ClassNotFoundException(name)
        }
    }

    private interface FakeTile
    private class FakeBooleanState
    private class FakeDrawableIcon(drawable: Drawable)
    private interface FakeDetailAdapter
    private class IncompatibleQsDetailContent

    private interface BinaryNamedItem

    private class BinaryNamedSelectableItem : BinaryNamedItem

    private interface BinaryNamedCallback

    private class BinaryNamedCompanion {
        fun convertOrInflate(
            context: Context,
            convertView: View?,
            parent: ViewGroup?
        ): BinaryNamedQsDetailContent {
            return convertView as? BinaryNamedQsDetailContent ?: BinaryNamedQsDetailContent(context)
        }
    }

    private class BinaryNamedQsDetailContent(context: Context?) : View(context) {
        fun setSuffix(suffix: String) = Unit

        fun setItems(vararg items: BinaryNamedItem) = Unit

        fun setCallback(callback: BinaryNamedCallback) = Unit

        companion object Holder {
            @JvmField
            val Companion: BinaryNamedCompanion = BinaryNamedCompanion()
        }
    }

    private class FakeQsDetailContent(context: Context?) : View(context) {
        interface Item

        class SelectableItem : Item

        interface Callback

        fun setSuffix(suffix: String) = Unit

        fun setItems(vararg items: Item) = Unit

        fun setCallback(callback: Callback) = Unit

        companion object {
            @JvmStatic
            fun convertOrInflate(context: Context, convertView: View?, parent: ViewGroup?): FakeQsDetailContent {
                return convertView as? FakeQsDetailContent ?: FakeQsDetailContent(context)
            }
        }
    }

    companion object {
        private const val QSTILE_CLASS = "com.android.systemui.plugins.qs.QSTile"
        private const val BOOLEAN_STATE_CLASS = "com.android.systemui.plugins.qs.QSTile\$BooleanState"
        private const val DRAWABLE_ICON_CLASS = "com.android.systemui.qs.tileimpl.QSTileImpl\$DrawableIcon"
        private const val DETAIL_ADAPTER_CLASS = "com.android.systemui.plugins.qs.DetailAdapter"
        private const val QS_DETAIL_CONTENT_CLASS = "com.android.systemui.qs.QSDetailContent"
    }
}
