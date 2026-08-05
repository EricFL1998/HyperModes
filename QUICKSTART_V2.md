# Complex Trigger 2.0 快速开始指南

## 🎯 已完成的工作

✅ 所有核心代码已实现并准备就绪！

### 文件清单

**数据模型**
- ✅ `ModeConfig.kt` - 添加 `TriggerGroup` (Single/Compound)
- ✅ `Models.kt` - 添加 `ModeTriggerGroup` 和 `triggerGroups` 字段

**业务逻辑**
- ✅ `TriggerGroupManager.kt` - 新的触发器组管理器（支持 AND/OR 逻辑）

**UI 组件**
- ✅ `TriggerTypeSelectionDialog.kt` - 选择单个/组合触发器
- ✅ `CompoundTriggerEditDialog.kt` - 编辑组合触发器
- ✅ `TriggerGroupCard.kt` - 显示触发器组

**资源和文档**
- ✅ `strings.xml` - 添加了所有必需的字符串
- ✅ 完整的文档和集成示例

## 🚀 接下来要做什么

只需要完成 **3 个集成步骤**：

### 步骤 1: 集成 UI 到 ModeDetailScreen.kt

打开 `INTEGRATION_EXAMPLE.kt`，将代码复制到 `ModeDetailScreen.kt` 中：

1. **添加状态变量**（第 1 节）
   ```kotlin
   var showTriggerTypeDialog by remember(mode.id) { mutableStateOf(false) }
   var showCompoundTriggerDialog by remember(mode.id) { mutableStateOf(false) }
   // ... 等等
   ```

2. **在 LazyColumn 中添加触发器显示**（第 2 节）
   - 使用 `TriggerGroupCard` 显示每个触发器组
   - 添加"添加触发器"按钮

3. **添加对话框**（第 3 节）
   - `TriggerTypeSelectionDialog`
   - `CompoundTriggerEditDialog`

### 步骤 2: 添加数据转换到 ModeStore.kt

添加转换函数，将 UI 层的 `ModeTriggerGroup` 转换为配置层的 `TriggerGroup`：

```kotlin
fun ModeTriggerGroup.toTriggerGroup(): TriggerGroup {
    return when (this) {
        is ModeTriggerGroup.Single -> TriggerGroup.Single(
            trigger = trigger.toComplexTrigger()
        )
        is ModeTriggerGroup.Compound -> TriggerGroup.Compound(
            triggers = triggers.map { it.toComplexTrigger() },
            name = name
        )
    }
}
```

### 步骤 3: 集成 TriggerGroupManager 到 RoutineCoreEngine.kt

替换或并存现有的 `ComplexTriggerManager`：

```kotlin
// 选项 A: 完全替换（推荐）
private val triggerManager = TriggerGroupManager(context, this)

// 选项 B: 并存（平滑迁移）
private val complexTriggerManager = ComplexTriggerManager(context, this)
private val triggerGroupManager = TriggerGroupManager(context, this)
```

## 📖 功能说明

### 用户操作流程

1. 用户点击"添加触发器"
2. 选择"单个触发器"或"组合触发器"
3. 如果选择组合：
   - 打开组合编辑对话框
   - 添加多个触发条件
   - 输入组合名称（可选）
   - 保存
4. 触发器组显示在列表中

### 触发逻辑

```
单个触发器: 条件满足 → 激活模式

组合触发器: 所有条件都满足 → 激活模式
  ├─ 条件1 ✓
  ├─ 条件2 ✓
  └─ 条件3 ✓

多个组合: 任意组合满足 → 激活模式
  组合1 OR 组合2 OR 组合3
```

## 🎨 UI 预览

**组合触发器显示：**
```
🔗 在家工作时
  2 所有条件必须同时满足
  
  • ⏰ 时间触发
    周一-五 9:00-18:00
  ∧ 📡 WiFi触发
    家里WiFi
```

**单个触发器显示：**
```
🔵 蓝牙触发
  连接任意车载设备
```

## 💡 实际应用场景

### 场景 1: 智能在家办公
```
模式: 在家工作
├─ [组合1] 工作时间在家
│  ├─ 时间: 周一-五 9:00-18:00
│  └─ WiFi: 家里WiFi
└─ [组合2] 使用办公软件在家
   ├─ 应用: 钉钉/企业微信
   └─ WiFi: 家里WiFi
```

### 场景 2: 智能驾驶模式
```
模式: 驾驶勿扰
├─ [单个] 连接车载蓝牙
├─ [组合] 导航+音乐
│  ├─ 应用: 高德地图
│  └─ 音乐: 正在播放
└─ [组合] 通勤时段在路上
   ├─ 时间: 7-9点或17-19点
   ├─ 位置: 市区范围
   └─ 音乐: 正在播放
```

### 场景 3: 健身房模式
```
模式: 健身
└─ [组合] 在健身房时
   ├─ 位置: 健身房
   ├─ 时间: 18:00-22:00
   └─ 音乐: 正在播放
```

## 🔍 测试建议

1. **基础测试**
   - 创建单个触发器
   - 创建简单组合（2个条件）
   - 编辑现有触发器组
   - 删除触发器组

2. **功能测试**
   - 组合触发器 AND 逻辑
   - 多个组合 OR 逻辑
   - 部分条件满足时不激活
   - 所有条件满足时激活

3. **兼容性测试**
   - 旧版本数据迁移
   - v1.3 触发器仍然工作
   - 混合使用新旧触发器

## 📚 参考文档

- **TRIGGER_V2_IMPLEMENTATION.md** - 详细实现文档
- **TRIGGER_V2_ARCHITECTURE.md** - 架构图和数据流
- **TRIGGER_V2_SUMMARY.md** - 完整总结
- **INTEGRATION_EXAMPLE.kt** - 完整集成代码示例

## 🤝 需要帮助？

所有代码都已经写好并测试通过结构！只需要：
1. 复制 INTEGRATION_EXAMPLE.kt 中的代码
2. 根据现有代码结构调整
3. 测试功能

祝你集成顺利！🎉
