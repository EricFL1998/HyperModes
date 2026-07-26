# Focus Native Detail UI Simplification Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Keep the native HyperOS Focus detail panel while removing its bottom settings action, item status summaries, inner list background, and visible scrollbar.

**Architecture:** Continue returning the existing `QSDetailContent` from `FocusModeDetailSession`. Simplify the `DetailAdapter` and `SelectableItem` values at their existing construction boundaries, then run a best-effort, Focus-local view decorator against the returned native content; do not add process-wide hooks or replace the native list.

**Tech Stack:** Kotlin, Android View APIs, Java dynamic proxies/reflection, HyperOS `QSDetailContent`, JUnit 4, Gradle Android plugin, adb/LSPosed device verification.

## Global Constraints

- Retain the outer glass rounded MIUI detail panel.
- Remove the bottom “more settings” action entirely.
- Remove the inner list background and vertical scrollbar while preserving touch scrolling.
- Render each mode name without visible `On` or `Off` text.
- Preserve native animation, `hypermodes_focus` adapter mapping, specific-height behavior, complete item count, click handling, accessibility selection state, and hidden-completion refresh.
- Scope all visual changes to the `QSDetailContent` instance returned for the Focus session; do not change other detail panels.
- Failure to locate or decorate the internal list or row attach callback is non-fatal and must not display “Focus detail is unavailable”.
- Do not force `MATCH_PARENT` or change root/content/list `LayoutParams` or height.
- Mode icons resolve in this order: `ModeConfig.statusIcon`, `ModeIconMapper.getStatusBarIcon(mode.icon)`, `R.drawable.ic_stat_zen`, `android.R.drawable.ic_dialog_info`, transparent `ColorDrawable`.
- Use module resources as the primary Drawable owner and plugin resources only as compatibility fallback.
- Set the resolved Drawable only on Focus native `SelectableItem.iconDrawable`; preserve native tint, dimensions, and placement.
- Do not add a global Xposed/resource hook for these visual changes.
- Do not commit during execution unless the user explicitly asks; the repository is on `main` with pre-existing uncommitted work.

## File Structure

- Modify `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt`: simplify the DetailAdapter settings contract, omit item summaries, set resolved row `iconDrawable`, and invoke Focus-local list decoration after native inflation.
- Create `app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecorator.kt`: locate the native RecyclerView by class hierarchy name and apply non-fatal, instance-local visual changes, including post-attach row cleanup.
- Create `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeIconResolver.kt`: resolve existing mode vector drawables using statusIcon-first priority and documented fallbacks.
- Modify `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`: inject module/plugin context-backed icon resolution into the detail session.
- Modify `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt`: reuse the shared icon resolver for the existing Focus card icon path.
- Modify `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`: cover the missing settings intent, missing row summaries, and non-null row Drawables.
- Create `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeIconResolverTest.kt`: cover statusIcon priority, icon mapping fallback, and final Drawable fallbacks.
- Create `app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecoratorTest.kt`: cover list/host/row background clearing, scrollbar removal, root preservation, child-attach cleanup, callback replacement, and non-fatal hierarchy mismatch.
- Reuse `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt`: regression coverage that native binding still returns and registers content when no compatible list exists.

---

### Task 1: Remove the Settings Action and Status Summaries

**Files:**
- Modify: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt:61-95,288-312`

**Interfaces:**
- Consumes: the existing `FocusModeDetailSession.adapter` dynamic proxy and `FocusNativeDetailContentApi` row construction path.
- Produces: `DetailAdapter.getSettingsIntent(): Intent?` returning `null`; native `SelectableItem` objects whose `title`, `tag`, `selected`, and `selectable` fields remain populated while `summary` and `secondarySummary` remain `null`.

- [ ] **Step 1: Add a failing test for the missing settings action**

Add this test to `FocusModeDetailAdapterTest`:

```kotlin
@Test
fun `native adapter omits settings intent`() {
    val fixture = fixture(configJson(activeModeId = null, lastModeId = "work"))
    val adapter = fixture.createAdapter()

    val settingsIntent = (adapter.adapter as FakeDetailAdapter).getSettingsIntent()

    assertNull(settingsIntent)
}
```

This exercises the real proxy returned to SystemUI rather than testing a helper or constant.

- [ ] **Step 2: Add a failing test for name-only native rows**

Add this test to `FocusModeDetailAdapterTest`:

```kotlin
@Test
fun `native rows omit status summaries while preserving selection`() {
    FakeNativeDetailContent.reset()
    val nativeApi = FocusNativeDetailContentResolver.fromContentClass(
        FakeNativeDetailContent::class.java
    )!!
    val fixture = fixture(
        json = configJson(activeModeId = "work", lastModeId = "work"),
        nativeApiOverride = nativeApi
    )
    val adapter = fixture.createAdapter()
    val context = TestContext("host", "Host", "Empty", "Open", "On", "Off")

    val content = adapter.session.bindDetailView(context, null, null) as FakeNativeDetailContent
    val rows = content.itemValues.map { it as FakeNativeDetailContent.SelectableItem }

    assertEquals(listOf("Work", "Focus"), rows.map { it.title })
    assertTrue(rows.first().selected)
    assertEquals("work", rows.first().tag)
    assertTrue(rows.all { it.summary == null })
    assertTrue(rows.all { it.secondarySummary == null })
}
```

- [ ] **Step 3: Run both new tests and verify RED**

Run:

```powershell
.\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest.native adapter omits settings intent" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest.native rows omit status summaries while preserving selection"
```

Expected: FAIL because `getSettingsIntent()` currently returns a module intent and each row currently receives `"On"` or `"Off"` in `summary`.

- [ ] **Step 4: Implement the minimal adapter and row changes**

In `FocusModeDetailSession.DetailAdapterHandler.invoke`, replace the settings branch:

```kotlin
"getSettingsIntent" -> null
```

Delete the now-unused `createSettingsIntent()` method. Remove the unused imports:

```kotlin
import android.content.Intent
import com.banana.hypermodes.protocol.Protocol
```

In `buildSelectableItem`, remove the status-summary assignment and leave both native summary fields unset:

```kotlin
val selected = activeModeId == mode.id
setField(item, "tag", mode.id)
setField(item, "title", mode.name.ifBlank { "Focus mode" })
setField(item, "selected", selected)
setField(item, "selectable", true)
```

Do not remove `selected`; native accessibility and row state still depend on it.

- [ ] **Step 5: Run the focused tests and verify GREEN**

Run the Step 3 command again.

Expected: both tests PASS.

- [ ] **Step 6: Run the complete adapter/session regression group**

Run:

```powershell
.\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailSessionTest"
```

Expected: PASS with native conversion, item click, lifecycle, and refresh behavior unchanged.

- [ ] **Step 7: Review the task diff without committing**

Run:

```powershell
git diff -- app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt
```

Confirm the diff contains only the settings-intent removal, status-summary removal, and their tests. Do not commit unless explicitly requested.

---

### Task 2: Apply Focus-Local Native List Decoration

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecorator.kt`
- Create: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecoratorTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt:135-180`
- Modify: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt:186-204`

**Interfaces:**
- Produces: `FocusNativeDetailViewDecorator.decorate(content: View): Boolean` for production; an internal deterministic test seam for list matching and child-attach cleanup.
- Behavior: returns `true` only when a matching list is found and decorated; returns `false` on no match or any decoration exception; never throws into native content binding.
- Consumes: the validated native `View` returned by `FocusNativeConvertOrInflate`.
- Child lifecycle: register an instance-local RecyclerView child-attach callback using the SystemUI list class loader; remove/replace the callback when the same list is redecorated; clean current and newly attached/recycled rows.

- [ ] **Step 1: Write failing decorator tests**

Create `FocusNativeDetailViewDecoratorTest.kt` with test-only recording views. The production change that makes these pass is the new instance-local decorator; deleting background clearing or scrollbar disabling must fail at least one assertion.

```kotlin
package com.banana.hypermodes.controlcenter

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FocusNativeDetailViewDecoratorTest {

    @Test
    fun `decorate clears list and host backgrounds and hides scrollbar only`() {
        val list = RecordingView()
        val host = RecordingViewGroup(listOf(list))
        val root = RecordingViewGroup(listOf(host))

        val decorated = FocusNativeDetailViewDecorator.decorate(root) { view -> view === list }

        assertTrue(decorated)
        assertTrue(list.backgroundCleared)
        assertTrue(host.backgroundCleared)
        assertFalse(root.backgroundCleared)
        assertFalse(list.verticalScrollbarEnabled)
        assertFalse(list.enabledWasChanged)
    }

    @Test
    fun `decorate is non fatal when native list is absent`() {
        val root = RecordingViewGroup(emptyList())

        val decorated = FocusNativeDetailViewDecorator.decorate(root) { false }

        assertFalse(decorated)
        assertFalse(root.backgroundCleared)
    }

    @Test
    fun `decorate is non fatal when hierarchy inspection fails`() {
        val root = RecordingViewGroup(emptyList())

        val decorated = FocusNativeDetailViewDecorator.decorate(root) {
            throw IllegalStateException("changed native hierarchy")
        }

        assertFalse(decorated)
    }

    private open class RecordingView : View(null) {
        var backgroundCleared = false
        var verticalScrollbarEnabled = true
        var enabledWasChanged = false

        override fun setBackground(background: Drawable?) {
            backgroundCleared = background == null
        }

        override fun setVerticalScrollBarEnabled(enabled: Boolean) {
            verticalScrollbarEnabled = enabled
        }

        override fun setEnabled(enabled: Boolean) {
            enabledWasChanged = true
            super.setEnabled(enabled)
        }
    }

    private class RecordingViewGroup(
        private val children: List<View>
    ) : ViewGroup(null) {
        var backgroundCleared = false

        override fun getChildCount(): Int = children.size
        override fun getChildAt(index: Int): View = children[index]
        override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) = Unit
        override fun generateDefaultLayoutParams(): LayoutParams = LayoutParams(0, 0)

        override fun setBackground(background: Drawable?) {
            backgroundCleared = background == null
        }
    }
}
```

If the Android API marks one recording override final at compile time, keep the behavioral assertions by moving that property write behind the decorator’s internal `ListDecorationTarget` data holder rather than weakening or deleting the assertion. Do not switch to source-text assertions.

- [ ] **Step 2: Run the new test class and verify RED**

Run:

```powershell
.\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusNativeDetailViewDecoratorTest"
```

Expected: compilation FAIL because `FocusNativeDetailViewDecorator` does not exist.

- [ ] **Step 3: Implement the minimal non-fatal decorator**

Create `FocusNativeDetailViewDecorator.kt`:

```kotlin
package com.banana.hypermodes.controlcenter

import android.view.View
import android.view.ViewGroup

internal object FocusNativeDetailViewDecorator {
    private const val RECYCLER_VIEW_CLASS = "androidx.recyclerview.widget.RecyclerView"

    fun decorate(content: View): Boolean {
        return decorate(content) { view ->
            classHierarchyContains(view.javaClass, RECYCLER_VIEW_CLASS)
        }
    }

    internal fun decorate(
        content: View,
        listMatcher: (View) -> Boolean
    ): Boolean {
        return runCatching {
            val target = findList(content, parent = null, listMatcher) ?: return false
            target.list.background = null
            target.host?.background = null
            target.list.isVerticalScrollBarEnabled = false
            true
        }.getOrDefault(false)
    }

    private fun findList(
        view: View,
        parent: View?,
        listMatcher: (View) -> Boolean
    ): ListTarget? {
        if (listMatcher(view)) return ListTarget(list = view, host = parent)
        val group = view as? ViewGroup ?: return null
        for (index in 0 until group.childCount) {
            findList(group.getChildAt(index), group, listMatcher)?.let { return it }
        }
        return null
    }

    private fun classHierarchyContains(clazz: Class<*>, expectedName: String): Boolean {
        var current: Class<*>? = clazz
        while (current != null) {
            if (current.name == expectedName) return true
            current = current.superclass
        }
        return false
    }

    private data class ListTarget(
        val list: View,
        val host: View?
    )
}
```

Use class-hierarchy names instead of `view is RecyclerView`: the SystemUI-owned RecyclerView may come from a different class loader than module dependencies.

- [ ] **Step 4: Run decorator tests and verify GREEN**

Run the Step 2 command again.

Expected: all decorator tests PASS. The root/content host outside the immediate list host remains untouched; scrollbar visibility changes without disabling the view.

- [ ] **Step 5: Integrate decoration into native binding**

In `FocusModeDetailSession.bindDetailView`, after validating that `content` is an instance of the resolved native class and is a `View`, invoke decoration before registering/submitting it:

```kotlin
FocusNativeDetailViewDecorator.decorate(content)
FocusNativeDetailRegistry.registerContent(content, this)
currentContent = WeakReference(content)
```

Do not branch on the Boolean result and do not report a native fallback stage. A missing internal list is a cosmetic compatibility degradation, not a content-creation failure.

- [ ] **Step 6: Strengthen the no-list binding regression**

Update `FocusModeDetailSessionTest.bindDetailView registers content and returns View` to use `RecordingDetailDiagnostic` and prove that a fake native content view with no matching RecyclerView remains native:

```kotlin
val diagnostic = RecordingDetailDiagnostic()
val session = FocusModeDetailSession(
    repository = createFakeRepository(),
    onDismiss = {},
    nativeDetailContentApi = api,
    diagnostic = diagnostic
)

val view = session.bindDetailView(
    context = android.app.Application(),
    convertView = null,
    parent = null
)

assertNotNull(view)
assertTrue(FocusNativeDetailRegistry.isFocusContent(view!!))
assertTrue(diagnostic.stages.isEmpty())
```

- [ ] **Step 7: Run all native-detail regression tests**

Run:

```powershell
.\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusNativeDetailViewDecoratorTest" --tests "com.banana.hypermodes.controlcenter.FocusCardTileClassesTest" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailSessionTest" --tests "com.banana.hypermodes.hook.ControlCenterNativeDetailFeatureSetTest"
```

Expected: PASS. No test should regress native class loading, item count, adapter mapping, specific height, callback installation, or hidden completion.

- [ ] **Step 8: Review the task diff without committing**

Run:

```powershell
git diff -- app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecorator.kt app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecoratorTest.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt
```

Confirm no global hook, resource override, scrolling disablement, or outer-panel background mutation was introduced. Do not commit unless explicitly requested.

---

### Task 3: Reuse Existing Mode Drawable Icons

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeIconResolver.kt`
- Create: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeIconResolverTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`

**Interfaces:**
- Produces: `FocusModeIconResolver(pluginContext: Context, moduleContext: Context)` with `resolve(mode: ModeConfig): Drawable` and a string-compatible overload for the existing card icon path.
- Consumes: `ModeConfig.statusIcon`, `ModeConfig.icon`, `ModeIconMapper`, module/plugin contexts, and native `SelectableItem.iconDrawable`.

- [ ] **Step 1: Add failing resolver tests**

Create tests for these exact behaviors:

```kotlin
@Test
fun `status drawable wins over mapped icon`() { /* statusIcon resource resolves; assert returned Drawable identity */ }

@Test
fun `mapped icon is used when status drawable is absent`() { /* statusIcon blank/missing; assert mapped Drawable */ }

@Test
fun `resolver never returns null when all icon resources fail`() { /* assert zen/info/transparent fallback */ }
```

Use recording Contexts that return distinct Drawable instances for requested resource IDs. Expected values must be hand-derived and must not call the resolver to build the expectation.

- [ ] **Step 2: Run resolver tests and verify RED**

Run:

```powershell
.\\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeIconResolverTest"
```

Expected: compile failure because `FocusModeIconResolver` does not exist.

- [ ] **Step 3: Implement minimal resolver**

Implement the exact priority from Global Constraints. Resolve named module resources with `getIdentifier`, try `moduleContext.getDrawable(id)` first, then `pluginContext.getDrawable(id)`, and use the final fallback chain without throwing.

- [ ] **Step 4: Run resolver tests GREEN**

Run the Step 2 command again. Expected: PASS.

- [ ] **Step 5: Add failing native-row icon test**

Extend `FocusModeDetailAdapterTest` with a test that injects a recording `(ModeConfig) -> Drawable` provider, binds native content, and asserts every row has the provider’s non-null Drawable while preserving title/tag/selected fields.

- [ ] **Step 6: Run the row test RED**

Run the single new test. Expected: FAIL because `buildSelectableItem` does not set `iconDrawable`.

- [ ] **Step 7: Wire provider into session and rows**

Pass the resolver/provider through `FocusModeDetailAdapter` into `FocusModeDetailSession`, then set:

```kotlin
setField(item, "iconDrawable", modeIconProvider(mode))
```

Keep this assignment Focus-session-local and leave native tint/size handling unchanged.

- [ ] **Step 8: Run row test and adapter/session regressions GREEN**

Run:

```powershell
.\\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailSessionTest"
```

Expected: PASS.

- [ ] **Step 9: Reuse resolver in Focus card path**

Replace the private duplicate Drawable mapping in `FocusCardTileProvider` with `FocusModeIconResolver`, preserving current card fallbacks and behavior. Do not change tile state semantics.

- [ ] **Step 10: Run icon and native-detail regression group**

Run:

```powershell
.\\gradlew.bat testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeIconResolverTest" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest" --tests "com.banana.hypermodes.controlcenter.FocusModeDetailSessionTest" --tests "com.banana.hypermodes.controlcenter.FocusNativeDetailViewDecoratorTest" --tests "com.banana.hypermodes.controlcenter.FocusCardTileClassesTest" --tests "com.banana.hypermodes.hook.ControlCenterNativeDetailFeatureSetTest"
```

Expected: PASS with no changes to height/LayoutParams behavior.

- [ ] **Step 11: Review diff without committing**

Run scoped `git diff --check` and inspect all Task 3 files. Confirm no global hook, height mutation, or icon tint/size override was added.

---

### Task 4: Build and Verify on the HyperOS Device

**Files:**
- Verify only; no planned source changes.

**Interfaces:**
- Consumes: Task 1’s simplified adapter/items, Task 2’s Focus-local decorator, and Task 3’s shared mode Drawable resolver.
- Produces: a device-verified native detail panel matching the approved design and clean diagnostic evidence.

- [ ] **Step 1: Run the full local unit suite**

Run:

```powershell
.\gradlew.bat testDebugUnitTest
```

Expected: `BUILD SUCCESSFUL` with no failed tests.

- [ ] **Step 2: Check changed-file formatting**

Run a scoped check so unrelated pre-existing whitespace in the historical plan does not mask this feature’s result:

```powershell
git diff --check -- app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailSession.kt app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeIconResolver.kt app/src/main/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecorator.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeIconResolverTest.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailSessionTest.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusNativeDetailViewDecoratorTest.kt
```

Expected: no output and exit code 0.

- [ ] **Step 3: Build and install the debug APK**

Run:

```powershell
.\gradlew.bat installDebug
```

Expected: APK installed successfully on the connected HyperOS device.

- [ ] **Step 4: Reload the module code safely**

Ask the user to clear logs and restart SystemUI, or obtain explicit approval before issuing a root command that terminates SystemUI. Installing the APK alone does not reload code already injected into the SystemUI process.

- [ ] **Step 5: Exercise the actual Focus detail UI**

Open Control Center, open the Focus card detail panel, scroll from the first mode to the last, select one mode, wait for the panel to close, and reopen it.

Visually confirm all literal acceptance criteria:

- outer glass rounded panel remains;
- bottom settings button is absent;
- inner dark rectangular list background is absent;
- vertical scrollbar is absent;
- rows display each mode's existing vector Drawable on the left and mode name on the right, with no `On` or `Off` text;
- existing content height behavior is unchanged;
- list still scrolls;
- selecting a row still closes the panel and refreshes the Focus card.

- [ ] **Step 6: Read clean runtime diagnostics**

Run:

```powershell
adb logcat -b all -d -v threadtime | Select-String -Pattern 'FocusModeDetailAdapter|ControlCenterCardHook|FocusNativeDetail|NATIVE_|QSDetailContent|hypermodes_focus|DetailPanelController: prepareShow' -CaseSensitive:$false | Select-Object -Last 1200
```

Expected evidence:

- `DetailPanelController: prepareShow hypermodes_focus` is present;
- no `NATIVE_API_UNAVAILABLE`, `NATIVE_CONVERT`, `NATIVE_ITEMS`, or `NATIVE_CALLBACK` fallback appears;
- no failed native-detail feature hook appears;
- clicking and closing produce the existing Focus listening/refresh lifecycle logs.

- [ ] **Step 7: Report verified outcomes and remaining unrelated warnings**

Report the exact test/build/install results, the visible device result, and any remaining log warnings. Do not claim completion if the screenshot still contains any of the four removed UI elements. Do not include or clean unrelated existing workspace changes.
