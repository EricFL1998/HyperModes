package com.banana.hypermodes.systemserver

import android.content.Context
import android.graphics.drawable.Icon
import android.os.Bundle
import android.os.IBinder
import android.os.UserHandle
import android.util.Log
import com.banana.hypermodes.protocol.Protocol

/**
 * Manages the "HyperModes" status bar icon using IStatusBarService.
 * This runs inside system_server and is independent of the app process.
 */
class StatusBarIconManager(private val context: Context, private val classLoader: ClassLoader) {

    private var statusBarService: Any? = null
    private val slotName = "hypermodes"

    init {
        try {
            val serviceManagerClass = classLoader.loadClass("android.os.ServiceManager")
            val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
            val binder = getServiceMethod.invoke(null, Context.STATUS_BAR_SERVICE) as IBinder
            
            val iStatusBarServiceClass = classLoader.loadClass("com.android.internal.statusbar.IStatusBarService")
            val asInterfaceMethod = iStatusBarServiceClass.getDeclaredClasses()
                .find { it.simpleName == "Stub" }
                ?.getMethod("asInterface", IBinder::class.java)
            
            statusBarService = asInterfaceMethod?.invoke(null, binder)
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

            val icon = Icon.createWithResource(Protocol.MODULE_PACKAGE, resId)

            // Call IStatusBarService.setIcon(String slot, StatusBarIcon icon)
            // StatusBarIcon(UserHandle user, String pkg, Icon icon, int iconLevel, int number,
            //               CharSequence contentDescription, Type type, Shape shape)

            val statusBarIconClass = classLoader.loadClass("com.android.internal.statusbar.StatusBarIcon")
            val userHandleClass = classLoader.loadClass("android.os.UserHandle")
            val systemUser = userHandleClass.getField("SYSTEM").get(null) as UserHandle

            // Type and Shape are enums in Android 14+ (HyperOS base)
            val typeEnum = classLoader.loadClass("com.android.internal.statusbar.StatusBarIcon\$Type")
            val systemIconType = typeEnum.getField("SystemIcon").get(null)

            val shapeEnum = classLoader.loadClass("com.android.internal.statusbar.StatusBarIcon\$Shape")
            val wrapContentShape = shapeEnum.getField("WRAP_CONTENT").get(null)

            val statusBarIconConstructor = statusBarIconClass.getConstructor(
                UserHandle::class.java, String::class.java, Icon::class.java,
                Int::class.javaPrimitiveType, Int::class.javaPrimitiveType,
                CharSequence::class.java, typeEnum, shapeEnum
            )

            val statusBarIcon = statusBarIconConstructor.newInstance(
                systemUser, Protocol.MODULE_PACKAGE, icon, 0, 0, contentDescription, systemIconType, wrapContentShape
            )

            val setIconMethod = service.javaClass.getMethod("setIcon", String::class.java, statusBarIconClass)
            setIconMethod.invoke(service, slotName, statusBarIcon)

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
