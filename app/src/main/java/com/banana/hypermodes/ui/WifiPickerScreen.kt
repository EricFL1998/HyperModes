package com.banana.hypermodes.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.ResultReceiver
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.banana.hypermodes.R
import com.banana.hypermodes.protocol.Protocol
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.Back
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun WifiPickerScreen(
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val wifiManager = remember { context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager }

    var ssids by remember { mutableStateOf<List<String>?>(null) }
    var query by remember { mutableStateOf("") }

    // Reading the current SSID requires fine location on Android 8.1+.
    // Reload after the user answers the permission prompt.
    var permissionVersion by remember { mutableStateOf(0) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { permissionVersion++ }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    LaunchedEffect(permissionVersion) {
        // Saved networks are only visible to system_server since Android 10 —
        // ask the module bridge for them and fall back to the current SSID.
        val current = currentSsid(wifiManager)
        querySavedSsids(context) { saved ->
            val merged = (saved.orEmpty() + listOfNotNull(current)).distinct()
            ssids = merged.sortedWith(compareBy({ it != current }, { it.lowercase() }))
        }
    }

    val scrollBehavior = MiuixScrollBehavior()

    Scaffold(
        topBar = {
            TopAppBar(
                title = stringResource(R.string.trigger_wifi),
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

            item {
                SearchBar(
                    modifier = Modifier.padding(bottom = 12.dp),
                    inputField = {
                        InputField(
                            query = query,
                            onQueryChange = { query = it },
                            onSearch = { },
                            expanded = false,
                            onExpandedChange = { },
                            label = stringResource(R.string.search_wifi)
                        )
                    },
                    expanded = false,
                    onExpandedChange = { }
                ) { }
            }

            val list = ssids
            if (list == null) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                val filtered = if (query.isBlank()) list else list.filter { it.contains(query, ignoreCase = true) }
                if (filtered.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.no_wifi_networks),
                            style = MiuixTheme.textStyles.body2,
                            color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                        )
                    }
                }
                items(filtered) { ssid ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        insideMargin = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                        onClick = {
                            onSelect(ssid)
                            onBack()
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "📶", modifier = Modifier.padding(end = 12.dp))
                            Text(
                                text = ssid,
                                style = MiuixTheme.textStyles.body1,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp).navigationBarsPadding())
            }
        }
    }
}

private fun currentSsid(wifiManager: WifiManager): String? {
    val info = wifiManager.connectionInfo
    if (info == null || info.networkId == -1) return null
    val ssid = info.ssid?.removeSurrounding("\"")
    return if (!ssid.isNullOrEmpty() && ssid != "<unknown ssid>") ssid else null
}

/**
 * Ask the system_server bridge for saved networks. onResult runs on the main
 * thread exactly once; null means the bridge is unavailable (module disabled
 * or an older build) and the caller falls back to the current SSID.
 */
private fun querySavedSsids(context: Context, onResult: (List<String>?) -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    var delivered = false
    fun deliver(result: List<String>?) {
        if (delivered) return
        delivered = true
        onResult(result)
    }
    val timeout = Runnable { deliver(null) }
    handler.postDelayed(timeout, 1500)

    val receiver = object : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle?) {
            handler.removeCallbacks(timeout)
            deliver(resultData?.getStringArray(Protocol.EXTRA_SSIDS)?.toList())
        }
    }
    try {
        context.sendBroadcast(Intent(Protocol.ACTION_GET_CONFIGURED_WIFI).apply {
            setPackage(Protocol.FRAMEWORK_PACKAGE)
            putExtra(Protocol.EXTRA_RESULT_RECEIVER, receiver)
        })
    } catch (t: Throwable) {
        handler.removeCallbacks(timeout)
        deliver(null)
    }
}
