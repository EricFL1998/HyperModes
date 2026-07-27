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
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
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
 * Hook for the com.miui.aod package to ensure the mode name is displayed persistently.
 * Injects into AODView and enforces visibility against system-level suppression.
 */
class AodPluginHook(private val module: XposedModule) {

    private var modeDisplayRef = WeakReference<LinearLayout>(null)
    private var isReceiverRegistered = false
    private var isInstalled = false

    companion object {
        private const val TAG = "HyperModes.AodPlugin"
    }

    fun install(classLoader: ClassLoader) {
        if (isInstalled) return
        try {
            val aodViewClass = classLoader.loadClass("com.miui.aod.AODView")
            val onAttachedToWindowMethod = aodViewClass.getDeclaredMethod("onAttachedToWindow")
            
            module.hook(onAttachedToWindowMethod)
                .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        try {
                            val getThisObjectMethod = (chain as Any).javaClass.getMethod("getThisObject")
                            val aodView = getThisObjectMethod.invoke(chain) as? ViewGroup ?: return result
                            injectModeDisplay(aodView)
                        } catch (t: Throwable) {
                            log("Injection failed: ${t.message}")
                        }
                        return result
                    }
                })

            isInstalled = true
            log("AOD Plugin hook installed successfully")
        } catch (t: Throwable) {
            log("Failed to install AOD Plugin hook: ${t.message}")
        }
    }

    private fun injectModeDisplay(aodView: ViewGroup) {
        log("injectModeDisplay called for $aodView")
        
        aodView.post {
            try {
                // Ensure we add to the actual AODView root
                val parent = aodView
                
                // Remove existing
                val existing = parent.findViewWithTag<View>("hypermodes_aod_display")
                if (existing != null) {
                    parent.removeView(existing)
                }

                val context = aodView.context
                val modeDisplay = createModeDisplayView(context)
                modeDisplay.tag = "hypermodes_aod_display"
                
                // Baseline position matches lockscreen resting position
                val params = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    bottomMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74f, context.resources.displayMetrics).toInt()
                }
                
                parent.addView(modeDisplay, params)
                modeDisplayRef = WeakReference(modeDisplay)

                // SYNC Lifecycle and Movement with AOD content
                val clockContainerId = context.resources.getIdentifier("clock_container", "id", Protocol.AOD_PACKAGE)
                if (clockContainerId != 0) {
                    val clockContainer = aodView.findViewById<View>(clockContainerId)
                    if (clockContainer != null) {
                        log("Syncing with clockContainer: $clockContainer")
                        
                        val syncListener = object : ViewTreeObserver.OnPreDrawListener {
                            override fun onPreDraw(): Boolean {
                                val modeView = modeDisplayRef.get() ?: return true
                                // Mirror translation to move with burn-in protection shifting
                                if (modeView.translationY != clockContainer.translationY) {
                                    modeView.translationY = clockContainer.translationY
                                }
                                // Ensure we stay visible even if parent container dims
                                if (modeView.alpha < 1.0f && aodView.visibility == View.VISIBLE) {
                                    modeView.alpha = 1.0f
                                }
                                if (modeView.visibility != View.VISIBLE && aodView.visibility == View.VISIBLE) {
                                    modeView.visibility = View.VISIBLE
                                }
                                return true
                            }
                        }
                        aodView.viewTreeObserver.addOnPreDrawListener(syncListener)
                        
                        modeDisplay.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
                            override fun onViewAttachedToWindow(v: View) {}
                            override fun onViewDetachedFromWindow(v: View) {
                                aodView.viewTreeObserver.removeOnPreDrawListener(syncListener)
                            }
                        })
                    }
                }

                updateModeDisplay(context, modeDisplay)
                ensureReceiverRegistered(context)
                
                log("AOD Root Injection successful and synced")
            } catch (t: Throwable) {
                log("AOD Root Injection failed in post: ${t.message}")
            }
        }
    }

    private fun createModeDisplayView(context: Context): LinearLayout {
        return object : LinearLayout(context) {
            // Strong visibility guards to prevent system suppression
            override fun setAlpha(alpha: Float) {
                super.setAlpha(1.0f)
            }

            override fun setVisibility(visibility: Int) {
                super.setVisibility(View.VISIBLE)
            }
        }.apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            id = View.generateViewId()
            visibility = View.VISIBLE
            
            // Icon
            val iconView = ImageView(context).apply {
                id = View.generateViewId()
                val size = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 18f, context.resources.displayMetrics).toInt()
                layoutParams = LinearLayout.LayoutParams(size, size).apply {
                    marginEnd = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, context.resources.displayMetrics).toInt()
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            addView(iconView)

            // Name
            val textView = TextView(context).apply {
                id = View.generateViewId()
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
            }
            addView(textView)
            
            elevation = 5000f
            clipChildren = false
            clipToPadding = false
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
                modeDisplay.getChildAt(0).visibility = View.GONE
                (modeDisplay.getChildAt(1) as TextView).text = ""
                return
            }

            val config = ConfigParser.parseConfig(json)
            val activeModeId = config.activeModeId
            val activeMode = config.modes.find { it.id == activeModeId }

            val iconView = modeDisplay.getChildAt(0) as ImageView
            val textView = modeDisplay.getChildAt(1) as TextView

            if (activeMode != null) {
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
            } else {
                iconView.visibility = View.GONE
                textView.text = ""
            }
        } catch (t: Throwable) {
            log("Update failed: ${t.message}")
        }
    }

    private fun log(msg: String) {
        module.log(android.util.Log.WARN, TAG, msg)
    }
}
