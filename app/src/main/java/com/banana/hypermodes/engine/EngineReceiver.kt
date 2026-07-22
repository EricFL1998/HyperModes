package com.banana.hypermodes.engine

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.protocol.Protocol
import java.time.ZoneId
import kotlin.concurrent.thread

/**
 * Stateless engine entry points (manifest-registered, cold-start safe):
 *
 * - ACTION_RESCHEDULE: re-arm every mode's next alarm (sent by the UI after
 *   any mode save/delete; also called by BootReceiver/TimeChangedReceiver).
 * - ACTION_ALARM_TRIGGER: AlarmManager fired — activate/deactivate the mode,
 *   persist the new enabled flag, re-arm the next trigger, notify the UI.
 *
 * Alarms use setExactAndAllowWhileIdle so they fire in doze; falls back to
 * inexact when SCHEDULE_EXACT_ALARM is not granted.
 */
class EngineReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        Log.i(TAG, "onReceive: action=${intent.action}")
        when (intent.action) {
            Protocol.ACTION_RESCHEDULE -> rescheduleAll(context)
            Protocol.ACTION_ALARM_TRIGGER -> {
                // Engine work (root shell fallbacks, settings writes) may
                // block — move it off the main thread.
                val pending = goAsync()
                val appContext = context.applicationContext
                thread {
                    try {
                        onAlarmTrigger(appContext, intent)
                    } finally {
                        pending.finish()
                    }
                }
            }
        }
    }

    private fun onAlarmTrigger(context: Context, intent: Intent) {
        val modeId = intent.getStringExtra(Protocol.EXTRA_MODE_ID) ?: return
        val isStart = intent.getStringExtra(Protocol.EXTRA_TRIGGER) == TRIGGER_START
        Log.i(TAG, "onAlarmTrigger: modeId=$modeId isStart=$isStart")
        val modes = ModeStore.load(context) { DefaultModes.get() }.toMutableList()
        val idx = modes.indexOfFirst { it.id == modeId }
        if (idx < 0) {
            // Mode was deleted while armed — drop the stale alarm.
            rescheduleAll(context)
            return
        }
        val mode = modes[idx]
        val engine = ModeEngine(context)
        when {
            isStart && !mode.enabled -> {
                Log.i(TAG, "activating mode $modeId")
                engine.activate(mode)
                modes[idx] = mode.copy(enabled = true)
                ModeStore.save(context, modes)
            }
            !isStart && mode.enabled -> {
                Log.i(TAG, "deactivating mode $modeId")
                engine.deactivate(mode)
                modes[idx] = mode.copy(enabled = false)
                ModeStore.save(context, modes)
            }
        }
        rescheduleAll(context)
        context.sendBroadcast(
            Intent(Protocol.ACTION_MODE_STATE).setPackage(context.packageName)
        )
    }

    companion object {
        private const val TAG = "EngineReceiver"
        const val TRIGGER_START = "start"
        const val TRIGGER_END = "end"
        private const val PREFS = "engine_alarms"
        private const val KEY_ARMED = "armed_ids"

        /** Re-arm the next alarm for every scheduled mode (except bedtime,
         *  which is driven by DeskClock's own alarms). */
        fun rescheduleAll(context: Context) {
            Log.i(TAG, "rescheduleAll called")
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val previouslyArmed =
                prefs.getStringSet(KEY_ARMED, emptySet())?.toSet() ?: emptySet()

            val now = System.currentTimeMillis()
            val zone = ZoneId.systemDefault()
            val armed = mutableSetOf<String>()

            ModeStore.load(context) { DefaultModes.get() }.forEach { mode ->
                val schedule = mode.settings.schedule
                if (mode.id == "bedtime" || schedule == null) return@forEach
                val next = ModeScheduler.nextTrigger(schedule, now, zone) ?: return@forEach
                val trigger = when (next.trigger) {
                    ModeScheduler.Trigger.START -> TRIGGER_START
                    ModeScheduler.Trigger.END -> TRIGGER_END
                }
                val pi = pendingIntent(context, mode.id, trigger)
                if (canScheduleExact(alarmManager)) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, next.epochMillis, pi
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, next.epochMillis, pi
                    )
                }
                Log.i(TAG, "armed ${mode.id} $trigger at ${java.util.Date(next.epochMillis)}")
                armed += mode.id
            }

            // Cancel alarms of modes that are no longer scheduled (deleted
            // or schedule disabled).
            (previouslyArmed - armed).forEach { id ->
                alarmManager.cancel(pendingIntent(context, id, null))
                Log.i(TAG, "cancelled alarm for $id")
            }
            prefs.edit().putStringSet(KEY_ARMED, armed).apply()
            Log.i(TAG, "rescheduleAll complete: armed=$armed")
        }

        private fun canScheduleExact(alarmManager: AlarmManager): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    alarmManager.canScheduleExactAlarms()

        private fun pendingIntent(
            context: Context, modeId: String, trigger: String?
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                modeId.hashCode(),
                Intent(Protocol.ACTION_ALARM_TRIGGER)
                    .setPackage(context.packageName)
                    .putExtra(Protocol.EXTRA_MODE_ID, modeId)
                    .putExtra(Protocol.EXTRA_TRIGGER, trigger),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    }
}
