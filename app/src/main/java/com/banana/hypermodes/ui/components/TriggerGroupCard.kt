package com.banana.hypermodes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
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
 */
@Composable
fun TriggerGroupCard(
    group: ModeTriggerGroup,
    groupIndex: Int,
    onRemove: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onEdit
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
                        Text(
                            text = getTriggerTypeIcon(group.trigger) + " " + getTriggerTitle(group.trigger),
                            style = MiuixTheme.textStyles.body1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    is ModeTriggerGroup.Compound -> {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "🔗 ${group.name ?: stringResource(R.string.compound_trigger)}",
                                style = MiuixTheme.textStyles.body1
                            )
                            Text(
                                text = "${group.triggers.size} ${stringResource(R.string.trigger_group_and_logic)}",
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
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

            // Show trigger details
            when (group) {
                is ModeTriggerGroup.Single -> {
                    Text(
                        text = getTriggerDescription(group.trigger),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                is ModeTriggerGroup.Compound -> {
                    Spacer(modifier = Modifier.height(8.dp))
                    group.triggers.forEachIndexed { index, trigger ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (index == 0) "•" else "∧",
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Column {
                                Text(
                                    text = getTriggerTypeIcon(trigger) + " " + getTriggerTitle(trigger),
                                    style = MiuixTheme.textStyles.body2
                                )
                                Text(
                                    text = getTriggerDescription(trigger),
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
    }
}

@Composable
private fun getTriggerDescription(trigger: ModeTrigger): String {
    return when (trigger) {
        is ModeTrigger.Time -> "${trigger.schedule.startHour}:${String.format("%02d", trigger.schedule.startMinute)} - ${trigger.schedule.endHour}:${String.format("%02d", trigger.schedule.endMinute)}"
        is ModeTrigger.App -> trigger.packageNames.joinToString(", ").take(50)
        is ModeTrigger.Wifi -> trigger.ssids.joinToString(", ")
        is ModeTrigger.Bluetooth -> {
            if (trigger.matchAnyCarAudio) "任意车载蓝牙"
            else "${trigger.deviceAddresses.size} 设备"
        }
        is ModeTrigger.Music -> "播放音乐时"
        is ModeTrigger.Location -> trigger.target.addressName ?: "位置触发"
        is ModeTrigger.Intent -> trigger.activateAction ?: "Intent 触发"
    }
}
