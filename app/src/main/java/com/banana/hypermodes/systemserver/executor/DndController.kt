package com.banana.hypermodes.systemserver.executor

import android.app.NotificationManager
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
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
 * Captures the original interruption filter on first apply and restores it on mode exit.
 *
 * @param context System context from system_server
 */
class DndController(private val context: Context) {

    /**
     * Set the DND level to the specified configuration.
     * Captures the original interruption filter on first call.
     *
     * @param level The DND level to apply (NONE, PRIORITY, or ALARMS)
     */
    fun setDndLevel(level: DndLevel) {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // If DND is disabled, force ALL and clear any stale captured original.
            // The original key may still hold the pre-mode value from an earlier active
            // mode; if left uncleared, a later restore() would read it and re-enable DND.
            if (level == DndLevel.DISABLED) {
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                Settings.Global.putString(context.contentResolver, KEY_ORIG_INTERRUPTION_FILTER, null)
                log("setDndLevel: DND disabled, set to INTERRUPTION_FILTER_ALL, original cleared")
                return
            }

            // Capture original interruption filter on first apply
            val current = nm.currentInterruptionFilter
            saveOriginal(KEY_ORIG_INTERRUPTION_FILTER, current)

            val filter = when (level) {
                DndLevel.DISABLED -> NotificationManager.INTERRUPTION_FILTER_ALL
                DndLevel.NONE -> NotificationManager.INTERRUPTION_FILTER_NONE
                DndLevel.PRIORITY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                DndLevel.ALARMS -> NotificationManager.INTERRUPTION_FILTER_ALARMS
            }

            nm.setInterruptionFilter(filter)
            log("setDndLevel: applied DND level $level (filter=$filter), original=$current")

        } catch (e: Exception) {
            log("setDndLevel: failed to set DND level $level: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Restore the original interruption filter that was active before the mode was applied.
     * Falls back to INTERRUPTION_FILTER_ALL if no original was captured.
     */
    fun restore() {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val original = takeOriginal(KEY_ORIG_INTERRUPTION_FILTER)
                ?: NotificationManager.INTERRUPTION_FILTER_ALL

            nm.setInterruptionFilter(original)
            log("restore: restored DND to original filter=$original")

        } catch (e: Exception) {
            log("restore: failed to restore DND: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Force DND off and clear any captured original filter.
     * Used when exiting bedtime mode after the wake alarm is dismissed:
     * the user expects both sleep mode and DND to turn off together.
     */
    fun forceDisableAndClearOriginal() {
        try {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            // 同时关闭 MIUI 勿扰（silence_mode == 4 为勿扰），
            // 否则 DeviceController.restore 恢复完 silence_mode 后勿扰仍会残留。
            Settings.System.putInt(context.contentResolver, "silence_mode", 0)
            context.contentResolver.let { resolver ->
                Settings.Global.putString(resolver, KEY_ORIG_INTERRUPTION_FILTER, null)
            }
            log("forceDisableAndClearOriginal: DND + MIUI silence mode disabled, original filter cleared")
        } catch (e: Exception) {
            log("forceDisableAndClearOriginal: failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun saveOriginal(key: String, value: Int) {
        if (Settings.Global.getInt(context.contentResolver, key, -1) == -1) {
            Settings.Global.putInt(context.contentResolver, key, value)
        }
    }

    private fun takeOriginal(key: String): Int? {
        val v = Settings.Global.getInt(context.contentResolver, key, -1)
        if (v == -1) return null
        Settings.Global.putString(context.contentResolver, key, null)
        return v
    }

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
    }

    companion object {
        private const val TAG = "DndController"
        private const val KEY_ORIG_INTERRUPTION_FILTER = "hypermodes_orig_interruption_filter"
    }
}
