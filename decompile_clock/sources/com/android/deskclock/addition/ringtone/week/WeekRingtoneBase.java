package com.android.deskclock.addition.ringtone.week;

import com.android.deskclock.DeskClockApp;
import com.android.deskclock.addition.resource.ExternalResourceUtils;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.Util;
import java.io.File;
import java.util.Calendar;

/* JADX INFO: loaded from: classes.dex */
public abstract class WeekRingtoneBase {
    protected static final String FRIDAY = "Friday";
    protected static final String MONDAY = "Monday";
    protected static final String SATURDAY = "Saturday";
    protected static final String SUNDAY = "Sunday";
    protected static final String THURSDAY = "Thursday";
    protected static final String TUESDAY = "Tuesday";
    protected static final String WEDNESDAY = "Wednesday";
    public final String TAG = "DC:WeekRingtone";
    protected final String ROOT_PATH = "week_ringtone";
    protected final String SOURCE_SUFFIX = ".mp3";

    protected abstract int getVersion();

    public abstract String getWeekRingtoneResource(Calendar calendar);

    protected String getWeekdayDesc(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        switch (Util.getWeekDay(calendar)) {
            case 1:
                return SUNDAY;
            case 2:
                return MONDAY;
            case 3:
                return TUESDAY;
            case 4:
                return WEDNESDAY;
            case 5:
                return THURSDAY;
            case 6:
                return FRIDAY;
            case 7:
                return SATURDAY;
            default:
                return null;
        }
    }

    protected String getExternalFolder() {
        try {
            File file = new File(appendString(DeskClockApp.getAppDEContext().getFilesDir().getAbsolutePath(), File.separator, ExternalResourceUtils.getModuleNameWithVersion(getVersion()), File.separator, ExternalResourceUtils.EXTERNAL_PATH_ASSERT, File.separator, "week_ringtone"));
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getAbsolutePath();
        } catch (Exception e) {
            Log.e("getRootPath error", e);
            return null;
        }
    }

    protected String appendString(String... strArr) {
        StringBuilder sb = new StringBuilder();
        for (String str : strArr) {
            sb.append(str);
        }
        return sb.toString();
    }
}
