# Old Architecture Cleanup Analysis

## Executive Summary

The zero-process system_server architecture is complete. Analysis shows:
- **7 files can be safely deleted** (old app-process architecture)
- **3 files must be kept temporarily** (still used by UI for Activity Recognition registration)
- **0 files need review**

Total cleanup: Remove 7 files + 7 manifest entries = **14 deletions**

---

## Safe to Delete

### engine/EngineReceiver.kt
**Reason**: Fully replaced by `ModeAlarmReceiver` + `ScheduledModeManager`

- **Old**: Manifest receiver handling `ACTION_RESCHEDULE` and `ACTION_ALARM_TRIGGER` in app process
- **New**: `ModeAlarmReceiver` receives `ACTION_START_MODE`/`ACTION_END_MODE` from `ScheduledModeManager` in system_server
- **References**: None in active code (only in BootReceiver which is also being deleted)
- **Manifest entry**: Lines 97-105 (`<receiver android:name=".engine.EngineReceiver">`)

### engine/ModeEngine.kt
**Reason**: Fully replaced by `RoutineCoreEngine` + `ModeActionExecutor`

- **Old**: App-process engine applying DND/grayscale/dark mode via direct APIs + root fallbacks
- **New**: `RoutineCoreEngine` orchestrates `ModeActionExecutor` which applies actions with system_server privileges
- **References**: Still imported by 3 UI files (HyperModesApp.kt:303, ModeDetailScreen.kt:211-213, BedtimeStateReceiver.kt:43-47)
- **Impact**: UI manual toggles and delete flows need migration to new API

### engine/ModeScheduler.kt
**Reason**: Fully replaced by `ScheduledModeManager`

- **Old**: Pure next-trigger calculation for app-process EngineReceiver
- **New**: `ScheduledModeManager.getNextOccurrence()` handles same logic in system_server
- **References**: Only used by EngineReceiver.kt (also being deleted)
- **Manifest entry**: None (library class)

### engine/EngineState.kt
**Reason**: No longer needed in zero-process architecture

- **Old**: SharedPreferences tracking reference counts + snapshots for app-process engine
- **New**: system_server maintains state in memory + Settings.Global for persistence
- **References**: Only used by ModeEngine.kt (also being deleted)
- **Manifest entry**: None (library class)

### engine/TimeChangedReceiver.kt
**Reason**: Replaced by system_server time change handling

- **Old**: Manifest receiver calling `EngineReceiver.rescheduleAll()` when time/timezone changes
- **New**: system_server's `ScheduledModeManager` handles rescheduling internally
- **References**: None in active code
- **Manifest entry**: Lines 109-117 (`<receiver android:name=".engine.TimeChangedReceiver">`)

### driving/BootReceiver.kt
**Reason**: No longer needed with persistent system_server architecture

- **Old**: BOOT_COMPLETED receiver re-registering Activity Recognition + calling `EngineReceiver.rescheduleAll()`
- **New**: system_server's `DrivingTriggerManager` persists across reboots, no re-registration needed
- **References**: None in active code
- **Manifest entry**: Lines 77-84 (`<receiver android:name=".driving.BootReceiver">`)
- **Note**: Activity Recognition is now handled by system_server `DrivingTriggerManager`

### receiver/BedtimeStateReceiver.kt
**Reason**: Fully replaced by `BedtimeListener` in system_server

- **Old**: Manifest receiver receiving DeskClock broadcasts, calling app-process `ModeEngine`
- **New**: `BedtimeListener` watches Settings.Secure + receives broadcasts directly in system_server
- **References**: None in active code
- **Manifest entry**: Lines 89-95 (`<receiver android:name=".receiver.BedtimeStateReceiver">`)
- **Note**: DeskClock hooks now communicate directly with system_server

---

## Must Keep (Still Used by UI)

### driving/DrivingDetector.kt
**Why keep**: UI still calls `DrivingDetector.ensureActivityRecognition()` for permission-gated Activity Recognition registration

**References**:
- `MainActivity.kt:19` - Re-registers on app launch
- `ModeDetailScreen.kt:320-321` - Re-registers when saving driving mode settings
- `DrivingDetectScreen.kt:50,70` - Re-registers when permissions granted

**Migration needed**: Move Activity Recognition registration to system_server (requires ACTIVITY_RECOGNITION system permission grant), then remove UI calls

### driving/BluetoothDrivingReceiver.kt
**Why keep**: Registered in manifest (lines 59-66), delegates to `DrivingDetector`

**Dependencies**: Required by `DrivingDetector.kt` which is still in use

**Migration needed**: Once DrivingDetector is removed, this can be deleted along with manifest entry

### driving/ActivityTransitionReceiver.kt
**Why keep**: Registered in manifest (lines 69-75), receives Activity Recognition callbacks, delegates to `DrivingDetector`

**Dependencies**: Required by `DrivingDetector.kt` which is still in use

**Migration needed**: Once DrivingDetector is removed, this can be deleted along with manifest entry

---

## Needs Review

*None* - All files have clear replacement paths.

---

## Migration Requirements

Before deleting the "Must Keep" files, complete these migrations:

### 1. UI Manual Toggle Migration
**Current**: `ModeDetailScreen.kt:211` and `HyperModesApp.kt:303` call `ModeEngine(context).activate/deactivate()`

**Required**: Create UI bridge to system_server
```kotlin
// New API in systemserver/
object ModeControlBridge {
    fun activateMode(context: Context, modeId: String) {
        // Write to Settings.Global to trigger RoutineCoreEngine
    }
    fun deactivateMode(context: Context, modeId: String) {
        // Write to Settings.Global to trigger RoutineCoreEngine
    }
}
```

### 2. Activity Recognition Migration
**Current**: UI calls `DrivingDetector.ensureActivityRecognition()` which registers Activity Recognition from app process

**Required**: Move registration to system_server `DrivingTriggerManager`
- Grant ACTIVITY_RECOGNITION to system_server via Xposed hook
- Register Activity Recognition client in `DrivingTriggerManager.init()`
- Remove all UI calls to `DrivingDetector.ensureActivityRecognition()`

---

## Git Commands (Safe Deletions Only)

```bash
# Remove old engine files
git rm "app/src/main/java/com/banana/hypermodes/engine/EngineReceiver.kt"
git rm "app/src/main/java/com/banana/hypermodes/engine/ModeEngine.kt"
git rm "app/src/main/java/com/banana/hypermodes/engine/ModeScheduler.kt"
git rm "app/src/main/java/com/banana/hypermodes/engine/EngineState.kt"
git rm "app/src/main/java/com/banana/hypermodes/engine/TimeChangedReceiver.kt"

# Remove obsolete receivers
git rm "app/src/main/java/com/banana/hypermodes/driving/BootReceiver.kt"
git rm "app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt"

# Remove manifest entries (manual edit required)
# Edit app/src/main/AndroidManifest.xml:
# - Lines 77-84: driving.BootReceiver
# - Lines 89-95: receiver.BedtimeStateReceiver
# - Lines 97-105: engine.EngineReceiver
# - Lines 109-117: engine.TimeChangedReceiver
```

**Note**: Manifest edits and UI migration must be done manually before the files can be deleted without breaking the build.

---

## Architecture Comparison

### Old App-Process Architecture
```
App Process (cold-start on every trigger):
├── EngineReceiver (alarm/reschedule entry)
├── ModeEngine (apply actions via APIs + root)
├── ModeScheduler (pure logic)
├── EngineState (SharedPrefs tracking)
├── TimeChangedReceiver (reschedule on time change)
├── DrivingDetector (BT + Activity Recognition)
├── BluetoothDrivingReceiver (manifest receiver)
├── ActivityTransitionReceiver (manifest receiver)
├── BootReceiver (re-register on boot)
└── BedtimeStateReceiver (DeskClock broadcasts)
```

### New Zero-Process Architecture
```
system_server (persistent, no app process needed):
├── RoutineCoreEngine (singleton orchestrator)
├── ModeActionExecutor (apply actions with system privileges)
├── ScheduledModeManager (alarm scheduling + trigger calculation)
├── DrivingTriggerManager (BT detection in system_server)
├── BedtimeListener (Settings.Secure observer)
└── ModeAlarmReceiver (app-process receiver for alarms only)
```

**Key Improvement**: No app process needed for background operation. Only `ModeAlarmReceiver` runs in app process to bridge AlarmManager callbacks to system_server.

---

## Blockers for Full Cleanup

1. **UI still uses ModeEngine directly** (3 files: HyperModesApp.kt, ModeDetailScreen.kt, BedtimeStateReceiver.kt)
   - Need Settings.Global bridge API for manual toggles
   
2. **UI still manages Activity Recognition registration** (3 files reference DrivingDetector)
   - Need to move registration to system_server with proper permissions

3. **Manifest receivers still registered** (BluetoothDrivingReceiver, ActivityTransitionReceiver)
   - Can remove once DrivingDetector is migrated to system_server

---

## Recommendation

**Phase 1 (Safe Now)**: Delete the 7 files marked "Safe to Delete" + remove their manifest entries after creating UI bridge

**Phase 2 (After UI Migration)**: Complete Activity Recognition migration to system_server, then delete remaining 3 driving files + manifest entries

**Total cleanup potential**: 10 files + 7 manifest entries across both phases
