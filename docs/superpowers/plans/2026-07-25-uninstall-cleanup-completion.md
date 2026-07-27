# HyperModes Uninstall Cleanup Completion Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Complete the uninstall cleanup fix so genuine uninstall immediately blocks scheduled modes, teardown has no persistence side effects, observers are released, and APK replacement never deletes configuration.

**Architecture:** Preserve the existing zero-process `system_server` engine and package lifecycle receivers. Extract the lifecycle and cleanup decision points into small pure/testable helpers, then wire those helpers into `RoutineCoreEngine`, `ScheduledModeManager`, `DrivingTriggerManager`, and `BedtimeListener`.

**Tech Stack:** Kotlin, Android APIs, LSPosed/libxposed, JUnit 4, Robolectric, Mockito.

## Global Constraints

- Do not replace `AlarmManager.OnAlarmListener` scheduling with app-process receivers or package-owned `PendingIntent`s.
- Genuine uninstall must perform full cleanup: cancel alarms, stop runtime listeners, restore active effects, clear in-memory state, remove `Settings.Global["pixel_routines_full_config"]`, and best-effort disable DeskClock bedtime.
- `Intent.EXTRA_REPLACING == true` must preserve configuration and must not trigger package-absence shutdown.
- `REMOVED` is terminal for the current `system_server` process and must be set before cleanup work or queued callbacks can run.
- All cleanup is idempotent and best-effort; one failed subsystem must not prevent the remaining cleanup steps.
- Avoid unrelated refactors and unrelated Focus Control Center behavior changes.
- Every production behavior change needs a failing test first and a passing test afterward.

---

### Task 1: Atomic Lifecycle Gate

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt`
- Test: `app/src/test/java/com/banana/hypermodes/systemserver/RoutineCoreEngineLifecycleTest.kt`

**Interfaces:**
- Consumes: existing `RoutineCoreEngine.LifecycleState` and `RoutineCoreEngine.getInstance()`.
- Produces: `RoutineCoreEngine.beginPackageRemoval(): Boolean`; existing `shutdownForPackageRemoval()` remains the public cleanup entry point.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.banana.hypermodes.systemserver

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RoutineCoreEngineLifecycleTest {

    @Test
    fun `beginPackageRemoval is atomic and terminal`() {
        val engine = RoutineCoreEngine.getInstance()
        val lifecycleField = RoutineCoreEngine::class.java.getDeclaredField("lifecycleState")
        lifecycleField.isAccessible = true
        lifecycleField.set(engine, RoutineCoreEngine.LifecycleState.RUNNING)

        assertTrue(engine.beginPackageRemoval())
        assertEquals(
            RoutineCoreEngine.LifecycleState.REMOVED,
            engine.getLifecycleState()
        )

        assertFalse(engine.beginPackageRemoval())
        assertEquals(
            RoutineCoreEngine.LifecycleState.REMOVED,
            engine.getLifecycleState()
        )
    }

    @Test
    fun `replacement cannot revive removed engine`() {
        val engine = RoutineCoreEngine.getInstance()
        val lifecycleField = RoutineCoreEngine::class.java.getDeclaredField("lifecycleState")
        lifecycleField.isAccessible = true
        lifecycleField.set(engine, RoutineCoreEngine.LifecycleState.REMOVED)

        engine.setLifecycleState(RoutineCoreEngine.LifecycleState.RUNNING)

        assertEquals(
            RoutineCoreEngine.LifecycleState.REMOVED,
            engine.getLifecycleState()
        )
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.RoutineCoreEngineLifecycleTest`
Expected: FAIL because `beginPackageRemoval()` does not exist.

- [ ] **Step 3: Write minimal implementation**

In `RoutineCoreEngine.kt`, replace the plain volatile lifecycle write with a synchronized terminal transition helper:

```kotlin
@Volatile
private var lifecycleState = LifecycleState.RUNNING

private val lifecycleLock = Any()

fun beginPackageRemoval(): Boolean = synchronized(lifecycleLock) {
    if (lifecycleState == LifecycleState.REMOVED) return false
    log("Engine lifecycle transition: $lifecycleState -> REMOVED")
    lifecycleState = LifecycleState.REMOVED
    true
}
```

Change `shutdownForPackageRemoval()` so the gate closes before posting work:

```kotlin
fun shutdownForPackageRemoval() {
    if (!beginPackageRemoval()) {
        log("Engine already removed, skipping shutdown")
        return
    }

    val cleanup = {
        log("Starting engine shutdown for package removal...")
        performPackageRemovalCleanup()
    }

    mainHandler?.post(cleanup) ?: cleanup()
}
```

Add the cleanup body as a private method:

```kotlin
private fun performPackageRemovalCleanup() {
    scheduledModeManager?.cancelAllSchedules()
    drivingTriggerManager?.cleanupForPackageRemoval()
    bedtimeListener?.cleanupForPackageRemoval()

    currentActiveMode?.let {
        log("Reverting active mode for shutdown: ${it.name}")
        modeActionExecutor?.revertMode(it)
        currentActiveMode = null
    }

    allModes = emptyList()
    dismissedScheduledModes.clear()

    systemContext?.let { context ->
        try {
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, null)
            log("Removed global config from Settings.Global")
        } catch (e: Exception) {
            log("Failed to remove global config: ${e.message}")
        }
    }

    log("RoutineCoreEngine shutdown complete")
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.RoutineCoreEngineLifecycleTest`
Expected: PASS.

- [ ] **Step 5: Run adjacent tests**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.RoutineCoreEngineTest --tests com.banana.hypermodes.protocol.PackageLifecyclePolicyTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt app/src/test/java/com/banana/hypermodes/systemserver/RoutineCoreEngineLifecycleTest.kt
git commit -m "fix: close engine lifecycle gate before uninstall cleanup"
```

---

### Task 2: Side-Effect-Free Trigger Teardown

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/DrivingTriggerManager.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListener.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt`
- Test: `app/src/test/java/com/banana/hypermodes/systemserver/trigger/TriggerCleanupPolicyTest.kt`

**Interfaces:**
- Consumes: existing `DrivingTriggerManager.cleanup()`, `BedtimeListener.cleanup()`, and `RoutineCoreEngine.performPackageRemovalCleanup()`.
- Produces: `DrivingTriggerManager.cleanupForPackageRemoval()`; `BedtimeListener.cleanupForPackageRemoval()`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.banana.hypermodes.systemserver.trigger

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TriggerCleanupPolicyTest {

    @Test
    fun `package removal cleanup never calls normal deactivation`() {
        assertFalse(TriggerCleanupPolicy.shouldDeactivateModeOnPackageRemoval)
    }

    @Test
    fun `normal cleanup still deactivates runtime trigger mode`() {
        assertTrue(TriggerCleanupPolicy.shouldDeactivateModeOnRuntimeCleanup)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.trigger.TriggerCleanupPolicyTest`
Expected: FAIL because `TriggerCleanupPolicy` does not exist.

- [ ] **Step 3: Write minimal implementation**

Create `TriggerCleanupPolicy.kt` beside the trigger classes:

```kotlin
package com.banana.hypermodes.systemserver.trigger

object TriggerCleanupPolicy {
    const val shouldDeactivateModeOnPackageRemoval: Boolean = false
    const val shouldDeactivateModeOnRuntimeCleanup: Boolean = true
}
```

In `DrivingTriggerManager.kt`, add:

```kotlin
fun cleanupForPackageRemoval() {
    unregisterBluetoothReceiver()
    drivingModes = emptyList()
    currentDrivingModeId = null
    isInitialized = false
}
```

In `BedtimeListener.kt`, add:

```kotlin
fun cleanupForPackageRemoval() {
    if (receiverRegistered) {
        try {
            context.unregisterReceiver(receiver)
            receiverRegistered = false
        } catch (e: Exception) {
            log("Failed to unregister bedtime active receiver: ${e.message}")
        }
    }

    registeredSecureKeys.forEach { key ->
        try {
            context.contentResolver.unregisterContentObserver(bedtimeSettingsObserver)
        } catch (e: Exception) {
            log("Failed to unregister $key observer: ${e.message}")
        }
    }
    registeredSecureKeys.clear()
    allModes = emptyList()
}
```

In `RoutineCoreEngine.performPackageRemovalCleanup()`, ensure the trigger calls are exactly:

```kotlin
drivingTriggerManager?.cleanupForPackageRemoval()
bedtimeListener?.cleanupForPackageRemoval()
```

Do not call `bedtimeListener.deactivateBedtime()` during package-removal cleanup.

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.trigger.TriggerCleanupPolicyTest`
Expected: PASS.

- [ ] **Step 5: Run adjacent tests**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.trigger.BedtimeListenerLifecycleTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/trigger/TriggerCleanupPolicy.kt app/src/main/java/com/banana/hypermodes/systemserver/trigger/DrivingTriggerManager.kt app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListener.kt app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt app/src/test/java/com/banana/hypermodes/systemserver/trigger/TriggerCleanupPolicyTest.kt
git commit -m "fix: remove uninstall cleanup side effects"
```

---

### Task 3: Release Engine Configuration Observer

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt`
- Test: `app/src/test/java/com/banana/hypermodes/systemserver/ObserverLifecyclePolicyTest.kt`

**Interfaces:**
- Consumes: existing `observeConfigChanges(context)` and `performPackageRemovalCleanup()`.
- Produces: private `configObserver: ContentObserver?` lifecycle management in `RoutineCoreEngine`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.banana.hypermodes.systemserver

import org.junit.Assert.assertTrue
import org.junit.Test

class ObserverLifecyclePolicyTest {

    @Test
    fun `package removal unregisters config observer`() {
        assertTrue(ObserverLifecyclePolicy.unregisterConfigObserverOnPackageRemoval)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.ObserverLifecyclePolicyTest`
Expected: FAIL because `ObserverLifecyclePolicy` does not exist.

- [ ] **Step 3: Write minimal implementation**

Create `ObserverLifecyclePolicy.kt` beside `RoutineCoreEngine.kt`:

```kotlin
package com.banana.hypermodes.systemserver

object ObserverLifecyclePolicy {
    const val unregisterConfigObserverOnPackageRemoval: Boolean = true
}
```

In `RoutineCoreEngine.kt`, add:

```kotlin
private var configObserver: ContentObserver? = null
```

Change `observeConfigChanges(context)` so it stores the observer:

```kotlin
private fun observeConfigChanges(context: Context) {
    val handler = mainHandler ?: Handler(Looper.getMainLooper())
    val observer = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if (lifecycleState == LifecycleState.REMOVED) return
            log("Config changed, reloading...")
            loadConfigFromSettings()
        }
    }

    context.contentResolver.registerContentObserver(
        Settings.Global.getUriFor(CONFIG_KEY),
        false,
        observer
    )
    configObserver = observer
    log("ContentObserver registered for $CONFIG_KEY")
}
```

In `performPackageRemovalCleanup()`, unregister it before clearing state:

```kotlin
systemContext?.let { context ->
    configObserver?.let { observer ->
        try {
            context.contentResolver.unregisterContentObserver(observer)
        } catch (e: Exception) {
            log("Failed to unregister config observer: ${e.message}")
        }
    }
}
configObserver = null
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.ObserverLifecyclePolicyTest`
Expected: PASS.

- [ ] **Step 5: Run adjacent tests**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.RoutineCoreEngineLifecycleTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt app/src/main/java/com/banana/hypermodes/systemserver/ObserverLifecyclePolicy.kt app/src/test/java/com/banana/hypermodes/systemserver/ObserverLifecyclePolicyTest.kt
git commit -m "fix: unregister engine config observer on uninstall"
```

---

### Task 4: Replacement-Safe Package Presence Policy

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/PackagePresencePolicy.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ScheduledModeManager.kt`
- Test: `app/src/test/java/com/banana/hypermodes/systemserver/PackagePresencePolicyTest.kt`

**Interfaces:**
- Consumes: `RoutineCoreEngine.LifecycleState` and package lookup result.
- Produces: `PackagePresencePolicy.onMissingPackage(state): MissingPackageAction` with values `SKIP_ONLY`, `SHUTDOWN`, and `ALLOW`.

- [ ] **Step 1: Write the failing test**

```kotlin
package com.banana.hypermodes.systemserver

import org.junit.Assert.assertEquals
import org.junit.Test

class PackagePresencePolicyTest {

    @Test
    fun `missing package during replacement only skips callback`() {
        assertEquals(
            PackagePresencePolicy.MissingPackageAction.SKIP_ONLY,
            PackagePresencePolicy.onMissingPackage(
                RoutineCoreEngine.LifecycleState.REPLACING
            )
        )
    }

    @Test
    fun `missing package while running shuts down`() {
        assertEquals(
            PackagePresencePolicy.MissingPackageAction.SHUTDOWN,
            PackagePresencePolicy.onMissingPackage(
                RoutineCoreEngine.LifecycleState.RUNNING
            )
        )
    }

    @Test
    fun `removed engine never schedules cleanup again`() {
        assertEquals(
            PackagePresencePolicy.MissingPackageAction.ALLOW,
            PackagePresencePolicy.onMissingPackage(
                RoutineCoreEngine.LifecycleState.REMOVED
            )
        )
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.PackagePresencePolicyTest`
Expected: FAIL because `PackagePresencePolicy` does not exist.

- [ ] **Step 3: Write minimal implementation**

Create `PackagePresencePolicy.kt`:

```kotlin
package com.banana.hypermodes.systemserver

object PackagePresencePolicy {

    enum class MissingPackageAction {
        SKIP_ONLY,
        SHUTDOWN,
        ALLOW
    }

    fun onMissingPackage(
        state: RoutineCoreEngine.LifecycleState
    ): MissingPackageAction {
        return when (state) {
            RoutineCoreEngine.LifecycleState.RUNNING -> MissingPackageAction.SHUTDOWN
            RoutineCoreEngine.LifecycleState.REPLACING -> MissingPackageAction.SKIP_ONLY
            RoutineCoreEngine.LifecycleState.REMOVED -> MissingPackageAction.ALLOW
        }
    }
}
```

In `ScheduledModeManager.kt`, replace the missing-package branch inside the alarm listener:

```kotlin
if (!isPackageInstalled(context, com.banana.hypermodes.protocol.Protocol.MODULE_PACKAGE)) {
    when (PackagePresencePolicy.onMissingPackage(engine.getLifecycleState())) {
        PackagePresencePolicy.MissingPackageAction.SHUTDOWN -> {
            log("Skipping alarm: package not installed, requesting engine shutdown")
            engine.shutdownForPackageRemoval()
        }
        PackagePresencePolicy.MissingPackageAction.SKIP_ONLY -> {
            log("Skipping alarm: package temporarily unavailable during replacement")
        }
        PackagePresencePolicy.MissingPackageAction.ALLOW -> Unit
    }
    return@OnAlarmListener
}
```

Add the import:

```kotlin
import com.banana.hypermodes.systemserver.PackagePresencePolicy
```

- [ ] **Step 4: Run test to verify it passes**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.systemserver.PackagePresencePolicyTest`
Expected: PASS.

- [ ] **Step 5: Run package lifecycle tests**

Run: `./gradlew :app:testDebugUnitTest --tests com.banana.hypermodes.protocol.PackageLifecyclePolicyTest --tests com.banana.hypermodes.systemserver.PackagePresencePolicyTest`
Expected: PASS.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/PackagePresencePolicy.kt app/src/main/java/com/banana/hypermodes/systemserver/trigger/ScheduledModeManager.kt app/src/test/java/com/banana/hypermodes/systemserver/PackagePresencePolicyTest.kt
git commit -m "fix: avoid uninstall cleanup during package replacement"
```

---

### Task 5: Full Verification

**Files:**
- Modify: none unless verification exposes a defect.
- Test: all existing unit tests.

**Interfaces:**
- Consumes: Tasks 1–4.
- Produces: verified build and test status.

- [ ] **Step 1: Run full unit test suite**

Run: `./gradlew :app:testDebugUnitTest`
Expected: PASS.

- [ ] **Step 2: Run debug build**

Run: `./gradlew :app:assembleDebug`
Expected: PASS.

- [ ] **Step 3: Check diff formatting**

Run: `git diff --check`
Expected: no output.

- [ ] **Step 4: Review final diff**

Run: `git diff --stat origin/uninstall-fix..HEAD`
Expected: only lifecycle/cleanup/test files are changed after the merge.

- [ ] **Step 5: Commit verification result if files changed**

```bash
git add -A
git commit -m "test: verify uninstall cleanup lifecycle"
```

If no file changed, skip the commit.

## Self-Review

### Spec coverage

- Atomic `REMOVED` gate: Task 1.
- Side-effect-free driving/bedtime teardown: Task 2.
- Config observer release: Task 3.
- Replacement-safe package absence handling: Task 4.
- Full test/build verification: Task 5.

### Placeholder scan

- No TODO/TBD placeholders.
- Every code step includes concrete Kotlin content.
- Every command includes the expected result.

### Type consistency

- `RoutineCoreEngine.beginPackageRemoval(): Boolean` is introduced once and consumed by `shutdownForPackageRemoval()`.
- `TriggerCleanupPolicy` constants are used only by its focused policy test; production behavior is wired through explicit `cleanupForPackageRemoval()` methods.
- `ObserverLifecyclePolicy` is a focused policy test seam; production observer ownership stays in `RoutineCoreEngine`.
- `PackagePresencePolicy.MissingPackageAction` values match the `ScheduledModeManager` branch exactly.
