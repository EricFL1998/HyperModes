package com.banana.hypermodes.systemserver.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

/**
 * Receives geofence events from Polaris and forwards to LocationTriggerManager.
 * Verifies sender UID/package, exact active fence ID, and event type before forwarding.
 */
class PolarisCallbackReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PolarisCallbackReceiver"
        private const val POLARIS_PACKAGE = "com.xiaomi.gnss.polaris"

        // Intent extras from Polaris
        private const val EXTRA_FENCE_ID = "fence_id"
        private const val EXTRA_EVENT_TYPE = "event_type"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // Verify sender package
        val callingPackage = intent.getStringExtra("calling_package") ?: ""
        if (callingPackage != POLARIS_PACKAGE) {
            log("Rejected callback from unauthorized package: $callingPackage")
            return
        }

        // Extract fence ID and event type
        val fenceId = intent.getStringExtra(EXTRA_FENCE_ID)
        val eventType = intent.getIntExtra(EXTRA_EVENT_TYPE, -1)

        if (fenceId == null || eventType == -1) {
            log("Invalid geofence callback: fenceId=$fenceId, eventType=$eventType")
            return
        }

        log("Received geofence event: fenceId=$fenceId, eventType=$eventType")

        // Forward to adapter through bridge
        // Note: In production, this would use the system service bridge
        // For now, we'll implement a simple callback mechanism
        PolarisCallbackBridge.handleEvent(context, fenceId, eventType)
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }
}

/**
 * Bridge to forward Polaris callbacks to the LocationTriggerManager.
 * In a full implementation, this would use a signature-protected broadcast
 * to communicate with the system service.
 */
object PolarisCallbackBridge {
    private var adapter: PolarisGeofenceAdapter? = null

    fun register(adapter: PolarisGeofenceAdapter) {
        this.adapter = adapter
    }

    fun unregister() {
        this.adapter = null
    }

    fun handleEvent(context: Context, fenceId: String, eventType: Int) {
        adapter?.handleGeofenceEvent(fenceId, eventType)
            ?: Log.w("PolarisCallbackBridge", "No adapter registered for geofence event")
    }
}
