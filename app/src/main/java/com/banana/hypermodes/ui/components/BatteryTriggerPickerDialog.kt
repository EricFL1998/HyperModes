package com.banana.hypermodes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

/**
 * Dialog to configure a battery-level trigger:
 * an operator (above / below / equal) and a percentage threshold.
 */
@Composable
fun BatteryTriggerPickerDialog(
    initialThreshold: Int = 20,
    initialOperator: String = "below",
    show: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: (threshold: Int, operator: String) -> Unit
) {
    var threshold by remember(show) { mutableIntStateOf(initialThreshold) }
    var operator by remember(show) { mutableStateOf(initialOperator) }

    val operatorOptions = listOf(
        "below" to stringResource(R.string.battery_below),
        "above" to stringResource(R.string.battery_above),
        "equal" to stringResource(R.string.battery_equal)
    )

    OverlayDialog(
        title = stringResource(R.string.battery_threshold),
        show = show,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Operator selection row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                operatorOptions.forEach { (value, label) ->
                    val selected = operator == value
                    TextButton(
                        text = label,
                        onClick = { operator = value },
                        modifier = Modifier.weight(1f),
                        colors = if (selected) {
                            ButtonDefaults.textButtonColorsPrimary()
                        } else {
                            ButtonDefaults.textButtonColors()
                        }
                    )
                }
            }

            // Threshold picker
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "$threshold%",
                    style = MiuixTheme.textStyles.headline1,
                    fontWeight = FontWeight.Bold
                )
                NumberPicker(
                    value = threshold,
                    onValueChange = { threshold = it },
                    range = 5..100,
                    label = { "$it%" },
                    wrapAround = false
                )
            }

            // Action buttons
            Spacer(modifier = Modifier.height(8.dp))
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
                    onClick = { onConfirm(threshold, operator) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
}
