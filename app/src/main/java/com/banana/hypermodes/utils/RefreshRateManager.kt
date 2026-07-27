package com.banana.hypermodes.utils

import android.content.Context
import android.provider.Settings
import android.util.Log
import android.view.WindowManager
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Manages detection and caching of supported device refresh rates.
 */
object RefreshRateManager {
    private const val TAG = "RefreshRateManager"
    private const val CACHE_KEY = "hypermodes_supported_refresh_rates_v2"

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
        // Method 1: FeatureParser (MIUI/HyperOS specific) - Highly accurate
        try {
            val featureParserClass = Class.forName("miui.util.FeatureParser")
            val getIntArrayMethod = featureParserClass.getMethod("getIntArray", String::class.java)
            val fpsList = getIntArrayMethod.invoke(null, "fpsList") as? IntArray
            if (fpsList != null && fpsList.isNotEmpty()) {
                Log.i(TAG, "Using curated fpsList from FeatureParser")
                return fpsList.toList().sorted()
            }
        } catch (e: Exception) {
            Log.d(TAG, "FeatureParser check failed: ${e.message}")
        }

        // Method 2: Android Display API - Includes intermediate rates, so we filter
        val rates = mutableSetOf<Int>()
        try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = @Suppress("DEPRECATION") windowManager.defaultDisplay
            val standardRates = listOf(60, 90, 120, 144, 165)
            
            display.supportedModes.forEach { mode ->
                val rate = mode.refreshRate.toInt()
                if (rate in standardRates) {
                    rates.add(rate)
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Display API check failed: ${e.message}")
        }

        // Final fallback if no standard rates found or Display API failed
        if (rates.isEmpty()) {
            Log.i(TAG, "Falling back to default 60/120Hz")
            return listOf(60, 120)
        }

        return rates.toList().sorted()
    }
}
