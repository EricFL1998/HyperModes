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
        private const val PREF_KEY_MAIN = "top_level_hypermodes"
        private const val SLOT_NAME = "hypermodes"
    }

    fun install(classLoader: ClassLoader) {
        try {
            hookIconCustomization(classLoader)
            hookTopLevelSettings(classLoader)
        } catch (t: Throwable) {
            log("Failed to install SettingsHook: $t")
        }
    }

    /**
     * Injects a "Modes" entry into the root Settings screen.
     */
    private fun hookTopLevelSettings(classLoader: ClassLoader) {
        try {
            val topLevelSettingsClass = classLoader.loadClass("com.android.settings.homepage.TopLevelSettings")
            val onCreate = topLevelSettingsClass.getDeclaredMethod("onCreate", Bundle::class.java)

            module.hook(onCreate).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    val fragment = chain.thisObject
                    val context = Reflect.call(fragment, "getContext") as Context
                    val screen = Reflect.call(fragment, "getPreferenceScreen") ?: return result

                    if (Reflect.call(screen, "findPreference", PREF_KEY_MAIN) != null) return result

                    // Create preference using reflection
                    val prefClass = classLoader.loadClass("com.android.settings.widget.HomepagePreference")
                    val pref = Reflect.newInstance(prefClass, context)
                    
                    Reflect.call(pref, "setKey", PREF_KEY_MAIN)
                    Reflect.call(pref, "setOrder", -100) // Place near the top

                    // Load resources from our module
                    val modContext = context.createPackageContext(Protocol.MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
                    val titleId = modContext.resources.getIdentifier("modes", "string", Protocol.MODULE_PACKAGE)
                    val summaryId = modContext.resources.getIdentifier("modes_settings_summary", "string", Protocol.MODULE_PACKAGE)
                    val iconId = modContext.resources.getIdentifier("ic_homepage_modes", "drawable", Protocol.MODULE_PACKAGE)

                    Reflect.call(pref, "setTitle", modContext.getString(titleId))
                    Reflect.call(pref, "setSummary", modContext.getString(summaryId))
                    if (iconId != 0) {
                        Reflect.call(pref, "setIcon", modContext.getDrawable(iconId))
                    }

                    // Intent to launch our MainActivity directly
                    val intent = Intent().setComponent(
                        ComponentName(Protocol.MODULE_PACKAGE, "com.banana.hypermodes.ui.MainActivity")
                    ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    Reflect.call(pref, "setIntent", intent)

                    Reflect.call(screen, "addPreference", pref)
                    return result
                }
            })
            log("TopLevelSettings hooked")
        } catch (t: Throwable) {
            log("Failed to hook TopLevelSettings: $t")
        }
    }

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
                    val fragment = chain.thisObject
                    val context = Reflect.call(fragment, "getContext") as Context
                    
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
                    val fragment = chain.thisObject
                    val context = Reflect.call(fragment, "getContext") as Context
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

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)
}
