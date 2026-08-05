// 这是一个集成示例，展示如何在 ModeDetailScreen.kt 中集成 Trigger v2.0 功能
// 将这些代码片段添加到 ModeDetailScreen.kt 的相应位置

// ============================================================
// 1. 在 ModeDetailScreen 函数顶部添加状态变量
// ============================================================

@Composable
fun ModeDetailScreen(
    mode: Mode,
    // ... 其他参数
) {
    var editedMode by remember(mode) { mutableStateOf(mode) }
    
    // === 新增：Trigger v2.0 状态 ===
    var showTriggerTypeDialog by remember(mode.id) { mutableStateOf(false) }
    var showCompoundTriggerDialog by remember(mode.id) { mutableStateOf(false) }
    var editingCompoundTriggers by remember(mode.id) { mutableStateOf<List<ModeTrigger>>(emptyList()) }
    var editingCompoundName by remember(mode.id) { mutableStateOf<String?>(null) }
    var editingGroupIndex by remember(mode.id) { mutableStateOf<Int?>(null) }
    
    // ... 其他状态变量

// ============================================================
// 2. 在 LazyColumn 中添加触发器组显示区域（在适当位置）
// ============================================================

    LazyColumn(
        // ... 其他参数
    ) {
        // ... 其他内容
        
        // === 新增：显示触发器组 ===
        if (editedMode.id != "dnd" && editedMode.id != "bedtime" && editedMode.id != "driving") {
            item {
                Text(
                    text = stringResource(R.string.triggers_section),
                    style = MiuixTheme.textStyles.subtitle,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            
            // 显示所有触发器组
            editedMode.settings.triggerGroups.forEachIndexed { index, group ->
                item(key = "trigger_group_$index") {
                    TriggerGroupCard(
                        group = group,
                        groupIndex = index,
                        onRemove = {
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(
                                    triggerGroups = editedMode.settings.triggerGroups.filterIndexed { i, _ -> i != index }
                                )
                            )
                        },
                        onEdit = {
                            when (group) {
                                is ModeTriggerGroup.Single -> {
                                    // 编辑单个触发器，打开相应的选择器
                                    showTriggerSelector = true
                                }
                                is ModeTriggerGroup.Compound -> {
                                    // 编辑组合触发器
                                    editingGroupIndex = index
                                    editingCompoundTriggers = group.triggers
                                    editingCompoundName = group.name
                                    showCompoundTriggerDialog = true
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
            
            // 添加触发器按钮
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = { showTriggerTypeDialog = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+ ${stringResource(R.string.add_trigger_title)}",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
        
        // ... 其他内容
    }

// ============================================================
// 3. 在 ModeDetailScreen 函数底部添加对话框
// ============================================================

    // === 新增：触发器类型选择对话框 ===
    TriggerTypeSelectionDialog(
        show = showTriggerTypeDialog,
        onDismissRequest = { showTriggerTypeDialog = false },
        onSelectSingle = {
            showTriggerTypeDialog = false
            showTriggerSelector = true  // 打开原有的触发器选择对话框
        },
        onSelectCompound = {
            showTriggerTypeDialog = false
            editingGroupIndex = null
            editingCompoundTriggers = emptyList()
            editingCompoundName = null
            showCompoundTriggerDialog = true
        }
    )
    
    // === 新增：组合触发器编辑对话框 ===
    CompoundTriggerEditDialog(
        show = showCompoundTriggerDialog,
        initialTriggers = editingCompoundTriggers,
        initialName = editingCompoundName,
        onDismissRequest = {
            showCompoundTriggerDialog = false
            editingGroupIndex = null
        },
        onConfirm = { triggers, name ->
            val newGroup = ModeTriggerGroup.Compound(
                triggers = triggers,
                name = name
            )
            
            editedMode = if (editingGroupIndex != null) {
                // 更新现有组
                editedMode.copy(
                    settings = editedMode.settings.copy(
                        triggerGroups = editedMode.settings.triggerGroups.mapIndexed { i, group ->
                            if (i == editingGroupIndex) newGroup else group
                        }
                    )
                )
            } else {
                // 添加新组
                editedMode.copy(
                    settings = editedMode.settings.copy(
                        triggerGroups = editedMode.settings.triggerGroups + newGroup
                    )
                )
            }
            
            showCompoundTriggerDialog = false
            editingGroupIndex = null
        },
        onAddTrigger = {
            // 暂时关闭组合触发器对话框，打开触发器选择器
            showCompoundTriggerDialog = false
            showTriggerSelector = true
        }
    )
    
    // 修改原有的 TriggerSelectionDialog
    TriggerSelectionDialog(
        show = showTriggerSelector,
        onDismissRequest = { 
            showTriggerSelector = false
            // 如果是从组合触发器对话框来的，返回组合触发器对话框
            if (editingCompoundTriggers.isNotEmpty() || editingGroupIndex != null) {
                showCompoundTriggerDialog = true
            }
        },
        onSelect = { triggerType ->
            when (triggerType) {
                "time" -> showTimePicker = true
                "app" -> onOpenAppTriggerPicker(editedMode)
                "wifi" -> onOpenWifiTriggerPicker(editedMode)
                "bluetooth" -> onOpenBluetoothTriggerPicker(editedMode)
                "music" -> {
                    val musicTrigger = ModeTrigger.Music
                    addTriggerToCurrentContext(musicTrigger)
                }
                "location" -> onOpenLocationTriggerPicker(editedMode)
                "intent" -> onOpenIntentTriggerPicker(editedMode)
            }
            showTriggerSelector = false
        }
    )
}

// ============================================================
// 4. 辅助函数：添加触发器到当前上下文
// ============================================================

private fun addTriggerToCurrentContext(trigger: ModeTrigger) {
    if (showCompoundTriggerDialog) {
        // 正在编辑组合触发器，添加到组合中
        editingCompoundTriggers = editingCompoundTriggers + trigger
        showCompoundTriggerDialog = true
    } else {
        // 创建单个触发器组
        val newGroup = ModeTriggerGroup.Single(trigger)
        editedMode = editedMode.copy(
            settings = editedMode.settings.copy(
                triggerGroups = editedMode.settings.triggerGroups + newGroup
            )
        )
    }
}

// ============================================================
// 5. 在 strings.xml 中确认这些资源存在
// ============================================================

/*
<string name="triggers_section">触发条件</string>
<string name="add_trigger_title">添加触发器</string>
<string name="select_trigger_type">选择触发器类型</string>
<string name="single_trigger">单个触发器</string>
<string name="single_trigger_desc">添加一个触发条件</string>
<string name="compound_trigger">组合触发器</string>
<string name="compound_trigger_desc">添加多个触发条件，需要同时满足</string>
<string name="compound_trigger_name">组合触发器名称</string>
<string name="add_trigger_to_group">添加触发条件</string>
<string name="trigger_group_and_logic">所有条件必须同时满足</string>
*/

// ============================================================
// 6. 回调处理示例：从触发器选择器返回
// ============================================================

// 当从 AppTriggerPicker 返回时：
fun onAppTriggerSelected(packageNames: Set<String>) {
    val appTrigger = ModeTrigger.App(packageNames)
    addTriggerToCurrentContext(appTrigger)
}

// 当从 WifiTriggerPicker 返回时：
fun onWifiTriggerSelected(ssids: Set<String>) {
    val wifiTrigger = ModeTrigger.Wifi(ssids)
    addTriggerToCurrentContext(wifiTrigger)
}

// 当从 BluetoothTriggerPicker 返回时：
fun onBluetoothTriggerSelected(addresses: Set<String>, matchAnyCarAudio: Boolean) {
    val bluetoothTrigger = ModeTrigger.Bluetooth(addresses, matchAnyCarAudio)
    addTriggerToCurrentContext(bluetoothTrigger)
}

// 当从 LocationTriggerPicker 返回时：
fun onLocationTriggerSelected(location: LocationTarget, transition: LocationTransition) {
    val locationTrigger = ModeTrigger.Location(
        id = UUID.randomUUID().toString(),
        target = location,
        transition = transition
    )
    addTriggerToCurrentContext(locationTrigger)
}

// 当从 IntentTriggerPicker 返回时：
fun onIntentTriggerSelected(activateAction: String?, deactivateAction: String?, packageName: String?) {
    val intentTrigger = ModeTrigger.Intent(activateAction, deactivateAction, packageName)
    addTriggerToCurrentContext(intentTrigger)
}

// 当从 TimePicker 返回时：
fun onTimeSelected(schedule: ModeSchedule) {
    val timeTrigger = ModeTrigger.Time(schedule)
    addTriggerToCurrentContext(timeTrigger)
}
