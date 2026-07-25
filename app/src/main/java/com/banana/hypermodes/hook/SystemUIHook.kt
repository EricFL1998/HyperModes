package com.banana.hypermodes.hook

import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hook for SystemUI to enable tinting of HyperModes status bar icons.
 *
 * By default, SystemUI only applies color tinting to icons from "com.android.systemui" package.
 * This hook modifies the package name comparison to include our package.
 */
class SystemUIHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes.SystemUIHook"
    }

    fun install(classLoader: ClassLoader) {
        try {
            // Hook StatusBarIconView.updateLightDarkTint to fake package name check
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
                            val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                            val iconView = getThisObjectMethod.invoke(chain)

                            // Get mIcon field
                            val iconField = iconView.javaClass.getDeclaredField("mIcon")
                            iconField.isAccessible = true
                            val statusBarIcon = iconField.get(iconView)

                            if (statusBarIcon != null) {
                                // Get package name
                                val pkgField = statusBarIcon.javaClass.getDeclaredField("pkg")
                                pkgField.isAccessible = true
                                val pkg = pkgField.get(statusBarIcon) as? String

                                // If this is our icon, temporarily change pkg to "com.android.systemui"
                                if (pkg == Protocol.MODULE_PACKAGE) {
                                    pkgField.set(statusBarIcon, "com.android.systemui")

                                    // Call original method with faked package
                                    val result = chain.proceed()

                                    // Restore original package
                                    pkgField.set(statusBarIcon, Protocol.MODULE_PACKAGE)

                                    return result
                                }
                            }
                        } catch (t: Throwable) {
                            log("Hook failed: ${t.message}", t)
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
        val message = if (t != null) "$msg: ${android.util.Log.getStackTraceString(t)}" else msg
        module.log(android.util.Log.WARN, TAG, message)
    }
}
