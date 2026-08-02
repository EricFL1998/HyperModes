package com.banana.hypermodes.hook.modedisplay

import android.content.Context
import android.provider.Settings
import com.banana.hypermodes.controlcenter.FocusModeDisplayNameResolver
import com.banana.hypermodes.data.ModeIconMapper
import com.banana.hypermodes.protocol.Protocol
import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.ModeConfig

data class ModeDisplayState(
    val name: String,
    val iconResName: String
)

object ModeDisplayStateReader {
    const val CONFIG_KEY = "pixel_routines_full_config"

    fun read(context: Context): ModeDisplayState? {
        val json = Settings.Global.getString(context.contentResolver, CONFIG_KEY)
        return fromJson(json) { mode -> resolveDisplayName(context, mode) }
    }

    fun fromJson(
        json: String?,
        resolveName: (ModeConfig) -> String = { it.name }
    ): ModeDisplayState? {
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
                name = resolveName(activeMode),
                iconResName = iconResName
            )
        }.getOrNull()
    }

    // Built-in modes store their English default names; translate to the module's
    // localized strings. Renamed or custom modes pass through untouched.
    private fun resolveDisplayName(context: Context, mode: ModeConfig): String =
        runCatching {
            val moduleContext = context.createPackageContext(
                Protocol.MODULE_PACKAGE,
                Context.CONTEXT_IGNORE_SECURITY
            )
            FocusModeDisplayNameResolver(moduleContext.resources, Protocol.MODULE_PACKAGE)
                .resolve(mode)
        }.getOrDefault(mode.name)
}
