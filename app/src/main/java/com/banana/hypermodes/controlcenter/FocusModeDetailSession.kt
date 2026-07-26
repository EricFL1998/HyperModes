package com.banana.hypermodes.controlcenter

import android.content.Context
import android.content.Intent
import android.view.View
import android.view.ViewGroup
import com.banana.hypermodes.protocol.Protocol
import java.lang.ref.WeakReference
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

enum class DetailLifecycleState {
    CLOSED,
    OPEN,
    CLOSING
}

class FocusModeDetailSession(
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val nativeDetailContentApi: FocusNativeDetailContentApi?,
    private val diagnostic: FocusDetailDiagnostic,
    private val detailAdapterInterface: Class<*>? = null
) {
    @Volatile
    var state: DetailLifecycleState = DetailLifecycleState.CLOSED
        private set

    val adapter: Any = createAdapterProxy()

    private val lock = Any()
    private var currentContent: WeakReference<Any>? = null
    @Volatile
    private var pendingCardRefresh = false

    private fun createAdapterProxy(): Any {
        val adapterClass = detailAdapterInterface
            ?: nativeDetailContentApi?.contentClass?.classLoader?.let { classLoader ->
                try {
                    classLoader.loadClass("com.android.systemui.plugins.qs.DetailAdapter")
                } catch (_: Throwable) {
                    null
                }
            }
            ?: return Any() // Fallback if no interface available

        return Proxy.newProxyInstance(
            adapterClass.classLoader,
            arrayOf(adapterClass),
            DetailAdapterHandler()
        )
    }

    private inner class DetailAdapterHandler : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            return when (method.name) {
                "getMetricsCategory" -> FocusNativeDetailRegistry.METRICS_CATEGORY
                "getTitle" -> "Focus Mode"
                "createDetailView" -> {
                    val arguments = args ?: emptyArray()
                    bindDetailView(
                        context = arguments.getOrNull(0) as? Context ?: return null,
                        convertView = arguments.getOrNull(1) as? View,
                        parent = arguments.getOrNull(2) as? ViewGroup
                    )
                }
                "getSettingsIntent" -> createSettingsIntent()
                "getToggleVisible" -> false
                "getToggleState" -> null
                "setToggleState" -> null
                "getToggleEnabled" -> false
                "shouldAnimate" -> true
                "hasHeader" -> false
                "getContainerHeight" -> -1
                "equals" -> proxy === (args?.firstOrNull())
                "hashCode" -> System.identityHashCode(proxy)
                "toString" -> "FocusModeDetailSessionAdapter@${Integer.toHexString(System.identityHashCode(proxy))}"
                else -> defaultReturnValue(method.returnType)
            }
        }

        private fun createSettingsIntent(): Intent {
            return Intent().apply {
                setClassName(Protocol.MODULE_PACKAGE, "com.banana.hypermodes.ui.MainActivity")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }
    }

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

    fun bindDetailView(
        context: Context,
        convertView: View?,
        parent: ViewGroup?
    ): View? {
        val api = nativeDetailContentApi ?: return null

        val content = api.convertOrInflate.invoke(context, convertView, parent) ?: return null
        if (content !is View) return null

        FocusNativeDetailRegistry.registerContent(content, this)
        currentContent = WeakReference(content)

        api.setSuffix.invoke(content, FocusNativeDetailRegistry.CONTENT_SUFFIX)

        return content
    }

    fun refreshItems() {
        synchronized(lock) {
            if (state != DetailLifecycleState.OPEN) return
            val content = currentContent?.get() ?: return
            val api = nativeDetailContentApi ?: return

            val snapshot = repository.loadOrInitialize()
            val rows = buildRows(snapshot, api)

            // Create properly typed array for reflection invoke
            val itemArrayType = api.setItems.parameterTypes[0].componentType ?: return
            val itemArray = java.lang.reflect.Array.newInstance(itemArrayType, rows.size)
            rows.forEachIndexed { index, row ->
                java.lang.reflect.Array.set(itemArray, index, row)
            }

            api.setItems.invoke(content, itemArray)
        }
    }

    fun hasPendingCardRefresh(): Boolean {
        synchronized(lock) {
            return pendingCardRefresh
        }
    }

    fun clearPendingCardRefresh() {
        synchronized(lock) {
            pendingCardRefresh = false
        }
    }

    private fun buildRows(snapshot: FocusCardSnapshot, api: FocusNativeDetailContentApi): Array<Any> {
        return snapshot.modes.map { mode ->
            buildSelectableItem(api, mode, snapshot.activeModeId)
        }.toTypedArray()
    }

    private fun buildSelectableItem(api: FocusNativeDetailContentApi, mode: com.banana.hypermodes.systemserver.config.ModeConfig, activeModeId: String?): Any {
        val constructor = api.selectableItemConstructor
        val item = when {
            constructor.parameterTypes.isEmpty() -> constructor.newInstance()
            else -> constructor.newInstance(null)
        }

        val selected = activeModeId == mode.id
        setField(item, "tag", mode.id)
        setField(item, "title", mode.name.ifBlank { "Focus mode" })
        setField(item, "summary", if (selected) "On" else "Off")
        setField(item, "selected", selected)
        setField(item, "selectable", true)

        return item
    }

    private fun setField(instance: Any, name: String, value: Any?) {
        try {
            val field = findField(instance.javaClass, name)
            field.isAccessible = true
            when (field.type) {
                Boolean::class.javaPrimitiveType -> field.setBoolean(instance, value as? Boolean ?: false)
                Int::class.javaPrimitiveType -> field.setInt(instance, value as? Int ?: 0)
                else -> field.set(instance, value)
            }
        } catch (_: Throwable) {
            // Ignore field not found
        }
    }

    private fun findField(clazz: Class<*>, name: String): java.lang.reflect.Field {
        var current: Class<*>? = clazz
        while (current != null) {
            try {
                return current.getDeclaredField(name)
            } catch (_: NoSuchFieldException) {
                current = current.superclass
            }
        }
        throw NoSuchFieldException("${clazz.name}.$name")
    }

    private fun defaultReturnValue(type: Class<*>): Any? = when (type) {
        Void.TYPE -> null
        Boolean::class.javaPrimitiveType -> false
        Byte::class.javaPrimitiveType -> 0.toByte()
        Short::class.javaPrimitiveType -> 0.toShort()
        Int::class.javaPrimitiveType -> 0
        Long::class.javaPrimitiveType -> 0L
        Float::class.javaPrimitiveType -> 0f
        Double::class.javaPrimitiveType -> 0.0
        Char::class.javaPrimitiveType -> ' '
        else -> null
    }
}
