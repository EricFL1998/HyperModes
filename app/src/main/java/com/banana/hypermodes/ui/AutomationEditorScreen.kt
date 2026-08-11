package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import android.content.ClipData
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import com.banana.hypermodes.R
import com.banana.hypermodes.automation.AutomationCatalog
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.ImportedIntentStore
import com.banana.hypermodes.data.IntentConfig
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
import androidx.compose.ui.platform.LocalView
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
    val description: String = "",
    /** 已导入意图元数据：非空时表示这是一个"发送意图"操作。 */
    val intentPackage: String? = null,
    val intentName: String? = null,
    val intentAction: String? = null,
    /** true 时创建"当"（意图触发）块，监听意图广播；false 为发送意图操作。 */
    val intentTrigger: Boolean = false
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
    var selectedCategory by remember { mutableStateOf<Any?>(null) }
    val listState = rememberLazyListState()

    // 每次打开弹窗都重置分类到"全部"、清空搜索
    LaunchedEffect(show) {
        if (show) {
            selectedCategory = null
            searchQuery = ""
            listState.scrollToItem(0)
        }
    }

    // 切换分类（含切回"全部"）时列表回到顶部
    LaunchedEffect(selectedCategory) {
        listState.scrollToItem(0)
    }

    // 可用分类（未限定 categories 时展示全部；被限定则只展示限定分类）
    val availableCategories = remember(categories) {
        if (categories == null) AutomationCatalog.Category.entries
        else categories.toList()
    }

    // 已导入的意图：app 名作为类别，每个 IntentAction 作为一个操作。
    // 用进程级缓存，避免每次弹窗/重组都重新读 SharedPreferences。
    val context = LocalContext.current
    val importedConfigs = remember { ImportedIntentStore.loadAllCached(context) }

    /** 根据 id 判断当前选中项是否为某个意图类别。 */
    fun isIntentCategorySelected(packageName: String): Boolean =
        selectedCategory == packageName

    WindowBottomSheet(
        show = show,
        onDismissRequest = onDismiss,
        title = "选择操作",
        insideMargin = DpSize(0.dp, 0.dp)
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

            // 分类按钮（横向滚动胶囊），点一下列表自动切换
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                contentPadding = PaddingValues(end = 8.dp)
            ) {
                item(key = "cat-all") {
                    CategoryChip(
                        label = "全部",
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null }
                    )
                }
                items(availableCategories, key = { it.name }) { category ->
                    CategoryChip(
                        label = category.label,
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
                items(importedConfigs, key = { "intent-${it.packageName}" }) { config ->
                    CategoryChip(
                        label = config.appName,
                        selected = isIntentCategorySelected(config.packageName),
                        onClick = { selectedCategory = config.packageName }
                    )
                }
            }

            // 分组操作列表（按分类，以粗体标题分组）
            val allActions = AutomationCatalog.entries
                .filter { categories == null || it.category in categories }
                .filter { selectedCategory == null || selectedCategory is AutomationCatalog.Category && it.category == selectedCategory }
                .map { entry ->
                    AutomationAction(
                        id = entry.id,
                        name = entry.name,
                        icon = entry.icon,
                        iconColor = entry.iconColor,
                        description = entry.description
                    )
                }
            // 已导入意图操作：按 app 名分组，每个 IntentAction 一个操作
            val intentActions: List<Pair<IntentConfig, List<AutomationAction>>> = importedConfigs
                .filter { config -> selectedCategory == null || selectedCategory == config.packageName }
                .map { config ->
                    config to config.intents.map { action ->
                        // 每个意图：发送意图操作（可拖拽进 "当" 的 {} 绑定）
                        AutomationAction(
                            id = "intent_send_${config.packageName}_${action.name}",
                            name = action.name,
                            icon = "📨",
                            iconColor = Color(0xFF5856D6),
                            description = "向 ${config.appName} 发送广播 ${action.name}",
                            intentPackage = config.packageName,
                            intentName = action.name,
                            intentAction = action.intents.firstOrNull()
                        )
                    }
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
            val filteredIntentActions = remember(searchQuery, intentActions) {
                intentActions.map { (config, actions) ->
                    config to if (searchQuery.isBlank()) {
                        actions
                    } else {
                        actions.filter { action ->
                            action.name.contains(searchQuery, ignoreCase = true) ||
                                    action.description.contains(searchQuery, ignoreCase = true)
                        }
                    }
                }.filter { it.second.isNotEmpty() }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState
            ) {
                if (searchQuery.isBlank()) {
                    if (selectedCategory == null) {
                        // 全部：按分类分组展示，带粗体标题
                        AutomationCatalog.grouped()
                            .filter { (category, _) -> categories == null || category in categories }
                            .forEach { (category, entries) ->
                                item(key = "header-${category.name}") {
                                    Text(
                                        text = category.label,
                                        style = MiuixTheme.textStyles.headline1.copy(fontWeight = FontWeight.Bold),
                                        modifier = Modifier.padding(
                                            start = 20.dp,
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
                        // 已导入意图：按 app 分组展示
                        filteredIntentActions.forEach { (config, actions) ->
                            item(key = "header-intent-${config.packageName}") {
                                Text(
                                    text = config.appName,
                                    style = MiuixTheme.textStyles.headline1.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(
                                        start = 20.dp,
                                        top = 20.dp,
                                        bottom = 12.dp
                                    )
                                )
                            }
                            items(actions, key = { it.id }) { action ->
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
                        // 选中分类：扁平展示该分类的操作
                        if (selectedCategory is AutomationCatalog.Category) {
                            items(allActions, key = { it.id }) { action ->
                                ActionOptionCard(
                                    action = action,
                                    onClick = {
                                        onActionSelected(action)
                                        onDismiss()
                                    }
                                )
                            }
                        } else {
                            // 选中意图类别：扁平展示该 app 的意图操作
                            filteredIntentActions.forEach { (_, actions) ->
                                items(actions, key = { it.id }) { action ->
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
                    filteredIntentActions.forEach { (_, actions) ->
                        items(actions, key = { it.id }) { action ->
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
            .padding(horizontal = 12.dp)
            .padding(bottom = 10.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        cornerRadius = 20.dp,
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

/** 分类胶囊按钮：选中态主题色高亮，未选中灰底。 */
@Composable
private fun CategoryChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) {
        MiuixTheme.colorScheme.primary
    } else {
        MiuixTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    }
    val contentColor = if (selected) {
        MiuixTheme.colorScheme.onPrimary
    } else {
        MiuixTheme.colorScheme.onSurface
    }
    Box(
        modifier = Modifier
            .padding(end = 8.dp, bottom = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 17.dp, vertical = 7.dp)
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.body2,
            color = contentColor
        )
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
            // 复用 {} 作用域拖入逻辑：意图块拖进"当"（TriggerIntent）时，
            // 把意图参数绑定到"当"块，并移除意图块；其余走原有移入 children 逻辑。
            val targetBlock = findBlock(blocks, targetId)
            val draggedBlock = findBlock(blocks, draggedId)
            if (targetBlock?.type is BlockType.TriggerIntent &&
                draggedBlock?.type is BlockType.SendIntent
            ) {
                val (withoutDragged, _) = extractBlockFromTree(blocks, draggedId)
                blocks = updateBlockInTree(withoutDragged, targetId) { b ->
                    b.copy(
                        parameters = b.parameters.map { p ->
                            when (p.key) {
                                "packageName" -> (p as? BlockParameter.StringParam)
                                    ?.copy(value = draggedBlock.stringParam("packageName"))
                                "intentName" -> (p as? BlockParameter.StringParam)
                                    ?.copy(value = draggedBlock.stringParam("intentName"))
                                "action" -> (p as? BlockParameter.StringParam)
                                    ?.copy(value = draggedBlock.stringParam("action"))
                                else -> p
                            } ?: p
                        }
                    )
                }
            } else {
                blocks = moveBlockIntoParent(blocks, draggedId, targetId)
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
                                val firstBelow = dragController.topLevelIds
                                    .filter { it != draggedId }
                                    .firstOrNull { id ->
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
                                    val dragged = dragController.draggedBlockId
                                    val firstBelow = dragController.topLevelIds
                                        .filter { it != dragged }
                                        .firstOrNull { id ->
                                        dragController.blockBounds[id]?.let { y < it.center.y } == true
                                        }
                                    dragController.gapIndicator = if (firstBelow != null) {
                                        firstBelow to true
                                    } else {
                                        dragController.topLevelIds.lastOrNull { it != dragged }?.let { it to false }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    // 缺口占位放在塌陷容器外层，独立撑开布局
                    DragGapPlaceholder(
                        dragController = dragController,
                        blockId = block.id,
                        before = true
                    )
                    DragCollapseHost(
                        blockId = block.id,
                        dragController = dragController,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                        onDetachIntent = {
                            // 拖出：立即解绑"当"并生成独立意图块插入到"当"之后，
                            // 返回新块 id 作为拖拽源（跟手、挤开、可插上下）。
                            val pkg = block.stringParam("packageName")
                            val name = block.stringParam("intentName")
                            val action = block.stringParam("action")
                            val newId = java.util.UUID.randomUUID().toString()
                            val intentBlock = AutomationBlock(
                                id = newId,
                                type = BlockType.SendIntent,
                                label = name.ifBlank { "意图" },
                                icon = "📨",
                                iconColor = Color(0xFF5856D6),
                                parameters = listOf(
                                    BlockParameter.StringParam("packageName", "应用包名", pkg),
                                    BlockParameter.StringParam("intentName", "意图名称", name),
                                    BlockParameter.StringParam("action", "广播 Action", action)
                                )
                            )
                            // 递归清空"当"块的绑定参数（支持嵌套），恢复"拖入意图"
                            val cleared = updateBlockInTree(blocks, block.id) { b ->
                                b.copy(
                                    parameters = b.parameters.map { p ->
                                        when (p.key) {
                                            "packageName", "intentName", "action" ->
                                                (p as? BlockParameter.StringParam)?.copy(value = "")
                                                    ?: p
                                            else -> p
                                        }
                                    }
                                )
                            }
                            // 插入到"当"块之后（保持与原位置相邻）
                            val idx = cleared.indexOfFirst { it.id == block.id }
                            blocks = if (idx >= 0) {
                                cleared.toMutableList().apply { add(idx + 1, intentBlock) }
                            } else {
                                cleared + intentBlock
                            }
                            newId
                        },
                        onRemove = {
                            blocks = blocks.filter { it.id != block.id }
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp)
                    )
                    }
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
            .fillMaxWidth(),
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
 * 意图触发的一行句子式编辑器：
 * 当 [🔔] [意图名] 时
 * 意图名来自下方 {} 中绑定的意图块（拖入后回填参数）；未绑定时提示拖入。
 */
@Composable
private fun IntentTriggerEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    dragController: DragController? = null,
    onDetachIntent: () -> String? = { null }
) {
    val context = LocalContext.current
    val view = LocalView.current
    val density = LocalDensity.current.density
    val cardBackground = MiuixTheme.colorScheme.background
    val cardText = MiuixTheme.colorScheme.onSurface
    val packageName = block.stringParam("packageName")
    val intentName = block.parameters.find { it.key == "intentName" }
        ?.let { (it as? BlockParameter.StringParam)?.value }
        .orEmpty()
    // 按包名解析 app 名称（来自已导入配置）；找不到时回退显示包名
    val appName = if (packageName.isNotBlank()) {
        ImportedIntentStore.loadAllCached(context)
            .firstOrNull { it.packageName == packageName }
            ?.appName
            ?: packageName
    } else {
        ""
    }
    val bound = intentName.isNotBlank()
    val displayText = if (bound) "$appName · $intentName" else "拖入意图"

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "当",
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
            modifier = Modifier.padding(end = 6.dp)
        )
        // 意图图标
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5856D6).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📨",
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        // 意图名胶囊（空槽位提示拖入）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .then(
                    // 已绑定：可长按拖出，还原为独立意图块
                    if (bound) {
                        Modifier.pointerInput(block.id) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = {
                                    // 拖拽开始瞬间：解绑"当"并生成独立块作为拖拽源，
                                    // 用自定义 DragShadowBuilder 绘制块卡片阴影。
                                    val newId = onDetachIntent()
                                    if (newId != null) {
                                        dragController?.let { dc ->
                                            dc.draggedBlockId = newId
                                            dc.draggedHeightPx =
                                                dc.blockBounds?.get(block.id)?.height ?: 0f
                                            dc.draggedSubtreeIds = setOf(newId)
                                        }
                                        val shadow = BlockDragShadowBuilder(
                                            label = displayText,
                                            density = density,
                                            widthPx = dragController?.blockBounds
                                                ?.get(block.id)?.width?.toInt()
                                                ?: (280 * density).toInt(),
                                            backgroundColor = cardBackground.toArgb(),
                                            textColor = cardText.toArgb()
                                        )
                                        view.startDragAndDrop(
                                            ClipData.newPlainText("hypermodes_block", newId),
                                            shadow,
                                            newId,
                                            0
                                        )
                                    }
                                },
                                onDrag = { change, _ -> change.consume() },
                                onDragEnd = { },
                                onDragCancel = { }
                            )
                        }
                    } else {
                        Modifier
                    }
                )
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = displayText,
                color = if (!bound) {
                    MiuixTheme.colorScheme.onSurfaceVariantSummary
                } else {
                    Color(0xFF0A84FF)
                },
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
}


/**
 * 拖出意图时的自定义拖拽阴影：绘制一个圆角块卡片（背景 + 图标 + 文字），
 * 替代系统默认的纯文字气泡，让拖拽跟手时看起来像真正的 block。
 */
private class BlockDragShadowBuilder(
    private val label: String,
    private val density: Float,
    private val widthPx: Int,
    private val backgroundColor: Int,
    private val textColor: Int
) : android.view.View.DragShadowBuilder() {

    private val shadowWidth = widthPx
    private val shadowHeight = (48 * density).toInt()

    override fun onProvideShadowMetrics(
        outShadowSize: android.graphics.Point,
        outShadowTouchPoint: android.graphics.Point
    ) {
        outShadowSize.set(shadowWidth, shadowHeight)
        // 触摸点放在卡片顶部：阴影完全显示在手指下方，
        // 避免拖出意图时阴影盖住"当"模块（手指就在"当"模块内）。
        outShadowTouchPoint.set(shadowWidth / 2, 0)
    }

    override fun onDrawShadow(canvas: android.graphics.Canvas) {
        val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG)
        // 白色 MIUIX 块卡片背景（与真实 block 一致）
        paint.color = backgroundColor
        canvas.drawRoundRect(
            0f, 0f, shadowWidth.toFloat(), shadowHeight.toFloat(),
            20 * density, 20 * density,
            paint
        )
        // 意图图标
        paint.color = textColor
        paint.textSize = 18 * density
        canvas.drawText(
            "📨",
            16 * density,
            shadowHeight / 2f + 6 * density,
            paint
        )
        // 文字（app · intent）
        paint.textSize = 15 * density
        canvas.drawText(
            label,
            44 * density,
            shadowHeight / 2f + 5 * density,
            paint
        )
    }
}


/**
 * 发送意图的一行句子式编辑器：
 * 📨 [意图名]
 * 只显示意图名称，保持一行简洁。
 */
@Composable
private fun SendIntentEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit
) {
    val context = LocalContext.current
    val packageName = block.stringParam("packageName")
    val intentName = block.stringParam("intentName")
        .ifBlank { "未命名意图" }
    // 按包名解析 app 名称（来自已导入配置）；找不到时回退显示包名
    val appName = ImportedIntentStore.loadAllCached(context)
        .firstOrNull { it.packageName == packageName }
        ?.appName
        ?: packageName

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 意图图标
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF5856D6).copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📨",
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        // 【app 名称】【意图名称】
        Text(
            text = "$appName · $intentName",
            style = MiuixTheme.textStyles.body1,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0A84FF)
        )
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
            .fillMaxWidth(),
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
    // 深色模式的状态胶囊已含"深色/浅色"，名称只显示"模式"避免重复
    val displayName = if (block.type is BlockType.SetDarkMode) "模式" else block.label

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
            .fillMaxWidth(),
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
        } else {
            // 操作类：状态词在前，名称在后（如 "开启 蓝牙"）
            Text(
                text = displayName,
                style = MiuixTheme.textStyles.body1,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 2.dp)
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


/**
 * 电量判断的一行句子式编辑器：
 * [🔋] [高于/低于] [80% 滚动数字选择器] 时
 * 运算符点击弹菜单，百分比点击弹滚动选择器，一行搞定。
 */
@Composable
private fun BatteryLevelEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    showWhenSuffix: Boolean = true
) {
    val operatorParam = block.parameters.find { it.key == "operator" } as? BlockParameter.ChoiceParam
    val levelParam = block.parameters.find { it.key == "level" } as? BlockParameter.IntParam
    var showOperatorMenu by remember { mutableStateOf(false) }
    var showLevelPicker by remember { mutableStateOf(false) }

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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        // 运算符胶囊（点击弹菜单）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { showOperatorMenu = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = operatorParam?.value ?: "高于",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
        }
        Spacer(modifier = Modifier.width(6.dp))

        // 百分比胶囊（点击弹滚动数字选择器）
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFDBEBFC))
                .clickable { showLevelPicker = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${levelParam?.value ?: 20}%",
                color = Color(0xFF0A84FF),
                fontWeight = FontWeight.Medium,
                style = MiuixTheme.textStyles.body1
            )
        }

        if (showWhenSuffix) {
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "时",
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurface
            )
        }
    }

    // 运算符菜单
    if (showOperatorMenu) {
        OverlayDialog(
            show = showOperatorMenu,
            onDismissRequest = { showOperatorMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = "比较方式",
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                (operatorParam?.options ?: emptyList()).forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (operatorParam != null) updateParam(operatorParam.copy(value = option))
                                showOperatorMenu = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                        if (option == operatorParam?.value) {
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

    // 百分比滚动数字选择器
    if (showLevelPicker) {
        OverlayDialog(
            show = showLevelPicker,
            onDismissRequest = { showLevelPicker = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = "电量百分比",
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                NumberPicker(
                    value = levelParam?.value ?: 20,
                    onValueChange = { value ->
                        if (levelParam != null) updateParam(levelParam.copy(value = value))
                    },
                    range = 0..100,
                    label = { "$it%" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.primary)
                        .clickable { showLevelPicker = false }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "确定",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}


/**
 * 通用的一行句子式编辑器：
 * [图标] [参数胶囊...] [展开按钮]
 * - ChoiceParam 渲染成胶囊，点击弹菜单
 * - IntParam 渲染成数值胶囊，点击弹滚动数字选择器
 * - packages/package 渲染成应用选择胶囊
 * - modeId 渲染成模式选择胶囊
 * - start/end 渲染成时间胶囊
 * 高级参数（advancedKeys）默认折叠在展开区，点击右侧按钮展开（学时间触发器）。
 */
@Composable
private fun SentenceChipEditor(
    block: AutomationBlock,
    onUpdate: (AutomationBlock) -> Unit,
    onPickApps: (BlockParameter.StringParam) -> Unit = {},
    mainKeys: List<String>,
    advancedKeys: List<String> = emptyList(),
    showWhenSuffix: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

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
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

        // 主参数胶囊
        mainKeys.forEach { key ->
            val param = block.parameters.find { it.key == key }
            if (param != null) {
                ParamChip(
                    param = param,
                    onChange = { updateParam(it) },
                    onPickApps = { onPickApps(it) }
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        if (showWhenSuffix) {
            Text(
                text = "时",
                style = MiuixTheme.textStyles.body1,
                color = MiuixTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }

        // 高级选项展开按钮（学时间触发器）
        if (advancedKeys.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MiuixTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f))
                    .clickable { expanded = !expanded },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (expanded) "▲" else "▼",
                    fontSize = 12.sp,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
        }
    }

    // 展开区：高级参数（复用胶囊行）
    if (expanded && advancedKeys.isNotEmpty()) {
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            advancedKeys.forEach { key ->
                val param = block.parameters.find { it.key == key }
                if (param != null) {
                    Text(
                        text = param.label,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    ParamChip(
                        param = param,
                        onChange = { updateParam(it) },
                        onPickApps = { onPickApps(it) }
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
            }
        }
    }
}


/**
 * 参数胶囊：按参数类型渲染可点击的蓝色胶囊。
 * - ChoiceParam：显示当前选项，点击弹菜单
 * - IntParam：显示数值（含单位），点击弹滚动数字选择器
 * - packages/package：显示已选应用，点击进应用选择器
 * - modeId：显示模式名，点击弹模式列表
 * - start/end：显示时间，点击弹时间选择器
 */
@Composable
private fun ParamChip(
    param: BlockParameter,
    onChange: (BlockParameter) -> Unit,
    onPickApps: (BlockParameter.StringParam) -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }
    var showNumberPicker by remember { mutableStateOf(false) }
    var showModePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    // 模式选择需要模式列表：默认三个内置模式做本地化，其余用保存的名称
    val context = LocalContext.current
    var modes by remember { mutableStateOf<List<Mode>?>(null) }
    LaunchedEffect(Unit) {
        if (param is BlockParameter.StringParam && param.key == "modeId") {
            modes = ModeStore.load(context) { DefaultModes.get() }
        }
    }

    val label: String
    val onClick: () -> Unit

    when (param) {
        is BlockParameter.ChoiceParam -> {
            label = param.value
            onClick = { showMenu = true }
        }
        is BlockParameter.IntParam -> {
            label = "${param.value}${if (param.max == 100) "%" else ""}"
            onClick = { showNumberPicker = true }
        }
        is BlockParameter.StringParam -> {
            when (param.key) {
                "packages", "package" -> {
                    val count = param.value.split(",")
                        .map { it.trim() }
                        .count { it.isNotEmpty() }
                    label = if (count > 0) "已选 $count 个应用" else "选择应用..."
                    onClick = { onPickApps(param) }
                }
                "modeId" -> {
                    label = if (param.value.isBlank()) {
                        "选择模式..."
                    } else {
                        modes?.find { it.id == param.value }
                            ?.let { localizedModeName(it) }
                            ?: param.value
                    }
                    onClick = { showModePicker = true }
                }
                "start", "end" -> {
                    val parts = param.value.split(":")
                    label = if (param.value.isBlank()) "--:--" else {
                        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
                        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
                        "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                    }
                    onClick = { showTimePicker = true }
                }
                else -> {
                    label = param.value.ifBlank { "未设置" }
                    onClick = { showMenu = true }
                }
            }
        }
        is BlockParameter.BooleanParam -> {
            label = if (param.value) "开启" else "关闭"
            onClick = { onChange(param.copy(value = !param.value)) }
        }
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFFDBEBFC))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = Color(0xFF0A84FF),
            fontWeight = FontWeight.Medium,
            style = MiuixTheme.textStyles.body1
        )
    }

    // 选项菜单
    if (showMenu && param is BlockParameter.ChoiceParam) {
        OverlayDialog(
            show = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = param.label,
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                param.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onChange(param.copy(value = option))
                                showMenu = false
                            }
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                        if (option == param.value) {
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

    // 数值滚动选择器
    if (showNumberPicker && param is BlockParameter.IntParam) {
        OverlayDialog(
            show = showNumberPicker,
            onDismissRequest = { showNumberPicker = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp)
            ) {
                Text(
                    text = param.label,
                    style = MiuixTheme.textStyles.title3,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                NumberPicker(
                    value = param.value,
                    onValueChange = { onChange(param.copy(value = it)) },
                    range = param.min..param.max,
                    label = { "$it${if (param.max == 100) "%" else ""}" }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MiuixTheme.colorScheme.primary)
                        .clickable { showNumberPicker = false }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "确定",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

    // 模式选择
    if (showModePicker && param is BlockParameter.StringParam && param.key == "modeId") {
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
                            text = localizedModeName(mode),
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
            }
        }
    }

    // 时间选择
    if (showTimePicker && param is BlockParameter.StringParam &&
        (param.key == "start" || param.key == "end")
    ) {
        val parts = param.value.split(":")
        TimePickerDialog(
            title = if (param.key == "start") "设置开始时间" else "设置结束时间",
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 0,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            show = true,
            onDismissRequest = { showTimePicker = false },
            onConfirm = { h, m ->
                onChange(
                    param.copy(
                        value = "${h.toString().padStart(2, '0')}:${m.toString().padStart(2, '0')}"
                    )
                )
                showTimePicker = false
            }
        )
    }
}


/** 模式显示名：默认三个内置模式（dnd/bedtime/driving）做本地化，其余用保存的名称。 */
@Composable
private fun localizedModeName(mode: Mode): String = when (mode.id) {
    "dnd" -> stringResource(R.string.mode_dnd)
    "bedtime" -> stringResource(R.string.mode_bedtime)
    "driving" -> stringResource(R.string.mode_driving)
    else -> mode.name
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

/** 读取块字符串参数（编辑器本地辅助，与 AutomationExecutor 保持一致）。 */
private fun AutomationBlock.stringParam(key: String, default: String = ""): String =
    parameters.find { it.key == key }
        ?.let { (it as? BlockParameter.StringParam)?.value }
        ?: default

/** 递归在块树中更新指定 id 的块（找不到时原样返回）。 */
private fun updateBlockInTree(
    blocks: List<AutomationBlock>,
    blockId: String,
    transform: (AutomationBlock) -> AutomationBlock
): List<AutomationBlock> = blocks.map { block ->
    if (block.id == blockId) {
        transform(block)
    } else {
        block.copy(
            children = updateBlockInTree(block.children, blockId, transform),
            elseChildren = updateBlockInTree(block.elseChildren, blockId, transform)
        )
    }
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
    /** 把"当收到意图时"上绑定的意图拖出还原为独立意图块，返回新块 id。 */
    onDetachIntent: () -> String? = { null },
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
                                    // 触发器块：落点在 {} 作用域内则移入作用域；
                                    // 意图触发（TriggerIntent）块本体下半部分可绑定意图，
                                    // 上半部分按重排处理（插到上方），其他触发器仅 {} 作用域内移入。
                                    if (block.isTriggerBlock() &&
                                        (dragController.scopeBounds[block.id]?.let {
                                            y >= it.top && y <= it.bottom
                                        } == true ||
                                            (block.type is BlockType.TriggerIntent &&
                                                dragController.blockBounds[block.id]?.let {
                                                    y >= it.center.y
                                                } == true))
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
                                    val x = event.toAndroidDragEvent().x
                                    // 指针落在子孙块上（嵌套容器内）：交给子孙块处理
                                    val overDescendant = dragController.blockBounds.any { (id, rect) ->
                                        id != block.id &&
                                            id !in dragController.draggedSubtreeIds &&
                                            x >= rect.left && x <= rect.right &&
                                            y >= rect.top && y <= rect.bottom &&
                                            rect.width < (dragController.blockBounds[block.id]?.width ?: 0f)
                                    }
                                    if (overDescendant) return
                                    // 触发器：指针在 {} 作用域内 → 高亮作用域，不显示缺口；
                                    // TriggerIntent 块本体下半部分也可绑定（高亮作用域），上半部分显示插入缺口。
                                    if (block.isTriggerBlock() &&
                                        (dragController.scopeBounds[block.id]?.let {
                                            y >= it.top && y <= it.bottom
                                        } == true ||
                                            (block.type is BlockType.TriggerIntent &&
                                                dragController.blockBounds[block.id]?.let {
                                                    y >= it.center.y
                                                } == true))
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
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
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
                    } else if (block.type is BlockType.TriggerBattery ||
                        block.type is BlockType.CheckBatteryLevel
                    ) {
                        // 电量判断：一行句子式编辑器（[🔋] [高于/低于] [80% 滚动选择器] 时）
                        BatteryLevelEditor(
                            block = block,
                            onUpdate = onUpdate,
                            showWhenSuffix = block.type is BlockType.TriggerBattery
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
                    } else if (block.type is BlockType.TriggerNetwork ||
                        block.type is BlockType.CheckNetworkType
                    ) {
                        // 网络判断：一行句子式编辑器（[🌐] [WiFi/移动数据/无网络] 时）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("type"),
                            showWhenSuffix = block.type is BlockType.TriggerNetwork
                        )
                    } else if (block.type is BlockType.TriggerDayOfWeek ||
                        block.type is BlockType.CheckDayOfWeek
                    ) {
                        // 星期判断：一行句子式编辑器（[📅] [周一至周五/周末/每天] 时）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("days"),
                            showWhenSuffix = block.type is BlockType.TriggerDayOfWeek
                        )
                    } else if (block.type is BlockType.TriggerIntent) {
                        // 意图触发：当 [📨] [意图名] 时（空槽位提示拖入意图）
                        IntentTriggerEditor(
                            block = block,
                            onUpdate = onUpdate,
                            dragController = dragController,
                            onDetachIntent = onDetachIntent
                        )
                    } else if (block.type is BlockType.SendIntent) {
                        // 发送意图：一行只显示意图名
                        SendIntentEditor(
                            block = block,
                            onUpdate = onUpdate
                        )
                    } else if (block.type is BlockType.TriggerApp ||
                        block.type is BlockType.CheckAppForeground
                    ) {
                        // 应用判断：一行句子式编辑器（[📱] [选择应用] 时）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            onPickApps = { param -> onPickApps(param) },
                            mainKeys = listOf("package"),
                            showWhenSuffix = block.type is BlockType.TriggerApp
                        )
                    } else if (block.type is BlockType.SetDnd) {
                        // 勿扰：一行句子式编辑器（[🔕] [勿扰级别]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("level")
                        )
                    } else if (block.type is BlockType.AdjustVolume) {
                        // 音量：一行主参数（[🔊] [音量]），音量类型折叠在展开区
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("level"),
                            advancedKeys = listOf("stream")
                        )
                    } else if (block.type is BlockType.AdjustBrightness) {
                        // 亮度：一行句子式编辑器（[💡] [亮度]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("level")
                        )
                    } else if (block.type is BlockType.SetRefreshRate) {
                        // 刷新率：一行句子式编辑器（[🖥] [刷新率]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("rate")
                        )
                    } else if (block.type is BlockType.SetPerformanceMode) {
                        // 性能模式：一行句子式编辑器（[⚡] [性能模式]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("mode")
                        )
                    } else if (block.type is BlockType.SetPreferredSim) {
                        // 数据卡：一行句子式编辑器（[📶] [SIM 1/SIM 2]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("slot")
                        )
                    } else if (block.type is BlockType.SetMode) {
                        // 模式：一行句子式编辑器（[🎯] [选择模式] [开启/关闭]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("modeId", "state")
                        )
                    } else if (block.type is BlockType.OpenApp ||
                        block.type is BlockType.SuspendApps ||
                        block.type is BlockType.UnsuspendApps
                    ) {
                        // 应用操作：一行句子式编辑器（[📱] [选择应用]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            onPickApps = { param -> onPickApps(param) },
                            mainKeys = listOf("packages")
                        )
                    } else if (block.type is BlockType.Repeat ||
                        block.type is BlockType.RepeatCount
                    ) {
                        // 重复：一行句子式编辑器（[🔁] [重复次数]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("count")
                        )
                    } else if (block.type is BlockType.Wait) {
                        // 等待：一行句子式编辑器（[⏳] [秒数]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("seconds")
                        )
                    } else if (block.type is BlockType.CheckTimeRange) {
                        // 时间范围：一行句子式编辑器（[🕐] [开始] [结束]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("start", "end")
                        )
                    } else if (block.type is BlockType.CheckVolumeLevel) {
                        // 音量判断：一行主参数（[🔊] [比较] [音量]），音量类型折叠在展开区
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("operator", "level"),
                            advancedKeys = listOf("stream")
                        )
                    } else if (block.type is BlockType.CheckBrightnessLevel) {
                        // 亮度判断：一行句子式编辑器（[💡] [比较] [亮度]）
                        SentenceChipEditor(
                            block = block,
                            onUpdate = onUpdate,
                            mainKeys = listOf("operator", "level")
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
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 缺口占位放在塌陷容器外层，独立撑开布局
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = childBlock.id,
                                before = true,
                                horizontalPadding = 8.dp
                            )
                            DragCollapseHost(
                                blockId = childBlock.id,
                                dragController = dragController,
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 缺口占位放在塌陷容器外层，独立撑开布局
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = childBlock.id,
                                before = true,
                                horizontalPadding = 8.dp
                            )
                            DragCollapseHost(
                                blockId = childBlock.id,
                                dragController = dragController,
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // 缺口占位放在塌陷容器外层，独立撑开布局
                            DragGapPlaceholder(
                                dragController = dragController,
                                blockId = elseBlock.id,
                                before = true,
                                horizontalPadding = 8.dp
                            )
                            DragCollapseHost(
                                blockId = elseBlock.id,
                                dragController = dragController,
                                modifier = Modifier.fillMaxWidth()
                            ) {
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
    // 松手落位时块快速淡入+轻微回弹缩放，让落位有"落下"的质感而非瞬移；
    // 塌陷过程（isSource 变 true）则用 snap 立即隐藏，避免拖影。
    val appearAlpha by animateFloatAsState(
        targetValue = if (isSource) 0f else 1f,
        animationSpec = if (isSource) snap<Float>() else tween(durationMillis = 180),
        label = "appearAlpha"
    )
    val appearScale by animateFloatAsState(
        targetValue = if (isSource) 0.97f else 1f,
        animationSpec = if (isSource) snap<Float>() else tween(durationMillis = 180),
        label = "appearScale"
    )
    // 仅拖拽源块时约束高度收缩；非拖拽时完全不加约束，
    // 否则 onSizeChanged 与高度约束互相反馈会形成布局振荡（flicker）。
    val collapseModifier = if (isSource) {
        Modifier.height(collapseHeight).clip(RoundedCornerShape(24.dp))
    } else {
        Modifier
    }
    Box(
        modifier = modifier
            .graphicsLayer {
                alpha = appearAlpha
                scaleX = appearScale
                scaleY = appearScale
            }
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
        // 悬停时用 spring 平滑撑开/收拢；松手瞬间（dragging 变 false）用 snap 立即闭合，
        // 避免缺口收缩与块落位叠加出双重高度的回弹
        animationSpec = if (dragging) {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        } else {
            snap()
        },
        label = "dragGap"
    )
    if (height > 0.dp) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = horizontalPadding)
                .height(height)
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








