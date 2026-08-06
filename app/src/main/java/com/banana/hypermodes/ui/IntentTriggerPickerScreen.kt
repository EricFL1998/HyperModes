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
import com.banana.hypermodes.data.ImportedIntentStore
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Add
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical

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
    val importedConfigs = remember { ImportedIntentStore.loadAll(context) }

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
                    IconButton(
                        onClick = { showAddDialog = true }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.Add,
                            contentDescription = "Add"
                        )
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
            val intentTriggers = editedMode.settings.triggers.filterIsInstance<ModeTrigger.Intent>()
            
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
                            if (trigger.activateAction != null) {
                                Text(
                                    text = stringResource(R.string.intent_activate) + ": ${trigger.activateAction}",
                                    style = MiuixTheme.textStyles.body1
                                )
                            }
                            if (trigger.deactivateAction != null) {
                                if (trigger.activateAction != null) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = stringResource(R.string.intent_deactivate) + ": ${trigger.deactivateAction}",
                                    style = MiuixTheme.textStyles.body1
                                )
                            }
                            if (trigger.packageName != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.intent_package) + ": ${trigger.packageName}",
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                text = stringResource(R.string.delete),
                                onClick = {
                                    val newTriggers = editedMode.settings.triggers - trigger
                                    editedMode = editedMode.copy(
                                        settings = editedMode.settings.copy(triggers = newTriggers)
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
                                    text = config.appName,
                                    style = MiuixTheme.textStyles.headline2
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                config.intents.forEach { action ->
                                    val alreadyAdded = editedMode.settings.triggers.any { t ->
                                        t is ModeTrigger.Intent &&
                                            t.packageName == config.packageName &&
                                            action.intents.any { it == t.activateAction || it == t.deactivateAction }
                                    }
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable(enabled = !alreadyAdded) {
                                                val newTrigger = ModeTrigger.Intent(
                                                    activateAction = action.intents.firstOrNull()?.takeIf { it.isNotBlank() },
                                                    deactivateAction = action.intents.getOrNull(1)?.takeIf { it.isNotBlank() },
                                                    packageName = config.packageName
                                                )
                                                val newTriggers = editedMode.settings.triggers + newTrigger
                                                editedMode = editedMode.copy(
                                                    settings = editedMode.settings.copy(triggers = newTriggers)
                                                )
                                                onSave(editedMode)
                                            }
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
                            val newTriggers = editedMode.settings.triggers + newTrigger
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(triggers = newTriggers)
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
