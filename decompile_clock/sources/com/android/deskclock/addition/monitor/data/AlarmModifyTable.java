package com.android.deskclock.addition.monitor.data;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class AlarmModifyTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/alarm_modify");
    public static final String[] PROJECTION = {"_id", "alarm_id", "type", "time", "content"};
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS alarm_modify (_id INTEGER PRIMARY KEY AUTOINCREMENT,alarm_id INTEGER,type INTEGER,time INTEGER,content TEXT);";
    public static final String TABLE_NAME = "alarm_modify";

    public static class Columns {
        public static final String ALARM_ID = "alarm_id";
        public static final String CONTENT = "content";
        public static final String TIME = "time";
        public static final String TYPE = "type";
        public static final String _ID = "_id";
    }
}
