package com.android.deskclock.addition.ringtone.week;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import com.android.deskclock.addition.MiuiTheme;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.addition.ringtone.RingtoneConstants;
import com.android.deskclock.addition.ringtone.RingtoneUriCompat;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.FileUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.PrefUtil;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class WeekRingtoneHelper {
    private static final String TAG = "DC:WeekRingtone";
    private static final String WEEK_RINGTONE_RESOURCE = "/system/media/audio/ui/miui_week_ringtone.ogg";

    public static String getWeekRingtonePath() {
        return "/system/media/audio/ui/miui_week_ringtone.ogg";
    }

    public static boolean isWeekRingtoneExist() {
        return FileUtil.isFileExist("/system/media/audio/ui/miui_week_ringtone.ogg");
    }

    public static Uri getWeekRingtoneUri() {
        if (RingtoneUriCompat.atLeastU()) {
            return RingtoneConstants.RINGTONE_URI_WEEK_NEW;
        }
        return RingtoneConstants.RINGTONE_URI_WEEK;
    }

    public static boolean isRomSupport() {
        try {
            if (!isWeekRingtoneExist()) {
                Log.i(TAG, "not support week ringtone: resource is not exist in rom");
                return false;
            }
            if (MiuiTheme.supportWeekRingtone()) {
                return true;
            }
            Log.i(TAG, "not support week ringtone: MiuiTheme not support");
            return false;
        } catch (Exception e) {
            Log.e("not support week ringtone: " + e.getMessage());
            return false;
        }
    }

    public static boolean isDeskclockSupport() {
        return ExternalResourceUtils.supportWeekRingtone();
    }

    public static boolean isSupport() {
        return isRomSupport() && isDeskclockSupport();
    }

    public static boolean shouldPlayWeekRingtone(int i) {
        if (i != -2) {
            return true;
        }
        Log.i("shouldPlayWeekRingtone false, timer_alarm");
        return false;
    }

    public static boolean isWeekRingtone(Uri uri) {
        if (uri == null) {
            Log.e("isWeekRingtone, null alert");
            return false;
        }
        try {
            Uri uri2 = RingtoneConstants.RINGTONE_URI_WEEK;
            Uri uri3 = RingtoneConstants.RINGTONE_URI_WEEK_NEW;
            Uri defaultAlarmRingtone = uri.equals(RingtoneManager.getDefaultUri(4)) ? AlarmRingtoneUtil.getDefaultAlarmRingtone() : uri;
            if (defaultAlarmRingtone == null) {
                return false;
            }
            boolean zContains = defaultAlarmRingtone.toString().contains("title=miui_week_ringtone");
            Log.i("isWeekRingtone, actualWeekRingtoneUri : " + uri2 + "   actualWeekRingtoneUriNew: " + uri3 + "   tempAlert: " + defaultAlarmRingtone);
            return uri2.equals(defaultAlarmRingtone) || uri3.equals(defaultAlarmRingtone) || zContains;
        } catch (Exception e) {
            Log.e("isWeekRingtone error, alert=" + uri, e);
            return false;
        }
    }

    public static String getWeekRingtoneBackground(Calendar calendar) {
        int miuiResourceVersion = PrefUtil.getMiuiResourceVersion();
        Log.i(TAG, "get week ringtone with version: " + miuiResourceVersion);
        WeekRingtoneV1 weekRingtoneV1 = miuiResourceVersion != 5 ? null : new WeekRingtoneV1();
        if (weekRingtoneV1 != null) {
            return weekRingtoneV1.getWeekRingtoneResource(calendar);
        }
        return null;
    }

    public static boolean isDefaultRingtone(Context context) {
        Uri defaultAlarmRingtone = AlarmRingtoneUtil.getDefaultAlarmRingtone(context);
        if (defaultAlarmRingtone == null) {
            return false;
        }
        return defaultAlarmRingtone.equals(getWeekRingtoneUri());
    }
}
