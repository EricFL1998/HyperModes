package com.banana.hypermodes.ui

import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.content.pm.ApplicationInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import android.graphics.Canvas
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.*
import com.banana.hypermodes.bridge.ModeControlBridge
import com.banana.hypermodes.data.WallpaperSnapshotBridge
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.components.TimePickerDialog
import com.banana.hypermodes.ui.components.TriggerSelectionDialog
import com.banana.hypermodes.ui.components.TriggerTypeSelectionDialog
import com.banana.hypermodes.ui.components.CompoundTriggerEditDialog
import com.banana.hypermodes.ui.components.TriggerGroupCard
import com.banana.hypermodes.ui.components.BatteryTriggerPickerDialog
import com.banana.hypermodes.ui.components.WallpaperOverviewCard
import java.io.File
import org.json.JSONObject
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.basic.Close
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
internal fun AdaptiveHeroDescription(text: String) {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val baseStyle = MiuixTheme.textStyles.body2
    val horizontalPadding = 32.dp

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        val maxWidthPx = with(density) { maxWidth.roundToPx() }
        val measuredWidth = textMeasurer.measure(
            text = text,
            style = baseStyle,
            maxLines = 1,
            softWrap = false,
            constraints = Constraints()
        ).size.width
        val scale = if (measuredWidth > maxWidthPx && measuredWidth > 0) {
            maxWidthPx.toFloat() / measuredWidth
        } else {
            1f
        }

        Text(
            text = text,
            style = baseStyle,
            fontSize = (baseStyle.fontSize.value * scale).coerceAtLeast(10f).sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            softWrap = false
        )
    }
}

@Composable
fun ContactFilterDialog(
    show: Boolean,
    current: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(R.string.select_contacts),
        show = show,
        onDismissRequest = onDismiss
    ) {
        Column {
            listOf(
                CONTACT_FILTER_NONE to stringResource(R.string.no_contacts),
                CONTACT_FILTER_ALL to stringResource(R.string.all_contacts),
                CONTACT_FILTER_STARRED to stringResource(R.string.starred_contacts)
            ).forEach { (filter, label) ->
                val selected = current == filter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(filter) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selected) {
                        Text(
                            text = "✓",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                    Text(
                        text = label,
                        style = MiuixTheme.textStyles.body1,
                        color = if (selected) MiuixTheme.colorScheme.primary
                        else MiuixTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TriggerRowCard(
    icon: String,
    label: String,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = icon, fontSize = 20.sp)
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = label,
                style = MiuixTheme.textStyles.body1,
                modifier = Modifier.weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = MiuixIcons.Basic.Close,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

@Composable
fun displayOptionsSummary(settings: ModeSettings): String {
    val enabled = buildList {
        if (settings.hideNotifications) add(stringResource(R.string.setting_hide_notifications))
        settings.darkMode?.let { add(if (it == 1) stringResource(R.string.theme_dark) else stringResource(R.string.theme_light)) }
        if (settings.enableAod != null) add(stringResource(R.string.aod_option))
        if (settings.enableGrayscale != null) add(stringResource(R.string.grayscale_mode))
        if (settings.enableRefreshRate != null) add(stringResource(R.string.refresh_rate_option))
        if (settings.enableEyeCare != null) add(stringResource(R.string.eye_care_mode))
        if (settings.enableRaiseToWake != null) add(stringResource(R.string.raise_to_wake))
        if (settings.enableWakeForNotifications != null) add(stringResource(R.string.wake_for_notifications))
    }
    return when {
        enabled.isEmpty() -> stringResource(R.string.options_none)
        enabled.size <= 2 -> enabled.joinToString(stringResource(R.string.list_separator))
        else -> stringResource(R.string.and_n_more, enabled[0], enabled[1], enabled.size - 2)
    }
}

@Composable
fun deviceControlSummary(settings: ModeSettings): String {
    val enabled = buildList {
        settings.performanceMode?.let { mode ->
            val label = when (mode) {
                1 -> stringResource(R.string.performance_high)
                2 -> stringResource(R.string.performance_power_save)
                else -> stringResource(R.string.performance_balanced)
            }
            add(label)
        }
        if (settings.enable5g != null) add(stringResource(R.string.five_g_network))
        if (settings.enableWifi != null) add(stringResource(R.string.wifi_control))
        if (settings.enableBluetooth != null) add(stringResource(R.string.bluetooth_control))
        if (settings.enableHotspot != null) add(stringResource(R.string.hotspot_control))
    }
    return when {
        enabled.isEmpty() -> stringResource(R.string.options_none)
        enabled.size <= 2 -> enabled.joinToString(stringResource(R.string.list_separator))
        else -> stringResource(R.string.and_n_more, enabled[0], enabled[1], enabled.size - 2)
    }
}

/** Clickable settings row with a value label on the right (图三/图四 style). */
@Composable
fun ValueSettingItem(
    title: String,
    subtitle: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = value,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = MiuixIcons.Basic.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

/** 就寝提醒 value text: 准时 / 提前15分钟 / 提前30分钟 / 提前1小时 / 无. */
@Composable
fun reminderSummary(minutes: Int): String = when (minutes) {
    -1 -> stringResource(R.string.reminder_none)
    0 -> stringResource(R.string.reminder_on_time)
    60 -> stringResource(R.string.reminder_hour_before)
    else -> stringResource(R.string.reminder_minutes_before, minutes)
}

/** 就寝提醒 picker with the official DeskClock options (0/15/30/60/-1). */
@Composable
fun SleepReminderDialog(
    show: Boolean,
    current: Int,
    onDismiss: () -> Unit,
    onSelect: (Int) -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(R.string.sleep_reminder),
        show = show,
        onDismissRequest = onDismiss
    ) {
        Column {
            listOf(0, 15, 30, 60, -1).forEach { minutes ->
                val selected = current == minutes
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(minutes) }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (selected) {
                        Text(
                            text = "✓",
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                    } else {
                        Spacer(modifier = Modifier.width(24.dp))
                    }
                    Text(
                        text = reminderSummary(minutes),
                        style = MiuixTheme.textStyles.body1,
                        color = if (selected) MiuixTheme.colorScheme.primary
                        else MiuixTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    subtitleContent: (@Composable () -> Unit)? = null
) {
    Card(
        modifier = modifier.fillMaxWidth(),
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
                if (subtitleContent != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    subtitleContent()
                } else if (subtitle.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = subtitle,
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
            }
            if (onClick == null) {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            } else {
                Icon(
                    imageVector = MiuixIcons.Basic.ArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                )
            }
        }
    }
}

/**
 * Stacked app icons for the Apps / Paused apps rows: overlapping circular
 * icons with a small trailing gap instead of a "N apps" text summary.
 */
@Composable
fun AppIconStack(
    packageNames: Set<String>,
    modifier: Modifier = Modifier,
    maxIcons: Int = 5
) {
    val context = LocalContext.current
    val icons = remember(packageNames) {
        packageNames.take(maxIcons).mapNotNull { pkg ->
            runCatching {
                val pm = context.packageManager
                pm.getApplicationIcon(pkg)
                    .toStackIconBitmap()
            }.getOrNull()
        }
    }
    if (icons.isEmpty()) return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        icons.forEachIndexed { index, bitmap ->
            val overlap = 12.dp
            Image(
                bitmap = bitmap,
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = if (index == 0) 0.dp else -overlap * index)
                    .clip(CircleShape)
                    .border(
                        width = 2.dp,
                        color = MiuixTheme.colorScheme.surface,
                        shape = CircleShape
                    )
            )
        }
        // Trailing whitespace after the stack
        Spacer(modifier = Modifier.width(8.dp))
    }
}

private fun android.graphics.drawable.Drawable.toStackIconBitmap(size: Int = 96): androidx.compose.ui.graphics.ImageBitmap =
    Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888).also { bmp ->
        val canvas = Canvas(bmp)
        setBounds(0, 0, size, size)
        draw(canvas)
    }.asImageBitmap()

@Composable
fun ScheduleCard(
    schedule: ModeSchedule,
    onScheduleChange: (ModeSchedule) -> Unit
) {
    val context = LocalContext.current
    var showSleepPicker by remember { mutableStateOf(false) }
    var showWakePicker by remember { mutableStateOf(false) }
    var showTurnOffDialog by remember { mutableStateOf(false) }

    fun sendToDeskClock(action: String, configure: android.content.Intent.() -> Unit = {}) {
        context.sendBroadcast(android.content.Intent(action).apply {
            setPackage(Protocol.TARGET_PACKAGE)
            configure()
        })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Column {
            // Wake alarm toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.bedtime_alarm),
                        style = MiuixTheme.textStyles.body1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.bedtime_alarm_desc),
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                    )
                }
                Switch(
                    checked = schedule.enabled,
                    onCheckedChange = { enabled ->
                        if (enabled) {
                            sendToDeskClock(
                                Protocol.ACTION_ENABLE_WAKE_ALARM
                            )
                            onScheduleChange(schedule.copy(enabled = true))
                        } else {
                            showTurnOffDialog = true
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sleep / Wake times side by side
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeColumn(
                    icon = "🌙",
                    label = stringResource(R.string.sleep_time),
                    hour = schedule.startHour,
                    minute = schedule.startMinute,
                    onClick = { showSleepPicker = true },
                    modifier = Modifier.weight(1f)
                )
                TimeColumn(
                    icon = "☀️",
                    label = stringResource(R.string.wake_time),
                    hour = schedule.endHour,
                    minute = schedule.endMinute,
                    onClick = { showWakePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    TimePickerDialog(
        title = stringResource(R.string.sleep_time),
        initialHour = schedule.startHour,
        initialMinute = schedule.startMinute,
        show = showSleepPicker,
        onDismissRequest = { showSleepPicker = false },
        onConfirm = { hour, minute ->
            val newSchedule = schedule.copy(startHour = hour, startMinute = minute)
            onScheduleChange(newSchedule)
            applyScheduleToDeskClock(newSchedule) { action, h1, m1, h2, m2, days ->
                sendToDeskClock(action) {
                    putExtra(Protocol.EXTRA_SLEEP_HOUR, h1)
                    putExtra(Protocol.EXTRA_SLEEP_MIN, m1)
                    putExtra(Protocol.EXTRA_WAKE_HOUR, h2)
                    putExtra(Protocol.EXTRA_WAKE_MIN, m2)
                    putExtra(Protocol.EXTRA_REPEAT_DAYS, days)
                }
            }
        }
    )

    TimePickerDialog(
        title = stringResource(R.string.wake_time),
        initialHour = schedule.endHour,
        initialMinute = schedule.endMinute,
        show = showWakePicker,
        onDismissRequest = { showWakePicker = false },
        onConfirm = { hour, minute ->
            val newSchedule = schedule.copy(endHour = hour, endMinute = minute)
            onScheduleChange(newSchedule)
            applyScheduleToDeskClock(newSchedule) { action, h1, m1, h2, m2, days ->
                sendToDeskClock(action) {
                    putExtra(Protocol.EXTRA_SLEEP_HOUR, h1)
                    putExtra(Protocol.EXTRA_SLEEP_MIN, m1)
                    putExtra(Protocol.EXTRA_WAKE_HOUR, h2)
                    putExtra(Protocol.EXTRA_WAKE_MIN, m2)
                    putExtra(Protocol.EXTRA_REPEAT_DAYS, days)
                }
            }
        }
    )

    WakeAlarmTurnOffDialog(
        show = showTurnOffDialog,
        onDismiss = { showTurnOffDialog = false },
        onOnce = {
            sendToDeskClock(
                Protocol.ACTION_SKIP_WAKE_ALARM_ONCE
            )
            showTurnOffDialog = false
        },
        onAlways = {
            sendToDeskClock(
                Protocol.ACTION_DISABLE_WAKE_ALARM
            )
            showTurnOffDialog = false
            onScheduleChange(schedule.copy(enabled = false))
        }
    )
}

private fun applyScheduleToDeskClock(
    schedule: ModeSchedule,
    send: (action: String, h1: Int, m1: Int, h2: Int, m2: Int, days: Int) -> Unit
) {
    send(
        Protocol.ACTION_APPLY_SCHEDULE,
        schedule.startHour, schedule.startMinute,
        schedule.endHour, schedule.endMinute,
        schedule.repeatDays
    )
}

@Composable
fun WakeAlarmTurnOffDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onOnce: () -> Unit,
    onAlways: () -> Unit
) {
    top.yukonga.miuix.kmp.overlay.OverlayDialog(
        title = stringResource(R.string.turn_off_alarm_title),
        show = show,
        onDismissRequest = onDismiss
    ) {
        Column {
            TextButton(
                text = stringResource(R.string.turn_off_once),
                onClick = onOnce,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.turn_off_always),
                onClick = onAlways,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.textButtonColorsPrimary()
            )
            Spacer(modifier = Modifier.height(12.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun TimeColumn(
    icon: String,
    label: String,
    hour: Int,
    minute: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = icon,
                style = MiuixTheme.textStyles.body2,
                modifier = Modifier.padding(end = 4.dp)
            )
            Text(
                text = label,
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = String.format("%02d:%02d", hour, minute),
            style = MiuixTheme.textStyles.headline1,
            fontSize = 44.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
