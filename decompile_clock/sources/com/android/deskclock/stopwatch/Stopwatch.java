package com.android.deskclock.stopwatch;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.provider.BaseColumns;
import com.android.deskclock.util.FBEUtil;

/* JADX INFO: loaded from: classes.dex */
public class Stopwatch {
    public static final long ELAPSED_TIME_ONE_DAY = 1440;
    public static final int ELAPSED_TIME_ONE_DAY_LEVEL = 2;
    public static final long ELAPSED_TIME_SEVEN_DAY = 10080;
    public static final int ELAPSED_TIME_SEVEN_DAY_LEVEL = 1;
    public static final long ELAPSED_TIME_THIRTY_DAY = 43200;
    public static final int ELAPSED_TIME_THIRTY_DAY_LEVEL = 0;
    public static final int ID_COLUMN_INDEX = 0;
    public static final String KEY_SHOWED_NOTIFICATION_ONE_DAY = "key_showed_notification_one_day";
    public static final String KEY_SHOWED_NOTIFICATION_SEVEN_DAY = "key_showed_notification_seven_day";
    public static final String KEY_SHOWED_NOTIFICATION_THIRTY_DAY = "key_showed_notification_thirty_day";
    public static final int LAP_ELAPSED_COLUMN_INDEX = 2;
    public static final int TOTAL_ELAPSED_COLUMN_INDEX = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/stopwatch");
    public static final String[] PROJECTION = {"_id", Columns.TOTAL_ELAPSED_COLUMN, Columns.LAP_ELAPSED_COLUMN};

    public static class Columns implements BaseColumns {
        public static final String LAP_ELAPSED_COLUMN = "lap_elapsed";
        public static final String TOTAL_ELAPSED_COLUMN = "total_elapsed";
    }

    public static final boolean isNotificationShowed(Context context, String str) {
        return FBEUtil.getDefaultSharedPreferences(context).getBoolean(str, false);
    }

    public static final void setKeyValue(Context context, String str, boolean z) {
        FBEUtil.getDefaultSharedPreferences(context).edit().putBoolean(str, z).apply();
    }

    public static final void resetKeyValue(Context context) {
        SharedPreferences.Editor editorEdit = FBEUtil.getDefaultSharedPreferences(context).edit();
        editorEdit.putBoolean(KEY_SHOWED_NOTIFICATION_ONE_DAY, false).apply();
        editorEdit.putBoolean(KEY_SHOWED_NOTIFICATION_SEVEN_DAY, false).apply();
        editorEdit.putBoolean(KEY_SHOWED_NOTIFICATION_THIRTY_DAY, false).apply();
    }
}
