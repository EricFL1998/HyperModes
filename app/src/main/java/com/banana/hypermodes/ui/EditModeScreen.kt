package com.banana.hypermodes.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme

/** Emoji icon choices for custom modes (stored directly as Mode.icon). */
val MODE_ICON_CHOICES = listOf(
    "⭐", "💼", "🏛️", "🏢", "💬", "👥", "💡", "📅",
    "⊝", "🚶", "⛳", "🏋️", "🏊", "🧗", "🤺", "🎮",
    "🎨", "❄️", "🔕", "🛠️", "🎹", "🎬", "📖", "🌿",
    "🎧", "🖥️", "🚆", "🚗", "🍴", "🛒", "🎰", "🐾",
    "🎟️", "👨‍👩‍👧", "❤️", "🏠", "🌙", "⏰", "🧘", "✈️"
)

/**
 * 修改模式 dialog: big icon preview, name field, icon grid, 完成 button.
 */
@Composable
fun EditModeDialog(
    show: Boolean,
    mode: Mode,
    onDismissRequest: () -> Unit,
    onDone: (Mode) -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    // Built-in modes are stored with their English default names; prefill the
    // rename field with the localized name (unless the user already renamed).
    val englishDefaults = mapOf(
        "dnd" to "Do Not Disturb",
        "bedtime" to "Bedtime",
        "driving" to "Driving"
    )
    
    var name by remember(show, mode) {
        mutableStateOf(
            if (englishDefaults[mode.id] == mode.name) {
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

    OverlayDialog(
        title = stringResource(R.string.edit_mode),
        show = show,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // Big icon preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .squircleBackground(
                            MiuixTheme.colorScheme.secondaryContainer,
                            40.dp
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 36.sp)
                }
            }

            // Name field
            SmallTitle(
                text = stringResource(R.string.mode_name),
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
            )
            TextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                    }
                )
            )

            // Icon grid label
            SmallTitle(
                text = stringResource(R.string.choose_icon),
                modifier = Modifier.padding(start = 12.dp, bottom = 8.dp)
            )
            
            // Icon grid: Wrap in a fixed-height scrollable area if needed, 
            // but OverlayDialog handles scrolling if the content is too large.
            // Using a simple Column here as OverlayDialog content is already inside a scrollable layout.
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                MODE_ICON_CHOICES.chunked(5).forEach { rowChoices ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        rowChoices.forEach { choice ->
                            val selected = icon == choice
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(4.dp)
                                    .aspectRatio(1f)
                                    .squircleSurface(
                                        if (selected) MiuixTheme.colorScheme.primary
                                        else MiuixTheme.colorScheme.secondaryContainer,
                                        20.dp
                                    )
                                    .clickable { icon = choice },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = choice,
                                    fontSize = 24.sp,
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

            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    text = stringResource(R.string.cancel),
                    onClick = onDismissRequest,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                TextButton(
                    text = stringResource(R.string.done),
                    onClick = {
                        onDone(mode.copy(name = name.trim().ifEmpty { mode.name }, icon = icon))
                        onDismissRequest()
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }
        }
    }
}
