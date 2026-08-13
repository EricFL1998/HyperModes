package com.banana.hypermodes.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.automation.AutomationCatalog
import com.banana.hypermodes.data.ImportedIntentStore
import com.banana.hypermodes.data.IntentConfig
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.InputField
import top.yukonga.miuix.kmp.basic.SearchBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
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
        title = stringResource(R.string.select_action),
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
                        label = stringResource(R.string.search_all_actions_hint)
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
                        label = stringResource(R.string.category_all),
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
            val allActions = AutomationCatalog.visibleEntries
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
