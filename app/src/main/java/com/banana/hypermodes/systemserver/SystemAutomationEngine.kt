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
import com.banana.hypermodes.automation.AutomationExecutor
import com.banana.hypermodes.automation.AutomationStore
import com.banana.hypermodes.automation.AutomationSystemOps
import com.banana.hypermodes.automation.isTriggerBlock
import com.banana.hypermodes.systemserver.executor.AppSuspendController
import com.banana.hypermodes.systemserver.executor.HotspotController
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

    /** system_server 内的特权操作：暂停/恢复应用直接调 AppSuspendController。 */
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
            Log.i(TAG, "自动化配置变化，重新评估")
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
                    Log.i(TAG, "事件 ${intent.action}，重新评估触发条件")
                    evaluateAll()
                }
            }
        }
    }

    fun init() {
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
            }
            context.registerReceiver(receiver, filter, null, mainHandler, Context.RECEIVER_EXPORTED)
            receiverRegistered = true
        }
        Log.i(TAG, "SystemAutomationEngine 已初始化")
        // 启动时评估一次
        evaluateAll()
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
        Log.i(TAG, "SystemAutomationEngine 已关闭")
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
                    Log.i(TAG, "触发条件满足：${automation.name}，开始执行")
                    val result = runCatching {
                        executor.execute(automation.blocks)
                    }.getOrNull()
                    Log.i(TAG, "自动化执行结果：${result?.success} ${result?.message}")
                }
            }
        }
    }

    companion object {
        private const val TAG = "SystemAutomationEngine"
    }
}
