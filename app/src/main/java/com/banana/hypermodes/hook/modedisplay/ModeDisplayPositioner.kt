package com.banana.hypermodes.hook.modedisplay

import kotlin.math.roundToInt

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
    // Native Full-AOD shrink constants (KeyguardPanelViewController.linkageViewAnim):
    // animation views settle at wallpaperScale - 0.05 = 0.95 around a pivot at
    // (0.5W, 0.4H) of the lock screen size.
    private const val FULL_AOD_ZOOM_SCALE = 0.95f
    private const val PIVOT_X_RATIO = 0.5f
    private const val PIVOT_Y_RATIO = 0.4f

    /** Lockscreen bounds converted to host-relative coordinates, unscaled. */
    fun calculateRaw(
        lockscreen: DisplayBounds?,
        host: DisplayBounds?
    ): DisplayPlacement? {
        val (relativeX, relativeY) = validatedRelative(lockscreen, host) ?: return null
        return DisplayPlacement(
            x = relativeX,
            y = relativeY,
            width = lockscreen!!.width,
            height = lockscreen.height
        )
    }

    /**
     * Where the native shrink animation carries lockscreen content:
     * pivot + 0.95 * (position - pivot), in host-relative coordinates.
     */
    fun calculate(
        lockscreen: DisplayBounds?,
        host: DisplayBounds?
    ): DisplayPlacement? {
        val (relativeX, relativeY) = validatedRelative(lockscreen, host) ?: return null
        val pivotX = host!!.width * PIVOT_X_RATIO
        val pivotY = host.height * PIVOT_Y_RATIO
        return DisplayPlacement(
            x = (pivotX + FULL_AOD_ZOOM_SCALE * (relativeX - pivotX)).roundToInt(),
            y = (pivotY + FULL_AOD_ZOOM_SCALE * (relativeY - pivotY)).roundToInt(),
            width = lockscreen!!.width,
            height = lockscreen.height
        )
    }

    private fun validatedRelative(
        lockscreen: DisplayBounds?,
        host: DisplayBounds?
    ): Pair<Int, Int>? {
        if (lockscreen == null || host == null) return null
        if (lockscreen.width <= 0 || lockscreen.height <= 0) return null
        if (host.width <= 0 || host.height <= 0) return null

        val relativeX = lockscreen.x - host.x
        val relativeY = lockscreen.y - host.y
        if (relativeX < 0 || relativeY < 0) return null
        if (relativeX + lockscreen.width > host.width) return null
        if (relativeY + lockscreen.height > host.height) return null
        return relativeX to relativeY
    }
}
