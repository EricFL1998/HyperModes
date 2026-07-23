package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.hooks.NotificationFilterHook
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

    /**
     * In system_server this callback REPLACES the first onPackageLoaded /
     * onPackageReady phase (libxposed API 101 docs) — the "android" case in
     * onPackageReady below never fires, so all system_server hooks
     * (keep-alive, Greezer exemption, exact-alarm, mode bridge) must be
     * installed here. Runs before critical services start, which is early
     * enough to hook AMS.systemReady and AlarmManagerService.
     *
     * OPTIMIZATION: Only install critical hooks here to avoid lspd Binder timeout.
     * Heavy reflection operations are deferred to SystemModeHook.systemReady callback.
     */
    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        log(Log.INFO, TAG, "onSystemServerStarting called - installing critical hooks only")
        try {
            // Install SystemModeHook early - it will install SettingsProviderHook in systemReady
            // and defer heavy operations to systemReady callback
            SystemModeHook(this).install(param.classLoader)

            // Install SystemKeepAliveHook with lightweight mode (defer heavy hooks to systemReady)
            SystemKeepAliveHook(this).install(param.classLoader, deferHeavyHooks = true)

            NotificationFilterHook(this).install(param.classLoader)
            log(Log.INFO, TAG, "critical hooks installed for system_server")
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install system_server hook", t)
        }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        // Note: system_server CAN reach this callback with packageName="system"
        // if onSystemServerStarting wasn't called (LSPosed version differences)
        if (processName != null && processName != param.packageName) {
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
                "android", "system" -> {
                    // Fallback: if onSystemServerStarting wasn't called, try here
                    // LSPosed may use "android" or "system" for system_server
                    log(Log.INFO, TAG, "onPackageReady for ${param.packageName} - installing system hooks (fallback)")

                    try {
                        SystemKeepAliveHook(this).install(param.classLoader)
                        log(Log.INFO, TAG, "SystemKeepAliveHook installed in fallback")
                    } catch (t: Throwable) {
                        log(Log.ERROR, TAG, "SystemKeepAliveHook failed in fallback", t)
                    }

                    try {
                        SystemModeHook(this).install(param.classLoader)
                        log(Log.INFO, TAG, "SystemModeHook installed in fallback")
                    } catch (t: Throwable) {
                        log(Log.ERROR, TAG, "SystemModeHook failed in fallback", t)
                    }

                    try {
                        NotificationFilterHook(this).install(param.classLoader)
                        log(Log.INFO, TAG, "NotificationFilterHook installed in fallback")
                    } catch (t: Throwable) {
                        log(Log.ERROR, TAG, "NotificationFilterHook failed in fallback", t)
                    }

                    log(Log.INFO, TAG, "system hooks installation completed for ${param.packageName}")
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
