package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
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
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.squircle.squircleBackground
import top.yukonga.miuix.kmp.squircle.squircleSurface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import androidx.compose.foundation.clickable

/** Emoji icon choices for custom modes (stored directly as Mode.icon). */
val MODE_ICON_CHOICES = listOf(
    "⭐", "💼", "🏛️", "🏢", "💬", "👥", "💡", "📅",
    "⊝", "🚶", "⛳", "🏋️", "🏊", "🧗", "🤺", "🎮",
    "🎨", "❄️", "🔕", "🛠️", "🎹", "🎬", "📖", "🌿",
    "🎧", "🖥️", "🚆", "🚗", "🍴", "🛒", "🎰", "🐾",
    "🎟️", "👨‍👩‍👧", "❤️", "🏠", "🌙", "⏰", "🧘", "✈️"
)

/**
 * 修改模式 page: big icon preview, name field, icon grid, 完成 button.
 */
@Composable
fun EditModeScreen(
    mode: Mode,
    onBack: () -> Unit,
    onDone: (Mode) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    // Built-in modes are stored with their English default names; prefill the
    // rename field with the localized name (unless the user already renamed).
    val englishDefaults = mapOf(
        "dnd" to "Do Not Disturb",
        "bedtime" to "Bedtime",
        "driving" to "Driving"
    )
    var name by remember {
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
    var icon by remember { mutableStateOf(mode.icon.ifEmpty { "⭐" }) }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.edit_mode),
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
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            ) {
                TextButton(
                    text = stringResource(R.string.done),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColorsPrimary(),
                    onClick = {
                        onDone(mode.copy(name = name.trim().ifEmpty { mode.name }, icon = icon))
                    }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            // Bottom padding keeps the last icon rows clear of the 完成 button bar.
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = padding.calculateBottomPadding() + 24.dp
            )
        ) {
            // Big icon preview
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .squircleBackground(
                                MiuixTheme.colorScheme.secondaryContainer,
                                48.dp
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 44.sp)
                    }
                }
            }

            // Name field
            item {
                SmallTitle(
                    text = stringResource(R.string.mode_name),
                    modifier = Modifier.padding(start = 28.dp, bottom = 8.dp)
                )
            }
            item {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    )
                )
            }

            // Icon grid: plain rows (not a nested LazyVerticalGrid) so the outer
            // LazyColumn owns all scrolling and every icon stays reachable.
            item {
                SmallTitle(
                    text = stringResource(R.string.choose_icon),
                    modifier = Modifier.padding(start = 28.dp, top = 8.dp, bottom = 8.dp)
                )
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    MODE_ICON_CHOICES.chunked(4).forEach { rowChoices ->
                        Row(modifier = Modifier.fillMaxWidth()) {
                            rowChoices.forEach { choice ->
                                val selected = icon == choice
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(8.dp)
                                        .aspectRatio(1f)
                                        .squircleSurface(
                                            if (selected) MiuixTheme.colorScheme.primary
                                            else MiuixTheme.colorScheme.secondaryContainer,
                                            32.dp
                                        )
                                        .clickable { icon = choice },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = choice,
                                        fontSize = 28.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                            // Keep the last row's cells the same size when it's partial
                            repeat(4 - rowChoices.size) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
