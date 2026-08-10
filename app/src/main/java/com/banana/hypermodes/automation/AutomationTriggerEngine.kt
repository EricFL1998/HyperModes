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
            }
        }
    }

    fun start() {
        if (started) return
        started = true
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
        }
        appContext.registerReceiver(receiver, filter)
        log("触发引擎已启动")
        // 启动时评估一次（应用启动/屏幕亮起等场景）
        evaluateAll()
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
        Log.i(TAG, msg)
    }

    companion object {
        private const val TAG = "AutomationTriggerEngine"
    }
}
