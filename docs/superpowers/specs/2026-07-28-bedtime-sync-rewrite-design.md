# Bedtime ↔ DeskClock Synchronization Rewrite — Design

Date: 2026-07-28
Status: Approved by user

## Problem

Bedtime mode state is currently owned by three parties at once (HyperModes
engine in system_server, the DeskClock hook, and the app's UI mirror), each
reconciling against the others with ad-hoc guards. Every guard we added fixed
one race and created the next. Observable bugs:

1. Turning bedtime on manually flaps the card off/on until the DeskClock round
   trip settles (stale query replies stomp the UI, engine broadcast corrects).
2. After turning bedtime off, dark mode only reverts to light after a
   lock/unlock cycle (restore depends on in-memory originals lost on reboot,
   and/or HyperOS defers the uiMode re-render to the next screen-off).
3. Rules for skip-once / dismiss / in-schedule-vs-outside-schedule behavior are
   scattered across RoutineCoreEngine, BedtimeListener, DeskClockHook and the
   UI, and disagree at the seams.

## Contract (user-approved rules)

1. DeskClock bedtime ON ⇒ bedtime mode ON, with all its features applied.
2. DeskClock wake-up alarm **dismissed** (the daily morning gesture) ⇒ bedtime
   mode OFF. This is a system deactivation, not a manual dismiss — the next
   scheduled night must still auto-activate.
3. DeskClock skip-once:
   - Set while bedtime is active (in schedule) ⇒ disable bedtime mode now.
   - Set outside schedule ⇒ the Clock app skips the whole next bedtime; the
     mode must therefore also not activate for that one occurrence, resuming
     the night after.
4. Disabling the bedtime mode **in** the schedule window ⇒ send skip-once to
   DeskClock (skips the pending wake alarm and ends the live session).
5. Enabling or disabling the bedtime mode **outside** the schedule window ⇒
   never send skip-once (wake alarm schedule untouched).

## Architecture

**DeskClock is the source of truth for bedtime state.** One reconciler in
system_server owns every transition; the engine mirrors DeskClock; the UI
mirrors the engine. All signals and decisions carry timestamps.

```
DeskClock (hook)  --state signals-->  BedtimeReconciler (system_server)
DeskClock (hook)  <--commands-------  RoutineCoreEngine
HyperModes UI     <--mode state-----  RoutineCoreEngine (ACTION_MODE_STATE)
HyperModes UI     --toggle--------->  Settings.Global config (activeModeId)
```

### Component 1: DeskClockHook (DeskClock process) — the only state writer

Reports state; never invents policy. Emits `ACTION_BEDTIME_ACTIVE` with extras:

- `inSleepMode: Boolean` — the new state
- `reason: String` — `zen_entered` | `zen_exited` | `alarm_dismissed` |
  `skip_once_active` | `skip_once_idle` | `alarm_disabled` | `alarm_enabled`
- `eventTime: Long` — wall-clock ms when the event happened in DeskClock

Emission points (all already hooked today, extended with reason/time):
`ZenModeUtil.enterZenMode` (`zen_entered`), `ZenModeUtil.exitZenMode`
(`zen_exited` — the hook cannot see *why* zen exited, so this reason is
generic), `AlarmHelper.dismissAlarm(MIN_VALUE)` (`alarm_dismissed`),
`AlarmHelper.skipAlarmForOnce(MIN_VALUE)` (classified `skip_once_active` vs
`skip_once_idle` by reading the `inZenMode` pref),
`AlarmHelper.enableAlarm(MIN_VALUE, false)` (`alarm_disabled`).

Note: the morning dismiss fires TWO signals — `alarm_dismissed` from the
dismiss hook and a generic `zen_exited` from the exitZenMode call it makes.
This is fine: both map to the same action and reconciler actions are
idempotent, so the second signal is a no-op.

Also executes engine commands (unchanged): `START_BEDTIME`,
`SKIP_WAKE_ALARM_ONCE` (skip + exit live session), `EXIT_BEDTIME` (exit live
session, no alarm change), `ENABLE_WAKE_ALARM`, `DISABLE_BEDTIME`,
schedule/reminder commands, queries.

The `doInWakeTime` hook keeps current behavior (bedtime survives wake time
until the alarm is dismissed). Skipped nights never enter zen on the target
ROM, so no skipped-occurrence hang exists.

### Component 2: BedtimeReconciler (system_server)

Replaces the decision logic of `BedtimeListener` (the class may be renamed or
kept as a thin I/O shell around the reconciler). Processes one event at a time
on the main handler — no concurrent decisions.

**State:** current mode (from engine), last DeskClock signal (state, reason,
eventTime), dismiss records (modeId → time, persisted in config), mode
activation time (from engine).

**Core invariant (anti-flap):** an event only wins if it is newer than the
event it contradicts. A stale "on" cannot re-activate a dismissed mode; a stale
or absent "off" cannot tear down a freshly activated mode. Wall-clock on one
device, all timestamps taken in system_server on receipt (DeskClock eventTime
is informational).

**Event table:**

| Event | Condition | Action |
|---|---|---|
| DeskClock signal ON (`zen_entered`) | mode not active, not dismissed after signalTime | activate bedtime mode (clears dismiss record) — rule 1 |
| DeskClock signal ON | dismiss record newer than signalTime | ignore (stale) |
| DeskClock signal OFF (`alarm_dismissed`, `zen_exited`) | bedtime mode active | deactivate, `isManualDismiss = false` — rule 2 |
| DeskClock signal OFF (`skip_once_active`) | bedtime mode active | deactivate, record manual dismiss — rule 3 in-schedule |
| DeskClock signal OFF (`skip_once_idle`) | bedtime mode not active | none — DeskClock skips the next night itself, so no ON signal arrives; mode stays off — rule 3 outside-schedule |
| DeskClock signal OFF | signalTime older than mode activation time | ignore (stale) |
| No DeskClock signal ever received | any | never deactivate (unknown ≠ off) |
| Mode toggled OFF via config, inside sleep window | bedtime mode active | deactivate + dismiss record + send `SKIP_WAKE_ALARM_ONCE` — rule 4 |
| Mode toggled OFF via config, outside window | bedtime mode active | deactivate + dismiss record + send `EXIT_BEDTIME` — rule 5 |
| Mode toggled ON via config | mode not active | activate + send `START_BEDTIME` (+ `ENABLE_WAKE_ALARM`); DeskClock's zen-entered signal will arrive as confirmation — rule 5 (no skip) |
| Wake alarm permanently disabled in DeskClock | bedtime mode active | deactivate, `isManualDismiss = false` (schedule ended by user in Clock app) |

Sleep-window check (in/out of schedule) lives in the engine and uses the
mode's own `startTime`/`endTime`/`repeatDays` (overnight windows supported);
unknown schedule defaults to in-window (legacy-safe). Already implemented as
`isInBedtimeWindow`.

### Component 3: RoutineCoreEngine — command routing

- Activation of a BEDTIME mode (both paths: `activateMode()` and the
  config-change branch that UI toggles actually use) sends
  `START_BEDTIME` + `ENABLE_WAKE_ALARM`.
- Manual dismissal of a BEDTIME mode (both paths) routes through
  `sendBedtimeDisableCommand`: in window → `SKIP_WAKE_ALARM_ONCE`,
  outside → `EXIT_BEDTIME`.
- Engine → DeskClock broadcasts carry no permission checks (signature
  permission can never be held by either side — already fixed).

### Component 4: UI — mirrors the engine only

- Card on/off state follows `ACTION_MODE_STATE` broadcasts and the persisted
  `activeModeId`, plus an optimistic flip the instant the user taps.
- **Delete** the `LaunchedEffect(bedtimeActive)` block in `HyperModesApp` that
  rewrites `modes[bedtime].enabled` from `DeskClockState.bedtimeActive` — this
  is the flap: stale query replies flip the card off, the engine broadcast
  flips it back on.
- `DeskClockState` keeps schedule data only (sleep/wake times, wake-alarm
  toggle, reminder, skip badge) for the detail-page rows. Its `bedtimeActive`
  flag becomes informational (intro-gate), never written into `modes`.
- `ModeStore.load` already maps `enabled` from `activeModeId` — that becomes
  the single mapping for the card.

### Component 5: DisplayModeController — revert that actually reverts

- Persist the "original" values (night mode, daltonizer, mimotion_pwm,
  paper mode, refresh rate) to a `Settings.Global` key so they survive
  system_server/process restarts; restore reads persisted first, in-memory
  second.
- After restoring night mode, verify on-device whether HyperOS applies the
  uiMode change immediately. If it defers (the lock-screen-to-go-light
  symptom), force a configuration re-apply from inside system_server (e.g.
  `ActivityManagerInternal.updateConfiguration` with the current
  configuration) right after the restore write.

## Data flow examples

**Manual ON (outside window):** tap → config `activeModeId=bedtime` + UI
optimistic ON → engine activates (features apply immediately) + sends
START_BEDTIME → DeskClock enters zen → signal ON (newer) → reconciler sees
mode already active → no-op. No flapping: nothing in the chain can issue an
"off" newer than the activation.

**Manual OFF (in window):** tap → config null + optimistic OFF → engine
deactivates + dismiss record + SKIP_WAKE_ALARM_ONCE → DeskClock skips alarm +
exits zen → signal OFF (newer than activation) → reconciler: mode already
inactive → no-op. Wake alarm skipped for this period; tomorrow unaffected
beyond the single skip.

**Clock-app skip-once outside schedule:** DeskClock stores skip, hook emits
OFF(`skip_once_idle`) → reconciler: mode not active → no-op. Next night
DeskClock skips bedtime → no ON signal → mode stays off. Night after: normal
ON signal → activation resumes.

## Error handling

- DeskClock hook dead (module disabled/process killed): commands are dropped
  silently (best-effort sends); reconciler never deactivates on absent signals,
  so a manually activated mode stays on instead of flapping off.
- Clock skew: all decision timestamps are taken in system_server on receipt;
  DeskClock `eventTime` is logging-only.
- Duplicate signals are idempotent (activate when already active / deactivate
  when already inactive are no-ops).
- Reboot with bedtime active: config restores the mode; first DeskClock signal
  reconciles. Absent any signal, the mode is left untouched.

## Testing

- **Unit (JVM):** reconciler event table — every row of the table above as a
  pure function test (fake clock, fake engine). Window check: same-day,
  overnight, repeat-day boundaries, midnight edge, unparseable schedule.
- **On-device verification:** the five rules exercised end-to-end with logcat
  assertions on both sides of each broadcast (engine tag
  `RoutineCoreEngine`/`BedtimeListener`, DeskClock hook tag `HyperModes`).
- Dark-mode revert: activate bedtime with dark mode, deactivate, confirm light
  returns without a lock cycle.

## File scope

- `hook/DeskClockHook.kt` — signal reasons/timestamps; skip classification;
  alarm-disabled signal
- `hook/BedtimeController.kt` — unchanged command set (already has
  `exitActiveBedtime`)
- `systemserver/trigger/BedtimeListener.kt` + `BedtimeListenerLifecycle.kt` —
  decision logic extracted into reconciler (same package; may keep class name)
- `systemserver/RoutineCoreEngine.kt` — command routing on activate/deactivate
  (mostly done), reconciler wiring
- `ui/HyperModesApp.kt` — remove mirror stomp; card follows engine
- `ui/DeskClockState.kt` — `bedtimeActive` informational only
- `systemserver/executor/DisplayModeController.kt` — persisted originals +
  config re-apply
- `protocol/Protocol.kt` — signal extras (`reason`, `eventTime`)
- tests: `systemserver/trigger/BedtimeReconcilerTest.kt` (new), window-check
  tests

## Explicitly out of scope

- Changing DeskClock's own skip/dismiss semantics (we mirror, not modify).
- dimWallpaper / keepScreenOff implementation (unrelated, currently no-ops).
- The 60-second → timestamp dismiss migration for SCHEDULED (non-bedtime)
  modes beyond what already exists.
