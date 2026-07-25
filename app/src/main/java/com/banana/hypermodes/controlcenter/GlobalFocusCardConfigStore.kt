package com.banana.hypermodes.controlcenter

import android.content.Context
import android.database.ContentObserver
import android.os.Handler
import android.os.Looper
import android.provider.Settings

private const val CONFIG_KEY = "pixel_routines_full_config"

class GlobalFocusCardConfigStore(
    context: Context,
    private val handler: Handler = Handler(Looper.getMainLooper())
) : ObservableFocusCardConfigStore {
    private val context: Context = context.applicationContext ?: context

    override fun read(): String? =
        Settings.Global.getString(context.contentResolver, CONFIG_KEY)

    override fun write(json: String): Boolean =
        Settings.Global.putString(context.contentResolver, CONFIG_KEY, json)

    override fun observe(onChanged: () -> Unit): AutoCloseable {
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                onChanged()
            }
        }
        context.contentResolver.registerContentObserver(
            Settings.Global.getUriFor(CONFIG_KEY),
            false,
            observer
        )
        return AutoCloseableRegistration {
            context.contentResolver.unregisterContentObserver(observer)
        }
    }

    private class AutoCloseableRegistration(
        private val onClose: () -> Unit
    ) : AutoCloseable {
        private var closed = false

        override fun close() {
            if (closed) return
            closed = true
            onClose()
        }
    }
}
