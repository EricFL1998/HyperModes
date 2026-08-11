package com.banana.hypermodes.systemserver.executor

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log

/**
 * 在 system_server 内开关个人热点，直接用系统 API flip switch，
 * 等价于系统设置里的热点开关。避免应用进程的 root shell 或
 * 已废弃的 setWifiApEnabled 反射（Android 11+ 已移除）。
 *
 * - 开启：WifiManager.startTetheredHotspot(SoftApConfiguration)
 * - 关闭：WifiManager.stopSoftAp()
 *
 * @param context system_server 的 SystemContext
 */
class HotspotController(private val context: Context) {

    /** 当前热点是否开启（isSoftApEnabled 是公开 API）。 */
    fun isHotspotEnabled(): Boolean {
        return try {
            val wifi = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifi.javaClass.getMethod("isSoftApEnabled").invoke(wifi) as? Boolean ?: false
        } catch (t: Throwable) {
            log("isHotspotEnabled failed: ${t.message}")
            false
        }
    }

    /** 开关个人热点。返回是否成功。 */
    fun setHotspotEnabled(enabled: Boolean): Boolean {
        return try {
            val wifi = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (enabled) enable(wifi) else disable(wifi)
        } catch (t: Throwable) {
            log("setHotspotEnabled($enabled) failed: ${t.message}")
            false
        }
    }

    private fun enable(wifi: WifiManager): Boolean {
        // 读取当前保存的热点配置（保留用户设置的 SSID/密码），再开启
        val config = try {
            wifi.javaClass.getMethod("getSoftApConfiguration").invoke(wifi)
        } catch (t: Throwable) {
            null
        }
        val sacClass = Class.forName("android.net.wifi.SoftApConfiguration")
        val method = wifi.javaClass.getMethod("startTetheredHotspot", sacClass)
        val result = method.invoke(wifi, config) as? Boolean ?: true
        log("enable result: $result")
        return result
    }

    private fun disable(wifi: WifiManager): Boolean {
        val method = wifi.javaClass.getMethod("stopSoftAp")
        val result = method.invoke(wifi) as? Boolean ?: true
        log("disable result: $result")
        return result
    }

    private fun log(msg: String) = Log.i(TAG, msg)

    companion object {
        private const val TAG = "HyperModes.Hotspot"
    }
}
