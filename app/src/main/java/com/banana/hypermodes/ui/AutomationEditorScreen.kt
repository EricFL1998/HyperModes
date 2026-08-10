package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
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
 * 自动化操作选择对话框（底部弹出，图一样式）。
 * iOS 分组卡片列表：无搜索框，按分类以粗体分组标题引领，
 * 每行为独立大圆角卡片——左彩色图标 + 双行文字 + 右侧圆形 i 信息按钮。
 */
@Composable
fun AutomationActionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onActionSelected: (AutomationAction) -> Unit = {},
    categories: Set<AutomationCatalog.Category>? = null
) {
    var searchQuery by remember { mutableStateOf("") }

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

            // 分组操作列表（按分类，以粗体标题分组）
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
            val filteredActions = remember(searchQuery, allActions) {
                if (searchQuery.isBlank()) {
                    allActions
                } else {
                    allActions.filter { action ->
                        action.name.contains(searchQuery, ignoreCase = true) ||
                                action.description.contains(searchQuery, ignoreCase = true)
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                if (searchQuery.isBlank()) {
                    AutomationCatalog.grouped()
                        .filter { (category, _) -> categories == null || category in categories }
                        .forEach { (category, entries) ->
                            item(key = "header-${category.name}") {
                                Text(
                                    text = category.label,
                                    style = MiuixTheme.textStyles.headline1.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(
                                        start = 24.dp,
                                        top = 20.dp,
                                        bottom = 12.dp
                                    )
                                )
                            }
                            items(entries.map { entry ->
                                AutomationAction(
                                    id = entry.id,
                                    name = entry.name,
                                    icon = entry.icon,
                                    iconColor = entry.iconColor,
                                    description = entry.description
                                )
                            }, key = { it.id }) { action ->
                                ActionOptionCard(
                                    action = action,
                                    onClick = {
                                        onActionSelected(action)
                                        onDismiss()
                                    }
                                )
                            }
                        }
                } else {
                    items(filteredActions, key = { it.id }) { action ->
                        ActionOptionCard(
                            action = action,
                            onClick = {
                                onActionSelected(action)
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

/** 图一样式的操作选项卡片：左图标 + 双行文字 + 右侧圆形 i 按钮。 */
@Composable
private fun ActionOptionCard(
    action: AutomationAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 10.dp),
        insideMargin = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        cornerRadius = 24.dp,
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧彩色图标（无底色容器，直接放置）
            Text(
                text = action.icon,
                fontSize = 30.sp,
                modifier = Modifier.padding(end = 14.dp)
            )

            // 双行文字
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = action.name,
                    style = MiuixTheme.textStyles.body1.copy(fontWeight = FontWeight.SemiBold)
                )
                if (action.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "“${action.description}”",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
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
    val dragController = remember { DragController() }
    var appPickRequest by remember { mutableStateOf<AppPickRequest?>(null) }
    var wifiPickRequest by remember { mutableStateOf<WifiPickRequest?>(null) }
    var bluetoothPickRequest by remember { mutableStateOf<BluetoothPickRequest?>(null) }
    var showOverflowMenu by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current
    val executor = remember { AutomationExecutor(context) }
    val coroutineScope = rememberCoroutineScope()
    var isExecuting by remember { mutableStateOf(false) }

    // 拖拽落点：把被拖块移入目标触发器容器的作用域
    LaunchedEffect(Unit) {
        dragController.onDrop = { draggedId, targetId ->
            if (targetId != null) {
                blocks = moveBlockIntoParent(blocks, draggedId, targetId)
            } else {
                // 拖出容器：若块在某个容器 children 中，则提取并放回顶层末尾
                val (withoutDragged, dragged) = extractBlockFromTree(blocks, draggedId)
                if (dragged != null && !blocks.any { it.id == draggedId }) {
                    blocks = withoutDragged + dragged
                }
            }
        }
    }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { coordinates ->
                    dragController.rootWindowTopLeft = coordinates.boundsInWindow().topLeft
                }
        ) {
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
                    dragController = dragController,
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
                    onPickWifi = { param ->
                        wifiPickRequest = WifiPickRequest(
                            blockId = block.id,
                            paramKey = param.key
                        )
                    },
                    onPickBluetooth = { param ->
                        bluetoothPickRequest = BluetoothPickRequest(
                            blockId = block.id,
                            paramKey = param.key
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

        // 浮动预览：跟随手指的简化块卡片（源块不平移，避免抽搐）
        dragController.draggedBlock?.let { dragged ->
            val previewOffset = dragController.draggedWindowTopLeft -
                    dragController.rootWindowTopLeft +
                    dragController.dragOffset
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = previewOffset.x
                        translationY = previewOffset.y
                        shadowElevation = 12f
                    }
                    .padding(horizontal = 16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    insideMargin = PaddingValues(horizontal = 18.dp, vertical = 14.dp),
                    cornerRadius = 24.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = dragged.icon,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(
                            text = dragged.label,
                            style = MiuixTheme.textStyles.body1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
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

    // WiFi 选择器（全屏覆盖）
    wifiPickRequest?.let { req ->
        WifiPickerScreen(
            onBack = { wifiPickRequest = null },
            onSelect = { ssid ->
                blocks = updateBlockStringParam(
                    blocks,
                    blockId = req.blockId,
                    key = req.paramKey,
                    value = ssid
                )
                wifiPickRequest = null
            }
        )
    }

    // 蓝牙设备选择器（全屏覆盖）
    bluetoothPickRequest?.let { req ->
        BluetoothPickerScreen(
            onBack = { bluetoothPickRequest = null },
            onSelect = { device ->
                blocks = updateBlockStringParam(
                    blocks,
                    blockId = req.blockId,
                    key = req.paramKey,
                    value = "${device.name}|${device.address}"
                )
                bluetoothPickRequest = null
            }
        )
    }
}

/**
 * WiFi 触发的一行句子式编辑器：
 * 当 [📶] [WiFi 名称胶囊] [条件胶囊] 时
 * - WiFi 名称胶囊点击 → 打开 WiFi 选择器
 * - 条件胶囊点击 → 弹出条件菜单（已加入 / 已断开连接 / 已加入或断开连接）
 */
@Composable
private fun WifiTriggerEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    onPickWifi: (BlockParameter.StringParam) -> Unit
) {
    val ssidParam = block.parameters.find { it.key == "ssid" } as? BlockParameter.StringParam
    val connectParam = block.parameters.find { it.key == "connect" } as? BlockParameter.ChoiceParam

    var showConnectMenu by remember { mutableStateOf(false) }

    fun updateParam(updated: BlockParameter) {
        onUpdate(
            block.copy(
                parameters = block.parameters.map {
                    if (it.key == updated.key) updated else it
                }
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "当",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 6.dp)
        )

        // WiFi 图标
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0A84FF).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📶",
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        // WiFi 名称胶囊（点击弹选择器）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { ssidParam?.let(onPickWifi) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (ssidParam?.value.isNullOrBlank()) "全部 WiFi" else ssidParam!!.value,
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        // 条件胶囊（点击弹菜单）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { showConnectMenu = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = connectParam?.value ?: "已加入",
                    color = Color(0xFF0A84FF),
                    fontWeight = FontWeight.Medium,
                    style = MiuixTheme.textStyles.body1
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "▾",
                    color = Color(0xFF0A84FF),
                    style = MiuixTheme.textStyles.body1,
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "时",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface
        )
    }

    // 条件菜单
    if (showConnectMenu) {
        OverlayDialog(
            show = showConnectMenu,
            onDismissRequest = { showConnectMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = "触发条件",
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                (connectParam?.options ?: emptyList()).forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (connectParam != null) updateParam(connectParam.copy(value = option))
                                showConnectMenu = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                        if (option == connectParam?.value) {
                            Text(
                                text = "✓",
                                color = MiuixTheme.colorScheme.primary,
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 蓝牙触发的一行句子式编辑器：
 * 当 [🔵] [设备胶囊] [条件胶囊] 时
 * - 设备胶囊点击 → 打开蓝牙设备选择器
 * - 条件胶囊点击 → 弹出条件菜单（已连接 / 已断开连接 / 已连接或断开连接）
 */
@Composable
private fun BluetoothTriggerEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    onPickBluetooth: (BlockParameter.StringParam) -> Unit
) {
    val deviceParam = block.parameters.find { it.key == "device" } as? BlockParameter.StringParam
    val connectParam = block.parameters.find { it.key == "connect" } as? BlockParameter.ChoiceParam

    var showConnectMenu by remember { mutableStateOf(false) }

    fun updateParam(updated: BlockParameter) {
        onUpdate(
            block.copy(
                parameters = block.parameters.map {
                    if (it.key == updated.key) updated else it
                }
            )
        )
    }

    // device 参数存 "名称|地址"，显示名称部分
    val deviceName = deviceParam?.value?.substringBefore("|")?.takeIf { it.isNotBlank() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "当",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 6.dp)
        )

        // 蓝牙图标
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF0A84FF).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🔵",
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        // 设备胶囊（点击弹选择器）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { deviceParam?.let(onPickBluetooth) }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = deviceName ?: "全部蓝牙设备",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        // 条件胶囊（点击弹菜单）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { showConnectMenu = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = connectParam?.value ?: "已连接",
                    color = Color(0xFF0A84FF),
                    fontWeight = FontWeight.Medium,
                    style = MiuixTheme.textStyles.body1
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "▾",
                    color = Color(0xFF0A84FF),
                    style = MiuixTheme.textStyles.body1,
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))

        Text(
            text = "时",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface
        )
    }

    // 条件菜单
    if (showConnectMenu) {
        OverlayDialog(
            show = showConnectMenu,
            onDismissRequest = { showConnectMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = "触发条件",
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                (connectParam?.options ?: emptyList()).forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (connectParam != null) updateParam(connectParam.copy(value = option))
                                showConnectMenu = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                        if (option == connectParam?.value) {
                            Text(
                                text = "✓",
                                color = MiuixTheme.colorScheme.primary,
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 状态类触发/检查的一行句子式编辑器：
 * 当 [图标] [状态胶囊] 时
 * 状态胶囊点击弹出菜单切换（开启/关闭 或 开始/停止 等），不再显示开关。
 */
@Composable
private fun StateChipEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    showWhenSuffix: Boolean = true
) {
    val stateParam = block.parameters.find { it.key == "state" } as? BlockParameter.ChoiceParam
    var showStateMenu by remember { mutableStateOf(false) }

    fun updateParam(updated: BlockParameter) {
        onUpdate(
            block.copy(
                parameters = block.parameters.map {
                    if (it.key == updated.key) updated else it
                }
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showWhenSuffix) {
            // 状态图标
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(block.iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = block.icon,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        } else {
            // 操作类：显示名称 + 状态胶囊（如 "WiFi [开启]"）
            Text(
                text = block.label,
                style = MiuixTheme.textStyles.body1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // 状态胶囊（点击弹菜单切换）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { showStateMenu = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stateParam?.value ?: "开启",
                    color = Color(0xFF0A84FF),
                    fontWeight = FontWeight.Medium,
                    style = MiuixTheme.textStyles.body1
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "▾",
                    color = Color(0xFF0A84FF),
                    style = MiuixTheme.textStyles.body1,
                    fontSize = 10.sp
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))

        if (showWhenSuffix) {
            Text(
                text = "时",
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }

    // 状态菜单
    if (showStateMenu) {
        OverlayDialog(
            show = showStateMenu,
            onDismissRequest = { showStateMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = "状态",
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                (stateParam?.options ?: emptyList()).forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (stateParam != null) updateParam(stateParam.copy(value = option))
                                showStateMenu = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                        if (option == stateParam?.value) {
                            Text(
                                text = "✓",
                                color = MiuixTheme.colorScheme.primary,
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                }
            }
        }
    }
}


/** 等待应用选择器返回的参数目标。 */
private data class AppPickRequest(
    val blockId: String,
    val paramKey: String,
    val initial: Set<String>,
    val single: Boolean
)

/** 等待 WiFi 选择器返回的参数目标。 */
private data class WifiPickRequest(
    val blockId: String,
    val paramKey: String
)

/** 等待蓝牙设备选择器返回的参数目标。 */
private data class BluetoothPickRequest(
    val blockId: String,
    val paramKey: String
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

/** 递归把新块加入指定父块的 children（支持嵌套触发器/IF/REPEAT）。 */
private fun addBlockToChildren(
    blocks: List<AutomationBlock>,
    parentId: String,
    newBlock: AutomationBlock
): List<AutomationBlock> = blocks.map { block ->
    if (block.id == parentId) {
        block.copy(children = block.children + newBlock)
    } else {
        block.copy(
            children = addBlockToChildren(block.children, parentId, newBlock),
            elseChildren = addBlockToChildren(block.elseChildren, parentId, newBlock)
        )
    }
}

/** 递归从树中提取指定块（返回移除后的树与被提取的块）。 */
private fun extractBlockFromTree(
    blocks: List<AutomationBlock>,
    blockId: String
): Pair<List<AutomationBlock>, AutomationBlock?> {
    var extracted: AutomationBlock? = null
    val result = blocks.mapNotNull { block ->
        if (block.id == blockId) {
            extracted = block
            null
        } else {
            val (newChildren, cExtracted) = extractBlockFromTree(block.children, blockId)
            val (newElse, eExtracted) = extractBlockFromTree(block.elseChildren, blockId)
            if (cExtracted != null) extracted = cExtracted
            if (eExtracted != null) extracted = eExtracted
            block.copy(children = newChildren, elseChildren = newElse)
        }
    }
    return result to extracted
}

/** 拖拽结束：把被拖块移入目标触发器容器的 children。 */
private fun moveBlockIntoParent(
    blocks: List<AutomationBlock>,
    draggedId: String,
    targetParentId: String
): List<AutomationBlock> {
    val (withoutDragged, dragged) = extractBlockFromTree(blocks, draggedId)
        ?: return blocks
    if (dragged == null) return blocks
    return addBlockToChildren(withoutDragged, targetParentId, dragged)
}

/** 拖拽控制器：跨嵌套层级共享拖拽状态与落点判定。 */
private class DragController {
    var draggedBlockId by mutableStateOf<String?>(null)
    var draggedBlock by mutableStateOf<AutomationBlock?>(null)
    var dragPosition by mutableStateOf(androidx.compose.ui.geometry.Offset.Zero)
    /** 相对手指起点的偏移（用于浮动预览视觉跟随）。 */
    var dragOffset by mutableStateOf(androidx.compose.ui.geometry.Offset.Zero)
    var dropTargetId by mutableStateOf<String?>(null)
    var onDrop: ((draggedId: String, targetId: String?) -> Unit)? = null

    /** 触发器容器的屏幕边界（blockId -> 窗口 Rect），用于落点检测。 */
    val containerBounds = mutableMapOf<String, androidx.compose.ui.geometry.Rect>()

    /** 所有块的窗口左上角（blockId -> 窗口 Offset），用于计算被拖块的手指窗口坐标。 */
    val blockWindowTopLefts = mutableMapOf<String, androidx.compose.ui.geometry.Offset>()

    /** 被拖块的窗口坐标（左上角），拖拽手指位置 = 该点 + 局部偏移。 */
    var draggedWindowTopLeft by mutableStateOf(androidx.compose.ui.geometry.Offset.Zero)

    /** 手指按下时的局部起点，用于计算相对偏移（避免所有块一起平移）。 */
    private var startLocal = androidx.compose.ui.geometry.Offset.Zero

    /** 根容器（编辑器内容）的窗口坐标，用于浮动预览定位。 */
    var rootWindowTopLeft by mutableStateOf(androidx.compose.ui.geometry.Offset.Zero)

    fun start(block: AutomationBlock, startLocalPosition: androidx.compose.ui.geometry.Offset = androidx.compose.ui.geometry.Offset.Zero) {
        draggedBlockId = block.id
        draggedBlock = block
        startLocal = startLocalPosition
        dragPosition = androidx.compose.ui.geometry.Offset.Zero
        dragOffset = androidx.compose.ui.geometry.Offset.Zero
        dropTargetId = null
    }

    /** 拖动中更新手指窗口位置并计算落点目标。 */
    fun move(localPosition: androidx.compose.ui.geometry.Offset) {
        dragPosition = localPosition
        // 相对起始点的位移（浮动预览用）
        dragOffset = localPosition - startLocal
        val windowPos = draggedWindowTopLeft + localPosition
        dropTargetId = containerBounds.entries
            .firstOrNull { it.value.contains(windowPos) }
            ?.key
    }

    fun end() {
        val draggedId = draggedBlockId
        val targetId = dropTargetId
        draggedBlockId = null
        draggedBlock = null
        dragPosition = androidx.compose.ui.geometry.Offset.Zero
        dragOffset = androidx.compose.ui.geometry.Offset.Zero
        dropTargetId = null
        if (draggedId != null) {
            onDrop?.invoke(draggedId, targetId)
        }
    }

    fun cancel() {
        draggedBlockId = null
        draggedBlock = null
        dragPosition = androidx.compose.ui.geometry.Offset.Zero
        dragOffset = androidx.compose.ui.geometry.Offset.Zero
        dropTargetId = null
    }
}

/** 递归从 children/elseChildren 中移除指定块（拖拽移出作用域时用）。 */
private fun removeBlockFromTree(
    blocks: List<AutomationBlock>,
    blockId: String
): Pair<List<AutomationBlock>, Boolean> {
    var removed = false
    val result = blocks.mapNotNull { block ->
        if (block.id == blockId) {
            removed = true
            null
        } else {
            val (newChildren, cRemoved) = removeBlockFromTree(block.children, blockId)
            val (newElse, eRemoved) = removeBlockFromTree(block.elseChildren, blockId)
            if (cRemoved || eRemoved) removed = true
            block.copy(children = newChildren, elseChildren = newElse)
        }
    }
    return result to removed
}

@Composable
private fun BlockCard(
    block: AutomationBlock,
    isTrigger: Boolean = false,
    dragController: DragController? = null,
    onUpdate: (AutomationBlock) -> Unit,
    onPickApps: (BlockParameter.StringParam) -> Unit,
    onPickWifi: (BlockParameter.StringParam) -> Unit = {},
    onPickBluetooth: (BlockParameter.StringParam) -> Unit = {},
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    nestLevel: Int = 0
) {
    // 长按拖拽：拖到触发器容器内即移入其作用域
    val dragModifier = if (dragController != null) {
        Modifier
            .onGloballyPositioned { coordinates ->
                // 记录每个块的窗口位置，供 move() 计算被拖块的手指窗口坐标
                dragController.blockWindowTopLefts[block.id] = coordinates.boundsInWindow().topLeft
            }
            .graphicsLayer {
                // 源块不平移（避免 feedback 环抽搐），仅降透明度提示正在拖
                val isDragged = dragController.draggedBlockId == block.id
                if (isDragged) {
                    alpha = 0.35f
                } else {
                    alpha = 1f
                }
            }
            .pointerInput(block.id) {
            detectDragGesturesAfterLongPress(
                onDragStart = { startOffset ->
                    dragController.start(block, startOffset)
                    // 记录被拖块的窗口坐标，供 move() 计算手指窗口位置
                    dragController.draggedWindowTopLeft =
                        dragController.blockWindowTopLefts[block.id] ?: androidx.compose.ui.geometry.Offset.Zero
                },
                onDrag = { change, _ ->
                    change.consume()
                    dragController.move(change.position)
                },
                onDragEnd = { dragController.end() },
                onDragCancel = { dragController.cancel() }
            )
            }
    } else {
        Modifier
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(dragModifier),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        cornerRadius = 24.dp
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // 内容区（去掉顶部图标+名称行，直接渲染编辑器）
                Column(modifier = Modifier.weight(1f)) {
                    if (block.type is BlockType.TriggerTime) {
                        // 时间触发：图一样式的定制编辑器（时间胶囊 + 重复选项）
                        TimeTriggerEditor(
                            block = block,
                            onUpdate = onUpdate
                        )
                    } else if (block.type is BlockType.TriggerWifi) {
                        // WiFi 触发：一行句子式编辑器（当 [📶] [WiFi名称] [条件] 时）
                        WifiTriggerEditor(
                            block = block,
                            onUpdate = onUpdate,
                            onPickWifi = { param -> onPickWifi(param) }
                        )
                    } else if (block.type is BlockType.TriggerBluetooth) {
                        // 蓝牙触发：一行句子式编辑器（当 [🔵] [设备] [条件] 时）
                        BluetoothTriggerEditor(
                            block = block,
                            onUpdate = onUpdate,
                            onPickBluetooth = { param -> onPickBluetooth(param) }
                        )
                    } else if (block.type is BlockType.TriggerCharging ||
                        block.type is BlockType.TriggerMusic ||
                        block.type is BlockType.CheckWifiState ||
                        block.type is BlockType.CheckBluetoothState ||
                        block.type is BlockType.CheckChargingState ||
                        block.type is BlockType.CheckScreenState ||
                        block.type is BlockType.CheckAirplaneState ||
                        block.type is BlockType.CheckDndState ||
                        block.type is BlockType.CheckSilentState ||
                        block.type is BlockType.CheckMobileDataState ||
                        block.type is BlockType.CheckMusicPlaying ||
                        block.type is BlockType.CheckAutoRotateState ||
                        block.type is BlockType.CheckHotspotState ||
                        block.type is BlockType.CheckNfcState ||
                        block.type is BlockType.CheckGpsState
                    ) {
                        // 状态类触发/检查：当 [图标] [状态胶囊] 时
                        StateChipEditor(
                            block = block,
                            onUpdate = onUpdate
                        )
                    } else if (block.type is BlockType.ToggleWifi ||
                        block.type is BlockType.ToggleBluetooth ||
                        block.type is BlockType.ToggleMobileData ||
                        block.type is BlockType.ToggleAirplane ||
                        block.type is BlockType.ToggleHotspot ||
                        block.type is BlockType.ToggleNfc ||
                        block.type is BlockType.ToggleGps ||
                        block.type is BlockType.ToggleFlashlight ||
                        block.type is BlockType.ToggleAutoRotate ||
                        block.type is BlockType.ToggleBatterySaver ||
                        block.type is BlockType.SetSilentMode ||
                        block.type is BlockType.SetAutoBrightness ||
                        block.type is BlockType.SetGrayscale ||
                        block.type is BlockType.SetRaiseToWake ||
                        block.type is BlockType.SetWakeForNotifications ||
                        block.type is BlockType.SetEyeCare ||
                        block.type is BlockType.SetAdaptiveRefreshRatePro ||
                        block.type is BlockType.Set5g ||
                        block.type is BlockType.SetMotionSicknessRelief
                    ) {
                        // 开关操作：显示名称 + 状态胶囊（如 "WiFi [开启]"）
                        StateChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            showWhenSuffix = false
                        )
                    } else {
                        block.parameters.forEach { param ->
                            Spacer(modifier = Modifier.height(6.dp))
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
                }

                // 右侧 × 删除按钮
                Spacer(modifier = Modifier.width(10.dp))
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

            // 触发器作用域：{ } 容器，条件满足时才执行内部操作
            if (block.isTriggerBlock() && nestLevel < 3) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            dragController?.containerBounds?.put(
                                block.id,
                                coordinates.boundsInWindow()
                            )
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (dragController?.dropTargetId == block.id) {
                                MiuixTheme.colorScheme.primary.copy(alpha = 0.18f)
                            } else {
                                MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f)
                            }
                        )
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "{ }",
                            style = MiuixTheme.textStyles.body1,
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "触发时执行",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    block.children.forEach { childBlock ->
                        BlockCard(
                            block = childBlock,
                            dragController = dragController,
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
                            onPickWifi = onPickWifi,
                            onPickBluetooth = onPickBluetooth,
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
        
            
            // 显示嵌套的子块（用于 IF、REPEAT 等，触发器已单独渲染作用域）
            if (!block.isTriggerBlock() && block.children.isNotEmpty() && nestLevel < 3) {
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
                            dragController = dragController,
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
                            onPickWifi = onPickWifi,
                            onPickBluetooth = onPickBluetooth,
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
                            dragController = dragController,
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
                            onPickWifi = onPickWifi,
                            onPickBluetooth = onPickBluetooth,
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








