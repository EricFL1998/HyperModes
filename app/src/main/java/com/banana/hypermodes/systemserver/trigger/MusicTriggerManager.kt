package com.banana.hypermodes.systemserver.trigger

import android.content.Context
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper

class MusicTriggerManager(
    private val context: Context,
    private val callback: (String, String, Boolean) -> Unit
) {
    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    private var modeIds: Set<String> = emptySet()
    private val handler = Handler(Looper.getMainLooper())

    private val sessionListener = MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
        updateControllers(controllers)
        check()
    }

    private val controllerCallback = object : MediaController.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackState?) {
            check()
        }
    }

    private var currentControllers: List<MediaController> = emptyList()
    private var isListening = false

    fun updateConfigs(newModeIds: Set<String>) {
        // Report modes that dropped out of the config as inactive so their
        // stale trigger tag doesn't pin the mode on forever.
        (modeIds - newModeIds).forEach { callback(it, "music", false) }
        modeIds = newModeIds
        if (modeIds.isNotEmpty() && !isListening) {
            try {
                // In system_server, null component name is allowed if we have permission
                mediaSessionManager.addOnActiveSessionsChangedListener(sessionListener, null, handler)
                isListening = true
                val controllers = mediaSessionManager.getActiveSessions(null)
                updateControllers(controllers)
            } catch (e: Exception) {
                // Ignore
            }
        } else if (modeIds.isEmpty() && isListening) {
            try {
                mediaSessionManager.removeOnActiveSessionsChangedListener(sessionListener)
            } catch (e: Exception) {
                // Ignore
            }
            isListening = false
            updateControllers(emptyList())
        }
        check()
    }

    private fun updateControllers(controllers: List<MediaController>?) {
        currentControllers.forEach { it.unregisterCallback(controllerCallback) }
        currentControllers = controllers ?: emptyList()
        currentControllers.forEach { it.registerCallback(controllerCallback, handler) }
    }

    fun check() {
        val isMusicPlaying = currentControllers.any { 
            it.playbackState?.state == PlaybackState.STATE_PLAYING 
        }
        modeIds.forEach { callback(it, "music", isMusicPlaying) }
    }
}
