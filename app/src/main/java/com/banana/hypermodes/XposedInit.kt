package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.protocol.Protocol
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
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        if (param.packageName != Protocol.TARGET_PACKAGE) return
        if (processName != null && processName != param.packageName) {
            log(Log.INFO, TAG, "skipping hook install in secondary process: $processName")
            return
        }
        try {
            DeskClockHook(this).install(param.classLoader)
            log(Log.INFO, TAG, "hook installed for ${param.packageName}")
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install hook", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
