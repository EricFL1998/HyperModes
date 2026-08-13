package com.banana.hypermodes.automation

import android.content.Context
import android.content.Intent
import android.util.Log
import com.banana.hypermodes.protocol.Protocol

/**
 * 应用进程的 AutomationSystemOps 实现：把每个特权操作编码成广播发给
 * system_server（SystemModeHook 注册的 bridge），由 system_server 用
 * 系统 API 真正执行。这样自动化测试/触发器和应用进程都走同一条 hook 路径，
 * 不再依赖 root shell。
 *
 * 广播是异步的，这里返回"广播已发出"即为成功（与系统级操作的实际结果
 * 由 system_server 内的执行结果为准）。
 */
class BridgeSystemOps(private val context: Context) : AutomationSystemOps {

    private fun send(action: String, configure: Intent.() -> Unit): Boolean {
        return try {
            val intent = Intent(action).apply(configure)
                .setPackage(Protocol.FRAMEWORK_PACKAGE)
            // 不再传 receiverPermission：接收方（system_server）不可能持有本 App 的
            // signature 权限 PERMISSION_CONTROL，之前这里会导致广播被 AMS 静默丢弃。
            // 发送方鉴权已由 receiver 注册时的 broadcastPermission=PERMISSION_CONTROL 保证。
            context.sendBroadcast(intent)
            true
        } catch (e: Exception) {
            Log.w(TAG, "bridge broadcast $action failed: ${e.message}")
            false
        }
    }

    override fun setAppsSuspended(packages: List<String>, suspend: Boolean): Boolean {
        return send(Protocol.ACTION_SET_PACKAGES_SUSPENDED) {
            putExtra(Protocol.EXTRA_PACKAGES, packages.toTypedArray())
            putExtra(Protocol.EXTRA_SUSPENDED, suspend)
        }
    }

    override fun setHotspotEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SET_HOTSPOT_ENABLED) {
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    override fun writeSetting(namespace: String, key: String, value: String): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_WRITE_SETTING)
            putExtra(Protocol.EXTRA_NAMESPACE, namespace)
            putExtra(Protocol.EXTRA_KEY, key)
            putExtra(Protocol.EXTRA_VALUE, value)
        }
    }

    override fun setAirplaneEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_AIRPLANE_ENABLED)
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    override fun setMobileDataEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_MOBILE_DATA_ENABLED)
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    override fun setFlashlightEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_FLASHLIGHT_ENABLED)
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    override fun setPreferredSimSlot(slot: Int): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_PREFERRED_SIM_SLOT)
            putExtra(Protocol.EXTRA_SLOT, slot)
        }
    }

    override fun setMotionSicknessReliefEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_MOTION_SICKNESS_RELIEF)
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    override fun setWifiEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_WIFI_ENABLED)
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    override fun setBluetoothEnabled(enabled: Boolean): Boolean {
        return send(Protocol.ACTION_SYSTEM_OP) {
            putExtra(Protocol.EXTRA_OP, Protocol.OP_SET_BLUETOOTH_ENABLED)
            putExtra(Protocol.EXTRA_ENABLED, enabled)
        }
    }

    private companion object {
        const val TAG = "BridgeSystemOps"
    }
}
