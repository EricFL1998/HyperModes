package com.banana.hypermodes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import androidx.compose.ui.res.painterResource
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.data.Mode
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.window.WindowBottomSheet
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** Emoji icon choices for custom modes (stored directly as Mode.icon). */
val MODE_ICON_CHOICES = listOf(
    "🌙", "🚗", "💼", "🎮", "📖", "🏠", "💡", "📅",
    "🎧", "🚶", "🎨", "❄️", "🔕", "🛠️", "🎹", "🎬",
    "🌿", "🖥️", "🚆", "🍴", "🛒", "🐾", "🎟️", "👨‍👩‍👧",
    "⭐", "⏰", "🧘", "✈️", "📍", "🏋️"
)

/**
 * 修改模式 dialog: big icon preview, name field, icon grid, 完成 button.
 * Uses a single primary button and ensures all content is scrollable.
 */
@Composable
fun EditModeDialog(
    show: Boolean,
    mode: Mode,
    isNew: Boolean = false,
    onDismissRequest: () -> Unit,
    onDone: (Mode) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    val englishDefaults = mapOf(
        "dnd" to "Do Not Disturb",
        "bedtime" to "Bedtime",
        "driving" to "Driving"
    )
    
    var name by remember(show, mode) {
        mutableStateOf(
            if (isNew) {
                // Start empty: the default name renders as a placeholder hint,
                // and onDone falls back to it when nothing is typed.
                ""
            } else if (englishDefaults[mode.id] == mode.name) {
                when (mode.id) {
                    "dnd" -> context.getString(R.string.mode_dnd)
                    "bedtime" -> context.getString(R.string.mode_bedtime)
                    "driving" -> context.getString(R.string.mode_driving)
                    else -> mode.name
                }
            } else {
                mode.name
            }
        )
    }
    var icon by remember(show, mode) { mutableStateOf(mode.icon.ifEmpty { "⭐" }) }

    WindowBottomSheet(
        show = show,
        onDismissRequest = onDismissRequest,
        title = if (isNew) stringResource(R.string.create_mode) else stringResource(R.string.edit_mode)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Big icon preview
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .squircleBackground(
                                    color = MiuixTheme.colorScheme.secondaryContainer,
                                    cornerRadius = 48.dp
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            top.yukonga.miuix.kmp.basic.Text(text = icon, fontSize = 44.sp)
                        }
                    }
                }

                // Name field: Centered Label
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        top.yukonga.miuix.kmp.basic.SmallTitle(
                            text = stringResource(R.string.mode_name),
                            modifier = Modifier.padding(bottom = 12.dp),
                            insideMargin = PaddingValues(0.dp) 
                        )
                    }
                }
                item {
                    top.yukonga.miuix.kmp.basic.TextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 5.dp)
                            .padding(bottom = 24.dp),
                        label = if (isNew) mode.name else "",
                        useLabelAsPlaceholder = true,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        )
                    )
                }

                // Icon grid: Centered Label
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        top.yukonga.miuix.kmp.basic.SmallTitle(
                            text = stringResource(R.string.choose_icon),
                            modifier = Modifier.padding(bottom = 12.dp),
                            insideMargin = PaddingValues(0.dp)
                        )
                    }
                }
                
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
                    ) {
                        MODE_ICON_CHOICES.chunked(5).forEach { rowChoices ->
                            Row(modifier = Modifier.fillMaxWidth()) {
                                rowChoices.forEach { choice ->
                                    val selected = icon == choice
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(6.dp)
                                            .aspectRatio(1f)
                                            .squircleSurface(
                                                color = if (selected) MiuixTheme.colorScheme.primary
                                                else MiuixTheme.colorScheme.secondaryContainer,
                                                cornerRadius = 24.dp
                                            )
                                            .clickable { icon = choice },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        top.yukonga.miuix.kmp.basic.Text(
                                            text = choice,
                                            fontSize = 28.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                repeat(5 - rowChoices.size) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }
            }

            // Bottom action: Single prominent blue pill button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .padding(top = 16.dp, bottom = 32.dp)
                    .height(60.dp)
                    .squircleSurface(
                        color = MiuixTheme.colorScheme.primary,
                        cornerRadius = 30.dp
                    )
                    .clickable {
                        onDone(mode.copy(
                            name = name.trim().ifEmpty { mode.name },
                            icon = icon,
                            statusIcon = ModeIconMapper.getStatusBarIcon(icon)
                        ))
                        onDismissRequest()
                    },
                contentAlignment = Alignment.Center
            ) {
                top.yukonga.miuix.kmp.basic.Text(
                    text = stringResource(R.string.done),
                    style = MiuixTheme.textStyles.body1,
                    color = MiuixTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )
            }
        }
    }
}
