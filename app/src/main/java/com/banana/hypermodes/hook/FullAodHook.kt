package com.banana.hypermodes.hook

import android.view.View
import android.widget.FrameLayout
import com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinator
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

internal object FullAodSignal {
    fun isFullAod(root: View): Boolean {
        return root.id != View.NO_ID && root.getTag(root.id) == true
    }
}

class FullAodHook(
    private val module: XposedModule,
    private val coordinator: ModeDisplayCoordinator
) {
    private var isInstalled = false

    fun install(classLoader: ClassLoader) {
        if (isInstalled) return

        try {
            val serviceClass = classLoader.loadClass(
                "com.android.keyguard.doze.MiuiDozeService"
            )
            hookDreamingStarted(serviceClass)
            hookDreamingStopped(serviceClass)
            hookStopDozing(classLoader)
            isInstalled = true
            log("Full-AOD hooks installed")
        } catch (t: Throwable) {
            log("Full-AOD hook installation failed: ${t.message}")
        }
    }

    private fun hookDreamingStarted(serviceClass: Class<*>) {
        val method = serviceClass.getDeclaredMethod("onDreamingStarted")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val service = HookUtils.getThisObject(chain) ?: return result
                        val injector = Reflect.getField(service, "mDozeServiceHostInjector")
                            ?: return result
                        val root = Reflect.getField(injector, "mAodView") as? FrameLayout
                            ?: return result
                        val fullAod = FullAodSignal.isFullAod(root)
                        // The aod_root_view's only persistent ancestor is the
                        // shade window root (verified on device: AodView ->
                        // NotificationShadeWindowView -> ViewRootImpl): it stays
                        // visible and unfaded through the whole transition,
                        // unlike the bottom area (fades) or aod_root_view
                        // (GONE until steady AOD). It is never scaled by the
                        // native animation, so the coordinator always mirrors
                        // the shrink with its glide path (isDepthMode = true).
                        coordinator.updatePanelHost(
                            root.parent as? android.view.ViewGroup,
                            isDepthMode = true
                        )
                        coordinator.onFullAodStarted(fullAod)
                    } catch (t: Throwable) {
                        log("onDreamingStarted handling failed: ${t.message}")
                    }
                    return result
                }
            })
    }

    private fun hookDreamingStopped(serviceClass: Class<*>) {
        val method = serviceClass.getDeclaredMethod("onDreamingStopped")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    log("onDreamingStopped observed; waiting for native AOD host exit")
                    return result
                }
            })
    }

    private fun hookStopDozing(classLoader: ClassLoader) {
        val hostClass = classLoader.loadClass(
            "com.android.systemui.statusbar.phone.DozeServiceHost"
        )
        val method = hostClass.getDeclaredMethod("stopDozing")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        // Native stopDozing has now hidden aod_root_view, so its exit
                        // transition is no longer using the child and cleanup is safe.
                        coordinator.onFullAodStopped()
                    } catch (t: Throwable) {
                        log("stopDozing cleanup failed: ${t.message}")
                    }
                    return result
                }
            })
    }

    private fun log(message: String) {
        module.log(android.util.Log.WARN, TAG, message)
        android.util.Log.w(TAG, message)
    }

    companion object {
        private const val TAG = "HyperModes.FullAod"
    }
}
