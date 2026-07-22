package com.banana.hypermodes.systemserver.trigger

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ModeType

/**
 * Manages driving mode detection with bluetooth priority logic.
 *
 * Strategy (蓝牙优先逻辑):
 * 1. If bluetooth trigger enabled AND bluetooth connected to target device → activate driving mode
 * 2. Else if motion trigger enabled AND speed > threshold → activate driving mode
 * 3. Otherwise → deactivate driving mode
 *
 * Note: Motion detection (ActivityRecognition API) is not implemented yet.
 * For now, only bluetooth detection is functional.
 */
class DrivingTriggerManager(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var currentDrivingModeId: String? = null
    private var drivingModes: List<ModeConfig> = emptyList()
    private var isReceiverRegistered = false
    private var isInitialized = false

    /**
     * Initialize with mode configurations.
     * Registers bluetooth receiver if there are DYNAMIC_TRIGGER modes.
     */
    fun init(modes: List<ModeConfig>) {
        if (isInitialized) {
            // Already initialized, just update modes
            updateModes(modes)
            return
        }

        drivingModes = modes.filter { it.type == ModeType.DYNAMIC_TRIGGER }

        if (drivingModes.isEmpty()) {
            log("No driving modes configured")
            isInitialized = true
            return
        }

        log("Initializing DrivingTriggerManager with ${drivingModes.size} driving modes")

        // Register bluetooth receiver
        registerBluetoothReceiver()

        // Check initial state
        checkDrivingConditions()

        isInitialized = true
    }

    /**
     * Update mode configurations.
     * Called when config changes in Settings.Global.
     */
    fun updateModes(modes: List<ModeConfig>) {
        val newDrivingModes = modes.filter { it.type == ModeType.DYNAMIC_TRIGGER }

        if (newDrivingModes.isEmpty() && drivingModes.isNotEmpty()) {
            // No more driving modes - unregister receiver
            unregisterBluetoothReceiver()
            deactivateDrivingMode()
        } else if (newDrivingModes.isNotEmpty() && drivingModes.isEmpty()) {
            // New driving modes added - register receiver
            registerBluetoothReceiver()
        }

        drivingModes = newDrivingModes

        // Recheck conditions with new config
        checkDrivingConditions()
    }

    private fun registerBluetoothReceiver() {
        if (isReceiverRegistered) return

        try {
            val btFilter = IntentFilter().apply {
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
            }
            context.registerReceiver(bluetoothReceiver, btFilter)
            isReceiverRegistered = true
            log("Bluetooth receiver registered")
        } catch (e: Exception) {
            log("Failed to register bluetooth receiver: ${e.message}")
        }
    }

    private fun unregisterBluetoothReceiver() {
        if (!isReceiverRegistered) return

        try {
            context.unregisterReceiver(bluetoothReceiver)
            isReceiverRegistered = false
            log("Bluetooth receiver unregistered")
        } catch (e: Exception) {
            log("Failed to unregister bluetooth receiver: ${e.message}")
        }
    }

    private val bluetoothReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
            } else {
                @Suppress("DEPRECATION")
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
            }

            when (intent.action) {
                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    log("Bluetooth connected: ${device?.address}")
                    checkDrivingConditions()
                }
                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    log("Bluetooth disconnected: ${device?.address}")
                    checkDrivingConditions()
                }
            }
        }
    }

    /**
     * Check driving conditions with bluetooth priority logic.
     *
     * Priority 1: Bluetooth trigger
     * Priority 2: Motion trigger (TODO - not implemented yet)
     */
    private fun checkDrivingConditions() {
        if (drivingModes.isEmpty()) {
            deactivateDrivingMode()
            return
        }

        for (mode in drivingModes) {
            val triggers = mode.triggers ?: continue

            // Priority 1: Bluetooth
            if (triggers.bluetooth?.enabled == true) {
                val connected = isBluetoothConnectedToTargets(
                    triggers.bluetooth.targetMacs,
                    triggers.bluetooth.matchAnyCarAudio
                )
                if (connected) {
                    log("Bluetooth trigger matched for mode: ${mode.name}")
                    activateDrivingMode(mode.id)
                    return
                }
            }

            // Priority 2: Motion (not implemented yet)
            // TODO: Implement ActivityRecognition API for speed detection
            // if (triggers.motion?.enabled == true) {
            //     if (currentSpeed > triggers.motion.speedThresholdKmH) {
            //         activateDrivingMode(mode.id)
            //         return
            //     }
            // }
        }

        // No conditions met - deactivate
        deactivateDrivingMode()
    }

    /**
     * Check if bluetooth is connected to target devices.
     *
     * @param targetMacs List of target MAC addresses
     * @param matchAnyCarAudio If true, match any car audio device
     * @return true if connected to a target device
     */
    private fun isBluetoothConnectedToTargets(
        targetMacs: List<String>,
        matchAnyCarAudio: Boolean
    ): Boolean {
        val adapter = bluetoothAdapter
        if (adapter == null || !adapter.isEnabled) {
            log("Bluetooth adapter not available or disabled")
            return false
        }

        try {
            val connectedDevices = getConnectedBluetoothDevices()

            if (connectedDevices.isEmpty()) {
                return false
            }

            // If matchAnyCarAudio is true, check if any connected device is car audio
            if (matchAnyCarAudio) {
                for (device in connectedDevices) {
                    if (isCarAudioDevice(device)) {
                        log("Car audio device connected: ${device.address}")
                        return true
                    }
                }
            }

            // Check if any connected device matches target MACs
            if (targetMacs.isNotEmpty()) {
                for (device in connectedDevices) {
                    if (targetMacs.contains(device.address)) {
                        log("Target device connected: ${device.address}")
                        return true
                    }
                }
            }

            // If no specific targets and matchAnyCarAudio is false, any connection counts
            if (targetMacs.isEmpty() && !matchAnyCarAudio && connectedDevices.isNotEmpty()) {
                log("Any bluetooth device connected")
                return true
            }

        } catch (e: SecurityException) {
            log("Permission denied for bluetooth access: ${e.message}")
        } catch (e: Exception) {
            log("Error checking bluetooth connections: ${e.message}")
        }

        return false
    }

    /**
     * Get all connected bluetooth devices.
     * Checks A2DP and Headset profiles.
     */
    private fun getConnectedBluetoothDevices(): Set<BluetoothDevice> {
        val adapter = bluetoothAdapter ?: return emptySet()
        val connected = mutableSetOf<BluetoothDevice>()

        try {
            // Check bonded devices that are connected
            val bondedDevices = adapter.bondedDevices ?: emptySet()
            for (device in bondedDevices) {
                // Use reflection to check connection state
                try {
                    val method = device.javaClass.getMethod("isConnected")
                    val isConnected = method.invoke(device) as? Boolean ?: false
                    if (isConnected) {
                        connected.add(device)
                    }
                } catch (e: Exception) {
                    // Fallback: assume bonded devices might be connected
                    // This is not ideal but works as a fallback
                }
            }
        } catch (e: Exception) {
            log("Error getting connected devices: ${e.message}")
        }

        return connected
    }

    /**
     * Check if a bluetooth device is a car audio device.
     */
    private fun isCarAudioDevice(device: BluetoothDevice): Boolean {
        return try {
            val bluetoothClass = device.bluetoothClass
            bluetoothClass?.deviceClass == android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Activate driving mode if not already active.
     */
    private fun activateDrivingMode(modeId: String) {
        if (currentDrivingModeId == modeId) {
            return
        }

        log("Activating driving mode: $modeId")
        currentDrivingModeId = modeId
        engine.activateMode(modeId)
    }

    /**
     * Deactivate current driving mode if any is active.
     */
    private fun deactivateDrivingMode() {
        val modeId = currentDrivingModeId ?: return

        log("Deactivating driving mode: $modeId")
        engine.deactivateMode(modeId)
        currentDrivingModeId = null
    }

    /**
     * Clean up resources.
     * Called when engine is destroyed.
     */
    fun cleanup() {
        unregisterBluetoothReceiver()
        deactivateDrivingMode()
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "DrivingTriggerManager"
    }
}
