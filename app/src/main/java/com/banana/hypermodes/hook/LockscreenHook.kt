package com.banana.hypermodes.hook

import android.widget.LinearLayout
import com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinator
import com.banana.hypermodes.hook.modedisplay.ModeDisplayViewFactory
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hook for SystemUI to display the active mode on the lockscreen.
 * Injects a mode display view into the keyguard indication area.
 */
class LockscreenHook(
    private val module: XposedModule,
    private val coordinator: ModeDisplayCoordinator
) {
    private var isInstalled = false

    companion object {
        private const val TAG = "HyperModes.LockHook"
    }

    fun install(classLoader: ClassLoader) {
        if (isInstalled) return
        try {
            // 1. Hook KeyguardBottomAreaSection.bindData (primary point)
            val sectionClass = classLoader.loadClass("com.android.keyguard.blueprint.KeyguardBottomAreaSection")
            val bindDataMethod = sectionClass.getDeclaredMethod("bindData", classLoader.loadClass("androidx.constraintlayout.widget.ConstraintLayout"))

            module.hook(bindDataMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val section = HookUtils.getThisObject(chain) ?: return result
                            val injector = Reflect.getField(section, "keyguardBottomAreaInjector") ?: return result
                            injectModeDisplay(injector)
                        } catch (t: Throwable) {
                            log("Injection failed in bindData: ${t.message}")
                        }
                        return result
                    }
                })

            // 2. Hook KeyguardBottomAreaInjector.updateIndicationTextLayoutParams (secondary point)
            val injectorClass = classLoader.loadClass("com.android.keyguard.injector.KeyguardBottomAreaInjector")
            val updateMethod = injectorClass.getDeclaredMethod("updateIndicationTextLayoutParams")
            module.hook(updateMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val injector = HookUtils.getThisObject(chain) ?: return result
                            injectModeDisplay(injector)
                        } catch (t: Throwable) {
                            log("Injection failed in updateIndicationTextLayoutParams: ${t.message}")
                        }
                        return result
                    }
                })

            isInstalled = true
            log("Lockscreen hooks installed successfully")
        } catch (t: Throwable) {
            log("Failed to install Lockscreen hook: ${t.message}")
        }
    }

    private fun injectModeDisplay(injector: Any) {
        val indicationArea = Reflect.getField(injector, "mIndicationArea") as? LinearLayout
        if (indicationArea == null) {
            log("mIndicationArea is null in injector $injector")
            return
        }

        // The coordinator owns the single display view; while it is parked in
        // the notification panel for Full-AOD it must not be recreated here.
        val managed = coordinator.peekDisplay()
        if (managed != null) {
            if (managed.parent == null && !coordinator.isParked()) {
                indicationArea.addView(managed, 0)
                log("lockscreen display re-attached")
            }
            coordinator.attachLockscreenDisplay(managed)
            return
        }

        val display = ModeDisplayViewFactory.create(indicationArea.context).also {
            it.tag = ModeDisplayViewFactory.LOCKSCREEN_TAG
            indicationArea.addView(it, 0)
            log("lockscreen display created")
        }

        coordinator.attachLockscreenDisplay(display)
    }

    private fun log(msg: String) {
        module.log(android.util.Log.WARN, TAG, msg)
        android.util.Log.w(TAG, msg)
    }
}
