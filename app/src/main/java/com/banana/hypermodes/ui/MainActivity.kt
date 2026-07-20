package com.banana.hypermodes.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.ui.theme.HyperModesTheme
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HyperModesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

private const val NO_RESPONSE_MESSAGE =
    "No response from module — is it enabled in LSPosed with DeskClock scope, " +
        "and has DeskClock been (force-)started since?"

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var sleepHour by remember { mutableIntStateOf(22) }
    var sleepMin by remember { mutableIntStateOf(30) }
    var wakeHour by remember { mutableIntStateOf(7) }
    var wakeMin by remember { mutableIntStateOf(30) }
    var days by remember { mutableStateOf((0..6).toSet()) }
    var stepLines by remember { mutableStateOf<List<String>>(emptyList()) }
    var inSleepMode by remember { mutableStateOf<Boolean?>(null) }
    var awaitingResult by remember { mutableStateOf(false) }

    // Per-step results from the hook (running in the DeskClock process).
    // EXPORTED because the sender is DeskClock's uid, which cannot hold our
    // signature permission; worst case a spoofed broadcast fakes status text.
    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(c: Context, intent: Intent) {
                @Suppress("DEPRECATION")
                stepLines =
                    intent.getStringArrayExtra(Protocol.EXTRA_STEPS)?.toList() ?: emptyList()
                inSleepMode = intent.getBooleanExtra(Protocol.EXTRA_IN_SLEEP_MODE, false)
                awaitingResult = false
            }
        }
        ContextCompat.registerReceiver(
            context, receiver, IntentFilter(Protocol.ACTION_RESULT),
            ContextCompat.RECEIVER_EXPORTED
        )
        onDispose { context.unregisterReceiver(receiver) }
    }

    // Timeout so a dead module doesn't leave the UI spinning forever.
    LaunchedEffect(awaitingResult) {
        if (awaitingResult) {
            delay(3000)
            if (awaitingResult) {
                awaitingResult = false
                stepLines = listOf(NO_RESPONSE_MESSAGE)
            }
        }
    }

    fun send(action: String, configure: Intent.() -> Unit = {}) {
        awaitingResult = true
        context.sendBroadcast(Intent(action).apply {
            setPackage(Protocol.TARGET_PACKAGE)
            configure()
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("HyperModes", style = MaterialTheme.typography.headlineLarge)
        Text(
            text = "Bedtime Control",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = when (inSleepMode) {
                true -> "Sleep mode: ON"
                false -> "Sleep mode: OFF"
                null -> "Sleep mode: unknown"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = when (inSleepMode) {
                true -> MaterialTheme.colorScheme.primary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // ---- Schedule editor ----
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sleep time", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPicker(sleepHour, 0..23) { sleepHour = it }
                    Text(":", style = MaterialTheme.typography.displayMedium)
                    NumberPicker(sleepMin, 0..59) { sleepMin = it }
                }
                Spacer(Modifier.height(8.dp))
                Text("Wake time", style = MaterialTheme.typography.titleMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    NumberPicker(wakeHour, 0..23) { wakeHour = it }
                    Text(":", style = MaterialTheme.typography.displayMedium)
                    NumberPicker(wakeMin, 0..59) { wakeMin = it }
                }
                Spacer(Modifier.height(8.dp))
                DaySelector(days) { days = it }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        send(Protocol.ACTION_APPLY_SCHEDULE) {
                            putExtra(Protocol.EXTRA_SLEEP_HOUR, sleepHour)
                            putExtra(Protocol.EXTRA_SLEEP_MIN, sleepMin)
                            putExtra(Protocol.EXTRA_WAKE_HOUR, wakeHour)
                            putExtra(Protocol.EXTRA_WAKE_MIN, wakeMin)
                            putExtra(Protocol.EXTRA_REPEAT_DAYS, Protocol.daysToBitmask(days))
                        }
                    },
                    enabled = !awaitingResult && days.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) { Text("APPLY SCHEDULE") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Manual control ----
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Manual control", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { send(Protocol.ACTION_STOP_BEDTIME) },
                        enabled = !awaitingResult
                    ) { Text("Stop bedtime") }
                    Button(
                        onClick = { send(Protocol.ACTION_START_BEDTIME) },
                        enabled = !awaitingResult
                    ) { Text("Start bedtime now") }
                }
                TextButton(
                    onClick = { send(Protocol.ACTION_QUERY_STATE) },
                    enabled = !awaitingResult
                ) { Text("Refresh state") }
                TextButton(
                    onClick = {
                        Shell.cmd("am force-stop com.android.deskclock").exec()
                    }
                ) { Text("Force-stop DeskClock") }
            }
        }

        Spacer(Modifier.height(16.dp))

        // ---- Status ----
        if (awaitingResult) {
            Text("⏳ Waiting for module…", style = MaterialTheme.typography.bodyLarge)
        } else if (stepLines.isNotEmpty()) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Last result", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    stepLines.forEach { line ->
                        Text(
                            text = line,
                            style = MaterialTheme.typography.bodySmall,
                            color = when {
                                "FAIL" in line -> MaterialTheme.colorScheme.error
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        Text(
            text = "Requires LSPosed — enable the module with DeskClock scope, then force-stop DeskClock once.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DaySelector(selected: Set<Int>, onChange: (Set<Int>) -> Unit) {
    val labels = listOf("M", "T", "W", "T", "F", "S", "S")
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        labels.forEachIndexed { index, label ->
            FilterChip(
                selected = index in selected,
                onClick = {
                    onChange(if (index in selected) selected - index else selected + index)
                },
                label = { Text(label) }
            )
        }
    }
}

@Composable
fun NumberPicker(value: Int, range: IntRange, onValueChange: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        IconButton(onClick = {
            onValueChange(if (value >= range.last) range.first else value + 1)
        }) {
            Text("▲", fontSize = 24.sp)
        }
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.displaySmall
        )
        IconButton(onClick = {
            onValueChange(if (value <= range.first) range.last else value - 1)
        }) {
            Text("▼", fontSize = 24.sp)
        }
    }
}
