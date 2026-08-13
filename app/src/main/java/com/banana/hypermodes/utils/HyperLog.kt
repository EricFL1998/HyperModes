package com.banana.hypermodes.utils

import android.util.Log
import com.banana.hypermodes.BuildConfig

/**
 * 统一的日志门控。
 *
 * 本模块会被加载到 system_server / SystemUI / DeskClock 等多个进程，
 * 热路径上的调试日志既污染 logcat 又拖慢执行。因此 v/d/i 级别只在
 * debug 构建（[BuildConfig.DEBUG]）下输出；w/e 属于告警与错误，始终输出。
 */
object HyperLog {
    fun v(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.v(tag, msg)
    }

    fun d(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.d(tag, msg)
    }

    fun i(tag: String, msg: String) {
        if (BuildConfig.DEBUG) Log.i(tag, msg)
    }

    fun w(tag: String, msg: String) {
        Log.w(tag, msg)
    }

    fun w(tag: String, msg: String, tr: Throwable) {
        Log.w(tag, msg, tr)
    }

    fun e(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    fun e(tag: String, msg: String, tr: Throwable) {
        Log.e(tag, msg, tr)
    }
}
