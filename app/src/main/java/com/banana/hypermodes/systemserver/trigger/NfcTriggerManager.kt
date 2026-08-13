package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.utils.HyperLog

/**
 * NFC 标签触发。
 *
 * 事件式触发：App 前台扫描到 NFC 标签后广播 [Protocol.ACTION_NFC_TAG]，
 * 这里匹配配置的标签 ID（留空 = 任意标签）。命中后**切换**该触发器的开关状态：
 * 扫描 → 激活模式，再次扫描同一标签 → 关闭模式。这样单个标签即可开/关模式，
 * 无需额外的停用事件。
 */
class NfcTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val lock = Any()
    private val handler = Handler(Looper.getMainLooper())

    /** triggerKey -> tagId（十六进制小写；空串 = 匹配任意标签） */
    private var configs: Map<String, String> = emptyMap()

    /** 每个触发器的当前开关状态（NFC 切换用） */
    private val activeStates = mutableMapOf<String, Boolean>()

    private var isReceiverRegistered = false
    private var isReleased = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val scanned = intent.getStringExtra(Protocol.EXTRA_NFC_TAG_ID)?.lowercase()
            val snapshot = synchronized(lock) {
                if (isReleased) return
                configs
            }
            if (scanned == null) return
            HyperLog.i(TAG, "NFC tag scanned: $scanned")
            snapshot.forEach { (triggerKey, tagId) ->
                if (tagId.isBlank() || tagId.equals(scanned, ignoreCase = true)) {
                    val next = synchronized(lock) {
                        val cur = activeStates[triggerKey] ?: false
                        val n = !cur
                        activeStates[triggerKey] = n
                        n
                    }
                    HyperLog.i(TAG, "NFC trigger $triggerKey -> $next")
                    callback(triggerKey, "nfc", next)
                }
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, String>) {
        val oldConfigs = synchronized(lock) {
            if (isReleased) {
                Log.w(TAG, "Manager is released, ignoring updateConfigs")
                return
            }
            val old = configs
            configs = newConfigs
            // 清掉已删除配置的开关状态
            activeStates.keys.retainAll(newConfigs.keys)
            old
        }

        // 被移除的配置必须上报为不满足
        (oldConfigs.keys - newConfigs.keys).forEach {
            callback(it, "nfc", false)
        }

        synchronized(lock) {
            if (newConfigs.isNotEmpty() && !isReceiverRegistered && !isReleased) {
                try {
                    context.registerReceiver(
                        receiver,
                        IntentFilter(Protocol.ACTION_NFC_TAG),
                        null,
                        handler,
                        Context.RECEIVER_EXPORTED
                    )
                    isReceiverRegistered = true
                    HyperLog.d(TAG, "NFC receiver registered")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register NFC receiver: ${e.message}")
                }
            } else if (newConfigs.isEmpty() && isReceiverRegistered) {
                try {
                    context.unregisterReceiver(receiver)
                } catch (_: Exception) {
                }
                isReceiverRegistered = false
            }
        }
    }

    /** 初始检查：NFC 为事件驱动，默认全部未激活。 */
    fun check() {
        // 无操作：扫描事件到达前状态均为 false
    }

    fun release() {
        synchronized(lock) {
            if (isReleased) return
            isReleased = true
            if (isReceiverRegistered) {
                try {
                    context.unregisterReceiver(receiver)
                } catch (_: Exception) {
                }
                isReceiverRegistered = false
            }
        }
    }

    private companion object {
        const val TAG = "NfcTriggerManager"
    }
}
