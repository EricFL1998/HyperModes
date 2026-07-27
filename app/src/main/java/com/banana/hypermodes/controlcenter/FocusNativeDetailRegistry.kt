package com.banana.hypermodes.controlcenter

import java.lang.ref.WeakReference
import java.util.Collections
import java.util.WeakHashMap

object FocusNativeDetailRegistry {
    const val TILE_SPEC = "hypermodes_focus"
    const val METRICS_CATEGORY = 118
    const val CONTENT_SUFFIX = "HyperModesFocus"

    private val adapterSessions = Collections.synchronizedMap(WeakHashMap<Any, WeakReference<FocusModeDetailSession>>())
    private val contentSessions = Collections.synchronizedMap(WeakHashMap<Any, WeakReference<FocusModeDetailSession>>())

    fun registerSession(adapter: Any, session: FocusModeDetailSession) {
        adapterSessions[adapter] = WeakReference(session)
    }

    fun registerContent(content: Any, session: FocusModeDetailSession) {
        contentSessions[content] = WeakReference(session)
    }

    fun unregisterSession(adapter: Any) {
        adapterSessions.remove(adapter)
    }

    fun unregisterContent(content: Any) {
        contentSessions.remove(content)
    }

    fun isFocusAdapter(adapter: Any): Boolean {
        return adapterSessions[adapter]?.get() != null
    }

    fun isFocusContent(content: Any): Boolean {
        return contentSessions[content]?.get() != null
    }

    fun adapterSession(adapter: Any): FocusModeDetailSession? {
        return adapterSessions[adapter]?.get()
    }

    fun contentSession(content: Any): FocusModeDetailSession? {
        return contentSessions[content]?.get()
    }
}
