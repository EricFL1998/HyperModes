package com.banana.hypermodes.systemserver

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Auto-grants WRITE_SECURE_SETTINGS permission to the module package,
 * running INSIDE system_server.
 *
 * This allows the front-end app to write to Settings.Global (e.g., config
 * storage for RoutineCoreEngine) without requiring manual ADB authorization.
 *
 * Hooks PermissionManagerService.checkPermission to return PERMISSION_GRANTED
 * when our package requests WRITE_SECURE_SETTINGS.
 */
class PermissionGrantHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        log("PermissionGrantHook.install starting")

        val pms = try {
            classLoader.loadClass(PERMISSION_MANAGER_SERVICE)
        } catch (t: Throwable) {
            log("PermissionManagerService not found: ${t.message}")
            return
        }

        log("PermissionManagerService found, installing hook")
        hookCheckPermission(pms)
        log("PermissionGrantHook.install complete")
    }

    /**
     * Hook PermissionManagerService.checkPermission to auto-grant
     * WRITE_SECURE_SETTINGS for our package.
     *
     * Method signature (AOSP):
     * int checkPermission(String permName, String pkgName, int userId)
     */
    private fun hookCheckPermission(pms: Class<*>) {
        val method = try {
            pms.getDeclaredMethod(
                "checkPermission",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType
            ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("checkPermission method not found: ${t.message}")
            return
        }

        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val permName = chain.getArg(0) as? String
                    val pkgName = chain.getArg(1) as? String
                    val userId = chain.getArg(2) as? Int

                    // Auto-grant WRITE_SECURE_SETTINGS for our package
                    if (pkgName == Protocol.MODULE_PACKAGE &&
                        permName == Manifest.permission.WRITE_SECURE_SETTINGS) {
                        log("auto-granting WRITE_SECURE_SETTINGS to $pkgName (userId=$userId)")
                        return PackageManager.PERMISSION_GRANTED
                    }

                    // All other cases: proceed normally
                    return chain.proceed()
                }
            })

        log("checkPermission hooked successfully")
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val PERMISSION_MANAGER_SERVICE = "com.android.server.pm.permission.PermissionManagerServiceImpl"
    }
}
