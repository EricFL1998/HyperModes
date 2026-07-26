package com.banana.hypermodes.hook

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.banana.hypermodes.controlcenter.FocusCardDetailFactory
import com.banana.hypermodes.controlcenter.FocusCardStateRepository
import com.banana.hypermodes.controlcenter.FocusCardTileClasses
import com.banana.hypermodes.controlcenter.FocusCardTileProvider
import com.banana.hypermodes.controlcenter.FocusModeDetailAdapter
import com.banana.hypermodes.controlcenter.GlobalFocusCardConfigStore
import com.banana.hypermodes.controlcenter.ModeIndexSelector
import com.banana.hypermodes.protocol.Protocol
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule
import java.lang.ref.WeakReference
import java.lang.invoke.MethodHandles
import java.lang.reflect.Field
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
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
            Handler()
        )
    }

    fun proxy(): Any = proxy

    fun focusRecord(): Any? {
        return runCatching {
            Reflect.call(delegate, "getTile", ControlCenterCardHook.FOCUS_CARD_SPEC)
        }.getOrNull()
    }

    private inner class Handler : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
            val safeArgs = args ?: emptyArray()
            return when (method.name) {
                "equals" -> proxy === safeArgs.firstOrNull()
                "hashCode" -> System.identityHashCode(proxy)
                "toString" -> "FocusTailContent(proxy=${System.identityHashCode(proxy)})"
                "getListItems" -> focusRecord()?.let { listOf(it) } ?: emptyList<Any>()
                "available" -> {
                    val delegateAvailable = callDelegate(method, safeArgs) as? Boolean == true
                    val recordAvailable = focusRecord() != null
                    delegateAvailable && recordAvailable
                }
                "getRightOrLeft" -> true
                "getPriority" -> Int.MAX_VALUE - 1
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
            return MethodHandles.privateLookupIn(method.declaringClass, MethodHandles.lookup())
                .unreflectSpecial(method, method.declaringClass)
                .bindTo(delegate)
                .invokeWithArguments(*args)
        }
    }

    private fun defaultValue(returnType: Class<*>): Any? {
        return when (returnType) {
            java.lang.Void.TYPE -> null
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

class ControlCenterCardHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        if (!markInstalling(classLoader)) return
        log("plugin ClassLoader accepted: $classLoader")

        try {
            val controllerClass = Reflect.findClass(QS_CONTROLLER_CLASS, classLoader)
            val specsMethod = controllerClass.getDeclaredMethod(GET_CARD_STYLE_TILE_SPECS)
            val createTileMethod = controllerClass.getDeclaredMethod(CREATE_TILE, String::class.java)
            val cardsControllerClass = Reflect.findClass(QS_CARDS_CONTROLLER_CLASS, classLoader)
            val preparePanelUpdateMethod = cardsControllerClass.getDeclaredMethod(PREPARE_PANEL_UPDATE)
            val tailFeatureSet = validatedTailFeatureSet(classLoader)

            if (specsMethod.parameterTypes.isNotEmpty() ||
                createTileMethod.parameterTypes.size != 1 ||
                preparePanelUpdateMethod.parameterTypes.isNotEmpty()
            ) {
                throw NoSuchMethodException(
                    "Control Center method signature mismatch: " +
                        "$GET_CARD_STYLE_TILE_SPECS(${specsMethod.parameterTypes.size}), " +
                        "$CREATE_TILE(${createTileMethod.parameterTypes.size}), " +
                        "$PREPARE_PANEL_UPDATE(${preparePanelUpdateMethod.parameterTypes.size})"
                )
            }

            log("Control Center card hooks installed for plugin ClassLoader=$classLoader")
            hookSpecs(specsMethod)
            hookCreateTile(createTileMethod, classLoader)
            hookPreparePanelUpdate(preparePanelUpdateMethod)
            if (tailFeatureSet == null) {
                log("Focus tail feature set unavailable; preserving native Focus card placement")
            } else {
                hookListItems(tailFeatureSet.listItemsMethod)
                hookDistributePanels(tailFeatureSet.distributePanelsMethod, classLoader)
            }
        } catch (t: Throwable) {
            unmarkInstalling(classLoader)
            log("compatibility failure for plugin ClassLoader=$classLoader", t)
        }
    }

    private fun hookSpecs(method: Method) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    return try {
                        val appended = appendFocusSpec(original)
                        if (original is List<*> && appended is List<*> && appended !== original &&
                            !original.contains(FOCUS_CARD_SPEC) && appendShapeLogged.compareAndSet(false, true)
                        ) {
                            log("Focus spec appended for list size=${original.size}, type=${original.javaClass.name}")
                        }
                        appended
                    } catch (t: Throwable) {
                        log("failed to append Focus spec in $QS_CONTROLLER_CLASS.$GET_CARD_STYLE_TILE_SPECS", t)
                        original
                    }
                }
            })
    }

    private fun hookCreateTile(method: Method, classLoader: ClassLoader) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val spec = chain.args.firstOrNull() as? String
                    if (spec != FOCUS_CARD_SPEC) return chain.proceed()

                    log("Focus tile creation requested")
                    return try {
                        val tile = createFocusTile(chain.thisObject, classLoader)
                        log("Focus tile creation succeeded")
                        tile
                    } catch (t: Throwable) {
                        logFocusCreationFailure(chain.thisObject, classLoader, t)
                        null
                    }
                }
            })
    }

    private fun hookPreparePanelUpdate(method: Method) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    if (applyFocusCardSizing(chain.thisObject) && focusSizingLogged.compareAndSet(false, true)) {
                        log("Focus card horizontal sizing applied")
                    }
                    return result
                }
            })
    }

    private fun hookListItems(method: Method) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val original = chain.proceed()
                    return try {
                        filterFocusRecord(original)
                    } catch (t: Throwable) {
                        log("failed to filter Focus record in $QS_CARDS_CONTROLLER_CLASS.$GET_LIST_ITEMS", t)
                        original
                    }
                }
            })
    }

    private fun hookDistributePanels(method: Method, classLoader: ClassLoader) {
        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    val result = chain.proceed()
                    try {
                        if (insertFocusTailFromDistributor(chain.thisObject, classLoader) &&
                            focusTailInsertionLogged.compareAndSet(false, true)
                        ) {
                            log("Focus tail inserted before right footer spacing")
                        }
                    } catch (t: Throwable) {
                        log("failed to insert Focus tail in $MAIN_PANEL_CONTENT_DISTRIBUTOR_CLASS.$DISTRIBUTE_PANELS", t)
                    }
                    return result
                }
            })
    }

    private fun createFocusTile(controller: Any?, classLoader: ClassLoader): Any {
        val pluginContext = controller?.let { Reflect.call(it, "getContext") as? Context }
            ?: throw IllegalStateException("QSController.getContext() did not return Context")
        val moduleContext = pluginContext.createPackageContext(
            Protocol.MODULE_PACKAGE,
            Context.CONTEXT_IGNORE_SECURITY or Context.CONTEXT_INCLUDE_CODE
        )
        val classes = FocusCardTileClasses.resolve(classLoader)
        val handler = Handler(Looper.getMainLooper())
        val store = GlobalFocusCardConfigStore(moduleContext, handler)
        val repository = FocusCardStateRepository(
            store = store,
            selector = ModeIndexSelector { size -> Random.nextInt(size) }
        )
        val detailFactory = FocusCardDetailFactory { onDismiss, onStateRefresh ->
            FocusModeDetailAdapter(
                pluginContext = pluginContext,
                moduleContext = moduleContext,
                detailAdapterInterface = classes.detailAdapterInterface,
                repository = repository,
                onDismiss = onDismiss,
                onStateRefresh = onStateRefresh,
                nativeDetailContentApi = classes.nativeDetailContentApi
            ).adapter
        }
        val tile = FocusCardTileProvider(
            pluginContext = pluginContext,
            moduleContext = moduleContext,
            classes = classes,
            repository = repository,
            observableStore = store,
            detailFactory = detailFactory,
            postToUi = { action -> handler.post(action) }
        ).create()
        return initializeFocusTile(tile, currentUserId())
            ?: throw IllegalStateException("Focus tile initialization failed")
    }

    private fun logFocusCreationFailure(controller: Any?, classLoader: ClassLoader, throwable: Throwable) {
        val pluginInfo = runCatching {
            val context = controller?.let { Reflect.call(it, "getContext") as? Context }
            if (context == null) {
                "context=<unavailable>"
            } else {
                val packageName = context.packageName ?: "<unknown>"
                val packageInfo = context.packageManager?.getPackageInfo(packageName, 0)
                val versionCode = packageInfo?.let { info ->
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                        info.longVersionCode
                    } else {
                        @Suppress("DEPRECATION")
                        info.versionCode.toLong()
                    }
                }
                "package=$packageName versionName=${packageInfo?.versionName} versionCode=$versionCode"
            }
        }.getOrElse {
            "context=<error:${it.javaClass.simpleName}>"
        }
        log(
            "Focus tile creation failed in $QS_CONTROLLER_CLASS.$CREATE_TILE(String), " +
                "pluginClassLoader=$classLoader, $pluginInfo",
            throwable
        )
    }

    private fun markInstalling(classLoader: ClassLoader): Boolean = synchronized(installLock) {
        pruneInstalledLoadersLocked()
        if (installedLoaders.any { it.get() === classLoader }) return false
        installedLoaders += WeakReference(classLoader)
        true
    }

    private fun unmarkInstalling(classLoader: ClassLoader) = synchronized(installLock) {
        installedLoaders.removeAll { it.get().let { installed -> installed == null || installed === classLoader } }
    }

    private fun pruneInstalledLoadersLocked() {
        installedLoaders.removeAll { it.get() == null }
    }

    private fun log(message: String, throwable: Throwable? = null) {
        val detail = if (throwable == null) message else "$message: ${Log.getStackTraceString(throwable)}"
        module.log(Log.WARN, TAG, detail)
    }

    companion object {
        const val FOCUS_CARD_SPEC = "hypermodes_focus"

        private const val TAG = "HyperModes.ControlCenterCardHook"
        private const val QS_CONTROLLER_CLASS = "miui.systemui.controlcenter.qs.QSController"
        private const val QS_CARDS_CONTROLLER_CLASS = "miui.systemui.controlcenter.panel.main.qs.QSCardsController"
        private const val MAIN_PANEL_CONTENT_CLASS = "miui.systemui.controlcenter.panel.main.MainPanelContent"
        private const val MAIN_PANEL_CONTENT_DISTRIBUTOR_CLASS =
            "miui.systemui.controlcenter.panel.main.MainPanelContentDistributor"
        private const val GET_CARD_STYLE_TILE_SPECS = "getCardStyleTileSpecs"
        private const val CREATE_TILE = "createTile"
        private const val PREPARE_PANEL_UPDATE = "preparePanelUpdate"
        private const val GET_LIST_ITEMS = "getListItems"
        private const val DISTRIBUTE_PANELS = "distributePanels"
        private const val RIGHT_PANEL_CONTENT = "rightPanelContent"
        private const val RIGHT_FOOTER_SPACE = "rightFooterSpace"
        private const val CHILD_CONTROLLERS = "childControllers"

        private val installLock = Any()
        private val installedLoaders = mutableListOf<WeakReference<ClassLoader>>()
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

        internal fun focusTailContent(
            delegate: Any,
            pluginClassLoader: ClassLoader
        ): FocusTailContent {
            val mainPanelContentInterface = Reflect.findClass(MAIN_PANEL_CONTENT_CLASS, pluginClassLoader)
            return focusTailContent(delegate, mainPanelContentInterface)
        }

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
            val distributePanelsMethod: Method,
            val rightPanelContentField: Field,
            val rightFooterSpaceField: Field,
            val childControllersField: Field
        )

        internal fun validatedTailFeatureSet(classLoader: ClassLoader): TailFeatureSet? {
            return try {
                val cardsControllerClass = Reflect.findClass(QS_CARDS_CONTROLLER_CLASS, classLoader)
                val distributorClass = Reflect.findClass(MAIN_PANEL_CONTENT_DISTRIBUTOR_CLASS, classLoader)
                val listItemsMethod = cardsControllerClass.getDeclaredMethod(GET_LIST_ITEMS).also { it.isAccessible = true }
                val distributePanelsMethod = distributorClass
                    .getDeclaredMethod(DISTRIBUTE_PANELS, Boolean::class.javaPrimitiveType)
                    .also { it.isAccessible = true }
                if (listItemsMethod.parameterTypes.isNotEmpty() || distributePanelsMethod.parameterTypes.size != 1) {
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

            // Find DeviceCenterEntryController (priority=50) and insert AFTER it
            // If not found, fall back to before FooterSpaceController
            val deviceCenterIndex = rightPanelContent.indexOfFirst { item ->
                item.javaClass.simpleName == "DeviceCenterEntryController"
            }

            val insertIndex = if (deviceCenterIndex >= 0) {
                // Found device center: insert right after it
                deviceCenterIndex + 1
            } else {
                // Fallback: insert before footer
                val footerIndex = rightFooterSpace?.let { footer ->
                    rightPanelContent.indexOfFirst { it === footer }
                } ?: -1
                if (footerIndex >= 0) footerIndex else rightPanelContent.size
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

                // Diagnostic: log panel structure
                Log.d("ControlCenterCardHook", "rightPanelContent structure (${rightPanelContent.size} items):")
                rightPanelContent.forEachIndexed { index, item ->
                    val isFooter = item === rightFooterSpace
                    val className = item?.javaClass?.simpleName ?: "null"
                    val priority = item?.let { runCatching { Reflect.call(it, "getPriority") }.getOrNull() }
                    Log.d("ControlCenterCardHook", "  [$index] $className (priority=$priority)${if (isFooter) " <- FOOTER" else ""}")
                }

                val cardsControllerClass = Reflect.findClass(QS_CARDS_CONTROLLER_CLASS, classLoader)
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

        internal fun initializeFocusTile(tile: Any, userId: Int): Any? {
            return try {
                Reflect.call(tile, "setTileSpec", FOCUS_CARD_SPEC)
                Reflect.call(tile, "userSwitch", userId)
                Reflect.call(tile, "refreshState")
                tile
            } catch (_: Throwable) {
                runCatching { Reflect.call(tile, "destroy") }
                null
            }
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
                    viewUpdateBackgroundMethod = findCompatibleMethod(resolvedTileView, "updateBackground", false)
                }

                recordMutationAttempted = true
                recordSetShrinkMethod.invoke(resolvedRecord, false)
                if (resolvedTileView == null) return true

                viewShrinkMutationAttempted = true
                viewUpdateShrinkMethod?.invoke(resolvedTileView, false)
                viewBackgroundMutationAttempted = true
                viewUpdateBackgroundMethod?.invoke(resolvedTileView, false)
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
                runCatching { viewUpdateBackgroundMethod.invoke(tileView, false) }
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
                "${clazz.name}.$name(${args.joinToString { it?.javaClass?.name ?: "null" }})"
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

        private fun currentUserId(): Int {
            return Reflect.callStatic(ActivityManager::class.java, "getCurrentUser") as? Int ?: 0
        }
    }
}
