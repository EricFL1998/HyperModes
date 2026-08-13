package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.utils.HyperLog

/**
 * Manages intent/broadcast-based triggers for modes.
 * Listens for specific broadcast intents and activates/deactivates modes accordingly.
 */
class IntentTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val handler = Handler(Looper.getMainLooper())
    private val receivers = mutableMapOf<String, BroadcastReceiver>()
    
    // Config: modeId -> (activateAction, deactivateAction, packageName)
    private var configs: Map<String, Triple<String?, String?, String?>> = emptyMap()

    fun updateConfigs(newConfigs: Map<String, Triple<String?, String?, String?>>) {
        HyperLog.i(TAG, "updateConfigs: ${newConfigs.size} modes with intent triggers")
        
        // Report modes that dropped out of the config as inactive
        (configs.keys - newConfigs.keys).forEach { 
            callback(it, "intent", false)
            unregisterReceiver(it)
        }
        
        // Unregister all old receivers and register new ones
        receivers.keys.toList().forEach { unregisterReceiver(it) }
        
        configs = newConfigs
        
        // Register receivers for each mode
        configs.forEach { (modeId, config) ->
            registerReceiver(modeId, config)
        }
    }

    private fun registerReceiver(modeId: String, config: Triple<String?, String?, String?>) {
        val (activateAction, deactivateAction, packageName) = config
        
        // At least one action must be defined
        if (activateAction == null && deactivateAction == null) {
            Log.w(TAG, "No actions defined for mode: $modeId, skipping")
            return
        }
        
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                HyperLog.i(TAG, "Received intent: ${intent.action} for mode: $modeId")
                
                // Check package match if specified
                val packageMatches = packageName == null || 
                    intent.`package` == packageName ||
                    intent.component?.packageName == packageName
                
                if (!packageMatches) {
                    HyperLog.d(TAG, "Package mismatch for mode: $modeId")
                    return
                }
                
                // Check if this is an activate or deactivate action
                when (intent.action) {
                    activateAction -> {
                        HyperLog.i(TAG, "Activate intent matched for mode: $modeId")
                        callback(modeId, "intent", true)
                    }
                    deactivateAction -> {
                        HyperLog.i(TAG, "Deactivate intent matched for mode: $modeId")
                        callback(modeId, "intent", false)
                    }
                    else -> {
                        HyperLog.d(TAG, "Unknown action for mode: $modeId, action: ${intent.action}")
                    }
                }
            }
        }
        
        try {
            val filter = IntentFilter()
            var actionCount = 0
            
            activateAction?.let { 
                filter.addAction(it)
                actionCount++
                HyperLog.i(TAG, "Registered activate action: $it for mode: $modeId")
            }
            
            deactivateAction?.let { 
                filter.addAction(it)
                actionCount++
                HyperLog.i(TAG, "Registered deactivate action: $it for mode: $modeId")
            }
            
            if (actionCount > 0) {
                context.registerReceiver(receiver, filter, null, handler, Context.RECEIVER_EXPORTED)
                receivers[modeId] = receiver
                HyperLog.i(TAG, "Registered receiver for mode: $modeId with $actionCount actions")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to register receiver for mode: $modeId", e)
        }
    }

    private fun unregisterReceiver(modeId: String) {
        receivers.remove(modeId)?.let { receiver ->
            try {
                context.unregisterReceiver(receiver)
                HyperLog.i(TAG, "Unregistered receiver for mode: $modeId")
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
    }
}
