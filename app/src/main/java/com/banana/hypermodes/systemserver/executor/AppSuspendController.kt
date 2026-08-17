package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.os.IBinder
import android.util.Log
import com.banana.hypermodes.utils.HyperLog

/**
 * Controller for suspending and unsuspending app packages using PackageManagerService.
 *
 * This component uses reflection to call IPackageManager.setPackagesSuspendedAsUser
 * with system-level privileges from within system_server. Suspended apps will have
 * their icons grayed out and cannot be launched until unsuspended.
 *
 * The controller tracks which packages are currently suspended so it can:
 * - Skip redundant suspend operations (no-op if already suspended with same packages)
 * - Unsuspend previously suspended packages when mode is deactivated
 *
 * @param context System context from system_server
 * @param classLoader System_server ClassLoader for reflection
 */
class AppSuspendController(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    /**
     * Currently suspended package names. Empty if no packages are suspended.
     */
    private var currentSuspendedApps: List<String> = emptyList()

    /**
     * Suspend the specified app packages.
     * This will gray out app icons and prevent the apps from being launched.
     *
     * If the same package list is already suspended, this method does nothing (no-op).
     * If different packages were previously suspended, they will be unsuspended first.
     *
     * @param packageNames List of package names to suspend (e.g., ["com.example.app"])
     */
    fun suspendApps(packageNames: List<String>) {
        if (packageNames.isEmpty()) {
            log("suspendApps: empty package list, skipping")
            return
        }

        // Check if we're already suspending the exact same packages
        if (packageNames.toSet() == currentSuspendedApps.toSet()) {
            log("suspendApps: same packages already suspended, skipping")
            return
        }

        // Unsuspend previous packages first if any
        if (currentSuspendedApps.isNotEmpty()) {
            log("suspendApps: unsuspending previous packages first")
            unsuspendApps()
        }

        try {
            // Get IPackageManager via ServiceManager
            val ipm = getPackageManagerService()

            val method = resolveSetPackagesSuspendedAsUser(ipm)
            method.invoke(
                ipm,
                packageNames.toTypedArray(),
                true,
                null,
                null,
                null,
                0,
                MODULE_PACKAGE,
                0,
                0
            )

            currentSuspendedApps = packageNames.toList()
            log("suspendApps: successfully suspended ${packageNames.size} packages: ${packageNames.joinToString()}")

        } catch (e: Exception) {
            log("suspendApps: failed to suspend packages: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Unsuspend all previously suspended packages.
     * This restores normal app icons and allows the apps to be launched again.
     *
     * If no packages are currently suspended, this method does nothing (no-op).
     */
    fun unsuspendApps() {
        if (currentSuspendedApps.isEmpty()) {
            log("unsuspendApps: no packages currently suspended, skipping")
            return
        }

        try {
            // Get IPackageManager via ServiceManager
            val ipm = getPackageManagerService()

            val method = resolveSetPackagesSuspendedAsUser(ipm)
            method.invoke(
                ipm,
                currentSuspendedApps.toTypedArray(),
                false,
                null,
                null,
                null,
                0,
                MODULE_PACKAGE,
                0,
                0
            )

            log("unsuspendApps: successfully unsuspended ${currentSuspendedApps.size} packages: ${currentSuspendedApps.joinToString()}")
            currentSuspendedApps = emptyList()

        } catch (e: Exception) {
            log("unsuspendApps: failed to unsuspend packages: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Get IPackageManager service using reflection.
     * Uses ServiceManager.getService("package") and IPackageManager.Stub.asInterface().
     *
     * @return IPackageManager proxy object
     * @throws Exception if ServiceManager or IPackageManager classes cannot be loaded
     */
    private fun getPackageManagerService(): Any {
        // Get binder from ServiceManager
        val serviceManagerClass = Class.forName("android.os.ServiceManager", true, classLoader)
        val getServiceMethod = serviceManagerClass.getMethod("getService", String::class.java)
        val binder = getServiceMethod.invoke(null, "package") as IBinder

        // Convert binder to IPackageManager using Stub.asInterface
        val stubClass = Class.forName("android.content.pm.IPackageManager\$Stub", true, classLoader)
        val asInterfaceMethod = stubClass.getMethod("asInterface", IBinder::class.java)
        return asInterfaceMethod.invoke(null, binder)
            ?: throw IllegalStateException("IPackageManager.Stub.asInterface returned null")
    }

    /** Android 17 / OS4 IPackageManager signature. */
    private fun resolveSetPackagesSuspendedAsUser(ipm: Any) = ipm.javaClass.getMethod(
        "setPackagesSuspendedAsUser",
        arrayOf<String>().javaClass,
        Boolean::class.javaPrimitiveType!!,
        android.os.PersistableBundle::class.java,
        android.os.PersistableBundle::class.java,
        Class.forName("android.content.pm.SuspendDialogInfo", false, classLoader),
        Int::class.javaPrimitiveType!!,
        String::class.java,
        Int::class.javaPrimitiveType!!,
        Int::class.javaPrimitiveType!!
    )

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
    }

    companion object {
        private const val TAG = "AppSuspendController"
        private const val MODULE_PACKAGE = "com.banana.hypermodes"
    }
}
