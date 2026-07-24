package com.banana.hypermodes.tile

import android.app.AutomaticZenRule
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.service.notification.Condition
import android.util.Log
import io.github.libxposed.api.XposedModule
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * Provider that creates Focus tile by implementing MiuiQSTile interface dynamically
 *
 * This creates a proxy object that implements the MiuiQSTile interface and handles
 * all tile interactions including clicks, state updates, and detail view.
 */
class FocusTileProvider(
    private val context: Context,
    private val module: XposedModule
) {
    companion object {
        private const val TAG = "HyperModes.FocusTileProvider"
        private const val FOCUS_TILE_SPEC = "focus"
    }

    private val notificationManager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    /**
     * Create a tile that implements MiuiQSTile interface (for plugin tiles)
     */
    fun createMiuiTile(classLoader: ClassLoader): Any {
        try {
            log("Creating Focus tile as MiuiQSTile")

            // Load MiuiQSTile interface
            val miuiQSTileInterface = classLoader.loadClass("com.android.systemui.plugins.miui.qs.MiuiQSTile")
            log("Loaded MiuiQSTile interface")

            // Create invocation handler for the proxy
            val handler = FocusTileInvocationHandler(classLoader)

            // Create proxy that implements MiuiQSTile
            val tile = Proxy.newProxyInstance(
                classLoader,
                arrayOf(miuiQSTileInterface),
                handler
            )

            log("Focus tile proxy created successfully")
            return tile
        } catch (t: Throwable) {
            log("Failed to create tile proxy: ${t.message}", t)
            throw t
        }
    }

    /**
     * InvocationHandler that implements all MiuiQSTile methods
     */
    private inner class FocusTileInvocationHandler(
        private val classLoader: ClassLoader
    ) : InvocationHandler {

        override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
            return try {
                when (method.name) {
                    // MiuiQSTile specific methods
                    "getTileSpec" -> FOCUS_TILE_SPEC
                    "isAvailable" -> true
                    "getState" -> getState()
                    "newTileState" -> newTileState()
                    "refreshState" -> {
                        // Refresh called with optional argument
                        refreshState(args?.getOrNull(0))
                        null
                    }
                    "handleClick" -> {
                        handleClick()
                        null
                    }
                    "addCallback" -> {
                        // Store callback for state updates
                        null
                    }
                    "removeCallback" -> {
                        null
                    }
                    "setListening" -> {
                        // Called when tile visibility changes
                        null
                    }
                    "getLongClickIntent" -> getLongClickIntent()
                    "getMetricsCategory" -> 118 // Same as DND tile
                    "composeChangeAnnouncement" -> "Focus mode"
                    "getStateMessage" -> null

                    // For detail view (if needed)
                    "getDetailAdapter" -> null // MiuiQSTile doesn't use detail adapters the same way

                    else -> {
                        log("Unhandled method: ${method.name}")
                        null
                    }
                }
            } catch (t: Throwable) {
                log("Error invoking ${method.name}: ${t.message}", t)
                null
            }
        }

        private fun newTileState(): Any {
            try {
                // Create QSTile.State
                val stateClass = classLoader.loadClass("com.android.systemui.plugins.qs.QSTile\$State")
                return stateClass.getDeclaredConstructor().newInstance()
            } catch (t: Throwable) {
                log("Failed to create new tile state: ${t.message}", t)
                throw t
            }
        }

        private fun getState(): Any {
            try {
                // Create QSTile.BooleanState (or State)
                val stateClass = try {
                    classLoader.loadClass("com.android.systemui.plugins.qs.QSTile\$BooleanState")
                } catch (e: Exception) {
                    classLoader.loadClass("com.android.systemui.plugins.qs.QSTile\$State")
                }
                val state = stateClass.getDeclaredConstructor().newInstance()

                val isActive = isAnyModeActive()
                val currentMode = getCurrentFocusMode()

                // Set state fields
                try {
                    stateClass.getField("value")?.set(state, isActive)
                } catch (e: Exception) {
                    // BooleanState might not exist
                }

                stateClass.getField("state").setInt(state, if (isActive) 2 else 1) // STATE_ACTIVE : STATE_INACTIVE
                stateClass.getField("label").set(state, getLabel())

                // Set secondary label (description) if mode is active
                if (currentMode != null) {
                    val description = currentMode.description.ifEmpty { "Active" }
                    try {
                        stateClass.getField("secondaryLabel")?.set(state, description)
                    } catch (e: Exception) {
                        // Field might not exist
                    }
                }

                // Set content description for accessibility
                try {
                    stateClass.getField("contentDescription")?.set(state, getLabel())
                } catch (e: Exception) {
                    // Field might not exist
                }

                log("State updated: active=$isActive, label=${getLabel()}")
                return state
            } catch (t: Throwable) {
                log("Failed to create state: ${t.message}", t)
                throw t
            }
        }

        private fun refreshState(arg: Any?) {
            // Just update the state - the callback will be notified
            getState()
        }

        private fun getLabel(): String {
            val currentMode = getCurrentFocusMode()
            return currentMode?.name ?: "Focus"
        }

        private fun handleClick() {
            log("Focus tile clicked")
            try {
                val currentMode = getCurrentFocusMode()

                if (currentMode != null) {
                    // Turn off current mode
                    log("Turning off mode: ${currentMode.name}")
                    setModeActive(currentMode.id, false)
                } else {
                    // No mode active - show a toast or do nothing
                    log("No active mode, user should configure in Settings")
                }
            } catch (t: Throwable) {
                log("Error in handleClick: ${t.message}", t)
            }
        }

        private fun getLongClickIntent(): Intent {
            return Intent("android.settings.ZEN_MODE_SETTINGS")
        }
    }

    // Helper methods to interact with system zen modes

    private fun getCurrentFocusMode(): FocusMode? {
        return try {
            val filter = notificationManager.currentInterruptionFilter
            if (filter == NotificationManager.INTERRUPTION_FILTER_ALL) {
                return null
            }

            // Find active rule
            val rules = notificationManager.automaticZenRules ?: return null
            for ((id, rule) in rules) {
                val state = notificationManager.getAutomaticZenRuleState(id)
                if (state == Condition.STATE_TRUE) {
                    val description = rule.triggerDescription ?: ""
                    return FocusMode(id, rule.name, description)
                }
            }

            null
        } catch (t: Throwable) {
            log("Failed to get current mode: ${t.message}", t)
            null
        }
    }

    private fun isAnyModeActive(): Boolean {
        return try {
            val filter = notificationManager.currentInterruptionFilter
            filter != NotificationManager.INTERRUPTION_FILTER_ALL
        } catch (t: Throwable) {
            log("Failed to check if any mode active: ${t.message}", t)
            false
        }
    }

    private fun setModeActive(ruleId: String, active: Boolean) {
        try {
            val rule = notificationManager.getAutomaticZenRule(ruleId)
            if (rule == null) {
                log("Rule not found: $ruleId")
                return
            }

            if (active) {
                // Enable the rule first if not enabled
                if (!rule.isEnabled) {
                    val updatedRule = AutomaticZenRule(
                        rule.name,
                        rule.owner,
                        rule.configurationActivity,
                        rule.conditionId,
                        rule.zenPolicy,
                        rule.interruptionFilter,
                        true
                    )
                    notificationManager.updateAutomaticZenRule(ruleId, updatedRule)
                }

                // Activate by setting condition to TRUE
                val condition = Condition(
                    rule.conditionId,
                    rule.name,
                    Condition.STATE_TRUE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)
                log("Activated mode: ${rule.name}")
            } else {
                // Deactivate by setting condition to FALSE
                val condition = Condition(
                    rule.conditionId,
                    rule.name,
                    Condition.STATE_FALSE
                )
                notificationManager.setAutomaticZenRuleState(ruleId, condition)
                log("Deactivated mode: ${rule.name}")
            }
        } catch (t: Throwable) {
            log("Failed to set mode active: ${t.message}", t)
        }
    }

    private fun log(msg: String, t: Throwable? = null) {
        if (t != null) {
            Log.e(TAG, msg, t)
        } else {
            Log.w(TAG, msg)
        }
    }

    /**
     * Data class representing a focus mode
     */
    data class FocusMode(
        val id: String,
        val name: String,
        val description: String
    )
}
