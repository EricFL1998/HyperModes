package com.banana.hypermodes.systemserver.hooks

import android.util.Log
import com.banana.hypermodes.systemserver.RoutineCoreEngine
import com.banana.hypermodes.systemserver.config.DndLevel
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModule

/**
 * Notification filtering hook running INSIDE system_server.
 *
 * Intercepts NotificationManagerService.shouldMuteNotificationLocked to filter
 * notifications based on the active mode's configuration.
 *
 * Logic:
 * - If no mode is active → proceed normally
 * - If DND level is ALARMS → proceed normally (system DND handles it)
 * - If DND level is NONE or PRIORITY:
 *   - Check if notification's package is in allowedApps whitelist
 *   - If allowed → return false (don't mute)
 *   - If not allowed → return true (mute)
 */
class NotificationFilterHook(private val module: XposedModule) {

    fun install(classLoader: ClassLoader) {
        log("NotificationFilterHook.install starting")

        val nms = try {
            classLoader.loadClass(NOTIFICATION_MANAGER_SERVICE)
        } catch (t: Throwable) {
            log("NotificationManagerService not found: ${t.message}")
            return
        }

        log("NotificationManagerService found, installing hook")
        hookShouldMuteNotification(nms, classLoader)
        log("NotificationFilterHook.install complete")
    }

    /**
     * Hook NotificationManagerService.shouldMuteNotificationLocked to filter
     * notifications based on active mode configuration.
     *
     * Method signature (AOSP):
     * boolean shouldMuteNotificationLocked(NotificationRecord record)
     */
    private fun hookShouldMuteNotification(nms: Class<*>, classLoader: ClassLoader) {
        val notificationRecordClass = try {
            classLoader.loadClass(NOTIFICATION_RECORD)
        } catch (t: Throwable) {
            log("NotificationRecord class not found: ${t.message}")
            return
        }

        val method = try {
            nms.getDeclaredMethod(
                "shouldMuteNotificationLocked",
                notificationRecordClass
            ).apply { isAccessible = true }
        } catch (t: Throwable) {
            log("shouldMuteNotificationLocked method not found: ${t.message}")
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

                        // If DND is disabled for this mode, or level is ALARMS, let system handle it
                        if (notificationConfig.dndLevel == DndLevel.DISABLED || 
                            notificationConfig.dndLevel == DndLevel.ALARMS) {
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
                        val isAllowed = notificationConfig.allowedApps.contains(packageName)

                        if (isAllowed) {
                            // Don't mute - allow the notification
                            log("Allowing notification from $packageName (mode=${activeMode.name})")
                            return false
                        } else {
                            // Mute the notification
                            log("Muting notification from $packageName (mode=${activeMode.name})")
                            return true
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

    private fun log(msg: String) = module.log(Log.INFO, TAG, msg)

    companion object {
        private const val TAG = "HyperModes"
        private const val NOTIFICATION_MANAGER_SERVICE = "com.android.server.notification.NotificationManagerService"
        private const val NOTIFICATION_RECORD = "com.android.server.notification.NotificationRecord"
    }
}
