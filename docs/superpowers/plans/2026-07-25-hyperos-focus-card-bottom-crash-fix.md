# HyperOS Focus Card Bottom Placement and Crash Fix Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Move the native Focus `QSRecord` to the final left-half row below device controls and eliminate the reproducible SystemUI crash when opening its native Detail panel.

**Architecture:** Keep Focus as the same native card-style `QSTile/QSRecord`, but remove it from the priority-20 `QSCardsController` list and expose it through a stable tail `MainPanelContent` proxy inserted before `rightFooterSpace`. Remove production `Tracking*` Android View subclasses, keep reflected native `QSDetailContent` as the preferred Detail implementation, and use only standard Views for the fail-closed fallback.

**Tech Stack:** Kotlin 2.4.10, Android minSdk 35 / compileSdk 37, libxposed API 101.0.1, Java reflection/proxy, JUnit 4.13.2, HyperOS 3 `OS3.0.317.0.WBLCNXM`, MIUISystemUIPlugin `17.1.4.71.0`.

## Global Constraints

- Work in the existing `feat/hyperos-focus-card` working tree and preserve unrelated pre-existing modifications.
- Do not reintroduce Android `TileService`, `MiuiQSTile`, custom RecyclerView views, or the deleted legacy tile files.
- Focus spec remains exactly `hypermodes_focus`.
- Focus remains the native `QSRecord` created by `QSCardsController`; the tail proxy delegates native creation/binding rather than creating a replacement View.
- Final placement is after all available device-center/device-control content and before structural `FooterSpaceController`; Focus occupies the left two spans and the right two spans remain empty.
- Native Detail routing remains `onShowDetail(true/false)` through `QSController` and `SecondaryPanelRouter`.
- Production fallback UI uses only standard Android `ScrollView`, `LinearLayout`, and `ImageView`; no JVM test tracking subclass may ship in the APK.
- Every private-API mismatch and detail-construction failure must fail closed without crashing SystemUI.
- Strict TDD: each production change follows a focused failing test observed before implementation.
- Do not commit unless the user explicitly asks; run diff checks instead.

---

## File Structure

### Modified production files

- `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
  - Filter Focus from native QSCards output, build a tail `MainPanelContent` proxy, and insert it into the right panel before footer spacing.
- `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
  - Remove `Tracking*` Views, add pure row descriptors, and contain native/manual Detail construction failures.

### Modified tests

- `app/src/test/java/com/banana/hypermodes/hook/ControlCenterCardHookTest.kt`
  - Cover Focus filtering, tail insertion, proxy delegation, idempotency, and placement with/without device content.
- `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`
  - Cover crash regression, pure row descriptors, native failure diagnostics, standard fallback, and safe empty fallback.

---

### Task 1: Remove the production Tracking View crash

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
- Test: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`

**Interfaces:**
- Preserve: `FocusModeDetailAdapter(...).create(): Any`
- Preserve: `FocusModeSelectionController.select(modeId: String)`
- Produce:

```kotlin
internal data class FocusModeRowDescriptor(
    val id: String,
    val title: String,
    val status: String,
    val contentDescription: String,
    val iconResId: Int,
    val selected: Boolean
)

internal fun buildFocusModeRows(snapshot: FocusCardSnapshot): List<FocusModeRowDescriptor>
```

- [ ] **Step 1: Write failing crash-regression and row-mapping tests**

Add tests that read the production class names through a dedicated exposed list and assert no tracking subclasses are present:

```kotlin
@Test
fun `manual fallback uses only standard Android view classes`() {
    assertEquals(
        listOf(
            android.widget.ScrollView::class.java,
            android.widget.LinearLayout::class.java,
            android.widget.ImageView::class.java
        ),
        FocusModeManualViewClasses.productionClasses
    )
}
```

Add pure mapping tests:

```kotlin
@Test
fun `row descriptors map mode identity status and selection`() {
    val rows = buildFocusModeRows(activeSnapshot("work"))
    assertEquals(listOf("dnd", "work"), rows.map { it.id })
    assertEquals(listOf(false, true), rows.map { it.selected })
    assertEquals("Work, On", rows.single { it.id == "work" }.contentDescription)
}
```

- [ ] **Step 2: Run the focused test and verify RED**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest"
```

Expected: compilation failure for missing `FocusModeManualViewClasses`, `FocusModeRowDescriptor`, and `buildFocusModeRows`, or assertion failure because production still uses `Tracking*` classes.

- [ ] **Step 3: Replace production Tracking subclasses with standard Views**

Delete these private production classes completely:

```text
TrackingScrollView
TrackingLinearLayout
TrackingImageView
TrackingClickDispatcher
```

In manual fallback construction use:

```kotlin
val scrollView = convertView as? ScrollView ?: ScrollView(context)
val list = LinearLayout(context)
val icon = ImageView(context)
```

Do not override `getChildCount`, `getChildAt`, `performClick`, `getDrawable`, or any other Android lifecycle-dispatched method.

- [ ] **Step 4: Extract pure row descriptors and consume them in both native/manual builders**

Implement `buildFocusModeRows(snapshot)` once. Native `SelectableItem` mapping and manual row creation both consume the same descriptors, avoiding View-tree introspection in JVM tests.

- [ ] **Step 5: Run focused test and compile**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest"
.\gradlew.bat :app:compileDebugKotlin
```

Expected: focused tests and Kotlin compilation pass.

---

### Task 2: Contain native and manual Detail construction failures

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
- Test: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`

**Interfaces:**
- Produce:

```kotlin
internal enum class FocusDetailFallbackStage {
    NATIVE_API_UNAVAILABLE,
    NATIVE_CONVERT,
    NATIVE_ITEMS,
    NATIVE_CALLBACK,
    MANUAL_BUILD
}

internal fun interface FocusDetailDiagnostic {
    fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?)
}
```

- [ ] **Step 1: Write failing fallback tests**

Cover:

```kotlin
@Test fun `native failure records exact stage and uses manual builder`()
@Test fun `manual failure returns safe empty view instead of throwing`()
@Test fun `createDetailView never leaks native reflection exception`()
```

Inject fake native/manual builders into a small internal coordinator rather than constructing Android Views in JVM tests.

- [ ] **Step 2: Run focused test and verify RED**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest"
```

Expected: missing coordinator/diagnostic types or uncaught fake exception.

- [ ] **Step 3: Implement a fail-closed detail-view coordinator**

Use this sequence:

```text
native API unavailable -> diagnose NATIVE_API_UNAVAILABLE -> manual
native convert/items/callback failure -> diagnose exact stage -> manual
manual failure -> diagnose MANUAL_BUILD -> return plain View(context)
```

`DetailInvocationHandler.invoke("createDetailView")` must wrap the full sequence and never allow an exception to reach `DetailPanelDelegate`.

- [ ] **Step 4: Add concise production logging**

Use one log entry per failed stage and exception class/message. Do not log every successful bind or row.

- [ ] **Step 5: Verify focused tests and resource compilation**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest"
.\gradlew.bat :app:processDebugResources :app:compileDebugKotlin
```

Expected: focused tests, resources, and Kotlin compile pass.

---

### Task 3: Filter Focus from the native QSCards content group

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
- Test: `app/src/test/java/com/banana/hypermodes/hook/ControlCenterCardHookTest.kt`

**Interfaces:**
- Produce:

```kotlin
internal fun filterFocusRecord(items: Any?): Any?
internal fun recordSpec(record: Any): String?
```

- [ ] **Step 1: Write failing list-filter tests**

Cover:

```kotlin
@Test fun `filter removes only focus record and preserves native order`()
@Test fun `filter returns original non-list result unchanged`()
@Test fun `filter fails closed when a record spec cannot be read`()
@Test fun `filter does not mutate immutable source list`()
```

Fake records expose `getSpec()`.

- [ ] **Step 2: Run focused test and verify RED**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
```

Expected: missing filter helper.

- [ ] **Step 3: Implement copy-and-filter helper**

Copy the source list. Remove exactly records whose reflected `getSpec()` equals `hypermodes_focus`. If any record cannot be inspected, return the original result rather than a partially filtered copy.

- [ ] **Step 4: Hook `QSCardsController.getListItems()` after original**

Install with `ExceptionMode.PROTECTIVE`:

```kotlin
val original = chain.proceed()
filterFocusRecord(original)
```

If the tail owner cannot be installed during class validation, do not install this filter hook; partial filtering would make Focus disappear.

- [ ] **Step 5: Run focused test and compile**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
.\gradlew.bat :app:compileDebugKotlin
```

Expected: focused tests and Kotlin compile pass.

---

### Task 4: Build a stable tail MainPanelContent proxy

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
- Test: `app/src/test/java/com/banana/hypermodes/hook/ControlCenterCardHookTest.kt`

**Interfaces:**
- Produce:

```kotlin
internal class FocusTailContent(
    private val delegate: Any,
    private val mainPanelContentInterface: Class<*>
) {
    fun proxy(): Any
    fun focusRecord(): Any?
}
```

The proxy handles `MainPanelContent` methods by name and returns type-correct defaults for future interface methods.

- [ ] **Step 1: Write failing proxy contract tests**

Using a fake MainPanelContent interface/delegate, cover:

- `getListItems()` returns exactly the native Focus record.
- `available()` delegates and also requires a Focus record.
- `getRightOrLeft()` returns true.
- `getPriority()` returns `Int.MAX_VALUE - 1`.
- `createViewHolder()` delegates.
- bind/unbind/update/spread/expand/payload methods delegate exactly once.
- `moveElement()` returns false.
- object methods `equals/hashCode/toString` are identity-safe.
- unknown primitive-returning methods get type-correct defaults.

- [ ] **Step 2: Run focused test and verify RED**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
```

Expected: missing proxy implementation.

- [ ] **Step 3: Implement the dynamic proxy**

Load `miui.systemui.controlcenter.panel.main.MainPanelContent` from the plugin ClassLoader. Delegate native lifecycle methods through `Reflect.call`. Never cache the Focus record across plugin rebuilds; resolve it through `delegate.getTile(FOCUS_CARD_SPEC)` whenever `getListItems()` or `available()` is called.

- [ ] **Step 4: Keep one stable proxy per plugin ClassLoader/controller**

Use weak identity keys so repeated plugin loads and configuration changes reuse the same tail owner, while unloaded plugin ClassLoaders remain collectible.

- [ ] **Step 5: Run focused test and compile**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
.\gradlew.bat :app:compileDebugKotlin
```

Expected: proxy tests and compilation pass.

---

### Task 5: Insert Focus tail before right footer spacing

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
- Test: `app/src/test/java/com/banana/hypermodes/hook/ControlCenterCardHookTest.kt`

**Interfaces:**
- Produce:

```kotlin
internal fun insertFocusTail(
    rightPanelContent: MutableList<Any>,
    tailProxy: Any,
    rightFooterSpace: Any?
): Boolean
```

- [ ] **Step 1: Write failing insertion tests**

Cover:

```kotlin
@Test fun `tail is inserted immediately before footer`()
@Test fun `tail appends when footer is absent`()
@Test fun `repeated insertion is idempotent`()
@Test fun `device content remains before tail`()
@Test fun `tail moves from stale earlier position to footer boundary`()
```

- [ ] **Step 2: Run focused test and verify RED**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
```

Expected: missing insertion helper.

- [ ] **Step 3: Implement pure idempotent insertion**

Remove all identity-equal occurrences of the proxy, find `rightFooterSpace` by identity, then insert immediately before it or append. Return whether the list changed.

- [ ] **Step 4: Hook `MainPanelContentDistributor.distributePanels(boolean)` after original**

Resolve:

```text
miui.systemui.controlcenter.panel.main.MainPanelContentDistributor
rightPanelContent
rightFooterSpace
childControllers
```

After `chain.proceed()`:

1. Find the live `QSCardsController` among `childControllers` by exact class.
2. Obtain/create its stable Focus tail proxy.
3. If the proxy is available and has a Focus record, insert it before footer.
4. If any step fails, leave the native panel list unchanged and log once.

Install the QSCards filter and distributor insertion hooks as one validated feature set so Focus cannot be filtered without a tail replacement.

- [ ] **Step 5: Preserve horizontal sizing**

Keep the existing `preparePanelUpdate()` hook. It must still set the native Focus record to `shrinkCardStyle=false`, giving span `2` in the tail row.

- [ ] **Step 6: Run focused tests and full build**

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
.\gradlew.bat :app:testDebugUnitTest
.\gradlew.bat :app:assembleDebug
```

Expected: all tests and build pass.

---

### Task 6: Full review and HyperOS device verification

**Files:**
- No additional production changes unless runtime evidence identifies a concrete defect.

- [ ] **Step 1: Run fresh local verification**

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug :app:compileDebugKotlin --stacktrace
git diff --check
```

Expected: `BUILD SUCCESSFUL`, no test failures, and no diff errors.

- [ ] **Step 2: Independently review the combined diff**

Review for:

- no production `Tracking*` Views;
- no exception escaping `createDetailView()`;
- no partial filter without tail insertion;
- tail proxy lifecycle delegation completeness;
- idempotent distribution and plugin reload behavior;
- no regression to native click/long-click/detail close routing.

- [ ] **Step 3: Install APK and restart SystemUI with explicit authorization**

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
adb logcat -c
adb shell su -c "killall com.android.systemui"
```

Wait for a new SystemUI PID.

- [ ] **Step 4: Verify placement**

Confirm on the target device:

- Focus is absent from the top QSCards group.
- Focus is below the available four-column device panel.
- Focus is the final real item before bottom spacing.
- Focus occupies the left two spans, leaves the right two spans blank, and has one-row Wi-Fi card height.
- Reopening Control Center, rotating, and toggling device-control availability do not duplicate or move Focus incorrectly.

- [ ] **Step 5: Verify long press and Detail lifecycle**

- Long press opens the native SecondaryPanel without restarting SystemUI.
- Native `QSDetailContent` rows appear when the private API is compatible.
- Any fallback reason is visible in concise HyperModes logs.
- Selecting a mode refreshes Focus and closes Detail through `onShowDetail(false)`.
- Status bar, blur, scrim, and main Control Center restore normally.

- [ ] **Step 6: Scan final failure logs**

```powershell
adb shell "logcat -d -v threadtime | grep -E 'FATAL EXCEPTION|Process: com.android.systemui|TrackingScrollView|ControlCenterCardHook.*fail|FocusModeDetail.*fail|PluginInstance.*Failure'"
```

Expected: no new SystemUI fatal exception or uncontained Focus failure.
