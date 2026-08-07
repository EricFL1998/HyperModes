package com.banana.hypermodes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.utils.overScrollVertical
import androidx.compose.foundation.background
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import com.banana.hypermodes.automation.AutomationBlock
import com.banana.hypermodes.automation.BlockParameter
import com.banana.hypermodes.automation.toAutomationBlock
import com.banana.hypermodes.automation.AutomationExecutor
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
import com.banana.hypermodes.automation.SavedAutomation
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.window.WindowListPopup

/**
 * 自动化操作项数据类
 */
data class AutomationAction(
    val id: String,
    val name: String,
    val icon: String,
    val iconColor: Color,
    val description: String = ""
)

/**
 * 自动化操作选择对话框（底部弹出）
 */
@Composable
fun AutomationActionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onActionSelected: (AutomationAction) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    
    // 常用系统控制
    val systemActions = listOf(
        AutomationAction(
            id = "toggle_wifi",
            name = "WiFi 开关",
            icon = "📶",
            iconColor = Color(0xFF007AFF),
            description = "切换 WiFi 开关状态"
        ),
        AutomationAction(
            id = "toggle_bluetooth",
            name = "蓝牙开关",
            icon = "🔵",
            iconColor = Color(0xFF007AFF),
            description = "切换蓝牙开关状态"
        ),
        AutomationAction(
            id = "set_dnd",
            name = "勿扰模式",
            icon = "🔕",
            iconColor = Color(0xFFFF3B30),
            description = "开启或关闭勿扰模式"
        ),
        AutomationAction(
            id = "adjust_volume",
            name = "调节音量",
            icon = "🔊",
            iconColor = Color(0xFFFF9500),
            description = "调整设备音量"
        ),
        AutomationAction(
            id = "adjust_brightness",
            name = "调节亮度",
            icon = "💡",
            iconColor = Color(0xFFFFCC00),
            description = "调整屏幕亮度"
        ),
        AutomationAction(
            id = "enable_mode",
            name = "启用模式",
            icon = "🌙",
            iconColor = Color(0xFF5E5CE6),
            description = "启用指定的 HyperMode"
        ),
        AutomationAction(
            id = "disable_mode",
            name = "关闭模式",
            icon = "☀️",
            iconColor = Color(0xFFFF9500),
            description = "关闭指定的 HyperMode"
        ),
        AutomationAction(
            id = "open_app",
            name = "打开 App",
            icon = "📱",
            iconColor = Color(0xFF5E5CE6),
            description = "启动指定应用程序"
        )
    )
    
    // 控制流
    val controlFlowActions = listOf(
        AutomationAction(
            id = "if_condition",
            name = "If 条件判断",
            icon = "🔀",
            iconColor = Color(0xFF34C759),
            description = "根据条件执行不同操作"
        ),
        AutomationAction(
            id = "repeat_count",
            name = "重复 N 次",
            icon = "🔁",
            iconColor = Color(0xFF5856D6),
            description = "重复执行指定次数"
        ),
        AutomationAction(
            id = "wait",
            name = "等待",
            icon = "⏱️",
            iconColor = Color(0xFFFF9500),
            description = "暂停执行一段时间"
        ),
        AutomationAction(
            id = "comment",
            name = "注释",
            icon = "💬",
            iconColor = Color(0xFF8E8E93),
            description = "添加说明文字"
        )
    )
    
    // 逻辑运算
    val logicActions = listOf(
        AutomationAction(
            id = "and_condition",
            name = "AND 与运算",
            icon = "➕",
            iconColor = Color(0xFF007AFF),
            description = "所有条件都满足"
        ),
        AutomationAction(
            id = "or_condition",
            name = "OR 或运算",
            icon = "〰️",
            iconColor = Color(0xFF007AFF),
            description = "任一条件满足"
        )
    )
    
    // 条件判断
    val conditionActions = listOf(
        AutomationAction(
            id = "check_wifi",
            name = "检查 WiFi 状态",
            icon = "📶",
            iconColor = Color(0xFF30B0C7),
            description = "判断 WiFi 是否开启"
        ),
        AutomationAction(
            id = "check_bluetooth",
            name = "检查蓝牙状态",
            icon = "🔵",
            iconColor = Color(0xFF30B0C7),
            description = "判断蓝牙是否开启"
        ),
        AutomationAction(
            id = "check_battery",
            name = "检查电量",
            icon = "🔋",
            iconColor = Color(0xFF30B0C7),
            description = "判断电量是否满足条件"
        ),
        AutomationAction(
            id = "check_time",
            name = "检查时间范围",
            icon = "🕐",
            iconColor = Color(0xFF30B0C7),
            description = "判断当前时间是否在范围内"
        )
    )
    
    val allActions = systemActions + controlFlowActions + logicActions + conditionActions
    
    // 根据搜索过滤
    val filteredActions = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            allActions
        } else {
            allActions.filter { action ->
                action.name.contains(searchQuery, ignoreCase = true) ||
                action.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }
    
    OverlayDialog(
        show = show,
        onDismissRequest = onDismiss,
        title = "选择操作"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            // 搜索框
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                inputField = {
                    InputField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = { },
                        expanded = false,
                        onExpandedChange = { },
                        label = "搜索常用控制..."
                    )
                },
                expanded = false,
                onExpandedChange = { }
            ) { }
            
            // 常用控制项列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                items(filteredActions) { action ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onActionSelected(action)
                                onDismiss()
                            }
                            .padding(horizontal = 24.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 图标
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(action.iconColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = action.icon,
                                style = MiuixTheme.textStyles.headline1.copy(fontSize = 24.sp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // 文本信息
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = action.name,
                                style = MiuixTheme.textStyles.body1
                            )
                            if (action.description.isNotEmpty()) {
                                Text(
                                    text = action.description,
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * 全屏自动化编辑界面。初始 block 列表由外部传入，
 * 通常是从"选择操作"弹窗选中的第一个控制项。
 */
@Composable
fun AutomationEditorScreen(
    automation: SavedAutomation,
    onBack: () -> Unit,
    onSave: (List<AutomationBlock>) -> Unit = {},
    onRename: (SavedAutomation) -> Unit = {},
    onDelete: (SavedAutomation) -> Unit = {}
) {
    var blocks by remember { mutableStateOf(automation.blocks) }
    var showAddActionDialog by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current
    val executor = remember { AutomationExecutor(context) }
    val coroutineScope = rememberCoroutineScope()
    var isExecuting by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "自动化",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = {
                        onSave(blocks)
                        onBack()
                    }) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (!isExecuting && blocks.isNotEmpty()) {
                                isExecuting = true
                                coroutineScope.launch {
                                    try {
                                        val result = executor.execute(blocks)
                                        Toast.makeText(
                                            context,
                                            if (result.success) "✅ ${result.message}" 
                                            else "❌ ${result.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            "执行错误: ${e.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } finally {
                                        isExecuting = false
                                    }
                                }
                            }
                        },
                        enabled = !isExecuting && blocks.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Basic.ArrowRight,
                            contentDescription = "测试"
                        )
                    }
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = null
                        )
                    }
                    // miuix dropdown anchored to the ⋮ button
                    WindowListPopup(
                        show = showOverflowMenu,
                        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                        alignment = PopupPositionProvider.Align.TopEnd,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        ListPopupColumn {
                            DropdownImpl(
                                text = stringResource(R.string.rename),
                                optionSize = 2,
                                isSelected = false,
                                index = 0,
                                onSelectedIndexChange = {
                                    showOverflowMenu = false
                                    onRename(automation)
                                }
                            )
                            DropdownImpl(
                                text = stringResource(R.string.delete),
                                optionSize = 2,
                                isSelected = false,
                                index = 1,
                                onSelectedIndexChange = {
                                    showOverflowMenu = false
                                    showDeleteConfirm = true
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddActionDialog = true }
            ) {
                Text(
                    text = "+",
                    fontSize = 32.sp,
                    color = MiuixTheme.colorScheme.onPrimary
                )
            }
        }
    ) { padding ->
        
        // 删除确认对话框
        OverlayDialog(
            show = showDeleteConfirm,
            onDismissRequest = { showDeleteConfirm = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp, top = 5.dp, bottom = 5.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.delete_automation),
                    style = MiuixTheme.textStyles.title3,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = "确定要删除「${automation.name}」吗？",
                    style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.Medium),
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        text = stringResource(R.string.cancel),
                        onClick = { showDeleteConfirm = false },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        text = stringResource(R.string.delete),
                        onClick = {
                            showDeleteConfirm = false
                            onDelete(automation)
                            onBack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = 88.dp
            )
        ) {
            item {
                Text(
                    text = if (blocks.isEmpty()) {
                        "点击下方 + 添加第一个操作"
                    } else {
                        "已添加 ${blocks.size} 个操作"
                    },
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp)
                )
            }

            items(blocks.size) { index ->
                val block = blocks[index]
                BlockCard(
                    block = block,
                    onUpdate = { updated ->
                        blocks = blocks.map { if (it.id == updated.id) updated else it }
                    },
                    onRemove = {
                        blocks = blocks.filter { it.id != block.id }
                    },
                    onMoveUp = if (index > 0) {
                        {
                            blocks = blocks.toMutableList().apply {
                                val temp = this[index]
                                this[index] = this[index - 1]
                                this[index - 1] = temp
                            }
                        }
                    } else null,
                    onMoveDown = if (index < blocks.size - 1) {
                        {
                            blocks = blocks.toMutableList().apply {
                                val temp = this[index]
                                this[index] = this[index + 1]
                                this[index + 1] = temp
                            }
                        }
                    } else null,
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }
        }
    }

    AutomationActionDialog(
        show = showAddActionDialog,
        onDismiss = { showAddActionDialog = false },
        onActionSelected = { action ->
            blocks = blocks + action.toAutomationBlock()
            showAddActionDialog = false
        }
    )
}

@Composable
private fun BlockCard(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    onRemove: () -> Unit,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    nestLevel: Int = 0
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        cornerRadius = 24.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(block.iconColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = block.icon,
                        style = MiuixTheme.textStyles.headline1.copy(fontSize = 24.sp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = block.label,
                        style = MiuixTheme.textStyles.body1
                    )
                }
                if (nestLevel == 0 && (onMoveUp != null || onMoveDown != null)) {
                    if (onMoveUp != null) {
                        TextButton(
                            text = "↑",
                            onClick = onMoveUp,
                        modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                    if (onMoveDown != null) {
                        TextButton(
                            text = "↓",
                            onClick = onMoveDown,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }
                TextButton(
                    text = "删除",
                    onClick = onRemove,
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }

            block.parameters.forEach { param ->
                Spacer(modifier = Modifier.height(12.dp))
                ParameterEditor(
                    param = param,
                    onChange = { newParam ->
                        onUpdate(
                            block.copy(
                                parameters = block.parameters.map {
                                    if (it.key == newParam.key) newParam else it
                                }
                            )
                        )
                    }
                )
            }
        
            
            // 显示嵌套的子块（用于 IF、REPEAT 等）
            if (block.children.isNotEmpty() && nestLevel < 3) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "执行操作",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    block.children.forEach { childBlock ->
                        BlockCard(
                            block = childBlock,
                            onUpdate = { updated ->
                                onUpdate(
                                    block.copy(
                                        children = block.children.map {
                                            if (it.id == updated.id) updated else it
                                        }
                                    )
                                )
                            },
                            onRemove = {
                                onUpdate(
                                    block.copy(
                                        children = block.children.filter { it.id != childBlock.id }
                                    )
                                )
                            },
                            modifier = Modifier.padding(bottom = 8.dp),
                            nestLevel = nestLevel + 1
                        )
                    }
                }
            }
            
            // 显示 ELSE 分支（仅用于 IF）
            if (block.elseChildren.isNotEmpty() && nestLevel < 3) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "否则执行",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    block.elseChildren.forEach { elseBlock ->
                        BlockCard(
                            block = elseBlock,
                            onUpdate = { updated ->
                                onUpdate(
                                    block.copy(
                                        elseChildren = block.elseChildren.map {
                                            if (it.id == updated.id) updated else it
                                        }
                                    )
                                )
                            },
                            onRemove = {
                                onUpdate(
                                    block.copy(
                                        elseChildren = block.elseChildren.filter { it.id != elseBlock.id }
                                    )
                                )
                            },
                            modifier = Modifier.padding(bottom = 8.dp),
                            nestLevel = nestLevel + 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParameterEditor(
    param: BlockParameter,
    onChange: (BlockParameter) -> Unit
) {
    when (param) {
        is BlockParameter.BooleanParam -> {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = param.label,
                    style = MiuixTheme.textStyles.body2
                )
                Switch(
                    checked = param.value,
                    onCheckedChange = { onChange(param.copy(value = it)) }
                )
            }
        }
        is BlockParameter.IntParam -> {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = param.label,
                        style = MiuixTheme.textStyles.body2
                    )
                    Text(
                        text = "${param.value}${if (param.max == 100) "%" else ""}",
                        style = MiuixTheme.textStyles.body2
                    )
                }
                Slider(
                    value = param.value.toFloat(),
                    onValueChange = { onChange(param.copy(value = it.toInt())) },
                    valueRange = param.min.toFloat()..param.max.toFloat(),
                    steps = param.max - param.min
                )
            }
        }
        is BlockParameter.ChoiceParam -> {
            Column {
                Text(
                    text = param.label,
                    style = MiuixTheme.textStyles.body2
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    param.options.forEach { option ->
                        val selected = option == param.value
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (selected) MiuixTheme.colorScheme.primary
                                    else MiuixTheme.colorScheme.secondaryContainer
                                )
                                .clickable { onChange(param.copy(value = option)) }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = option,
                                style = MiuixTheme.textStyles.body2,
                                color = if (selected) MiuixTheme.colorScheme.onPrimary
                                    else MiuixTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}








