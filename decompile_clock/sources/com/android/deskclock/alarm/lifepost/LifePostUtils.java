package com.android.deskclock.alarm.lifepost;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.text.format.DateFormat;
import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.addition.weather.WeatherUtils;
import com.android.deskclock.alarm.lifepost.model.LifePost;
import com.android.deskclock.settings.AlarmSettingsFragment;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.PrefUtil;
import com.android.deskclock.util.Util;
import com.android.deskclock.util.permission.UserNoticeUtil;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/* JADX INFO: loaded from: classes.dex */
public class LifePostUtils {
    private static final String TAG = "DC:LifePostUtils";
    private static String TIME_FORMAT = "HH:mm";
    private static final long TIME_IGNORE = 21600000;
    private static final HashMap<Integer, Integer> sWakeUpPercentage = new HashMap<>();

    public static boolean isGallerySupport(Context context) {
        return false;
    }

    private static void putWakeUpPercentage() {
        HashMap<Integer, Integer> map = sWakeUpPercentage;
        map.put(4, 100);
        map.put(5, 97);
        map.put(6, 84);
        map.put(7, 48);
        map.put(8, 16);
        map.put(9, 4);
    }

    public static boolean isLifePostEnabled() {
        return (MiuiSdk.isMiui15() || Util.isInternational() || !FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppContext()).getBoolean(AlarmSettingsFragment.KEY_LIFE_POST_STATE, true)) ? false : true;
    }

    public static boolean isShowLifePost(Context context, Alarm alarm) {
        return (!WeatherUtils.isFBEWeather() ? FBEUtil.isLockedUnderFBE(context) ^ true : true) && showLifePost(context, alarm);
    }

    private static boolean showLifePost(Context context, Alarm alarm) {
        if (alarm == null || !isLifePostEnabled() || !FBEUtil.getDefaultSharedPreferences(context).getBoolean("key_open_alarm_life_post", true) || isLifePostShownToday()) {
            return false;
        }
        int i = alarm.hour;
        return (i >= 4 && i < 10) || (i == 10 && alarm.minutes == 0);
    }

    public static boolean isLifePostShownToday() {
        return PrefUtil.getLifePostCloseTime() >= startTimeToday();
    }

    public static int getWakeUpPercentage(long j) {
        putWakeUpPercentage();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        int i = calendar.get(11);
        int i2 = calendar.get(12);
        HashMap<Integer, Integer> map = sWakeUpPercentage;
        int i3 = i + 1;
        int iIntValue = map.get(Integer.valueOf(i3)) == null ? 0 : map.get(Integer.valueOf(i3)).intValue();
        try {
            return Math.min(iIntValue + (((60 - i2) * (map.get(Integer.valueOf(i)) != null ? map.get(Integer.valueOf(i)).intValue() - iIntValue : 0)) / 60) + SecureRandom.getInstanceStrong().nextInt(5), 100);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return 100;
        }
    }

    public static boolean isToday(long j) {
        long jCurrentTimeMillis = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(jCurrentTimeMillis);
        int i = calendar.get(1);
        int i2 = calendar.get(6);
        calendar.setTimeInMillis(j);
        return i == calendar.get(1) && i2 == calendar.get(6);
    }

    public static String getTimeDate(long j) {
        return new SimpleDateFormat(DeskClockApp.getAppContext().getString(R.string.worldcolock_time_date)).format(new Date(j));
    }

    public static CharSequence getTimeInHour(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        return DateFormat.format(TIME_FORMAT, calendar);
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0056  */
    public static int queryAcumulateWakeUpDays(Context context) throws Throwable {
        Cursor cursorQuery;
        Cursor cursor = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(LifePost.CONTENT_URI, null, "wake_up_time < " + (System.currentTimeMillis() - TIME_IGNORE), null, null);
                if (cursorQuery == null) {
                    if (cursorQuery == null) {
                        return 0;
                    }
                    cursorQuery.close();
                    return 0;
                }
                try {
                    int count = cursorQuery.getCount();
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                    return count;
                } catch (Exception e) {
                    e = e;
                    Log.e(TAG, "queryAcumulateWakeUpDays(): query days failed", e);
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    } else {
                        cursor = cursorQuery;
                    }
                    if (cursor == null) {
                        return 0;
                    }
                    cursor.close();
                    return 0;
                }
            } catch (Exception e2) {
                e = e2;
                cursorQuery = null;
            } catch (Throwable th) {
                th = th;
                if (cursor != null) {
                    cursor.close();
                }
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            cursor = context;
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public static void recordWakeUp(Context context, long j, int i) {
        try {
            int iDelete = context.getContentResolver().delete(LifePost.CONTENT_URI, "wake_up_time >= " + (j - TIME_IGNORE), null);
            if (iDelete > 0) {
                Log.e(TAG, "recordWakeUp(): delete record today:" + iDelete);
            }
            ContentValues contentValues = new ContentValues();
            contentValues.put(LifePost.Columns.WAKE_UP_TIME, Long.valueOf(j));
            contentValues.put(LifePost.Columns.PERCENTAGE, Integer.valueOf(i));
            context.getContentResolver().insert(LifePost.CONTENT_URI, contentValues);
        } catch (Exception e) {
            Log.e(TAG, "recordWakeUp(): record wake up time failed", e);
        }
    }

    public static long startTimeToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar.getTimeInMillis();
    }

    public static boolean isNewsSupport(Context context) {
        return UserNoticeUtil.isNetPermissionAgreed() && FBEUtil.getDefaultSharedPreferences(context).getBoolean("key_open_alarm_life_post_news", false);
    }

    public static void executeLifePostDataLoadTask(Alarm alarm) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (!UserNoticeUtil.isNetPermissionAgreed()) {
            com.android.deskclock.util.Log.i(TAG, "network permission rejected");
            return;
        }
        if (isLifePostShownToday()) {
            com.android.deskclock.util.Log.i(TAG, "life post has shown today");
        } else if (!isShowLifePost(appDEContext, alarm)) {
            com.android.deskclock.util.Log.i(TAG, "not life post alarm");
        } else {
            appDEContext.startForegroundService(new Intent(appDEContext, (Class<?>) RecommendIntentService.class));
        }
    }
}
