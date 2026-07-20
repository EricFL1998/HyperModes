package com.banana.hypermodes

import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * XposedInit - Entry point for LSPosed module
 *
 * This is the main hook entry that LSPosed loads on boot.
 * It delegates to DeskClockHook for the actual implementation.
 *
 * Based on HyperOS Bedtime Timer & Manual Trigger Implementation V2
 */
class XposedInit : IXposedHookLoadPackage {

    companion object {
        private const val TAG = "HyperModesXposed"
    }

    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        // Log EVERY package for debugging
        XposedBridge.log("$TAG: Package loaded: ${lpparam.packageName}")

        // Only hook into com.android.deskclock
        if (lpparam.packageName != "com.android.deskclock") {
            return
        }

        XposedBridge.log("$TAG: ========================================")
        XposedBridge.log("$TAG: ✅ MODULE LOADED FOR DESKCLOCK!")
        XposedBridge.log("$TAG: ========================================")

        try {
            // Delegate to DeskClockHook for the actual implementation
            val hook = DeskClockHook()
            hook.handleLoadPackage(lpparam)

            XposedBridge.log("$TAG: DeskClockHook initialized successfully")

        } catch (e: Throwable) {
            XposedBridge.log("$TAG: Fatal error initializing hook: ${e.message}")
            e.printStackTrace()
        }
    }
}
