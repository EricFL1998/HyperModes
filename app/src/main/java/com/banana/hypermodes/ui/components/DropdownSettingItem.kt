package com.banana.hypermodes.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.window.WindowListPopup

@Composable
fun <T> DropdownSettingItem(
    title: String,
    subtitle: String,
    selected: Boolean, // Whether the override is enabled
    onToggle: (Boolean) -> Unit,
    value: T,
    options: List<Pair<T, String>>,
    onValueChange: (T) -> Unit,
    modifier: Modifier = Modifier
) {
    var showPopup by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.fillMaxWidth(),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
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
                if (selected) {
                    Box {
                        Row(
                            modifier = Modifier.clickable { showPopup = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = options.find { it.first == value }?.second ?: "",
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }

                        WindowListPopup(
                            show = showPopup,
                            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                            alignment = PopupPositionProvider.Align.TopEnd,
                            onDismissRequest = { showPopup = false }
                        ) {
                            ListPopupColumn {
                                options.forEachIndexed { index, pair ->
                                    DropdownImpl(
                                        text = pair.second,
                                        optionSize = options.size,
                                        isSelected = value == pair.first,
                                        index = index,
                                        onSelectedIndexChange = {
                                            onValueChange(pair.first)
                                            showPopup = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Switch(
                    checked = selected,
                    onCheckedChange = onToggle
                )
            }
        }
    }
}
