package com.banana.hypermodes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.DeskClockState

/**
 * Receives the DeskClock hook's push when the OFFICIAL bedtime state changes
 * (scheduled sleep/wake alarms, or the user toggling bedtime inside the Clock
 * app). Manifest-registered so it works even when our UI isn't running —
 * the system_server keep-alive hooks allow this broadcast to cold-start us.
 *
 * Syncs both the live compose state (DeskClockState.bedtimeActive) and the
 * persisted mode list, so the home page shows 已启用 and the detail page's
 * 立即开启 button flips to 关闭 the moment bedtime officially starts.
 */
class BedtimeStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Protocol.ACTION_BEDTIME_ACTIVE) return
        val active = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)

        DeskClockState.updateBedtimeActive(context, active)

        // Persist into the mode list so a cold-started UI is already correct.
        val modes = ModeStore.load(context) { DefaultModes.get() }.toMutableList()
        val idx = modes.indexOfFirst { it.id == "bedtime" }
        if (idx >= 0 && modes[idx].enabled != active) {
            modes[idx] = modes[idx].copy(enabled = active)
            ModeStore.save(context, modes)
        }
    }
}
