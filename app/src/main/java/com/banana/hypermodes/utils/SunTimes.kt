package com.banana.hypermodes.utils

import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/**
 * 日出/日落天文计算（NOAA 简化算法）。
 *
 * 输入当天日期、观测点经纬度（十进制度）、设备当前时区偏移（分钟），
 * 返回当天日出/日落的本地时间（分钟，0..1440）。
 * 极昼/极夜（无日出或日落）时对应值为 null。
 */
object SunTimes {

    data class Result(
        val sunriseMinutes: Double?,
        val sunsetMinutes: Double?
    )

    fun compute(
        year: Int,
        month: Int, // 1..12
        day: Int,
        latitude: Double,
        longitude: Double,
        timezoneOffsetMinutes: Int
    ): Result {
        if (latitude !in -90.0..90.0 || longitude !in -180.0..180.0) {
            return Result(null, null)
        }

        // Day of year (1-based)
        val n1 = floor(275.0 * month / 9.0)
        val n2 = floor((month + 9.0) / 12.0)
        val n3 = 1.0 + floor((year - 4.0 * floor(year / 4.0) + 2.0) / 3.0)
        val n = n1 - n2 * n3 + day - 30.0

        val lngHour = longitude / 15.0

        val sunrise = eventMinutes(
            n = n,
            lngHour = lngHour,
            latitude = latitude,
            tApprox = n + ((6.0 - lngHour) / 24.0),
            rising = true,
            timezoneOffsetMinutes = timezoneOffsetMinutes
        )
        val sunset = eventMinutes(
            n = n,
            lngHour = lngHour,
            latitude = latitude,
            tApprox = n + ((18.0 - lngHour) / 24.0),
            rising = false,
            timezoneOffsetMinutes = timezoneOffsetMinutes
        )
        return Result(sunrise, sunset)
    }

    private fun eventMinutes(
        n: Double,
        lngHour: Double,
        latitude: Double,
        tApprox: Double,
        rising: Boolean,
        timezoneOffsetMinutes: Int
    ): Double? {
        val m = 0.9856 * tApprox - 3.289
        val l = norm360(
            m + 1.916 * sinDeg(m) + 0.020 * sinDeg(2.0 * m) + 282.634
        )
        var ra = toDeg(atan(0.91764 * tanDeg(l)))
        ra = norm360(ra)

        val lQuadrant = floor(l / 90.0) * 90.0
        val raQuadrant = floor(ra / 90.0) * 90.0
        ra = ra + (lQuadrant - raQuadrant)
        val raHours = ra / 15.0

        val sinDec = 0.39782 * sinDeg(l)
        val cosDec = cos(asin(sinDec))

        val cosH = (cos(toRad(90.833)) - sinDec * sinDeg(latitude)) /
            (cosDec * cosDeg(latitude))
        if (cosH < -1.0 || cosH > 1.0) return null

        val h = if (rising) {
            (360.0 - toDeg(acos(cosH))) / 15.0
        } else {
            toDeg(acos(cosH)) / 15.0
        }

        val t = h + raHours - 0.06571 * tApprox - 6.622
        val ut = norm360(t - lngHour)
        return normMinutes(ut * 60.0 + timezoneOffsetMinutes)
    }

    private fun norm360(v: Double): Double {
        var x = v % 360.0
        if (x < 0) x += 360.0
        return x
    }

    private fun normMinutes(v: Double): Double {
        var x = v % 1440.0
        if (x < 0) x += 1440.0
        return x
    }

    private fun sinDeg(v: Double): Double = sin(toRad(v))
    private fun cosDeg(v: Double): Double = cos(toRad(v))
    private fun tanDeg(v: Double): Double = tan(toRad(v))
    private fun toRad(v: Double): Double = Math.toRadians(v)
    private fun toDeg(v: Double): Double = Math.toDegrees(v)
}
