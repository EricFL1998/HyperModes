package com.banana.hypermodes.controlcenter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import com.banana.hypermodes.R
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.protocol.Protocol
import java.lang.reflect.Array as ReflectArray
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Modifier
import java.lang.reflect.Proxy

private const val DETAIL_METRICS_CATEGORY = 118
private const val NATIVE_DETAIL_SUFFIX = "HyperModesFocus"

internal data class FocusModeRowDescriptor(
    val id: String,
    val title: String,
    val status: String,
    val contentDescription: String,
    val iconResId: Int,
    val selected: Boolean
)

internal enum class FocusDetailFallbackStage {
    NATIVE_API_UNAVAILABLE,
    NATIVE_CONVERT,
    NATIVE_ITEMS,
    NATIVE_CALLBACK,
    MANUAL_BUILD,
    SAFE_BUILD
}

internal fun interface FocusDetailDiagnostic {
    fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?)
}

internal object FocusDetailLogDiagnostic : FocusDetailDiagnostic {
    private const val TAG = "FocusModeDetail"

    override fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?) {
        val detail = throwable?.let { error ->
            val message = error.message?.takeIf { it.isNotBlank() }
            if (message == null) error.javaClass.name else "${error.javaClass.name}: $message"
        } ?: "no exception"
        Log.w(TAG, "Detail fallback at $stage: $detail")
    }
}

internal sealed class FocusDetailBuildResult<out T> {
    data class Success<T>(val view: T) : FocusDetailBuildResult<T>()
    data class Failed(val stage: FocusDetailFallbackStage, val throwable: Throwable?) : FocusDetailBuildResult<Nothing>()
}

internal class FocusDetailViewCoordinator<T>(
    private val nativeBuilder: (() -> FocusDetailBuildResult<T>?)?,
    private val manualBuilder: () -> T,
    private val safeBuilder: () -> T,
    private val lastResortBuilder: () -> T?,
    private val diagnostic: FocusDetailDiagnostic
) {
    fun createDetailView(): T? {
        val nativeResult = if (nativeBuilder == null) {
            diagnose(FocusDetailFallbackStage.NATIVE_API_UNAVAILABLE, null)
            null
        } else {
            try {
                nativeBuilder.invoke()
            } catch (throwable: Throwable) {
                FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CONVERT, throwable)
            }
        }
        when (nativeResult) {
            is FocusDetailBuildResult.Success -> return nativeResult.view
            is FocusDetailBuildResult.Failed -> diagnose(nativeResult.stage, nativeResult.throwable)
            null -> Unit
        }

        return try {
            manualBuilder()
        } catch (manualThrowable: Throwable) {
            diagnose(FocusDetailFallbackStage.MANUAL_BUILD, manualThrowable)
            try {
                safeBuilder()
            } catch (safeThrowable: Throwable) {
                diagnose(FocusDetailFallbackStage.SAFE_BUILD, safeThrowable)
                lastResortBuilder()
            }
        }
    }

    private fun diagnose(stage: FocusDetailFallbackStage, throwable: Throwable?) {
        runCatching { diagnostic.failed(stage, throwable) }
    }
}

internal object FocusModeManualViewClasses {
    val productionClasses: List<Class<out View>> = listOf(
        ScrollView::class.java,
        LinearLayout::class.java,
        ImageView::class.java
    )
}

internal fun buildFocusModeRows(snapshot: FocusCardSnapshot): List<FocusModeRowDescriptor> {
    return snapshot.modes.map { mode ->
        val selected = snapshot.activeModeId == mode.id
        val title = mode.name.ifBlank { "Focus mode" }
        val status = if (selected) "On" else "Off"
        FocusModeRowDescriptor(
            id = mode.id,
            title = title,
            status = status,
            contentDescription = "$title, $status",
            iconResId = modeIconResId(mode.icon),
            selected = selected
        )
    }
}

private fun modeIconResId(modeIcon: String?): Int {
    return try {
        ModeIconMapper.getStatusBarIconRes(modeIcon ?: "")
    } catch (_: Throwable) {
        R.drawable.ic_stat_zen
    }
}

interface FocusModeActivator {
    fun activate(modeId: String): Boolean
}

private class RepositoryFocusModeActivator(
    private val repository: FocusCardStateRepository
) : FocusModeActivator {
    override fun activate(modeId: String): Boolean = repository.activate(modeId)
}

class FocusModeSelectionController(
    private val repository: FocusModeActivator,
    private val dismiss: () -> Unit,
    private val refreshState: () -> Unit
) {
    private var terminal = false

    fun select(modeId: String) {
        if (terminal) return
        terminal = true
        try {
            runCatching { repository.activate(modeId) }
            refreshState()
        } finally {
            dismiss()
        }
    }
}

class FocusModeDetailAdapter(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val detailAdapterInterface: Class<*>,
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val onStateRefresh: () -> Unit = {},
    private val nativeDetailContentApi: FocusNativeDetailContentApi? = FocusNativeDetailContentResolver.fromClassLoader(
        detailAdapterInterface.classLoader
    )
) {
    private var diagnostic: FocusDetailDiagnostic = FocusDetailLogDiagnostic
    private var safeViewFactory: (Context) -> View = { context -> View(context) }

    internal constructor(
        pluginContext: Context,
        moduleContext: Context,
        detailAdapterInterface: Class<*>,
        repository: FocusCardStateRepository,
        onDismiss: () -> Unit,
        onStateRefresh: () -> Unit,
        nativeDetailContentApi: FocusNativeDetailContentApi?,
        diagnostic: FocusDetailDiagnostic,
        safeViewFactory: (Context) -> View = { context -> View(context) }
    ) : this(
        pluginContext = pluginContext,
        moduleContext = moduleContext,
        detailAdapterInterface = detailAdapterInterface,
        repository = repository,
        onDismiss = onDismiss,
        onStateRefresh = onStateRefresh,
        nativeDetailContentApi = nativeDetailContentApi
    ) {
        this.diagnostic = diagnostic
        this.safeViewFactory = safeViewFactory
    }

    fun create(): Any {
        return Proxy.newProxyInstance(
            detailAdapterInterface.classLoader,
            arrayOf(detailAdapterInterface),
            DetailInvocationHandler(
                pluginContext = pluginContext,
                moduleContext = moduleContext,
                detailAdapterInterface = detailAdapterInterface,
                repository = repository,
                onDismiss = onDismiss,
                onStateRefresh = onStateRefresh,
                nativeDetailContentApi = nativeDetailContentApi,
                diagnostic = diagnostic,
                safeViewFactory = safeViewFactory
            )
        )
    }
}

data class FocusNativeDetailContentApi(
    val contentClass: Class<*>,
    val itemInterface: Class<*>,
    val selectableItemClass: Class<*>,
    val callbackInterface: Class<*>,
    val convertOrInflate: FocusNativeConvertOrInflate,
    val selectableItemConstructor: Constructor<*>,
    val setSuffix: Method,
    val setItems: Method,
    val setCallback: Method
)

class FocusNativeConvertOrInflate(
    private val ownerProvider: () -> Any?,
    private val method: Method
) {
    fun invoke(context: Context, convertView: View?, parent: ViewGroup?): Any? {
        return method.invoke(ownerProvider(), context, convertView, parent)
    }
}

internal object FocusNativeDetailContentResolver {
    fun fromClassLoader(classLoader: ClassLoader?): FocusNativeDetailContentApi? {
        if (classLoader == null) return null
        return runCatching {
            fromContentClass(classLoader.loadClass("com.android.systemui.qs.QSDetailContent"))
        }.getOrNull()
    }

    fun fromContentClass(contentClass: Class<*>): FocusNativeDetailContentApi? {
        return runCatching {
            val itemInterface = nestedClass(contentClass, "Item")
            val selectableItemClass = nestedClass(contentClass, "SelectableItem")
            val callbackInterface = nestedClass(contentClass, "Callback")
            val setItems = findMethod(contentClass, "setItems") { method ->
                method.parameterTypes.size == 1 &&
                    method.parameterTypes[0].isArray &&
                    method.parameterTypes[0].componentType?.isAssignableFrom(itemInterface) == true
            }
            val constructor = selectableItemClass.declaredConstructors.firstOrNull { constructor ->
                constructor.parameterTypes.isEmpty() ||
                    (constructor.parameterTypes.size == 1 && constructor.parameterTypes[0].isAssignableFrom(contentClass))
            } ?: throw NoSuchMethodException("${selectableItemClass.name} constructor for ${contentClass.name}")

            constructor.isAccessible = true
            FocusNativeDetailContentApi(
                contentClass = contentClass,
                itemInterface = itemInterface,
                selectableItemClass = selectableItemClass,
                callbackInterface = callbackInterface,
                convertOrInflate = findConvertOrInflate(contentClass),
                selectableItemConstructor = constructor,
                setSuffix = findMethod(contentClass, "setSuffix") { method ->
                    method.parameterTypes.contentEquals(arrayOf(String::class.java))
                },
                setItems = setItems,
                setCallback = findMethod(contentClass, "setCallback") { method ->
                    method.parameterTypes.size == 1 && method.parameterTypes[0].isAssignableFrom(callbackInterface)
                }
            )
        }.getOrNull()
    }

    private fun nestedClass(contentClass: Class<*>, simpleName: String): Class<*> {
        return contentClass.declaredClasses.firstOrNull { it.simpleName == simpleName }
            ?: throw ClassNotFoundException("${contentClass.name}\$$simpleName")
    }

    private fun findConvertOrInflate(contentClass: Class<*>): FocusNativeConvertOrInflate {
        findMethodOrNull(contentClass, "convertOrInflate", ::isConvertOrInflateMethod)?.let { method ->
            val owner = if (Modifier.isStatic(method.modifiers)) ({ null }) else ({ contentClass.getDeclaredConstructor().newInstance() })
            return FocusNativeConvertOrInflate(ownerProvider = owner, method = method)
        }

        val companionClass = contentClass.declaredClasses.firstOrNull { it.simpleName == "Companion" }
            ?: throw NoSuchMethodException("${contentClass.name}.Companion.convertOrInflate")
        val companionField = contentClass.declaredFields.firstOrNull { it.name == "Companion" }?.apply {
            isAccessible = true
        }
        val method = findMethod(companionClass, "convertOrInflate", ::isConvertOrInflateMethod)
        return FocusNativeConvertOrInflate(
            ownerProvider = {
                if (Modifier.isStatic(method.modifiers)) {
                    null
                } else {
                    companionField?.get(null)
                        ?: throw NoSuchFieldException("${contentClass.name}.Companion")
                }
            },
            method = method
        )
    }

    private fun isConvertOrInflateMethod(method: Method): Boolean {
        return method.parameterTypes.size == 3 &&
            Context::class.java.isAssignableFrom(method.parameterTypes[0]) &&
            View::class.java.isAssignableFrom(method.parameterTypes[1]) &&
            ViewGroup::class.java.isAssignableFrom(method.parameterTypes[2])
    }

    private fun findMethod(clazz: Class<*>, name: String, predicate: (Method) -> Boolean): Method {
        return findMethodOrNull(clazz, name, predicate)
            ?: throw NoSuchMethodException("${clazz.name}.$name")
    }

    private fun findMethodOrNull(clazz: Class<*>, name: String, predicate: (Method) -> Boolean): Method? {
        var current: Class<*>? = clazz
        while (current != null) {
            current.declaredMethods.firstOrNull { it.name == name && predicate(it) }?.let { method ->
                method.isAccessible = true
                return method
            }
            current = current.superclass
        }
        clazz.methods.firstOrNull { it.name == name && predicate(it) }?.let { method ->
            method.isAccessible = true
            return method
        }
        return null
    }
}

internal object FocusModeDetailViewContextSelector {
    fun select(hostContext: Any?, fallbackPluginContext: Context): Context {
        return hostContext as? Context ?: fallbackPluginContext
    }
}

internal object FocusModeAppLauncher {
    fun launchMainActivity(
        parentContext: Context?,
        rowContext: Context,
        pluginContext: Context,
        moduleContext: Context
    ): Boolean {
        return listOfNotNull(parentContext, rowContext, pluginContext, moduleContext).any { context ->
            runCatching {
                context.startActivity(mainActivityIntent())
            }.isSuccess
        }
    }

    private fun mainActivityIntent(): Intent {
        return Intent().apply {
            setClassName(Protocol.MODULE_PACKAGE, "com.banana.hypermodes.ui.MainActivity")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}

private class DetailInvocationHandler(
    private val pluginContext: Context,
    private val moduleContext: Context,
    private val detailAdapterInterface: Class<*>,
    private val repository: FocusCardStateRepository,
    private val onDismiss: () -> Unit,
    private val onStateRefresh: () -> Unit,
    private val nativeDetailContentApi: FocusNativeDetailContentApi?,
    private val diagnostic: FocusDetailDiagnostic,
    private val safeViewFactory: (Context) -> View
) : InvocationHandler {
    private val invalidEvent: Any? = reflectInvalidDetailEvent(detailAdapterInterface)
    private val selectionController = FocusModeSelectionController(
        repository = RepositoryFocusModeActivator(repository),
        dismiss = onDismiss,
        refreshState = onStateRefresh
    )
    private val nativeBuilder = nativeDetailContentApi?.let {
        NativeFocusModeDetailContentBuilder(
            repository = repository,
            selectionController = selectionController,
            api = it,
            rowLocalizer = ::localizedRow,
            drawableLoader = ::loadDrawable
        )
    }

    override fun invoke(proxy: Any, method: Method, args: Array<out Any?>?): Any? {
        val arguments = args ?: emptyArray()
        return when (method.name) {
            "equals" -> proxy === arguments.firstOrNull()
            "hashCode" -> System.identityHashCode(proxy)
            "toString" -> "FocusModeDetailAdapterProxy@${Integer.toHexString(System.identityHashCode(proxy))}"
            "getTitle" -> stringFromPlugin(R.string.focus_card_title, "Focus modes")
            "getToggleVisible" -> false
            "getToggleState" -> null
            "setToggleState" -> null
            "getToggleEnabled" -> false
            "getMetricsCategory" -> DETAIL_METRICS_CATEGORY
            "getSettingsIntent" -> null
            "openDetailEvent", "closeDetailEvent", "moreSettingsEvent" -> invalidEvent
            "createDetailView" -> createDetailView(arguments)
            "shouldAnimate" -> true
            "hasHeader" -> false
            "getContainerHeight" -> -1
            else -> defaultValue(method.returnType)
        }
    }

    private fun createDetailView(arguments: Array<out Any?>): View? {
        val context = FocusModeDetailViewContextSelector.select(arguments.getOrNull(0), pluginContext)
        val convertView = arguments.getOrNull(1) as? View
        val parent = arguments.getOrNull(2) as? ViewGroup
        return FocusDetailViewCoordinator(
            nativeBuilder = nativeBuilder?.let { builder ->
                {
                    builder.build(
                        context = context,
                        convertView = convertView,
                        parent = parent
                    )
                }
            },
            manualBuilder = {
                buildModeListView(
                    context = context,
                    convertView = convertView,
                    parent = parent
                )
            },
            safeBuilder = { safeViewFactory(context) },
            lastResortBuilder = { existingDetailViewOrFinalFallback(convertView, parent) },
            diagnostic = diagnostic
        ).createDetailView()
    }

    private fun existingDetailViewOrFinalFallback(convertView: View?, parent: ViewGroup?): View? {
        convertView?.let { return it }
        parent?.let { return it }
        // No existing View reference is available and both manual and safe View construction failed.
        // Returning null contains the failure at this invocation boundary instead of crashing SystemUI.
        return null
    }

    private fun buildModeListView(context: Context, convertView: View?, parent: ViewGroup?): View {
        val scrollView = convertView as? ScrollView ?: ScrollView(context)
        scrollView.removeAllViews()
        scrollView.isFillViewport = false

        val list = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            // Restored padding for better centering after removing header
            setPadding(dp(context, 16), dp(context, 16), dp(context, 16), dp(context, 16))
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        scrollView.addView(
            list,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val snapshot = repository.loadOrInitialize()
        val rows = buildFocusModeRows(snapshot)
        if (rows.isEmpty()) {
            addEmptyState(context, list, parent)
        } else {
            rows.forEach { row ->
                list.addView(
                    createModeRow(
                        context = context,
                        descriptor = localizedRow(row),
                        parent = parent
                    )
                )
            }
        }
        return scrollView
    }

    private fun createModeRow(
        context: Context,
        descriptor: FocusModeRowDescriptor,
        parent: ViewGroup?
    ): View {
        val row = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            isClickable = true
            isFocusable = true
            isSelected = descriptor.selected
            isActivated = descriptor.selected
            tag = "focus-mode-row-${descriptor.id}"
            setPadding(dp(context, 16), dp(context, 14), dp(context, 16), dp(context, 14))
            setSelectableItemBackground(context)
            contentDescription = descriptor.contentDescription
            setOnClickListener {
                selectionController.select(descriptor.id)
            }
        }

        val indicator = View(context).apply {
            visibility = if (descriptor.selected) View.VISIBLE else View.INVISIBLE
            setBackgroundColor(resolveColor(context, android.R.attr.colorAccent, 0xFF3F7EFF.toInt()))
        }
        row.addView(
            indicator,
            LinearLayout.LayoutParams(dp(context, 3), ViewGroup.LayoutParams.MATCH_PARENT).apply {
                marginEnd = dp(context, 12)
            }
        )

        val icon = ImageView(context).apply {
            setImageDrawable(loadDrawable(descriptor.iconResId))
            isActivated = descriptor.selected
            isSelected = descriptor.selected
            contentDescription = null
        }
        row.addView(
            icon,
            LinearLayout.LayoutParams(dp(context, 28), dp(context, 28)).apply {
                marginEnd = dp(context, 12)
            }
        )

        val textContainer = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val title = TextView(context).apply {
            text = descriptor.title
            textSize = 16f
            setTextColor(0xFFFFFFFF.toInt())
            if (descriptor.selected) setTypeface(typeface, Typeface.BOLD)
        }
        textContainer.addView(title)

        // Only show status if it's meaningful (not "false" or empty)
        if (descriptor.status.isNotBlank() && descriptor.status != "false" && descriptor.status != "Off") {
            val subtitle = TextView(context).apply {
                text = descriptor.status
                textSize = 13f
                setTextColor(0xCCFFFFFF.toInt())
            }
            textContainer.addView(subtitle)
        }
        row.addView(
            textContainer,
            LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
        )

        row.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply {
            bottomMargin = dp(parent?.context ?: context, 4)
        }
        return row
    }

    private fun localizedRow(row: FocusModeRowDescriptor): FocusModeRowDescriptor {
        // Only translate the three default modes; keep custom mode names as-is
        val localizedTitle = when (row.title) {
            "Do Not Disturb" -> stringFromPlugin(R.string.mode_dnd, "勿扰模式")
            "Bedtime" -> stringFromPlugin(R.string.mode_bedtime, "睡眠模式")
            "Driving" -> stringFromPlugin(R.string.mode_driving, "行驶模式")
            else -> row.title.ifBlank { stringFromPlugin(R.string.focus_card_fallback, "Focus mode") }
        }

        // No status display at all
        return row.copy(
            title = localizedTitle,
            status = "",
            contentDescription = localizedTitle
        )
    }

    private fun addEmptyState(context: Context, list: LinearLayout, parent: ViewGroup?) {
        val emptyText = TextView(context).apply {
            text = stringFromPlugin(R.string.focus_card_empty, "No modes configured")
            gravity = Gravity.CENTER
            setPadding(dp(context, 16), dp(context, 24), dp(context, 16), dp(context, 12))
        }
        list.addView(
            emptyText,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )

        val button = Button(context).apply {
            text = stringFromPlugin(R.string.focus_card_open_app, "Open HyperModes")
            tag = "focus-mode-open-app"
            setOnClickListener {
                FocusModeAppLauncher.launchMainActivity(
                    parentContext = parent?.context,
                    rowContext = context,
                    pluginContext = pluginContext,
                    moduleContext = moduleContext
                )
            }
        }
        list.addView(
            button,
            LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
                topMargin = dp(context, 4)
            }
        )
    }

    private fun loadDrawable(iconResId: Int): Drawable {
        return drawableFromContexts(iconResId)
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

    private fun activeStateText(active: Boolean): String {
        return if (active) {
            stringFromPlugin(R.string.focus_card_active, "On")
        } else {
            stringFromPlugin(R.string.focus_card_inactive, "Off")
        }
    }

    private fun stringFromPlugin(id: Int, fallback: String): String {
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

    private fun View.setSelectableItemBackground(context: Context) {
        val out = android.util.TypedValue()
        val resolved = try {
            context.theme?.resolveAttribute(android.R.attr.selectableItemBackground, out, true) == true
        } catch (_: Throwable) {
            false
        }
        if (resolved && out.resourceId != 0) {
            try {
                setBackgroundResource(out.resourceId)
            } catch (_: Throwable) {
                // Leave the view without press feedback when the host theme cannot resolve it.
            }
        }
    }

    private fun resolveColor(context: Context, attr: Int, fallback: Int): Int {
        val out = android.util.TypedValue()
        return try {
            if (context.theme?.resolveAttribute(attr, out, true) == true) out.data else fallback
        } catch (_: Throwable) {
            fallback
        }
    }

    private fun dp(context: Context, value: Int): Int {
        val density = try {
            context.resources?.displayMetrics?.density ?: 1f
        } catch (_: Throwable) {
            1f
        }
        return (value * density + 0.5f).toInt()
    }

    private fun defaultValue(type: Class<*>): Any? = defaultReturnValue(type)
}

private class NativeFocusModeDetailContentBuilder(
    private val repository: FocusCardStateRepository,
    private val selectionController: FocusModeSelectionController,
    private val api: FocusNativeDetailContentApi,
    private val rowLocalizer: (FocusModeRowDescriptor) -> FocusModeRowDescriptor,
    private val drawableLoader: (Int) -> Drawable
) {
    fun build(context: Context, convertView: View?, parent: ViewGroup?): FocusDetailBuildResult<View>? {
        val rows = buildFocusModeRows(repository.loadOrInitialize()).map(rowLocalizer)
        if (rows.isEmpty()) return null

        val content = try {
            api.convertOrInflate.invoke(context, convertView, parent)
        } catch (throwable: Throwable) {
            return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CONVERT, throwable)
        } ?: return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CONVERT, null)
        if (!api.contentClass.isInstance(content)) {
            return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CONVERT, null)
        }
        val view = content as? View
            ?: return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CONVERT, null)

        try {
            api.setSuffix.invoke(content, NATIVE_DETAIL_SUFFIX)
            val itemArrayType = api.setItems.parameterTypes[0].componentType
                ?: return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_ITEMS, null)
            val itemArray = ReflectArray.newInstance(itemArrayType, rows.size)
            rows.forEachIndexed { index, row ->
                ReflectArray.set(
                    itemArray,
                    index,
                    createSelectableItem(
                        content = content,
                        row = row
                    )
                )
            }
            api.setItems.invoke(content, itemArray)
        } catch (throwable: Throwable) {
            return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_ITEMS, throwable)
        }

        try {
            api.setCallback.invoke(content, createCallback())
        } catch (throwable: Throwable) {
            return FocusDetailBuildResult.Failed(FocusDetailFallbackStage.NATIVE_CALLBACK, throwable)
        }

        return FocusDetailBuildResult.Success(view)
    }

    private fun createSelectableItem(content: Any, row: FocusModeRowDescriptor): Any {
        val item = instantiateSelectableItem(content)
        setField(item, "tag", row.id)
        setField(item, "title", row.title)
        setField(item, "summary", row.status)
        setField(item, "secondarySummary", null)
        setField(item, "contentDescription", row.contentDescription)
        setField(item, "selectable", true)
        setField(item, "selected", row.selected)
        setField(item, "isForceSingle", true)
        setField(item, "clickToDisconnect", false)
        setField(item, "iconRes", 0)
        setField(item, "icon2Res", 0)
        setField(item, "iconDrawable", drawableLoader(row.iconResId))
        return item
    }

    private fun instantiateSelectableItem(content: Any): Any {
        val params = api.selectableItemConstructor.parameterTypes
        return when {
            params.isEmpty() -> api.selectableItemConstructor.newInstance()
            params.size == 1 && params[0].isAssignableFrom(content.javaClass) ->
                api.selectableItemConstructor.newInstance(content)
            else -> throw NoSuchMethodException("${api.selectableItemClass.name} constructor cannot receive ${content.javaClass.name}")
        }
    }

    private fun createCallback(): Any {
        return Proxy.newProxyInstance(
            api.callbackInterface.classLoader,
            arrayOf(api.callbackInterface),
            InvocationHandler { proxy, method, args ->
                val arguments = args ?: emptyArray()
                when (method.name) {
                    "equals" -> proxy === arguments.firstOrNull()
                    "hashCode" -> System.identityHashCode(proxy)
                    "toString" -> "HyperModesFocusDetailCallback@${Integer.toHexString(System.identityHashCode(proxy))}"
                    "onDetailItemClick" -> {
                        activateTaggedItem(arguments.firstOrNull())
                        null
                    }
                    "onDetailItemDisconnect" -> null
                    else -> defaultReturnValue(method.returnType)
                }
            }
        )
    }

    private fun activateTaggedItem(item: Any?) {
        val modeId = tagFromItem(item) as? String ?: return
        selectionController.select(modeId)
    }

    private fun tagFromItem(item: Any?): Any? {
        if (item == null) return null
        return runCatching {
            val method = item.javaClass.methods.firstOrNull { it.name == "getTag" && it.parameterTypes.isEmpty() }
                ?: item.javaClass.declaredMethods.firstOrNull { it.name == "getTag" && it.parameterTypes.isEmpty() }
            if (method != null) {
                method.isAccessible = true
                method.invoke(item)
            } else {
                findField(item.javaClass, "tag").apply { isAccessible = true }.get(item)
            }
        }.getOrNull()
    }

    private fun setField(instance: Any, name: String, value: Any?) {
        val field = findField(instance.javaClass, name)
        field.isAccessible = true
        when (field.type) {
            Boolean::class.javaPrimitiveType -> field.setBoolean(instance, value as? Boolean ?: false)
            Int::class.javaPrimitiveType -> field.setInt(instance, value as? Int ?: 0)
            else -> field.set(instance, value)
        }
    }
}

private fun reflectInvalidDetailEvent(detailAdapterInterface: Class<*>): Any? {
    return runCatching {
        findField(detailAdapterInterface, "INVALID").apply { isAccessible = true }.get(null)
    }.getOrNull()
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
    clazz.interfaces.forEach { iface ->
        runCatching { return findField(iface, name) }
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
    Char::class.javaPrimitiveType -> ' '
    else -> null
}
