package com.banana.hypermodes.systemserver.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.banana.hypermodes.protocol.Protocol

/**
 * Receives geofence events from Polaris and forwards to LocationTriggerManager.
 * Verifies sender UID/package, exact active fence ID, and event type before forwarding.
 */
class PolarisCallbackReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "PolarisCallbackReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val sentFromPackage = getSentFromPackage()
        val sentFromUid = getSentFromUid()
        val packagesForUid = context.packageManager.getPackagesForUid(sentFromUid)

        forwardValidated(context, intent, sentFromPackage, packagesForUid)
    }

    /**
     * Get the package name that sent this broadcast.
     */
    override fun getSentFromPackage(): String? {
        return try {
            super.getSentFromPackage()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get sent-from package", e)
            null
        }
    }

    /**
     * Get the UID that sent this broadcast.
     */
    override fun getSentFromUid(): Int {
        return try {
            super.getSentFromUid()
        } catch (e: Exception) {
            Log.w(TAG, "Failed to get sent-from UID", e)
            -1
        }
    }

    /**
     * Internal helper for deterministic testing.
     * Validates sender and payload, then forwards via internal broadcast.
     */
    internal fun forwardValidated(
        context: Context,
        intent: Intent,
        sentFromPackage: String?,
        packagesForUid: Array<String>?
    ) {
        // Verify sender is Polaris
        if (!PolarisContract.senderIsPolaris(sentFromPackage, packagesForUid)) {
            Log.w(TAG, "Rejected callback from unauthorized package: $sentFromPackage")
            return
        }

        // Extract and validate callback payload
        val fenceId = intent.getStringExtra(PolarisContract.EXTRA_FENCE_ID)
        val event = intent.getIntExtra(PolarisContract.EXTRA_EVENT, -1)
        val callback = PolarisContract.parseCallback(fenceId, event)

        if (callback == null) {
            Log.w(TAG, "Invalid geofence callback: fenceId=$fenceId, event=$event")
            return
        }

        Log.i(TAG, "Forwarding validated geofence event: ${callback.fenceId}, event=${callback.event}")

        // Forward via internal broadcast to system_server
        context.sendBroadcast(
            Intent(Protocol.ACTION_POLARIS_GEOFENCE_EVENT)
                .setPackage(Protocol.FRAMEWORK_PACKAGE)
                .putExtra(Protocol.EXTRA_POLARIS_FENCE_ID, callback.fenceId)
                .putExtra(Protocol.EXTRA_POLARIS_EVENT, callback.event)
        )
    }
}
