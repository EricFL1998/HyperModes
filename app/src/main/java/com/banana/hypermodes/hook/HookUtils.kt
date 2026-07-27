package com.banana.hypermodes.hook

import io.github.libxposed.api.XposedInterface

object HookUtils {
    fun getThisObject(chain: XposedInterface.Chain): Any? {
        return try {
            val method = chain.javaClass.getMethod("getThisObject")
            method.invoke(chain)
        } catch (e: Exception) {
            null
        }
    }

    fun getArgs(chain: XposedInterface.Chain): Array<Any?> {
        return try {
            val method = chain.javaClass.getMethod("getArgs")
            method.invoke(chain) as Array<Any?>
        } catch (e: Exception) {
            emptyArray()
        }
    }
}
