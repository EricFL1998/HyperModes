package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.Log

/**
 * Manages battery-level triggers.
 *
 * Listens to ACTION_BATTERY_CHANGED and reports whether the current battery
 * percentage satisfies each configured threshold/operator pair.
 */
class BatteryTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val lock = Any()
    private var configs: Map<String, Pair<Int, String>> = emptyMap()
    private var isReceiverRegistered = false
    private var isReleased = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            synchronized(lock) {
                if (isReleased) return
            }
            if (intent.action == Intent.ACTION_BATTERY_CHANGED) {
                check()
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, Pair<Int, String>>) {
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
            callback(it, "battery", false)
        }

        // Register/unregister receiver based on config
        val hasConfigs = newConfigs.isNotEmpty()
        synchronized(lock) {
            if (hasConfigs && !isReceiverRegistered && !isReleased) {
                try {
                    context.registerReceiver(
                        receiver,
                        IntentFilter(Intent.ACTION_BATTERY_CHANGED)
                    )
                    isReceiverRegistered = true
                    Log.d(TAG, "Battery receiver registered")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register battery receiver: ${e.message}")
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

        val level = getBatteryLevel() ?: return

        currentConfigs.forEach { (triggerKey, config) ->
            val (threshold, operator) = config
            val isActive = when (operator) {
                "above" -> level >= threshold
                "below" -> level <= threshold
                "equal" -> level == threshold
                else -> false
            }
            callback(triggerKey, "battery", isActive)
        }
    }

    private fun getBatteryLevel(): Int? {
        return try {
            val intent = context.registerReceiver(
                null,
                IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            ) ?: return null
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            if (level < 0 || scale <= 0) null
            else (level * 100 / scale)
        } catch (e: Exception) {
            Log.e(TAG, "Error getting battery level: ${e.message}")
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
                Log.d(TAG, "Battery receiver unregistered")
            } catch (e: IllegalArgumentException) {
                Log.d(TAG, "Battery receiver already unregistered")
            } catch (e: Exception) {
                Log.e(TAG, "Error unregistering battery receiver: ${e.message}")
            }
            isReceiverRegistered = false
        }
    }

    companion object {
        private const val TAG = "BatteryTriggerManager"
    }
}
