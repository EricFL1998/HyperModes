package com.banana.hypermodes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.overlay.OverlayBottomSheet
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CreateModeDialog(
    show: Boolean,
    deletedBuiltIns: List<Mode>,
    onDismiss: () -> Unit,
    onCreateCustom: () -> Unit,
    onRestoreBuiltIn: (Mode) -> Unit
) {
    OverlayBottomSheet(
        show = show,
        onDismissRequest = onDismiss,
        title = stringResource(R.string.create_mode)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Custom entry
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onCreateCustom)
                            .padding(horizontal = 28.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "😊",
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = stringResource(R.string.custom),
                            style = MiuixTheme.textStyles.body1,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Deleted built-in modes, listed individually
                items(deletedBuiltIns.size) { index ->
                    val builtIn = deletedBuiltIns[index]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onRestoreBuiltIn(builtIn) }
                            .padding(horizontal = 28.dp, vertical = 18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = builtIn.icon,
                            fontSize = 32.sp,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Column {
                            Text(
                                text = when (builtIn.id) {
                                    "dnd" -> stringResource(R.string.mode_dnd)
                                    "bedtime" -> stringResource(R.string.mode_bedtime)
                                    "driving" -> stringResource(R.string.mode_driving)
                                    else -> builtIn.name
                                },
                                style = MiuixTheme.textStyles.body1,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = when (builtIn.id) {
                                    "dnd" -> stringResource(R.string.mode_dnd_desc)
                                    "bedtime" -> stringResource(R.string.mode_bedtime_desc)
                                    "driving" -> stringResource(R.string.mode_driving_desc)
                                    else -> builtIn.description
                                },
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
