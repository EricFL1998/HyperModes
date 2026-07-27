package com.banana.hypermodes.utils

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.Display
import android.view.WindowManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manages detection and caching of supported device refresh rates.
 */
object RefreshRateManager {
    private const val TAG = "RefreshRateManager"
    private const val CACHE_KEY = "hypermodes_supported_refresh_rates"

    /**
     * Initializes the manager by detecting and caching supported refresh rates.
     * Should be called on app startup.
     */
    fun initialize(context: Context) {
        if (getCachedRefreshRates(context).isNotEmpty()) return

        val rates = detectSupportedRefreshRates(context)
        if (rates.isNotEmpty()) {
            try {
                Settings.Global.putString(
                    context.contentResolver,
                    CACHE_KEY,
                    Json.encodeToString(rates)
                )
                Log.i(TAG, "Cached refresh rates: $rates")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to cache refresh rates", e)
            }
        }
    }

    /**
     * Returns the cached supported refresh rates.
     */
    fun getCachedRefreshRates(context: Context): List<Int> {
        return try {
            val json = Settings.Global.getString(context.contentResolver, CACHE_KEY)
            if (json != null) {
                Json.decodeFromString<List<Int>>(json)
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun detectSupportedRefreshRates(context: Context): List<Int> {
        val rates = mutableSetOf<Int>()

        // Method 1: FeatureParser (MIUI/HyperOS specific)
        try {
            val featureParserClass = Class.forName("miui.util.FeatureParser")
            val getIntArrayMethod = featureParserClass.getMethod("getIntArray", String::class.java)
            val fpsList = getIntArrayMethod.invoke(null, "fpsList") as? IntArray
            fpsList?.forEach { rates.add(it) }
        } catch (e: Exception) {
            Log.d(TAG, "FeatureParser check failed: ${e.message}")
        }

        // Method 2: Android Display API
        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = windowManager.defaultDisplay
            display.supportedModes.forEach { mode ->
                rates.add(mode.refreshRate.toInt())
            }
        } catch (e: Exception) {
            Log.d(TAG, "Display API check failed: ${e.message}")
        }

        // Fallback defaults if everything fails
        if (rates.isEmpty()) {
            rates.addAll(listOf(60, 90, 120, 144))
        }

        return rates.toList().sorted()
    }
}
