package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.RoutineCoreEngine

/**
 * I/O shell: receives DeskClock bedtime-state broadcasts and forwards them to
 * RoutineCoreEngine.onBedtimeSignal. All decisions live in BedtimeReconciler;
 * this class owns no state and no policy.
 */
class BedtimeListener(
    private val context: Context,
    private val engine: RoutineCoreEngine,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var receiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != Protocol.ACTION_BEDTIME_ACTIVE) return
            // 只接受 DeskClock 发来的信号，防止任意 App 伪造切换睡眠模式。
            val sender = runCatching { getSentFromPackage() }.getOrNull()
            if (sender != Protocol.TARGET_PACKAGE) {
                log("Rejected bedtime signal from ${sender ?: "unknown"}")
                return
            }
            val active = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)
            val reason = intent.getStringExtra(Protocol.EXTRA_BEDTIME_REASON)
            val eventTime = intent.getLongExtra(Protocol.EXTRA_EVENT_TIME, 0L)
            log("Received bedtime signal: active=$active reason=$reason")
            engine.onBedtimeSignal(active, reason, eventTime)
        }
    }

    fun registerStateSources() {
        if (receiverRegistered) return
        try {
            val filter = IntentFilter(Protocol.ACTION_BEDTIME_ACTIVE)
            context.registerReceiver(receiver, filter, null, handler, Context.RECEIVER_EXPORTED)
            receiverRegistered = true
            log("Bedtime signal receiver registered")
        } catch (e: Exception) {
            log("Failed to register bedtime signal receiver: ${e.message}")
        }
    }

    fun cleanup() {
        if (receiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
                receiverRegistered = false
                log("Bedtime signal receiver unregistered")
            } catch (e: Exception) {
                log("Failed to unregister bedtime signal receiver: ${e.message}")
            }
        }
    }

    /** Clean up package-removal resources without normal mode deactivation. */
    fun cleanupForPackageRemoval() = cleanup()

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
    }

    companion object {
        private const val TAG = "BedtimeListener"
    }
}
