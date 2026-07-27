package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.utils.RefreshRateManager
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowUpDown
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup

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
    val context = LocalContext.current
    var editedMode by remember { mutableStateOf(mode) }
    BackHandler(onBack = onBack)

    val scrollBehavior = MiuixScrollBehavior()
    var showRefreshRatePicker by remember { mutableStateOf(false) }

    val supportedRates = remember {
        RefreshRateManager.getCachedRefreshRates(context).ifEmpty { listOf(60, 90, 120, 144) }
    }

    val isSupported = remember {
        runCatching {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "ro.display.enable_pwm_switch", false) as Boolean
        }.getOrDefault(defaultValue = false)
    }

    val isEyeCareSupported = remember {
        runCatching {
            val featureParserClass = Class.forName("miui.util.FeatureParser")
            val getBooleanMethod = featureParserClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "support_qingshan_eyecare", false) as Boolean
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

            if (isEyeCareSupported) {
                item {
                    SettingItem(
                        title = stringResource(R.string.eye_care_mode),
                        subtitle = stringResource(R.string.eye_care_mode_desc),
                        checked = editedMode.settings.enableEyeCare,
                        onCheckedChange = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableEyeCare = enabled)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp)
                    )
                }
            }

            // Frame Rate Toggle + Value (Combined)
            item {
                RefreshRateSettingItem(
                    title = stringResource(R.string.refresh_rate_option),
                    subtitle = stringResource(R.string.refresh_rate_option_desc),
                    checked = editedMode.settings.enableRefreshRate,
                    onCheckedChange = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableRefreshRate = enabled)
                        )
                        onSave(editedMode)
                    },
                    value = stringResource(R.string.refresh_rate_unit, editedMode.settings.refreshRate),
                    onValueClick = { showRefreshRatePicker = true },
                    showPopup = showRefreshRatePicker,
                    onDismissPopup = { showRefreshRatePicker = false },
                    supportedRates = supportedRates,
                    onRateSelect = { rate ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(refreshRate = rate)
                        )
                        onSave(editedMode)
                        showRefreshRatePicker = false
                    },
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                )
            }

            // Bottom spacer with navigation bar padding
            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}

@Composable
fun RefreshRateSettingItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    value: String,
    onValueClick: () -> Unit,
    showPopup: Boolean,
    onDismissPopup: () -> Unit,
    supportedRates: List<Int>,
    onRateSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
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
                if (checked) {
                    Box {
                        Row(
                            modifier = Modifier.clickable(onClick = onValueClick),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = value,
                                style = MiuixTheme.textStyles.body2,
                                color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = MiuixIcons.Basic.ArrowUpDown,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MiuixTheme.colorScheme.onSurfaceVariantSummary
                            )
                        }

                        WindowListPopup(
                            show = showPopup,
                            popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
                            alignment = PopupPositionProvider.Align.TopEnd,
                            onDismissRequest = onDismissPopup
                        ) {
                            ListPopupColumn {
                                supportedRates.forEachIndexed { index, rate ->
                                    DropdownImpl(
                                        text = stringResource(R.string.refresh_rate_unit, rate),
                                        optionSize = supportedRates.size,
                                        isSelected = value.startsWith(rate.toString()),
                                        index = index,
                                        onSelectedIndexChange = {
                                            onRateSelect(rate)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}

