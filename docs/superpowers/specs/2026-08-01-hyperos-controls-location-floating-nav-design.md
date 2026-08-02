# HyperOS Floating Navigation, Device Controls, and Location Triggers — Design

Date: 2026-08-01
Status: Approved by user
Branch: intent-config-import

## Goal

Add four related HyperOS features without replacing the existing mode engine:

1. Move the main-screen FAB lower and remove its duplicated bottom offsets.
2. Add silent-mode and airplane-mode overrides to Device Control.
3. Add continuous “Arrive at location” and “Leave location” mode triggers by reusing Xiaomi SecurityAdd for place selection and Xiaomi Polaris for geofencing.
4. Make the main tab capsule fully floating, with no full-width bottom background strip. Do not add blur because Gallery’s official Flutter implementation cannot be reused directly in this Compose app.

The work is divided into three independently testable boundaries: the main UI shell, privileged device actions, and Xiaomi location adapters. The location condition model is reusable by the future Automations screen, but this iteration exposes it only through mode triggers.

## Confirmed Product Decisions

- Use Xiaomi’s official location chain only. If SecurityAdd or Polaris is unavailable, do not add a third-party map or geofence fallback.
- Add location conditions to the existing mode-trigger list first and reserve shared model interfaces for the future Automations screen.
- Treat arrive/leave as continuous states, not one-shot events.
- Match Security Center’s fixed 500-metre geofence radius.
- Silent control is three-state: unchanged, enabled, or disabled. It does not expose separate media/vibration controls.
- Airplane control is three-state: unchanged, enabled, or disabled.
- Reject saves when airplane mode is enabled while Wi-Fi, Bluetooth, or 5G is also configured to be enabled.
- Do not hook Security Center’s private task editor or database. Reuse only SecurityAdd’s picker contract and Polaris’s geofence service.
- Reproduce Gallery’s clipped backdrop-filter visual in Compose rather than treating a translucent fill or private MIUI View blur as the primary implementation.

## Reverse-Engineering Evidence

### Gallery

The unpacked Gallery at `apk_decompiled/miuigallary_decompiler` is a Flutter/HyperOS Flutter application. Its UI lives in `libapp.so`, where the relevant native strings include `home_navBar`, `BackdropFilterLayer`, `ImageFilter::initBlur`, and `ClipRRectShader::Create`. This supports a clipped, local backdrop-filter design rather than a full-width navigation surface or an Android `View#setMiBackgroundBlurMode` implementation.

### Settings

The unpacked Settings app uses Xiaomi sound APIs:

- `MiuiSettings.SilenceMode.setSilenceMode(context, 4, null)` for silent.
- `MiuiSettings.SilenceMode.setSilenceMode(context, 1, null)` for DND.
- `MiuiSettings.SilenceMode.setSilenceMode(context, 0, null)` for normal.

For airplane mode, Settings prefers privileged framework behavior and includes the equivalent fallback of writing `Settings.Global["airplane_mode_on"]`, then broadcasting `android.intent.action.AIRPLANE_MODE` with boolean extra `state` to all users. Settings also guards emergency callback mode, SCBM, satellite state, user restrictions, and MIUI enterprise restrictions.

### Security Center and Polaris

Security Center’s `AddressSelectActivity` is non-exported and cannot be a stable integration surface. It delegates map selection to:

- Package: `com.miui.securityadd`
- Activity: `com.miui.auto_task.MapSelectActivity`

Security Center expects result extras `latitude`, `longitude`, `cityName`, `provinceName`, and `addressName`. For locations within China, it converts the returned map coordinates once before storing and registering them.

Security Center registers 500-metre geofences through:

- Package: `com.xiaomi.gnss.polaris`
- Service: `com.xiaomi.gnss.polaris.PolarisService`
- Root Binder descriptor: `com.xiaomi.gnss.polaris.IPolarisService`
- Geofence Binder descriptor: `com.xiaomi.gnss.polaris.geofence.IMiGeoManagerService`

Polaris transition values are `11` for enter and `12` for exit. It delivers explicit broadcasts to a component registered by the caller.

## Existing Architecture and Constraints

- The app currently starts in `MainTabsScreen` in `HyperModesApp.kt`.
- The active Miuix `FloatingNavigationBar` is placed in `Scaffold.bottomBar`; Scaffold therefore reserves a full-width bottom region and pads content above it.
- The existing custom `BottomTabBar.kt` is untracked and not currently used. It must be preserved while the active tab implementation is consolidated.
- The shared FAB is inside content already padded by Scaffold, then receives `navigationBarsPadding()` and another `80.dp` bottom padding. These offsets are double-counted.
- Device actions run from `system_server` through `ModeActionExecutor` and `DeviceController`.
- Mode configuration is serialized to `Settings.Global["pixel_routines_full_config"]`.
- Complex trigger types are persisted in `ModeConfig.ComplexTrigger` and aggregated by `ComplexTriggerManager` using OR semantics.
- The working tree already contains user modifications and untracked UI files. Implementation must preserve them and edit them in place.

## Part 1: Main UI Shell

### Layout

Replace the main-tabs `Scaffold.bottomBar` structure with one full-screen `Box` containing:

1. The `HorizontalPager` and each page’s scrolling content.
2. The shared FAB overlaid at `Alignment.BottomEnd`.
3. The floating tab capsule overlaid at `Alignment.BottomCenter`.

No full-width `Surface`, background, or bottom-bar slot may be drawn behind the capsule. Page content extends to the physical bottom edge and is visible behind the capsule while scrolling.

Each page reserves bottom scroll space equal to:

- measured capsule height;
- capsule-to-navigation visual gap;
- navigation-bar inset;
- a small final content gap.

This keeps the final card reachable above the capsule without removing the live content behind it during scrolling.

### Gallery-Style Backdrop Blur

Use the stable Haze Compose 1.7.2 source/effect pipeline (`dev.chrisbanes.haze:haze:1.7.2`):

- Register the pager/content container as the blur source.
- Clip the effect to the capsule’s squircle/rounded shape.
- Blur only the capsule bounds.
- Add a theme-aware translucent material tint above the blur.
- Add a subtle highlight/border so separation does not depend on blur alone.
- Preserve Miuix shadow, icon sizing, `selectableGroup`, `Role.Tab`, selected-state semantics, and at least 48dp touch targets.

Do not use `Modifier.blur()` because it blurs the composable’s own layer instead of the live content behind it. Do not use Android window blur because it is a cross-window effect. Do not make private MIUI View blur the primary path; Gallery evidence points to a clipped backdrop filter and private View APIs would require a fragile `AndroidView` bridge.

If the backdrop effect cannot initialize, render the same local capsule with a theme-aware translucent fill, border, and shadow. The fallback must remain fully floating and must never restore the full-width bottom strip.

### Insets

Keep the existing edge-to-edge Activity configuration with transparent system bars and disabled navigation-bar contrast enforcement.

The capsule owns one navigation-bar inset calculation. Disable Miuix’s built-in bottom inset padding if positioning is handled by the overlay. Page content, capsule, and FAB must not each independently add `navigationBarsPadding()`.

### FAB Position

Position the FAB from the same measured bottom-layout geometry as the capsule:

- right margin: 16dp;
- FAB bottom edge: 12dp above the capsule top;
- capsule bottom: one visual gap above the navigation-bar inset.

This replaces Scaffold content padding, `navigationBarsPadding()`, and the hardcoded additional `80.dp`. The FAB therefore moves visibly lower while remaining clear of the tab capsule and the last list item.

Landscape and short-height layouts must retain non-overlapping FAB and capsule bounds. If vertical space is insufficient, reduce the inter-component gap before moving either control into the gesture region.

## Part 2: Device Controls

### Configuration Model

Add two nullable booleans to both the UI `ModeSettings` and persisted `DeviceConfig`:

```kotlin
val silentMode: Boolean? = null
val airplaneMode: Boolean? = null
```

Semantics:

- `null`: leave unchanged;
- `true`: enable on mode entry;
- `false`: disable on mode entry.

Missing fields in older JSON decode to `null`, so no destructive migration is required. UI-to-config and config-to-UI mappings must be symmetric.

### Device Control UI

Add two `DropdownSettingItem` rows in `DeviceControlScreen`, following the existing Wi-Fi and Bluetooth interaction pattern:

- Silent mode
- Airplane mode

Each row has an enable/disable override toggle and an On/Off value selector. English and Simplified Chinese strings describe that the value applies while the mode is active and restores afterward.

The existing DND control and the new silent override both affect Xiaomi Silence/Zen state. If both are configured, the mode continues to save and restore its normal DND configuration, then applies the silent override, and finally applies DND. The final active state is therefore DND; the silent override has no visible effect while DND is enabled but remains stored for use if DND is later disabled. This is the confirmed DND-priority policy.

Unlike the current immediate-save behavior, validation must run before persisting an invalid combination. When `airplaneMode == true` and any of `enableWifi`, `enableBluetooth`, or `enable5g` is `true`:

- mark the relevant rows as conflicting;
- show an inline localized explanation;
- do not persist the invalid edit;
- require the conflicting wireless fields to be unchanged or disabled.

Airplane mode set to `false` does not conflict with wireless enable actions.

### Silent Execution

Add a focused Xiaomi silent adapter behind `DeviceController`:

1. Read the current Xiaomi silence/zen integer.
2. Save it only before the first active override.
3. When enabling silent, call `MiuiSettings.SilenceMode.setSilenceMode(context, 4, null)` reflectively.
4. When disabling silent, call the same API with `0`.
5. On mode exit, restore the exact saved integer, including `1` if the device was previously in DND.

If the Xiaomi API is unavailable, log a structured action failure and continue applying other settings. The existing DND controller remains independent; silent control does not replace it.

### Airplane Execution

Add a focused airplane adapter behind `DeviceController`:

1. Reject the action during emergency callback mode or SCBM.
2. Reject the action while satellite mode is active.
3. Respect `UserManager.DISALLOW_AIRPLANE_MODE` and available MIUI enterprise restrictions.
4. Save the original airplane state before the first active override.
5. Prefer the hidden privileged `ConnectivityManager.setAirplaneMode(boolean)` from `system_server`.
6. If that method is absent, use the Settings-compatible fallback:
   - write `Settings.Global["airplane_mode_on"]`;
   - broadcast `android.intent.action.AIRPLANE_MODE`;
   - include boolean extra `state`;
   - send to `UserHandle.ALL` with `FLAG_RECEIVER_REPLACE_PENDING`.
7. Restore through the same adapter on mode exit.

A blocked or failed airplane action must not abort unrelated mode actions. Log the exact guard or invocation that failed.

### Original-State Storage

Continue the existing persisted original-state pattern, adding dedicated keys for silent and airplane values. Capture each original only once while an override is active, and consume it on restoration. Restoration remains valid across app-process death because the values live in Settings, while normal engine shutdown and mode changes continue to call `DeviceController.restore()`.

### Device-Action Ordering and Restoration

`ModeActionExecutor.applyMode()` must apply device silent state before `DndController.setDndLevel()`, so DND wins whenever both are configured.

Original-state capture remains independent: `DeviceController` stores the Xiaomi Silence/Zen integer before applying the silent override, while `DndController` must be upgraded to store the pre-mode interruption filter before applying DND. On mode exit, `DeviceController.restore()` first restores its captured Xiaomi value, then `DndController.restore()` restores the pre-mode interruption filter as the final owner. This prevents combined configurations from always resetting to “allow all,” and makes DND-priority behavior reversible for silent-only, DND-only, and combined modes.

## Part 3: Shared Location Model

Define reusable serializable location types outside the mode-specific wrapper:

```kotlin
data class LocationTarget(
    val addressName: String,
    val cityName: String = "",
    val provinceName: String = "",
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int = 500
)

enum class LocationTransition { ARRIVE, LEAVE }
```

Add `ModeTrigger.Location` and `ComplexTrigger.Location` with:

- a random, persisted trigger UUID;
- `LocationTarget`;
- `LocationTransition`.

The shared types can later be wrapped by an Automations-screen condition without changing persisted location data. This iteration does not implement a full condition/action automation editor.

Validation requires:

- latitude in `[-90, 90]`;
- longitude in `[-180, 180]`;
- non-blank address name;
- radius fixed to 500 for newly created triggers.

Imported future configs may carry a different positive radius without data loss, but this UI does not expose radius editing.

## Part 4: Official Xiaomi Place Selection

### Capability Gate

Before enabling either location trigger type, query both halves of the official chain:

1. `com.miui.securityadd/com.miui.auto_task.MapSelectActivity` is resolvable and launchable.
2. The system-server Polaris adapter reports a supported, bindable geofence service.

Add package visibility for `com.miui.securityadd` and `com.xiaomi.gnss.polaris`. Return capability details through a signature-protected HyperModes bridge rather than assuming support from the device brand alone.

If either half is unavailable, keep the trigger type visible but disabled and show the missing component/service. This explains the limitation without silently hiding the requested feature.

### Picker Contract

Launch SecurityAdd with an explicit Activity Result contract and no input extras. On `RESULT_OK`, read:

- `latitude` as `Double`;
- `longitude` as `Double`;
- `cityName` as `String`;
- `provinceName` as `String`;
- `addressName` as `String`.

Reject missing, non-finite, out-of-range, or zeroed coordinates and blank address names. Cancellation leaves the mode unchanged. `ActivityNotFoundException`, `SecurityException`, and malformed results produce a localized error and no partial trigger.

### Coordinate Conversion

Mirror Security Center’s conversion once at the picker boundary:

- outside the documented China bounds, preserve coordinates;
- inside the bounds, apply the same GCJ-like offset subtraction used by Security Center;
- persist the converted WGS-like coordinates;
- never reconvert on reload, config import, or geofence re-registration.

Unit-test boundary and representative China/non-China coordinates to prevent double conversion.

## Part 5: Polaris Geofence Adapter

### Boundary

Create a dedicated `PolarisGeofenceAdapter` in the system-server trigger layer. It owns:

- support detection;
- explicit service startup/binding;
- Binder interface discovery;
- callback-component registration;
- geofence add/update/delete/list/find operations;
- reconnect and reconciliation;
- conversion of Polaris transitions into internal trigger state.

No other trigger manager references Polaris Binder classes or raw transaction values.

### Support Detection

A supported result requires all of the following:

- Android API level meets the Polaris requirements;
- `persist.sys.gps.fence` or `persist.sys.gps.polaris` advertises support;
- the explicit Polaris service exists;
- binding succeeds without `SecurityException`;
- `IPolarisService.getGeoManagerService()` returns a live Binder;
- callback registration and a read-only geofence call succeed.

Any failure produces a typed unsupported reason. Do not spoof callers, bypass exported checks, hook Security Center internals, or write its database.

### Fence Registration

For every enabled `ComplexTrigger.Location`:

- geofence ID is derived from a persisted random trigger UUID plus an install-scoped random secret;
- package namespace remains `com.banana.hypermodes`;
- radius is 500 metres;
- transition type is enter + exit (`3`);
- confidence is `3`, matching Security Center;
- the registered callback component belongs to HyperModes.

On every mode-config update and service reconnect, reconcile desired fences against Polaris’s current HyperModes namespace:

- add missing fences;
- update changed coordinates/radius;
- delete stale HyperModes fences;
- never mutate Security Center or another package’s fences.

Release or empty configuration removes only HyperModes-managed fences and unbinds cleanly.

### Callback Receiver and Event Authentication

Declare a manifest receiver for explicit cross-package Polaris callbacks:

- exported because Polaris is another package;
- no public intent filter;
- minimal work in `onReceive`.

Before accepting an event:

1. Require `getSentFromUid()` to resolve to the Polaris package. Reject unknown sender UID/package.
2. Accept only transition event `11` or `12`.
3. Require the complete fence ID to match the current HyperModes registry.
4. When Binder is available, query Polaris and verify the fence ID, coordinates, and radius.
5. Forward a validated internal event through a HyperModes receiver guarded by `com.banana.hypermodes.permission.CONTROL`.

The receiver cannot itself require the HyperModes signature permission because Polaris does not hold it. Sender verification, unguessable IDs, exact registry matching, and Binder cross-checking form the trust boundary. Invalid events are logged and ignored.

The callback may briefly start the HyperModes app process for event validation and forwarding, but it does not create a persistent background process. Trigger evaluation and mode actions remain in `system_server`.

### Continuous State Semantics

Map events as follows:

| Trigger | Enter (11) | Exit (12) |
|---|---|---|
| ARRIVE | active | inactive |
| LEAVE | inactive | active |

Feed the resulting state into `ComplexTriggerManager` with a tag unique to the trigger UUID. A mode stays active while any complex trigger or schedule remains active, preserving current OR semantics.

On engine or system-server restart, location state starts as unknown, not inside or outside. Configuration load alone must not activate or deactivate a mode. A validated Polaris transition establishes the first known state. This avoids treating an unknown startup state as “leave”.

If an imported config contains location triggers on an unsupported device:

- preserve the serialized triggers;
- show them as unsupported in the UI;
- do not register or evaluate them;
- do not silently remove them.

## Part 6: Protocol and Data Flow

Extend `Protocol` with narrowly scoped, signature-protected internal actions/extras for:

- querying location capability with a `ResultReceiver`;
- forwarding a validated Polaris transition from the manifest receiver to `system_server`.

The UI never calls Polaris directly. The only direct Xiaomi Activity integration is the SecurityAdd picker. The system-server bridge owns Polaris support detection and all fence operations.

End-to-end flow:

1. Mode detail requests capability from `system_server`.
2. User chooses ARRIVE or LEAVE and completes SecurityAdd selection.
3. UI validates and converts the picker result, creates a UUID-backed location trigger, and saves the mode config.
4. `RoutineCoreEngine` observes the config change.
5. `ComplexTriggerManager` passes location configs to a new `LocationTriggerManager`.
6. `LocationTriggerManager` reconciles desired fences through `PolarisGeofenceAdapter`.
7. Polaris sends an explicit callback to the manifest receiver.
8. Receiver authenticates and forwards the transition through the signature-protected bridge.
9. `LocationTriggerManager` updates the trigger UUID’s continuous state.
10. `ComplexTriggerManager` activates or deactivates the mode using existing OR logic.

## Error Handling and Diagnostics

- Blur initialization failure: use the local translucent capsule fallback.
- SecurityAdd missing or private: capability disabled; no fallback picker.
- Picker cancellation: no state change.
- Malformed picker result: localized error; no partial trigger.
- Polaris service absent, permission denied, Binder dead, or interface changed: location triggers become unsupported/pending while other triggers continue.
- Service reconnect: reconcile before accepting queried fence state.
- Invalid callback sender, transition, ID, or fence geometry: ignore and log.
- Silent/airplane API failure: report the failed action and continue other mode actions.
- Airplane safety guard: skip only the airplane action and record the guard reason.
- Unsupported location trigger imported from another device: preserve and display it without evaluating it.

Logs must identify subsystem and mode/trigger IDs but must not log precise coordinates or address names in normal builds.

## Testing

### Unit Tests

Extend existing config/model tests for:

- old JSON without new device/location fields;
- symmetric UI/config mappings;
- location trigger UUID and target round trips;
- unsupported-device preservation;
- coordinate validation and exactly-once conversion;
- imported positive non-500 radius preservation.

Add device-controller/adapter tests using injectable platform facades for:

- silent unchanged/on/off;
- restoration from normal, silent, and DND;
- airplane preferred API and Settings/broadcast fallback;
- ECM, SCBM, satellite, user, and enterprise guards;
- original-state capture only once;
- airplane/wireless conflict validation.

Add location tests for:

- each Polaris capability failure;
- add/update/delete/reconnect reconciliation;
- no cross-package fence deletion;
- transition 11/12 mapping;
- continuous ARRIVE/LEAVE state;
- unknown startup state;
- configuration load causing no activation;
- duplicate/out-of-order event idempotence;
- forged sender, unknown ID, invalid transition, and geometry mismatch rejection;
- OR interaction with Wi-Fi, Bluetooth, app, music, and schedule triggers.

Add Compose/UI tests for:

- disabled location trigger reasons;
- picker cancellation/malformed result handling;
- airplane conflict presentation and blocked persistence;
- selected tab semantics and minimum touch target;
- bottom overlay occupying only capsule bounds.

### Manual Verification

On the target HyperOS device, verify:

- light/dark themes;
- gesture and three-button navigation;
- portrait, landscape, and short-height layouts;
- content visibly moving behind and locally blurring under the capsule;
- no full-width bottom background strip;
- last cards remain reachable;
- FAB is lower and never overlaps the capsule;
- rapid scrolling and pager switching remain smooth;
- blur fallback remains local if the effect is disabled;
- silent and airplane apply and restore exact prior state;
- airplane guards prevent unsafe toggles;
- invalid wireless/airplane combinations cannot be saved;
- SecurityAdd returns a usable address and coordinate;
- entering and leaving a 500-metre fence updates mode state continuously;
- Polaris/system-server restart reconciles fences;
- unsupported official components produce a clear disabled state;
- existing time, app, Wi-Fi, Bluetooth, music, driving, and bedtime behavior is unchanged.

Run at minimum:

```text
./gradlew :app:testDebugUnitTest
./gradlew :app:assembleDebug
```

## Out of Scope

- Hooking Security Center’s private activities, services, database, or task model.
- Third-party maps, Places SDKs, or Fused Location geofencing fallback.
- Configurable geofence radius.
- One-shot or duration-based location triggers.
- A complete condition/action editor in the Automations screen.
- Fine-grained silent media, assistant, alarm, or vibration controls.
- Allowing airplane-on and wireless-on overrides in the same mode.
- Applying private MIUI View blur through an `AndroidView` bridge by default.
- Refactoring unrelated existing trigger managers or implementing the current driving-motion TODO.

## Acceptance Criteria

The feature is complete when:

1. The tab capsule is the only bottom navigation surface, content moves behind it, and the capsule locally blurs that content or uses its local translucent fallback.
2. The full-width bottom background strip is absent in all supported navigation modes.
3. The FAB is positioned from shared inset geometry and is visibly lower than the current implementation without overlap.
4. Silent and airplane fields round-trip through persisted mode configuration, apply in `system_server`, and restore exact original state.
5. Invalid airplane/wireless combinations cannot be persisted.
6. SecurityAdd selection and Polaris geofencing work on a supported target device without touching Security Center’s private task storage.
7. ARRIVE and LEAVE are continuous trigger states integrated into existing OR semantics.
8. Unsupported official location components preserve existing location trigger data while disabling evaluation with a clear UI reason.
9. Forged or malformed location callbacks cannot alter a mode.
10. Existing automated tests plus the new tests pass, the debug APK assembles, and manual HyperOS verification covers the listed visual and system behaviors.
