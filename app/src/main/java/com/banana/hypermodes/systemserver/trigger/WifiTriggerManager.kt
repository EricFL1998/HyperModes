package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.Log
import com.banana.hypermodes.utils.HyperLog

/**
 * Manages WiFi-based triggers.
 * Thread-safe implementation with proper synchronization.
 */
class WifiTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
    private val lock = Any()
    private var configs: Map<String, List<String>> = emptyMap()
    private var isReceiverRegistered = false
    private var isReleased = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            synchronized(lock) {
                if (isReleased) return
            }
            
            if (intent.action == WifiManager.NETWORK_STATE_CHANGED_ACTION) {
                check()
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, List<String>>) {
        val oldConfigs = synchronized(lock) {
            if (isReleased) {
                Log.w(TAG, "Manager is released, ignoring updateConfigs")
                return
            }
            val old = configs
            configs = newConfigs
            old
        }

        // Modes that dropped out of the config must be reported inactive
        (oldConfigs.keys - newConfigs.keys).forEach { 
            callback(it, "wifi", false) 
        }

        // Register/unregister receiver based on config
        val hasConfigs = newConfigs.isNotEmpty()
        synchronized(lock) {
            if (hasConfigs && !isReceiverRegistered && !isReleased) {
                try {
                    context.registerReceiver(receiver, IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION))
                    isReceiverRegistered = true
                    HyperLog.d(TAG, "WiFi receiver registered")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register WiFi receiver: ${e.message}")
                    // Don't set isReceiverRegistered = true
                }
            } else if (!hasConfigs && isReceiverRegistered) {
                unregisterReceiverSafely()
            }
        }

        check()
    }

    fun check() {
        val currentConfigs = synchronized(lock) {
            if (isReleased) return
            configs
        }

        if (currentConfigs.isEmpty()) return

        val currentSsid = getCurrentSsid()
        
        currentConfigs.forEach { (modeId, ssids) ->
            val isActive = if (ssids.isEmpty()) {
                // Empty list means "any WiFi connection"
                currentSsid != null
            } else {
                // Match specific SSIDs (case-insensitive)
                ssids.any { it.equals(currentSsid, ignoreCase = true) }
            }
            callback(modeId, "wifi", isActive)
        }
    }

    private fun getCurrentSsid(): String? {
        val manager = wifiManager
        if (manager == null) {
            Log.w(TAG, "WifiManager is null")
            return null
        }

        return try {
            @Suppress("DEPRECATION")
            val info = manager.connectionInfo
            if (info == null || info.networkId == -1) return null
            
            var ssid = info.ssid
            if (ssid == null || ssid == "<unknown ssid>") return null
            
            // Remove quotes if present
            if (ssid.startsWith("\"") && ssid.endsWith("\"")) {
                ssid = ssid.substring(1, ssid.length - 1)
            }
            
            ssid
        } catch (e: Exception) {
            Log.e(TAG, "Error getting current SSID: ${e.message}")
            null
        }
    }

    fun release() {
        synchronized(lock) {
            isReleased = true
            unregisterReceiverSafely()
            configs = emptyMap()
        }
    }

    private fun unregisterReceiverSafely() {
        // Must be called within synchronized(lock)
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
                HyperLog.d(TAG, "WiFi receiver unregistered")
            } catch (e: IllegalArgumentException) {
                HyperLog.d(TAG, "WiFi receiver already unregistered")
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering WiFi receiver: ${e.message}")
            }
            isReceiverRegistered = false
        }
    }

    companion object {
        private const val TAG = "WifiTriggerManager"
    }
}
