package com.banana.hypermodes.systemserver.executor

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.config.DndLevel

/**
 * Controller for managing Do Not Disturb (DND) settings.
 *
 * This component uses NotificationManager to set the system-wide interruption filter
 * based on the configured DND level. Different levels control which notifications
 * can interrupt the user:
 * - NONE: No interruptions (complete silence)
 * - PRIORITY: Only priority notifications (starred contacts, priority apps)
 * - ALARMS: Only alarms can interrupt
 *
 * @param context System context from system_server
 */
class DndController(private val context: Context) {

    /**
     * Set the DND level to the specified configuration.
     *
     * @param level The DND level to apply (NONE, PRIORITY, or ALARMS)
     */
    fun setDndLevel(level: DndLevel) {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val filter = when (level) {
                DndLevel.DISABLED -> NotificationManager.INTERRUPTION_FILTER_ALL
                DndLevel.NONE -> NotificationManager.INTERRUPTION_FILTER_NONE
                DndLevel.PRIORITY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                DndLevel.ALARMS -> NotificationManager.INTERRUPTION_FILTER_ALARMS
            }

            nm.setInterruptionFilter(filter)
            log("setDndLevel: applied DND level $level (filter=$filter)")

        } catch (e: Exception) {
            log("setDndLevel: failed to set DND level $level: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Restore normal notification behavior by disabling DND.
     * This allows all notifications to come through.
     */
    fun restore() {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            log("restore: DND disabled, all notifications allowed")

        } catch (e: Exception) {
            log("restore: failed to restore DND: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "DndController"
    }
}
