package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager

class WifiTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var configs: Map<String, List<String>> = emptyMap()
    private var isReceiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                check()
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, List<String>>) {
        // Modes that dropped out of the config (trigger removed or mode deleted)
        // must be reported inactive, otherwise their stale tag would pin the
        // mode active forever in ComplexTriggerManager.
        (configs.keys - newConfigs.keys).forEach { callback(it, "wifi", false) }
        configs = newConfigs
        if (configs.isNotEmpty() && !isReceiverRegistered) {
            context.registerReceiver(receiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))
            isReceiverRegistered = true
        } else if (configs.isEmpty() && isReceiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {
                // Already unregistered; ignore.
            }
            isReceiverRegistered = false
        }
        check()
    }

    fun check() {
        val currentSsid = getCurrentSsid()
        configs.forEach { (modeId, ssids) ->
            val isActive = if (ssids.isEmpty()) {
                currentSsid != null
            } else {
                ssids.any { it == currentSsid }
            }
            callback(modeId, "wifi", isActive)
        }
    }

    private fun getCurrentSsid(): String? {
        @Suppress("DEPRECATION")
        val info = wifiManager.connectionInfo
        if (info == null || info.networkId == -1) return null
        var ssid = info.ssid
        if (ssid == null || ssid == "<unknown ssid>") return null
        if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
            ssid = ssid.substring(1, ssid.length - 1)
        }
        return ssid
    }
}
