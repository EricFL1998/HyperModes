package com.banana.hypermodes.controlcenter

import com.banana.hypermodes.systemserver.config.ConfigParser
import com.banana.hypermodes.systemserver.config.ModeConfig

interface FocusCardConfigStore {
    fun read(): String?
    fun write(json: String): Boolean
}

interface ObservableFocusCardConfigStore : FocusCardConfigStore {
    fun observe(onChanged: () -> Unit): AutoCloseable
}

fun interface ModeIndexSelector {
    fun select(size: Int): Int
}

data class FocusCardSnapshot(
    val modes: List<ModeConfig>,
    val displayedMode: ModeConfig?,
    val activeModeId: String?,
    val isActive: Boolean,
    val configValid: Boolean
)

class FocusCardStateRepository(
    private val store: FocusCardConfigStore,
    private val selector: ModeIndexSelector
) {
    fun loadOrInitialize(): FocusCardSnapshot {
        val raw = store.read() ?: return unavailable(configValid = true)
        val config = try {
            ConfigParser.parseConfig(raw)
        } catch (_: Exception) {
            return unavailable(configValid = false)
        }

        val snapshot = snapshotFrom(config)
        val activeModeId = snapshot.activeModeId
        if (activeModeId != null && config.lastModeId != activeModeId) {
            return snapshotAfterWrite(
                nextJson = ConfigParser.updateLastModeId(raw, activeModeId),
                fallback = snapshot
            )
        }

        if (snapshot.displayedMode != null || config.modes.isEmpty()) return snapshot

        val index = selector.select(config.modes.size).coerceIn(0, config.modes.lastIndex)
        val selected = config.modes[index]
        return snapshotAfterWrite(
            nextJson = ConfigParser.updateLastModeId(raw, selected.id),
            fallback = snapshot
        )
    }

    fun activate(modeId: String): Boolean {
        val raw = store.read() ?: return false
        val config = try {
            ConfigParser.parseConfig(raw)
        } catch (_: Exception) {
            return false
        }

        if (config.modes.none { it.id == modeId }) return false

        return store.write(ConfigParser.updateActiveModeId(raw, modeId))
    }

    fun deactivate(): Boolean {
        val raw = store.read() ?: return false
        try {
            ConfigParser.parseConfig(raw)
        } catch (_: Exception) {
            return false
        }

        return store.write(ConfigParser.updateActiveModeId(raw, null))
    }

    private fun snapshotAfterWrite(nextJson: String, fallback: FocusCardSnapshot): FocusCardSnapshot {
        if (!store.write(nextJson)) return fallback
        val stored = store.read() ?: return unavailable(configValid = true)
        val storedConfig = try {
            ConfigParser.parseConfig(stored)
        } catch (_: Exception) {
            return unavailable(configValid = false)
        }
        return snapshotFrom(storedConfig)
    }

    private fun snapshotFrom(config: com.banana.hypermodes.systemserver.config.FullConfig): FocusCardSnapshot {
        val byId = config.modes.associateBy { it.id }
        val active = config.activeModeId?.let(byId::get)
        if (active != null) {
            return FocusCardSnapshot(config.modes, active, active.id, true, true)
        }

        val remembered = config.lastModeId?.let(byId::get)
        if (remembered != null) {
            return FocusCardSnapshot(config.modes, remembered, null, false, true)
        }

        return FocusCardSnapshot(config.modes, null, null, false, true)
    }

    private fun unavailable(configValid: Boolean): FocusCardSnapshot {
        return FocusCardSnapshot(
            modes = emptyList(),
            displayedMode = null,
            activeModeId = null,
            isActive = false,
            configValid = configValid
        )
    }
}
