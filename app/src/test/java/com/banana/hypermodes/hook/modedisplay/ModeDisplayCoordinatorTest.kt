package com.banana.hypermodes.hook.modedisplay

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import java.util.IdentityHashMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
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

    @Test
    fun `full aod start adds one positioned tagged child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        val params = fullAod.layoutParams as FrameLayout.LayoutParams
        assertEquals(420, params.leftMargin)
        assertEquals(2000, params.topMargin)
        assertEquals(240, params.width)
        assertEquals(56, params.height)
        assertEquals("Work", (fullAod.getChildAt(1) as TextView).text.toString())
        assertEquals(View.VISIBLE, fullAod.visibility)
    }

    @Test
    fun `full aod lifecycle leaves lockscreen animation properties to its native parent`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        assertEquals(View.VISIBLE, lockscreen.visibility)
        assertEquals(1f, lockscreen.alpha)
        assertEquals(0f, lockscreen.translationY)
    }

    @Test
    fun `repeated start reuses the same child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)
        val first = root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG)
        coordinator.onFullAodStarted(root, isFullAod = true)
        val second = root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG)

        assertSame(first, second)
        assertEquals(1, root.childCount)
    }

    @Test
    fun `ordinary aod flag does not inject and stop removes active copy`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = false)
        assertNull(root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG))

        coordinator.onFullAodStarted(root, isFullAod = true)
        coordinator.onFullAodStopped()
        assertNull(root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG))
    }

    @Test
    fun `refresh updates both copies and hides them when mode stops`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)
        coordinator.onFullAodStarted(root, isFullAod = true)
        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)

        state = ModeDisplayState("Gaming", "ic_stat_game")
        coordinator.refresh(context)
        assertEquals("Gaming", (lockscreen.getChildAt(1) as TextView).text.toString())
        assertEquals("Gaming", (fullAod.getChildAt(1) as TextView).text.toString())

        state = null
        coordinator.refresh(context)
        assertEquals(View.GONE, lockscreen.visibility)
        assertEquals(View.GONE, fullAod.visibility)
    }

    @Test
    fun `missing coordinates never expose an unpositioned child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        assertEquals(View.INVISIBLE, fullAod.visibility)
    }

    @Test
    fun `child keeps neutral transforms and inherits movement from root only`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)
        coordinator.onFullAodStarted(root, isFullAod = true)
        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)

        root.translationY = 10f
        root.alpha = 0.6f
        root.scaleX = 0.95f
        root.scaleY = 0.95f

        assertSame(root, fullAod.parent)
        assertEquals(0f, fullAod.translationY)
        assertEquals(1f, fullAod.alpha)
        assertEquals(1f, fullAod.scaleX)
        assertEquals(1f, fullAod.scaleY)
    }

    @Test
    fun `same lockscreen temporary missing bounds uses cached coordinates`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)
        bounds.remove(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        val params = fullAod.layoutParams as FrameLayout.LayoutParams
        assertEquals(View.VISIBLE, fullAod.visibility)
        assertEquals(420, params.leftMargin)
        assertEquals(2000, params.topMargin)
        assertEquals(240, params.width)
        assertEquals(56, params.height)
    }

    @Test
    fun `replacement lockscreen without bounds does not reuse stale coordinates`() {
        val coordinator = coordinator()
        val firstLockscreen = ModeDisplayViewFactory.create(context)
        val replacementLockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[firstLockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(firstLockscreen)
        coordinator.attachLockscreenDisplay(replacementLockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        assertEquals(View.INVISIBLE, fullAod.visibility)
    }

    @Test
    fun `replacing lockscreen view removes old layout listener so detached view cannot overwrite bounds`() {
        val coordinator = coordinator()
        val firstLockscreen = ModeDisplayViewFactory.create(context)
        val replacementLockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[firstLockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[replacementLockscreen] = DisplayBounds(100, 1900, 200, 50)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)

        coordinator.attachLockscreenDisplay(firstLockscreen)
        coordinator.attachLockscreenDisplay(replacementLockscreen)

        // Simulate the detached first lockscreen laying out with new bogus bounds.
        bounds[firstLockscreen] = DisplayBounds(999, 9999, 999, 99)
        firstLockscreen.layout(0, 0, 999, 99)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        val params = fullAod.layoutParams as FrameLayout.LayoutParams
        assertEquals(100, params.leftMargin)
        assertEquals(1800, params.topMargin)
        assertEquals(200, params.width)
        assertEquals(50, params.height)
    }

    @Test
    fun `detaching root before pre draw removes pending unpositioned child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        coordinator.attachLockscreenDisplay(lockscreen)
        coordinator.onFullAodStarted(root, isFullAod = true)
        assertNotNull(root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG))

        root.dispatchDetachedFromWindowForTest()

        assertNull(root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG))
        assertEquals(0, root.childCount)
    }

    private fun View.dispatchDetachedFromWindowForTest() {
        val method = View::class.java.getDeclaredMethod("dispatchDetachedFromWindow")
        method.isAccessible = true
        method.invoke(this)
    }
}
