package com.banana.hypermodes.hook

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.View
import com.banana.hypermodes.controlcenter.FocusCardDetailFactory
import com.banana.hypermodes.controlcenter.FocusCardStateRepository
import com.banana.hypermodes.controlcenter.FocusCardTileClasses
import com.banana.hypermodes.controlcenter.FocusCardTileProvider
import com.banana.hypermodes.controlcenter.FocusModeDetailAdapter
import com.banana.hypermodes.controlcenter.FocusModeDetailSession
import com.banana.hypermodes.controlcenter.FocusNativeDetailPolicy
import com.banana.hypermodes.controlcenter.FocusNativeDetailRegistry
import com.banana.hypermodes.controlcenter.FocusNativeRowVisualCleaner
import com.banana.hypermodes.controlcenter.GlobalFocusCardConfigStore
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.util.Collections
import java.util.IdentityHashMap
import kotlin.random.Random

/** Control Center integration for the OS4 SystemUI tile pipeline. */
class ControlCenterCardHook(private val module: XposedModule) {

    fun install(systemUiClassLoader: ClassLoader) {
        if (!markInstalling(systemUiClassLoader)) return
        try {
            val factoryClass = load(systemUiClassLoader, MIUI_QS_FACTORY)
            val createTile = resolveCreateTileMethod(factoryClass)
            val classes = FocusCardTileClasses.resolve(systemUiClassLoader)
            val nativeDetailFeatures = validatedNativeDetailFeatureSet(systemUiClassLoader)
                ?: throw IllegalStateException("OS4 native detail feature set is incomplete")
            hookCreateTile(createTile, systemUiClassLoader, classes)
            installNativeDetailHooks(nativeDetailFeatures, systemUiClassLoader)
            logMsg("OS4 Control Center hooks installed on ${factoryClass.name}")
        } catch (t: Throwable) {
            unmarkInstalling(systemUiClassLoader)
            logMsg("OS4 Control Center hook installation failed", t)
        }
    }

    private fun hookCreateTile(method: Method, classLoader: ClassLoader, classes: FocusCardTileClasses) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val spec = chain.args.firstOrNull() as? String
                    runCatching { ensureFocusTilePersisted(chain.thisObject, classLoader) }
                        .onFailure { logPersistFailureOnce(it) }
                    if (spec != FOCUS_CARD_SPEC) return chain.proceed()
                    return try {
                        createFocusTile(chain.thisObject, classes)
                    } catch (t: Throwable) {
                        logMsg("OS4 Focus tile creation failed", t)
                        null
                    }
                }
            })
    }

    private fun ensureFocusTilePersisted(factory: Any?, classLoader: ClassLoader) {
        val owner = factory ?: return
        val userId = currentUserId()
        val shouldPersist = synchronized(persistLock) {
            if (persistedFactories[owner]?.contains(userId) == true ||
                persistingFactories[owner]?.contains(userId) == true
            ) {
                false
            } else {
                persistingFactories.getOrPut(owner) { mutableSetOf() }.add(userId)
                true
            }
        }
        if (!shouldPersist) return
        var persisted = false
        try {
            val host = resolveOs4Host(owner)
            val interactorField = findField(host.javaClass, "interactor")
                ?: throw NoSuchFieldException("${host.javaClass.name}.interactor")
            interactorField.isAccessible = true
            val interactor = interactorField.get(host)
                ?: throw IllegalStateException("MiuiQSHostAdapter.interactor is null")
            if (interactor.javaClass.name != CURRENT_TILES_INTERACTOR) {
                throw IllegalStateException(
                    "Expected $CURRENT_TILES_INTERACTOR, got ${interactor.javaClass.name}"
                )
            }
            val tileSpecClass = load(classLoader, TILE_SPEC_CLASS)
            val companionClass = load(classLoader, "$TILE_SPEC_CLASS\$Companion")
            val createMethod = companionClass.getDeclaredMethod("create", String::class.java)
                .apply { isAccessible = true }
            val tileSpec = createMethod.invoke(null, FOCUS_CARD_SPEC)
                ?: throw IllegalStateException("TileSpec.Companion.create returned null")
            val addTile = resolveAddTileMethod(interactor.javaClass, tileSpec)
            addTile.invoke(interactor, tileSpec, userId)
            persisted = true
        } finally {
            synchronized(persistLock) {
                persistingFactories[owner]?.let { users ->
                    users.remove(userId)
                    if (users.isEmpty()) persistingFactories.remove(owner)
                }
                if (persisted) persistedFactories.getOrPut(owner) { mutableSetOf() }.add(userId)
            }
        }
    }

    private fun createFocusTile(factory: Any?, classes: FocusCardTileClasses): Any {
        val host = resolveOs4Host(factory ?: throw IllegalStateException("MiuiQSFactory instance is null"))
        val systemUiContext = Reflect.call(host, "getUserContext") as? Context
            ?: throw IllegalStateException("MiuiQSHostAdapter.getUserContext() returned no Context")
        val moduleContext = systemUiContext.createPackageContext(
            Protocol.MODULE_PACKAGE, Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
        )
        val handler = Handler(Looper.getMainLooper())
        val store = GlobalFocusCardConfigStore(moduleContext, handler)
        val repository = FocusCardStateRepository(store = store, selector = { size -> Random.nextInt(size) })
        val detailFactory = FocusCardDetailFactory { onDismiss, onStateRefresh ->
            FocusModeDetailAdapter(
                pluginContext = systemUiContext, moduleContext = moduleContext,
                detailAdapterInterface = classes.detailAdapterInterface, repository = repository,
                onDismiss = onDismiss, onStateRefresh = onStateRefresh,
                nativeDetailContentApi = classes.nativeDetailContentApi
            ).adapter
        }
        val tile = FocusCardTileProvider(
            pluginContext = systemUiContext, moduleContext = moduleContext, classes = classes,
            repository = repository, observableStore = store, detailFactory = detailFactory,
            postToUi = { action -> handler.post(action) }
        ).create()
        return initializeFocusTile(tile, currentUserId())
            ?: throw IllegalStateException("Focus tile initialization failed")
    }

    private fun installNativeDetailHooks(features: NativeDetailFeatureSet, classLoader: ClassLoader) {
        hookItemCount(features.getItemCountMethod, features.contentClass)
        hookRowVisualCleanup(features.onBindViewHolderMethod, features.contentClass, classLoader)
        hookDetailClose(features.detailCloseMethod, features.detailAdapterField)
    }

    private fun hookItemCount(method: Method, contentClass: Class<*>) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    return runCatching {
                        val outer = chain.thisObject?.let { FocusNativeDetailPolicy.resolveOuterContent(it, contentClass) }
                        val items = outer?.let(FocusNativeDetailPolicy::readItemsArray)?.size ?: 0
                        val suffix = outer?.let(FocusNativeDetailPolicy::readSuffix)
                        FocusNativeDetailPolicy.shouldReturnFullItemCount(outer, suffix, items, FocusNativeDetailRegistry)
                            ?: original
                    }.getOrElse { original }
                }
            })
    }

    private fun hookRowVisualCleanup(method: Method, contentClass: Class<*>, classLoader: ClassLoader) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    runCatching {
                        val outer = chain.thisObject?.let { FocusNativeDetailPolicy.resolveOuterContent(it, contentClass) }
                        if (FocusNativeDetailPolicy.shouldCleanBoundRow(outer, FocusNativeDetailRegistry)) {
                            resolveBoundItemView(chain.args.firstOrNull())?.let { row ->
                                FocusNativeRowVisualCleaner.clear(row, classLoader)
                            }
                        }
                    }
                    return result
                }
            })
    }

    private fun hookDetailClose(method: Method, detailAdapterField: Field) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val closing = isClosingDetailInvocation(chain.thisObject, detailAdapterField)
                    val session = if (closing) findSession(chain.thisObject) else null
                    val result = chain.proceed()
                    session?.let { runCatching { it.onPanelHidden() } }
                    return result
                }
            })
    }

    private fun findSession(root: Any?): FocusModeDetailSession? {
        val seen = Collections.newSetFromMap(IdentityHashMap<Any, Boolean>())
        fun visit(value: Any?, depth: Int): FocusModeDetailSession? {
            if (value == null || depth > 4 || !seen.add(value)) return null
            FocusNativeDetailRegistry.adapterSession(value)?.let { return it }
            value.javaClass.declaredFields.forEach { field ->
                if (Modifier.isStatic(field.modifiers)) return@forEach
                runCatching { field.isAccessible = true; visit(field.get(value), depth + 1) }
                    .getOrNull()?.let { return it }
            }
            return null
        }
        return visit(root, 0)
    }

    private fun initializeFocusTile(tile: Any, userId: Int): Any? = try {
        Reflect.call(tile, "setTileSpec", FOCUS_CARD_SPEC)
        Reflect.call(tile, "userSwitch", userId)
        Reflect.call(tile, "refreshState")
        tile
    } catch (_: Throwable) {
        runCatching { Reflect.call(tile, "destroy") }
        null
    }

    internal data class NativeDetailFeatureSet(
        val contentClass: Class<*>, val getItemCountMethod: Method,
        val onBindViewHolderMethod: Method, val detailCloseMethod: Method,
        val detailAdapterField: Field
    )

    private fun resolveOs4Host(factory: Any): Any {
        val hostLazyField = findField(factory.javaClass, "qsHostLazy")
            ?: throw NoSuchFieldException("${factory.javaClass.name}.qsHostLazy")
        hostLazyField.isAccessible = true
        val hostLazy = hostLazyField.get(factory)
            ?: throw IllegalStateException("MiuiQSFactory.qsHostLazy is null")
        val host = Reflect.call(hostLazy, "get")
            ?: throw IllegalStateException("MiuiQSFactory.qsHostLazy.get() returned null")
        if (host.javaClass.name != MIUI_QS_HOST_ADAPTER) {
            throw IllegalStateException("Expected $MIUI_QS_HOST_ADAPTER, got ${host.javaClass.name}")
        }
        return host
    }

    private fun load(loader: ClassLoader, name: String): Class<*> = Class.forName(name, false, loader)

    private fun findField(clazz: Class<*>, name: String): Field? {
        var current: Class<*>? = clazz
        while (current != null) {
            try { return current.getDeclaredField(name) } catch (_: NoSuchFieldException) { current = current.superclass }
        }
        return null
    }

    private fun markInstalling(loader: ClassLoader): Boolean = synchronized(installLock) {
        installedLoaders.removeAll { it.get() == null }
        if (installedLoaders.any { it.get() === loader }) false else { installedLoaders += WeakReference(loader); true }
    }

    private fun unmarkInstalling(loader: ClassLoader) = synchronized(installLock) {
        installedLoaders.removeAll { it.get() == null || it.get() === loader }
    }

    private fun logMsg(message: String, throwable: Throwable? = null) {
        android.util.Log.w(TAG, if (throwable == null) message else "$message: ${android.util.Log.getStackTraceString(throwable)}")
    }

    private fun currentUserId(): Int = runCatching {
        Reflect.callStatic(ActivityManager::class.java, "getCurrentUser") as? Int ?: 0
    }.getOrDefault(0)

    private fun logPersistFailureOnce(throwable: Throwable) {
        synchronized(persistFailureTypes) {
            val key = throwable.javaClass.name + ":" + throwable.message
            if (!persistFailureTypes.add(key)) return
        }
        logMsg("OS4 Focus tile repository insertion deferred until the pipeline is ready", throwable)
    }

    companion object {
        const val FOCUS_CARD_SPEC = "hypermodes_focus"
        internal fun resolveBoundItemView(holder: Any?): View? {
            if (holder == null) return null
            var current: Class<*>? = holder.javaClass
            while (current != null) {
                val owner = current
                runCatching {
                    val field = owner.getDeclaredField("itemView").apply { isAccessible = true }
                    (field.get(holder) as? View)?.let { return it }
                }
                current = current.superclass
            }
            return holder.javaClass.methods.firstOrNull { it.name == "getItemView" && it.parameterTypes.isEmpty() }
                ?.let { runCatching { it.invoke(holder) }.getOrNull() as? View }
        }

        internal fun validatedNativeDetailFeatureSet(
            classLoader: ClassLoader, onFailure: (Throwable) -> Unit = {}
        ): NativeDetailFeatureSet? = try {
            val contentClass = Class.forName(QS_DETAIL_CONTENT_CLASS, false, classLoader)
            val adapterClass = contentClass.declaredClasses.firstOrNull { it.simpleName == "Adapter" }
                ?: Class.forName("$QS_DETAIL_CONTENT_CLASS\$Adapter", false, classLoader)
            val count = adapterClass.getDeclaredMethod("getItemCount").apply { isAccessible = true }
            val bind = adapterClass.declaredMethods.first { method ->
                method.name == "onBindViewHolder" && method.parameterTypes.size == 3 &&
                    method.parameterTypes[1] == Int::class.javaPrimitiveType &&
                    List::class.java.isAssignableFrom(method.parameterTypes[2])
            }.apply { isAccessible = true }
            val close = Class.forName(MIUI_QS_DETAIL_CLOSE_LAMBDA, false, classLoader)
                .getDeclaredMethod("run").apply { isAccessible = true }
            val detailAdapterField = close.declaringClass.getDeclaredField("f$1").apply { isAccessible = true }
            NativeDetailFeatureSet(contentClass, count, bind, close, detailAdapterField)
        } catch (t: Throwable) {
            onFailure(t); null
        }

        internal fun resolveCreateTileMethod(factoryClass: Class<*>): Method =
            factoryClass.declaredMethods.firstOrNull { method ->
                method.name == CREATE_TILE && method.parameterTypes.contentEquals(arrayOf(String::class.java))
            }?.apply { isAccessible = true }
                ?: throw NoSuchMethodException("${factoryClass.name}.$CREATE_TILE(String)")

        internal fun resolveAddTileMethod(interactorClass: Class<*>, tileSpec: Any): Method =
            interactorClass.declaredMethods.firstOrNull { method ->
                method.name == ADD_TILE && method.parameterTypes.size == 2 &&
                    method.parameterTypes[1] == Int::class.javaPrimitiveType &&
                    method.parameterTypes[0].isInstance(tileSpec)
            }?.apply { isAccessible = true }
                ?: throw NoSuchMethodException("${interactorClass.name}.$ADD_TILE(TileSpec,int)")

        internal fun isClosingDetailInvocation(lambda: Any?, detailAdapterField: Field): Boolean =
            lambda != null && runCatching { detailAdapterField.get(lambda) == null }.getOrDefault(false)
        private const val TAG = "HyperModes.ControlCenterCardHook"
        private const val MIUI_QS_FACTORY = "com.android.systemui.qs.tileimpl.MiuiQSFactory"
        private const val MIUI_QS_HOST_ADAPTER = "com.android.systemui.qs.pipeline.domain.adapter.MiuiQSHostAdapter"
        private const val CURRENT_TILES_INTERACTOR = "com.android.systemui.qs.pipeline.domain.interactor.CurrentTilesInteractorImpl"
        private const val TILE_SPEC_CLASS = "com.android.systemui.qs.pipeline.shared.TileSpec"
        private const val QS_DETAIL_CONTENT_CLASS = "com.android.systemui.qs.QSDetailContent"
        private const val MIUI_QS_DETAIL_CLOSE_LAMBDA = "com.android.systemui.qs.MiuiQSDetail\$2\$\$ExternalSyntheticLambda1"
        private const val CREATE_TILE = "createTile"
        private const val ADD_TILE = "addTile"
        private val installLock = Any()
        private val installedLoaders = mutableListOf<WeakReference<ClassLoader>>()
        private val persistLock = Any()
        private val persistedFactories = java.util.WeakHashMap<Any, MutableSet<Int>>()
        private val persistingFactories = java.util.WeakHashMap<Any, MutableSet<Int>>()
        private val persistFailureTypes = mutableSetOf<String>()
    }
}
