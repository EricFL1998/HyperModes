package com.banana.hypermodes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Pick a trigger type.
 *
 * Rendered as a compact two-column grid so the dialog stays short even as
 * new trigger types are added. The grid scrolls beyond a max height instead
 * of growing the dialog off-screen.
 */
@Composable
fun TriggerSelectionDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSelect: (String) -> Unit
) {
    val options = listOf(
        TriggerOption("time", "⏰", stringResource(R.string.trigger_time), stringResource(R.string.trigger_time_desc)),
        TriggerOption("app", "📱", stringResource(R.string.trigger_app), stringResource(R.string.trigger_app_desc)),
        TriggerOption("wifi", "📡", stringResource(R.string.trigger_wifi), stringResource(R.string.trigger_wifi_desc)),
        TriggerOption("bluetooth", "🔵", stringResource(R.string.trigger_bluetooth), stringResource(R.string.trigger_bluetooth_desc)),
        TriggerOption("music", "🎵", stringResource(R.string.trigger_music), stringResource(R.string.trigger_music_desc)),
        TriggerOption("location", "📍", stringResource(R.string.trigger_location), stringResource(R.string.trigger_location_desc)),
        TriggerOption("intent", "⚡", stringResource(R.string.trigger_intent), stringResource(R.string.trigger_intent_desc)),
        TriggerOption("battery", "🔋", stringResource(R.string.trigger_battery), stringResource(R.string.trigger_battery_desc))
    )

    OverlayDialog(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.add_trigger_title)
    ) {
        Column {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                options.chunked(2).forEach { rowOptions ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowOptions.forEach { option ->
                            TriggerOptionTile(
                                option = option,
                                onClick = { onSelect(option.id) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        repeat(2 - rowOptions.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
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

private data class TriggerOption(
    val id: String,
    val icon: String,
    val title: String,
    val description: String
)

@Composable
private fun TriggerOptionTile(
    option: TriggerOption,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .squircleSurface(
                    color = MiuixTheme.colorScheme.secondaryContainer,
                    cornerRadius = 15.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option.icon,
                fontSize = 24.sp
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = option.title,
            style = MiuixTheme.textStyles.body2,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = option.description,
            style = MiuixTheme.textStyles.body2,
            fontSize = 11.sp,
            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
