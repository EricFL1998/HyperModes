package com.banana.hypermodes.engine

import android.app.NotificationManager
import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.provider.Settings
import com.banana.hypermodes.data.CONTACT_FILTER_ALL
import com.banana.hypermodes.data.CONTACT_FILTER_STARRED
import com.banana.hypermodes.data.DefaultModes
import com.banana.hypermodes.data.DndLevel
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeStore
import com.banana.hypermodes.hook.StepResult
import com.banana.hypermodes.protocol.Protocol

/**
 * The single entry point for mode activation/deactivation. Runs wherever it
 * is invoked — UI process (manual toggles), alarm receivers, driving
 * detection, bedtime state pushes.
 *
 * Continuous enforcement is delegated to system-native mechanisms (zen
 * policy, secure settings, package suspension, channel bypass), so no
 * resident process is needed while a mode is on.
 *
 * Every step is individually caught and recorded — one failure never aborts
 * the remaining steps.
 */
class ModeEngine(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun activate(mode: Mode, skipBedtimeTrigger: Boolean = false): List<StepResult> {
        val results = mutableListOf<StepResult>()
        val s = mode.settings
        if (s.enableDnd) applyDnd(results, true, s.dndLevel, s.contactFilter)
        if (s.enableGrayscale) applyGrayscale(results, true)
        if (s.enableDarkMode) applyDarkMode(results, true)
        syncSuspendedApps(results, mode, activating = true)
        syncBypassApps(results, mode, activating = true)
        if (mode.id == "bedtime" && !skipBedtimeTrigger) {
            triggerDeskClockBedtime(results, true)
        }
        return results
    }

    fun deactivate(mode: Mode, skipBedtimeTrigger: Boolean = false): List<StepResult> {
        val results = mutableListOf<StepResult>()
        val s = mode.settings
        if (s.enableDnd) applyDnd(results, false, s.dndLevel, s.contactFilter)
        if (s.enableGrayscale) applyGrayscale(results, false)
        if (s.enableDarkMode) applyDarkMode(results, false)
        syncSuspendedApps(results, mode, activating = false)
        syncBypassApps(results, mode, activating = false)
        if (mode.id == "bedtime" && !skipBedtimeTrigger) {
            triggerDeskClockBedtime(results, false)
        }
        return results
    }

    // ---- DND (interruption filter + zen policy incl. contact filter) ----

    private fun applyDnd(
        results: MutableList<StepResult>, enable: Boolean,
        level: DndLevel, contactFilter: Int
    ) {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            results += StepResult.fail("dnd", "notification policy access not granted")
            return
        }
        if (enable) {
            if (!EngineState.acquire(context, EngineState.KEY_DND)) {
                results += StepResult.ok("dnd (already held)")
                return
            }
            val p = notificationManager.notificationPolicy
            EngineState.putSnapshot(context, EngineState.KEY_DND, mapOf(
                "filter" to notificationManager.currentInterruptionFilter,
                "categories" to p.priorityCategories,
                "callSenders" to p.priorityCallSenders,
                "messageSenders" to p.priorityMessageSenders,
                "conversationSenders" to p.priorityConversationSenders,
                "visualEffects" to p.suppressedVisualEffects
            ))
            runStep(results, "dnd policy") {
                notificationManager.notificationPolicy = buildPolicy(contactFilter)
            }
            runStep(results, "dnd filter") {
                notificationManager.setInterruptionFilter(
                    when (level) {
                        DndLevel.NONE -> NotificationManager.INTERRUPTION_FILTER_NONE
                        DndLevel.PRIORITY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                        DndLevel.ALARMS -> NotificationManager.INTERRUPTION_FILTER_ALARMS
                    }
                )
            }
        } else {
            if (!EngineState.release(context, EngineState.KEY_DND)) {
                results += StepResult.ok("dnd (still held)")
                return
            }
            runStep(results, "dnd restore") {
                notificationManager.notificationPolicy = NotificationManager.Policy(
                    EngineState.getSnapshot(context, EngineState.KEY_DND, "categories", 0),
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "callSenders",
                        NotificationManager.Policy.PRIORITY_SENDERS_ANY
                    ),
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "messageSenders",
                        NotificationManager.Policy.PRIORITY_SENDERS_ANY
                    ),
                    EngineState.getSnapshot(context, EngineState.KEY_DND, "visualEffects", 0),
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "conversationSenders",
                        NotificationManager.Policy.CONVERSATION_SENDERS_ANYONE
                    )
                )
                notificationManager.setInterruptionFilter(
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DND, "filter",
                        NotificationManager.INTERRUPTION_FILTER_ALL
                    )
                )
            }
        }
    }

    /** Map contactFilter to a zen Policy: who may interrupt while the mode is on. */
    private fun buildPolicy(contactFilter: Int): NotificationManager.Policy = when (contactFilter) {
        CONTACT_FILTER_ALL -> NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_CALLS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES or
                    NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            0,
            NotificationManager.Policy.CONVERSATION_SENDERS_ANYONE
        )
        CONTACT_FILTER_STARRED -> NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_CALLS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_MESSAGES or
                    NotificationManager.Policy.PRIORITY_CATEGORY_REPEAT_CALLERS or
                    NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,
            NotificationManager.Policy.PRIORITY_SENDERS_STARRED,
            NotificationManager.Policy.PRIORITY_SENDERS_STARRED,
            0,
            NotificationManager.Policy.CONVERSATION_SENDERS_IMPORTANT
        )
        else -> NotificationManager.Policy(
            NotificationManager.Policy.PRIORITY_CATEGORY_ALARMS,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            NotificationManager.Policy.PRIORITY_SENDERS_ANY,
            0,
            NotificationManager.Policy.CONVERSATION_SENDERS_NONE
        )
    }

    // ---- Grayscale (accessibility daltonizer, root fallback) ----

    private fun applyGrayscale(results: MutableList<StepResult>, enable: Boolean) {
        if (enable) {
            if (!EngineState.acquire(context, EngineState.KEY_GRAYSCALE)) {
                results += StepResult.ok("grayscale (already held)")
                return
            }
            EngineState.putSnapshot(context, EngineState.KEY_GRAYSCALE, mapOf(
                "enabled" to readSecureInt("accessibility_display_daltonizer_enabled", 0),
                "type" to readSecureInt("accessibility_display_daltonizer", 0)
            ))
            runStep(results, "grayscale on") {
                putSecureInt("accessibility_display_daltonizer", 0) // 0 = monochromacy
                putSecureInt("accessibility_display_daltonizer_enabled", 1)
            }
        } else {
            if (!EngineState.release(context, EngineState.KEY_GRAYSCALE)) {
                results += StepResult.ok("grayscale (still held)")
                return
            }
            runStep(results, "grayscale restore") {
                putSecureInt(
                    "accessibility_display_daltonizer",
                    EngineState.getSnapshot(context, EngineState.KEY_GRAYSCALE, "type", 0)
                )
                putSecureInt(
                    "accessibility_display_daltonizer_enabled",
                    EngineState.getSnapshot(context, EngineState.KEY_GRAYSCALE, "enabled", 0)
                )
            }
        }
    }

    private fun readSecureInt(name: String, default: Int): Int = try {
        Settings.Secure.getInt(context.contentResolver, name, default)
    } catch (t: Throwable) {
        default
    }

    /** Direct write first; fall back to root shell (libsu) like the old ModeManager. */
    private fun putSecureInt(name: String, value: Int) {
        try {
            Settings.Secure.putInt(context.contentResolver, name, value)
        } catch (e: SecurityException) {
            com.topjohnwu.superuser.Shell.cmd("settings put secure $name $value").exec()
        }
    }

    // ---- Dark mode (UiModeManager, root fallback) ----

    private fun applyDarkMode(results: MutableList<StepResult>, enable: Boolean) {
        if (enable) {
            if (!EngineState.acquire(context, EngineState.KEY_DARK_MODE)) {
                results += StepResult.ok("darkMode (already held)")
                return
            }
            val night = context.resources.configuration.uiMode and
                    Configuration.UI_MODE_NIGHT_MASK
            EngineState.putSnapshot(context, EngineState.KEY_DARK_MODE, mapOf("night" to night))
            runStep(results, "darkMode on") { setNightMode(Configuration.UI_MODE_NIGHT_YES) }
        } else {
            if (!EngineState.release(context, EngineState.KEY_DARK_MODE)) {
                results += StepResult.ok("darkMode (still held)")
                return
            }
            runStep(results, "darkMode restore") {
                setNightMode(
                    EngineState.getSnapshot(
                        context, EngineState.KEY_DARK_MODE, "night",
                        Configuration.UI_MODE_NIGHT_NO
                    )
                )
            }
        }
    }

    private fun setNightMode(mode: Int) {
        try {
            val uiModeManager =
                context.getSystemService(Context.UI_MODE_SERVICE) as UiModeManager
            uiModeManager.nightMode = mode
        } catch (e: SecurityException) {
            val shell = when (mode) {
                Configuration.UI_MODE_NIGHT_YES -> "yes"
                Configuration.UI_MODE_NIGHT_NO -> "no"
                else -> "auto"
            }
            com.topjohnwu.superuser.Shell.cmd("cmd uimode night $shell").exec()
        }
    }

    // ---- Suspended apps / bypass-Dnd apps (via SystemModeHook in system_server) ----

    /**
     * Recompute the union of pausedApps over all enabled modes (the toggled
     * [mode] counts per [activating], since ModeStore is written after the
     * engine runs) and diff against what we previously suspended.
     */
    private fun syncSuspendedApps(
        results: MutableList<StepResult>, mode: Mode, activating: Boolean
    ) = syncPackageSet(
        results, mode, activating,
        select = { it.settings.pausedApps },
        trackKey = EngineState.TRACK_SUSPENDED,
        action = Protocol.ACTION_SET_PACKAGES_SUSPENDED,
        flagExtra = Protocol.EXTRA_SUSPENDED,
        step = "suspendApps"
    )

    private fun syncBypassApps(
        results: MutableList<StepResult>, mode: Mode, activating: Boolean
    ) = syncPackageSet(
        results, mode, activating,
        select = { it.settings.allowedApps },
        trackKey = EngineState.TRACK_BYPASSED,
        action = Protocol.ACTION_SET_CHANNELS_BYPASS_DND,
        flagExtra = Protocol.EXTRA_BYPASS,
        step = "bypassDnd"
    )

    private fun syncPackageSet(
        results: MutableList<StepResult>, mode: Mode, activating: Boolean,
        select: (Mode) -> Set<String>,
        trackKey: String, action: String, flagExtra: String, step: String
    ) {
        val modes = ModeStore.load(context) { DefaultModes.get() }
        val want = modes
            .filter { if (it.id == mode.id) activating else it.enabled }
            .flatMap { select(it) }
            .toSet()
        val prev = EngineState.getTracked(context, trackKey)
        val toOn = want - prev
        val toOff = prev - want
        EngineState.putTracked(context, trackKey, want)
        if (toOn.isNotEmpty()) dispatchToSystem(results, action, toOn, flagExtra, true, "$step on")
        if (toOff.isNotEmpty()) dispatchToSystem(results, action, toOff, flagExtra, false, "$step off")
        if (toOn.isEmpty() && toOff.isEmpty()) results += StepResult.ok("$step (no change)")
    }

    /**
     * Fire-and-forget bridge to SystemModeHook in system_server (dynamic
     * receiver registered with our signature permission — only this app can
     * send). The outcome lands in the LSPosed module log.
     */
    private fun dispatchToSystem(
        results: MutableList<StepResult>, action: String,
        packages: Set<String>, flagExtra: String, flag: Boolean, step: String
    ) {
        try {
            context.sendBroadcast(Intent(action).apply {
                putExtra(Protocol.EXTRA_PACKAGES, packages.toTypedArray())
                putExtra(flagExtra, flag)
            }, Protocol.PERMISSION_CONTROL)
            results += StepResult.ok("$step dispatched (${packages.size})")
        } catch (t: Throwable) {
            results += StepResult.fail(step, t)
        }
    }

    // ---- Bedtime (DeskClock trigger, existing wire protocol) ----

    private fun triggerDeskClockBedtime(results: MutableList<StepResult>, start: Boolean) {
        runStep(results, if (start) "deskclock bedtime start" else "deskclock bedtime stop") {
            val intent = Intent(
                if (start) Protocol.ACTION_START_BEDTIME else Protocol.ACTION_STOP_BEDTIME
            )
            intent.setPackage(Protocol.DESKCLOCK_PACKAGE)
            context.sendBroadcast(intent, Protocol.PERMISSION_CONTROL)
        }
    }

    private fun runStep(results: MutableList<StepResult>, name: String, block: () -> Any?) {
        try {
            block()
            results += StepResult.ok(name)
        } catch (t: Throwable) {
            results += StepResult.fail(name, t)
        }
    }
}
