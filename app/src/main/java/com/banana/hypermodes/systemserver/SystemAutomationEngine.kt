package com.banana.hypermodes.systemserver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import com.banana.hypermodes.automation.AutomationExecutor
import com.banana.hypermodes.automation.AutomationBlock
import com.banana.hypermodes.automation.AutomationStore
import com.banana.hypermodes.automation.AutomationSystemOps
import com.banana.hypermodes.automation.BlockParameter
import com.banana.hypermodes.automation.BlockType
import com.banana.hypermodes.automation.isTriggerBlock
import com.banana.hypermodes.systemserver.executor.AppSuspendController
import com.banana.hypermodes.systemserver.executor.HotspotController
import com.banana.hypermodes.systemserver.executor.SystemOpsExecutor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 自动化触发引擎（system_server 版，零进程）。
 *
 * 与应用进程的 AutomationTriggerEngine 职责相同，但运行在 system_server 内：
 * - 监听 Settings.Global["hypermodes_automations_config"] 配置变化
 * - 注册系统事件接收器（时间、WiFi、蓝牙、电量、充电、网络）
 * - 对含触发块的启用自动化做边沿触发评估，条件满足时执行一次
 *
 * 应用被杀后触发依然生效，符合项目零进程架构。
 */
class SystemAutomationEngine(
    private val context: Context,
    classLoader: ClassLoader
) {

    private val appSuspendController = AppSuspendController(context, classLoader)
    private val hotspotController = HotspotController(context)
    private val systemOpsExecutor = SystemOpsExecutor(context)

    /** system_server 内的特权操作：全部直接调控制器，无 root 兜底。 */
    private val systemOps = object : AutomationSystemOps {
        override fun setAppsSuspended(packages: List<String>, suspend: Boolean): Boolean {
            return try {
                if (suspend) {
                    appSuspendController.suspendApps(packages)
                } else {
                    // AppSuspendController 恢复所有当前暂停应用；自动化语义为恢复指定应用，
                    // 两者在"恢复"场景效果一致（控制器只跟踪本自动化暂停的包）。
                    appSuspendController.unsuspendApps()
                }
                true
            } catch (t: Throwable) {
                Log.w(TAG, "setAppsSuspended failed: ${t.message}")
                false
            }
        }

        override fun setHotspotEnabled(enabled: Boolean): Boolean {
            return try {
                hotspotController.setHotspotEnabled(enabled)
            } catch (t: Throwable) {
                Log.w(TAG, "setHotspotEnabled failed: ${t.message}")
                false
            }
        }

        override fun writeSetting(namespace: String, key: String, value: String): Boolean {
            return systemOpsExecutor.writeSetting(namespace, key, value)
        }

        override fun setAirplaneEnabled(enabled: Boolean): Boolean {
            return systemOpsExecutor.setAirplaneEnabled(enabled)
        }

        override fun setMobileDataEnabled(enabled: Boolean): Boolean {
            return systemOpsExecutor.setMobileDataEnabled(enabled)
        }

        override fun setFlashlightEnabled(enabled: Boolean): Boolean {
            return systemOpsExecutor.setFlashlightEnabled(enabled)
        }

        override fun setPreferredSimSlot(slot: Int): Boolean {
            return systemOpsExecutor.setPreferredSimSlot(slot)
        }

        override fun setMotionSicknessReliefEnabled(enabled: Boolean): Boolean {
            return systemOpsExecutor.setMotionSicknessReliefEnabled(enabled)
        }
    }

    private val executor = AutomationExecutor(context, systemOps)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mainHandler = Handler(Looper.getMainLooper())

    /** automationId -> 上次评估时触发条件是否满足（边沿检测）。 */
    private val triggerStates = mutableMapOf<String, Boolean>()
    private val lock = Any()

    private var observerRegistered = false
    private var receiverRegistered = false

    private val configObserver = object : ContentObserver(mainHandler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            HyperLog.i(TAG, "自动化配置变化，重新评估")
            refreshIntentActions()
            evaluateAll()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_TIME_TICK,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED,
                Intent.ACTION_POWER_CONNECTED,
                Intent.ACTION_POWER_DISCONNECTED,
                WifiManager.WIFI_STATE_CHANGED_ACTION,
                WifiManager.NETWORK_STATE_CHANGED_ACTION,
                BluetoothDevice.ACTION_ACL_CONNECTED,
                BluetoothDevice.ACTION_ACL_DISCONNECTED,
                ConnectivityManager.CONNECTIVITY_ACTION -> {
                    HyperLog.i(TAG, "事件 ${intent.action}，重新评估触发条件")
                    evaluateAll()
                }
                else -> {
                    // 意图触发：匹配到 TriggerIntent 的 action 时执行对应自动化
                    val action = intent.action
                    if (action != null && intentActions.contains(action)) {
                        HyperLog.i(TAG, "收到意图广播 $action，触发匹配的自动化")
                        handleIntentTrigger(action)
                    }
                }
            }
        }
    }

    /** 所有启用自动化中 TriggerIntent 的广播 action 集合（用于意图触发监听）。 */
    private var intentActions: Set<String> = emptySet()

    fun init() {
        // 收集意图触发的 action，动态注册监听
        intentActions = collectIntentActions()
        if (!observerRegistered) {
            context.contentResolver.registerContentObserver(
                Settings.Global.getUriFor(AutomationStore.CONFIG_KEY),
                false,
                configObserver
            )
            observerRegistered = true
        }
        if (!receiverRegistered) {
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                intentActions.forEach { addAction(it) }
            }
            context.registerReceiver(receiver, filter, null, mainHandler, Context.RECEIVER_EXPORTED)
            receiverRegistered = true
        }
        HyperLog.i(TAG, "SystemAutomationEngine 已初始化")
        // 启动时评估一次
        evaluateAll()
    }

    /** 收集所有启用自动化中 TriggerIntent 块的广播 action。 */
    private fun collectIntentActions(): Set<String> {
        val automations = runCatching {
            AutomationStore.load(context).filter { it.enabled }
        }.getOrDefault(emptyList())
        val actions = mutableSetOf<String>()
        fun walk(blocks: List<AutomationBlock>) {
            for (block in blocks) {
                if (block.type is BlockType.TriggerIntent) {
                    block.stringParam("action").takeIf { it.isNotBlank() }?.let { actions.add(it) }
                }
                walk(block.children)
                walk(block.elseChildren)
            }
        }
        automations.forEach { walk(it.blocks) }
        return actions
    }

    /** 配置变化后重新收集意图 action 并更新接收器，保证新增/删除的意图触发立即生效。 */
    private fun refreshIntentActions() {
        val newActions = collectIntentActions()
        val added = newActions - intentActions
        val removed = intentActions - newActions
        if (added.isEmpty() && removed.isEmpty()) return
        intentActions = newActions
        if (receiverRegistered) {
            runCatching { context.unregisterReceiver(receiver) }
            val filter = IntentFilter().apply {
                addAction(Intent.ACTION_TIME_TICK)
                addAction(Intent.ACTION_TIME_CHANGED)
                addAction(Intent.ACTION_TIMEZONE_CHANGED)
                addAction(Intent.ACTION_POWER_CONNECTED)
                addAction(Intent.ACTION_POWER_DISCONNECTED)
                addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)
                addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
                addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
                addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
                addAction(ConnectivityManager.CONNECTIVITY_ACTION)
                intentActions.forEach { addAction(it) }
            }
            context.registerReceiver(receiver, filter, null, mainHandler, Context.RECEIVER_EXPORTED)
            HyperLog.i(TAG, "意图触发 action 已刷新：新增 ${added.size}，移除 ${removed.size}")
        }
    }

    /** 收到匹配意图：设置 pendingIntentAction 并直接执行对应自动化（事件触发，不走边沿检测）。 */
    private fun handleIntentTrigger(action: String) {
        val automations = runCatching {
            AutomationStore.load(context).filter { it.enabled }
        }.getOrDefault(emptyList())
        for (automation in automations) {
            val hasMatching = automation.blocks.any { block ->
                block.type is BlockType.TriggerIntent &&
                    block.stringParam("action") == action
            }
            if (!hasMatching) continue
            scope.launch {
                executor.pendingIntentAction = action
                val result = runCatching {
                    executor.execute(automation.blocks)
                }.getOrNull()
                executor.pendingIntentAction = null
                HyperLog.i(TAG, "意图触发执行结果：${result?.success} ${result?.message}")
            }
        }
    }

    fun shutdown() {
        if (observerRegistered) {
            runCatching { context.contentResolver.unregisterContentObserver(configObserver) }
            observerRegistered = false
        }
        if (receiverRegistered) {
            runCatching { context.unregisterReceiver(receiver) }
            receiverRegistered = false
        }
        synchronized(lock) { triggerStates.clear() }
        HyperLog.i(TAG, "SystemAutomationEngine 已关闭")
    }

    /**
     * 重新评估所有启用自动化的触发条件，边沿触发执行。
     */
    fun evaluateAll() {
        val automations = try {
            AutomationStore.load(context).filter { it.enabled }
        } catch (t: Throwable) {
            Log.w(TAG, "加载自动化配置失败: ${t.message}")
            return
        }

        for (automation in automations) {
            if (automation.blocks.none { it.isTriggerBlock() }) continue // 全局自动化不自动执行
            scope.launch {
                val met = runCatching {
                    executor.evaluateTrigger(automation.blocks)
                }.getOrDefault(false)

                val previous = synchronized(lock) {
                    triggerStates[automation.id] ?: false
                }
                val risingEdge = met && !previous
                synchronized(lock) {
                    triggerStates[automation.id] = met
                }

                if (risingEdge) {
                    HyperLog.i(TAG, "触发条件满足：${automation.name}，开始执行")
                    val result = runCatching {
                        executor.execute(automation.blocks)
                    }.getOrNull()
                    HyperLog.i(TAG, "自动化执行结果：${result?.success} ${result?.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "SystemAutomationEngine"
    }
}

/** 读取块字符串参数（system_server 触发引擎本地辅助，与 AutomationExecutor 一致）。 */
private fun AutomationBlock.stringParam(key: String, default: String = ""): String =
    parameters.find { it.key == key }
        ?.let { (it as? BlockParameter.StringParam)?.value }
        ?: default
