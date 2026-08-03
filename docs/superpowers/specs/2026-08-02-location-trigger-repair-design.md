# Location Trigger Repair Design

## Problem

Location triggers are persisted and routed into `LocationTriggerManager`, while other complex triggers work. The failure is isolated to the Polaris integration.

The current implementation does not match the device's Polaris contract:

1. The local `IMiGeoManagerService` AIDL omits methods from the remote interface. AIDL transaction IDs are positional, so calls after the first omitted method target the wrong remote operations.
2. The adapter derives the client package and callback component from the system-server `Context`. That context belongs to `android`, not HyperModes.
3. The manifest receiver requires HyperModes' signature permission. Polaris cannot hold this permission, so its callback cannot be delivered.
4. The receiver reads `fence_id` and `event_type`, but Polaris sends `context-data` and `transition-event`.
5. The receiver runs in the HyperModes app process, while the registered adapter lives in `system_server`. A process-local singleton cannot forward events across that boundary.
6. A newly registered ARRIVE trigger may already contain the device. Waiting only for a future ENTER event leaves the trigger inactive indefinitely.

The relevant contracts were confirmed from the unpacked device applications:

- `apk_decompiled/polaris_decompiler/sources/com/xiaomi/gnss/polaris/geofence/IMiGeoManagerService.java`
- `apk_decompiled/polaris_decompiler/sources/com/xiaomi/gnss/polaris/geofence/b.java`
- `apk_decompiled/polaris_decompiler/sources/com/xiaomi/gnss/polaris/geofence/g.java`
- `apk_decompiled/securitycenter_decompiler/sources/com/miui/autotask/common/PolarisGeofenceReceiver.java`

## Required Behavior

When a user creates an ARRIVE trigger for their current location, the mode activates as soon as Polaris registers the fence and reports that the device is inside it. The user must not need to leave and re-enter the area.

Subsequent transitions remain continuous:

| Trigger | Inside / ENTER | Outside / EXIT |
|---|---:|---:|
| ARRIVE | active | inactive |
| LEAVE | inactive | active |

An unknown Polaris status does not activate or deactivate the trigger. It remains unknown until Polaris reports a known status or transition.

## Architecture

### Polaris Client Identity

The Polaris adapter remains in `system_server`, alongside `RoutineCoreEngine` and the other trigger managers. Binder calls use the real client package `android`, because Polaris verifies that the supplied package belongs to `Binder.getCallingUid()`.

The adapter must not claim to be `com.banana.hypermodes` while calling from the system UID. Polaris rejects mismatched package names with `SecurityException("packageName error")`.

The callback component is independent of the Binder client package. The adapter registers:

```text
client package: android
callback component: com.banana.hypermodes/.systemserver.geofence.PolarisCallbackReceiver
```

Polaris stores this component against the `android` client namespace and sends an explicit broadcast to it.

### Exact Binder Contract

The local AIDL mirrors the complete remote method order:

1. `getVendorVersion()`
2. `addGeofenceWithFlag(packageName, geofence, flag)`
3. `addGeofence(packageName, geofence)`
4. `deleteGeofence(packageName, geofence)`
5. `deleteGeofenceById(packageName, geofenceId)`
6. `updateGeofence(packageName, geofence)`
7. `listGeofence(packageName)`
8. `findGeofenceById(packageName, geofenceId)`
9. `registerComponent(packageName, component)`
10. `getComponent(packageName)`
11. `sendDebugEvent(packageName, location, event, geofence)`
12. `getAllGeofenceStatus(packageName)`
13. `getGeofenceStatus(packageName, geofenceId)`

`IPolarisService` retains its confirmed order:

1. `getPolarisVersion()`
2. `getGeoManagerService()`

There is no separate remote `unregisterComponent` transaction. Unregistration calls `registerComponent(clientPackage, null)`.

### Fence Ownership and Reconciliation

HyperModes fence IDs retain a dedicated `hypermodes_` prefix and are indexed by full fence ID in memory. Every fence is created with:

- the persisted trigger-derived ID;
- the configured latitude, longitude, and positive radius;
- transition type `3` (enter and exit);
- confidence `3`;
- package name `android`, matching the Binder client namespace.

On config update and Binder reconnect, the adapter calls `listGeofence("android")` and reconciles only IDs beginning with `hypermodes_`:

- add desired fences that are missing;
- update fences whose geometry or transition settings changed;
- delete stale HyperModes fences;
- leave every non-HyperModes fence untouched.

A fence is added to the local registry only after the remote operation succeeds and returns a non-blank ID. Failed operations remain pending for the next reconnect or config reconciliation.

### Callback Delivery and Authentication

Polaris sends an explicit component broadcast with no action requirement and these extras:

- `context-data`: complete geofence ID;
- `transition-event`: `11` for ENTER or `12` for EXIT;
- `transition-location`: optional `Location` diagnostic payload.

The manifest receiver is exported because Polaris is another package. It has no public intent filter and does not require HyperModes' signature permission, because Polaris does not possess that permission.

The receiver accepts a callback only when all of these checks pass:

1. `BroadcastReceiver.getSentFromUid()` is available and resolves to `com.xiaomi.gnss.polaris`.
2. The event is exactly `11` or `12`.
3. The fence ID is non-blank and starts with `hypermodes_`.
4. The event is forwarded through the internal HyperModes action to the exported system-server receiver guarded by `com.banana.hypermodes.permission.CONTROL`.
5. The system-server adapter finds the complete ID in its current in-memory registry.
6. While Binder is live, `findGeofenceById("android", fenceId)` confirms the fence belongs to the current namespace and matches the registered geometry.

The manifest receiver never accesses a process-local adapter singleton. The internal broadcast is the explicit process boundary.

The HyperModes app can send the protected internal broadcast because it owns and requests its signature permission. External applications cannot send it. The system-server receiver still treats the payload as untrusted and validates it against the live registry and Polaris.

### Initial State

After each successful add, update, or reconnect reconciliation, the adapter calls `getGeofenceStatus("android", fenceId)`:

- `1` (`GEO_STATUS_IN`) produces the same internal state update as ENTER;
- `2` (`GEO_STATUS_OUT`) produces the same internal state update as EXIT;
- `0` (`GEO_STATUS_UNKNOWN`) leaves state unknown;
- `-1` or an exception marks the status query unavailable and leaves state unknown.

This query runs only after remote registration succeeds. It does not infer state from the picker coordinates or an app-side last-known location.

`LocationTriggerManager` remains the sole owner of ARRIVE/LEAVE activation semantics and de-duplicates a queried status followed by an identical callback.

### Lifecycle

On Binder death or service disconnect, the adapter clears remote handles but preserves desired trigger configuration. It reconnects using the existing bounded reconnect mechanism, re-registers the callback component, reconciles remote fences, then queries initial status.

Removing all location triggers removes only HyperModes-prefixed fences. Engine shutdown unregisters the component with `registerComponent("android", null)` and unbinds the service.

## Error Handling

- Binding failure, permission rejection, null child service, or ABI mismatch keeps location triggers inactive and records a typed diagnostic; other trigger managers continue operating.
- A malformed or unauthenticated callback is ignored.
- A remote add returning null or blank is treated as failure and is not recorded locally.
- Status-query failure leaves state unknown rather than assuming outside, which avoids incorrectly activating LEAVE.
- Logs include mode ID, trigger ID, operation, and error category but omit precise coordinates and address text in normal builds.

## Testing

Tests are written before production changes and must fail for the current implementation.

### JVM Tests

1. The complete AIDL source exposes the confirmed 13-method order and no `unregisterComponent` transaction.
2. Callback parsing accepts `context-data` plus events `11`/`12` and rejects old extra names, invalid events, blank IDs, and non-HyperModes IDs.
3. Internal forwarding targets the protected system-server action rather than a process-local singleton.
4. `GEO_STATUS_IN` immediately activates ARRIVE and deactivates LEAVE.
5. `GEO_STATUS_OUT` deactivates ARRIVE and activates LEAVE.
6. Unknown status produces no activation callback.
7. A queried status followed by the same transition does not emit duplicate state changes.
8. Reconciliation adds, updates, and deletes only HyperModes-prefixed fences and records only successful remote operations.
9. Binder client package is `android`, while callback component package is `com.banana.hypermodes`.

### Build and Device Verification

1. Run focused location-trigger and protocol tests.
2. Run the full JVM test suite.
3. Assemble the debug APK and verify generated AIDL transaction constants against the unpacked Polaris contract.
4. Install the APK, reboot or restart the framework so LSPosed reloads module code, and create an ARRIVE trigger at the current location.
5. Verify Polaris lists the HyperModes fence under the `android` namespace and stores the HyperModes receiver component.
6. Verify the initial status is `IN` and the mode activates without leaving the area.
7. Move outside and back inside, verifying one deactivation and one activation.
8. Reboot and verify reconciliation restores the same behavior without touching other Polaris fences.

## Non-Goals

- Moving the mode engine or Polaris client into a persistent HyperModes app process.
- Spoofing Security Center's package or UID.
- Hooking or modifying Polaris internals.
- Writing Polaris databases directly.
- Inferring initial state from app-side location APIs.
- Changing shared behavior for Wi-Fi, Bluetooth, app, music, or time triggers.
