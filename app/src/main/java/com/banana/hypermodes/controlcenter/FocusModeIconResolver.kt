package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.systemserver.config.ModeConfig

internal class FocusModeIconResolver(
    private val moduleContext: Context
) {
    fun resolve(mode: ModeConfig): Drawable {
        return resolve(icon = mode.icon, statusIcon = mode.statusIcon)
    }

    fun resolve(icon: String?, statusIcon: String? = null): Drawable {
        return statusIcon
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
            ?.let(::drawableByName)
            ?: drawableByName(mappedIconName(icon))
            ?: drawableFromModule(R.drawable.ic_stat_zen)
            ?: ColorDrawable(Color.TRANSPARENT)
    }

    private fun mappedIconName(icon: String?): String {
        return try {
            ModeIconMapper.getStatusBarIcon(icon ?: "")
        } catch (_: Throwable) {
            "ic_stat_zen"
        }
    }

    private fun drawableByName(name: String): Drawable? {
        val resId = drawableId(name)
        return drawableFromModule(resId)
    }

    private fun drawableId(name: String): Int {
        return try {
            moduleContext.resources?.getIdentifier(name, "drawable", moduleContext.packageName) ?: 0
        } catch (_: Throwable) {
            0
        }
    }

    private fun drawableFromModule(resId: Int): Drawable? {
        if (resId == 0) return null
        return try {
            moduleContext.resources?.getDrawable(resId, null)
        } catch (_: Throwable) {
            null
        }
    }
}
