# Complex Trigger 2.0 架构图

## 触发逻辑示意

```
模式激活条件 = TriggerGroup1 OR TriggerGroup2 OR TriggerGroup3 ...

TriggerGroup (Single):
  └─ Trigger 1  ✓ → 模式激活

TriggerGroup (Compound):
  ├─ Trigger 1  ✓
  ├─ Trigger 2  ✓  → 全部满足 → 模式激活
  └─ Trigger 3  ✓

TriggerGroup (Compound - 部分满足):
  ├─ Trigger 1  ✓
  ├─ Trigger 2  ✗  → 未全部满足 → 模式不激活
  └─ Trigger 3  ✓
```

## 实际示例

### 示例 1: 在家办公模式

```
模式: 在家办公
├─ [组合1] 工作时段在家
│  ├─ ⏰ 时间: 周一-周五 9:00-18:00  ✓
│  └─ 📡 WiFi: 家里WiFi                ✓
│                                    → 激活 ✓
├─ [组合2] 使用办公软件在家
│  ├─ 📱 应用: 钉钉/飞书/企业微信      ✗
│  └─ 📡 WiFi: 家里WiFi                ✓
│                                    → 不激活 ✗
```

只要任意一个组合满足，模式就会激活。上面的例子中，组合1 满足，所以模式激活。

### 示例 2: 驾驶勿扰增强版

```
模式: 驾驶勿扰
├─ [单个1] 蓝牙车载
│  └─ 🔵 蓝牙: 连接任意车载设备        ✓ → 激活 ✓
│
├─ [组合2] 导航+音乐
│  ├─ 📱 应用: 高德地图/百度地图      ✓
│  └─ 🎵 音乐: 正在播放              ✓
│                                    → 激活 ✓
│
└─ [组合3] 特定位置+时间
   ├─ 📍 位置: 进入市区                ✓
   ├─ ⏰ 时间: 7:00-9:00, 17:00-19:00 ✓
   └─ 🎵 音乐: 正在播放               ✗
                                     → 不激活 ✗
```

这个例子中，单个1 和 组合2 都满足，模式会激活。

## 系统架构

```
┌─────────────────────────────────────────────────────┐
│                   ModeDetailScreen                   │
│  ┌────────────────────────────────────────────────┐ │
│  │  TriggerTypeSelectionDialog                    │ │
│  │  - 单个触发器                                   │ │
│  │  - 组合触发器                                   │ │
│  └────────────────────────────────────────────────┘ │
│                         │                            │
│         ┌───────────────┴───────────────┐            │
│         ▼                               ▼            │
│  ┌──────────────┐             ┌──────────────────┐  │
│  │ 单个触发器    │             │ 组合触发器编辑   │  │
│  │ TriggerDialog│             │ CompoundTrigger  │  │
│  │              │             │ EditDialog       │  │
│  └──────────────┘             └──────────────────┘  │
│                                                      │
│  ┌────────────────────────────────────────────────┐ │
│  │           TriggerGroupCard (显示)              │ │
│  │  - Single: 显示单个触发器                       │ │
│  │  - Compound: 显示 AND 关系的多个触发器          │ │
│  └────────────────────────────────────────────────┘ │
└──────────────────────┬───────────────────────────────┘
                       │ 保存
                       ▼
┌─────────────────────────────────────────────────────┐
│                    ModeStore.kt                      │
│  - 转换 ModeTriggerGroup → TriggerGroup              │
│  - 保存到 Settings.Global                            │
└──────────────────────┬───────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────┐
│                 RoutineCoreEngine                    │
│                         │                            │
│                         ▼                            │
│             TriggerGroupManager (v2.0)               │
│  ┌────────────────────────────────────────────────┐ │
│  │ 触发器状态跟踪                                  │ │
│  │  modeId → triggerKey → isActive                 │ │
│  ├────────────────────────────────────────────────┤ │
│  │ 组状态评估                                      │ │
│  │  - Single: 单个触发器状态                       │ │
│  │  - Compound: ALL 触发器 AND 逻辑                │ │
│  ├────────────────────────────────────────────────┤ │
│  │ 模式激活决策                                    │ │
│  │  - ANY 组满足 → 激活模式                        │ │
│  │  - NO 组满足 → 停用模式                         │ │
│  └────────────────────────────────────────────────┘ │
│                         │                            │
│         ┌───────────────┼───────────────┐            │
│         ▼               ▼               ▼            │
│   WifiManager    BluetoothManager  AppManager ...    │
└─────────────────────────────────────────────────────┘
```

## 数据流

### 1. 创建流程
```
用户操作
  → 选择触发器类型 (Single/Compound)
    → 配置触发条件
      → 保存到 Mode.settings.triggerGroups
        → 转换为 ModeConfig.triggerGroups
          → 持久化到 Settings.Global
```

### 2. 触发流程
```
系统事件 (WiFi连接/应用启动/时间到达...)
  → 相应 TriggerManager 检测到变化
    → 通知 TriggerGroupManager
      → 更新触发器状态
        → 评估组合条件 (AND)
          → 评估组间条件 (OR)
            → 决定是否激活/停用模式
              → 调用 RoutineCoreEngine
```

## 代码结构

```
HyperModes/
├── systemserver/
│   ├── config/
│   │   └── ModeConfig.kt
│   │       ├── TriggerGroup.Single
│   │       └── TriggerGroup.Compound
│   └── trigger/
│       ├── TriggerGroupManager.kt          (新增 v2.0)
│       ├── ComplexTriggerManager.kt        (保留 v1.3 兼容)
│       ├── WifiTriggerManager.kt
│       ├── BluetoothTriggerManager.kt
│       └── ...
├── data/
│   └── Models.kt
│       ├── ModeTriggerGroup.Single
│       └── ModeTriggerGroup.Compound
└── ui/
    ├── ModeDetailScreen.kt                 (需要集成)
    └── components/
        ├── TriggerTypeSelectionDialog.kt   (新增)
        ├── CompoundTriggerEditDialog.kt    (新增)
        ├── TriggerGroupCard.kt             (新增)
        └── TriggerSelectionDialog.kt       (已有)
```

## 兼容性策略

### 数据层
- `ModeConfig` 同时包含 `complexTriggers` (v1.3) 和 `triggerGroups` (v2.0)
- 读取配置时，优先使用 `triggerGroups`，fallback 到 `complexTriggers`

### 逻辑层
- `TriggerGroupManager` 同时处理两种格式
- 为 legacy triggers 创建虚拟的 Single TriggerGroup

### UI 层
- 新建模式使用 v2.0 界面
- 编辑旧模式时，显示迁移提示或自动转换

