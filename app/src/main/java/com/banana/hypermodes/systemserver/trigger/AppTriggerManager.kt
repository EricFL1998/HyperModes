package com.banana.hypermodes.systemserver.trigger

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper

class AppTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    @Volatile private var configs: Map<String, List<String>> = emptyMap()

    // Dedicated thread: this runs inside system_server, so polling on the main
    // looper would add a 2-second chore to the thread that serves the framework.
    private val thread = HandlerThread("AppTriggerManager").apply { start() }
    private val handler = Handler(thread.looper)
    private val mainHandler = Handler(Looper.getMainLooper())

    private val checkRunnable = object : Runnable {
        override fun run() {
            try {
                check()
            } catch (t: Throwable) {
                // An uncaught throwable on this thread would crash system_server.
            } finally {
                handler.postDelayed(this, POLL_INTERVAL_MS)
            }
        }
    }

    fun updateConfigs(newConfigs: Map<String, List<String>>) {
        // Report modes that dropped out of the config as inactive so their
        // stale trigger tag doesn't pin the mode on forever.
        (configs.keys - newConfigs.keys).forEach { callback(it, "app", false) }
        configs = newConfigs
        if (configs.isNotEmpty()) {
            handler.removeCallbacks(checkRunnable)
            handler.postDelayed(checkRunnable, POLL_INTERVAL_MS)
        } else {
            handler.removeCallbacks(checkRunnable)
        }
    }

    fun check() {
        val foregroundPackage = getForegroundPackage()
        val snapshot = configs
        mainHandler.post {
            snapshot.forEach { (modeId, packageNames) ->
                val isActive = packageNames.any { it == foregroundPackage }
                callback(modeId, "app", isActive)
            }
        }
    }

    /** Stop polling and quit the thread. Called on engine shutdown. */
    fun release() {
        handler.removeCallbacksAndMessages(null)
        thread.quitSafely()
    }

    private fun getForegroundPackage(): String? {
        @Suppress("DEPRECATION")
        val tasks = activityManager.getRunningTasks(1)
        return tasks?.getOrNull(0)?.topActivity?.packageName
    }

    companion object {
        private const val POLL_INTERVAL_MS = 2000L
    }
}
