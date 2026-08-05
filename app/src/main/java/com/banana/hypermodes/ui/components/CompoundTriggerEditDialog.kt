package com.banana.hypermodes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.Close
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Dialog for editing a compound trigger (multiple triggers with AND logic)
 */
@Composable
fun CompoundTriggerEditDialog(
    show: Boolean,
    initialTriggers: List<ComplexTrigger>,
    initialName: String?,
    onDismissRequest: () -> Unit,
    onConfirm: (triggers: List<ComplexTrigger>, name: String?) -> Unit,
    onAddTrigger: () -> Unit
) {
    var triggers by remember(initialTriggers) { mutableStateOf(initialTriggers) }
    var name by remember(initialName) { mutableStateOf(initialName ?: "") }

    OverlayDialog(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.compound_trigger)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 400.dp)
        ) {
            // Name input
            item {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = stringResource(R.string.compound_trigger_name),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                )
            }

            // Info text
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    color = MiuixTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = "⚡ ${stringResource(R.string.trigger_group_and_logic)}",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            // Trigger list
            items(triggers) { trigger ->
                TriggerItemCard(
                    trigger = trigger,
                    onRemove = {
                        triggers = triggers - trigger
                    }
                )
            }

            // Add trigger button
            item {
                TextButton(
                    text = "+ ${stringResource(R.string.add_trigger_to_group)}",
                    onClick = onAddTrigger,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }

            // Action buttons
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(
                        text = stringResource(R.string.cancel),
                        onClick = onDismissRequest,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(
                        text = stringResource(R.string.confirm),
                        onClick = {
                            if (triggers.isNotEmpty()) {
                                onConfirm(triggers, name.takeIf { it.isNotBlank() })
                            }
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        enabled = triggers.isNotEmpty()
                    )
                }
            }
        }
    }
}

@Composable
private fun TriggerItemCard(
    trigger: ComplexTrigger,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = getTriggerTitle(trigger),
                    style = MiuixTheme.textStyles.body1
                )
                Text(
                    text = getTriggerDescription(trigger),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Close,
                    contentDescription = "Remove",
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

@Composable
private fun getTriggerTitle(trigger: ComplexTrigger): String {
    return when (trigger) {
        is ComplexTrigger.Time -> stringResource(R.string.trigger_time)
        is ComplexTrigger.App -> stringResource(R.string.trigger_app)
        is ComplexTrigger.Wifi -> stringResource(R.string.trigger_wifi)
        is ComplexTrigger.Bluetooth -> stringResource(R.string.trigger_bluetooth)
        is ComplexTrigger.Music -> stringResource(R.string.trigger_music)
        is ComplexTrigger.Location -> stringResource(R.string.trigger_location)
        is ComplexTrigger.Intent -> stringResource(R.string.trigger_intent)
    }
}

@Composable
private fun getTriggerDescription(trigger: ComplexTrigger): String {
    return when (trigger) {
        is ComplexTrigger.Time -> "${trigger.startTime} - ${trigger.endTime}"
        is ComplexTrigger.App -> trigger.packageNames.joinToString(", ").take(50)
        is ComplexTrigger.Wifi -> trigger.ssids.joinToString(", ")
        is ComplexTrigger.Bluetooth -> {
            if (trigger.matchAnyCarAudio) "任意车载蓝牙"
            else trigger.deviceAddresses.joinToString(", ").take(30)
        }
        is ComplexTrigger.Music -> "播放音乐时"
        is ComplexTrigger.Location -> trigger.addressName ?: "${trigger.latitude}, ${trigger.longitude}"
        is ComplexTrigger.Intent -> trigger.activateAction ?: "Intent trigger"
    }
}
