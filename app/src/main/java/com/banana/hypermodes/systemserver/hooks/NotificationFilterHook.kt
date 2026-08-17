package com.banana.hypermodes.systemserver.hooks

import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.DndLevel
import com.banana.hypermodes.systemserver.config.NotificationConfig
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Notification filtering hook running INSIDE system_server.
 *
 * Intercepts NotificationAttentionHelper.shouldMuteNotificationLocked to filter
 * notifications based on the active mode's configuration.
 *
 * In the decompiled OS4 services, the AOSP 1-arg boolean
 * NotificationManagerService.shouldMuteNotificationLocked is not present;
 * the mute decision lives on NotificationAttentionHelper as
 * int shouldMuteNotificationLocked(NotificationRecord, Signals, boolean)
 * where 0 = don't mute and each non-zero value is a concrete mute-reason bit.
 *
 * Logic:
 * - If no mode is active -> proceed normally
 * - If DND level is ALARMS -> proceed normally (system DND handles it)
 * - If DND level is NONE or PRIORITY:
 *   - Check if notification's package is in allowedApps whitelist
 *   - If allowed -> preserve the native OS4 decision
 *   - If not allowed -> return OS4's intercepted/Zen mute reason
 */
class NotificationFilterHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        log("NotificationFilterHook.install starting")

        val helper = try {
            classLoader.loadClass(NOTIFICATION_ATTENTION_HELPER)
        } catch (t: Throwable) {
            log("NotificationAttentionHelper not found: ${t.message}")
            return
        }

        log("NotificationAttentionHelper found, installing hook")
        hookShouldMuteNotification(helper, classLoader)
        log("NotificationFilterHook.install complete")
    }

    /**
     * Hook NotificationAttentionHelper.shouldMuteNotificationLocked to filter
     * notifications based on active mode configuration.
     *
     * Method signature (decompiled HyperOS services):
     * int shouldMuteNotificationLocked(NotificationRecord, Signals, boolean)
     */
    private fun hookShouldMuteNotification(helper: Class<*>, classLoader: ClassLoader) {
        val notificationRecordClass = try {
            classLoader.loadClass(NOTIFICATION_RECORD)
        } catch (t: Throwable) {
            log("NotificationRecord class not found: ${t.message}")
            return
        }

        val signalsClass = try {
            classLoader.loadClass(NOTIFICATION_SIGNALS)
        } catch (t: Throwable) {
            log("NotificationAttentionHelper.Signals class not found: ${t.message}")
            return
        }

        val method = try {
            helper.getDeclaredMethod(
                "shouldMuteNotificationLocked",
                notificationRecordClass,
                signalsClass,
                Boolean::class.javaPrimitiveType
            ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("shouldMuteNotificationLocked (3-arg int) not found: ${t.message}")
            return
        }

        module.hook(method)
            .setExceptionMode(XposedInterface.ExceptionMode.PROTECTIVE)
            .intercept(object : XposedInterface.Hooker {
                override fun intercept(chain: XposedInterface.Chain): Any? {
                    try {
                        // Get current active mode
                        val activeMode = RoutineCoreEngine.getInstance().getCurrentActiveMode()

                        // If no mode is active, proceed normally
                        if (activeMode == null) {
                            return chain.proceed()
                        }

                        val notificationConfig = activeMode.notification

                        // If DND level is ALARMS, let system handle it (standard priority logic)
                        if (notificationConfig.dndLevel == DndLevel.ALARMS) {
                            return chain.proceed()
                        }

                        // If DND is disabled AND no custom whitelist is provided, let system handle it
                        if (notificationConfig.dndLevel == DndLevel.DISABLED && notificationConfig.allowedApps.isEmpty()) {
                            return chain.proceed()
                        }

                        // Extract package name from NotificationRecord
                        val record = chain.getArg(0)
                        val packageName = try {
                            extractPackageName(record)
                        } catch (e: Exception) {
                            log("Failed to extract package name: ${e.message}")
                            return chain.proceed()
                        }

                        // Check if package is in the allowed apps whitelist
                        val muteOverride = notificationMuteOverride(notificationConfig, packageName)

                        if (muteOverride == null) {
                            // Whitelisting only bypasses HyperModes filtering. Preserve
                            // OS4's own silent/group/rate-limit/DND decisions.
                            log("Allowing notification from $packageName (mode=${activeMode.name})")
                            return chain.proceed()
                        } else {
                            // OS4 uses 512 for the native "record is intercepted"
                            // branch. Use that defined reason instead of an unknown bit.
                            log("Muting notification from $packageName (mode=${activeMode.name})")
                            return muteOverride
                        }
                    } catch (t: Throwable) {
                        log("Error in notification filter hook: ${t.message}")
                        t.printStackTrace()
                        // On error, proceed normally to avoid breaking notification system
                        return chain.proceed()
                    }
                }
            })

        log("shouldMuteNotificationLocked hooked successfully")
    }

    /**
     * Extract package name from NotificationRecord.
     * Uses reflection to call getSbn() then getPackageName().
     */
    private fun extractPackageName(record: Any): String {
        try {
            // NotificationRecord.getSbn() returns StatusBarNotification
            val sbn = record.javaClass.getMethod("getSbn").invoke(record)
                ?: throw IllegalStateException("NotificationRecord.getSbn() returned null")

            // StatusBarNotification.getPackageName() returns String
            return sbn.javaClass.getMethod("getPackageName").invoke(sbn) as? String
                ?: throw IllegalStateException("StatusBarNotification.getPackageName() returned null")
        } catch (e: NoSuchMethodException) {
            throw IllegalStateException("NotificationRecord API changed: method not found - ${e.message}", e)
        } catch (e: ClassCastException) {
            throw IllegalStateException("NotificationRecord API changed: type mismatch - ${e.message}", e)
        }
    }

    private fun log(msg: String) = module.log(Log.WARN, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val NOTIFICATION_ATTENTION_HELPER = "com.android.server.notification.NotificationAttentionHelper"
        private const val NOTIFICATION_SIGNALS = "com.android.server.notification.NotificationAttentionHelper\$Signals"
        private const val NOTIFICATION_RECORD = "com.android.server.notification.NotificationRecord"
        internal const val MUTE_REASON_INTERCEPTED = 512

        internal fun notificationMuteOverride(
            notificationConfig: NotificationConfig?,
            packageName: String
        ): Int? {
            if (notificationConfig == null) return null
            if (notificationConfig.dndLevel == DndLevel.ALARMS) return null
            if (notificationConfig.dndLevel == DndLevel.DISABLED &&
                notificationConfig.allowedApps.isEmpty()
            ) return null
            return if (packageName in notificationConfig.allowedApps) {
                null
            } else {
                MUTE_REASON_INTERCEPTED
            }
        }
    }
}
