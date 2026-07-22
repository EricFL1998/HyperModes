package com.banana.hypermodes.driving

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.ActivityTransitionEvent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

/**
 * Receives Activity Recognition IN_VEHICLE transitions (fired by Play
 * Services via PendingIntent, even when the app process is dead).
 */
class ActivityTransitionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != DrivingDetector.ACTION_TRANSITION) return
        if (!ActivityTransitionResult.hasResult(intent)) return

        val result = ActivityTransitionResult.extractResult(intent) ?: return
        for (event: ActivityTransitionEvent in result.transitionEvents) {
            if (event.activityType != DetectedActivity.IN_VEHICLE) continue
            Log.i(TAG, "IN_VEHICLE transition: ${event.transitionType}")
            when (event.transitionType) {
                com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_ENTER ->
                    DrivingDetector.onVehicleEnter(context)
                com.google.android.gms.location.ActivityTransition.ACTIVITY_TRANSITION_EXIT ->
                    DrivingDetector.onVehicleExit(context)
            }
        }
    }

    private companion object {
        const val TAG = "HyperModes"
    }
}
