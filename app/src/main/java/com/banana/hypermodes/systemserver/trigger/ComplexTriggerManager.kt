package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.config.ModeType

/**
 * Manages complex triggers (v1.3) for all modes.
 * Aggregates states from specialized sub-managers (WiFi, Music, Apps, etc.).
 * A mode is activated if ANY of its complex triggers are active (OR logic).
 */
class ComplexTriggerManager(
    private val context: Context,
    private val engine: RoutineCoreEngine
) {
    private var allModes: List<ModeConfig> = emptyList()

    // Sub-managers
    private val wifiManager = WifiTriggerManager(context, ::onTriggerChanged)
    private val musicManager = MusicTriggerManager(context, ::onTriggerChanged)
    private val appManager = AppTriggerManager(context, ::onTriggerChanged)
    private val bluetoothManager = BluetoothTriggerManager(context, ::onTriggerChanged)
    private val locationManager = LocationTriggerManager(context, ::onTriggerChanged)
    private val intentManager = IntentTriggerManager(context, ::onTriggerChanged)
    private val batteryManager = BatteryTriggerManager(context, ::onTriggerChanged)
    // ScheduledModeManager still handles the Time triggers for now as it's complex,
    // but we can integrate it here if we want to unify everything.
    // For now, let's keep ScheduledModeManager as is and focus on the new ones.

    private val activeModesByTrigger = mutableMapOf<String, MutableSet<String>>() // modeId -> active trigger tags

    fun init(modes: List<ModeConfig>) {
        Log.e(TAG, "============ ComplexTriggerManager.init() called with ${modes.size} modes ============")
        allModes = modes

        // Update sub-managers with relevant configs
        updateSubManagers()

        // Initial check
        checkAllConditions()
    }

    fun updateModes(modes: List<ModeConfig>) {
        Log.w(TAG, "ComplexTriggerManager.updateModes() called with ${modes.size} modes")
        allModes = modes
        updateSubManagers()
        checkAllConditions()
    }

    fun isModeActiveByTrigger(modeId: String): Boolean {
        return activeModesByTrigger[modeId]?.isNotEmpty() ?: false
    }

    private fun updateSubManagers() {
        Log.e(TAG, "ComplexTriggerManager.updateSubManagers() called")
        val wifiConfigs = mutableMapOf<String, List<String>>()
        val appConfigs = mutableMapOf<String, List<String>>()
        val bluetoothConfigs = mutableMapOf<String, Pair<List<String>, Boolean>>()
        var musicModeIds = mutableSetOf<String>()
        val intentConfigs = mutableMapOf<String, Triple<String?, String?, String?>>()
        val locationConfigs = mutableMapOf<String, List<Pair<String, ComplexTrigger.Location>>>()
        val batteryConfigs = mutableMapOf<String, Pair<Int, String>>()

        allModes.forEach { mode ->
            // DYNAMIC_TRIGGER modes (built-in driving) are owned by the legacy
            // DrivingTriggerManager with its own bluetooth-priority logic —
            // feeding their triggers here would double-manage the mode.
            if (mode.type == ModeType.DYNAMIC_TRIGGER) return@forEach

            mode.complexTriggers.forEach { trigger ->
                when (trigger) {
                    is ComplexTrigger.Wifi ->
                        wifiConfigs[mode.id] = ((wifiConfigs[mode.id] ?: emptyList()) + trigger.ssids).distinct()
                    is ComplexTrigger.App ->
                        appConfigs[mode.id] = ((appConfigs[mode.id] ?: emptyList()) + trigger.packageNames).distinct()
                    is ComplexTrigger.Bluetooth -> {
                        val prev = bluetoothConfigs[mode.id]
                        bluetoothConfigs[mode.id] =
                            (((prev?.first ?: emptyList()) + trigger.deviceAddresses).distinct()) to
                                    ((prev?.second ?: false) || trigger.matchAnyCarAudio)
                    }
                    is ComplexTrigger.Music -> musicModeIds.add(mode.id)
                    is ComplexTrigger.Intent -> {
                        intentConfigs[mode.id] = Triple(trigger.activateAction, trigger.deactivateAction, trigger.packageName)
                    }
                    is ComplexTrigger.Location -> {
                        val prev = locationConfigs[mode.id] ?: emptyList()
                        locationConfigs[mode.id] = prev + (trigger.id to trigger)
                        Log.e(TAG, "Found location trigger: modeId=${mode.id}, triggerId=${trigger.id}, lat=${trigger.latitude}, lng=${trigger.longitude}")
                    }
                    is ComplexTrigger.Battery -> {
                        batteryConfigs[mode.id] = trigger.threshold to trigger.operator
                    }
                    is ComplexTrigger.Time -> { /* Handled by ScheduledModeManager */ }
                }
            }
        }

        Log.e(TAG, "Total location configs: ${locationConfigs.size} modes")
        wifiManager.updateConfigs(wifiConfigs)
        appManager.updateConfigs(appConfigs)
        bluetoothManager.updateConfigs(bluetoothConfigs)
        musicManager.updateConfigs(musicModeIds)
        locationManager.updateConfigs(locationConfigs)
        intentManager.updateConfigs(intentConfigs)
        batteryManager.updateConfigs(batteryConfigs)
    }

    private fun checkAllConditions() {
        wifiManager.check()
        appManager.check()
        bluetoothManager.check()
        musicManager.check()
        batteryManager.check()
    }

    private fun onTriggerChanged(modeId: String, triggerType: String, isActive: Boolean) {
        val activeTriggers = activeModesByTrigger.getOrPut(modeId) { mutableSetOf() }
        val wasActive = activeTriggers.isNotEmpty()

        if (isActive) {
            activeTriggers.add(triggerType)
        } else {
            activeTriggers.remove(triggerType)
            if (activeTriggers.isEmpty()) {
                activeModesByTrigger.remove(modeId)
            }
        }

        val isNowActive = activeTriggers.isNotEmpty()

        if (isNowActive && !wasActive) {
            log("Mode $modeId activated by $triggerType")
            engine.activateMode(modeId)
        } else if (!isNowActive && wasActive) {
            // Always let the engine decide: its isAnyTriggerActive guard keeps
            // the mode up while a schedule or another trigger still holds it.
            // (A manually toggled mode that has triggers follows trigger
            // semantics — the same rule schedules have always had.)
            log("Mode $modeId deactivated (no active triggers left)")
            engine.deactivateMode(modeId, isManualDismiss = false)
        }
    }

    /** Stop all sub-managers and release their resources (engine shutdown). */
    fun release() {
        updateModes(emptyList()) // stops callbacks and unregisters receivers
        appManager.release()
        intentManager.release()
        locationManager.release()
        batteryManager.release()
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "ComplexTriggerManager"
    }
}
