package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.ui.components.DropdownSettingItem
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun DeviceControlScreen(
    mode: Mode,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
    val context = LocalContext.current
    var editedMode by remember { mutableStateOf(mode) }
    BackHandler(onBack = onBack)

    val scrollBehavior = MiuixScrollBehavior()

    val is5gSupported = remember {
        runCatching {
            val systemPropertiesClass = Class.forName("android.os.SystemProperties")
            val getMethod = systemPropertiesClass.getMethod("get", String::class.java)
            val support5g = getMethod.invoke(null, "ro.miui.support_5g_choice") as String
            support5g.isNotEmpty()
        }.getOrDefault(defaultValue = true)
    }

    val booleanOptions = listOf(
        true to stringResource(R.string.option_on),
        false to stringResource(R.string.option_off)
    )

    val performanceModes = listOf(
        0 to stringResource(R.string.performance_balanced),
        1 to stringResource(R.string.performance_high),
        2 to stringResource(R.string.performance_power_save)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.device_control),
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

            // Performance Mode
            item {
                DropdownSettingItem(
                    title = stringResource(R.string.performance_mode),
                    subtitle = stringResource(R.string.performance_mode_desc),
                    selected = editedMode.settings.performanceMode != null,
                    onToggle = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(performanceMode = if (enabled) 0 else null)
                        )
                        onSave(editedMode)
                    },
                    value = editedMode.settings.performanceMode ?: 0,
                    options = performanceModes,
                    onValueChange = { value ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(performanceMode = value)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // 5G Network
            if (is5gSupported) {
                item {
                    DropdownSettingItem(
                        title = stringResource(R.string.five_g_network),
                        subtitle = stringResource(R.string.five_g_network_desc),
                        selected = editedMode.settings.enable5g != null,
                        onToggle = { enabled ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enable5g = if (enabled) true else null)
                            )
                            onSave(editedMode)
                        },
                        value = editedMode.settings.enable5g ?: true,
                        options = booleanOptions,
                        onValueChange = { value ->
                            editedMode = editedMode.copy(
                                settings = editedMode.settings.copy(enable5g = value)
                            )
                            onSave(editedMode)
                        },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            // WiFi Control
            item {
                DropdownSettingItem(
                    title = stringResource(R.string.wifi_control),
                    subtitle = stringResource(R.string.wifi_control_desc),
                    selected = editedMode.settings.enableWifi != null,
                    onToggle = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableWifi = if (enabled) true else null)
                        )
                        onSave(editedMode)
                    },
                    value = editedMode.settings.enableWifi ?: true,
                    options = booleanOptions,
                    onValueChange = { value ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableWifi = value)
                        )
                        onSave(editedMode)
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Bluetooth Control
            item {
                DropdownSettingItem(
                    title = stringResource(R.string.bluetooth_control),
                    subtitle = stringResource(R.string.bluetooth_control_desc),
                    selected = editedMode.settings.enableBluetooth != null,
                    onToggle = { enabled ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableBluetooth = if (enabled) true else null)
                        )
                        onSave(editedMode)
                    },
                    value = editedMode.settings.enableBluetooth ?: true,
                    options = booleanOptions,
                    onValueChange = { value ->
                        editedMode = editedMode.copy(
                            settings = editedMode.settings.copy(enableBluetooth = value)
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
