package com.android.deskclock.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import miuix.appcompat.app.floatingactivity.multiapp.MethodCodeHelper;

/* JADX INFO: loaded from: classes.dex */
public class TimeUtil {
    private static final String DEFAULT_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATE_CN = "yyyy年MM月dd日";
    public static final String FORMAT_DATE_EN = "yyyy-MM-dd";
    public static final String FORMAT_DAY_CN = "HH时mm分ss秒";
    public static final String FORMAT_DAY_CN_2 = "HH时mm分";
    public static final String FORMAT_DAY_EN = "HH:mm:ss";
    public static final String FORMAT_DAY_EN_2 = "HH:mm";
    public static final String FORMAT_DAY_EN_3 = "mm:ss";
    public static final String FORMAT_DAY_EN_4 = "HH";
    public static final String FORMAT_TIME_CN_2 = "yyyy年MM月dd HH时mm分";
    public static final String FORMAT_TIME_EN = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME_EN_2 = "yyyy-MM-dd HH:mm";
    public static final long SIXTY_HEXADECIMAL = 60;
    private static final String TAG = "DC:TimeUtil";
    public static final int TIME_AFTER = 3;
    public static final int TIME_BEFORE = 1;
    public static final int TIME_ING = 2;
    public static final String FORMAT_TIME_CN = "yyyy年MM月dd HH时mm分ss秒";
    private static final SimpleDateFormat SDF = new SimpleDateFormat(FORMAT_TIME_CN, Locale.CHINA);

    public static int betweenTime(long j, long j2, long j3) {
        if (j2 > j3) {
            j2 = j3;
            j3 = j2;
        }
        if (j2 > j) {
            return 1;
        }
        return j3 < j ? 3 : 2;
    }

    public static int getYear() {
        return Calendar.getInstance().get(1);
    }

    public static int getMonth() {
        return Calendar.getInstance().get(2) + 1;
    }

    public static int getCurrentMonthDay() {
        return Calendar.getInstance().get(5);
    }

    public static int getWeekDay() {
        return Calendar.getInstance().get(7) - 1;
    }

    public static int getHour() {
        return Calendar.getInstance().get(11);
    }

    public static int getMinute() {
        return Calendar.getInstance().get(12);
    }

    public static String getFormatTime() {
        return getFormatTime(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormatTime(long j) {
        return getFormatTime(j, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getFormatTime(String str) {
        return getFormatTime(System.currentTimeMillis(), str);
    }

    public static String getFormatTime(long j, String str) {
        try {
            return new SimpleDateFormat(str).format(Long.valueOf(j));
        } catch (Exception unused) {
            return String.valueOf(j);
        }
    }

    public static String composeTime(int i, int i2) {
        return i + MethodCodeHelper.IDENTITY_INFO_SEPARATOR + i2;
    }

    private static String format(String str, Date date) {
        SimpleDateFormat simpleDateFormat = SDF;
        simpleDateFormat.setTimeZone(TimeZone.getDefault());
        simpleDateFormat.applyPattern(str);
        return simpleDateFormat.format(date);
    }

    public static String formatDate(String str, Date date) {
        return formatDate(str, date, null);
    }

    public static String formatDate(String str, Date date, TimeZone timeZone) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(str);
        if (timeZone != null) {
            simpleDateFormat.setTimeZone(timeZone);
        }
        return simpleDateFormat.format(date);
    }

    public static String convertToTime(long j) {
        return convertToTime("yyyy-MM-dd HH:mm:ss", j);
    }

    public static int convertToHour(long j) {
        return Integer.parseInt(convertToTime(FORMAT_DAY_EN_4, j));
    }

    public static String convertToTime(String str, long j) {
        return convertToTime(str, new Date(j));
    }

    public static String convertToDifftime(String str, long j) {
        Date date = new Date(j);
        SimpleDateFormat simpleDateFormat = SDF;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        simpleDateFormat.applyPattern(str);
        return simpleDateFormat.format(date);
    }

    public static long convertDateTolong(String str, String str2) {
        try {
            return new SimpleDateFormat(str2).parse(str).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L;
        }
    }

    public static String convertToTime(String str, Date date) {
        return format(str, date);
    }

    public static String convertToTime(String str, Calendar calendar) {
        return format(str, calendar.getTime());
    }

    public static long covertToLong(String str, String str2) {
        try {
            return SDF.parse(str2).getTime();
        } catch (ParseException e) {
            android.util.Log.e(TAG, e.getMessage());
            return -1L;
        }
    }

    public static Date convertToTDate(String str, long j) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return simpleDateFormat.parse(simpleDateFormat.format(Long.valueOf(j)));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int transformDurationToMins(long j) {
        int i = (int) (j / 60000);
        if (i != 0 || j <= 0) {
            return i;
        }
        return 1;
    }

    public static int getHourFromDuration(long j) {
        return (int) (j / AlarmHelper.ARRIVING_ALARM_DURATION);
    }

    public static int getMinuteFromDuration(long j) {
        return (int) ((j / 60000) % 60);
    }

    public static int getSecondFromDuration(long j) {
        return (int) ((j / 1000) % 60);
    }
}
