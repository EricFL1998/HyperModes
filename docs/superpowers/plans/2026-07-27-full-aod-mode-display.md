# Full-AOD Mode Display Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Display the active HyperModes icon and name throughout HyperOS Full-AOD at the same screen position as the lockscreen display while inheriting SystemUI's native Full-AOD animations and burn-in movement.

**Architecture:** Replace the ordinary miuiaod injection with a SystemUI-only coordinator. The lockscreen and Full-AOD use separate view instances built from one factory and one parsed display state; the Full-AOD instance is attached under SystemUI's native `aod_root_view`, whose existing membership in `KeyguardPanelViewController.animationViews` supplies native alpha, scale, transition, reset, and burn-in transforms.

**Tech Stack:** Kotlin 2.4.10, Android SDK 37/minSdk 35, Java 11, libxposed API 101.0.1, kotlinx.serialization, JUnit 4, Robolectric 4.16.1, HyperOS 3 SystemUI internals.

## Global Constraints

- Target only Xiaomi HyperOS 3 based on Android 16.
- Support Full-AOD only; ordinary black-background AOD is explicitly out of scope.
- Keep the existing lockscreen icon/name appearance and current bottom indication placement.
- Attach Full-AOD content only to SystemUI `com.android.keyguard.widget.AodView` / `R.id.aod_root_view`.
- Determine an actual Full-AOD session from `aodView.getTag(aodView.id) == true` after `MiuiDozeService.onDreamingStarted()` proceeds.
- Do not inject into `com.miui.aod.AODView`.
- Do not create a custom animation timer or register a parallel `FullAodStateListener`.
- Do not directly drive alpha, scale, or translation for the Full-AOD view; inherit those transforms from `aod_root_view`.
- Do not add the child mode display itself to `KeyguardPanelViewController.animationViews`, which would double-apply transforms inherited from its parent.
- Use libxposed protective exception mode for every new hook and always allow the original SystemUI method to run.
- Keep SystemUI object references weak and make repeated hook/lifecycle callbacks idempotent.
- Use the main UI thread for all View creation, attachment, positioning, refresh, and removal.
- If no valid lockscreen-to-AOD coordinate can be calculated, keep the Full-AOD copy non-visible for that session rather than guessing a fallback margin.
- Do not commit, amend, or push unless the user explicitly authorizes Git commits.

## File Structure

### Create

- `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayState.kt` — parse the global config into one display-only state.
- `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayViewFactory.kt` — create and bind the shared icon/name visual.
- `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayPositioner.kt` — pure lockscreen-screen-bounds to AOD-relative placement calculation.
- `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayCoordinator.kt` — own lockscreen/AOD weak references, receiver, coordinate cache, idempotent injection, refresh, and cleanup.
- `app/src/main/java/com/banana/hypermodes/hook/FullAodHook.kt` — connect `MiuiDozeService` lifecycle and the final SystemUI `AodView.GONE` boundary to the coordinator.
- `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayStateReaderTest.kt` — config-to-display-state tests.
- `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayViewFactoryTest.kt` — shared visual binding tests.
- `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayPositionerTest.kt` — coordinate validity and conversion tests.
- `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayCoordinatorTest.kt` — Full-AOD lifecycle and idempotency tests.
- `app/src/test/java/com/banana/hypermodes/hook/FullAodSignalTest.kt` — native Full-AOD tag interpretation tests.

### Modify

- `app/src/main/java/com/banana/hypermodes/hook/LockscreenHook.kt` — delegate view/state ownership to the coordinator and remove the custom Doze animation.
- `app/src/main/java/com/banana/hypermodes/XposedInit.kt` — construct the shared coordinator, install `FullAodHook` in SystemUI, and remove miuiaod routing.
- `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt` — remove the now-unused `AOD_PACKAGE` constant.
- `app/src/main/resources/META-INF/xposed/scope.list` — remove the `com.miui.aod` static scope.
- `docs/testing/integration-test-plan.md` — add the Full-AOD device verification matrix and diagnostic boundaries.

### Delete

- `app/src/main/java/com/banana/hypermodes/hook/AodPluginHook.kt` — obsolete ordinary-AOD root injection and visibility override.

---

### Task 1: Display State Reader

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayState.kt`
- Create: `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayStateReaderTest.kt`

**Interfaces:**
- Consumes: `ConfigParser.parseConfig(json: String): FullConfig`, `ModeIconMapper.getStatusBarIcon(emoji: String): String`, `Settings.Global` key `pixel_routines_full_config`.
- Produces: `data class ModeDisplayState(val name: String, val iconResName: String)`, `ModeDisplayStateReader.fromJson(json: String?): ModeDisplayState?`, and `ModeDisplayStateReader.read(context: Context): ModeDisplayState?`.

- [ ] **Step 1: Write the failing state-reader tests**

Create `ModeDisplayStateReaderTest.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModeDisplayStateReaderTest {

    @Test
    fun `active mode becomes display state and explicit status icon wins`() {
        val state = ModeDisplayStateReader.fromJson(configJson(
            activeModeId = "work",
            statusIcon = "ic_stat_star"
        ))

        assertEquals(ModeDisplayState("Work", "ic_stat_star"), state)
    }

    @Test
    fun `blank status icon falls back to mapped mode icon`() {
        val state = ModeDisplayStateReader.fromJson(configJson(
            activeModeId = "work",
            statusIcon = "   "
        ))

        assertEquals(ModeDisplayState("Work", "ic_stat_work"), state)
    }

    @Test
    fun `missing active mode returns null`() {
        assertNull(ModeDisplayStateReader.fromJson(configJson(
            activeModeId = "missing",
            statusIcon = "ic_stat_work"
        )))
    }

    @Test
    fun `blank and malformed configs return null`() {
        assertNull(ModeDisplayStateReader.fromJson(null))
        assertNull(ModeDisplayStateReader.fromJson("   "))
        assertNull(ModeDisplayStateReader.fromJson("{not-json"))
    }

    private fun configJson(activeModeId: String, statusIcon: String): String = """
        {
          "activeModeId": "$activeModeId",
          "modes": [
            {
              "id": "work",
              "name": "Work",
              "icon": "💼",
              "statusIcon": "$statusIcon",
              "type": "SCHEDULED",
              "notification": { "dndLevel": "PRIORITY", "allowedApps": [] },
              "display": {},
              "pausedApps": []
            }
          ]
        }
    """.trimIndent()
}
```

- [ ] **Step 2: Run the focused test and confirm the missing implementation**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayStateReaderTest"
```

Expected: FAIL during Kotlin compilation with unresolved references to `ModeDisplayStateReader` and `ModeDisplayState`.

- [ ] **Step 3: Implement the display-only state reader**

Create `ModeDisplayState.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import android.content.Context
import android.provider.Settings
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.systemserver.config.ConfigParser

data class ModeDisplayState(
    val name: String,
    val iconResName: String
)

object ModeDisplayStateReader {
    const val CONFIG_KEY = "pixel_routines_full_config"

    fun read(context: Context): ModeDisplayState? {
        val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
        return fromJson(json)
    }

    fun fromJson(json: String?): ModeDisplayState? {
        if (json.isNullOrBlank()) return null

        return runCatching {
            val config = ConfigParser.parseConfig(json)
            val activeMode = config.modes.firstOrNull { it.id == config.activeModeId }
                ?: return null
            val iconResName = activeMode.statusIcon
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?: ModeIconMapper.getStatusBarIcon(activeMode.icon)

            ModeDisplayState(
                name = activeMode.name,
                iconResName = iconResName
            )
        }.getOrNull()
    }
}
```

- [ ] **Step 4: Run the state-reader tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayStateReaderTest"
```

Expected: PASS for all four tests.

- [ ] **Step 5: Validate the task diff without committing**

Run:

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors; the two Task 1 implementation files are present alongside the already approved uncommitted spec and plan documents.

---

### Task 2: Shared Native-Style View Factory

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayViewFactory.kt`
- Create: `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayViewFactoryTest.kt`

**Interfaces:**
- Consumes: `ModeDisplayState` from Task 1 and module package constant `Protocol.MODULE_PACKAGE`.
- Produces: `ModeDisplayViewFactory.create(context: Context): LinearLayout`, `ModeDisplayViewFactory.bind(context: Context, view: LinearLayout, state: ModeDisplayState?)`, and tags `LOCKSCREEN_TAG` / `FULL_AOD_TAG`.

- [ ] **Step 1: Write failing factory tests for structure and state binding**

Create `ModeDisplayViewFactoryTest.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ModeDisplayViewFactoryTest {
    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun `factory creates the shared two-child native-style row`() {
        val view = ModeDisplayViewFactory.create(context)

        assertEquals(LinearLayout.HORIZONTAL, view.orientation)
        assertEquals(Gravity.CENTER, view.gravity)
        assertEquals(2, view.childCount)
        assertTrue(view.getChildAt(0) is ImageView)
        assertTrue(view.getChildAt(1) is TextView)
        assertEquals(Color.WHITE, (view.getChildAt(1) as TextView).currentTextColor)
        assertEquals(View.GONE, view.visibility)
        assertEquals(0f, view.translationX)
        assertEquals(0f, view.translationY)
        assertEquals(1f, view.alpha)
        assertEquals(1f, view.scaleX)
        assertEquals(1f, view.scaleY)
    }

    @Test
    fun `binding active state shows name and module drawable`() {
        val view = ModeDisplayViewFactory.create(context)

        ModeDisplayViewFactory.bind(
            context,
            view,
            ModeDisplayState(name = "Work", iconResName = "ic_stat_work")
        )

        val icon = view.getChildAt(0) as ImageView
        val label = view.getChildAt(1) as TextView
        assertEquals(View.VISIBLE, view.visibility)
        assertEquals(View.VISIBLE, icon.visibility)
        assertNotNull(icon.drawable)
        assertEquals("Work", label.text.toString())
    }

    @Test
    fun `missing drawable keeps name but hides icon`() {
        val view = ModeDisplayViewFactory.create(context)

        ModeDisplayViewFactory.bind(
            context,
            view,
            ModeDisplayState(name = "Unknown", iconResName = "missing_drawable")
        )

        assertEquals(View.VISIBLE, view.visibility)
        assertEquals(View.GONE, view.getChildAt(0).visibility)
        assertEquals("Unknown", (view.getChildAt(1) as TextView).text.toString())
    }

    @Test
    fun `binding no state hides the complete row`() {
        val view = ModeDisplayViewFactory.create(context)
        ModeDisplayViewFactory.bind(
            context,
            view,
            ModeDisplayState(name = "Work", iconResName = "ic_stat_work")
        )

        ModeDisplayViewFactory.bind(context, view, null)

        assertEquals(View.GONE, view.visibility)
        assertEquals("", (view.getChildAt(1) as TextView).text.toString())
    }
}
```

- [ ] **Step 2: Run the focused test and confirm the factory is absent**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayViewFactoryTest"
```

Expected: FAIL during Kotlin compilation with unresolved reference `ModeDisplayViewFactory`.

- [ ] **Step 3: Implement one shared factory without animation overrides**

Create `ModeDisplayViewFactory.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.banana.hypermodes.protocol.Protocol

object ModeDisplayViewFactory {
    const val LOCKSCREEN_TAG = "hypermodes_lockscreen_mode_display"
    const val FULL_AOD_TAG = "hypermodes_full_aod_mode_display"

    fun create(context: Context): LinearLayout {
        val verticalPadding = context.dp(4f)
        val iconSize = context.dp(18f)
        val iconEndMargin = context.dp(6f)

        return LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            id = View.generateViewId()
            visibility = View.GONE
            setPadding(0, verticalPadding, 0, verticalPadding)

            addView(ImageView(context).apply {
                layoutParams = LinearLayout.LayoutParams(iconSize, iconSize).apply {
                    marginEnd = iconEndMargin
                }
                scaleType = ImageView.ScaleType.FIT_CENTER
            })

            addView(TextView(context).apply {
                setTextColor(Color.WHITE)
                setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14f)
                typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            })
        }
    }

    fun bind(context: Context, view: LinearLayout, state: ModeDisplayState?) {
        val iconView = view.getChildAt(0) as ImageView
        val textView = view.getChildAt(1) as TextView

        if (state == null) {
            iconView.setImageDrawable(null)
            iconView.visibility = View.GONE
            textView.text = ""
            view.visibility = View.GONE
            return
        }

        val drawable = runCatching {
            val moduleContext = context.createPackageContext(
                Protocol.MODULE_PACKAGE,
                Context.CONTEXT_IGNORE_SECURITY
            )
            val iconResId = moduleContext.resources.getIdentifier(
                state.iconResName,
                "drawable",
                Protocol.MODULE_PACKAGE
            )
            if (iconResId == 0) null else moduleContext.getDrawable(iconResId)
        }.getOrNull()

        iconView.setImageDrawable(drawable)
        iconView.visibility = if (drawable == null) View.GONE else View.VISIBLE
        textView.text = state.name
        view.visibility = View.VISIBLE
    }

    private fun Context.dp(value: Float): Int = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        value,
        resources.displayMetrics
    ).toInt()
}
```

- [ ] **Step 4: Run the factory and state tests together**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayViewFactoryTest" --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayStateReaderTest"
```

Expected: PASS; the factory tests also confirm that the factory does not introduce custom alpha/scale/translation values.

- [ ] **Step 5: Validate the task diff without committing**

Run:

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors; Task 1 and Task 2 files appear as new files.

---

### Task 3: Pure Coordinate Conversion

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayPositioner.kt`
- Create: `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayPositionerTest.kt`

**Interfaces:**
- Consumes: screen-coordinate rectangles for the lockscreen view and the SystemUI AOD root.
- Produces: `DisplayBounds`, `DisplayPlacement`, and `ModeDisplayPositioner.calculate(lockscreen: DisplayBounds?, host: DisplayBounds?): DisplayPlacement?`.

- [ ] **Step 1: Write failing coordinate conversion tests**

Create `ModeDisplayPositionerTest.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ModeDisplayPositionerTest {

    @Test
    fun `screen bounds convert to host-relative placement`() {
        val result = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 420, y = 2100, width = 240, height = 56),
            host = DisplayBounds(x = 0, y = 100, width = 1080, height = 2300)
        )

        assertEquals(DisplayPlacement(x = 420, y = 2000, width = 240, height = 56), result)
    }

    @Test
    fun `host offset is removed from both axes`() {
        val result = ModeDisplayPositioner.calculate(
            lockscreen = DisplayBounds(x = 160, y = 360, width = 120, height = 40),
            host = DisplayBounds(x = 100, y = 200, width = 500, height = 500)
        )

        assertEquals(DisplayPlacement(x = 60, y = 160, width = 120, height = 40), result)
    }

    @Test
    fun `missing zero-sized and off-host bounds are rejected`() {
        assertNull(ModeDisplayPositioner.calculate(null, DisplayBounds(0, 0, 1080, 2400)))
        assertNull(ModeDisplayPositioner.calculate(
            DisplayBounds(0, 0, 0, 40),
            DisplayBounds(0, 0, 1080, 2400)
        ))
        assertNull(ModeDisplayPositioner.calculate(
            DisplayBounds(-1, 200, 100, 40),
            DisplayBounds(0, 0, 1080, 2400)
        ))
        assertNull(ModeDisplayPositioner.calculate(
            DisplayBounds(1000, 200, 100, 40),
            DisplayBounds(0, 0, 1080, 2400)
        ))
    }
}
```

- [ ] **Step 2: Run the focused test and confirm the geometry types are absent**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayPositionerTest"
```

Expected: FAIL during Kotlin compilation with unresolved references to `ModeDisplayPositioner`, `DisplayBounds`, and `DisplayPlacement`.

- [ ] **Step 3: Implement strict coordinate validation and conversion**

Create `ModeDisplayPositioner.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

data class DisplayBounds(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

data class DisplayPlacement(
    val x: Int,
    val y: Int,
    val width: Int,
    val height: Int
)

object ModeDisplayPositioner {
    fun calculate(
        lockscreen: DisplayBounds?,
        host: DisplayBounds?
    ): DisplayPlacement? {
        if (lockscreen == null || host == null) return null
        if (lockscreen.width <= 0 || lockscreen.height <= 0) return null
        if (host.width <= 0 || host.height <= 0) return null

        val relativeX = lockscreen.x - host.x
        val relativeY = lockscreen.y - host.y
        if (relativeX < 0 || relativeY < 0) return null
        if (relativeX + lockscreen.width > host.width) return null
        if (relativeY + lockscreen.height > host.height) return null

        return DisplayPlacement(
            x = relativeX,
            y = relativeY,
            width = lockscreen.width,
            height = lockscreen.height
        )
    }
}
```

- [ ] **Step 4: Run the pure position tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayPositionerTest"
```

Expected: PASS for all three tests.

- [ ] **Step 5: Validate the task diff without committing**

Run:

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors; the Task 3 production and test files are new.

---

### Task 4: SystemUI Mode Display Coordinator

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayCoordinator.kt`
- Create: `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayCoordinatorTest.kt`

**Interfaces:**
- Consumes: `ModeDisplayStateReader.read`, `ModeDisplayViewFactory.create/bind`, `ModeDisplayPositioner.calculate`, and `Protocol.ACTION_MODE_STATE`.
- Produces: `attachLockscreenDisplay(view: LinearLayout)`, `onFullAodStarted(root: FrameLayout, isFullAod: Boolean)`, `onFullAodStopped()`, and `refresh(context: Context)`.

- [ ] **Step 1: Write failing coordinator lifecycle tests**

Create `ModeDisplayCoordinatorTest.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import java.util.IdentityHashMap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ModeDisplayCoordinatorTest {
    private val context = RuntimeEnvironment.getApplication()
    private val bounds = IdentityHashMap<View, DisplayBounds>()
    private var state: ModeDisplayState? = ModeDisplayState("Work", "ic_stat_work")

    private fun coordinator() = ModeDisplayCoordinator(
        readState = { state },
        readBounds = { bounds[it] },
        logger = {}
    )

    @Test
    fun `full aod start adds one positioned tagged child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        val params = fullAod.layoutParams as FrameLayout.LayoutParams
        assertEquals(420, params.leftMargin)
        assertEquals(2000, params.topMargin)
        assertEquals(240, params.width)
        assertEquals(56, params.height)
        assertEquals("Work", (fullAod.getChildAt(1) as TextView).text.toString())
        assertEquals(View.VISIBLE, fullAod.visibility)
    }

    @Test
    fun `repeated start reuses the same child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)
        val first = root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG)
        coordinator.onFullAodStarted(root, isFullAod = true)
        val second = root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG)

        assertSame(first, second)
        assertEquals(1, root.childCount)
    }

    @Test
    fun `ordinary aod flag does not inject and stop removes active copy`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = false)
        assertNull(root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG))

        coordinator.onFullAodStarted(root, isFullAod = true)
        coordinator.onFullAodStopped()
        assertNull(root.findViewWithTag<View>(ModeDisplayViewFactory.FULL_AOD_TAG))
    }

    @Test
    fun `refresh updates both copies and hides them when mode stops`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)
        coordinator.onFullAodStarted(root, isFullAod = true)
        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)

        state = ModeDisplayState("Gaming", "ic_stat_game")
        coordinator.refresh(context)
        assertEquals("Gaming", (lockscreen.getChildAt(1) as TextView).text.toString())
        assertEquals("Gaming", (fullAod.getChildAt(1) as TextView).text.toString())

        state = null
        coordinator.refresh(context)
        assertEquals(View.GONE, lockscreen.visibility)
        assertEquals(View.GONE, fullAod.visibility)
    }

    @Test
    fun `missing coordinates never expose an unpositioned child`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        coordinator.attachLockscreenDisplay(lockscreen)

        coordinator.onFullAodStarted(root, isFullAod = true)

        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        assertEquals(View.INVISIBLE, fullAod.visibility)
    }

    @Test
    fun `child keeps neutral transforms and inherits movement from root only`() {
        val coordinator = coordinator()
        val lockscreen = ModeDisplayViewFactory.create(context)
        val root = FrameLayout(context)
        bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
        bounds[root] = DisplayBounds(0, 100, 1080, 2300)
        coordinator.attachLockscreenDisplay(lockscreen)
        coordinator.onFullAodStarted(root, isFullAod = true)
        val fullAod = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)

        root.translationY = 10f
        root.alpha = 0.6f
        root.scaleX = 0.95f
        root.scaleY = 0.95f

        assertSame(root, fullAod.parent)
        assertEquals(0f, fullAod.translationY)
        assertEquals(1f, fullAod.alpha)
        assertEquals(1f, fullAod.scaleX)
        assertEquals(1f, fullAod.scaleY)
    }
}
```

- [ ] **Step 2: Run the focused test and confirm the coordinator is absent**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinatorTest"
```

Expected: FAIL during Kotlin compilation with unresolved reference `ModeDisplayCoordinator`.

- [ ] **Step 3: Implement weak references, one receiver, cached bounds, and idempotent Full-AOD attachment**

Create `ModeDisplayCoordinator.kt`:

```kotlin
package com.banana.hypermodes.hook.modedisplay

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.banana.hypermodes.protocol.Protocol
import java.lang.ref.WeakReference

class ModeDisplayCoordinator(
    private val readState: (Context) -> ModeDisplayState? = ModeDisplayStateReader::read,
    private val readBounds: (View) -> DisplayBounds? = ::screenBounds,
    private val logger: (String) -> Unit
) {
    private var lockscreenRef = WeakReference<LinearLayout>(null)
    private var fullAodRef = WeakReference<LinearLayout>(null)
    private var fullAodRootRef = WeakReference<FrameLayout>(null)
    private var lastLockscreenBounds: DisplayBounds? = null
    private var fullAodPositioned = false
    private var receiverRegistered = false
    private var receiver: BroadcastReceiver? = null
    private var pendingRootRef = WeakReference<FrameLayout>(null)
    private var pendingPreDraw: ViewTreeObserver.OnPreDrawListener? = null

    fun attachLockscreenDisplay(view: LinearLayout) {
        val changed = lockscreenRef.get() !== view
        lockscreenRef = WeakReference(view)
        captureLockscreenBounds(view)
        if (changed) {
            view.addOnLayoutChangeListener { current, _, _, _, _, _, _, _, _ ->
                captureLockscreenBounds(current)
            }
        }
        ensureReceiverRegistered(view.context)
        refresh(view.context)
        logger("lockscreen attached: view=$view")
    }

    fun onFullAodStarted(root: FrameLayout, isFullAod: Boolean) {
        logger("dream start: fullAod=$isFullAod root=$root")
        if (!isFullAod) {
            removeFullAodView()
            return
        }

        ensureReceiverRegistered(root.context)
        removeFullAodViewFromDifferentRoot(root)

        val existing = root.findViewWithTag<LinearLayout>(ModeDisplayViewFactory.FULL_AOD_TAG)
        val display = existing ?: ModeDisplayViewFactory.create(root.context).also {
            it.tag = ModeDisplayViewFactory.FULL_AOD_TAG
            root.addView(
                it,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    Gravity.TOP or Gravity.START
                )
            )
            logger("full AOD display created")
        }

        fullAodRootRef = WeakReference(root)
        fullAodRef = WeakReference(display)
        fullAodPositioned = positionFullAod(root, display)
        if (!fullAodPositioned) {
            display.visibility = View.INVISIBLE
            scheduleOneShotPosition(root, display)
        }
        refresh(root.context)
    }

    fun onFullAodStopped() {
        logger("dream stop")
        removeFullAodView()
        lockscreenRef.get()?.let { refresh(it.context) }
    }

    fun refresh(context: Context) {
        val state = readState(context)
        lockscreenRef.get()?.let {
            ModeDisplayViewFactory.bind(it.context, it, state)
            captureLockscreenBounds(it)
        }
        fullAodRef.get()?.let {
            ModeDisplayViewFactory.bind(it.context, it, state)
            if (!fullAodPositioned && state != null) {
                it.visibility = View.INVISIBLE
            }
        }
        logger("display refresh: active=${state != null}")
    }

    private fun captureLockscreenBounds(view: View) {
        readBounds(view)?.let {
            lastLockscreenBounds = it
            logger("lockscreen bounds: $it")
        }
    }

    private fun positionFullAod(root: FrameLayout, display: LinearLayout): Boolean {
        lockscreenRef.get()?.let(::captureLockscreenBounds)
        val hostBounds = readBounds(root)
        val placement = ModeDisplayPositioner.calculate(lastLockscreenBounds, hostBounds)
        if (placement == null) {
            logger("full AOD placement unavailable: lock=$lastLockscreenBounds host=$hostBounds")
            return false
        }

        val params = (display.layoutParams as? FrameLayout.LayoutParams)
            ?: FrameLayout.LayoutParams(placement.width, placement.height)
        params.gravity = Gravity.TOP or Gravity.START
        params.leftMargin = placement.x
        params.topMargin = placement.y
        params.width = placement.width
        params.height = placement.height
        display.layoutParams = params
        fullAodPositioned = true
        ModeDisplayViewFactory.bind(display.context, display, readState(display.context))
        logger("full AOD placement: $placement")
        return true
    }

    private fun scheduleOneShotPosition(root: FrameLayout, display: LinearLayout) {
        clearPendingPreDraw()
        lateinit var listener: ViewTreeObserver.OnPreDrawListener
        listener = ViewTreeObserver.OnPreDrawListener {
            if (root.viewTreeObserver.isAlive) {
                root.viewTreeObserver.removeOnPreDrawListener(listener)
            }
            pendingPreDraw = null
            pendingRootRef = WeakReference(null)
            fullAodPositioned = positionFullAod(root, display)
            if (!fullAodPositioned) {
                display.visibility = View.INVISIBLE
            }
            true
        }
        pendingRootRef = WeakReference(root)
        pendingPreDraw = listener
        root.viewTreeObserver.addOnPreDrawListener(listener)
    }

    private fun removeFullAodViewFromDifferentRoot(root: FrameLayout) {
        val currentRoot = fullAodRootRef.get()
        if (currentRoot != null && currentRoot !== root) {
            removeFullAodView()
        }
    }

    private fun removeFullAodView() {
        clearPendingPreDraw()
        val display = fullAodRef.get()
        (display?.parent as? ViewGroup)?.removeView(display)
        fullAodRef = WeakReference(null)
        fullAodRootRef = WeakReference(null)
        fullAodPositioned = false
    }

    private fun clearPendingPreDraw() {
        val root = pendingRootRef.get()
        val listener = pendingPreDraw
        if (root != null && listener != null && root.viewTreeObserver.isAlive) {
            root.viewTreeObserver.removeOnPreDrawListener(listener)
        }
        pendingRootRef = WeakReference(null)
        pendingPreDraw = null
    }

    private fun ensureReceiverRegistered(context: Context) {
        if (receiverRegistered) return
        val appContext = context.applicationContext ?: context
        val stateReceiver = object : BroadcastReceiver() {
            override fun onReceive(receiveContext: Context, intent: Intent) {
                if (intent.action == Protocol.ACTION_MODE_STATE) {
                    refresh(receiveContext)
                }
            }
        }
        appContext.registerReceiver(
            stateReceiver,
            IntentFilter(Protocol.ACTION_MODE_STATE),
            Context.RECEIVER_EXPORTED
        )
        receiver = stateReceiver
        receiverRegistered = true
        logger("mode-state receiver registered")
    }

    companion object {
        private fun screenBounds(view: View): DisplayBounds? {
            if (!view.isLaidOut || view.width <= 0 || view.height <= 0) return null
            val location = IntArray(2)
            view.getLocationOnScreen(location)
            return DisplayBounds(
                x = location[0],
                y = location[1],
                width = view.width,
                height = view.height
            )
        }
    }
}
```

- [ ] **Step 4: Run coordinator tests and fix only implementation/test-environment mismatches**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinatorTest"
```

Expected: PASS for all six tests. If Robolectric queues a posted framework callback, drain only the main looper in the affected test with `shadowOf(Looper.getMainLooper()).idle()`; do not introduce production sleeps or custom timing.

- [ ] **Step 5: Run all mode-display unit tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.*"
```

Expected: PASS for state parsing, factory binding, positioning, lifecycle, idempotency, and neutral child transforms.

- [ ] **Step 6: Validate the task diff without committing**

Run:

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors; Tasks 1–4 add focused mode-display files without modifying existing production sources.

---

### Task 5: Refactor the Lockscreen Path onto the Shared Coordinator

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/LockscreenHook.kt:1-229`
- Test: `app/src/test/java/com/banana/hypermodes/hook/modedisplay/ModeDisplayCoordinatorTest.kt`

**Interfaces:**
- Consumes: `ModeDisplayCoordinator.attachLockscreenDisplay(view)` and `ModeDisplayViewFactory.create(context)` / `LOCKSCREEN_TAG`.
- Produces: the existing lockscreen injection with no duplicate view and no custom `DozeScrimController` animation.

- [ ] **Step 1: Add a failing regression assertion that Full-AOD start does not mutate the lockscreen row's animation properties**

Add this test to `ModeDisplayCoordinatorTest.kt`:

```kotlin
@Test
fun `full aod lifecycle leaves lockscreen animation properties to its native parent`() {
    val coordinator = coordinator()
    val lockscreen = ModeDisplayViewFactory.create(context)
    val root = FrameLayout(context)
    bounds[lockscreen] = DisplayBounds(420, 2100, 240, 56)
    bounds[root] = DisplayBounds(0, 100, 1080, 2300)
    coordinator.attachLockscreenDisplay(lockscreen)

    coordinator.onFullAodStarted(root, isFullAod = true)

    assertEquals(View.VISIBLE, lockscreen.visibility)
    assertEquals(1f, lockscreen.alpha)
    assertEquals(0f, lockscreen.translationY)
}
```

- [ ] **Step 2: Run the regression test before changing `LockscreenHook`**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinatorTest.full aod lifecycle leaves lockscreen animation properties to its native parent"
```

Expected: PASS at the coordinator layer. This locks the required behavior before removing the old hook-level 400 ms fade that violates it.

- [ ] **Step 3: Replace `LockscreenHook` view/state ownership with the coordinator**

Apply these structural changes to `LockscreenHook.kt`:

```kotlin
package com.banana.hypermodes.hook

import android.widget.LinearLayout
import com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinator
import com.banana.hypermodes.hook.modedisplay.ModeDisplayViewFactory
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

class LockscreenHook(
    private val module: XposedModule,
    private val coordinator: ModeDisplayCoordinator
) {
    private var isInstalled = false

    companion object {
        private const val TAG = "HyperModes.LockHook"
    }
```

Keep the existing protective hooks for:

```kotlin
com.android.keyguard.blueprint.KeyguardBottomAreaSection.bindData(
    androidx.constraintlayout.widget.ConstraintLayout
)

com.android.keyguard.injector.KeyguardBottomAreaInjector.updateIndicationTextLayoutParams()
```

Replace `injectModeDisplay(injector: Any)` with:

```kotlin
private fun injectModeDisplay(injector: Any) {
    val indicationArea = Reflect.getField(injector, "mIndicationArea") as? LinearLayout
    if (indicationArea == null) {
        log("mIndicationArea is null in injector $injector")
        return
    }

    val existing = indicationArea.findViewWithTag<LinearLayout>(
        ModeDisplayViewFactory.LOCKSCREEN_TAG
    )
    val display = existing ?: ModeDisplayViewFactory.create(indicationArea.context).also {
        it.tag = ModeDisplayViewFactory.LOCKSCREEN_TAG
        indicationArea.addView(it, 0)
        log("lockscreen display created")
    }

    coordinator.attachLockscreenDisplay(display)
}
```

Delete these old responsibilities from `LockscreenHook.kt`:

- `modeDisplayRef`;
- `isReceiverRegistered`;
- `hookDozeTransition()` and its invocation;
- local `createModeDisplayView()`;
- local `ensureReceiverRegistered()`;
- local `updateModeDisplay()`;
- imports used only by those deleted members (`BroadcastReceiver`, `Context`, `Intent`, `IntentFilter`, `Color`, `Settings`, `TypedValue`, `Gravity`, `View`, `ImageView`, `TextView`, `ModeIconMapper`, `Protocol`, `ConfigParser`, `WeakReference`).

Do not replace the removed Doze animation with another child-level alpha or visibility animation. HyperOS Full-AOD will fade `KeyguardBottomAreaView`; the new AOD copy inherits the separate native AOD host animation.

- [ ] **Step 4: Run lockscreen-related mode-display tests and compile production sources**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.modedisplay.*" :app:compileDebugKotlin
```

Expected: PASS and successful Kotlin compilation. No unresolved old imports or references to `hookDozeTransition` remain.

- [ ] **Step 5: Validate the task diff without committing**

Run:

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors; `LockscreenHook.kt` is the only modified pre-existing source file at this checkpoint.

---

### Task 6: Install the Native SystemUI Full-AOD Lifecycle Hook and Remove miuiaod Injection

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/FullAodHook.kt`
- Create: `app/src/test/java/com/banana/hypermodes/hook/FullAodSignalTest.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/XposedInit.kt:1-117`
- Modify: `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt:8-15`
- Modify: `app/src/main/resources/META-INF/xposed/scope.list:1-8`
- Delete: `app/src/main/java/com/banana/hypermodes/hook/AodPluginHook.kt`

**Interfaces:**
- Consumes: `ModeDisplayCoordinator.onFullAodStarted(root, isFullAod)` / `onFullAodStopped()`, `Reflect.getField`, and `HookUtils.getThisObject`.
- Produces: `FullAodSignal.isFullAod(root: View): Boolean`, protective observation hooks on SystemUI `MiuiDozeService.onDreamingStarted()` / `onDreamingStopped()`, and final cleanup after `DozeServiceHost.stopDozing()` hides the native `AodView`.

- [ ] **Step 1: Write failing tests for the exact native Full-AOD tag contract**

Create `FullAodSignalTest.kt`:

```kotlin
package com.banana.hypermodes.hook

import android.view.View
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class FullAodSignalTest {
    private val context = RuntimeEnvironment.getApplication()

    @Test
    fun `true tag stored under the root id means full aod`() {
        val root = View(context).apply {
            id = View.generateViewId()
            setTag(id, true)
        }

        assertTrue(FullAodSignal.isFullAod(root))
    }

    @Test
    fun `missing false wrong-type and no-id tags are rejected`() {
        val missing = View(context).apply { id = View.generateViewId() }
        val disabled = View(context).apply {
            id = View.generateViewId()
            setTag(id, false)
        }
        val wrongType = View(context).apply {
            id = View.generateViewId()
            setTag(id, "true")
        }
        val noId = View(context)

        assertFalse(FullAodSignal.isFullAod(missing))
        assertFalse(FullAodSignal.isFullAod(disabled))
        assertFalse(FullAodSignal.isFullAod(wrongType))
        assertFalse(FullAodSignal.isFullAod(noId))
    }
}
```

- [ ] **Step 2: Run the signal test and confirm the implementation is absent**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.FullAodSignalTest"
```

Expected: FAIL during Kotlin compilation with unresolved reference `FullAodSignal`.

- [ ] **Step 3: Implement the SystemUI lifecycle hook**

Create `FullAodHook.kt`:

```kotlin
package com.banana.hypermodes.hook

import android.view.View
import android.widget.FrameLayout
import com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinator
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

internal object FullAodSignal {
    fun isFullAod(root: View): Boolean {
        return root.id != View.NO_ID && root.getTag(root.id) == true
    }
}

class FullAodHook(
    private val module: XposedModule,
    private val coordinator: ModeDisplayCoordinator
) {
    private var isInstalled = false

    fun install(classLoader: ClassLoader) {
        if (isInstalled) return

        try {
            val serviceClass = classLoader.loadClass(
                "com.android.keyguard.doze.MiuiDozeService"
            )
            hookDreamingStarted(serviceClass)
            hookDreamingStopped(serviceClass)
            hookStopDozing(classLoader)
            isInstalled = true
            log("Full-AOD hooks installed")
        } catch (t: Throwable) {
            log("Full-AOD hook installation failed: ${t.message}")
        }
    }

    private fun hookDreamingStarted(serviceClass: Class<*>) {
        val method = serviceClass.getDeclaredMethod("onDreamingStarted")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val service = HookUtils.getThisObject(chain) ?: return result
                        val injector = Reflect.getField(service, "mDozeServiceHostInjector")
                            ?: return result
                        val root = Reflect.getField(injector, "mAodView") as? FrameLayout
                            ?: return result
                        val fullAod = FullAodSignal.isFullAod(root)
                        log("onDreamingStarted: fullAod=$fullAod root=$root")
                        coordinator.onFullAodStarted(root, fullAod)
                    } catch (t: Throwable) {
                        log("onDreamingStarted handling failed: ${t.message}")
                    }
                    return result
                }
            })
    }

    private fun hookDreamingStopped(serviceClass: Class<*>) {
        val method = serviceClass.getDeclaredMethod("onDreamingStopped")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    log("onDreamingStopped observed; waiting for native AOD host exit")
                    return result
                }
            })
    }

    private fun hookStopDozing(classLoader: ClassLoader) {
        val hostClass = classLoader.loadClass(
            "com.android.systemui.statusbar.phone.DozeServiceHost"
        )
        val method = hostClass.getDeclaredMethod("stopDozing")
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        // Native stopDozing has now hidden aod_root_view, so its exit
                        // transition is no longer using the child and cleanup is safe.
                        coordinator.onFullAodStopped()
                    } catch (t: Throwable) {
                        log("stopDozing cleanup failed: ${t.message}")
                    }
                    return result
                }
            })
    }

    private fun log(message: String) {
        module.log(android.util.Log.WARN, TAG, message)
    }

    companion object {
        private const val TAG = "HyperModes.FullAod"
    }
}
```

The start hook deliberately calls `chain.proceed()` first so HyperOS has already written the linkage and Full-AOD tags and passed the native AOD root into the plugin. `onDreamingStopped()` is observation-only; cleanup waits until native `DozeServiceHost.stopDozing()` has hidden `aod_root_view`, preserving the native wake/exit lifecycle. No hook targets the plugin's `com.miui.aod.AODView`.

- [ ] **Step 4: Run Full-AOD signal and coordinator tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.FullAodSignalTest" --tests "com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinatorTest"
```

Expected: PASS.

- [ ] **Step 5: Rewire `XposedInit` to one SystemUI coordinator**

Replace the AOD imports and lazy properties with:

```kotlin
import com.banana.hypermodes.hook.FullAodHook
import com.banana.hypermodes.hook.modedisplay.ModeDisplayCoordinator

private val modeDisplayCoordinator by lazy {
    ModeDisplayCoordinator { message ->
        log(Log.WARN, "HyperModes.ModeDisplay", message)
    }
}
private val lockscreenHook by lazy { LockscreenHook(this, modeDisplayCoordinator) }
private val fullAodHook by lazy { FullAodHook(this, modeDisplayCoordinator) }
```

In the `com.android.systemui` branch, install both hooks from the SystemUI classloader:

```kotlin
"com.android.systemui" -> {
    Log.e(TAG, "!!! com.android.systemui ready - hooking plugin loading")
    hookPluginLoading(param.classLoader)
    SystemUIHook(this).install(param.classLoader)
    lockscreenHook.install(param.classLoader)
    fullAodHook.install(param.classLoader)
}
```

Delete the `"com.miui.aod"` package branch. In `hookPluginLoading`, retain the `miui.systemui.plugin` branch for the Control Center, but delete the `else if (pkg == "com.miui.aod")` branch. Delete the `AodPluginHook` import and lazy property.

- [ ] **Step 6: Remove the obsolete ordinary-AOD source, constant, and static scope**

Delete:

```text
app/src/main/java/com/banana/hypermodes/hook/AodPluginHook.kt
```

Delete this line from `Protocol.kt`:

```kotlin
const val AOD_PACKAGE = "com.miui.aod"
```

Delete this line from `scope.list`:

```text
com.miui.aod
```

Leave `app/src/main/res/values/arrays.xml` unchanged because it already excludes `com.miui.aod`.

- [ ] **Step 7: Verify no old AOD injection references remain**

Use the dedicated search tool for:

```text
AodPluginHook|AOD_PACKAGE|com\.miui\.aod\.AODView|hypermodes_aod_display
```

Search under `app/src`. Expected: zero matches. A literal `com.miui.aod` match outside `app/src` in the decompiled reference tree is expected and must not be edited.

- [ ] **Step 8: Run focused tests and compile the app**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.FullAodSignalTest" --tests "com.banana.hypermodes.hook.modedisplay.*" :app:compileDebugKotlin
```

Expected: PASS and successful Kotlin compilation with only the SystemUI implementation path.

- [ ] **Step 9: Validate the task diff without committing**

Run:

```powershell
git diff --check
git status --short
```

Expected: no whitespace errors; the obsolete AOD source is deleted, the new hook is present, and the scope/entrypoint changes are visible.

---

### Task 7: Full Regression Build and Device Diagnostic Procedure

**Files:**
- Modify: `docs/testing/integration-test-plan.md`
- Verify: all files changed in Tasks 1–6

**Interfaces:**
- Consumes: log tags `HyperModes.LockHook`, `HyperModes.ModeDisplay`, and `HyperModes.FullAod`.
- Produces: repeatable manual verification for the exact Full-AOD state, host, coordinates, native movement, lifecycle cleanup, and mode refresh boundaries.

- [ ] **Step 1: Add a Full-AOD integration section to the test document**

Append this section to `docs/testing/integration-test-plan.md`:

```markdown
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
```

- [ ] **Step 2: Run the complete JVM test suite**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Expected: `BUILD SUCCESSFUL` with all existing and new unit tests passing.

- [ ] **Step 3: Assemble the debug APK**

Run:

```powershell
.\gradlew.bat :app:assembleDebug
```

Expected: `BUILD SUCCESSFUL` and APK at `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 4: Check the complete diff**

Run:

```powershell
git diff --check
git status --short
git diff --stat
```

Expected:

- no whitespace errors;
- no changes under `apk_decompiled`;
- no `AodPluginHook.kt` or `com.miui.aod` scope remains under `app/src`;
- the design and implementation plan remain uncommitted unless the user separately authorized commits.

- [ ] **Step 5: Perform device installation only after explicit authorization and a connected-device check**

First run:

```powershell
adb devices -l
```

If no authorized device is listed, report that device verification is blocked and do not claim Full-AOD success. If the user explicitly authorizes installation and an authorized device is listed, run:

```powershell
adb install -r "app\build\outputs\apk\debug\app-debug.apk"
```

Then reload the LSPosed module/SystemUI using the user's established safe device procedure and execute Test 8. Do not restart SystemUI or the device without explicit authorization.

- [ ] **Step 6: Report verification evidence without overstating unrun checks**

Report separately:

- JVM test result and test count;
- debug APK build result and path;
- whether `adb devices -l` found an authorized device;
- whether installation was authorized and completed;
- each observed Test 8 result;
- any missing device evidence as “not run,” not as passed.
