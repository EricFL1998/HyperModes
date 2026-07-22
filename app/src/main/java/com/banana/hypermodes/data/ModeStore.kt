package com.banana.hypermodes.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Persists the user's mode list (including deletions of built-in modes and
 * user-created custom modes) to SharedPreferences as JSON.
 */
object ModeStore {
    private const val PREF_NAME = "mode_store"
    private const val KEY_MODES = "modes"

    val BUILT_IN_IDS = listOf("dnd", "bedtime", "driving")

    /** Load the persisted mode list, or the defaults on first run. */
    fun load(context: Context, defaults: () -> List<Mode>): List<Mode> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_MODES, null) ?: return defaults()
        return try {
            val arr = JSONArray(json)
            (0 until arr.length()).map { modeFromJson(arr.getJSONObject(it)) }
        } catch (e: Exception) {
            defaults()
        }
    }

    fun save(context: Context, modes: List<Mode>) {
        val arr = JSONArray()
        modes.forEach { arr.put(modeToJson(it)) }
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putString(KEY_MODES, arr.toString()).apply()
    }

    private fun modeToJson(mode: Mode): JSONObject = JSONObject().apply {
        put("id", mode.id)
        put("name", mode.name)
        put("icon", mode.icon)
        put("description", mode.description)
        put("enabled", mode.enabled)
        put("settings", settingsToJson(mode.settings))
    }

    private fun settingsToJson(s: ModeSettings): JSONObject = JSONObject().apply {
        put("enableDnd", s.enableDnd)
        put("dndLevel", s.dndLevel.name)
        put("enableGrayscale", s.enableGrayscale)
        put("enableDarkMode", s.enableDarkMode)
        put("dimWallpaper", s.dimWallpaper)
        put("keepScreenOff", s.keepScreenOff)
        put("pausedApps", JSONArray(s.pausedApps.toList()))
        put("allowedContacts", JSONArray(s.allowedContacts.toList()))
        put("contactFilter", s.contactFilter)
        put("allowedApps", JSONArray(s.allowedApps.toList()))
        put("keepScreenOn", s.keepScreenOn)
        put("hideNotifications", s.hideNotifications)
        put("drivingAutoDetect", s.drivingAutoDetect)
        put("drivingDetectMode", s.drivingDetectMode)
        s.schedule?.let { put("schedule", scheduleToJson(it)) }
    }

    private fun scheduleToJson(s: ModeSchedule): JSONObject = JSONObject().apply {
        put("enabled", s.enabled)
        put("startHour", s.startHour)
        put("startMinute", s.startMinute)
        put("endHour", s.endHour)
        put("endMinute", s.endMinute)
        put("repeatDays", s.repeatDays)
    }

    private fun modeFromJson(o: JSONObject): Mode = Mode(
        id = o.getString("id"),
        name = o.getString("name"),
        icon = o.getString("icon"),
        description = o.optString("description"),
        enabled = o.optBoolean("enabled"),
        settings = settingsFromJson(o.optJSONObject("settings") ?: JSONObject())
    )

    private fun settingsFromJson(o: JSONObject): ModeSettings = ModeSettings(
        enableDnd = o.optBoolean("enableDnd", true),
        dndLevel = runCatching { DndLevel.valueOf(o.optString("dndLevel", "PRIORITY")) }
            .getOrDefault(DndLevel.PRIORITY),
        enableGrayscale = o.optBoolean("enableGrayscale"),
        enableDarkMode = o.optBoolean("enableDarkMode"),
        dimWallpaper = o.optBoolean("dimWallpaper"),
        keepScreenOff = o.optBoolean("keepScreenOff"),
        pausedApps = o.optJSONArray("pausedApps").toStringSet(),
        allowedContacts = o.optJSONArray("allowedContacts").toStringSet(),
        contactFilter = o.optInt("contactFilter", CONTACT_FILTER_NONE),
        allowedApps = o.optJSONArray("allowedApps").toStringSet(),
        keepScreenOn = o.optBoolean("keepScreenOn"),
        hideNotifications = o.optBoolean("hideNotifications"),
        drivingAutoDetect = o.optBoolean("drivingAutoDetect", true),
        drivingDetectMode = o.optInt("drivingDetectMode", DRIVING_DETECT_BLUETOOTH),
        schedule = o.optJSONObject("schedule")?.let { scheduleFromJson(it) }
    )

    private fun scheduleFromJson(o: JSONObject): ModeSchedule = ModeSchedule(
        enabled = o.optBoolean("enabled"),
        startHour = o.optInt("startHour", 22),
        startMinute = o.optInt("startMinute", 0),
        endHour = o.optInt("endHour", 7),
        endMinute = o.optInt("endMinute", 0),
        repeatDays = o.optInt("repeatDays", 0x7F)
    )

    private fun JSONArray?.toStringSet(): Set<String> {
        if (this == null) return emptySet()
        return (0 until length()).map { getString(it) }.toSet()
    }
}
