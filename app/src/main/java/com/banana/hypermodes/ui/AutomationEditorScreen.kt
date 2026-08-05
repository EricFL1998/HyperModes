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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import androidx.compose.foundation.background

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
    
    // 常用控制项（优先显示）
    val commonActions = listOf(
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
    
    // 根据搜索过滤
    val filteredActions = remember(searchQuery) {
        if (searchQuery.isEmpty()) {
            commonActions
        } else {
            commonActions.filter { action ->
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
