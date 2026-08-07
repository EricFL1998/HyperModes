package com.banana.hypermodes.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ImportedIntentStore
import com.banana.hypermodes.data.ModeTrigger
import com.banana.hypermodes.data.ModeTriggerGroup
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.Close
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Display a trigger group with visual indication for compound (AND) logic
 * Compound triggers can be edited via long press
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TriggerGroupCard(
    group: ModeTriggerGroup,
    groupIndex: Int,
    onRemove: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (group is ModeTriggerGroup.Compound && onLongClick != null) {
                    Modifier.combinedClickable(
                        onClick = {},
                        onLongClick = onLongClick
                    )
                } else {
                    Modifier
                }
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (group) {
                    is ModeTriggerGroup.Single -> {
                        // Single line display: icon + title + description
                        Text(
                            text = getTriggerTypeIcon(group.trigger) + " " + getTriggerTitle(group.trigger) + " · " + getTriggerDescription(group.trigger),
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    is ModeTriggerGroup.Compound -> {
                        Column(modifier = Modifier.weight(1f)) {
                            group.triggers.forEachIndexed { index, trigger ->
                                Text(
                                    text = getTriggerTypeIcon(trigger) + " " + getTriggerTitle(trigger) + " · " + getTriggerDescription(trigger),
                                    style = MiuixTheme.textStyles.body2
                                )
                            }
                        }
                    }
                }
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = MiuixIcons.Basic.Close,
                        contentDescription = "Remove",
                        tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                    )
                }
            }
        }
    }
}

@Composable
private fun getTriggerTypeIcon(trigger: ModeTrigger): String {
    return when (trigger) {
        is ModeTrigger.Time -> "⏰"
        is ModeTrigger.App -> "📱"
        is ModeTrigger.Wifi -> "📡"
        is ModeTrigger.Bluetooth -> "🔵"
        is ModeTrigger.Music -> "🎵"
        is ModeTrigger.Location -> "📍"
        is ModeTrigger.Intent -> "⚡"
        is ModeTrigger.Battery -> "🔋"
    }
}

@Composable
private fun getTriggerTitle(trigger: ModeTrigger): String {
    return when (trigger) {
        is ModeTrigger.Time -> stringResource(R.string.trigger_time)
        is ModeTrigger.App -> stringResource(R.string.trigger_app)
        is ModeTrigger.Wifi -> stringResource(R.string.trigger_wifi)
        is ModeTrigger.Bluetooth -> stringResource(R.string.trigger_bluetooth)
        is ModeTrigger.Music -> stringResource(R.string.trigger_music)
        is ModeTrigger.Location -> stringResource(R.string.trigger_location)
        is ModeTrigger.Intent -> stringResource(R.string.trigger_intent)
        is ModeTrigger.Battery -> stringResource(R.string.trigger_battery)
    }
}

@Composable
private fun getTriggerDescription(trigger: ModeTrigger): String {
    val context = LocalContext.current
    val importedConfigs = remember { ImportedIntentStore.loadAll(context) }
    return when (trigger) {
        is ModeTrigger.Time -> "${trigger.schedule.startHour}:${String.format("%02d", trigger.schedule.startMinute)} - ${trigger.schedule.endHour}:${String.format("%02d", trigger.schedule.endMinute)}"
        is ModeTrigger.App -> {
            val pm = context.packageManager
            trigger.packageNames.joinToString(", ") { packageName ->
                try {
                    val appInfo = pm.getApplicationInfo(packageName, 0)
                    pm.getApplicationLabel(appInfo).toString()
                } catch (e: Exception) {
                    packageName
                }
            }.take(50)
        }
        is ModeTrigger.Wifi -> trigger.ssids.joinToString(", ")
        is ModeTrigger.Bluetooth -> {
            if (trigger.matchAnyCarAudio) "任意车载蓝牙"
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
        is ModeTrigger.Music -> stringResource(R.string.trigger_on_music)
        is ModeTrigger.Location -> trigger.target.addressName ?: "位置触发"
        is ModeTrigger.Intent -> {
            importedConfigs.firstNotNullOfOrNull { config ->
                if (config.packageName == trigger.packageName) {
                    config.intents.firstOrNull { action ->
                        action.intents.any { it == trigger.activateAction || it == trigger.deactivateAction }
                    }?.name
                } else null
            } ?: (trigger.activateAction ?: "Intent 触发")
        }
        is ModeTrigger.Battery -> when (trigger.operator) {
            "above" -> stringResource(R.string.trigger_on_battery_above, trigger.threshold)
            "below" -> stringResource(R.string.trigger_on_battery_below, trigger.threshold)
            else -> stringResource(R.string.trigger_on_battery_equal, trigger.threshold)
        }
    }
}

