package com.banana.hypermodes.hook

import android.content.Context
import android.database.ContentObserver
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.banana.hypermodes.hook.modedisplay.ModeDisplayState
import com.banana.hypermodes.hook.modedisplay.ModeDisplayStateReader
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.ref.WeakReference

/**
 * OS4 natively shows a status row on the lockscreen ("勿扰 | 3 条通知"),
 * rendered by com.miui.systemui.notification.view.NotificationNumStateView.
 * The zen segment is only visible while system DND is on, driven by
 * NotificationNumStateViewModel.isZenModeEnabled -> the view's field ->
 * the updateZenMode Runnable (fade in/out of zenView + divider + the
 * translate compensations).
 *
 * This hook turns that native row into HyperModes' mode display:
 *  - Whenever a mode is active the zen segment is forced on even without
 *    DND, carrying the mode name + mode icon instead of "勿扰" + moon.
 *  - All show/hide animation goes through the NATIVE updateZenMode Runnable
 *    (instantiated via reflection), so fades, divider and translations stay
 *    exactly native.
 *  - Hooking the Runnable's run() neutralizes binder emissions that would
 *    fade the segment out (DND off) while a mode is active.
 *  - With no mode active everything falls back to stock behavior, and a
 *    previously overridden row is restored (native text/icon, native fade).
 */
class ZenTextHook(private val module: XposedModule) {

    companion object {
        private const val TAG = "HyperModes.ZenTextHook"
        private const val NUM_STATE_VIEW_CLASS =
            "com.miui.systemui.notification.view.NotificationNumStateView"
        private const val ZEN_RUNNABLE_CLASS =
            "com.miui.systemui.notification.view.NotificationNumStateView\$updateZenMode\$1"
        private const val FIELD_THIS_0 = "this\$0"
        private const val FIELD_IS_ZEN = "\$isZenModeEnabled"
        private const val FIELD_CLASS_ID = "\$r8\$classId"
    }

    private var viewRef = WeakReference<View>(null)
    private var settingsObserver: ContentObserver? = null
    private var lastState: ModeDisplayState? = null

    fun install(classLoader: ClassLoader) {
        val viewClass = try {
            classLoader.loadClass(NUM_STATE_VIEW_CLASS)
        } catch (t: Throwable) {
            log("NumStateView class not found (OS4 only): " + t.message)
            return
        }

        try {
            hookUpdateZenViewText(viewClass)
            hookUpdateColor(viewClass)
            hookZenRunnable(classLoader)
            log("OS4 zen text hook installed")
        } catch (t: Throwable) {
            log("install failed: " + t.message)
        }
    }

    /** Text/icon entry point: runs after every native updateZenViewText(). */
    private fun hookUpdateZenViewText(viewClass: Class<*>) {
        val method = viewClass.getDeclaredMethod("updateZenViewText")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val view = HookUtils.getThisObject(chain) as? View
                        if (view != null) applyModeText(view)
                    } catch (t: Throwable) {
                        log("applyModeText failed: " + t.message)
                    }
                    return result
                }
            })
    }

    /**
     * updateColor() resets the zen icon to the native light/dark moon;
     * re-apply the mode icon after it whenever an override is live.
     */
    private fun hookUpdateColor(viewClass: Class<*>) {
        val method = viewClass.getDeclaredMethod("updateColor")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val view = HookUtils.getThisObject(chain) as? View ?: return result
                        val state = lastState ?: return result
                        if (viewRef.get() === view) applyModeIcon(view, state)
                    } catch (t: Throwable) {
                        log("updateColor re-apply failed: " + t.message)
                    }
                    return result
                }
            })
    }

    /**
     * The binder posts updateZenMode(0) with the CAPTURED DND value after
     * every isZenModeEnabled emission; when it is false the runnable fades
     * the zen segment out. While a mode is active we flip the captured flag
     * back to true so the fade-in branch (divider + translations included)
     * runs instead. The classId==1 variant drives the notification count
     * and is left untouched.
     */
    private fun hookZenRunnable(classLoader: ClassLoader) {
        val runnableClass = classLoader.loadClass(ZEN_RUNNABLE_CLASS)
        val method = runnableClass.getDeclaredMethod("run")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    try {
                        val runnable = HookUtils.getThisObject(chain) ?: return chain.proceed()
                        val classId = runCatching {
                            Reflect.getField(runnable, FIELD_CLASS_ID) as? Int
                        }.getOrNull()
                        if (classId == 0) {
                            val view = runCatching {
                                Reflect.getField(runnable, FIELD_THIS_0) as? View
                            }.getOrNull()
                            if (view != null && isModeActive(view.context)) {
                                runCatching {
                                    Reflect.setBooleanField(runnable, FIELD_IS_ZEN, true)
                                    Reflect.setBooleanField(view, "isZenModeEnabled", true)
                                }
                            }
                        }
                    } catch (t: Throwable) {
                        log("zen runnable intercept failed: " + t.message)
                    }
                    return chain.proceed()
                }
            })
    }

    /**
     * Forces the zen segment semantics: with a mode active the segment is
     * "on" regardless of DND, showing the mode name + icon. Called from the
     * updateZenViewText hook and from the settings observer (mode switches
     * that never toggle DND produce no binder emission).
     */
    private fun applyModeText(view: View) {
        viewRef = WeakReference(view)
        ensureSettingsObserver(view.context)

        val state = runCatching { ModeDisplayStateReader.read(view.context) }.getOrNull()
        val realZen = isRealZenOn(view.context)
        val effectiveZen = realZen || state != null

        // onMeasure/getRealWidth read this field; keeping it in sync makes
        // the segment take part in layout even when DND is off.
        runCatching { Reflect.setBooleanField(view, "isZenModeEnabled", effectiveZen) }

        val zenView = Reflect.getField(view, "zenView") as? View ?: return
        val zenText = Reflect.getField(view, "zenText") as? TextView

        if (state != null) {
            zenText?.text = state.name
            zenView.contentDescription = state.name
            applyModeIcon(view, state)
            if (lastState?.name != state.name) log("zen text overridden: " + state.name)
        } else if (lastState != null) {
            // Mode exited: restore native looks (row may still be visible
            // if DND itself is on).
            restoreNativeText(view, zenText, zenView)
            runCatching { Reflect.call(view, "updateColor") }
            log("zen text restored to native")
        }
        lastState = state

        val shown = zenView.visibility == View.VISIBLE && zenView.alpha >= 0.5f
        when {
            effectiveZen && !shown -> postZenAnimation(view, show = true)
            !effectiveZen && shown -> postZenAnimation(view, show = false)
        }
        if (effectiveZen) view.requestLayout()
    }

    /** Runs the native updateZenMode(zen) animation for this view. */
    private fun postZenAnimation(view: View, show: Boolean) {
        runCatching {
            val loader = view.javaClass.classLoader ?: return@runCatching
            val clazz = loader.loadClass(ZEN_RUNNABLE_CLASS)
            val runnable = Reflect.newInstance(clazz, 0)
            Reflect.setObjectField(runnable, FIELD_THIS_0, view)
            Reflect.setBooleanField(runnable, FIELD_IS_ZEN, show)
            view.post(runnable as Runnable)
        }.onFailure { log("post zen animation failed: " + it.message) }
    }

    private fun applyModeIcon(view: View, state: ModeDisplayState) {
        val iconView = Reflect.getField(view, "zenIcon") as? ImageView ?: return
        // Fresh instance per set: a Drawable must not be shared between the
        // old and new views during a keyguard rebuild (single callback host).
        loadModeDrawable(view.context, state.iconResName)
            ?.let { iconView.setImageDrawable(it) }
    }

    private fun loadModeDrawable(context: Context, iconResName: String): Drawable? =
        runCatching {
            val moduleContext = context.createPackageContext(
                Protocol.MODULE_PACKAGE,
                Context.CONTEXT_IGNORE_SECURITY
            )
            val resId = moduleContext.resources.getIdentifier(
                iconResName, "drawable", Protocol.MODULE_PACKAGE
            )
            if (resId == 0) null else moduleContext.getDrawable(resId)
        }.getOrNull()

    private fun restoreNativeText(view: View, zenText: TextView?, zenView: View) {
        runCatching {
            val res = view.resources
            val id = res.getIdentifier(
                "keyguard_num_state_zen_mode_text",
                "string",
                view.context.packageName
            )
            if (id != 0) {
                val native = res.getString(id)
                zenText?.text = native
                zenView.contentDescription = native
            }
        }.onFailure { log("restoreNativeText failed: " + it.message) }
    }

    private fun isModeActive(context: Context): Boolean =
        runCatching { ModeDisplayStateReader.read(context) }.getOrNull() != null

    private fun isRealZenOn(context: Context): Boolean =
        runCatching {
            Settings.Global.getInt(context.contentResolver, "zen_mode", 0) != 0
        }.getOrDefault(false)

    // The engine writes Settings.Global on every activation/deactivation;
    // mode switches without a DND edge produce no binder emission, so this
    // observer is the refresh path while the lockscreen is already showing.
    private fun ensureSettingsObserver(context: Context) {
        if (settingsObserver != null) return
        val appContext = context.applicationContext ?: context
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean) {
                val view = viewRef.get() ?: return
                runCatching { applyModeText(view) }
                    .onFailure { log("observer re-apply failed: " + it.message) }
            }
        }
        runCatching {
            appContext.contentResolver.registerContentObserver(
                Settings.Global.getUriFor(ModeDisplayStateReader.CONFIG_KEY),
                false,
                observer
            )
        }.onSuccess {
            settingsObserver = observer
            log("settings observer registered")
        }.onFailure {
            log("settings observer registration failed: " + it.message)
        }
    }

    private fun log(msg: String) {
        module.log(android.util.Log.WARN, TAG, msg)
        android.util.Log.w(TAG, msg)
    }
}
