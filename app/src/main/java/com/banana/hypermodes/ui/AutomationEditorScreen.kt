package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import android.content.ClipData
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
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

    // 拖拽落点：
    // - targetId == null：拖回顶层
    // - 目标块（普通或触发器）：按 before 插到其上方/下方（重排）
    //   拖入触发器 `{}` 作用域由 onDropIntoScope 单独处理
    LaunchedEffect(Unit) {
        dragController.onDrop = { draggedId, targetId, before ->
            if (targetId != null) {
                blocks = moveBlockBeforeAfter(blocks, draggedId, targetId, before)
            } else {
                // 拖到顶层空白区域：提取后按 before 插到顶层最前或追加末尾
                val (withoutDragged, dragged) = extractBlockFromTree(blocks, draggedId)
                if (dragged != null) {
                    blocks = if (before) listOf(dragged) + withoutDragged
                    else withoutDragged + dragged
                }
            }
        }
        dragController.onDropIntoScope = { draggedId, targetId ->
            blocks = moveBlockIntoParent(blocks, draggedId, targetId)
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
        
        // 同步顶层块顺序，供拖拽空白区域落点计算
        dragController.topLevelIds = blocks.map { it.id }

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
                    dragController.rootBounds = coordinates.boundsInWindow()
                }
                // 顶层区域也是放置目标：按落点位置插到最前或追加末尾
                .dragAndDropTarget(
                    shouldStartDragAndDrop = { isHyperModesBlockDrag(it) },
                    target = remember {
                        object : DragAndDropTarget {
                            override fun onEnded(event: DragAndDropEvent) {
                                dragController.draggedBlockId = null
                                dragController.dropTargetId = null
                                dragController.gapIndicator = null
                            }

                            override fun onDrop(event: DragAndDropEvent): Boolean {
                                val draggedId = event.blockIdOrNull() ?: return false
                                val y = event.toAndroidDragEvent().y
                                // 与缺口指示一致：找到指针下方最近的顶层块，插到它前面；否则追加末尾
                                val firstBelow = dragController.topLevelIds.firstOrNull { id ->
                                    dragController.blockBounds[id]?.let { y < it.center.y } == true
                                }
                                if (firstBelow != null) {
                                    dragController.onDrop?.invoke(draggedId, firstBelow, true)
                                } else {
                                    dragController.onDrop?.invoke(draggedId, null, false)
                                }
                                dragController.draggedBlockId = null
                                dragController.dropTargetId = null
                                dragController.gapIndicator = null
                                return true
                            }

                            override fun onMoved(event: DragAndDropEvent) {
                                // 顶层空白区域悬停：最前或最后显示缺口
                                val y = event.toAndroidDragEvent().y
                                // 若指针落在某个顶层卡片上，交给卡片处理
                                val overCard = dragController.topLevelIds.any { id ->
                                    dragController.blockBounds[id]?.let {
                                        y >= it.top && y <= it.bottom
                                    } == true
                                }
                                if (!overCard) {
                                    val firstBelow = dragController.topLevelIds.firstOrNull { id ->
                                        dragController.blockBounds[id]?.let { y < it.center.y } == true
                                    }
                                    dragController.gapIndicator = if (firstBelow != null) {
                                        firstBelow to true
                                    } else {
                                        dragController.topLevelIds.lastOrNull()?.let { it to false }
                                    }
                                }
                            }
                        }
                    }
                )
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

            items(blocks.size, key = { blocks[it].id }) { index ->
                val block = blocks[index]
                DragCollapseHost(
                    blockId = block.id,
                    dragController = dragController,
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                ) {
                    DragGapPlaceholder(
                        dragController = dragController,
                        blockId = block.id,
                        before = true
                    )
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
                    DragGapPlaceholder(
                        dragController = dragController,
                        blockId = block.id,
                        before = false
                    )
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
            Text(
                text = connectParam?.value ?: "已加入",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
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
            Text(
                text = connectParam?.value ?: "已连接",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
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
            Text(
                text = stateParam?.value ?: "开启",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
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

/** 在块树中按 id 查找块（含嵌套）。 */
private fun findBlock(
    blocks: List<AutomationBlock>,
    blockId: String
): AutomationBlock? {
    for (block in blocks) {
        if (block.id == blockId) return block
        findBlock(block.children, blockId)?.let { return it }
        findBlock(block.elseChildren, blockId)?.let { return it }
    }
    return null
}

/** 重排：把被拖块插到目标块的上方（before）或下方（after），保持同一层级。 */
private fun moveBlockBeforeAfter(
    blocks: List<AutomationBlock>,
    draggedId: String,
    targetId: String,
    before: Boolean
): List<AutomationBlock> {
    // 拖到自身（或目标不存在）：不移动，防止误删
    if (draggedId == targetId) return blocks
    // 同层级重排：先定位目标块所在列表，把被拖块从树中移除，再插到目标旁边
    val (withoutDragged, dragged) = extractBlockFromTree(blocks, draggedId)
        ?: return blocks
    if (dragged == null) return blocks

    fun reorder(list: List<AutomationBlock>): List<AutomationBlock> {
        val idx = list.indexOfFirst { it.id == targetId }
        if (idx < 0) return list
        val newList = list.toMutableList()
        val insertAt = if (before) idx else idx + 1
        newList.add(insertAt, dragged)
        return newList
    }

    fun walk(list: List<AutomationBlock>): Pair<List<AutomationBlock>, Boolean> {
        val atThisLevel = list.any { it.id == targetId }
        if (atThisLevel) return reorder(list) to true
        var changed = false
        val mapped = list.map { block ->
            val (newChildren, c1) = walk(block.children)
            val (newElse, c2) = walk(block.elseChildren)
            if (c1 || c2) {
                changed = true
                block.copy(children = newChildren, elseChildren = newElse)
            } else block
        }
        return mapped to changed
    }

    val (result, _) = walk(withoutDragged)
    return result
}

/** 拖拽结束：把被拖块移入目标触发器容器的 children。 */
private fun moveBlockIntoParent(
    blocks: List<AutomationBlock>,
    draggedId: String,
    targetParentId: String
): List<AutomationBlock> {
    // 拖到自己（或目标父块不存在）：不移动，防止误删
    if (draggedId == targetParentId) return blocks
    val (withoutDragged, dragged) = extractBlockFromTree(blocks, draggedId)
        ?: return blocks
    if (dragged == null) return blocks
    return addBlockToChildren(withoutDragged, targetParentId, dragged)
}

/** 判断拖放事件是否来自 HyperModes 的块（按 localState 识别，clipData 在某些系统上不可靠）。 */
private fun isHyperModesBlockDrag(event: DragAndDropEvent): Boolean {
    return event.blockIdOrNull() != null
}

/** 从拖放事件中读取被拖块的 id（优先 localState，失败再读 clipData）。 */
private fun DragAndDropEvent.blockIdOrNull(): String? {
    val local = toAndroidDragEvent().localState as? String
    if (!local.isNullOrBlank()) return local
    return toAndroidDragEvent().clipData?.getItemAt(0)?.text?.toString()
}

/** 收集块及其所有子孙块 id（拖拽时用于忽略对自身子树的悬停）。 */
private fun collectSubtreeIds(block: AutomationBlock): Set<String> =
    setOf(block.id) +
        block.children.flatMap { collectSubtreeIds(it) } +
        block.elseChildren.flatMap { collectSubtreeIds(it) }

/** 拖拽控制器：官方 dragAndDrop API 下仅维护高亮状态与落点回调。 */
private class DragController {
    /** 当前高亮的放置目标容器 id（拖拽进入时设置，退出时清空）。 */
    var dropTargetId by mutableStateOf<String?>(null)
    /** 当前被拖拽的块 id（拖拽开始设置，结束/取消清除）。 */
    var draggedBlockId by mutableStateOf<String?>(null)
    /** 缺口指示：目标块 id + 是否插到其上方；null 表示无缺口。拖拽悬停时让其他卡片挤开。 */
    var gapIndicator by mutableStateOf<Pair<String, Boolean>?>(null)
    /** 被拖卡片的高度（px），用于缺口占位高度。 */
    var draggedHeightPx by mutableStateOf(0f)
    /** 被拖块及其所有子孙块 id（拖拽时忽略对自身子树的悬停/放置，防止误删）。 */
    var draggedSubtreeIds by mutableStateOf<Set<String>>(emptySet())
    /** 顶层块 id 顺序（组合期间同步，用于空白区域落点计算）。 */
    var topLevelIds: List<String> = emptyList()
    /** 所有块的窗口边界（blockId -> 窗口 Rect），用于判断插到目标上方/下方。 */
    val blockBounds = mutableMapOf<String, androidx.compose.ui.geometry.Rect>()
    /** 各块的自然布局高度（px），源块塌陷动画用（拖拽时记录，源块高度动画到 0）。 */
    val naturalHeights = mutableMapOf<String, Int>()
    /** 触发器 `{}` 容器的窗口边界（blockId -> 窗口 Rect），落点在其中则移入作用域。 */
    val scopeBounds = mutableMapOf<String, androidx.compose.ui.geometry.Rect>()
    /** 顶层放置区域的窗口边界（用于判断拖到最前还是末尾）。 */
    var rootBounds by mutableStateOf<androidx.compose.ui.geometry.Rect?>(null)
    /**
     * 落点回调。
     * @param targetId 目标块 id（null 表示拖回顶层）
     * @param before 拖到目标块上方（插前）；false 为下方（插后），仅重排时有效
     */
    var onDrop: ((draggedId: String, targetId: String?, before: Boolean) -> Unit)? = null
    /** 拖入触发器 `{}` 作用域的回调。 */
    var onDropIntoScope: ((draggedId: String, targetId: String) -> Unit)? = null
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
    Card(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (dragController != null) {
                    // 触发器块本体也是放置目标：拖到触发器块任意位置即移入其作用域
                    val baseModifier = Modifier
                        .onGloballyPositioned { coordinates ->
                            dragController.blockBounds[block.id] = coordinates.boundsInWindow()
                        }
                        .graphicsLayer {
                            // 拖拽中的源块完全隐藏（系统阴影已跟手，原块直接消失）
                            alpha = if (dragController.draggedBlockId == block.id) 0f else 1f
                        }
                        .dragAndDropSource(
                            transferData = {
                                dragController.draggedBlockId = block.id
                                dragController.draggedHeightPx =
                                    dragController.blockBounds[block.id]?.height ?: 0f
                                dragController.draggedSubtreeIds = collectSubtreeIds(block)
                                DragAndDropTransferData(
                                    clipData = ClipData.newPlainText("hypermodes_block", block.id),
                                    localState = block.id
                                )
                            }
                        )
                    // 所有块都可作为放置目标：
                    // - 落点在触发器 `{}` 作用域内 → 移入作用域
                    // - 其他情况 → 按落点 Y 决定插到其上方还是下方（重排）
                    baseModifier.dragAndDropTarget(
                        shouldStartDragAndDrop = { isHyperModesBlockDrag(it) },
                        target = remember(block.id) {
                            object : DragAndDropTarget {
                                override fun onEnded(event: DragAndDropEvent) {
                                    dragController.draggedBlockId = null
                                    dragController.dropTargetId = null
                                    dragController.gapIndicator = null
                                }

                                override fun onEntered(event: DragAndDropEvent) {
                                    updateHover(event)
                                }

                                override fun onMoved(event: DragAndDropEvent) {
                                    updateHover(event)
                                }

                                override fun onExited(event: DragAndDropEvent) {
                                    if (dragController.dropTargetId == block.id) {
                                        dragController.dropTargetId = null
                                    }
                                    if (dragController.gapIndicator?.first == block.id) {
                                        dragController.gapIndicator = null
                                    }
                                }

                                override fun onDrop(event: DragAndDropEvent): Boolean {
                                    val draggedId = event.blockIdOrNull() ?: return false
                                    // 拖到自身（或其子孙）上：视为取消，防止误删
                                    if (block.id == draggedId || block.id in dragController.draggedSubtreeIds) {
                                        dragController.draggedBlockId = null
                                        dragController.dropTargetId = null
                                        dragController.gapIndicator = null
                                        return true
                                    }
                                    val y = event.toAndroidDragEvent().y
                                    // 触发器块：落点在 {} 作用域内则移入作用域
                                    if (block.isTriggerBlock() &&
                                        dragController.scopeBounds[block.id]?.let {
                                            y >= it.top && y <= it.bottom
                                        } == true
                                    ) {
                                        dragController.onDropIntoScope?.invoke(draggedId, block.id)
                                        dragController.draggedBlockId = null
                                        dragController.dropTargetId = null
                                        dragController.gapIndicator = null
                                        return true
                                    }
                                    // 判断落点相对目标块的上/下半，决定插前还是插后
                                    val before = dragController.blockBounds[block.id]
                                        ?.let { bounds -> y < bounds.center.y }
                                        ?: false
                                    dragController.onDrop?.invoke(draggedId, block.id, before)
                                    dragController.draggedBlockId = null
                                    dragController.dropTargetId = null
                                    dragController.gapIndicator = null
                                    return true
                                }

                                /** 悬停时更新高亮与缺口指示：作用域内高亮容器，其余按上下半区显示缺口。 */
                                private fun updateHover(event: DragAndDropEvent) {
                                    // 悬停在自己的子孙上：不显示缺口（防止把块拖进自己的作用域）
                                    if (block.id in dragController.draggedSubtreeIds) {
                                        dragController.dropTargetId = null
                                        dragController.gapIndicator = null
                                        return
                                    }
                                    val y = event.toAndroidDragEvent().y
                                    // 指针落在子孙块上（嵌套容器内）：交给子孙块处理
                                    val overDescendant = dragController.blockBounds.any { (id, rect) ->
                                        id != block.id &&
                                            id !in dragController.draggedSubtreeIds &&
                                            y >= rect.top && y <= rect.bottom &&
                                            rect.width < (dragController.blockBounds[block.id]?.width ?: 0f)
                                    }
                                    if (overDescendant) return
                                    // 触发器：指针在 {} 作用域内 → 高亮作用域，不显示缺口
                                    if (block.isTriggerBlock() &&
                                        dragController.scopeBounds[block.id]?.let {
                                            y >= it.top && y <= it.bottom
                                        } == true
                                    ) {
                                        dragController.dropTargetId = block.id
                                        dragController.gapIndicator = null
                                        return
                                    }
                                    dragController.dropTargetId = null
                                    val before = dragController.blockBounds[block.id]
                                        ?.let { bounds -> y < bounds.center.y }
                                        ?: true
                                    dragController.gapIndicator = block.id to before
                                }
                            }
                        }
                    )
                } else {
                    Modifier
                }
            ),
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
                        block.type is BlockType.SetDarkMode ||
                        block.type is BlockType.SetGrayscale ||
                        block.type is BlockType.SetRaiseToWake ||
                        block.type is BlockType.SetWakeForNotifications ||
                        block.type is BlockType.SetEyeCare ||
                        block.type is BlockType.SetAdaptiveRefreshRatePro ||
                        block.type is BlockType.SetAod ||
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
                            dragController?.scopeBounds?.put(
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
                        DragCollapseHost(
                            blockId = childBlock.id,
                            dragController = dragController,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = childBlock.id,
                                before = true,
                                horizontalPadding = 8.dp
                            )
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
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = childBlock.id,
                                before = false,
                                horizontalPadding = 8.dp
                            )
                        }
                    }
                    // 作用域尾部缺口：悬停在 {} 容器空白处时，显示将追加到末尾的占位
                    DragGapPlaceholder(
                        dragController = dragController,
                        blockId = block.id,
                        before = false,
                        horizontalPadding = 8.dp,
                        scopeAppend = true
                    )
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
                        DragCollapseHost(
                            blockId = childBlock.id,
                            dragController = dragController,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = childBlock.id,
                                before = true,
                                horizontalPadding = 8.dp
                            )
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
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = childBlock.id,
                                before = false,
                                horizontalPadding = 8.dp
                            )
                        }
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
                        DragCollapseHost(
                            blockId = elseBlock.id,
                            dragController = dragController,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = elseBlock.id,
                                before = true,
                                horizontalPadding = 8.dp
                            )
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
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = elseBlock.id,
                                before = false,
                                horizontalPadding = 8.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 拖拽源块塌陷容器：当本块被拖拽时高度动画收缩到 0，
 * 让其他卡片补位（解决源块 alpha=0 仍占空间的问题）。
 */
@Composable
private fun DragCollapseHost(
    blockId: String,
    dragController: DragController?,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    if (dragController == null) {
        Box(modifier = modifier) { content() }
        return
    }
    val isSource = dragController.draggedBlockId == blockId
    var naturalHeight by remember(blockId) { mutableStateOf(0.dp) }
    val density = LocalDensity.current
    val collapseHeight by animateDpAsState(
        targetValue = if (isSource) 0.dp else naturalHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "sourceCollapse"
    )
    val collapseModifier = if (isSource || collapseHeight < naturalHeight) {
        Modifier.height(collapseHeight).clip(RoundedCornerShape(24.dp))
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .onSizeChanged { size ->
                // 非拖拽时记录自然高度，供拖拽时动画到 0 用
                if (!isSource) {
                    naturalHeight = with(density) { size.height.toDp() }
                }
            }
            .then(collapseModifier)
    ) {
        content()
    }
}

/**
 * 拖拽悬停时的占位缺口：在被拖块将落下的位置撑开一段动画高度，
 * 让其他卡片"挤开"，指示松手后的插入位置。
 * [before] 为 true 时缺口位于目标块上方（插前），false 位于下方（插后）。
 * [scopeAppend] 为 true 时，缺口在触发器 `{}` 作用域高亮（dropTargetId 命中）时显示，
 * 表示将追加到作用域末尾。
 */
@Composable
private fun DragGapPlaceholder(
    dragController: DragController?,
    blockId: String,
    before: Boolean,
    horizontalPadding: Dp = 12.dp,
    scopeAppend: Boolean = false
) {
    if (dragController == null) return
    val dragging = dragController.draggedBlockId != null
    val active = if (scopeAppend) {
        dragging && dragController.dropTargetId == blockId
    } else {
        dragging &&
            dragController.gapIndicator?.first == blockId &&
            dragController.gapIndicator?.second == before
    }
    val density = LocalDensity.current
    val targetHeight = if (active) {
        with(density) { dragController.draggedHeightPx.toDp() }
    } else {
        0.dp
    }
    val height by animateDpAsState(
        targetValue = targetHeight,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "dragGap"
    )
    if (height > 0.dp) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .height(height)
                .clip(RoundedCornerShape(24.dp))
                .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f))
        )
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
                    .clickable { expanded = !expanded }
            )
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








