package com.banana.hypermodes.manager

import android.app.NotificationManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import com.banana.hypermodes.data.Mode
import com.banana.hypermodes.data.ModeSettings
import com.banana.hypermodes.protocol.Protocol

/**
 * Manages mode activation, deactivation, and settings application
 */
class ModeManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val usageStatsManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
    } else null

    /**
     * Activate a mode with its settings
     */
    fun activateMode(mode: Mode) {
        val settings = mode.settings

        // Apply DND
        if (settings.enableDnd) {
            applyDnd(settings.dndLevel)
        }

        // Apply display settings
        if (settings.enableGrayscale) {
            enableGrayscale(true)
        }

        // Pause apps
        if (settings.pausedApps.isNotEmpty()) {
            pauseApps(settings.pausedApps)
        }

        // For bedtime mode, also trigger DeskClock bedtime
        if (mode.id == "bedtime") {
            triggerDeskClockBedtime(true)
        }
    }

    /**
     * Deactivate a mode and restore normal settings
     */
    fun deactivateMode(mode: Mode) {
        val settings = mode.settings

        // Restore DND
        if (settings.enableDnd) {
            restoreDnd()
        }

        // Restore display
        if (settings.enableGrayscale) {
            enableGrayscale(false)
        }

        // Resume apps
        if (settings.pausedApps.isNotEmpty()) {
            resumeApps(settings.pausedApps)
        }

        // For bedtime mode, stop DeskClock bedtime
        if (mode.id == "bedtime") {
            triggerDeskClockBedtime(false)
        }
    }

    private fun applyDnd(level: com.banana.hypermodes.data.DndLevel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!notificationManager.isNotificationPolicyAccessGranted) {
                // Request permission
                val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
                return
            }

            val filter = when (level) {
                com.banana.hypermodes.data.DndLevel.NONE -> NotificationManager.INTERRUPTION_FILTER_NONE
                com.banana.hypermodes.data.DndLevel.PRIORITY -> NotificationManager.INTERRUPTION_FILTER_PRIORITY
                com.banana.hypermodes.data.DndLevel.ALARMS -> NotificationManager.INTERRUPTION_FILTER_ALARMS
            }
            notificationManager.setInterruptionFilter(filter)
        }
    }

    private fun restoreDnd() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
        }
    }

    /**
     * Enable/disable grayscale mode via accessibility settings
     */
    private fun enableGrayscale(enable: Boolean) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Settings.Secure.putInt(
                    context.contentResolver,
                    Settings.Secure.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED,
                    if (enable) 1 else 0
                )
                if (enable) {
                    // 0 = Grayscale (monochromacy)
                    Settings.Secure.putInt(
                        context.contentResolver,
                        Settings.Secure.ACCESSIBILITY_DISPLAY_DALTONIZER,
                        0
                    )
                }
            }
        } catch (e: SecurityException) {
            // Need WRITE_SECURE_SETTINGS permission
            // This will be handled by the module hook
        }
    }

    /**
     * Pause apps using UsageStatsManager (requires SUSPEND_APPS permission)
     */
    private fun pauseApps(packageNames: Set<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                usageStatsManager?.setAppStandbyBucket(
                    packageNames.firstOrNull() ?: return,
                    UsageStatsManager.STANDBY_BUCKET_RESTRICTED
                )
            } catch (e: Exception) {
                // Requires system permissions, will be handled by module
            }
        }
    }

    private fun resumeApps(packageNames: Set<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                usageStatsManager?.setAppStandbyBucket(
                    packageNames.firstOrNull() ?: return,
                    UsageStatsManager.STANDBY_BUCKET_ACTIVE
                )
            } catch (e: Exception) {
                // Requires system permissions
            }
        }
    }

    /**
     * Trigger DeskClock bedtime via broadcast
     */
    private fun triggerDeskClockBedtime(start: Boolean) {
        val intent = Intent(if (start) Protocol.ACTION_START_BEDTIME else Protocol.ACTION_STOP_BEDTIME)
        intent.setPackage(Protocol.DESKCLOCK_PACKAGE)
        context.sendBroadcast(intent, Protocol.PERMISSION_CONTROL)
    }

    companion object {
        // Predefined modes
        fun getDefaultModes(): List<Mode> = listOf(
            Mode(
                id = "dnd",
                name = "Do Not Disturb",
                icon = "⊝",
                description = "Silence notifications and calls",
                settings = ModeSettings(
                    enableDnd = true,
                    dndLevel = com.banana.hypermodes.data.DndLevel.PRIORITY
                )
            ),
            Mode(
                id = "bedtime",
                name = "Bedtime",
                icon = "🌙",
                description = "From 11:00 pm - 7:00 am",
                settings = ModeSettings(
                    enableDnd = true,
                    enableGrayscale = true,
                    dimWallpaper = true,
                    schedule = com.banana.hypermodes.data.ModeSchedule(
                        enabled = true,
                        startHour = 23,
                        startMinute = 0,
                        endHour = 7,
                        endMinute = 0
                    )
                )
            ),
            Mode(
                id = "driving",
                name = "Driving",
                icon = "🚗",
                description = "Using device's motion and Bluetooth connection",
                settings = ModeSettings(
                    enableDnd = true,
                    dndLevel = com.banana.hypermodes.data.DndLevel.PRIORITY,
                    hideNotifications = true
                )
            )
        )
    }
}
