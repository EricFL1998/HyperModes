package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.automation.AutomationCatalog
import com.banana.hypermodes.automation.AutomationCatalog.Category
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * 自动化操作选择界面 - 全屏 Picker 风格，支持搜索
 */
@Composable
fun AutomationActionPickerScreen(
    onBack: () -> Unit,
    onActionSelected: (AutomationAction) -> Unit
) {
    BackHandler(onBack = onBack)
    var searchQuery by remember { mutableStateOf("") }

    // 目录中的全部操作
    val allActions = remember {
        AutomationCatalog.entries.map { entry ->
            AutomationAction(
                id = entry.id,
                name = entry.name,
                icon = entry.icon,
                iconColor = entry.iconColor,
                description = entry.description
            )
        }
    }

    // 根据搜索过滤
    val filteredActions = remember(searchQuery, allActions) {
        if (searchQuery.isEmpty()) {
            allActions
        } else {
            allActions.filter { action ->
                action.name.contains(searchQuery, ignoreCase = true) ||
                        action.description.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    // 分组显示
    val groupedActions = remember(filteredActions, searchQuery) {
        buildList {
            if (searchQuery.isEmpty()) {
                // 未搜索时分组显示
                AutomationCatalog.grouped().forEach { (category, entries) ->
                    val categoryActions = entries.map { entry ->
                        AutomationAction(
                            id = entry.id,
                            name = entry.name,
                            icon = entry.icon,
                            iconColor = entry.iconColor,
                            description = entry.description
                        )
                    }
                    add(category.label to categoryActions.filter { it in filteredActions })
                }
            } else {
                // 搜索时不分组
                add("搜索结果" to filteredActions)
            }
        }.filter { it.second.isNotEmpty() }
    }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "添加操作",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding())
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // 搜索框
            item {
                SearchBar(
                    modifier = Modifier.padding(bottom = 12.dp),
                    inputField = {
                        InputField(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it },
                            onSearch = { },
                            expanded = false,
                            onExpandedChange = { },
                            label = "搜索操作"
                        )
                    },
                    expanded = false,
                    onExpandedChange = { }
                ) { }
            }

            // 分组操作列表
            groupedActions.forEach { (groupName, actions) ->
                if (searchQuery.isEmpty()) {
                    item {
                        SmallTitle(
                            text = groupName,
                            modifier = Modifier.padding(start = 28.dp, top = 8.dp, bottom = 8.dp)
                        )
                    }
                }

                items(actions, key = { it.id }) { action ->
                    AutomationActionCard(
                        action = action,
                        onClick = {
                            onActionSelected(action)
                            onBack()
                        }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AutomationActionCard(
    action: AutomationAction,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标背景
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(action.iconColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 24.sp
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
                    Spacer(modifier = Modifier.height(2.dp))
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
