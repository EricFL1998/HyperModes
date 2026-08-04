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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import androidx.compose.foundation.background

/**
 * 自动化操作分类
 */
enum class ActionCategory {
    AUTOMATION,  // 自动化
    SCRIPT,      // 脚本
    CONTROL,     // 控制
    DEVICE       // 设备
}

/**
 * 自动化操作项
 */
data class AutomationAction(
    val id: String,
    val name: String,
    val icon: String,
    val iconColor: Color,
    val category: ActionCategory,
    val description: String = ""
)

/**
 * 获取预定义的操作列表
 */
@Composable
fun getAutomationActions(): List<AutomationAction> {
    return listOf(
        // 自动化类别
        AutomationAction(
            id = "send_message",
            name = "发送信息",
            icon = "💬",
            iconColor = Color(0xFF4CD964),
            category = ActionCategory.AUTOMATION
        ),
        AutomationAction(
            id = "open_app",
            name = "打开 App",
            icon = "📱",
            iconColor = Color(0xFF5E5CE6),
            category = ActionCategory.AUTOMATION
        ),
        AutomationAction(
            id = "play_music",
            name = "播放音乐",
            icon = "🎵",
            iconColor = Color(0xFFFF2D55),
            category = ActionCategory.AUTOMATION
        ),
        AutomationAction(
            id = "note",
            name = "备忘录",
            icon = "📝",
            iconColor = Color(0xFFFFCC00),
            category = ActionCategory.AUTOMATION
        ),
        
        // 脚本类别
        AutomationAction(
            id = "run_script",
            name = "运行脚本",
            icon = "⚙️",
            iconColor = Color(0xFF8E8E93),
            category = ActionCategory.SCRIPT
        ),
        AutomationAction(
            id = "shell_command",
            name = "Shell 命令",
            icon = "💻",
            iconColor = Color(0xFF007AFF),
            category = ActionCategory.SCRIPT
        ),
        
        // 控制类别
        AutomationAction(
            id = "enable_mode",
            name = "启用模式",
            icon = "🌙",
            iconColor = Color(0xFF5E5CE6),
            category = ActionCategory.CONTROL
        ),
        AutomationAction(
            id = "disable_mode",
            name = "关闭模式",
            icon = "☀️",
            iconColor = Color(0xFFFF9500),
            category = ActionCategory.CONTROL
        ),
        AutomationAction(
            id = "set_dnd",
            name = "设置勿扰",
            icon = "🔕",
            iconColor = Color(0xFFFF3B30),
            category = ActionCategory.CONTROL
        ),
        
        // 设备类别
        AutomationAction(
            id = "toggle_wifi",
            name = "WiFi 开关",
            icon = "📶",
            iconColor = Color(0xFF007AFF),
            category = ActionCategory.DEVICE
        ),
        AutomationAction(
            id = "toggle_bluetooth",
            name = "蓝牙开关",
            icon = "🔵",
            iconColor = Color(0xFF007AFF),
            category = ActionCategory.DEVICE
        ),
        AutomationAction(
            id = "adjust_volume",
            name = "调节音量",
            icon = "🔊",
            iconColor = Color(0xFFFF9500),
            category = ActionCategory.DEVICE
        ),
        AutomationAction(
            id = "adjust_brightness",
            name = "调节亮度",
            icon = "💡",
            iconColor = Color(0xFFFFCC00),
            category = ActionCategory.DEVICE
        )
    )
}

/**
 * 自动化操作选择对话框（底部弹出）
 */
@Composable
fun AutomationActionDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onActionSelected: (AutomationAction) -> Unit = {}
) {
    android.util.Log.d("HyperModes", "AutomationActionDialog called, show=$show")
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ActionCategory?>(null) }
    
    val allActions = getAutomationActions()
    
    // 根据搜索和分类过滤操作
    val filteredActions = remember(searchQuery, selectedCategory) {
        allActions.filter { action ->
            val matchesSearch = searchQuery.isEmpty() || 
                action.name.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == null || 
                action.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    top.yukonga.miuix.kmp.overlay.OverlayBottomSheet(
        show = show,
        onDismissRequest = onDismiss,
        title = "添加操作"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            // 搜索框
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 12.dp)
                    .height(48.dp),
                label = "搜索操作",
                leadingIcon = {
                    Text(text = "🔍", fontSize = 18.sp)
                }
            )
            
            // 分类标签
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CategoryChip(
                    label = "自动化",
                    icon = "✓",
                    isSelected = selectedCategory == ActionCategory.AUTOMATION,
                    onClick = { 
                        selectedCategory = if (selectedCategory == ActionCategory.AUTOMATION) null 
                                          else ActionCategory.AUTOMATION 
                    }
                )
                CategoryChip(
                    label = "脚本",
                    icon = "⚡",
                    isSelected = selectedCategory == ActionCategory.SCRIPT,
                    onClick = { 
                        selectedCategory = if (selectedCategory == ActionCategory.SCRIPT) null 
                                          else ActionCategory.SCRIPT 
                    }
                )
                CategoryChip(
                    label = "控制",
                    icon = "🎮",
                    isSelected = selectedCategory == ActionCategory.CONTROL,
                    onClick = { 
                        selectedCategory = if (selectedCategory == ActionCategory.CONTROL) null 
                                          else ActionCategory.CONTROL 
                    }
                )
                CategoryChip(
                    label = "设备",
                    icon = "📱",
                    isSelected = selectedCategory == ActionCategory.DEVICE,
                    onClick = { 
                        selectedCategory = if (selectedCategory == ActionCategory.DEVICE) null 
                                          else ActionCategory.DEVICE 
                    }
                )
            }
            
            // 操作列表
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
                            .padding(horizontal = 28.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 图标
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(action.iconColor.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = action.icon,
                                fontSize = 22.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(16.dp))
                        
                        // 名称
                        Text(
                            text = action.name,
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.onSurface,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                
                // 如果没有结果
                if (filteredActions.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "没有找到匹配的操作",
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

/**
 * 分类标签芯片
 */
@Composable
fun CategoryChip(
    label: String,
    icon: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                if (isSelected) MiuixTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MiuixTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = icon,
                fontSize = 14.sp
            )
            Text(
                text = label,
                style = MiuixTheme.textStyles.body2,
                fontSize = 13.sp,
                color = if (isSelected) MiuixTheme.colorScheme.primary
                       else MiuixTheme.colorScheme.onSurface
            )
        }
    }
}
