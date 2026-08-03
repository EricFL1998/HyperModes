package com.banana.hypermodes.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.*
import kotlinx.serialization.json.Json
import top.yukonga.miuix.kmp.basic.*

import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun AutomationsScreen(
    onBack: () -> Unit,
    showBackButton: Boolean = false,
    showFab: Boolean = true,
    useFloatingLayout: Boolean = false
) {
    val context = LocalContext.current
    val scrollBehavior = MiuixScrollBehavior()
    var showMenu by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importedConfig by remember { mutableStateOf<IntentConfig?>(null) }

    val json = remember {
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }
    }

    // Use the new file picker (ActivityResultContracts.OpenDocument)
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val content = inputStream?.bufferedReader()?.use { reader -> reader.readText() }
                if (content != null) {
                    val config = json.decodeFromString<IntentConfig>(content)
                    importedConfig = config
                    showImportDialog = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // TODO: Show error toast
            }
        }
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
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(
                                imageVector = MiuixIcons.More,
                                contentDescription = "More options"
                            )
                        }
                        WindowListPopup(
                            show = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            ListPopupColumn {
                                DropdownImpl(
                                    text = "Import Intent Config",
                                    optionSize = 1,
                                    isSelected = false,
                                    index = 0,
                                    onSelectedIndexChange = {
                                        showMenu = false
                                        filePickerLauncher.launch(arrayOf("application/json"))
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = if (showFab) {
            {
                FloatingActionButton(
                    onClick = {
                        // TODO: Add automation creation logic
                    }
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = if (useFloatingLayout) {
                PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = BottomLayoutGeometry.contentBottomPadding().calculateBottomPadding()
                )
            } else {
                PaddingValues(top = padding.calculateTopPadding())
            }
        ) {
            // Description text
            item {
                Text(
                    text = "自动化功能即将推出...",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 28.dp, vertical = 18.dp)
                )
            }

            // Show imported config preview
            importedConfig?.let { config ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp, vertical = 12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Imported: ${config.appName}",
                                style = MiuixTheme.textStyles.headline2
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Package: ${config.packageName}",
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            config.intents.forEach { action ->
                                Text(
                                    text = "• ${action.name}",
                                    style = MiuixTheme.textStyles.body1
                                )
                                action.intents.forEach { intent ->
                                    Text(
                                        text = "  - $intent",
                                        style = MiuixTheme.textStyles.body2,
                                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Placeholder content
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 100.dp),
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
                            text = "自动化",
                            style = MiuixTheme.textStyles.headline1,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "根据时间、地点或活动自动触发模式",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }

            // Bottom spacer
            item {
                if (!useFloatingLayout) {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }

    // Import confirmation dialog
    if (showImportDialog && importedConfig != null) {
        top.yukonga.miuix.kmp.overlay.OverlayDialog(
            show = showImportDialog,
            onDismissRequest = { showImportDialog = false }
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Import Intent Config",
                    style = MiuixTheme.textStyles.headline2
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Import automation for ${importedConfig?.appName}?",
                    style = MiuixTheme.textStyles.body1
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "This will create a new mode with intent triggers based on the app's broadcast actions.",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    TextButton(
                        text = "Cancel",
                        onClick = { showImportDialog = false },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        text = "Import",
                        onClick = {
                            // Create a new mode with intent triggers
                            importedConfig?.let { config ->
                                val modeStore = ModeStore
                                val currentModes = modeStore.load(context) { emptyList() }
                                
                                // Generate unique mode ID
                                val modeId = "intent_${config.packageName}_${System.currentTimeMillis()}"
                                
                                // Create intent triggers from the config
                                val triggers = config.intents.flatMap { intentAction ->
                                    intentAction.intents.map { action ->
                                        ModeTrigger.Intent(
                                            actions = setOf(action),
                                            packageName = config.packageName
                                        )
                                    }
                                }
                                
                                // Create new mode
                                val newMode = Mode(
                                    id = modeId,
                                    name = config.appName,
                                    icon = "music_note",
                                    description = "Auto-imported from ${config.appName}",
                                    enabled = false,
                                    settings = ModeSettings(
                                        triggers = triggers
                                    )
                                )
                                
                                // Add to mode list and save
                                val updatedModes = currentModes + newMode
                                modeStore.save(context, updatedModes)
                                
                                showImportDialog = false
                                // TODO: Show success toast
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
    }
}
