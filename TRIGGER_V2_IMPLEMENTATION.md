# Complex Trigger 2.0 实现文档

## 概述

Complex Trigger 2.0 增强了触发器系统，支持组合触发器（AND 逻辑）。

## 核心概念

### 1. 触发器组 (TriggerGroup)

触发器组有两种类型：

- **Single (单个触发器)**: 包含一个触发条件
- **Compound (组合触发器)**: 包含多个触发条件，必须**同时满足**才会激活

### 2. 逻辑关系

- **组内关系**: Compound 组内的所有触发器必须同时满足 (AND 逻辑)
- **组间关系**: 多个 TriggerGroup 之间是 OR 关系，任意一个组满足即可激活模式

### 示例场景

**场景：在家办公模式**
- 组合触发器 1: 工作日 (9:00-18:00) **AND** 连接家里WiFi
- 组合触发器 2: 使用办公应用 **AND** 连接家里WiFi

只要满足其中一个组合，模式就会激活。

## 数据模型

### ModeConfig.kt

```kotlin
@Serializable
data class ModeConfig(
    // ... 其他字段
    val complexTriggers: List<ComplexTrigger> = emptyList(),  // v1.3 兼容
    val triggerGroups: List<TriggerGroup> = emptyList(),      // v2.0 新增
)

@Serializable
sealed class TriggerGroup {
    @Serializable
    data class Single(
        val trigger: ComplexTrigger
    ) : TriggerGroup()

    @Serializable
    data class Compound(
        val triggers: List<ComplexTrigger>,
        val name: String? = null  // 用户自定义组名
    ) : TriggerGroup()
}
```

### Models.kt (UI 层)

```kotlin
data class ModeSettings(
    // ... 其他字段
    val triggers: List<ModeTrigger> = emptyList(),            // v1.3
    val triggerGroups: List<ModeTriggerGroup> = emptyList(),  // v2.0
)

sealed class ModeTriggerGroup {
    data class Single(
        val trigger: ModeTrigger
    ) : ModeTriggerGroup()

    data class Compound(
        val triggers: List<ModeTrigger>,
        val name: String? = null
    ) : ModeTriggerGroup()
}
```

## 核心组件

### 1. TriggerGroupManager

负责管理 v2.0 触发器组的核心逻辑：

- 跟踪每个触发器的状态
- 评估组合触发器的 AND 条件
- 处理多个触发器组的 OR 逻辑
- 向后兼容 v1.3 的 complexTriggers

### 2. UI 组件

#### TriggerTypeSelectionDialog
- 让用户选择创建单个触发器还是组合触发器

#### CompoundTriggerEditDialog
- 编辑组合触发器
- 添加/删除触发条件
- 设置组合名称
- 显示 AND 逻辑提示

#### TriggerGroupCard
- 展示触发器组
- 单个触发器：显示类型和详情
- 组合触发器：显示所有条件及 AND 关系

## 使用流程

### 创建组合触发器

1. 用户点击"添加触发器"
2. 弹出 TriggerTypeSelectionDialog 选择类型
3. 选择"组合触发器"
4. 打开 CompoundTriggerEditDialog
5. 依次添加多个触发条件
6. 可选：输入组合名称（如"在家工作时"）
7. 确认保存

### 在 ModeDetailScreen 中集成

需要在 ModeDetailScreen 中：

1. 添加状态管理：
```kotlin
var showTriggerTypeDialog by remember { mutableStateOf(false) }
var isEditingCompound by remember { mutableStateOf(false) }
var editingCompoundTriggers by remember { mutableStateOf<List<ModeTrigger>>(emptyList()) }
```

2. 显示触发器组列表：
```kotlin
editedMode.settings.triggerGroups.forEachIndexed { index, group ->
    TriggerGroupCard(
        group = group,
        groupIndex = index,
        onRemove = { /* 移除该组 */ },
        onEdit = { /* 编辑该组 */ }
    )
}
```

3. 添加触发器按钮：
```kotlin
TextButton(
    text = "+ ${stringResource(R.string.add_trigger_title)}",
    onClick = { showTriggerTypeDialog = true }
)
```

## 向后兼容

系统同时支持：
- `complexTriggers` (v1.3): OR 逻辑，任意触发器激活
- `triggerGroups` (v2.0): 支持 AND/OR 组合逻辑

TriggerGroupManager 会自动处理两种格式，确保平滑迁移。

## 字符串资源

已添加的字符串资源：
- `select_trigger_type`: "选择触发器类型"
- `single_trigger`: "单个触发器"
- `single_trigger_desc`: "添加一个触发条件"
- `compound_trigger`: "组合触发器"
- `compound_trigger_desc`: "添加多个触发条件，需要同时满足"
- `compound_trigger_name`: "组合触发器名称"
- `add_trigger_to_group`: "添加触发条件"
- `trigger_group_and_logic`: "所有条件必须同时满足"

## 下一步

需要在以下文件中集成 UI：

1. **ModeDetailScreen.kt**: 
   - 添加触发器组显示区域
   - 集成 TriggerTypeSelectionDialog
   - 集成 CompoundTriggerEditDialog
   - 处理触发器组的添加/编辑/删除

2. **ModeStore.kt**:
   - 添加转换逻辑，将 ModeTriggerGroup 转换为 TriggerGroup
   - 支持保存和加载触发器组

3. **RoutineCoreEngine.kt**:
   - 将 ComplexTriggerManager 替换为 TriggerGroupManager
   - 或者让两者共存以支持平滑迁移

## 测试场景

1. **单个触发器**: 应与 v1.3 行为一致
2. **简单组合**: 时间 + WiFi（工作日在家）
3. **复杂组合**: 时间 + 位置 + 应用（特定时间在特定地点使用特定应用）
4. **多组合**: 组合1 OR 组合2（灵活的激活条件）

