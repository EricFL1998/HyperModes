# HyperOS 3 Focus Control Center Card Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 通过 LSPosed 将 HyperModes 作为原生 1×2 card-style QSTile 自动加入 HyperOS 3 控制中心，并支持当前/上次模式展示、单击开关、长按原生 Detail 切换及实时刷新。

**Architecture:** 在 `com.android.systemui` 中捕获 `miui.systemui.plugin` 的 ClassLoader，Hook `QSController.getCardStyleTileSpecs()` 追加 `hypermodes_focus`，并 Hook `QSController.createTile(String)` 返回反射动态实现的当前版 `QSTile`。HyperOS 原生 `QSCardsController → QSRecord → QSCardItemView` 继续负责 2-span 布局、卡片样式、动画、触觉反馈和 Detail 路由；HyperModes 仅负责配置状态、图标、点击和模式列表。

**Tech Stack:** Kotlin 2.4.10, Android minSdk 35 / compileSdk 37, libxposed API 101.0.1, kotlinx.serialization 1.7.3, Java reflection/proxy, Settings.Global, ContentObserver, JUnit 4.13.2, HyperOS 3 MIUISystemUIPlugin 17.1.4.71.0 private APIs.

## Global Constraints

- 目标设备基线：Android 16、HyperOS `OS3.0.317.0.WBLCNXM`、`MIUISystemUIPlugin 17.1.4.71.0`。
- 唯一卡片 spec：`hypermodes_focus`。
- 卡片只加入 `getCardStyleTileSpecs()`，不得加入普通快捷开关列表或编辑页。
- 卡片必须由原生 `QSRecord(isCard = true)` 产生 view type `2273` 和正常 span size `2`；不得手工向 RecyclerView 插入独立 View。
- 模式唯一状态源：`Settings.Global["pixel_routines_full_config"]`。
- 同一时间最多一个模式运行，不实现多模式优先级。
- 首次无历史时仅随机一次并持久化；存在有效 `lastModeId` 后不得因展开面板、SystemUI 重启或设备重启重新随机。
- 现有配置 JSON 必须向后兼容；解析失败时不得覆盖原 JSON。
- 所有 HyperOS 私有 API 均通过插件 ClassLoader 反射，不新增编译期 SystemUI 依赖。
- 所有 Hook 使用 `ExceptionMode.PROTECTIVE`，任何失败不得传播到 SystemUI 主线程。
- 保留用户当前工作区中与本功能无关的未提交修改，不做无关重构。
- 当前处于 `main` 且用户未要求提交：每个任务只做测试与 diff 检查，不执行 `git commit`。
- PowerShell 下使用 `.\gradlew.bat`；Git Bash 下使用 `./gradlew`。

---

## File Structure

### New production files

- `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardStateRepository.kt`
  - 纯 Kotlin 状态选择与配置写入；定义配置存储接口、snapshot 和首次随机算法。
- `app/src/main/java/com/banana/hypermodes/controlcenter/GlobalFocusCardConfigStore.kt`
  - Settings.Global 读写及 ContentObserver 生命周期。
- `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileClasses.kt`
  - 从插件 ClassLoader 解析 QSTile、BooleanState、DrawableIcon、DetailAdapter 等类。
- `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt`
  - QSTile 动态代理、callback、listening、状态构建、图标和单击/长按。
- `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
  - DetailAdapter 动态代理和模式列表 View。

### Modified production files

- `app/src/main/java/com/banana/hypermodes/systemserver/config/ModeConfig.kt`
  - `FullConfig` 增加可选 `lastModeId`。
- `app/src/main/java/com/banana/hypermodes/systemserver/config/ConfigParser.kt`
  - 激活时同步历史、关闭时保留历史，新增仅更新历史的方法。
- `app/src/main/java/com/banana/hypermodes/data/ModeStore.kt`
  - 保存模式列表时保留 `lastModeId`。
- `app/src/main/java/com/banana/hypermodes/hook/Reflect.kt`
  - 增加布尔字段写入和安全字段查找，供 QSTile.State 反射赋值。
- `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
  - 实现插件内 `QSController` 两个核心 Hook。
- `app/src/main/java/com/banana/hypermodes/XposedInit.kt`
  - 只在 SystemUI 插件加载后安装新卡片 Hook，移除旧路线入口。
- `app/src/main/res/values/strings.xml`
- `app/src/main/res/values-zh-rCN/strings.xml`
  - Focus Detail 标题、空状态和打开应用文本。

### Removed legacy files after replacement passes tests

- `app/src/main/java/com/banana/hypermodes/hook/ControlCenterHook.kt`
- `app/src/main/java/com/banana/hypermodes/tile/FocusTileProvider.kt`
- `app/src/main/java/com/banana/hypermodes/tile/FocusDetailAdapter.kt`

### New tests

- `app/src/test/java/com/banana/hypermodes/systemserver/config/FocusCardConfigParserTest.kt`
- `app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardStateRepositoryTest.kt`
- `app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardTileProviderTest.kt`
- `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`
- `app/src/test/java/com/banana/hypermodes/hook/ControlCenterCardHookTest.kt`

---

### Task 1: Persist `lastModeId` without breaking existing configs

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/config/ModeConfig.kt:98-102`
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/config/ConfigParser.kt:38-50`
- Modify: `app/src/main/java/com/banana/hypermodes/data/ModeStore.kt:33-53`
- Test: `app/src/test/java/com/banana/hypermodes/systemserver/config/FocusCardConfigParserTest.kt`

**Interfaces:**
- Produces: `FullConfig.lastModeId: String?`
- Produces: `ConfigParser.updateActiveModeId(jsonString: String, modeId: String?): String`
  - 非空 `modeId` 同时写入 `activeModeId` 与 `lastModeId`。
  - 空 `modeId` 只清空 `activeModeId`，保留 `lastModeId`。
- Produces: `ConfigParser.updateLastModeId(jsonString: String, modeId: String?): String`
- Later tasks consume these exact signatures.

- [ ] **Step 1: Write failing compatibility and update tests**

Create `FocusCardConfigParserTest.kt`:

```kotlin
package com.banana.hypermodes.systemserver.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FocusCardConfigParserTest {
    private val modeJson = """
        {
          "activeModeId": null,
          "modes": [{
            "id": "work",
            "name": "Work",
            "icon": "💼",
            "type": "SCHEDULED",
            "notification": {"dndLevel": "PRIORITY", "allowedApps": []},
            "display": {},
            "pausedApps": []
          }]
        }
    """.trimIndent()

    @Test
    fun `legacy config without lastModeId parses as null`() {
        assertNull(ConfigParser.parseConfig(modeJson).lastModeId)
    }

    @Test
    fun `activation records active and last mode`() {
        val config = ConfigParser.parseConfig(
            ConfigParser.updateActiveModeId(modeJson, "work")
        )
        assertEquals("work", config.activeModeId)
        assertEquals("work", config.lastModeId)
    }

    @Test
    fun `deactivation preserves last mode`() {
        val active = ConfigParser.updateActiveModeId(modeJson, "work")
        val config = ConfigParser.parseConfig(
            ConfigParser.updateActiveModeId(active, null)
        )
        assertNull(config.activeModeId)
        assertEquals("work", config.lastModeId)
    }

    @Test
    fun `last mode can be initialized without activation`() {
        val config = ConfigParser.parseConfig(
            ConfigParser.updateLastModeId(modeJson, "work")
        )
        assertNull(config.activeModeId)
        assertEquals("work", config.lastModeId)
    }
}
```

- [ ] **Step 2: Run the focused test and verify it fails**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.config.FocusCardConfigParserTest"
```

Expected: compilation failure because `lastModeId` and `updateLastModeId` do not exist.

- [ ] **Step 3: Add the backward-compatible field and parser operations**

Change `FullConfig` to:

```kotlin
@Serializable
data class FullConfig(
    val activeModeId: String? = null,
    val lastModeId: String? = null,
    val modes: List<ModeConfig>
)
```

Change parser operations to:

```kotlin
fun updateActiveModeId(jsonString: String, modeId: String?): String {
    val config = parseConfig(jsonString)
    val updated = if (modeId == null) {
        config.copy(activeModeId = null)
    } else {
        config.copy(activeModeId = modeId, lastModeId = modeId)
    }
    return serializeConfig(updated)
}

fun updateLastModeId(jsonString: String, modeId: String?): String {
    val config = parseConfig(jsonString)
    return serializeConfig(config.copy(lastModeId = modeId))
}
```

- [ ] **Step 4: Preserve history when the app saves the mode list**

In `ModeStore.save`, parse the current config once and build:

```kotlin
val existing = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
    ?.let { ConfigParser.parseConfig(it) }
val fullConfig = FullConfig(
    activeModeId = existing?.activeModeId,
    lastModeId = existing?.lastModeId,
    modes = modeConfigs
)
```

Keep the existing catch-and-log behavior. Do not replace malformed existing JSON with an empty config in this task.

- [ ] **Step 5: Run config tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.systemserver.config.*"
```

Expected: all config tests pass, including legacy JSON tests.

- [ ] **Step 6: Review the task diff**

Run:

```powershell
git diff --check -- app/src/main/java/com/banana/hypermodes/systemserver/config app/src/main/java/com/banana/hypermodes/data/ModeStore.kt app/src/test/java/com/banana/hypermodes/systemserver/config
```

Expected: no whitespace errors.

---

### Task 2: Implement the pure Focus card state repository

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardStateRepository.kt`
- Test: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardStateRepositoryTest.kt`

**Interfaces:**
- Consumes: `ConfigParser`, `FullConfig`, `ModeConfig` from Task 1.
- Produces:

```kotlin
interface FocusCardConfigStore {
    fun read(): String?
    fun write(json: String): Boolean
}

fun interface ModeIndexSelector {
    fun select(size: Int): Int
}

data class FocusCardSnapshot(
    val modes: List<ModeConfig>,
    val displayedMode: ModeConfig?,
    val activeModeId: String?,
    val isActive: Boolean,
    val configValid: Boolean
)

class FocusCardStateRepository(
    private val store: FocusCardConfigStore,
    private val selector: ModeIndexSelector
) {
    fun loadOrInitialize(): FocusCardSnapshot
    fun activate(modeId: String): Boolean
    fun deactivate(): Boolean
}
```

- Task 3 supplies the production store.
- Tasks 4 and 5 consume `loadOrInitialize`, `activate`, and `deactivate`.

- [ ] **Step 1: Write failing state-selection tests**

Create tests with a `FakeStore` and deterministic selector. Cover these exact cases:

```kotlin
@Test fun `active mode wins and becomes last mode`()
@Test fun `valid last mode is shown while inactive`()
@Test fun `first load selects once and persists selection`()
@Test fun `persisted selection is not rerolled on later loads`()
@Test fun `deleted last mode selects a valid replacement`()
@Test fun `empty mode list produces unavailable snapshot`()
@Test fun `malformed json is not overwritten`()
@Test fun `activate rejects unknown mode`()
@Test fun `deactivate preserves history`()
```

Use this fake:

```kotlin
private class FakeStore(var json: String?) : FocusCardConfigStore {
    val writes = mutableListOf<String>()
    override fun read(): String? = json
    override fun write(json: String): Boolean {
        writes += json
        this.json = json
        return true
    }
}
```

For the first-use test, inject `ModeIndexSelector { size -> size - 1 }`, assert that the last mode is chosen, exactly one write occurs, and the second load performs no additional write.

- [ ] **Step 2: Run the focused test and verify it fails**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusCardStateRepositoryTest"
```

Expected: compilation failure because repository types do not exist.

- [ ] **Step 3: Implement state selection**

Implement `loadOrInitialize()` with this sequence:

```kotlin
val raw = store.read() ?: return unavailable(configValid = true)
val config = try {
    ConfigParser.parseConfig(raw)
} catch (_: Exception) {
    return unavailable(configValid = false)
}
val byId = config.modes.associateBy { it.id }
val active = config.activeModeId?.let(byId::get)
if (active != null) {
    if (config.lastModeId != active.id) {
        store.write(ConfigParser.updateLastModeId(raw, active.id))
    }
    return FocusCardSnapshot(config.modes, active, active.id, true, true)
}
val remembered = config.lastModeId?.let(byId::get)
if (remembered != null) {
    return FocusCardSnapshot(config.modes, remembered, null, false, true)
}
if (config.modes.isEmpty()) return unavailable(configValid = true)
val index = selector.select(config.modes.size).coerceIn(0, config.modes.lastIndex)
val selected = config.modes[index]
store.write(ConfigParser.updateLastModeId(raw, selected.id))
return FocusCardSnapshot(config.modes, selected, null, false, true)
```

`unavailable()` returns no displayed mode, no active ID, `isActive = false`, and the supplied `configValid`.

- [ ] **Step 4: Implement activation and deactivation**

`activate(modeId)` must parse the latest store value, reject an unknown ID, call `ConfigParser.updateActiveModeId(raw, modeId)`, and return the store write result.

`deactivate()` must parse the latest store value, call `updateActiveModeId(raw, null)`, and preserve the history through Task 1 semantics.

Neither operation may synthesize a replacement JSON when the input is missing or malformed.

- [ ] **Step 5: Run repository and config tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusCardStateRepositoryTest" --tests "com.banana.hypermodes.systemserver.config.*"
```

Expected: all pass.

- [ ] **Step 6: Review the task diff**

Run:

```powershell
git diff --check -- app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardStateRepository.kt app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardStateRepositoryTest.kt
```

Expected: no errors.

---

### Task 3: Add the Settings.Global store and observer lifecycle

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/GlobalFocusCardConfigStore.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardStateRepository.kt`
- Test: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardStateRepositoryTest.kt`

**Interfaces:**
- Extend the store contract with:

```kotlin
interface ObservableFocusCardConfigStore : FocusCardConfigStore {
    fun observe(onChanged: () -> Unit): AutoCloseable
}
```

- Produce:

```kotlin
class GlobalFocusCardConfigStore(
    context: Context,
    private val handler: Handler = Handler(Looper.getMainLooper())
) : ObservableFocusCardConfigStore
```

- Task 4 consumes `ObservableFocusCardConfigStore.observe`.

- [ ] **Step 1: Add a repository test for failed writes**

Add tests proving `activate`, `deactivate`, and first-use initialization return/retain a usable snapshot when `write()` returns `false`, without pretending the in-memory state changed.

Use a fake store whose `write()` records the attempted JSON but leaves `json` unchanged.

- [ ] **Step 2: Run the test and verify the new case fails**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusCardStateRepositoryTest"
```

Expected: the new failed-write assertion exposes any optimistic mutation.

- [ ] **Step 3: Implement the production store**

Use the exact key:

```kotlin
private const val CONFIG_KEY = "pixel_routines_full_config"
```

Implement:

```kotlin
override fun read(): String? =
    Settings.Global.getString(context.contentResolver, CONFIG_KEY)

override fun write(json: String): Boolean =
    Settings.Global.putString(context.contentResolver, CONFIG_KEY, json)
```

`observe` must register a `ContentObserver` on `Settings.Global.getUriFor(CONFIG_KEY)` with `notifyForDescendants = false`. Return an idempotent `AutoCloseable` that unregisters exactly once.

Use `context.applicationContext ?: context` only for retaining Context; continue using that Context's ContentResolver.

- [ ] **Step 4: Make repository writes authoritative**

After a successful write, later reads determine state. After a failed write, do not return a fabricated active/last state. This keeps Settings.Global authoritative.

- [ ] **Step 5: Run all JVM tests**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

Expected: all current tests pass.

- [ ] **Step 6: Compile the Android implementation**

Run:

```powershell
.\gradlew.bat :app:compileDebugKotlin
```

Expected: successful Kotlin compilation.

---

### Task 4: Build the current-version QSTile proxy

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileClasses.kt`
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusCardTileProvider.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/hook/Reflect.kt`
- Modify: `app/src/test/java/com/banana/hypermodes/hook/ReflectTest.kt`
- Test: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusCardTileProviderTest.kt`

**Interfaces:**
- Consumes: `FocusCardStateRepository`, `ObservableFocusCardConfigStore`.
- Produces:

```kotlin
internal data class FocusCardTileClasses(
    val tileInterface: Class<*>,
    val booleanStateClass: Class<*>,
    val drawableIconClass: Class<*>,
    val detailAdapterInterface: Class<*>
) {
    companion object {
        fun resolve(classLoader: ClassLoader): FocusCardTileClasses
    }
}

fun interface FocusCardDetailFactory {
    fun create(onDismiss: () -> Unit): Any?
}

class FocusCardTileProvider(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val classes: FocusCardTileClasses,
    private val repository: FocusCardStateRepository,
    private val observableStore: ObservableFocusCardConfigStore,
    private val detailFactory: FocusCardDetailFactory?,
    private val postToUi: ((() -> Unit) -> Unit)
) {
    fun create(): Any
}
```

- Task 5 supplies `FocusCardDetailFactory`.
- Task 6 constructs this provider from the Hook.

- [ ] **Step 1: Extend reflection tests**

Add to `ReflectTest`:

```kotlin
@Test
fun `setBooleanField writes private boolean fields`() {
    class FlagFixture { private var enabled = false; fun enabled() = enabled }
    val fixture = FlagFixture()
    Reflect.setBooleanField(fixture, "enabled", true)
    assertEquals(true, fixture.enabled())
}
```

- [ ] **Step 2: Add fake QSTile contract tests**

In `FocusCardTileProviderTest`, define test-only interfaces/classes with current QSTile method names:

```kotlin
private interface FakeCallback {
    fun onStateChanged(state: FakeBooleanState)
    fun onShowDetail(show: Boolean)
}

private class FakeBooleanState {
    @JvmField var spec: String? = null
    @JvmField var label: CharSequence? = null
    @JvmField var contentDescription: CharSequence? = null
    @JvmField var icon: Any? = null
    @JvmField var state: Int = 0
    @JvmField var value: Boolean = false
    @JvmField var dualTarget: Boolean = true
    @JvmField var handlesLongClick: Boolean = false
    @JvmField var handlesSecondaryClick: Boolean = true
}
```

The fake tile interface must include at least:

```kotlin
fun addCallback(callback: FakeCallback)
fun removeCallback(callback: FakeCallback)
fun removeCallbacks()
fun getState(): FakeBooleanState
fun getTileSpec(): String
fun getTileLabel(): CharSequence
fun isAvailable(): Boolean
fun isListening(): Boolean
fun isDestroyed(): Boolean
fun isTileReady(): Boolean
fun getCurrentTileUser(): Int
fun getMetricsCategory(): Int
fun setListening(token: Any, listening: Boolean)
fun click()
fun longClick()
fun refreshState()
fun destroy()
```

Test:

- active snapshot maps to `state = 2`, `value = true`;
- remembered inactive maps to `state = 1`, `value = false`;
- empty modes map to `state = 0` and `isAvailable() == false`;
- callback receives `onStateChanged` after refresh;
- multiple listener tokens create one observer and closing the last token closes it;
- click deactivates active mode and activates inactive displayed mode;
- long click invokes `onShowDetail(true)`;
- destroy closes observer, clears callbacks, and makes `isDestroyed()` true;
- primitive-returning methods never return null;
- `equals`, `hashCode`, and `toString` behave safely.

- [ ] **Step 3: Run the tests and verify they fail**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ReflectTest" --tests "com.banana.hypermodes.controlcenter.FocusCardTileProviderTest"
```

Expected: compilation failure for missing classes/methods.

- [ ] **Step 4: Implement class resolution**

Resolve exact names:

```kotlin
val tile = classLoader.loadClass("com.android.systemui.plugins.qs.QSTile")
val state = classLoader.loadClass("com.android.systemui.plugins.qs.QSTile\$BooleanState")
val icon = classLoader.loadClass("miui.systemui.controlcenter.qs.DrawableIcon")
val detail = classLoader.loadClass("com.android.systemui.plugins.qs.DetailAdapter")
```

Validate `DrawableIcon` has a single-`Drawable` constructor. Throw a descriptive exception containing the missing class/method name; Task 6 catches it.

- [ ] **Step 5: Implement the invocation handler**

Use `Proxy.newProxyInstance(classes.tileInterface.classLoader, arrayOf(classes.tileInterface), handler)`.

Handle the current interface explicitly:

- callbacks: `addCallback`, `removeCallback`, `removeCallbacks`, `removeCallbacksByType`;
- lifecycle: `setListening`, `isListening`, `destroy`, `isDestroyed`;
- state: `getState`, `refreshState`, `getTileLabel`, `getTileSpec`, `setTileSpec`;
- interaction: both zero/one-argument forms of `click`, `longClick`, and `secondaryClick`;
- detail: `getDetailAdapter`, `setDetailListening`, `showDetail`;
- identity/metrics: `getCurrentTileUser`, `getMetricsCategory`, `getMetricsSpec`, `getInstanceId`, `populate`;
- availability: `isAvailable`, `isConnected`, `isTileReady`;
- user switch: `userSwitch` refreshes state;
- details view model overloads return null/false according to return type.

For an unknown method, use a shared `defaultValue(returnType)`:

```kotlin
private fun defaultValue(type: Class<*>): Any? = when (type) {
    Void.TYPE -> null
    Boolean::class.javaPrimitiveType -> false
    Byte::class.javaPrimitiveType -> 0.toByte()
    Short::class.javaPrimitiveType -> 0.toShort()
    Int::class.javaPrimitiveType -> 0
    Long::class.javaPrimitiveType -> 0L
    Float::class.javaPrimitiveType -> 0f
    Double::class.javaPrimitiveType -> 0.0
    Char::class.javaPrimitiveType -> ' '
    else -> null
}
```

- [ ] **Step 6: Build and publish BooleanState**

Instantiate `classes.booleanStateClass.getDeclaredConstructor().newInstance()` and set:

```text
spec = hypermodes_focus
label = displayed mode name or localized fallback
state = 2 active / 1 inactive / 0 unavailable
value = active
dualTarget = false
handlesLongClick = true
handlesSecondaryClick = false
contentDescription = localized mode and status
icon = plugin DrawableIcon
```

Create `DrawableIcon` from the module drawable selected via `ModeIconMapper.getStatusBarIcon(mode.icon)`. Use `ic_stat_zen` when lookup or loading fails.

Notify callbacks on `postToUi` by reflectively invoking `onStateChanged(state)`.

- [ ] **Step 7: Implement listening and clicks**

Track listening tokens by identity. Register one observer when the first token starts listening and close it after the last token stops. Observer changes call `refreshState()`.

Click behavior:

```kotlin
val snapshot = repository.loadOrInitialize()
when {
    snapshot.displayedMode == null -> Unit
    snapshot.isActive -> repository.deactivate()
    else -> repository.activate(snapshot.displayedMode.id)
}
refreshState()
```

Long click calls `onShowDetail(true)` on every callback only when a Detail adapter exists.

- [ ] **Step 8: Run proxy tests and compile**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ReflectTest" --tests "com.banana.hypermodes.controlcenter.FocusCardTileProviderTest"
.\gradlew.bat :app:compileDebugKotlin
```

Expected: all focused tests pass and production Kotlin compiles.

---

### Task 5: Implement the native Detail mode selector

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapter.kt`
- Create: `app/src/test/java/com/banana/hypermodes/controlcenter/FocusModeDetailAdapterTest.kt`
- Modify: `app/src/main/res/values/strings.xml`
- Modify: `app/src/main/res/values-zh-rCN/strings.xml`

**Interfaces:**
- Consumes: `FocusCardStateRepository`, `FocusCardDetailFactory`, plugin `DetailAdapter` Class, module Context.
- Produces:

```kotlin
class FocusModeDetailAdapter(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val detailAdapterInterface: Class<*>,
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit
) {
    fun create(): Any
}
```

- Task 6 creates this adapter inside the tile's `FocusCardDetailFactory`.

- [ ] **Step 1: Add localized strings**

Add English:

```xml
<string name="focus_card_title">Focus modes</string>
<string name="focus_card_empty">No modes configured</string>
<string name="focus_card_open_app">Open HyperModes</string>
<string name="focus_card_fallback">Focus mode</string>
<string name="focus_card_active">On</string>
<string name="focus_card_inactive">Off</string>
```

Add Chinese:

```xml
<string name="focus_card_title">专注模式</string>
<string name="focus_card_empty">尚未配置模式</string>
<string name="focus_card_open_app">打开 HyperModes</string>
<string name="focus_card_fallback">专注模式</string>
<string name="focus_card_active">已开启</string>
<string name="focus_card_inactive">已关闭</string>
```

- [ ] **Step 2: Write proxy contract tests**

Define a fake detail interface with:

```kotlin
private interface FakeDetailAdapter {
    fun getTitle(): CharSequence
    fun getToggleVisible(): Boolean
    fun getToggleState(): Boolean?
    fun setToggleState(enabled: Boolean)
    fun getToggleEnabled(): Boolean
    fun getMetricsCategory(): Int
    fun getSettingsIntent(): Intent?
    fun getContainerHeight(): Int
}
```

Test that the proxy returns the localized title, `getToggleVisible() == false`, `getToggleState() == null`, `getToggleEnabled() == false`, metrics `118`, and safe defaults for unsupported methods.

- [ ] **Step 3: Run the test and verify it fails**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest"
```

Expected: missing implementation failure.

- [ ] **Step 4: Implement DetailAdapter proxy methods**

Handle:

```text
getTitle -> localized focus_card_title
getToggleVisible -> false
getToggleState -> null
setToggleState -> Unit
getToggleEnabled -> false
getMetricsCategory -> 118
getSettingsIntent -> explicit MainActivity intent
createDetailView -> buildModeListView(context, convertView, parent)
shouldAnimate -> true
hasHeader -> true
getContainerHeight -> -1
```

Use the same type-correct default helper as Task 4 for all other methods.

- [ ] **Step 5: Build the mode list view**

Create a `ScrollView` containing a vertical `LinearLayout`.

For each mode in `repository.loadOrInitialize().modes`:

- create a horizontal row with an `ImageView` and vertical text container;
- load the mapped module icon, falling back to `ic_stat_zen`;
- display the mode name;
- mark the active mode using the plugin theme's activated/selected state and an accent indicator;
- use `android.R.attr.selectableItemBackground` for press feedback;
- use plugin density and theme attributes, not hard-coded pixel values;
- assign an accessibility content description containing mode name and active state.

On row click:

```kotlin
if (repository.activate(mode.id)) {
    onDismiss()
}
```

For an empty list, show the empty text and an “Open HyperModes” button. The button starts:

```kotlin
Intent().setClassName(
    Protocol.MODULE_PACKAGE,
    "com.banana.hypermodes.ui.MainActivity"
).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
```

- [ ] **Step 6: Run tests and resource compilation**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.controlcenter.FocusModeDetailAdapterTest"
.\gradlew.bat :app:processDebugResources :app:compileDebugKotlin
```

Expected: tests pass and resources compile.

---

### Task 6: Hook HyperOS card specs and tile creation

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterCardHook.kt`
- Test: `app/src/test/java/com/banana/hypermodes/hook/ControlCenterCardHookTest.kt`

**Interfaces:**
- Consumes: all control-center components from Tasks 2–5.
- Produces:

```kotlin
class ControlCenterCardHook(private val module: XposedModule) {
    fun install(classLoader: ClassLoader)

    companion object {
        const val FOCUS_CARD_SPEC = "hypermodes_focus"
        internal fun appendFocusSpec(result: Any?): Any?
    }
}
```

- `XposedInit` in Task 7 calls `install(pluginClassLoader)`.

- [ ] **Step 1: Write list-injection tests**

Test:

```kotlin
@Test fun `appendFocusSpec preserves order and appends focus`()
@Test fun `appendFocusSpec does not duplicate focus`()
@Test fun `appendFocusSpec accepts immutable input list`()
@Test fun `appendFocusSpec leaves non-list return unchanged`()
@Test fun `appendFocusSpec leaves null unchanged`()
```

Expected result for `["wifi", "cell"]` is `["wifi", "cell", "hypermodes_focus"]`.

- [ ] **Step 2: Run the test and verify it fails**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
```

Expected: missing helper failure.

- [ ] **Step 3: Implement ClassLoader-scoped installation**

Use a synchronized weak set:

```kotlin
private val installedLoaders =
    Collections.newSetFromMap(WeakHashMap<ClassLoader, Boolean>())
```

Resolve:

```text
miui.systemui.controlcenter.qs.QSController
getCardStyleTileSpecs()
createTile(String)
```

Validate exact parameter counts. If validation fails, log one error with plugin ClassLoader and stop without installing partial hooks.

- [ ] **Step 4: Hook `getCardStyleTileSpecs()`**

Interceptor behavior:

```kotlin
val original = chain.proceed()
return appendFocusSpec(original)
```

`appendFocusSpec` copies the source collection into a new `ArrayList<String>`, preserves order, and appends only when absent.

- [ ] **Step 5: Hook `createTile(String)`**

If `chain.args[0] != FOCUS_CARD_SPEC`, return `chain.proceed()` exactly once.

For Focus:

1. Obtain plugin Context from `chain.thisObject` through `Reflect.call(controller, "getContext") as? Context`.
2. Create module Context using `Protocol.MODULE_PACKAGE` and `CONTEXT_IGNORE_SECURITY or CONTEXT_INCLUDE_CODE`.
3. Resolve `FocusCardTileClasses` from the plugin ClassLoader.
4. Create one `GlobalFocusCardConfigStore` and one `FocusCardStateRepository` with `ModeIndexSelector { Random.nextInt(it) }`.
5. Create `FocusCardDetailFactory` that builds `FocusModeDetailAdapter`; its dismiss callback asks the tile callbacks to show detail `false`.
6. Return `FocusCardTileProvider.create()`.

Catch every failure, log class/method and plugin version context where available, and return `null`. Do not call the original create method for the private Focus spec.

- [ ] **Step 6: Add concise lifecycle logs**

Log only:

- plugin ClassLoader accepted;
- target methods validated;
- Focus spec appended for the first observed list shape;
- Focus tile creation requested/succeeded/failed;
- compatibility failure.

Do not log every state refresh or RecyclerView bind.

- [ ] **Step 7: Run hook tests and compile**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest --tests "com.banana.hypermodes.hook.ControlCenterCardHookTest"
.\gradlew.bat :app:compileDebugKotlin
```

Expected: tests pass and Hook compiles.

---

### Task 7: Wire the SystemUI plugin lifecycle and remove the obsolete route

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/XposedInit.kt:36-110`
- Remove: `app/src/main/java/com/banana/hypermodes/hook/ControlCenterHook.kt`
- Remove: `app/src/main/java/com/banana/hypermodes/tile/FocusTileProvider.kt`
- Remove: `app/src/main/java/com/banana/hypermodes/tile/FocusDetailAdapter.kt`

**Interfaces:**
- Consumes: `ControlCenterCardHook.install(pluginClassLoader)`.
- Produces: one active control-center injection route from the SystemUI process.

- [ ] **Step 1: Remove the old hook installation calls**

In `onPackageReady`:

- keep the `com.android.systemui` branch;
- keep `hookPluginLoading(param.classLoader)` and `SystemUIHook.install`;
- remove the independent `miui.systemui.plugin` branch;
- remove `ControlCenterHook` imports and construction.

The plugin does not have its own target PID on the test device.

- [ ] **Step 2: Correct the `PluginInstance.loadPlugin()` interception**

The decompiled method returns `void`. Interceptor sequence:

```kotlin
val result = chain.proceed()
val pluginInstance = chain.thisObject ?: return result
val pkg = Reflect.call(pluginInstance, "getPackage") as? String
if (pkg != "miui.systemui.plugin") return result
val pluginContext = Reflect.call(pluginInstance, "getPluginContext") as? Context
val pluginClassLoader = pluginContext?.classLoader
if (pluginClassLoader != null) {
    ControlCenterCardHook(this@XposedInit).install(pluginClassLoader)
}
return result
```

Do not assume `getPackage()` or `getPluginContext()` are declared directly on a generated subclass; use `Reflect.call` so superclass walking applies.

- [ ] **Step 3: Add a timing fallback for an already-loaded plugin**

Immediately after installing the `loadPlugin` hook, inspect currently reachable `PluginInstance` only if the codebase already exposes one through the hooked callback; do not scan the heap or force plugin construction. The normal fallback is the next plugin/SystemUI reload.

Keep this behavior explicit in logs: “hook installed; waiting for miui.systemui.plugin load”.

- [ ] **Step 4: Remove obsolete source files**

After Tasks 1–6 tests pass, remove the old files listed above. Verify no production source references `ControlCenterHook`, old `FocusTileProvider`, old `FocusDetailAdapter`, `LocalMiuiQSTilePlugin`, `getAllPluginTiles`, or `MiuiQSTile`.

Run:

```powershell
rg -n "ControlCenterHook|LocalMiuiQSTilePlugin|getAllPluginTiles|MiuiQSTile|FocusDetailAdapter" app/src/main/java
```

Expected: no matches, except intentionally retained migration documentation outside production source.

- [ ] **Step 5: Run the full unit suite and assemble**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug --stacktrace
```

Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Check the complete implementation diff**

Run:

```powershell
git diff --check
git status --short
```

Expected: no new whitespace errors. Review the status carefully because unrelated user modifications remain in the worktree.

---

### Task 8: Verify on the target HyperOS 3 device

**Files:**
- No production changes unless evidence identifies a specific compatibility defect.
- Record findings in the implementation session summary; do not create speculative fixes.

**Interfaces:**
- Consumes: debug APK from Task 7.
- Produces: device evidence for Hook installation, card creation, visual placement, interaction, refresh, persistence, and cleanup.

- [ ] **Step 1: Capture the device baseline**

Run:

```powershell
adb devices -l
adb shell "getprop ro.mi.os.version.name; getprop ro.mi.os.version.incremental; dumpsys package miui.systemui.plugin | grep -E 'versionCode|versionName'"
```

Expected: the documented HyperOS 3 device and plugin version, or a clearly recorded version change before continuing.

- [ ] **Step 2: Install the debug APK**

Run:

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

Expected: `Success`.

- [ ] **Step 3: Clear logs and restart SystemUI**

With user-approved root access:

```powershell
adb logcat -c
adb shell su -c "killall com.android.systemui"
```

Wait for `pidof com.android.systemui` to return a PID before opening Control Center.

- [ ] **Step 4: Verify the Hook boundary logs**

Run:

```powershell
adb shell "logcat -d -v threadtime | grep -E 'HyperModes|ControlCenterCardHook|hypermodes_focus'"
```

Required evidence in order:

1. SystemUI module loaded.
2. `miui.systemui.plugin` loaded.
3. plugin ClassLoader obtained.
4. `QSController` methods validated.
5. Focus spec appended.
6. `createTile(hypermodes_focus)` observed.
7. Focus QSTile created.
8. listening started after card attachment.

If one boundary is missing, stop and investigate that boundary before changing later layers.

- [ ] **Step 5: Verify visual placement**

Open Control Center and confirm:

- exactly one Focus card;
- second row, left side, below Wi-Fi/mobile data;
- 1×2 size matching native cards;
- native corner radius, blur, margin, text, icon tint, press animation, haptics;
- no ordinary quick-setting copy in the edit list.

Capture a screenshot for comparison.

- [ ] **Step 6: Verify first-use and persistence rules**

Prepare a config with multiple modes, `activeModeId = null`, and `lastModeId = null`.

Confirm:

1. One configured mode is selected.
2. `lastModeId` is written once.
3. Reopening Control Center does not reroll.
4. Restarting SystemUI does not reroll.
5. Rebooting does not reroll.

Inspect the stored JSON after each stage with an authorized Settings read command.

- [ ] **Step 7: Verify click behavior and external refresh**

Confirm:

- clicking inactive card activates the displayed mode;
- card changes to native active style immediately;
- clicking active card deactivates it and continues displaying it;
- activating another mode from HyperModes updates the card without SystemUI restart;
- automatic schedule/trigger activation also updates it;
- deleting the remembered mode selects and persists a valid replacement.

- [ ] **Step 8: Verify long-press Detail behavior**

Confirm:

- long press opens the native HyperOS Detail panel;
- all configured modes appear with icons and names;
- active mode is visually identified;
- selecting another mode immediately switches to it;
- Detail closes and card refreshes;
- empty mode list shows empty state and opens HyperModes through its action.

- [ ] **Step 9: Verify lifecycle cleanup and duplicate prevention**

Repeatedly close/open Control Center and restart SystemUI. Confirm:

- no duplicate cards;
- no repeated observer registration warnings;
- no SystemUI crash, plugin disable, or protected-plugin failure;
- callback and observer cleanup occurs on tile destroy.

Run a final focused log scan for exceptions:

```powershell
adb shell "logcat -d -v threadtime | grep -E 'FATAL EXCEPTION|PluginInstance.*Failure|ControlCenterCardHook.*failed|FocusCard.*failed'"
```

Expected: no unhandled failures.

- [ ] **Step 10: Run final local verification**

Run:

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:assembleDebug
git diff --check
git status --short
```

Report exact results. Do not claim completion if any required device behavior has not been observed.
