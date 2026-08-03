# Polaris 代理服务架构设计

**问题**: System_server 无法直接绑定第三方服务（Polaris），即使使用 createPackageContext 也会超时。

**根本原因**: 
- Android系统限制system_server进程绑定第三方服务
- SecurityCenter能工作是因为它在自己的应用进程中运行
- 我们的system_server hook无法突破这个限制

---

## 解决方案：应用进程代理架构

```
System_Server (RoutineCoreEngine)
    ↓ AIDL
HyperModes App Process (PolarisProxyService)
    ↓ SDK
Polaris Service (com.xiaomi.gnss.polaris)
```

### 组件设计

#### 1. PolarisProxyService (应用进程)
运行在应用进程中的Service，负责：
- 使用PolarisManager SDK连接Polaris服务
- 管理地理围栏的添加/删除
- 接收Polaris回调并转发给system_server

**AIDL接口**:
```aidl
interface IPolarisProxy {
    void init();
    void addGeofence(String fenceId, String modeId, String triggerId, 
                     double lat, double lng, int radius, int transitionType);
    void removeGeofence(String fenceId);
    void removeAllGeofences();
}
```

#### 2. PolarisProxyClient (system_server)
在system_server中的客户端，负责：
- 绑定到PolarisProxyService
- 转发LocationTriggerManager的请求
- 接收并路由地理围栏事件

#### 3. 事件流

**注册地理围栏**:
```
LocationTriggerManager
  → PolarisProxyClient (AIDL call)
  → PolarisProxyService
  → PolarisManager.addGeofence()
  → Polaris Service
```

**接收事件**:
```
Polaris Service
  → Broadcast to PolarisCallbackReceiver
  → PolarisProxyService.onGeofenceEvent()
  → Broadcast to system_server
  → PolarisProxyClient
  → LocationTriggerManager.onGeofenceEvent()
```

---

## 实现步骤

### Phase 1: AIDL接口定义
```aidl
// IPolarisProxy.aidl
package com.banana.hypermodes.proxy;

interface IPolarisProxy {
    void init();
    boolean isConnected();
    void addGeofence(String fenceId, String modeId, String triggerId,
                     double latitude, double longitude, int radius, 
                     int transitionType, int confidence);
    void removeGeofence(String fenceId);
    void clearAllGeofences();
}
```

### Phase 2: PolarisProxyService实现
```kotlin
class PolarisProxyService : Service() {
    private var polarisManager: PolarisManager? = null
    private var geofenceService: PolarisGeofenceService? = null
    
    private val binder = object : IPolarisProxy.Stub() {
        override fun init() {
            // Initialize PolarisManager in app process
            polarisManager = PolarisManager.getInstance(applicationContext)
            polarisManager?.connectPolarisServiceSync()
            geofenceService = polarisManager?.getSubService(...)
        }
        
        override fun addGeofence(...) {
            val fence = MiGeofence().apply { ... }
            geofenceService?.addGeofence(fence)
        }
        
        override fun removeGeofence(fenceId: String) {
            geofenceService?.deleteGeofence(fenceId)
        }
    }
    
    override fun onBind(intent: Intent): IBinder = binder
}
```

### Phase 3: PolarisProxyClient实现
```kotlin
class PolarisProxyClient(private val context: Context) {
    private var proxyService: IPolarisProxy? = null
    
    private val connection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            proxyService = IPolarisProxy.Stub.asInterface(service)
            proxyService?.init()
        }
    }
    
    fun bind() {
        val intent = Intent()
        intent.component = ComponentName(
            "com.banana.hypermodes",
            "com.banana.hypermodes.proxy.PolarisProxyService"
        )
        context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
    }
    
    fun addGeofence(...) {
        proxyService?.addGeofence(...)
    }
}
```

### Phase 4: 集成到LocationTriggerManager
```kotlin
class LocationTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val polarisClient = PolarisProxyClient(context)
    
    init {
        polarisClient.bind()
    }
    
    fun updateConfigs(...) {
        allTriggers.forEach { (modeId, triggerId, config) ->
            polarisClient.addGeofence(...)
        }
    }
}
```

---

## 优势

1. **绕过system_server限制**: 应用进程可以正常绑定Polaris
2. **使用官方SDK**: 保持与SecurityCenter相同的实现
3. **解耦架构**: 代理服务可以独立测试和维护
4. **进程隔离**: 应用崩溃不影响system_server

## 劣势

1. **额外进程**: 需要保持应用进程存活
2. **IPC开销**: 增加了一层AIDL调用
3. **复杂性**: 架构变复杂，需要管理服务生命周期

---

## 替代方案对比

### 方案A: 直接在system_server中绑定 (当前尝试)
- ❌ 失败: system_server无权限绑定第三方服务
- ❌ createPackageContext无法解决权限问题

### 方案B: 应用进程代理 (推荐)
- ✅ 绕过system_server限制
- ✅ 使用官方SDK
- ⚠️ 需要额外架构

### 方案C: 完全迁移到应用进程
- ✅ 最简单的绑定
- ❌ 需要重写整个RoutineCoreEngine
- ❌ 失去system_server的权限优势

---

## 下一步

1. 创建 IPolarisProxy.aidl 接口
2. 实现 PolarisProxyService
3. 实现 PolarisProxyClient
4. 更新 LocationTriggerManager 使用客户端
5. 在 AndroidManifest.xml 中注册服务
6. 测试完整流程

---

## 参考

- SecurityCenter AutoTask实现: 在应用进程中直接使用SDK
- Android系统限制: system_server只能绑定系统签名的服务
- LSPosed文档: Hook在system_server但不能突破系统限制
