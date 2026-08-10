package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
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
import com.banana.hypermodes.automation.AutomationCatalog
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.ui.components.TimePickerDialog
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.utils.overScrollVertical
import androidx.compose.foundation.background
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import com.banana.hypermodes.automation.AutomationBlock
import com.banana.hypermodes.automation.BlockParameter
import com.banana.hypermodes.automation.BlockType
import com.banana.hypermodes.automation.toAutomationBlock
import com.banana.hypermodes.automation.AutomationExecutor
import com.banana.hypermodes.automation.isTriggerBlock
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.widget.Toast
import com.banana.hypermodes.automation.SavedAutomation
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.window.WindowListPopup
import top.yukonga.miuix.kmp.window.WindowBottomSheet

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
    onActionSelected: (AutomationAction) -> Unit = {},
    categories: Set<AutomationCatalog.Category>? = null
) {
    var searchQuery by remember { mutableStateOf("") }

    // 目录中的操作（可按分类过滤，触发条件选择时只显示触发分类）
    val allActions = AutomationCatalog.entries
        .filter { categories == null || it.category in categories }
        .map { entry ->
            AutomationAction(
                id = entry.id,
                name = entry.name,
                icon = entry.icon,
                iconColor = entry.iconColor,
                description = entry.description
            )
        }
    
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
    
    WindowBottomSheet(
        show = show,
        onDismissRequest = onDismiss,
        title = "选择操作"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            // 搜索框（最顶部，可搜索全部操作）
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
                        label = "搜索全部操作..."
                    )
                },
                expanded = false,
                onExpandedChange = { }
            ) { }
            
            // 操作列表
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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
    var appPickRequest by remember { mutableStateOf<AppPickRequest?>(null) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current
    val executor = remember { AutomationExecutor(context) }
    val coroutineScope = rememberCoroutineScope()
    var isExecuting by remember { mutableStateOf(false) }

    // System back button must return to the automation list the same way the
    // top-bar back arrow does (auto-save + navigate back).
    BackHandler {
        onSave(blocks)
        onBack()
    }

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
                Icon(
                    imageVector = MiuixIcons.Add,
                    contentDescription = stringResource(R.string.add),
                    tint = MiuixTheme.colorScheme.onPrimary
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
                    isTrigger = block.isTriggerBlock(),
                    onUpdate = { updated ->
                        blocks = blocks.map { if (it.id == updated.id) updated else it }
                    },
                    onPickApps = { param ->
                        appPickRequest = AppPickRequest(
                            blockId = block.id,
                            paramKey = param.key,
                            initial = param.value.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet(),
                            single = block.type is BlockType.OpenApp
                        )
                    },
                    onRemove = {
                        blocks = blocks.filter { it.id != block.id }
                    },
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

    // 应用选择器（全屏覆盖）
    appPickRequest?.let { req ->
        AppPickerScreen(
            title = if (req.single) "选择应用" else "选择应用（可多选）",
            initialSelection = req.initial,
            singleSelection = req.single,
            onBack = { appPickRequest = null },
            onSelectionChanged = { selected ->
                blocks = updateBlockStringParam(
                    blocks,
                    blockId = req.blockId,
                    key = req.paramKey,
                    value = selected.sorted().joinToString(",")
                )
                appPickRequest = null
            }
        )
    }
}

/** 等待应用选择器返回的参数目标。 */
private data class AppPickRequest(
    val blockId: String,
    val paramKey: String,
    val initial: Set<String>,
    val single: Boolean
)

/** 递归更新块树中指定 StringParam 的值（支持嵌套 IF/REPEAT）。 */
private fun updateBlockStringParam(
    blocks: List<AutomationBlock>,
    blockId: String,
    key: String,
    value: String
): List<AutomationBlock> = blocks.map { block ->
    if (block.id == blockId) {
        block.copy(
            parameters = block.parameters.map { param ->
                if (param.key == key && param is BlockParameter.StringParam) {
                    param.copy(value = value)
                } else {
                    param
                }
            }
        )
    } else {
        block.copy(
            children = updateBlockStringParam(block.children, blockId, key, value),
            elseChildren = updateBlockStringParam(block.elseChildren, blockId, key, value)
        )
    }
}

@Composable
private fun BlockCard(
    block: AutomationBlock,
    isTrigger: Boolean = false,
    onUpdate: (AutomationBlock) -> Unit,
    onPickApps: (BlockParameter.StringParam) -> Unit,
    onRemove: () -> Unit,
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
                // 最右侧 × 删除按钮
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                        .clickable(onClick = onRemove),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "×",
                        fontSize = 20.sp,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        style = MiuixTheme.textStyles.body1
                    )
                }
            }

            if (block.type is BlockType.TriggerTime) {
                // 时间触发：图一样式的定制编辑器（时间胶囊 + 重复选项）
                TimeTriggerEditor(
                    block = block,
                    onUpdate = onUpdate
                )
            } else {
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
                        },
                        onPickApps = { if (it is BlockParameter.StringParam) onPickApps(it) }
                    )
                }
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
                            onPickApps = onPickApps,
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
                            onPickApps = onPickApps,
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
    onChange: (BlockParameter) -> Unit,
    onPickApps: (BlockParameter) -> Unit = {}
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
        is BlockParameter.StringParam -> {
            Column {
                Text(
                    text = param.label,
                    style = MiuixTheme.textStyles.body2
                )
                Spacer(modifier = Modifier.height(6.dp))
                if (param.key == "packages") {
                    // 应用选择：点击进入全屏选择器，避免手填包名
                    val selectedCount = param.value.split(",")
                        .map { it.trim() }
                        .count { it.isNotEmpty() }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                            .clickable { onPickApps(param) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCount > 0) "已选择 $selectedCount 个应用" else "选择应用...",
                            style = MiuixTheme.textStyles.body1
                        )
                        Text(
                            text = "›",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                } else if (param.key == "modeId") {
                    // 模式选择：点击后弹出模式列表，避免手填 modeId
                    val context = LocalContext.current
                    var showModePicker by remember { mutableStateOf(false) }
                    var modes by remember { mutableStateOf<List<Mode>?>(null) }
                    LaunchedEffect(Unit) {
                        modes = ModeStore.load(context) { DefaultModes.get() }
                    }
                    val selectedMode = modes?.find { it.id == param.value }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f))
                            .clickable { showModePicker = true }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedMode?.let { "${it.icon} ${it.name}" }
                                ?: if (param.value.isBlank()) "选择模式..." else param.value,
                            style = MiuixTheme.textStyles.body1
                        )
                        Text(
                            text = "›",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                    if (showModePicker) {
                        OverlayDialog(
                            show = showModePicker,
                            onDismissRequest = { showModePicker = false }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(5.dp)
                            ) {
                                Text(
                                    text = "选择模式",
                                    style = MiuixTheme.textStyles.title3,
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )
                                (modes ?: emptyList()).forEach { mode ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                onChange(param.copy(value = mode.id))
                                                showModePicker = false
                                            }
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = mode.icon,
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(end = 12.dp)
                                        )
                                        Text(
                                            text = mode.name,
                                            style = MiuixTheme.textStyles.body1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (mode.id == param.value) {
                                            Text(
                                                text = "✓",
                                                color = MiuixTheme.colorScheme.primary,
                                                style = MiuixTheme.textStyles.body1
                                            )
                                        }
                                    }
                                }
                                if (modes.isNullOrEmpty()) {
                                    Text(
                                        text = "暂无模式，请先在「模式」页创建",
                                        style = MiuixTheme.textStyles.body2,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    TextField(
                        value = param.value,
                        onValueChange = { onChange(param.copy(value = it)) },
                        modifier = Modifier.fillMaxWidth(),
                        useLabelAsPlaceholder = false,
                        singleLine = true
                    )
                }
            }
        }
    }
}

/**
 * 时间触发的定制编辑界面（仿 iOS 快捷指令样式）：
 * - 顶部：橙色时钟图标 + 开始/结束时间胶囊（点击弹时间选择器）+ 删除按钮
 * - 下方：重复选项（每天 / 工作日 / 周末）
 */
@Composable
private fun TimeTriggerEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit
) {
    val start = block.parameters.find { it.key == "start" } as? BlockParameter.StringParam
    val repeat = block.parameters.find { it.key == "repeat" } as? BlockParameter.ChoiceParam

    var showTimePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) } // 展开后显示重复等选项

    fun updateParam(updated: BlockParameter) {
        onUpdate(
            block.copy(
                parameters = block.parameters.map {
                    if (it.key == updated.key) updated else it
                }
            )
        )
    }

    fun parseTime(value: String): Pair<Int, Int> {
        val parts = value.split(":")
        return (parts.getOrNull(0)?.toIntOrNull() ?: 0) to
                (parts.getOrNull(1)?.toIntOrNull() ?: 0)
    }

    fun timeLabel(value: String): String {
        val (h, m) = parseTime(value)
        return if (value.isBlank()) "--:--" else "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
    }

    @Composable
    fun TimeChip(
        label: String,
        value: String,
        onPick: () -> Unit
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFDBEBFC))
                .clickable(onClick = onPick)
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$label  ${timeLabel(value)}",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "▾",
                color = Color(0xFF0A84FF),
                style = MiuixTheme.textStyles.body1
            )
        }
    }

    Column {
        // 第一行：时钟图标 + 开始时间胶囊 + 右侧展开按钮
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFFFF9500)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🕐",
                    fontSize = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            TimeChip(
                label = "开始",
                value = start?.value ?: "",
                onPick = { showTimePicker = true }
            )
            Spacer(modifier = Modifier.weight(1f))
            // 展开/收起按钮
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                    .clickable { expanded = !expanded },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (expanded) "▴" else "▾",
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    style = MiuixTheme.textStyles.body1
                )
            }
        }

        // 展开后才显示重复选项
        if (expanded) {
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "重复",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(end = 12.dp)
                )
                repeat?.options?.forEach { option ->
                    val selected = option == repeat.value
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (selected) MiuixTheme.colorScheme.primary
                                else MiuixTheme.colorScheme.secondaryContainer
                            )
                            .clickable { updateParam(repeat.copy(value = option)) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .padding(end = 4.dp),
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

    // 时间选择器弹窗
    if (showTimePicker) {
        val (initialHour, initialMinute) = parseTime(start?.value ?: "")
        TimePickerDialog(
            title = "设置开始时间",
            initialHour = initialHour,
            initialMinute = initialMinute,
            show = true,
            onDismissRequest = { showTimePicker = false },
            onConfirm = { h, m ->
                val value = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                val param = block.parameters.find { it.key == "start" } as? BlockParameter.StringParam
                if (param != null) updateParam(param.copy(value = value))
                showTimePicker = false
            }
        )
    }
}








