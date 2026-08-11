package com.banana.hypermodes.hook

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.reflect.Proxy

/**
 * Hooks the MIUI Settings app to inject:
 * 1. A "Modes" entry into the main settings screen.
 * 2. A "Modes" toggle into the "Custom Status Bar Icons" screen.
 */
class SettingsHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes"
        private const val PREF_KEY_TOGGLE = "setting_hypermodes"
        private const val SLOT_NAME = "hypermodes"
        private const val MIUI_SETTINGS = "com.android.settings.MiuiSettings"
        private const val MIUI_HEADER = "com.android.settingslib.miuisettings.preference.PreferenceActivity\$Header"
        private const val MAIN_ACTIVITY = "com.banana.hypermodes.ui.MainActivity"
        private const val OUR_HEADER_ID = 0x7F0B0E1DL // Arbitrary unique ID
    }

    fun install(classLoader: ClassLoader) {
        // Install hooks independently - if one fails, the other should still work
        try {
            hookMiuiSettings(classLoader)
        } catch (t: Throwable) {
            log("Failed to install MiuiSettings hook: $t")
        }

        try {
            hookHeaderAdapter(classLoader)
        } catch (t: Throwable) {
            log("Failed to install HeaderAdapter hook: $t")
        }

        try {
            hookIconCustomization(classLoader)
        } catch (t: Throwable) {
            log("Failed to install IconCustomization hook: $t")
        }
    }

    /**
     * Injects a "Modes" entry into the root Settings screen.
     */
    private fun hookMiuiSettings(classLoader: ClassLoader) {
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
                        val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                        val activity = getThisObjectMethod.invoke(chain) as? android.app.Activity
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

    private fun hookHeaderAdapter(classLoader: ClassLoader) {
        val adapterClass = try {
            classLoader.loadClass("com.android.settings.MiuiSettings\$HeaderAdapter")
        } catch (t: Throwable) {
            log("HeaderAdapter class not found, skipping hook")
            return
        }

        val viewHolderClass = try {
            classLoader.loadClass("com.android.settings.MiuiSettings\$HeaderViewHolder")
        } catch (t: Throwable) {
            log("HeaderViewHolder class not found")
            null
        }

        val headerClass = try {
            classLoader.loadClass(MIUI_HEADER)
        } catch (t: Throwable) {
            log("Header class not found")
            null
        }

        if (viewHolderClass == null || headerClass == null) return

        val setIcon = try {
            adapterClass.getDeclaredMethod("setIcon", viewHolderClass, headerClass)
                .apply { isAccessible = true }
        } catch (t: Throwable) {
            log("setIcon method not found: ${t.message}")
            return
        }

        module.hook(setIcon)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val viewHolder = chain.getArg(0)
                        val header = chain.getArg(1)
                        val id = getLongField(header!!, "id")
                        if (id == OUR_HEADER_ID) {
                            val iconView = Reflect.getField(viewHolder!!, "icon") as? android.widget.ImageView
                            if (iconView != null) {
                                val context = iconView.context
                                val modContext = context.createPackageContext(
                                    Protocol.MODULE_PACKAGE,
                                    Context.CONTEXT_IGNORE_SECURITY
                                )
                                val iconId = modContext.resources.getIdentifier(
                                    "ic_homepage_modes", "drawable", Protocol.MODULE_PACKAGE
                                )
                                if (iconId != 0) {
                                    iconView.visibility = android.view.View.VISIBLE
                                    iconView.setImageDrawable(modContext.getDrawable(iconId))
                                }
                            }
                        }
                    } catch (t: Throwable) {
                        // Silent fail
                    }
                    return result
                }
            })
        log("HeaderAdapter.setIcon hooked")
    }

    private fun injectHeader(context: Context, headers: MutableList<Any>, classLoader: ClassLoader) {
        // Duplicate guard
        for (h in headers) {
            if (getLongField(h, "id") == OUR_HEADER_ID) return
        }

        val header = classLoader.loadClass(MIUI_HEADER)
            .getDeclaredConstructor().apply { isAccessible = true }.newInstance()

        Reflect.setObjectField(header, "id", OUR_HEADER_ID)
        // Set iconRes to 0 to prevent native MIUI logic from showing the old icon.
        // The actual icon will be injected via HeaderAdapter.setIcon hook.
        Reflect.setIntField(header, "iconRes", 0)

        Reflect.setObjectField(header, "title", modesTitle(context))
        Reflect.setObjectField(header, "summary", null)
        Reflect.setObjectField(header, "intent", Intent().apply {
            setClassName(Protocol.MODULE_PACKAGE, MAIN_ACTIVITY)
            putExtra("isDisplayHomeAsUpEnabled", true)
        })
        Reflect.setObjectField(header, "extras", Bundle().apply {
            putParcelableArrayList("header_user", arrayListOf(userHandleOf(0)))
        })

        val pos = findLauncherHeaderPosition(context, headers)
        // Insert after "桌面" (launcher) and copy its groupId so the new entry
        // shares the same card/module as "桌面" / "显示".
        if (pos >= 0) {
            val launcherHeader = headers[pos]
            val groupId = getIntField(launcherHeader, "groupId")
            if (groupId > 0) {
                Reflect.setIntField(header, "groupId", groupId)
                log("Copied groupId $groupId from launcher header")
            }
            headers.add(pos + 1, header)
        } else {
            headers.add(header)
        }
        log("modes header injected at ${if (pos >= 0) pos + 1 else headers.size - 1}")
    }

    /** Position of the launcher/home header (we insert after it). */
    private fun findLauncherHeaderPosition(context: Context, headers: List<Any>): Int {
        // Match by resource id first, then by localized title text.
        val launcherIds = listOf("launcher_settings", "home_settings")
            .mapNotNull { name ->
                context.resources.getIdentifier(name, "id", Protocol.SETTINGS_PACKAGE)
                    .takeIf { it != 0 }?.toLong()
            }
        val launcherTitles = listOf("home_title", "launcher_settings")
            .mapNotNull { name ->
                val id = context.resources.getIdentifier(name, "string", Protocol.SETTINGS_PACKAGE)
                if (id != 0) context.resources.getString(id) else null
            }

        for (i in headers.indices) {
            val head = headers[i]
            if (launcherIds.contains(getLongField(head, "id"))) return i
            val title = Reflect.getField(head, "title")?.toString()
            if (title != null && launcherTitles.contains(title)) return i
        }
        return -1
    }

    private fun findDisplayHeaderPosition(context: Context, headers: List<Any>): Int {
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
            val title = Reflect.getField(head, "title")?.toString()
            if (title != null && displayTitles.contains(title)) return i
        }
        return -1
    }

    private fun modesTitle(context: Context): CharSequence {
        // 注入到系统设置首页的入口名称：模式 + 自动化
        return "模式与自动化"
    }

    private fun getLongField(target: Any, name: String): Long =
        (Reflect.getField(target, name) as? Long) ?: -1L

    private fun getIntField(target: Any, name: String): Int =
        (Reflect.getField(target, name) as? Int) ?: 0

    private fun userHandleOf(userId: Int): android.os.UserHandle =
        android.os.UserHandle::class.java
            .getMethod("of", Int::class.javaPrimitiveType)
            .invoke(null, userId) as android.os.UserHandle

    /**
     * Injects the icon visibility toggle into Status Bar customization.
     */
    private fun hookIconCustomization(classLoader: ClassLoader) {
        try {
            val settingsFragmentClass = classLoader.loadClass("com.android.settings.IconDisplayCustomizationSettings")

            // Hook onCreate to inject UI elements
            val onCreate = settingsFragmentClass.getDeclaredMethod("onCreate", Bundle::class.java)
            module.hook(onCreate).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                    val fragment = getThisObjectMethod.invoke(chain)
                    val context = Reflect.call(fragment!!, "getContext") as Context
                    
                    val systemCategory = Reflect.getField(fragment, "mSettingsSystemState") ?: return result

                    // Create CheckBoxPreference for HyperModes using reflection
                    val checkBoxClass = classLoader.loadClass("androidx.preference.CheckBoxPreference")
                    val checkBox = Reflect.newInstance(checkBoxClass, context)
                    
                    Reflect.call(checkBox, "setKey", PREF_KEY_TOGGLE)
                    Reflect.call(checkBox, "setPersistent", false)

                    // Load strings from our module
                    val modContext = context.createPackageContext(
                        Protocol.MODULE_PACKAGE,
                        Context.CONTEXT_IGNORE_SECURITY
                    )
                    val titleResId = modContext.resources.getIdentifier(
                        "modes", "string", Protocol.MODULE_PACKAGE
                    )
                    val iconResId = modContext.resources.getIdentifier(
                        "setting_modes", "drawable", Protocol.MODULE_PACKAGE
                    )

                    Reflect.call(checkBox, "setTitle", modContext.getString(titleResId))
                    if (iconResId != 0) {
                        val icon = modContext.getDrawable(iconResId)
                        Reflect.call(checkBox, "setIcon", icon)
                    }

                    // Add to the system category
                    Reflect.call(systemCategory, "addPreference", checkBox)

                    // Set listener
                    val listenerClass = classLoader.loadClass("androidx.preference.Preference\$OnPreferenceChangeListener")
                    val proxy = Proxy.newProxyInstance(
                        classLoader,
                        arrayOf(listenerClass)
                    ) { _, method, args ->
                        if (method.name == "onPreferenceChange") {
                            val isChecked = args[1] as Boolean
                            setStatusBarHideIconSlotName(context, !isChecked, SLOT_NAME)
                            true
                        } else {
                            null
                        }
                    }
                    Reflect.call(checkBox, "setOnPreferenceChangeListener", proxy)
                    return result
                }
            })

            // Hook onResume to update our preference state
            val onResume = settingsFragmentClass.getDeclaredMethod("onResume")
            module.hook(onResume).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                    val fragment = getThisObjectMethod.invoke(chain)
                    val context = Reflect.call(fragment!!, "getContext") as Context
                    val checkBox = Reflect.call(fragment, "findPreference", PREF_KEY_TOGGLE) ?: return result
                    
                    val isHidden = isHideIconSlotName(context, SLOT_NAME)
                    Reflect.call(checkBox, "setChecked", !isHidden)
                    return result
                }
            })
            log("IconDisplayCustomizationSettings hooked")
        } catch (t: Throwable) {
            log("Failed to hook IconDisplayCustomizationSettings: $t")
        }
    }

    private fun isHideIconSlotName(context: Context, slot: String): Boolean {
        val list = toHideIconSlotNameList(
            Settings.System.getString(context.contentResolver, "status_bar_hide_icon_slot_name")
        )
        return list.contains(slot)
    }

    private fun setStatusBarHideIconSlotName(context: Context, hide: Boolean, slot: String) {
        val list = toHideIconSlotNameList(
            Settings.System.getString(context.contentResolver, "status_bar_hide_icon_slot_name")
        ).toMutableList()

        if (hide && !list.contains(slot)) {
            list.add(slot)
        } else if (!hide && list.contains(slot)) {
            list.remove(slot)
        } else {
            return
        }

        val result = list.joinToString(",")
        Settings.System.putString(context.contentResolver, "status_bar_hide_icon_slot_name", result)
    }

    private fun toHideIconSlotNameList(str: String?): List<String> {
        val finalStr = str ?: "alarm_clock,phone,pad,pc,tv,car,sound_box,glasses,camera"
        return if (finalStr.isEmpty()) emptyList() else finalStr.split(",")
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)
}
