package com.banana.hypermodes.controlcenter

import android.view.View
import android.view.ViewGroup
import java.util.WeakHashMap
import kotlin.math.roundToInt

internal object FocusNativeDetailViewDecorator {
    private const val RECYCLER_VIEW_CLASS = "androidx.recyclerview.widget.RecyclerView"
    private const val TOP_PADDING_DP = 28f
    private const val LEFT_PADDING_DP = 12f
    private val originalPaddings = WeakHashMap<View, Pair<Int, Int>>()

    fun decorate(content: View): Boolean {
        return decorate(content) { view ->
            classHierarchyContains(view.javaClass, RECYCLER_VIEW_CLASS)
        }
    }

    internal fun decorate(
        content: View,
        listMatcher: (View) -> Boolean
    ): Boolean {
        return runCatching {
            val target = findList(content, parent = null, listMatcher) ?: return false
            target.list.background = null
            target.host?.background = null
            target.list.isVerticalScrollBarEnabled = false
            applyExtraPaddings(target.list)
            true
        }.getOrDefault(false)
    }

    private fun applyExtraPaddings(list: View) {
        val (originalLeft, originalTop) = synchronized(originalPaddings) {
            originalPaddings.getOrPut(list) { list.paddingLeft to list.paddingTop }
        }
        val density = runCatching { list.resources.displayMetrics.density }.getOrDefault(1f)
        val extraTop = (TOP_PADDING_DP * density).roundToInt()
        val extraLeft = (LEFT_PADDING_DP * density).roundToInt()

        list.setPadding(
            originalLeft + extraLeft,
            originalTop + extraTop,
            list.paddingRight,
            list.paddingBottom
        )
        if (list is ViewGroup) list.clipToPadding = false
    }

    private fun findList(
        view: View,
        parent: View?,
        listMatcher: (View) -> Boolean
    ): ListTarget? {
        if (listMatcher(view)) return ListTarget(list = view, host = parent)
        val group = view as? ViewGroup ?: return null
        for (index in 0 until group.childCount) {
            findList(group.getChildAt(index), group, listMatcher)?.let { return it }
        }
        return null
    }

    private fun classHierarchyContains(clazz: Class<*>, expectedName: String): Boolean {
        var current: Class<*>? = clazz
        while (current != null) {
            if (current.name == expectedName) return true
            current = current.superclass
        }
        return false
    }

    private data class ListTarget(
        val list: View,
        val host: View?
    )
}
