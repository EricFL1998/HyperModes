package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ModeSchedule
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

// Mirrors com.android.deskclock.Alarm.DaysOfWeek coded values.
const val REPEAT_NEVER = 0
const val REPEAT_EVERY_DAY = 127
const val REPEAT_MON_FRI = 31
const val REPEAT_LEGAL_WORKDAY = 128
const val REPEAT_LEGAL_OFFDAY = 256

/** Human-readable summary of a DeskClock repeat bitmask (作息卡片上的副标题). */
@Composable
fun repeatSummary(days: Int): String = when (days) {
    REPEAT_NEVER -> stringResource(R.string.never)
    REPEAT_EVERY_DAY -> stringResource(R.string.repeat_every_day)
    REPEAT_MON_FRI -> stringResource(R.string.repeat_mon_fri)
    REPEAT_LEGAL_WORKDAY -> stringResource(R.string.repeat_workday)
    REPEAT_LEGAL_OFFDAY -> stringResource(R.string.repeat_holiday)
    else -> {
        val names = listOf(
            stringResource(R.string.day_mon_short),
            stringResource(R.string.day_tue_short),
            stringResource(R.string.day_wed_short),
            stringResource(R.string.day_thu_short),
            stringResource(R.string.day_fri_short),
            stringResource(R.string.day_sat_short),
            stringResource(R.string.day_sun_short)
        )
        val sep = stringResource(R.string.list_separator)
        (0..6).filter { days and (1 shl it) != 0 }
            .joinToString(sep) { names[it] }
    }
}

/**
 * 重复 page (图一): preset repeat options + 自定义 entry.
 */
@Composable
fun RepeatScreen(
    schedule: ModeSchedule,
    onBack: () -> Unit,
    onOpenCustom: (ModeSchedule) -> Unit,
    onSelect: (ModeSchedule) -> Unit
) {
    BackHandler(onBack = onBack)

    val presets = listOf(
        REPEAT_EVERY_DAY to stringResource(R.string.repeat_every_day),
        REPEAT_LEGAL_WORKDAY to stringResource(R.string.repeat_workday),
        REPEAT_LEGAL_OFFDAY to stringResource(R.string.repeat_holiday),
        REPEAT_MON_FRI to stringResource(R.string.repeat_mon_fri)
    )
    val isPreset = presets.any { it.first == schedule.repeatDays }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.repeat),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding())
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Preset options
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Column {
                        presets.forEach { (days, label) ->
                            val selected = schedule.repeatDays == days
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(schedule.copy(repeatDays = days)) }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
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
                    }
                }
            }

            // Custom entry
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                    onClick = { onOpenCustom(schedule) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.repeat_custom),
                                style = MiuixTheme.textStyles.body1,
                                color = if (!isPreset) MiuixTheme.colorScheme.primary
                                else MiuixTheme.colorScheme.onSurface
                            )
                            if (!isPreset) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = repeatSummary(schedule.repeatDays),
                                    style = MiuixTheme.textStyles.body2,
                                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                                )
                            }
                        }
                        Icon(
                            imageVector = MiuixIcons.Basic.ArrowRight,
                            contentDescription = null,
                            tint = MiuixTheme.colorScheme.onSurfaceVariantActions
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}

/**
 * 自定义 page (图二): per-day checkboxes Mon..Sun.
 */
@Composable
fun CustomRepeatScreen(
    schedule: ModeSchedule,
    onBack: () -> Unit,
    onSelect: (ModeSchedule) -> Unit
) {
    BackHandler(onBack = onBack)

    val dayLabels = listOf(
        R.string.day_mon, R.string.day_tue, R.string.day_wed,
        R.string.day_thu, R.string.day_fri, R.string.day_sat, R.string.day_sun
    )

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.repeat_custom),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding())
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    Column {
                        dayLabels.forEachIndexed { day, labelRes ->
                            val checked = schedule.repeatDays and (1 shl day) != 0
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val on = !checked
                                        val newDays = if (on) {
                                            schedule.repeatDays or (1 shl day)
                                        } else {
                                            schedule.repeatDays and (1 shl day).inv()
                                        }
                                        onSelect(schedule.copy(repeatDays = newDays))
                                    }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(labelRes),
                                    style = MiuixTheme.textStyles.body1
                                )
                                if (checked) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                color = MiuixTheme.colorScheme.primary,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "✓",
                                            color = MiuixTheme.colorScheme.surface,
                                            style = MiuixTheme.textStyles.body2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}
