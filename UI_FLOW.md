# Complex Trigger 2.0 - UI 交互流程

## 用户交互流程图

```
用户在 ModeDetailScreen
        ↓
   点击 "Add Trigger"
        ↓
┌──────────────────────────────┐
│ TriggerTypeSelectionDialog   │
│  ○ 单个触发器                │
│  ○ 组合触发器 (AND)          │
└──────────────────────────────┘
        ↓                ↓
        ↓                ↓
   [单个触发器]      [组合触发器]
        ↓                ↓
        ↓        ┌──────────────────────┐
        ↓        │ CompoundTriggerEdit  │
        ↓        │ Dialog               │
        ↓        │  - 触发器列表        │
        ↓        │  - 添加更多触发器    │
        ↓        │  - 组名称            │
        ↓        └──────────────────────┘
        ↓                ↓
        ↓←───────────────┘
        ↓
┌──────────────────────────────┐
│ TriggerSelectionDialog       │
│  ○ 时间                      │
│  ○ 应用                      │
│  ○ WiFi                      │
│  ○ 蓝牙                      │
│  ○ 位置                      │
│  ○ Intent                    │
│  ○ 音乐播放                  │
└──────────────────────────────┘
        ↓
   [选择具体触发器]
        ↓
  ┌───────────────┐
  │ 时间: TimePicker (开始/结束)
  │ 应用: AppPicker Screen
  │ WiFi: WifiPicker Screen
  │ 蓝牙: BluetoothPicker Screen
  │ 位置: LocationPicker Screen
  │ Intent: IntentPicker Screen
  │ 音乐: 直接添加
  └───────────────┘
        ↓
   [触发器已添加]
        ↓
  ┌─────────────────────┐
  │ LaunchedEffect      │
  │ 自动转换为          │
  │ TriggerGroup        │
  └─────────────────────┘
        ↓
  显示在 ModeDetailScreen
  使用 TriggerGroupCard
```

## 数据流

```
用户操作
    ↓
UI 状态更新
    ↓
├─ 单个触发器: ModeTriggerGroup.Single(trigger)
│  ↓
│  添加到 editedMode.settings.triggerGroups
│  ↓
│  onSave(editedMode)
│
└─ 组合触发器: ModeTriggerGroup.Compound(triggers, name)
   ↓
   1. 收集多个 triggers 到 editingCompoundTriggers
   2. 用户输入 name
   3. 创建 Compound group
   ↓
   添加到 editedMode.settings.triggerGroups
   ↓
   onSave(editedMode)
```

## 触发逻辑

```
TriggerGroupManager
    ↓
检查每个 TriggerGroup
    ↓
├─ Single Group
│  └─ 检查单个触发器是否满足
│
└─ Compound Group (AND)
   └─ 检查所有触发器是否都满足
    ↓
任一 Group 满足 (OR)
    ↓
激活模式
```

## 实际例子

### 例子 1: 工作模式
```
TriggerGroup 1 (Compound): "上班时间在公司"
  ├─ Time: 09:00-18:00 (AND)
  └─ Location: 公司地址
  
OR

TriggerGroup 2 (Single): "连接公司 WiFi"
  └─ WiFi: CompanyNetwork
```
**逻辑**: (时间 AND 位置) OR WiFi
- 如果在 9-18 点且在公司 → 激活
- 或者连接到公司 WiFi → 激活

### 例子 2: 驾驶模式
```
TriggerGroup 1 (Compound): "开车听音乐"
  ├─ Bluetooth: 车载蓝牙 (AND)
  └─ Music: 正在播放音乐
  
OR

TriggerGroup 2 (Single): "导航应用"
  └─ App: Google Maps
```
**逻辑**: (蓝牙 AND 音乐) OR 导航
- 如果连接车载蓝牙且在播放音乐 → 激活
- 或者打开导航应用 → 激活

### 例子 3: 家庭模式
```
TriggerGroup 1 (Compound): "晚上在家"
  ├─ Time: 18:00-08:00 (AND)
  ├─ Location: 家庭地址 (AND)
  └─ WiFi: 家庭 WiFi
```
**逻辑**: 时间 AND 位置 AND WiFi（三个条件都满足）
- 只有在晚上 6 点到早上 8 点
- 且在家里
- 且连接家庭 WiFi
- 才激活

## 状态管理

### ModeDetailScreen 状态变量

| 变量 | 类型 | 用途 |
|------|------|------|
| `showTriggerTypeDialog` | Boolean | 显示触发器类型选择对话框 |
| `showCompoundTriggerDialog` | Boolean | 显示组合触发器编辑对话框 |
| `editingCompoundTriggers` | List<ModeTrigger> | 正在编辑的组合触发器列表 |
| `editingCompoundName` | String? | 正在编辑的组合触发器名称 |
| `editingGroupIndex` | Int? | 正在编辑的 group 索引 |

### 状态转换

```
初始状态
  ↓
点击 "Add Trigger"
  ↓
showTriggerTypeDialog = true
  ↓
选择 "单个触发器"
  ↓
showTriggerTypeDialog = false
showTriggerSelector = true
  ↓
选择触发器类型 → 进入对应 Picker
  ↓
返回时 LaunchedEffect 检测到新 trigger
  ↓
自动转换为 Single TriggerGroup
  ↓
onSave(editedMode)

---

初始状态
  ↓
点击 "Add Trigger"
  ↓
showTriggerTypeDialog = true
  ↓
选择 "组合触发器"
  ↓
showTriggerTypeDialog = false
showCompoundTriggerDialog = true
editingCompoundTriggers = []
  ↓
点击 "添加触发器" (在 CompoundTriggerEditDialog 中)
  ↓
showCompoundTriggerDialog = false (暂时)
showTriggerSelector = true
  ↓
选择触发器 → Picker → 返回
  ↓
LaunchedEffect 检测到在 compound 模式
  ↓
editingCompoundTriggers += new trigger
showCompoundTriggerDialog = true (重新显示)
  ↓
用户继续添加或点击 "确认"
  ↓
创建 Compound TriggerGroup
  ↓
onSave(editedMode)
```

## 关键代码位置

| 功能 | 文件 | 位置 |
|------|------|------|
| UI 组件 | TriggerTypeSelectionDialog.kt | 选择单个/组合 |
| UI 组件 | CompoundTriggerEditDialog.kt | 编辑组合触发器 |
| UI 组件 | TriggerGroupCard.kt | 显示触发器组 |
| 集成 | ModeDetailScreen.kt | 行 32-34 (imports) |
| 集成 | ModeDetailScreen.kt | 行 103-108 (状态) |
| 集成 | ModeDetailScreen.kt | 行 110-165 (监听器) |
| 集成 | ModeDetailScreen.kt | 行 451-483 (显示) |
| 集成 | ModeDetailScreen.kt | 行 712-768 (对话框) |
| 业务逻辑 | TriggerGroupManager.kt | AND/OR 逻辑 |
| 数据模型 | Models.kt | ModeTriggerGroup |
