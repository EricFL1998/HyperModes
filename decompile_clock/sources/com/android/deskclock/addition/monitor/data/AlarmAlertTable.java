package com.android.deskclock.addition.monitor.data;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class AlarmAlertTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/alarm_alert");
    public static final String[] PROJECTION = {"_id", "alarm_id", "alarm_time", Columns.NOTIFY_TIME, Columns.PLAY_TIME, Columns.SHOW_TIME, Columns.VOLUME, Columns.SCREEN_LOCKED};
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS alarm_alert (_id INTEGER PRIMARY KEY AUTOINCREMENT,alarm_id INTEGER,alarm_time INTEGER,notify_time INTEGER,play_time INTEGER,show_time INTEGER,volume INTEGER,screen_locked INTEGER);";
    public static final String TABLE_NAME = "alarm_alert";

    public static class Columns {
        public static final String ALARM_ID = "alarm_id";
        public static final String ALARM_TIME = "alarm_time";
        public static final String NOTIFY_TIME = "notify_time";
        public static final String PLAY_TIME = "play_time";
        public static final String SCREEN_LOCKED = "screen_locked";
        public static final String SHOW_TIME = "show_time";
        public static final String VOLUME = "volume";
        public static final String _ID = "_id";
    }
}
