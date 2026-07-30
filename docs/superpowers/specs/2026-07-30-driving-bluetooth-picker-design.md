# Driving Mode Bluetooth Device Picker — Design

Date: 2026-07-30
Status: Approved by user
Branch: complex-triggers

## Goal

Let the user pick specific Bluetooth devices in the driving mode detail screen.
Driving mode activates when connected to **any car-audio device OR any picked
device** (OR semantics, confirmed with user). The picked-device list lives in
the driving mode detail screen (confirmed with user).

## Background

- Driving mode uses the legacy path: `ModeSettings.drivingAutoDetect` +
  `drivingDetectMode`, mapped in `Models.kt` to
  `TriggerConfig.bluetooth = BluetoothTrigger(enabled, matchAnyCarAudio = true,
  targetMacs = emptyList())` — `targetMacs` is currently hardcoded empty.
- `DrivingTriggerManager.isBluetoothConnectedToTargets(targetMacs,
  matchAnyCarAudio)` already implements the desired OR logic (car-audio match
  OR target-MAC match). **No engine changes are needed.**
- Routing picked devices through the new complex-trigger system was rejected:
  `ComplexTriggerManager` deliberately skips `DYNAMIC_TRIGGER` modes to avoid
  double-managing driving mode (fixed earlier this same branch).

## Data Model

`ModeSettings` gains one field:

```kotlin
val drivingTargetDevices: Set<String> = emptySet() // Bluetooth MAC addresses
```

Mapping (both directions in `Models.kt`):

- `Mode.toModeConfig()`: `BluetoothTrigger(..., matchAnyCarAudio = true,
  targetMacs = s.drivingTargetDevices.toList())`
- `ModeConfig.toMode()`: `drivingTargetDevices =
  triggers?.bluetooth?.targetMacs?.toSet() ?: emptySet()`

Only MACs are persisted; display names are resolved at render time from the
bonded-device list (device names can change; unpaired devices fall back to
showing the raw MAC).

**Preserving devices across the auto-detect toggle (targeted fix):** today
`toModeConfig()` writes the driving `TriggerConfig` only when
`drivingAutoDetect` is on, so toggling detection off discards `targetMacs`
(and the detect mode) from the stored config. Since losing picked devices on
a toggle would be a real annoyance, `toModeConfig()` will write the driving
`TriggerConfig` whenever `id == "driving"` regardless of `drivingAutoDetect`.
This is inert on the engine side: `DrivingTriggerManager` only looks at modes
whose `type == DYNAMIC_TRIGGER`, and the type mapping is unchanged.
(Implementation must verify nothing else treats `triggers != null` as
"detection enabled".)

Backward compatibility: configs written before this feature have empty
`targetMacs` → `drivingTargetDevices` is empty → behavior unchanged.

## UI

Driving section of `ModeDetailScreen` (visible when `drivingAutoDetect` is on),
below the detection-mode row:

- One row per picked device: bonded-device name (fallback: MAC) + ✕ delete
  button. Visual style matches the existing trigger cards in custom modes.
- "＋ Add device" row opens a new `Screen.DrivingBluetoothPicker(mode)`
  (depth 2) that reuses the existing `BluetoothPickerScreen` — it already
  requests `BLUETOOTH_CONNECT` at runtime and shows an empty state.
- Selecting a device adds its MAC to `drivingTargetDevices` (Set semantics
  give dedup for free) via `onSave`, then pops back to the detail screen.

The device list applies to both detect modes (`DRIVING_DETECT_BLUETOOTH` and
`DRIVING_DETECT_MOTION_BLUETOOTH`) since they share the same
`BluetoothTrigger` block.

## Error Handling / Edge Cases

- **Bluetooth permission denied**: `BluetoothPickerScreen` already handles the
  runtime request and shows its empty state; user can retry on re-entry.
- **Device unpaired later**: MAC remains in the set; the manager simply never
  matches it; the row renders as the raw MAC and stays deletable.
- **No devices picked**: exactly today's behavior (car-audio match only).
- **Duplicate adds**: prevented by Set semantics.

## Testing

The repo has no unit-test infrastructure. Verification:

1. `./gradlew :app:assembleDebug` passes.
2. Round-trip check by inspection: `toModeConfig()` and `toMode()` mappings
   are symmetric (Set <-> List ordering is not user-visible; equality churn
   is avoided because JSON round-trips preserve list order).

## New Strings

| Key | en | zh-rCN |
|---|---|---|
| `driving_devices_title` | Bluetooth devices | 蓝牙设备 |
| `add_device` | Add device | 添加设备 |

The picker's top-bar title reuses the existing `trigger_bluetooth` string.

## Out of Scope

- Turning off `matchAnyCarAudio` (stays `true`; picked devices are additive).
- Migrating driving detection to the complex-trigger system.
- Motion detection (still unimplemented in `DrivingTriggerManager`).
