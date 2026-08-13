package com.banana.hypermodes.systemserver.trigger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.utils.HolidayCalendar
import com.banana.hypermodes.utils.HyperLog
import java.util.Calendar

/**
 * 节假日/工作日触发。
 *
 * 条件式触发：按官方调休判定（[HolidayCalendar]，与小米闹钟同源）评估今天是否
 * 节假日/工作日。监听日期变化（跨零点）、时间变化、时区变化时重新评估，配置
 * 加载时也会立即检查。
 */
class HolidayTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val lock = Any()
    private val handler = Handler(Looper.getMainLooper())

    /** triggerKey -> kind（"节假日" / "工作日"） */
    private var configs: Map<String, String> = emptyMap()
    private var isReceiverRegistered = false
    private var isReleased = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            synchronized(lock) {
                if (isReleased) return
            }
            HyperLog.i(TAG, "Date/time changed, re-evaluating holiday triggers")
            check()
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
            old
        }

        // 被移除的配置必须上报为不满足，避免陈旧 true 钉住模式
        (oldConfigs.keys - newConfigs.keys).forEach {
            callback(it, "holiday", false)
        }

        synchronized(lock) {
            if (newConfigs.isNotEmpty() && !isReceiverRegistered && !isReleased) {
                try {
                    context.registerReceiver(
                        receiver,
                        IntentFilter().apply {
                            addAction(Intent.ACTION_DATE_CHANGED)
                            addAction(Intent.ACTION_TIME_CHANGED)
                            addAction(Intent.ACTION_TIMEZONE_CHANGED)
                        },
                        null,
                        handler,
                        Context.RECEIVER_EXPORTED
                    )
                    isReceiverRegistered = true
                    HyperLog.d(TAG, "Holiday receiver registered")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to register holiday receiver: ${e.message}")
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

    /** 立即按当前日期评估所有配置并上报。 */
    fun check() {
        val snapshot = synchronized(lock) { configs }
        val now = Calendar.getInstance()
        val isHoliday = HolidayCalendar.isHoliday(now)
        snapshot.forEach { (triggerKey, kind) ->
            val active = if (kind == "节假日") isHoliday else !isHoliday
            HyperLog.d(TAG, "Holiday check key=$triggerKey kind=$kind active=$active")
            callback(triggerKey, "holiday", active)
        }
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
        const val TAG = "HolidayTriggerManager"
    }
}
