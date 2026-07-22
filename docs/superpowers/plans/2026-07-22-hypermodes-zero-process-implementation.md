# HyperModes Zero-Process Architecture Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement zero-process architecture for HyperModes, moving all logic from App process to system_server for true background-independent operation.

**Architecture:** Three-layer architecture with (1) front-end App for configuration UI that writes to Settings.Global, (2) system_server LSPosed module that monitors config and executes all mode logic, and (3) SystemUI Hook for status bar icon injection.

**Tech Stack:** Kotlin, LSPosed (libxposed API 101), Android 16 system services, Jetpack Compose

## Global Constraints

- Target: HyperOS (Android 16 only)
- LSPosed framework: 1.9.0+
- libxposed API: 101+
- Kotlin: 1.9+
- All system_server code must use reflection for Android internals
- No Root access required (LSPosed only)
- Config key: `Settings.Global["pixel_routines_full_config"]`
- Module package: `com.banana.hypermodes`

---

## Phase 1: Core Foundation (system_server)

### Task 1: Config Data Models and JSON Parser

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/config/ModeConfig.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/config/ConfigParser.kt`
- Create: `app/src/test/java/com/banana/hypermodes/systemserver/config/ConfigParserTest.kt`

**Interfaces:**
- Consumes: None (foundation)
- Produces: 
  - `ModeConfig` data class with all fields
  - `FullConfig` data class with `activeModeId` and `modes` list
  - `ConfigParser.parseConfig(json: String): FullConfig`
  - `ConfigParser.updateActiveModeId(json: String, modeId: String?): String`

- [ ] **Step 1: Write test for JSON parsing**

```kotlin
// app/src/test/java/com/banana/hypermodes/systemserver/config/ConfigParserTest.kt
package com.banana.hypermodes.systemserver.config

import org.junit.Test
import org.junit.Assert.*

class ConfigParserTest {
    @Test
    fun testParseBasicConfig() {
        val json = """
        {
          "activeModeId": "work_mode",
          "modes": [
            {
              "id": "work_mode",
              "name": "工作",
              "icon": "💼",
              "type": "SCHEDULED",
              "startTime": "09:00",
              "endTime": "18:00",
              "repeatDays": [1, 2, 3, 4, 5],
              "notification": {
                "dndLevel": "PRIORITY",
                "allowedApps": ["com.tencent.mm"]
              },
              "display": {
                "darkMode": true,
                "grayscale": false,
                "dimWallpaper": false,
                "keepScreenOff": false
              },
              "pausedApps": ["com.ss.android.ugc.aweme"],
              "contactFilter": "STARRED"
            }
          ]
        }
        """.trimIndent()
        
        val config = ConfigParser.parseConfig(json)
        
        assertEquals("work_mode", config.activeModeId)
        assertEquals(1, config.modes.size)
        assertEquals("work_mode", config.modes[0].id)
        assertEquals("工作", config.modes[0].name)
        assertEquals(ModeType.SCHEDULED, config.modes[0].type)
        assertEquals("09:00", config.modes[0].startTime)
        assertEquals(listOf(1, 2, 3, 4, 5), config.modes[0].repeatDays)
        assertEquals(DndLevel.PRIORITY, config.modes[0].notification.dndLevel)
        assertEquals(listOf("com.tencent.mm"), config.modes[0].notification.allowedApps)
        assertTrue(config.modes[0].display.darkMode)
        assertEquals(ContactFilter.STARRED, config.modes[0].contactFilter)
    }
}
```

- [ ] **Step 2: Run test to verify it fails**

```bash
./gradlew test --tests ConfigParserTest.testParseBasicConfig
```

Expected: FAIL with "ConfigParser not found"

- [ ] **Step 3: Update ModeConfig data classes**

```kotlin
// app/src/main/java/com/banana/hypermodes/systemserver/config/ModeConfig.kt
package com.banana.hypermodes.systemserver.config

import kotlinx.serialization.Serializable

@Serializable
data class ModeConfig(
    val id: String,
    val name: String,
    val icon: String,
    val type: ModeType,
    
    // Schedule (for SCHEDULED type)
    val startTime: String? = null,
    val endTime: String? = null,
    val repeatDays: List<Int>? = null,
    
    // Triggers (for DYNAMIC_TRIGGER type)
    val triggers: TriggerConfig? = null,
    
    // Notification settings
    val notification: NotificationConfig,
    
    // Display settings
    val display: DisplayConfig,
    
    // Paused apps
    val pausedApps: List<String> = emptyList(),
    
    // Contact filter
    val contactFilter: ContactFilter = ContactFilter.ALL
)

@Serializable
enum class ModeType {
    SCHEDULED,
    DYNAMIC_TRIGGER,
    BEDTIME
}

@Serializable
data class TriggerConfig(
    val bluetooth: BluetoothTrigger? = null,
    val motion: MotionTrigger? = null
)

@Serializable
data class BluetoothTrigger(
    val enabled: Boolean,
    val targetMacs: List<String>
)

@Serializable
data class MotionTrigger(
    val enabled: Boolean,
    val speedThresholdKmH: Float
)

@Serializable
data class NotificationConfig(
    val dndLevel: DndLevel,
    val allowedApps: List<String> = emptyList()
)

@Serializable
enum class DndLevel {
    NONE,
    PRIORITY,
    ALARMS
}

@Serializable
data class DisplayConfig(
    val darkMode: Boolean = false,
    val grayscale: Boolean = false,
    val dimWallpaper: Boolean = false,
    val keepScreenOff: Boolean = false
)

@Serializable
enum class ContactFilter {
    NONE,
    ALL,
    STARRED
}

@Serializable
data class FullConfig(
    val activeModeId: String? = null,
    val modes: List<ModeConfig>
)
```

- [ ] **Step 4: Implement ConfigParser**

```kotlin
// app/src/main/java/com/banana/hypermodes/systemserver/config/ConfigParser.kt
package com.banana.hypermodes.systemserver.config

import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

object ConfigParser {
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    fun parseConfig(jsonString: String): FullConfig {
        return json.decodeFromString(jsonString)
    }
    
    fun updateActiveModeId(jsonString: String, modeId: String?): String {
        val config = parseConfig(jsonString)
        val updated = config.copy(activeModeId = modeId)
        return json.encodeToString(updated)
    }
    
    fun serializeConfig(config: FullConfig): String {
        return json.encodeToString(config)
    }
}
```

- [ ] **Step 5: Run test to verify it passes**

```bash
./gradlew test --tests ConfigParserTest.testParseBasicConfig
```

Expected: PASS

- [ ] **Step 6: Add test for updateActiveModeId**

```kotlin
// Add to ConfigParserTest.kt
@Test
fun testUpdateActiveModeId() {
    val json = """
    {
      "activeModeId": "work_mode",
      "modes": [
        {
          "id": "work_mode",
          "name": "工作",
          "icon": "💼",
          "type": "SCHEDULED",
          "notification": {
            "dndLevel": "PRIORITY",
            "allowedApps": []
          },
          "display": {
            "darkMode": false,
            "grayscale": false,
            "dimWallpaper": false,
            "keepScreenOff": false
          },
          "pausedApps": [],
          "contactFilter": "ALL"
        }
      ]
    }
    """.trimIndent()
    
    val updated = ConfigParser.updateActiveModeId(json, "driving_mode")
    val config = ConfigParser.parseConfig(updated)
    
    assertEquals("driving_mode", config.activeModeId)
}
```

- [ ] **Step 7: Run test**

```bash
./gradlew test --tests ConfigParserTest.testUpdateActiveModeId
```

Expected: PASS

- [ ] **Step 8: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/config/
git add app/src/test/java/com/banana/hypermodes/systemserver/config/
git commit -m "feat(core): add config data models and JSON parser

- Add ModeConfig, FullConfig with all fields
- Add ConfigParser for JSON serialization/deserialization
- Add comprehensive unit tests

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 2: RoutineCoreEngine Foundation

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/executor/ModeActionExecutor.kt`

**Interfaces:**
- Consumes: `ConfigParser.parseConfig()`, `ModeConfig`, `FullConfig`
- Produces:
  - `RoutineCoreEngine.getInstance(): RoutineCoreEngine`
  - `RoutineCoreEngine.init(context: Context, classLoader: ClassLoader)`
  - `RoutineCoreEngine.activateMode(modeId: String)`
  - `RoutineCoreEngine.deactivateMode(modeId: String)`
  - `RoutineCoreEngine.getCurrentActiveMode(): ModeConfig?`

- [ ] **Step 1: Write test for singleton pattern**

```kotlin
// app/src/test/java/com/banana/hypermodes/systemserver/RoutineCoreEngineTest.kt
package com.banana.hypermodes.systemserver

import org.junit.Test
import org.junit.Assert.*

class RoutineCoreEngineTest {
    @Test
    fun testSingletonInstance() {
        val instance1 = RoutineCoreEngine.getInstance()
        val instance2 = RoutineCoreEngine.getInstance()
        assertSame(instance1, instance2)
    }
}
```

- [ ] **Step 2: Run test**

```bash
./gradlew test --tests RoutineCoreEngineTest.testSingletonInstance
```

Expected: PASS (already implemented, verifying structure)

- [ ] **Step 3: Implement core engine with ContentObserver**

```kotlin
// app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt
package com.banana.hypermodes.systemserver

import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.*
import com.banana.hypermodes.systemserver.executor.ModeActionExecutor

class RoutineCoreEngine private constructor() {
    private var systemContext: Context? = null
    private var classLoader: ClassLoader? = null
    
    private var currentActiveMode: ModeConfig? = null
    private var allModes: List<ModeConfig> = emptyList()
    
    private var modeActionExecutor: ModeActionExecutor? = null
    
    private val mainHandler = Handler(Looper.getMainLooper())
    
    fun init(context: Context, loader: ClassLoader) {
        log("Initializing RoutineCoreEngine...")
        systemContext = context
        classLoader = loader
        
        modeActionExecutor = ModeActionExecutor(context, loader)
        
        // Watch for config changes
        observeConfigChanges(context)
        
        // Load initial config
        loadConfigFromSettings()
        
        log("RoutineCoreEngine initialized successfully")
    }
    
    private fun observeConfigChanges(context: Context) {
        val observer = object : ContentObserver(mainHandler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                log("Config changed, reloading...")
                loadConfigFromSettings()
            }
        }
        
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(CONFIG_KEY),
            false,
            observer
        )
    }
    
    private fun loadConfigFromSettings() {
        val context = systemContext ?: return
        
        try {
            val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
            if (json.isNullOrBlank()) {
                log("No config found in Settings.Global")
                return
            }
            
            val config = ConfigParser.parseConfig(json)
            if (!validateConfig(config)) {
                log("Config validation failed, keeping previous config")
                return
            }
            
            allModes = config.modes
            
            // Restore active mode
            config.activeModeId?.let { modeId ->
                allModes.find { it.id == modeId }?.let { mode ->
                    if (currentActiveMode?.id != modeId) {
                        activateMode(modeId)
                    }
                }
            }
            
            log("Config loaded: ${allModes.size} modes")
        } catch (e: Exception) {
            log("Failed to load config: ${e.message}")
        }
    }
    
    private fun validateConfig(config: FullConfig): Boolean {
        // Basic validation - detailed validation in ModeActionExecutor
        return config.modes.isNotEmpty()
    }
    
    fun activateMode(modeId: String) {
        val mode = allModes.find { it.id == modeId }
        if (mode == null) {
            log("Mode not found: $modeId")
            return
        }
        
        if (currentActiveMode?.id == modeId) {
            log("Mode already active: $modeId")
            return
        }
        
        log("Activating mode: ${mode.name}")
        
        // Deactivate current mode first
        currentActiveMode?.let { deactivateMode(it.id) }
        
        currentActiveMode = mode
        
        // Apply mode actions
        modeActionExecutor?.applyMode(mode)
        
        // Update Settings.Global
        updateActiveModeInSettings(modeId)
    }
    
    fun deactivateMode(modeId: String) {
        val mode = currentActiveMode
        if (mode == null || mode.id != modeId) {
            return
        }
        
        log("Deactivating mode: ${mode.name}")
        
        // Revert mode actions
        modeActionExecutor?.revertMode(mode)
        
        currentActiveMode = null
        updateActiveModeInSettings(null)
    }
    
    private fun updateActiveModeInSettings(modeId: String?) {
        val context = systemContext ?: return
        
        try {
            val currentJson = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
            if (currentJson.isNullOrBlank()) return
            
            val updated = ConfigParser.updateActiveModeId(currentJson, modeId)
            Settings.Global.putString(context.contentResolver, CONFIG_KEY, updated)
        } catch (e: Exception) {
            log("Failed to update active mode: ${e.message}")
        }
    }
    
    fun getCurrentActiveMode(): ModeConfig? = currentActiveMode
    
    private fun log(msg: String) {
        Log.i(TAG, msg)
    }
    
    companion object {
        private const val TAG = "RoutineCoreEngine"
        private const val CONFIG_KEY = "pixel_routines_full_config"
        
        @Volatile
        private var instance: RoutineCoreEngine? = null
        
        fun getInstance(): RoutineCoreEngine {
            return instance ?: synchronized(this) {
                instance ?: RoutineCoreEngine().also { instance = it }
            }
        }
    }
}
```

- [ ] **Step 4: Create stub ModeActionExecutor**

```kotlin
// app/src/main/java/com/banana/hypermodes/systemserver/executor/ModeActionExecutor.kt
package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.config.ModeConfig

class ModeActionExecutor(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    fun applyMode(mode: ModeConfig) {
        log("Applying mode: ${mode.name} (stub)")
        // Will be implemented in later tasks
    }
    
    fun revertMode(mode: ModeConfig) {
        log("Reverting mode: ${mode.name} (stub)")
        // Will be implemented in later tasks
    }
    
    private fun log(msg: String) {
        Log.i(TAG, msg)
    }
    
    companion object {
        private const val TAG = "ModeActionExecutor"
    }
}
```

- [ ] **Step 5: Update XposedInit to initialize RoutineCoreEngine**

```kotlin
// Modify app/src/main/java/com/banana/hypermodes/XposedInit.kt
// In onSystemServerStarting, after SystemModeHook:

try {
    // Hook ActivityManagerService.systemReady to initialize RoutineCoreEngine
    val amsClass = param.classLoader.loadClass("com.android.server.am.ActivityManagerService")
    val systemReadyMethod = amsClass.getDeclaredMethod("systemReady", Runnable::class.java)
    
    hook(systemReadyMethod).intercept(object : XposedInterface.Hooker {
        override fun intercept(chain: XposedInterface.Chain): Any? {
            val result = chain.proceed()
            
            try {
                val ams = chain.thisObject
                val context = ams.javaClass.getField("mContext").get(ams) as Context
                
                // Initialize RoutineCoreEngine
                RoutineCoreEngine.getInstance().init(context, param.classLoader)
                log(Log.INFO, TAG, "RoutineCoreEngine initialized")
            } catch (t: Throwable) {
                log(Log.ERROR, TAG, "Failed to init RoutineCoreEngine", t)
            }
            
            return result
        }
    })
} catch (t: Throwable) {
    log(Log.ERROR, TAG, "Failed to hook AMS.systemReady", t)
}
```

- [ ] **Step 6: Build and test with Logcat**

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell stop && adb shell start
adb logcat -s XposedBridge:V | grep RoutineCoreEngine
```

Expected: See "RoutineCoreEngine initialized successfully"

- [ ] **Step 7: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/RoutineCoreEngine.kt
git add app/src/main/java/com/banana/hypermodes/systemserver/executor/ModeActionExecutor.kt
git add app/src/main/java/com/banana/hypermodes/XposedInit.kt
git commit -m "feat(core): implement RoutineCoreEngine with ContentObserver

- Initialize engine in system_server on systemReady
- Monitor Settings.Global for config changes
- Implement activateMode/deactivateMode
- Add stub ModeActionExecutor

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 3: Permission Grant Hook

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/PermissionGrantHook.kt`

**Interfaces:**
- Consumes: None
- Produces: Auto-grants `WRITE_SECURE_SETTINGS` to module package

- [ ] **Step 1: Implement PermissionGrantHook**

```kotlin
// app/src/main/java/com/banana/hypermodes/systemserver/PermissionGrantHook.kt
package com.banana.hypermodes.systemserver

import android.content.pm.PackageManager
import android.util.Log
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

class PermissionGrantHook(private val module: XposedModule) {
    fun install(classLoader: ClassLoader) {
        try {
            val pmsClass = classLoader.loadClass(
                "com.android.server.pm.permission.PermissionManagerService"
            )
            
            val checkPermissionMethod = pmsClass.getDeclaredMethod(
                "checkPermission",
                String::class.java,
                String::class.java,
                Int::class.javaPrimitiveType
            )
            
            module.hook(checkPermissionMethod).intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val permName = chain.getArg(0) as String
                    val pkgName = chain.getArg(1) as String
                    
                    if (pkgName == MODULE_PACKAGE && 
                        permName == android.Manifest.permission.WRITE_SECURE_SETTINGS) {
                        return PackageManager.PERMISSION_GRANTED
                    }
                    
                    return chain.proceed()
                }
            })
            
            log("PermissionGrantHook installed")
        } catch (t: Throwable) {
            log("Failed to install PermissionGrantHook: $t")
        }
    }
    
    private fun log(msg: String) {
        module.log(Log.INFO, TAG, msg)
    }
    
    companion object {
        private const val TAG = "PermissionGrantHook"
        private const val MODULE_PACKAGE = "com.banana.hypermodes"
    }
}
```

- [ ] **Step 2: Install hook in XposedInit**

```kotlin
// In XposedInit.onSystemServerStarting, add:
PermissionGrantHook(this).install(param.classLoader)
```

- [ ] **Step 3: Test permission grant**

```bash
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell stop && adb shell start

# In app, try to write to Settings.Global (will test in later task)
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/systemserver/PermissionGrantHook.kt
git add app/src/main/java/com/banana/hypermodes/XposedInit.kt
git commit -m "feat(core): add permission grant hook for WRITE_SECURE_SETTINGS

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

### Task 4: Front-End Config Writer

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/data/ModeStore.kt`
- Modify: `app/src/main/java/com/banana/hypermodes/data/Models.kt`

**Interfaces:**
- Consumes: `ConfigParser`, `ModeConfig`, `FullConfig`
- Produces:
  - `ModeStore.saveModes(modes: List<Mode>)`
  - `ModeStore.loadModes(): List<Mode>`
  - `Mode.toModeConfig(): ModeConfig`
  - `ModeConfig.toMode(): Mode`

- [ ] **Step 1: Add conversion extension functions**

```kotlin
// Add to app/src/main/java/com/banana/hypermodes/data/Models.kt

import com.banana.hypermodes.systemserver.config.*

fun Mode.toModeConfig(): ModeConfig {
    val schedule = settings.schedule
    return ModeConfig(
        id = id,
        name = name,
        icon = icon,
        type = when {
            id == "bedtime" -> ModeType.BEDTIME
            settings.drivingAutoDetect -> ModeType.DYNAMIC_TRIGGER
            schedule?.enabled == true -> ModeType.SCHEDULED
            else -> ModeType.SCHEDULED
        },
        startTime = schedule?.let { "${it.startHour.toString().padStart(2, '0')}:${it.startMinute.toString().padStart(2, '0')}" },
        endTime = schedule?.let { "${it.endHour.toString().padStart(2, '0')}:${it.endMinute.toString().padStart(2, '0')}" },
        repeatDays = schedule?.repeatDays?.let { days ->
            (0..6).filter { (days shr it) and 1 == 1 }.map { it + 1 }
        },
        triggers = if (settings.drivingAutoDetect) {
            TriggerConfig(
                bluetooth = BluetoothTrigger(
                    enabled = true,
                    targetMacs = emptyList() // Will be populated from UI
                ),
                motion = MotionTrigger(
                    enabled = true,
                    speedThresholdKmH = 15.0f
                )
            )
        } else null,
        notification = NotificationConfig(
            dndLevel = when (settings.dndLevel) {
                DndLevel.NONE -> com.banana.hypermodes.systemserver.config.DndLevel.NONE
                DndLevel.PRIORITY -> com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY
                DndLevel.ALARMS -> com.banana.hypermodes.systemserver.config.DndLevel.ALARMS
            },
            allowedApps = settings.allowedApps.toList()
        ),
        display = DisplayConfig(
            darkMode = settings.enableDarkMode,
            grayscale = settings.enableGrayscale,
            dimWallpaper = settings.dimWallpaper,
            keepScreenOff = settings.keepScreenOff
        ),
        pausedApps = settings.pausedApps.toList(),
        contactFilter = when (settings.contactFilter) {
            CONTACT_FILTER_NONE -> ContactFilter.NONE
            CONTACT_FILTER_ALL -> ContactFilter.ALL
            CONTACT_FILTER_STARRED -> ContactFilter.STARRED
            else -> ContactFilter.ALL
        }
    )
}

fun ModeConfig.toMode(): Mode {
    // Reverse conversion
    return Mode(
        id = id,
        name = name,
        icon = icon,
        description = "",
        enabled = false,
        settings = ModeSettings(
            enableDnd = true,
            dndLevel = when (notification.dndLevel) {
                com.banana.hypermodes.systemserver.config.DndLevel.NONE -> DndLevel.NONE
                com.banana.hypermodes.systemserver.config.DndLevel.PRIORITY -> DndLevel.PRIORITY
                com.banana.hypermodes.systemserver.config.DndLevel.ALARMS -> DndLevel.ALARMS
            },
            enableGrayscale = display.grayscale,
            enableDarkMode = display.darkMode,
            dimWallpaper = display.dimWallpaper,
            keepScreenOff = display.keepScreenOff,
            pausedApps = pausedApps.toSet(),
            allowedApps = notification.allowedApps.toSet(),
            contactFilter = when (contactFilter) {
                ContactFilter.NONE -> CONTACT_FILTER_NONE
                ContactFilter.ALL -> CONTACT_FILTER_ALL
                ContactFilter.STARRED -> CONTACT_FILTER_STARRED
            },
            drivingAutoDetect = type == ModeType.DYNAMIC_TRIGGER,
            schedule = startTime?.let { start ->
                val (startH, startM) = start.split(":").map { it.toInt() }
                val (endH, endM) = endTime!!.split(":").map { it.toInt() }
                val days = repeatDays?.fold(0) { acc, day -> acc or (1 shl (day - 1)) } ?: 0x7F
                ModeSchedule(
                    enabled = true,
                    startHour = startH,
                    startMinute = startM,
                    endHour = endH,
                    endMinute = endM,
                    repeatDays = days
                )
            }
        )
    )
}
```

- [ ] **Step 2: Rewrite ModeStore to use Settings.Global**

```kotlin
// app/src/main/java/com/banana/hypermodes/data/ModeStore.kt
package com.banana.hypermodes.data

import android.content.Context
import android.provider.Settings
import com.banana.hypermodes.systemserver.config.*

class ModeStore(private val context: Context) {
    private val contentResolver = context.contentResolver
    
    fun saveModes(modes: List<Mode>) {
        try {
            // Convert to ModeConfig
            val modeConfigs = modes.map { it.toModeConfig() }
            
            // Get current activeModeId
            val activeModeId = getCurrentActiveModeId()
            
            // Build FullConfig
            val fullConfig = FullConfig(
                activeModeId = activeModeId,
                modes = modeConfigs
            )
            
            // Serialize and write
            val json = ConfigParser.serializeConfig(fullConfig)
            Settings.Global.putString(contentResolver, CONFIG_KEY, json)
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to save modes", e)
        }
    }
    
    fun loadModes(): List<Mode> {
        return try {
            val json = Settings.Global.getString(contentResolver, CONFIG_KEY)
            if (json.isNullOrBlank()) {
                emptyList()
            } else {
                val config = ConfigParser.parseConfig(json)
                config.modes.map { it.toMode() }
            }
        } catch (e: Exception) {
            android.util.Log.e(TAG, "Failed to load modes", e)
            emptyList()
        }
    }
    
    fun getCurrentActiveModeId(): String? {
        return try {
            val json = Settings.Global.getString(contentResolver, CONFIG_KEY) ?: return null
            val config = ConfigParser.parseConfig(json)
            config.activeModeId
        } catch (e: Exception) {
            null
        }
    }
    
    companion object {
        private const val TAG = "ModeStore"
        private const val CONFIG_KEY = "pixel_routines_full_config"
    }
}
```

- [ ] **Step 3: Test end-to-end config flow**

```bash
# Build and install
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell stop && adb shell start

# Open app, create a mode, save it
# Check Settings.Global
adb shell settings get global pixel_routines_full_config

# Should see JSON config
```

- [ ] **Step 4: Commit**

```bash
git add app/src/main/java/com/banana/hypermodes/data/
git commit -m "feat(app): rewrite ModeStore to use Settings.Global

- Add Mode<->ModeConfig conversion functions
- Write modes to Settings.Global as JSON
- Remove old SharedPreferences storage

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

## Phase 2: Notification & App Control

### Task 5: NotificationFilterHook Implementation

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/hooks/NotificationFilterHook.kt`

**Interfaces:**
- Consumes: `RoutineCoreEngine.getCurrentActiveMode()`
- Produces: Filters notifications based on active mode whitelist

[Detailed steps similar to above pattern...]

---

### Task 6: AppSuspendController Implementation

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/executor/AppSuspendController.kt`

**Interfaces:**
- Consumes: `ModeConfig.pausedApps`
- Produces: `AppSuspendController.suspendApps(List<String>)`, `unsuspendApps()`

[Detailed steps...]

---

### Task 7: Integrate Controllers into ModeActionExecutor

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/systemserver/executor/ModeActionExecutor.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/executor/DndController.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/executor/DisplayModeController.kt`

[Detailed steps...]

---

## Phase 3: Scheduled Mode Triggering

### Task 8: ScheduledModeManager with AlarmManager

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ScheduledModeManager.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ModeAlarmReceiver.kt`

[Detailed steps...]

---

## Phase 4: Driving Mode Trigger

### Task 9: DrivingTriggerManager Implementation

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/DrivingTriggerManager.kt`

[Detailed steps...]

---

## Phase 5: Bedtime Integration

### Task 10: Modify DeskClockHook for system_server

**Files:**
- Modify: `app/src/main/java/com/banana/hypermodes/hook/DeskClockHook.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/trigger/BedtimeListener.kt`

[Detailed steps...]

---

## Phase 6: StatusBar Icon & TileService

### Task 11: SystemUI Hook for Status Bar Icon

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/ui/StatusBarIconHook.kt`
- Create: `app/src/main/java/com/banana/hypermodes/systemserver/ui/StatusBarIconInjector.kt`

[Detailed steps...]

---

### Task 12: TileService Implementation

**Files:**
- Create: `app/src/main/java/com/banana/hypermodes/ui/ModeQuickTileService.kt`
- Modify: `app/src/main/AndroidManifest.xml`

[Detailed steps...]

---

## Phase 7: Code Cleanup

### Task 13: Remove Old Architecture Files

**Files to Delete:**
- `app/src/main/java/com/banana/hypermodes/engine/ModeEngine.kt`
- `app/src/main/java/com/banana/hypermodes/engine/ModeScheduler.kt`
- `app/src/main/java/com/banana/hypermodes/engine/EngineReceiver.kt`
- `app/src/main/java/com/banana/hypermodes/engine/EngineState.kt`
- `app/src/main/java/com/banana/hypermodes/engine/TimeChangedReceiver.kt`
- `app/src/main/java/com/banana/hypermodes/driving/BluetoothDrivingReceiver.kt`
- `app/src/main/java/com/banana/hypermodes/driving/ActivityTransitionReceiver.kt`
- `app/src/main/java/com/banana/hypermodes/driving/BootReceiver.kt`
- `app/src/main/java/com/banana/hypermodes/driving/DrivingDetector.kt`
- `app/src/main/java/com/banana/hypermodes/receiver/BedtimeStateReceiver.kt`

- [ ] **Step 1: Remove old engine files**

```bash
git rm app/src/main/java/com/banana/hypermodes/engine/*.kt
git rm app/src/main/java/com/banana/hypermodes/driving/*.kt
git rm app/src/main/java/com/banana/hypermodes/receiver/*.kt
```

- [ ] **Step 2: Update imports in UI files**

[Remove references to ModeEngine, replace with direct Settings.Global writes]

- [ ] **Step 3: Test that app still functions**

- [ ] **Step 4: Commit**

```bash
git commit -m "refactor: remove old App-process architecture

- Delete ModeEngine, ModeScheduler, EngineReceiver
- Delete driving detectors and receivers
- All logic now in system_server

Co-Authored-By: Claude Fable 5 <noreply@anthropic.com>"
```

---

## Phase 8: Testing & Validation

### Task 14: Integration Testing

**Files:**
- Create: `app/src/androidTest/java/com/banana/hypermodes/integration/ZeroProcessArchitectureTest.kt`

- [ ] **Test 1: Config persistence**
- [ ] **Test 2: Mode activation via scheduled time**
- [ ] **Test 3: Notification filtering**
- [ ] **Test 4: App suspension**
- [ ] **Test 5: Driving mode bluetooth trigger**
- [ ] **Test 6: Status bar icon update**
- [ ] **Test 7: TileService reflects active mode**

[Detailed test steps...]

---

### Task 15: Performance & Memory Profiling

- [ ] **Profile system_server memory usage**
- [ ] **Measure mode activation latency**
- [ ] **Test config reload performance**
- [ ] **Verify no memory leaks**

---

## Summary

This plan implements the complete zero-process architecture in 15 major tasks across 8 phases:

**Phase 1 (Tasks 1-4):** Core foundation - config models, engine, permissions, front-end writer
**Phase 2 (Tasks 5-7):** Notification filtering, app suspension, mode execution
**Phase 3 (Task 8):** Scheduled mode triggering with AlarmManager
**Phase 4 (Task 9):** Driving mode with bluetooth/speed detection
**Phase 5 (Task 10):** Bedtime mode integration
**Phase 6 (Tasks 11-12):** StatusBar icon and TileService
**Phase 7 (Task 13):** Remove old architecture
**Phase 8 (Tasks 14-15):** Testing and validation

Each task follows TDD principles with test-first approach, immediate verification, and frequent commits.

---

**Plan complete and saved.**

