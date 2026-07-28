package com.banana.hypermodes.hook.modedisplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.banana.hypermodes.protocol.Protocol
import java.lang.ref.WeakReference
import kotlin.math.roundToInt

/**
 * Owns the single mode-display view. On the lockscreen it lives in the keyguard
 * indication area; entering Full-AOD it is re-parented into the notification
 * panel — which never fades and inherits native burn-in translation — so the
 * text itself rides into the AOD position instead of fading out and being
 * redrawn. On wake it is restored to the indication area untouched.
 */
class ModeDisplayCoordinator(
    private val readState: (Context) -> ModeDisplayState? = ModeDisplayStateReader::read,
    private val readBounds: (View) -> DisplayBounds? = ::screenBounds,
    private val logger: (String) -> Unit
) {
    private var lockscreenRef = WeakReference<LinearLayout>(null)
    private var lockscreenLayoutListener: View.OnLayoutChangeListener? = null
    private var lastLockscreenBounds: DisplayBounds? = null
    private var receiverRegistered = false
    private var receiver: BroadcastReceiver? = null

    private var panelRef = WeakReference<ViewGroup>(null)
    private var depthMode = false
    private var parkedHome: ParkedHome? = null
    private var depthTargetX = 0f
    private var depthTargetY = 0f

    private data class ParkedHome(
        val parent: ViewGroup,
        val index: Int,
        val layoutParams: ViewGroup.LayoutParams
    )

    fun attachLockscreenDisplay(view: LinearLayout) {
        val previous = lockscreenRef.get()
        if (previous !== view) {
            lockscreenLayoutListener?.let { previous?.removeOnLayoutChangeListener(it) }
            lockscreenRef = WeakReference(view)
            lockscreenLayoutListener = null
        }
        if (!isParked()) {
            captureLockscreenBounds(view, clearWhenUnavailable = previous !== view)
        }
        if (previous !== view) {
            val listener = View.OnLayoutChangeListener { current, _, _, _, _, _, _, _, _ ->
                if (!isParked()) {
                    // Keep the last good bounds when a read fails (e.g. the
                    // view is momentarily unlaid-out right after a restore);
                    // otherwise a doze restart without a lockscreen showing in
                    // between would have no position to park at. A genuinely
                    // new view clears stale bounds in the attach path above.
                    captureLockscreenBounds(current, clearWhenUnavailable = false)
                }
            }
            view.addOnLayoutChangeListener(listener)
            lockscreenLayoutListener = listener
        }
        ensureReceiverRegistered(view.context)
        refresh(view.context)
        logger("lockscreen attached: view=$view")
    }

    /** The managed display view, wherever it is currently parented. */
    fun peekDisplay(): LinearLayout? = lockscreenRef.get()

    fun isParked(): Boolean = parkedHome != null

    fun updatePanelHost(panel: ViewGroup?, isDepthMode: Boolean) {
        if (panel == null) return
        val changed = panelRef.get() !== panel || depthMode != isDepthMode
        panelRef = WeakReference(panel)
        depthMode = isDepthMode
        if (changed) logger("panel host updated: panel=$panel depthMode=$isDepthMode")
    }

    fun onFullAodStarted(isFullAod: Boolean) {
        logger("dream start: fullAod=$isFullAod parked=${isParked()}")
        if (!isFullAod) {
            restoreCopyToHome()
            return
        }
        parkOrReassert()
    }

    fun onFullAodStopped() {
        logger("dream stop")
        restoreCopyToHome()
        refreshDisplay()
    }

    fun refresh(context: Context) {
        refreshDisplay()
        logger("display refresh: active=${readState(context) != null}")
    }

    private fun refreshDisplay() {
        val view = lockscreenRef.get() ?: return
        ModeDisplayViewFactory.bind(view.context, view, readState(view.context))
        if (!isParked()) {
            captureLockscreenBounds(view)
        }
    }

    private fun parkOrReassert() {
        val view = lockscreenRef.get() ?: run {
            logger("park skipped: no lockscreen display")
            return
        }
        val panel = panelRef.get() ?: run {
            logger("park skipped: no panel host")
            return
        }
        if (panel.width <= 0 || panel.height <= 0) {
            logger("park skipped: panel not laid out")
            return
        }
        if (view.parent === panel) {
            val parkedBounds = lastLockscreenBounds ?: run {
                logger("reassert skipped: no lockscreen bounds")
                return
            }
            reassertParked(view, panel, parkedBounds)
            return
        }

        val homeParent = view.parent as? ViewGroup ?: run {
            logger("park skipped: display has no parent")
            return
        }
        // Use the last steady-lockscreen bounds. By dream start the bottom area
        // is already mid-transition, so a fresh on-screen read would be
        // distorted, and once parked a fresh read would describe the panel
        // position rather than the lockscreen home.
        val bounds = lastLockscreenBounds ?: run {
            logger("park skipped: no lockscreen bounds")
            return
        }
        val raw = ModeDisplayPositioner.calculateRaw(bounds, panelBounds(panel)) ?: run {
            logger("park skipped: raw placement unavailable: lock=$bounds panel=${panelBounds(panel)}")
            return
        }

        parkedHome = ParkedHome(homeParent, homeParent.indexOfChild(view), view.layoutParams)
        homeParent.removeView(view)
        val params = FrameLayout.LayoutParams(raw.width, raw.height, Gravity.TOP or Gravity.START)
        params.leftMargin = raw.x
        params.topMargin = raw.y
        panel.addView(view, params)
        val applied = view.layoutParams as? FrameLayout.LayoutParams
        logger(
            "display parked in panel at raw $raw depthMode=$depthMode " +
                "appliedMargins=(${applied?.leftMargin}, ${applied?.topMargin})"
        )

        if (depthMode) {
            // Depth-video mode scales only the bottom area and status bar, not
            // the panel, so mirror the native shrink trajectory manually.
            val endpoint = ModeDisplayPositioner.calculate(bounds, panelBounds(panel))
            depthTargetX = endpoint?.let { (it.x - raw.x).toFloat() } ?: 0f
            depthTargetY = endpoint?.let { (it.y - raw.y).toFloat() } ?: 0f
            view.translationX = 0f
            view.translationY = 0f
            view.animate()
                .translationX(depthTargetX)
                .translationY(depthTargetY)
                .setDuration(DEPTH_GLIDE_DURATION_MS)
                .setInterpolator(DecelerateInterpolator(1f))
                .start()
            logger("depth glide to ($depthTargetX, $depthTargetY)")
        }
    }

    private fun reassertParked(view: LinearLayout, panel: ViewGroup, bounds: DisplayBounds) {
        val host = panelBounds(panel)
        if (depthMode) {
            val raw = ModeDisplayPositioner.calculateRaw(bounds, host)
            val endpoint = ModeDisplayPositioner.calculate(bounds, host)
            if (raw != null && endpoint != null) {
                depthTargetX = (endpoint.x - raw.x).toFloat()
                depthTargetY = (endpoint.y - raw.y).toFloat()
            }
            val changed = view.translationX != depthTargetX || view.translationY != depthTargetY
            view.translationX = depthTargetX
            view.translationY = depthTargetY
            if (changed) logger("depth reassert: ($depthTargetX, $depthTargetY)")
            return
        }

        val endpoint = ModeDisplayPositioner.calculate(bounds, host) ?: run {
            logger("reassert skipped: endpoint unavailable")
            return
        }
        val params = (view.layoutParams as? FrameLayout.LayoutParams)
            ?: FrameLayout.LayoutParams(endpoint.width, endpoint.height)
        params.gravity = Gravity.TOP or Gravity.START
        // Undo the panel's live native transform so the rendered position lands
        // on the shrink endpoint. Reduces to raw coordinates at scale 0.95 and
        // to the endpoint itself when the panel is unscaled.
        val scaleX = panel.scaleX.takeIf { it > 0f } ?: 1f
        val scaleY = panel.scaleY.takeIf { it > 0f } ?: 1f
        val newLeft = (panel.pivotX + (endpoint.x - panel.pivotX) / scaleX).roundToInt()
        val newTop = (panel.pivotY + (endpoint.y - panel.pivotY) / scaleY).roundToInt()
        val changed = params.leftMargin != newLeft || params.topMargin != newTop
        params.leftMargin = newLeft
        params.topMargin = newTop
        params.width = endpoint.width
        params.height = endpoint.height
        view.layoutParams = params
        if (changed) {
            logger("panel reassert: margins=($newLeft, $newTop) scale=$scaleY")
        }
    }

    private fun restoreCopyToHome() {
        val home = parkedHome ?: return
        parkedHome = null
        val view = lockscreenRef.get()
        if (view == null) {
            logger("restore skipped: display view lost")
            return
        }
        view.animate().cancel()
        view.translationX = 0f
        view.translationY = 0f
        (view.parent as? ViewGroup)?.removeView(view)
        home.parent.addView(view, home.index.coerceAtMost(home.parent.childCount), home.layoutParams)
        logger("display restored to lockscreen home")
    }

    private fun panelBounds(panel: ViewGroup): DisplayBounds =
        readBounds(panel) ?: DisplayBounds(0, 0, panel.width, panel.height)

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

        if (bounds != lastLockscreenBounds) {
            lastLockscreenBounds = bounds
            logger("lockscreen bounds: $bounds")
        }
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
        private const val DEPTH_GLIDE_DURATION_MS = 580L

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
