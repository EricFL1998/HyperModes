package com.banana.hypermodes.controlcenter

import android.content.Context
import android.util.Log

private const val TAG = "FocusModeDetailAdapter"

class FocusModeDetailAdapter(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val detailAdapterInterface: Class<*>,
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val onStateRefresh: () -> Unit = {},
    nativeDetailContentApi: FocusNativeDetailContentApi?
) {
    val session = FocusModeDetailSession(
        repository = repository,
        onDismiss = onDismiss,
        nativeDetailContentApi = nativeDetailContentApi,
        diagnostic = object : FocusDetailDiagnostic {
            override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {
                Log.w(TAG, "Detail fallback: $stage", throwable)
            }
        },
        detailAdapterInterface = detailAdapterInterface
    )

    init {
        FocusNativeDetailRegistry.registerSession(session.adapter, session)
    }

    val adapter: Any get() = session.adapter

    fun setDetailListening(listening: Boolean) {
        session.setDetailListening(listening)
        if (!listening && session.hasPendingCardRefresh()) {
            onStateRefresh()
            session.clearPendingCardRefresh()
        }
    }

    fun onPanelHidden() {
        session.onPanelHidden()
        if (session.hasPendingCardRefresh()) {
            onStateRefresh()
            session.clearPendingCardRefresh()
        }
    }

    fun refreshItems() {
        session.refreshItems()
    }

    fun destroy() {
        session.destroy()
    }
}
