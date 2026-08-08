package com.banana.hypermodes.data

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import com.banana.hypermodes.protocol.Protocol

/**
 * Ask the system_server bridge to capture the current lock-screen style JSON +
 * wallpaper files into a snapshot dir readable by the App. Used after the user
 * edits wallpaper in the official ThemeManager UI, so HyperModes can store the
 * resulting set in a mode.
 *
 * onResult runs on the main thread exactly once; null means the bridge is
 * unavailable (module disabled or an older build).
 */
object WallpaperSnapshotBridge {

    /**
     * Capture the current lock-screen style JSON + wallpaper files into a shared
     * preview dir (never overwrites a mode's saved wallpaper). Used when entering
     * the detail page so unconfigured previews show the real system wallpaper
     * and lock-screen style.
     */
    fun captureCurrent(context: Context, onResult: (WallpaperSet?) -> Unit) {
        captureInternal(context, modeId = "preview", previewOnly = true, onResult)
    }

    /** Capture into a mode-specific dir; used after the user edits wallpaper in
     *  the official UI so the resulting set can be stored in the mode. */
    fun capture(context: Context, modeId: String, onResult: (WallpaperSet?) -> Unit) {
        captureInternal(context, modeId = modeId, previewOnly = false, onResult)
    }

    private fun captureInternal(
        context: Context,
        modeId: String,
        previewOnly: Boolean,
        onResult: (WallpaperSet?) -> Unit
    ) {
        val handler = Handler(Looper.getMainLooper())
        var delivered = false
        fun deliver(result: WallpaperSet?) {
            if (delivered) return
            delivered = true
            onResult(result)
        }
        val timeout = Runnable { deliver(null) }
        handler.postDelayed(timeout, 2000)

        val receiver = object : ResultReceiver(handler) {
            override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
                handler.removeCallbacks(timeout)
                deliver(parse(resultData))
            }
        }
        try {
            context.sendBroadcast(
                Intent(Protocol.ACTION_CAPTURE_WALLPAPER_SNAPSHOT).apply {
                    setPackage(Protocol.FRAMEWORK_PACKAGE)
                    putExtra(Protocol.EXTRA_MODE_ID, modeId)
                    putExtra(Protocol.EXTRA_PREVIEW_ONLY, previewOnly)
                    putExtra(Protocol.EXTRA_RESULT_RECEIVER, receiver)
                }
            )
        } catch (t: Throwable) {
            handler.removeCallbacks(timeout)
            deliver(null)
        }
    }

    private fun parse(data: Bundle?): WallpaperSet? {
        if (data == null) return null
        val lockImage = data.getString(Protocol.EXTRA_LOCK_IMAGE_PATH)
        val desktopImage = data.getString(Protocol.EXTRA_DESKTOP_IMAGE_PATH)
        val lockJson = data.getString(Protocol.EXTRA_LOCKSCREEN_JSON)
        if (lockImage == null && desktopImage == null && lockJson == null) return null

        return WallpaperSet(
            lock = if (lockImage != null || lockJson != null) {
                WallpaperItem(
                    imagePath = lockImage,
                    lockscreenJson = lockJson,
                    templateEditorJson = data.getString(Protocol.EXTRA_TEMPLATE_EDITOR_JSON),
                    effectType = if (data.containsKey(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE)) {
                        data.getInt(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE)
                    } else null,
                    which = 2
                )
            } else null,
            desktop = if (desktopImage != null) {
                WallpaperItem(
                    imagePath = desktopImage,
                    scrollEnabled = if (data.containsKey(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED)) {
                        data.getBoolean(Protocol.EXTRA_DESKTOP_SCROLL_ENABLED)
                    } else null,
                    effectType = if (data.containsKey(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE)) {
                        data.getInt(Protocol.EXTRA_WALLPAPER_EFFECT_TYPE)
                    } else null,
                    which = 1
                )
            } else null
        )
    }
}
