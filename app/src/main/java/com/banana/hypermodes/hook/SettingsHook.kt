package com.banana.hypermodes.hook

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Injects a "模式" (Modes) entry into the HyperOS Settings homepage,
 * positioned directly above 显示和触控 (display), mimicking the Pixel entry
 * (title 模式, summary 勿扰、睡眠、自定义模式). Tapping it opens HyperModes.
 *
 * HyperOS/MIUI's homepage is com.android.settings.MiuiSettings, whose
 * updateHeaderList(List) builds a header list — the same mechanism the
 * reference module (HyperCeiler) uses. We insert a
 * PreferenceActivity$Header with our id, icon, title, summary and launch
 * intent, before the display header.
 */
class SettingsHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val miuiSettings = try {
            classLoader.loadClass(MIUI_SETTINGS)
        } catch (t: Throwable) {
            log("MiuiSettings not found, skipping header hook")
            return
        }
        val updateHeaderList = try {
            miuiSettings.getDeclaredMethod("updateHeaderList", List::class.java)
                .apply { isAccessible = true }
        } catch (t: Throwable) {
            log("updateHeaderList not found: ${t.message}")
            return
        }
        module.hook(updateHeaderList)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val activity = chain.thisObject as? android.app.Activity
                        @Suppress("UNCHECKED_CAST")
                        val headers = chain.getArg(0) as? MutableList<Any>
                        if (activity != null && headers != null) {
                            injectHeader(activity, headers, classLoader)
                        }
                    } catch (t: Throwable) {
                        log("header injection failed: $t")
                    }
                    return result
                }
            })
        log("MiuiSettings.updateHeaderList hooked")
    }

    /** Stock Modes glyph from Settings' own resources.
     * ic_homepage_modes wraps the glyph in a pink AdaptiveIconShapeDrawable
     * background; ic_zen_priority_modes_expressive is the same glyph bare. */
    private fun modesIconRes(context: Context): Int {
        for (name in listOf(
            "ic_zen_priority_modes_expressive",
            "ic_do_not_disturb_mode_settings",
            "ic_homepage_modes"
        )) {
            val id = context.resources.getIdentifier(name, "drawable", Protocol.SETTINGS_PACKAGE)
            if (id != 0) return id
        }
        return 0
    }

    private fun injectHeader(context: Context, headers: MutableList<Any>, classLoader: ClassLoader) {
        // Duplicate guard — updateHeaderList runs on every resume.
        for (h in headers) {
            if (getLongField(h, "id") == OUR_HEADER_ID) return
        }

        val header = classLoader.loadClass(MIUI_HEADER)
            .getDeclaredConstructor().apply { isAccessible = true }.newInstance()

        setField(header, "id", OUR_HEADER_ID)
        // The homepage renders icons via ImageView.setImageResource resolved
        // against SETTINGS' own resource table, so only a Settings drawable id
        // works here. ic_homepage_modes is the stock Modes tile's own icon.
        setField(header, "iconRes", modesIconRes(context))
        setField(header, "title", modesTitle(context))
        setField(header, "summary", modesSummary(context))
        setField(header, "intent", Intent().apply {
            setClassName(Protocol.MODULE_PACKAGE, MAIN_ACTIVITY)
            putExtra("isDisplayHomeAsUpEnabled", true)
        })
        setField(header, "extras", Bundle().apply {
            // Hidden API — accessible at runtime thanks to LSPosed.
            putParcelableArrayList("header_user", arrayListOf(userHandleOf(0)))
        })

        val pos = findDisplayHeaderPosition(context, headers)
        if (pos >= 0) headers.add(pos, header) else headers.add(header)
        log("modes header injected at ${if (pos >= 0) pos else headers.size - 1}")
    }

    /** Position of the display header (we insert before it = above 显示和触控). */
    private fun findDisplayHeaderPosition(context: Context, headers: List<Any>): Int {
        // Match by resource id first, then by localized title text.
        val displayIds = listOf("display_settings", "display", "display_and_touch")
            .mapNotNull { name ->
                context.resources.getIdentifier(name, "id", Protocol.SETTINGS_PACKAGE)
                    .takeIf { it != 0 }?.toLong()
            }
        val displayTitles = listOf("display_settings", "display", "display_and_touch")
            .mapNotNull { name ->
                val id = context.resources.getIdentifier(name, "string", Protocol.SETTINGS_PACKAGE)
                if (id != 0) context.resources.getString(id) else null
            }

        for (i in headers.indices) {
            val head = headers[i]
            if (displayIds.contains(getLongField(head, "id"))) return i
            val title = getField(head, "title")?.toString()
            if (title != null && displayTitles.contains(title)) return i
        }
        return -1
    }

    /** 模式 — reuse Settings' own localized zen_modes_list_title when present. */
    private fun modesTitle(context: Context): CharSequence {
        val id = context.resources.getIdentifier("zen_modes_list_title", "string", Protocol.SETTINGS_PACKAGE)
        return if (id != 0) context.resources.getString(id) else "模式"
    }

    /** 勿扰、睡眠、自定义模式 — our own localized string via package context. */
    private fun modesSummary(context: Context): CharSequence = try {
        val ourContext = context.createPackageContext(Protocol.MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
        val id = ourContext.resources.getIdentifier("modes_settings_summary", "string", Protocol.MODULE_PACKAGE)
        if (id != 0) ourContext.resources.getString(id) else DEFAULT_SUMMARY
    } catch (t: Throwable) {
        DEFAULT_SUMMARY
    }

    private fun getLongField(target: Any, name: String): Long =
        (getField(target, name) as? Long) ?: -1L

    /** UserHandle.of(userId) — @hide API, called reflectively (LSPosed lifts the hidden-API restriction). */
    private fun userHandleOf(userId: Int): android.os.UserHandle =
        android.os.UserHandle::class.java
            .getMethod("of", Int::class.javaPrimitiveType)
            .invoke(null, userId) as android.os.UserHandle

    private fun getField(target: Any, name: String): Any? {
        var cls: Class<*>? = target.javaClass
        while (cls != null) {
            try {
                val f = cls.getDeclaredField(name)
                f.isAccessible = true
                return f.get(target)
            } catch (e: NoSuchFieldException) {
                cls = cls.superclass
            }
        }
        return null
    }

    private fun setField(target: Any, name: String, value: Any?) {
        var cls: Class<*>? = target.javaClass
        while (cls != null) {
            try {
                val f = cls.getDeclaredField(name)
                f.isAccessible = true
                f.set(target, value)
                return
            } catch (e: NoSuchFieldException) {
                cls = cls.superclass
            }
        }
        throw NoSuchFieldException("${target.javaClass.name}#$name")
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val MIUI_SETTINGS = "com.android.settings.MiuiSettings"
        private const val MIUI_HEADER =
            "com.android.settingslib.miuisettings.preference.PreferenceActivity\$Header"
        private const val MAIN_ACTIVITY = "com.banana.hypermodes.ui.MainActivity"
        // Unique id for our injected header (doubles as the duplicate guard).
        private const val OUR_HEADER_ID = 845_214L
        private const val DEFAULT_SUMMARY = "勿扰、睡眠、自定义模式"
    }
}
