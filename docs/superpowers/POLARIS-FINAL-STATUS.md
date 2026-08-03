# Polaris 位置触发器集成 - 最终状态报告

**日期**: 2026-08-02  
**状态**: 架构验证成功，服务绑定问题待解决

---

## ✅ 已完成的工作

### 1. SDK 集成
- ✅ 从 SecurityCenter 提取完整的 Polaris SDK
- ✅ 提取并集成所有 AIDL 接口文件
- ✅ 添加到项目依赖并成功编译

### 2. ContentProvider 代理架构
创建了完整的跨进程通信架构：

**应用进程侧**：
- `PolarisProxyProvider` - ContentProvider 实现
  - 管理 Polaris SDK 生命周期
  - 提供 init, add_geofence, remove_geofence, clear_all, is_connected 方法
  - 处理地理围栏事件回调

**System Server 侧**：
- `PolarisProxyClient` - ContentProvider 客户端
  - 从 system_server 调用 ContentProvider
  - 管理期望的围栏状态
  - 实现围栏调和逻辑（reconciliation）
  - 包含自动重试机制（最多3次）

**集成层**：
- `LocationTriggerManager` - 位置触发器管理器
  - 接收配置更新
  - 将触发器转换为 Polaris 围栏
  - 处理围栏事件并回调

### 3. 功能验证（手动测试）

通过手动命令验证了完整架构：

```bash
# 1. 初始化 Polaris SDK
adb shell "content call --uri content://com.banana.hypermodes.provider.polaris --method init"
Result: Bundle[{success=true}]

# 2. 添加地理围栏
adb shell "content call --uri content://com.banana.hypermodes.provider.polaris --method add_geofence ..."
Result: Bundle[{success=true}]

# 3. Polaris 服务确认
PolarisService-GeoService-Provider: addGeofence call by :com.banana.hypermodes
PolarisProxyProvider: Added geofence: hypermodes_custom_...

# 4. 位置事件触发
iZatGeofenceCallback-onTransitionEvent, event:2, handler:hypermodes_custom_...
```

**结论**：ContentProvider 架构完全可行，Polaris SDK 集成成功。

---

## ❌ 当前问题

### 核心问题：Polaris 服务绑定失败

**现象**：
```
PolarisProxyProvider: Connecting to Polaris service...
PolarisManager: wait connect time 0, bindResult: true
PolarisManager: wait connect time 1, bindResult: true
[没有 onServiceConnected 回调]
Failed to get PolarisGeofenceService
```

**分析**：
1. `bindService()` 返回 `true` - 绑定请求被接受
2. `onServiceConnected()` 回调从未被调用 - 服务连接失败
3. 2秒超时后抛出异常

**可能的原因**：
1. **Polaris 服务未启动** - 系统启动时 Polaris 服务还没准备好
2. **权限问题** - 虽然在应用进程中绑定，但可能缺少某些权限
3. **服务崩溃** - Polaris 服务启动后立即崩溃
4. **多实例冲突** - SecurityCenter 已经绑定了 Polaris，不允许第二个应用绑定

### 集成流程（当前状态）

配置更新时的完整流程：

```
✅ Settings.Global 配置更新
  ↓
✅ RoutineCoreEngine.loadConfigFromSettings()
  ↓
✅ ComplexTriggerManager.init(modes)
  ↓
✅ ComplexTriggerManager.updateSubManagers()
  ↓ Found location trigger: modeId=custom_1785676702435
✅ LocationTriggerManager.updateConfigs(locationConfigs)
  ↓
✅ PolarisProxyClient.updateTriggers()
  ↓
✅ PolarisProxyClient.isConnected() → false
  ↓
✅ PolarisProxyClient.init() (with retry)
  ↓
✅ ContentProvider: PolarisProxyProvider.handleInit()
  ↓
✅ PolarisManager.connectPolarisServiceSync()
  ↓
✅ context.startService(Polaris)
  ↓
✅ context.bindService(Polaris) → returns true
  ↓
❌ onServiceConnected() → NEVER CALLED
  ↓
❌ Timeout after 2 seconds
  ↓
❌ Failed to get PolarisGeofenceService
```

---

## 🔍 SecurityCenter 的做法

查看了 SecurityCenter 的代码，发现它：
1. 在应用进程中使用 Polaris SDK（和我们一样）
2. 调用 `context.startService()` 然后 `context.bindService()`（SDK 内部实现）
3. 等待 `onServiceConnected` 回调

**关键差异**：SecurityCenter 是系统应用，可能有特殊权限或白名单。

---

## 🎯 解决方案建议

### 方案 A：延迟初始化 + 后台重试

在 `LocationTriggerManager` 中：
1. 首次初始化失败时不报错
2. 使用 Handler 每隔 30 秒重试一次
3. 直到 Polaris 服务准备好

```kotlin
private val handler = Handler(Looper.getMainLooper())
private val retryRunnable = object : Runnable {
    override fun run() {
        if (!polarisClient.isConnected()) {
            Log.i(TAG, "Retrying Polaris initialization...")
            polarisClient.init()
            handler.postDelayed(this, 30000)
        }
    }
}
```

### 方案 B：监听系统广播

监听 Polaris 应用/服务的启动：
- `ACTION_PACKAGE_ADDED`
- `ACTION_BOOT_COMPLETED`
- 自定义广播（如果 Polaris 服务发送）

### 方案 C：使用 BroadcastReceiver 替代 ContentProvider

如果 Polaris 不允许多个应用绑定，可以：
1. 在 SecurityCenter 中添加 hook
2. 拦截 Polaris 事件并转发广播
3. HyperModes 监听广播

### 方案 D：检查权限和白名单

1. 检查 Polaris 服务的 AndroidManifest
2. 查看是否需要特殊权限
3. 查看是否有包名白名单

---

## 📝 下一步行动

### 立即可做
1. ✅ 实现方案 A（延迟重试）- 最简单
2. 查看 Polaris 服务的日志，确认为什么 `onServiceConnected` 没有被调用
3. 检查是否需要在 AndroidManifest 中声明额外权限

### 需要调研
1. 反编译 SecurityCenter，查看它的 AndroidManifest 权限
2. 检查 Polaris 服务是否有包名白名单
3. 查看系统日志中 Polaris 服务的启动/崩溃信息

### 备选方案
1. 如果 Polaris 确实不允许第三方绑定，使用方案 C（hook SecurityCenter）
2. 或者使用 Android 原生 Geofencing API 替代 Polaris

---

## 📚 关键文件

### 代码文件
- `app/src/main/java/com/banana/hypermodes/proxy/PolarisProxyProvider.kt`
- `app/src/main/java/com/banana/hypermodes/systemserver/geofence/PolarisProxyClient.kt`
- `app/src/main/java/com/banana/hypermodes/systemserver/trigger/LocationTriggerManager.kt`
- `app/src/main/java/com/banana/hypermodes/systemserver/trigger/ComplexTriggerManager.kt`
- `app/src/main/java/com/xiaomi/gnss/polaris/sdk/PolarisManager.java`

### 配置文件
- `app/src/main/AndroidManifest.xml` - PolarisProxyProvider 注册
- `app/src/main/aidl/` - Polaris AIDL 接口

### 文档
- `docs/superpowers/STATUS-POLARIS-INTEGRATION.md` - 中期状态
- `docs/superpowers/plans/2026-08-02-location-trigger-repair.md` - 初始计划
- `docs/superpowers/specs/2026-08-02-location-trigger-repair-design.md` - 设计文档

---

## 🧪 测试命令

### 手动测试 ContentProvider

```bash
# 检查连接状态
adb shell "content call --uri content://com.banana.hypermodes.provider.polaris --method is_connected"

# 初始化
adb shell "content call --uri content://com.banana.hypermodes.provider.polaris --method init"

# 添加围栏
adb shell "content call --uri content://com.banana.hypermodes.provider.polaris --method add_geofence \
  --extra fence_id:s:test_fence \
  --extra mode_id:s:test_mode \
  --extra trigger_id:s:test_trigger \
  --extra latitude:d:29.67 \
  --extra longitude:d:121.43 \
  --extra radius:i:500 \
  --extra transition_type:i:1 \
  --extra confidence:i:2"

# 清除所有围栏
adb shell "content call --uri content://com.banana.hypermodes.provider.polaris --method clear_all"
```

### 触发配置更新

```bash
# 触发配置重新加载
adb shell "settings put global pixel_routines_full_config \"\$(settings get global pixel_routines_full_config)\""

# 查看日志
adb logcat -d | grep -E "LocationTriggerManager|PolarisProxyClient|PolarisProxyProvider"
```

### 手动启动 Polaris 服务

```bash
# 启动服务
adb shell "am startservice com.xiaomi.gnss.polaris/.PolarisService"

# 检查服务状态
adb shell "dumpsys activity services | grep -A10 polaris"
```

---

## 📊 架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      Settings.Global                         │
│                 pixel_routines_full_config                   │
└───────────────────────┬─────────────────────────────────────┘
                        │ ContentObserver
                        ↓
┌─────────────────────────────────────────────────────────────┐
│              System Server Process (PID 3849)                │
├─────────────────────────────────────────────────────────────┤
│  RoutineCoreEngine                                           │
│    ↓                                                         │
│  ComplexTriggerManager                                       │
│    ↓                                                         │
│  LocationTriggerManager                                      │
│    ↓                                                         │
│  PolarisProxyClient ─────────────┐                          │
│    - isConnected()                │                          │
│    - init() [retry 3x]            │                          │
│    - updateTriggers()             │                          │
│    - reconcile()                  │                          │
└───────────────────────────────────┼──────────────────────────┘
                                    │ ContentProvider IPC
                                    ↓
┌─────────────────────────────────────────────────────────────┐
│            App Process (com.banana.hypermodes)               │
├─────────────────────────────────────────────────────────────┤
│  PolarisProxyProvider (ContentProvider)                      │
│    - call("init")                                            │
│    - call("add_geofence")                                    │
│    - call("remove_geofence")                                 │
│    - call("is_connected")                                    │
│    ↓                                                         │
│  PolarisManager (Xiaomi SDK)                                 │
│    - connectPolarisServiceSync()                             │
│    - getSubService(Geofence)                                 │
└───────────────────────────────────┼──────────────────────────┘
                                    │ Binder IPC
                                    ↓
┌─────────────────────────────────────────────────────────────┐
│        Polaris Service (com.xiaomi.gnss.polaris)             │
├─────────────────────────────────────────────────────────────┤
│  PolarisService                                              │
│    ↓                                                         │
│  PolarisGeoService-Provider                                  │
│    - registerComponent()                                     │
│    - addGeofence()                                           │
│    - removeGeofence()                                        │
│    ↓                                                         │
│  QcomGeoManager                                              │
│    ↓                                                         │
│  Hardware Geofence (Qualcomm)                                │
└─────────────────────────────────────────────────────────────┘
```

---

## 💡 关键发现

1. **ContentProvider 架构是正确的** - 手动测试证明完全可行
2. **Polaris SDK 本身没问题** - 在应用进程中可以正常使用（当服务可用时）
3. **问题在服务绑定** - `onServiceConnected` 回调从未被调用
4. **自动集成基本完成** - 配置更新 → 检测 → 初始化 → 添加围栏的流程已打通
5. **只差最后一步** - 需要解决 Polaris 服务绑定的时序/权限问题

---

**结论**：架构设计和实现都是正确的。现在需要解决 Polaris 服务绑定失败的问题，这可能需要：
1. 添加后台重试机制
2. 检查权限配置
3. 或者考虑替代方案（hook SecurityCenter 或使用原生 Geofencing API）
