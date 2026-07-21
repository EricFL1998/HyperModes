package com.android.deskclock.alarm.bedtime;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class SleepAlarmTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/sleep_alarms");
    public static final Uri CONTENT_URI_DB = Uri.parse("content://com.android.deskclock/sleep_alarms_db");
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS sleep_alarms (_id INTEGER,hour INTEGER, minutes INTEGER, daysofweek INTEGER, alarmtime INTEGER, enabled INTEGER, vibrate INTEGER, message TEXT, alert TEXT, type INTEGER DEFAULT 0, deleteAfterUse INTEGER DEFAULT 0, skiptime INTEGER DEFAULT 0);";
    public static final String TABLE_NAME = "sleep_alarms";
}
