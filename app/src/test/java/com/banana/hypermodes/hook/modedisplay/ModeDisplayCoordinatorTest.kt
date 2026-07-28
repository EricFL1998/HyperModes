package com.banana.hypermodes.hook.modedisplay

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import java.util.IdentityHashMap
import kotlin.math.roundToInt
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ModeDisplayCoordinatorTest {
    private val context = RuntimeEnvironment.getApplication()
    private val bounds = IdentityHashMap<View, DisplayBounds>()
    private var state: ModeDisplayState? = ModeDisplayState("Work", "ic_stat_work")

    private fun coordinator() = ModeDisplayCoordinator(
        readState = { state },
        readBounds = { bounds[it] },
        logger = {}
    )

    private fun attachedDisplay(coordinator: ModeDisplayCoordinator): Pair<LinearLayout, LinearLayout> {
        val indication = LinearLayout(context)
        val display = ModeDisplayViewFactory.create(context)
        display.tag = ModeDisplayViewFactory.LOCKSCREEN_TAG
        indication.addView(display, 0)
        bounds[display] = DisplayBounds(420, 2100, 240, 56)
        coordinator.attachLockscreenDisplay(display)
        return indication to display
    }

    private fun panel(width: Int = 1080, height: Int = 2300): FrameLayout {
        val panel = FrameLayout(context)
        panel.layout(0, 0, width, height)
        return panel
    }

    @Test
    fun `full aod start parks the single display in the panel at raw lockscreen position`() {
        val coordinator = coordinator()
        val (indication, display) = attachedDisplay(coordinator)
        val panel = panel()
        coordinator.updatePanelHost(panel, isDepthMode = false)

        coordinator.onFullAodStarted(isFullAod = true)

        assertTrue(coordinator.isParked())
        assertSame(panel, display.parent)
        assertEquals(0, indication.childCount)
        val params = display.layoutParams as FrameLayout.LayoutParams
        assertEquals(420, params.leftMargin)
        assertEquals(2100, params.topMargin)
        assertEquals(240, params.width)
        assertEquals(56, params.height)
        assertEquals(0f, display.translationX)
        assertEquals(0f, display.translationY)
        assertEquals("Work", (display.getChildAt(1) as TextView).text.toString())
    }

    @Test
    fun `stop restores the display to its lockscreen home with original layout params`() {
        val coordinator = coordinator()
        val (indication, display) = attachedDisplay(coordinator)
        val homeParams = display.layoutParams
        coordinator.updatePanelHost(panel(), isDepthMode = false)
        coordinator.onFullAodStarted(isFullAod = true)

        coordinator.onFullAodStopped()

        assertFalse(coordinator.isParked())
        assertSame(indication, display.parent)
        assertEquals(0, indication.indexOfChild(display))
        assertSame(homeParams, display.layoutParams)
        assertEquals(0f, display.translationX)
        assertEquals(0f, display.translationY)
    }

    @Test
    fun `ordinary aod does not park and restores a parked display`() {
        val coordinator = coordinator()
        val (indication, display) = attachedDisplay(coordinator)
        coordinator.updatePanelHost(panel(), isDepthMode = false)

        coordinator.onFullAodStarted(isFullAod = false)
        assertFalse(coordinator.isParked())
        assertSame(indication, display.parent)

        coordinator.onFullAodStarted(isFullAod = true)
        assertTrue(coordinator.isParked())
        coordinator.onFullAodStarted(isFullAod = false)
        assertFalse(coordinator.isParked())
        assertSame(indication, display.parent)
    }

    @Test
    fun `depth mode reassert glides the display to the native shrink endpoint`() {
        val coordinator = coordinator()
        val (_, display) = attachedDisplay(coordinator)
        coordinator.updatePanelHost(panel(), isDepthMode = true)

        coordinator.onFullAodStarted(isFullAod = true)
        // Margins stay at the raw lockscreen position; the glide carries the
        // view to the endpoint like the native shrink does.
        val params = display.layoutParams as FrameLayout.LayoutParams
        assertEquals(420, params.leftMargin)
        assertEquals(2100, params.topMargin)

        coordinator.onFullAodStarted(isFullAod = true) // reassert pins targets
        // endpoint (426, 2041) - raw (420, 2100) = (6, -59)
        assertEquals(6f, display.translationX)
        assertEquals(-59f, display.translationY)
    }

    @Test
    fun `non-depth reassert keeps rendered position on the endpoint while the panel is scaled`() {
        val coordinator = coordinator()
        val (_, display) = attachedDisplay(coordinator)
        val panel = panel()
        coordinator.updatePanelHost(panel, isDepthMode = false)
        coordinator.onFullAodStarted(isFullAod = true)

        // Native shrink: panel scaled to 0.95 around the (0.5W, 0.4H) pivot.
        panel.scaleY = 0.95f
        panel.pivotX = 540f
        panel.pivotY = 920f
        coordinator.onFullAodStarted(isFullAod = true)

        val params = display.layoutParams as FrameLayout.LayoutParams
        assertEquals(426, params.leftMargin) // scaleX untouched at 1.0
        assertEquals((920f + (2041f - 920f) / 0.95f).roundToInt(), params.topMargin)
        val renderedY = 920f + 0.95f * (params.topMargin - 920f)
        assertEquals(2041f, renderedY, 1f)
    }

    @Test
    fun `non-depth reassert after a scale reset lands directly on the endpoint`() {
        val coordinator = coordinator()
        val (_, display) = attachedDisplay(coordinator)
        val panel = panel()
        coordinator.updatePanelHost(panel, isDepthMode = false)
        coordinator.onFullAodStarted(isFullAod = true)

        coordinator.onFullAodStarted(isFullAod = true)

        val params = display.layoutParams as FrameLayout.LayoutParams
        assertEquals(426, params.leftMargin)
        assertEquals(2041, params.topMargin)
    }

    @Test
    fun `missing panel or missing bounds skips parking and keeps the display home`() {
        val coordinator = coordinator()
        val (indication, display) = attachedDisplay(coordinator)

        coordinator.onFullAodStarted(isFullAod = true)
        assertFalse(coordinator.isParked())
        assertSame(indication, display.parent)

        coordinator.updatePanelHost(panel(), isDepthMode = false)
        bounds.remove(display)
        val freshCoordinator = coordinator()
        val freshIndication = LinearLayout(context)
        val freshDisplay = ModeDisplayViewFactory.create(context)
        freshIndication.addView(freshDisplay, 0)
        freshCoordinator.attachLockscreenDisplay(freshDisplay)
        freshCoordinator.updatePanelHost(panel(), isDepthMode = false)
        freshCoordinator.onFullAodStarted(isFullAod = true)
        assertFalse(freshCoordinator.isParked())
        assertSame(freshIndication, freshDisplay.parent)
    }

    @Test
    fun `cached lockscreen bounds are used when live bounds are unavailable at park`() {
        val coordinator = coordinator()
        val (_, display) = attachedDisplay(coordinator)
        coordinator.updatePanelHost(panel(), isDepthMode = false)
        bounds.remove(display)

        coordinator.onFullAodStarted(isFullAod = true)

        assertTrue(coordinator.isParked())
        val params = display.layoutParams as FrameLayout.LayoutParams
        assertEquals(420, params.leftMargin)
        assertEquals(2100, params.topMargin)
    }

    @Test
    fun `replacement lockscreen without bounds does not park on stale coordinates`() {
        val coordinator = coordinator()
        val indication = LinearLayout(context)
        val first = ModeDisplayViewFactory.create(context)
        val replacement = ModeDisplayViewFactory.create(context)
        indication.addView(first, 0)
        bounds[first] = DisplayBounds(420, 2100, 240, 56)
        coordinator.attachLockscreenDisplay(first)
        indication.removeView(first)
        indication.addView(replacement, 0)
        coordinator.attachLockscreenDisplay(replacement)
        coordinator.updatePanelHost(panel(), isDepthMode = false)

        coordinator.onFullAodStarted(isFullAod = true)

        assertFalse(coordinator.isParked())
        assertSame(indication, replacement.parent)
    }

    @Test
    fun `refresh rebinds the single display in any parent and hides it without a mode`() {
        val coordinator = coordinator()
        val (_, display) = attachedDisplay(coordinator)
        coordinator.updatePanelHost(panel(), isDepthMode = false)
        coordinator.onFullAodStarted(isFullAod = true)

        state = ModeDisplayState("Gaming", "ic_stat_game")
        coordinator.refresh(context)
        assertEquals("Gaming", (display.getChildAt(1) as TextView).text.toString())

        state = null
        coordinator.refresh(context)
        assertEquals(View.GONE, display.visibility)
    }

    @Test
    fun `peek display exposes the managed view for duplicate guards`() {
        val coordinator = coordinator()
        assertNull(coordinator.peekDisplay())
        val (_, display) = attachedDisplay(coordinator)
        assertSame(display, coordinator.peekDisplay())
    }
}
