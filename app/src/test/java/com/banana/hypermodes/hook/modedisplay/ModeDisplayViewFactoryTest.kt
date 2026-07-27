package com.banana.hypermodes.hook.modedisplay

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ModeDisplayViewFactoryTest {
    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun `factory creates the shared two-child native-style row`() {
        val view = ModeDisplayViewFactory.create(context)

        assertEquals(LinearLayout.HORIZONTAL, view.orientation)
        assertEquals(Gravity.CENTER, view.gravity)
        assertEquals(2, view.childCount)
        assertTrue(view.getChildAt(0) is ImageView)
        assertTrue(view.getChildAt(1) is TextView)
        assertEquals(Color.WHITE, (view.getChildAt(1) as TextView).currentTextColor)
        assertEquals(View.GONE, view.visibility)
        assertEquals(0f, view.translationX)
        assertEquals(0f, view.translationY)
        assertEquals(1f, view.alpha)
        assertEquals(1f, view.scaleX)
        assertEquals(1f, view.scaleY)
    }

    @Test
    fun `binding active state shows name and module drawable`() {
        val view = ModeDisplayViewFactory.create(context)

        ModeDisplayViewFactory.bind(
            context,
            view,
            ModeDisplayState(name = "Work", iconResName = "ic_stat_work")
        )

        val icon = view.getChildAt(0) as ImageView
        val label = view.getChildAt(1) as TextView
        assertEquals(View.VISIBLE, view.visibility)
        assertEquals(View.VISIBLE, icon.visibility)
        assertNotNull(icon.drawable)
        assertEquals("Work", label.text.toString())
    }

    @Test
    fun `missing drawable keeps name but hides icon`() {
        val view = ModeDisplayViewFactory.create(context)

        ModeDisplayViewFactory.bind(
            context,
            view,
            ModeDisplayState(name = "Unknown", iconResName = "missing_drawable")
        )

        assertEquals(View.VISIBLE, view.visibility)
        assertEquals(View.GONE, view.getChildAt(0).visibility)
        assertEquals("Unknown", (view.getChildAt(1) as TextView).text.toString())
    }

    @Test
    fun `binding no state hides the complete row`() {
        val view = ModeDisplayViewFactory.create(context)
        ModeDisplayViewFactory.bind(
            context,
            view,
            ModeDisplayState(name = "Work", iconResName = "ic_stat_work")
        )

        ModeDisplayViewFactory.bind(context, view, null)

        assertEquals(View.GONE, view.visibility)
        assertEquals("", (view.getChildAt(1) as TextView).text.toString())
    }
}
