package com.banana.hypermodes.ui

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.compose.ui.unit.dp
import com.banana.hypermodes.R
import com.banana.hypermodes.data.LocationTarget
import com.banana.hypermodes.data.LocationTransition
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeTrigger
import com.banana.hypermodes.data.ModeTriggerGroup
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic
import java.util.UUID
import kotlin.math.*

@Composable
fun LocationTriggerPickerScreen(
    mode: Mode,
    onBack: () -> Unit,
    onSave: (Mode) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var selectedTransition by remember { mutableStateOf(LocationTransition.ARRIVE) }
    var showTransitionDialog by remember { mutableStateOf(false) }

    val scrollBehavior = MiuixScrollBehavior()

    // Check if SecurityAdd is available
    val isSecurityAddAvailable = remember {
        try {
            context.packageManager.getPackageInfo("com.miui.securityadd", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    // SecurityAdd map picker launcher
    val mapPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val latitude = data?.getDoubleExtra("latitude", 0.0) ?: 0.0
            val longitude = data?.getDoubleExtra("longitude", 0.0) ?: 0.0
            val cityName = data?.getStringExtra("cityName")
            val provinceName = data?.getStringExtra("provinceName")
            val addressName = data?.getStringExtra("addressName")

            // Validate coordinates
            if (latitude != 0.0 && longitude != 0.0 && isValidCoordinate(latitude, longitude)) {
                // Apply China coordinate conversion (GCJ-02)
                val converted = convertToGCJ02(latitude, longitude)

                val trigger = ModeTrigger.Location(
                    id = UUID.randomUUID().toString(),
                    target = LocationTarget(
                        latitude = converted.first,
                        longitude = converted.second,
                        radius = 500,
                        addressName = addressName,
                        cityName = cityName,
                        provinceName = provinceName
                    ),
                    transition = selectedTransition
                )

                val newGroups = mode.settings.triggerGroups + ModeTriggerGroup.Single(trigger)
                val updatedMode = mode.copy(
                    settings = mode.settings.copy(triggerGroups = newGroups)
                )
                onSave(updatedMode)
                onBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.location_picker_title),
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
            if (!isSecurityAddAvailable) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        insideMargin = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.location_unsupported),
                            style = MiuixTheme.textStyles.body1,
                            color = MiuixTheme.colorScheme.error
                        )
                    }
                }
            } else {
                // Transition type selector
                item {
                    SmallTitle(
                        text = stringResource(R.string.trigger_location),
                        modifier = Modifier.padding(start = 28.dp, top = 16.dp, bottom = 8.dp)
                    )
                }

                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        onClick = { showTransitionDialog = true }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (selectedTransition) {
                                    LocationTransition.ARRIVE -> stringResource(R.string.location_transition_arrive)
                                    LocationTransition.LEAVE -> stringResource(R.string.location_transition_leave)
                                },
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                }

                // Select location button
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        onClick = {
                            // Launch SecurityAdd map picker
                            val intent = Intent().apply {
                                setClassName(
                                    "com.miui.securityadd",
                                    "com.miui.auto_task.MapSelectActivity"
                                )
                            }
                            try {
                                mapPickerLauncher.launch(intent)
                            } catch (e: Exception) {
                                // Handle launch failure
                            }
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "＋",
                                style = MiuixTheme.textStyles.title2,
                                color = MiuixTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.location_picker_title),
                                style = MiuixTheme.textStyles.body1
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }

        // Transition selection dialog
        if (showTransitionDialog) {
            top.yukonga.miuix.kmp.overlay.OverlayDialog(
                show = showTransitionDialog,
                onDismissRequest = { showTransitionDialog = false }
            ) {
                Column {
                    listOf(
                        LocationTransition.ARRIVE to stringResource(R.string.location_transition_arrive),
                        LocationTransition.LEAVE to stringResource(R.string.location_transition_leave)
                    ).forEach { (transition, label) ->
                        val selected = selectedTransition == transition
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedTransition = transition
                                    showTransitionDialog = false
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selected) {
                                Text(
                                    text = "✓",
                                    style = MiuixTheme.textStyles.body1,
                                    color = MiuixTheme.colorScheme.primary,
                                    modifier = Modifier.padding(end = 12.dp)
                                )
                            } else {
                                Spacer(modifier = Modifier.width(24.dp))
                            }
                            Text(
                                text = label,
                                style = MiuixTheme.textStyles.body1,
                                color = if (selected) MiuixTheme.colorScheme.primary
                                else MiuixTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        text = stringResource(R.string.cancel),
                        onClick = { showTransitionDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

private fun isValidCoordinate(latitude: Double, longitude: Double): Boolean {
    return latitude >= -90 && latitude <= 90 && longitude >= -180 && longitude <= 180
}

/**
 * Convert WGS-84 coordinates to GCJ-02 (China's coordinate system).
 * Security Center uses GCJ-02, so we apply the conversion exactly once before persistence.
 */
private fun convertToGCJ02(wgsLat: Double, wgsLon: Double): Pair<Double, Double> {
    // WGS-84 to GCJ-02 transformation (simplified)
    // This is a rough approximation; production code should use a proper library
    val a = 6378245.0
    val ee = 0.00669342162296594323

    fun transformLat(x: Double, y: Double): Double {
        var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(abs(x))
        ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(y * PI) + 40.0 * sin(y / 3.0 * PI)) * 2.0 / 3.0
        ret += (160.0 * sin(y / 12.0 * PI) + 320 * sin(y * PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * sqrt(abs(x))
        ret += (20.0 * sin(6.0 * x * PI) + 20.0 * sin(2.0 * x * PI)) * 2.0 / 3.0
        ret += (20.0 * sin(x * PI) + 40.0 * sin(x / 3.0 * PI)) * 2.0 / 3.0
        ret += (150.0 * sin(x / 12.0 * PI) + 300.0 * sin(x / 30.0 * PI)) * 2.0 / 3.0
        return ret
    }

    val dLat = transformLat(wgsLon - 105.0, wgsLat - 35.0)
    val dLon = transformLon(wgsLon - 105.0, wgsLat - 35.0)
    val radLat = wgsLat / 180.0 * PI
    var magic = sin(radLat)
    magic = 1 - ee * magic * magic
    val sqrtMagic = sqrt(magic)
    val deltaLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * PI)
    val deltaLon = (dLon * 180.0) / (a / sqrtMagic * cos(radLat) * PI)

    return Pair(wgsLat + deltaLat, wgsLon + deltaLon)
}
