package com.banana.hypermodes.hook.modedisplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.banana.hypermodes.protocol.Protocol
import java.lang.ref.WeakReference

class ModeDisplayCoordinator(
    private val readState: (Context) -> ModeDisplayState? = ModeDisplayStateReader::read,
    private val readBounds: (View) -> DisplayBounds? = ::screenBounds,
    private val logger: (String) -> Unit
) {
    private var lockscreenRef = WeakReference<LinearLayout>(null)
    private var lockscreenLayoutListener: View.OnLayoutChangeListener? = null
    private var fullAodRef = WeakReference<LinearLayout>(null)
    private var fullAodRootRef = WeakReference<FrameLayout>(null)
    private var lastLockscreenBounds: DisplayBounds? = null
    private var fullAodPositioned = false
    private var receiverRegistered = false
    private var receiver: BroadcastReceiver? = null
    private var pendingRootRef = WeakReference<FrameLayout>(null)
    private var pendingPreDraw: ViewTreeObserver.OnPreDrawListener? = null
    private var pendingAttachState: View.OnAttachStateChangeListener? = null

    fun attachLockscreenDisplay(view: LinearLayout) {
        val previous = lockscreenRef.get()
        if (previous !== view) {
            lockscreenLayoutListener?.let { previous?.removeOnLayoutChangeListener(it) }
            lockscreenRef = WeakReference(view)
            lockscreenLayoutListener = null
        }
        captureLockscreenBounds(view, clearWhenUnavailable = previous !== view)
        if (previous !== view) {
            val listener = View.OnLayoutChangeListener { current, _, _, _, _, _, _, _, _ ->
                captureLockscreenBounds(current, clearWhenUnavailable = true)
            }
            view.addOnLayoutChangeListener(listener)
            lockscreenLayoutListener = listener
        }
        ensureReceiverRegistered(view.context)
        refresh(view.context)
        logger("lockscreen attached: view=$view")
    }

    fun onFullAodStarted(root: FrameLayout, isFullAod: Boolean) {
        logger("dream start: fullAod=$isFullAod root=$root")
        if (!isFullAod) {
            removeFullAodView()
            return
        }

        ensureReceiverRegistered(root.context)
        removeFullAodViewFromDifferentRoot(root)

        val existing = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        val display = existing ?: ModeDisplayViewFactory.create(root.context).also {
            it.tag = ModeDisplayViewFactory.FULL_AOD_TAG
            root.addView(
                it,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP or Gravity.START
                )
            )
            logger("full AOD display created")
        }

        fullAodRootRef = WeakReference(root)
        fullAodRef = WeakReference(display)
        fullAodPositioned = positionFullAod(root, display)
        if (!fullAodPositioned) {
            display.visibility = View.INVISIBLE
            scheduleOneShotPosition(root, display)
        }
        refresh(root.context)
    }

    fun onFullAodStopped() {
        logger("dream stop")
        removeFullAodView()
        lockscreenRef.get()?.let { refresh(it.context) }
    }

    fun refresh(context: Context) {
        val state = readState(context)
        lockscreenRef.get()?.let {
            ModeDisplayViewFactory.bind(it.context, it, state)
            captureLockscreenBounds(it)
        }
        fullAodRef.get()?.let {
            ModeDisplayViewFactory.bind(it.context, it, state)
            if (!fullAodPositioned && state != null) {
                it.visibility = View.INVISIBLE
            }
        }
        logger("display refresh: active=${state != null}")
    }

    private fun captureLockscreenBounds(
        view: View,
        clearWhenUnavailable: Boolean = false
    ) {
        val bounds = readBounds(view)
        if (bounds == null) {
            if (clearWhenUnavailable) {
                lastLockscreenBounds = null
                logger("lockscreen bounds unavailable; cleared cached bounds")
            }
            return
        }

        lastLockscreenBounds = bounds
        logger("lockscreen bounds: $bounds")
    }

    private fun positionFullAod(root: FrameLayout, display: LinearLayout): Boolean {
        lockscreenRef.get()?.let { captureLockscreenBounds(it) }
        val hostBounds = readBounds(root)
        val placement = ModeDisplayPositioner.calculate(lastLockscreenBounds, hostBounds)
        if (placement == null) {
            logger("full AOD placement unavailable: lock=$lastLockscreenBounds host=$hostBounds")
            return false
        }

        val params = (display.layoutParams as? FrameLayout.LayoutParams)
            ?: FrameLayout.LayoutParams(placement.width, placement.height)
        params.gravity = Gravity.TOP or Gravity.START
        params.leftMargin = placement.x
        params.topMargin = placement.y
        params.width = placement.width
        params.height = placement.height
        display.layoutParams = params
        fullAodPositioned = true
        ModeDisplayViewFactory.bind(display.context, display, readState(display.context))
        logger("full AOD placement: $placement")
        return true
    }

    private fun scheduleOneShotPosition(root: FrameLayout, display: LinearLayout) {
        clearPendingPreDraw()
        val rootRef = WeakReference(root)
        val displayRef = WeakReference(display)
        val listener = ViewTreeObserver.OnPreDrawListener {
            val currentRoot = rootRef.get()
            val currentDisplay = displayRef.get()
            clearPendingPreDraw()
            if (currentRoot == null || currentDisplay == null || currentDisplay.parent !== currentRoot) {
                return@OnPreDrawListener true
            }
            fullAodPositioned = positionFullAod(currentRoot, currentDisplay)
            if (!fullAodPositioned) {
                currentDisplay.visibility = View.INVISIBLE
            }
            true
        }
        val attachState = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = Unit

            override fun onViewDetachedFromWindow(v: View) {
                removeFullAodView()
            }
        }
        pendingRootRef = rootRef
        pendingPreDraw = listener
        pendingAttachState = attachState
        root.addOnAttachStateChangeListener(attachState)
        root.viewTreeObserver.addOnPreDrawListener(listener)
    }

    private fun removeFullAodViewFromDifferentRoot(root: FrameLayout) {
        val currentRoot = fullAodRootRef.get()
        if (currentRoot != null && currentRoot !== root) {
            removeFullAodView()
        }
    }

    private fun removeFullAodView() {
        clearPendingPreDraw()
        val display = fullAodRef.get()
        (display?.parent as? ViewGroup)?.removeView(display)
        fullAodRef = WeakReference(null)
        fullAodRootRef = WeakReference(null)
        fullAodPositioned = false
    }

    private fun clearPendingPreDraw() {
        val root = pendingRootRef.get()
        val preDrawListener = pendingPreDraw
        val attachStateListener = pendingAttachState
        if (root != null) {
            if (preDrawListener != null && root.viewTreeObserver.isAlive) {
                root.viewTreeObserver.removeOnPreDrawListener(preDrawListener)
            }
            if (attachStateListener != null) {
                root.removeOnAttachStateChangeListener(attachStateListener)
            }
        }
        pendingRootRef = WeakReference(null)
        pendingPreDraw = null
        pendingAttachState = null
    }

    private fun ensureReceiverRegistered(context: Context) {
        if (receiverRegistered) return
        val appContext = context.applicationContext ?: context
        val stateReceiver = object : BroadcastReceiver() {
            override fun onReceive(receiveContext: Context, intent: Intent) {
                if (intent.action == Protocol.ACTION_MODE_STATE) {
                    refresh(receiveContext)
                }
            }
        }
        appContext.registerReceiver(
            stateReceiver,
            IntentFilter(Protocol.ACTION_MODE_STATE),
            Context.RECEIVER_EXPORTED
        )
        receiver = stateReceiver
        receiverRegistered = true
        logger("mode-state receiver registered")
    }

    companion object {
        private fun screenBounds(view: View): DisplayBounds? {
            if (!view.isLaidOut || view.width <= 0 || view.height <= 0) return null
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            return DisplayBounds(
                x = location[0],
                y = location[1],
                width = view.width,
                height = view.height
            )
        }
    }
}
