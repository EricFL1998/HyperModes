package com.banana.hypermodes.driving

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

/**
 * Manifest receiver for car Bluetooth connect/disconnect. Only devices that
 * report the car-audio Bluetooth class count as "vehicle Bluetooth"
 * (official behavior) — headphones and wearables are ignored. When the class
 * is unreadable (missing BLUETOOTH_CONNECT grant), treat it as a vehicle so
 * detection still works.
 */
class BluetoothDrivingReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val device: BluetoothDevice? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
        }

        when (intent.action) {
            BluetoothDevice.ACTION_ACL_CONNECTED -> {
                if (isVehicle(device)) {
                    Log.i(TAG, "car Bluetooth connected: ${device?.safeName()}")
                    DrivingDetector.onVehicleEnter(context)
                }
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                if (isVehicle(device)) {
                    Log.i(TAG, "car Bluetooth disconnected: ${device?.safeName()}")
                    DrivingDetector.onVehicleExit(context)
                }
            }
        }
    }

    private fun isVehicle(device: BluetoothDevice?): Boolean {
        if (device == null) return true
        return try {
            device.bluetoothClass?.deviceClass == BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO
        } catch (e: SecurityException) {
            true
        }
    }

    private fun BluetoothDevice.safeName(): String = try {
        name ?: address
    } catch (e: SecurityException) {
        address
    }

    private companion object {
        const val TAG = "HyperModes"
    }
}
