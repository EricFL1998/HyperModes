package com.banana.hypermodes.hook

import android.view.View
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ControlCenterNativeDetailFeatureSetTest {
    @Test
    fun `bound row resolver reads itemView field`() {
        val row = View(null)
        assertSame(row, ControlCenterCardHook.resolveBoundItemView(FieldHolder(row)))
    }

    @Test
    fun `bound row resolver falls back to getItemView`() {
        val row = View(null)
        assertSame(row, ControlCenterCardHook.resolveBoundItemView(MethodHolder(row)))
        assertNull(ControlCenterCardHook.resolveBoundItemView(FieldHolder("not a view")))
    }

    @Test
    fun `OS4 detail transition only reports hidden when adapter field is null`() {
        val field = DetailTransition::class.java.getDeclaredField("adapter")
        assertFalse(ControlCenterCardHook.isClosingDetailInvocation(DetailTransition(Any()), field))
        assertTrue(ControlCenterCardHook.isClosingDetailInvocation(DetailTransition(null), field))
    }

    private class FieldHolder(@JvmField val itemView: Any?)
    private class MethodHolder(private val row: View) { fun getItemView(): View = row }
    private class DetailTransition(@JvmField val adapter: Any?)
}
