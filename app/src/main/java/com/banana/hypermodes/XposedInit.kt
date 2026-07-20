package com.banana.hypermodes

import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.protocol.Protocol
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/** LSPosed entry point (listed in assets/xposed_init). Thin delegator only. */
class XposedInit : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName != Protocol.TARGET_PACKAGE) return
        try {
            DeskClockHook().install(lpparam)
            XposedBridge.log("HyperModes: hook installed for ${lpparam.packageName}")
        } catch (t: Throwable) {
            XposedBridge.log("HyperModes: failed to install hook: $t")
        }
    }
}
