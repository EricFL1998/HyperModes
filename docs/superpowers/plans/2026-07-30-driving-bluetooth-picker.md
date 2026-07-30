# Driving Mode Bluetooth Device Picker Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Let the user pick specific Bluetooth devices in the driving mode detail screen; driving activates on any car-audio device OR any picked device.

**Architecture:** Extend the legacy driving config path. `ModeSettings` gains `drivingTargetDevices: Set<String>` (MACs), mapped to `TriggerConfig.bluetooth.targetMacs` which `DrivingTriggerManager` already ORs with car-audio matching. No engine changes. UI adds a device list to the driving section of `ModeDetailScreen` and a new `Screen.DrivingBluetoothPicker` reusing the existing `BluetoothPickerScreen`.

**Tech Stack:** Kotlin, Jetpack Compose (Miuix), kotlinx.serialization (config JSON in Settings.Global).

**Spec:** `docs/superpowers/specs/2026-07-30-driving-bluetooth-picker-design.md`

## Global Constraints

- No changes in `systemserver/` — `DrivingTriggerManager` already implements the OR semantics (`targetMacs` + `matchAnyCarAudio`).
- `matchAnyCarAudio` stays `true`; picked devices are additive.
- `toModeConfig()` writes the driving `TriggerConfig` whenever `id == "driving"` (even with auto-detect off) so picked devices survive toggling. Verified safe: the only `mode.triggers` consumer besides `Models.kt` is `DrivingTriggerManager.kt:153`, gated on `type == DYNAMIC_TRIGGER`.
- Every new string goes in BOTH `app/src/main/res/values/strings.xml` and `app/src/main/res/values-zh-rCN/strings.xml`.
- This repo has no unit-test infrastructure (spec confirms). Verification = `./gradlew.bat :app:assembleDebug` BUILD SUCCESSFUL + the round-trip inspection in Task 1.
- Commits: conventional style (`feat:` / `fix:`) with `Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>` trailer. Commit ONLY the files listed in each task's commit step — the working tree contains unrelated v1.3 work and several files are already staged; always `git commit -- <paths>` with explicit paths.
- Note: `.\gradlew.bat :app:assembleDebug` can exceed 10 minutes on a cold daemon; run it in the background if the foreground call times out.

---

### Task 1: Data model — `drivingTargetDevices` in ModeSettings + config mapping

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/data/Models.kt`

**Interfaces:**
- Consumes: `BluetoothTrigger(enabled: Boolean, matchAnyCarAudio: Boolean, targetMacs: List<String>)` and `TriggerConfig(bluetooth: BluetoothTrigger?, motion: MotionTrigger?)` from `systemserver/config/ModeConfig.kt:83-93` (already exist).
- Produces: `ModeSettings.drivingTargetDevices: Set<String>` (default `emptySet()`), persisted as `ModeConfig.triggers.bluetooth.targetMacs` for the driving mode. Task 2 reads `ModeSettings.drivingTargetDevices`.

- [ ] **Step 1: Add the field to ModeSettings**

In `Models.kt`, inside `data class ModeSettings`, immediately after the `drivingDetectMode` line (currently line 60), add:

```kotlin
    // Specific Bluetooth devices that trigger driving detection in addition
    // to any car-audio device (MAC addresses).
    val drivingTargetDevices: Set<String> = emptySet(),
```

- [ ] **Step 2: Write devices into the config (and preserve them across toggles)**

In `Models.kt` `fun Mode.toModeConfig()`, replace this block (currently lines 199-215):

```kotlin
    // Build trigger config only for the built-in driving mode.
    val triggers = if (id == "driving" && s.drivingAutoDetect) {
        TriggerConfig(
            bluetooth = BluetoothTrigger(
                enabled = s.drivingDetectMode == DRIVING_DETECT_BLUETOOTH ||
                         s.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH,
                matchAnyCarAudio = true,
                targetMacs = emptyList()
            ),
```

with:

```kotlin
    // Build trigger config for the built-in driving mode. Written even when
    // auto-detection is off so the detect mode and picked devices survive
    // toggling — DrivingTriggerManager only reads this for DYNAMIC_TRIGGER
    // modes, so it stays inert while detection is disabled.
    val triggers = if (id == "driving") {
        TriggerConfig(
            bluetooth = BluetoothTrigger(
                enabled = s.drivingDetectMode == DRIVING_DETECT_BLUETOOTH ||
                         s.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH,
                matchAnyCarAudio = true,
                targetMacs = s.drivingTargetDevices.toList()
            ),
```

(The `motion = ...` tail of the block is unchanged.)

- [ ] **Step 3: Read devices back from the config**

In `Models.kt` `fun ModeConfig.toMode(...)`, in the `ModeSettings(...)` construction, immediately after the line `drivingDetectMode = drivingDetectMode,` add:

```kotlin
            drivingTargetDevices = triggers?.bluetooth?.targetMacs?.toSet() ?: emptySet(),
```

- [ ] **Step 4: Verify round-trip by inspection**

Confirm the mapping is symmetric by re-reading the three edited spots:
- `toModeConfig()`: `targetMacs = s.drivingTargetDevices.toList()`
- `toMode()`: `drivingTargetDevices = triggers?.bluetooth?.targetMacs?.toSet() ?: emptySet()`
- List order survives the JSON round-trip, so no equality churn; empty set <-> empty list.

- [ ] **Step 5: Build**

Run: `.\gradlew.bat :app:assembleDebug --console=plain`
Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git commit -m "feat: persist picked Bluetooth devices for driving detection

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>" -- app/src/main/java/com/banana/hypermodes/data/Models.kt
```

---

### Task 2: Driving device list UI + picker navigation + strings

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/ui/ModeDetailScreen.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`

**Interfaces:**
- Consumes: `ModeSettings.drivingTargetDevices: Set<String>` (Task 1); existing `BluetoothPickerScreen(onBack: () -> Unit, onSelect: (BluetoothDeviceEntry) -> Unit)` with `BluetoothDeviceEntry(name: String, address: String)` from `ui/BluetoothPickerScreen.kt`.
- Produces: `Screen.DrivingBluetoothPicker(val mode: Mode)`; `ModeDetailScreen` param `onOpenDrivingBluetoothPicker: (Mode) -> Unit`; shared composable `TriggerRowCard(icon: String, label: String, onDelete: () -> Unit)`.

- [ ] **Step 1: Add strings (both locales)**

In `app/src/main/res/values/strings.xml`, before `</resources>` add:

```xml
    <string name="driving_devices_title">Bluetooth devices</string>
    <string name="add_device">Add device</string>
```

In `app/src/main/res/values-zh-rCN/strings.xml`, before `</resources>` add:

```xml
    <string name="driving_devices_title">蓝牙设备</string>
    <string name="add_device">添加设备</string>
```

- [ ] **Step 2: Extract a shared TriggerRowCard from TriggerCard**

In `ModeDetailScreen.kt`, the existing `TriggerCard` composable ends with a `Card(...) { Row { ... } }` block. Replace that entire `Card` block with a delegation, and add the new shared composable below `TriggerCard`:

```kotlin
    TriggerRowCard(icon = icon, label = label, onDelete = onDelete)
}

@Composable
fun TriggerRowCard(
    icon: String,
    label: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MiuixTheme.textStyles.body1,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = MiuixIcons.Basic.Close,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}
```

(This moves — not duplicates — the existing TriggerCard card body; all imports it uses already exist in the file.)

- [ ] **Step 3: Add the callback parameter to ModeDetailScreen**

In `ModeDetailScreen.kt` `fun ModeDetailScreen(...)`, add after the parameter `onOpenBluetoothTriggerPicker: (Mode) -> Unit,`:

```kotlin
    onOpenDrivingBluetoothPicker: (Mode) -> Unit,
```

Add the import at the top of the file:

```kotlin
import android.bluetooth.BluetoothManager
```

- [ ] **Step 4: Render the device list in the driving section**

In `ModeDetailScreen.kt`, inside `if (editedMode.id == "driving") { ... }`, immediately AFTER the `item { Card(...) { Row { ... Switch ... } } }` block that contains the `drivingAutoDetect` switch (i.e. just before the closing `}` of the `if (editedMode.id == "driving")` block, currently around line 357), insert:

```kotlin
                if (editedMode.settings.drivingAutoDetect) {
                    item {
                        SmallTitle(
                            text = stringResource(R.string.driving_devices_title),
                            modifier = Modifier.padding(start = 28.dp, top = 4.dp, bottom = 8.dp)
                        )
                    }

                    // Resolve bonded-device names for display; falls back to the
                    // raw MAC when unpaired or BLUETOOTH_CONNECT is denied.
                    val deviceNames = remember(editedMode.settings.drivingTargetDevices) {
                        val names = mutableMapOf<String, String>()
                        try {
                            val manager = context.getSystemService(BluetoothManager::class.java)
                            manager?.adapter?.bondedDevices?.forEach { device ->
                                device.name?.let { names[device.address] = it }
                            }
                        } catch (_: SecurityException) {
                            // Permission denied: labels fall back to MAC addresses
                        }
                        names
                    }

                    editedMode.settings.drivingTargetDevices.forEach { address ->
                        item {
                            TriggerRowCard(
                                icon = "🎧",
                                label = deviceNames[address] ?: address,
                                onDelete = {
                                    editedMode = editedMode.copy(
                                        settings = editedMode.settings.copy(
                                            drivingTargetDevices =
                                                editedMode.settings.drivingTargetDevices - address
                                        )
                                    )
                                    onSave(editedMode)
                                }
                            )
                        }
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)
                                .padding(bottom = 12.dp),
                            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                            onClick = { onOpenDrivingBluetoothPicker(editedMode) }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "＋",
                                    style = MiuixTheme.textStyles.title2,
                                    color = MiuixTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = stringResource(R.string.add_device),
                                    style = MiuixTheme.textStyles.body1
                                )
                            }
                        }
                    }
                }
```

(`context` is already available: `val context = LocalContext.current` at the top of `ModeDetailScreen`.)

- [ ] **Step 5: Add the navigation Screen + depth entry**

In `HyperModesApp.kt`, in `sealed class Screen`, add after `data class BluetoothTriggerPicker(val mode: Mode) : Screen()`:

```kotlin
    data class DrivingBluetoothPicker(val mode: Mode) : Screen()
```

In the private `Screen.depth()` function at the bottom of the file, extend the depth-2 line so it reads:

```kotlin
    is Screen.DisplayOptions, is Screen.DeviceControl, is Screen.Repeat, is Screen.AppPicker,
    is Screen.AppTriggerPicker, is Screen.WifiTriggerPicker, is Screen.BluetoothTriggerPicker,
    is Screen.DrivingBluetoothPicker,
    is Screen.DrivingDetect -> 2
```

- [ ] **Step 6: Wire the picker branch in the screen `when`**

In `HyperModesApp.kt`, in the big `when (val screen = currentScreen)` (or equivalent `when` over screens), immediately after the `is Screen.BluetoothTriggerPicker -> { ... }` branch, add:

```kotlin
                is Screen.DrivingBluetoothPicker -> {
                    val mode = editingMode ?: screen.mode
                    BluetoothPickerScreen(
                        onBack = {
                            currentScreen = Screen.ModeDetail(editingMode ?: screen.mode)
                        },
                        onSelect = { device ->
                            if (!mode.settings.drivingTargetDevices.contains(device.address)) {
                                val updated = mode.copy(
                                    settings = mode.settings.copy(
                                        drivingTargetDevices =
                                            mode.settings.drivingTargetDevices + device.address
                                    )
                                )
                                editingMode = updated
                                upsertMode(updated)
                            }
                        }
                    )
                }
```

- [ ] **Step 7: Pass the callback at the ModeDetailScreen call site**

In `HyperModesApp.kt`, at the `ModeDetailScreen(...)` call, add after the `onOpenBluetoothTriggerPicker = { ... },` argument:

```kotlin
                        onOpenDrivingBluetoothPicker = { updated ->
                            editingMode = updated
                            currentScreen = Screen.DrivingBluetoothPicker(updated)
                        },
```

- [ ] **Step 8: Build**

Run: `.\gradlew.bat :app:assembleDebug --console=plain`
Expected: BUILD SUCCESSFUL

- [ ] **Step 9: Commit**

```bash
git commit -m "feat: driving mode Bluetooth device picker UI

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>" -- app/src/main/java/com/banana/hypermodes/ui/ModeDetailScreen.kt app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt app/src/main/res/values/strings.xml app/src/main/res/values-zh-rCN/strings.xml
```

---

## Self-Review Notes

- **Spec coverage:** data model (Task 1 Steps 1-3), preserve-across-toggle (Task 1 Step 2), device list + add row in detail screen (Task 2 Step 4), picker navigation (Task 2 Steps 5-7), strings both locales (Task 2 Step 1), no engine changes (no `systemserver/` files touched), build verification (Task 1 Step 5, Task 2 Step 8). Spec's out-of-scope items untouched.
- **Placeholder scan:** none — every code step contains the full code.
- **Type consistency:** `drivingTargetDevices: Set<String>` (Task 1) is read with `+ device.address` / `- address` / `contains` (Task 2) — all Set ops. `TriggerRowCard(icon, label, onDelete)` signature identical at definition (Task 2 Step 2) and use (Task 2 Step 4). `onOpenDrivingBluetoothPicker: (Mode) -> Unit` identical in declaration (Step 3) and call site (Step 7). `Screen.DrivingBluetoothPicker(val mode: Mode)` identical in Steps 5/6/7.
