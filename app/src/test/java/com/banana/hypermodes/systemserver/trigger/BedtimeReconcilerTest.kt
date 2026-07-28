package com.banana.hypermodes.systemserver.trigger

import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler.Command
import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler.Decision
import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler.Event
import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler.Reason
import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler.Snapshot
import org.junit.Assert.assertEquals
import org.junit.Test

class BedtimeReconcilerTest {

    private val active = Snapshot(
        bedtimeModeExists = true, modeActive = true,
        modeActivatedAt = 1_000L, dismissedAt = null
    )
    private val idle = Snapshot(
        bedtimeModeExists = true, modeActive = false,
        modeActivatedAt = 0L, dismissedAt = null
    )

    private fun signal(on: Boolean, reason: Reason, at: Long) =
        Event.DeskClockSignal(on, reason, at)

    // --- Rule 1: DeskClock ON => mode ON ---

    @Test
    fun `zen entered while idle activates mode`() {
        assertEquals(
            listOf(Decision.ActivateMode),
            BedtimeReconciler.decide(signal(true, Reason.ZEN_ENTERED, 2_000L), idle)
        )
    }

    @Test
    fun `zen entered while already active is ignored`() {
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(true, Reason.ZEN_ENTERED, 2_000L), active)
        )
    }

    @Test
    fun `stale on-signal does not re-activate a dismissed mode`() {
        val s = idle.copy(dismissedAt = 3_000L)
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(true, Reason.ZEN_ENTERED, 2_000L), s)
        )
    }

    @Test
    fun `fresh on-signal after dismiss activates (next night works)`() {
        val s = idle.copy(dismissedAt = 2_000L)
        assertEquals(
            listOf(Decision.ActivateMode),
            BedtimeReconciler.decide(signal(true, Reason.ZEN_ENTERED, 3_000L), s)
        )
    }

    @Test
    fun `on-signal with no bedtime mode configured is ignored`() {
        val s = idle.copy(bedtimeModeExists = false)
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(true, Reason.ZEN_ENTERED, 2_000L), s)
        )
    }

    // --- Rule 2: dismiss / generic exit => system deactivation, no dismiss record ---

    @Test
    fun `alarm dismissed deactivates without dismiss record`() {
        assertEquals(
            listOf(Decision.DeactivateMode(recordDismiss = false)),
            BedtimeReconciler.decide(signal(false, Reason.ALARM_DISMISSED, 2_000L), active)
        )
    }

    @Test
    fun `zen exited deactivates without dismiss record`() {
        assertEquals(
            listOf(Decision.DeactivateMode(recordDismiss = false)),
            BedtimeReconciler.decide(signal(false, Reason.ZEN_EXITED, 2_000L), active)
        )
    }

    @Test
    fun `alarm permanently disabled deactivates without dismiss record`() {
        assertEquals(
            listOf(Decision.DeactivateMode(recordDismiss = false)),
            BedtimeReconciler.decide(signal(false, Reason.ALARM_DISABLED, 2_000L), active)
        )
    }

    // --- Rule 3: skip-once ---

    @Test
    fun `skip once while active deactivates with dismiss record`() {
        assertEquals(
            listOf(Decision.DeactivateMode(recordDismiss = true)),
            BedtimeReconciler.decide(signal(false, Reason.SKIP_ONCE_ACTIVE, 2_000L), active)
        )
    }

    @Test
    fun `skip once while idle does nothing`() {
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(false, Reason.SKIP_ONCE_IDLE, 2_000L), idle)
        )
    }

    @Test
    fun `skip once idle does not tear down an active mode`() {
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(false, Reason.SKIP_ONCE_IDLE, 2_000L), active)
        )
    }

    // --- Staleness / unknown state ---

    @Test
    fun `stale off-signal does not tear down a freshly activated mode`() {
        // signal predates modeActivatedAt=1000
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(false, Reason.ZEN_EXITED, 500L), active)
        )
    }

    @Test
    fun `off-signal while inactive is a no-op`() {
        assertEquals(
            emptyList<Decision>(),
            BedtimeReconciler.decide(signal(false, Reason.ZEN_EXITED, 2_000L), idle)
        )
    }

    // --- Rule 4/5: manual toggles via config ---

    @Test
    fun `manual activation sends start and enable, never skip`() {
        assertEquals(
            listOf(
                Decision.SendDeskClockCommand(Command.START_BEDTIME),
                Decision.SendDeskClockCommand(Command.ENABLE_WAKE_ALARM)
            ),
            BedtimeReconciler.decide(Event.ModeActivatedViaConfig(2_000L), idle)
        )
    }

    @Test
    fun `manual deactivation inside window sends skip once`() {
        assertEquals(
            listOf(Decision.SendDeskClockCommand(Command.SKIP_WAKE_ALARM_ONCE)),
            BedtimeReconciler.decide(
                Event.ModeDeactivatedViaConfig(2_000L, inSleepWindow = true), active
            )
        )
    }

    @Test
    fun `manual deactivation outside window sends exit, never skip`() {
        assertEquals(
            listOf(Decision.SendDeskClockCommand(Command.EXIT_BEDTIME)),
            BedtimeReconciler.decide(
                Event.ModeDeactivatedViaConfig(2_000L, inSleepWindow = false), active
            )
        )
    }

    // --- Reason parsing (backward compat with older hook builds) ---

    @Test
    fun `reason fromString maps names and falls back to state`() {
        assertEquals(Reason.ZEN_ENTERED, Reason.fromString("ZEN_ENTERED", true))
        assertEquals(Reason.SKIP_ONCE_ACTIVE, Reason.fromString("SKIP_ONCE_ACTIVE", false))
        assertEquals(Reason.ZEN_ENTERED, Reason.fromString(null, true))
        assertEquals(Reason.ZEN_EXITED, Reason.fromString(null, false))
        assertEquals(Reason.ZEN_EXITED, Reason.fromString("garbage", false))
    }
}
