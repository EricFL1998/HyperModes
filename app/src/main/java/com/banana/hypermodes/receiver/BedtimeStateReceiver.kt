package com.banana.hypermodes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.engine.ModeEngine
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.DeskClockState

/**
 * Receives the DeskClock hook's push when the OFFICIAL bedtime state changes
 * (scheduled sleep/wake alarms, or the user toggling bedtime inside the Clock
 * app). Manifest-registered so it works even when our UI isn't running —
 * the system_server keep-alive hooks allow this broadcast to cold-start us.
 *
 * Beyond syncing the toggle, this is where bedtime's EXTRA settings
 * (DND policy, grayscale, ...) get applied on scheduled activation:
 * the engine runs with skipBedtimeTrigger = true (DeskClock is already
 * driving the bedtime itself — re-sending START_BEDTIME would loop).
 *
 * Idempotency: if ModeStore already shows the same enabled flag, we
 * initiated the change ourselves (manual toggle already ran the engine)
 * and there is nothing to do.
 */
class BedtimeStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Protocol.ACTION_BEDTIME_ACTIVE) return
        val active = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)

        DeskClockState.updateBedtimeActive(context, active)

        val modes = ModeStore.load(context) { DefaultModes.get() }.toMutableList()
        val idx = modes.indexOfFirst { it.id == "bedtime" }
        if (idx < 0) return
        if (modes[idx].enabled == active) return // we initiated this ourselves

        val updated = modes[idx].copy(enabled = active)
        modes[idx] = updated
        ModeStore.save(context, modes)

        val engine = ModeEngine(context)
        if (active) {
            engine.activate(updated, skipBedtimeTrigger = true)
        } else {
            engine.deactivate(updated, skipBedtimeTrigger = true)
        }
        context.sendBroadcast(
            Intent(Protocol.ACTION_MODE_STATE).setPackage(context.packageName)
        )
    }
}
