# HyperModes Rewrite Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Rewrite the HyperModes LSPosed module to drive HyperOS DeskClock's internal bedtime APIs (schedule editing + manual bedtime start/stop) per the approved spec.

**Architecture:** One APK, one Gradle module (`:app`). Hook half runs inside `com.android.deskclock` (Xposed entry `XposedInit` → `DeskClockHook` receiver → `BedtimeController` typed façade). App half is a Compose UI sending explicit broadcasts and showing per-step results.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), Xposed API 82 (`compileOnly`), JUnit 4 for pure-logic unit tests, AGP 9.2.1.

**Spec:** `docs/superpowers/specs/2026-07-20-hypermodes-rewrite-design.md`

## Global Constraints

- Internal DeskClock class names (verbatim from finding.md):
  - `com.android.deskclock.alarm.bedtime.BedtimeUtil`
  - `com.android.deskclock.alarm.bedtime.HealthDataUtil`
  - `com.android.deskclock.alarm.bedtime.MiHomeHelper`
  - `com.android.deskclock.util.AlarmHelper` (**`util` package, NOT `alarm.bedtime`**)
- Powerkeeper (no reflection, raw broadcast/provider only):
  - Wake broadcast: action `com.miui.powerkeeper_request_wake`, extra `reason` = `1`, package `com.miui.powerkeeper`
  - State: `ContentResolver.call("content://com.miui.powerkeeper.configure", "getSleepModeState", null, null)` → Bundle boolean `isInSleep`
- Every internal call is its own individually-caught step producing a `StepResult`; one failure must never abort later steps.
- Repeat-day bitmask: bit 0 = Monday … bit 6 = Sunday; `127` = every day.
- minSdk 35, compileSdk 37, Java 11. Commands below are for Windows PowerShell (`.\gradlew.bat`).
- **Deviation from spec §Communication (correctness fix):** both receivers must be `RECEIVER_EXPORTED` — sender and receiver live in different uids, so `NOT_EXPORTED` would silently block delivery. The *command* receiver (in DeskClock) is instead protected by a signature-level permission `com.banana.hypermodes.permission.CONTROL`; the *result* receiver (in our app) accepts spoofing risk (worst case: fake status text) because DeskClock cannot hold our signature permission.

---

### Task 1: Repository prep

**Files:**
- Create: `.gitignore` (repo root)
- Delete: `xposed/` (entire orphaned Gradle module directory)
- Delete: `app/src/test/java/com/banana/hypermodes/ExampleUnitTest.kt`
- Delete: `app/src/androidTest/java/com/banana/hypermodes/ExampleInstrumentedTest.kt`

**Interfaces:**
- Consumes: nothing.
- Produces: a git repo all later tasks commit into.

- [ ] **Step 1: Initialize git and write root .gitignore**

The project is not currently a git repository.

```bash
cd "e:/work/Android Project/HyperModes"
git init
```

Create `.gitignore` at the repo root:

```gitignore
*.iml
.gradle/
/local.properties
/.idea/
.DS_Store
/build/
/captures
.externalNativeBuild
.cxx
local.properties
**/build/
.kotlin/
```

- [ ] **Step 2: Delete orphaned and placeholder files**

```powershell
Remove-Item -Recurse -Force "e:/work/Android Project/HyperModes/xposed"
Remove-Item -Force "e:/work/Android Project/HyperModes/app/src/test/java/com/banana/hypermodes/ExampleUnitTest.kt"
Remove-Item -Force "e:/work/Android Project/HyperModes/app/src/androidTest/java/com/banana/hypermodes/ExampleInstrumentedTest.kt"
```

- [ ] **Step 3: Commit**

```bash
cd "e:/work/Android Project/HyperModes"
git add -A
git commit -m "chore: init git, remove orphaned xposed module and placeholder tests"
```

---

### Task 2: Shared broadcast protocol (TDD)

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt`
- Test: `app/src/test/java/com/banana/hypermodes/protocol/ProtocolTest.kt`

**Interfaces:**
- Consumes: nothing.
- Produces (used by Tasks 3–6): `Protocol.ACTION_APPLY_SCHEDULE`, `ACTION_START_BEDTIME`, `ACTION_STOP_BEDTIME`, `ACTION_QUERY_STATE`, `ACTION_RESULT`, `EXTRA_SLEEP_HOUR`, `EXTRA_SLEEP_MIN`, `EXTRA_WAKE_HOUR`, `EXTRA_WAKE_MIN`, `EXTRA_REPEAT_DAYS`, `EXTRA_STEPS`, `EXTRA_IN_SLEEP_MODE`, `MODULE_PACKAGE`, `TARGET_PACKAGE`, `PERMISSION_CONTROL`, `EVERY_DAY`, `daysToBitmask(Set<Int>): Int`, `bitmaskToDays(Int): Set<Int>`.

- [ ] **Step 1: Write the failing test**

Create `app/src/test/java/com/banana/hypermodes/protocol/ProtocolTest.kt`:

```kotlin
package com.banana.hypermodes.protocol

import org.junit.Assert.assertEquals
import org.junit.Test

class ProtocolTest {

    @Test
    fun `every day bitmask is 127`() {
        assertEquals(127, Protocol.EVERY_DAY)
    }

    @Test
    fun `single day encodes to its bit`() {
        assertEquals(1, Protocol.daysToBitmask(setOf(0)))   // Monday
        assertEquals(64, Protocol.daysToBitmask(setOf(6)))  // Sunday
    }

    @Test
    fun `weekdays encode to 31`() {
        assertEquals(31, Protocol.daysToBitmask(setOf(0, 1, 2, 3, 4)))
    }

    @Test
    fun `empty set encodes to 0`() {
        assertEquals(0, Protocol.daysToBitmask(emptySet()))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `out of range day throws`() {
        Protocol.daysToBitmask(setOf(7))
    }

    @Test
    fun `bitmask round-trips through days`() {
        val days = setOf(0, 2, 6)
        assertEquals(days, Protocol.bitmaskToDays(Protocol.daysToBitmask(days)))
    }

    @Test
    fun `bitmask 127 decodes to all seven days`() {
        assertEquals((0..6).toSet(), Protocol.bitmaskToDays(127))
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.protocol.ProtocolTest"`
Expected: FAIL — `Protocol` unresolved (compilation error).

- [ ] **Step 3: Write the implementation**

Create `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt`:

```kotlin
package com.banana.hypermodes.protocol

/**
 * Wire protocol shared by the manager UI (our process) and the LSPosed hook
 * (running inside com.android.deskclock). Pure constants + day-bitmask math;
 * no Android imports so it stays unit-testable on the JVM.
 */
object Protocol {
    const val MODULE_PACKAGE = "com.banana.hypermodes"
    const val TARGET_PACKAGE = "com.android.deskclock"

    /** Signature-level permission guarding the command receiver in DeskClock. */
    const val PERMISSION_CONTROL = "com.banana.hypermodes.permission.CONTROL"

    // App -> hook
    const val ACTION_APPLY_SCHEDULE = "com.banana.hypermodes.APPLY_SCHEDULE"
    const val ACTION_START_BEDTIME = "com.banana.hypermodes.START_BEDTIME"
    const val ACTION_STOP_BEDTIME = "com.banana.hypermodes.STOP_BEDTIME"
    const val ACTION_QUERY_STATE = "com.banana.hypermodes.QUERY_STATE"

    // Hook -> app
    const val ACTION_RESULT = "com.banana.hypermodes.RESULT"

    // Extras
    const val EXTRA_SLEEP_HOUR = "sleepHour"
    const val EXTRA_SLEEP_MIN = "sleepMin"
    const val EXTRA_WAKE_HOUR = "wakeHour"
    const val EXTRA_WAKE_MIN = "wakeMin"
    const val EXTRA_REPEAT_DAYS = "repeatDays"
    const val EXTRA_STEPS = "steps"
    const val EXTRA_IN_SLEEP_MODE = "inSleepMode"

    /** Bit 0 = Monday ... bit 6 = Sunday. 127 = every day. */
    const val EVERY_DAY = 0b1111111

    fun daysToBitmask(days: Set<Int>): Int {
        require(days.all { it in 0..6 }) { "day index out of range: $days" }
        return days.fold(0) { acc, day -> acc or (1 shl day) }
    }

    fun bitmaskToDays(bitmask: Int): Set<Int> =
        (0..6).filter { bitmask and (1 shl it) != 0 }.toSet()
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.protocol.ProtocolTest"`
Expected: PASS (7 tests).

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt app/src/test/java/com/banana/hypermodes/protocol/ProtocolTest.kt
git commit -m "feat: add shared broadcast protocol with day bitmask helpers"
```

---

### Task 3: BedtimeController — typed façade over DeskClock internals

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/StepResult.kt`
- Create: `app/src/main/java/com/banana/hypermodes/hook/BedtimeController.kt`
- Test: `app/src/test/java/com/banana/hypermodes/hook/StepResultTest.kt`

**Interfaces:**
- Consumes: nothing from other tasks (Xposed API only).
- Produces (used by Task 4):
  - `StepResult(name: String, success: Boolean, detail: String = "")`, `.format(): String`, `StepResult.ok(name)`, `StepResult.fail(name, Throwable)`, `StepResult.fail(name, String)`
  - `BedtimeController(context: Context, classLoader: ClassLoader)` with `applySchedule(sleepHour: Int, sleepMin: Int, wakeHour: Int, wakeMin: Int, repeatDays: Int): List<StepResult>`, `startBedtime(): List<StepResult>`, `stopBedtime(): List<StepResult>`, `querySleepModeState(): Boolean`

- [ ] **Step 1: Write the failing test**

Create `app/src/test/java/com/banana/hypermodes/hook/StepResultTest.kt`:

```kotlin
package com.banana.hypermodes.hook

import org.junit.Assert.assertEquals
import org.junit.Test

class StepResultTest {

    @Test
    fun `ok formats as name colon OK`() {
        assertEquals("setZenMode: OK", StepResult.ok("setZenMode").format())
    }

    @Test
    fun `fail with throwable formats message`() {
        val r = StepResult.fail("getSleepAlarm", IllegalStateException("no bedtime"))
        assertEquals("getSleepAlarm: FAIL: no bedtime", r.format())
    }

    @Test
    fun `fail with null message falls back to exception class name`() {
        val r = StepResult.fail("getSleepAlarm", IllegalStateException())
        assertEquals("getSleepAlarm: FAIL: IllegalStateException", r.format())
    }

    @Test
    fun `fail with plain detail string`() {
        val r = StepResult.fail("saveSleepAlarm", "skipped: alarm mutation failed")
        assertEquals("saveSleepAlarm: FAIL: skipped: alarm mutation failed", r.format())
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.StepResultTest"`
Expected: FAIL — `StepResult` unresolved.

- [ ] **Step 3: Implement StepResult**

Create `app/src/main/java/com/banana/hypermodes/hook/StepResult.kt`:

```kotlin
package com.banana.hypermodes.hook

/** Outcome of one internal-API step; formatted into the result broadcast. */
data class StepResult(val name: String, val success: Boolean, val detail: String = "") {
    fun format(): String = if (success) "$name: OK" else "$name: FAIL: $detail"

    companion object {
        fun ok(name: String) = StepResult(name, true)
        fun fail(name: String, e: Throwable) =
            StepResult(name, false, e.message ?: e.javaClass.simpleName)
        fun fail(name: String, detail: String) = StepResult(name, false, detail)
    }
}
```

- [ ] **Step 4: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.StepResultTest"`
Expected: PASS (4 tests).

- [ ] **Step 5: Implement BedtimeController**

Create `app/src/main/java/com/banana/hypermodes/hook/BedtimeController.kt`:

```kotlin
package com.banana.hypermodes.hook

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

/**
 * Typed façade over HyperOS DeskClock's internal bedtime APIs.
 * Runs INSIDE the com.android.deskclock process with its Context/ClassLoader.
 *
 * Class/method names per finding.md (decompiled HyperOS DeskClock).
 * Every step is individually caught and recorded — one renamed method must
 * never abort the remaining steps.
 */
class BedtimeController(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    companion object {
        private const val TAG = "HyperModes"

        private const val CLS_BEDTIME_UTIL = "com.android.deskclock.alarm.bedtime.BedtimeUtil"
        private const val CLS_HEALTH_DATA_UTIL = "com.android.deskclock.alarm.bedtime.HealthDataUtil"
        private const val CLS_MI_HOME_HELPER = "com.android.deskclock.alarm.bedtime.MiHomeHelper"
        private const val CLS_ALARM_HELPER = "com.android.deskclock.util.AlarmHelper"
        private const val CLS_DAYS_OF_WEEK = "com.android.deskclock.Alarm\$DaysOfWeek"

        // SleepModeUtil.exitSleepMode equivalent — sent raw, no reflection.
        private const val POWERKEEPER_PACKAGE = "com.miui.powerkeeper"
        private const val ACTION_REQUEST_WAKE = "com.miui.powerkeeper_request_wake"
        private const val EXTRA_REASON = "reason"
        private const val REASON_DESK_CLOCK = 1

        // SleepModeUtil.inSleepMode equivalent — direct provider call.
        private const val POWERKEEPER_PROVIDER = "content://com.miui.powerkeeper.configure"
        private const val METHOD_GET_SLEEP_MODE_STATE = "getSleepModeState"
        private const val KEY_IS_IN_SLEEP = "isInSleep"
    }

    private val bedtimeUtil by lazy { XposedHelpers.findClass(CLS_BEDTIME_UTIL, classLoader) }
    private val healthDataUtil by lazy { XposedHelpers.findClass(CLS_HEALTH_DATA_UTIL, classLoader) }
    private val miHomeHelper by lazy { XposedHelpers.findClass(CLS_MI_HOME_HELPER, classLoader) }
    private val alarmHelper by lazy { XposedHelpers.findClass(CLS_ALARM_HELPER, classLoader) }

    /** Current powerkeeper sleep-mode state; false on any error. */
    fun querySleepModeState(): Boolean = try {
        val bundle = context.contentResolver.call(
            Uri.parse(POWERKEEPER_PROVIDER), METHOD_GET_SLEEP_MODE_STATE, null, null
        )
        bundle?.getBoolean(KEY_IS_IN_SLEEP, false) ?: false
    } catch (t: Throwable) {
        XposedBridge.log("$TAG: querySleepModeState error: ${t.message}")
        false
    }

    /** Edit the persisted bedtime schedule (official getSleepAlarm/saveSleepAlarm flow). */
    fun applySchedule(
        sleepHour: Int, sleepMin: Int,
        wakeHour: Int, wakeMin: Int,
        repeatDays: Int
    ): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // 1. Fetch the existing sleep alarm. Never construct a new one —
        //    that would lose its database id and custom flags.
        val alarm: Any? = try {
            val a = XposedHelpers.callStaticMethod(bedtimeUtil, "getSleepAlarm", context)
            if (a == null) {
                results += StepResult.fail(
                    "getSleepAlarm", "returned null - create a bedtime in the Clock app first"
                )
            } else {
                results += StepResult.ok("getSleepAlarm")
            }
            a
        } catch (t: Throwable) {
            results += StepResult.fail("getSleepAlarm", t)
            null
        }

        // 2+3. Mutate and persist. Skipped entirely if there is no alarm;
        //      save is skipped if mutation failed (never persist a
        //      half-mutated Alarm).
        if (alarm != null) {
            if (mutateAlarm(alarm, sleepHour, sleepMin, repeatDays, results)) {
                runStep(results, "saveSleepAlarm") {
                    XposedHelpers.callStaticMethod(bedtimeUtil, "saveSleepAlarm", context, alarm)
                }
            } else {
                results += StepResult.fail("saveSleepAlarm", "skipped: alarm mutation failed")
            }
        }

        // 4+5. Mi Health sync — independent of the Alarm object, always runs.
        runStep(results, "updateSleepSchedule") {
            XposedHelpers.callStaticMethod(
                healthDataUtil, "updateSleepSchedule", context, sleepHour, sleepMin
            )
        }
        runStep(results, "updateWakeSchedule") {
            XposedHelpers.callStaticMethod(
                healthDataUtil, "updateWakeSchedule", context, wakeHour, wakeMin
            )
        }

        // 6. Reschedule the bedtime reminder notification.
        runStep(results, "setSleepNotification") {
            XposedHelpers.callStaticMethod(alarmHelper, "setSleepNotification", context)
        }

        // 7. Mi Home IoT ecosystem.
        notifyMiHome(results)

        return results
    }

    /** Manually enter bedtime mode, mimicking the official DeskClock sequence. */
    fun startBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        runStep(results, "setSleepNotification") {
            XposedHelpers.callStaticMethod(alarmHelper, "setSleepNotification", context)
        }

        // Zen Mode only when the user enabled DND integration in Bedtime settings.
        val dndEnabled: Boolean? = try {
            XposedHelpers.callStaticMethod(bedtimeUtil, "getDisturbanceState", context) as Boolean
        } catch (t: Throwable) {
            results += StepResult.fail("getDisturbanceState", t)
            null
        }
        when (dndEnabled) {
            true -> runStep(results, "setZenMode") {
                XposedHelpers.callStaticMethod(alarmHelper, "setZenMode", context)
            }
            false -> results += StepResult.ok("setZenMode (skipped: DND integration off in Clock settings)")
            null -> results += StepResult.fail("setZenMode", "skipped: disturbance state unknown")
        }

        notifyMiHome(results)
        return results
    }

    /** Exit bedtime mode: official powerkeeper wake broadcast + best-effort Zen exit. */
    fun stopBedtime(): List<StepResult> {
        val results = mutableListOf<StepResult>()

        // 1. Exit powerkeeper sleep mode — the exact Intent
        //    SleepModeUtil.exitSleepMode sends (no reflection dependency).
        runStep(results, "exitSleepMode (powerkeeper broadcast)") {
            context.sendBroadcast(Intent(ACTION_REQUEST_WAKE).apply {
                setPackage(POWERKEEPER_PACKAGE)
                putExtra(EXTRA_REASON, REASON_DESK_CLOCK)
            })
        }

        // 2. Exit Zen Mode — best-effort, with standard DND fallback.
        try {
            XposedHelpers.callStaticMethod(alarmHelper, "exitZenMode", context)
            results += StepResult.ok("exitZenMode")
        } catch (t: Throwable) {
            try {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
                results += StepResult.ok("exitZenMode (via NotificationManager fallback)")
            } catch (t2: Throwable) {
                results += StepResult.fail("exitZenMode", "${t.message}; fallback: ${t2.message}")
            }
        }

        // 3. Mi Home wake signal.
        notifyMiHome(results)
        return results
    }

    private fun notifyMiHome(results: MutableList<StepResult>) {
        runStep(results, "MiHomeHelper.notifyBedtimeChanged") {
            val helper = XposedHelpers.newInstance(miHomeHelper, context)
            XposedHelpers.callMethod(helper, "notifyBedtimeChanged")
        }
    }

    private fun runStep(results: MutableList<StepResult>, name: String, block: () -> Any?) {
        try {
            block()
            results += StepResult.ok(name)
        } catch (t: Throwable) {
            results += StepResult.fail(name, t)
            XposedBridge.log("$TAG: step $name failed: ${t.message}")
        }
    }

    /**
     * finding.md does not document Alarm's member names, so try AOSP DeskClock
     * names first (public int fields hour/minutes, daysOfWeek wrapper), then
     * setter methods. Every attempt is logged into the result for on-device
     * diagnosis without recompiling.
     */
    private fun mutateAlarm(
        alarm: Any, sleepHour: Int, sleepMin: Int, repeatDays: Int,
        results: MutableList<StepResult>
    ): Boolean {
        val tried = mutableListOf<String>()

        // Time: fields first, setters as fallback.
        try {
            XposedHelpers.setIntField(alarm, "hour", sleepHour)
            XposedHelpers.setIntField(alarm, "minutes", sleepMin)
            tried += "fields hour/minutes"
        } catch (t: Throwable) {
            try {
                XposedHelpers.callMethod(alarm, "setHour", sleepHour)
                XposedHelpers.callMethod(alarm, "setMinutes", sleepMin)
                tried += "setters setHour/setMinutes"
            } catch (t2: Throwable) {
                results += StepResult.fail("mutateAlarm", "time: ${t2.message}")
                return false
            }
        }

        // Days.
        try {
            setAlarmDays(alarm, repeatDays, tried)
        } catch (t: Throwable) {
            results += StepResult.fail("mutateAlarm", "days [${tried.joinToString()}]: ${t.message}")
            return false
        }

        results += StepResult.ok("mutateAlarm [${tried.joinToString()}]")
        return true
    }

    private fun setAlarmDays(alarm: Any, repeatDays: Int, tried: MutableList<String>) {
        // Variant 1: plain int field (Xiaomi may have flattened the AOSP wrapper).
        try {
            XposedHelpers.setIntField(alarm, "daysOfWeek", repeatDays)
            tried += "daysOfWeek as int field"
            return
        } catch (t: Throwable) {
            tried += "int field failed"
        }

        // Variant 2: AOSP Alarm.DaysOfWeek(int) wrapper assigned to the field.
        try {
            val dowClass = XposedHelpers.findClass(CLS_DAYS_OF_WEEK, alarm.javaClass.classLoader)
            val dow = XposedHelpers.newInstance(dowClass, repeatDays)
            XposedHelpers.setObjectField(alarm, "daysOfWeek", dow)
            tried += "DaysOfWeek wrapper field"
            return
        } catch (t: Throwable) {
            tried += "wrapper field failed"
        }

        // Variant 3: setter taking the wrapper.
        val dowClass = XposedHelpers.findClass(CLS_DAYS_OF_WEEK, alarm.javaClass.classLoader)
        val dow = XposedHelpers.newInstance(dowClass, repeatDays)
        XposedHelpers.callMethod(alarm, "setDaysOfWeek", dow)
        tried += "setDaysOfWeek(wrapper)"
    }
}
```

- [ ] **Step 6: Verify the module compiles**

Run: `.\gradlew.bat :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL (old `DeskClockHook.kt` still references nothing new; it will be deleted in Task 4 — if compilation of the old file interferes, delete `app/src/main/java/com/banana/hypermodes/DeskClockHook.kt` now as part of this step).

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/hook/ app/src/test/java/com/banana/hypermodes/hook/
git commit -m "feat: add BedtimeController facade over DeskClock internal bedtime APIs"
```

---

### Task 4: Hook wiring — XposedInit + DeskClockHook receiver

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/XposedInit.kt` (full rewrite)
- Create: `app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt`
- Delete: `app/src/main/java/com/banana/hypermodes/DeskClockHook.kt` (old guessed-API hook)

**Interfaces:**
- Consumes: `Protocol.*` (Task 2), `BedtimeController`, `StepResult` (Task 3).
- Produces: a module that, when DeskClock starts, listens for the four command actions and replies with `Protocol.ACTION_RESULT`.

- [ ] **Step 1: Rewrite XposedInit**

Replace the entire content of `app/src/main/java/com/banana/hypermodes/XposedInit.kt`:

```kotlin
package com.banana.hypermodes

import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.protocol.Protocol
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/** LSPosed entry point (listed in assets/xposed_init). Thin delegator only. */
class XposedInit : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: LoadPackageParam) {
        if (lpparam.packageName != Protocol.TARGET_PACKAGE) return
        try {
            DeskClockHook().install(lpparam)
            XposedBridge.log("HyperModes: hook installed for ${lpparam.packageName}")
        } catch (t: Throwable) {
            XposedBridge.log("HyperModes: failed to install hook: $t")
        }
    }
}
```

- [ ] **Step 2: Create DeskClockHook**

Create `app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt`:

```kotlin
package com.banana.hypermodes.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.banana.hypermodes.protocol.Protocol
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * Hooks DeskClock's Application.onCreate to register the command receiver
 * inside the DeskClock process, then delegates to BedtimeController.
 *
 * The receiver must be RECEIVER_EXPORTED (sender is our app, a different uid)
 * and is guarded by our signature-level permission so only our app can
 * trigger it.
 */
class DeskClockHook {

    fun install(lpparam: LoadPackageParam) {
        XposedHelpers.findAndHookMethod(
            Application::class.java.name, lpparam.classLoader, "onCreate",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val app = param.thisObject as Application
                    try {
                        registerReceiver(app, lpparam.classLoader)
                    } catch (t: Throwable) {
                        XposedBridge.log("HyperModes: receiver registration failed: $t")
                    }
                }
            }
        )
    }

    private fun registerReceiver(app: Application, classLoader: ClassLoader) {
        val controller = BedtimeController(app, classLoader)

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val results: List<StepResult> = when (intent.action) {
                    Protocol.ACTION_APPLY_SCHEDULE -> controller.applySchedule(
                        sleepHour = intent.getIntExtra(Protocol.EXTRA_SLEEP_HOUR, 22),
                        sleepMin = intent.getIntExtra(Protocol.EXTRA_SLEEP_MIN, 30),
                        wakeHour = intent.getIntExtra(Protocol.EXTRA_WAKE_HOUR, 7),
                        wakeMin = intent.getIntExtra(Protocol.EXTRA_WAKE_MIN, 30),
                        repeatDays = intent.getIntExtra(Protocol.EXTRA_REPEAT_DAYS, Protocol.EVERY_DAY)
                    )
                    Protocol.ACTION_START_BEDTIME -> controller.startBedtime()
                    Protocol.ACTION_STOP_BEDTIME -> controller.stopBedtime()
                    Protocol.ACTION_QUERY_STATE -> emptyList()
                    else -> return
                }
                XposedBridge.log(
                    "HyperModes: ${intent.action} -> ${results.joinToString { it.format() }}"
                )
                sendResult(app, results, controller.querySleepModeState())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_APPLY_SCHEDULE)
            addAction(Protocol.ACTION_START_BEDTIME)
            addAction(Protocol.ACTION_STOP_BEDTIME)
            addAction(Protocol.ACTION_QUERY_STATE)
        }
        app.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        XposedBridge.log("HyperModes: command receiver registered in DeskClock")
    }

    private fun sendResult(context: Context, results: List<StepResult>, inSleepMode: Boolean) {
        context.sendBroadcast(Intent(Protocol.ACTION_RESULT).apply {
            setPackage(Protocol.MODULE_PACKAGE)
            putExtra(Protocol.EXTRA_STEPS, results.map { it.format() }.toTypedArray())
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, inSleepMode)
        })
    }
}
```

- [ ] **Step 3: Delete the old hook**

```powershell
Remove-Item -Force "e:/work/Android Project/HyperModes/app/src/main/java/com/banana/hypermodes/DeskClockHook.kt"
```

- [ ] **Step 4: Verify the module compiles**

Run: `.\gradlew.bat :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL.

- [ ] **Step 5: Commit**

```bash
git add -A
git commit -m "feat: wire XposedInit to DeskClockHook command receiver with signature permission"
```

---

### Task 5: Manifest and resource cleanup

**Files:**
- Modify: `app/src/main/AndroidManifest.xml` (full rewrite)
- Modify: `app/src/main/res/values/arrays.xml`

**Interfaces:**
- Consumes: `Protocol.PERMISSION_CONTROL` value (Task 2) — must match the manifest `<permission>` name exactly.
- Produces: a manifest with no dangling references (`.ModeLoggerReceiver` removed), the signature permission, and `.ui.MainActivity` as launcher (created in Task 6 — build this task together with Task 6, or expect a manifest-merger error about the missing activity until Task 6 lands).

- [ ] **Step 1: Rewrite the manifest**

Replace the entire content of `app/src/main/AndroidManifest.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- Guards the command receiver inside DeskClock; only apps signed with
         our certificate (i.e. this app itself) can trigger it. -->
    <permission
        android:name="com.banana.hypermodes.permission.CONTROL"
        android:protectionLevel="signature" />
    <uses-permission android:name="com.banana.hypermodes.permission.CONTROL" />

    <uses-permission android:name="android.permission.ACCESS_NOTIFICATION_POLICY" />

    <queries>
        <package android:name="com.android.deskclock" />
    </queries>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:supportsRtl="true"
        android:theme="@style/Theme.HyperModes">

        <!-- LSPosed module metadata -->
        <meta-data
            android:name="xposedmodule"
            android:value="true" />
        <meta-data
            android:name="xposeddescription"
            android:value="HyperOS Bedtime control - edit the DeskClock bedtime schedule and manually start/stop bedtime mode with full Mi Health / Mi Home / Zen Mode sync" />
        <meta-data
            android:name="xposedminversion"
            android:value="93" />
        <meta-data
            android:name="xposedscope"
            android:resource="@array/xposed_scope" />

        <activity
            android:name=".ui.MainActivity"
            android:exported="true"
            android:label="@string/app_name"
            android:theme="@style/Theme.HyperModes">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>

</manifest>
```

- [ ] **Step 2: Trim the Xposed scope array to DeskClock only**

Replace the entire content of `app/src/main/res/values/arrays.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string-array name="xposed_scope">
        <item>com.android.deskclock</item>
    </string-array>
</resources>
```

- [ ] **Step 3: Commit**

```bash
git add app/src/main/AndroidManifest.xml app/src/main/res/values/arrays.xml
git commit -m "chore: clean manifest (drop phantom receiver and dead permissions), trim scope"
```

Note: a full build at this point fails on the missing `.ui.MainActivity` — that is expected and fixed in Task 6. No build step in this task.

---

### Task 6: Compose UI rewrite

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/ui/MainActivity.kt`
- Delete: `app/src/main/java/com/banana/hypermodes/MainActivity.kt`

**Interfaces:**
- Consumes: `Protocol.*` (Task 2); step-line format `"name: OK"` / `"name: FAIL: detail"` from `StepResult.format()` (Task 3).
- Produces: the manager UI — the user-facing half of the app.

- [ ] **Step 1: Create the new MainActivity**

Create `app/src/main/java/com/banana/hypermodes/ui/MainActivity.kt`:

```kotlin
package com.banana.hypermodes.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.theme.HyperModesTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HyperModesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

private const val NO_RESPONSE_MESSAGE =
    "No response from module — is it enabled in LSPosed with DeskClock scope, " +
        "and has DeskClock been (force-)started since?"

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var sleepHour by remember { mutableIntStateOf(22) }
    var sleepMin by remember { mutableIntStateOf(30) }
    var wakeHour by remember { mutableIntStateOf(7) }
    var wakeMin by remember { mutableIntStateOf(30) }
    var days by remember { mutableStateOf((0..6).toSet()) }
    var stepLines by remember { mutableStateOf<List<String>>(emptyList()) }
    var inSleepMode by remember { mutableStateOf<Boolean?>(null) }
    var awaitingResult by remember { mutableStateOf(false) }

    // Per-step results from the hook (running in the DeskClock process).
    // EXPORTED because the sender is DeskClock's uid, which cannot hold our
    // signature permission; worst case a spoofed broadcast fakes status text.
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                @Suppress("DEPRECATION")
                stepLines =
                    intent.getStringArrayExtra(Protocol.EXTRA_STEPS)?.toList() ?: emptyList()
                inSleepMode = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)
                awaitingResult = false
            }
        }
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter(Protocol.ACTION_RESULT),
            ContextCompat.RECEIVER_EXPORTED
        )
        onDispose { context.unregisterReceiver(receiver) }
    }

    // Timeout so a dead module doesn't leave the UI spinning forever.
    LaunchedEffect(awaitingResult) {
        if (awaitingResult) {
            delay(3000)
            if (awaitingResult) {
                awaitingResult = false
                stepLines = listOf(NO_RESPONSE_MESSAGE)
            }
        }
    }

    fun send(action: String, configure: Intent.() -> Unit = {}) {
        awaitingResult = true
        context.sendBroadcast(Intent(action).apply {
            setPackage(Protocol.TARGET_PACKAGE)
            configure()
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("HyperModes", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Bedtime Control",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = when (inSleepMode) {
                true -> "Sleep mode: ON"
                false -> "Sleep mode: OFF"
                null -> "Sleep mode: unknown"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = when (inSleepMode) {
                true -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // ---- Schedule editor ----
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sleep time", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPicker(sleepHour, 0..23) { sleepHour = it }
                    Text(":", style = MaterialTheme.typography.displayMedium)
                    NumberPicker(sleepMin, 0..59) { sleepMin = it }
                }
                Spacer(Modifier.height(8.dp))
                Text("Wake time", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPicker(wakeHour, 0..23) { wakeHour = it }
                    Text(":", style = MaterialTheme.typography.displayMedium)
                    NumberPicker(wakeMin, 0..59) { wakeMin = it }
                }
                Spacer(Modifier.height(8.dp))
                DaySelector(days) { days = it }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        send(Protocol.ACTION_APPLY_SCHEDULE) {
                            putExtra(Protocol.EXTRA_SLEEP_HOUR, sleepHour)
                            putExtra(Protocol.EXTRA_SLEEP_MIN, sleepMin)
                            putExtra(Protocol.EXTRA_WAKE_HOUR, wakeHour)
                            putExtra(Protocol.EXTRA_WAKE_MIN, wakeMin)
                            putExtra(Protocol.EXTRA_REPEAT_DAYS, Protocol.daysToBitmask(days))
                        }
                    },
                    enabled = !awaitingResult && days.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("APPLY SCHEDULE") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Manual control ----
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Manual control", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { send(Protocol.ACTION_STOP_BEDTIME) },
                        enabled = !awaitingResult
                    ) { Text("Stop bedtime") }
                    Button(
                        onClick = { send(Protocol.ACTION_START_BEDTIME) },
                        enabled = !awaitingResult
                    ) { Text("Start bedtime now") }
                }
                TextButton(
                    onClick = { send(Protocol.ACTION_QUERY_STATE) },
                    enabled = !awaitingResult
                ) { Text("Refresh state") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Status ----
        if (awaitingResult) {
            Text("⏳ Waiting for module…", style = MaterialTheme.typography.bodyLarge)
        } else if (stepLines.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Last result", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    stepLines.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                "FAIL" in line -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Requires LSPosed — enable the module with DeskClock scope, then force-stop DeskClock once.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DaySelector(selected: Set<Int>, onChange: (Set<Int>) -> Unit) {
    val labels = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        labels.forEachIndexed { index, label ->
            FilterChip(
                selected = index in selected,
                onClick = {
                    onChange(if (index in selected) selected - index else selected + index)
                },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun NumberPicker(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = {
            onValueChange(if (value >= range.last) range.first else value + 1)
        }) {
            Text("▲", fontSize = 24.sp)
        }
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.displaySmall
        )
        IconButton(onClick = {
            onValueChange(if (value <= range.first) range.last else value - 1)
        }) {
            Text("▼", fontSize = 24.sp)
        }
    }
}
```

- [ ] **Step 2: Delete the old MainActivity**

```powershell
Remove-Item -Force "e:/work/Android Project/HyperModes/app/src/main/java/com/banana/hypermodes/MainActivity.kt"
```

- [ ] **Step 3: Build the full APK**

Run: `.\gradlew.bat :app:assembleDebug`
Expected: BUILD SUCCESSFUL; APK at `app/build/outputs/apk/debug/app-debug.apk`.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "feat: rewrite manager UI with day selector, manual control, and per-step status"
```

---

### Task 7: Final verification

**Files:**
- Modify: nothing (docs only if issues are found)

**Interfaces:**
- Consumes: everything.

- [ ] **Step 1: Full clean build + unit tests**

Run: `.\gradlew.bat clean :app:assembleDebug :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL; 11 unit tests pass (7 ProtocolTest + 4 StepResultTest).

- [ ] **Step 2: Verify Xposed packaging survived the rewrite**

Run:
```bash
cd "e:/work/Android Project/HyperModes"
tar -tf app/build/outputs/apk/debug/app-debug.apk 2>/dev/null | grep -iE "xposed_init|xposed/" || unzip -l app/build/outputs/apk/debug/app-debug.apk | grep -iE "xposed_init|xposed/"
```
Expected: `assets/xposed_init` and `META-INF/xposed/` entries present. Also confirm `assets/xposed_init` content is still `com.banana.hypermodes.XposedInit` (that class still exists at the root package, so no change needed).

- [ ] **Step 3: On-device checklist (manual — requires the LSPosed device)**

Hand this list to the user; it cannot be automated from the dev machine:

1. Install `app-debug.apk`, enable in LSPosed with scope = DeskClock, force-stop DeskClock.
2. Open HyperModes → tap **Refresh state** → state line matches reality.
3. **Apply schedule** with new times + weekday subset → status card all OK → verify Clock app bedtime UI and Mi Health show the new schedule.
4. **Start bedtime now** → DND turns on (if enabled in Clock bedtime settings), Mi Home devices react, status card all OK.
5. **Stop bedtime** → sleep mode exits, DND off.
6. If `mutateAlarm` FAILs: the status line lists which field/setter names were tried — capture it (or `XposedBridge` log) to pin down Xiaomi's actual `Alarm` member names.

- [ ] **Step 4: Commit**

```bash
git add -A
git commit -m "chore: final verification pass"
```

---

### Task 8: Migrate to libxposed API 101 (modern LSPosed API)

**Files:**
- Modify: `app/build.gradle.kts` (swap Xposed dependency)
- Modify: `app/src/main/java/com/banana/hypermodes/XposedInit.kt` (full rewrite)
- Modify: `app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt` (full rewrite)
- Modify: `app/src/main/java/com/banana/hypermodes/hook/BedtimeController.kt` (replace XposedHelpers/XposedBridge with Reflect + logger)
- Create: `app/src/main/java/com/banana/hypermodes/hook/Reflect.kt`
- Test: `app/src/test/java/com/banana/hypermodes/hook/ReflectTest.kt`
- Modify: `app/src/main/resources/META-INF/xposed/module.prop` (`targetApiVersion=101`)
- Modify: `app/src/main/AndroidManifest.xml` (drop legacy meta-data, add `android:description`)
- Modify: `app/src/main/res/values/strings.xml` (add `xposed_description`)
- Delete: `app/src/main/assets/xposed_init`

**Interfaces:**
- Consumes: `Protocol.*` (Task 2), `StepResult` (Task 3).
- Produces:
  - `Reflect.findClass(name: String, classLoader: ClassLoader): Class<*>`, `Reflect.callStatic(clazz: Class<*>, name: String, vararg args: Any?): Any?`, `Reflect.call(instance: Any, name: String, vararg args: Any?): Any?`, `Reflect.newInstance(clazz: Class<*>, vararg args: Any?): Any`, `Reflect.setIntField(instance: Any, name: String, value: Int)`, `Reflect.setObjectField(instance: Any, name: String, value: Any?)`
  - `BedtimeController(context: Context, classLoader: ClassLoader, log: (String) -> Unit)` — same four public operations as before.
  - `DeskClockHook(module: XposedModule)` with `install(classLoader: ClassLoader)`.

**Context for the implementer:** API 101 facts verified against the published sources jar (`io.github.libxposed:api:101.0.1`):
- Entry: `abstract class XposedModule : XposedInterfaceWrapper, XposedModuleInterface`; override `onPackageReady(param: XposedModuleInterface.PackageReadyParam)` (gives `getPackageName()`, `getClassLoader()`).
- Hooking: `module.hook(executable).setExceptionMode(...).intercept(hooker)`; `XposedInterface.Hooker.intercept(chain: XposedInterface.Chain): Any?` — call `chain.proceed()` to run the original; `chain.getThisObject()`.
- Logging: `module.log(priority: Int, tag: String?, msg: String)` and an overload with `Throwable`. No `XposedBridge`, no `XposedHelpers`.

- [ ] **Step 1: Swap the Gradle dependency**

In `app/build.gradle.kts`, replace both `de.robv.android.xposed:api:82` lines with:

```kotlin
    compileOnly("io.github.libxposed:api:101.0.1")
    compileOnly("io.github.libxposed:api:101.0.1:sources")
```

- [ ] **Step 2: Write the failing Reflect test**

Create `app/src/test/java/com/banana/hypermodes/hook/ReflectTest.kt`:

```kotlin
package com.banana.hypermodes.hook

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class ReflectTest {

    @Suppress("unused")
    open class Base {
        var baseField: Int = 1
    }

    @Suppress("unused")
    class Fixture : Base() {
        var hour: Int = 0
        var label: Any? = null
        private var secret: Int = 7

        fun setHour(value: Int) {
            hour = value
        }

        fun greet(prefix: String, times: Int): String = prefix.repeat(times)

        fun secretValue(): Int = secret

        class WithCtor(val ctx: String, val count: Int)

        companion object {
            @JvmStatic
            fun add(a: Int, b: Int): Int = a + b

            @JvmStatic
            fun describe(ctx: Appendable?): String = if (ctx == null) "null-ok" else "non-null"

            @JvmStatic
            fun noArgs(): String = "none"
        }
    }

    @Test
    fun `callStatic resolves int parameters with boxed args`() {
        assertEquals(5, Reflect.callStatic(Fixture::class.java, "add", 2, 3))
    }

    @Test
    fun `callStatic with no args`() {
        assertEquals("none", Reflect.callStatic(Fixture::class.java, "noArgs"))
    }

    @Test
    fun `callStatic matches null arg to reference parameter`() {
        assertEquals("null-ok", Reflect.callStatic(Fixture::class.java, "describe", null))
    }

    @Test
    fun `callStatic throws NoSuchMethodException for unknown name`() {
        assertThrows(NoSuchMethodException::class.java) {
            Reflect.callStatic(Fixture::class.java, "missing")
        }
    }

    @Test
    fun `call invokes instance method with mixed args`() {
        assertEquals("abab", Reflect.call(Fixture(), "greet", "ab", 2))
    }

    @Test
    fun `call works for setter style methods`() {
        val f = Fixture()
        Reflect.call(f, "setHour", 42)
        assertEquals(42, f.hour)
    }

    @Test
    fun `setIntField writes private fields`() {
        val f = Fixture()
        Reflect.setIntField(f, "secret", 99)
        assertEquals(99, f.secretValue())
    }

    @Test
    fun `setIntField walks superclasses`() {
        val f = Fixture()
        Reflect.setIntField(f, "baseField", 9)
        assertEquals(9, f.baseField)
    }

    @Test
    fun `setObjectField writes reference fields`() {
        val f = Fixture()
        Reflect.setObjectField(f, "label", "x")
        assertEquals("x", f.label)
    }

    @Test
    fun `newInstance matches constructor by arg types`() {
        val o = Reflect.newInstance(Fixture.WithCtor::class.java, "a", 3) as Fixture.WithCtor
        assertEquals("a", o.ctx)
        assertEquals(3, o.count)
    }

    @Test
    fun `findClass loads via given classloader`() {
        assertEquals(
            String::class.java,
            Reflect.findClass("java.lang.String", javaClass.classLoader)
        )
    }
}
```

- [ ] **Step 3: Run test to verify it fails**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ReflectTest"`
Expected: FAIL — `Reflect` unresolved.

- [ ] **Step 4: Implement Reflect**

Create `app/src/main/java/com/banana/hypermodes/hook/Reflect.kt`:

```kotlin
package com.banana.hypermodes.hook

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Minimal reflection helpers replacing XposedHelpers (not shipped in
 * libxposed API 101). Lookups walk superclasses and match parameters by
 * assignability with primitive/wrapper equivalence; null matches any
 * reference type.
 */
internal object Reflect {

    fun findClass(name: String, classLoader: ClassLoader): Class<*> =
        Class.forName(name, false, classLoader)

    fun callStatic(clazz: Class<*>, name: String, vararg args: Any?): Any? {
        val method = findMethod(clazz, name, args)
        method.isAccessible = true
        return method.invoke(null, *args)
    }

    fun call(instance: Any, name: String, vararg args: Any?): Any? {
        val method = findMethod(instance.javaClass, name, args)
        method.isAccessible = true
        return method.invoke(instance, *args)
    }

    fun newInstance(clazz: Class<*>, vararg args: Any?): Any {
        val ctor = findConstructor(clazz, args)
        ctor.isAccessible = true
        return ctor.newInstance(*args)
    }

    fun setIntField(instance: Any, name: String, value: Int) {
        val field = findField(instance.javaClass, name)
        field.isAccessible = true
        field.setInt(instance, value)
    }

    fun setObjectField(instance: Any, name: String, value: Any?) {
        val field = findField(instance.javaClass, name)
        field.isAccessible = true
        field.set(instance, value)
    }

    private fun findMethod(clazz: Class<*>, name: String, args: Array<out Any?>): Method {
        var c: Class<*>? = clazz
        while (c != null) {
            c.declaredMethods
                .firstOrNull { it.name == name && paramsMatch(it.parameterTypes, args) }
                ?.let { return it }
            c = c.superclass
        }
        throw NoSuchMethodException(
            "${clazz.name}.$name(${args.joinToString { it?.javaClass?.name ?: "null" }})"
        )
    }

    private fun findConstructor(clazz: Class<*>, args: Array<out Any?>): Constructor<*> =
        clazz.declaredConstructors.firstOrNull { paramsMatch(it.parameterTypes, args) }
            ?: throw NoSuchMethodException(
                "${clazz.name}<init>(${args.joinToString { it?.javaClass?.name ?: "null" }})"
            )

    private fun findField(clazz: Class<*>, name: String): Field {
        var c: Class<*>? = clazz
        while (c != null) {
            try {
                return c.getDeclaredField(name)
            } catch (_: NoSuchFieldException) {
                c = c.superclass
            }
        }
        throw NoSuchFieldException("${clazz.name}.$name")
    }

    private fun paramsMatch(types: Array<Class<*>>, args: Array<out Any?>): Boolean {
        if (types.size != args.size) return false
        return types.indices.all { i ->
            val arg = args[i] ?: return@all !types[i].isPrimitive
            matches(types[i], arg.javaClass)
        }
    }

    private fun matches(param: Class<*>, arg: Class<*>): Boolean {
        if (param.isAssignableFrom(arg)) return true
        if (!param.isPrimitive) return false
        val boxed: Class<*> = when (param) {
            java.lang.Integer.TYPE -> java.lang.Integer::class.java
            java.lang.Long.TYPE -> java.lang.Long::class.java
            java.lang.Boolean.TYPE -> java.lang.Boolean::class.java
            java.lang.Double.TYPE -> java.lang.Double::class.java
            java.lang.Float.TYPE -> java.lang.Float::class.java
            java.lang.Short.TYPE -> java.lang.Short::class.java
            java.lang.Byte.TYPE -> java.lang.Byte::class.java
            Character.TYPE -> java.lang.Character::class.java
            else -> return false
        }
        return arg == boxed
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

Run: `.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ReflectTest"`
Expected: PASS (11 tests).

- [ ] **Step 6: Rewrite BedtimeController onto Reflect + logger**

Apply these mechanical replacements throughout `app/src/main/java/com/banana/hypermodes/hook/BedtimeController.kt` (structure, step order, and StepResult names stay exactly as they are):

1. Constructor becomes:
```kotlin
class BedtimeController(
    private val context: Context,
    private val classLoader: ClassLoader,
    private val log: (String) -> Unit
) {
```
2. Imports: drop `de.robv.android.xposed.XposedBridge` and `de.robv.android.xposed.XposedHelpers`; no new imports needed (Reflect is same-package).
3. `XposedHelpers.findClass(X, classLoader)` → `Reflect.findClass(X, classLoader)`
4. `XposedHelpers.callStaticMethod(cls, name, ...args)` → `Reflect.callStatic(cls, name, ...args)`
5. `XposedHelpers.callMethod(obj, name, ...args)` → `Reflect.call(obj, name, ...args)`
6. `XposedHelpers.newInstance(cls, ...args)` → `Reflect.newInstance(cls, ...args)`
7. `XposedHelpers.setIntField(...)` → `Reflect.setIntField(...)`; `XposedHelpers.setObjectField(...)` → `Reflect.setObjectField(...)`
8. `XposedBridge.log("$TAG: ...")` → `log("...")` (the tag is applied by the caller's logger).

- [ ] **Step 7: Rewrite DeskClockHook**

Replace the entire content of `app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt`:

```kotlin
package com.banana.hypermodes.hook

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Hooks Application.attach(Context) inside DeskClock — the same capture point
 * the reference module (HyperCeiler) uses: attach is final, always called,
 * and after chain.proceed() the Application's base context is ready.
 * Registers the command receiver, then delegates to BedtimeController.
 *
 * The receiver must be RECEIVER_EXPORTED (sender is our app, a different uid)
 * and is guarded by our signature-level permission so only our app can
 * trigger it.
 */
class DeskClockHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val attach = Application::class.java.getDeclaredMethod("attach", Context::class.java)
        module.hook(attach)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    val app = chain.thisObject as Application
                    try {
                        registerReceiver(app, classLoader)
                    } catch (t: Throwable) {
                        log("receiver registration failed: $t")
                    }
                    return result
                }
            })
    }

    private fun registerReceiver(app: Application, classLoader: ClassLoader) {
        val controller = BedtimeController(app, classLoader) { msg -> log(msg) }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val results: List<StepResult> = when (intent.action) {
                    Protocol.ACTION_APPLY_SCHEDULE -> controller.applySchedule(
                        sleepHour = intent.getIntExtra(Protocol.EXTRA_SLEEP_HOUR, 22),
                        sleepMin = intent.getIntExtra(Protocol.EXTRA_SLEEP_MIN, 30),
                        wakeHour = intent.getIntExtra(Protocol.EXTRA_WAKE_HOUR, 7),
                        wakeMin = intent.getIntExtra(Protocol.EXTRA_WAKE_MIN, 30),
                        repeatDays = intent.getIntExtra(Protocol.EXTRA_REPEAT_DAYS, Protocol.EVERY_DAY)
                    )
                    Protocol.ACTION_START_BEDTIME -> controller.startBedtime()
                    Protocol.ACTION_STOP_BEDTIME -> controller.stopBedtime()
                    Protocol.ACTION_QUERY_STATE -> emptyList()
                    else -> return
                }
                log("${intent.action} -> ${results.joinToString { it.format() }}")
                sendResult(app, results, controller.querySleepModeState())
            }
        }

        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_APPLY_SCHEDULE)
            addAction(Protocol.ACTION_START_BEDTIME)
            addAction(Protocol.ACTION_STOP_BEDTIME)
            addAction(Protocol.ACTION_QUERY_STATE)
        }
        app.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        log("command receiver registered in DeskClock")
    }

    private fun sendResult(context: Context, results: List<StepResult>, inSleepMode: Boolean) {
        context.sendBroadcast(Intent(Protocol.ACTION_RESULT).apply {
            setPackage(Protocol.MODULE_PACKAGE)
            putExtra(Protocol.EXTRA_STEPS, results.map { it.format() }.toTypedArray())
            putExtra(Protocol.EXTRA_IN_SLEEP_MODE, inSleepMode)
        })
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
    }
}
```

- [ ] **Step 8: Rewrite XposedInit**

Replace the entire content of `app/src/main/java/com/banana/hypermodes/XposedInit.kt`:

```kotlin
package com.banana.hypermodes

import android.util.Log
import com.banana.hypermodes.hook.DeskClockHook
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * LSPosed (libxposed API 101) entry point, listed in
 * META-INF/xposed/java_init.list. Thin delegator only.
 */
class XposedInit : XposedModule() {
    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        if (param.packageName != Protocol.TARGET_PACKAGE) return
        try {
            DeskClockHook(this).install(param.classLoader)
            log(Log.INFO, TAG, "hook installed for ${param.packageName}")
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install hook", t)
        }
    }

    companion object {
        private const val TAG = "HyperModes"
    }
}
```

- [ ] **Step 9: Update packaging files**

1. Set `app/src/main/resources/META-INF/xposed/module.prop` to:

```
minApiVersion=101
targetApiVersion=101
autoHotReload=true
staticScope=false
```

2. Delete `app/src/main/assets/xposed_init` (legacy loader file; the modern loader uses `java_init.list`, which already contains `com.banana.hypermodes.XposedInit` — leave it and `scope.list` unchanged).

3. In `app/src/main/AndroidManifest.xml`, remove all four legacy meta-data entries (`xposedmodule`, `xposeddescription`, `xposedminversion`, `xposedscope`) and add to the `<application>` tag:

```xml
        android:description="@string/xposed_description"
```

4. In `app/src/main/res/values/strings.xml`, add:

```xml
    <string name="xposed_description">HyperOS Bedtime control - edit the DeskClock bedtime schedule and manually start/stop bedtime mode with full Mi Health / Mi Home / Zen Mode sync</string>
```

- [ ] **Step 10: Full verification build**

Run: `.\gradlew.bat clean :app:assembleDebug :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL; all unit tests pass (Protocol 7 + StepResult 4 + Reflect 11 = 22). Then verify the APK packaging: `META-INF/xposed/java_init.list`, `module.prop`, `scope.list` present; `assets/xposed_init` ABSENT; `java_init.list` content is `com.banana.hypermodes.XposedInit`.

- [ ] **Step 11: Commit**

```bash
git add -A
git commit -m "feat: migrate module to libxposed API 101 (XposedModule entry, modern packaging)"
```

---

## Self-Review Notes

- **Spec coverage:** architecture (Tasks 3–4), protocol (Task 2), all four call sequences (Task 3), UI incl. day chips + state display (Task 6), cleanup (Tasks 1, 4, 5), Alarm-field fallback (Task 3 `mutateAlarm`/`setAlarmDays`), error handling (Task 3 `runStep` + Task 6 timeout), testing (Tasks 2, 3, 7). The one deliberate deviation (exported receivers + signature permission) is documented in Global Constraints.
- **Placeholder scan:** none — all code complete.
- **Type consistency:** `StepResult.ok/fail/format`, `Protocol` constant names, and `BedtimeController` signatures are used identically in Tasks 3, 4, and 6.
