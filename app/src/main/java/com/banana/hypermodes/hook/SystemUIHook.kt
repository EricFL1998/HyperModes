package com.banana.hypermodes.hook

import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hook for SystemUI to enable tinting of HyperModes status bar icons.
 *
 * By default, SystemUI only applies color tinting to icons from "com.android.systemui" package.
 * This hook intercepts the package check to allow our icons to be tinted based on status bar theme.
 */
class SystemUIHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes.SystemUIHook"
    }

    fun install(classLoader: ClassLoader) {
        try {
            // Hook StatusBarIconView.updateLightDarkTint to enable tinting for our icons
            val statusBarIconViewClass = classLoader.loadClass(
                "com.android.systemui.statusbar.StatusBarIconView"
            )

            val updateLightDarkTintMethod = statusBarIconViewClass.getDeclaredMethod(
                "updateLightDarkTint",
                ArrayList::class.java,
                Float::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType
            )

            module.hook(updateLightDarkTintMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        try {
                            // Get the StatusBarIconView instance
                            val iconView = chain.thisObject

                            // Get mIcon field to check package name
                            val iconField = iconView.javaClass.getDeclaredField("mIcon")
                            iconField.isAccessible = true
                            val statusBarIcon = iconField.get(iconView)

                            if (statusBarIcon != null) {
                                // Get package name from StatusBarIcon
                                val pkgField = statusBarIcon.javaClass.getDeclaredField("pkg")
                                pkgField.isAccessible = true
                                val pkg = pkgField.get(statusBarIcon) as? String

                                // If this is our icon, force useTint to true
                                if (pkg == Protocol.MODULE_PACKAGE) {
                                    // Get mExParams field
                                    val exParamsField = iconView.javaClass.getDeclaredField("mExParams")
                                    exParamsField.isAccessible = true
                                    val exParams = exParamsField.get(iconView)

                                    // Set useTint = true in StatusBarIconViewEx
                                    val useTintField = exParams.javaClass.getDeclaredField("useTint")
                                    useTintField.isAccessible = true
                                    useTintField.setBoolean(exParams, true)
                                }
                            }
                        } catch (t: Throwable) {
                            // Silent fail - don't break SystemUI
                        }

                        return chain.proceed()
                    }
                })

            log("SystemUI hook installed successfully")
        } catch (t: Throwable) {
            log("Failed to install SystemUI hook: ${t.message}", t)
        }
    }

    private fun log(msg: String, t: Throwable? = null) {
        if (t != null) {
            Log.e(TAG, msg, t)
        } else {
            Log.i(TAG, msg)
        }
    }
}
