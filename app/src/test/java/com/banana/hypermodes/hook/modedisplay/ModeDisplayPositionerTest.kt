package com.banana.hypermodes.hook.modedisplay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModeDisplayPositionerTest {

    @Test
    fun `screen bounds convert to native zoom endpoint in host space`() {
        val result = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 420, y = 2100, width = 240, height = 56),
            host = DisplayBounds(x = 0, y = 100, width = 1080, height = 2300)
        )

        // relative = (420, 2000); pivot = (540, 920); endpoint = pivot + 0.95 * (rel - pivot)
        assertEquals(DisplayPlacement(x = 426, y = 1946, width = 240, height = 56), result)
    }

    @Test
    fun `host offset is removed before zoom compensation`() {
        val result = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 160, y = 360, width = 120, height = 40),
            host = DisplayBounds(x = 100, y = 200, width = 500, height = 500)
        )

        // relative = (60, 160); pivot = (250, 200)
        assertEquals(DisplayPlacement(x = 70, y = 162, width = 120, height = 40), result)
    }

    @Test
    fun `content at the pivot keeps its position and content below it moves up`() {
        val atPivot = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 540, y = 920, width = 100, height = 40),
            host = DisplayBounds(x = 0, y = 0, width = 1080, height = 2300)
        )
        assertEquals(DisplayPlacement(x = 540, y = 920, width = 100, height = 40), atPivot)

        val belowPivot = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 540, y = 2000, width = 100, height = 40),
            host = DisplayBounds(x = 0, y = 0, width = 1080, height = 2300)
        )
        // y = 920 + 0.95 * (2000 - 920) = 1946: shifted up like the native shrink
        assertEquals(1946, belowPivot?.y)
    }

    @Test
    fun `missing zero-sized and off-host bounds are rejected`() {
        assertNull(ModeDisplayPositioner.calculate(null, DisplayBounds(0, 0, 1080, 2400)))
        assertNull(ModeDisplayPositioner.calculate(
            DisplayBounds(0, 0, 0, 40),
            DisplayBounds(0, 0, 1080, 2400)
        ))
        assertNull(ModeDisplayPositioner.calculate(
            DisplayBounds(-1, 200, 100, 40),
            DisplayBounds(0, 0, 1080, 2400)
        ))
        assertNull(ModeDisplayPositioner.calculate(
            DisplayBounds(1000, 200, 100, 40),
            DisplayBounds(0, 0, 1080, 2400)
        ))
    }
}
