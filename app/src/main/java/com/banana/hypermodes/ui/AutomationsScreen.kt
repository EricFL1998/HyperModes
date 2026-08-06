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
    var showImportedIntentsDialog by remember { mutableStateOf(false) }
    var importedConfig by remember { mutableStateOf<IntentConfig?>(null) }
    var importedConfigs by remember { mutableStateOf(ImportedIntentStore.loadAll(context)) }

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
                                    text = stringResource(R.string.import_intent_config),
                                    optionSize = 2,
                                    isSelected = false,
                                    index = 0,
                                    onSelectedIndexChange = {
                                        showMenu = false
                                        filePickerLauncher.launch(arrayOf("application/json"))
                                    }
                                )
                                DropdownImpl(
                                    text = stringResource(R.string.view_imported_intents),
                                    optionSize = 2,
                                    isSelected = false,
                                    index = 1,
                                    onSelectedIndexChange = {
                                        showMenu = false
                                        showImportedIntentsDialog = true
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
                    text = stringResource(R.string.import_intent_config_title),
                    style = MiuixTheme.textStyles.headline2
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.import_intent_config_message, importedConfig?.appName ?: ""),
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
                        onClick = { showImportDialog = false },
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        text = "导入",
                        onClick = {
                            importedConfig?.let { config ->
                                ImportedIntentStore.save(context, config)
                                importedConfigs = ImportedIntentStore.loadAll(context)
                            }
                            showImportDialog = false
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary()
                    )
                }
            }
        }
    }

    // Imported intents viewer dialog
    if (showImportedIntentsDialog) {
        top.yukonga.miuix.kmp.overlay.OverlayDialog(
            show = showImportedIntentsDialog,
            onDismissRequest = { showImportedIntentsDialog = false }
        ) {
            Column(
                modifier = Modifier.padding(24.dp)
            ) {
                if (importedConfigs.isEmpty()) {
                    Text(
                        text = stringResource(R.string.no_imported_intents),
                        style = MiuixTheme.textStyles.body1,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                } else {
                    importedConfigs.forEach { config ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = config.appName,
                                    style = MiuixTheme.textStyles.headline2
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                config.intents.forEachIndexed { index, action ->
                                    Text(
                                        text = action.name,
                                        style = MiuixTheme.textStyles.body1,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    if (index != config.intents.lastIndex) {
                                        Spacer(modifier = Modifier.height(12.dp))
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                TextButton(
                    text = stringResource(R.string.done),
                    onClick = { showImportedIntentsDialog = false },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
