package com.banana.hypermodes.utils

import android.content.Context
import android.provider.Settings
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.int
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar
import java.util.concurrent.Executors

/**
 * 节假日/工作日判断（官方调休判定，与小米闹钟一致）。
 *
 * 判定顺序（同 DeskClock HolidayHelper.isHoliday）：
 *   1. 该日在官方调休上班表（workday）→ 工作日（即使落在周末）；
 *   2. 该日在官方放假日表（freeday）→ 节假日（即使落在工作日）；
 *   3. 否则回退基础规则：周一至周五 = 工作日，周六/周日 = 节假日。
 *
 * 数据源：[HolidayData] 内置 2011-2026 官方表（与闹钟 res/raw/holiday.json
 * 同源，versioncode 24）；启动时优先读 Settings.Global 缓存，并每日一次
 * 从官方接口（与闹钟同一 URL）拉取最新调休表异步刷新、回写缓存。
 */
object HolidayCalendar {

    private const val TAG = "HolidayCalendar"

    /** 与小米闹钟相同的官方节假日数据接口。 */
    private const val API_URL = "https://api.comm.miui.com/holiday/holiday.jsp"

    private const val CACHE_KEY = "pixel_routines_holiday_json"
    private const val LAST_REFRESH_KEY = "pixel_routines_holiday_refresh_time"

    /** 刷新节流：两次尝试之间至少间隔 24 小时。 */
    private const val REFRESH_INTERVAL_MS = 24L * 60 * 60 * 1000

    private data class YearData(
        val workdays: Set<Int>,
        val freedays: Set<Int>
    )

    private val json = Json { ignoreUnknownKeys = true }

    @Volatile
    private var table: Map<Int, YearData> = parse(HolidayData.HOLIDAY_JSON)

    @Volatile
    private var cacheLoaded = false

    private val refreshExecutor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "hypermodes-holiday").apply { isDaemon = true }
    }

    /**
     * 是否节假日（含官方调休判定）。与闹钟一致：调休上班日不是节假日；
     * 官方放假日是节假日；其余回退为周末判定。
     */
    fun isHoliday(cal: Calendar): Boolean {
        val year = cal.get(Calendar.YEAR)
        val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
        val yearData = table[year]
        if (yearData != null) {
            // 调休上班日（通常是周末）→ 不是节假日
            if (dayOfYear in yearData.workdays) return false
            // 官方放假日（通常是调休连休）→ 是节假日
            if (dayOfYear in yearData.freedays) return true
        }
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        return dow == Calendar.SATURDAY || dow == Calendar.SUNDAY
    }

    fun isWorkday(cal: Calendar): Boolean = !isHoliday(cal)

    /**
     * 每日最多一次，从官方接口刷新节假日/调休表并缓存到 Settings.Global。
     * 异步执行，失败静默（保留现有表）。
     */
    fun refreshIfStale(context: Context) {
        if (!cacheLoaded) {
            loadCache(context)
        }
        val appContext = context.applicationContext
        val resolver = appContext.contentResolver
        val lastAttempt = try {
            Settings.Global.getLong(resolver, LAST_REFRESH_KEY, 0L)
        } catch (e: Exception) {
            0L
        }
        if (System.currentTimeMillis() - lastAttempt < REFRESH_INTERVAL_MS) return

        try {
            Settings.Global.putLong(resolver, LAST_REFRESH_KEY, System.currentTimeMillis())
        } catch (_: Exception) {
            // 写失败不影响刷新
        }

        refreshExecutor.execute {
            runCatching {
                val connection = URL(API_URL).openConnection() as HttpURLConnection
                try {
                    connection.connectTimeout = 10_000
                    connection.readTimeout = 10_000
                    val body = connection.inputStream.bufferedReader().use { it.readText() }
                    val updated = parse(body)
                    if (updated.isNotEmpty()) {
                        table = updated
                        try {
                            Settings.Global.putString(resolver, CACHE_KEY, body)
                        } catch (_: Exception) {
                            // 缓存写失败不影响内存表
                        }
                        val years = updated.keys
                        HyperLog.i(TAG, "Holiday data refreshed (${years.minOrNull()}-${years.maxOrNull()})")
                    }
                } finally {
                    connection.disconnect()
                }
            }.onFailure {
                HyperLog.w(TAG, "Holiday refresh failed: ${it.message}")
            }
        }
    }

    /** 优先加载 Settings.Global 中缓存的最新官方数据（若有）。 */
    fun loadCache(context: Context) {
        runCatching {
            val cached = Settings.Global.getString(
                context.applicationContext.contentResolver,
                CACHE_KEY
            ) ?: return
            val updated = parse(cached)
            if (updated.isNotEmpty()) {
                table = updated
                HyperLog.i(TAG, "Loaded cached holiday data")
            }
            cacheLoaded = true
        }
    }

    /** 解析官方 holiday.json（{"versioncode":N,"holiday":[{year,workday,freeday}]}）。 */
    private fun parse(jsonText: String): Map<Int, YearData> {
        val root = json.parseToJsonElement(jsonText).jsonObject
        val holidayArray = root["holiday"]?.jsonArray ?: return emptyMap()
        val result = LinkedHashMap<Int, YearData>()
        for (entry in holidayArray) {
            val obj = entry.jsonObject
            val year = obj["year"]?.jsonPrimitive?.intOrNull ?: continue
            val workdays = obj["workday"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.intOrNull }
                ?.toSet() ?: emptySet()
            val freedays = obj["freeday"]?.jsonArray
                ?.mapNotNull { it.jsonPrimitive.intOrNull }
                ?.toSet() ?: emptySet()
            result[year] = YearData(workdays, freedays)
        }
        return result
    }

    private val JsonPrimitive.intOrNull: Int?
        get() = runCatching { int }.getOrNull()
}
