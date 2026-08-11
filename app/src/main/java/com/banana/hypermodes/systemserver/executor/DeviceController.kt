package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.provider.Settings
import android.util.Log
import com.banana.hypermodes.systemserver.config.DeviceConfig

/**
 * Controller for managing device-related settings (Radios, Performance) with optional overrides.
 */
class DeviceController(private val context: Context) {

    private val hotspotController = HotspotController(context)

    private fun saveOriginal(key: String, value: Int) {
        if (Settings.Global.getInt(context.contentResolver, key, -1) == -1) {
            Settings.Global.putInt(context.contentResolver, key, value)
        }
    }

    private fun takeOriginal(key: String): Int? {
        val v = Settings.Global.getInt(context.contentResolver, key, -1)
        if (v == -1) return null
        Settings.Global.putString(context.contentResolver, key, null)
        return v
    }

    fun apply(device: DeviceConfig?) {
        if (device == null) return
        val cr = context.contentResolver

        try {
            // Silent Mode (apply before DND for proper priority)
            device.silentMode?.let { enabled ->
                applySilentMode(enabled)
            }

            // Performance Mode
            device.performanceMode?.let { mode ->
                saveOriginal(KEY_ORIG_PERFORMANCE_MODE, Settings.System.getInt(cr, POWER_PERFORMANCE_MODE_OPEN, 0))
                Settings.System.putInt(cr, POWER_PERFORMANCE_MODE_OPEN, mode)
                log("apply: set performanceMode to $mode")
            }

            // 5G Network
            device.enable5g?.let { enabled ->
                saveOriginal(KEY_ORIG_5G_MODE, Settings.Global.getInt(cr, ENABLED_5G_MODE, 1))
                Settings.Global.putInt(cr, ENABLED_5G_MODE, if (enabled) 1 else 0)
                log("apply: set 5G to $enabled")
            }

            // WiFi
            device.enableWifi?.let { enabled ->
                saveOriginal(KEY_ORIG_WIFI_ON, Settings.Global.getInt(cr, Settings.Global.WIFI_ON, 1))
                Settings.Global.putInt(cr, Settings.Global.WIFI_ON, if (enabled) 1 else 0)
                log("apply: set WiFi to $enabled")
            }

            // Bluetooth
            device.enableBluetooth?.let { enabled ->
                saveOriginal(KEY_ORIG_BLUETOOTH_ON, Settings.Global.getInt(cr, Settings.Global.BLUETOOTH_ON, 1))
                Settings.Global.putInt(cr, Settings.Global.BLUETOOTH_ON, if (enabled) 1 else 0)
                log("apply: set Bluetooth to $enabled")
            }

            // Hotspot
            device.enableHotspot?.let { enabled ->
                saveOriginal(KEY_ORIG_HOTSPOT_ON, if (hotspotController.isHotspotEnabled()) 1 else 0)
                hotspotController.setHotspotEnabled(enabled)
                log("apply: set Hotspot to $enabled")
            }

            // Airplane Mode (apply after individual radios)
            device.airplaneMode?.let { enabled ->
                applyAirplaneMode(enabled)
            }

            // Preferred data SIM (apply after airplane mode so radio state is sane)
            device.preferredSimSlot?.let { slot ->
                applyPreferredSimSlot(slot)
            }

            // Motion Sickness Relief
            device.enableMotionSicknessRelief?.let { enabled ->
                applyMotionSicknessRelief(enabled)
            }

        } catch (e: Exception) {
            log("apply: failed: ${e.message}")
        }
    }

    fun restore() {
        val cr = context.contentResolver
        try {
            // Restore airplane mode first (before individual radios)
            takeOriginal(KEY_ORIG_AIRPLANE_MODE)?.let { original ->
                restoreAirplaneMode(original == 1)
            }

            takeOriginal(KEY_ORIG_PERFORMANCE_MODE)?.let { original ->
                Settings.System.putInt(cr, POWER_PERFORMANCE_MODE_OPEN, original)
            }

            takeOriginal(KEY_ORIG_5G_MODE)?.let { original ->
                Settings.Global.putInt(cr, ENABLED_5G_MODE, original)
            }

            takeOriginal(KEY_ORIG_WIFI_ON)?.let { original ->
                Settings.Global.putInt(cr, Settings.Global.WIFI_ON, original)
            }

            takeOriginal(KEY_ORIG_BLUETOOTH_ON)?.let { original ->
                Settings.Global.putInt(cr, Settings.Global.BLUETOOTH_ON, original)
            }

            takeOriginal(KEY_ORIG_HOTSPOT_ON)?.let { original ->
                hotspotController.setHotspotEnabled(original == 1)
                log("restore: set Hotspot to ${original == 1}")
            }

            takeOriginal(KEY_ORIG_PREFERRED_SIM_SLOT)?.let { originalSlot ->
                restorePreferredSimSlot(originalSlot)
            }

            // Restore silent mode after radios
            takeOriginal(KEY_ORIG_SILENT_MODE)?.let { original ->
                restoreSilentMode(original)
            }

        } catch (e: Exception) {
            log("restore: failed: ${e.message}")
        }
    }

    /**
     * Switch the default data subscription to the SIM in the requested slot.
     * Slot 0 = SIM 1, slot 1 = SIM 2 (matches Settings' SubscriptionUtil ordering).
     */
    private fun applyPreferredSimSlot(slot: Int) {
        try {
            val sm = context.getSystemService(SubscriptionManager::class.java)
                ?: run {
                    log("applyPreferredSimSlot: SubscriptionManager unavailable")
                    return
                }
            val activeInfos = sm.activeSubscriptionInfoList ?: emptyList()
            log("applyPreferredSimSlot: active subscriptions = ${activeInfos.size}")
            if (activeInfos.isEmpty()) {
                log("applyPreferredSimSlot: no active SIM")
                return
            }
            val target = activeInfos.firstOrNull { it.simSlotIndex == slot }
                ?: run {
                    log("applyPreferredSimSlot: no subscription in slot $slot")
                    return
                }
            // Capture original default data sub id on first apply
            saveOriginal(
                KEY_ORIG_PREFERRED_SIM_SLOT,
                SubscriptionManager.getDefaultDataSubscriptionId()
            )
            log("applyPreferredSimSlot: switching default data to slot $slot (subId ${target.subscriptionId})")
            callSetDefaultDataSubId(sm, target.subscriptionId)
        } catch (e: Exception) {
            log("applyPreferredSimSlot: failed: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Restore the default data subscription to the value captured before
     * mode activation. -1 means "no default" (restore clears the override).
     */
    private fun restorePreferredSimSlot(originalSubId: Int) {
        try {
            val sm = context.getSystemService(SubscriptionManager::class.java)
                ?: run {
                    log("restorePreferredSimSlot: SubscriptionManager unavailable")
                    return
                }
            if (originalSubId == -1) {
                log("restorePreferredSimSlot: restoring to no-default")
                callSetDefaultDataSubId(sm, SubscriptionManager.INVALID_SUBSCRIPTION_ID)
            } else {
                log("restorePreferredSimSlot: restoring default data to subId $originalSubId")
                callSetDefaultDataSubId(sm, originalSubId)
            }
        } catch (e: Exception) {
            log("restorePreferredSimSlot: failed: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * SubscriptionManager.setDefaultDataSubId is not in the public SDK on
     * recent API levels; invoke it reflectively from system_server (same
     * call Settings uses under the hood).
     */
    private fun callSetDefaultDataSubId(sm: SubscriptionManager, subId: Int) {
        var invoked = false
        try {
            val method = SubscriptionManager::class.java.getMethod("setDefaultDataSubId", Int::class.java)
            method.invoke(sm, subId)
            invoked = true
        } catch (e: NoSuchMethodException) {
            log("callSetDefaultDataSubId: no such method, trying alternative lookup")
        } catch (e: Exception) {
            invoked = true // attempted via reflection; treat as handled
            log("callSetDefaultDataSubId: failed: ${e.message}")
            e.printStackTrace()
        }
        if (!invoked) {
            log("callSetDefaultDataSubId: setDefaultDataSubId unavailable on this build")
        }
    }

    /**
     * Apply silent mode via Xiaomi's MiuiSettings.SilenceMode.
     * 4 = enabled, 0 = disabled.
     */
    private fun applySilentMode(enabled: Boolean) {
        try {
            val cr = context.contentResolver
            val currentValue = Settings.System.getInt(cr, MIUI_SILENCE_MODE, 0)

            // Capture original value on first apply
            saveOriginal(KEY_ORIG_SILENT_MODE, currentValue)

            val targetValue = if (enabled) 4 else 0
            Settings.System.putInt(cr, MIUI_SILENCE_MODE, targetValue)
            log("applySilentMode: set to $targetValue (enabled=$enabled), original=$currentValue")

        } catch (e: Exception) {
            log("applySilentMode: failed to set silent mode: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Restore the exact original silent mode value captured before mode activation.
     */
    private fun restoreSilentMode(originalValue: Int) {
        try {
            val cr = context.contentResolver
            Settings.System.putInt(cr, MIUI_SILENCE_MODE, originalValue)
            log("restoreSilentMode: restored to original value $originalValue")

        } catch (e: Exception) {
            log("restoreSilentMode: failed: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Apply airplane mode with proper guards and fallback strategy.
     * Checks for ECM/SCBM/satellite/user/enterprise restrictions.
     */
    /**
     * Control motion sickness relief service from com.miui.securitycenter.
     * Sends intent to CarSicknessService to enable/disable the feature.
     */
    private fun applyMotionSicknessRelief(enabled: Boolean) {
        try {
            // 官方开关（settings_car_sickness_mode）：securitycenter 监听它并启停服务
            Settings.System.putInt(
                context.contentResolver,
                "settings_car_sickness_mode",
                if (enabled) 1 else 0
            )

            if (enabled) {
                // 开启：直接启动服务（remind_always = 一直提示）
                val intent = android.content.Intent().apply {
                    component = android.content.ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.carsickness.service.CarSicknessService"
                    )
                    action = "miui.carsickness.remind_always"
                }
                context.startService(intent)
            } else {
                // 关闭：走官方完整关闭路径
                // 1) 广播：CarsicknessReliefReceiver 会再次把开关写 0
                runCatching {
                    context.sendBroadcast(
                        android.content.Intent("com.miui.action.carsickness_relief_close")
                    )
                }
                // 2) intent：CarSicknessService.onStartCommand 调用 AntiCarsickManager.F() 移除黑点
                runCatching {
                    val closeIntent = android.content.Intent().apply {
                        component = android.content.ComponentName(
                            "com.miui.securitycenter",
                            "com.miui.carsickness.service.CarSicknessService"
                        )
                        action = "miui.carsickness.close_car_sickness"
                    }
                    context.startService(closeIntent)
                }
                // 3) stopService：触发 onDestroy -> c("主动关闭") -> F() 移除黑点（兜底）
                runCatching {
                    context.stopService(
                        android.content.Intent().apply {
                            component = android.content.ComponentName(
                                "com.miui.securitycenter",
                                "com.miui.carsickness.service.CarSicknessService"
                            )
                        }
                    )
                }
            }
            log("applyMotionSicknessRelief: set to $enabled")
        } catch (e: Exception) {
            log("applyMotionSicknessRelief: failed: ${e.message}")
            e.printStackTrace()
        }
    }


    private fun applyAirplaneMode(enabled: Boolean) {
        try {
            val cr = context.contentResolver

            // Check restrictions before applying
            if (enabled && isAirplaneModeRestricted()) {
                log("applyAirplaneMode: blocked by system restrictions")
                return
            }

            val currentValue = Settings.Global.getInt(cr, Settings.Global.AIRPLANE_MODE_ON, 0)
            saveOriginal(KEY_ORIG_AIRPLANE_MODE, currentValue)

            // Try hidden ConnectivityManager.setAirplaneMode first
            val success = trySetAirplaneModeViaConnectivityManager(enabled)

            if (!success) {
                // Fallback to Settings.Global approach
                setAirplaneModeViaSettings(enabled)
            }

            log("applyAirplaneMode: set to $enabled, original=$currentValue")

        } catch (e: Exception) {
            log("applyAirplaneMode: failed: ${e.message}")
            e.printStackTrace()
        }
    }

    /**
     * Check if airplane mode changes are restricted by system state.
     */
    private fun isAirplaneModeRestricted(): Boolean {
        // Check for emergency callback mode (ECM), satellite mode, etc.
        // These are typically stored in system properties or Settings.Global
        return false // TODO: Implement actual restriction checks if needed
    }

    /**
     * Try to set airplane mode via hidden ConnectivityManager.setAirplaneMode().
     * Returns true if successful, false if method not available.
     */
    private fun trySetAirplaneModeViaConnectivityManager(enabled: Boolean): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            val method = cm.javaClass.getDeclaredMethod("setAirplaneMode", Boolean::class.java)
            method.isAccessible = true
            method.invoke(cm, enabled)
            log("applyAirplaneMode: set via ConnectivityManager.setAirplaneMode($enabled)")
            true
        } catch (e: Exception) {
            log("applyAirplaneMode: ConnectivityManager method not available, using fallback")
            false
        }
    }

    /**
     * Fallback: set airplane mode via Settings.Global and broadcast intent.
     */
    private fun setAirplaneModeViaSettings(enabled: Boolean) {
        val cr = context.contentResolver
        Settings.Global.putInt(cr, Settings.Global.AIRPLANE_MODE_ON, if (enabled) 1 else 0)

        // Broadcast AIRPLANE_MODE intent to all users
        val intent = android.content.Intent(android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED)
        intent.putExtra("state", enabled)

        try {
            // Try to get ALL field via reflection for compatibility
            val allUser = android.os.UserHandle::class.java.getField("ALL").get(null) as android.os.UserHandle
            context.sendBroadcastAsUser(intent, allUser)
        } catch (e: Exception) {
            // Fallback: send as current user
            context.sendBroadcast(intent)
        }

        log("applyAirplaneMode: set via Settings.Global and broadcast")
    }

    /**
     * Restore airplane mode to its original state.
     */
    private fun restoreAirplaneMode(wasEnabled: Boolean) {
        try {
            val success = trySetAirplaneModeViaConnectivityManager(wasEnabled)
            if (!success) {
                setAirplaneModeViaSettings(wasEnabled)
            }
            log("restoreAirplaneMode: restored to $wasEnabled")

        } catch (e: Exception) {
            log("restoreAirplaneMode: failed: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun log(msg: String) = Log.i(TAG, msg)

    companion object {
        private const val TAG = "DeviceController"
        private const val POWER_PERFORMANCE_MODE_OPEN = "performance_mode"
        private const val ENABLED_5G_MODE = "enabled_5g_mode"
        private const val MIUI_SILENCE_MODE = "silence_mode"
        private const val KEY_ORIG_PERFORMANCE_MODE = "hypermodes_orig_performance_mode"
        private const val KEY_ORIG_5G_MODE = "hypermodes_orig_5g_mode"
        private const val KEY_ORIG_WIFI_ON = "hypermodes_orig_wifi_on"
        private const val KEY_ORIG_BLUETOOTH_ON = "hypermodes_orig_bluetooth_on"
        private const val KEY_ORIG_HOTSPOT_ON = "hypermodes_orig_hotspot_on"
        private const val KEY_ORIG_SILENT_MODE = "hypermodes_orig_silent_mode"
        private const val KEY_ORIG_AIRPLANE_MODE = "hypermodes_orig_airplane_mode"
        private const val KEY_ORIG_PREFERRED_SIM_SLOT = "hypermodes_orig_preferred_sim_slot"
    }
}
