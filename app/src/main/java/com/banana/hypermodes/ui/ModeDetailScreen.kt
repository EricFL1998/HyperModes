package com.banana.hypermodes.ui

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.data.*
import com.banana.hypermodes.manager.ModeManager
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.icon.icons.ImmersionMore
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun ModeDetailScreen(
    mode: Mode,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
    var editedMode by remember { mutableStateOf(mode) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = editedMode.name,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.ArrowBack,
                            contentDescription = "Back"
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
        ) {
            // Enable toggle
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Use ${editedMode.name}",
                                style = MiuixTheme.textStyles.body1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = editedMode.description,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }
                        Switch(
                            checked = editedMode.enabled,
                            onCheckedChange = { enabled ->
                                editedMode = editedMode.copy(enabled = enabled)
                                if (enabled) {
                                    ModeManager(context).activateMode(editedMode)
                                } else {
                                    ModeManager(context).deactivateMode(editedMode)
                                }
                                onSave(editedMode)
                            }
                        )
                    }
                }
            }

            // Schedule section (for bedtime)
            if (editedMode.id == "bedtime") {
                item {
                    SmallTitle(
                        text = "Schedule",
                        modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    ScheduleCard(
                        schedule = editedMode.settings.schedule ?: ModeSchedule(),
                        onScheduleChange = { newSchedule ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(schedule = newSchedule)
                            )
                            onSave(editedMode)
                        }
                    )
                }
            }

            // What to customize section
            item {
                SmallTitle(
                    text = "What to customize",
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            // DND toggle
            item {
                SettingItem(
                    title = "Do Not Disturb",
                    subtitle = when (editedMode.settings.dndLevel) {
                        DndLevel.NONE -> "No interruptions"
                        DndLevel.PRIORITY -> "Priority only"
                        DndLevel.ALARMS -> "Alarms only"
                    },
                    checked = editedMode.settings.enableDnd,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableDnd = enabled)
                        )
                        onSave(editedMode)
                    },
                    onClick = { /* TODO: Open DND level picker */ }
                )
            }

            // Grayscale toggle
            item {
                SettingItem(
                    title = "Grayscale",
                    subtitle = "Reduce eye strain with black and white screen",
                    checked = editedMode.settings.enableGrayscale,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableGrayscale = enabled)
                        )
                        onSave(editedMode)
                    }
                )
            }

            // Dim wallpaper
            item {
                SettingItem(
                    title = "Dim wallpaper",
                    subtitle = "Reduce brightness of wallpaper",
                    checked = editedMode.settings.dimWallpaper,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(dimWallpaper = enabled)
                        )
                        onSave(editedMode)
                    }
                )
            }

            // Hide notifications
            item {
                SettingItem(
                    title = "Hide notifications",
                    subtitle = "Don't show notification content on lock screen",
                    checked = editedMode.settings.hideNotifications,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(hideNotifications = enabled)
                        )
                        onSave(editedMode)
                    }
                )
            }

            // Pause apps
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    onClick = { /* TODO: Open app picker */ }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pause apps",
                                style = MiuixTheme.textStyles.body1
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (editedMode.settings.pausedApps.isEmpty()) {
                                    "No apps paused"
                                } else {
                                    "${editedMode.settings.pausedApps.size} apps paused"
                                },
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }
                        Icon(
                            imageVector = MiuixIcons.ImmersionMore,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.body1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
            if (onClick == null) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            } else {
                Icon(
                    imageVector = MiuixIcons.ImmersionMore,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

@Composable
fun ScheduleCard(
    schedule: ModeSchedule,
    onScheduleChange: (ModeSchedule) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Set schedule",
                    style = MiuixTheme.textStyles.body1
                )
                Switch(
                    checked = schedule.enabled,
                    onCheckedChange = { enabled ->
                        onScheduleChange(schedule.copy(enabled = enabled))
                    }
                )
            }

            if (schedule.enabled) {
                Spacer(modifier = Modifier.height(12.dp))

                // Start time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Starts",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Text(
                        text = String.format("%02d:%02d", schedule.startHour, schedule.startMinute),
                        style = MiuixTheme.textStyles.body1
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // End time
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Ends",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                    Text(
                        text = String.format("%02d:%02d", schedule.endHour, schedule.endMinute),
                        style = MiuixTheme.textStyles.body1
                    )
                }
            }
        }
    }
}
