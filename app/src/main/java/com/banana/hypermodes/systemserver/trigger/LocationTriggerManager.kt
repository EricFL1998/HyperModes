package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.UserManager
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.geofence.PolarisProxyClient
import com.xiaomi.gnss.polaris.geofence.MiGeofence

/**
 * Manages location triggers via Polaris geofencing.
 * Translates Polaris enter/exit events into continuous ARRIVE/LEAVE state.
 * State starts unknown after restart; config load never synthesizes enter/leave.
 * 
 * Thread-safe implementation with proper synchronization.
 */
class LocationTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val polarisClient = PolarisProxyClient(context, ::onGeofenceEvent)
    private val handler = Handler(Looper.getMainLooper())
    
    // Synchronized state
    private val lock = Any()
    private var configs: Map<String, List<Pair<String, ComplexTrigger.Location>>> = emptyMap()
    private val triggerStates = mutableMapOf<String, Boolean?>()
    private var retryCount = 0
    private var lastRetryTime = 0L
    private var isReleased = false
    
    private val maxRetries = 20
    private var isPolarisPackageInstalled = false

    private val userUnlockReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_USER_UNLOCKED) {
                HyperLog.i(TAG, "=== User Unlocked Event Received ===")
                HyperLog.i(TAG, "Resetting retry mechanism and attempting immediate Polaris init")
                
                synchronized(lock) {
                    if (isReleased) return
                    retryCount = 0
                    lastRetryTime = 0L
                }
                
                handler.removeCallbacks(retryRunnable)
                handler.post(retryRunnable)
            }
        }
    }

    private val geofenceEventReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action != Protocol.ACTION_POLARIS_GEOFENCE_EVENT) return

            val fenceId = intent.getStringExtra(Protocol.EXTRA_POLARIS_FENCE_ID) ?: return
            val eventCode = intent.getIntExtra(Protocol.EXTRA_POLARIS_EVENT, -1)

            HyperLog.i(TAG, "Received geofence event broadcast: fenceId=$fenceId, eventCode=$eventCode")

            // Parse fence ID using the proper parser
            val parsed = PolarisProxyClient.parseFenceId(fenceId)
            if (parsed == null) {
                Log.w(TAG, "Invalid fence ID format: $fenceId")
                return
            }

            val (modeId, triggerId) = parsed

            // Convert event code to GeofenceEvent
            val event = when (eventCode) {
                MiGeofence.TRANSITION_TYPE_ENTER -> GeofenceEvent.ENTER
                MiGeofence.TRANSITION_TYPE_EXIT -> GeofenceEvent.EXIT
                else -> {
                    Log.w(TAG, "Unknown event code: $eventCode")
                    return
                }
            }

            HyperLog.i(TAG, "Parsed geofence event: mode=$modeId, trigger=$triggerId, event=$event")
            onGeofenceEvent(modeId, triggerId, event)
        }
    }

    init {
        HyperLog.i(TAG, "LocationTriggerManager initialized in process ${android.os.Process.myPid()}")
        
        // Check if Polaris package is installed
        isPolarisPackageInstalled = try {
            context.packageManager.getPackageInfo(POLARIS_PACKAGE, 0)
            HyperLog.i(TAG, "Polaris package detected: $POLARIS_PACKAGE")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.w(TAG, "Polaris package not found: $POLARIS_PACKAGE")
            false
        }

        // Register for user unlock events
        val unlockFilter = IntentFilter(Intent.ACTION_USER_UNLOCKED)
        context.registerReceiver(userUnlockReceiver, unlockFilter)

        // Register for geofence events from app process
        val geofenceFilter = IntentFilter(Protocol.ACTION_POLARIS_GEOFENCE_EVENT)
        context.registerReceiver(geofenceEventReceiver, geofenceFilter)
        HyperLog.i(TAG, "Registered receiver for ${Protocol.ACTION_POLARIS_GEOFENCE_EVENT}")
    }

    fun updateConfigs(newConfigs: Map<String, List<Pair<String, ComplexTrigger.Location>>>) {
        HyperLog.i(TAG, "=== updateConfigs called ===")
        
        val oldConfigs = synchronized(lock) {
            if (isReleased) {
                Log.w(TAG, "Manager is released, ignoring updateConfigs")
                return
            }
            val old = configs
            configs = newConfigs
            old
        }
        
        HyperLog.i(TAG, "Previous configs: ${oldConfigs.size} modes, New configs: ${newConfigs.size} modes")

        // Report modes that dropped out as inactive
        val oldKeys = oldConfigs.keys
        val newKeys = newConfigs.keys
        (oldKeys - newKeys).forEach { modeId ->
            oldConfigs[modeId]?.forEach { (triggerId, _) ->
                val key = "$modeId:$triggerId"
                synchronized(lock) {
                    triggerStates.remove(key)
                }
                callback(modeId, "location:$triggerId", false)
                HyperLog.i(TAG, "Removed trigger: $key")
            }
        }

        if (newConfigs.isEmpty()) {
            HyperLog.i(TAG, "No location triggers configured, canceling retry mechanism")
            handler.removeCallbacks(retryRunnable)
            return
        }

        HyperLog.i(TAG, "Location triggers configured: ${newConfigs.size} mode(s)")
        newConfigs.forEach { (modeId, triggers) ->
            HyperLog.i(TAG, "  Mode $modeId: ${triggers.size} trigger(s)")
            triggers.forEach { (triggerId, location) ->
                HyperLog.d(TAG, "    - $triggerId: lat=${location.latitude}, lng=${location.longitude}, r=${location.radius}m, trans=${location.transition}")
            }
        }

        // Check prerequisites before attempting to connect
        if (!isPolarisPackageInstalled) {
            Log.e(TAG, "Cannot initialize: Polaris package not installed")
            return
        }

        val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
        if (userManager?.isUserUnlocked != true) {
            Log.w(TAG, "User is locked. Will wait for ACTION_USER_UNLOCKED broadcast")
            return
        }

        // If we have location triggers but Polaris isn't connected, start/continue retry mechanism
        if (!polarisClient.isConnected()) {
            HyperLog.i(TAG, "Polaris not connected, initiating retry mechanism")
            scheduleRetry(immediate = true)
        } else {
            HyperLog.i(TAG, "Polaris already connected, updating triggers immediately")
            updatePolarisGeofences()
        }
    }

    private fun updatePolarisGeofences() {
        // Reconcile geofences with Polaris
        val allTriggers = mutableListOf<Triple<String, String, ComplexTrigger.Location>>()
        
        synchronized(lock) {
            configs.forEach { (modeId, triggers) ->
                triggers.forEach { (triggerId, location) ->
                    allTriggers.add(Triple(modeId, triggerId, location))
                }
            }
        }

        HyperLog.i(TAG, "Pushing ${allTriggers.size} trigger(s) to Polaris")
        polarisClient.updateTriggers(allTriggers)

        // Initialize state for new triggers as unknown
        synchronized(lock) {
            allTriggers.forEach { (modeId, triggerId, _) ->
                val key = "$modeId:$triggerId"
                if (!triggerStates.containsKey(key)) {
                    triggerStates[key] = null // unknown
                }
            }
        }
    }

    private fun onGeofenceEvent(modeId: String, triggerId: String, event: GeofenceEvent) {
        val key = "$modeId:$triggerId"
        
        val (trigger, wasInside) = synchronized(lock) {
            val t = configs[modeId]?.find { it.first == triggerId }?.second
            val was = triggerStates[key]
            t to was
        }
        
        if (trigger == null) {
            Log.w(TAG, "Received event for unknown trigger: $key")
            return
        }

        val isInside = when (event) {
            GeofenceEvent.ENTER -> true
            GeofenceEvent.EXIT -> false
        }

        synchronized(lock) {
            triggerStates[key] = isInside
        }

        // Determine if trigger should be active based on transition type
        val shouldActivate = when (trigger.transition) {
            "ARRIVE" -> isInside
            "LEAVE" -> !isInside
            else -> false
        }

        HyperLog.i(TAG, "Geofence event: mode=$modeId, trigger=$triggerId, event=$event, inside=$isInside, transition=${trigger.transition}, activate=$shouldActivate")

        // Only fire callback if state actually changed (or was unknown)
        if (wasInside == null || wasInside != isInside) {
            HyperLog.i(TAG, "  State changed from $wasInside -> $isInside, firing callback")
            callback(modeId, "location:$triggerId", shouldActivate)
        } else {
            HyperLog.d(TAG, "  State unchanged ($isInside), skipping callback")
        }
    }

    /**
     * Release resources when shutting down.
     * Clears all trigger states and releases the Polaris adapter.
     */
    fun release() {
        HyperLog.i(TAG, "Releasing LocationTriggerManager")
        
        synchronized(lock) {
            isReleased = true
        }
        
        // Remove callbacks before unregistering receivers
        handler.removeCallbacks(retryRunnable)
        
        try {
            context.unregisterReceiver(userUnlockReceiver)
        } catch (e: Exception) {
            HyperLog.d(TAG, "userUnlockReceiver already unregistered")
        }
        try {
            context.unregisterReceiver(geofenceEventReceiver)
        } catch (e: Exception) {
            HyperLog.d(TAG, "geofenceEventReceiver already unregistered")
        }
        
        updateConfigs(emptyMap())
        polarisClient.cleanup()
    }

    private fun scheduleRetry(immediate: Boolean = false) {
        synchronized(lock) {
            if (isReleased) return
        }
        
        // Cancel any existing retry
        handler.removeCallbacks(retryRunnable)

        // Check if user is unlocked
        val userManager = context.getSystemService(Context.USER_SERVICE) as? UserManager
        if (userManager?.isUserUnlocked != true) {
            HyperLog.i(TAG, "User is locked. Polaris init will be deferred until unlock broadcast")
            return
        }

        val currentRetryCount = synchronized(lock) { retryCount }
        
        if (currentRetryCount >= maxRetries) {
            Log.w(TAG, "Max retry attempts ($maxRetries) reached, giving up on Polaris initialization")
            return
        }

        // Exponential backoff: 0s, 2s, 5s, 10s, 20s, 30s, 60s, then 60s intervals
        val delay = if (immediate) {
            0L
        } else {
            when (currentRetryCount) {
                0 -> 2000L
                1 -> 5000L
                2 -> 10000L
                3 -> 20000L
                4 -> 30000L
                else -> 60000L
            }
        }

        HyperLog.i(TAG, "Scheduling Polaris init retry in ${delay/1000}s (attempt ${currentRetryCount + 1}/$maxRetries)")
        
        if (delay == 0L) {
            handler.post(retryRunnable)
        } else {
            handler.postDelayed(retryRunnable, delay)
        }
    }

    private val retryRunnable = object : Runnable {
        override fun run() {
            synchronized(lock) {
                if (isReleased) {
                    HyperLog.i(TAG, "Retry triggered but manager is released, aborting")
                    return
                }
            }
            
            val now = System.currentTimeMillis()
            
            // Only retry if we have location triggers
            val hasConfigs = synchronized(lock) { configs.isNotEmpty() }
            if (!hasConfigs) {
                HyperLog.i(TAG, "Retry triggered but no location triggers configured, aborting")
                return
            }

            // If already connected, apply triggers and exit
            if (polarisClient.isConnected()) {
                HyperLog.i(TAG, "=== Polaris Connected Successfully! ===")
                val attempts = synchronized(lock) {
                    val count = retryCount
                    retryCount = 0
                    count
                }
                HyperLog.i(TAG, "Connection established after $attempts retry attempt(s)")
                updatePolarisGeofences()
                return
            }

            // Log retry attempt
            val attemptNumber = synchronized(lock) {
                retryCount++
                retryCount
            }
            
            HyperLog.i(TAG, "=== Polaris Init Retry Attempt $attemptNumber/$maxRetries ===")
            
            synchronized(lock) {
                HyperLog.i(TAG, "Time since last retry: ${now - lastRetryTime}ms")
                lastRetryTime = now
            }

            // Attempt initialization
            polarisClient.init()

            // Check if connection succeeded
            if (polarisClient.isConnected()) {
                HyperLog.i(TAG, "=== Polaris Connected on Attempt $attemptNumber! ===")
                synchronized(lock) {
                    retryCount = 0
                }
                updatePolarisGeofences()
            } else {
                // Schedule next retry if we haven't exceeded max attempts
                val shouldRetry = synchronized(lock) { retryCount < maxRetries }
                if (shouldRetry) {
                    Log.w(TAG, "Polaris init failed, will retry...")
                    scheduleRetry(immediate = false)
                } else {
                    Log.e(TAG, "=== Polaris Init Failed After $maxRetries Attempts ===")
                    Log.e(TAG, "Location triggers will not work until Polaris service is available")
                }
            }
        }
    }

    companion object {
        private const val TAG = "LocationTriggerManager"
        private const val POLARIS_PACKAGE = "com.xiaomi.gnss.polaris"
    }
}

enum class GeofenceEvent {
    ENTER,
    EXIT
}
