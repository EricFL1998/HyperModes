package com.banana.hypermodes.hook

import android.content.Context
import android.util.Log
import com.banana.hypermodes.tile.FocusTileProvider
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hook to inject Focus tile into MIUI Control Center via MiuiQSTilePlugin
 *
 * MIUI loads QS tiles through MiuiSystemUIPlugin.apk which implements MiuiQSTilePlugin.
 * We hook the getAllPluginTiles() method to add our custom Focus tile to the map.
 */
class ControlCenterHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes.ControlCenterHook"
        private const val FOCUS_TILE_SPEC = "focus"
    }

    fun install(classLoader: ClassLoader) {
        try {
            log("ControlCenterHook.install() called")

            // Hook the MiuiQSTilePlugin to inject our tile
            hookMiuiQSTilePlugin(classLoader)

            log("Control Center hook installation initiated")
        } catch (t: Throwable) {
            log("Failed to install Control Center hook: ${t.message}", t)
        }
    }

    private var isHookingPlugin = false

    /**
     * Hook MiuiQSTilePlugin.getAllPluginTiles() to add our Focus tile
     */
    private fun hookMiuiQSTilePlugin(classLoader: ClassLoader) {
        try {
            val pluginClass = try {
                classLoader.loadClass("miui.systemui.quicksettings.LocalMiuiQSTilePlugin")
            } catch (t: Throwable) {
                // Could be ClassNotFoundException or NoClassDefFoundError
                log("LocalMiuiQSTilePlugin not found yet, hooking class loader")
                hookClassLoading(classLoader)
                return
            }

            log("Found plugin class: ${pluginClass.name}")
            hookPluginMethods(pluginClass, classLoader)

        } catch (t: Throwable) {
            log("Failed to hook MiuiQSTilePlugin: ${t.message}", t)
        }
    }

    private fun hookClassLoading(classLoader: ClassLoader) {
        try {
            val loadClassMethod = ClassLoader::class.java.getDeclaredMethod(
                "loadClass",
                String::class.java,
                Boolean::class.javaPrimitiveType
            )

            module.hook(loadClassMethod)
                .setExceptionMode(io.github.libxposed.api.XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : io.github.libxposed.api.XposedInterface.Hooker {
                    private val processing = ThreadLocal<Boolean>()

                    override fun intercept(chain: io.github.libxposed.api.XposedInterface.Chain): Any? {
                        if (processing.get() == true) return chain.proceed()
                        
                        val className = chain.args[0] as? String
                        if (className == "miui.systemui.quicksettings.LocalMiuiQSTilePlugin") {
                            processing.set(true)
                            try {
                                val result = chain.proceed()
                                val pluginClass = result as? Class<*>
                                if (pluginClass != null && !isHookingPlugin) {
                                    isHookingPlugin = true
                                    log("LocalMiuiQSTilePlugin class loaded! Hooking now")
                                    hookPluginMethods(pluginClass, classLoader)
                                }
                                return result
                            } finally {
                                processing.set(false)
                            }
                        }
                        return chain.proceed()
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook ClassLoader for plugin: ${t.message}", t)
        }
    }

    private fun hookPluginMethods(pluginClass: Class<*>, classLoader: ClassLoader) {
        try {
            // Hook getAllPluginTiles method
            val getAllPluginTilesMethod = pluginClass.getDeclaredMethod("getAllPluginTiles")

            module.hook(getAllPluginTilesMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        try {
                            // Call original method to get the tiles map
                            val result = chain.proceed()

                            if (result is Map<*, *>) {
                                @Suppress("UNCHECKED_CAST")
                                val tilesMap = result as MutableMap<String, Any>

                                // Check if our focus tile is already there
                                if (!tilesMap.containsKey(FOCUS_TILE_SPEC)) {
                                    // Get context from the plugin
                                    val plugin = chain.thisObject
                                    val context = getContextFromPlugin(plugin)

                                    if (context != null) {
                                        // Create our focus tile
                                        val focusTile = createFocusTile(context, classLoader)
                                        if (focusTile != null) {
                                            tilesMap[FOCUS_TILE_SPEC] = focusTile
                                            log("Successfully added Focus tile to plugin tiles map")
                                        }
                                    } else {
                                        log("Failed to get context from plugin")
                                    }
                                }

                                return tilesMap
                            }
                        } catch (t: Throwable) {
                            log("Error in getAllPluginTiles hook: ${t.message}", t)
                        }

                        return chain.proceed()
                    }
                })

            log("MiuiQSTilePlugin hooked successfully")
        } catch (t: Throwable) {
            log("Failed to hook MiuiQSTilePlugin: ${t.message}", t)
        }
    }

    /**
     * Create our Focus tile that implements MiuiQSTile interface
     */
    private fun createFocusTile(context: Context, classLoader: ClassLoader): Any? {
        try {
            log("Creating Focus tile for MIUI plugin")

            // Create our Focus tile provider which implements MiuiQSTile
            val focusTileProvider = FocusTileProvider(context, module)

            // Create the tile (it will implement MiuiQSTile interface via proxy)
            val tile = focusTileProvider.createMiuiTile(classLoader)
            log("Focus tile created successfully")

            return tile
        } catch (t: Throwable) {
            log("Failed to create Focus tile: ${t.message}", t)
            return null
        }
    }

    /**
     * Extract Context from plugin object using reflection
     */
    private fun getContextFromPlugin(plugin: Any): Context? {
        try {
            // Try common field names
            val fieldNames = listOf("mPluginContext", "mSysUIContext", "mContext", "context")

            for (fieldName in fieldNames) {
                try {
                    val field = plugin.javaClass.getDeclaredField(fieldName)
                    field.isAccessible = true
                    val value = field.get(plugin)
                    if (value is Context) {
                        log("Got context from field: $fieldName")
                        return value
                    }
                } catch (e: NoSuchFieldException) {
                    // Try next field name
                }
            }

            // Try getPluginContext method
            try {
                val method = plugin.javaClass.getDeclaredMethod("getPluginContext")
                method.isAccessible = true
                val value = method.invoke(plugin)
                if (value is Context) {
                    log("Got context from getPluginContext()")
                    return value
                }
            } catch (e: Exception) {
                // Continue
            }

            // Try getSysuiContext method
            try {
                val method = plugin.javaClass.getDeclaredMethod("getSysuiContext")
                method.isAccessible = true
                val value = method.invoke(plugin)
                if (value is Context) {
                    log("Got context from getSysuiContext()")
                    return value
                }
            } catch (e: Exception) {
                // Continue
            }
        } catch (t: Throwable) {
            log("Error getting context: ${t.message}", t)
        }

        return null
    }

    private fun log(msg: String, t: Throwable? = null) {
        val message = if (t != null) "$msg: ${android.util.Log.getStackTraceString(t)}" else msg
        module.log(android.util.Log.WARN, TAG, message)
    }
}
