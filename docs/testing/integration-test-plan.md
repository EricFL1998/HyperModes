# Integration Test Plan

## Overview

This document outlines manual integration tests for the HyperModes system. These tests verify the entire system works correctly on a real Android device with the Xposed module installed.

## Prerequisites

- Android device with LSPosed/EdXposed installed
- HyperModes module installed and enabled
- ADB access for verification commands
- Test applications installed for suspension testing

## Test Cases

### Test 1: Config Persistence Test

**Objective**: Verify mode configurations persist across system_server restarts

**Steps**:
1. Configure a mode via Settings.Global:
   ```shell
   adb shell settings put global hypermode_config '{"modes":[{"id":"test","name":"Test Mode","dnd":true}]}'
   ```
2. Verify config is loaded:
   ```shell
   adb logcat | grep RoutineCoreEngine
   ```
3. Restart system_server:
   ```shell
   adb shell killall system_server
   ```
4. Wait for device to recover
5. Verify config is still present:
   ```shell
   adb shell settings get global hypermode_config
   ```

**Expected Result**: Config persists after restart, RoutineCoreEngine logs show config reloaded

**Status**: ❌ Not Tested

---

### Test 2: Mode Activation Test

**Objective**: Verify mode activation applies all configured settings

**Setup**:
```json
{
  "modes": [{
    "id": "bedtime",
    "name": "Bedtime",
    "dnd": true,
    "dndLevel": 2,
    "appSuspension": true,
    "suspendedPackages": ["com.instagram.android", "com.twitter.android"],
    "dimWallpaper": true,
    "keepScreenOff": false
  }]
}
```

**Steps**:
1. Write config to Settings.Global
2. Activate mode:
   ```shell
   adb shell settings put global hypermode_active_mode bedtime
   ```
3. Verify DND is enabled:
   ```shell
   adb shell dumpsys notification | grep "zen mode"
   ```
4. Verify apps are suspended:
   ```shell
   adb shell pm list packages --user 0 -e | grep instagram
   ```
5. Check launcher shows grayed icons

**Expected Result**: DND enabled, apps suspended, icons grayed in launcher

**Status**: ❌ Not Tested

---

### Test 3: Notification Filtering Test

**Objective**: Verify notification whitelist works during active mode

**Setup**:
```json
{
  "modes": [{
    "id": "focus",
    "name": "Focus",
    "dnd": false,
    "notificationFiltering": true,
    "allowedPackages": ["com.google.android.apps.messaging"]
  }]
}
```

**Steps**:
1. Write config and activate mode
2. Send notification from whitelisted app:
   ```shell
   adb shell am broadcast -a com.test.SEND_NOTIFICATION --es package com.google.android.apps.messaging
   ```
3. Send notification from non-whitelisted app:
   ```shell
   adb shell am broadcast -a com.test.SEND_NOTIFICATION --es package com.instagram.android
   ```
4. Verify only whitelisted notification appears

**Expected Result**: Only notifications from allowedPackages are shown

**Status**: ❌ Not Tested

---

### Test 4: App Suspension Test

**Objective**: Verify suspended apps cannot be launched

**Steps**:
1. Configure mode with app suspension
2. Activate mode
3. Verify app icon is grayed in launcher
4. Tap suspended app icon
5. Verify toast message appears: "App is paused for Bedtime mode"
6. Try launching via ADB:
   ```shell
   adb shell am start -n com.instagram.android/.mainactivity.MainActivity
   ```

**Expected Result**: App cannot launch, toast shown, ADB launch blocked

**Status**: ❌ Not Tested

---

### Test 5: Driving Mode Bluetooth Trigger

**Objective**: Verify driving mode activates when car bluetooth connects

**Setup**:
```json
{
  "modes": [{
    "id": "driving",
    "name": "Driving",
    "dnd": true,
    "triggers": [{
      "type": "bluetooth",
      "deviceAddress": "AA:BB:CC:DD:EE:FF"
    }]
  }]
}
```

**Steps**:
1. Write config with bluetooth trigger (use actual car BT address)
2. Disconnect car bluetooth
3. Connect car bluetooth
4. Verify mode activates:
   ```shell
   adb shell settings get global hypermode_active_mode
   ```
5. Disconnect car bluetooth
6. Verify mode deactivates

**Expected Result**: Mode activates on BT connect, deactivates on disconnect

**Status**: ❌ Not Tested

---

### Test 6: Bedtime Integration Test

**Objective**: Verify integration with Android's system bedtime feature

**Steps**:
1. Enable system bedtime in Settings > Digital Wellbeing > Bedtime mode
2. Set bedtime schedule (e.g., 10 PM - 7 AM)
3. Configure HyperModes bedtime:
   ```json
   {
     "modes": [{
       "id": "bedtime",
       "name": "Bedtime",
       "dnd": true,
       "appSuspension": true,
       "suspendedPackages": ["com.instagram.android"]
     }]
   }
   ```
4. Wait for bedtime to activate (or use `adb shell cmd time set` to skip ahead)
5. Verify HyperModes mode is active
6. Check logcat for EngineReceiver:
   ```shell
   adb logcat | grep EngineReceiver
   ```

**Expected Result**: HyperModes bedtime activates when system bedtime starts

**Status**: ❌ Not Tested

---

### Test 7: Scheduled Mode Test

**Objective**: Verify scheduled modes activate/deactivate at correct times

**Setup**:
```json
{
  "modes": [{
    "id": "work",
    "name": "Work Focus",
    "dnd": true,
    "schedule": {
      "enabled": true,
      "startTime": "09:00",
      "endTime": "17:00",
      "daysOfWeek": [1, 2, 3, 4, 5]
    }
  }]
}
```

**Steps**:
1. Write config with schedule
2. Set device time to 08:59 on a weekday:
   ```shell
   adb shell su -c "date 072209592026.00"
   ```
3. Wait 1 minute
4. Verify mode activates:
   ```shell
   adb shell settings get global hypermode_active_mode
   ```
5. Set device time to 17:01
6. Verify mode deactivates

**Expected Result**: Mode activates at 09:00, deactivates at 17:00

**Status**: ❌ Not Tested

---

## Regression Tests

After making changes, re-run:
1. Config Persistence Test (Test 1)
2. Mode Activation Test (Test 2)
3. App Suspension Test (Test 4)

## Performance Verification

During testing, monitor:
- Logcat for errors/warnings
- System responsiveness (no ANRs)
- Notification delivery speed
- Battery drain during active modes

## Known Issues

- Motion detection not implemented (TODO in DrivingDetectionHelper)
- dimWallpaper setting not implemented in SystemModeHook
- keepScreenOff setting not implemented in SystemModeHook

## Test Environment

- **Device**: _____________
- **Android Version**: _____________
- **LSPosed Version**: _____________
- **HyperModes Version**: _____________
- **Test Date**: _____________

---

### Test 8: Full-AOD Mode Display

**Objective:** Verify that the active mode display moves from the lockscreen to SystemUI Full-AOD at the same screen position and inherits native Full-AOD transforms.

**Prerequisites:**

- HyperOS Full-AOD is enabled with a supported full-screen wallpaper configuration.
- HyperModes is enabled for `com.android.systemui` in LSPosed.
- One mode with a visible icon and name is active.
- The ordinary `com.miui.aod` package is not required in the module scope.

**Diagnostic setup:**

```powershell
adb logcat -c
adb logcat -v threadtime | Select-String "HyperModes.LockHook|HyperModes.ModeDisplay|HyperModes.FullAod"
```

**Steps:**

1. Wake the device and leave it on the lockscreen.
2. Confirm the active mode icon and name are visible in the lockscreen indication area.
3. Turn the screen off and wait for Full-AOD to finish entering.
4. Confirm the icon and name remain visible at the same resting screen position.
5. Leave Full-AOD active long enough to observe a native burn-in position tick.
6. Confirm the mode display moves with the AOD host and does not move twice relative to the other AOD content.
7. While still in Full-AOD, activate a different mode and confirm the icon/name refresh.
8. Deactivate the mode and confirm the Full-AOD row hides.
9. Reactivate a mode, wake the device, and confirm the lockscreen row returns without a duplicate.
10. Repeat the lockscreen → Full-AOD → wake cycle twice.

**Required log boundaries:**

- `Full-AOD hooks installed`
- `onDreamingStarted: fullAod=true`
- `dream start: fullAod=true`
- `lockscreen bounds:`
- `full AOD placement:`
- `display refresh: active=true` or `display refresh: active=false`
- `onDreamingStopped observed; waiting for native AOD host exit`
- native `stopDozing()` handling followed by `dream stop`

**Expected result:**

- Full-AOD displays exactly one mode row.
- The row starts at the lockscreen row's screen position.
- The row inherits native AOD parent alpha, scale, and burn-in movement.
- No fixed 74 dp fallback position, forced alpha, forced visibility, or independent movement timer is used.
- Mode changes are reflected while the screen is off.
- Wake and repeated Dream lifecycles leave no stale or duplicate rows.
- SystemUI remains stable if coordinates are unavailable; that session logs `full AOD placement unavailable` and does not show an incorrectly positioned row.

**Status:** Not yet run on a connected device.
