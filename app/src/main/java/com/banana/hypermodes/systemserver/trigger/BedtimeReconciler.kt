package com.banana.hypermodes.systemserver.trigger

/**
 * Pure decision logic for bedtime mode <-> DeskClock synchronization.
 * No Android dependencies, no internal state — every decision is derived
 * solely from the event and the caller-supplied snapshot, so it is fully
 * JVM-testable.
 *
 * Contract (docs/superpowers/specs/2026-07-28-bedtime-sync-rewrite-design.md):
 * DeskClock is the source of truth; an event only wins if it is newer than
 * the event it contradicts.
 */
object BedtimeReconciler {

    enum class Reason {
        ZEN_ENTERED, ZEN_EXITED, ALARM_DISMISSED,
        SKIP_ONCE_ACTIVE, SKIP_ONCE_IDLE,
        ALARM_DISABLED;

        companion object {
            /** Backward-compatible mapping for signals from older hook builds
             * that carry no reason extra: infer from the state alone. */
            fun fromString(value: String?, active: Boolean): Reason = when (value) {
                "ZEN_ENTERED" -> ZEN_ENTERED
                "ZEN_EXITED" -> ZEN_EXITED
                "ALARM_DISMISSED" -> ALARM_DISMISSED
                "SKIP_ONCE_ACTIVE" -> SKIP_ONCE_ACTIVE
                "SKIP_ONCE_IDLE" -> SKIP_ONCE_IDLE
                "ALARM_DISABLED" -> ALARM_DISABLED
                else -> if (active) ZEN_ENTERED else ZEN_EXITED
            }
        }
    }

    enum class Command { START_BEDTIME, SKIP_WAKE_ALARM_ONCE, EXIT_BEDTIME, ENABLE_WAKE_ALARM }

    sealed interface Event {
        /** A state signal from DeskClock. [receivedAt] is stamped in
         * system_server on receipt — sender clocks are never trusted. */
        data class DeskClockSignal(val active: Boolean, val reason: Reason, val receivedAt: Long) : Event
        /** Bedtime mode activated via config (manual UI toggle / config restore). */
        data class ModeActivatedViaConfig(val receivedAt: Long) : Event
        /** Bedtime mode deactivated via config (manual UI toggle). */
        data class ModeDeactivatedViaConfig(val receivedAt: Long, val inSleepWindow: Boolean) : Event
    }

    sealed interface Decision {
        /** Activate the bedtime mode (engine clears dismiss, applies, persists). */
        object ActivateMode : Decision
        /** Deactivate; [recordDismiss] mirrors the engine's isManualDismiss. */
        data class DeactivateMode(val recordDismiss: Boolean) : Decision
        /** Send a command to DeskClock. */
        data class SendDeskClockCommand(val command: Command) : Decision
    }

    data class Snapshot(
        val bedtimeModeExists: Boolean,
        val modeActive: Boolean,
        /** When the bedtime mode was activated (ms); 0 when not active. */
        val modeActivatedAt: Long,
        /** Manual-dismiss record for the bedtime mode, if any. */
        val dismissedAt: Long?
    )

    fun decide(event: Event, s: Snapshot): List<Decision> = when (event) {
        is Event.DeskClockSignal -> onSignal(event, s)
        is Event.ModeActivatedViaConfig ->
            if (!s.bedtimeModeExists) emptyList()
            else listOf(
                Decision.SendDeskClockCommand(Command.START_BEDTIME),
                Decision.SendDeskClockCommand(Command.ENABLE_WAKE_ALARM)
            )
        is Event.ModeDeactivatedViaConfig ->
            if (!s.bedtimeModeExists) emptyList()
            else listOf(
                Decision.SendDeskClockCommand(
                    if (event.inSleepWindow) Command.SKIP_WAKE_ALARM_ONCE
                    else Command.EXIT_BEDTIME
                )
            )
    }

    private fun onSignal(e: Event.DeskClockSignal, s: Snapshot): List<Decision> {
        if (!s.bedtimeModeExists) return emptyList()
        return if (e.active) {
            when {
                s.modeActive -> emptyList()
                // Stale ON: the user dismissed after this signal was sent.
                s.dismissedAt != null && s.dismissedAt >= e.receivedAt -> emptyList()
                else -> listOf(Decision.ActivateMode)
            }
        } else {
            when {
                // DeskClock skips the whole next bedtime itself — nothing to do.
                e.reason == Reason.SKIP_ONCE_IDLE -> emptyList()
                !s.modeActive -> emptyList()
                // Stale OFF: predates the current activation.
                e.receivedAt < s.modeActivatedAt -> emptyList()
                e.reason == Reason.SKIP_ONCE_ACTIVE ->
                    listOf(Decision.DeactivateMode(recordDismiss = true))
                else -> listOf(Decision.DeactivateMode(recordDismiss = false))
            }
        }
    }
}
