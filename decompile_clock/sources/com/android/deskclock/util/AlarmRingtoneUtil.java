package com.android.deskclock.util;

import android.content.Context;
import android.content.Intent;
import android.media.ExtraRingtone;
import android.media.ExtraRingtoneManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.text.TextUtils;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.addition.ringtone.digital.DigitalTimerRingtoneHelper;
import com.android.deskclock.addition.ringtone.star.WYStarRingtoneHelper;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.addition.ringtone.week.WeekRingtoneHelper;
import com.android.deskclock.addition.xiaoai.XiaoAiRingtoneHelper;
import com.android.deskclock.util.stat.StatHelper;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes.dex */
public class AlarmRingtoneUtil {
    private static final String TAG = "DC:AlarmRingtoneUtil";
    private static final String TIMER_DEFAULT_RINGTONE_MATCH = "2131755013";

    public static Uri getDefaultAlarmRingtone() {
        Uri actualDefaultRingtoneUri;
        try {
            actualDefaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(DeskClockApp.getAppDEContext(), 4);
            Log.d("getDefaultAlarmRingtone : " + actualDefaultRingtoneUri);
        } catch (Exception e) {
            Log.e("getDefaultAlarmRingtone error, use default", e);
            actualDefaultRingtoneUri = null;
        }
        Log.i("getDefaultAlarmRingtone=" + actualDefaultRingtoneUri);
        return actualDefaultRingtoneUri;
    }

    public static Uri getDefaultAlarmRingtone(Context context) {
        Uri actualDefaultRingtoneUri;
        try {
            actualDefaultRingtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context, 4);
        } catch (Exception e) {
            Log.e("getDefaultAlarmRingtone error, use default", e);
            actualDefaultRingtoneUri = null;
        }
        Log.i("getDefaultAlarmRingtone=" + actualDefaultRingtoneUri);
        return actualDefaultRingtoneUri;
    }

    public static void setDefaultAlarmRingtone(Uri uri) {
        Context appDEContext = DeskClockApp.getAppDEContext();
        if (uri == null) {
            RingtoneManager.setActualDefaultRingtoneUri(appDEContext, 4, null);
        } else {
            try {
                ExtraRingtoneManager.saveDefaultSound(appDEContext, 4, uri);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
        try {
            if (uri != null && TextUtils.equals(uri.toString(), WeatherRingtoneHelper.getWeatherRingtoneUri().toString())) {
                StatHelper.updateAlarmProperties(StatHelper.PROP_IS_DYNAMIC_ALARM_DEFAULT, "YES");
            } else {
                StatHelper.updateAlarmProperties(StatHelper.PROP_IS_DYNAMIC_ALARM_DEFAULT, "NO");
            }
        } catch (Exception e) {
            Log.e("add mi state error: " + e.getMessage());
        }
    }

    public static String getAlarmRingtoneTitle(Context context, Uri uri) {
        return getAlarmRingtoneTitle(context, uri, Integer.MIN_VALUE);
    }

    public static String getAlarmRingtoneTitle(Context context, Uri uri, int i) {
        Log.d(TAG, "getAlarmRingtoneUri: " + uri);
        if (uri == null) {
            return context.getString(R.string.silent_alarm_summary);
        }
        if (WeatherRingtoneHelper.isWeatherRingtone(uri)) {
            return context.getString(R.string.ringtone_weather);
        }
        if (WeekRingtoneHelper.isWeekRingtone(uri)) {
            return context.getString(R.string.ringtone_week);
        }
        if (WYStarRingtoneHelper.isWYStarAlert(uri)) {
            return context.getString(R.string.star_wy_alarm_speech);
        }
        if (DigitalTimerRingtoneHelper.isDigitalTimer(uri)) {
            return context.getString(R.string.ringtone_digital_timer);
        }
        if (XiaoAiRingtoneHelper.isXiaoAiRingtone(uri)) {
            return context.getString(R.string.xiaoai_ringtone_title);
        }
        if (XiaoAiRingtoneHelper.isXiaoAiAlarm(context, i) && XiaoAiRingtoneHelper.isXiaoAiRingtone(uri)) {
            return context.getString(R.string.xiaoai_ringtone_title);
        }
        try {
            String ringtoneTitle = ExtraRingtone.getRingtoneTitle(context, uri, true);
            Log.d(TAG, "getAlarmRingtoneTitle: " + ringtoneTitle);
            if (!ringtoneTitle.contains("2131755014") && !ringtoneTitle.contains(context.getString(R.string.xiaoai_ringtone_title))) {
                if (!ringtoneTitle.contains("timer_ring") && !isInteger(uri.toString()) && !ringtoneTitle.contains(TIMER_DEFAULT_RINGTONE_MATCH)) {
                    return ringtoneTitle;
                }
                return context.getString(R.string.ringtone_digital_timer);
            }
            return context.getString(R.string.xiaoai_ringtone_title);
        } catch (Exception e) {
            Log.e("getAlarmRingtoneTitle error: " + e.getMessage());
            return uri.toString();
        }
    }

    public static boolean isInteger(String str) {
        return Pattern.compile("[0-9]*").matcher(str).matches();
    }

    public static void takePersistableUriPermission(Intent intent, Uri uri, Context context) {
        if (uri == null || intent == null || context == null || (intent.getFlags() & 64) == 0 || !uri.toString().startsWith("content://")) {
            return;
        }
        try {
            context.getContentResolver().takePersistableUriPermission(uri, 1);
        } catch (Exception e) {
            android.util.Log.e(TAG, "takePersistableUriPermission error: " + e.getMessage());
        }
    }

    public static boolean isFireFliesRingtone(Uri uri) {
        if (uri == null) {
            return false;
        }
        String string = uri.toString();
        return string.startsWith(RingtoneConstants.SYSTEM_MEDIA_PATH_NEW) && string.contains("Fireflies");
    }

    public static Uri getXiaoAiOrDigitalTimerAlertUri(Context context, Uri uri) {
        if (!RingtoneUriCompat.atLeastU()) {
            return uri;
        }
        if (getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), uri).contains(context.getString(R.string.ringtone_digital_timer))) {
            return DigitalTimerRingtoneHelper.getRingtoneUri();
        }
        return getAlarmRingtoneTitle(DeskClockApp.getAppDEContext(), uri).contains(context.getString(R.string.xiaoai_ringtone_title)) ? XiaoAiRingtoneHelper.getRingtoneUri() : uri;
    }
}
