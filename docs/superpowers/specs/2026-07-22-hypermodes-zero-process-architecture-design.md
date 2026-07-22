# HyperModes Zero-Process Architecture Design

**Date:** 2026-07-22  
**Target System:** HyperOS (Android 16)  
**Core Technology:** LSPosed / system_server Injection  
**Design Goal:** 实现零进程常驻的系统级模式引擎，解决 HyperOS 划掉后台导致的进程停止问题

---

## 1. 总体架构设计

### 1.1 架构概览

HyperModes 采用**零进程常驻架构**，核心逻辑完全运行在 `system_server` 进程中，通过 LSPosed 框架注入系统服务。前端 App 仅作为配置界面，用户可以随意强停或划掉后台，不影响任何功能。

**三层架构：**

```
┌──────────────────────────────────────────────────┐
│  Layer 1: 前端 App（配置层）                      │
│  ┌────────────────────────────────────────────┐  │
│  │ • Jetpack Compose UI                       │  │
│  │ • 配置编辑与验证                            │  │
│  │ • JSON 序列化                               │  │
│  │ • Settings.Global 写入                      │  │
│  │ • TileService（快速设置磁贴）               │  │
│  └────────────────────────────────────────────┘  │
└────────────────────┬─────────────────────────────┘
                     │
                     │ JSON 配置通过 Settings.Global
                     │
                     ▼
┌──────────────────────────────────────────────────┐
│  Layer 2: system_server（LSPosed 模块）          │
│  ┌────────────────────────────────────────────┐  │
│  │ RoutineCoreEngine（单例）                   │  │
│  │ ├─ ConfigObserver（监听配置变更）           │  │
│  │ ├─ ScheduledModeManager（定时调度）         │  │
│  │ ├─ DrivingTriggerManager（行驶检测）        │  │
│  │ ├─ BedtimeListener（睡眠模式监听）          │  │
│  │ └─ ModeActionExecutor（模式执行器）         │  │
│  └────────────────────────────────────────────┘  │
│                                                   │
│  ┌────────────────────────────────────────────┐  │
│  │ 功能 Hooks                                  │  │
│  │ ├─ NotificationFilterHook                   │  │
│  │ ├─ AppSuspendController                     │  │
│  │ ├─ DndController                            │  │
│  │ ├─ DisplayModeController                    │  │
│  │ ├─ ContactFilterController                  │  │
│  │ └─ PermissionGrantHook                      │  │
│  └────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
                     │
                     │ Hook SystemUI
                     ▼
┌──────────────────────────────────────────────────┐
│  Layer 3: SystemUI Hook（状态栏扩展）            │
│  ┌────────────────────────────────────────────┐  │
│  │ StatusBarIconInjector                       │  │
│  │ • Hook StatusBarIconController              │  │
│  │ • 注入模式图标到状态栏                       │  │
│  │ • 监听 Settings.Global 更新图标              │  │
│  │ • 点击图标打开 TileService 或 App           │  │
│  └────────────────────────────────────────────┘  │
└──────────────────────────────────────────────────┘
```

### 1.2 核心设计原则

1. **零进程常驻**
   - 所有调度、检测、执行逻辑在 system_server 中运行
   - 前端 App 被杀不影响任何功能

2. **配置驱动**
   - 唯一的通信通道是 `Settings.Global`
   - JSON 格式配置，单一数据源

3. **权限透明化**
   - Hook 系统权限检查，前端 App 无需手动 ADB 授权
   - 用户安装即用

4. **模块化设计**
   - 每个功能独立的 Hook 模块
   - 易于测试和维护

5. **Android 16 专注**
   - 不考虑多版本兼容，代码更简洁
   - 使用最新 API

### 1.3 数据流

**配置更新流程：**
```
用户在 App UI 修改配置
    ↓
验证配置（包名、时间格式等）
    ↓
序列化为 JSON
    ↓
写入 Settings.Global["pixel_routines_full_config"]
    ↓
system_server 的 ContentObserver 触发
    ↓
解析 JSON，验证配置
    ↓
更新调度器（定时/蓝牙/速度监听）
    ↓
如果有 activeModeId，立即激活模式
```

**模式激活流程：**
```
触发条件满足（定时到达/蓝牙连接/睡眠开始）
    ↓
RoutineCoreEngine.activateMode(modeId)
    ↓
查找对应的 ModeConfig
    ↓
ModeActionExecutor 执行各项操作：
    ├─ 设置 DND 级别
    ├─ 暂停应用列表
    ├─ 切换显示模式（深色/灰度）
    ├─ Hook 通知过滤
    └─ Hook 联系人过滤
    ↓
更新 Settings.Global["activeModeId"]
    ↓
StatusBarIconInjector 更新图标
    ↓
TileService 更新磁贴显示
```

---

## 2. 配置数据协议

### 2.1 JSON Schema

前端 App 将所有模式配置序列化为 JSON，存储在 `Settings.Global["pixel_routines_full_config"]`。

**完整配置结构：**

```json
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
        "allowedApps": ["com.tencent.mm", "com.alibaba.android.rimet"]
      },
      "display": {
        "darkMode": true,
        "grayscale": false,
        "dimWallpaper": false,
        "keepScreenOff": false
      },
      "pausedApps": ["com.ss.android.ugc.aweme", "com.zhihu.android"],
      "contactFilter": "STARRED"
    },
    {
      "id": "driving_mode",
      "name": "驾驶",
      "icon": "🚗",
      "type": "DYNAMIC_TRIGGER",
      "triggers": {
        "bluetooth": {
          "enabled": true,
          "targetMacs": ["00:11:22:33:44:55", "AA:BB:CC:DD:EE:FF"]
        },
        "motion": {
          "enabled": true,
          "speedThresholdKmH": 15.0
        }
      },
      "notification": {
        "dndLevel": "ALARMS",
        "allowedApps": ["com.autonavi.minimap"]
      },
      "display": {
        "darkMode": false,
        "grayscale": false,
        "dimWallpaper": false,
        "keepScreenOff": false
      },
      "pausedApps": [],
      "contactFilter": "ALL"
    },
    {
      "id": "bedtime",
      "name": "就寝",
      "icon": "🌙",
      "type": "BEDTIME",
      "notification": {
        "dndLevel": "PRIORITY",
        "allowedApps": []
      },
      "display": {
        "darkMode": true,
        "grayscale": true,
        "dimWallpaper": true,
        "keepScreenOff": true
      },
      "pausedApps": ["com.ss.android.ugc.aweme"],
      "contactFilter": "STARRED"
    }
  ]
}
```

### 2.2 字段说明

**根对象：**
- `activeModeId` (string, nullable): 当前激活的模式 ID，null 表示无激活模式
- `modes` (array): 所有模式配置列表

**ModeConfig 对象：**
- `id` (string): 模式唯一标识符
- `name` (string): 模式显示名称
- `icon` (string): 模式图标（emoji 或图标名）
- `type` (enum): 模式类型
  - `SCHEDULED`: 定时模式（如工作、学习）
  - `DYNAMIC_TRIGGER`: 动态触发模式（如驾驶）
  - `BEDTIME`: 睡眠模式（由系统闹钟控制）

**定时模式字段 (type=SCHEDULED)：**
- `startTime` (string): 开始时间 "HH:mm"
- `endTime` (string): 结束时间 "HH:mm"
- `repeatDays` (array<int>): 重复日期，1-7 代表周一到周日

**动态触发模式字段 (type=DYNAMIC_TRIGGER)：**
- `triggers.bluetooth.enabled` (boolean): 是否启用蓝牙触发
- `triggers.bluetooth.targetMacs` (array<string>): 目标蓝牙设备 MAC 地址列表
- `triggers.motion.enabled` (boolean): 是否启用速度触发
- `triggers.motion.speedThresholdKmH` (float): 速度阈值（公里/小时）

**通知设置：**
- `notification.dndLevel` (enum): 勿扰级别
  - `NONE`: 完全静音
  - `PRIORITY`: 优先通知（星标联系人 + 白名单应用）
  - `ALARMS`: 仅闹钟
- `notification.allowedApps` (array<string>): 通知白名单应用包名

**显示设置：**
- `display.darkMode` (boolean): 深色模式
- `display.grayscale` (boolean): 灰度模式
- `display.dimWallpaper` (boolean): 壁纸变暗
- `display.keepScreenOff` (boolean): 保持屏幕关闭

**应用限制：**
- `pausedApps` (array<string>): 暂停的应用包名列表

**联系人过滤：**
- `contactFilter` (enum): 联系人过滤级别
  - `NONE`: 阻止所有来电/短信
  - `ALL`: 允许所有联系人
  - `STARRED`: 仅允许星标联系人

### 2.3 配置验证规则

**前端 App 验证（写入前）：**
1. 所有必填字段不能为空
2. `startTime` 和 `endTime` 必须是有效的 "HH:mm" 格式
3. `repeatDays` 必须是 1-7 的整数数组
4. `pausedApps` 中的包名必须是已安装的用户应用（非系统应用）
5. 蓝牙 MAC 地址必须是有效格式 "XX:XX:XX:XX:XX:XX"
6. 速度阈值必须大于 0
7. 模式 ID 必须唯一

**system_server 验证（读取后）：**
1. JSON 解析失败 → 拒绝整个配置，保持上一次有效配置
2. 必填字段缺失 → 拒绝整个配置
3. 包名验证：检查是否已安装，过滤掉系统应用和关键应用黑名单
4. 时间格式错误 → 拒绝该模式，其他模式继续生效

**关键应用黑名单（不允许暂停）：**
```kotlin
val CRITICAL_PACKAGES = setOf(
    "com.android.systemui",
    "com.android.settings",
    "com.miui.home",
    "com.android.launcher3",
    "com.android.phone",
    "com.android.mms"
)
```

---

## 3. system_server 核心引擎设计

### 3.1 XposedInit 入口点

**文件：** `XposedInit.kt`

```kotlin
class XposedInit : XposedModule() {
    override fun onSystemServerStarting(param: SystemServerStartingParam) {
        try {
            // 1. Hook NotificationManagerService
            HookNotificationManagerService(this, param.classLoader)
            
            // 2. Hook PackageManagerService (应用暂停)
            HookPackageManagerService(this, param.classLoader)
            
            // 3. Hook 权限检查（授予前端 App WRITE_SECURE_SETTINGS）
            HookPermissionGrant(this, param.classLoader)
            
            // 4. 初始化核心引擎（在 AMS.systemReady 后）
            HookActivityManagerService(this, param.classLoader)
            
            log(Log.INFO, TAG, "system_server hooks installed")
        } catch (t: Throwable) {
            log(Log.ERROR, TAG, "failed to install hooks", t)
        }
    }
    
    override fun onPackageReady(param: PackageReadyParam) {
        when (param.packageName) {
            DESKCLOCK_PACKAGE -> {
                // Hook 睡眠模式监听
                DeskClockHook(this).install(param.classLoader)
            }
            SYSTEMUI_PACKAGE -> {
                // Hook 状态栏图标注入
                StatusBarIconHook(this).install(param.classLoader)
            }
        }
    }
}
```

### 3.2 RoutineCoreEngine（核心引擎单例）

**职责：**
- 监听配置变更
- 管理所有触发器（定时、蓝牙、速度、睡眠）
- 执行模式激活/停用
- 维护当前激活模式状态

**核心方法：**

```kotlin
class RoutineCoreEngine private constructor() {
    private var systemContext: Context? = null
    private var notificationManager: NotificationManager? = null
    private var packageManager: PackageManager? = null
    
    private var currentActiveMode: ModeConfig? = null
    private var allModes: List<ModeConfig> = emptyList()
    
    private lateinit var scheduledModeManager: ScheduledModeManager
    private lateinit var drivingTriggerManager: DrivingTriggerManager
    private lateinit var modeActionExecutor: ModeActionExecutor
    
    fun init(context: Context, classLoader: ClassLoader) {
        systemContext = context
        notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
            as NotificationManager
        packageManager = context.packageManager
        
        // 初始化各管理器
        scheduledModeManager = ScheduledModeManager(context, this)
        drivingTriggerManager = DrivingTriggerManager(context, this)
        modeActionExecutor = ModeActionExecutor(context, classLoader)
        
        // 监听配置变更
        observeConfigChanges(context)
        
        // 加载初始配置
        loadConfigFromSettings()
    }
    
    private fun observeConfigChanges(context: Context) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
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
                log("No config found")
                return
            }
            
            val config = ConfigParser.parseConfig(json)
            if (!validateConfig(config)) {
                log("Config validation failed, keeping previous config")
                return
            }
            
            allModes = config.modes
            
            // 更新所有触发器
            scheduledModeManager.updateSchedules(
                config.modes.filter { it.type == ModeType.SCHEDULED }
            )
            drivingTriggerManager.updateConfig(
                config.modes.find { it.type == ModeType.DYNAMIC_TRIGGER }
            )
            
            // 恢复激活的模式
            config.activeModeId?.let { activateMode(it) }
            
            log("Config loaded: ${allModes.size} modes")
        } catch (e: Exception) {
            log("Failed to load config: ${e.message}")
        }
    }
    
    private fun validateConfig(config: FullConfig): Boolean {
        // 验证所有模式
        for (mode in config.modes) {
            // 验证暂停应用列表
            val validPausedApps = mode.pausedApps.filter { pkg ->
                !CRITICAL_PACKAGES.contains(pkg) && isUserApp(pkg)
            }
            if (validPausedApps.size != mode.pausedApps.size) {
                log("Filtered invalid apps in mode ${mode.id}")
            }
        }
        return true
    }
    
    private fun isUserApp(packageName: String): Boolean {
        return try {
            val appInfo = packageManager?.getApplicationInfo(packageName, 0)
            appInfo != null && (appInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0)
        } catch (e: Exception) {
            false
        }
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
        
        // 先停用当前模式
        currentActiveMode?.let { deactivateMode(it.id) }
        
        currentActiveMode = mode
        
        // 执行模式动作
        modeActionExecutor.applyMode(mode)
        
        // 更新 Settings.Global 中的 activeModeId
        updateActiveModeInSettings(modeId)
    }
    
    fun deactivateMode(modeId: String) {
        val mode = currentActiveMode
        if (mode == null || mode.id != modeId) {
            return
        }
        
        log("Deactivating mode: ${mode.name}")
        
        // 恢复默认状态
        modeActionExecutor.revertMode(mode)
        
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
    
    companion object {
        private const val CONFIG_KEY = "pixel_routines_full_config"
        private val CRITICAL_PACKAGES = setOf(
            "com.android.systemui",
            "com.android.settings",
            "com.miui.home",
            "com.android.launcher3",
            "com.android.phone",
            "com.android.mms"
        )
        
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

### 3.3 ScheduledModeManager（定时模式调度器）

**职责：**
- 使用 `AlarmManager` 注册定时模式的启动/停止闹钟
- 处理 `repeatDays` 的周期性调度

**核心逻辑：**

```kotlin
class ScheduledModeManager(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val pendingIntents = mutableMapOf<String, PendingIntent>()
    
    fun updateSchedules(modes: List<ModeConfig>) {
        // 清除所有旧的闹钟
        clearAllAlarms()
        
        // 为每个定时模式注册闹钟
        for (mode in modes) {
            scheduleMode(mode)
        }
    }
    
    private fun scheduleMode(mode: ModeConfig) {
        val startTime = parseTime(mode.startTime ?: return)
        val endTime = parseTime(mode.endTime ?: return)
        val repeatDays = mode.repeatDays ?: return
        
        // 注册启动闹钟
        scheduleAlarm(mode.id, startTime, repeatDays, isStart = true)
        
        // 注册停止闹钟
        scheduleAlarm(mode.id, endTime, repeatDays, isStart = false)
    }
    
    private fun scheduleAlarm(
        modeId: String,
        time: Pair<Int, Int>,
        repeatDays: List<Int>,
        isStart: Boolean
    ) {
        val (hour, minute) = time
        
        // 计算下次触发时间
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        // 如果时间已过，移到明天
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        // 找到下一个符合 repeatDays 的日期
        while (!repeatDays.contains(calendar.get(Calendar.DAY_OF_WEEK))) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        
        val intent = Intent(context, ModeAlarmReceiver::class.java).apply {
            action = if (isStart) ACTION_START_MODE else ACTION_STOP_MODE
            putExtra(EXTRA_MODE_ID, modeId)
        }
        
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            modeId.hashCode() + if (isStart) 0 else 1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
        
        pendingIntents["${modeId}_${if (isStart) "start" else "stop"}"] = pendingIntent
    }
    
    private fun clearAllAlarms() {
        for (pendingIntent in pendingIntents.values) {
            alarmManager.cancel(pendingIntent)
        }
        pendingIntents.clear()
    }
    
    private fun parseTime(time: String): Pair<Int, Int> {
        val parts = time.split(":")
        return Pair(parts[0].toInt(), parts[1].toInt())
    }
}

// BroadcastReceiver 在 system_server 中注册
class ModeAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val modeId = intent.getStringExtra(EXTRA_MODE_ID) ?: return
        val engine = RoutineCoreEngine.getInstance()
        
        when (intent.action) {
            ACTION_START_MODE -> engine.activateMode(modeId)
            ACTION_STOP_MODE -> engine.deactivateMode(modeId)
        }
    }
}
```

---

## 4. 触发器设计

### 4.1 DrivingTriggerManager（行驶模式触发器）

**触发逻辑：** 蓝牙优先，速度作为备选
- 如果配置了蓝牙设备且连接成功 → 立即触发
- 如果未配置蓝牙或蓝牙未连接 → 使用速度判断（> 阈值）

**实现：**

```kotlin
class DrivingTriggerManager(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private var drivingModeConfig: ModeConfig? = null
    private var isBtConnected = false
    private var isSpeedReached = false
    
    private val btReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device = intent.getParcelableExtra<BluetoothDevice>(
                BluetoothDevice.EXTRA_DEVICE
            ) ?: return
            
            val config = drivingModeConfig ?: return
            val btConfig = config.triggers?.bluetooth ?: return
            
            if (!btConfig.enabled) return
            
            // 检查是否是目标设备
            if (!btConfig.targetMacs.contains(device.address)) return
            
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    isBtConnected = true
                    evaluate()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    isBtConnected = false
                    evaluate()
                }
            }
        }
    }
    
    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            val config = drivingModeConfig ?: return
            val motionConfig = config.triggers?.motion ?: return
            
            if (!motionConfig.enabled) return
            
            if (location.hasSpeed()) {
                val speedKmH = location.speed * 3.6f
                isSpeedReached = (speedKmH >= motionConfig.speedThresholdKmH)
                evaluate()
            }
        }
        
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }
    
    fun updateConfig(mode: ModeConfig?) {
        drivingModeConfig = mode
        
        if (mode == null) {
            stop()
            return
        }
        
        start()
    }
    
    private fun start() {
        val config = drivingModeConfig ?: return
        
        // 注册蓝牙监听
        if (config.triggers?.bluetooth?.enabled == true) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            context.registerReceiver(btReceiver, filter, Context.RECEIVER_EXPORTED)
        }
        
        // 注册位置监听
        if (config.triggers?.motion?.enabled == true) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) 
                as LocationManager
            try {
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    5000L, // 5秒
                    10f,   // 10米
                    locationListener,
                    Looper.getMainLooper()
                )
            } catch (e: Exception) {
                log("Failed to register location listener: ${e.message}")
            }
        }
    }
    
    private fun stop() {
        try {
            context.unregisterReceiver(btReceiver)
        } catch (e: Exception) {}
        
        try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) 
                as LocationManager
            locationManager.removeUpdates(locationListener)
        } catch (e: Exception) {}
        
        isBtConnected = false
        isSpeedReached = false
    }
    
    private fun evaluate() {
        val config = drivingModeConfig ?: return
        
        // 蓝牙优先逻辑
        val shouldActivate = if (config.triggers?.bluetooth?.enabled == true && 
                                  config.triggers?.bluetooth?.targetMacs?.isNotEmpty() == true) {
            // 配置了蓝牙设备 → 使用蓝牙状态
            isBtConnected
        } else {
            // 未配置蓝牙 → 使用速度判断
            isSpeedReached
        }
        
        if (shouldActivate) {
            engine.activateMode(config.id)
        } else {
            engine.deactivateMode(config.id)
        }
    }
}
```

### 4.2 BedtimeListener（睡眠模式监听器）

**特殊性：**
- 睡眠模式的时间调度由系统闹钟（DeskClock）控制
- system_server 只负责监听睡眠模式的启动/关闭事件
- 收到事件后，执行额外的功能（通知过滤、应用暂停等）

**实现：**

```kotlin
class BedtimeListener(
    private val engine: RoutineCoreEngine
) {
    fun onBedtimeStateChanged(active: Boolean) {
        val bedtimeMode = engine.allModes.find { it.type == ModeType.BEDTIME }
        if (bedtimeMode == null) {
            log("Bedtime mode not configured")
            return
        }
        
        if (active) {
            engine.activateMode(bedtimeMode.id)
        } else {
            engine.deactivateMode(bedtimeMode.id)
        }
    }
}
```

**DeskClockHook 集成：**

保留现有的 `DeskClockHook.kt`，但修改 Broadcast 目标：

```kotlin
// 在 DeskClockHook 的 hookBedtimeStateSignals 中
private fun hookBedtimeStateSignals(classLoader: ClassLoader) {
    // ... 现有的 Hook 逻辑 ...
    
    module.hook(method)
        .intercept(object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                val result = chain.proceed()
                try {
                    val context = chain.getArg(0) as? Context ?: return result
                    val active = readInZenMode(context, classLoader, fallback)
                    
                    // 通知 system_server 的 BedtimeListener
                    RoutineCoreEngine.getInstance()
                        .getBedtimeListener()
                        .onBedtimeStateChanged(active)
                    
                    log("Bedtime state changed: active=$active")
                } catch (t: Throwable) {
                    log("Bedtime state notification failed: $t")
                }
                return result
            }
        })
}
```

---

## 5. 功能模块设计

### 5.1 NotificationFilterHook（通知过滤）

**Hook 点：** `NotificationManagerService.shouldMuteNotificationLocked`

**逻辑：**
- 获取当前激活模式的通知配置
- 如果发通知的应用在白名单中 → 放行
- 否则 → 静音/拦截

**实现：**

```kotlin
class NotificationFilterHook {
    fun install(classLoader: ClassLoader, module: XposedModule) {
        val nmsClass = classLoader.loadClass(
            "com.android.server.notification.NotificationManagerService"
        )
        val shouldMuteMethod = nmsClass.getDeclaredMethod(
            "shouldMuteNotificationLocked",
            classLoader.loadClass("com.android.server.notification.NotificationRecord")
        )
        
        module.hook(shouldMuteMethod).intercept(object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                val activeMode = RoutineCoreEngine.getInstance().getCurrentActiveMode()
                
                // 没有激活模式 → 使用系统默认行为
                if (activeMode == null) {
                    return chain.proceed()
                }
                
                val notificationConfig = activeMode.notification
                
                // DND 级别为 NONE → 全部静音（除非在白名单）
                // DND 级别为 PRIORITY → 检查白名单
                // DND 级别为 ALARMS → 只允许闹钟（系统处理）
                
                if (notificationConfig.dndLevel == DndLevel.ALARMS) {
                    // 让系统处理，只允许闹钟
                    return chain.proceed()
                }
                
                // 获取发通知的应用包名
                val record = chain.getArg(0)
                val sbn = record.javaClass.getMethod("getSbn").invoke(record)
                val packageName = sbn.javaClass.getMethod("getPackageName")
                    .invoke(sbn) as String
                
                // 检查是否在白名单中
                val isAllowed = notificationConfig.allowedApps.contains(packageName)
                
                if (isAllowed) {
                    return false // 不静音，放行通知
                } else {
                    return true  // 静音通知
                }
            }
        })
    }
}
```

### 5.2 AppSuspendController（应用暂停）

**使用 API：** `PackageManagerService.setPackagesSuspendedAsUser`

**效果：**
- 应用图标变灰
- 点击时提示"应用已暂停"
- 应用进程可能仍在后台（但启动被阻止）

**实现：**

```kotlin
class AppSuspendController(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    private var currentSuspendedApps: List<String> = emptyList()
    
    fun suspendApps(packageNames: List<String>) {
        if (packageNames == currentSuspendedApps) return
        
        try {
            val pms = ServiceManager.getService("package")
            val pmsClass = classLoader.loadClass("android.content.pm.IPackageManager\$Stub")
            val pmsProxy = pmsClass.getMethod("asInterface", IBinder::class.java)
                .invoke(null, pms)
            
            val setPackagesSuspendedMethod = pmsProxy.javaClass.getMethod(
                "setPackagesSuspendedAsUser",
                Array<String>::class.java,  // packageNames
                Boolean::class.javaPrimitiveType,  // suspended
                android.os.PersistableBundle::class.java,  // appExtras
                android.os.PersistableBundle::class.java,  // launcherExtras
                android.content.pm.SuspendDialogInfo::class.java,  // dialogInfo
                String::class.java,  // callingPackage
                Int::class.javaPrimitiveType  // userId
            )
            
            // 暂停新应用
            setPackagesSuspendedMethod.invoke(
                pmsProxy,
                packageNames.toTypedArray(),
                true,  // suspended = true
                null,  // appExtras
                null,  // launcherExtras
                null,  // dialogInfo
                "PixelRoutine",  // callingPackage
                0  // userId = 0 (系统用户)
            )
            
            currentSuspendedApps = packageNames
            log("Suspended ${packageNames.size} apps")
        } catch (e: Exception) {
            log("Failed to suspend apps: ${e.message}")
        }
    }
    
    fun unsuspendApps() {
        if (currentSuspendedApps.isEmpty()) return
        
        try {
            val pms = ServiceManager.getService("package")
            val pmsClass = classLoader.loadClass("android.content.pm.IPackageManager\$Stub")
            val pmsProxy = pmsClass.getMethod("asInterface", IBinder::class.java)
                .invoke(null, pms)
            
            val setPackagesSuspendedMethod = pmsProxy.javaClass.getMethod(
                "setPackagesSuspendedAsUser",
                Array<String>::class.java,
                Boolean::class.javaPrimitiveType,
                android.os.PersistableBundle::class.java,
                android.os.PersistableBundle::class.java,
                android.content.pm.SuspendDialogInfo::class.java,
                String::class.java,
                Int::class.javaPrimitiveType
            )
            
            // 解除暂停
            setPackagesSuspendedMethod.invoke(
                pmsProxy,
                currentSuspendedApps.toTypedArray(),
                false,  // suspended = false
                null,
                null,
                null,
                "PixelRoutine",
                0
            )
            
            log("Unsuspended ${currentSuspendedApps.size} apps")
            currentSuspendedApps = emptyList()
        } catch (e: Exception) {
            log("Failed to unsuspend apps: ${e.message}")
        }
    }
}
```

### 5.3 DndController（勿扰模式控制）

**使用 API：** `NotificationManager.setInterruptionFilter`

**实现：**

```kotlin
class DndController(private val context: Context) {
    fun setDndLevel(level: DndLevel) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
            as NotificationManager
        
        val filter = when (level) {
            DndLevel.NONE -> NotificationManager.INTERRUPTION_FILTER_NONE
            DndLevel.PRIORITY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
            DndLevel.ALARMS -> NotificationManager.INTERRUPTION_FILTER_ALARMS
        }
        
        notificationManager.setInterruptionFilter(filter)
        log("DND level set to $level")
    }
    
    fun restore() {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) 
            as NotificationManager
        notificationManager.setInterruptionFilter(
            NotificationManager.INTERRUPTION_FILTER_ALL
        )
        log("DND restored to ALL")
    }
}
```

### 5.4 DisplayModeController（显示模式控制）

**功能：**
- 深色模式：`UiModeManager.setNightMode`
- 灰度模式：`Settings.Secure.accessibility_display_daltonizer_enabled`
- 壁纸变暗/屏幕保持关闭：通过系统 API 实现

**实现：**

```kotlin
class DisplayModeController(private val context: Context) {
    fun applyDisplaySettings(display: DisplayConfig) {
        // 1. 深色模式
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) 
            as UiModeManager
        uiModeManager.setNightMode(
            if (display.darkMode) UiModeManager.MODE_NIGHT_YES 
            else UiModeManager.MODE_NIGHT_NO
        )
        
        // 2. 灰度模式
        Settings.Secure.putInt(
            context.contentResolver,
            "accessibility_display_daltonizer_enabled",
            if (display.grayscale) 1 else 0
        )
        if (display.grayscale) {
            Settings.Secure.putInt(
                context.contentResolver,
                "accessibility_display_daltonizer",
                0  // 灰度模式
            )
        }
        
        // 3. 壁纸变暗（通过降低壁纸 alpha）
        // 需要 Hook WallpaperManagerService
        
        // 4. 保持屏幕关闭（降低唤醒敏感度）
        // 需要 Hook PowerManagerService
        
        log("Display settings applied: darkMode=${display.darkMode}, grayscale=${display.grayscale}")
    }
    
    fun restore() {
        val uiModeManager = context.getSystemService(Context.UI_MODE_SERVICE) 
            as UiModeManager
        uiModeManager.setNightMode(UiModeManager.MODE_NIGHT_AUTO)
        
        Settings.Secure.putInt(
            context.contentResolver,
            "accessibility_display_daltonizer_enabled",
            0
        )
        
        log("Display settings restored")
    }
}
```

### 5.5 ContactFilterController（联系人过滤）

**逻辑：**
- `NONE`: 阻止所有来电/短信（通过 Hook TelecomManager/TelephonyManager）
- `ALL`: 允许所有联系人
- `STARRED`: 仅允许星标联系人（通过查询 ContactsProvider）

**实现：**

```kotlin
class ContactFilterController(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    private var currentFilter: ContactFilter = ContactFilter.ALL
    
    fun setContactFilter(filter: ContactFilter) {
        currentFilter = filter
        log("Contact filter set to $filter")
    }
    
    fun shouldAllowCall(phoneNumber: String): Boolean {
        return when (currentFilter) {
            ContactFilter.NONE -> false
            ContactFilter.ALL -> true
            ContactFilter.STARRED -> isStarredContact(phoneNumber)
        }
    }
    
    private fun isStarredContact(phoneNumber: String): Boolean {
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.Contacts.STARRED),
            "${ContactsContract.CommonDataKinds.Phone.NUMBER} = ?",
            arrayOf(phoneNumber),
            null
        )
        
        cursor?.use {
            if (it.moveToFirst()) {
                val starred = it.getInt(0)
                return starred == 1
            }
        }
        
        return false
    }
    
    fun installHook(module: XposedModule) {
        // Hook TelecomManager.endCall 或 PhoneInterfaceManager
        // 在来电时检查 shouldAllowCall，如果返回 false 则自动挂断
    }
}
```

---

## 6. ModeActionExecutor（模式执行器）

**职责：** 统一执行模式的所有功能

**实现：**

```kotlin
class ModeActionExecutor(
    private val context: Context,
    private val classLoader: ClassLoader
) {
    private val dndController = DndController(context)
    private val appSuspendController = AppSuspendController(context, classLoader)
    private val displayModeController = DisplayModeController(context)
    private val contactFilterController = ContactFilterController(context, classLoader)
    
    fun applyMode(mode: ModeConfig) {
        log("Applying mode: ${mode.name}")
        
        // 1. 设置 DND 级别
        dndController.setDndLevel(mode.notification.dndLevel)
        
        // 2. 暂停应用
        if (mode.pausedApps.isNotEmpty()) {
            appSuspendController.suspendApps(mode.pausedApps)
        }
        
        // 3. 应用显示设置
        displayModeController.applyDisplaySettings(mode.display)
        
        // 4. 设置联系人过滤
        contactFilterController.setContactFilter(mode.contactFilter)
        
        log("Mode applied successfully")
    }
    
    fun revertMode(mode: ModeConfig) {
        log("Reverting mode: ${mode.name}")
        
        // 1. 恢复 DND
        dndController.restore()
        
        // 2. 解除应用暂停
        appSuspendController.unsuspendApps()
        
        // 3. 恢复显示设置
        displayModeController.restore()
        
        // 4. 恢复联系人过滤
        contactFilterController.setContactFilter(ContactFilter.ALL)
        
        log("Mode reverted successfully")
    }
}
```

---

## 7. 权限授予 Hook

### 7.1 PermissionGrantHook

**目标：** 自动授予前端 App `WRITE_SECURE_SETTINGS` 权限，无需手动 ADB 授权

**Hook 点：** `PermissionManagerService.checkPermission`

**实现：**

```kotlin
class PermissionGrantHook {
    fun install(classLoader: ClassLoader, module: XposedModule) {
        val pmsClass = classLoader.loadClass(
            "com.android.server.pm.permission.PermissionManagerService"
        )
        
        val checkPermissionMethod = pmsClass.getDeclaredMethod(
            "checkPermission",
            String::class.java,  // permName
            String::class.java,  // pkgName
            Int::class.javaPrimitiveType  // userId
        )
        
        module.hook(checkPermissionMethod).intercept(object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                val permName = chain.getArg(0) as String
                val pkgName = chain.getArg(1) as String
                
                // 如果是我们的 App 请求 WRITE_SECURE_SETTINGS 权限
                if (pkgName == MODULE_PACKAGE && 
                    permName == android.Manifest.permission.WRITE_SECURE_SETTINGS) {
                    return PackageManager.PERMISSION_GRANTED
                }
                
                // 其他情况走正常流程
                return chain.proceed()
            }
        })
        
        log("PermissionGrantHook installed")
    }
    
    companion object {
        private const val MODULE_PACKAGE = "com.banana.hypermodes"
    }
}
```

---

## 8. SystemUI Hook（状态栏图标）

### 8.1 StatusBarIconInjector

**目标：** 在状态栏注入当前模式的图标

**Hook 点：** `StatusBarIconController` 或 `CollapsedStatusBarFragment`

**实现：**

```kotlin
class StatusBarIconHook(private val module: XposedModule) {
    fun install(classLoader: ClassLoader) {
        // Hook SystemUIApplication.onCreate
        val systemUIAppClass = classLoader.loadClass(
            "com.android.systemui.SystemUIApplication"
        )
        
        val onCreateMethod = systemUIAppClass.getDeclaredMethod("onCreate")
        
        module.hook(onCreateMethod).intercept(object : XposedInterface.Hooker {
            override fun intercept(chain: XposedInterface.Chain): Any? {
                val result = chain.proceed()
                val app = chain.thisObject as Application
                
                try {
                    // 初始化状态栏图标注入器
                    StatusBarIconInjector.getInstance().init(app, classLoader)
                } catch (t: Throwable) {
                    log("Failed to init StatusBarIconInjector: $t")
                }
                
                return result
            }
        })
    }
}

class StatusBarIconInjector private constructor() {
    private var context: Context? = null
    private var iconView: ImageView? = null
    
    fun init(context: Context, classLoader: ClassLoader) {
        this.context = context
        
        // 监听 Settings.Global 的 activeModeId 变更
        observeActiveModeChange(context)
        
        // 注入图标到状态栏
        injectIcon(context, classLoader)
    }
    
    private fun observeActiveModeChange(context: Context) {
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                updateIcon()
            }
        }
        
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor("pixel_routines_full_config"),
            false,
            observer
        )
    }
    
    private fun injectIcon(context: Context, classLoader: ClassLoader) {
        try {
            // 查找 StatusBar 或 CollapsedStatusBarFragment
            val statusBarClass = classLoader.loadClass(
                "com.android.systemui.statusbar.phone.StatusBar"
            )
            
            // 在状态栏右侧（系统图标区）注入自定义图标
            // 具体实现依赖于 HyperOS SystemUI 的结构
            
            iconView = ImageView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    dpToPx(context, 16),
                    dpToPx(context, 16)
                )
                scaleType = ImageView.ScaleType.CENTER_INSIDE
                setPadding(dpToPx(context, 4), 0, dpToPx(context, 4), 0)
                
                // 点击打开 TileService 或 App
                setOnClickListener {
                    openModeSelector(context)
                }
            }
            
            // 将 iconView 添加到状态栏布局中
            // 需要反射找到状态栏的图标容器
            
            updateIcon()
            
            log("StatusBar icon injected")
        } catch (e: Exception) {
            log("Failed to inject icon: ${e.message}")
        }
    }
    
    private fun updateIcon() {
        val context = this.context ?: return
        val iconView = this.iconView ?: return
        
        try {
            val json = Settings.Global.getString(
                context.contentResolver,
                "pixel_routines_full_config"
            )
            
            if (json.isNullOrBlank()) {
                iconView.visibility = View.GONE
                return
            }
            
            val config = ConfigParser.parseConfig(json)
            val activeMode = config.modes.find { it.id == config.activeModeId }
            
            if (activeMode == null) {
                iconView.visibility = View.GONE
            } else {
                iconView.visibility = View.VISIBLE
                // 显示模式图标（emoji 或 drawable）
                iconView.setImageDrawable(createIconDrawable(context, activeMode.icon))
            }
        } catch (e: Exception) {
            log("Failed to update icon: ${e.message}")
        }
    }
    
    private fun createIconDrawable(context: Context, icon: String): Drawable {
        // 将 emoji 转换为 Drawable
        // 或者从资源中加载图标
        return TextDrawable(context, icon)
    }
    
    private fun openModeSelector(context: Context) {
        // 打开快速设置面板中的 TileService
        // 或者直接启动 App
        val intent = Intent().apply {
            component = ComponentName(
                "com.banana.hypermodes",
                "com.banana.hypermodes.ui.MainActivity"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }
    
    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
    
    companion object {
        @Volatile
        private var instance: StatusBarIconInjector? = null
        
        fun getInstance(): StatusBarIconInjector {
            return instance ?: synchronized(this) {
                instance ?: StatusBarIconInjector().also { instance = it }
            }
        }
    }
}
```

---

## 9. 前端 App 设计

### 9.1 职责边界

**前端 App 的职责：**
1. 提供配置 UI（Compose）
2. 验证用户输入
3. 序列化为 JSON 并写入 `Settings.Global`
4. 提供 `TileService`（快速设置磁贴）
5. 监听 `Settings.Global` 更新 UI 状态

**前端 App 不负责：**
- ❌ 调度定时模式
- ❌ 监听蓝牙/位置
- ❌ 执行功能（通知过滤、应用暂停等）
- ❌ 保持常驻进程

### 9.2 配置写入逻辑

**ModeStore.kt（改造）：**

```kotlin
class ModeStore(private val context: Context) {
    private val contentResolver = context.contentResolver
    
    fun saveModes(modes: List<Mode>) {
        // 1. 验证配置
        val validatedModes = modes.map { validateMode(it) }
        
        // 2. 转换为 ModeConfig
        val modeConfigs = validatedModes.map { it.toModeConfig() }
        
        // 3. 获取当前激活的模式 ID
        val activeModeId = getCurrentActiveModeId()
        
        // 4. 构建完整配置
        val fullConfig = FullConfig(
            activeModeId = activeModeId,
            modes = modeConfigs
        )
        
        // 5. 序列化为 JSON
        val json = Json.encodeToString(fullConfig)
        
        // 6. 写入 Settings.Global
        Settings.Global.putString(contentResolver, CONFIG_KEY, json)
    }
    
    private fun validateMode(mode: Mode): Mode {
        // 验证暂停应用列表
        val validPausedApps = mode.settings.pausedApps.filter { pkg ->
            isUserApp(pkg) && !CRITICAL_PACKAGES.contains(pkg)
        }
        
        // 验证时间格式
        mode.settings.schedule?.let { schedule ->
            require(schedule.startHour in 0..23)
            require(schedule.startMinute in 0..59)
            require(schedule.endHour in 0..23)
            require(schedule.endMinute in 0..59)
        }
        
        return mode.copy(
            settings = mode.settings.copy(
                pausedApps = validPausedApps.toSet()
            )
        )
    }
    
    private fun isUserApp(packageName: String): Boolean {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(packageName, 0)
            (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0
        } catch (e: Exception) {
            false
        }
    }
    
    fun getCurrentActiveModeId(): String? {
        val json = Settings.Global.getString(contentResolver, CONFIG_KEY) ?: return null
        val config = Json.decodeFromString<FullConfig>(json)
        return config.activeModeId
    }
    
    fun loadModes(): List<Mode> {
        val json = Settings.Global.getString(contentResolver, CONFIG_KEY) ?: return emptyList()
        val config = Json.decodeFromString<FullConfig>(json)
        return config.modes.map { it.toMode() }
    }
    
    companion object {
        private const val CONFIG_KEY = "pixel_routines_full_config"
        private val CRITICAL_PACKAGES = setOf(
            "com.android.systemui",
            "com.android.settings",
            "com.miui.home",
            "com.android.launcher3"
        )
    }
}
```

### 9.3 TileService 实现

**ModeQuickTileService.kt：**

```kotlin
class ModeQuickTileService : TileService() {
    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            updateTile()
        }
    }
    
    override fun onStartListening() {
        super.onStartListening()
        
        // 监听配置变更
        contentResolver.registerContentObserver(
            Settings.Global.getUriFor(CONFIG_KEY),
            false,
            contentObserver
        )
        
        updateTile()
    }
    
    override fun onStopListening() {
        super.onStopListening()
        contentResolver.unregisterContentObserver(contentObserver)
    }
    
    override fun onClick() {
        super.onClick()
        
        // 打开模式选择对话框或 App
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivityAndCollapse(intent)
    }
    
    private fun updateTile() {
        val tile = qsTile ?: return
        
        try {
            val json = Settings.Global.getString(contentResolver, CONFIG_KEY)
            if (json.isNullOrBlank()) {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.no_active_mode)
                tile.updateTile()
                return
            }
            
            val config = Json.decodeFromString<FullConfig>(json)
            val activeMode = config.modes.find { it.id == config.activeModeId }
            
            if (activeMode == null) {
                tile.state = Tile.STATE_INACTIVE
                tile.label = getString(R.string.no_active_mode)
            } else {
                tile.state = Tile.STATE_ACTIVE
                tile.label = activeMode.name
                // 设置图标
                val icon = Icon.createWithResource(this, getIconResource(activeMode.icon))
                tile.icon = icon
            }
            
            tile.updateTile()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update tile", e)
        }
    }
    
    private fun getIconResource(icon: String): Int {
        // 根据 emoji 或图标名返回对应的资源 ID
        return R.drawable.ic_mode_default
    }
    
    companion object {
        private const val TAG = "ModeQuickTileService"
        private const val CONFIG_KEY = "pixel_routines_full_config"
    }
}
```

**AndroidManifest.xml 注册：**

```xml
<service
    android:name=".ui.ModeQuickTileService"
    android:icon="@drawable/ic_tile"
    android:label="@string/tile_label"
    android:permission="android.permission.BIND_QUICK_SETTINGS_TILE"
    android:exported="true">
    <intent-filter>
        <action android:name="android.service.quicksettings.action.QS_TILE" />
    </intent-filter>
</service>
```

---

## 10. 代码清理计划

### 10.1 需要删除的文件

**App 进程中的引擎/调度代码（旧架构）：**
- `engine/ModeEngine.kt` - 移除，功能迁移到 system_server 的 `RoutineCoreEngine`
- `engine/ModeScheduler.kt` - 移除，功能迁移到 `ScheduledModeManager`
- `engine/EngineReceiver.kt` - 移除，不再需要 App 进程接收器
- `engine/EngineState.kt` - 移除，状态由 system_server 管理
- `engine/TimeChangedReceiver.kt` - 移除，由 system_server 的 AlarmManager 处理
- `driving/BluetoothDrivingReceiver.kt` - 移除，功能迁移到 `DrivingTriggerManager`
- `driving/ActivityTransitionReceiver.kt` - 移除
- `driving/BootReceiver.kt` - 移除，system_server 无需 BootReceiver
- `driving/DrivingDetector.kt` - 移除，功能迁移到 `DrivingTriggerManager`
- `receiver/BedtimeStateReceiver.kt` - 移除，改为 system_server 直接监听

### 10.2 需要保留/改造的文件

**保留（UI 层）：**
- `ui/**/*.kt` - 所有 UI 组件保留
- `data/Models.kt` - 保留，但需要添加转换方法到 `ModeConfig`
- `data/ModeStore.kt` - 改造为只负责读写 `Settings.Global`
- `data/DefaultModes.kt` - 保留

**保留（Hook 层）：**
- `XposedInit.kt` - 改造，增加 system_server Hook
- `hook/DeskClockHook.kt` - 保留，改造为通知 system_server
- `hook/BedtimeController.kt` - 保留
- `hook/Reflect.kt` - 保留工具类
- `hook/StepResult.kt` - 保留

**新增（system_server 层）：**
- `systemserver/RoutineCoreEngine.kt` - 已存在，需完善
- `systemserver/config/ModeConfig.kt` - 已存在，需完善
- `systemserver/config/ConfigParser.kt` - 新增，JSON 解析
- `systemserver/trigger/ScheduledModeManager.kt` - 新增
- `systemserver/trigger/DrivingTriggerManager.kt` - 新增
- `systemserver/trigger/BedtimeListener.kt` - 新增
- `systemserver/executor/ModeActionExecutor.kt` - 新增
- `systemserver/executor/NotificationFilterHook.kt` - 新增
- `systemserver/executor/AppSuspendController.kt` - 新增
- `systemserver/executor/DndController.kt` - 新增
- `systemserver/executor/DisplayModeController.kt` - 新增
- `systemserver/executor/ContactFilterController.kt` - 新增
- `systemserver/PermissionGrantHook.kt` - 新增
- `systemserver/StatusBarIconHook.kt` - 新增

---

## 11. 测试策略

### 11.1 单元测试

**测试配置解析：**
```kotlin
@Test
fun testConfigParsing() {
    val json = """
        {
          "activeModeId": "work_mode",
          "modes": [...]
        }
    """
    val config = ConfigParser.parseConfig(json)
    assertEquals("work_mode", config.activeModeId)
    assertEquals(1, config.modes.size)
}
```

**测试配置验证：**
```kotlin
@Test
fun testConfigValidation() {
    val config = FullConfig(
        activeModeId = null,
        modes = listOf(
            ModeConfig(
                id = "test",
                pausedApps = listOf("com.android.systemui", "com.test.app")
            )
        )
    )
    
    val validated = RoutineCoreEngine.validateConfig(config)
    // 验证系统应用被过滤掉
    assertEquals(1, validated.modes[0].pausedApps.size)
    assertEquals("com.test.app", validated.modes[0].pausedApps[0])
}
```

### 11.2 集成测试

**测试定时模式触发：**
1. 在 App UI 中创建定时模式（9:00-18:00）
2. 修改系统时间到 9:00
3. 检查 Logcat 确认 `RoutineCoreEngine.activateMode` 被调用
4. 检查 DND 状态、应用暂停状态
5. 修改系统时间到 18:00
6. 检查模式是否停用

**测试行驶模式触发：**
1. 配置蓝牙设备 MAC 地址
2. 连接该蓝牙设备
3. 检查行驶模式是否激活
4. 断开蓝牙
5. 检查模式是否停用

**测试应用暂停：**
1. 激活包含暂停应用的模式
2. 检查目标应用图标是否变灰
3. 尝试启动目标应用，确认被阻止
4. 停用模式
5. 确认应用恢复正常

**测试通知过滤：**
1. 激活模式（白名单只包含微信）
2. 发送测试通知（抖音、微信）
3. 确认只有微信通知显示
4. 停用模式
5. 确认所有通知恢复

### 11.3 日志监控

**关键日志点：**
```bash
adb logcat -s XposedBridge:V | grep HyperModes
```

**期望日志：**
```
[RoutineCoreEngine] Config loaded: 3 modes
[ScheduledModeManager] Scheduled alarm for work_mode at 09:00
[DrivingTriggerManager] Bluetooth connected: AA:BB:CC:DD:EE:FF
[RoutineCoreEngine] Activating mode: 驾驶
[ModeActionExecutor] Applying mode: 驾驶
[NotificationFilterHook] Muted notification from com.ss.android.ugc.aweme
[AppSuspendController] Suspended 2 apps
[StatusBarIconInjector] Icon updated: 🚗
```

---

## 12. 部署与发布

### 12.1 LSPosed 模块配置

**xposed_init（META-INF/xposed/xposed_init）：**
```
com.banana.hypermodes.XposedInit
```

**作用域配置：**
- 勾选 `android`（system_server）
- 勾选 `com.android.deskclock`（系统闹钟）
- 勾选 `com.android.systemui`（状态栏）

### 12.2 用户安装步骤

1. 安装 LSPosed 框架
2. 安装 HyperModes APK
3. 在 LSPosed 管理器中启用 HyperModes 模块
4. 勾选作用域：android、com.android.deskclock、com.android.systemui
5. 软重启系统：`adb shell stop && adb shell start`
6. 打开 HyperModes App，配置模式
7. 添加快速设置磁贴（可选）

### 12.3 兼容性说明

**支持的系统：**
- HyperOS 2.0+（基于 Android 16）

**不支持的系统：**
- MIUI 14 及更早版本
- 其他 Android ROM

**依赖：**
- LSPosed 框架 1.9.0+
- libxposed API 101+

---

## 13. 未来扩展

### 13.1 短期计划

1. **蓝牙设备选择器 UI** - 扫描并显示配对的蓝牙设备列表
2. **更丰富的通知过滤** - 支持按通知类型过滤（来电、短信、应用通知）
3. **模式切换动画** - 状态栏图标切换时的动画效果
4. **模式切换历史** - 记录模式激活/停用历史，供用户查看

### 13.2 长期计划

1. **AI 智能模式推荐** - 根据用户习惯自动推荐模式配置
2. **位置触发** - 到达特定地点自动激活模式
3. **NFC 触发** - 扫描 NFC 标签触发模式
4. **多用户支持** - 支持工作资料/多用户环境
5. **云同步** - 模式配置云端同步

---

## 14. 总结

### 14.1 架构优势

1. **零进程常驻** - 彻底解决 HyperOS 停止状态问题
2. **系统级权限** - 无需 Root，利用 LSPosed 获得系统权限
3. **配置驱动** - 单一数据源，前后端解耦
4. **模块化设计** - 每个功能独立，易于维护和测试
5. **用户体验优秀** - 状态栏图标 + 快速设置磁贴，操作便捷

### 14.2 技术亮点

1. **system_server 注入** - 在 Android 最核心进程中运行
2. **通知精准过滤** - Hook NotificationManagerService 实现白名单
3. **应用暂停** - 使用系统 API 实现图标变灰
4. **蓝牙优先触发逻辑** - 智能判断行驶模式
5. **SystemUI Hook** - 状态栏图标无缝集成

### 14.3 开发优先级

**Phase 1（核心功能）：**
1. RoutineCoreEngine + 配置解析
2. ScheduledModeManager（定时模式）
3. NotificationFilterHook（通知过滤）
4. AppSuspendController（应用暂停）
5. ModeActionExecutor（模式执行器）

**Phase 2（触发器）：**
6. DrivingTriggerManager（行驶模式）
7. BedtimeListener（睡眠模式集成）

**Phase 3（显示与控制）：**
8. DisplayModeController（深色/灰度）
9. DndController（勿扰模式）
10. ContactFilterController（联系人过滤）

**Phase 4（UI 扩展）：**
11. StatusBarIconHook（状态栏图标）
12. TileService（快速设置磁贴）
13. 前端 UI 改造

**Phase 5（优化与测试）：**
14. 清理旧代码
15. 完整测试
16. 性能优化
17. 文档完善

---

**设计文档结束**

