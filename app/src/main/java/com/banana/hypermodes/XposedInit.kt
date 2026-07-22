package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.PermissionGrantHook
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
     */
    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        try {
            // Install permission grant hook FIRST so the app can write to Settings.Global
            PermissionGrantHook(this).install(param.classLoader)
            SystemKeepAliveHook(this).install(param.classLoader)
            SystemModeHook(this).install(param.classLoader)
            NotificationFilterHook(this).install(param.classLoader)
            log(Log.INFO, TAG, "hook installed for system_server")
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install system_server hook", t)
        }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        // Only hook in each app's main process. Note: system_server never
        // reaches this callback — it is scoped via the virtual package
        // "system" and reported through onSystemServerStarting above.
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
                // "android" (system_server) is handled in onSystemServerStarting —
                // onPackageReady's first phase never fires there.
            }
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install hook", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
