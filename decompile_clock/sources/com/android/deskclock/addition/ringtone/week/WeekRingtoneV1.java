package com.android.deskclock.addition.ringtone.week;

import android.content.SharedPreferences;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.lifepost.LifePostUtils;
import com.android.deskclock.util.FBEUtil;
import java.io.File;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public class WeekRingtoneV1 extends WeekRingtoneBase {
    private static final String KEY_WEEK_RINGTONE_FIRST_PLAY_TIME = "week_ringtone_first_play_time";

    @Override // com.android.deskclock.addition.ringtone.week.WeekRingtoneBase
    protected int getVersion() {
        return 5;
    }

    @Override // com.android.deskclock.addition.ringtone.week.WeekRingtoneBase
    public String getWeekRingtoneResource(Calendar calendar) {
        String weekdayDesc;
        SharedPreferences defaultSharedPreferences = FBEUtil.getDefaultSharedPreferences(DeskClockApp.getAppDEContext());
        boolean z = defaultSharedPreferences.getLong(KEY_WEEK_RINGTONE_FIRST_PLAY_TIME, 0L) >= LifePostUtils.startTimeToday();
        if (!z) {
            defaultSharedPreferences.edit().putLong(KEY_WEEK_RINGTONE_FIRST_PLAY_TIME, System.currentTimeMillis()).apply();
        }
        String externalFolder = getExternalFolder();
        if (externalFolder == null || (weekdayDesc = getWeekdayDesc(calendar)) == null) {
            return null;
        }
        String[] strArr = new String[7];
        strArr[0] = externalFolder;
        strArr[1] = File.separator;
        strArr[2] = weekdayDesc;
        strArr[3] = File.separator;
        strArr[4] = weekdayDesc;
        strArr[5] = String.valueOf(z ? 2 : 1);
        strArr[6] = ".mp3";
        return appendString(strArr);
    }
}
