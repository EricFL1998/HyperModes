package com.banana.hypermodes.engine

import android.content.Context

/**
 * Persistent engine bookkeeping (SharedPreferences "engine_state"):
 *
 * - Reference counts per capability ("dnd", "grayscale", "darkMode") so two
 *   simultaneously active modes don't restore each other's settings:
 *   the first activation snapshots + applies, the last deactivation restores.
 * - Int snapshots (interruption filter, zen policy, daltonizer, night mode)
 *   recorded before first apply, restored after last release.
 * - Tracked package sets ("suspended_apps", "bypassed_apps") — what WE have
 *   suspended / set bypass-Dnd on, so deactivation only touches our own.
 */
object EngineState {
    private const val PREFS = "engine_state"
    private const val COUNT_PREFIX = "count_"
    private const val SNAP_PREFIX = "snap_"

    const val KEY_DND = "dnd"
    const val KEY_GRAYSCALE = "grayscale"
    const val KEY_DARK_MODE = "darkMode"
    const val TRACK_SUSPENDED = "suspended_apps"
    const val TRACK_BYPASSED = "bypassed_apps"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    /** +1 holder; returns true when the caller is the FIRST holder and must apply. */
    fun acquire(context: Context, key: String): Boolean {
        val p = prefs(context)
        val count = p.getInt(COUNT_PREFIX + key, 0)
        p.edit().putInt(COUNT_PREFIX + key, count + 1).apply()
        return count == 0
    }

    /** -1 holder; returns true when the caller was the LAST holder and must restore. */
    fun release(context: Context, key: String): Boolean {
        val p = prefs(context)
        val count = p.getInt(COUNT_PREFIX + key, 0)
        if (count <= 1) {
            p.edit().putInt(COUNT_PREFIX + key, 0).apply()
            return count == 1
        }
        p.edit().putInt(COUNT_PREFIX + key, count - 1).apply()
        return false
    }

    fun putSnapshot(context: Context, key: String, values: Map<String, Int>) {
        val e = prefs(context).edit()
        values.forEach { (name, v) -> e.putInt(SNAP_PREFIX + key + "_" + name, v) }
        e.apply()
    }

    fun getSnapshot(context: Context, key: String, name: String, default: Int): Int =
        prefs(context).getInt(SNAP_PREFIX + key + "_" + name, default)

    fun getTracked(context: Context, key: String): Set<String> =
        prefs(context).getStringSet(key, emptySet())?.toSet() ?: emptySet()

    fun putTracked(context: Context, key: String, value: Set<String>) {
        prefs(context).edit().putStringSet(key, value).apply()
    }
}
