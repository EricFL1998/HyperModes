# HyperModes Uninstall Cleanup Design

## Summary

HyperModes must stop all of its automation when `com.banana.hypermodes` is genuinely uninstalled. The cleanup must cancel scheduled callbacks held by `system_server`, restore any currently active mode effects, stop runtime observers and trigger listeners, remove the durable global configuration, and make already-loaded hook code inert until its host process restarts.

An APK replacement is not an uninstall. A normal update (`Intent.EXTRA_REPLACING == true`) must preserve all modes, active state, and schedules.

## Root Cause

The zero-process mode engine deliberately moved ownership outside the app process:

- `RoutineCoreEngine` runs inside `system_server`.
- `ScheduledModeManager` registers `AlarmManager.OnAlarmListener` instances from the system context.
- Mode configuration and `activeModeId` live in `Settings.Global["pixel_routines_full_config"]`.
- Mode effects such as DND, suspended apps, display changes, and the status-bar icon are applied by the system-server engine.

Android uninstall removes the APK and app-private data, but it does not automatically:

- remove a custom `Settings.Global` entry;
- cancel listener alarms owned by the already-running `system_server` process;
- unload code already injected into `system_server` or DeskClock;
- call HyperModes's normal mode-deactivation path.

There is currently no package-removal listener that bridges Android's package lifecycle into `RoutineCoreEngine`. Consequently, pending alarms can still activate or deactivate modes after the UI has disappeared, and an active mode can remain applied with no remaining UI through which to turn it off.

A related defect exists when the global config is removed or becomes blank: `RoutineCoreEngine.loadConfigFromSettings()` returns without cancelling the previously loaded schedules or reverting the previously active mode. Missing config therefore has to be treated as an empty runtime configuration, not as “leave the old state untouched.”

## Goals

- On genuine uninstall, immediately prevent any new HyperModes automation from running.
- Cancel every tracked start and end alarm in `ScheduledModeManager`.
- Restore all effects of the currently active mode and remove its status-bar icon.
- Stop Bluetooth/driving, bedtime, and configuration-observer callbacks owned by the engine.
- Clear in-memory modes and manual-dismiss records.
- Remove `Settings.Global["pixel_routines_full_config"]` so reinstall does not resurrect old schedules.
- Best-effort disable the DeskClock-owned bedtime schedule while the injected DeskClock bridge is available.
- Preserve all configuration and behavior during an APK update.
- Make cleanup idempotent and safe if Android emits more than one removal action.
- Prevent an alarm/removal race from reactivating a mode after cleanup starts.

## Non-goals

- Do not replace `OnAlarmListener` scheduling with app-process receivers or package-owned `PendingIntent`s.
- Do not require the HyperModes application process to run during uninstall.
- Do not change normal schedule semantics, mode priority, or UI behavior.
- Do not add a user-visible “reset” screen as part of this fix.
- Do not clear configuration when the app is force-stopped, swiped from recents, or updated in place.
- Do not claim guaranteed cleanup of DeskClock private state when the DeskClock process and injected command bridge are not alive; that external cleanup is best-effort because HyperModes cannot execute its removed APK code in a newly started DeskClock process after uninstall.

## Package Lifecycle Policy

Introduce a small, pure decision function that accepts:

- broadcast action;
- package name parsed from `intent.data?.schemeSpecificPart`;
- `Intent.EXTRA_REPLACING`.

It returns one of:

- `IGNORE` — another package or an unrelated action;
- `REPLACEMENT_STARTED` — matching `ACTION_PACKAGE_REMOVED` with `EXTRA_REPLACING=true`;
- `REPLACEMENT_FINISHED` — matching `ACTION_PACKAGE_ADDED` with `EXTRA_REPLACING=true`;
- `REMOVE` — matching `ACTION_PACKAGE_REMOVED` or `ACTION_PACKAGE_FULLY_REMOVED` without replacement.

Both system-server and DeskClock package receivers use this same policy. Keeping action interpretation pure avoids duplicating subtle update-vs-uninstall rules and allows local JVM tests without Android broadcast machinery.

## Architecture

### 1. System-server package receiver

`SystemModeHook` registers one dynamic receiver from the system context after `ActivityManagerService.systemReady()` for:

- `Intent.ACTION_PACKAGE_REMOVED`;
- `Intent.ACTION_PACKAGE_FULLY_REMOVED`;
- `Intent.ACTION_PACKAGE_ADDED`;
- data scheme `package`.

The receiver is separate from the signature-permission bridge receiver. Package lifecycle broadcasts are protected system broadcasts, and the lifecycle receiver must not depend on a custom permission whose defining APK is being removed.

For `com.banana.hypermodes`:

- replacement start marks the engine as `REPLACING` without cancelling schedules or clearing state;
- replacement finish returns the engine to `RUNNING` and reloads/reschedules from the preserved config;
- genuine removal invokes `RoutineCoreEngine.shutdownForPackageRemoval()`;
- duplicate removal events invoke the same idempotent method and have no additional effect.

Events for every other package are ignored.

### 2. Engine lifecycle state

`RoutineCoreEngine` owns an explicit thread-safe lifecycle state:

- `RUNNING` — normal automation is allowed;
- `REPLACING` — package replacement is in progress; automation continues from the already-loaded config, while package-presence failures must not be interpreted as uninstall;
- `REMOVED` — cleanup has started or completed; automation is permanently blocked for the life of this `system_server` process.

The transition into `REPLACING` does not cancel existing schedules or revert a mode. `REPLACEMENT_FINISHED` returns the engine to `RUNNING` and rebuilds schedules from the preserved config to ensure there is exactly one current listener set.

All public automation entry points and asynchronous callbacks check the lifecycle gate before changing system state:

- `activateMode()`;
- `deactivateMode()`;
- `rescheduleAllAlarms()`;
- scheduled start/end listeners;
- driving callbacks;
- bedtime callbacks;
- config observer reloads.

The scheduled callback additionally verifies that the module package is still installed. If it is absent while the lifecycle is `RUNNING`, the callback does not activate or deactivate anything and requests the same idempotent removal cleanup. If the lifecycle is `REPLACING`, a package-lookup failure only skips that boundary and never deletes user state. This is defense in depth for a missed lifecycle broadcast without turning a transient update window into a destructive uninstall cleanup.

The lifecycle flag changes to `REMOVED` before any asynchronous resource is cancelled. Therefore, an alarm already queued on the main handler can run only as a no-op once cleanup begins.

### 3. Idempotent engine shutdown

`RoutineCoreEngine.shutdownForPackageRemoval()` executes on the engine's main handler and is safe to call repeatedly. The first call changes the lifecycle state to `REMOVED`; later calls return after logging that cleanup has already started or completed.

Cleanup order:

1. Mark the lifecycle `REMOVED` to close the race gate.
2. Cancel all tracked scheduled alarms.
3. Unregister the engine config observer.
4. Stop driving and bedtime listeners without allowing their teardown callbacks to activate another mode.
5. Revert the current mode's system effects and remove the status-bar icon.
6. Clear `currentActiveMode`, `allModes`, and manual-dismiss records.
7. Send an explicit best-effort `ACTION_DISABLE_BEDTIME` command to the DeskClock package.
8. Remove `Settings.Global["pixel_routines_full_config"]` by writing `null` through `Settings.Global.putString`.
9. Log a concise per-step result and a final completion result.

Every step is isolated with protective error handling. Failure to restore one subsystem must not prevent alarms from being cancelled, other effects from being restored, or the durable config from being removed.

Normal mode deactivation and uninstall restoration share the same action-reversion implementation. The action executor must attempt DND restore, app unsuspension, display restore, and icon removal independently so one controller failure does not skip later controllers.

### 4. Scheduled alarm ownership

`ScheduledModeManager.cancelAllSchedules()` becomes an explicit lifecycle operation rather than a private implementation detail. It:

- calls `AlarmManager.cancel(listener)` for every tracked listener;
- continues if one cancellation fails;
- clears the in-memory listener map in all cases;
- is safe when the map is already empty.

Each `OnAlarmListener` checks the engine lifecycle/package-availability gate before reading dismiss state or invoking activation/deactivation. The manager does not schedule new alarms after the engine reaches `REMOVED`.

### 5. Observer and trigger cleanup

`RoutineCoreEngine`, `BedtimeListener`, and other components retain the exact receiver/observer objects they register so teardown can unregister them safely.

Required lifecycle operations:

- engine config observer: unregister from the `ContentResolver`;
- `DrivingTriggerManager`: unregister its Bluetooth receiver and clear local mode state; its shutdown path must not call back into a removed engine to persist state;
- `BedtimeListener`: unregister its broadcast receiver and both secure-settings observers, clear its mode list, and ignore callbacks after shutdown;
- package lifecycle receiver: it may remain registered in `system_server` after removal, but all matching later events observe the terminal `REMOVED` state and do no work.

If the global config becomes null or blank while the engine is still running, the engine applies an empty configuration:

- cancel schedules;
- remove trigger mode lists and listeners no longer needed;
- revert an active mode;
- clear in-memory mode and dismiss state.

This path does not mark the package `REMOVED`; a later valid config write can initialize automation again.

### 6. DeskClock bedtime cleanup

DeskClock stores its own bedtime master switch, wake alarm, and active sleep state outside HyperModes's global config. Deleting the bedtime mode currently disables these through `BedtimeController.disableBedtime()`.

To reuse that behavior during uninstall:

- `DeskClockHook` registers a separate protected-package-lifecycle receiver while its host process is alive;
- genuine HyperModes removal invokes `BedtimeController.disableBedtime()` directly in the DeskClock process;
- replacement events do nothing;
- `system_server` also sends the existing explicit `ACTION_DISABLE_BEDTIME` command before its configuration is removed, covering the normal case where the DeskClock command receiver is alive.

This cleanup is deliberately best-effort. If DeskClock is not running at uninstall, no injected DeskClock code is available to modify DeskClock's private storage. The guaranteed acceptance criteria concern HyperModes-owned schedules, global config, and applied system effects; DeskClock cleanup is reported separately in logs.

## Runtime Data Flow

### Genuine uninstall

1. Android emits a package-removal broadcast for `com.banana.hypermodes` with `EXTRA_REPLACING=false`.
2. The system-server lifecycle receiver classifies it as `REMOVE`.
3. The engine atomically changes from `RUNNING` to `REMOVED`.
4. Any queued alarm/trigger callback reaches the lifecycle gate and exits.
5. The engine cancels alarm listeners and unregisters observers/triggers.
6. The engine restores the active mode effects.
7. DeskClock cleanup is attempted.
8. The global config is removed.
9. Already-loaded hooks remain mapped until their host processes restart, but they are inert.

### APK update

1. Android emits package removal with `EXTRA_REPLACING=true`.
2. The receiver classifies it as `REPLACEMENT_STARTED`; no state or config is cleared.
3. Android installs the replacement and emits package added with `EXTRA_REPLACING=true`.
4. The receiver classifies it as `REPLACEMENT_FINISHED`.
5. The engine returns to `RUNNING`, reloads the preserved config, and rebuilds schedules without duplicating listeners.

### Alarm racing with uninstall

- If the alarm executes first, it may perform its normal boundary action; uninstall cleanup immediately restores and clears it afterward.
- If removal cleanup changes the lifecycle first, the alarm callback sees `REMOVED` and performs no action.
- At no point after the cleanup gate closes may a callback write `activeModeId`, apply a mode, or create another alarm.

## Error Handling and Diagnostics

Log only lifecycle boundaries and failures:

- matching package action and whether it was replacement/removal;
- transition into `REPLACING`, `RUNNING`, or `REMOVED`;
- number of tracked alarms cancelled;
- observer/receiver teardown failures;
- active-mode restore result;
- DeskClock cleanup dispatch/result when available;
- global config removal result;
- callback rejected because the engine is removed or the package is absent.

Do not log every package broadcast or successful package-presence check.

Cleanup is monotonic: once `REMOVED`, an exception cannot return the engine to `RUNNING`. A failed cleanup step can be retried internally if safe, but duplicate external removal broadcasts do not reactivate the engine.

## Testing Strategy

### Pure JVM policy tests

Test the shared package lifecycle classifier:

- matching `PACKAGE_REMOVED`, `replacing=false` -> `REMOVE`;
- matching `PACKAGE_FULLY_REMOVED` -> `REMOVE`;
- matching `PACKAGE_REMOVED`, `replacing=true` -> `REPLACEMENT_STARTED`;
- matching `PACKAGE_ADDED`, `replacing=true` -> `REPLACEMENT_FINISHED`;
- another package -> `IGNORE`;
- missing/malformed package URI -> `IGNORE`;
- unrelated action -> `IGNORE`.

### Engine cleanup tests

Use small injected/internal test seams for settings, lifecycle resources, and action restoration rather than constructing Android framework services in local JVM tests.

Required cases:

- first removal marks the engine stopped before invoking cleanup collaborators;
- all alarms are cancelled;
- active mode restoration is attempted;
- trigger and observer cleanup runs;
- in-memory modes and dismiss records are cleared;
- global config deletion runs even when an earlier step throws;
- repeated removal is safe and does not repeat destructive effects;
- activation, deactivation, and rescheduling are rejected after removal;
- missing config while running is treated as empty and cleans stale runtime state;
- replacement start/finish never clears config and finishes with one rebuilt schedule set.

### Scheduler tests

Use a fake alarm gateway to verify:

- two listeners per enabled scheduled mode are tracked;
- `cancelAllSchedules()` cancels every tracked listener and clears the registry;
- cancellation continues when one fake alarm throws;
- a queued callback cannot activate/deactivate after the lifecycle gate closes;
- a missing module package rejects the callback and requests cleanup;
- replacement state does not trigger destructive cleanup.

### Action restoration tests

Verify independent best-effort restoration:

- DND restore failure still permits app unsuspension, display restore, and icon removal;
- app-unsuspension failure still permits display restore and icon removal;
- repeated restoration does not reapply a mode.

### DeskClock tests

- genuine removal invokes `disableBedtime()`;
- replacement does not invoke it;
- another package does not invoke it;
- a failed DeskClock cleanup is contained and does not escape the receiver.

### Build verification

- `./gradlew :app:testDebugUnitTest`
- `./gradlew :app:assembleDebug`
- `git diff --check`

### Device verification

On a scoped HyperOS test device:

1. Install and enable HyperModes in system framework and relevant app scopes.
2. Create an enabled scheduled mode with a visible effect and a boundary one or two minutes in the future.
3. Activate a mode that enables DND/display changes and suspends a test app.
4. Confirm `settings get global pixel_routines_full_config` contains the configuration.
5. Uninstall HyperModes without disabling or deleting the mode first.
6. Confirm the active effects and status-bar icon are removed promptly.
7. Wait past both the scheduled start and end boundaries; confirm no HyperModes effect runs.
8. Confirm the global setting no longer exists.
9. Reinstall and verify old modes are not resurrected.
10. Repeat with `adb install -r` and verify configuration and schedules survive the update.
11. Repeat while an alarm boundary is close to uninstall and verify no mode remains active afterward.
12. If DeskClock is running, verify its bedtime master schedule is disabled; if it is not running, verify the limitation is logged without affecting the guaranteed HyperModes cleanup.

## Acceptance Criteria

- Genuine uninstall immediately makes the system-server engine inert.
- Every HyperModes-owned scheduled alarm is cancelled.
- No scheduled callback can activate a mode after cleanup starts.
- An active mode is reverted, including best-effort DND restoration, app unsuspension, display restoration, and icon removal.
- Engine observers and dynamic trigger listeners are stopped.
- `Settings.Global["pixel_routines_full_config"]` is removed.
- Reinstall does not restore the removed installation's old schedules.
- `EXTRA_REPLACING=true` updates preserve config and resume with one non-duplicated schedule set.
- Missing global config cannot leave stale schedules or active runtime state behind.
- Duplicate removal broadcasts do not crash, reschedule, or reapply a mode.
- DeskClock bedtime cleanup is attempted when the injected DeskClock bridge is available, and any inability to perform that external cleanup is contained and diagnosable.
