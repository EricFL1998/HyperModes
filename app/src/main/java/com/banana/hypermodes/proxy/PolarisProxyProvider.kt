package com.banana.hypermodes.proxy

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.UserManager
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.geofence.PolarisFenceSpec
import com.xiaomi.gnss.polaris.geofence.MiGeofence
import com.xiaomi.gnss.polaris.sdk.PolarisManager
import com.xiaomi.gnss.polaris.sdk.exception.PolarisException
import com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService

/**
 * ContentProvider-based proxy for Polaris SDK.
 * Runs in app process and provides access to Polaris from system_server.
 *
 * This solves the problem that system_server cannot directly bind to third-party services.
 * SecurityCenter uses the same pattern with AutoTaskServiceProvider.
 * 
 * Thread-safe implementation with proper synchronization.
 */
class PolarisProxyProvider : ContentProvider() {

    private val lock = Any()
    
    @Volatile
    private var polarisManager: PolarisManager? = null
    
    @Volatile
    private var geofenceService: PolarisGeofenceService? = null
    
    @Volatile
    private var isConnected = false
    
    private val liveById = mutableMapOf<String, PolarisFenceSpec>()
    private var lastInitAttemptTime = 0L
    private var initAttemptCount = 0
    
    private var geofenceReceiver: BroadcastReceiver? = null
    private var componentName: ComponentName? = null

    companion object {
        private const val TAG = "PolarisProxyProvider"
        private const val POLARIS_PACKAGE = "com.xiaomi.gnss.polaris"
        private const val MIN_INIT_INTERVAL_MS = 2000L
    }

    override fun onCreate(): Boolean {
        HyperLog.i(TAG, "PolarisProxyProvider created in process ${android.os.Process.myPid()}")
        return true
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        HyperLog.d(TAG, "call: method=$method, arg=$arg, connected=$isConnected")

        return when (method) {
            PolarisProxyContract.METHOD_INIT -> handleInit()
            PolarisProxyContract.METHOD_ADD_GEOFENCE -> handleAddGeofence(extras)
            PolarisProxyContract.METHOD_REMOVE_GEOFENCE -> handleRemoveGeofence(extras)
            PolarisProxyContract.METHOD_CLEAR_ALL -> handleClearAll()
            PolarisProxyContract.METHOD_IS_CONNECTED -> handleIsConnected()
            else -> {
                Log.w(TAG, "Unknown method: $method")
                Bundle().apply {
                    putBoolean(PolarisProxyContract.RESULT_SUCCESS, false)
                    putString(PolarisProxyContract.RESULT_ERROR_MSG, "Unknown method: $method")
                }
            }
        }
    }

    private fun handleInit(): Bundle {
        synchronized(lock) {
            // If already connected, return success immediately
            if (isConnected) {
                HyperLog.i(TAG, "Already connected, skipping re-initialization")
                return Bundle().apply {
                    putBoolean(PolarisProxyContract.RESULT_SUCCESS, true)
                    putString(PolarisProxyContract.RESULT_ERROR_MSG, "Already connected (attempt #$initAttemptCount)")
                }
            }
            
            // Throttle init attempts to avoid hammering the service
            val now = System.currentTimeMillis()
            if (now - lastInitAttemptTime < MIN_INIT_INTERVAL_MS) {
                val waitTime = MIN_INIT_INTERVAL_MS - (now - lastInitAttemptTime)
                Log.w(TAG, "Init called too soon after last attempt (${now - lastInitAttemptTime}ms ago), throttling")
                return errorBundle("Init throttled, try again in ${waitTime}ms")
            }
            lastInitAttemptTime = now
            initAttemptCount++
        }

        HyperLog.i(TAG, "===== Polaris Init Attempt #$initAttemptCount =====")

        return try {
            val appContext = context?.applicationContext ?: context
            if (appContext == null) {
                Log.e(TAG, "Context is null!")
                return errorBundle("Context is null")
            }

            // 1. Check if Polaris package is installed
            val polarisInstalled = try {
                appContext.packageManager.getPackageInfo(POLARIS_PACKAGE, 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }

            if (!polarisInstalled) {
                Log.e(TAG, "Polaris package ($POLARIS_PACKAGE) is not installed!")
                return errorBundle("Polaris package not installed")
            }
            HyperLog.i(TAG, "✓ Polaris package is installed")

            // 2. Check if user is unlocked
            val userManager = appContext.getSystemService(Context.USER_SERVICE) as? UserManager
            if (userManager == null) {
                Log.e(TAG, "✗ UserManager is null")
                return errorBundle("UserManager is null")
            }

            if (!userManager.isUserUnlocked) {
                Log.w(TAG, "✗ User is locked, cannot connect to Polaris")
                return errorBundle("User is locked")
            }
            HyperLog.i(TAG, "✓ User is unlocked")

            // 3. Connect to Polaris
            HyperLog.i(TAG, "Connecting to Polaris service...")
            
            val manager = try {
                PolarisManager.getInstance(appContext)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get PolarisManager instance", e)
                return errorBundle("Failed to get PolarisManager: ${e.message}")
            }

            // Start and bind to Polaris service
            try {
                manager.connectPolarisServiceSync()
            } catch (e: PolarisException) {
                Log.e(TAG, "Failed to connect to Polaris service", e)
                return errorBundle("Failed to connect: ${e.message}")
            }

            val geoService = try {
                manager.getSubService(PolarisManager.ServiceType.Geofence) as? PolarisGeofenceService
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get PolarisGeofenceService", e)
                return errorBundle("Failed to get geofence service: ${e.message}")
            }

            if (geoService == null) {
                Log.e(TAG, "PolarisGeofenceService is null after connection")
                return errorBundle("Geofence service is null")
            }

            // 4. Register component with Polaris to receive events
            val component = ComponentName(appContext, PolarisProxyProvider::class.java)
            try {
                geoService.registerComponent(component)
                HyperLog.i(TAG, "✓ Registered component with Polaris: $component")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register component", e)
                return errorBundle("Failed to register component: ${e.message}")
            }

            // 5. Register broadcast receiver for geofence events
            registerGeofenceReceiver(appContext)

            synchronized(lock) {
                polarisManager = manager
                geofenceService = geoService
                componentName = component
                isConnected = true
            }

            HyperLog.i(TAG, "===== Polaris Connected Successfully! (Attempt #$initAttemptCount) =====")

            Bundle().apply {
                putBoolean(PolarisProxyContract.RESULT_SUCCESS, true)
                putString(PolarisProxyContract.RESULT_ERROR_MSG, "Connected after attempt #$initAttemptCount")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Unexpected error during Polaris initialization", e)
            errorBundle("Unexpected error: ${e.message}")
        }
    }

    private fun registerGeofenceReceiver(context: Context) {
        // Unregister old receiver if exists
        geofenceReceiver?.let {
            try {
                context.unregisterReceiver(it)
            } catch (e: Exception) {
                HyperLog.d(TAG, "Old receiver already unregistered")
            }
        }

        geofenceReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context, intent: Intent) {
                // Polaris sends geofence events through broadcasts
                // The action and extras depend on how Polaris SDK works
                
                HyperLog.i(TAG, "===== Broadcast Received: ${intent.action} =====")
                intent.extras?.let { extras ->
                    for (key in extras.keySet()) {
                        HyperLog.d(TAG, "  Extra: $key = ${extras.get(key)}")
                    }
                }

                // Try to extract geofence event information
                // Based on Polaris SDK, events may come as explicit broadcasts
                val geofenceId = intent.getStringExtra("geofence_id") 
                    ?: intent.getStringExtra("fence_id")
                    ?: intent.getStringExtra("id")
                
                val transitionType = intent.getIntExtra("transition_type", -1)
                    .takeIf { it != -1 }
                    ?: intent.getIntExtra("event", -1)
                    ?: intent.getIntExtra("transition", -1)

                if (geofenceId != null && transitionType != -1) {
                    HyperLog.i(TAG, "Geofence Event: id=$geofenceId, transition=$transitionType")

                    // Forward to system_server via broadcast
                    val forwardIntent = Intent(Protocol.ACTION_POLARIS_GEOFENCE_EVENT).apply {
                        setPackage(Protocol.MODULE_PACKAGE)
                        putExtra(Protocol.EXTRA_POLARIS_FENCE_ID, geofenceId)
                        putExtra(Protocol.EXTRA_POLARIS_EVENT, transitionType)
                    }

                    try {
                        ctx.sendBroadcast(forwardIntent)
                        HyperLog.i(TAG, "✓ Forwarded geofence event to system_server")
                    } catch (e: Exception) {
                        Log.e(TAG, "✗ Failed to forward geofence event", e)
                    }
                }
            }
        }

        // Register for Polaris geofence broadcasts
        // SecurityCenter uses implicit broadcasts from Polaris
        val filter = IntentFilter().apply {
            // Common Polaris geofence actions
            addAction("com.xiaomi.gnss.polaris.geofence.TRANSITION")
            addAction("com.xiaomi.gnss.polaris.GEOFENCE_EVENT")
            addAction("android.location.GEOFENCE_TRANSITION")
        }
        
        try {
            context.registerReceiver(geofenceReceiver, filter)
            HyperLog.i(TAG, "✓ Registered geofence event receiver")
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to register geofence receiver", e)
        }
    }

    private fun handleAddGeofence(extras: Bundle?): Bundle {
        if (extras == null) {
            return errorBundle("Missing parameters")
        }

        val service = synchronized(lock) {
            if (!isConnected) {
                return errorBundle("Polaris not connected")
            }
            geofenceService
        }

        if (service == null) {
            return errorBundle("Geofence service is null")
        }

        return try {
            val fenceId = extras.getString(PolarisProxyContract.PARAM_FENCE_ID) ?: return errorBundle("Missing fence_id")
            val modeId = extras.getString(PolarisProxyContract.PARAM_MODE_ID) ?: return errorBundle("Missing mode_id")
            val triggerId = extras.getString(PolarisProxyContract.PARAM_TRIGGER_ID) ?: return errorBundle("Missing trigger_id")
            val latitude = extras.getDouble(PolarisProxyContract.PARAM_LATITUDE)
            val longitude = extras.getDouble(PolarisProxyContract.PARAM_LONGITUDE)
            val radius = extras.getInt(PolarisProxyContract.PARAM_RADIUS)
            val transitionType = extras.getInt(PolarisProxyContract.PARAM_TRANSITION_TYPE)
            val confidence = extras.getInt(PolarisProxyContract.PARAM_CONFIDENCE)

            val miGeofence = MiGeofence().apply {
                setId(fenceId)
                setLatitude(latitude)
                setLongitude(longitude)
                setRadius(radius)
                setTransitionType(transitionType)
                setConfidence(confidence)
            }

            service.addGeofence(miGeofence)

            // Track the fence
            synchronized(lock) {
                liveById[fenceId] = PolarisFenceSpec(
                    fenceId = fenceId,
                    modeId = modeId,
                    triggerId = triggerId,
                    latitude = latitude,
                    longitude = longitude,
                    radiusMeters = radius,
                    transitionType = transitionType,
                    confidence = confidence
                )
            }

            HyperLog.i(TAG, "✓ Added geofence: $fenceId (lat=$latitude, lng=$longitude, r=${radius}m)")

            Bundle().apply {
                putBoolean(PolarisProxyContract.RESULT_SUCCESS, true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to add geofence", e)
            errorBundle("Failed to add geofence: ${e.message}")
        }
    }

    private fun handleRemoveGeofence(extras: Bundle?): Bundle {
        if (extras == null) {
            return errorBundle("Missing parameters")
        }

        val service = synchronized(lock) {
            if (!isConnected) {
                return errorBundle("Polaris not connected")
            }
            geofenceService
        }

        if (service == null) {
            return errorBundle("Geofence service is null")
        }

        return try {
            val fenceId = extras.getString(PolarisProxyContract.PARAM_FENCE_ID) ?: return errorBundle("Missing fence_id")

            service.deleteGeofence(fenceId)
            
            synchronized(lock) {
                liveById.remove(fenceId)
            }

            HyperLog.i(TAG, "✓ Removed geofence: $fenceId")

            Bundle().apply {
                putBoolean(PolarisProxyContract.RESULT_SUCCESS, true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to remove geofence", e)
            errorBundle("Failed to remove geofence: ${e.message}")
        }
    }

    private fun handleClearAll(): Bundle {
        val service = synchronized(lock) {
            if (!isConnected) {
                return errorBundle("Polaris not connected")
            }
            geofenceService
        }

        if (service == null) {
            return errorBundle("Geofence service is null")
        }

        return try {
            val fenceIds = synchronized(lock) {
                liveById.keys.toList()
            }
            
            fenceIds.forEach { fenceId ->
                try {
                    service.deleteGeofence(fenceId)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to remove fence during clear: $fenceId", e)
                }
            }
            
            synchronized(lock) {
                liveById.clear()
            }

            HyperLog.i(TAG, "✓ Cleared all geofences")

            Bundle().apply {
                putBoolean(PolarisProxyContract.RESULT_SUCCESS, true)
            }
        } catch (e: Exception) {
            Log.e(TAG, "✗ Failed to clear all geofences", e)
            errorBundle("Failed to clear all: ${e.message}")
        }
    }

    private fun handleIsConnected(): Bundle {
        val connected = synchronized(lock) { isConnected }
        
        return Bundle().apply {
            putBoolean(PolarisProxyContract.RESULT_SUCCESS, true)
            putBoolean(PolarisProxyContract.RESULT_IS_CONNECTED, connected)
            if (!connected) {
                putString(PolarisProxyContract.RESULT_ERROR_MSG, 
                    "Not connected. Attempted init $initAttemptCount time(s)")
            }
        }
    }

    private fun errorBundle(message: String): Bundle {
        return Bundle().apply {
            putBoolean(PolarisProxyContract.RESULT_SUCCESS, false)
            putString(PolarisProxyContract.RESULT_ERROR_MSG, message)
        }
    }

    override fun shutdown() {
        super.shutdown()
        
        // Unregister component
        synchronized(lock) {
            if (isConnected) {
                try {
                    geofenceService?.unregisterComponent()
                    HyperLog.i(TAG, "✓ Unregistered component from Polaris")
                } catch (e: Exception) {
                    Log.e(TAG, "✗ Failed to unregister component", e)
                }
            }
        }
        
        // Unregister receiver
        geofenceReceiver?.let {
            try {
                context?.unregisterReceiver(it)
            } catch (e: Exception) {
                HyperLog.d(TAG, "Receiver already unregistered")
            }
        }
        geofenceReceiver = null
    }

    // Standard ContentProvider methods - not used, but required
    override fun query(uri: Uri, projection: Array<out String>?, selection: String?,
                      selectionArgs: Array<out String>?, sortOrder: String?): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?,
                       selectionArgs: Array<out String>?): Int = 0
}
