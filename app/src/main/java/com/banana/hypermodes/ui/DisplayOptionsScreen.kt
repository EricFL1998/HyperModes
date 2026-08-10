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
import com.banana.hypermodes.ui.components.DropdownSettingItem
import com.banana.hypermodes.utils.RefreshRateManager
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowUpDown
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import top.yukonga.miuix.kmp.window.WindowListPopup

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

    val supportedRates = remember {
        RefreshRateManager.getCachedRefreshRates(context).ifEmpty { listOf(60, 90, 120, 144) }
    }

    val isAdaptiveRefreshRateProSupported = remember {
        runCatching {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getBooleanMethod = systemPropertiesClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "ro.display.enable_pwm_switch", false) as Boolean
        }.getOrDefault(defaultValue = true)
    }

    val isRefreshRateSupported = remember {
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

    val isAodSupported = remember {
        runCatching {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            val aodMask = getMethod.invoke(null, "ro.miui.special.aod.mask") as String
            val miuiVersion = getMethod.invoke(null, "ro.miui.ui.version.name") as String
            aodMask.isNotEmpty() || miuiVersion.isNotEmpty()
        }.getOrDefault(defaultValue = true)
    }

    val isGestureWakeupSupported = remember {
        runCatching {
            val featureParserClass = Class.forName("miui.util.FeatureParser")
            val getBooleanMethod = featureParserClass.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)
            getBooleanMethod.invoke(null, "support_gesture_wakeup", false) as Boolean
        }.getOrDefault(defaultValue = true)
    }

    val booleanOptions = listOf(
        true to stringResource(R.string.option_on),
        false to stringResource(R.string.option_off)
    )

    val themeOptions = listOf(
        0 to stringResource(R.string.theme_light),
        1 to stringResource(R.string.theme_dark)
    )

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
            item {
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Theme Override
            item {
                DropdownSettingItem(
                    title = stringResource(R.string.theme_option),
                    subtitle = stringResource(R.string.theme_option_desc),
                    selected = editedMode.settings.darkMode != null,
                    onToggle = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(darkMode = if (enabled) 1 else null)
                        )
                        onSave(editedMode)
                    },
                    value = editedMode.settings.darkMode ?: 1,
                    options = themeOptions,
                    onValueChange = { value ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(darkMode = value)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Always-on Display
            if (isAodSupported) {
                item {
                    DropdownSettingItem(
                        title = stringResource(R.string.aod_option),
                        subtitle = stringResource(R.string.aod_option_desc),
                        selected = editedMode.settings.enableAod != null,
                        onToggle = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableAod = if (enabled) true else null)
                            )
                            onSave(editedMode)
                        },
                        value = editedMode.settings.enableAod ?: true,
                        options = booleanOptions,
                        onValueChange = { value ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableAod = value)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Grayscale
            item {
                DropdownSettingItem(
                    title = stringResource(R.string.grayscale_mode),
                    subtitle = stringResource(R.string.grayscale_mode_desc),
                    selected = editedMode.settings.enableGrayscale != null,
                    onToggle = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableGrayscale = if (enabled) true else null)
                        )
                        onSave(editedMode)
                    },
                    value = editedMode.settings.enableGrayscale ?: true,
                    options = booleanOptions,
                    onValueChange = { value ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableGrayscale = value)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Refresh Rate (Keep existing UI as it's already a complex override)
            if (isRefreshRateSupported) {
                item {
                    RefreshRateSettingItem(
                        title = stringResource(R.string.refresh_rate_option),
                        subtitle = stringResource(R.string.refresh_rate_option_desc),
                        checked = editedMode.settings.enableRefreshRate == true,
                        onCheckedChange = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableRefreshRate = if (enabled) true else null)
                            )
                            onSave(editedMode)
                        },
                        value = stringResource(R.string.refresh_rate_unit, editedMode.settings.refreshRate),
                        supportedRates = supportedRates,
                        onRateSelect = { rate ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(refreshRate = rate)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Eye Care
            if (isEyeCareSupported) {
                item {
                    DropdownSettingItem(
                        title = stringResource(R.string.eye_care_mode),
                        subtitle = stringResource(R.string.eye_care_mode_desc),
                        selected = editedMode.settings.enableEyeCare != null,
                        onToggle = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableEyeCare = if (enabled) true else null)
                            )
                            onSave(editedMode)
                        },
                        value = editedMode.settings.enableEyeCare ?: true,
                        options = booleanOptions,
                        onValueChange = { value ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableEyeCare = value)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Adaptive Refresh Rate Pro
            if (isAdaptiveRefreshRateProSupported) {
                item {
                    DropdownSettingItem(
                        title = stringResource(R.string.adaptive_refresh_rate_pro),
                        subtitle = stringResource(R.string.adaptive_refresh_rate_pro_desc),
                        selected = editedMode.settings.enableAdaptiveRefreshRatePro != null,
                        onToggle = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableAdaptiveRefreshRatePro = if (enabled) true else null)
                            )
                            onSave(editedMode)
                        },
                        value = editedMode.settings.enableAdaptiveRefreshRatePro ?: true,
                        options = booleanOptions,
                        onValueChange = { value ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableAdaptiveRefreshRatePro = value)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Raise to Wake
            if (isGestureWakeupSupported) {
                item {
                    DropdownSettingItem(
                        title = stringResource(R.string.raise_to_wake),
                        subtitle = stringResource(R.string.raise_to_wake_desc),
                        selected = editedMode.settings.enableRaiseToWake != null,
                        onToggle = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableRaiseToWake = if (enabled) true else null)
                            )
                            onSave(editedMode)
                        },
                        value = editedMode.settings.enableRaiseToWake ?: true,
                        options = booleanOptions,
                        onValueChange = { value ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enableRaiseToWake = value)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // Wake for Notifications
            item {
                DropdownSettingItem(
                    title = stringResource(R.string.wake_for_notifications),
                    subtitle = stringResource(R.string.wake_for_notifications_desc),
                    selected = editedMode.settings.enableWakeForNotifications != null,
                    onToggle = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableWakeForNotifications = if (enabled) true else null)
                        )
                        onSave(editedMode)
                    },
                    value = editedMode.settings.enableWakeForNotifications ?: true,
                    options = booleanOptions,
                    onValueChange = { value ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableWakeForNotifications = value)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

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
    supportedRates: List<Int>,
    onRateSelect: (Int) -> Unit,
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
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
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
                            modifier = Modifier.clickable { showPopup = true },
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
                            onDismissRequest = { showPopup = false }
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
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }
        }
    }
}
