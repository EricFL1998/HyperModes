package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.hook.ControlCenterCardHook
import com.banana.hypermodes.hook.AodEditorHook
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.hook.FullAodHook
import com.banana.hypermodes.hook.LockscreenHook
import com.banana.hypermodes.hook.SettingsHook
import com.banana.hypermodes.hook.SystemKeepAliveHook
import com.banana.hypermodes.hook.SystemModeHook
import com.banana.hypermodes.hook.SystemUIHook
import com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinator
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.hooks.NotificationFilterHook
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

class XposedInit : XposedModule() {
    private var processName: String? = null
    private val modeDisplayCoordinator by lazy {
        ModeDisplayCoordinator { message ->
            log(Log.WARN, "HyperModes.ModeDisplay", message)
        }
    }
    private val lockscreenHook by lazy { LockscreenHook(this, modeDisplayCoordinator) }
    private val fullAodHook by lazy { FullAodHook(this, modeDisplayCoordinator) }

    override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        this.processName = param.processName
        HyperLog.d(TAG, "onModuleLoaded: processName=${this.processName}")
    }

    override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        HyperLog.d(TAG, "onSystemServerStarting called")
        try {
            SystemModeHook(this).install(param.classLoader)
            SystemKeepAliveHook(this).install(param.classLoader)
            NotificationFilterHook(this).install(param.classLoader)
        } catch (t: Throwable) {
            Log.e(TAG, "!!! failed to install system_server hook", t)
        }
    }

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        val pkg = param.packageName
        HyperLog.d(TAG, "onPackageReady: pkg=$pkg, proc=$processName")

        try {
            when (pkg) {
                Protocol.TARGET_PACKAGE -> {
                    DeskClockHook(this).install(param.classLoader)
                }
                Protocol.SETTINGS_PACKAGE -> {
                    SettingsHook(this).install(param.classLoader)
                }
                "com.android.systemui" -> {
                    HyperLog.d(TAG, "com.android.systemui ready - installing OS4 native QS hook")
                    ControlCenterCardHook(this).install(param.classLoader)
                    SystemUIHook(this).install(param.classLoader)
                    lockscreenHook.install(param.classLoader)
                    fullAodHook.install(param.classLoader)
                }
                "com.miui.aod" -> {
                    HyperLog.d(TAG, "com.miui.aod ready - hooking keyguard editor")
                    AodEditorHook(this).install(param.classLoader)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "!!! XposedInit error in $pkg", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
