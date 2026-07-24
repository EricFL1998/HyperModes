package com.banana.hypermodes.hook

import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hook to add Focus tile to Control Center card-style tiles
 *
 * This modifies the resource arrays that define which tiles should be rendered
 * as large cards (1x2) instead of small icons.
 */
class ControlCenterCardHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes.ControlCenterCardHook"
        private const val FOCUS_TILE_SPEC = "focus"
    }

    fun install(classLoader: ClassLoader) {
        try {
            log("ControlCenterCardHook.install() called")

            // Hook QSController initialization to inject our Focus tile into card specs
            hookQSControllerInit(classLoader)

            log("Control Center card hook installed successfully")
        } catch (t: Throwable) {
            log("Failed to install Control Center card hook: ${t.message}", t)
        }
    }

    /**
     * Hook QSController constructor to modify cardStyleTileSpecs after initialization
     */
    private fun hookQSControllerInit(classLoader: ClassLoader) {
        try {
            // Try to load QSController - it might not be available yet if called from SystemUI
            val qsControllerClass = try {
                classLoader.loadClass("miui.systemui.controlcenter.qs.QSController")
            } catch (e: ClassNotFoundException) {
                log("QSController not found in current classLoader, will try alternative approach")
                // Hook ClassLoader.loadClass to catch when QSController is loaded
                hookClassLoading(classLoader)
                return
            }

            log("Found QSController class directly")
            hookQSController(qsControllerClass)

        } catch (t: Throwable) {
            log("Failed to hook QSController: ${t.message}", t)
        }
    }

    /**
     * Hook ClassLoader to catch when QSController is loaded
     */
    private var isHookingQSController = false

    private fun hookClassLoading(classLoader: ClassLoader) {
        try {
            val loadClassMethod = ClassLoader::class.java.getDeclaredMethod(
                "loadClass",
                String::class.java,
                Boolean::class.javaPrimitiveType
            )

            module.hook(loadClassMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    private val processing = ThreadLocal<Boolean>()

                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        if (processing.get() == true) return chain.proceed()
                        
                        val className = chain.args[0] as? String
                        if (className == "miui.systemui.controlcenter.qs.QSController") {
                            processing.set(true)
                            try {
                                val result = chain.proceed()
                                val qsControllerClass = result as? Class<*>
                                if (qsControllerClass != null && !isHookingQSController) {
                                    isHookingQSController = true
                                    log("QSController class loaded! Hooking methods now")
                                    hookQSController(qsControllerClass)
                                }
                                return result
                            } finally {
                                processing.set(false)
                            }
                        }
                        
                        return chain.proceed()
                    }
                })

            log("Hooked ClassLoader.loadClass to wait for QSController")
        } catch (t: Throwable) {
            log("Failed to hook ClassLoader: ${t.message}", t)
        }
    }

    /**
     * Actually hook the QSController class
     */
    private fun hookQSController(qsControllerClass: Class<*>) {
        try {
            // Hook the getCardStyleTileSpecs() method instead of constructor
            val getCardStyleMethod = qsControllerClass.getDeclaredMethod("getCardStyleTileSpecs")

            module.hook(getCardStyleMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()

                        try {
                            if (result is List<*>) {
                                @Suppress("UNCHECKED_CAST")
                                val tileSpecs = result as? List<String>

                                if (tileSpecs != null && !tileSpecs.contains(FOCUS_TILE_SPEC)) {
                                    val mutableList = tileSpecs.toMutableList()
                                    mutableList.add(FOCUS_TILE_SPEC)
                                    log("Added Focus tile to card style specs: $mutableList")
                                    return mutableList
                                }
                            }
                        } catch (t: Throwable) {
                            log("Error modifying card style specs: ${t.message}", t)
                        }

                        return result
                    }
                })

            log("Hooked QSController.getCardStyleTileSpecs() successfully")
        } catch (t: Throwable) {
            log("Failed to hook QSController methods: ${t.message}", t)
        }
    }

    private fun log(msg: String, t: Throwable? = null) {
        val message = if (t != null) "$msg: ${android.util.Log.getStackTraceString(t)}" else msg
        module.log(android.util.Log.WARN, TAG, message)
    }
}
