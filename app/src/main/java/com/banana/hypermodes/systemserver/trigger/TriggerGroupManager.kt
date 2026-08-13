package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.ModeConfig
import com.banana.hypermodes.systemserver.config.TriggerGroup
import com.banana.hypermodes.systemserver.config.ComplexTrigger
import com.banana.hypermodes.systemserver.config.ModeType
import java.util.Calendar

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
    private val holidayManager = HolidayTriggerManager(context, ::onTriggerChanged)
    private val nfcManager = NfcTriggerManager(context, ::onTriggerChanged)

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
        recheckAll()
    }

    fun updateModes(modes: List<ModeConfig>) {
        allModes = modes
        updateSubManagers()
        checkAllConditions()
        recheckAll()
    }

    fun isModeActiveByTrigger(modeId: String): Boolean {
        // A mode is active if ANY of its trigger groups is satisfied
        return groupStates[modeId]?.values?.any { it } ?: false
    }

    /** 重新评估某个模式的触发器组满足度（Time 触发由 ScheduledModeManager 的时间窗变化触发）。 */
    fun recheck(modeId: String) {
        recomputeGroups(modeId)
    }

    private fun updateSubManagers() {
                val wifiConfigs = mutableMapOf<String, List<String>>()
        val appConfigs = mutableMapOf<String, List<String>>()
        val bluetoothConfigs = mutableMapOf<String, Pair<List<String>, Boolean>>()
        val musicModeIds = mutableSetOf<String>()
        val intentConfigs = mutableMapOf<String, Triple<String?, String?, String?>>()
        val locationConfigs = mutableMapOf<String, List<Pair<String, ComplexTrigger.Location>>>()
        val batteryConfigs = mutableMapOf<String, Pair<Int, String>>()
        val holidayConfigs = mutableMapOf<String, String>()
        val nfcConfigs = mutableMapOf<String, String>()

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
                        is ComplexTrigger.Holiday -> {
                            holidayConfigs[triggerKey] = trigger.kind
                        }
                        is ComplexTrigger.Nfc -> {
                            nfcConfigs[triggerKey] = trigger.tagId
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
        holidayManager.updateConfigs(holidayConfigs)
        nfcManager.updateConfigs(nfcConfigs)
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
            is ComplexTrigger.Holiday -> "holiday_${trigger.kind}"
            is ComplexTrigger.Nfc -> "nfc_${trigger.tagId}"
        }
    }

    private fun checkAllConditions() {
        wifiManager.check()
        appManager.check()
        bluetoothManager.check()
        musicManager.check()
        batteryManager.check()
        holidayManager.check()
        nfcManager.check()
    }

    private fun onTriggerChanged(triggerKey: String, triggerType: String, isActive: Boolean) {
        // Parse triggerKey to get modeId
        // Format: "modeId_groupN_triggerKey"
        // modeId itself may contain underscores, so anchor on "_group"
        val groupMarker = "_group"
        val groupIdx = triggerKey.indexOf(groupMarker)
        if (groupIdx < 0) return
        val modeId = triggerKey.substring(0, groupIdx)

        // Update trigger state
        val modeStates = triggerStates.getOrPut(modeId) { mutableMapOf() }
        modeStates[triggerKey] = isActive

        recomputeGroups(modeId)
    }

    /** 重新评估一个模式的触发器组满足度，并在上升/下降沿激活/停用模式。 */
    private fun recomputeGroups(modeId: String) {
        val mode = allModes.find { it.id == modeId } ?: return
        if (mode.triggerGroups.isEmpty()) return

        val groups = groupStates.getOrPut(modeId) { mutableMapOf() }
        val wasAnyGroupActive = groups.values.any { it }

        mode.triggerGroups.forEachIndexed { idx, group ->
            val triggers = when (group) {
                is TriggerGroup.Single -> listOf(group.trigger)
                is TriggerGroup.Compound -> group.triggers
            }

            // Check if ALL triggers in this group are active (AND logic)。
            // Time 触发直接按当前时间窗判断，不依赖子管理器上报。
            val allActive = triggers.all { trigger ->
                when (trigger) {
                    is ComplexTrigger.Time -> isTimeActive(trigger)
                    else -> triggerStates[modeId]
                        ?.get("${modeId}_group${idx}_${getTriggerKey(trigger)}") == true
                }
            }
            groups[idx] = allActive
        }

        // 清理已删除组的残留状态，避免陈旧的 true 把模式钉在激活态。
        groups.keys.filter { it >= mode.triggerGroups.size }.forEach { groups.remove(it) }

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

    /** 对所有模式重算触发器组（用于配置加载后补上 Time 触发的状态）。 */
    private fun recheckAll() {
        allModes.forEach { recomputeGroups(it.id) }
    }

    /** 判断 Time 触发当前是否处于时间窗内（跨天支持，1=周一 .. 7=周日）。 */
    private fun isTimeActive(trigger: ComplexTrigger.Time): Boolean {
        val start = parseTime(trigger.startTime) ?: return false
        val end = parseTime(trigger.endTime) ?: return false
        val startMin = start.first * 60 + start.second
        val endMin = end.first * 60 + end.second
        if (startMin == endMin) return true

        val now = Calendar.getInstance()
        val curMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
        val repeatDays = trigger.repeatDays
            .filter { it in 1..7 }
            .takeIf { it.isNotEmpty() }
            ?: ALL_DAYS

        val inWindow = if (endMin < startMin) {
            curMin >= startMin || curMin < endMin
        } else {
            curMin >= startMin && curMin < endMin
        }
        if (!inWindow) return false

        val dayOfWeek = now.get(Calendar.DAY_OF_WEEK)
        val curDay = if (dayOfWeek == Calendar.SUNDAY) 7 else dayOfWeek - 1
        val effectiveDay = if (endMin < startMin && curMin < endMin) {
            if (curDay == 1) 7 else curDay - 1
        } else {
            curDay
        }
        return repeatDays.contains(effectiveDay)
    }

    private fun parseTime(time: String): Pair<Int, Int>? {
        val parts = time.split(":")
        if (parts.size != 2) return null
        val hour = parts[0].toIntOrNull() ?: return null
        val minute = parts[1].toIntOrNull() ?: return null
        if (hour !in 0..23 || minute !in 0..59) return null
        return hour to minute
    }

    fun release() {
        updateModes(emptyList())
        appManager.release()
        intentManager.release()
        locationManager.release()
        batteryManager.release()
        holidayManager.release()
        nfcManager.release()
    }

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
    }

    companion object {
        private const val TAG = "TriggerGroupManager"
        private val ALL_DAYS = listOf(1, 2, 3, 4, 5, 6, 7)
    }
}



