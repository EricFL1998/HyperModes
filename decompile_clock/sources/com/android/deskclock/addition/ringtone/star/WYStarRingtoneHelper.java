package com.android.deskclock.addition.ringtone.star;

import android.content.ContentValues;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import com.android.deskclock.Alarm;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.weather.WeatherRingtoneHelper;
import com.android.deskclock.util.AlarmHelper;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class WYStarRingtoneHelper {
    public static final String DEFAULT_WANGYUAN_ALARM_RINGTONE = "/system/media/audio/ui/wangyuan.ogg";
    private static final String TAG = "DC:WYStarRingtone";
    private static boolean isSupportWYStarAlert = true;

    public static String getWYStarRingtonePath() {
        return "/system/media/audio/ui/wangyuan.ogg";
    }

    public static boolean isWYStarRingtoneExist() {
        return FileUtil.isFileExist("/system/media/audio/ui/wangyuan.ogg");
    }

    public static Uri getWYStarRingtoneUri() {
        return RingtoneConstants.RINGTONE_URI_WANGYUAN;
    }

    public static boolean isRomSupport() {
        return isWYStarRingtoneExist();
    }

    public static boolean isDeskclockSupport() {
        return !Util.isInternational() && isSupportWYStarAlert;
    }

    public static boolean isSupport() {
        return isRomSupport() && isDeskclockSupport();
    }

    public static ContentValues updateWYStarAlertToDefault(ContentValues contentValues) {
        if (contentValues.containsKey("alert") && isWYStarAlert(contentValues.getAsString("alert")) && !isSupport()) {
            Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
            if (isWYStarAlert(defaultAlarmRingtone)) {
                contentValues.put("alert", WeatherRingtoneHelper.getWeatherRingtoneUri().toString());
            } else {
                contentValues.put("alert", defaultAlarmRingtone == null ? "" : defaultAlarmRingtone.toString());
            }
        }
        return contentValues;
    }

    public static boolean updateWYStarAlertToDefault(Context context, Alarm alarm) {
        if (!isWYStarAlert(alarm.alert) || isSupport()) {
            return false;
        }
        Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
        if (isWYStarAlert(defaultAlarmRingtone)) {
            alarm.alert = WeatherRingtoneHelper.getWeatherRingtoneUri();
        } else {
            alarm.alert = defaultAlarmRingtone;
        }
        AlarmHelper.setAlarm(context, alarm);
        return true;
    }

    public static boolean isWYStarAlert(String str) {
        return isWYStarAlert(Uri.parse(str));
    }

    public static boolean isWYStarAlert(Uri uri) {
        Uri defaultAlarmRingtone;
        if (uri == null) {
            Log.e(TAG, "isWYStarAlert, null alert");
            return false;
        }
        try {
            Uri wYStarRingtoneUri = getWYStarRingtoneUri();
            if (!uri.equals(RingtoneManager.getDefaultUri(4))) {
                Log.d(TAG, "isWYStarAlert alert=" + uri);
                defaultAlarmRingtone = uri;
            } else {
                defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone();
                Log.d(TAG, "isWYStarAlert defaultUri=" + defaultAlarmRingtone);
            }
            if (defaultAlarmRingtone == null) {
                return false;
            }
            return wYStarRingtoneUri.equals(defaultAlarmRingtone);
        } catch (Exception e) {
            Log.e("isWYStarAlert error, alert=" + uri, e);
            return false;
        }
    }
}
