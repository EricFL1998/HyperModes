package com.banana.hypermodes.systemserver.trigger

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class BluetoothTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val bluetoothAdapter: BluetoothAdapter? = context.getSystemService(BluetoothManager::class.java)?.adapter
    private var configs: Map<String, Pair<List<String>, Boolean>> = emptyMap()
    private var isReceiverRegistered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED,
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    check()
                }
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, Pair<List<String>, Boolean>>) {
        // Report modes that dropped out of the config as inactive so their
        // stale trigger tag doesn't pin the mode on forever.
        (configs.keys - newConfigs.keys).forEach { callback(it, "bluetooth", false) }
        configs = newConfigs
        if (configs.isNotEmpty() && !isReceiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            context.registerReceiver(receiver, filter)
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
        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            configs.keys.forEach { callback(it, "bluetooth", false) }
            return
        }

        val connectedDevices = getConnectedBluetoothDevices()
        configs.forEach { (modeId, config) ->
            val (targetMacs, matchAnyCarAudio) = config
            val isActive = if (matchAnyCarAudio) {
                connectedDevices.any { isCarAudioDevice(it) || targetMacs.contains(it.address) }
            } else if (targetMacs.isNotEmpty()) {
                connectedDevices.any { targetMacs.contains(it.address) }
            } else {
                connectedDevices.isNotEmpty()
            }
            callback(modeId, "bluetooth", isActive)
        }
    }

    private fun getConnectedBluetoothDevices(): Set<BluetoothDevice> {
        val adapter = bluetoothAdapter ?: return emptySet()
        val connected = mutableSetOf<BluetoothDevice>()
        try {
            val bondedDevices = adapter.bondedDevices ?: emptySet()
            for (device in bondedDevices) {
                try {
                    val method = device.javaClass.getMethod("isConnected")
                    if (method.invoke(device) as? Boolean == true) {
                        connected.add(device)
                    }
                } catch (e: Exception) {
                    // Ignore
                }
            }
        } catch (e: Exception) {
            // Ignore
        }
        return connected
    }

    private fun isCarAudioDevice(device: BluetoothDevice): Boolean {
        return try {
            val bluetoothClass = device.bluetoothClass
            bluetoothClass?.deviceClass == android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO
        } catch (e: Exception) {
            false
        }
    }
}
