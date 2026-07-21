package com.banana.hypermodes.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.manager.ModeManager
import com.topjohnwu.superuser.Shell
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ImmersionMore
import top.yukonga.miuix.kmp.theme.MiuixTheme

private const val PREF_NAME = "hypermodes_prefs"
private const val KEY_FIRST_LAUNCH = "first_launch"

sealed class Screen {
    object Welcome : Screen()
    object ModesList : Screen()
    data class ModeDetail(val mode: Mode) : Screen()
}

@Composable
fun HyperModesApp() {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE) }
    var currentScreen by remember {
        mutableStateOf<Screen>(
            if (prefs.getBoolean(KEY_FIRST_LAUNCH, true)) Screen.Welcome else Screen.ModesList
        )
    }

    MiuixTheme(
        darkTheme = isSystemInDarkTheme()
    ) {
        when (val screen = currentScreen) {
            is Screen.Welcome -> {
                WelcomeScreen(
                    onComplete = {
                        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
                        currentScreen = Screen.ModesList
                    }
                )
            }
            is Screen.ModesList -> {
                ModesListScreen(
                    onModeClick = { mode ->
                        currentScreen = Screen.ModeDetail(mode)
                    }
                )
            }
            is Screen.ModeDetail -> {
                ModeDetailScreen(
                    mode = screen.mode,
                    onBack = { currentScreen = Screen.ModesList },
                    onSave = { updatedMode ->
                        // TODO: Persist updated mode
                    }
                )
            }
        }
    }
}

@Composable
fun WelcomeScreen(onComplete: () -> Unit) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Welcome"
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome to HyperModes",
                        style = MiuixTheme.textStyles.headline1
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Minimise distractions and take control of your attention with modes for sleep, work, driving and everything in between.",
                        style = MiuixTheme.textStyles.body2,
                        color = MiuixTheme.colorScheme.onSurfaceVariantActions
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        color = MiuixTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Setup Required",
                                style = MiuixTheme.textStyles.subtitle
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "To activate the module, please force-stop DeskClock and reopen it.",
                                style = MiuixTheme.textStyles.body2
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    TextButton(
                        text = if (Shell.isAppGrantedRoot() == true) "Reload DeskClock" else "Open DeskClock Settings",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            val result = Shell.cmd(
                                "am force-stop com.android.deskclock",
                                "sleep 1",
                                "am start -n com.android.deskclock/.DeskClockTabActivity"
                            ).exec()

                            if (!result.isSuccess) {
                                context.startActivity(
                                    Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = android.net.Uri.parse("package:com.android.deskclock")
                                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    }
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        text = "Continue",
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColorsPrimary(),
                        onClick = onComplete
                    )
                }
            }
        }
    }
}

@Composable
fun ModesListScreen(onModeClick: (Mode) -> Unit) {
    val modes = remember { ModeManager.getDefaultModes() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Modes"
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            item {
                Text(
                    text = "Minimise distractions and take control of your attention with modes for sleep, work, driving and everything in between.",
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)
                )
            }

            // Mode items
            items(modes.size) { index ->
                val mode = modes[index]
                ModeItem(
                    icon = mode.icon,
                    title = mode.name,
                    subtitle = mode.description,
                    onClick = { onModeClick(mode) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Create your own mode
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    onClick = { /* TODO */ }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "+",
                            style = MiuixTheme.textStyles.headline2,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                        Text(
                            text = "Create your own mode",
                            style = MiuixTheme.textStyles.body1
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModeItem(
    icon: String,
    title: String,
    subtitle: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = icon,
                    style = MiuixTheme.textStyles.headline2,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Column {
                    Text(
                        text = title,
                        style = MiuixTheme.textStyles.body1
                    )
                    if (subtitle != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = subtitle,
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }
            Icon(
                imageVector = MiuixIcons.ImmersionMore,
                contentDescription = null,
                tint = MiuixTheme.colorScheme.onSurfaceVariantActions
            )
        }
    }
}
