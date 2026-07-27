package com.banana.hypermodes.hook.modedisplay

data class DisplayBounds(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

data class DisplayPlacement(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

object ModeDisplayPositioner {
    fun calculate(
        lockscreen: DisplayBounds?,
        host: DisplayBounds?
    ): DisplayPlacement? {
        if (lockscreen == null || host == null) return null
        if (lockscreen.width <= 0 || lockscreen.height <= 0) return null
        if (host.width <= 0 || host.height <= 0) return null

        val relativeX = lockscreen.x - host.x
        val relativeY = lockscreen.y - host.y
        if (relativeX < 0 || relativeY < 0) return null
        if (relativeX + lockscreen.width > host.width) return null
        if (relativeY + lockscreen.height > host.height) return null

        return DisplayPlacement(
            x = relativeX,
            y = relativeY,
            width = lockscreen.width,
            height = lockscreen.height
        )
    }
}
