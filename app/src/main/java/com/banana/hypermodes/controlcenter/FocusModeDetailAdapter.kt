package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import com.banana.hypermodes.systemserver.config.ModeConfig

private const val TAG = "FocusModeDetailAdapter"

class FocusModeDetailAdapter(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val detailAdapterInterface: Class<*>,
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val onStateRefresh: () -> Unit = {},
    nativeDetailContentApi: FocusNativeDetailContentApi?,
    modeIconProvider: ((ModeConfig) -> Drawable)? = null,
    modeDisplayNameProvider: ((ModeConfig) -> String)? = null
) {
    private val iconResolver = FocusModeIconResolver(pluginContext, moduleContext)
    private val displayNameResolver = runCatching {
        FocusModeDisplayNameResolver(
            moduleContext.resources,
            moduleContext.packageName
        )
    }.getOrNull()
    private val resolvedModeIconProvider = modeIconProvider ?: iconResolver::resolve
    private val resolvedModeDisplayNameProvider = modeDisplayNameProvider
        ?: (displayNameResolver?.let { resolver -> { mode: ModeConfig -> resolver.resolve(mode) } }
            ?: { mode: ModeConfig -> mode.name })

    val session = FocusModeDetailSession(
        repository = repository,
        onDismiss = onDismiss,
        nativeDetailContentApi = nativeDetailContentApi,
        diagnostic = object : FocusDetailDiagnostic {
            override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {
                Log.w(TAG, "Detail fallback: $stage", throwable)
            }
        },
        detailAdapterInterface = detailAdapterInterface,
        onStateRefresh = onStateRefresh,
        modeIconProvider = resolvedModeIconProvider,
        modeDisplayNameProvider = resolvedModeDisplayNameProvider
    )

    init {
        FocusNativeDetailRegistry.registerSession(session.adapter, session)
    }

    val adapter: Any get() = session.adapter

    fun setDetailListening(listening: Boolean) {
        session.setDetailListening(listening)
    }

    fun onPanelHidden() {
        session.onPanelHidden()
    }

    fun refreshItems() {
        session.refreshItems()
    }

    fun destroy() {
        session.destroy()
    }
}
