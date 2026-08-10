package com.banana.hypermodes.automation

import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.PowerManager
import android.provider.Settings
import android.telephony.SubscriptionManager
import android.util.Log
import com.banana.hypermodes.bridge.ModeControlBridge
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.delay
import java.util.Calendar

/**
 * 自动化执行引擎，负责解释和执行自动化块。
 *
 * 实现策略：
 * - 应用进程持有的普通权限（WiFi/蓝牙状态查询、DND、音量等）直接用 Android API；
 * - 需要系统级权限的操作（开关无线电、写 Settings.System 等）通过 libsu root shell 执行，
 *   失败时降级为 Settings.Global/Secure 写入（应用已通过 UniversalPermissionHook 获得
 *   WRITE_SECURE_SETTINGS），再失败则返回明确错误。
 */
/**
 * 系统特权操作接口。由 system_server 环境的调用方注入：
 * 应用进程走广播 + root shell，system_server 内直接调用 AppSuspendController。
 */
interface AutomationSystemOps {
    /** 暂停/恢复指定应用。返回是否成功。 */
    fun setAppsSuspended(packages: List<String>, suspend: Boolean): Boolean
}

class AutomationExecutor(
    private val context: Context,
    private val systemOps: AutomationSystemOps? = null
) {

    private val TAG = "AutomationExecutor"

    /** 根 shell 是否可用（libsu 初始化失败或设备无 root 时为 false）。 */
    private val rootAvailable: Boolean by lazy {
        try {
            Shell.isAppGrantedRoot() == true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 执行自动化块列表。
     * @param maxSteps 全局步数上限，防止嵌套/循环失控（INFINITE 表示不限制）。
     */
    suspend fun execute(
        blocks: List<AutomationBlock>,
        maxSteps: Long = DEFAULT_MAX_STEPS
    ): ExecutionResult {
        val steps = StepBudget(maxSteps)
        for (block in blocks) {
            if (!steps.consume()) {
                return ExecutionResult(success = false, message = "执行步骤超过上限，已中止")
            }
            val result = executeBlock(block, steps)
            if (!result.success) {
                return result
            }
        }
        return ExecutionResult(success = true, message = "执行完成")
    }

    /**
     * 评估自动化中的触发条件是否全部满足（多个触发块按 AND 组合）。
     * 用于引擎决定是否执行自动化；无触发块时视为恒满足（全局自动化）。
     */
    suspend fun evaluateTrigger(blocks: List<AutomationBlock>): Boolean {
        val triggers = blocks.filter { it.isTriggerBlock() }
        if (triggers.isEmpty()) return true
        val steps = StepBudget(DEFAULT_MAX_STEPS)
        for (trigger in triggers) {
            if (!steps.consume()) return false
            val result = triggerCondition(trigger)
            if (!result.success || result.conditionMet != true) return false
        }
        return true
    }

    /**
     * 执行单个块。
     */
    private suspend fun executeBlock(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        Log.d(TAG, "Executing block: ${block.label} (${block.type.id})")

        return when (block.type) {
            // ==================== 触发条件 ====================
            is BlockType.TriggerTime,
            is BlockType.TriggerWifi,
            is BlockType.TriggerBluetooth,
            is BlockType.TriggerBattery,
            is BlockType.TriggerCharging,
            is BlockType.TriggerNetwork,
            is BlockType.TriggerMusic,
            is BlockType.TriggerApp,
            is BlockType.TriggerDayOfWeek -> executeTrigger(block, steps)

            // ==================== 系统控制 ====================
            is BlockType.ToggleWifi -> executeToggleWifi(block, block.stateExpected())
            is BlockType.ToggleBluetooth -> executeToggleBluetooth(block, block.stateExpected())
            is BlockType.ToggleMobileData -> executeToggleMobileData(block, block.stateExpected())
            is BlockType.ToggleAirplane -> executeToggleAirplane(block, block.stateExpected())
            is BlockType.ToggleHotspot -> executeToggleHotspot(block, block.stateExpected())
            is BlockType.ToggleNfc -> executeToggleNfc(block, block.stateExpected())
            is BlockType.ToggleGps -> executeToggleGps(block, block.stateExpected())
            is BlockType.ToggleFlashlight -> executeToggleFlashlight(block, block.stateExpected())
            is BlockType.ToggleAutoRotate -> executeToggleAutoRotate(block, block.stateExpected())
            is BlockType.ToggleBatterySaver -> executeToggleBatterySaver(block, block.stateExpected())
            is BlockType.SetSilentMode -> executeSetSilentMode(block, block.stateExpected())
            is BlockType.SetDnd -> executeSetDnd(block)
            is BlockType.AdjustVolume -> executeAdjustVolume(block)
            is BlockType.AdjustBrightness -> executeAdjustBrightness(block)
            is BlockType.SetAutoBrightness -> executeSetAutoBrightness(block, block.stateExpected())

            // ==================== 显示 ====================
            is BlockType.SetGrayscale -> executeSetGrayscale(block, block.stateExpected())
            is BlockType.SetRaiseToWake -> executeSetRaiseToWake(block, block.stateExpected())
            is BlockType.SetWakeForNotifications -> executeSetWakeForNotifications(block, block.stateExpected())
            is BlockType.SetEyeCare -> executeSetEyeCare(block, block.stateExpected())
            is BlockType.SetRefreshRate -> executeSetRefreshRate(block)
            is BlockType.SetAdaptiveRefreshRatePro -> executeSetAdaptiveRefreshRatePro(block, block.stateExpected())

            // ==================== 设备 ====================
            is BlockType.SetPerformanceMode -> executeSetPerformanceMode(block)
            is BlockType.Set5g -> executeSet5g(block, block.stateExpected())
            is BlockType.SetPreferredSim -> executeSetPreferredSim(block)
            is BlockType.SetMotionSicknessRelief -> executeSetMotionSicknessRelief(block, block.stateExpected())

            // ==================== 模式 ====================
            is BlockType.EnableMode -> executeEnableMode(block)
            is BlockType.DisableMode -> executeDisableMode(block)

            // ==================== 应用 ====================
            is BlockType.OpenApp -> executeOpenApp(block)
            is BlockType.SuspendApps -> executeSuspendApps(block)
            is BlockType.UnsuspendApps -> executeUnsuspendApps(block)

            // ==================== 控制流 ====================
            is BlockType.IfCondition -> executeIf(block, steps)
            is BlockType.Repeat -> executeRepeat(block, steps)
            is BlockType.RepeatCount -> executeRepeatCount(block, steps)
            is BlockType.Wait -> executeWait(block)
            is BlockType.Comment -> ExecutionResult(success = true, message = "注释已跳过")

            // ==================== 逻辑运算 ====================
            is BlockType.AndCondition -> executeAnd(block, steps)
            is BlockType.OrCondition -> executeOr(block, steps)

            // ==================== 条件判断 ====================
            is BlockType.CheckWifiState -> checkWifiState(block, expected = block.stateExpected(), label = "WiFi 状态")
            is BlockType.CheckBluetoothState -> checkBluetoothState(block, expected = block.stateExpected(), label = "蓝牙状态")
            is BlockType.CheckBatteryLevel -> checkBatteryLevel(block)
            is BlockType.CheckChargingState -> checkChargingState(block, expected = block.stateExpected(), label = "充电状态")
            is BlockType.CheckTimeRange -> checkTimeRange(block)
            is BlockType.CheckDayOfWeek -> checkDayOfWeek(block)
            is BlockType.CheckScreenState -> checkScreenState(block, expected = block.stateExpected())
            is BlockType.CheckAirplaneState -> checkAirplaneState(block, expected = block.stateExpected())
            is BlockType.CheckDndState -> checkDndState(block, expected = block.stateExpected())
            is BlockType.CheckSilentState -> checkSilentState(block, expected = block.stateExpected())
            is BlockType.CheckMobileDataState -> checkMobileDataState(block, expected = block.stateExpected())
            is BlockType.CheckNetworkType -> checkNetworkType(block)
            is BlockType.CheckMusicPlaying -> checkMusicPlaying(block, expected = block.stateExpected(), label = "音乐播放")
            is BlockType.CheckAppForeground -> checkAppForeground(block)
            is BlockType.CheckAutoRotateState -> checkAutoRotateState(block, expected = block.stateExpected())
            is BlockType.CheckHotspotState -> checkHotspotState(block, expected = block.stateExpected())
            is BlockType.CheckNfcState -> checkNfcState(block, expected = block.stateExpected())
            is BlockType.CheckGpsState -> checkGpsState(block, expected = block.stateExpected())
            is BlockType.CheckVolumeLevel -> checkVolumeLevel(block)
            is BlockType.CheckBrightnessLevel -> checkBrightnessLevel(block)
        }
    }

    /**
     * 触发器块：仅评估触发条件本身，不执行作用域内操作。
     */
    private fun triggerCondition(block: AutomationBlock): ExecutionResult = when (block.type) {
        is BlockType.TriggerTime -> checkTimeRange(block, "触发")
        is BlockType.TriggerWifi -> checkWifiSsid(block)
        is BlockType.TriggerBluetooth -> checkBluetoothDevice(block)
        is BlockType.TriggerBattery -> checkBatteryLevel(block, "触发")
        is BlockType.TriggerCharging -> checkChargingState(
            block,
            expected = block.choiceParam("state", "开始充电") == "开始充电",
            "触发"
        )
        is BlockType.TriggerNetwork -> checkNetworkType(block)
        is BlockType.TriggerMusic -> checkMusicPlaying(
            block,
            expected = block.choiceParam("state", "开始播放") == "开始播放",
            "触发"
        )
        is BlockType.TriggerApp -> checkAppForeground(block)
        is BlockType.TriggerDayOfWeek -> checkDayOfWeek(block)
        else -> ExecutionResult(false, "未知触发类型")
    }

    /**
     * 执行触发器：条件满足时执行其 children（{} 作用域内操作），
     * 不满足时仅跳过作用域内操作，不影响平级块。
     */
    private suspend fun executeTrigger(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        val condition = triggerCondition(block)
        if (!condition.success) return condition
        return if (condition.conditionMet == true) {
            if (block.children.isEmpty()) {
                ExecutionResult(true, "触发条件满足，无作用域内操作")
            } else {
                execute(block.children, steps.remaining())
            }
        } else {
            ExecutionResult(true, "触发条件未满足，跳过作用域内操作")
        }
    }

    // ==================== 参数读取辅助 ====================

    private fun AutomationBlock.boolParam(key: String, default: Boolean): Boolean =
        parameters.find { it.key == key }?.let { (it as? BlockParameter.BooleanParam)?.value } ?: default

    private fun AutomationBlock.intParam(key: String, default: Int): Int =
        parameters.find { it.key == key }?.let { (it as? BlockParameter.IntParam)?.value } ?: default

    private fun AutomationBlock.stringParam(key: String, default: String = ""): String =
        parameters.find { it.key == key }?.let { (it as? BlockParameter.StringParam)?.value } ?: default

    private fun AutomationBlock.choiceParam(key: String, default: String = ""): String =
        parameters.find { it.key == key }?.let { (it as? BlockParameter.ChoiceParam)?.value } ?: default

    /** 从 state 参数（开启/关闭）解析期望布尔状态。 */
    private fun AutomationBlock.stateExpected(default: Boolean = true): Boolean =
        choiceParam("state", if (default) "开启" else "关闭") == "开启"

    // ==================== 系统控制实现 ====================

    private fun executeToggleWifi(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("svc wifi ${if (enabled) "enable" else "disable"}")
        if (ok) return ExecutionResult(true, "WiFi 已${if (enabled) "开启" else "关闭"}")
        // 降级：Settings.Global 写入（需 WRITE_SECURE_SETTINGS）
        return try {
            Settings.Global.putInt(context.contentResolver, Settings.Global.WIFI_ON, if (enabled) 1 else 0)
            ExecutionResult(true, "WiFi 已${if (enabled) "开启" else "关闭"}")
        } catch (e: Exception) {
            ExecutionResult(false, "WiFi 控制失败：${e.message}")
        }
    }

    private fun executeToggleBluetooth(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        // 优先用系统 API（BLUETOOTH_CONNECT 已授权）
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
        if (adapter != null) {
            try {
                val changed = if (enabled) adapter.enable() else adapter.disable()
                if (changed) return ExecutionResult(true, "蓝牙已${if (enabled) "开启" else "关闭"}")
            } catch (e: Exception) {
                Log.w(TAG, "Bluetooth API 失败，尝试 root: ${e.message}")
            }
        }
        val ok = runRoot(
            "cmd bluetooth_manager ${if (enabled) "enable" else "disable"}",
            "svc bluetooth ${if (enabled) "enable" else "disable"}"
        )
        return if (ok) {
            ExecutionResult(true, "蓝牙已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "蓝牙控制失败")
        }
    }

    private fun executeToggleMobileData(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("svc data ${if (enabled) "enable" else "disable"}") ||
                writeSetting("global", "mobile_data", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "移动数据已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "移动数据控制失败")
        }
    }

    private fun executeToggleAirplane(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("cmd connectivity airplane-mode ${if (enabled) "enable" else "disable"}")
        if (ok) return ExecutionResult(true, "飞行模式已${if (enabled) "开启" else "关闭"}")
        return try {
            Settings.Global.putInt(
                context.contentResolver,
                Settings.Global.AIRPLANE_MODE_ON,
                if (enabled) 1 else 0
            )
            context.sendBroadcast(
                Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED).putExtra("state", enabled)
            )
            ExecutionResult(true, "飞行模式已${if (enabled) "开启" else "关闭"}")
        } catch (e: Exception) {
            ExecutionResult(false, "飞行模式控制失败：${e.message}")
        }
    }

    private fun executeToggleHotspot(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("cmd connectivity tethering ${if (enabled) "enable" else "disable"} wifi")
        if (ok) return ExecutionResult(true, "热点已${if (enabled) "开启" else "关闭"}")
        return try {
            // 降级：反射 WifiManager.setWifiApEnabled
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val method = wifiManager.javaClass.getMethod("setWifiApEnabled", android.net.wifi.WifiConfiguration::class.java, Boolean::class.javaPrimitiveType!!)
            method.invoke(wifiManager, null, enabled)
            ExecutionResult(true, "热点已${if (enabled) "开启" else "关闭"}")
        } catch (e: Exception) {
            ExecutionResult(false, "热点控制失败：${e.message}")
        }
    }

    private fun executeToggleNfc(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put secure nfc_on ${if (enabled) 1 else 0}") ||
                writeSetting("secure", "nfc_on", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "NFC 已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "NFC 控制失败")
        }
    }

    private fun executeToggleGps(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val mode = if (enabled) 3 else 0 // 3 = 高精度
        val ok = runRoot("settings put secure location_mode $mode") ||
                writeSetting("secure", "location_mode", mode.toString())
        return if (ok) {
            ExecutionResult(true, "定位已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "定位控制失败")
        }
    }

    private fun executeToggleFlashlight(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("cmd flashlight set-flashlight ${if (enabled) "on" else "off"}")
        return if (ok) {
            ExecutionResult(true, "手电筒已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "手电筒控制失败（需 root 或设备不支持）")
        }
    }

    private fun executeToggleAutoRotate(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put system accelerometer_rotation ${if (enabled) 1 else 0}") ||
                writeSetting("system", "accelerometer_rotation", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "自动旋转已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "自动旋转控制失败")
        }
    }

    private fun executeToggleBatterySaver(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("cmd battery_saver set ${if (enabled) "true" else "false"}") ||
                writeSetting("global", "low_power", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "省电模式已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "省电模式控制失败")
        }
    }

    private fun executeSetSilentMode(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        // MIUI silence_mode: 4 = 开启, 0 = 关闭
        val ok = runRoot("settings put system silence_mode ${if (enabled) 4 else 0}") ||
                writeSetting("system", "silence_mode", if (enabled) "4" else "0")
        return if (ok) {
            ExecutionResult(true, "静音模式已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "静音模式控制失败")
        }
    }

    private fun executeSetDnd(block: AutomationBlock): ExecutionResult {
        val level = block.choiceParam("level", "仅优先")
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val filter = when (level) {
            "关闭" -> NotificationManager.INTERRUPTION_FILTER_ALL
            "仅闹钟" -> NotificationManager.INTERRUPTION_FILTER_ALARMS
            "仅优先" -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
            "完全静音" -> NotificationManager.INTERRUPTION_FILTER_NONE
            else -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
        }
        return try {
            nm.setInterruptionFilter(filter)
            ExecutionResult(true, "勿扰模式已设为「$level」")
        } catch (e: Exception) {
            ExecutionResult(false, "勿扰模式设置失败：${e.message}")
        }
    }

    private fun executeAdjustVolume(block: AutomationBlock): ExecutionResult {
        val level = block.intParam("level", 50)
        val streamName = block.choiceParam("stream", "媒体")
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val stream = when (streamName) {
            "媒体" -> AudioManager.STREAM_MUSIC
            "铃声" -> AudioManager.STREAM_RING
            "通知" -> AudioManager.STREAM_NOTIFICATION
            "闹钟" -> AudioManager.STREAM_ALARM
            else -> AudioManager.STREAM_MUSIC
        }
        return try {
            val max = audioManager.getStreamMaxVolume(stream)
            val target = (level * max / 100).coerceIn(0, max)
            audioManager.setStreamVolume(stream, target, 0)
            ExecutionResult(true, "$streamName 音量已设为 $level%")
        } catch (e: Exception) {
            ExecutionResult(false, "音量设置失败：${e.message}")
        }
    }

    private fun executeAdjustBrightness(block: AutomationBlock): ExecutionResult {
        val level = block.intParam("level", 50)
        val value = (level * 255 / 100).coerceIn(0, 255)
        val ok = runRoot("settings put system screen_brightness $value") ||
                writeSetting("system", "screen_brightness", value.toString())
        return if (ok) {
            ExecutionResult(true, "亮度已设为 $level%")
        } else {
            ExecutionResult(false, "亮度控制失败")
        }
    }

    private fun executeSetAutoBrightness(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put system screen_brightness_mode ${if (enabled) 1 else 0}") ||
                writeSetting("system", "screen_brightness_mode", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "自动亮度已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "自动亮度控制失败")
        }
    }

    // ==================== 显示实现 ====================

    private fun executeSetGrayscale(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot(
            "settings put secure accessibility_display_daltonizer_enabled ${if (enabled) 1 else 0}",
            "settings put secure accessibility_display_daltonizer 0"
        ) || (writeSetting("secure", "accessibility_display_daltonizer_enabled", if (enabled) "1" else "0") &&
                writeSetting("secure", "accessibility_display_daltonizer", "0"))
        return if (ok) {
            ExecutionResult(true, "灰度模式已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "灰度模式控制失败")
        }
    }

    private fun executeSetRaiseToWake(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put system gesture_wakeup ${if (enabled) 1 else 0}") ||
                writeSetting("system", "gesture_wakeup", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "抬腕亮屏已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "抬腕亮屏控制失败")
        }
    }

    private fun executeSetWakeForNotifications(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put system wakeup_for_keyguard_notification ${if (enabled) 1 else 0}") ||
                writeSetting("system", "wakeup_for_keyguard_notification", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "通知亮屏已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "通知亮屏控制失败")
        }
    }

    private fun executeSetEyeCare(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put system screen_paper_mode_enabled ${if (enabled) 1 else 0}") ||
                writeSetting("system", "screen_paper_mode_enabled", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "纸质护眼已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "纸质护眼控制失败")
        }
    }

    private fun executeSetRefreshRate(block: AutomationBlock): ExecutionResult {
        val rate = block.choiceParam("rate", "60")
        val ok = runRoot("settings put secure user_refresh_rate $rate") ||
                writeSetting("secure", "user_refresh_rate", rate)
        return if (ok) {
            ExecutionResult(true, "刷新率已设为 ${rate}Hz")
        } else {
            ExecutionResult(false, "刷新率设置失败")
        }
    }

    private fun executeSetAdaptiveRefreshRatePro(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        // mimotion_pwm_enable: 2 = 自适应 Pro, 1 = 关闭
        val ok = runRoot("settings put secure mimotion_pwm_enable ${if (enabled) 2 else 1}") ||
                writeSetting("secure", "mimotion_pwm_enable", if (enabled) "2" else "1")
        return if (ok) {
            ExecutionResult(true, "自适应刷新率 Pro 已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "自适应刷新率 Pro 控制失败")
        }
    }

    // ==================== 设备实现 ====================

    private fun executeSetPerformanceMode(block: AutomationBlock): ExecutionResult {
        val modeName = block.choiceParam("mode", "均衡")
        val mode = when (modeName) {
            "性能" -> 1
            "省电" -> 2
            else -> 0
        }
        val ok = runRoot("settings put system performance_mode $mode") ||
                writeSetting("system", "performance_mode", mode.toString())
        return if (ok) {
            ExecutionResult(true, "性能模式已设为「$modeName」")
        } else {
            ExecutionResult(false, "性能模式设置失败")
        }
    }

    private fun executeSet5g(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        val ok = runRoot("settings put global enabled_5g_mode ${if (enabled) 1 else 0}") ||
                writeSetting("global", "enabled_5g_mode", if (enabled) "1" else "0")
        return if (ok) {
            ExecutionResult(true, "5G 已${if (enabled) "开启" else "关闭"}")
        } else {
            ExecutionResult(false, "5G 控制失败")
        }
    }

    private fun executeSetPreferredSim(block: AutomationBlock): ExecutionResult {
        val slotName = block.choiceParam("slot", "SIM 1")
        val slot = if (slotName.contains("2")) 1 else 0
        return try {
            val sm = context.getSystemService(SubscriptionManager::class.java) ?: return ExecutionResult(false, "订阅服务不可用")
            val infos = sm.activeSubscriptionInfoList ?: emptyList()
            val target = infos.firstOrNull { it.simSlotIndex == slot } ?: return ExecutionResult(false, "未找到对应 SIM 卡")
            val method = SubscriptionManager::class.java.getMethod("setDefaultDataSubId", Int::class.java)
            method.invoke(sm, target.subscriptionId)
            ExecutionResult(true, "默认数据卡已切换为 ${if (slot == 0) "SIM 1" else "SIM 2"}")
        } catch (e: Exception) {
            ExecutionResult(false, "SIM 切换失败：${e.message}")
        }
    }

    private fun executeSetMotionSicknessRelief(block: AutomationBlock, enabled: Boolean): ExecutionResult {
        return try {
            // 官方开关：securitycenter 监听 settings_car_sickness_mode 并启停服务
            Settings.System.putInt(
                context.contentResolver,
                "settings_car_sickness_mode",
                if (enabled) 1 else 0
            )

            if (enabled) {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.carsickness.service.CarSicknessService"
                    )
                    action = "miui.carsickness.remind_always"
                }
                context.startService(intent)
            } else {
                // 1) 广播：Receiver 再次把开关写 0
                runCatching {
                    context.sendBroadcast(
                        Intent("com.miui.action.carsickness_relief_close")
                    )
                }
                // 2) intent：服务调用 AntiCarsickManager.F() 移除黑点
                runCatching {
                    val closeIntent = Intent().apply {
                        component = ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.carsickness.service.CarSicknessService"
                        )
                        action = "miui.carsickness.close_car_sickness"
                    }
                    context.startService(closeIntent)
                }
                // 3) stopService：onDestroy -> F() 兜底移除黑点
                runCatching {
                    context.stopService(
                        Intent().apply {
                            component = ComponentName(
                                "com.miui.securitycenter",
                                "com.miui.carsickness.service.CarSicknessService"
                            )
                        }
                    )
                }
            }
            ExecutionResult(true, "防晕车已${if (enabled) "开启" else "关闭"}")
        } catch (e: Exception) {
            ExecutionResult(false, "防晕车控制失败：${e.message}")
        }
    }

    // ==================== 模式实现 ====================

    private fun executeEnableMode(block: AutomationBlock): ExecutionResult {
        val modeId = block.stringParam("modeId")
        if (modeId.isBlank()) return ExecutionResult(false, "未指定要启用的模式")
        ModeControlBridge.activateMode(context, modeId)
        return ExecutionResult(true, "已启用模式：$modeId")
    }

    private fun executeDisableMode(block: AutomationBlock): ExecutionResult {
        val modeId = block.stringParam("modeId")
        if (modeId.isBlank()) return ExecutionResult(false, "未指定要关闭的模式")
        ModeControlBridge.deactivateMode(context, modeId)
        return ExecutionResult(true, "已关闭模式：$modeId")
    }

    // ==================== 应用实现 ====================

    private fun executeOpenApp(block: AutomationBlock): ExecutionResult {
        val packages = block.stringParam("packages").split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (packages.isEmpty()) return ExecutionResult(false, "未指定应用包名")
        val pm = context.packageManager
        var opened = false
        for (pkg in packages) {
            try {
                val intent = pm.getLaunchIntentForPackage(pkg)
                if (intent != null) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                    opened = true
                }
            } catch (e: Exception) {
                Log.w(TAG, "openApp failed for $pkg: ${e.message}")
            }
        }
        return if (opened) {
            ExecutionResult(true, "已打开应用：${packages.joinToString()}")
        } else {
            ExecutionResult(false, "应用启动失败（未找到启动入口）")
        }
    }

    private fun executeSuspendApps(block: AutomationBlock): ExecutionResult {
        val packages = block.stringParam("packages").split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (packages.isEmpty()) return ExecutionResult(false, "未指定应用包名")
        return suspendOrUnsuspend(packages, true)
    }

    private fun executeUnsuspendApps(block: AutomationBlock): ExecutionResult {
        val packages = block.stringParam("packages").split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (packages.isEmpty()) return ExecutionResult(false, "未指定应用包名")
        return suspendOrUnsuspend(packages, false)
    }

    private fun suspendOrUnsuspend(packages: List<String>, suspend: Boolean): ExecutionResult {
        // system_server 内：直接调用特权控制器（无需广播绕行）
        systemOps?.let { ops ->
            val ok = try {
                ops.setAppsSuspended(packages, suspend)
            } catch (e: Exception) {
                Log.w(TAG, "systemOps suspend failed: ${e.message}")
                false
            }
            if (ok) {
                return ExecutionResult(true, "已${if (suspend) "暂停" else "恢复"}应用：${packages.joinToString()}")
            }
            return ExecutionResult(false, "应用${if (suspend) "暂停" else "恢复"}失败")
        }

        // 应用进程：优先走 system_server 特权桥接（IPackageManager.setPackagesSuspendedAsUser）
        val sent = try {
            val intent = Intent(com.banana.hypermodes.protocol.Protocol.ACTION_SET_PACKAGES_SUSPENDED)
                .putExtra(com.banana.hypermodes.protocol.Protocol.EXTRA_PACKAGES, packages.toTypedArray())
                .putExtra(com.banana.hypermodes.protocol.Protocol.EXTRA_SUSPENDED, suspend)
                .setPackage("android")
            context.sendBroadcast(intent, com.banana.hypermodes.protocol.Protocol.PERMISSION_CONTROL)
            true
        } catch (e: Exception) {
            Log.w(TAG, "bridge broadcast failed: ${e.message}")
            false
        }
        if (sent) return ExecutionResult(true, "已${if (suspend) "暂停" else "恢复"}应用：${packages.joinToString()}")

        // 降级：root shell `pm suspend` / `pm unsuspend`
        val cmds = packages.map { pkg ->
            if (suspend) "pm suspend $pkg" else "pm unsuspend $pkg"
        }
        val ok = runRoot(*cmds.toTypedArray())
        return if (ok) {
            ExecutionResult(true, "已${if (suspend) "暂停" else "恢复"}应用：${packages.joinToString()}")
        } else {
            ExecutionResult(false, "应用${if (suspend) "暂停" else "恢复"}失败")
        }
    }

    // ==================== 控制流实现 ====================

    private suspend fun executeIf(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        // children[0] 为条件块，children[1..] 为 THEN 分支
        if (block.children.isEmpty()) {
            return ExecutionResult(false, "如果块缺少条件")
        }
        val condition = block.children.first()
        val result = executeBlock(condition, steps)
        if (!result.success) return result

        return if (result.conditionMet == true) {
            val thenBlocks = block.children.drop(1)
            if (thenBlocks.isEmpty()) ExecutionResult(true, "条件满足，无操作")
            else execute(thenBlocks, steps.remaining())
        } else {
            if (block.elseChildren.isEmpty()) ExecutionResult(true, "条件不满足，无操作")
            else execute(block.elseChildren, steps.remaining())
        }
    }

    private suspend fun executeRepeat(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        val count = block.intParam("count", 1)
        return repeatLoop(block, count, steps)
    }

    private suspend fun executeRepeatCount(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        val count = block.intParam("count", 1)
        return repeatLoop(block, count, steps)
    }

    private suspend fun repeatLoop(block: AutomationBlock, count: Int, steps: StepBudget): ExecutionResult {
        val children = block.children
        if (children.isEmpty()) return ExecutionResult(true, "重复执行完成（0 次操作）")
        for (i in 1..count) {
            if (!steps.consume()) return ExecutionResult(false, "执行步骤超过上限，已中止")
            val result = execute(children, steps.remaining())
            if (!result.success) {
                return ExecutionResult(
                    success = false,
                    message = "重复执行失败（第 $i 次）：${result.message}",
                    blockId = result.blockId
                )
            }
        }
        return ExecutionResult(true, "重复执行完成（$count 次）")
    }

    private suspend fun executeWait(block: AutomationBlock): ExecutionResult {
        val seconds = block.intParam("seconds", 1)
        delay(seconds * 1000L)
        return ExecutionResult(true, "等待 $seconds 秒")
    }

    // ==================== 逻辑运算实现 ====================

    private suspend fun executeAnd(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        for (child in block.children) {
            val result = executeBlock(child, steps)
            if (!result.success) return result
            if (result.conditionMet == false) {
                return ExecutionResult(true, "并且条件不满足", conditionMet = false)
            }
        }
        return ExecutionResult(true, "并且条件满足", conditionMet = true)
    }

    private suspend fun executeOr(block: AutomationBlock, steps: StepBudget): ExecutionResult {
        for (child in block.children) {
            val result = executeBlock(child, steps)
            if (!result.success) return result
            if (result.conditionMet == true) {
                return ExecutionResult(true, "或者条件满足", conditionMet = true)
            }
        }
        return ExecutionResult(true, "或者条件不满足", conditionMet = false)
    }

    // ==================== 条件判断实现 ====================

    private fun checkWifiState(block: AutomationBlock, expected: Boolean, label: String = "WiFi 状态"): ExecutionResult {
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val actual = wifiManager.isWifiEnabled
        return conditionResult(label, actual, expected)
    }

    private fun checkBluetoothState(block: AutomationBlock, expected: Boolean, label: String = "蓝牙状态"): ExecutionResult {
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
        val actual = adapter?.isEnabled ?: false
        return conditionResult(label, actual, expected)
    }

    private fun checkBluetoothDevice(block: AutomationBlock): ExecutionResult {
        val expectedDevice = block.stringParam("device").trim()
        val connect = block.choiceParam("connect", "已连接")
        val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
        val connectedNames = try {
            adapter?.bondedDevices
                ?.filter { isDeviceConnected(it) }
                ?.map { it.name ?: it.address }
                ?.toSet()
                ?: emptySet()
        } catch (e: Exception) {
            emptySet()
        }
        // 未指定设备时默认"全部蓝牙设备"：只要连接了任意设备即视为已连接
        val hasAnyDevice = connectedNames.isNotEmpty()
        val connected = if (expectedDevice.isBlank()) {
            hasAnyDevice
        } else {
            connectedNames.any { it.equals(expectedDevice, ignoreCase = true) }
        }
        val met = when (connect) {
            "已连接" -> connected
            "已断开连接" -> !connected
            else -> true // 已连接或断开连接：状态变化即触发（边沿检测处理）
        }
        return ExecutionResult(
            true,
            "蓝牙触发：当前已连接 ${connectedNames.ifEmpty { "无" }}${if (met) "满足" else "不满足"}",
            conditionMet = met
        )
    }

    /** BluetoothDevice.isConnected 是隐藏 API，通过反射判断。 */
    private fun isDeviceConnected(device: Any): Boolean {
        return try {
            val method = device.javaClass.getMethod("isConnected")
            method.invoke(device) as? Boolean ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun checkBatteryLevel(block: AutomationBlock, label: String = "电量"): ExecutionResult {
        val operator = block.choiceParam("operator", "大于")
            .let { if (it == "低于" && label == "触发") "小于" else it }
        val level = block.intParam("level", 50)
        val actual = currentBatteryLevel()
        val met = compareInt(actual, level, operator)
        return ExecutionResult(
            success = true,
            message = "$label $actual% ${operator} $level%",
            conditionMet = met
        )
    }

    private fun checkChargingState(block: AutomationBlock, expected: Boolean, label: String = "充电状态"): ExecutionResult {
        val status = currentBatteryStatus()
        val charging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        return conditionResult(label, charging, expected)
    }

    private fun checkTimeRange(block: AutomationBlock, label: String = "当前时间"): ExecutionResult {
        val start = block.stringParam("start", "00:00")
        val end = block.stringParam("end", "23:59")
        val now = Calendar.getInstance()
        val startMin = parseMinutes(start)
        val endMin = parseMinutes(end)
        val nowMin = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        val inTimeRange = if (startMin <= endMin) {
            nowMin in startMin..endMin
        } else {
            // 跨午夜（如 22:00 - 06:00）
            nowMin >= startMin || nowMin <= endMin
        }

        // 星期过滤（时间触发块的"重复"参数：每天/工作日/周末）
        val repeat = block.choiceParam("repeat", "每天")
        val today = now.get(Calendar.DAY_OF_WEEK)
        val isWeekend = today == Calendar.SATURDAY || today == Calendar.SUNDAY
        val dayOk = when (repeat) {
            "工作日" -> !isWeekend
            "周末" -> isWeekend
            else -> true // 每天或未设置
        }

        val met = inTimeRange && dayOk
        return ExecutionResult(
            success = true,
            message = "$label ${pad(now.get(Calendar.HOUR_OF_DAY))}:${pad(now.get(Calendar.MINUTE))} " +
                    "在 $start-$end 内（$repeat）",
            conditionMet = met
        )
    }

    private fun checkWifiSsid(block: AutomationBlock): ExecutionResult {
        val expectedSsid = block.stringParam("ssid").trim().removePrefix("\"").removeSuffix("\"")
        val connect = block.choiceParam("connect", "已加入")
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val currentSsid = wifiManager.connectionInfo?.ssid?.removePrefix("\"")?.removeSuffix("\"")
        // 未指定 SSID 时默认"全部 WiFi"：只要连接了任意 WiFi 即视为已加入
        val hasAnyWifi = !currentSsid.isNullOrBlank()
        val connected = if (expectedSsid.isBlank()) {
            hasAnyWifi
        } else {
            currentSsid.equals(expectedSsid, ignoreCase = true)
        }
        val met = when (connect) {
            "已加入" -> connected
            "已断开连接" -> !connected
            else -> true // 已加入或断开连接：状态变化即触发（边沿检测处理）
        }
        return ExecutionResult(
            true,
            "WiFi 触发：当前${currentSsid ?: "未连接"}${if (met) "满足" else "不满足"}",
            conditionMet = met
        )
    }

    private fun checkDayOfWeek(block: AutomationBlock): ExecutionResult {
        val days = block.choiceParam("days", "每天")
        val today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val isWeekend = today == Calendar.SATURDAY || today == Calendar.SUNDAY
        val met = when (days) {
            "周一至周五" -> !isWeekend
            "周末" -> isWeekend
            else -> true
        }
        return ExecutionResult(true, "今天是${weekdayName(today)}，条件${
            if (met) "满足" else "不满足"
        }", conditionMet = met)
    }

    private fun checkScreenState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val actual = pm.isInteractive
        return conditionResult("屏幕状态", actual, expected)
    }

    private fun checkAirplaneState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = Settings.Global.getInt(
            context.contentResolver,
            Settings.Global.AIRPLANE_MODE_ON,
            0
        ) == 1
        return conditionResult("飞行模式", actual, expected)
    }

    private fun checkDndState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val actual = nm.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
        return conditionResult("勿扰模式", actual, expected)
    }

    private fun checkSilentState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = Settings.System.getInt(context.contentResolver, "silence_mode", 0) == 4
        return conditionResult("静音模式", actual, expected)
    }

    private fun checkMobileDataState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = Settings.Global.getInt(context.contentResolver, "mobile_data", 0) == 1
        return conditionResult("移动数据", actual, expected)
    }

    private fun checkNetworkType(block: AutomationBlock): ExecutionResult {
        val expected = block.choiceParam("type", "WiFi")
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return ExecutionResult(
            true, "无网络", conditionMet = (expected == "无网络")
        )
        val caps = cm.getNetworkCapabilities(network) ?: return ExecutionResult(
            true, "无网络", conditionMet = (expected == "无网络")
        )
        val actual = when {
            caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "WiFi"
            caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "移动数据"
            else -> "无网络"
        }
        val met = actual == expected
        return ExecutionResult(true, "当前网络：$actual", conditionMet = met)
    }

    private fun checkMusicPlaying(block: AutomationBlock, expected: Boolean, label: String = "音乐播放"): ExecutionResult {
        val msm = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
        val controllers = msm.getActiveSessions(null)
        val playing = controllers.any { ctrl ->
            ctrl.playbackState?.state == PlaybackState.STATE_PLAYING
        }
        return conditionResult(label, playing, expected)
    }

    private fun checkAppForeground(block: AutomationBlock): ExecutionResult {
        val packageName = block.stringParam("package")
        if (packageName.isBlank()) return ExecutionResult(false, "未指定应用包名")
        val actual = foregroundPackage() == packageName
        return ExecutionResult(
            true,
            "前台应用${if (actual) "是" else "不是"} $packageName",
            conditionMet = actual
        )
    }

    private fun checkAutoRotateState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        ) == 1
        return conditionResult("自动旋转", actual, expected)
    }

    private fun checkHotspotState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = try {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.javaClass.getMethod("isWifiApEnabled").invoke(wifiManager) as Boolean
        } catch (e: Exception) {
            false
        }
        return conditionResult("个人热点", actual, expected)
    }

    private fun checkNfcState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = Settings.Secure.getInt(context.contentResolver, "nfc_on", 0) == 1
        return conditionResult("NFC", actual, expected)
    }

    private fun checkGpsState(block: AutomationBlock, expected: Boolean): ExecutionResult {
        val actual = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.LOCATION_MODE,
            Settings.Secure.LOCATION_MODE_OFF
        ) != Settings.Secure.LOCATION_MODE_OFF
        return conditionResult("定位", actual, expected)
    }

    private fun checkVolumeLevel(block: AutomationBlock): ExecutionResult {
        val operator = block.choiceParam("operator", "大于")
        val level = block.intParam("level", 50)
        val streamName = block.choiceParam("stream", "媒体")
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val stream = when (streamName) {
            "媒体" -> AudioManager.STREAM_MUSIC
            "铃声" -> AudioManager.STREAM_RING
            "通知" -> AudioManager.STREAM_NOTIFICATION
            "闹钟" -> AudioManager.STREAM_ALARM
            else -> AudioManager.STREAM_MUSIC
        }
        val max = audioManager.getStreamMaxVolume(stream)
        val current = audioManager.getStreamVolume(stream)
        val actualPct = if (max > 0) current * 100 / max else 0
        val met = compareInt(actualPct, level, operator)
        return ExecutionResult(
            true,
            "$streamName 音量 $actualPct% ${operator} $level%",
            conditionMet = met
        )
    }

    private fun checkBrightnessLevel(block: AutomationBlock): ExecutionResult {
        val operator = block.choiceParam("operator", "大于")
        val level = block.intParam("level", 50)
        val raw = Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, 128)
        val actualPct = raw * 100 / 255
        val met = compareInt(actualPct, level, operator)
        return ExecutionResult(
            true,
            "亮度 $actualPct% ${operator} $level%",
            conditionMet = met
        )
    }

    // ==================== 内部工具 ====================

    private fun conditionResult(label: String, actual: Boolean, expected: Boolean): ExecutionResult {
        val met = actual == expected
        return ExecutionResult(
            success = true,
            message = "$label${if (met) "满足" else "不满足"}条件",
            conditionMet = met
        )
    }

    private fun compareInt(actual: Int, target: Int, operator: String): Boolean = when (operator) {
        "大于" -> actual > target
        "小于" -> actual < target
        "等于" -> actual == target
        else -> false
    }

    private fun currentBatteryLevel(): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    private fun currentBatteryStatus(): Int {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
    }

    private fun parseMinutes(hhmm: String): Int {
        val parts = hhmm.split(":")
        val h = parts.getOrNull(0)?.toIntOrNull() ?: 0
        val m = parts.getOrNull(1)?.toIntOrNull() ?: 0
        return h.coerceIn(0, 23) * 60 + m.coerceIn(0, 59)
    }

    private fun pad(v: Int) = v.toString().padStart(2, '0')

    private fun weekdayName(day: Int): String = when (day) {
        Calendar.MONDAY -> "周一"
        Calendar.TUESDAY -> "周二"
        Calendar.WEDNESDAY -> "周三"
        Calendar.THURSDAY -> "周四"
        Calendar.FRIDAY -> "周五"
        Calendar.SATURDAY -> "周六"
        else -> "周日"
    }

    private fun foregroundPackage(): String? {
        // 尝试 ActivityManager.getRunningTasks（需 GET_TASKS 权限，应用已声明）
        try {
            val am = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
            val tasks = am.getRunningTasks(1)
            tasks?.firstOrNull()?.topActivity?.packageName?.let { return it }
        } catch (e: Exception) {
            Log.w(TAG, "getRunningTasks failed: ${e.message}")
        }
        // 降级：root dumpsys
        return try {
            val result = Shell.cmd("dumpsys window windows | grep -E 'mCurrentFocus|mFocusedApp' | head -1").exec()
            if (result.isSuccess) {
                val line = result.getOut().firstOrNull() ?: return null
                Regex("([a-zA-Z0-9_.]+)/").find(line)?.groupValues?.get(1)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 执行 root shell 命令。任一命令成功即返回 true。
     */
    private fun runRoot(vararg commands: String): Boolean {
        if (!rootAvailable) return false
        return try {
            commands.any { cmd ->
                val result = Shell.cmd(cmd).exec()
                if (result.isSuccess) true
                else {
                    Log.d(TAG, "root cmd failed: $cmd -> ${result.getErr().joinToString()}")
                    false
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "root shell error: ${e.message}")
            false
        }
    }

    /**
     * 直接写入 Settings（system_server 有全部系统权限；应用进程通过
     * UniversalPermissionHook 获得 WRITE_SECURE_SETTINGS，Global/Secure 可写）。
     * 作为 root 命令的降级路径，保证零进程场景（无 root shell）也能执行。
     *
     * @param namespace "global" / "secure" / "system"
     */
    private fun writeSetting(namespace: String, key: String, value: String): Boolean {
        return try {
            val resolver = context.contentResolver
            val ok = when (namespace) {
                "global" -> Settings.Global.putString(resolver, key, value)
                "secure" -> Settings.Secure.putString(resolver, key, value)
                else -> Settings.System.putString(resolver, key, value)
            }
            if (!ok) Log.w(TAG, "writeSetting rejected: $namespace/$key=$value")
            ok
        } catch (e: Exception) {
            Log.w(TAG, "writeSetting failed: $namespace/$key -> ${e.message}")
            false
        }
    }

    companion object {
        private const val TAG = "AutomationExecutor"
        private const val DEFAULT_MAX_STEPS = 10_000L
    }

    /** 全局步数预算：消耗 + 剩余，防止失控循环。 */
    private class StepBudget(private var remaining: Long) {
        fun consume(): Boolean {
            if (remaining <= 0) return false
            remaining--
            return true
        }

        fun remaining(): Long = remaining
    }
}

/**
 * 执行结果
 */
data class ExecutionResult(
    val success: Boolean,
    val message: String,
    val blockId: String? = null,
    val conditionMet: Boolean? = null // 用于条件判断块
)

/** 是否为触发条件块（自动化流程中的"当...时"门控）。 */
fun AutomationBlock.isTriggerBlock(): Boolean = when (type) {
    is BlockType.TriggerTime,
    is BlockType.TriggerWifi,
    is BlockType.TriggerBluetooth,
    is BlockType.TriggerBattery,
    is BlockType.TriggerCharging,
    is BlockType.TriggerNetwork,
    is BlockType.TriggerMusic,
    is BlockType.TriggerApp,
    is BlockType.TriggerDayOfWeek -> true
    else -> false
}
