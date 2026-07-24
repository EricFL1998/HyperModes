package com.banana.hypermodes.systemserver

import android.content.Context
import android.os.IBinder
import android.util.Log
import com.banana.hypermodes.protocol.Protocol

/**
 * Manages the "HyperModes" status bar icon using IStatusBarService.
 * This runs inside system_server and is independent of the app process.
 */
class StatusBarIconManager(private val context: Context, private val classLoader: ClassLoader) {

    private var statusBarService: Any? = null
    private val slotName = "hypermodes"
    private var isInitialized = false

    /**
     * Lazy initialization of StatusBarService.
     * Called on first use instead of in constructor to speed up startup.
     */
    private fun ensureInitialized() {
        if (isInitialized) return

        try {
            val serviceManagerClass = classLoader.loadClass("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, Context.STATUS_BAR_SERVICE) as IBinder

            val iStatusBarServiceClass = classLoader.loadClass("com.android.internal.statusbar.IStatusBarService")
            val asInterfaceMethod = iStatusBarServiceClass.getDeclaredClasses()
                .find { it.simpleName == "Stub" }
                ?.getMethod("asInterface", IBinder::class.java)

            statusBarService = asInterfaceMethod?.invoke(null, binder)
            isInitialized = true
            log("StatusBarIconManager initialized, service found: ${statusBarService != null}")
        } catch (e: Exception) {
            log("Failed to initialize StatusBarIconManager: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Set the status bar icon for the active mode.
     * @param iconResName The resource name of the monochromatic vector icon in this package.
     * @param contentDescription Description for accessibility.
     */
    fun setIcon(iconResName: String?, contentDescription: String) {
        ensureInitialized()
        val service = statusBarService ?: return
        val resName = iconResName ?: "ic_stat_zen"

        try {
            // Create package context to access our module's resources
            val moduleContext = context.createPackageContext(
                Protocol.MODULE_PACKAGE,
                Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
            )

            val resId = moduleContext.resources.getIdentifier(resName, "drawable", Protocol.MODULE_PACKAGE)
            if (resId == 0) {
                log("Icon resource not found: $resName in package ${Protocol.MODULE_PACKAGE}")
                return
            }

            // HyperOS uses: setIcon(String slot, String iconPackage, int iconId, int iconLevel, String contentDescription)
            val setIconMethod = service.javaClass.getMethod(
                "setIcon",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType,
                Int::class.javaPrimitiveType,
                String::class.java
            )
            setIconMethod.invoke(service, slotName, Protocol.MODULE_PACKAGE, resId, 0, contentDescription)

            log("Status bar icon set: $resName (resId=$resId)")
        } catch (e: Exception) {
            log("Failed to set status bar icon: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Remove the status bar icon.
     */
    fun removeIcon() {
        ensureInitialized()
        val service = statusBarService ?: return
        try {
            val removeIconMethod = service.javaClass.getMethod("removeIcon", String::class.java)
            removeIconMethod.invoke(service, slotName)
            log("Status bar icon removed")
        } catch (e: Exception) {
            log("Failed to remove status bar icon: ${e.message}")
            // Don't print stack trace for "slot not found" if already removed
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "StatusBarIconManager"
    }
}
