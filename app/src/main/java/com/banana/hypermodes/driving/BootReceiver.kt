package com.banana.hypermodes.driving

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-registers driving motion detection after reboot so the feature works
 * without the user ever opening the app.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            DrivingDetector.ensureActivityRecognition(context)
            // Alarms don't survive reboot — re-arm every scheduled mode.
            com.banana.hypermodes.engine.EngineReceiver.rescheduleAll(context)
        }
    }
}
