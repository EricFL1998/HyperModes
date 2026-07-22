package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine

/**
 * Receives scheduled mode alarms and triggers mode activation/deactivation.
 * This receiver is triggered by AlarmManager at scheduled times.
 */
class ModeAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val modeId = intent.getStringExtra(ScheduledModeManager.EXTRA_MODE_ID)
        if (modeId == null) {
            log("Received alarm with no mode ID, ignoring")
            return
        }

        when (intent.action) {
            ScheduledModeManager.ACTION_START_MODE -> {
                log("Received START alarm for mode: $modeId")
                try {
                    RoutineCoreEngine.getInstance().activateMode(modeId)
                } catch (e: Exception) {
                    log("Failed to activate mode: ${e.message}")
                    e.printStackTrace()
                }
            }

            ScheduledModeManager.ACTION_END_MODE -> {
                log("Received END alarm for mode: $modeId")
                try {
                    RoutineCoreEngine.getInstance().deactivateMode(modeId)
                } catch (e: Exception) {
                    log("Failed to deactivate mode: ${e.message}")
                    e.printStackTrace()
                }
            }

            else -> {
                log("Received alarm with unknown action: ${intent.action}")
            }
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "ModeAlarmReceiver"
    }
}
