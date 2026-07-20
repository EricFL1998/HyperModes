package com.banana.hypermodes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.banana.hypermodes.ui.theme.HyperModesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

@Composable
fun MainScreen() {
    val context = LocalContext.current

    var hour by remember { mutableStateOf(22) }
    var min by remember { mutableStateOf(30) }
    var wakeHour by remember { mutableStateOf(7) }
    var wakeMin by remember { mutableStateOf(30) }
    var status by remember { mutableStateOf("Ready") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "HyperModes",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "Bedtime Control",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        // Sleep Time
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Sleep Time", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberPicker(value = hour, range = 0..23, onValueChange = { hour = it })
                    Text(":", style = MaterialTheme.typography.displayMedium)
                    NumberPicker(value = min, range = 0..59, onValueChange = { min = it })
                }
            }
        }

        // Wake Time
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Wake Time", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberPicker(value = wakeHour, range = 0..23, onValueChange = { wakeHour = it })
                    Text(":", style = MaterialTheme.typography.displayMedium)
                    NumberPicker(value = wakeMin, range = 0..59, onValueChange = { wakeMin = it })
                }
            }
        }

        // Status
        Text(
            text = status,
            style = MaterialTheme.typography.bodyLarge,
            color = when {
                status.startsWith("✅") -> MaterialTheme.colorScheme.primary
                status.startsWith("❌") -> MaterialTheme.colorScheme.error
                status.startsWith("⏳") -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.onSurface
            },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Update Button
        Button(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    status = "⏳ Sending to LSPosed module..."

                    // Send broadcast to LSPosed module hook
                    val intent = Intent("com.banana.hypermodes.UPDATE_BEDTIME").apply {
                        putExtra("sleepHour", hour)
                        putExtra("sleepMin", min)
                        putExtra("wakeHour", wakeHour)
                        putExtra("wakeMin", wakeMin)
                        putExtra("repeat", 127)
                    }
                    context.sendBroadcast(intent)

                    delay(1000)
                    status = "✅ Broadcast sent! Check Clock app"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("UPDATE BEDTIME", style = MaterialTheme.typography.titleMedium)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "⚠️ Requires LSPosed module\nActivate in LSPosed Manager\nScope: DeskClock only",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NumberPicker(
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        IconButton(
            onClick = {
                val newValue = if (value >= range.last) range.first else value + 1
                onValueChange(newValue)
            }
        ) {
            Text("▲", fontSize = 32.sp)
        }
        Text(
            text = value.toString().padStart(2, '0'),
            style = MaterialTheme.typography.displayLarge,
            modifier = Modifier.padding(vertical = 8.dp)
        )
        IconButton(
            onClick = {
                val newValue = if (value <= range.first) range.last else value - 1
                onValueChange(newValue)
            }
        ) {
            Text("▼", fontSize = 32.sp)
        }
    }
}
