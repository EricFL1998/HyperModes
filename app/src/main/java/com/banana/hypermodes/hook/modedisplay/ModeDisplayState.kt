package com.banana.hypermodes.hook.modedisplay

import android.content.Context
import android.provider.Settings
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.systemserver.config.ConfigParser

data class ModeDisplayState(
    val name: String,
    val iconResName: String
)

object ModeDisplayStateReader {
    const val CONFIG_KEY = "pixel_routines_full_config"

    fun read(context: Context): ModeDisplayState? {
        val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
        return fromJson(json)
    }

    fun fromJson(json: String?): ModeDisplayState? {
        if (json.isNullOrBlank()) return null

        return runCatching {
            val config = ConfigParser.parseConfig(json)
            val activeMode = config.modes.firstOrNull { it.id == config.activeModeId }
                ?: return null
            val iconResName = activeMode.statusIcon
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
                ?: ModeIconMapper.getStatusBarIcon(activeMode.icon)

            ModeDisplayState(
                name = activeMode.name,
                iconResName = iconResName
            )
        }.getOrNull()
    }
}
