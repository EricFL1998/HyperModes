package com.banana.hypermodes.controlcenter

object FocusNativeDetailPolicy {

    fun shouldReturnFullItemCount(
        outerContent: Any?,
        suffix: String?,
        itemsLength: Int,
        registry: FocusNativeDetailRegistry
    ): Int? {
        if (outerContent == null) return null
        if (!registry.isFocusContent(outerContent)) return null
        if (suffix != FocusNativeDetailRegistry.CONTENT_SUFFIX) return null
        return itemsLength
    }

    fun shouldMapToFocusSpec(
        adapter: Any?,
        registry: FocusNativeDetailRegistry
    ): String? {
        if (adapter == null) return null
        if (!registry.isFocusAdapter(adapter)) return null
        return FocusNativeDetailRegistry.TILE_SPEC
    }

    fun shouldUseSpecificHeight(
        adapter: Any?,
        registry: FocusNativeDetailRegistry
    ): Boolean? {
        if (adapter == null) return null
        if (!registry.isFocusAdapter(adapter)) return null
        return true
    }

    fun resolveOuterContent(innerAdapter: Any, contentClass: Class<*>): Any? {
        return try {
            // Try synthetic this$0 field first
            val field = innerAdapter.javaClass.getDeclaredField("this\$0")
            field.isAccessible = true
            val outer = field.get(innerAdapter)
            if (contentClass.isInstance(outer)) outer else null
        } catch (e: NoSuchFieldException) {
            // Fallback: find any field assignable to content class
            try {
                val field = innerAdapter.javaClass.declaredFields.firstOrNull {
                    contentClass.isAssignableFrom(it.type)
                }
                field?.let {
                    it.isAccessible = true
                    it.get(innerAdapter)
                }
            } catch (e: Throwable) {
                null
            }
        } catch (e: Throwable) {
            null
        }
    }

    fun readItemsArray(content: Any): Array<*>? {
        return try {
            val field = content.javaClass.getDeclaredField("items")
            field.isAccessible = true
            field.get(content) as? Array<*>
        } catch (e: Throwable) {
            null
        }
    }

    fun readSuffix(content: Any): String? {
        return try {
            val field = content.javaClass.getDeclaredField("suffix")
            field.isAccessible = true
            field.get(content) as? String
        } catch (e: Throwable) {
            null
        }
    }
}
