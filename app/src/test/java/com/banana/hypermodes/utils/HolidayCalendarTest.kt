package com.banana.hypermodes.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar
import java.util.GregorianCalendar

class HolidayCalendarTest {

    private fun cal(y: Int, m: Int, d: Int): Calendar =
        GregorianCalendar(y, m - 1, d)

    /** 调休上班日：即使落在周末，也必须是工作日。 */
    @Test
    fun `2026 makeup workdays on weekends are workdays`() {
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 1, 4)))   // 元旦调休 周日
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 2, 14)))  // 春节调休 周六
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 2, 28)))  // 春节调休 周六
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 5, 9)))   // 劳动节调休 周六
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 9, 20)))  // 国庆调休 周日
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 10, 10))) // 国庆调休 周六
    }

    /** 官方放假日：即使落在工作日，也必须是节假日。 */
    @Test
    fun `2026 official holidays on weekdays are holidays`() {
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 1, 1)))   // 元旦 周四
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 2, 17)))  // 除夕 周二
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 5, 1)))   // 劳动节 周五
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 10, 1)))  // 国庆 周四
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 2, 23)))  // 春节最后一天 周一
    }

    /** 假期结束后第一个工作日恢复正常。 */
    @Test
    fun `2026 first workday after spring festival`() {
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 2, 24))) // 正月初八 周二
    }

    /** 普通工作日/周末回退规则。 */
    @Test
    fun `plain weekday and weekend fallback`() {
        assertFalse(HolidayCalendar.isHoliday(cal(2026, 8, 13))) // 周四
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 8, 16)))  // 周日
        assertFalse(HolidayCalendar.isWorkday(cal(2026, 8, 16)))
    }

    /** 内置官方表覆盖 2011-2026（数据来自官方接口，versioncode 24）。 */
    @Test
    fun `builtin table covers 2011 through 2026`() {
        assertTrue(HolidayCalendar.isHoliday(cal(2011, 10, 1)))  // 2011 国庆
        assertTrue(HolidayCalendar.isHoliday(cal(2015, 2, 19)))  // 2015 春节
        assertFalse(HolidayCalendar.isHoliday(cal(2021, 2, 7)))  // 2021 春节调休 周日
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 5, 1)))   // 2026 劳动节
        assertTrue(HolidayCalendar.isHoliday(cal(2026, 8, 16)))  // 2026 普通周日
    }
}
