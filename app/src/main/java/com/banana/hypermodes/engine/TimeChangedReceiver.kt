package com.banana.hypermodes.engine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Wall-clock changed (time set / timezone / date) — every computed alarm is
 * now wrong, so re-arm all of them.
 */
class TimeChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        EngineReceiver.rescheduleAll(context)
    }
}
