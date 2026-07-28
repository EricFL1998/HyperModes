# Bedtime ↔ DeskClock Sync Rewrite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rewrite bedtime mode ↔ DeskClock synchronization so DeskClock is the single source of truth, eliminating the on/off flapping and the dark-mode-revert-needs-lock bug.

**Architecture:** A stateless, pure-Kotlin `BedtimeReconciler` in system_server maps every event (DeskClock state signal, manual config toggle) to a list of decisions. `RoutineCoreEngine` executes decisions; `BedtimeListener` becomes a dumb broadcast→event shell; the UI mirrors engine state only. Spec: `docs/superpowers/specs/2026-07-28-bedtime-sync-rewrite-design.md`.

**Tech Stack:** Kotlin, Android (minSdk 35), LSPosed/libxposed hooks, JUnit4 JVM tests (`./gradlew :app:testDebugUnitTest`).

## Global Constraints

- All decision timestamps are taken in **system_server on receipt**; DeskClock `eventTime` is informational only.
- Engine → DeskClock broadcasts carry **no** permission checks (neither side can hold our signature permission).
- `Protocol.kt` stays pure constants, no Android imports.
- `BedtimeReconciler` stays pure Kotlin, no Android imports (JVM-testable).
- Rules (from spec): (1) DeskClock bedtime ON ⇒ mode ON; (2) wake-alarm dismiss ⇒ mode OFF, **not** a manual dismiss; (3) Clock skip-once ⇒ in-schedule: mode OFF (+dismiss record), outside: nothing (DeskClock skips the night itself); (4) mode disabled in-window ⇒ send SKIP_WAKE_ALARM_ONCE; (5) mode toggled outside window ⇒ never send skip-once.
- Commit after every task. Message style follows repo history (`feat:`, `fix:`, …).

---

### Task 1: BedtimeReconciler — pure decision logic + tests

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeReconciler.kt`
- Test: `app/src/test/java/com/banana/hypermodes/systemserver/trigger/BedtimeReconcilerTest.kt`

**Interfaces:**
- Consumes: nothing (new component).
- Produces (used by Task 3):
  - `object BedtimeReconciler`
  - `BedtimeReconciler.Reason` enum: `ZEN_ENTERED, ZEN_EXITED, ALARM_DISMISSED, SKIP_ONCE_ACTIVE, SKIP_ONCE_IDLE, ALARM_DISABLED`; `companion fun fromString(value: String?, active: Boolean): Reason`
  - `BedtimeReconciler.Command` enum: `START_BEDTIME, SKIP_WAKE_ALARM_ONCE, EXIT_BEDTIME, ENABLE_WAKE_ALARM`
  - `BedtimeReconciler.Event` sealed interface: `DeskClockSignal(active: Boolean, reason: Reason, receivedAt: Long)`, `ModeActivatedViaConfig(receivedAt: Long)`, `ModeDeactivatedViaConfig(receivedAt: Long, inSleepWindow: Boolean)`
  - `BedtimeReconciler.Decision` sealed interface: `ActivateMode`, `DeactivateMode(recordDismiss: Boolean)`, `SendDeskClockCommand(command: Command)`
  - `BedtimeReconciler.Snapshot(bedtimeModeExists: Boolean, modeActive: Boolean, modeActivatedAt: Long, dismissedAt: Long?)`
  - `fun decide(event: Event, s: Snapshot): List<Decision>`

- [ ] **Step 1: Write the failing tests**

Create `app/src/test/java/com/banana/hypermodes/systemserver/trigger/BedtimeReconcilerTest.kt`:

```kotlin
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
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.trigger.BedtimeReconcilerTest"`
Expected: FAIL — `BedtimeReconciler` does not exist (compile error).

- [ ] **Step 3: Implement BedtimeReconciler**

Create `app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeReconciler.kt`:

```kotlin
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
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.trigger.BedtimeReconcilerTest"`
Expected: PASS (18 tests)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeReconciler.kt app/src/test/java/com/banana/hypermodes/systemserver/trigger/BedtimeReconcilerTest.kt
git commit -m "feat: add BedtimeReconciler decision logic for bedtime sync"
```

---

### Task 2: Protocol extras + DeskClockHook signal emission

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt` (add extras after `EXTRA_IS_SKIPPED`)
- Modify: `app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt` (emission points: `hookBedtimeStateSignals` ~line 147, `hookAlarmSkip` ~line 185, `hookAlarmEnable` ~line 226, dismiss hook in `hookWakeAlarmDismissal` ~line 94)

**Interfaces:**
- Consumes: `BedtimeReconciler.Reason` names (Task 1) as plain strings.
- Produces: `Protocol.EXTRA_BEDTIME_REASON` (`"bedtimeReason"`), `Protocol.EXTRA_EVENT_TIME` (`"eventTime"`); `ACTION_BEDTIME_ACTIVE` broadcasts always carry `inSleepMode` + `bedtimeReason` + `eventTime`. Used by Task 3's `BedtimeListener`.
- Note: `ALARM_ENABLED` from the spec's reason list is intentionally **not** emitted — no rule consumes it (YAGNI). Do not add it.

- [ ] **Step 1: Add Protocol extras**

In `Protocol.kt`, after the line `const val EXTRA_IS_SKIPPED = "isSkipped"`:

```kotlin
    /** Extra on ACTION_BEDTIME_ACTIVE: why the state changed
     *  (BedtimeReconciler.Reason name; absent on older hook builds). */
    const val EXTRA_BEDTIME_REASON = "bedtimeReason"
    /** Extra on ACTION_BEDTIME_ACTIVE: sender-side event wall time
     *  (informational only — receivers stamp their own receipt time). */
    const val EXTRA_EVENT_TIME = "eventTime"
```

- [ ] **Step 2: Add the shared emitter to DeskClockHook**

In `DeskClockHook.kt`, next to the existing `readInZenMode` helper (~line 267):

```kotlin
    /** Single funnel for all bedtime-state pushes to system_server. */
    private fun sendBedtimeState(context: Context, active: Boolean, reason: String) {
        context.sendBroadcast(Intent(Protocol.ACTION_BEDTIME_ACTIVE).apply {
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, active)
            putExtra(Protocol.EXTRA_BEDTIME_REASON, reason)
            putExtra(Protocol.EXTRA_EVENT_TIME, System.currentTimeMillis())
        })
        log("bedtime state -> active=$active reason=$reason")
    }
```

- [ ] **Step 3: Route all five emission points through it**

3a. In `hookBedtimeStateSignals` (enter/exit zen interceptor), replace:

```kotlin
                            context.sendBroadcast(Intent(Protocol.ACTION_BEDTIME_ACTIVE).apply {
                                putExtra(Protocol.EXTRA_IN_SLEEP_MODE, active)
                            })
```

with:

```kotlin
                            sendBedtimeState(context, active, if (active) "ZEN_ENTERED" else "ZEN_EXITED")
```

3b. In `hookWakeAlarmDismissal`'s dismissAlarm interceptor, after the `exitZenMode` invoke and its log line (`log("wake alarm dismissed -> exitZenMode")`), add:

```kotlin
                                sendBedtimeState(context, false, "ALARM_DISMISSED")
```

3c. In `hookAlarmSkip`'s interceptor, replace the whole `if (alarmId == Int.MIN_VALUE) { ... }` body with:

```kotlin
                        if (alarmId == Int.MIN_VALUE) {
                            log("Bedtime alarm skipped manually in DeskClock")
                            if (readInZenMode(context, classLoader, false)) {
                                // Skip during an ACTIVE sleep period: exit the live
                                // session too (hooked exitZenMode also pushes state).
                                val controller = BedtimeController(context, classLoader) { msg -> log(msg) }
                                val steps = controller.exitActiveBedtime()
                                log("skip during active bedtime -> exitActiveBedtime: ${steps.joinToString { it.format() }}")
                                sendBedtimeState(context, false, "SKIP_ONCE_ACTIVE")
                            } else {
                                // Idle pre-skip: DeskClock skips the next bedtime itself.
                                sendBedtimeState(context, false, "SKIP_ONCE_IDLE")
                            }
                        }
```

3d. In `hookAlarmEnable`'s interceptor, replace the `if (alarmId == Int.MIN_VALUE && enabled) { ... }` block (currently only logs) with:

```kotlin
                        if (alarmId == Int.MIN_VALUE && !enabled) {
                            log("Bedtime wake alarm disabled permanently in DeskClock")
                            sendBedtimeState(context, false, "ALARM_DISABLED")
                        }
```

- [ ] **Step 4: Compile**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL. (Unit tests don't cover the hook; device verification is Task 6.)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt
git commit -m "feat: tag DeskClock bedtime signals with reason and event time"
```

---

### Task 3: Engine wiring — reconciler in RoutineCoreEngine, BedtimeListener slim-down

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListener.kt` (rewrite)
- Delete: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListenerLifecycle.kt`

**Interfaces:**
- Consumes: `BedtimeReconciler.*` (Task 1), `Protocol.EXTRA_BEDTIME_REASON` (Task 2).
- Produces: `RoutineCoreEngine.onBedtimeSignal(active: Boolean, reasonName: String?)` — the single entry point for DeskClock signals. `BedtimeListener(context, engine)` constructor now takes **2 args** (no lifecycle).

- [ ] **Step 1: Rewrite BedtimeListener as a broadcast→event shell**

Replace the entire content of `BedtimeListener.kt` with:

```kotlin
package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.RoutineCoreEngine

/**
 * I/O shell: receives DeskClock bedtime-state broadcasts and forwards them to
 * RoutineCoreEngine.onBedtimeSignal. All decisions live in BedtimeReconciler;
 * this class owns no state and no policy.
 */
class BedtimeListener(
    private val context: Context,
    private val engine: RoutineCoreEngine,
) {
    private val handler = Handler(Looper.getMainLooper())
    private var receiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Protocol.ACTION_BEDTIME_ACTIVE) {
                val active = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)
                val reason = intent.getStringExtra(Protocol.EXTRA_BEDTIME_REASON)
                log("Received bedtime signal: active=$active reason=$reason")
                engine.onBedtimeSignal(active, reason)
            }
        }
    }

    fun registerStateSources() {
        if (receiverRegistered) return
        try {
            val filter = IntentFilter(Protocol.ACTION_BEDTIME_ACTIVE)
            context.registerReceiver(receiver, filter, null, handler, Context.RECEIVER_EXPORTED)
            receiverRegistered = true
            log("Bedtime signal receiver registered")
        } catch (e: Exception) {
            log("Failed to register bedtime signal receiver: ${e.message}")
        }
    }

    fun cleanup() {
        if (receiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
                receiverRegistered = false
                log("Bedtime signal receiver unregistered")
            } catch (e: Exception) {
                log("Failed to unregister bedtime signal receiver: ${e.message}")
            }
        }
    }

    /** Clean up package-removal resources without normal mode deactivation. */
    fun cleanupForPackageRemoval() = cleanup()

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "BedtimeListener"
    }
}
```

- [ ] **Step 2: Wire the reconciler into RoutineCoreEngine**

2a. Delete the field `private var bedtimeListenerLifecycle = BedtimeListenerLifecycle()` and the import of `BedtimeListenerLifecycle`. In `init()`, replace:

```kotlin
        bedtimeListenerLifecycle = BedtimeListenerLifecycle()
        bedtimeListener = BedtimeListener(context, this, bedtimeListenerLifecycle).also {
            it.registerStateSources()
        }
```

with:

```kotlin
        bedtimeListener = BedtimeListener(context, this).also {
            it.registerStateSources()
        }
```

2b. In `loadConfigFromSettings()`, replace BOTH listener call sites. Replace `bedtimeListener?.updateModes(emptyList())` (empty-config branch) with nothing (delete the line), and replace:

```kotlin
            // Initialize the bedtime bridge on the first load, then synchronize its modes.
            // This runs after the persisted active mode is restored so external bedtime state wins.
            bedtimeListener?.let { listener ->
                bedtimeListenerLifecycle.onModesLoaded(
                    modes = config.modes,
                    initialize = listener::init,
                    update = listener::updateModes
                )
            }
```

with nothing (delete the block — the listener no longer tracks modes).

2c. In the config-activate branch (`log("Activating mode from config: ${mode.name}")`), replace:

```kotlin
                        // Manual UI activation comes through THIS config path, not
                        // activateMode() — sync DeskClock bedtime here too, or the
                        // official state stays off and the UI mirror flips the mode
                        // back off seconds later.
                        if (mode.type == ModeType.BEDTIME) {
                            sendBedtimeActivateCommands()
                        }
```

with:

```kotlin
                        // Manual UI activation comes through THIS config path, not
                        // activateMode() — route DeskClock sync through the reconciler.
                        if (mode.type == ModeType.BEDTIME) {
                            executeBedtimeDecisions(BedtimeReconciler.decide(
                                BedtimeReconciler.Event.ModeActivatedViaConfig(
                                    System.currentTimeMillis()
                                ),
                                bedtimeSnapshot()
                            ))
                        }
```

2d. In the config-null (deactivate) branch, replace:

```kotlin
                        // Mirror deactivateMode(): a manual bedtime dismiss via
                        // Settings.Global must also tell DeskClock, otherwise the
                        // official bedtime state stays "on" and re-enables this
                        // mode (UI flips back to enabled).
                        if (activeMode.type == ModeType.BEDTIME) {
                            sendBedtimeDisableCommand(activeMode)
                        }
```

with:

```kotlin
                        // Manual bedtime dismiss via Settings.Global: command routing
                        // decided by the reconciler (skip-once in-window, exit outside).
                        if (activeMode.type == ModeType.BEDTIME) {
                            executeBedtimeDecisions(BedtimeReconciler.decide(
                                BedtimeReconciler.Event.ModeDeactivatedViaConfig(
                                    System.currentTimeMillis(),
                                    isInBedtimeWindow(activeMode)
                                ),
                                bedtimeSnapshot()
                            ))
                        }
```

2e. In `activateMode()`, delete the bedtime sync block:

```kotlin
        // IF it's Bedtime mode, sync DeskClock: wake alarm armed, and push the
        // official bedtime ON so the system state tracks this mode deliberately
        // (BedtimeListener reconciles against that state; without it a manual
        // activation reads as "bedtime off" and gets torn down again).
        if (mode.type == ModeType.BEDTIME) {
            sendBedtimeActivateCommands()
        }
```

(DeskClock-driven activation must NOT echo commands back; config-driven activation sends them via 2c.)

2f. In `deactivateMode()`, delete:

```kotlin
            // IF it's Bedtime mode, also tell DeskClock to end the session
            // (skip the wake alarm once when inside the sleep window)
            if (mode.type == ModeType.BEDTIME) {
                sendBedtimeDisableCommand(mode)
            }
```

(Reconciler-driven deactivation carries its own commands; system-driven deactivation must not echo.)

2g. Delete the now-unused helpers `sendBedtimeActivateCommands()` and `sendBedtimeDisableCommand(mode)`. Keep `isInBedtimeWindow(mode)` and `sendBedtimeCommand(action)`.

2h. Add the signal entry point and decision executor (place next to `sendBedtimeCommand`):

```kotlin
    /**
     * Single entry point for DeskClock bedtime-state signals (via BedtimeListener).
     * All policy lives in BedtimeReconciler; this only builds the snapshot and
     * executes the resulting decisions.
     */
    fun onBedtimeSignal(active: Boolean, reasonName: String?) {
        if (lifecycleState == LifecycleState.REMOVED) return
        val reason = BedtimeReconciler.Reason.fromString(reasonName, active)
        val decisions = BedtimeReconciler.decide(
            BedtimeReconciler.Event.DeskClockSignal(active, reason, System.currentTimeMillis()),
            bedtimeSnapshot()
        )
        log("Bedtime signal active=$active reason=$reason -> ${decisions.joinToString()}")
        executeBedtimeDecisions(decisions)
    }

    private fun bedtimeSnapshot(): BedtimeReconciler.Snapshot {
        val bedtime = allModes.firstOrNull { it.type == ModeType.BEDTIME }
        return BedtimeReconciler.Snapshot(
            bedtimeModeExists = bedtime != null,
            modeActive = currentActiveMode?.type == ModeType.BEDTIME,
            modeActivatedAt = currentModeActivatedAt,
            dismissedAt = bedtime?.let { dismissedScheduledModes[it.id] }
        )
    }

    private fun executeBedtimeDecisions(decisions: List<BedtimeReconciler.Decision>) {
        if (decisions.isEmpty()) return
        val bedtime = allModes.firstOrNull { it.type == ModeType.BEDTIME } ?: return
        for (d in decisions) when (d) {
            BedtimeReconciler.Decision.ActivateMode -> activateMode(bedtime.id)
            is BedtimeReconciler.Decision.DeactivateMode ->
                deactivateMode(bedtime.id, isManualDismiss = d.recordDismiss)
            is BedtimeReconciler.Decision.SendDeskClockCommand -> sendBedtimeCommand(
                when (d.command) {
                    BedtimeReconciler.Command.START_BEDTIME ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_START_BEDTIME
                    BedtimeReconciler.Command.SKIP_WAKE_ALARM_ONCE ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_SKIP_WAKE_ALARM_ONCE
                    BedtimeReconciler.Command.EXIT_BEDTIME ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_EXIT_BEDTIME
                    BedtimeReconciler.Command.ENABLE_WAKE_ALARM ->
                        com.banana.hypermodes.protocol.Protocol.ACTION_ENABLE_WAKE_ALARM
                }
            )
        }
    }
```

2i. Add the import `import com.banana.hypermodes.systemserver.trigger.BedtimeReconciler` (the `trigger.BedtimeListener` import already exists).

- [ ] **Step 3: Delete BedtimeListenerLifecycle.kt**

```bash
git rm app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListenerLifecycle.kt
```

- [ ] **Step 4: Compile and run the full test suite**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL, all tests pass (18 reconciler + existing).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListener.kt
git commit -m "refactor: route all bedtime state transitions through BedtimeReconciler"
```

---

### Task 4: UI mirrors engine only (kill the flap)

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt` (lines ~160-187)
- Modify: `app/src/main/java/com/banana/hypermodes/ui/DeskClockState.kt` (doc comment on `bedtimeActive`)

**Interfaces:**
- Consumes: nothing new.
- Produces: bedtime card `enabled` derives ONLY from `ModeStore.load` (which maps `activeModeId`) and `ACTION_MODE_STATE` reloads, plus the optimistic flip in `ModeDetailScreen` (already in place, keep it).

- [ ] **Step 1: Remove the DeskClock-status stomp**

In `HyperModesApp.kt`, replace:

```kotlin
    var modes by remember { mutableStateOf<List<Mode>>(emptyList()) }
    LaunchedEffect(Unit) {
        modes = sortModes(
            ModeStore.load(context) { DefaultModes.get() }.map {
                // Bedtime's enabled flag mirrors the official DeskClock state
                if (it.id == "bedtime") it.copy(enabled = DeskClockState.bedtimeActive) else it
            }
        )
    }
```

with:

```kotlin
    var modes by remember { mutableStateOf<List<Mode>>(emptyList()) }
    LaunchedEffect(Unit) {
        // Card state mirrors the engine (activeModeId via ModeStore.load) —
        // never DeskClock status directly, which races the command round trip
        // and flaps the card until the backend catches up.
        modes = sortModes(ModeStore.load(context) { DefaultModes.get() })
    }
```

- [ ] **Step 2: Delete the live-sync stomp**

Delete this entire block from `HyperModesApp.kt`:

```kotlin
    // Live-sync the bedtime card when the official state changes while the
    // UI is alive (e.g. scheduled activation pushed by the DeskClock hook).
    val bedtimeActive = DeskClockState.bedtimeActive
    LaunchedEffect(bedtimeActive) {
        val idx = modes.indexOfFirst { it.id == "bedtime" }
        if (idx >= 0 && modes[idx].enabled != bedtimeActive) {
            persistModes(modes.toMutableList().apply {
                set(idx, modes[idx].copy(enabled = bedtimeActive))
            })
        }
    }
```

(Engine broadcasts `ACTION_MODE_STATE` on every activation/deactivation — the existing receiver below it already reloads the list. That is now the ONLY live-sync.)

- [ ] **Step 3: Update DeskClockState doc**

In `DeskClockState.kt`, replace the comment above `bedtimeActive`:

```kotlin
    /** Whether the official DeskClock bedtime is active RIGHT NOW
     *  (powerkeeper sleep mode / inZenMode), as pushed by the hook. */
```

with:

```kotlin
    /** Whether the official DeskClock bedtime is active RIGHT NOW, as pushed
     *  by the hook. INFORMATIONAL ONLY — never write this into a mode's
     *  `enabled` flag; mode state comes from the engine (activeModeId /
     *  ACTION_MODE_STATE). Writing it into modes races the command round
     *  trip and flaps the UI. */
```

- [ ] **Step 4: Compile**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL. (`DeskClockState.bedtimeActive` may now be unread — that is fine; it stays for the detail-page rows and future use. If the compiler flags the now-unused `persistModes`-in-LaunchedEffect path, no action needed — `persistModes` is still used elsewhere.)

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt app/src/main/java/com/banana/hypermodes/ui/DeskClockState.kt
git commit -m "fix: stop mirroring DeskClock status into mode state (kills card flapping)"
```

---

### Task 5: DisplayModeController — persisted originals + forced config re-apply

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/executor/DisplayModeController.kt`

**Interfaces:**
- Consumes: nothing new.
- Produces: originals persisted under `Settings.Global` keys `hypermodes_orig_night_mode`, `hypermodes_orig_daltonizer_enabled`, `hypermodes_orig_daltonizer`, `hypermodes_orig_mimotion_pwm`, `hypermodes_orig_paper_mode`, `hypermodes_orig_refresh_rate` (value `-1`/absent = nothing saved). `restore()` clears keys after reverting.

- [ ] **Step 1: Replace in-memory originals with Settings.Global persistence**

Replace the five `private var original...` field declarations with:

```kotlin
    /**
     * Original values are persisted in Settings.Global so they survive
     * system_server restarts and reboots — in-memory fields lost them, which
     * left dark mode stuck on until HyperOS re-evaluated at the next
     * screen-off. -1 = nothing saved.
     */
    private fun saveOriginal(key: String, value: Int) {
        if (Settings.Global.getInt(context.contentResolver, key, -1) == -1) {
            Settings.Global.putInt(context.contentResolver, key, value)
        }
    }

    /** Returns the saved original and clears it, or null if none. */
    private fun takeOriginal(key: String): Int? {
        val v = Settings.Global.getInt(context.contentResolver, key, -1)
        if (v == -1) return null
        Settings.Global.putString(context.contentResolver, key, null)
        return v
    }
```

Add to the companion object:

```kotlin
        private const val KEY_ORIG_NIGHT_MODE = "hypermodes_orig_night_mode"
        private const val KEY_ORIG_DALTONIZER_ENABLED = "hypermodes_orig_daltonizer_enabled"
        private const val KEY_ORIG_DALTONIZER = "hypermodes_orig_daltonizer"
        private const val KEY_ORIG_PWM = "hypermodes_orig_mimotion_pwm"
        private const val KEY_ORIG_PAPER = "hypermodes_orig_paper_mode"
        private const val KEY_ORIG_REFRESH = "hypermodes_orig_refresh_rate"
```

Then update each apply/restore pair (pattern shown for dark mode; repeat for the other four):

In `apply()`, replace:

```kotlin
                if (originalDarkMode == null) {
                    originalDarkMode = uiModeManager.nightMode
                    log("apply: saved original dark mode: $originalDarkMode")
                }
```

with:

```kotlin
                saveOriginal(KEY_ORIG_NIGHT_MODE, uiModeManager.nightMode)
```

In `restore()`, replace:

```kotlin
            originalDarkMode?.let { original ->
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.nightMode = original
                log("restore: reverted dark mode to $original")
                originalDarkMode = null
            }
```

with:

```kotlin
            takeOriginal(KEY_ORIG_NIGHT_MODE)?.let { original ->
                val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
                uiModeManager.nightMode = original
                log("restore: reverted dark mode to $original")
            }
```

Grayscale: `saveOriginal(KEY_ORIG_DALTONIZER_ENABLED, Settings.Secure.getInt(cr, "accessibility_display_daltonizer_enabled", 0))` and `saveOriginal(KEY_ORIG_DALTONIZER, Settings.Secure.getInt(cr, "accessibility_display_daltonizer", 0))`; restore via `takeOriginal` for both (mode key restored only if present, same as before).

PWM: `saveOriginal(KEY_ORIG_PWM, Settings.Secure.getInt(cr, "mimotion_pwm_enable", 1))`; restore via `takeOriginal(KEY_ORIG_PWM)`.

Eye care: `saveOriginal(KEY_ORIG_PAPER, Settings.System.getInt(cr, "screen_paper_mode_enabled", 0))`; restore via `takeOriginal(KEY_ORIG_PAPER)`.

Refresh rate: `saveOriginal(KEY_ORIG_REFRESH, Settings.Secure.getInt(cr, "user_refresh_rate", 60))`; restore via `takeOriginal(KEY_ORIG_REFRESH)`.

Delete the six `private var original...` fields.

- [ ] **Step 2: Force configuration re-apply after night-mode changes**

Add to DisplayModeController:

```kotlin
    /**
     * HyperOS can defer a night-mode revert until the next screen-off (the
     * "lock once to go light" symptom). Force a global configuration refresh
     * from inside system_server so the theme flips immediately. Best-effort.
     */
    private fun forceConfigurationReapply() {
        try {
            val amClass = Class.forName("android.app.ActivityManagerNative")
            val am = amClass.getMethod("getDefault").invoke(null)
            val update = amClass.getMethod(
                "updateConfiguration", android.content.res.Configuration::class.java
            )
            try {
                update.invoke(am, null)
            } catch (t: Throwable) {
                update.invoke(am, android.content.res.Configuration())
            }
            log("forced configuration re-apply")
        } catch (t: Throwable) {
            log("forceConfigurationReapply failed: ${t.message}")
        }
    }
```

Call it at the end of `restore()` (after the refresh-rate block) and at the end of `apply()` when `display.darkMode` was applied.

- [ ] **Step 3: Compile**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/executor/DisplayModeController.kt
git commit -m "fix: persist display originals and force config re-apply on revert"
```

---

### Task 6: Build, install, on-device verification

**Files:** none (verification only).

- [ ] **Step 1: Build and run all tests**

Run: `./gradlew :app:assembleDebug :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL, all tests pass.

- [ ] **Step 2: Install and remind reboot**

Run: `adb install -r app/build/outputs/apk/debug/app-debug.apk`
Expected: Success. Tell the user to reboot (system_server + DeskClock hooks changed).

- [ ] **Step 3: Watch the log during verification**

Run in background: `adb logcat -v time RoutineCoreEngine:I BedtimeListener:I DisplayModeController:I ModeControlBridge:I *:S | grep -v LSPosedFramework`
(DeskClock-hook lines arrive via LSPosed's own log — check separately with `adb logcat -d | grep "HyperModes"` if needed.)

- [ ] **Step 4: Verify each rule with the user**

1. DeskClock bedtime ON (scheduled time or Clock-app toggle) ⇒ mode activates with features.
2. Morning wake-alarm dismiss ⇒ mode OFF; next night still auto-activates (no dismiss record).
3a. Skip-once while active ⇒ mode OFF now.
3b. Skip-once outside schedule ⇒ mode stays off for the skipped night, resumes after.
4. Mode OFF in-window ⇒ DeskClock receives `SKIP_WAKE_ALARM_ONCE` (`Sent command to DeskClock: ...SKIP_WAKE_ALARM_ONCE`).
5. Mode ON/OFF outside window ⇒ no skip sent (`START_BEDTIME` / `EXIT_BEDTIME` instead).
6. Manual ON: card stays on, no off/on flapping.
7. Mode OFF with dark mode: theme returns to light WITHOUT locking the screen.

- [ ] **Step 5: Final commit if any fixes; push**

```bash
git push origin main
```

---

## Self-Review Notes

- **Spec coverage:** rules 1–5 → Task 1 event table + Task 3 wiring; signal reasons → Task 2; UI flap → Task 4; dark-mode revert → Task 5; DeskClockHook `exitActiveBedtime`/`EXIT_BEDTIME` command already exist (previous work) and are reused, not reimplemented. `handleZenModeChange` self-trigger fix (previous work) is untouched.
- **Intentional deviations from spec text:** `ALARM_ENABLED` reason not emitted (no rule consumes it). `BedtimeListenerLifecycle` deleted rather than kept (reconciler is stateless; no re-evaluation on config load).
- **Type consistency:** `Reason.fromString`, `Event.DeskClockSignal/ModeActivatedViaConfig/ModeDeactivatedViaConfig`, `Decision.ActivateMode/DeactivateMode/SendDeskClockCommand`, `Command.*`, `Snapshot(bedtimeModeExists, modeActive, modeActivatedAt, dismissedAt)`, `RoutineCoreEngine.onBedtimeSignal(active: Boolean, reasonName: String?)`, `Protocol.EXTRA_BEDTIME_REASON` / `EXTRA_EVENT_TIME` — used identically across Tasks 1–3.
