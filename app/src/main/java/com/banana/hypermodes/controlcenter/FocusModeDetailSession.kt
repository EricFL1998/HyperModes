package com.banana.hypermodes.controlcenter

import java.lang.ref.WeakReference

enum class DetailLifecycleState {
    CLOSED,
    OPEN,
    CLOSING
}

class FocusModeDetailSession(
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val nativeDetailContentApi: FocusNativeDetailContentApi?,
    private val diagnostic: FocusDetailDiagnostic
) {
    @Volatile
    var state: DetailLifecycleState = DetailLifecycleState.CLOSED
        private set

    val adapter: Any = Any() // Placeholder

    private val lock = Any()
    private var currentContent: WeakReference<Any>? = null
    @Volatile
    private var pendingCardRefresh = false

    fun setDetailListening(listening: Boolean) {
        synchronized(lock) {
            when {
                listening && state == DetailLifecycleState.CLOSED -> {
                    state = DetailLifecycleState.OPEN
                }
                !listening && state == DetailLifecycleState.OPEN -> {
                    state = DetailLifecycleState.CLOSING
                    pendingCardRefresh = true
                }
            }
        }
    }

    fun onPanelHidden() {
        synchronized(lock) {
            if (state == DetailLifecycleState.CLOSING) {
                state = DetailLifecycleState.CLOSED
                currentContent = null
                // Pending refresh will be posted by caller
            }
        }
    }

    fun destroy() {
        synchronized(lock) {
            state = DetailLifecycleState.CLOSED
            currentContent = null
            pendingCardRefresh = false
        }
        FocusNativeDetailRegistry.unregisterSession(adapter)
    }
}
