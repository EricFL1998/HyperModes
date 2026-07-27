package com.banana.hypermodes.systemserver

import android.database.ContentObserver

/**
 * Owns the engine config observer registration so package-removal cleanup
 * can release exactly the observer that was registered, idempotently.
 */
class EngineObserverOwner(
    private val registerAction: (ContentObserver) -> Unit,
    private val unregisterAction: (ContentObserver) -> Unit
) {
    var current: ContentObserver? = null
        private set

    fun register(observer: ContentObserver) {
        if (current === observer) return
        registerAction(observer)
        current = observer
    }

    fun release() {
        val observer = current ?: return
        current = null
        unregisterAction(observer)
    }
}
