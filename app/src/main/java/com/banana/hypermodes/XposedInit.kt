package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
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
        // Only hook in each app's main process. system_server is exempt: its
        // process name doesn't match the "android" package name.
        if (param.packageName != Protocol.FRAMEWORK_PACKAGE &&
            processName != null && processName != param.packageName
        ) {
            log(Log.INFO, TAG, "skipping hook install in secondary process: $processName")
            return
        }
        try {
            when (param.packageName) {
                Protocol.TARGET_PACKAGE -> {
                    DeskClockHook(this).install(param.classLoader)
                    log(Log.INFO, TAG, "hook installed for ${param.packageName}")
                }
                Protocol.SETTINGS_PACKAGE -> {
                    SettingsHook(this).install(param.classLoader)
                    log(Log.INFO, TAG, "hook installed for ${param.packageName}")
                }
                Protocol.FRAMEWORK_PACKAGE -> {
                    SystemKeepAliveHook(this).install(param.classLoader)
                    SystemModeHook(this).install(param.classLoader)
                    log(Log.INFO, TAG, "hook installed for ${param.packageName}")
                }
            }
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install hook", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
