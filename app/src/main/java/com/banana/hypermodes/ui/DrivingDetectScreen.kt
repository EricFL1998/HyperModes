package com.banana.hypermodes.ui

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.DRIVING_DETECT_BLUETOOTH
import com.banana.hypermodes.data.DRIVING_DETECT_MOTION_BLUETOOTH
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.driving.DrivingDetector
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * 驾车勿扰 page (Pixel's driving detection setup): illustration on top,
 * radio choice between Bluetooth-only and motion+Bluetooth activation,
 * plus the explanatory info text at the bottom.
 */
@Composable
fun DrivingDetectScreen(
    mode: Mode,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
    BackHandler(onBack = onBack)
    var editedMode by remember { mutableStateOf(mode) }
    val scrollBehavior = MiuixScrollBehavior()
    val context = LocalContext.current

    // Runtime permissions needed per detection source.
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        DrivingDetector.ensureActivityRecognition(context)
    }

    fun select(detectMode: Int) {
        editedMode = editedMode.copy(
            settings = editedMode.settings.copy(drivingDetectMode = detectMode)
        )
        onSave(editedMode)
        permissionLauncher.launch(
            when (detectMode) {
                DRIVING_DETECT_MOTION_BLUETOOTH -> arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                )
                else -> arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT)
            }
        )
    }

    LaunchedEffect(Unit) {
        DrivingDetector.ensureActivityRecognition(context)
        // The default option (使用蓝牙) is pre-selected — request its runtime
        // permissions on page entry instead of waiting for a redundant tap.
        if (editedMode.settings.drivingAutoDetect) {
            val needed = when (editedMode.settings.drivingDetectMode) {
                DRIVING_DETECT_MOTION_BLUETOOTH -> arrayOf(
                    android.Manifest.permission.BLUETOOTH_CONNECT,
                    android.Manifest.permission.ACTIVITY_RECOGNITION
                )
                else -> arrayOf(android.Manifest.permission.BLUETOOTH_CONNECT)
            }.filter {
                androidx.core.content.ContextCompat.checkSelfPermission(context, it) !=
                        android.content.pm.PackageManager.PERMISSION_GRANTED
            }
            if (needed.isNotEmpty()) permissionLauncher.launch(needed.toTypedArray())
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.driving_dnd),
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
            // Illustration (stands in for Pixel's driving artwork)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp, bottom = 12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🚗",
                            fontSize = 96.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Radio option: Bluetooth only
            item {
                DrivingDetectOption(
                    title = stringResource(R.string.use_bluetooth),
                    subtitle = stringResource(R.string.use_bluetooth_desc),
                    selected = editedMode.settings.drivingDetectMode == DRIVING_DETECT_BLUETOOTH,
                    onClick = { select(DRIVING_DETECT_BLUETOOTH) }
                )
            }

            // Radio option: motion + Bluetooth
            item {
                DrivingDetectOption(
                    title = stringResource(R.string.use_motion_bluetooth),
                    subtitle = stringResource(R.string.use_motion_bluetooth_desc),
                    selected = editedMode.settings.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH,
                    onClick = { select(DRIVING_DETECT_MOTION_BLUETOOTH) }
                )
            }

            // Info text
            item {
                Text(
                    text = stringResource(R.string.driving_dnd_info),
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                )
            }

            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}

@Composable
private fun DrivingDetectOption(
    title: String,
    subtitle: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 12.dp),
        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.body1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MiuixTheme.textStyles.body2,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                )
            }
        }
    }
}
