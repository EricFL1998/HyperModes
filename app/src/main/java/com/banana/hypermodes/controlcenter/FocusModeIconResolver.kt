package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.systemserver.config.ModeConfig

internal class FocusModeIconResolver(
    private val pluginContext: Context,
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
            ?: drawableFromContexts(R.drawable.ic_stat_zen)
            ?: drawableFromContexts(android.R.drawable.ic_dialog_info)
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
        return drawableFromContexts(resId)
    }

    private fun drawableId(name: String): Int {
        return try {
            val packageName = moduleContext.packageName ?: pluginContext.packageName
            moduleContext.resources?.getIdentifier(name, "drawable", packageName) ?: 0
        } catch (_: Throwable) {
            0
        }
    }

    private fun drawableFromContexts(resId: Int): Drawable? {
        if (resId == 0) return null
        return drawableFromContext(moduleContext, resId) ?: drawableFromContext(pluginContext, resId)
    }

    private fun drawableFromContext(context: Context, resId: Int): Drawable? {
        return try {
            context.resources?.getDrawable(resId, null)
        } catch (_: Throwable) {
            null
        }
    }
}
