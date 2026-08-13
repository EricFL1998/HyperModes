package com.banana.hypermodes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * 节假日/工作日触发器配置：选择当天是节假日还是工作日时激活。
 */
@Composable
fun HolidayTriggerPickerDialog(
    initialKind: String = "节假日",
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (kind: String) -> Unit
) {
    var kind by remember(show) { mutableStateOf(initialKind) }

    val options = listOf(
        "节假日" to stringResource(R.string.holiday_kind_holiday),
        "工作日" to stringResource(R.string.holiday_kind_workday)
    )

    OverlayDialog(
        title = stringResource(R.string.trigger_holiday),
        show = show,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                options.forEach { (value, label) ->
                    val selected = kind == value
                    TextButton(
                        text = label,
                        onClick = { kind = value },
                        modifier = Modifier.weight(1f),
                        colors = if (selected) {
                            ButtonDefaults.textButtonColorsPrimary()
                        } else {
                            ButtonDefaults.textButtonColors()
                        }
                    )
                }
            }

            Text(
                text = stringResource(R.string.holiday_trigger_desc),
                style = MiuixTheme.textStyles.body2,
                color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                modifier = Modifier.padding(vertical = 4.dp)
            )

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
                    onClick = { onConfirm(kind) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
}
