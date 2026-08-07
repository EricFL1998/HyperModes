package com.banana.hypermodes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.automation.SavedAutomation
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Switch
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import androidx.compose.foundation.background

@Composable
fun AutomationsScreen(
    onBack: () -> Unit,
    showBackButton: Boolean = false,
    showFab: Boolean = true,
    useFloatingLayout: Boolean = false,
    onCreateAutomation: () -> Unit = {},
    onEditAutomation: (SavedAutomation) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    
    // Long press delete state
    var menuAutomation by remember { mutableStateOf<SavedAutomation?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    
    // TODO: 后续从数据库加载
    var automations by remember {
        mutableStateOf(
            listOf(
                SavedAutomation(
                    name = "智能夜间模式",
                    description = "22:00-07:00 自动调节",
                    blocks = emptyList(),
                    enabled = true
                ),
                SavedAutomation(
                    name = "省电模式",
                    description = "电量低于 20%",
                    blocks = emptyList(),
                    enabled = false
                ),
                SavedAutomation(
                    name = "工作模式",
                    description = "工作日 09:00-18:00",
                    blocks = emptyList(),
                    enabled = true
                ),
                SavedAutomation(
                    name = "驾驶模式",
                    description = "连接车载蓝牙时",
                    blocks = emptyList(),
                    enabled = false
                )
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.automations),
                scrollBehavior = scrollBehavior,
                navigationIcon = if (showBackButton) {
                    {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = MiuixIcons.Back,
                                contentDescription = stringResource(R.string.back)
                            )
                        }
                    }
                } else {
                    { }
                }
            )
        },
        floatingActionButton = if (showFab) {
            {
                FloatingActionButton(
                    onClick = onCreateAutomation
                ) {
                    Text(
                        text = "+",
                        fontSize = 32.sp,
                        color = MiuixTheme.colorScheme.onPrimary
                    )
                }
            }
        } else {
            { }
        }
    ) { padding ->
        if (automations.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🤖",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "还没有自动化任务",
                        style = MiuixTheme.textStyles.headline1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "点击右下角 + 创建第一个自动化",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = padding.calculateTopPadding())
            ) {
                // Description text - 和模式列表一样
                Text(
                    text = "根据时间、地点或条件自动执行操作",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp)
                )
                
                // Top spacer - 和模式列表一样
                Spacer(modifier = Modifier.height(12.dp))
                
                // Grid layout - 2 columns like modes list
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                        .scrollEndHaptic()
                        .overScrollVertical()
                        .nestedScroll(scrollBehavior.nestedScrollConnection),
                    contentPadding = PaddingValues(
                        start = 12.dp,
                        end = 12.dp,
                        bottom = if (useFloatingLayout) {
                            88.dp
                        } else {
                            88.dp
                        }
                    ),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(automations) { automation ->
                        AutomationCard(
                            automation = automation,
                            onClick = { onEditAutomation(automation) },
                            onLongPress = {
                                menuAutomation = automation
                                showDeleteConfirm = true
                            },
                            onToggle = { enabled ->
                                automations = automations.map {
                                    if (it.id == automation.id) it.copy(enabled = enabled) else it
                                }
                            }
                        )
                    }
                }
            }
        }
        
        // Delete confirmation dialog for automations
        menuAutomation?.let { automation ->
            top.yukonga.miuix.kmp.overlay.OverlayDialog(
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
                        text = stringResource(R.string.delete),
                        style = MiuixTheme.textStyles.title3,
                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                    )
                    Text(
                        text = "确定要删除「${automation.name}」吗?",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            text = stringResource(android.R.string.cancel),
                            onClick = { showDeleteConfirm = false },
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            text = stringResource(R.string.delete),
                            onClick = {
                                showDeleteConfirm = false
                                // Delete automation
                                automations = automations.filter { it.id != automation.id }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AutomationCard(
    automation: SavedAutomation,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
        insideMargin = PaddingValues(horizontal = 24.dp, vertical = 20.dp),
        cornerRadius = 36.dp,
        onClick = onClick,
        onLongPress = onLongPress
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = automation.name,
                    style = MiuixTheme.textStyles.headline1,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    color = if (automation.enabled) {
                        MiuixTheme.colorScheme.onSurface
                    } else {
                        MiuixTheme.colorScheme.onSurfaceVariantSummary
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (automation.enabled) {
                        "已启用 · ${automation.description}"
                    } else {
                        automation.description
                    },
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Text(
                text = "🤖",
                style = MiuixTheme.textStyles.headline2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}
