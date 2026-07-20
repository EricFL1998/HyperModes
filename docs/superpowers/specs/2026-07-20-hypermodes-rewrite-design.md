# HyperModes — Complete Rewrite Design

Date: 2026-07-20
Status: Approved by user

## Purpose

Rewrite the HyperModes LSPosed module so it correctly drives HyperOS DeskClock's
internal bedtime machinery, per the reverse-engineered findings in `finding.md`
and the decompiled `SleepModeUtil` the user supplied. Two capabilities:

1. **Edit the bedtime schedule** (sleep time, wake time, repeat days) by mutating
   DeskClock's own `Alarm` object and persisting it, so Mi Health, Mi Home, and
   the rest of the ecosystem pick up the change exactly as if the user edited it
   in the Clock app.
2. **Manually start/stop bedtime mode**, mimicking the official DeskClock
   sequence (sleep notification, Zen Mode, Mi Home notification, powerkeeper
   sleep mode).

## Architecture

One APK, one Gradle module (`:app`), two runtime halves:

- **Hook half** (`com.banana.hypermodes.hook`) — runs *inside* the
  `com.android.deskclock` process. Required because the internal methods need
  DeskClock's ClassLoader and app Context (Mi Health provider access).
  - Framework: **modern libxposed API 101** (`io.github.libxposed:api:101.0.1`,
    `compileOnly`). Entry class extends `XposedModule` and overrides
    `onPackageReady` (NOT legacy `IXposedHookLoadPackage` / API 82 — the
    reference module in `example lsposed module/` uses the same modern style).
    Packaging is modern-only: `META-INF/xposed/java_init.list` + `module.prop`
    (`minApiVersion=101`, `targetApiVersion=101`) + `scope.list`; **no**
    `assets/xposed_init` and **no** `xposedmodule`/`xposedminversion`/
    `xposedscope` manifest meta-data (the manager picks the module up from
    module.prop; description moves to `android:description`).
  - `XposedInit` — entry point. Filters to `com.android.deskclock`, delegates
    to `DeskClockHook`, which hooks `Application.attach(Context)` (the pattern
    the reference module uses — always runs, never overridden) via
    `module.hook(...).intercept(...)`, then registers the command broadcast
    receiver (see "Export model" below — exported + signature permission).
  - `BedtimeController` — typed Kotlin façade over the internal APIs. One method
    per operation: `applySchedule(...)`, `startBedtime()`, `stopBedtime()`,
    `querySleepModeState()`. libxposed API 101 ships no `XposedHelpers`, so all
    reflective calls go through a small internal `Reflect` helper object
    (superclass-walking method/field lookup with primitive/wrapper matching),
    never raw reflection strings scattered through hook code. Resolved classes
    cached per process.
  - Logging via `module.log(priority, tag, msg)` (API 101 has no
    `XposedBridge`).
  - Every step individually try/caught, accumulating a list of
    `StepResult(name, success, detail)` so one missing/renamed method never
    aborts the remaining steps and failures are visible on-device.

- **App half** (`com.banana.hypermodes.ui`) — the manager UI (Jetpack Compose):
  - Sleep time picker (hour/minute steppers).
  - Wake time picker (hour/minute steppers).
  - Seven day-of-week toggle chips (Mon–Sun) → repeat bitmask (bit 0 = Monday …
    bit 6 = Sunday; 127 = every day). Default: every day.
  - **Apply schedule** button.
  - **Start bedtime now** / **Stop bedtime** buttons, with current sleep-mode
    state shown and the relevant button highlighted.
  - Status card listing per-step results from the last operation.

## Communication protocol

Request → per-step result. No fire-and-forget.

- **App → hook**: explicit broadcast with `setPackage("com.android.deskclock")`.
  - `com.banana.hypermodes.APPLY_SCHEDULE` — extras: `sleepHour`, `sleepMin`,
    `wakeHour`, `wakeMin` (Int), `repeatDays` (Int bitmask).
  - `com.banana.hypermodes.START_BEDTIME`
  - `com.banana.hypermodes.STOP_BEDTIME`
  - `com.banana.hypermodes.QUERY_STATE`
- **Hook → app**: result broadcast `com.banana.hypermodes.RESULT` with
  `setPackage("com.banana.hypermodes")`, extras:
  - `steps`: StringArray of `"<step name>: OK | FAIL: <error>"` lines.
  - `inSleepMode`: Boolean — current powerkeeper sleep-mode state, included in
    every result so the UI stays in sync.
  - Received by a receiver registered while the UI is in the foreground,
    shown in the status card.
- **Export model (corrected during planning):** both receivers must be
  `RECEIVER_EXPORTED` — sender and receiver live in different uids, so
  `NOT_EXPORTED` would silently block delivery. The command receiver (in
  DeskClock) is guarded by a signature-level permission
  `com.banana.hypermodes.permission.CONTROL` so only this app can trigger it;
  the result receiver (in our app) accepts spoofing risk (worst case: fake
  status text) because DeskClock's uid cannot hold our signature permission.

## Exact call sequences

All run inside the DeskClock process with its `Context` (the hooked
`Application` instance). Class names from `finding.md`:

- `BedtimeUtil`  = `com.android.deskclock.alarm.bedtime.BedtimeUtil`
- `HealthDataUtil` = `com.android.deskclock.alarm.bedtime.HealthDataUtil`
- `MiHomeHelper` = `com.android.deskclock.alarm.bedtime.MiHomeHelper`
- `AlarmHelper`  = `com.android.deskclock.util.AlarmHelper`
  (note: `util` package, NOT `alarm.bedtime` — the old code used the wrong one)

### Apply schedule

1. `BedtimeUtil.getSleepAlarm(context)` → `com.android.deskclock.Alarm`.
   If null → stop and report "No bedtime configured in Clock app — create one
   first." (Never construct a new Alarm; that would lose the DB id/flags.)
2. Mutate the Alarm object's hour / minute / days-of-week (see Risk § below).
3. `BedtimeUtil.saveSleepAlarm(context, alarm)`.
4. `HealthDataUtil.updateSleepSchedule(context, sleepHour, sleepMin)` —
   returns Int rows updated; log the value.
5. `HealthDataUtil.updateWakeSchedule(context, wakeHour, wakeMin)` — same.
6. `AlarmHelper.setSleepNotification(context)` — reschedule bedtime reminder.
7. `MiHomeHelper` instantiated with `context` (`XposedHelpers.newInstance`),
   then instance method `notifyBedtimeChanged()`.

### Start bedtime

1. `AlarmHelper.setSleepNotification(context)`.
2. `BedtimeUtil.getDisturbanceState(context)` → Boolean; if true,
   `AlarmHelper.setZenMode(context)`. If false, log "DND integration disabled
   by user — skipping Zen Mode" (respect the user's Clock-app setting).
3. `MiHomeHelper(context).notifyBedtimeChanged()`.

### Stop bedtime

1. Send broadcast `com.miui.powerkeeper_request_wake` with extra `reason = 1`
   (`REASON_DESK_CLOCK`), `setPackage("com.miui.powerkeeper")` — the exact
   Intent `SleepModeUtil.exitSleepMode` sends. Raw broadcast, no reflection.
2. Exit Zen Mode, best-effort: try guessed `AlarmHelper.exitZenMode(context)`;
   on failure fall back to standard DND-off via `NotificationManager`.
3. `MiHomeHelper(context).notifyBedtimeChanged()` (wake signal to IoT).

### Query sleep-mode state

`ContentResolver.call(Uri("content://com.miui.powerkeeper.configure"),
"getSleepModeState", null, null)` → Bundle boolean `isInSleep`. On exception,
report false + the error. Direct provider call, no reflection (robust against
Xiaomi moving/renaming `SleepModeUtil`).

## Risk: Alarm field names

`finding.md` does not document `com.android.deskclock.Alarm`'s field/method
names for time and days. Strategy, in order, each attempt logged to the result:

1. Fields `hour`, `minutes` via `XposedHelpers.setIntField` (AOSP names).
2. Days: field `daysOfWeek` (AOSP uses an `Alarm.DaysOfWeek` wrapper — try
   setting the Int bitmask field, then constructing the wrapper if present),
   then fallback `days` / `mDaysOfWeek` style names.
3. Setter methods `setHour`/`setMinutes`/`setDaysOfWeek` if fields fail.
4. If all fail → report FAIL with the list of names tried, so it is diagnosable
   on-device without recompiling; schedule save is skipped to avoid persisting
   a half-mutated Alarm.

Steps 4–7 of Apply schedule still run even if the Alarm mutation fails (Mi
Health sync uses the hour/min parameters directly, not the Alarm object).

## Cleanup (part of the rewrite)

- Delete the orphaned `xposed/` Gradle module directory (not included in
  `settings.gradle.kts`; abandoned split-module attempt).
- Delete legacy Xposed packaging: `app/src/main/assets/xposed_init` and the
  `xposedmodule`/`xposedminversion`/`xposedscope`/`xposeddescription` manifest
  meta-data; replace the `de.robv.android.xposed:api:82` dependency with
  `io.github.libxposed:api:101.0.1`; set `module.prop` `targetApiVersion=101`.
- Remove the phantom `.ModeLoggerReceiver` from `AndroidManifest.xml` — it
  references a class that does not exist.
- Prune dead manifest permissions: storage, `SET_ALARM`,
  `SCHEDULE_EXACT_ALARM`, `BIND_NOTIFICATION_LISTENER_SERVICE`. Keep
  `ACCESS_NOTIFICATION_POLICY` (DND control) and the Xposed metadata/scope.
- Delete unused `ModeLoggerReceiver`-era code paths and the old guessed method
  names (`notifySleepChange`, static `setZenMode`, `queryWakeAlarm`,
  `ENTER_ZENMODE`/`EXIT_ZENMODE` broadcasts, `tryInstanceMethod` singleton
  guessing).

## Error handling

- Each internal call wrapped individually; failures recorded as StepResults and
  reported, never thrown across steps.
- Receiver registration failures logged via `XposedBridge.log`.
- UI shows per-step OK/FAIL plus current sleep-mode state; a "module not
  responding" hint if no result arrives within a timeout (likely LSPosed scope
  not enabled or DeskClock not running).

## Testing

- No device/emulator with LSPosed is available in CI: verification is a
  successful Gradle build (`assembleDebug`) plus code review.
- JVM unit tests cover the pure logic: `Protocol` bitmask helpers,
  `StepResult` formatting, and the `Reflect` resolution helpers (fixture
  classes: static/instance calls, primitive-int parameters, null args,
  superclass fields, constructors).
- On-device verification checklist (manual, documented in the plan):
  apply schedule → check Clock app UI + Mi Health; start bedtime → DND on,
  Mi Home devices react; stop bedtime → sleep mode exits; state query matches
  powerkeeper reality.
