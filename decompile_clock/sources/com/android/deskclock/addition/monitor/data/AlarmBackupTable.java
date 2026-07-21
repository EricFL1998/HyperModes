package com.android.deskclock.addition.monitor.data;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class AlarmBackupTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/alarm_backup");
    public static final String[] PROJECTION = {"_id", "hour", "minutes", "daysofweek", "alarmtime", "enabled", "vibrate", "message", "alert", "type", "deleteAfterUse", "skiptime"};
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS alarm_backup (_id INTEGER PRIMARY KEY,hour TEXT,minutes TEXT,daysofweek TEXT,alarmtime INTEGER,enabled INTEGER,vibrate INTEGER,message TEXT,alert TEXT,type TEXT,deleteAfterUse TEXT,skiptime TEXT);";
    public static final String TABLE_NAME = "alarm_backup";

    public static class Columns {
        public static final String ALARM_TIME = "alarmtime";
        public static final String ALERT = "alert";
        public static final String DAYS_OF_WEEK = "daysofweek";
        public static final String DELETE_AFTER_USE = "deleteAfterUse";
        public static final String ENABLED = "enabled";
        public static final String HOUR = "hour";
        public static final String MESSAGE = "message";
        public static final String MINUTES = "minutes";
        public static final String SKIP_TIME = "skiptime";
        public static final String TYPE = "type";
        public static final String VIBRATE = "vibrate";
        public static final String _ID = "_id";
    }
}
