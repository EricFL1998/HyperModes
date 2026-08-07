package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.TriggerGroup
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.config.ModeType

/**
 * Manages TriggerGroup v2.0 logic.
 * 
 * - Multiple TriggerGroups: OR logic (any group can activate the mode)
 * - Compound TriggerGroup: AND logic (all triggers in the group must be active)
 * - Single TriggerGroup: behaves like v1.3 single trigger
 */
class TriggerGroupManager(
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

    // Track individual trigger states per mode
    // modeId -> triggerKey -> isActive
    private val triggerStates = mutableMapOf<String, MutableMap<String, Boolean>>()
    
    // Track which groups are satisfied per mode
    // modeId -> groupIndex -> isSatisfied
    private val groupStates = mutableMapOf<String, MutableMap<Int, Boolean>>()

    fun init(modes: List<ModeConfig>) {
                allModes = modes
        updateSubManagers()
        checkAllConditions()
    }

    fun updateModes(modes: List<ModeConfig>) {
                allModes = modes
        updateSubManagers()
        checkAllConditions()
    }

    fun isModeActiveByTrigger(modeId: String): Boolean {
        // A mode is active if ANY of its trigger groups is satisfied
        return groupStates[modeId]?.values?.any { it } ?: false
    }

    private fun updateSubManagers() {
                val wifiConfigs = mutableMapOf<String, List<String>>()
        val appConfigs = mutableMapOf<String, List<String>>()
        val bluetoothConfigs = mutableMapOf<String, Pair<List<String>, Boolean>>()
        val musicModeIds = mutableSetOf<String>()
        val intentConfigs = mutableMapOf<String, Triple<String?, String?, String?>>()
        val locationConfigs = mutableMapOf<String, List<Pair<String, ComplexTrigger.Location>>>()
        val batteryConfigs = mutableMapOf<String, Pair<Int, String>>()

        allModes.forEach { mode ->
            if (mode.type == ModeType.DYNAMIC_TRIGGER) return@forEach

            // Process trigger groups
            mode.triggerGroups.forEachIndexed { groupIndex, group ->
                val triggers = when (group) {
                    is TriggerGroup.Single -> listOf(group.trigger)
                    is TriggerGroup.Compound -> group.triggers
                }

                triggers.forEach { trigger ->
                    val triggerKey = "${mode.id}_group${groupIndex}_${getTriggerKey(trigger)}"
                    
                    when (trigger) {
                        is ComplexTrigger.Wifi -> {
                                                        wifiConfigs[triggerKey] = trigger.ssids
                        }
                        is ComplexTrigger.App -> {
                            appConfigs[triggerKey] = trigger.packageNames
                        }
                        is ComplexTrigger.Bluetooth -> {
                            bluetoothConfigs[triggerKey] = trigger.deviceAddresses to trigger.matchAnyCarAudio
                        }
                        is ComplexTrigger.Music -> {
                            musicModeIds.add(triggerKey)
                        }
                        is ComplexTrigger.Intent -> {
                            intentConfigs[triggerKey] = Triple(trigger.activateAction, trigger.deactivateAction, trigger.packageName)
                        }
                        is ComplexTrigger.Location -> {
                            val prev = locationConfigs[triggerKey] ?: emptyList()
                            locationConfigs[triggerKey] = prev + (trigger.id to trigger)
                        }
                        is ComplexTrigger.Battery -> {
                            batteryConfigs[triggerKey] = trigger.threshold to trigger.operator
                        }
                        is ComplexTrigger.Time -> { /* Handled by ScheduledModeManager */ }
                    }
                }
            }

        }

        wifiManager.updateConfigs(wifiConfigs)
        appManager.updateConfigs(appConfigs)
        bluetoothManager.updateConfigs(bluetoothConfigs)
        musicManager.updateConfigs(musicModeIds)
        locationManager.updateConfigs(locationConfigs)
        intentManager.updateConfigs(intentConfigs)
        batteryManager.updateConfigs(batteryConfigs)
    }

    private fun getTriggerKey(trigger: ComplexTrigger): String {
        return when (trigger) {
            is ComplexTrigger.Time -> "time_${trigger.startTime}_${trigger.endTime}"
            is ComplexTrigger.App -> "app_" + trigger.packageNames.sorted().joinToString("_")
            is ComplexTrigger.Wifi -> "wifi_" + trigger.ssids.sorted().joinToString("_")
            is ComplexTrigger.Bluetooth -> "bt_" + trigger.deviceAddresses.sorted().joinToString("_")
            is ComplexTrigger.Music -> "music"
            is ComplexTrigger.Intent -> "intent_" + (trigger.activateAction ?: "")
            is ComplexTrigger.Location -> "location_${trigger.id}"
            is ComplexTrigger.Battery -> "battery_${trigger.threshold}_${trigger.operator}"
        }
    }

    private fun checkAllConditions() {
        wifiManager.check()
        appManager.check()
        bluetoothManager.check()
        musicManager.check()
        batteryManager.check()
    }

    private fun onTriggerChanged(triggerKey: String, triggerType: String, isActive: Boolean) {
                // Parse triggerKey to get modeId and groupIndex
        // Format: "modeId_groupN_triggerKey"
        // modeId itself may contain underscores, so anchor on "_group"
        val groupMarker = "_group"
        val groupIdx = triggerKey.indexOf(groupMarker)
        if (groupIdx < 0) return
        val modeId: String
        val groupIndex: Int?
        modeId = triggerKey.substring(0, groupIdx)
        val afterGroup = triggerKey.substring(groupIdx + groupMarker.length)
        groupIndex = afterGroup.substringBefore("_").toIntOrNull()

        // Update trigger state
        val modeStates = triggerStates.getOrPut(modeId) { mutableMapOf() }
        modeStates[triggerKey] = isActive

        // Check if this mode uses trigger groups
        val mode = allModes.find { it.id == modeId } ?: return
        
        if (mode.triggerGroups.isNotEmpty()) {
                        // v2.0 logic: check group satisfaction
            val groups = groupStates.getOrPut(modeId) { mutableMapOf() }
            val wasAnyGroupActive = groups.values.any { it }

            mode.triggerGroups.forEachIndexed { idx, group ->
                val triggers = when (group) {
                    is TriggerGroup.Single -> listOf(group.trigger)
                    is TriggerGroup.Compound -> group.triggers
                }
                
                                // Check if ALL triggers in this group are active (AND logic)
                val allActive = triggers.all { trigger ->
                    val key = "${modeId}_group${idx}_${getTriggerKey(trigger)}"
                    val state = modeStates[key]
                                        modeStates[key] == true
                }
                
                groups[idx] = allActive
            }

            val isAnyGroupActive = groups.values.any { it }

            // Activate/deactivate based on group states
            if (isAnyGroupActive && !wasAnyGroupActive) {
                log("Mode $modeId activated (trigger group satisfied)")
                engine.activateMode(modeId)
            } else if (!isAnyGroupActive && wasAnyGroupActive) {
                log("Mode $modeId deactivated (no trigger groups satisfied)")
                engine.deactivateMode(modeId, isManualDismiss = false)
            }
        }
    }

    fun release() {
        updateModes(emptyList())
        appManager.release()
        intentManager.release()
        locationManager.release()
        batteryManager.release()
    }

    private fun log(msg: String) {
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "TriggerGroupManager"
    }
}



