package com.banana.hypermodes.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.banana.hypermodes.automation.AutomationStore
import com.banana.hypermodes.automation.AutomationExecutor
import com.banana.hypermodes.data.ImportedIntentStore
import com.banana.hypermodes.data.IntentConfig
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Switch
import kotlinx.serialization.json.Json
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.icon.extended.Play
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowBottomSheet
import top.yukonga.miuix.kmp.window.WindowListPopup
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.aspectRatio

@Composable
fun AutomationsScreen(
    onBack: () -> Unit,
    showBackButton: Boolean = false,
    showFab: Boolean = true,
    useFloatingLayout: Boolean = false,
    onCreateAutomation: () -> Unit = {},
    onEditAutomation: (SavedAutomation) -> Unit = {},
    refreshTrigger: Int = 0,
    onRefreshNeeded: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    val executor = remember { AutomationExecutor(context) }
    val coroutineScope = rememberCoroutineScope()
    
    // Long press delete state
    var menuAutomation by remember { mutableStateOf<SavedAutomation?>(null) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    // Overflow menu (⋮): import intent configs / view imported intents
    var showOverflowMenu by remember { mutableStateOf(false) }
    var importedConfigs by remember { mutableStateOf(ImportedIntentStore.loadAll(context)) }
    var showImportedIntentsDialog by remember { mutableStateOf(false) }
    var pendingImport by remember { mutableStateOf<IntentConfig?>(null) }
    var showImportConfirm by remember { mutableStateOf(false) }

    val json = remember {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val fileContent = inputStream?.bufferedReader()?.use { reader -> reader.readText() }
                if (fileContent != null) {
                    val config = json.decodeFromString<IntentConfig>(fileContent)
                    pendingImport = config
                    showImportConfirm = true
                }
            } catch (e: Exception) {
                Toast.makeText(context, R.string.import_error, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    // 从存储加载自动化列表
    var automations by remember {
        mutableStateOf(AutomationStore.load(context))
    }
    
    // 监听 refreshTrigger 变化并重新加载
    LaunchedEffect(refreshTrigger) {
        automations = AutomationStore.load(context)
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
                },
                actions = {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = null
                        )
                    }
                    WindowListPopup(
                        show = showOverflowMenu,
                        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                        alignment = PopupPositionProvider.Align.TopEnd,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        ListPopupColumn {
                            DropdownImpl(
                                text = stringResource(R.string.import_intent_config),
                                optionSize = 2,
                                isSelected = false,
                                index = 0,
                                onSelectedIndexChange = {
                                    showOverflowMenu = false
                                    filePickerLauncher.launch(arrayOf("application/json"))
                                }
                            )
                            DropdownImpl(
                                text = stringResource(R.string.view_imported_intents),
                                optionSize = 2,
                                isSelected = false,
                                index = 1,
                                onSelectedIndexChange = {
                                    showOverflowMenu = false
                                    importedConfigs = ImportedIntentStore.loadAll(context)
                                    showImportedIntentsDialog = true
                                }
                            )
                        }
                    }
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
                            onRun = {
                                coroutineScope.launch {
                                    val result = executor.execute(automation.blocks)
                                    Toast.makeText(
                                        context,
                                        if (result.success) "✅ ${result.message}"
                                        else "❌ ${result.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            onLongPress = {
                                menuAutomation = automation
                                showDeleteConfirm = true
                            },
                            onToggle = { enabled ->
                                val updated = automation.copy(enabled = enabled)
                                AutomationStore.update(context, updated)
                                automations = AutomationStore.load(context)
                                onRefreshNeeded()
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
                                // 从存储中删除自动化
                                AutomationStore.delete(context, automation.id)
                                automations = AutomationStore.load(context)
                                onRefreshNeeded()
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        }

        // Import intent config confirmation dialog
        if (showImportConfirm && pendingImport != null) {
            OverlayDialog(
                show = showImportConfirm,
                onDismissRequest = { showImportConfirm = false }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.import_intent_config_title),
                        style = MiuixTheme.textStyles.headline2
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(
                            R.string.import_intent_config_message,
                            pendingImport?.appName ?: ""
                        ),
                        style = MiuixTheme.textStyles.body1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.import_intent_config_desc),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TextButton(
                            text = stringResource(R.string.cancel),
                            onClick = {
                                showImportConfirm = false
                                pendingImport = null
                            },
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(
                            text = stringResource(R.string.import_confirmed),
                            onClick = {
                                pendingImport?.let { config ->
                                    ImportedIntentStore.save(context, config)
                                    importedConfigs = ImportedIntentStore.loadAll(context)
                                }
                                showImportConfirm = false
                                pendingImport = null
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.textButtonColorsPrimary()
                        )
                    }
                }
            }
        }

        // View imported intents dialog (app name + intent names only)
        if (showImportedIntentsDialog) {
            OverlayDialog(
                show = showImportedIntentsDialog,
                onDismissRequest = { showImportedIntentsDialog = false },
                title = stringResource(R.string.imported_intents_title)
            ) {
                Column {
                    if (importedConfigs.isEmpty()) {
                        Text(
                            text = stringResource(R.string.no_imported_intents),
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 400.dp)
                        ) {
                            items(importedConfigs.size) { configIndex ->
                                val config = importedConfigs[configIndex]
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = config.appName,
                                        style = MiuixTheme.textStyles.body1,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                                    )
                                    config.intents.forEach { action ->
                                        Text(
                                            text = action.name,
                                            style = MiuixTheme.textStyles.body2,
                                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                            modifier = Modifier.padding(start = 12.dp, top = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        text = stringResource(R.string.cancel),
                        onClick = { showImportedIntentsDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    )
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
    onToggle: (Boolean) -> Unit,
    onRun: () -> Unit = {}
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = automation.name,
                    style = MiuixTheme.textStyles.headline1,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
                )
            }
            // 右上角运行按钮
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MiuixTheme.colorScheme.primary.copy(alpha = 0.12f))
                    .clickable(onClick = onRun),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MiuixIcons.Play,
                    contentDescription = "运行",
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = automation.icon,
                style = MiuixTheme.textStyles.headline2,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

// Automation icon choices
val AUTOMATION_ICON_CHOICES = listOf(
    "🤖", "⚡", "🔄", "⏰", "📍", "🎯", "🔔", "🌙",
    "☀️", "🏠", "🚗", "💼", "📱", "🎵", "🔊", "💡",
    "📶", "🔵", "🔕", "🔋", "⚙️", "🎮", "📖", "🛠️",
    "🎯", "🎪", "🎨", "🧪", "🔬", "🎬"
)

@Composable
fun CreateAutomationDialog(
    show: Boolean,
    initialName: String = "",
    initialIcon: String = "🤖",
    onDismissRequest: () -> Unit,
    onDone: (name: String, icon: String) -> Unit
) {
    val context = LocalContext.current
    val focusManager = androidx.compose.ui.platform.LocalFocusManager.current
    
    var name by remember(show) { mutableStateOf(initialName) }
    var icon by remember(show) { mutableStateOf(initialIcon) }

    WindowBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.create_automation)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Big icon preview
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(RoundedCornerShape(48.dp))
                                .background(MiuixTheme.colorScheme.secondaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = icon, fontSize = 44.sp)
                        }
                    }
                }

                // Name field
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        SmallTitle(
                            text = stringResource(R.string.automation_name),
                            modifier = Modifier.padding(bottom = 12.dp),
                            insideMargin = PaddingValues(0.dp)
                        )
                    }
                }
                item {
                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .padding(bottom = 24.dp),
                        label = "新建自动化",
                        useLabelAsPlaceholder = true,
                        singleLine = true,
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                            imeAction = androidx.compose.ui.text.input.ImeAction.Done
                        ),
                        keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }

                // Icon grid
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        SmallTitle(
                            text = stringResource(R.string.choose_icon),
                            modifier = Modifier.padding(bottom = 12.dp),
                            insideMargin = PaddingValues(0.dp)
                        )
                    }
                }
                
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
                    ) {
                        AUTOMATION_ICON_CHOICES.chunked(5).forEach { rowChoices ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowChoices.forEach { choice ->
                                    val selected = icon == choice
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(6.dp)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(24.dp))
                                            .background(
                                                if (selected) MiuixTheme.colorScheme.primary
                                                else MiuixTheme.colorScheme.secondaryContainer
                                            )
                                            .clickable { icon = choice },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = choice,
                                            fontSize = 28.sp,
                                            color = if (selected) MiuixTheme.colorScheme.onPrimary
                                            else MiuixTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // 完成 button at bottom
            TextButton(
                text = stringResource(R.string.done),
                onClick = {
                    val finalName = name.ifBlank { "新建自动化" }
                    onDone(finalName, icon)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
        }
    }
}
