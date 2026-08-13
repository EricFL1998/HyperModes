package com.banana.hypermodes.systemserver.executor

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.provider.Settings
import android.bluetooth.BluetoothManager
import android.telephony.SubscriptionManager
import android.util.Log
import com.banana.hypermodes.utils.HyperLog

/**
 * system_server 内的统一特权操作执行器。自动化里所有需要系统级权限的
 * 操作都收敛到这里，用系统 API / 直接写 Settings 实现，不再依赖 root shell。
 *
 * 被两类调用方使用：
 * - SystemAutomationEngine 直接持有（零进程自动化）
 * - SystemModeHook 的 ACTION_SYSTEM_OP bridge（应用进程广播转发）
 */
class SystemOpsExecutor(private val context: Context) {

    /** 直接写 Settings（namespace: system/secure/global）。system_server 有全部写权限。 */
    fun writeSetting(namespace: String, key: String, value: String): Boolean {
        return try {
            val cr = context.contentResolver
            val ok = when (namespace) {
                "system" -> Settings.System.putString(cr, key, value)
                "secure" -> Settings.Secure.putString(cr, key, value)
                "global" -> Settings.Global.putString(cr, key, value)
                else -> false
            }
            log("writeSetting($namespace/$key=$value) -> $ok")
            ok
        } catch (t: Throwable) {
            log("writeSetting failed: ${t.message}")
            false
        }
    }

    /** 飞行模式：反射 ConnectivityManager.setAirplaneMode（系统设置同款路径）。 */
    fun setAirplaneEnabled(enabled: Boolean): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            val method = cm.javaClass.getDeclaredMethod("setAirplaneMode", Boolean::class.java)
            method.isAccessible = true
            method.invoke(cm, enabled)
            log("setAirplaneEnabled($enabled)")
            true
        } catch (t: Throwable) {
            log("setAirplaneEnabled failed: ${t.message}")
            false
        }
    }

    /** 移动数据：反射 ConnectivityManager.setMobileDataEnabled。 */
    fun setMobileDataEnabled(enabled: Boolean): Boolean {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE)
            val method = cm.javaClass.getMethod("setMobileDataEnabled", Boolean::class.java)
            method.invoke(cm, enabled)
            log("setMobileDataEnabled($enabled)")
            true
        } catch (t: Throwable) {
            log("setMobileDataEnabled failed: ${t.message}")
            false
        }
    }

    /** 手电筒：反射 FlashlightController.setFlashlightEnabled（cmd flashlight 同款服务）。 */
    fun setFlashlightEnabled(enabled: Boolean): Boolean {
        return try {
            val localServices = Class.forName("com.android.server.LocalServices")
            val getService = localServices.getMethod("getService", Class::class.java)
            val controllerClass = Class.forName("com.android.server.flashlight.FlashlightController")
            val controller = getService.invoke(null, controllerClass)
            val method = controllerClass.getMethod("setFlashlightEnabled", Boolean::class.java)
            method.invoke(controller, enabled)
            log("setFlashlightEnabled($enabled)")
            true
        } catch (t: Throwable) {
            log("setFlashlightEnabled failed: ${t.message}")
            false
        }
    }

    /** 默认数据 SIM 卡槽：反射 SubscriptionManager.setDefaultDataSubId。 */
    fun setPreferredSimSlot(slot: Int): Boolean {
        return try {
            val sm = context.getSystemService(SubscriptionManager::class.java)
                ?: return false
            val infos = sm.activeSubscriptionInfoList ?: emptyList()
            val target = infos.firstOrNull { it.simSlotIndex == slot }
                ?: run {
                    log("setPreferredSimSlot: no subscription in slot $slot")
                    return false
                }
            val method = SubscriptionManager::class.java.getMethod("setDefaultDataSubId", Int::class.java)
            method.invoke(sm, target.subscriptionId)
            log("setPreferredSimSlot(slot $slot -> subId ${target.subscriptionId})")
            true
        } catch (t: Throwable) {
            log("setPreferredSimSlot failed: ${t.message}")
            false
        }
    }

    /** 防晕车：写 MIUI 开关 + 通知 securitycenter 启停服务（与 DeviceController 同款实现）。 */
    fun setMotionSicknessReliefEnabled(enabled: Boolean): Boolean {
        return try {
            Settings.System.putInt(
                context.contentResolver,
                "settings_car_sickness_mode",
                if (enabled) 1 else 0
            )
            if (enabled) {
                val intent = Intent().apply {
                    component = ComponentName(
                        "com.miui.securitycenter",
                        "com.miui.carsickness.service.CarSicknessService"
                    )
                    action = "miui.carsickness.remind_always"
                }
                context.startService(intent)
            } else {
                runCatching {
                    context.sendBroadcast(
                        Intent("com.miui.action.carsickness_relief_close")
                    )
                }
                runCatching {
                    context.startService(
                        Intent().apply {
                            component = ComponentName(
                                "com.miui.securitycenter",
                                "com.miui.carsickness.service.CarSicknessService"
                            )
                            action = "miui.carsickness.close_car_sickness"
                        }
                    )
                }
                runCatching {
                    context.stopService(
                        Intent().apply {
                            component = ComponentName(
                                "com.miui.securitycenter",
                                "com.miui.carsickness.service.CarSicknessService"
                            )
                        }
                    )
                }
            }
            log("setMotionSicknessReliefEnabled($enabled)")
            true
        } catch (t: Throwable) {
            log("setMotionSicknessReliefEnabled failed: ${t.message}")
            false
        }
    }

    /** WiFi 开关：system_server 有 CHANGE_WIFI_STATE 特权，直接调 WifiManager。 */
    fun setWifiEnabled(enabled: Boolean): Boolean {
        return try {
            val wm = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            @Suppress("DEPRECATION")
            wm.setWifiEnabled(enabled)
            log("setWifiEnabled($enabled)")
            true
        } catch (t: Throwable) {
            log("setWifiEnabled failed: ${t.message}")
            false
        }
    }

    /** 蓝牙开关：system_server 有 BLUETOOTH_ADMIN 特权。 */
    fun setBluetoothEnabled(enabled: Boolean): Boolean {
        return try {
            val adapter = context.getSystemService(BluetoothManager::class.java)?.adapter
                ?: return false
            @Suppress("DEPRECATION")
            if (enabled) adapter.enable() else adapter.disable()
            log("setBluetoothEnabled($enabled)")
            true
        } catch (t: Throwable) {
            log("setBluetoothEnabled failed: ${t.message}")
            false
        }
    }

    private fun log(msg: String) = HyperLog.i(TAG, msg)

    companion object {
        private const val TAG = "SystemOpsExecutor"
    }
}
