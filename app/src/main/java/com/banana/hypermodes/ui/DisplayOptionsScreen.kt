package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * 显示选项 sub-page: 过滤通知时的显示选项 / 灰度模式 / 让屏幕保持关闭状态 /
 * 调暗壁纸 / 启用深色主题.
 */
@Composable
fun DisplayOptionsScreen(
    mode: Mode,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
    var editedMode by remember { mutableStateOf(mode) }
    BackHandler(onBack = onBack)

    val scrollBehavior = MiuixScrollBehavior()

    val isSupported = remember {
        runCatching {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "ro.display.enable_pwm_switch", false) as Boolean
        }.getOrDefault(defaultValue = false)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.display_options),
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
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding())
        ) {
            // Top spacer
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Display options for filtered notifications
            item {
                SettingItem(
                    title = stringResource(R.string.display_when_filtered),
                    subtitle = stringResource(R.string.setting_hide_notifications_desc),
                    checked = editedMode.settings.hideNotifications,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(hideNotifications = enabled)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // Grayscale
            item {
                SettingItem(
                    title = stringResource(R.string.grayscale_mode),
                    subtitle = stringResource(R.string.grayscale_mode_desc),
                    checked = editedMode.settings.enableGrayscale,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableGrayscale = enabled)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // Keep screen off
            item {
                SettingItem(
                    title = stringResource(R.string.keep_screen_off),
                    subtitle = stringResource(R.string.keep_screen_off_desc),
                    checked = editedMode.settings.keepScreenOff,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(keepScreenOff = enabled)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // Dim wallpaper
            item {
                SettingItem(
                    title = stringResource(R.string.dim_wallpaper_option),
                    subtitle = stringResource(R.string.dim_wallpaper_option_desc),
                    checked = editedMode.settings.dimWallpaper,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(dimWallpaper = enabled)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // Dark theme
            item {
                SettingItem(
                    title = stringResource(R.string.dark_theme_option),
                    subtitle = stringResource(R.string.dark_theme_option_desc),
                    checked = editedMode.settings.enableDarkMode,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableDarkMode = enabled)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            if (isSupported) {
                item {
                    SettingItem(
                        title = stringResource(R.string.adaptive_refresh_rate_pro),
                        subtitle = stringResource(R.string.adaptive_refresh_rate_pro_desc),
                        checked = editedMode.settings.enableAdaptiveRefreshRatePro,
                        onCheckedChange = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableAdaptiveRefreshRatePro = enabled)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            }

            // Bottom spacer with navigation bar padding
            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}
