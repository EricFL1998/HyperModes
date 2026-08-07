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
        private const val SYSTEMUI_PACKAGE = "com.android.systemui"

        /**
         * Candidate signatures for the light/dark tint method. HyperOS uses the
         * 4-arg form; AOSP uses the 3-arg onDarkChanged. Try each in order so a
         * signature mismatch on any ROM doesn't silently disable tinting.
         */
        private val TINT_SIGNATURES = listOf(
            listOf(
                ArrayList::class.java,
                Float::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                Boolean::class.javaPrimitiveType
            ),
            listOf(
                ArrayList::class.java,
                Float::class.javaPrimitiveType,
                Int::class.javaPrimitiveType
            )
        )
    }

    fun install(classLoader: ClassLoader) {
        val statusBarIconViewClass = try {
            classLoader.loadClass("com.android.systemui.statusbar.StatusBarIconView")
        } catch (t: Throwable) {
            log("StatusBarIconView not found: ${t.message}")
            return
        }

        var installed = false
        for (signature in TINT_SIGNATURES) {
            val method = try {
                statusBarIconViewClass.getDeclaredMethod("updateLightDarkTint", *signature.toTypedArray())
            } catch (_: NoSuchMethodException) {
                null
            }
            if (method != null) {
                installTintHook(method)
                installed = true
                log("Installed updateLightDarkTint hook (${signature.size} args)")
                break
            }
        }

        if (!installed) {
            // Fallback: hook onDarkChanged (AOSP signature) as a second chance.
            val onDarkChanged = try {
                statusBarIconViewClass.getMethod(
                    "onDarkChanged",
                    ArrayList::class.java,
                    Float::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
            } catch (_: NoSuchMethodException) {
                null
            }
            if (onDarkChanged != null) {
                installTintHook(onDarkChanged)
                installed = true
                log("Installed onDarkChanged fallback hook")
            }
        }

        if (!installed) {
            log("No tint hook signature matched on this ROM")
        }
    }

    private fun installTintHook(method: java.lang.reflect.Method) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val iconView = HookUtils.getThisObject(chain) ?: return chain.proceed()
                    val statusBarIcon = try {
                        val iconField = iconView.javaClass.getDeclaredField("mIcon")
                        iconField.isAccessible = true
                        iconField.get(iconView)
                    } catch (t: Throwable) {
                        null
                    }

                    if (statusBarIcon == null) return chain.proceed()

                    val pkgField = try {
                        val f = statusBarIcon.javaClass.getDeclaredField("pkg")
                        f.isAccessible = true
                        f
                    } catch (t: Throwable) {
                        null
                    }
                    if (pkgField == null) return chain.proceed()

                    val pkg = try {
                        pkgField.get(statusBarIcon) as? String
                    } catch (t: Throwable) {
                        null
                    }

                    // Only our icon needs the fake package; every other icon
                    // must proceed untouched.
                    if (pkg != Protocol.MODULE_PACKAGE) return chain.proceed()

                    // Temporarily change pkg so SystemUI tints our icon, then
                    // restore it even if the original throws (PROTECTIVE mode
                    // swallows the exception, so without finally the field would
                    // stay faked forever and break later comparisons).
                    try {
                        pkgField.set(statusBarIcon, SYSTEMUI_PACKAGE)
                        return chain.proceed()
                    } finally {
                        try {
                            pkgField.set(statusBarIcon, Protocol.MODULE_PACKAGE)
                        } catch (t: Throwable) {
                            log("Failed to restore pkg field: ${t.message}")
                        }
                    }
                }
            })
    }

    private fun log(msg: String, t: Throwable? = null) {
        val message = if (t != null) "$msg: ${android.util.Log.getStackTraceString(t)}" else msg
        module.log(android.util.Log.WARN, TAG, message)
    }
}
