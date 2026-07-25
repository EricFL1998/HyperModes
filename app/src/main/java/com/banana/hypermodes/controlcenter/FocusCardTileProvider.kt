package com.banana.hypermodes.controlcenter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Looper
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ModeIconMapper
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
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
    private var observer: AutoCloseable? = null
    private var destroyed = false
    private var tileSpec = TILE_SPEC
    private var currentState: Any? = null
    private var detailAdapter: Any? = null

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
            "setDetailListening" -> null
            "showDetail" -> {
                if (!destroyed) notifyShowDetail(arguments.firstOrNull() as? Boolean ?: false)
                null
            }
            "getCurrentTileUser" -> 0
            "getMetricsCategory" -> 0
            "getMetricsSpec" -> tileSpec
            "getInstanceId" -> null
            "populate" -> arguments.firstOrNull()
            "isAvailable" -> repository.loadOrInitialize().displayedMode != null
            "isConnected" -> false
            "isTileReady" -> true
            "userSwitch" -> {
                refreshState()
                null
            }
            else -> defaultValue(method.returnType)
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
            if (wasEmpty && listenerTokens.isNotEmpty()) {
                observer = observableStore.observe { refreshState() }
            }
        } else {
            listenerTokens.remove(token)
            if (listenerTokens.isEmpty()) closeObserver()
        }
    }

    private fun destroy() {
        if (destroyed) return
        destroyed = true
        callbacks.clear()
        listenerTokens.clear()
        closeObserver()
        detailAdapter = null
    }

    private fun closeObserver() {
        observer?.close()
        observer = null
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
        val label = mode?.name ?: fallbackLabel()
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
        return repository.loadOrInitialize().displayedMode?.name ?: fallbackLabel()
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
        val drawable = loadDrawable(modeIcon)
        return try {
            val constructor = classes.drawableIconClass.getDeclaredConstructor(Drawable::class.java)
            constructor.isAccessible = true
            constructor.newInstance(drawable)
        } catch (_: Throwable) {
            null
        }
    }

    private fun loadDrawable(modeIcon: String?): Drawable {
        val iconName = try {
            ModeIconMapper.getStatusBarIcon(modeIcon ?: "")
        } catch (_: Throwable) {
            "ic_stat_zen"
        }
        val mappedResId = drawableId(iconName)
        return drawableFromContexts(mappedResId)
            ?: drawableFromContexts(R.drawable.ic_stat_zen)
            ?: drawableFromContexts(android.R.drawable.ic_dialog_info)
            ?: ColorDrawable(Color.TRANSPARENT)
    }

    private fun drawableFromContexts(resId: Int): Drawable? {
        if (resId == 0) return null
        return try {
            moduleContext.getDrawable(resId)
        } catch (_: Throwable) {
            try {
                pluginContext.getDrawable(resId)
            } catch (_: Throwable) {
                null
            }
        }
    }

    private fun drawableId(name: String): Int {
        return try {
            val packageName = moduleContext.packageName ?: pluginContext.packageName
            moduleContext.resources?.getIdentifier(name, "drawable", packageName) ?: 0
        } catch (_: Throwable) {
            0
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
        return created
    }

    private fun notifyShowDetail(show: Boolean) {
        if (destroyed) return
        val targets = callbacks.toList()
        runOnUi {
            if (destroyed) return@runOnUi
            targets.forEach { callback -> invokeCallback(callback, "onShowDetail", show) }
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
