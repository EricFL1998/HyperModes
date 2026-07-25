package com.banana.hypermodes

import android.content.Context
import android.util.Log
import com.banana.hypermodes.hook.ControlCenterCardHook
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.Reflect
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
import com.banana.hypermodes.hook.SystemUIHook
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.hooks.NotificationFilterHook
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class XposedInit : XposedModule() {
    private var processName: String? = null

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        this.processName = param.processName
        Log.e(TAG, "!!! onModuleLoaded: processName=${this.processName}")
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        Log.e(TAG, "!!! onSystemServerStarting called")
        try {
            SystemModeHook(this).install(param.classLoader)
            SystemKeepAliveHook(this).install(param.classLoader, deferHeavyHooks = true)
            NotificationFilterHook(this).install(param.classLoader)
        } catch (t: Throwable) {
            Log.e(TAG, "!!! failed to install system_server hook: ${Log.getStackTraceString(t)}")
        }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        val pkg = param.packageName
        Log.e(TAG, "!!! onPackageReady: pkg=$pkg, proc=$processName")

        try {
            when (pkg) {
                Protocol.TARGET_PACKAGE -> {
                    DeskClockHook(this).install(param.classLoader)
                }
                Protocol.SETTINGS_PACKAGE -> {
                    SettingsHook(this).install(param.classLoader)
                }
                "com.android.systemui" -> {
                    Log.e(TAG, "!!! com.android.systemui ready - hooking plugin loading")
                    hookPluginLoading(param.classLoader)
                    SystemUIHook(this).install(param.classLoader)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "!!! XposedInit error in $pkg: ${Log.getStackTraceString(t)}")
        }
    }

    private fun hookPluginLoading(systemUIClassLoader: ClassLoader) {
        try {
            val pluginInstanceClass = systemUIClassLoader.loadClass("com.android.systemui.shared.plugins.PluginInstance")
            val loadPluginMethod = pluginInstanceClass.getDeclaredMethod("loadPlugin")

            this.hook(loadPluginMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val pluginInstance = chain.thisObject ?: return result
                            val pkg = Reflect.call(pluginInstance, "getPackage") as? String
                            Log.e(TAG, "!!! Plugin package: $pkg")
                            if (pkg != "miui.systemui.plugin") return result

                            val pluginContext = Reflect.call(pluginInstance, "getPluginContext") as? Context
                            val pluginClassLoader = pluginContext?.classLoader
                            if (pluginClassLoader != null) {
                                Log.e(TAG, "!!! miui.systemui.plugin loaded, installing control center card hook")
                                ControlCenterCardHook(this@XposedInit).install(pluginClassLoader)
                            } else {
                                Log.e(TAG, "!!! miui.systemui.plugin pluginContext/classLoader is null")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "!!! Error extracting plugin classloader from PluginInstance: ${e.message}", e)
                        }
                        return result
                    }
                })
            Log.e(TAG, "!!! PluginInstance.loadPlugin hook installed; waiting for miui.systemui.plugin load")
        } catch (t: Throwable) {
            Log.e(TAG, "!!! Failed to hook PluginInstance.loadPlugin", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
