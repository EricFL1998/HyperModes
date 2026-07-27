package com.banana.hypermodes.controlcenter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Modifier

enum class FocusDetailFallbackStage {
    NATIVE_API_UNAVAILABLE,
    NATIVE_CONVERT,
    NATIVE_ITEMS,
    NATIVE_CALLBACK,
    MANUAL_BUILD,
    SAFE_BUILD,
    CONTENT_CREATION,
    ADAPTER_BINDING,
    STATE_TRANSITION,
    REGISTRY_OPERATION
}

fun interface FocusDetailDiagnostic {
    fun failed(stage: FocusDetailFallbackStage, throwable: Throwable?)
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
    fun fromClassLoader(
        classLoader: ClassLoader?,
        onFailure: (Throwable) -> Unit = {}
    ): FocusNativeDetailContentApi? {
        if (classLoader == null) {
            onFailure(IllegalStateException("SystemUI ClassLoader is null"))
            return null
        }
        return try {
            resolveContentClass(
                contentClass = classLoader.loadClass("com.android.systemui.qs.QSDetailContent"),
                classLoader = classLoader
            )
        } catch (throwable: Throwable) {
            onFailure(throwable)
            null
        }
    }

    fun fromContentClass(contentClass: Class<*>): FocusNativeDetailContentApi? {
        return runCatching {
            resolveContentClass(
                contentClass = contentClass,
                classLoader = contentClass.classLoader
            )
        }.getOrNull()
    }

    private fun resolveContentClass(
        contentClass: Class<*>,
        classLoader: ClassLoader?
    ): FocusNativeDetailContentApi {
        val itemInterface = nestedClass(contentClass, "Item", classLoader)
        val selectableItemClass = nestedClass(contentClass, "SelectableItem", classLoader)
        val callbackInterface = nestedClass(contentClass, "Callback", classLoader)
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
        return FocusNativeDetailContentApi(
            contentClass = contentClass,
            itemInterface = itemInterface,
            selectableItemClass = selectableItemClass,
            callbackInterface = callbackInterface,
            convertOrInflate = findConvertOrInflate(contentClass, classLoader),
            selectableItemConstructor = constructor,
            setSuffix = findMethod(contentClass, "setSuffix") { method ->
                method.parameterTypes.contentEquals(arrayOf(String::class.java))
            },
            setItems = setItems,
            setCallback = findMethod(contentClass, "setCallback") { method ->
                method.parameterTypes.size == 1 && method.parameterTypes[0].isAssignableFrom(callbackInterface)
            }
        )
    }

    private fun nestedClass(
        contentClass: Class<*>,
        simpleName: String,
        classLoader: ClassLoader?
    ): Class<*> {
        return contentClass.declaredClasses.firstOrNull { it.simpleName == simpleName }
            ?: classLoader?.loadClass("${contentClass.name}\$$simpleName")
            ?: throw ClassNotFoundException("${contentClass.name}\$$simpleName")
    }

    private fun findConvertOrInflate(
        contentClass: Class<*>,
        classLoader: ClassLoader?
    ): FocusNativeConvertOrInflate {
        findMethodOrNull(contentClass, "convertOrInflate", ::isConvertOrInflateMethod)?.let { method ->
            val owner = if (Modifier.isStatic(method.modifiers)) ({ null }) else ({ contentClass.getDeclaredConstructor().newInstance() })
            return FocusNativeConvertOrInflate(ownerProvider = owner, method = method)
        }

        val companionClass = runCatching {
            nestedClass(contentClass, "Companion", classLoader)
        }.getOrElse {
            throw NoSuchMethodException("${contentClass.name}.Companion.convertOrInflate").apply {
                initCause(it)
            }
        }
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
