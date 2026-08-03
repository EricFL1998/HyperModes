package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log

/**
 * Manages intent/broadcast-based triggers for modes.
 * Listens for specific broadcast intents and activates modes accordingly.
 */
class IntentTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val receivers = mutableMapOf<String, BroadcastReceiver>()
    private var configs: Map<String, List<Pair<List<String>, String?>>> = emptyMap() // modeId -> List<(actions, packageName)>

    fun updateConfigs(newConfigs: Map<String, List<Pair<List<String>, String?>>>) {
        Log.i(TAG, "updateConfigs: ${newConfigs.size} modes with intent triggers")
        
        // Report modes that dropped out of the config as inactive
        (configs.keys - newConfigs.keys).forEach { 
            callback(it, "intent", false)
            unregisterReceiver(it)
        }
        
        // Unregister all old receivers and register new ones
        receivers.keys.toList().forEach { unregisterReceiver(it) }
        
        configs = newConfigs
        
        // Register receivers for each mode
        configs.forEach { (modeId, intentConfigs) ->
            registerReceiver(modeId, intentConfigs)
        }
    }

    private fun registerReceiver(modeId: String, intentConfigs: List<Pair<List<String>, String?>>) {
        if (intentConfigs.isEmpty()) return
        
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                Log.i(TAG, "Received intent: ${intent.action} for mode: $modeId")
                
                // Check if this intent matches any of our configured intents
                val matches = intentConfigs.any { (actions, packageName) ->
                    val actionMatches = actions.contains(intent.action)
                    val packageMatches = packageName == null || 
                        intent.`package` == packageName ||
                        intent.component?.packageName == packageName
                    actionMatches && packageMatches
                }
                
                if (matches) {
                    Log.i(TAG, "Intent matched for mode: $modeId")
                    // Activate mode when intent is received
                    callback(modeId, "intent", true)
                    
                    // Deactivate after a short delay (intent triggers are momentary)
                    handler.postDelayed({
                        callback(modeId, "intent", false)
                    }, TRIGGER_DURATION_MS)
                }
            }
        }
        
        try {
            val filter = IntentFilter()
            intentConfigs.forEach { (actions, _) ->
                actions.forEach { action ->
                    filter.addAction(action)
                    Log.i(TAG, "Registered action: $action for mode: $modeId")
                }
            }
            
            context.registerReceiver(receiver, filter, null, handler)
            receivers[modeId] = receiver
            Log.i(TAG, "Registered receiver for mode: $modeId with ${intentConfigs.sumOf { it.first.size }} actions")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register receiver for mode: $modeId", e)
        }
    }

    private fun unregisterReceiver(modeId: String) {
        receivers.remove(modeId)?.let { receiver ->
            try {
                context.unregisterReceiver(receiver)
                Log.i(TAG, "Unregistered receiver for mode: $modeId")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to unregister receiver for mode: $modeId", e)
            }
        }
    }

    /** Stop all receivers and release resources (engine shutdown). */
    fun release() {
        receivers.keys.toList().forEach { unregisterReceiver(it) }
        handler.removeCallbacksAndMessages(null)
    }

    companion object {
        private const val TAG = "IntentTriggerManager"
        private const val TRIGGER_DURATION_MS = 5000L // Keep mode active for 5 seconds after intent
    }
}
