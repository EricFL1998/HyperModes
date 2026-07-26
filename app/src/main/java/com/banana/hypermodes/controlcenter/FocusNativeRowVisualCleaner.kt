package com.banana.hypermodes.controlcenter

import android.view.View
import java.lang.reflect.Method

object FocusNativeRowVisualCleaner {
    private const val MI_BLUR_COMPAT_CLASS = "com.miui.systemui.util.MiBlurCompat"
    private const val FOLME_CLASS = "miuix.animation.Folme"

    fun clear(row: View, classLoader: ClassLoader?) {
        row.isSelected = false
        row.isActivated = false
        row.background = null
        row.foreground = null
        val api = RowVisualReflectionApi.from(classLoader)
        runCatching { api.setBlurMode?.invoke(null, 0, row) }
        runCatching { api.clearBlendColor?.invoke(null, row) }
        runCatching { api.cleanFolme?.invoke(null, row) }
    }

    private data class RowVisualReflectionApi(
        val setBlurMode: Method?,
        val clearBlendColor: Method?,
        val cleanFolme: Method?
    ) {
        companion object {
            fun from(classLoader: ClassLoader?): RowVisualReflectionApi {
                val miBlurCompat = runCatching {
                    classLoader?.loadClass(MI_BLUR_COMPAT_CLASS)
                }.getOrNull()
                val setBlurMode = runCatching {
                    miBlurCompat?.getDeclaredMethod(
                        "setMiViewBlurModeCompat",
                        Integer.TYPE,
                        View::class.java
                    )?.apply { isAccessible = true }
                }.getOrNull()
                val clearBlendColor = runCatching {
                    miBlurCompat?.getDeclaredMethod(
                        "clearMiBackgroundBlendColorCompat",
                        View::class.java
                    )?.apply { isAccessible = true }
                }.getOrNull()
                val folme = runCatching {
                    classLoader?.loadClass(FOLME_CLASS)
                }.getOrNull()
                val cleanFolme = runCatching {
                    folme?.getDeclaredMethod("clean", View::class.java)?.apply { isAccessible = true }
                }.getOrNull()
                return RowVisualReflectionApi(
                    setBlurMode = setBlurMode,
                    clearBlendColor = clearBlendColor,
                    cleanFolme = cleanFolme
                )
            }
        }
    }
}
