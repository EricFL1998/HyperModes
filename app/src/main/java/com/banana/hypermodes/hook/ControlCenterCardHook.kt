package com.banana.hypermodes.hook

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.content.SharedPreferences
import android.content.res.Resources
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
import com.banana.hypermodes.utils.HyperLog
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.ref.WeakReference
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.lang.reflect.Modifier
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

internal class FocusTailContent(
    private val delegate: Any,
    private val mainPanelContentInterface: Class<*>
) {
    private val proxy: Any by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        Proxy.newProxyInstance(
            mainPanelContentInterface.classLoader,
            arrayOf(mainPanelContentInterface),
            TailInvocationHandler()
        )
    }

    fun proxy(): Any = proxy

    fun focusRecord(): Any? {
        return runCatching {
            Reflect.call(delegate, "getTile", ControlCenterCardHook.FOCUS_CARD_SPEC)
        }.getOrNull()
    }

    private inner class TailInvocationHandler : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
            val safeArgs = args ?: emptyArray()
            return when (method.name) {
                "equals" -> proxy === safeArgs.firstOrNull()
                "hashCode" -> System.identityHashCode(proxy)
                "toString" -> "FocusTailContent(proxy=" + System.identityHashCode(proxy) + ")"
                "getListItems" -> focusRecord()?.let { listOf(it) } ?: emptyList<Any>()
                "available" -> {
                    if (ControlCenterCardHook.isInEditMode(delegate)) return false
                    val delegateAvailable = callDelegate(method, safeArgs) as? Boolean == true
                    val recordAvailable = focusRecord() != null
                    delegateAvailable && recordAvailable
                }
                "getRightOrLeft" -> true
                "getPriority" -> 60
                "moveElement" -> false
                "createViewHolder" -> callDelegate(method, safeArgs) ?: defaultValue(method.returnType)
                else -> callDelegate(method, safeArgs) ?: defaultValue(method.returnType)
            }
        }

        private fun callDelegate(method: Method, args: Array<out Any?>): Any? {
            return runCatching {
                Reflect.call(delegate, method.name, *args)
            }.recoverCatching {
                callInterfaceDefault(method, args)
            }.getOrNull()
        }

        private fun callInterfaceDefault(method: Method, args: Array<out Any?>): Any? {
            if (!method.isDefault) throw NoSuchMethodException(method.name)
            return java.lang.invoke.MethodHandles.privateLookupIn(method.declaringClass, java.lang.invoke.MethodHandles.lookup())
                .unreflectSpecial(method, method.declaringClass)
                .bindTo(delegate)
                .invokeWithArguments(*args)
        }

        private fun defaultValue(returnType: Class<*>): Any? {
            return when (returnType) {
                Void.TYPE -> null
                java.lang.Boolean.TYPE -> false
                java.lang.Integer.TYPE -> 0
                java.lang.Long.TYPE -> 0L
                java.lang.Float.TYPE -> 0f
                java.lang.Double.TYPE -> 0.0
                java.lang.Short.TYPE -> 0.toShort()
                java.lang.Byte.TYPE -> 0.toByte()
                java.lang.Character.TYPE -> '\u0000'
                else -> null
            }
        }
    }
}

/** Control Center integration for the OS4 SystemUI tile pipeline. */
class ControlCenterCardHook(private val module: XposedModule) {

    fun install(systemUiClassLoader: ClassLoader) {
        if (!markInstalling(systemUiClassLoader)) return
        ensureFocusTileLarge(systemUiClassLoader)
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

    /**
     * OS4 keeps the HyperOS control center UI inside the miui.systemui.plugin APK
     * (loaded via PluginInstance). Tiles themselves are created by the SystemUI
     * pipeline (see [install]), but large-vs-small rendering and card placement are
     * decided by plugin classes, exactly like OS3. This installs those plugin-side
     * hooks once the plugin ClassLoader is available.
     */
    fun installPluginHooks(pluginClassLoader: ClassLoader) {
        if (!markInstalling(pluginClassLoader)) return
        logMsg("Plugin ClassLoader accepted: " + pluginClassLoader)
        try {
            val controllerClass = load(pluginClassLoader, QS_CONTROLLER_CLASS)
            val specsMethod = controllerClass.getDeclaredMethod(GET_CARD_STYLE_TILE_SPECS)
                .apply { isAccessible = true }
            if (specsMethod.parameterTypes.isNotEmpty()) {
                throw NoSuchMethodException(
                    QS_CONTROLLER_CLASS + "." + GET_CARD_STYLE_TILE_SPECS + "(" + specsMethod.parameterTypes.size + ")"
                )
            }
            hookSpecs(specsMethod)

            // QSListController filters small-tile rendering through this getter. The
            // backing field is initialized in the QSController constructor, which runs
            // inside PluginInstance.loadPlugin() BEFORE our after-proceed hook installs,
            // so the field snapshot would not contain the focus spec. Hooking the getter
            // guarantees the card spec is always excluded from the small-tile list and
            // prevents a duplicate 1x1 entry alongside the 1x2 card.
            runCatching {
                val excludeGetter = controllerClass.getDeclaredMethod(GET_QS_LIST_EXCLUDE_TILE_SPECS)
                    .apply { isAccessible = true }
                if (excludeGetter.parameterTypes.isEmpty()) {
                    hookQsListExcludeSpecs(excludeGetter)
                }
            }.onFailure { logMsg("QSList exclude getter hook unavailable", it) }

            // TileQueryHelper (edit mode) reads the start-exclude list through a separate
            // getter, so cover it as well to keep the focus tile out of the small-tile
            // path there too.
            runCatching {
                val startExcludeGetter = controllerClass.getDeclaredMethod(GET_QS_LIST_START_EXCLUDE_TILE_SPECS)
                    .apply { isAccessible = true }
                if (startExcludeGetter.parameterTypes.isEmpty()) {
                    hookQsListExcludeSpecs(startExcludeGetter)
                }
            }.onFailure { logMsg("QSList start-exclude getter hook unavailable", it) }

            runCatching {
                val cardsControllerClass = load(pluginClassLoader, QS_CARDS_CONTROLLER_CLASS)
                val preparePanelUpdate = cardsControllerClass.getDeclaredMethod(PREPARE_PANEL_UPDATE)
                    .apply { isAccessible = true }
                if (preparePanelUpdate.parameterTypes.isNotEmpty()) {
                    throw NoSuchMethodException(
                        QS_CARDS_CONTROLLER_CLASS + "." + PREPARE_PANEL_UPDATE + "(" + preparePanelUpdate.parameterTypes.size + ")"
                    )
                }
                hookPreparePanelUpdate(preparePanelUpdate)
            }.onFailure { logMsg("Focus card sizing hook unavailable on this plugin", it) }

            val tailFeatureSet = validatedTailFeatureSet(pluginClassLoader)
            if (tailFeatureSet == null) {
                logMsg("Focus tail feature set unavailable; relying on card-style promotion only")
            } else {
                hookListItems(tailFeatureSet.listItemsMethod)
                hookListItems(tailFeatureSet.listItemsMethodQsList)
                hookDistributePanels(tailFeatureSet.distributePanelsMethod, pluginClassLoader)
            }

            val pluginDetailFeatureSet = validatedPluginDetailFeatureSet(pluginClassLoader)
            if (pluginDetailFeatureSet == null) {
                logMsg("Plugin detail feature set unavailable; detail exit notifications disabled")
            } else {
                hookAdapterMapping(pluginDetailFeatureSet.secondaryParamsFromMethod)
                hookSpecificHeight(pluginDetailFeatureSet.getUseSpecificHeightMethod)
                hookPanelHidden(pluginDetailFeatureSet.onHiddenMethod)
            }
            logMsg("OS4 plugin control center hooks installed for " + pluginClassLoader)
        } catch (t: Throwable) {
            unmarkInstalling(pluginClassLoader)
            logMsg("OS4 plugin control center hook installation failed", t)
        }
    }

    private fun hookAdapterMapping(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val adapter = chain.args.firstOrNull()
                    val focusSpec = FocusNativeDetailPolicy.shouldMapToFocusSpec(
                        adapter = adapter,
                        registry = FocusNativeDetailRegistry
                    )
                    return focusSpec ?: chain.proceed()
                }
            })
    }

    private fun hookSpecificHeight(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    return try {
                        val params = chain.thisObject ?: return original
                        val adapter = reflectAdapter(params)
                        FocusNativeDetailPolicy.shouldUseSpecificHeight(
                            adapter = adapter,
                            registry = FocusNativeDetailRegistry
                        ) ?: original
                    } catch (t: Throwable) {
                        logMsg("failed to intercept specific height in " + DETAIL_PANEL_PARAMS_CLASS + ".getUseSpecificHeight", t)
                        original
                    }
                }
            })
    }

    private fun hookPanelHidden(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val delegate = chain.thisObject
                    val adapter = delegate?.let { reflectAdapter(it) }
                    val session = adapter?.let { FocusNativeDetailRegistry.adapterSession(it) }
                    val result = chain.proceed()
                    if (session != null) {
                        try {
                            session.onPanelHidden()
                        } catch (t: Throwable) {
                            logMsg("failed to notify panel hidden for Focus session", t)
                        }
                    }
                    return result
                }
            })
    }

    private fun reflectAdapter(instance: Any?): Any? {
        if (instance == null) return null
        return try {
            for (name in listOf("adapter", "mAdapter", "detailAdapter")) {
                val field = findField(instance.javaClass, name) ?: continue
                field.isAccessible = true
                return field.get(instance)
            }
            null
        } catch (_: Throwable) {
            null
        }
    }

    private fun hookSpecs(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    return try {
                        val appended = appendFocusSpec(original)
                        if (original is List<*> && appended is List<*> && appended !== original &&
                            !original.contains(FOCUS_CARD_SPEC) && appendShapeLogged.compareAndSet(false, true)
                        ) {
                            logMsg("Focus spec appended to card style list size=" + original.size)
                        }
                        appended
                    } catch (t: Throwable) {
                        logMsg("failed to append Focus spec in " + QS_CONTROLLER_CLASS + "." + GET_CARD_STYLE_TILE_SPECS, t)
                        original
                    }
                }
            })
    }

    private fun hookQsListExcludeSpecs(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    if (original !is ArrayList<*>) return original
                    return try {
                        if (original.any { it == FOCUS_CARD_SPEC }) {
                            original
                        } else {
                            @Suppress("UNCHECKED_CAST")
                            (original.clone() as ArrayList<Any>).also { it.add(FOCUS_CARD_SPEC) }
                        }
                    } catch (t: Throwable) {
                        logMsg("failed to append Focus spec to QSList exclusion list", t)
                        original
                    }
                }
            })
    }

    private fun hookPreparePanelUpdate(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (applyFocusCardSizing(chain.thisObject) && focusSizingLogged.compareAndSet(false, true)) {
                        logMsg("Focus card horizontal sizing applied")
                    }
                    return result
                }
            })
    }

    private fun hookListItems(method: Method) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    return try {
                        filterFocusRecord(original)
                    } catch (t: Throwable) {
                        logMsg("failed to filter Focus record in " + method.declaringClass.name + "." + GET_LIST_ITEMS, t)
                        original
                    }
                }
            })
    }

    private fun hookDistributePanels(method: Method, classLoader: ClassLoader) {
        module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        if (insertFocusTailFromDistributor(chain.thisObject, classLoader) &&
                            focusTailInsertionLogged.compareAndSet(false, true)
                        ) {
                            logMsg("Focus tail inserted into main panel content")
                        }
                    } catch (t: Throwable) {
                        logMsg("failed to insert Focus tail in " + MAIN_PANEL_CONTENT_DISTRIBUTOR_CLASS + "." + DISTRIBUTE_PANELS, t)
                    }
                    return result
                }
            })
    }

    private fun ensureFocusTileLarge(classLoader: ClassLoader) {
        try {
            hookDefaultLargeTilesRepository(classLoader)
           hookLargeTileSpecsIO(classLoader)
           hookIconTilesInteractor(classLoader)
           hookIconTilesViewModelImpl(classLoader)
           hookSizedTileImpl(classLoader)
            hookAllIsIconTilePaths(classLoader)
            logMsg("Focus large tile hooks installed")
        } catch (t: Throwable) {
            logMsg("Failed to ensure focus large tile", t)
        }
    }

    private fun hookDefaultLargeTilesRepository(classLoader: ClassLoader) {
        val repoClass = load(classLoader, DEFAULT_LARGE_TILES_REPO)
        val constructor = repoClass.getDeclaredConstructor(Resources::class.java).apply { isAccessible = true }
        module.hook(constructor).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    runCatching { injectFocusTileIntoLargeRepository(chain.thisObject, classLoader) }
                        .onFailure { logMsg("Failed to inject focus large tile", it) }
                    return result
                }
            })
        runCatching { patchExistingLargeTileRepository(classLoader) }
    }

    private fun hookLargeTileSpecsIO(classLoader: ClassLoader) {
        val prefsRepoClass = load(classLoader, QS_PREFERENCES_REPO)
        val readMethod = prefsRepoClass.getDeclaredMethod("getLargeTilesSpecs", SharedPreferences::class.java).apply { isAccessible = true }
        module.hook(readMethod).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed() as? Set<*>
                    return original?.let { injectIntoTileSpecSet(it, classLoader) }
                }
            })
        val writeMethod = prefsRepoClass.getDeclaredMethod("writeLargeTileSpecs", SharedPreferences::class.java, Set::class.java).apply { isAccessible = true }
        module.hook(writeMethod).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.args.getOrNull(1) as? Set<*>
                    val patched = original?.let { injectIntoTileSpecSet(it, classLoader) }
                    if (patched != null) chain.args[1] = patched
                    return chain.proceed()
                }
            })
    }

    private fun patchExistingLargeTileRepository(classLoader: ClassLoader) {
        try {
            val prefsRepoClass = load(classLoader, QS_PREFERENCES_REPO)
            for (constructor in prefsRepoClass.declaredConstructors) {
                constructor.isAccessible = true
                module.hook(constructor).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(object : XposedInterface.Hooker {
                        override fun intercept(chain: XposedInterface.Chain): Any? {
                            val result = chain.proceed()
                            runCatching {
                                val repoField = findField(chain.thisObject.javaClass, "defaultLargeTilesRepository")
                                    ?: throw NoSuchFieldException("defaultLargeTilesRepository")
                                repoField.isAccessible = true
                                val repo = repoField.get(chain.thisObject)
                                if (repo != null) injectFocusTileIntoLargeRepository(repo, classLoader)
                            }.onFailure { logMsg("Failed to patch existing large tile repository", it) }
                            return result
                        }
                    })
            }
        } catch (t: Throwable) {
            logMsg("Failed to hook QSPreferencesRepository constructor", t)
        }
    }

    private fun injectFocusTileIntoLargeRepository(repository: Any, classLoader: ClassLoader) {
        val field = findField(repository.javaClass, "defaultLargeTiles")
            ?: throw NoSuchFieldException("${repository.javaClass.name}.defaultLargeTiles")
        field.isAccessible = true
        val current = field.get(repository) as? Set<*>
        if (current == null) {
            logMsg("defaultLargeTiles is null")
            return
        }
        field.set(repository, injectIntoTileSpecSet(current, classLoader))
        logMsg("Injected $FOCUS_CARD_SPEC into defaultLargeTiles")
    }

    private fun injectIntoTileSpecSet(set: Set<*>, classLoader: ClassLoader): Set<Any> {
        val spec = createTileSpec(classLoader, FOCUS_CARD_SPEC)
        if (spec == null || set.contains(spec)) {
            @Suppress("UNCHECKED_CAST")
            return set as Set<Any>
        }
        @Suppress("UNCHECKED_CAST")
        val newSet = LinkedHashSet<Any>(set as Set<Any>)
        newSet.add(spec)
        return Collections.unmodifiableSet(newSet)
    }

    private fun createTileSpec(classLoader: ClassLoader, spec: String): Any? {
        val companionClass = load(classLoader, "$TILE_SPEC_CLASS\$Companion")
        val method = companionClass.getDeclaredMethod("create", String::class.java).apply { isAccessible = true }
        return method.invoke(null, spec)
    }

    private fun hookSizedTileImpl(classLoader: ClassLoader) {
        try {
            val clazz = load(classLoader, SIZED_TILE_IMPL)
            val constructor = clazz.getDeclaredConstructor(Int::class.javaPrimitiveType, Object::class.java).apply { isAccessible = true }
            module.hook(constructor).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                   override fun intercept(chain: XposedInterface.Chain): Any? {
                       val tile = chain.args.getOrNull(1)
                        val extractedSpec = extractTileSpecString(tile)
                        if (extractedSpec?.contains("hypermode") == true || tile?.javaClass?.name?.contains("Tile") == true) {
                            logMsg("SizedTileImpl: tileClass=" + tile?.javaClass?.simpleName + ", spec=" + extractedSpec + ", width=" + chain.args.getOrNull(0))
                        }
                        if (extractedSpec == FOCUS_CARD_SPEC) {
                           val currentWidth = chain.args.getOrNull(0) as? Int ?: 1
                           if (currentWidth <= 1) {
                               chain.args[0] = 2
                               logMsg("Forced $FOCUS_CARD_SPEC SizedTileImpl width to 2 (was $currentWidth)")
                           }
                       }
                       return chain.proceed()
                   }
                })
        } catch (t: Throwable) {
            logMsg("Failed to hook SizedTileImpl constructor", t)
        }
    }

   private fun hookAllIsIconTilePaths(classLoader: ClassLoader) {
       val tileSpecClass = load(classLoader, TILE_SPEC_CLASS)
       val classesToHook = listOf(
           ICON_TILES_VIEW_MODEL_IMPL,
           DYNAMIC_ICON_TILES_VIEW_MODEL,
           PAGINATED_GRID_VIEW_MODEL
       )
        for (className in classesToHook) {
            try {
                val clazz = load(classLoader, className)
                val method = try {
                    clazz.getDeclaredMethod("isIconTile", tileSpecClass)
                } catch (_: NoSuchMethodException) {
                    clazz.getMethod("isIconTile", tileSpecClass)
                }?.apply { isAccessible = true } ?: continue
                module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                    .intercept(object : XposedInterface.Hooker {
                       override fun intercept(chain: XposedInterface.Chain): Any? {
                           val spec = chain.args.firstOrNull()
                           val specString = spec?.let { extractTileSpecString(it) }
                            if (specString?.contains("hypermode") == true) {
                                logMsg("isIconTile on " + clazz.simpleName + ": specString=" + specString)
                            }
                           if (specString == FOCUS_CARD_SPEC) {
                               logMsg("Forcing $FOCUS_CARD_SPEC as large tile via " + clazz.simpleName + ".isIconTile")
                               return false
                           }
                           return chain.proceed()
                       }
                    })
            } catch (t: Throwable) {
                logMsg("Failed to hook isIconTile on $className", t)
            }
        }
    }

    private fun extractTileSpecString(tile: Any?): String? {
        if (tile == null) return null
        getTileSpecString(tile)?.let { return it }
        for (fieldName in listOf("spec", "tileSpec")) {
            try {
                val field = findField(tile.javaClass, fieldName) ?: continue
                field.isAccessible = true
                val value = field.get(tile) ?: continue
                getTileSpecString(value)?.let { return it }
                value.toString().takeIf { it.isNotBlank() && it != "Invalid" }?.let { return it }
            } catch (_: Throwable) {}
        }
        return null
    }

    private fun hookIconTilesInteractor(classLoader: ClassLoader) {
        val interactorClass = load(classLoader, ICON_TILES_INTERACTOR)
        for (constructor in interactorClass.declaredConstructors) {
            constructor.isAccessible = true
            module.hook(constructor).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val result = chain.proceed()
                        runCatching { patchIconTilesInteractorLargeTiles(result, classLoader) }
                            .onFailure { logMsg("Failed to patch IconTilesInteractor large tiles", it) }
                        return result
                    }
                })
        }
    }

    private fun patchIconTilesInteractorLargeTiles(interactor: Any?, classLoader: ClassLoader) {
        if (interactor == null) return
        val field = findField(interactor.javaClass, "largeTilesSpecs") ?: return
        field.isAccessible = true
        val currentFlow = field.get(interactor) ?: return
        val getValue = currentFlow.javaClass.methods.firstOrNull {
            it.name == "getValue" && it.parameterTypes.isEmpty()
        }?.apply { isAccessible = true } ?: return
        @Suppress("UNCHECKED_CAST")
        val currentValue = getValue.invoke(currentFlow) as? Set<Any>
        if (currentValue == null) {
            logMsg("IconTilesInteractor.largeTilesSpecs value is null")
            return
        }
        val patchedValue = injectIntoTileSpecSet(currentValue, classLoader)
        val mutableFlow = createMutableStateFlow(classLoader, patchedValue)
        val readonlyFlow = createReadonlyStateFlow(classLoader, mutableFlow)
        field.set(interactor, readonlyFlow)
        logMsg("Patched IconTilesInteractor.largeTilesSpecs, added focus tile if missing")
    }

    private fun createMutableStateFlow(classLoader: ClassLoader, initialValue: Any): Any {
        val stateFlowKt = load(classLoader, "kotlinx.coroutines.flow.StateFlowKt")
        val method = stateFlowKt.getDeclaredMethod("MutableStateFlow", Object::class.java).apply { isAccessible = true }
        return method.invoke(null, initialValue)
            ?: throw IllegalStateException("MutableStateFlow returned null")
    }

    private fun createReadonlyStateFlow(classLoader: ClassLoader, mutableFlow: Any): Any {
        val readonlyClass = load(classLoader, "kotlinx.coroutines.flow.ReadonlyStateFlow")
        val constructor = readonlyClass.getDeclaredConstructor(
            Class.forName("kotlinx.coroutines.flow.MutableStateFlow", false, classLoader)
        ).apply { isAccessible = true }
        return constructor.newInstance(mutableFlow)
    }

    private fun hookDynamicIconTilesInteractor(classLoader: ClassLoader) {
        try {
            val clazz = load(classLoader, DYNAMIC_ICON_TILES_INTERACTOR)
            val method = clazz.getDeclaredMethod("getLargeTiles").apply { isAccessible = true }
            module.hook(method).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
                .intercept(object : XposedInterface.Hooker {
                    override fun intercept(chain: XposedInterface.Chain): Any? {
                        val original = chain.proceed()
                        return runCatching { wrapLargeTilesStateFlow(original, classLoader) }
                            .onFailure { logMsg("Failed to wrap large tiles StateFlow", it) }
                            .getOrDefault(original)
                    }
                })
        } catch (t: Throwable) {
            logMsg("Failed to hook DynamicIconTilesInteractor", t)
        }
    }

    private fun wrapLargeTilesStateFlow(original: Any?, classLoader: ClassLoader): Any? {
        if (original == null) return null
        val stateFlowClass = load(classLoader, "kotlinx.coroutines.flow.StateFlow")
        val flowClass = load(classLoader, "kotlinx.coroutines.flow.Flow")
        return Proxy.newProxyInstance(
            classLoader,
            arrayOf(stateFlowClass, flowClass),
            InvocationHandler { _, method, args ->
                when (method.name) {
                    "getValue" -> {
                        val value = method.invoke(original, *(args ?: emptyArray()))
                        (value as? Set<*>)?.let { injectIntoTileSpecSet(it, classLoader) } ?: value
                    }
                    "equals" -> original === args?.firstOrNull()
                    "hashCode" -> System.identityHashCode(original)
                    "toString" -> "PatchedLargeTilesStateFlow"
                    else -> method.invoke(original, *(args ?: emptyArray()))
                }
            }
        )
    }

    private fun hookIconTilesViewModelImpl(classLoader: ClassLoader) {
        val clazz = load(classLoader, ICON_TILES_VIEW_MODEL_IMPL)
        val tileSpecClass = load(classLoader, TILE_SPEC_CLASS)
        val isIconTile = clazz.getDeclaredMethod("isIconTile", tileSpecClass).apply { isAccessible = true }
        module.hook(isIconTile).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val spec = chain.args.firstOrNull()
                    if (spec != null && getTileSpecString(spec) == FOCUS_CARD_SPEC) {
                        logMsg("Forcing hypermodes_focus as large tile")
                        return false
                    }
                    return chain.proceed()
                }
            })
        val getLargeTiles = clazz.getDeclaredMethod("getLargeTiles").apply { isAccessible = true }
        module.hook(getLargeTiles).setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    return wrapLargeTilesStateFlow(chain.proceed(), classLoader)
                }
            })
    }

    private fun getTileSpecString(spec: Any): String? {
        return runCatching {
            spec.javaClass.methods.firstOrNull { it.name == "getSpec" && it.parameterTypes.isEmpty() }
                ?.apply { isAccessible = true }?.invoke(spec) as? String
        }.getOrNull()
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
        private const val DEFAULT_LARGE_TILES_REPO = "com.android.systemui.qs.panels.data.repository.DefaultLargeTilesRepositoryImpl"
        private const val QS_PREFERENCES_REPO = "com.android.systemui.qs.panels.data.repository.QSPreferencesRepository"
        private const val ICON_TILES_INTERACTOR = "com.android.systemui.qs.panels.domain.interactor.IconTilesInteractor"
        private const val DYNAMIC_ICON_TILES_INTERACTOR = "com.android.systemui.qs.panels.domain.interactor.DynamicIconTilesInteractor"
        private const val ICON_TILES_VIEW_MODEL_IMPL = "com.android.systemui.qs.panels.ui.viewmodel.IconTilesViewModelImpl"
        private const val ICON_TILES_VIEW_MODEL = "com.android.systemui.qs.panels.ui.viewmodel.IconTilesViewModel"
        private const val DYNAMIC_ICON_TILES_VIEW_MODEL = "com.android.systemui.qs.panels.ui.viewmodel.DynamicIconTilesViewModel"
        private const val PAGINATED_GRID_VIEW_MODEL = "com.android.systemui.qs.panels.ui.viewmodel.PaginatedGridViewModel"
        private const val SIZED_TILE_IMPL = "com.android.systemui.qs.panels.shared.model.SizedTileImpl"
        private const val QS_DETAIL_CONTENT_CLASS = "com.android.systemui.qs.QSDetailContent"
        private const val MIUI_QS_DETAIL_CLOSE_LAMBDA = "com.android.systemui.qs.MiuiQSDetail\$2\$\$ExternalSyntheticLambda1"
        private const val CREATE_TILE = "createTile"
        private const val ADD_TILE = "addTile"
        private const val QS_CONTROLLER_CLASS = "miui.systemui.controlcenter.qs.QSController"
        private const val QS_CARDS_CONTROLLER_CLASS = "miui.systemui.controlcenter.panel.main.qs.QSCardsController"
        private const val QS_LIST_CONTROLLER_CLASS = "miui.systemui.controlcenter.panel.main.qs.QSListController"
        private const val MAIN_PANEL_CONTENT_CLASS = "miui.systemui.controlcenter.panel.main.MainPanelContent"
        private const val MAIN_PANEL_CONTENT_DISTRIBUTOR_CLASS =
            "miui.systemui.controlcenter.panel.main.MainPanelContentDistributor"
        private const val SECONDARY_PARAMS_KT_CLASS =
            "miui.systemui.controlcenter.panel.secondary.SecondaryParamsKt"
        private const val DETAIL_PANEL_PARAMS_CLASS =
            "miui.systemui.controlcenter.panel.secondary.DetailPanelParams"
        private const val DETAIL_PANEL_DELEGATE_CLASS =
            "miui.systemui.controlcenter.panel.secondary.detail.DetailPanelDelegate"
        private const val GET_CARD_STYLE_TILE_SPECS = "getCardStyleTileSpecs"
        private const val GET_QS_LIST_EXCLUDE_TILE_SPECS = "getQsListExcludeTileSpecs"
        private const val GET_QS_LIST_START_EXCLUDE_TILE_SPECS = "getQsListStartExcludeTileSpecs"
        private const val PREPARE_PANEL_UPDATE = "preparePanelUpdate"
        private const val GET_LIST_ITEMS = "getListItems"
        private const val DISTRIBUTE_PANELS = "distributePanels"
        private const val RIGHT_PANEL_CONTENT = "rightPanelContent"
        private const val RIGHT_FOOTER_SPACE = "rightFooterSpace"
        private const val CHILD_CONTROLLERS = "childControllers"
        private val installLock = Any()
        private val installedLoaders = mutableListOf<WeakReference<ClassLoader>>()
        private val persistLock = Any()
        private val persistedFactories = java.util.WeakHashMap<Any, MutableSet<Int>>()
        private val persistingFactories = java.util.WeakHashMap<Any, MutableSet<Int>>()
        private val persistFailureTypes = mutableSetOf<String>()
        private val tailContentLock = Any()
        private val tailContents = mutableListOf<TailContentEntry>()
        private val appendShapeLogged = AtomicBoolean(false)
        private val focusSizingLogged = AtomicBoolean(false)
        private val focusTailInsertionLogged = AtomicBoolean(false)

        internal fun focusTailContent(
            delegate: Any,
            mainPanelContentInterface: Class<*>
        ): FocusTailContent = synchronized(tailContentLock) {
            pruneTailContentsLocked()
            tailContents.firstOrNull { entry ->
                entry.classLoader.get() === mainPanelContentInterface.classLoader &&
                    entry.delegate.get() === delegate
            }?.tail?.get()?.let { return@synchronized it }

            val tail = FocusTailContent(delegate, mainPanelContentInterface)
            tailContents += TailContentEntry(
                classLoader = WeakReference(mainPanelContentInterface.classLoader),
                delegate = WeakReference(delegate),
                tail = WeakReference(tail)
            )
            tail
        }

        internal fun focusTailContent(delegate: Any, pluginClassLoader: ClassLoader): FocusTailContent {
            val mainPanelContentInterface = loadClass(pluginClassLoader, MAIN_PANEL_CONTENT_CLASS)
            return focusTailContent(delegate, mainPanelContentInterface)
        }

        private fun loadClass(loader: ClassLoader, name: String): Class<*> =
            Class.forName(name, false, loader)

        private fun pruneTailContentsLocked() {
            tailContents.removeAll { entry ->
                entry.classLoader.get() == null || entry.delegate.get() == null || entry.tail.get() == null
            }
        }

        private data class TailContentEntry(
            val classLoader: WeakReference<ClassLoader>,
            val delegate: WeakReference<Any>,
            val tail: WeakReference<FocusTailContent>
        )

        internal data class TailFeatureSet(
            val listItemsMethod: Method,
            val listItemsMethodQsList: Method,
            val distributePanelsMethod: Method,
            val rightPanelContentField: Field,
            val rightFooterSpaceField: Field,
            val childControllersField: Field
        )

        internal data class PluginDetailFeatureSet(
            val secondaryParamsFromMethod: Method,
            val getUseSpecificHeightMethod: Method,
            val onHiddenMethod: Method
        )

        internal fun validatedPluginDetailFeatureSet(classLoader: ClassLoader): PluginDetailFeatureSet? {
            return try {
                val secondaryParamsKtClass = loadClass(classLoader, SECONDARY_PARAMS_KT_CLASS)
                val detailPanelParamsClass = loadClass(classLoader, DETAIL_PANEL_PARAMS_CLASS)
                val detailPanelDelegateClass = loadClass(classLoader, DETAIL_PANEL_DELEGATE_CLASS)
                val detailAdapterClass = loadClass(classLoader, "com.android.systemui.plugins.qs.DetailAdapter")

                val secondaryParamsFromMethod = secondaryParamsKtClass
                    .getDeclaredMethod("from", detailAdapterClass)
                    .also { it.isAccessible = true }
                val getUseSpecificHeightMethod = detailPanelParamsClass
                    .getDeclaredMethod("getUseSpecificHeight")
                    .also { it.isAccessible = true }
                val onHiddenMethod = detailPanelDelegateClass
                    .getDeclaredMethod("onHidden")
                    .also { it.isAccessible = true }

                if (secondaryParamsFromMethod.parameterTypes.size != 1 ||
                    getUseSpecificHeightMethod.parameterTypes.isNotEmpty() ||
                    onHiddenMethod.parameterTypes.isNotEmpty()
                ) {
                    return null
                }
                PluginDetailFeatureSet(
                    secondaryParamsFromMethod = secondaryParamsFromMethod,
                    getUseSpecificHeightMethod = getUseSpecificHeightMethod,
                    onHiddenMethod = onHiddenMethod
                )
            } catch (_: Throwable) {
                null
            }
        }

        internal fun validatedTailFeatureSet(classLoader: ClassLoader): TailFeatureSet? {
            return try {
                val cardsControllerClass = loadClass(classLoader, QS_CARDS_CONTROLLER_CLASS)
                val qsListControllerClass = loadClass(classLoader, QS_LIST_CONTROLLER_CLASS)
                val distributorClass = loadClass(classLoader, MAIN_PANEL_CONTENT_DISTRIBUTOR_CLASS)
                val listItemsMethod = cardsControllerClass.getDeclaredMethod(GET_LIST_ITEMS)
                    .also { it.isAccessible = true }
                val listItemsMethodQsList = qsListControllerClass.getDeclaredMethod(GET_LIST_ITEMS)
                    .also { it.isAccessible = true }
                val distributePanelsMethod = distributorClass
                    .getDeclaredMethod(DISTRIBUTE_PANELS, Boolean::class.javaPrimitiveType)
                    .also { it.isAccessible = true }
                if (listItemsMethod.parameterTypes.isNotEmpty() ||
                    listItemsMethodQsList.parameterTypes.isNotEmpty() ||
                    distributePanelsMethod.parameterTypes.size != 1
                ) {
                    return null
                }
                val rightPanelContentField = accessibleField(distributorClass, RIGHT_PANEL_CONTENT)
                val rightFooterSpaceField = accessibleField(distributorClass, RIGHT_FOOTER_SPACE)
                val childControllersField = accessibleField(distributorClass, CHILD_CONTROLLERS)
                if (!MutableList::class.java.isAssignableFrom(rightPanelContentField.type) ||
                    !Iterable::class.java.isAssignableFrom(childControllersField.type)
                ) {
                    return null
                }
                TailFeatureSet(
                    listItemsMethod = listItemsMethod,
                    listItemsMethodQsList = listItemsMethodQsList,
                    distributePanelsMethod = distributePanelsMethod,
                    rightPanelContentField = rightPanelContentField,
                    rightFooterSpaceField = rightFooterSpaceField,
                    childControllersField = childControllersField
                )
            } catch (_: Throwable) {
                null
            }
        }

        private fun accessibleField(clazz: Class<*>, name: String): Field {
            val field = clazz.getDeclaredField(name)
            field.isAccessible = true
            return field
        }

        internal fun appendFocusSpec(result: Any?): Any? {
            if (result == null || result !is List<*>) return result
            val specs = ArrayList<String>(result.size + 1)
            for (item in result) {
                val spec = item as? String ?: return result
                specs += spec
            }
            if (!specs.contains(FOCUS_CARD_SPEC)) specs += FOCUS_CARD_SPEC
            return specs
        }

        internal fun filterFocusRecord(items: Any?): Any? {
            if (items !is List<*>) return items
            return try {
                val filtered = ArrayList<Any>(items.size)
                for (item in items) {
                    val record = item ?: return items
                    val spec = recordSpec(record) ?: return items
                    if (spec != FOCUS_CARD_SPEC) filtered += record
                }
                filtered
            } catch (_: Throwable) {
                items
            }
        }

        internal fun insertFocusTail(
            rightPanelContent: MutableList<Any>,
            tailProxy: Any,
            rightFooterSpace: Any?
        ): Boolean {
            val original = rightPanelContent.toList()
            rightPanelContent.removeAll { it === tailProxy }
            val deviceCenterIndex = rightPanelContent.indexOfFirst { item ->
                item.javaClass.simpleName == "DeviceCenterEntryController"
            }
            val qsListIndex = rightPanelContent.indexOfFirst { item ->
                item.javaClass.simpleName == "QSListController"
            }
            val insertIndex = when {
                deviceCenterIndex >= 0 -> deviceCenterIndex + 1
                qsListIndex >= 0 -> qsListIndex
                else -> {
                    val footerIndex = rightFooterSpace?.let { footer ->
                        rightPanelContent.indexOfFirst { it === footer }
                    } ?: -1
                    if (footerIndex >= 0) footerIndex else rightPanelContent.size
                }
            }
            rightPanelContent.add(insertIndex, tailProxy)
            return rightPanelContent.size != original.size ||
                rightPanelContent.indices.any { index -> rightPanelContent[index] !== original[index] }
        }

        internal fun insertFocusTailFromDistributor(distributor: Any?, classLoader: ClassLoader): Boolean {
            if (distributor == null) return false
            val featureSet = validatedTailFeatureSet(classLoader) ?: return false
            return try {
                val rightPanelContent = featureSet.rightPanelContentField.get(distributor) as? MutableList<*>
                    ?: return false
                val rightFooterSpace = featureSet.rightFooterSpaceField.get(distributor)
                val childControllers = featureSet.childControllersField.get(distributor) as? Iterable<*>
                    ?: return false

                if (isInEditMode(distributor)) {
                    val cardsControllerClass = loadClass(classLoader, QS_CARDS_CONTROLLER_CLASS)
                    val cardsController = childControllers.firstOrNull {
                        it != null && it.javaClass == cardsControllerClass
                    } ?: return false
                    val tailContent = focusTailContent(cardsController, classLoader)
                    val tailProxy = tailContent.proxy()
                    @Suppress("UNCHECKED_CAST")
                    (rightPanelContent as MutableList<Any>).removeAll { it === tailProxy }
                    return false
                }

                HyperLog.d(
                    "ControlCenterCardHook",
                    "rightPanelContent structure (" + rightPanelContent.size + " items):"
                )
                rightPanelContent.forEachIndexed { index, item ->
                    val className = item?.javaClass?.simpleName ?: "null"
                    HyperLog.d("ControlCenterCardHook", "  [$index] $className")
                }

                val cardsControllerClass = loadClass(classLoader, QS_CARDS_CONTROLLER_CLASS)
                val cardsController = childControllers.firstOrNull { child ->
                    child != null && child.javaClass == cardsControllerClass
                } ?: return false
                val tailContent = focusTailContent(cardsController, classLoader)
                val tailProxy = tailContent.proxy()
                tailContent.focusRecord() ?: return false

                @Suppress("UNCHECKED_CAST")
                insertFocusTail(rightPanelContent as MutableList<Any>, tailProxy, rightFooterSpace)
            } catch (_: Throwable) {
                false
            }
        }

        internal fun recordSpec(record: Any): String? {
            return runCatching { Reflect.call(record, "getSpec") as? String }.getOrNull()
        }

        internal fun isInEditMode(instance: Any): Boolean {
            return try {
                val modeControllerProvider = findFieldByType(instance.javaClass, "modeController", "mainPanelModeController")
                    ?.let { field ->
                        field.isAccessible = true
                        field.get(instance)
                    }
                val modeController = Reflect.call(modeControllerProvider ?: return false, "get")
                val mode = Reflect.call(modeController ?: return false, "getMode")
                mode.toString() == "EDIT"
            } catch (_: Throwable) {
                false
            }
        }

        private fun findFieldByType(clazz: Class<*>, vararg names: String): Field? {
            var current: Class<*>? = clazz
            while (current != null) {
                for (name in names) {
                    try {
                        return current.getDeclaredField(name)
                    } catch (_: NoSuchFieldException) {
                        continue
                    }
                }
                current = current.superclass
            }
            return null
        }

        internal fun applyFocusCardSizing(controller: Any?): Boolean {
            if (controller == null) return false

            var record: Any? = null
            var tileView: Any? = null
            var recordSetShrinkMethod: Method? = null
            var viewUpdateShrinkMethod: Method? = null
            var viewUpdateBackgroundMethod: Method? = null
            var originalShrink: Boolean? = null
            var recordMutationAttempted = false
            var viewShrinkMutationAttempted = false
            var viewBackgroundMutationAttempted = false

            return try {
                val getTileMethod = findCompatibleMethod(controller, "getTile", FOCUS_CARD_SPEC)
                val resolvedRecord = getTileMethod.invoke(controller, FOCUS_CARD_SPEC) ?: return false
                record = resolvedRecord

                recordSetShrinkMethod = findCompatibleMethod(resolvedRecord, "setShrinkCardStyle", false)
                val recordGetShrinkMethod = findCompatibleMethod(resolvedRecord, "getShrinkCardStyle")
                val recordGetTileViewMethod = findCompatibleMethod(resolvedRecord, "getTileView")
                originalShrink = recordGetShrinkMethod.invoke(resolvedRecord) as? Boolean ?: return false
                tileView = recordGetTileViewMethod.invoke(resolvedRecord)

                val resolvedTileView = tileView
                if (resolvedTileView != null) {
                    viewUpdateShrinkMethod = findCompatibleMethod(resolvedTileView, "updateShrinkCardStyle", false)
                    viewUpdateBackgroundMethod = resolveUpdateBackgroundMethod(resolvedTileView)
                }

                recordMutationAttempted = true
                recordSetShrinkMethod.invoke(resolvedRecord, false)
                if (resolvedTileView == null) return true

                viewShrinkMutationAttempted = true
                viewUpdateShrinkMethod?.invoke(resolvedTileView, false)
                viewBackgroundMutationAttempted = true
                invokeUpdateBackground(viewUpdateBackgroundMethod, resolvedTileView)
                true
            } catch (_: Throwable) {
                restoreFocusCardSizing(
                    record = record,
                    tileView = tileView,
                    recordSetShrinkMethod = recordSetShrinkMethod,
                    viewUpdateShrinkMethod = viewUpdateShrinkMethod,
                    viewUpdateBackgroundMethod = viewUpdateBackgroundMethod,
                    originalShrink = originalShrink,
                    recordMutationAttempted = recordMutationAttempted,
                    viewShrinkMutationAttempted = viewShrinkMutationAttempted,
                    viewBackgroundMutationAttempted = viewBackgroundMutationAttempted
                )
                false
            }
        }

        private fun restoreFocusCardSizing(
            record: Any?,
            tileView: Any?,
            recordSetShrinkMethod: Method?,
            viewUpdateShrinkMethod: Method?,
            viewUpdateBackgroundMethod: Method?,
            originalShrink: Boolean?,
            recordMutationAttempted: Boolean,
            viewShrinkMutationAttempted: Boolean,
            viewBackgroundMutationAttempted: Boolean
        ) {
            if (originalShrink == null) return
            if (recordMutationAttempted && record != null && recordSetShrinkMethod != null) {
                runCatching { recordSetShrinkMethod.invoke(record, originalShrink) }
            }
            if (viewShrinkMutationAttempted && tileView != null && viewUpdateShrinkMethod != null) {
                runCatching { viewUpdateShrinkMethod.invoke(tileView, originalShrink) }
            }
            if (viewBackgroundMutationAttempted && tileView != null && viewUpdateBackgroundMethod != null) {
                runCatching { invokeUpdateBackground(viewUpdateBackgroundMethod, tileView) }
            }
        }

        /**
         * OS3 QSCardItemView.updateBackground(boolean) became updateBackground(boolean, boolean)
         * on OS4. Resolve whichever exists; the returned method is invoked through
         * [invokeUpdateBackground] with the correct arity.
         */
        private fun resolveUpdateBackgroundMethod(tileView: Any): Method? {
            return runCatching { findCompatibleMethod(tileView, "updateBackground", false) }.getOrNull()
                ?: runCatching { findCompatibleMethod(tileView, "updateBackground", false, false) }.getOrNull()
        }

        private fun invokeUpdateBackground(method: Method?, tileView: Any) {
            if (method == null) return
            when (method.parameterTypes.size) {
                1 -> method.invoke(tileView, false)
                2 -> method.invoke(tileView, false, false)
            }
        }

        private fun findCompatibleMethod(instance: Any, name: String, vararg args: Any?): Method {
            val method = findCompatibleMethod(instance.javaClass, name, args)
            method.isAccessible = true
            return method
        }

        private fun findCompatibleMethod(clazz: Class<*>, name: String, args: Array<out Any?>): Method {
            var current: Class<*>? = clazz
            while (current != null) {
                current.declaredMethods
                    .firstOrNull { it.name == name && paramsMatch(it.parameterTypes, args) }
                    ?.let { return it }
                current = current.superclass
            }
            throw NoSuchMethodException(
                clazz.name + "." + name + "(" + args.joinToString { it?.javaClass?.name ?: "null" } + ")"
            )
        }

        private fun paramsMatch(types: Array<Class<*>>, args: Array<out Any?>): Boolean {
            if (types.size != args.size) return false
            return types.indices.all { index ->
                val arg = args[index] ?: return@all !types[index].isPrimitive
                matches(types[index], arg.javaClass)
            }
        }

        private fun matches(param: Class<*>, arg: Class<*>): Boolean {
            if (param.isAssignableFrom(arg)) return true
            if (!param.isPrimitive) return false
            val boxed: Class<*> = when (param) {
                Int::class.javaPrimitiveType -> Int::class.javaObjectType
                Long::class.javaPrimitiveType -> Long::class.javaObjectType
                Boolean::class.javaPrimitiveType -> Boolean::class.javaObjectType
                Double::class.javaPrimitiveType -> Double::class.javaObjectType
                Float::class.javaPrimitiveType -> Float::class.javaObjectType
                Short::class.javaPrimitiveType -> Short::class.javaObjectType
                Byte::class.javaPrimitiveType -> Byte::class.javaObjectType
                Char::class.javaPrimitiveType -> Char::class.javaObjectType
                else -> return false
            }
            return arg == boxed
        }
    }
}
