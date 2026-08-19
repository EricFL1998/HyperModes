package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.hook.ControlCenterCardHook
import com.banana.hypermodes.hook.AodEditorHook
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
import com.banana.hypermodes.hook.SystemUIHook
import com.banana.hypermodes.hook.ZenTextHook
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.hooks.NotificationFilterHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class XposedInit : XposedModule() {
    private var processName: String? = null

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        this.processName = param.processName
        HyperLog.d(TAG, "onModuleLoaded: processName=${this.processName}")
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        HyperLog.d(TAG, "onSystemServerStarting called")
        try {
            SystemModeHook(this).install(param.classLoader)
            SystemKeepAliveHook(this).install(param.classLoader)
            NotificationFilterHook(this).install(param.classLoader)
        } catch (t: Throwable) {
            Log.e(TAG, "!!! failed to install system_server hook", t)
        }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        val pkg = param.packageName
        HyperLog.d(TAG, "onPackageReady: pkg=$pkg, proc=$processName")

        try {
            when (pkg) {
                Protocol.TARGET_PACKAGE -> {
                    DeskClockHook(this).install(param.classLoader)
                }
                Protocol.SETTINGS_PACKAGE -> {
                    SettingsHook(this).install(param.classLoader)
                }
                "com.android.systemui" -> {
                    HyperLog.d(TAG, "com.android.systemui ready - installing OS4 native QS hook")
                    ControlCenterCardHook(this).install(param.classLoader)
                    hookPluginLoading(param.classLoader)
                    SystemUIHook(this).install(param.classLoader)
                    ZenTextHook(this).install(param.classLoader)
                }
                "com.miui.aod" -> {
                    HyperLog.d(TAG, "com.miui.aod ready - hooking keyguard editor")
                    AodEditorHook(this).install(param.classLoader)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "!!! XposedInit error in $pkg", t)
        }
    }

    /**
     * OS4: the HyperOS control center UI is provided by the miui.systemui.plugin APK and
     * loaded through com.android.systemui.shared.plugins.PluginInstance.
     *
     * Primary hook point is PluginInstance$PluginFactory.createClassLoader(): it runs inside
     * loadPlugin -> createPlugin, strictly BEFORE Plugin.onCreate and before
     * ControlCenterContentController builds the control center view. Installing the plugin
     * hooks here guarantees getCardStyleTileSpecs/createCardTiles already see our additions
     * on the very first build. PluginInstance.loadPlugin is kept as a fallback.
     */
    private fun hookPluginLoading(systemUiClassLoader: ClassLoader) {
        var primaryInstalled = false
        try {
            val pluginInstanceClass =
                systemUiClassLoader.loadClass("com.android.systemui.shared.plugins.PluginInstance")
            val pluginFactoryClass = pluginInstanceClass.declaredClasses.firstOrNull {
                it.simpleName == "Plugin" + "Factory"
            } ?: Class.forName(
                "com.android.systemui.shared.plugins.PluginInstance" + "\$" + "PluginFactory",
                false,
                systemUiClassLoader
            )
            val createClassLoaderMethod = pluginFactoryClass.getDeclaredMethod("createClassLoader")
                .apply { isAccessible = true }

            hook(createClassLoaderMethod)
                .setExceptionMode(io.github.libxposed.api.XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : io.github.libxposed.api.XposedInterface.Hooker {
                    override fun intercept(chain: io.github.libxposed.api.XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        val classLoader = result as? ClassLoader ?: return result
                        installPluginHooksIfCapable(classLoader, chain.thisObject)
                        return result
                    }
                })
            primaryInstalled = true
            HyperLog.d(TAG, "PluginFactory.createClassLoader hook installed (primary)")
        } catch (t: Throwable) {
            HyperLog.d(TAG, "Failed to hook PluginFactory.createClassLoader: ${t.message}")
        }

        if (primaryInstalled) return
        try {
            val pluginInstanceClass =
                systemUiClassLoader.loadClass("com.android.systemui.shared.plugins.PluginInstance")
            val loadPluginMethod = pluginInstanceClass.getDeclaredMethod("loadPlugin").apply {
                isAccessible = true
            }

            hook(loadPluginMethod)
                .setExceptionMode(io.github.libxposed.api.XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : io.github.libxposed.api.XposedInterface.Hooker {
                    override fun intercept(chain: io.github.libxposed.api.XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val instance = chain.thisObject ?: return result
                            val pluginData = findField(instance.javaClass, "pluginData")
                                ?.get(instance) ?: return result
                            val plugin = findField(pluginData.javaClass, "plugin")
                                ?.get(pluginData) ?: return result
                            val pluginClassLoader = plugin.javaClass.classLoader ?: return result
                            installPluginHooksIfCapable(pluginClassLoader, instance)
                        } catch (t: Throwable) {
                            HyperLog.d(TAG, "PluginInstance.loadPlugin hook error: ${t.message}")
                        }
                        return result
                    }
                })
            HyperLog.d(TAG, "PluginInstance.loadPlugin hook installed (fallback)")
        } catch (t: Throwable) {
            HyperLog.d(TAG, "Failed to hook PluginInstance.loadPlugin: ${t.message}")
        }
    }

    private fun installPluginHooksIfCapable(pluginClassLoader: ClassLoader, source: Any?) {
        try {
            val packageName = source?.let { src ->
                runCatching { findField(src.javaClass, "pluginAppInfo")?.get(src) }
                    .getOrNull()
                    ?.let { runCatching { findField(it.javaClass, "packageName")?.get(it) as? String }.getOrNull() }
                    ?: runCatching { findField(src.javaClass, "packageName")?.get(src) as? String }.getOrNull()
            }

            // Capability-first: the OS4 plugin package name may differ from
            // OS3's miui.systemui.plugin, so install whenever the plugin
            // ClassLoader can actually see the card-style controller.
            val canLoadController = runCatching {
                Class.forName(
                    "miui.systemui.controlcenter.qs.QSController",
                    false,
                    pluginClassLoader
                )
            }.isSuccess
            if (!canLoadController) return

            HyperLog.d(
                TAG,
                "control center plugin detected: package=" + packageName + ", loader=" + pluginClassLoader
            )
            ControlCenterCardHook(this@XposedInit).installPluginHooks(pluginClassLoader)
        } catch (t: Throwable) {
            HyperLog.d(TAG, "installPluginHooksIfCapable error: ${t.message}")
        }
    }

    private fun findField(clazz: Class<*>, name: String): java.lang.reflect.Field? {
        var current: Class<*>? = clazz
        while (current != null) {
            try {
                return current.getDeclaredField(name).apply { isAccessible = true }
            } catch (_: NoSuchFieldException) {
                current = current.superclass
            }
        }
        return null
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
