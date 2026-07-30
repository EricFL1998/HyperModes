package com.banana.hypermodes.systemserver.trigger

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.HandlerThread

class AppTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    private var configs: Map<String, List<String>> = emptyMap()

    // Dedicated thread: this runs inside system_server, so polling on the main
    // looper would add a 2-second chore to the thread that serves the framework.
    private val thread = HandlerThread("AppTriggerManager").apply { start() }
    private val handler = Handler(thread.looper)

    private val checkRunnable = object : Runnable {
        override fun run() {
            check()
            handler.postDelayed(this, 2000) // Poll every 2 seconds
        }
    }

    fun updateConfigs(newConfigs: Map<String, List<String>>) {
        // Report modes that dropped out of the config as inactive so their
        // stale trigger tag doesn't pin the mode on forever.
        (configs.keys - newConfigs.keys).forEach { callback(it, "app", false) }
        configs = newConfigs
        if (configs.isNotEmpty()) {
            handler.removeCallbacks(checkRunnable)
            handler.post(checkRunnable)
        } else {
            handler.removeCallbacks(checkRunnable)
        }
    }

    fun check() {
        val foregroundPackage = getForegroundPackage()
        configs.forEach { (modeId, packageNames) ->
            val isActive = packageNames.any { it == foregroundPackage }
            callback(modeId, "app", isActive)
        }
    }

    private fun getForegroundPackage(): String? {
        @Suppress("DEPRECATION")
        val tasks = activityManager.getRunningTasks(1)
        return tasks?.getOrNull(0)?.topActivity?.packageName
    }
}
