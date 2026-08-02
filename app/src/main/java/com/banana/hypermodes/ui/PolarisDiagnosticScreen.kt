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
import com.banana.hypermodes.systemserver.trigger.PolarisProbeTestUtil
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

/**
 * Diagnostic screen for Polaris geofencing capability probe.
 *
 * This screen allows on-device testing of the Polaris adapter to determine
 * whether location triggers can be implemented on the target device.
 *
 * Usage: Add this screen to the navigation flow or invoke via debug menu.
 */
@Composable
fun PolarisDiagnosticScreen(
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    var probeResult by remember { mutableStateOf<PolarisProbeTestUtil.ProbeResult?>(null) }
    var isProbing by remember { mutableStateOf(false) }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = "Polaris Capability Probe",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = MiuixIcons.Back,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
        ) {
            // Instructions
            item {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Polaris Geofencing Probe",
                            style = MiuixTheme.textStyles.headline2
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "This probe tests whether Xiaomi Polaris geofencing service is available and allows non-SecurityCenter callers.",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Requirements:\n• LSPosed module active\n• System_server hook installed\n• HyperOS device with Security Center",
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Run Probe Button
            item {
                TextButton(
                    text = if (isProbing) "Probing..." else "Run Probe",
                    onClick = {
                        isProbing = true
                        probeResult = null
                        PolarisProbeTestUtil.runProbe(context) { result ->
                            probeResult = result
                            isProbing = false
                        }
                    },
                    enabled = !isProbing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColorsPrimary()
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Result Display
            probeResult?.let { result ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Probe Result",
                                style = MiuixTheme.textStyles.headline2
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Formatted diagnostic report
                            Text(
                                text = PolarisProbeTestUtil.formatDiagnosticReport(result),
                                style = MiuixTheme.textStyles.body2.copy(
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                ),
                                color = when (result) {
                                    is PolarisProbeTestUtil.ProbeResult.Supported ->
                                        MiuixTheme.colorScheme.primary
                                    is PolarisProbeTestUtil.ProbeResult.Unsupported ->
                                        MiuixTheme.colorScheme.error
                                    is PolarisProbeTestUtil.ProbeResult.Error ->
                                        MiuixTheme.colorScheme.onSurfaceVariantSummary
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
