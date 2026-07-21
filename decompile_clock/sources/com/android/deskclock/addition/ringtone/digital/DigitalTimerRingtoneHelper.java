package com.android.deskclock.addition.ringtone.digital;

import android.media.RingtoneManager;
import android.net.Uri;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.R;
import com.android.deskclock.util.AlarmRingtoneUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class DigitalTimerRingtoneHelper {
    public static Uri DIGITAL_TIMER_URI = getRingtoneUri();

    public static Uri getRingtoneUri() {
        if (Util.isSpecialDevice()) {
            return new Uri.Builder().scheme("android.resource").authority(DeskClockApp.getAppContext().getPackageName()).path(String.valueOf(R.raw.special_timer_ring)).build();
        }
        return new Uri.Builder().scheme("android.resource").authority(DeskClockApp.getAppContext().getPackageName()).path(String.valueOf(R.raw.timer_ring)).build();
    }

    public static String getRingtonePath() {
        return getRingtoneUri().toString();
    }

    public static boolean isDigitalTimer(Uri uri) {
        if (uri == null) {
            return false;
        }
        try {
            Uri ringtoneUri = getRingtoneUri();
            Uri defaultAlarmRingtone = uri.equals(RingtoneManager.getDefaultUri(4)) ? AlarmRingtoneUtil.getDefaultAlarmRingtone() : uri;
            if (defaultAlarmRingtone == null) {
                return false;
            }
            return ringtoneUri.equals(defaultAlarmRingtone);
        } catch (Exception e) {
            Log.e("isDigitalTimer error, alert=" + uri, e);
            return false;
        }
    }
}
