package com.banana.hypermodes.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeTrigger
import com.banana.hypermodes.data.ModeTriggerGroup
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.banana.hypermodes.data.ImportedIntentStore
import com.banana.hypermodes.data.IntentConfig
import kotlinx.serialization.json.Json
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun IntentTriggerPickerScreen(
    mode: Mode,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
    val context = LocalContext.current
    var editedMode by remember { mutableStateOf(mode) }
    var showAddDialog by remember { mutableStateOf(false) }
    var activateActionInput by remember { mutableStateOf("") }
    var deactivateActionInput by remember { mutableStateOf("") }
    var packageInput by remember { mutableStateOf("") }
    var importedConfigs by remember { mutableStateOf(ImportedIntentStore.loadAll(context)) }
    var showAddOptions by remember { mutableStateOf(false) }
    var importedConfig by remember { mutableStateOf<IntentConfig?>(null) }
    var showImportDialog by remember { mutableStateOf(false) }
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
                    importedConfig = config
                    showImportDialog = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val scrollBehavior = MiuixScrollBehavior(top.yukonga.miuix.kmp.basic.rememberTopAppBarState())

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.trigger_intent),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(
                        onClick = onBack
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showAddOptions = true }) {
                        Icon(
                            imageVector = MiuixIcons.Add,
                            contentDescription = "Add"
                        )
                    }
                    WindowListPopup(
                        show = showAddOptions,
                        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                        alignment = PopupPositionProvider.Align.TopEnd,
                        onDismissRequest = { showAddOptions = false }
                    ) {
                        ListPopupColumn {
                            DropdownImpl(
                                text = stringResource(R.string.add_intent_trigger),
                                optionSize = 2,
                                isSelected = false,
                                index = 0,
                                onSelectedIndexChange = {
                                    showAddOptions = false
                                    showAddDialog = true
                                }
                            )
                            DropdownImpl(
                                text = stringResource(R.string.import_intent_config),
                                optionSize = 2,
                                isSelected = false,
                                index = 1,
                                onSelectedIndexChange = {
                                    showAddOptions = false
                                    filePickerLauncher.launch(arrayOf("application/json"))
                                }
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .overScrollVertical(),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp)
        ) {
            // Show existing intent triggers
            val intentTriggers = editedMode.settings.triggerGroups
                .filterIsInstance<ModeTriggerGroup.Single>()
                .map { it.trigger }
                .filterIsInstance<ModeTrigger.Intent>()

            @Composable
            fun resolveIntentName(trigger: ModeTrigger.Intent): String {
                importedConfigs.forEach { config ->
                    if (config.packageName == trigger.packageName) {
                        config.intents.forEach { action ->
                            if (action.intents.any { it == trigger.activateAction || it == trigger.deactivateAction }) {
                                return action.name
                            }
                        }
                    }
                }
                return trigger.activateAction ?: stringResource(R.string.trigger_intent)
            }
            
            if (intentTriggers.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.trigger_intent_desc),
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }

            intentTriggers.forEach { trigger ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = resolveIntentName(trigger),
                                style = MiuixTheme.textStyles.body1
                            )
                            if (trigger.packageName != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.intent_package) + ": " + trigger.packageName,
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                text = stringResource(R.string.delete),
                                onClick = {
                                    val newGroups = editedMode.settings.triggerGroups.filterNot {
                                        it is ModeTriggerGroup.Single && it.trigger == trigger
                                    }
                                    editedMode = editedMode.copy(
                                        settings = editedMode.settings.copy(triggerGroups = newGroups)
                                    )
                                    onSave(editedMode)
                                },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // Imported intent configs from the automations screen
            if (importedConfigs.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.imported_intents_title),
                        style = MiuixTheme.textStyles.title2,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }
                importedConfigs.forEach { config ->
                    val missingIntents = config.intents.filter { action ->
                        editedMode.settings.triggerGroups.none { group ->
                            group is ModeTriggerGroup.Single &&
                                group.trigger is ModeTrigger.Intent &&
                                group.trigger.packageName == config.packageName &&
                                action.intents.any { it == group.trigger.activateAction || it == group.trigger.deactivateAction }
                        }
                    }
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable(enabled = missingIntents.isNotEmpty()) {
                                    // Add all not-yet-added intents from this config at once
                                    val newTriggers = missingIntents.map { action ->
                                        ModeTrigger.Intent(
                                            activateAction = action.intents.firstOrNull()?.takeIf { it.isNotBlank() },
                                            deactivateAction = action.intents.getOrNull(1)?.takeIf { it.isNotBlank() },
                                            packageName = config.packageName
                                        )
                                    }
                                    val newGroups = newTriggers.map { ModeTriggerGroup.Single(it) }
                                    val combined = editedMode.settings.triggerGroups + newGroups
                                    editedMode = editedMode.copy(
                                        settings = editedMode.settings.copy(triggerGroups = combined)
                                    )
                                    onSave(editedMode)
                                }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = config.appName,
                                    style = MiuixTheme.textStyles.headline2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                config.intents.forEach { action ->
                                    val alreadyAdded = editedMode.settings.triggerGroups.any { group ->
                                        group is ModeTriggerGroup.Single &&
                                            group.trigger is ModeTrigger.Intent &&
                                            group.trigger.packageName == config.packageName &&
                                            action.intents.any { it == group.trigger.activateAction || it == group.trigger.deactivateAction }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = action.name,
                                            style = MiuixTheme.textStyles.body1,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = if (alreadyAdded) {
                                                stringResource(R.string.already_added)
                                            } else {
                                                "+"
                                            },
                                            style = MiuixTheme.textStyles.body2,
                                            color = if (alreadyAdded) {
                                                MiuixTheme.colorScheme.onSurfaceVariantSummary
                                            } else {
                                                MiuixTheme.colorScheme.primary
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
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
                        text = stringResource(R.string.import_confirmed),
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

    // Add intent dialog
    OverlayDialog(
        show = showAddDialog,
        onDismissRequest = {
            showAddDialog = false
            activateActionInput = ""
            deactivateActionInput = ""
            packageInput = ""
        },
        title = stringResource(R.string.add_intent_trigger)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.intent_activate_desc),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            top.yukonga.miuix.kmp.basic.TextField(
                value = activateActionInput,
                onValueChange = { activateActionInput = it },
                label = stringResource(R.string.intent_activate),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.intent_deactivate_desc),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            top.yukonga.miuix.kmp.basic.TextField(
                value = deactivateActionInput,
                onValueChange = { deactivateActionInput = it },
                label = stringResource(R.string.intent_deactivate),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            top.yukonga.miuix.kmp.basic.TextField(
                value = packageInput,
                onValueChange = { packageInput = it },
                label = stringResource(R.string.intent_package) + " (" + stringResource(R.string.optional) + ")",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(
                    text = stringResource(R.string.cancel),
                    onClick = {
                        showAddDialog = false
                        activateActionInput = ""
                        deactivateActionInput = ""
                        packageInput = ""
                    },
                    modifier = Modifier.weight(1f)
                )
                TextButton(
                    text = stringResource(R.string.add),
                    onClick = {
                        // At least one action must be defined
                        if (activateActionInput.isNotBlank() || deactivateActionInput.isNotBlank()) {
                            val newTrigger = ModeTrigger.Intent(
                                activateAction = activateActionInput.takeIf { it.isNotBlank() }?.trim(),
                                deactivateAction = deactivateActionInput.takeIf { it.isNotBlank() }?.trim(),
                                packageName = packageInput.takeIf { it.isNotBlank() }?.trim()
                            )
                            val newGroups = editedMode.settings.triggerGroups + ModeTriggerGroup.Single(newTrigger)
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(triggerGroups = newGroups)
                            )
                            onSave(editedMode)
                            showAddDialog = false
                            activateActionInput = ""
                            deactivateActionInput = ""
                            packageInput = ""
                        }
                    },
                    enabled = activateActionInput.isNotBlank() || deactivateActionInput.isNotBlank(),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
