package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import com.banana.hypermodes.systemserver.config.ModeConfig
import java.lang.ref.WeakReference
import java.lang.invoke.MethodHandles
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
    private val detailAdapterInterface: Class<*>? = null,
    private val onStateRefresh: () -> Unit = {},
    private val modeIconProvider: (ModeConfig) -> Drawable = { ColorDrawable(android.graphics.Color.TRANSPARENT) },
    private val modeDisplayNameProvider: (ModeConfig) -> String = { mode -> mode.name.ifBlank { "Focus mode" } }
) {
    @Volatile
    var state: DetailLifecycleState = DetailLifecycleState.CLOSED
        private set

    val adapter: Any = createAdapterProxy()

    private val lock = Any()
    private var currentContent: WeakReference<Any>? = null
    @Volatile
    private var pendingCardRefresh = false
    @Volatile
    private var closeRequested = false

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
                "getSettingsIntent" -> null
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
                else -> if (method.isDefault) {
                    MethodHandles.privateLookupIn(method.declaringClass, MethodHandles.lookup())
                        .unreflectSpecial(method, method.declaringClass)
                        .bindTo(proxy)
                        .invokeWithArguments(*(args ?: emptyArray()))
                } else {
                    defaultReturnValue(method.returnType)
                }
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
        val shouldRefresh = synchronized(lock) {
            releaseCurrentContentLocked()
            if (state != DetailLifecycleState.CLOSING) {
                false
            } else {
                state = DetailLifecycleState.CLOSED
                val pending = pendingCardRefresh
                pendingCardRefresh = false
                pending
            }
        }
        if (shouldRefresh) onStateRefresh()
    }

    fun destroy() {
        synchronized(lock) {
            state = DetailLifecycleState.CLOSED
            releaseCurrentContentLocked()
            pendingCardRefresh = false
        }
        FocusNativeDetailRegistry.unregisterSession(adapter)
    }

    private fun registerCurrentContent(content: Any) {
        synchronized(lock) {
            releaseCurrentContentLocked()
            FocusNativeDetailRegistry.registerContent(content, this)
            currentContent = WeakReference(content)
        }
    }

    private fun releaseCurrentContentIfSame(content: Any) {
        synchronized(lock) {
            if (currentContent?.get() === content) releaseCurrentContentLocked()
        }
    }

    private fun releaseCurrentContentLocked() {
        val content = currentContent?.get()
        currentContent = null
        if (content != null) {
            runCatching { FocusNativeDetailRegistry.unregisterContent(content) }
        }
    }

    fun bindDetailView(
        context: Context,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val api = nativeDetailContentApi
        if (api == null) {
            failNativeDetail(
                FocusDetailFallbackStage.NATIVE_API_UNAVAILABLE,
                IllegalStateException("OS4 QSDetailContent API is unavailable")
            )
        }

        val content = try {
            api.convertOrInflate.invoke(context, convertView, parent)
        } catch (throwable: Throwable) {
            failNativeDetail(FocusDetailFallbackStage.NATIVE_CONVERT, unwrapReflectionFailure(throwable))
        }
        if (content == null || !api.contentClass.isInstance(content) || content !is View) {
            failNativeDetail(
                FocusDetailFallbackStage.NATIVE_CONVERT,
                IllegalStateException("OS4 QSDetailContent.convertOrInflate returned an incompatible value")
            )
        }

        FocusNativeDetailViewDecorator.decorate(content)
        registerCurrentContent(content)
        closeRequested = false

        try {
            api.setSuffix.invoke(content, FocusNativeDetailRegistry.CONTENT_SUFFIX)
            submitItems(content, api)
        } catch (throwable: Throwable) {
            releaseCurrentContentIfSame(content)
            failNativeDetail(FocusDetailFallbackStage.NATIVE_ITEMS, unwrapReflectionFailure(throwable))
        }

        try {
            api.setCallback.invoke(content, createNativeCallback(api))
        } catch (throwable: Throwable) {
            releaseCurrentContentIfSame(content)
            failNativeDetail(FocusDetailFallbackStage.NATIVE_CALLBACK, unwrapReflectionFailure(throwable))
        }

        return content
    }

    private fun failNativeDetail(stage: FocusDetailFallbackStage, throwable: Throwable): Nothing {
        diagnostic.failed(stage, throwable)
        throw IllegalStateException("OS4 native detail failed at $stage", throwable)
    }

    private fun unwrapReflectionFailure(throwable: Throwable): Throwable {
        return (throwable as? java.lang.reflect.InvocationTargetException)?.targetException ?: throwable
    }

    private fun submitItems(content: Any, api: FocusNativeDetailContentApi) {
        val snapshot = repository.loadOrInitialize()
        val rows = buildRows(content, snapshot, api)
        val itemArrayType = api.setItems.parameterTypes[0].componentType
            ?: throw NoSuchMethodException("${api.contentClass.name}.setItems array component")
        val itemArray = java.lang.reflect.Array.newInstance(itemArrayType, rows.size)
        rows.forEachIndexed { index, row ->
            java.lang.reflect.Array.set(itemArray, index, row)
        }
        api.setItems.invoke(content, itemArray)
    }

    private fun createNativeCallback(api: FocusNativeDetailContentApi): Any {
        return Proxy.newProxyInstance(
            api.callbackInterface.classLoader,
            arrayOf(api.callbackInterface),
            InvocationHandler { proxy, method, args ->
                when (method.name) {
                    "equals" -> proxy === args?.firstOrNull()
                    "hashCode" -> System.identityHashCode(proxy)
                    "toString" -> "HyperModesFocusDetailCallback@${Integer.toHexString(System.identityHashCode(proxy))}"
                    "onDetailItemClick" -> {
                        val item = args?.firstOrNull()
                        val modeId = item?.let(::readItemTag) as? String
                        if (modeId != null) selectMode(modeId)
                        null
                    }
                    "onDetailItemDisconnect" -> null
                    else -> defaultReturnValue(method.returnType)
                }
            }
        )
    }

    private fun readItemTag(item: Any): Any? {
        return runCatching {
            item.javaClass.methods.firstOrNull { it.name == "getTag" && it.parameterTypes.isEmpty() }
                ?.invoke(item)
                ?: findField(item.javaClass, "tag").apply { isAccessible = true }.get(item)
        }.getOrNull()
    }

    private fun selectMode(modeId: String) {
        synchronized(lock) {
            if (closeRequested) return
            closeRequested = true
            pendingCardRefresh = true
            if (state == DetailLifecycleState.OPEN) state = DetailLifecycleState.CLOSING
        }
        runCatching { repository.activate(modeId) }
        onDismiss()
    }

    fun refreshItems() {
        synchronized(lock) {
            if (state != DetailLifecycleState.OPEN) return
            val content = currentContent?.get() ?: return
            val api = nativeDetailContentApi ?: return
            runCatching { submitItems(content, api) }
                .onFailure { diagnostic.failed(FocusDetailFallbackStage.NATIVE_ITEMS, unwrapReflectionFailure(it)) }
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

    private fun buildRows(
        content: Any,
        snapshot: FocusCardSnapshot,
        api: FocusNativeDetailContentApi
    ): Array<Any> {
        return snapshot.modes.map { mode ->
            buildSelectableItem(content, api, mode, snapshot.activeModeId)
        }.toTypedArray()
    }

    private fun buildSelectableItem(
        content: Any,
        api: FocusNativeDetailContentApi,
        mode: ModeConfig,
        activeModeId: String?
    ): Any {
        val constructor = api.selectableItemConstructor
        val item = when {
            constructor.parameterTypes.isEmpty() -> constructor.newInstance()
            constructor.parameterTypes.size == 1 &&
                constructor.parameterTypes[0].isAssignableFrom(content.javaClass) -> constructor.newInstance(content)
            else -> throw NoSuchMethodException(
                "${api.selectableItemClass.name} constructor cannot receive ${content.javaClass.name}"
            )
        }

        setField(item, "tag", mode.id)
        setField(item, "title", modeDisplayNameProvider(mode))
        setField(item, "selected", false)
        setField(item, "isForceSingle", false)
        setField(item, "selectable", true)
        setField(item, "iconDrawable", modeIconProvider(mode))

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
