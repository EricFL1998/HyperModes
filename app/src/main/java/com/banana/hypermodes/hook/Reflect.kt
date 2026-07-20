package com.banana.hypermodes.hook

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Minimal reflection helpers replacing XposedHelpers (not shipped in
 * libxposed API 101). Lookups walk superclasses and match parameters by
 * assignability with primitive/wrapper equivalence; null matches any
 * reference type.
 */
internal object Reflect {

    fun findClass(name: String, classLoader: ClassLoader): Class<*> =
        Class.forName(name, false, classLoader)

    fun callStatic(clazz: Class<*>, name: String, vararg args: Any?): Any? {
        val method = findMethod(clazz, name, args)
        method.isAccessible = true
        return method.invoke(null, *args)
    }

    fun call(instance: Any, name: String, vararg args: Any?): Any? {
        val method = findMethod(instance.javaClass, name, args)
        method.isAccessible = true
        return method.invoke(instance, *args)
    }

    fun newInstance(clazz: Class<*>, vararg args: Any?): Any {
        val ctor = findConstructor(clazz, args)
        ctor.isAccessible = true
        return ctor.newInstance(*args)
    }

    fun setIntField(instance: Any, name: String, value: Int) {
        val field = findField(instance.javaClass, name)
        field.isAccessible = true
        field.setInt(instance, value)
    }

    fun setObjectField(instance: Any, name: String, value: Any?) {
        val field = findField(instance.javaClass, name)
        field.isAccessible = true
        field.set(instance, value)
    }

    private fun findMethod(clazz: Class<*>, name: String, args: Array<out Any?>): Method {
        var c: Class<*>? = clazz
        while (c != null) {
            c.declaredMethods
                .firstOrNull { it.name == name && paramsMatch(it.parameterTypes, args) }
                ?.let { return it }
            c = c.superclass
        }
        throw NoSuchMethodException(
            "${clazz.name}.$name(${args.joinToString { it?.javaClass?.name ?: "null" }})"
        )
    }

    private fun findConstructor(clazz: Class<*>, args: Array<out Any?>): Constructor<*> =
        clazz.declaredConstructors.firstOrNull { paramsMatch(it.parameterTypes, args) }
            ?: throw NoSuchMethodException(
                "${clazz.name}<init>(${args.joinToString { it?.javaClass?.name ?: "null" }})"
            )

    private fun findField(clazz: Class<*>, name: String): Field {
        var c: Class<*>? = clazz
        while (c != null) {
            try {
                return c.getDeclaredField(name)
            } catch (_: NoSuchFieldException) {
                c = c.superclass
            }
        }
        throw NoSuchFieldException("${clazz.name}.$name")
    }

    private fun paramsMatch(types: Array<Class<*>>, args: Array<out Any?>): Boolean {
        if (types.size != args.size) return false
        return types.indices.all { i ->
            val arg = args[i] ?: return@all !types[i].isPrimitive
            matches(types[i], arg.javaClass)
        }
    }

    private fun matches(param: Class<*>, arg: Class<*>): Boolean {
        if (param.isAssignableFrom(arg)) return true
        if (!param.isPrimitive) return false
        val boxed: Class<*> = when (param) {
            java.lang.Integer.TYPE -> java.lang.Integer::class.java
            java.lang.Long.TYPE -> java.lang.Long::class.java
            java.lang.Boolean.TYPE -> java.lang.Boolean::class.java
            java.lang.Double.TYPE -> java.lang.Double::class.java
            java.lang.Float.TYPE -> java.lang.Float::class.java
            java.lang.Short.TYPE -> java.lang.Short::class.java
            java.lang.Byte.TYPE -> java.lang.Byte::class.java
            Character.TYPE -> java.lang.Character::class.java
            else -> return false
        }
        return arg == boxed
    }
}
