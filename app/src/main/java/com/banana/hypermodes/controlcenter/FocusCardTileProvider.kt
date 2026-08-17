package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Looper
import com.banana.hypermodes.R
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.invoke.MethodHandles
import java.util.IdentityHashMap

private const val TILE_SPEC = "hypermodes_focus"
private const val STATE_UNAVAILABLE = 0
private const val STATE_INACTIVE = 1
private const val STATE_ACTIVE = 2

fun interface FocusCardDetailFactory {
    fun create(onDismiss: () -> Unit, onStateRefresh: () -> Unit): Any?
}

class FocusCardTileProvider(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val classes: FocusCardTileClasses,
    private val repository: FocusCardStateRepository,
    private val observableStore: ObservableFocusCardConfigStore,
    private val detailFactory: FocusCardDetailFactory?,
    private val postToUi: ((() -> Unit) -> Unit),
    private val isMainThread: () -> Boolean = {
        runCatching { Looper.myLooper() == Looper.getMainLooper() }.getOrDefault(false)
    }
) {
    fun create(): Any {
        val handler = TileInvocationHandler(
            pluginContext = pluginContext,
            moduleContext = moduleContext,
            classes = classes,
            repository = repository,
            observableStore = observableStore,
            detailFactory = detailFactory,
            postToUi = postToUi,
            isMainThread = isMainThread
        )
        return Proxy.newProxyInstance(
            classes.tileInterface.classLoader,
            arrayOf(classes.tileInterface),
            handler
        )
    }
}

private class TileInvocationHandler(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val classes: FocusCardTileClasses,
    private val repository: FocusCardStateRepository,
    private val observableStore: ObservableFocusCardConfigStore,
    private val detailFactory: FocusCardDetailFactory?,
    private val postToUi: ((() -> Unit) -> Unit),
    private val isMainThread: () -> Boolean
) : InvocationHandler {
    private val callbacks = mutableListOf<Any>()
    private val listenerTokens = identitySet<Any>()
    private var observerRegistration: AutoCloseable? = null
    private var destroyed = false
    private var tileSpec = TILE_SPEC
    private var currentUserId = 0
    private var currentState: Any? = null
    private var detailAdapter: Any? = null
    private var detailSession: FocusModeDetailSession? = null
    private val iconResolver = FocusModeIconResolver(moduleContext)
    private val displayNameResolver = runCatching {
        FocusModeDisplayNameResolver(
            moduleContext.resources,
            moduleContext.packageName
        )
    }.getOrNull()

    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        val arguments = args ?: emptyArray()
        return when (method.name) {
            "equals" -> proxy === arguments.firstOrNull()
            "hashCode" -> System.identityHashCode(proxy)
            "toString" -> "FocusCardTileProxy@${Integer.toHexString(System.identityHashCode(proxy))}"
            "addCallback" -> {
                arguments.firstOrNull()?.let(::addCallback)
                null
            }
            "removeCallback" -> {
                arguments.firstOrNull()?.let(::removeCallback)
                null
            }
            "removeCallbacks", "removeCallbacksByType" -> {
                callbacks.clear()
                null
            }
            "setListening" -> {
                val token = arguments.getOrNull(0)
                val listening = arguments.getOrNull(1) as? Boolean ?: false
                if (token != null) setListening(token, listening)
                null
            }
            "isListening" -> listenerTokens.isNotEmpty()
            "destroy" -> {
                destroy()
                null
            }
            "isDestroyed" -> destroyed
            "getState" -> getState()
            "refreshState" -> {
                refreshState()
                null
            }
            "getTileLabel" -> tileLabel()
            "getTileSpec" -> tileSpec
            "setTileSpec" -> {
                tileSpec = arguments.firstOrNull() as? String ?: TILE_SPEC
                null
            }
            "click" -> {
                handleClick()
                null
            }
            "secondaryClick", "secondClick" -> null
            "longClick" -> {
                handleLongClick()
                null
            }
            "getDetailAdapter" -> getOrCreateDetailAdapter()
            "setDetailListening" -> {
                val listening = arguments.firstOrNull() as? Boolean ?: false
                setDetailListening(listening)
                null
            }
            "showDetail" -> {
                if (!destroyed) notifyShowDetail(arguments.firstOrNull() as? Boolean ?: false)
                null
            }
            "getCurrentTileUser" -> currentUserId
            "getMetricsCategory" -> FocusNativeDetailRegistry.METRICS_CATEGORY
            "getMetricsSpec" -> tileSpec
            "getInstanceId" -> null
            "populate" -> arguments.firstOrNull()
            "isAvailable" -> repository.loadOrInitialize().displayedMode != null
            "isConnected" -> false
            "isTileReady" -> true
            "userSwitch" -> {
                currentUserId = arguments.firstOrNull() as? Int ?: currentUserId
                refreshState()
                null
            }
            "onDetailPanelHidden" -> {
                handleDetailPanelHidden()
                null
            }
            else -> if (method.isDefault) {
                MethodHandles.privateLookupIn(method.declaringClass, MethodHandles.lookup())
                    .unreflectSpecial(method, method.declaringClass)
                    .bindTo(proxy)
                    .invokeWithArguments(*arguments)
            } else {
                defaultValue(method.returnType)
            }
        }
    }

    private fun addCallback(callback: Any) {
        if (callbacks.none { it === callback }) callbacks += callback
    }

    private fun removeCallback(callback: Any) {
        callbacks.removeAll { it === callback }
    }

    private fun setListening(token: Any, listening: Boolean) {
        if (destroyed) return
        if (listening) {
            val wasEmpty = listenerTokens.isEmpty()
            listenerTokens.add(token)
            updateObserverOwnership()
            // Immediately refresh when starting to listen
            if (wasEmpty) {
                refreshState()
            }
        } else {
            listenerTokens.remove(token)
            updateObserverOwnership()
        }
    }

    private fun updateObserverOwnership() {
        val needsObserver = listenerTokens.isNotEmpty() ||
                            (detailSession?.state == DetailLifecycleState.OPEN)

        if (needsObserver && observerRegistration == null) {
            observerRegistration = observableStore.observe {
                handleConfigChange()
            }
        } else if (!needsObserver && observerRegistration != null) {
            observerRegistration?.close()
            observerRegistration = null
        }
    }

    private fun handleConfigChange() {
        if (destroyed) return
        val session = detailSession
        when (session?.state) {
            DetailLifecycleState.OPEN -> {
                refreshState()
                session.refreshItems()
            }
            DetailLifecycleState.CLOSING -> {
                // Pending refresh will be posted by onPanelHidden
            }
            else -> {
                if (listenerTokens.isNotEmpty()) {
                    refreshState()
                }
            }
        }
    }

    private fun destroy() {
        if (destroyed) return
        destroyed = true
        callbacks.clear()
        listenerTokens.clear()
        observerRegistration?.close()
        observerRegistration = null
        detailSession?.destroy()
        detailAdapter = null
        detailSession = null
    }

    private fun refreshState() {
        if (destroyed) return
        val state = buildState()
        currentState = state
        val targets = callbacks.toList()
        runOnUi {
            if (destroyed) return@runOnUi
            targets.forEach { callback -> invokeCallback(callback, "onStateChanged", state) }
        }
    }

    private fun getState(): Any {
        val state = buildState()
        currentState = state
        return state
    }

    private fun buildState(): Any {
        val state = classes.booleanStateClass.getDeclaredConstructor().newInstance()
        populateState(state)
        return state
    }

    private fun populateState(state: Any) {
        val snapshot = repository.loadOrInitialize()
        val mode = snapshot.displayedMode
        val active = snapshot.isActive && mode != null
        val label = mode?.let { displayNameResolver?.resolve(it) ?: it.name } ?: fallbackLabel()
        setObjectFieldIfPresent(state, "spec", tileSpec)
        setObjectFieldIfPresent(state, "label", label)
        setObjectFieldIfPresent(state, "contentDescription", contentDescription(label, active, mode != null))
        setObjectFieldIfPresent(state, "icon", createIcon(mode?.icon))
        setIntFieldIfPresent(
            state,
            "state",
            when {
                mode == null -> STATE_UNAVAILABLE
                active -> STATE_ACTIVE
                else -> STATE_INACTIVE
            }
        )
        setBooleanFieldIfPresent(state, "value", active)
        setBooleanFieldIfPresent(state, "dualTarget", false)
        setBooleanFieldIfPresent(state, "handlesLongClick", true)
        setBooleanFieldIfPresent(state, "handlesSecondaryClick", false)
    }

    private fun tileLabel(): CharSequence {
        return repository.loadOrInitialize().displayedMode?.let {
            displayNameResolver?.resolve(it) ?: it.name
        } ?: fallbackLabel()
    }

    private fun stringFromContext(id: Int, fallback: String): String {
        return try {
            moduleContext.resources?.getString(id) ?: fallback
        } catch (_: Throwable) {
            try {
                pluginContext.resources?.getString(id) ?: fallback
            } catch (_: Throwable) {
                fallback
            }
        }
    }

    private fun fallbackLabel(): CharSequence = appLabel(pluginContext) ?: appLabel(moduleContext) ?: "HyperModes"

    private fun appLabel(context: Context): CharSequence? {
        return try {
            context.getString(R.string.app_name)
        } catch (_: Throwable) {
            null
        }
    }

    private fun contentDescription(label: CharSequence, active: Boolean, available: Boolean): CharSequence {
        return when {
            !available -> label
            active -> "$label on"
            else -> "$label off"
        }
    }

    private fun createIcon(modeIcon: String?): Any? {
        val drawable = iconResolver.resolve(modeIcon)
        return try {
            val constructor = classes.drawableIconClass.getDeclaredConstructor(Drawable::class.java)
            constructor.isAccessible = true
            constructor.newInstance(drawable)
        } catch (_: Throwable) {
            null
        }
    }

    private fun handleClick() {
        if (destroyed) return
        val snapshot = repository.loadOrInitialize()
        when {
            snapshot.displayedMode == null -> Unit
            snapshot.isActive -> repository.deactivate()
            else -> repository.activate(snapshot.displayedMode.id)
        }
        refreshState()
    }

    private fun handleLongClick() {
        if (destroyed) return
        if (getOrCreateDetailAdapter() != null) notifyShowDetail(true)
    }

    private fun getOrCreateDetailAdapter(): Any? {
        val existing = detailAdapter
        if (existing != null) return existing
        val created = detailFactory?.create(
            onDismiss = { notifyShowDetail(false) },
            onStateRefresh = { refreshState() }
        )
        detailAdapter = created
        // Extract session if this is a FocusModeDetailAdapter
        if (created != null) {
            detailSession = FocusNativeDetailRegistry.adapterSession(created)
        }
        return created
    }

    private fun setDetailListening(listening: Boolean) {
        if (destroyed) return
        val session = detailSession
        if (session != null) {
            session.setDetailListening(listening)
            updateObserverOwnership()
        }
    }

    private fun notifyShowDetail(show: Boolean) {
        if (destroyed) return
        val targets = callbacks.toList()
        runOnUi {
            if (destroyed) return@runOnUi
            targets.forEach { callback -> invokeCallback(callback, "onShowDetail", show) }
        }
    }

    private fun handleDetailPanelHidden() {
        if (destroyed) return
        runOnUi {
            if (!destroyed) {
                refreshState()
            }
        }
    }

    private fun runOnUi(action: () -> Unit) {
        if (isMainThread()) {
            action()
        } else {
            postToUi(action)
        }
    }

    private fun invokeCallback(callback: Any, name: String, argument: Any) {
        val method = callback.javaClass.methods.firstOrNull {
            it.name == name && it.parameterTypes.size == 1 && parameterAccepts(it.parameterTypes[0], argument)
        } ?: callback.javaClass.declaredMethods.firstOrNull {
            it.name == name && it.parameterTypes.size == 1 && parameterAccepts(it.parameterTypes[0], argument)
        } ?: return
        method.isAccessible = true
        method.invoke(callback, argument)
    }

    private fun parameterAccepts(parameter: Class<*>, argument: Any): Boolean {
        if (parameter.isPrimitive) {
            return parameter == Boolean::class.javaPrimitiveType && argument is Boolean
        }
        return parameter.isAssignableFrom(argument.javaClass)
    }

    private fun setObjectFieldIfPresent(instance: Any, name: String, value: Any?) {
        runCatching {
            val field = findField(instance.javaClass, name)
            field.isAccessible = true
            field.set(instance, value)
        }
    }

    private fun setIntFieldIfPresent(instance: Any, name: String, value: Int) {
        runCatching {
            val field = findField(instance.javaClass, name)
            field.isAccessible = true
            field.setInt(instance, value)
        }
    }

    private fun setBooleanFieldIfPresent(instance: Any, name: String, value: Boolean) {
        runCatching {
            val field = findField(instance.javaClass, name)
            field.isAccessible = true
            field.setBoolean(instance, value)
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

    private fun <T : Any> identitySet(): MutableSet<T> = java.util.Collections.newSetFromMap(IdentityHashMap<T, Boolean>())

    private fun defaultValue(type: Class<*>): Any? = when (type) {
        Void.TYPE -> null
        Boolean::class.javaPrimitiveType -> false
        Byte::class.javaPrimitiveType -> 0.toByte()
        Short::class.javaPrimitiveType -> 0.toShort()
        Int::class.javaPrimitiveType -> 0
        Long::class.javaPrimitiveType -> 0L
        Float::class.javaPrimitiveType -> 0f
        Double::class.javaPrimitiveType -> 0.0
        Char::class.javaPrimitiveType -> ' '
        else -> null
    }
}
