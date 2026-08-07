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

    // 常用系统控制
    val systemActions = remember {
        listOf(
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
    }

    // 控制流
    val controlFlowActions = remember {
        listOf(
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
    }

    // 逻辑运算
    val logicActions = remember {
        listOf(
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
    }

    // 条件判断
    val conditionActions = remember {
        listOf(
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
    }

    val allActions = systemActions + controlFlowActions + logicActions + conditionActions

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
                add("系统控制" to filteredActions.filter { it in systemActions })
                add("控制流" to filteredActions.filter { it in controlFlowActions })
                add("逻辑运算" to filteredActions.filter { it in logicActions })
                add("条件判断" to filteredActions.filter { it in conditionActions })
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
                            label = "搜索操作..."
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
