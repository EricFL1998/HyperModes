# 常驻模式引擎（Engine / UI 分离）实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 把模式激活逻辑从 UI 剥离为 AlarmManager 驱动的无常驻进程引擎，使定时模式在 app 被杀后仍按时触发，并做实暂停应用、按应用放行、联系人过滤。

**Architecture:** 引擎 = `ModeEngine`（激活/恢复，系统原生机制代理持续执行）+ `ModeScheduler`（纯函数计算下次触发）+ manifest receivers（闹钟触发/重排/开机/时间变化自愈）。system_server 侧新增 `SystemModeHook` 桥接 `setPackagesSuspended` 与渠道 bypassDnd，并豁免本应用待机桶。UI 只读写 ModeStore 并广播 RESCHEDULE。

**Tech Stack:** Kotlin, Android (minSdk 35 / compileSdk 37), libxposed API 101, AlarmManager, JUnit4（已有 testImplementation）。

## Global Constraints

- 所有代码文件包名根：`com.banana.hypermodes`；新增引擎代码放 `engine/` 包。
- minSdk 35：可直接用 `java.time.*`（无需 desugaring）。
- 不新增任何 gradle 依赖；单元测试用已有 JUnit4。
- libxposed hook 风格：`module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE).intercept(object : XposedInterface.Hooker {...})`，所有反射查找不到时 log 并 graceful return。
- `StepResult` 用现有的 `com.banana.hypermodes.hook.StepResult`（`StepResult.ok(name)` / `StepResult.fail(name, msg)` / `StepResult.fail(name, throwable)`）。
- 新字符串同时加到 `app/src/main/res/values/strings.xml`（英文）和 `app/src/main/res/values-zh-rCN/strings.xml`（中文），插入到 `</resources>` 之前。
- Gradle 命令在 Git Bash 下用 `./gradlew`（PowerShell 下用 `.\gradlew.bat`）。
- 引擎只处理 schedule 边界；bedtime 模式不设闹钟（DeskClock 驱动），其余带 `schedule.enabled == true` 的模式一律设闹钟（与 mode.enabled 无关）。
- dimWallpaper / keepScreenOn / keepScreenOff / hideNotifications 维持现状（仅存储的标志位，无系统机制可代理；本次不做，与重构前行为一致）。

---

### Task 1: ModeScheduler 纯函数 + JVM 单元测试（TDD）

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/engine/ModeScheduler.kt`
- Test: `app/src/test/java/com/banana/hypermodes/engine/ModeSchedulerTest.kt`

**Interfaces:**
- Consumes: `com.banana.hypermodes.data.ModeSchedule(enabled, startHour, startMinute, endHour, endMinute, repeatDays)`（已存在，纯 Kotlin 无 Android 依赖）
- Produces: `ModeScheduler.nextTrigger(schedule: ModeSchedule, nowMillis: Long, zone: ZoneId): ModeScheduler.NextTrigger?`，`NextTrigger(epochMillis: Long, trigger: ModeScheduler.Trigger)`，`Trigger { START, END }` — Task 4 的 EngineReceiver 依赖此签名。

- [ ] **Step 1: 写失败测试**

创建 `app/src/test/java/com/banana/hypermodes/engine/ModeSchedulerTest.kt`：

```kotlin
package com.banana.hypermodes.engine

import com.banana.hypermodes.data.ModeSchedule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.Instant
import java.time.ZoneId

class ModeSchedulerTest {
    private val zone: ZoneId = ZoneId.of("UTC")

    private fun at(iso: String) = Instant.parse(iso).toEpochMilli()

    private fun schedule(
        startH: Int, startM: Int, endH: Int, endM: Int,
        days: Int = 0x7F, enabled: Boolean = true
    ) = ModeSchedule(enabled, startH, startM, endH, endM, days)

    @Test
    fun `disabled schedule returns null`() {
        val s = schedule(9, 0, 17, 0, enabled = false)
        assertNull(ModeScheduler.nextTrigger(s, at("2026-07-22T08:00:00Z"), zone))
    }

    @Test
    fun `before window - next is START today`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T08:00:00Z"), zone)
        )
    }

    @Test
    fun `inside window - next is END today`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T17:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-22T12:00:00Z"), zone)
        )
    }

    @Test
    fun `after window - next is START tomorrow`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-23T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T18:00:00Z"), zone)
        )
    }

    @Test
    fun `overnight window - before start, next is START tonight`() {
        val s = schedule(23, 0, 7, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T23:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T22:00:00Z"), zone)
        )
    }

    @Test
    fun `overnight window - after midnight, next is END this morning`() {
        val s = schedule(23, 0, 7, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T07:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-22T02:00:00Z"), zone)
        )
    }

    @Test
    fun `weekdays only - friday evening rolls to monday`() {
        // Mon..Fri = bits 0..4 = 0b0011111 = 31; 2026-07-24 is a Friday
        val s = schedule(9, 0, 17, 0, days = 31)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-27T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-24T18:00:00Z"), zone)
        )
    }

    @Test
    fun `single weekday - waits a full week`() {
        // Tuesday only = bit 1 = 2; 2026-07-22 is a Wednesday
        val s = schedule(9, 0, 17, 0, days = 2)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-28T09:00:00Z"), ModeScheduler.Trigger.START),
            ModeScheduler.nextTrigger(s, at("2026-07-22T12:00:00Z"), zone)
        )
    }

    @Test
    fun `overnight weekday window - saturday early morning still ends`() {
        // Weekdays 23:00-07:00; Sat 2026-07-25 02:00 is inside Friday's window
        val s = schedule(23, 0, 7, 0, days = 31)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-25T07:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-25T02:00:00Z"), zone)
        )
    }

    @Test
    fun `exactly at start time counts as inside window`() {
        val s = schedule(9, 0, 17, 0)
        assertEquals(
            ModeScheduler.NextTrigger(at("2026-07-22T17:00:00Z"), ModeScheduler.Trigger.END),
            ModeScheduler.nextTrigger(s, at("2026-07-22T09:00:00Z"), zone)
        )
    }
}
```

- [ ] **Step 2: 运行测试确认失败**

Run: `./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.engine.ModeSchedulerTest"`
Expected: FAIL — 编译错误 `Unresolved reference: ModeScheduler`

- [ ] **Step 3: 实现 ModeScheduler**

创建 `app/src/main/java/com/banana/hypermodes/engine/ModeScheduler.kt`：

```kotlin
package com.banana.hypermodes.engine

import com.banana.hypermodes.data.ModeSchedule
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId

/**
 * Pure next-trigger calculation for a ModeSchedule. No Android dependencies —
 * unit-tested on the JVM (ModeSchedulerTest).
 */
object ModeScheduler {

    enum class Trigger { START, END }

    data class NextTrigger(val epochMillis: Long, val trigger: Trigger)

    /**
     * Next schedule boundary strictly after [nowMillis], or null when the
     * schedule is disabled. Overnight windows (end <= start) end the next day.
     * repeatDays: bit0 = Monday ... bit6 = Sunday (Protocol semantics).
     */
    fun nextTrigger(schedule: ModeSchedule, nowMillis: Long, zone: ZoneId): NextTrigger? {
        if (!schedule.enabled) return null
        val today = Instant.ofEpochMilli(nowMillis).atZone(zone).toLocalDate()
        val start = LocalTime.of(schedule.startHour, schedule.startMinute)
        val end = LocalTime.of(schedule.endHour, schedule.endMinute)
        val overnight = end <= start

        var best: NextTrigger? = null
        // Yesterday (an overnight window may have started then) through a week
        // ahead (guaranteed to contain the next active day).
        for (offset in -1..7) {
            val day = today.plusDays(offset.toLong())
            val bit = day.dayOfWeek.value - 1 // Monday = 0 ... Sunday = 6
            if (schedule.repeatDays and (1 shl bit) == 0) continue
            val startMillis = day.atTime(start).atZone(zone).toInstant().toEpochMilli()
            val endDay = if (overnight) day.plusDays(1) else day
            val endMillis = endDay.atTime(end).atZone(zone).toInstant().toEpochMilli()
            for (candidate in listOf(
                NextTrigger(startMillis, Trigger.START),
                NextTrigger(endMillis, Trigger.END)
            )) {
                if (candidate.epochMillis > nowMillis &&
                    (best == null || candidate.epochMillis < best.epochMillis)
                ) {
                    best = candidate
                }
            }
        }
        return best
    }
}
```

- [ ] **Step 4: 运行测试确认通过**

Run: `./gradlew :app:testDebugUnitTest --tests "com.banana.hypermodes.engine.ModeSchedulerTest"`
Expected: PASS（10 个测试全绿）

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/engine/ModeScheduler.kt app/src/test/java/com/banana/hypermodes/engine/ModeSchedulerTest.kt
git commit -m "feat: add ModeScheduler next-trigger calculation with JVM tests"
```

---

### Task 2: Protocol 扩展 + DefaultModes 抽取

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt`
- Create: `app/src/main/java/com/banana/hypermodes/data/DefaultModes.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt`（仅换引用）
- Modify: `app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt`（仅换引用）
- Modify: `app/src/main/java/com/banana/hypermodes/driving/DrivingDetector.kt`（仅换引用）

**Interfaces:**
- Produces:
  - `DefaultModes.get(): List<Mode>` — 取代 `ModeManager.getDefaultModes()`（Task 7 删除 ModeManager）
  - Protocol 新常量：`ACTION_RESCHEDULE`、`ACTION_ALARM_TRIGGER`、`ACTION_MODE_STATE`、`ACTION_SET_PACKAGES_SUSPENDED`、`ACTION_SET_CHANNELS_BYPASS_DND`、`EXTRA_MODE_ID`、`EXTRA_TRIGGER`、`EXTRA_PACKAGES`、`EXTRA_SUSPENDED`、`EXTRA_BYPASS` — Task 3/4/5/7 依赖。

- [ ] **Step 1: Protocol.kt 添加常量**

在 `Protocol` object 中 `ACTION_DISABLE_BEDTIME` 之后追加：

```kotlin
    // Engine (own process, manifest receivers)
    const val ACTION_RESCHEDULE = "com.banana.hypermodes.RESCHEDULE"
    const val ACTION_ALARM_TRIGGER = "com.banana.hypermodes.ALARM_TRIGGER"
    /** Engine -> UI: a mode was activated/deactivated by the engine. */
    const val ACTION_MODE_STATE = "com.banana.hypermodes.MODE_STATE"

    // App -> system_server hook (SystemModeHook bridge)
    const val ACTION_SET_PACKAGES_SUSPENDED = "com.banana.hypermodes.SET_PACKAGES_SUSPENDED"
    const val ACTION_SET_CHANNELS_BYPASS_DND = "com.banana.hypermodes.SET_CHANNELS_BYPASS_DND"

    // Engine extras
    const val EXTRA_MODE_ID = "modeId"
    const val EXTRA_TRIGGER = "trigger" // "start" | "end"
    const val EXTRA_PACKAGES = "packages"
    const val EXTRA_SUSPENDED = "suspended"
    const val EXTRA_BYPASS = "bypass"
```

- [ ] **Step 2: 创建 DefaultModes.kt**

`ModeManager.getDefaultModes()` 的函数体原样搬入新文件（数据归属 data 包）：

```kotlin
package com.banana.hypermodes.data

/** Factory for the three built-in modes (勿扰 / 睡眠 / 驾驶). */
object DefaultModes {
    fun get(): List<Mode> = listOf(
        Mode(
            id = "dnd",
            name = "Do Not Disturb",
            icon = "⊝",
            description = "Silence notifications and calls",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = DndLevel.PRIORITY
            )
        ),
        Mode(
            id = "bedtime",
            name = "Bedtime",
            icon = "🌙",
            description = "From 11:00 pm - 7:00 am",
            settings = ModeSettings(
                enableDnd = true,
                enableGrayscale = true,
                dimWallpaper = true,
                schedule = ModeSchedule(
                    enabled = true,
                    startHour = 23,
                    startMinute = 0,
                    endHour = 7,
                    endMinute = 0
                )
            )
        ),
        Mode(
            id = "driving",
            name = "Driving",
            icon = "🚗",
            description = "Using device's motion and Bluetooth connection",
            settings = ModeSettings(
                enableDnd = true,
                dndLevel = DndLevel.PRIORITY,
                hideNotifications = true
            )
        )
    )
}
```

- [ ] **Step 3: 替换全部 `ModeManager.getDefaultModes()` 引用**

三处调用点改为 `DefaultModes.get()`，import 相应替换：

- `ui/HyperModesApp.kt`：`ModeStore.load(context) { ModeManager.getDefaultModes() }`（约 line 126-128）和 `ModeManager.getDefaultModes().filter {...}`（约 line 623-624 与 line 656-657，两处）→ 全部改为 `DefaultModes.get()`；import `com.banana.hypermodes.data.DefaultModes`，移除不再使用的 `import com.banana.hypermodes.manager.ModeManager`。
- `receiver/BedtimeStateReceiver.kt:29`：`ModeStore.load(context) { ModeManager.getDefaultModes() }` → `ModeStore.load(context) { DefaultModes.get() }`，import 同样替换。
- `driving/DrivingDetector.kt:44` 与 `:73`：同样替换（此文件 Task 6 还会改 ModeManager 激活调用，本步只换 defaults 引用）。

- [ ] **Step 4: 编译验证**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL（ModeManager 仍存在，只是没人再调 getDefaultModes）

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/protocol/Protocol.kt app/src/main/java/com/banana/hypermodes/data/DefaultModes.kt app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt app/src/main/java/com/banana/hypermodes/driving/DrivingDetector.kt
git commit -m "refactor: extract DefaultModes, extend Protocol for the mode engine"
```

---

### Task 3: EngineState + ModeEngine

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/engine/EngineState.kt`
- Create: `app/src/main/java/com/banana/hypermodes/engine/ModeEngine.kt`

**Interfaces:**
- Consumes: `DefaultModes.get()`（Task 2）、Protocol 新常量（Task 2）、`StepResult`、`ModeStore.load/save`
- Produces（Task 4/6/7 依赖）：
  - `ModeEngine(context: Context)`
  - `ModeEngine.activate(mode: Mode, skipBedtimeTrigger: Boolean = false): List<StepResult>`
  - `ModeEngine.deactivate(mode: Mode, skipBedtimeTrigger: Boolean = false): List<StepResult>`

- [ ] **Step 1: 创建 EngineState.kt（快照 + 引用计数 + 跟踪集合）**

```kotlin
package com.banana.hypermodes.engine

import android.content.Context

/**
 * Persistent engine bookkeeping (SharedPreferences "engine_state"):
 *
 * - Reference counts per capability ("dnd", "grayscale", "darkMode") so two
 *   simultaneously active modes don't restore each other's settings:
 *   the first activation snapshots + applies, the last deactivation restores.
 * - Int snapshots (interruption filter, zen policy, daltonizer, night mode)
 *   recorded before first apply, restored after last release.
 * - Tracked package sets ("suspended_apps", "bypassed_apps") — what WE have
 *   suspended / set bypass-Dnd on, so deactivation only touches our own.
 */
object EngineState {
    private const val PREFS = "engine_state"
    private const val COUNT_PREFIX = "count_"
    private const val SNAP_PREFIX = "snap_"

    const val KEY_DND = "dnd"
    const val KEY_GRAYSCALE = "grayscale"
    const val KEY_DARK_MODE = "darkMode"
    const val TRACK_SUSPENDED = "suspended_apps"
    const val TRACK_BYPASSED = "bypassed_apps"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** +1 holder; returns true when the caller is the FIRST holder and must apply. */
    fun acquire(context: Context, key: String): Boolean {
        val p = prefs(context)
        val count = p.getInt(COUNT_PREFIX + key, 0)
        p.edit().putInt(COUNT_PREFIX + key, count + 1).apply()
        return count == 0
    }

    /** -1 holder; returns true when the caller was the LAST holder and must restore. */
    fun release(context: Context, key: String): Boolean {
        val p = prefs(context)
        val count = p.getInt(COUNT_PREFIX + key, 0)
        if (count <= 1) {
            p.edit().putInt(COUNT_PREFIX + key, 0).apply()
            return count == 1
        }
        p.edit().putInt(COUNT_PREFIX + key, count - 1).apply()
        return false
    }

    fun putSnapshot(context: Context, key: String, values: Map<String, Int>) {
        val e = prefs(context).edit()
        values.forEach { (name, v) -> e.putInt(SNAP_PREFIX + key + "_" + name, v) }
        e.apply()
    }

    fun getSnapshot(context: Context, key: String, name: String, default: Int): Int =
        prefs(context).getInt(SNAP_PREFIX + key + "_" + name, default)

    fun getTracked(context: Context, key: String): Set<String> =
        prefs(context).getStringSet(key, emptySet())?.toSet() ?: emptySet()

    fun putTracked(context: Context, key: String, value: Set<String>) {
        prefs(context).edit().putStringSet(key, value).apply()
    }
}
```

- [ ] **Step 2: 创建 ModeEngine.kt**

```kotlin
package com.banana.hypermodes.engine

import android.app.NotificationManager
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import com.banana.hypermodes.data.CONTACT_FILTER_ALL
import com.banana.hypermodes.data.CONTACT_FILTER_STARRED
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.DndLevel
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.hook.StepResult
import com.banana.hypermodes.protocol.Protocol

/**
 * The single entry point for mode activation/deactivation. Runs wherever it
 * is invoked — UI process (manual toggles), alarm receivers, driving
 * detection, bedtime state pushes.
 *
 * Continuous enforcement is delegated to system-native mechanisms (zen
 * policy, secure settings, package suspension, channel bypass), so no
 * resident process is needed while a mode is on.
 *
 * Every step is individually caught and recorded — one failure never aborts
 * the remaining steps.
 */
class ModeEngine(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun activate(mode: Mode, skipBedtimeTrigger: Boolean = false): List<StepResult> {
        val results = mutableListOf<StepResult>()
        val s = mode.settings
        if (s.enableDnd) applyDnd(results, true, s.dndLevel, s.contactFilter)
        if (s.enableGrayscale) applyGrayscale(results, true)
        if (s.enableDarkMode) applyDarkMode(results, true)
        syncSuspendedApps(results, mode, activating = true)
        syncBypassApps(results, mode, activating = true)
        if (mode.id == "bedtime" && !skipBedtimeTrigger) {
            triggerDeskClockBedtime(results, true)
        }
        return results
    }

    fun deactivate(mode: Mode, skipBedtimeTrigger: Boolean = false): List<StepResult> {
        val results = mutableListOf<StepResult>()
        val s = mode.settings
        if (s.enableDnd) applyDnd(results, false, s.dndLevel, s.contactFilter)
        if (s.enableGrayscale) applyGrayscale(results, false)
        if (s.enableDarkMode) applyDarkMode(results, false)
        syncSuspendedApps(results, mode, activating = false)
        syncBypassApps(results, mode, activating = false)
        if (mode.id == "bedtime" && !skipBedtimeTrigger) {
            triggerDeskClockBedtime(results, false)
        }
        return results
    }

    // ---- DND (interruption filter + zen policy incl. contact filter) ----

    private fun applyDnd(
        results: MutableList<StepResult>, enable: Boolean,
        level: DndLevel, contactFilter: Int
    ) {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            results += StepResult.fail("dnd", "notification policy access not granted")
            return
        }
        if (enable) {
            if (!EngineState.acquire(context, EngineState.KEY_DND)) {
                results += StepResult.ok("dnd (already held)")
                return
            }
            val p = notificationManager.notificationPolicy
            EngineState.putSnapshot(context, EngineState.KEY_DND, mapOf(
                "filter" to notificationManager.currentInterruptionFilter,
                "categories" to p.priorityCategories,
                "callSenders" to p.priorityCallSenders,
                "messageSenders" to p.priorityMessageSenders,
                "conversationSenders" to p.priorityConversationSenders
            ))
            runStep(results, "dnd policy") {
                notificationManager.notificationPolicy = buildPolicy(contactFilter)
            }
            runStep(results, "dnd filter") {
                notificationManager.setInterruptionFilter(
                    when (level) {
                        DndLevel.NONE -> NotificationManager.INTERRUPTION_FILTER_NONE
                        DndLevel.PRIORITY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                        DndLevel.ALARMS -> NotificationManager.INTERRUPTION_FILTER_ALARMS
                    }
                )
            }
        } else {
            if (!EngineState.release(context, EngineState.KEY_DND)) {
                results += StepResult.ok("dnd (still held)")
                return
            }
            runStep(results, "dnd restore") {
                notificationManager.notificationPolicy = NotificationManager.Policy(
                    EngineState.getSnapshot(context, EngineState.KEY_DND, "categories", 0),
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "callSenders",
                        NotificationManager.Policy.PRIORITY_SENDERS_ANY
                    ),
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "messageSenders",
                        NotificationManager.Policy.PRIORITY_SENDERS_ANY
                    ),
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "conversationSenders",
                        NotificationManager.Policy.PRIORITY_CONVERSATION_SENDERS_ANYONE
                    )
                )
                notificationManager.setInterruptionFilter(
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "filter",
                        NotificationManager.INTERRUPTION_FILTER_ALL
                    )
                )
            }
        }
    }

    /** Map contactFilter to a zen Policy: who may interrupt while the mode is on. */
    private fun buildPolicy(contactFilter: Int): NotificationManager.Policy = when (contactFilter) {
        CONTACT_FILTER_ALL -> NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_CALLS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES or
                    NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            NotificationManager.Policy.PRIORITY_CONVERSATION_SENDERS_ANYONE
        )
        CONTACT_FILTER_STARRED -> NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_CALLS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES or
                    NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,
            NotificationManager.Policy.PRIORITY_SENDERS_STARRED,
            NotificationManager.Policy.PRIORITY_SENDERS_STARRED,
            NotificationManager.Policy.PRIORITY_CONVERSATION_SENDERS_IMPORTANT
        )
        else -> NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            NotificationManager.Policy.PRIORITY_CONVERSATION_SENDERS_NONE
        )
    }

    // ---- Grayscale (accessibility daltonizer, root fallback) ----

    private fun applyGrayscale(results: MutableList<StepResult>, enable: Boolean) {
        if (enable) {
            if (!EngineState.acquire(context, EngineState.KEY_GRAYSCALE)) {
                results += StepResult.ok("grayscale (already held)")
                return
            }
            EngineState.putSnapshot(context, EngineState.KEY_GRAYSCALE, mapOf(
                "enabled" to readSecureInt("accessibility_display_daltonizer_enabled", 0),
                "type" to readSecureInt("accessibility_display_daltonizer", 0)
            ))
            runStep(results, "grayscale on") {
                putSecureInt("accessibility_display_daltonizer", 0) // 0 = monochromacy
                putSecureInt("accessibility_display_daltonizer_enabled", 1)
            }
        } else {
            if (!EngineState.release(context, EngineState.KEY_GRAYSCALE)) {
                results += StepResult.ok("grayscale (still held)")
                return
            }
            runStep(results, "grayscale restore") {
                putSecureInt(
                    "accessibility_display_daltonizer",
                    EngineState.getSnapshot(context, EngineState.KEY_GRAYSCALE, "type", 0)
                )
                putSecureInt(
                    "accessibility_display_daltonizer_enabled",
                    EngineState.getSnapshot(context, EngineState.KEY_GRAYSCALE, "enabled", 0)
                )
            }
        }
    }

    private fun readSecureInt(name: String, default: Int): Int = try {
        Settings.Secure.getInt(context.contentResolver, name, default)
    } catch (t: Throwable) {
        default
    }

    /** Direct write first; fall back to root shell (libsu) like the old ModeManager. */
    private fun putSecureInt(name: String, value: Int) {
        try {
            Settings.Secure.putInt(context.contentResolver, name, value)
        } catch (e: SecurityException) {
            com.topjohnwu.superuser.Shell.cmd("settings put secure $name $value").exec()
        }
    }

    // ---- Dark mode (UiModeManager, root fallback) ----

    private fun applyDarkMode(results: MutableList<StepResult>, enable: Boolean) {
        if (enable) {
            if (!EngineState.acquire(context, EngineState.KEY_DARK_MODE)) {
                results += StepResult.ok("darkMode (already held)")
                return
            }
            val night = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            EngineState.putSnapshot(context, EngineState.KEY_DARK_MODE, mapOf("night" to night))
            runStep(results, "darkMode on") { setNightMode(Configuration.UI_MODE_NIGHT_YES) }
        } else {
            if (!EngineState.release(context, EngineState.KEY_DARK_MODE)) {
                results += StepResult.ok("darkMode (still held)")
                return
            }
            runStep(results, "darkMode restore") {
                setNightMode(
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DARK_MODE, "night",
                        Configuration.UI_MODE_NIGHT_NO
                    )
                )
            }
        }
    }

    private fun setNightMode(mode: Int) {
        try {
            val uiModeManager =
                context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            uiModeManager.nightMode = mode
        } catch (e: SecurityException) {
            val shell = when (mode) {
                Configuration.UI_MODE_NIGHT_YES -> "yes"
                Configuration.UI_MODE_NIGHT_NO -> "no"
                else -> "auto"
            }
            com.topjohnwu.superuser.Shell.cmd("cmd uimode night $shell").exec()
        }
    }

    // ---- Suspended apps / bypass-Dnd apps (via SystemModeHook in system_server) ----

    /**
     * Recompute the union of pausedApps over all enabled modes (the toggled
     * [mode] counts per [activating], since ModeStore is written after the
     * engine runs) and diff against what we previously suspended.
     */
    private fun syncSuspendedApps(
        results: MutableList<StepResult>, mode: Mode, activating: Boolean
    ) = syncPackageSet(
        results, mode, activating,
        select = { it.settings.pausedApps },
        trackKey = EngineState.TRACK_SUSPENDED,
        action = Protocol.ACTION_SET_PACKAGES_SUSPENDED,
        flagExtra = Protocol.EXTRA_SUSPENDED,
        step = "suspendApps"
    )

    private fun syncBypassApps(
        results: MutableList<StepResult>, mode: Mode, activating: Boolean
    ) = syncPackageSet(
        results, mode, activating,
        select = { it.settings.allowedApps },
        trackKey = EngineState.TRACK_BYPASSED,
        action = Protocol.ACTION_SET_CHANNELS_BYPASS_DND,
        flagExtra = Protocol.EXTRA_BYPASS,
        step = "bypassDnd"
    )

    private fun syncPackageSet(
        results: MutableList<StepResult>, mode: Mode, activating: Boolean,
        select: (Mode) -> Set<String>,
        trackKey: String, action: String, flagExtra: String, step: String
    ) {
        val modes = ModeStore.load(context) { DefaultModes.get() }
        val want = modes
            .filter { if (it.id == mode.id) activating else it.enabled }
            .flatMap { select(it) }
            .toSet()
        val prev = EngineState.getTracked(context, trackKey)
        val toOn = want - prev
        val toOff = prev - want
        EngineState.putTracked(context, trackKey, want)
        if (toOn.isNotEmpty()) dispatchToSystem(results, action, toOn, flagExtra, true, "$step on")
        if (toOff.isNotEmpty()) dispatchToSystem(results, action, toOff, flagExtra, false, "$step off")
        if (toOn.isEmpty() && toOff.isEmpty()) results += StepResult.ok("$step (no change)")
    }

    /**
     * Fire-and-forget bridge to SystemModeHook in system_server (dynamic
     * receiver registered with our signature permission — only this app can
     * send). The outcome lands in the LSPosed module log.
     */
    private fun dispatchToSystem(
        results: MutableList<StepResult>, action: String,
        packages: Set<String>, flagExtra: String, flag: Boolean, step: String
    ) {
        try {
            context.sendBroadcast(Intent(action).apply {
                putExtra(Protocol.EXTRA_PACKAGES, packages.toTypedArray())
                putExtra(flagExtra, flag)
            }, Protocol.PERMISSION_CONTROL)
            results += StepResult.ok("$step dispatched (${packages.size})")
        } catch (t: Throwable) {
            results += StepResult.fail(step, t)
        }
    }

    // ---- Bedtime (DeskClock trigger, existing wire protocol) ----

    private fun triggerDeskClockBedtime(results: MutableList<StepResult>, start: Boolean) {
        runStep(results, if (start) "deskclock bedtime start" else "deskclock bedtime stop") {
            val intent = Intent(
                if (start) Protocol.ACTION_START_BEDTIME else Protocol.ACTION_STOP_BEDTIME
            )
            intent.setPackage(Protocol.DESKCLOCK_PACKAGE)
            context.sendBroadcast(intent, Protocol.PERMISSION_CONTROL)
        }
    }

    private fun runStep(results: MutableList<StepResult>, name: String, block: () -> Any?) {
        try {
            block()
            results += StepResult.ok(name)
        } catch (t: Throwable) {
            results += StepResult.fail(name, t)
        }
    }
}
```

- [ ] **Step 3: 编译验证**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/engine/EngineState.kt app/src/main/java/com/banana/hypermodes/engine/ModeEngine.kt
git commit -m "feat: add ModeEngine with snapshot/restore and refcounted capabilities"
```

---

### Task 4: EngineReceiver（闹钟触发 + 重排）+ TimeChangedReceiver + manifest + BootReceiver

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/engine/EngineReceiver.kt`
- Create: `app/src/main/java/com/banana/hypermodes/engine/TimeChangedReceiver.kt`
- Modify: `app/src/main/AndroidManifest.xml`
- Modify: `app/src/main/java/com/banana/hypermodes/driving/BootReceiver.kt`

**Interfaces:**
- Consumes: `ModeScheduler.nextTrigger`（Task 1）、`DefaultModes.get()` + Protocol 常量（Task 2）、`ModeEngine.activate/deactivate`（Task 3）
- Produces:
  - `EngineReceiver.rescheduleAll(context: Context)` — BootReceiver/TimeChangedReceiver 调用；Task 7 的 UI 广播 ACTION_RESCHEDULE 也汇入这里
  - manifest 声明 `SCHEDULE_EXACT_ALARM`、`REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`

- [ ] **Step 1: 创建 EngineReceiver.kt**

```kotlin
package com.banana.hypermodes.engine

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.protocol.Protocol
import java.time.ZoneId
import kotlin.concurrent.thread

/**
 * Stateless engine entry points (manifest-registered, cold-start safe):
 *
 * - ACTION_RESCHEDULE: re-arm every mode's next alarm (sent by the UI after
 *   any mode save/delete; also called by BootReceiver/TimeChangedReceiver).
 * - ACTION_ALARM_TRIGGER: AlarmManager fired — activate/deactivate the mode,
 *   persist the new enabled flag, re-arm the next trigger, notify the UI.
 *
 * Alarms use setExactAndAllowWhileIdle so they fire in doze; falls back to
 * inexact when SCHEDULE_EXACT_ALARM is not granted.
 */
class EngineReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Protocol.ACTION_RESCHEDULE -> rescheduleAll(context)
            Protocol.ACTION_ALARM_TRIGGER -> {
                // Engine work (root shell fallbacks, settings writes) may
                // block — move it off the main thread.
                val pending = goAsync()
                val appContext = context.applicationContext
                thread {
                    try {
                        onAlarmTrigger(appContext, intent)
                    } finally {
                        pending.finish()
                    }
                }
            }
        }
    }

    private fun onAlarmTrigger(context: Context, intent: Intent) {
        val modeId = intent.getStringExtra(Protocol.EXTRA_MODE_ID) ?: return
        val isStart = intent.getStringExtra(Protocol.EXTRA_TRIGGER) == TRIGGER_START
        val modes = ModeStore.load(context) { DefaultModes.get() }.toMutableList()
        val idx = modes.indexOfFirst { it.id == modeId }
        if (idx < 0) {
            // Mode was deleted while armed — drop the stale alarm.
            rescheduleAll(context)
            return
        }
        val mode = modes[idx]
        val engine = ModeEngine(context)
        when {
            isStart && !mode.enabled -> {
                engine.activate(mode)
                modes[idx] = mode.copy(enabled = true)
                ModeStore.save(context, modes)
            }
            !isStart && mode.enabled -> {
                engine.deactivate(mode)
                modes[idx] = mode.copy(enabled = false)
                ModeStore.save(context, modes)
            }
        }
        rescheduleAll(context)
        context.sendBroadcast(
            Intent(Protocol.ACTION_MODE_STATE).setPackage(context.packageName)
        )
    }

    companion object {
        const val TRIGGER_START = "start"
        const val TRIGGER_END = "end"
        private const val PREFS = "engine_alarms"
        private const val KEY_ARMED = "armed_ids"

        /** Re-arm the next alarm for every scheduled mode (except bedtime,
         *  which is driven by DeskClock's own alarms). */
        fun rescheduleAll(context: Context) {
            val alarmManager =
                context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            val previouslyArmed =
                prefs.getStringSet(KEY_ARMED, emptySet())?.toSet() ?: emptySet()

            val now = System.currentTimeMillis()
            val zone = ZoneId.systemDefault()
            val armed = mutableSetOf<String>()

            ModeStore.load(context) { DefaultModes.get() }.forEach { mode ->
                val schedule = mode.settings.schedule
                if (mode.id == "bedtime" || schedule == null) return@forEach
                val next = ModeScheduler.nextTrigger(schedule, now, zone) ?: return@forEach
                val trigger = when (next.trigger) {
                    ModeScheduler.Trigger.START -> TRIGGER_START
                    ModeScheduler.Trigger.END -> TRIGGER_END
                }
                val pi = pendingIntent(context, mode.id, trigger)
                if (canScheduleExact(alarmManager)) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, next.epochMillis, pi
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP, next.epochMillis, pi
                    )
                }
                armed += mode.id
            }

            // Cancel alarms of modes that are no longer scheduled (deleted
            // or schedule disabled).
            (previouslyArmed - armed).forEach { id ->
                alarmManager.cancel(pendingIntent(context, id, null))
            }
            prefs.edit().putStringSet(KEY_ARMED, armed).apply()
        }

        private fun canScheduleExact(alarmManager: AlarmManager): Boolean =
            Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                    alarmManager.canScheduleExactAlarms()

        private fun pendingIntent(
            context: Context, modeId: String, trigger: String?
        ): PendingIntent =
            PendingIntent.getBroadcast(
                context,
                modeId.hashCode(),
                Intent(Protocol.ACTION_ALARM_TRIGGER)
                    .setPackage(context.packageName)
                    .putExtra(Protocol.EXTRA_MODE_ID, modeId)
                    .putExtra(Protocol.EXTRA_TRIGGER, trigger),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
    }
}
```

- [ ] **Step 2: 创建 TimeChangedReceiver.kt**

```kotlin
package com.banana.hypermodes.engine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Wall-clock changed (time set / timezone / date) — every computed alarm is
 * now wrong, so re-arm all of them.
 */
class TimeChangedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        EngineReceiver.rescheduleAll(context)
    }
}
```

- [ ] **Step 3: AndroidManifest.xml 添加权限与 receiver**

`RECEIVE_BOOT_COMPLETED` 权限之后添加：

```xml
    <!-- Scheduled modes: exact alarms fire on time even in doze -->
    <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
    <!-- Lets the UI offer the battery-optimization exemption prompt -->
    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
```

`BedtimeStateReceiver` 之后添加：

```xml
        <!-- Mode engine: alarm triggers + reschedule (our own app only) -->
        <receiver
            android:name=".engine.EngineReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="com.banana.hypermodes.ALARM_TRIGGER" />
                <action android:name="com.banana.hypermodes.RESCHEDULE" />
            </intent-filter>
        </receiver>

        <!-- Re-arm schedule alarms when the wall clock changes (protected
             broadcasts — only the system can send them). -->
        <receiver
            android:name=".engine.TimeChangedReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.TIME_SET" />
                <action android:name="android.intent.action.TIMEZONE_CHANGED" />
                <action android:name="android.intent.action.DATE_CHANGED" />
            </intent-filter>
        </receiver>
```

- [ ] **Step 4: BootReceiver 重排闹钟**

`driving/BootReceiver.kt` 的 `onReceive` 改为：

```kotlin
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            DrivingDetector.ensureActivityRecognition(context)
            // Alarms don't survive reboot — re-arm every scheduled mode.
            com.banana.hypermodes.engine.EngineReceiver.rescheduleAll(context)
        }
    }
```

- [ ] **Step 5: 编译 + 单测回归**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL，ModeSchedulerTest 仍全绿

- [ ] **Step 6: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/engine/EngineReceiver.kt app/src/main/java/com/banana/hypermodes/engine/TimeChangedReceiver.kt app/src/main/AndroidManifest.xml app/src/main/java/com/banana/hypermodes/driving/BootReceiver.kt
git commit -m "feat: add engine receivers — alarm trigger, reschedule, time-change healing"
```

---

### Task 5: SystemModeHook（system_server 桥）+ 待机桶豁免

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/hook/SystemModeHook.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/XposedInit.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/hook/SystemKeepAliveHook.kt`

**Interfaces:**
- Consumes: Protocol 常量 `ACTION_SET_PACKAGES_SUSPENDED` / `ACTION_SET_CHANNELS_BYPASS_DND` / `EXTRA_PACKAGES` / `EXTRA_SUSPENDED` / `EXTRA_BYPASS` / `PERMISSION_CONTROL`（Task 2）；ModeEngine 的 dispatchToSystem 广播（Task 3）
- Produces: system_server 内的动态 receiver，处理暂停应用与渠道 bypassDnd；无对外 Kotlin 接口（经广播调用）

- [ ] **Step 1: 创建 SystemModeHook.kt**

```kotlin
package com.banana.hypermodes.hook

import android.app.NotificationChannel
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Privileged-operation bridge running INSIDE system_server.
 *
 * The mode engine (our app process) can't call setPackagesSuspended or edit
 * other apps' notification channels — both need system permissions. This
 * hook captures ActivityManagerService.systemReady (mContext is ready by
 * then) and registers a dynamic receiver guarded by our signature
 * permission, so only HyperModes can dispatch:
 *
 * - ACTION_SET_PACKAGES_SUSPENDED: suspend/unsuspend packages via the
 *   "package" binder (IPackageManager.setPackagesSuspendedAsUser).
 * - ACTION_SET_CHANNELS_BYPASS_DND: set/clear bypass-Dnd on every channel
 *   of the given packages via the "notification" binder. Original per-channel
 *   bypass flags are remembered in memory and restored on clear.
 *
 * All binder calls are name-matched reflectively and best-effort: a renamed
 * method on a MIUI update logs a failure instead of crashing system_server.
 */
class SystemModeHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        val ams = try {
            classLoader.loadClass(AMS)
        } catch (t: Throwable) {
            log("ActivityManagerService not found: ${t.message}")
            return
        }
        val systemReady = ams.declaredMethods.firstOrNull { it.name == "systemReady" }
        if (systemReady == null) {
            log("systemReady not found")
            return
        }
        module.hook(systemReady)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        val context = ams.getDeclaredField("mContext")
                            .apply { isAccessible = true }
                            .get(chain.thisObject) as Context
                        registerBridge(context)
                    } catch (t: Throwable) {
                        log("bridge registration failed: $t")
                    }
                    return result
                }
            })
        log("systemReady hooked for mode bridge")
    }

    private fun registerBridge(context: Context) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                val packages = intent.getStringArrayExtra(Protocol.EXTRA_PACKAGES)
                    ?.toList() ?: return
                when (intent.action) {
                    Protocol.ACTION_SET_PACKAGES_SUSPENDED ->
                        setPackagesSuspended(
                            packages,
                            intent.getBooleanExtra(Protocol.EXTRA_SUSPENDED, false)
                        )
                    Protocol.ACTION_SET_CHANNELS_BYPASS_DND ->
                        setChannelsBypassDnd(
                            c, packages,
                            intent.getBooleanExtra(Protocol.EXTRA_BYPASS, false)
                        )
                }
            }
        }
        val filter = IntentFilter().apply {
            addAction(Protocol.ACTION_SET_PACKAGES_SUSPENDED)
            addAction(Protocol.ACTION_SET_CHANNELS_BYPASS_DND)
        }
        context.registerReceiver(
            receiver, filter,
            Protocol.PERMISSION_CONTROL, null,
            Context.RECEIVER_EXPORTED
        )
        log("mode bridge receiver registered in system_server")
    }

    /** Original bypass flag per "pkg/channelId", captured before our first
     *  override so clearing restores rather than blindly writing false. */
    private val originalBypass = mutableMapOf<String, Boolean>()

    private fun setPackagesSuspended(packages: List<String>, suspended: Boolean) {
        try {
            val ipm = binder("package", "android.content.pm.IPackageManager\$Stub")
            val method = ipm.javaClass.methods.first {
                it.name == "setPackagesSuspendedAsUser"
            }
            // Signature across API levels:
            // (String[], boolean, PersistableBundle, PersistableBundle,
            //  SuspendDialogInfo, String callingPackage, int userId)
            val args = method.parameterTypes.map { t ->
                when {
                    t == Array<String>::class.java -> packages.toTypedArray()
                    t == Boolean::class.javaPrimitiveType -> suspended
                    t == Int::class.javaPrimitiveType -> 0 // userId: system user
                    t == String::class.java -> Protocol.MODULE_PACKAGE
                    else -> null
                }
            }.toTypedArray()
            method.invoke(ipm, *args)
            log("setPackagesSuspended($suspended): ${packages.joinToString()}")
        } catch (t: Throwable) {
            log("setPackagesSuspended failed: $t")
        }
    }

    private fun setChannelsBypassDnd(context: Context, packages: List<String>, bypass: Boolean) {
        val inm = try {
            binder("notification", "android.app.INotificationManager\$Stub")
        } catch (t: Throwable) {
            log("notification binder unavailable: $t")
            return
        }
        val getChannels = inm.javaClass.methods.firstOrNull {
            it.name == "getNotificationChannelsForPackage"
        }
        val updateChannel = inm.javaClass.methods.firstOrNull {
            it.name == "updateNotificationChannelForPackage"
        }
        if (getChannels == null || updateChannel == null) {
            log("channel methods not found on INotificationManager")
            return
        }
        for (pkg in packages) {
            try {
                val uid = context.packageManager.getPackageUid(pkg, 0)
                // (String pkg, int uid, boolean includeDeleted)
                val slice = getChannels.invoke(inm, pkg, uid, false)
                val channels = slice.javaClass.getMethod("getList")
                    .invoke(slice) as List<*>
                for (ch in channels) {
                    if (ch !is NotificationChannel) continue
                    val key = "$pkg/${ch.id}"
                    if (bypass) {
                        originalBypass.putIfAbsent(key, ch.canBypassDnd())
                        ch.setBypassDnd(true)
                    } else {
                        ch.setBypassDnd(originalBypass.remove(key) ?: false)
                    }
                    updateChannel.invoke(inm, pkg, uid, ch)
                }
                log("bypassDnd($bypass): $pkg (${channels.size} channels)")
            } catch (t: Throwable) {
                log("bypassDnd failed for $pkg: $t")
            }
        }
    }

    /** ServiceManager.getService(name) + Stub.asInterface(binder), reflectively. */
    private fun binder(service: String, stubClass: String): Any {
        val binder = Class.forName("android.os.ServiceManager")
            .getMethod("getService", String::class.java)
            .invoke(null, service) as IBinder
        return Class.forName(stubClass)
            .getMethod("asInterface", IBinder::class.java)
            .invoke(null, binder)
    }

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val AMS = "com.android.server.am.ActivityManagerService"
    }
}
```

- [ ] **Step 2: XposedInit 接入**

`XposedInit.kt` 的 `Protocol.FRAMEWORK_PACKAGE ->` 分支改为同时安装两个 hook：

```kotlin
                Protocol.FRAMEWORK_PACKAGE -> {
                    SystemKeepAliveHook(this).install(param.classLoader)
                    SystemModeHook(this).install(param.classLoader)
                    log(Log.INFO, TAG, "hook installed for ${param.packageName}")
                }
```

（`SystemModeHook` 与 `SystemKeepAliveHook` 同包，无需 import。）

- [ ] **Step 3: SystemKeepAliveHook 追加待机桶豁免**

在 `SystemKeepAliveHook.install` 中 `surviveSwipeFromRecents(classLoader)` 之后加一行 `exemptFromStandbyBuckets(classLoader)`，并新增方法：

```kotlin
    /**
     * Exact alarms from background-restricted standby buckets are throttled.
     * System apps are exempt — force our bucket report to ACTIVE so the
     * engine's setExactAndAllowWhileIdle alarms fire on time, like a system
     * app's would. Best-effort: if MIUI renamed the method, the module log
     * says so and alarms still fire (allowWhileIdle bypasses most throttling).
     */
    private fun exemptFromStandbyBuckets(classLoader: ClassLoader) {
        val controller = try {
            classLoader.loadClass(APP_STANDBY_CONTROLLER)
        } catch (t: Throwable) {
            log("AppStandbyController not found: ${t.message}")
            return
        }
        // AOSP: getAppStandbyBucket(String packageName, int userId,
        // long elapsedRealtime, boolean shouldMinimizeUsage)
        val method = controller.declaredMethods.firstOrNull {
            it.name == "getAppStandbyBucket" &&
                    it.parameterTypes.firstOrNull() == String::class.java
        }
        if (method == null) {
            log("getAppStandbyBucket(String,...) not found")
            return
        }
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    if (chain.getArg(0) == Protocol.MODULE_PACKAGE) {
                        return android.app.usage.UsageStatsManager.STANDBY_BUCKET_ACTIVE
                    }
                    return chain.proceed()
                }
            })
        log("getAppStandbyBucket hooked")
    }
```

并在 companion object 中添加常量：

```kotlin
        private const val APP_STANDBY_CONTROLLER = "com.android.server.usage.AppStandbyController"
```

- [ ] **Step 4: 编译验证**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/hook/SystemModeHook.kt app/src/main/java/com/banana/hypermodes/XposedInit.kt app/src/main/java/com/banana/hypermodes/hook/SystemKeepAliveHook.kt
git commit -m "feat: add system_server bridge for suspend/bypassDnd, standby bucket exemption"
```

---

### Task 6: DrivingDetector + BedtimeStateReceiver 接入引擎

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/driving/DrivingDetector.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt`

**Interfaces:**
- Consumes: `ModeEngine.activate/deactivate`（Task 3）、`DefaultModes.get()`（Task 2）
- Produces: 无新接口（行为接线）

- [ ] **Step 1: DrivingDetector 改用 ModeEngine**

`driving/DrivingDetector.kt` 的 `setDrivingActive` 改为：

```kotlin
    private fun setDrivingActive(context: Context, mode: Mode, active: Boolean) {
        val updated = mode.copy(enabled = active)
        val modes = ModeStore.load(context) { DefaultModes.get() }
        ModeStore.save(context, modes.map { if (it.id == "driving") updated else it })
        val engine = com.banana.hypermodes.engine.ModeEngine(context)
        if (active) engine.activate(updated) else engine.deactivate(updated)
    }
```

并移除文件顶部的 `import com.banana.hypermodes.manager.ModeManager`。

- [ ] **Step 2: BedtimeStateReceiver 应用 bedtime 全部附加设置**

整体替换 `receiver/BedtimeStateReceiver.kt`：

```kotlin
package com.banana.hypermodes.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.engine.ModeEngine
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.DeskClockState

/**
 * Receives the DeskClock hook's push when the OFFICIAL bedtime state changes
 * (scheduled sleep/wake alarms, or the user toggling bedtime inside the Clock
 * app). Manifest-registered so it works even when our UI isn't running —
 * the system_server keep-alive hooks allow this broadcast to cold-start us.
 *
 * Beyond syncing the toggle, this is where bedtime's EXTRA settings
 * (DND policy, grayscale, ...) get applied on scheduled activation:
 * the engine runs with skipBedtimeTrigger = true (DeskClock is already
 * driving the bedtime itself — re-sending START_BEDTIME would loop).
 *
 * Idempotency: if ModeStore already shows the same enabled flag, we
 * initiated the change ourselves (manual toggle already ran the engine)
 * and there is nothing to do.
 */
class BedtimeStateReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Protocol.ACTION_BEDTIME_ACTIVE) return
        val active = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)

        DeskClockState.updateBedtimeActive(context, active)

        val modes = ModeStore.load(context) { DefaultModes.get() }.toMutableList()
        val idx = modes.indexOfFirst { it.id == "bedtime" }
        if (idx < 0) return
        if (modes[idx].enabled == active) return // we initiated this ourselves

        val updated = modes[idx].copy(enabled = active)
        modes[idx] = updated
        ModeStore.save(context, modes)

        val engine = ModeEngine(context)
        if (active) {
            engine.activate(updated, skipBedtimeTrigger = true)
        } else {
            engine.deactivate(updated, skipBedtimeTrigger = true)
        }
        context.sendBroadcast(
            Intent(Protocol.ACTION_MODE_STATE).setPackage(context.packageName)
        )
    }
}
```

- [ ] **Step 3: 编译验证**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/driving/DrivingDetector.kt app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt
git commit -m "feat: route driving detection and bedtime pushes through ModeEngine"
```

---

### Task 7: UI 接线（减法 + 暂停应用选择 + 精确闹钟提示）+ 删除 ModeManager

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/ui/HyperModesApp.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/ui/ModeDetailScreen.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/ui/AppPickerScreen.kt`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`
- Delete: `app/src/main/java/com/banana/hypermodes/manager/ModeManager.kt`

**Interfaces:**
- Consumes: `ModeEngine.activate/deactivate`（Task 3）、`DefaultModes.get()`（Task 2）、Protocol `ACTION_RESCHEDULE` / `ACTION_MODE_STATE`（Task 2）
- Produces: 无（终端改动）

- [ ] **Step 1: 新字符串**

`app/src/main/res/values/strings.xml` 的 `</resources>` 之前：

```xml
    <string name="paused_apps">Paused apps</string>
    <string name="no_apps_paused">No apps paused</string>
    <string name="apps_paused">%1$d paused</string>
    <string name="exact_alarm_title">Allow exact alarms</string>
    <string name="exact_alarm_desc">Scheduled modes may fire late until exact alarms are allowed.</string>
    <string name="grant_permission">Allow</string>
```

`app/src/main/res/values-zh-rCN/strings.xml` 的 `</resources>` 之前：

```xml
    <string name="paused_apps">暂停应用</string>
    <string name="no_apps_paused">未暂停任何应用</string>
    <string name="apps_paused">已暂停 %1$d 个</string>
    <string name="exact_alarm_title">允许精确闹钟</string>
    <string name="exact_alarm_desc">未授权精确闹钟，定时模式可能延迟触发。</string>
    <string name="grant_permission">去授权</string>
```

- [ ] **Step 2: AppPickerScreen 参数化（放行 / 暂停）**

`ui/AppPickerScreen.kt` 的函数签名改为（新增 `paused` 参数，默认 false 保持旧行为）：

```kotlin
/**
 * App picker. [paused] = false: tick apps that may interrupt (allowedApps);
 * [paused] = true: tick apps to suspend while the mode is on (pausedApps).
 */
@Composable
fun AppPickerScreen(
    mode: Mode,
    paused: Boolean = false,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
```

TopAppBar title 改为：

```kotlin
                title = stringResource(if (paused) R.string.paused_apps else R.string.select_apps),
```

列表项的选中读取（原 `val allowed = editedMode.settings.allowedApps.contains(app.packageName)`）改为：

```kotlin
                    val selectedSet = if (paused) editedMode.settings.pausedApps
                    else editedMode.settings.allowedApps
                    val allowed = selectedSet.contains(app.packageName)
```

Switch 的 `onCheckedChange` 改为：

```kotlin
                                onCheckedChange = { on ->
                                    val newApps = if (on) selectedSet + app.packageName
                                    else selectedSet - app.packageName
                                    editedMode = editedMode.copy(
                                        settings = if (paused) {
                                            editedMode.settings.copy(pausedApps = newApps)
                                        } else {
                                            editedMode.settings.copy(allowedApps = newApps)
                                        }
                                    )
                                    onSave(editedMode)
                                }
```

- [ ] **Step 3: ModeDetailScreen — 引擎开关 + 暂停应用行**

3a. import 替换：`import com.banana.hypermodes.manager.ModeManager` → `import com.banana.hypermodes.engine.ModeEngine`。

3b. 函数签名新增回调参数：

```kotlin
@Composable
fun ModeDetailScreen(
    mode: Mode,
    onBack: () -> Unit,
    onOpenDisplayOptions: (Mode) -> Unit,
    onOpenRepeat: (Mode) -> Unit,
    onOpenApps: (Mode) -> Unit,
    onOpenPausedApps: (Mode) -> Unit,
    onOpenDrivingDetect: (Mode) -> Unit,
    onRename: (Mode) -> Unit,
    onDelete: (Mode) -> Unit,
    onSave: (Mode) -> Unit
) {
```

3c. 立即开启/关闭按钮的 onClick（原 `ModeManager(context).activateMode/deactivateMode`）改为：

```kotlin
                        onClick = {
                            val enabled = !editedMode.enabled
                            editedMode = editedMode.copy(enabled = enabled)
                            val engine = ModeEngine(context)
                            if (enabled) {
                                engine.activate(editedMode)
                            } else {
                                engine.deactivate(editedMode)
                            }
                            onSave(editedMode)
                        }
```

3d. "更多设置"区显示设置行之后追加暂停应用行（在 `displayOptionsSummary` 那个 `item { SettingItem(title = stringResource(R.string.display_settings), ...) }` 之后）：

```kotlin
            // Paused apps row: which apps get suspended while the mode is on
            item {
                SettingItem(
                    title = stringResource(R.string.paused_apps),
                    subtitle = if (editedMode.settings.pausedApps.isEmpty()) {
                        stringResource(R.string.no_apps_paused)
                    } else {
                        stringResource(R.string.apps_paused, editedMode.settings.pausedApps.size)
                    },
                    checked = false,
                    onCheckedChange = {},
                    onClick = { onOpenPausedApps(editedMode) },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }
```

- [ ] **Step 4: HyperModesApp 接线**

4a. import：`import com.banana.hypermodes.engine.ModeEngine`、`import com.banana.hypermodes.data.DefaultModes`（Task 2 已加则跳过重复）。

4b. `Screen.AppPicker` 加 paused 字段：

```kotlin
    data class AppPicker(val mode: Mode, val paused: Boolean = false) : Screen()
```

4c. 提取排序为顶层私有函数（放在 `sealed class Screen` 之后），供初次加载与 MODE_STATE 刷新共用：

```kotlin
/** Official ordering: DND, Bedtime, Driving, then custom modes by name. */
private fun sortModes(list: List<Mode>): List<Mode> = list.sortedWith(
    compareBy(
        { when (it.id) { "dnd" -> 0; "bedtime" -> 1; "driving" -> 2; else -> 3 } },
        { it.name }
    )
)
```

4d. 初次加载（`LaunchedEffect(Unit)` 中 modes 赋值）改为使用 sortModes：

```kotlin
        modes = sortModes(
            ModeStore.load(context) { DefaultModes.get() }.map {
                // Bedtime's enabled flag mirrors the official DeskClock state
                if (it.id == "bedtime") it.copy(enabled = DeskClockState.bedtimeActive) else it
            }
        )
```

4e. `persistModes` 追加 RESCHEDULE 广播（任何模式保存/删除后引擎重排闹钟）：

```kotlin
    fun persistModes(updated: List<Mode>) {
        modes = updated
        ModeStore.save(context, updated)
        context.sendBroadcast(
            Intent(Protocol.ACTION_RESCHEDULE).setPackage(context.packageName)
        )
    }
```

4f. 新增 MODE_STATE 监听（引擎定时触发后刷新列表），放在现有 ACTION_RESULT 监听的 `DisposableEffect` 之后：

```kotlin
    // Refresh the mode list when the engine activates/deactivates a mode
    // (scheduled trigger, bedtime push) while the UI is alive.
    DisposableEffect(Unit) {
        val receiver = object : android.content.BroadcastReceiver() {
            override fun onReceive(c: Context, intent: android.content.Intent) {
                modes = sortModes(
                    ModeStore.load(context) { DefaultModes.get() }.map {
                        if (it.id == "bedtime") {
                            it.copy(enabled = DeskClockState.bedtimeActive)
                        } else it
                    }
                )
            }
        }
        androidx.core.content.ContextCompat.registerReceiver(
            context, receiver,
            android.content.IntentFilter(Protocol.ACTION_MODE_STATE),
            androidx.core.content.ContextCompat.RECEIVER_NOT_EXPORTED
        )
        onDispose { context.unregisterReceiver(receiver) }
    }
```

4g. `onDelete`（`Screen.ModeDetail` 分支）：删除激活中的模式先走引擎恢复系统设置：

```kotlin
                        onDelete = { deleted ->
                            if (deleted.enabled) {
                                ModeEngine(context).deactivate(deleted)
                            }
                            persistModes(modes.filterNot { it.id == deleted.id })
                            when (deleted.id) {
                                // ... 原有 driving/bedtime 分支保持不变 ...
                            }
                            editingMode = null
                            currentScreen = Screen.ModesList
                        },
```

4h. `Screen.AppPicker` 分支传入 paused：

```kotlin
                is Screen.AppPicker -> {
                    AppPickerScreen(
                        mode = editingMode ?: screen.mode,
                        paused = screen.paused,
                        onBack = { currentScreen = Screen.ModeDetail(editingMode ?: screen.mode) },
                        onSave = { updatedMode ->
                            editingMode = updatedMode
                            upsertMode(updatedMode)
                        }
                    )
                }
```

4i. `Screen.ModeDetail` 分支的 ModeDetailScreen 调用新增两个回调（`onOpenApps` 之后）：

```kotlin
                        onOpenPausedApps = { updated ->
                            editingMode = updated
                            currentScreen = Screen.AppPicker(updated, paused = true)
                        },
```

- [ ] **Step 5: 精确闹钟提示卡（ModesListScreen）**

在 `ModesListScreen` 的 `LazyColumn` 中，描述文本 item 之后、模式列表之前插入（仅当存在启用中的定时模式且未授权精确闹钟时显示）：

```kotlin
            // Exact-alarm nudge: without it, scheduled modes may fire late.
            val alarmManager = remember {
                context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            }
            val exactAlarmsMissing = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S &&
                    !alarmManager.canScheduleExactAlarms() &&
                    modes.any { it.settings.schedule?.enabled == true && it.id != "bedtime" }
            if (exactAlarmsMissing) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.exact_alarm_title),
                                style = MiuixTheme.textStyles.body1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.exact_alarm_desc),
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                text = stringResource(R.string.grant_permission),
                                colors = ButtonDefaults.textButtonColorsPrimary(),
                                onClick = {
                                    context.startActivity(
                                        Intent(
                                            android.provider.Settings
                                                .ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                                        )
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
```

注意：LazyColumn 作用域中 `modes` 是参数，`context` 已在函数内定义（`LocalContext.current`），`Intent` 已在文件 import。`android.app.AlarmManager`/`Build`/`Settings` 用全限定名避免新增 import 冲突。

- [ ] **Step 6: 删除 ModeManager.kt**

```bash
git rm app/src/main/java/com/banana/hypermodes/manager/ModeManager.kt
```

- [ ] **Step 7: 全量编译 + 单测**

Run: `./gradlew :app:assembleDebug :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL，无 `ModeManager` 残留引用，ModeSchedulerTest 全绿

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/ui/ app/src/main/res/values/strings.xml app/src/main/res/values-zh-rCN/strings.xml
git commit -m "feat: wire UI to ModeEngine, add paused-apps picker, drop ModeManager"
```

---

### Task 8: 真机端到端验证

**Files:**
- 无代码改动（验证脚本与通过标准）

**Interfaces:**
- Consumes: 全部前序任务
- Produces: 验证记录（在 PR/提交说明中粘贴结果）

前置：LSPosed 中模块已勾选 DeskClock + 系统框架 + 设置（com.android.settings）作用域，并已重启过一次（SystemModeHook/SystemKeepAliveHook 需要）。

- [ ] **Step 1: 安装**

Run: `./gradlew :app:installDebug`
Expected: INSTALL SUCCESSFUL；打开 app 确认模式列表正常渲染

- [ ] **Step 2: 验证闹钟已排**

在 app 里给一个自定义模式设置 2 分钟后的开始时间（结束时间 = 开始 + 3 分钟），保存后：

Run: `adb shell dumpsys alarm | grep -B2 -A4 banana`
Expected: 看到 `com.banana.hypermodes` 的 RTC_WAKEUP 闹钟，触发时间与设定一致

- [ ] **Step 3: 杀进程后定时激活**

Run: `adb shell am kill com.banana.hypermodes`
然后等到开始时间之后：

Run: `adb shell settings get secure accessibility_display_daltonizer_enabled`（若该模式开了灰度）
Run: `adb shell dumpsys notification | grep -m1 mInterruptionFilter`
Expected: 灰度 = 1（开启时）/ DND filter 与模式设置一致；结束时间过后两者恢复原值（快照恢复）

- [ ] **Step 4: 暂停应用**

在模式里选 1 个暂停应用并手动开启模式：

Run: `adb shell dumpsys package <被暂停包名> | grep -i suspended`
Expected: `suspended=true`；关闭模式后为 `suspended=false`（LSPosed 日志可见 `setPackagesSuspended(true)`）

- [ ] **Step 5: 双模式叠加引用计数**

两个模式都开灰度/DND：开启 A 再开启 B，关闭 A —— 灰度/DND 仍在；关闭 B —— 恢复。
Expected: 中间不闪烁恢复，`dumpsys notification` 的 filter 在关 A 后不变

- [ ] **Step 6: 重启自愈**

Run: `adb reboot`
重启后不打开 app，重复 Step 2 检查闹钟已重排；到点触发正常。

- [ ] **Step 7: 定时就寝附加设置**

将 DeskClock 就寝时间设为 2 分钟后，等到触发：
Expected: 除 DeskClock 进入就寝外，灰度/DND policy 随之应用（LSPosed 日志有 `enterZenMode -> bedtime active=true`）；起床时间后全部恢复。

- [ ] **Step 8: 记录结果并提交验证说明**

把 Step 2-7 的实际输出整理进提交说明或 PR 描述；如有失败项回到对应 Task 修复。

```bash
git commit --allow-empty -m "test: on-device verification of the mode engine (see message body)"
```

---

## Self-Review 结论

- **Spec 覆盖**：定时调度（Task 1/4）、系统级豁免（Task 5）、暂停应用做实（Task 3/5/7）、联系人过滤 policy（Task 3）、按应用放行（Task 3/5）、bedtime 附加设置补齐（Task 6）、驾驶走引擎（Task 6）、UI 减法与 RESCHEDULE/MODE_STATE（Task 7）、手动/定时交互语义（Task 4 的 enabled 检查 + Task 7 手动开关）、错误降级（各 Task 的 try/catch + exact alarm fallback）、测试（Task 1 单测 + Task 8 真机）。spec 中"电池优化白名单"一项收缩为 `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` 权限声明（Task 4）——UI 暂不弹该授权（闹钟 allowWhileIdle 已覆盖 doze），如需再加。
- **已知有意不做**（与 Global Constraints 一致）：dimWallpaper/keepScreenOn/keepScreenOff/hideNotifications 仍是仅存储标志位。
- **类型一致性**：`ModeEngine.activate/deactivate(mode, skipBedtimeTrigger)`、`EngineReceiver.rescheduleAll(context)`、`DefaultModes.get()`、`AppPickerScreen(mode, paused, onBack, onSave)`、`ModeDetailScreen(..., onOpenPausedApps, ...)` 在各 Task 间签名一致。
