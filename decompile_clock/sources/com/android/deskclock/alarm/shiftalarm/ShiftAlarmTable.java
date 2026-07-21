package com.android.deskclock.alarm.shiftalarm;

/* JADX INFO: loaded from: classes.dex */
public class ShiftAlarmTable {
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS shift_alarms (_id INTEGER PRIMARY KEY,starttime INTEGER, duration INTEGER, alert TEXT, enable INTEGER, vibrate INTEGER, message TEXT, skipindex INTEGER, skiptime INTEGER, groupid INTEGER, alarmid INTEGER, shiftindex INTEGER, type INTEGER,alarmCount INTEGER,hour INTEGER,minutes INTEGER);";
    public static final String TABLE_NAME = "shift_alarms";
}
