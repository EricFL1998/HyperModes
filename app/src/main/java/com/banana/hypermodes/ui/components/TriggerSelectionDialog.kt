package com.banana.hypermodes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Pick a trigger type.
 *
 * Single-column list that scrolls beyond a max height so the dialog stays on
 * screen even as new trigger types are added.
 */
@Composable
fun TriggerSelectionDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (String) -> Unit
) {
    OverlayDialog(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.add_trigger_title)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_time),
                    subtitle = stringResource(R.string.trigger_time_desc),
                    onClick = { onSelect("time") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_app),
                    subtitle = stringResource(R.string.trigger_app_desc),
                    onClick = { onSelect("app") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_wifi),
                    subtitle = stringResource(R.string.trigger_wifi_desc),
                    onClick = { onSelect("wifi") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_bluetooth),
                    subtitle = stringResource(R.string.trigger_bluetooth_desc),
                    onClick = { onSelect("bluetooth") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_music),
                    subtitle = stringResource(R.string.trigger_music_desc),
                    onClick = { onSelect("music") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_location),
                    subtitle = stringResource(R.string.trigger_location_desc),
                    onClick = { onSelect("location") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_intent),
                    subtitle = stringResource(R.string.trigger_intent_desc),
                    onClick = { onSelect("intent") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_battery),
                    subtitle = stringResource(R.string.trigger_battery_desc),
                    onClick = { onSelect("battery") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_holiday),
                    subtitle = stringResource(R.string.trigger_holiday_desc),
                    onClick = { onSelect("holiday") }
                )
                TriggerOptionItem(
                    title = stringResource(R.string.trigger_nfc),
                    subtitle = stringResource(R.string.trigger_nfc_desc),
                    onClick = { onSelect("nfc") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(
                text = stringResource(R.string.cancel),
                onClick = onDismissRequest,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun TriggerOptionItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Text(
            text = title,
            style = MiuixTheme.textStyles.body1
        )
        Text(
            text = subtitle,
            style = MiuixTheme.textStyles.body2,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
        )
    }
}
