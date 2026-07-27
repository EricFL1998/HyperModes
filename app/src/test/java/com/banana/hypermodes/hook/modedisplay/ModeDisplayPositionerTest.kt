package com.banana.hypermodes.hook.modedisplay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModeDisplayPositionerTest {

    @Test
    fun `screen bounds convert to host-relative placement`() {
        val result = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 420, y = 2100, width = 240, height = 56),
            host = DisplayBounds(x = 0, y = 100, width = 1080, height = 2300)
        )

        assertEquals(DisplayPlacement(x = 420, y = 2000, width = 240, height = 56), result)
    }

    @Test
    fun `host offset is removed from both axes`() {
        val result = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 160, y = 360, width = 120, height = 40),
            host = DisplayBounds(x = 100, y = 200, width = 500, height = 500)
        )

        assertEquals(DisplayPlacement(x = 60, y = 160, width = 120, height = 40), result)
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
