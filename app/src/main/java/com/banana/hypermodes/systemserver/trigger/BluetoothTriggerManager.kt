package com.banana.hypermodes.systemserver.trigger

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.banana.hypermodes.utils.HyperLog

/**
 * Manages Bluetooth-based triggers.
 * Thread-safe implementation with proper synchronization and error handling.
 */
class BluetoothTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val bluetoothAdapter: BluetoothAdapter? = 
        context.getSystemService(BluetoothManager::class.java)?.adapter
    
    private val lock = Any()
    private var configs: Map<String, Pair<List<String>, Boolean>> = emptyMap()
    private var isReceiverRegistered = false
    private var isReleased = false
    private var lastConnectedDevices: Set<BluetoothDevice> = emptySet()
    private var lastCheckTime = 0L

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            synchronized(lock) {
                if (isReleased) return
            }

            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED,
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    check()
                }
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, Pair<List<String>, Boolean>>) {
        val oldConfigs = synchronized(lock) {
            if (isReleased) {
                Log.w(TAG, "Manager is released, ignoring updateConfigs")
                return
            }
            val old = configs
            configs = newConfigs
            old
        }

        // Report modes that dropped out of the config as inactive
        (oldConfigs.keys - newConfigs.keys).forEach { 
            callback(it, "bluetooth", false) 
        }

        // Register/unregister receiver based on config
        val hasConfigs = newConfigs.isNotEmpty()
        synchronized(lock) {
            if (hasConfigs && !isReceiverRegistered && !isReleased) {
                try {
                    val filter = IntentFilter().apply {
                        addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                        addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                    }
                    context.registerReceiver(receiver, filter)
                    isReceiverRegistered = true
                    HyperLog.d(TAG, "Bluetooth receiver registered")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register Bluetooth receiver: ${e.message}")
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

        val adapter = bluetoothAdapter
        if (adapter == null) {
            Log.w(TAG, "BluetoothAdapter is null")
            currentConfigs.keys.forEach { callback(it, "bluetooth", false) }
            return
        }

        if (!adapter.isEnabled) {
            currentConfigs.keys.forEach { callback(it, "bluetooth", false) }
            return
        }

        val connectedDevices = getConnectedBluetoothDevices()
        
        currentConfigs.forEach { (modeId, config) ->
            val (targetMacs, matchAnyCarAudio) = config
            
            val isActive = when {
                matchAnyCarAudio -> {
                    // Match car audio OR specific devices
                    connectedDevices.any { 
                        isCarAudioDevice(it) || targetMacs.any { mac -> 
                            mac.equals(it.address, ignoreCase = true) 
                        }
                    }
                }
                targetMacs.isNotEmpty() -> {
                    // Match specific devices (case-insensitive MAC comparison)
                    connectedDevices.any { device ->
                        targetMacs.any { mac -> 
                            mac.equals(device.address, ignoreCase = true) 
                        }
                    }
                }
                else -> false
            }
            
            callback(modeId, "bluetooth", isActive)
        }
    }

    private fun getConnectedBluetoothDevices(): Set<BluetoothDevice> {
        val adapter = bluetoothAdapter ?: return emptySet()
        
        // Cache connected devices for 1 second to avoid excessive checks
        val now = System.currentTimeMillis()
        if (now - lastCheckTime < 1000) {
            return lastConnectedDevices
        }

        val connected = mutableSetOf<BluetoothDevice>()
        
        try {
            val bondedDevices = adapter.bondedDevices ?: emptySet()
            
            for (device in bondedDevices) {
                if (isDeviceConnected(device)) {
                    connected.add(device)
                }
            }
        } catch (e: SecurityException) {
            Log.e(TAG, "SecurityException getting bonded devices: ${e.message}")
        } catch (e: Exception) {
            Log.e(TAG, "Error getting connected devices: ${e.message}")
        }

        lastConnectedDevices = connected
        lastCheckTime = now
        
        return connected
    }

    private fun isDeviceConnected(device: BluetoothDevice): Boolean {
        return try {
            // Try reflection first (hidden API)
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as? Boolean == true
        } catch (e: NoSuchMethodException) {
            // Method not available on this Android version
            // Log only once to avoid spam
            if (lastCheckTime == 0L) {
                Log.w(TAG, "isConnected() method not available, using fallback")
            }
            false
        } catch (e: Exception) {
            HyperLog.d(TAG, "Error checking device connection: ${e.message}")
            false
        }
    }

    private fun isCarAudioDevice(device: BluetoothDevice): Boolean {
        return try {
            val bluetoothClass = device.bluetoothClass
            bluetoothClass?.deviceClass == android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO
        } catch (e: Exception) {
            HyperLog.d(TAG, "Error checking car audio device: ${e.message}")
            false
        }
    }

    fun release() {
        synchronized(lock) {
            isReleased = true
            unregisterReceiverSafely()
            configs = emptyMap()
            lastConnectedDevices = emptySet()
        }
    }

    private fun unregisterReceiverSafely() {
        // Must be called within synchronized(lock)
        if (isReceiverRegistered) {
            try {
                context.unregisterReceiver(receiver)
                HyperLog.d(TAG, "Bluetooth receiver unregistered")
            } catch (e: IllegalArgumentException) {
                HyperLog.d(TAG, "Bluetooth receiver already unregistered")
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering Bluetooth receiver: ${e.message}")
            }
            isReceiverRegistered = false
        }
    }

    companion object {
        private const val TAG = "BluetoothTriggerManager"
    }
}
