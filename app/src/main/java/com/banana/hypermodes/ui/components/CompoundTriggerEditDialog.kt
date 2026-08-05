package com.banana.hypermodes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ModeTrigger
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
    initialTriggers: List<ModeTrigger>,
    initialName: String?,
    onDismissRequest: () -> Unit,
    onConfirm: (triggers: List<ModeTrigger>, name: String?) -> Unit,
    onAddTrigger: () -> Unit
) {
    var triggers by remember { mutableStateOf(initialTriggers) }
    var name by remember(initialName) { mutableStateOf(initialName ?: "") }


    // Sync triggers when initialTriggers changes
    LaunchedEffect(initialTriggers) {
        triggers = initialTriggers
    }
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
                        text = stringResource(R.string.done),
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
    trigger: ModeTrigger,
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
            Text(
                text = getTriggerDescription(trigger),
                style = MiuixTheme.textStyles.body1,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = MiuixIcons.Basic.Close,
                    contentDescription = "Remove"
                )
            }
        }
    }
}



@Composable
private fun getTriggerDescription(trigger: ModeTrigger): String {
    val context = LocalContext.current
    
    return when (trigger) {
        is ModeTrigger.Time -> {
            val start = "%02d:%02d".format(trigger.schedule.startHour, trigger.schedule.startMinute)
            val end = "%02d:%02d".format(trigger.schedule.endHour, trigger.schedule.endMinute)
            "$start - $end"
        }
        is ModeTrigger.App -> {
            val pm = context.packageManager
            trigger.packageNames.joinToString(", ") { packageName ->
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    packageName
                }
            }
        }
        is ModeTrigger.Wifi -> trigger.ssids.joinToString(", ")
        is ModeTrigger.Bluetooth -> {
            if (trigger.matchAnyCarAudio) "Any car audio"
            else {
                val bluetoothAdapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter()
                trigger.deviceAddresses.joinToString(", ") { address ->
                    try {
                        bluetoothAdapter?.getRemoteDevice(address)?.name ?: address
                    } catch (e: Exception) {
                        address
                    }
                }
            }
        }
        is ModeTrigger.Music -> "Playing music"
        is ModeTrigger.Location -> trigger.target.addressName ?: "Location"
        is ModeTrigger.Intent -> trigger.activateAction ?: "Intent"
    }
}

