package com.banana.hypermodes.ui

import kotlin.math.cos
import kotlin.math.sin

/**
 * Coordinate conversion utilities for Xiaomi Polaris geofencing.
 *
 * The picker returns GCJ-02 coordinates in China. Polaris expects coordinates
 * with the GCJ offset removed (WGS-84 approximation).
 */
object XiaomiLocationCoordinates {

    // China bounds for GCJ-02 applicability
    private const val LON_MIN = 72.004
    private const val LON_MAX = 137.8347
    private const val LAT_MIN = 0.8293
    private const val LAT_MAX = 55.8271

    /**
     * Convert picker coordinates to Polaris-ready coordinates.
     *
     * Inside China: subtract GCJ-02 offset to approximate WGS-84.
     * Outside China: return unchanged.
     *
     * Returns null for invalid inputs (NaN, infinity, out of range, zero).
     */
    fun toPolaris(latitude: Double, longitude: Double): Pair<Double, Double>? {
        // Validate inputs
        if (!latitude.isFinite() || !longitude.isFinite()) return null
        if (latitude == 0.0 && longitude == 0.0) return null
        if (latitude < -90 || latitude > 90) return null
        if (longitude < -180 || longitude > 180) return null

        // Check if in China
        val inChina = longitude in LON_MIN..LON_MAX && latitude in LAT_MIN..LAT_MAX

        if (!inChina) {
            return latitude to longitude
        }

        // Inside China: subtract GCJ offset
        val (deltaLat, deltaLon) = computeGCJOffset(latitude, longitude)
        return (latitude - deltaLat) to (longitude - deltaLon)
    }

    private fun computeGCJOffset(lat: Double, lon: Double): Pair<Double, Double> {
        val a = 6378245.0
        val ee = 0.00669342162296594323

        val dLat = transformLat(lon - 105.0, lat - 35.0)
        val dLon = transformLon(lon - 105.0, lat - 35.0)
        val radLat = lat / 180.0 * Math.PI
        var magic = sin(radLat)
        magic = 1 - ee * magic * magic
        val sqrtMagic = kotlin.math.sqrt(magic)
        val deltaLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * Math.PI)
        val deltaLon = (dLon * 180.0) / (a / sqrtMagic * cos(radLat) * Math.PI)

        return deltaLat to deltaLon
    }

    private fun transformLat(x: Double, y: Double): Double {
        var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * kotlin.math.sqrt(kotlin.math.abs(x))
        ret += (20.0 * sin(6.0 * x * Math.PI) + 20.0 * sin(2.0 * x * Math.PI)) * 2.0 / 3.0
        ret += (20.0 * sin(y * Math.PI) + 40.0 * sin(y / 3.0 * Math.PI)) * 2.0 / 3.0
        ret += (160.0 * sin(y / 12.0 * Math.PI) + 320 * sin(y * Math.PI / 30.0)) * 2.0 / 3.0
        return ret
    }

    private fun transformLon(x: Double, y: Double): Double {
        var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * kotlin.math.sqrt(kotlin.math.abs(x))
        ret += (20.0 * sin(6.0 * x * Math.PI) + 20.0 * sin(2.0 * x * Math.PI)) * 2.0 / 3.0
        ret += (20.0 * sin(x * Math.PI) + 40.0 * sin(x / 3.0 * Math.PI)) * 2.0 / 3.0
        ret += (150.0 * sin(x / 12.0 * Math.PI) + 300.0 * sin(x / 30.0 * Math.PI)) * 2.0 / 3.0
        return ret
    }
}
