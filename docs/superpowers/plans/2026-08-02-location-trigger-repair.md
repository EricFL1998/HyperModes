# Location Trigger Repair Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Make Xiaomi Polaris location triggers register correctly, cross the app/system-server process boundary safely, and activate an ARRIVE mode immediately when Polaris reports the device is already inside the selected area.

**Architecture:** `RoutineCoreEngine` and the Polaris Binder client remain in `system_server`; Binder operations use the truthful `android` package namespace, while Polaris sends explicit callbacks to the HyperModes manifest receiver. That receiver authenticates and forwards a narrow internal event to a signature-permission-protected dynamic receiver in `system_server`, where the live adapter verifies the fence and feeds one deduplicating location state machine. Registration and reconnect reconciliation query Polaris status so an already-inside ARRIVE trigger activates without requiring a new ENTER transition.

**Tech Stack:** Kotlin, Java 11, Android SDK 35–37, AIDL/Binder, libxposed system-server hooks, JUnit 4, Robolectric, Mockito, Gradle.

## Global Constraints

- Preserve unrelated working-tree changes; do not reset or overwrite the existing LSPosed metadata, diagnostic logging, or other user edits.
- Do not commit unless the user explicitly asks; each task ends at a verified review checkpoint instead.
- Use the real Polaris Binder client package `android`; never claim the Binder caller is `com.banana.hypermodes` from `system_server`.
- Register the callback component as `com.banana.hypermodes/.systemserver.geofence.PolarisCallbackReceiver`.
- Mirror the complete device AIDL method order exactly; transaction positions are part of the ABI.
- Only add, update, or delete fence IDs beginning with `hypermodes_`; never mutate other Polaris fences.
- Treat Polaris status `0`, `-1`, null results, and exceptions as unknown; never assume outside.
- A successful status `IN(1)` must immediately establish inside state; an identical later ENTER callback must be deduplicated.
- Keep normal logs free of precise coordinates and address names.
- Polaris enforces an eight-fence cap for the `android` namespace; capacity failure preserves desired configuration and does not affect other trigger managers.
- Reconnect attempts use bounded delays of 1 second, 5 seconds, and 15 seconds; release cancels pending retries.
- The picker returns GCJ-02 in China. Convert once at the picker boundary using the approved offset subtraction; do not convert on config load, import, reconciliation, or callback.
- Existing persisted location coordinates are preserved; this repair does not silently rewrite them.
- Remove `com.xiaomi.gnss.polaris` from Xposed scope unless a concrete hook is added; this Binder/broadcast design does not inject into Polaris.

---

## File Structure

### Create

- `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceContracts.kt` — one source of truth for client/callback identity, event/status constants, fence snapshots, and callback payload validation.
- `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisFenceReconciler.kt` — pure desired-vs-remote reconciliation planner that ignores non-HyperModes fences.
- `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeoService.kt` — testable façade over generated `IMiGeoManagerService`.
- `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerStateMachine.kt` — pure unknown/inside/outside state and ARRIVE/LEAVE activation/deduplication.
- `app/src/main/java/com/banana/hypermodes/ui/XiaomiLocationCoordinates.kt` — pure validated GCJ-02 picker-to-Polaris coordinate conversion.
- `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisAidlContractTest.kt`
- `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceContractsTest.kt`
- `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisFenceReconcilerTest.kt`
- `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisCallbackReceiverTest.kt`
- `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceAdapterTest.kt`
- `app/src/test/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerStateMachineTest.kt`
- `app/src/test/java/com/banana/hypermodes/ui/XiaomiLocationCoordinatesTest.kt`

### Modify

- `app/src/main/aidl/com/xiaomi/gnss/polaris/geofence/IMiGeoManagerService.aidl` — complete 13-method ABI.
- `app/src/main/aidl/com/xiaomi/gnss/polaris/IPolarisService.aidl` — retain and test confirmed two-method order.
- `app/src/main/java/com/xiaomi/gnss/polaris/geofence/MiGeofence.java` — add confirmed status constants without changing parcel order.
- `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt` — add the internal geofence event action and extras.
- `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisCallbackReceiver.kt` — authenticate actual Polaris extras and forward across processes; delete `PolarisCallbackBridge`.
- `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceAdapter.kt` — truthful identity, complete remote reconciliation, callback verification, status query, and bounded reconnect.
- `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerManager.kt` — use one state machine for queried and callback state, handle per-trigger removal, expose verified event entry point, release adapter.
- `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ComplexTriggerManager.kt` — delegate forwarded events and release location resources.
- `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt` — narrow internal geofence forwarding entry point.
- `app/src/main/java/com/banana/hypermodes/hook/SystemModeHook.kt` — protected dynamic system-server receiver.
- `app/src/main/java/com/banana/hypermodes/ui/LocationTriggerPickerScreen.kt` — use the tested picker coordinate converter.
- `app/src/main/AndroidManifest.xml` — exported, permissionless, filterless explicit Polaris receiver.
- `app/src/main/res/values/arrays.xml` — remove unnecessary Polaris Xposed injection scope.
- `app/src/test/java/com/banana/hypermodes/protocol/ProtocolTest.kt` — internal action/extra contract assertions.
- `app/src/test/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerTest.kt` — retain DTO coverage; behavior moves to the state-machine test.

---

### Task 1: Lock the Polaris Binder ABI

**Files:**
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisAidlContractTest.kt`
- Modify: `app/src/main/aidl/com/xiaomi/gnss/polaris/geofence/IMiGeoManagerService.aidl`
- Verify: `app/src/main/aidl/com/xiaomi/gnss/polaris/IPolarisService.aidl`
- Modify: `app/src/main/java/com/xiaomi/gnss/polaris/geofence/MiGeofence.java`

**Interfaces:**
- Produces generated `IMiGeoManagerService` methods with transaction IDs 1–13.
- Produces `MiGeofence.GEO_STATUS_UNKNOWN`, `GEO_STATUS_IN`, and `GEO_STATUS_OUT`.
- Preserves `MiGeofence` parcel field order: ID, latitude, longitude, radius, transition, confidence, package.

- [ ] **Step 1: Write a failing AIDL source-contract test**

Create a test that reads the AIDL from the repository root and extracts method names in declaration order:

```kotlin
package com.banana.hypermodes.systemserver.geofence

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.io.File

class PolarisAidlContractTest {
    private val projectRoot = File(System.getProperty("user.dir"))
        .let { if (it.name == "app") it.parentFile else it }

    @Test
    fun `geo manager methods preserve the device transaction order`() {
        val source = File(
            projectRoot,
            "app/src/main/aidl/com/xiaomi/gnss/polaris/geofence/IMiGeoManagerService.aidl"
        ).readText()
        val methodNames = Regex("(?:String|void|int|Bundle|ComponentName|MiGeofence|List<MiGeofence>)\\s+(\\w+)\\s*\\(")
            .findAll(source)
            .map { it.groupValues[1] }
            .toList()

        assertEquals(
            listOf(
                "getVendorVersion",
                "addGeofenceWithFlag",
                "addGeofence",
                "deleteGeofence",
                "deleteGeofenceById",
                "updateGeofence",
                "listGeofence",
                "findGeofenceById",
                "registerComponent",
                "getComponent",
                "sendDebugEvent",
                "getAllGeofenceStatus",
                "getGeofenceStatus"
            ),
            methodNames
        )
        assertFalse(source.contains("unregisterComponent"))
    }
}
```

- [ ] **Step 2: Run the test and verify the expected ABI failure**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisAidlContractTest"
```

Expected: FAIL because the current AIDL contains only six methods and includes `unregisterComponent`.

- [ ] **Step 3: Replace the AIDL with the exact remote declaration order**

Use `inout` for add/update methods because the remote proxy reads the mutated parcelable back:

```aidl
package com.xiaomi.gnss.polaris.geofence;

import android.content.ComponentName;
import android.location.Location;
import android.os.Bundle;
import com.xiaomi.gnss.polaris.geofence.MiGeofence;

interface IMiGeoManagerService {
    String getVendorVersion();
    String addGeofenceWithFlag(String packageName, inout MiGeofence geofence, int flag);
    String addGeofence(String packageName, inout MiGeofence geofence);
    void deleteGeofence(String packageName, in MiGeofence geofence);
    void deleteGeofenceById(String packageName, String geofenceId);
    void updateGeofence(String packageName, inout MiGeofence geofence);
    List<MiGeofence> listGeofence(String packageName);
    MiGeofence findGeofenceById(String packageName, String geofenceId);
    void registerComponent(String packageName, in ComponentName component);
    ComponentName getComponent(String packageName);
    void sendDebugEvent(String packageName, in Location location, int event, in MiGeofence geofence);
    Bundle getAllGeofenceStatus(String packageName);
    int getGeofenceStatus(String packageName, String geofenceId);
}
```

Keep `IPolarisService.aidl` in this order:

```aidl
String getPolarisVersion();
IMiGeoManagerService getGeoManagerService();
```

Add only these constants to `MiGeofence.java`:

```java
public static final int GEO_STATUS_UNKNOWN = 0;
public static final int GEO_STATUS_IN = 1;
public static final int GEO_STATUS_OUT = 2;
```

- [ ] **Step 4: Verify the source test and generated AIDL contract**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisAidlContractTest" :app:compileDebugAidl
```

Expected: PASS. Inspect the generated file under `app/build/generated/aidl_source_output_dir/debug/out/.../IMiGeoManagerService.java` and confirm `TRANSACTION_registerComponent = 9` and `TRANSACTION_getGeofenceStatus = 13`.

- [ ] **Step 5: Review checkpoint**

Confirm the diff changes only the contract test, the AIDL ABI, and the three status constants. Do not commit.

---

### Task 2: Define Pure Polaris Contracts and Location State Semantics

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceContracts.kt`
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceContractsTest.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerStateMachine.kt`
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerStateMachineTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerManager.kt`

**Interfaces:**
- Produces `PolarisContract.CLIENT_PACKAGE`, callback identity, real extra names, events, statuses, and `parseCallback`.
- Produces `PolarisFenceSpec` and `PolarisRemoteFence` value types for reconciliation and verification.
- Produces `LocationTriggerStateMachine.applyState(modeId, triggerId, transition, inside)` and `remove(modeId, triggerId)`.

- [ ] **Step 1: Write failing contract and state-machine tests**

Cover the actual callback values and initial-state behavior:

```kotlin
@Test
fun `actual Polaris payload is accepted`() {
    assertEquals(
        PolarisCallback("hypermodes_abc", 11),
        PolarisContract.parseCallback("hypermodes_abc", 11)
    )
}

@Test
fun `legacy extras cannot form a valid payload`() {
    assertNull(PolarisContract.parseCallback(null, -1))
    assertNull(PolarisContract.parseCallback("other_abc", 11))
    assertNull(PolarisContract.parseCallback("hypermodes_abc", 20))
}

@Test
fun `inside status immediately activates ARRIVE once`() {
    val changes = mutableListOf<Boolean>()
    val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

    machine.applyState("mode", "trigger", "ARRIVE", true)
    machine.applyState("mode", "trigger", "ARRIVE", true)

    assertEquals(listOf(true), changes)
}

@Test
fun `unknown status emits no state`() {
    val changes = mutableListOf<Boolean>()
    val machine = LocationTriggerStateMachine { _, _, active -> changes += active }

    machine.applyStatus("mode", "trigger", "LEAVE", PolarisContract.STATUS_UNKNOWN)

    assertTrue(changes.isEmpty())
}
```

Also test OUT/LEAVE, status followed by matching transition, invalid transition text, and trigger removal emitting `false` exactly once.

- [ ] **Step 2: Run tests and verify missing-type failures**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisGeofenceContractsTest" --tests "com.banana.hypermodes.systemserver.trigger.LocationTriggerStateMachineTest"
```

Expected: compilation FAIL because the new types do not exist.

- [ ] **Step 3: Implement the minimal pure contracts**

Use one constants object and immutable values:

```kotlin
internal object PolarisContract {
    const val SERVICE_PACKAGE = "com.xiaomi.gnss.polaris"
    const val SERVICE_CLASS = "com.xiaomi.gnss.polaris.PolarisService"
    const val CLIENT_PACKAGE = "android"
    const val CALLBACK_PACKAGE = "com.banana.hypermodes"
    const val FENCE_PREFIX = "hypermodes_"
    const val EXTRA_FENCE_ID = "context-data"
    const val EXTRA_EVENT = "transition-event"
    const val EVENT_ENTER = 11
    const val EVENT_EXIT = 12
    const val STATUS_UNKNOWN = 0
    const val STATUS_IN = 1
    const val STATUS_OUT = 2
    const val TRANSITION_BOTH = 3
    const val CONFIDENCE_HIGH = 3

    fun parseCallback(fenceId: String?, event: Int): PolarisCallback? {
        val id = fenceId?.takeIf { it.isNotBlank() && it.startsWith(FENCE_PREFIX) } ?: return null
        if (event != EVENT_ENTER && event != EVENT_EXIT) return null
        return PolarisCallback(id, event)
    }

    fun senderIsPolaris(sentFromPackage: String?, packagesForUid: Array<String>?): Boolean =
        sentFromPackage == SERVICE_PACKAGE && packagesForUid?.contains(SERVICE_PACKAGE) == true
}

internal data class PolarisCallback(val fenceId: String, val event: Int)

internal data class PolarisFenceSpec(
    val fenceId: String,
    val modeId: String,
    val triggerId: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val transitionType: Int = PolarisContract.TRANSITION_BOTH,
    val confidence: Int = PolarisContract.CONFIDENCE_HIGH
)

internal data class PolarisRemoteFence(
    val fenceId: String,
    val latitude: Double,
    val longitude: Double,
    val radiusMeters: Int,
    val transitionType: Int,
    val confidence: Int,
    val packageName: String?
)
```

- [ ] **Step 4: Implement one deduplicating state machine**

```kotlin
internal class LocationTriggerStateMachine(
    private val callback: (String, String, Boolean) -> Unit
) {
    private val states = mutableMapOf<String, Boolean>()

    fun applyStatus(modeId: String, triggerId: String, transition: String, status: Int) {
        when (status) {
            PolarisContract.STATUS_IN -> applyState(modeId, triggerId, transition, true)
            PolarisContract.STATUS_OUT -> applyState(modeId, triggerId, transition, false)
        }
    }

    fun applyState(modeId: String, triggerId: String, transition: String, inside: Boolean) {
        val key = "$modeId:$triggerId"
        if (states[key] == inside) return
        states[key] = inside
        val active = when (transition) {
            "ARRIVE" -> inside
            "LEAVE" -> !inside
            else -> return
        }
        callback(modeId, "location:$triggerId", active)
    }

    fun remove(modeId: String, triggerId: String) {
        val key = "$modeId:$triggerId"
        if (states.remove(key) != null) {
            callback(modeId, "location:$triggerId", false)
        }
    }
}
```

Adjust `LocationTriggerManager` so both queried state and events feed this class. Diff old and new **trigger IDs**, not only mode IDs, during `updateConfigs`; removing one trigger from a retained mode must clear its unique active tag.

- [ ] **Step 5: Run focused tests**

Run the Task 2 test command again.

Expected: PASS, including queried-IN then ENTER deduplication.

- [ ] **Step 6: Review checkpoint**

Confirm no Android service binding or broadcast code entered the pure state machine. Do not commit.

---

### Task 3: Repair and Authenticate the Cross-Process Callback

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisCallbackReceiver.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisCallbackReceiverTest.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/protocol/ProtocolTest.kt`

**Interfaces:**
- Produces `Protocol.ACTION_POLARIS_GEOFENCE_EVENT`.
- Produces `Protocol.EXTRA_POLARIS_FENCE_ID` and `Protocol.EXTRA_POLARIS_EVENT`.
- The manifest receiver accepts only explicit Polaris broadcasts, then emits a package-scoped internal broadcast toward `android`.

- [ ] **Step 1: Write failing protocol and Robolectric receiver tests**

Test these behaviors:

```kotlin
@Test
fun `valid Polaris callback forwards the protected internal payload`() {
    val incoming = Intent()
        .putExtra(PolarisContract.EXTRA_FENCE_ID, "hypermodes_abc")
        .putExtra(PolarisContract.EXTRA_EVENT, PolarisContract.EVENT_ENTER)

    receiver.forwardValidated(context, incoming, PolarisContract.SERVICE_PACKAGE, arrayOf(PolarisContract.SERVICE_PACKAGE))

    val forwarded = shadowOf(context).broadcastIntents.single()
    assertEquals(Protocol.ACTION_POLARIS_GEOFENCE_EVENT, forwarded.action)
    assertEquals(Protocol.FRAMEWORK_PACKAGE, forwarded.`package`)
    assertEquals("hypermodes_abc", forwarded.getStringExtra(Protocol.EXTRA_POLARIS_FENCE_ID))
    assertEquals(11, forwarded.getIntExtra(Protocol.EXTRA_POLARIS_EVENT, -1))
}
```

Use an internal/package-visible helper for deterministic testing; `onReceive` supplies `sentFromPackage`, `sentFromUid`, and package resolution. Add rejection tests for a non-Polaris sender, invalid event, legacy key names, blank ID, and non-HyperModes ID.

Add a narrow manifest source test asserting the receiver block has `android:exported="true"` but no `android:permission` and no `<intent-filter>`.

- [ ] **Step 2: Run tests and verify the expected failures**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisCallbackReceiverTest" --tests "com.banana.hypermodes.protocol.ProtocolTest"
```

Expected: FAIL because the internal protocol constants/helper do not exist and the manifest still blocks Polaris.

- [ ] **Step 3: Add the internal protocol constants**

```kotlin
const val ACTION_POLARIS_GEOFENCE_EVENT =
    "com.banana.hypermodes.POLARIS_GEOFENCE_EVENT"
const val EXTRA_POLARIS_FENCE_ID = "polarisFenceId"
const val EXTRA_POLARIS_EVENT = "polarisEvent"
```

- [ ] **Step 4: Replace process-local forwarding with an authenticated internal broadcast**

Delete `PolarisCallbackBridge`. `onReceive` must call `sentFromPackage` and `sentFromUid`, resolve `packageManager.getPackagesForUid(sentFromUid)`, and reject unless both identify Polaris. The forwarding helper sends:

```kotlin
context.sendBroadcast(
    Intent(Protocol.ACTION_POLARIS_GEOFENCE_EVENT)
        .setPackage(Protocol.FRAMEWORK_PACKAGE)
        .putExtra(Protocol.EXTRA_POLARIS_FENCE_ID, callback.fenceId)
        .putExtra(Protocol.EXTRA_POLARIS_EVENT, callback.event)
)
```

Do **not** pass `Protocol.PERMISSION_CONTROL` as `sendBroadcast`'s receiver-permission argument; the system-server receiver registration in Task 4 enforces that permission on the sender.

- [ ] **Step 5: Correct the manifest receiver declaration**

```xml
<receiver
    android:name=".systemserver.geofence.PolarisCallbackReceiver"
    android:exported="true" />
```

There is no action filter because Polaris sends an actionless explicit component broadcast. There is no receiver permission because Polaris does not hold HyperModes' signature permission.

- [ ] **Step 6: Run focused tests and merge-manifest verification**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisCallbackReceiverTest" --tests "com.banana.hypermodes.protocol.ProtocolTest" :app:processDebugMainManifest
```

Expected: PASS. Confirm the merged receiver is exported, filterless, and permissionless.

- [ ] **Step 7: Review checkpoint**

Search for `PolarisCallbackBridge`, `fence_id`, `event_type`, and `calling_package`; no production references should remain. Do not commit.

---

### Task 4: Install the Protected System-Server Event Route

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/SystemModeHook.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ComplexTriggerManager.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerManager.kt`
- Create or extend: `app/src/test/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerTest.kt`

**Interfaces:**
- Produces `RoutineCoreEngine.handlePolarisGeofenceEvent(fenceId: String, event: Int)`.
- Produces `ComplexTriggerManager.handlePolarisGeofenceEvent(fenceId: String, event: Int)`.
- Produces `LocationTriggerManager.handlePolarisGeofenceEvent(fenceId: String, event: Int)`.
- The adapter remains the owner of live-fence verification before invoking state callbacks.

- [ ] **Step 1: Write a failing routing test around a narrow handler**

Extract the intent-to-engine handoff into an internal helper so it is testable without constructing `SystemModeHook`:

```kotlin
internal class PolarisSystemEventHandler(
    private val dispatch: (String, Int) -> Unit
) {
    fun handle(fenceId: String?, event: Int) {
        val callback = PolarisContract.parseCallback(fenceId, event) ?: return
        dispatch(callback.fenceId, callback.event)
    }
}
```

Test valid delivery once and invalid payload rejection.

- [ ] **Step 2: Run the route test and verify missing-type failure**

Run the focused `LocationTriggerTest` (or a new `PolarisSystemEventHandlerTest` if that keeps responsibilities clearer).

Expected: compilation FAIL because the handler and delegation methods do not exist.

- [ ] **Step 3: Implement the protected dynamic receiver**

In `SystemModeHook.registerBridge`, add `Protocol.ACTION_POLARIS_GEOFENCE_EVENT` to the filter and handle it before package-list extraction:

```kotlin
Protocol.ACTION_POLARIS_GEOFENCE_EVENT -> {
    PolarisSystemEventHandler { fenceId, event ->
        RoutineCoreEngine.getInstance().handlePolarisGeofenceEvent(fenceId, event)
    }.handle(
        intent.getStringExtra(Protocol.EXTRA_POLARIS_FENCE_ID),
        intent.getIntExtra(Protocol.EXTRA_POLARIS_EVENT, -1)
    )
    return
}
```

Keep the existing registration:

```kotlin
context.registerReceiver(
    receiver,
    filter,
    Protocol.PERMISSION_CONTROL,
    null,
    Context.RECEIVER_EXPORTED
)
```

This registration permission requires the **sender** to hold HyperModes' signature permission. The manifest receiver runs under HyperModes UID and qualifies.

- [ ] **Step 4: Add narrow delegation through the live engine graph**

```kotlin
// RoutineCoreEngine
fun handlePolarisGeofenceEvent(fenceId: String, event: Int) {
    complexTriggerManager?.handlePolarisGeofenceEvent(fenceId, event)
}

// ComplexTriggerManager
fun handlePolarisGeofenceEvent(fenceId: String, event: Int) {
    locationManager.handlePolarisGeofenceEvent(fenceId, event)
}

// LocationTriggerManager
fun handlePolarisGeofenceEvent(fenceId: String, event: Int) {
    polarisAdapter.handleGeofenceEvent(fenceId, event)
}
```

Do not instantiate a second adapter in the receiver path.

- [ ] **Step 5: Run focused routing and protocol tests**

Expected: PASS; invalid events never reach the engine dispatch lambda.

- [ ] **Step 6: Review checkpoint**

Confirm the only app-to-system-server entry is the signature-protected dynamic receiver and that all payloads are parsed again in system_server. Do not commit.

---

### Task 5: Build a Pure Fence Reconciliation Plan

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisFenceReconciler.kt`
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisFenceReconcilerTest.kt`

**Interfaces:**
- Produces `PolarisFenceOperation.Add`, `.Update`, `.Delete`, and `.Keep`.
- Produces `PolarisFenceReconciler.plan(desired, remote)`.
- The plan never returns an operation for a non-`hypermodes_` remote fence.

- [ ] **Step 1: Write failing reconciliation tests**

Cover missing, changed, stale, unchanged, and foreign fences:

```kotlin
@Test
fun `foreign remote fences are never changed`() {
    val operations = PolarisFenceReconciler.plan(
        desired = emptyList(),
        remote = listOf(remoteFence("security_center_1"))
    )
    assertTrue(operations.isEmpty())
}

@Test
fun `changed geometry produces one update`() {
    val desired = spec("hypermodes_a", latitude = 30.1)
    val remote = remoteFence("hypermodes_a", latitude = 30.2)

    assertEquals(listOf(PolarisFenceOperation.Update(desired)),
        PolarisFenceReconciler.plan(listOf(desired), listOf(remote)))
}
```

Also assert deterministic operation order by fence ID to make logs and tests stable.

- [ ] **Step 2: Run the test and verify missing-type failure**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisFenceReconcilerTest"
```

Expected: compilation FAIL.

- [ ] **Step 3: Implement the minimal planner**

```kotlin
internal sealed interface PolarisFenceOperation {
    data class Add(val fence: PolarisFenceSpec) : PolarisFenceOperation
    data class Update(val fence: PolarisFenceSpec) : PolarisFenceOperation
    data class Delete(val fenceId: String) : PolarisFenceOperation
    data class Keep(val fence: PolarisFenceSpec) : PolarisFenceOperation
}

internal object PolarisFenceReconciler {
    fun plan(
        desired: Collection<PolarisFenceSpec>,
        remote: Collection<PolarisRemoteFence>
    ): List<PolarisFenceOperation> {
        val desiredById = desired.associateBy { it.fenceId }
        val managedRemote = remote
            .filter { it.fenceId.startsWith(PolarisContract.FENCE_PREFIX) }
            .associateBy { it.fenceId }

        return buildList {
            (managedRemote.keys - desiredById.keys).sorted().forEach {
                add(PolarisFenceOperation.Delete(it))
            }
            desiredById.toSortedMap().forEach { (id, spec) ->
                val current = managedRemote[id]
                add(when {
                    current == null -> PolarisFenceOperation.Add(spec)
                    !current.matches(spec) -> PolarisFenceOperation.Update(spec)
                    else -> PolarisFenceOperation.Keep(spec)
                })
            }
        }
    }
}
```

Use exact double equality because persisted values pass through without adapter conversion. Compare radius, transition, confidence, and package namespace as well.

- [ ] **Step 4: Run focused tests**

Expected: PASS for every operation and the foreign-fence guard.

- [ ] **Step 5: Review checkpoint**

Confirm this file has no Android Context, Binder, logging, or mutable registry. Do not commit.

---

### Task 6: Rewrite the Adapter Around a Testable Polaris Service

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeoService.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceAdapter.kt`
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceAdapterTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerManager.kt`

**Interfaces:**
- Produces a façade for list/add/update/delete/find/status/register operations.
- Adapter stores desired fences independently of connection state.
- Adapter records a live fence only after successful remote confirmation.
- Adapter verifies callbacks against both local desired/live state and `findGeofenceById`.
- Adapter reports status through the same callback used for transitions.

- [ ] **Step 1: Write failing fake-service adapter tests**

The fake records calls and can return failures. Test:

1. client package is always `android`;
2. callback component belongs to HyperModes;
3. callback registration occurs before reconciliation;
4. add result must equal the desired full ID and be nonblank before local registration;
5. update is followed by `findById` confirmation;
6. stale managed fence is deleted, foreign fence untouched;
7. status IN emits ENTER-equivalent state immediately;
8. unknown/error status emits nothing;
9. forwarded event must pass `findById` geometry verification;
10. failed add/update/delete does not corrupt the live registry;
11. list failure performs no destructive operation;
12. more than eight fences or remote capacity failure preserves desired state.

Use a test constructor/factory that injects `PolarisGeoService` and bypasses real binding. Assert behavior, not private maps.

- [ ] **Step 2: Run the adapter test and verify failures**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisGeofenceAdapterTest"
```

Expected: compilation or assertion FAIL because the façade/reconciliation behavior does not exist.

- [ ] **Step 3: Implement the AIDL façade**

```kotlin
internal interface PolarisGeoService {
    fun registerComponent(component: ComponentName?)
    fun list(): List<PolarisRemoteFence>
    fun add(fence: PolarisFenceSpec): String?
    fun update(fence: PolarisFenceSpec)
    fun deleteById(fenceId: String)
    fun findById(fenceId: String): PolarisRemoteFence?
    fun status(fenceId: String): Int
    fun isAlive(): Boolean
}
```

`AidlPolarisGeoService` wraps `IMiGeoManagerService` and hard-codes `PolarisContract.CLIENT_PACKAGE` for every call. Conversion to `MiGeofence` sets package name `android`, transition `3`, and confidence `3`; conversion from `MiGeofence` preserves all comparison fields.

- [ ] **Step 4: Rework adapter desired/live state and reconciliation**

Keep two maps:

```kotlin
private var desiredById: Map<String, PolarisFenceSpec> = emptyMap()
private val liveById = mutableMapOf<String, PolarisFenceSpec>()
```

`reconcile(triggers)` always replaces `desiredById`; if disconnected, it initiates/awaits binding. If connected, it:

1. calls `list()` once;
2. computes pure operations;
3. executes each operation independently;
4. records live state only after success and remote confirmation;
5. queries known status for every live desired fence.

Treat null list/exception as a complete reconciliation failure and do no deletes.

- [ ] **Step 5: Add verified callback handling**

```kotlin
fun handleGeofenceEvent(fenceId: String, event: Int) {
    val payload = PolarisContract.parseCallback(fenceId, event) ?: return
    val expected = liveById[payload.fenceId] ?: return
    val remote = geoService?.findById(payload.fenceId) ?: return
    if (!remote.matches(expected)) return

    callback(
        expected.modeId,
        expected.triggerId,
        if (event == PolarisContract.EVENT_ENTER) GeofenceEvent.ENTER else GeofenceEvent.EXIT
    )
}
```

Status query maps IN to ENTER and OUT to EXIT through this same callback target. It does not call `handleGeofenceEvent`, because status is not a broadcast and does not need a second remote lookup; the state machine still deduplicates it.

- [ ] **Step 6: Run adapter and reconciler tests**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.geofence.PolarisGeofenceAdapterTest" --tests "com.banana.hypermodes.systemserver.geofence.PolarisFenceReconcilerTest"
```

Expected: PASS.

- [ ] **Step 7: Review checkpoint**

Check logs include operation/fence IDs but not latitude, longitude, or address. Confirm no call uses `context.packageName` as the Polaris namespace or callback package. Do not commit.

---

### Task 7: Finish Binding, Reconnect, and Release Lifecycle

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceAdapter.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerManager.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ComplexTriggerManager.kt`
- Extend: `app/src/test/java/com/banana/hypermodes/systemserver/geofence/PolarisGeofenceAdapterTest.kt`

**Interfaces:**
- Adapter reconnect delays are `[1_000L, 5_000L, 15_000L]`.
- `LocationTriggerManager.release()` releases the adapter.
- `ComplexTriggerManager.release()` clears trigger state, releases app manager, and releases location manager.

- [ ] **Step 1: Write failing lifecycle tests with a fake scheduler/binding controller**

Test disconnect behavior:

```kotlin
@Test
fun `disconnect retains desired fences and schedules bounded retries`() {
    adapter.reconcile(listOf(trigger))
    adapter.onDisconnectedForTest()

    assertEquals(listOf(1_000L), scheduler.delays)
    scheduler.runNextFailure()
    assertEquals(listOf(1_000L, 5_000L), scheduler.delays)
}
```

Also test no fourth retry after 15 seconds, config update can restart an exhausted connection cycle, successful reconnect resets retry index and performs register → reconcile → status, and release cancels retries/unregisters with `registerComponent(null)`/unbinds once.

- [ ] **Step 2: Run lifecycle tests and verify failure**

Expected: FAIL because the adapter currently has no bounded reconnect and calls the nonexistent remote `unregisterComponent`.

- [ ] **Step 3: Implement explicit connection states and bounded retries**

Use an internal enum:

```kotlin
private enum class ConnectionState { DISCONNECTED, BINDING, CONNECTED, RELEASED }
```

On `onServiceConnected`, wrap the child service, link death where possible, set CONNECTED, reset retry index, register the HyperModes callback component under `android`, then reconcile and query status.

On `onServiceDisconnected`, `onBindingDied`, or Binder death, clear handles, transition to DISCONNECTED, unbind defensively, and schedule only the next bounded delay. A config update while DISCONNECTED starts a new connection cycle; RELEASED never reconnects.

- [ ] **Step 4: Implement correct release semantics**

Release must:

1. clear trigger states via manager/config removal;
2. attempt deletion of current HyperModes-managed remote fences only;
3. call `geoService.registerComponent(null)` (which maps to transaction 9 with a null component);
4. cancel pending handler callbacks;
5. unlink death recipient where applicable;
6. unbind once;
7. set state RELEASED and clear desired/live maps.

Add:

```kotlin
fun LocationTriggerManager.release() {
    updateConfigs(emptyMap())
    polarisAdapter.release()
}
```

and invoke `locationManager.release()` from `ComplexTriggerManager.release()`.

- [ ] **Step 5: Run lifecycle and trigger-state tests**

Expected: PASS; deleted trigger IDs deactivate their unique tags even when remote cleanup fails.

- [ ] **Step 6: Review checkpoint**

Confirm all retry callbacks are cancellable and no adapter binds after RELEASED. Do not commit.

---

### Task 8: Correct the Picker Coordinate Boundary

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/ui/XiaomiLocationCoordinates.kt`
- Create: `app/src/test/java/com/banana/hypermodes/ui/XiaomiLocationCoordinatesTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/ui/LocationTriggerPickerScreen.kt`

**Interfaces:**
- Produces `XiaomiLocationCoordinates.toPolaris(latitude, longitude): Pair<Double, Double>`.
- Outside China, coordinates are unchanged.
- Inside China, subtract the GCJ offset once.
- Reject non-finite, out-of-range, zeroed coordinates before persistence.

- [ ] **Step 1: Write failing coordinate tests**

Use representative and boundary vectors:

```kotlin
@Test
fun `outside China is unchanged`() {
    assertEquals(
        51.5074 to -0.1278,
        XiaomiLocationCoordinates.toPolaris(51.5074, -0.1278)
    )
}

@Test
fun `Beijing GCJ coordinate subtracts rather than adds offset`() {
    val converted = XiaomiLocationCoordinates.toPolaris(39.908823, 116.397470)
    assertEquals(39.9074, converted.first, 0.001)
    assertEquals(116.3912, converted.second, 0.001)
    assertTrue(converted.first < 39.908823)
    assertTrue(converted.second < 116.397470)
}
```

Add tests for China bounds, NaN/infinity, out-of-range, and zero coordinates. Validation should return null or a typed result; choose one API and use it consistently in the picker.

- [ ] **Step 2: Run tests and verify missing-type failure**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.ui.XiaomiLocationCoordinatesTest"
```

Expected: compilation FAIL.

- [ ] **Step 3: Implement the tested conversion**

Move the current math into the utility, add China bounds (`lon 72.004..137.8347`, `lat 0.8293..55.8271`), and return:

```kotlin
return (gcjLat - deltaLat) to (gcjLon - deltaLon)
```

not the current `+ delta` result. Keep conversion at the picker only.

- [ ] **Step 4: Use the converter exactly once in the picker**

Replace the private `convertToGCJ02` function with the utility call and delete the duplicated math. Persist the returned pair unchanged into `LocationTarget`; do not alter model conversion or adapter code.

- [ ] **Step 5: Run focused model/coordinate tests**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.ui.XiaomiLocationCoordinatesTest" --tests "com.banana.hypermodes.systemserver.trigger.LocationTriggerTest" --tests "com.banana.hypermodes.data.ModeConversionTest"
```

Expected: PASS and location round trips preserve already-stored values exactly.

- [ ] **Step 6: Review checkpoint**

Search for `convertToGCJ02`; no production reference should remain. Do not commit.

---

### Task 9: Remove Unneeded Polaris Injection and Resolve Probe Naming

**Files:**
- Modify: `app/src/main/res/values/arrays.xml`
- Rename or modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/PolarisGeofenceAdapter.kt`
- Modify imports: `app/src/main/java/com/banana/hypermodes/hook/SystemModeHook.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/systemserver/trigger/PolarisGeofenceAdapterTest.kt`

**Interfaces:**
- Produces a uniquely named `PolarisCapabilityProbe` for diagnostics.
- Leaves only the real runtime adapter named `PolarisGeofenceAdapter`.
- Does not inject Xposed code into the Polaris process.

- [ ] **Step 1: Write/rename the probe classification test first**

Rename the old test to cover `PolarisCapabilityProbe.CapabilityResult`. Run it before renaming production code and verify compilation fails against the wished-for class name.

- [ ] **Step 2: Rename the old probe class and file**

Rename `systemserver/trigger/PolarisGeofenceAdapter.kt` to `PolarisCapabilityProbe.kt`, update its class name, TAG, call sites, and tests. Do not change probe behavior in this task.

- [ ] **Step 3: Remove Polaris from `xposed_scope`**

Delete only:

```xml
<item>com.xiaomi.gnss.polaris</item>
```

The runtime design uses Binder and broadcast contracts and installs no Polaris package hook.

- [ ] **Step 4: Run probe tests and resource processing**

Run:

```powershell
./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.trigger.PolarisCapabilityProbeTest" :app:processDebugResources
```

Expected: PASS.

- [ ] **Step 5: Review checkpoint**

Search for two classes named `PolarisGeofenceAdapter`; exactly one remains. Do not commit.

---

### Task 10: Full Verification and On-Device Proof

**Files:**
- No production changes unless verification exposes a test-backed defect.
- Update test files only if a newly discovered failing case requires a new red-green cycle.

**Interfaces:**
- Produces evidence that the generated ABI, JVM behavior, APK, and device integration all work.

- [ ] **Step 1: Run all focused location tests**

```powershell
./gradlew :app:testDebugUnitTest \
  --tests "com.banana.hypermodes.systemserver.geofence.*" \
  --tests "com.banana.hypermodes.systemserver.trigger.LocationTrigger*" \
  --tests "com.banana.hypermodes.ui.XiaomiLocationCoordinatesTest" \
  --tests "com.banana.hypermodes.protocol.ProtocolTest"
```

Expected: all PASS with no unexpected warnings.

- [ ] **Step 2: Run the full JVM suite**

```powershell
./gradlew :app:testDebugUnitTest
```

Expected: BUILD SUCCESSFUL. If an unrelated pre-existing failure occurs, report it exactly and do not conceal it.

- [ ] **Step 3: Compile AIDL and assemble the APK**

```powershell
./gradlew :app:compileDebugAidl :app:assembleDebug
```

Expected: BUILD SUCCESSFUL and a debug APK under `app/build/outputs/apk/debug/`.

- [ ] **Step 4: Inspect generated Binder transaction constants**

Search generated `IMiGeoManagerService.java` and verify:

```text
TRANSACTION_getVendorVersion = 1
TRANSACTION_addGeofenceWithFlag = 2
TRANSACTION_addGeofence = 3
TRANSACTION_registerComponent = 9
TRANSACTION_getGeofenceStatus = 13
```

There must be no generated `TRANSACTION_unregisterComponent`.

- [ ] **Step 5: Inspect the final working diff**

Run `git diff --check`, `git status --short`, and a targeted diff over all location/AIDL/protocol/manifest files. Confirm no unrelated user changes were reverted and no precise location was added to logs.

- [ ] **Step 6: Install only after explicit user authorization if installation has not already been requested**

When authorized:

```powershell
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

Because this is an LSPosed module loaded into `system_server`, reboot or framework restart is required before judging the new code.

- [ ] **Step 7: Verify registration on the connected Xiaomi device**

After reload, create one ARRIVE trigger at the current location. Collect narrowly filtered logs for:

```text
PolarisGeofenceAdapter
PolarisCallbackReceiver
LocationTriggerManager
ComplexTriggerManager
```

Verify in order:

1. Polaris binds from system_server;
2. child geo service is live;
3. callback component is registered under client namespace `android`;
4. a `hypermodes_` fence is added or reconciled;
5. `getGeofenceStatus` returns `IN(1)`;
6. `LocationTriggerManager` emits active once;
7. `RoutineCoreEngine` activates the expected mode without leaving the area.

- [ ] **Step 8: Verify subsequent transitions and restart**

Move outside and back inside, confirming one inactive and one active transition. Reboot once, then confirm register → reconcile → status repeats and non-HyperModes Polaris fences remain unchanged.

- [ ] **Step 9: Final review checkpoint**

Do not claim the bug fixed unless focused tests, full tests, assembly, generated AIDL inspection, and the on-device current-location activation all have fresh passing evidence. Do not commit unless explicitly requested.
