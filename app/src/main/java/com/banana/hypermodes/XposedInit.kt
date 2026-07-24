package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.hook.ControlCenterCardHook
import com.banana.hypermodes.hook.ControlCenterHook
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
import com.banana.hypermodes.hook.SystemUIHook
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.hooks.NotificationFilterHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * LSPosed (libxposed API 101) entry point, listed in
 * META-INF/xposed/java_init.list. Thin delegator only.
 */
class XposedInit : XposedModule() {
    private var processName: String? = null

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        processName = param.processName
        log(Log.WARN, TAG, "XposedInit.onModuleLoaded: processName=$processName")
    }

    /**
     * In system_server this callback REPLACES the first onPackageLoaded /
     * onPackageReady phase (libxposed API 101 docs) — the "android" case in
     * onPackageReady below never fires, so all system_server hooks
     * (keep-alive, Greezer exemption, exact-alarm, mode bridge) must be
     * installed here. Runs before critical services start, which is early
     * enough to hook AMS.systemReady and AlarmManagerService.
     *
     * OPTIMIZATION: Only install critical hooks here to avoid lspd Binder timeout.
     * Heavy reflection operations are deferred to SystemModeHook.systemReady callback.
     */
    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        log(Log.WARN, TAG, "onSystemServerStarting called - installing critical hooks only")
        try {
            // Install SystemModeHook early - it will install SettingsProviderHook in systemReady
            // and defer heavy operations to systemReady callback
            SystemModeHook(this).install(param.classLoader)

            // Install SystemKeepAliveHook with lightweight mode (defer heavy hooks to systemReady)
            SystemKeepAliveHook(this).install(param.classLoader, deferHeavyHooks = true)

            NotificationFilterHook(this).install(param.classLoader)
            log(Log.WARN, TAG, "critical hooks installed for system_server")
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install system_server hook", t)
        }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        log(Log.WARN, TAG, "XposedInit.onPackageReady: packageName=${param.packageName}, processName=$processName")

        // Note: system_server CAN reach this callback with packageName="system"
        // if onSystemServerStarting wasn't called (LSPosed version differences)
        // Don't skip SystemUI or plugin packages even if processName differs
        if (processName != null && processName != param.packageName) {
            // Allow SystemUI and plugin to proceed even with different process name
            if (param.packageName != "com.android.systemui" &&
                param.packageName != "miui.systemui.plugin") {
                log(Log.WARN, TAG, "skipping hook install in secondary process: $processName for package ${param.packageName}")
                return
            }
            log(Log.WARN, TAG, "allowing SystemUI/plugin hook despite process mismatch")
        }
        try {
            when (param.packageName) {
                Protocol.TARGET_PACKAGE -> {
                    DeskClockHook(this).install(param.classLoader)
                    log(Log.WARN, TAG, "hook installed for ${param.packageName}")
                }
                Protocol.SETTINGS_PACKAGE -> {
                    SettingsHook(this).install(param.classLoader)
                    log(Log.WARN, TAG, "hook installed for ${param.packageName}")
                }
                "com.android.systemui" -> {
                    SystemUIHook(this).install(param.classLoader)
                    
                    // Hook PluginInstance.loadPlugin to catch when the SystemUI Plugin is loaded
                    hookPluginLoading(param.classLoader)
                    
                    log(Log.WARN, TAG, "hooks installed for ${param.packageName}")
                }
                "miui.systemui.plugin" -> {
                    // We will handle this via SystemUI's plugin loading hook for better ClassLoader access
                    log(Log.WARN, TAG, "miui.systemui.plugin ready - will hook via SystemUI if needed")
                }
                "android", "system" -> {
                    // Fallback: if onSystemServerStarting wasn't called, try here
                    // LSPosed may use "android" or "system" for system_server
                    log(Log.WARN, TAG, "onPackageReady for ${param.packageName} - installing system hooks (fallback)")

                    try {
                        SystemKeepAliveHook(this).install(param.classLoader)
                        log(Log.WARN, TAG, "SystemKeepAliveHook installed in fallback")
                    } catch (t: Throwable) {
                        log(Log.ERROR, TAG, "SystemKeepAliveHook failed in fallback", t)
                    }

                    try {
                        SystemModeHook(this).install(param.classLoader)
                        log(Log.WARN, TAG, "SystemModeHook installed in fallback")
                    } catch (t: Throwable) {
                        log(Log.ERROR, TAG, "SystemModeHook failed in fallback", t)
                    }

                    try {
                        NotificationFilterHook(this).install(param.classLoader)
                        log(Log.WARN, TAG, "NotificationFilterHook installed in fallback")
                    } catch (t: Throwable) {
                        log(Log.ERROR, TAG, "NotificationFilterHook failed in fallback", t)
                    }

                    log(Log.WARN, TAG, "system hooks installation completed for ${param.packageName}")
                }
            }
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install hook", t)
        }
    }

    private fun hookPluginLoading(systemUIClassLoader: ClassLoader) {
        try {
            val pluginInstanceClass = systemUIClassLoader.loadClass(
                "com.android.systemui.shared.plugins.PluginInstance"
            )

            val loadPluginMethod = pluginInstanceClass.getDeclaredMethod("loadPlugin")

            hook(loadPluginMethod)
                .setExceptionMode(io.github.libxposed.api.XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : io.github.libxposed.api.XposedInterface.Hooker {
                    override fun intercept(chain: io.github.libxposed.api.XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val pluginInstance = chain.thisObject
                            if (pluginInstance == null) return result

                            // Get package name of the plugin
                            val getPackageMethod = (pluginInstance as Any).javaClass.getDeclaredMethod("getPackage")
                            val pkg = getPackageMethod.invoke(pluginInstance) as? String
                            
                            if (pkg == "miui.systemui.plugin") {
                                log(Log.WARN, TAG, "SystemUI Plugin loaded, installing hooks")
                                
                                // Get plugin context which has the ClassLoader
                                val getPluginContextMethod = (pluginInstance as Any).javaClass.getDeclaredMethod("getPluginContext")
                                val pluginContext = getPluginContextMethod.invoke(pluginInstance) as? android.content.Context
                                
                                if (pluginContext != null) {
                                    val pluginClassLoader = pluginContext.classLoader
                                    com.banana.hypermodes.hook.ControlCenterHook(this@XposedInit).install(pluginClassLoader)
                                    com.banana.hypermodes.hook.ControlCenterCardHook(this@XposedInit).install(pluginClassLoader)
                                }
                            }
                        } catch (t: Throwable) {
                            log(Log.ERROR, TAG, "Error in loadPlugin hook", t)
                        }
                        return result
                    }
                })
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "Failed to hook plugin loading", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
