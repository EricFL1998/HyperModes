package com.banana.hypermodes.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
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
 * 自动化编辑界面
 */
@Composable
fun AutomationEditorScreen(
    onBack: () -> Unit,
    automationId: String? = null,
    onActionSelected: (AutomationAction) -> Unit = {}
) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<ActionCategory?>(null) }
    var showBottomSheet by remember { mutableStateOf(true) }
    
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MiuixTheme.colorScheme.background)
    ) {
        // 主内容区域
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 顶部标题栏
            TopAppBar(
                title = if (automationId == null) "新建快捷指令" else "编辑快捷指令",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = "返回"
                        )
                    }
                }
            )
            
            // 内容区域
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // 提示文本
                    Text(
                        text = "从下方添加操作以创建快捷指令。",
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 200.dp)
                    )
                }
            }
        }
        
        // 底部操作面板
        AnimatedVisibility(
            visible = showBottomSheet,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            BottomActionSheet(
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                selectedCategory = selectedCategory,
                onCategorySelected = { selectedCategory = if (selectedCategory == it) null else it },
                filteredActions = filteredActions,
                onActionClick = onActionSelected
            )
        }
    }
}

/**
 * 底部操作面板
 */
@Composable
fun BottomActionSheet(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    selectedCategory: ActionCategory?,
    onCategorySelected: (ActionCategory) -> Unit,
    filteredActions: List<AutomationAction>,
    onActionClick: (AutomationAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.65f)
            .clip(RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp))
            .background(MiuixTheme.colorScheme.surface)
    ) {
        // 顶部指示器
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(48.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MiuixTheme.colorScheme.onSurfaceVariantSummary.copy(alpha = 0.3f))
            )
        }
        
        // 搜索框
        SearchField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 分类标签
        CategoryTabs(
            selectedCategory = selectedCategory,
            onCategorySelected = onCategorySelected,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        // 操作列表
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .scrollEndHaptic()
                .overScrollVertical(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            items(filteredActions) { action ->
                ActionItem(
                    action = action,
                    onClick = { onActionClick(action) },
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * 搜索框
 */
@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(16.dp)),
        label = "搜索操作",
        leadingIcon = {
            Text(text = "🔍", fontSize = 20.sp)
        },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Text(text = "🎤", fontSize = 18.sp)
                }
            }
        } else null
    )
}

/**
 * 分类标签
 */
@Composable
fun CategoryTabs(
    selectedCategory: ActionCategory?,
    onCategorySelected: (ActionCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CategoryChip(
            label = "自动化",
            icon = "✓",
            isSelected = selectedCategory == ActionCategory.AUTOMATION,
            onClick = { onCategorySelected(ActionCategory.AUTOMATION) }
        )
        CategoryChip(
            label = "脚本",
            icon = "⚡",
            isSelected = selectedCategory == ActionCategory.SCRIPT,
            onClick = { onCategorySelected(ActionCategory.SCRIPT) }
        )
        CategoryChip(
            label = "控制",
            icon = "🎮",
            isSelected = selectedCategory == ActionCategory.CONTROL,
            onClick = { onCategorySelected(ActionCategory.CONTROL) }
        )
        CategoryChip(
            label = "设备",
            icon = "📱",
            isSelected = selectedCategory == ActionCategory.DEVICE,
            onClick = { onCategorySelected(ActionCategory.DEVICE) }
        )
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
            .clip(RoundedCornerShape(20.dp))
            .background(
                if (isSelected) MiuixTheme.colorScheme.primary.copy(alpha = 0.15f)
                else MiuixTheme.colorScheme.surfaceVariant
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = icon,
                fontSize = 16.sp
            )
            Text(
                text = label,
                style = MiuixTheme.textStyles.body2,
                color = if (isSelected) MiuixTheme.colorScheme.primary
                       else MiuixTheme.colorScheme.onSurface
            )
        }
    }
}

/**
 * 操作项
 */
@Composable
fun ActionItem(
    action: AutomationAction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(action.iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = action.icon,
                    fontSize = 24.sp
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
            
            // 箭头
            Text(
                text = "ⓘ",
                fontSize = 20.sp,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
    }
}
