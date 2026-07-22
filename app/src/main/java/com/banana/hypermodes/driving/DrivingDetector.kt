package com.banana.hypermodes.driving

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.DRIVING_DETECT_BLUETOOTH
import com.banana.hypermodes.data.DRIVING_DETECT_MOTION_BLUETOOTH
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeStore
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity

/**
 * Driving auto-detection core (驾车时):
 * - Car Bluetooth connect  -> turn driving mode on
 * - Disconnect / vehicle exit -> turn it back off (only if we turned it on)
 *
 * Two detection sources, matching the 驾车勿扰 page options:
 * - [DRIVING_DETECT_BLUETOOTH]: BluetoothDrivingReceiver (manifest) only.
 * - [DRIVING_DETECT_MOTION_BLUETOOTH]: additionally Activity Recognition
 *   IN_VEHICLE transitions, registered here (works with the app dead —
 *   Play Services fires the PendingIntent).
 */
object DrivingDetector {
    private const val TAG = "HyperModes"
    private const val PREFS = "driving_detector"

    /** Whether the last activation was automatic (only then we auto-deactivate). */
    private const val KEY_AUTO_ACTIVE = "auto_active"

    const val ACTION_TRANSITION = "com.banana.hypermodes.DRIVING_ACTIVITY_TRANSITION"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** Current driving mode from the store, or null if the user deleted it. */
    private fun drivingMode(context: Context): Mode? =
        ModeStore.load(context) { DefaultModes.get() }
            .firstOrNull { it.id == "driving" }

    fun onVehicleEnter(context: Context) {
        val mode = drivingMode(context) ?: return
        if (!mode.settings.drivingAutoDetect) {
            Log.i(TAG, "driving auto-detect disabled, ignoring vehicle enter")
            return
        }
        if (mode.enabled) return
        Log.i(TAG, "vehicle enter -> activating driving mode")
        setDrivingActive(context, mode, true)
        prefs(context).edit().putBoolean(KEY_AUTO_ACTIVE, true).apply()
    }

    fun onVehicleExit(context: Context) {
        val mode = drivingMode(context) ?: return
        if (!prefs(context).getBoolean(KEY_AUTO_ACTIVE, false)) {
            // Was turned on manually — leave it alone.
            return
        }
        if (!mode.enabled) return
        Log.i(TAG, "vehicle exit -> deactivating driving mode")
        setDrivingActive(context, mode, false)
        prefs(context).edit().putBoolean(KEY_AUTO_ACTIVE, false).apply()
    }

    private fun setDrivingActive(context: Context, mode: Mode, active: Boolean) {
        val updated = mode.copy(enabled = active)
        val modes = ModeStore.load(context) { DefaultModes.get() }
        ModeStore.save(context, modes.map { if (it.id == "driving") updated else it })
        val engine = com.banana.hypermodes.engine.ModeEngine(context)
        if (active) engine.activate(updated) else engine.deactivate(updated)
    }

    /** Register/unregister IN_VEHICLE transitions per current settings. */
    fun ensureActivityRecognition(context: Context) {
        val mode = drivingMode(context)
        val wantMotion = mode != null &&
                mode.settings.drivingAutoDetect &&
                mode.settings.drivingDetectMode == DRIVING_DETECT_MOTION_BLUETOOTH &&
                hasMotionPermission(context)

        val client = ActivityRecognition.getClient(context)
        val pendingIntent = transitionPendingIntent(context)
        if (!wantMotion) {
            // No-op when never registered; clears stale registrations after
            // the user switches to Bluetooth-only or disables the master switch.
            client.removeActivityTransitionUpdates(pendingIntent)
            return
        }
        try {
            val transitions = listOf(
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.IN_VEHICLE)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build(),
                ActivityTransition.Builder()
                    .setActivityType(DetectedActivity.IN_VEHICLE)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()
            )
            client.requestActivityTransitionUpdates(
                ActivityTransitionRequest(transitions), pendingIntent
            )
            Log.i(TAG, "IN_VEHICLE transition updates registered")
        } catch (t: Throwable) {
            // No Play Services (e.g. emulator) — Bluetooth detection still works.
            Log.w(TAG, "activity recognition unavailable: ${t.message}")
        }
    }

    private fun transitionPendingIntent(context: Context): PendingIntent =
        PendingIntent.getBroadcast(
            context, 0,
            Intent(ACTION_TRANSITION).setPackage(context.packageName),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

    fun hasMotionPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED

    fun hasBluetoothPermission(context: Context): Boolean =
        ContextCompat.checkSelfPermission(
            context, android.Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
}
