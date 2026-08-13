package com.banana.hypermodes.automation

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.utils.HyperLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * 自动化触发引擎（应用进程内第一版）。
 *
 * 职责：监听系统事件（时间、WiFi、蓝牙、电量、充电、网络），对每个启用且
 * 含触发块的自动化评估触发条件，条件从"不满足"变为"满足"（边沿触发）时
 * 执行一次自动化流程。全局自动化（无触发块）不在此自动执行。
 *
 * 后续可迁移到 system_server 与 RoutineCoreEngine 并列，实现零进程常驻触发。
 */
class AutomationTriggerEngine(private val context: Context) {

    private val appContext = context.applicationContext
    private val executor = AutomationExecutor(appContext)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val handler = Handler(Looper.getMainLooper())

    /** automationId -> 上次评估时触发条件是否满足（用于边沿检测）。 */
    private val triggerStates = mutableMapOf<String, Boolean>()
    private val lock = Any()

    private var started = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
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
                    log("事件 ${intent.action}，重新评估触发条件")
                    evaluateAll()
                }
                else -> {
                    // 意图触发：匹配到 TriggerIntent 的 action 时执行对应自动化
                    val action = intent.action
                    if (action != null && intentActions.contains(action)) {
                        log("收到意图广播 $action，触发匹配的自动化")
                        handleIntentTrigger(action)
                    }
                }
            }
        }
    }

    /** 所有启用自动化中 TriggerIntent 的广播 action 集合（用于意图触发监听）。 */
    private var intentActions: Set<String> = emptySet()

    fun start() {
        if (started) return
        started = true
        // 收集意图触发的 action，动态注册监听
        intentActions = collectIntentActions()
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
        appContext.registerReceiver(receiver, filter)
        log("触发引擎已启动")
        // 启动时评估一次（应用启动/屏幕亮起等场景）
        evaluateAll()
    }

    /** 收集所有启用自动化中 TriggerIntent 块的广播 action。 */
    private fun collectIntentActions(): Set<String> {
        val automations = runCatching {
            AutomationStore.load(appContext).filter { it.enabled }
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

    /** 收到匹配意图：设置 pendingIntentAction 并直接执行对应自动化（事件触发，不走边沿检测）。 */
    private fun handleIntentTrigger(action: String) {
        val automations = runCatching {
            AutomationStore.load(appContext).filter { it.enabled }
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
                log("意图触发执行结果：${result?.success} ${result?.message}")
            }
        }
    }

    fun stop() {
        if (!started) return
        started = false
        runCatching { appContext.unregisterReceiver(receiver) }
        synchronized(lock) { triggerStates.clear() }
        log("触发引擎已停止")
    }

    /**
     * 重新评估所有启用自动化的触发条件，边沿触发执行。
     */
    fun evaluateAll() {
        val automations = AutomationStore.load(appContext).filter { it.enabled }
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
                    log("触发条件满足：${automation.name}（${automation.id}），开始执行")
                    val result = runCatching {
                        executor.execute(automation.blocks)
                    }.getOrNull()
                    log("自动化执行结果：${result?.success} ${result?.message}")
                }
            }
        }
    }

    private fun log(msg: String) {
        HyperLog.i(TAG, msg)
    }

    companion object {
        private const val TAG = "AutomationTriggerEngine"
    }
}

/** 读取块字符串参数（应用进程触发引擎本地辅助）。 */
private fun AutomationBlock.stringParam(key: String, default: String = ""): String =
    parameters.find { it.key == key }
        ?.let { (it as? BlockParameter.StringParam)?.value }
        ?: default
