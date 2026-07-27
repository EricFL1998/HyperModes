package com.banana.hypermodes.hook

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.config.ConfigParser
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.ref.WeakReference

/**
 * Hook for SystemUI to display the active mode on the lockscreen.
 * Injects a mode display view into the keyguard indication area.
 */
class LockscreenHook(private val module: XposedModule) {

    private var modeDisplayRef = WeakReference<LinearLayout>(null)
    private var isReceiverRegistered = false
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
                            val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                            val section = getThisObjectMethod.invoke(chain) ?: return result
                            val injector = Reflect.getField(section, "keyguardBottomAreaInjector") ?: return result
                            log("bindData triggered, injecting...")
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
                            val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                            val injector = getThisObjectMethod.invoke(chain) ?: return result
                            log("updateIndicationTextLayoutParams triggered, injecting...")
                            injectModeDisplay(injector)
                        } catch (t: Throwable) {
                            log("Injection failed in updateIndicationTextLayoutParams: ${t.message}")
                        }
                        return result
                    }
                })

            isInstalled = true
            log("Lockscreen hooks installed successfully")
            
            hookDozeTransition(classLoader)
        } catch (t: Throwable) {
            log("Failed to install Lockscreen hook: ${t.message}")
        }
    }

    private fun hookDozeTransition(classLoader: ClassLoader) {
        try {
            val dozeScrimControllerClass = classLoader.loadClass("com.android.systemui.statusbar.phone.DozeScrimController")
            val setDozingMethod = dozeScrimControllerClass.getDeclaredMethod("setDozing", Boolean::class.javaPrimitiveType)

            module.hook(setDozingMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val getArgsMethod = (chain as Any).javaClass.getMethod("getArgs")
                        val args = getArgsMethod.invoke(chain) as Array<*>
                        val isDozing = args[0] as? Boolean ?: false
                        val modeDisplay = modeDisplayRef.get()
                        
                        if (modeDisplay != null) {
                            if (isDozing) {
                                modeDisplay.animate().alpha(0f).setDuration(400).withEndAction {
                                    modeDisplay.visibility = View.INVISIBLE
                                }.start()
                            } else {
                                modeDisplay.visibility = View.VISIBLE
                                modeDisplay.animate().alpha(1f).setDuration(400).start()
                                updateModeDisplay(modeDisplay.context, modeDisplay)
                            }
                        }
                        return chain.proceed()
                    }
                })
        } catch (t: Throwable) {
            log("Failed to hook doze transition: ${t.message}")
        }
    }

    private fun injectModeDisplay(injector: Any) {
        val modeDisplay = modeDisplayRef.get()
        if (modeDisplay != null && modeDisplay.parent != null) {
            updateModeDisplay(modeDisplay.context, modeDisplay)
            return
        }

        val indicationArea = Reflect.getField(injector, "mIndicationArea") as? LinearLayout
        if (indicationArea == null) {
            log("mIndicationArea is null in injector $injector")
            return
        }
        val context = indicationArea.context
        
        log("Injecting mode display into indicationArea: $indicationArea")
        val newModeDisplay = createModeDisplayView(context)
        indicationArea.addView(newModeDisplay, 0)
        modeDisplayRef = WeakReference(newModeDisplay)

        updateModeDisplay(context, newModeDisplay)
        ensureReceiverRegistered(context)
        log("Lockscreen Injection successful")
    }

    private fun createModeDisplayView(context: Context): LinearLayout {
        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            id = View.generateViewId()
            visibility = View.GONE
            
            val margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, context.resources.displayMetrics).toInt()
            setPadding(0, margin, 0, margin)

            // Icon
            val iconView = ImageView(context).apply {
                val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, context.resources.displayMetrics).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, context.resources.displayMetrics).toInt()
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            addView(iconView)

            // Name
            val textView = TextView(context).apply {
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            }
            addView(textView)
        }
    }

    private fun ensureReceiverRegistered(context: Context) {
        if (isReceiverRegistered) return
        val filter = IntentFilter(Protocol.ACTION_MODE_STATE)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                modeDisplayRef.get()?.let { updateModeDisplay(c, it) }
            }
        }
        context.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        isReceiverRegistered = true
    }

    private fun updateModeDisplay(context: Context, modeDisplay: LinearLayout) {
        try {
            val json = Settings.Global.getString(context.contentResolver, "pixel_routines_full_config")
            if (json.isNullOrBlank()) {
                modeDisplay.visibility = View.GONE
                return
            }

            val config = ConfigParser.parseConfig(json)
            val activeModeId = config.activeModeId
            val activeMode = config.modes.find { it.id == activeModeId }

            if (activeMode != null) {
                val iconView = modeDisplay.getChildAt(0) as ImageView
                val textView = modeDisplay.getChildAt(1) as TextView

                val moduleContext = context.createPackageContext(Protocol.MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY)
                val iconResName = ModeIconMapper.getStatusBarIcon(activeMode.icon)
                val iconResId = moduleContext.resources.getIdentifier(iconResName, "drawable", Protocol.MODULE_PACKAGE)
                
                if (iconResId != 0) {
                    iconView.setImageDrawable(moduleContext.getDrawable(iconResId))
                    iconView.visibility = View.VISIBLE
                } else {
                    iconView.visibility = View.GONE
                }
                
                textView.text = activeMode.name
                modeDisplay.visibility = View.VISIBLE
            } else {
                modeDisplay.visibility = View.GONE
            }
        } catch (t: Throwable) {
            log("Update failed: ${t.message}")
        }
    }

    private fun log(msg: String) {
        module.log(android.util.Log.WARN, TAG, msg)
    }
}
