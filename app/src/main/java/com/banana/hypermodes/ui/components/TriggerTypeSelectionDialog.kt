package com.banana.hypermodes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
 * Dialog to select between Single Trigger or Compound Trigger (v2.0)
 */
@Composable
fun TriggerTypeSelectionDialog(
    show: Boolean,
    onDismissRequest: () -> Unit,
    onSelectSingle: () -> Unit,
    onSelectCompound: () -> Unit
) {
    OverlayDialog(
        show = show,
        onDismissRequest = onDismissRequest,
        title = stringResource(R.string.select_trigger_type)
    ) {
        Column {
            TriggerTypeOptionItem(
                title = stringResource(R.string.single_trigger),
                subtitle = stringResource(R.string.single_trigger_desc),
                onClick = onSelectSingle
            )
            TriggerTypeOptionItem(
                title = stringResource(R.string.compound_trigger),
                subtitle = stringResource(R.string.compound_trigger_desc),
                onClick = onSelectCompound
            )

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
private fun TriggerTypeOptionItem(
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
