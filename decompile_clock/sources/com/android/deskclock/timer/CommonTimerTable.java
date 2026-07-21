package com.android.deskclock.timer;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class CommonTimerTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/common_timers");
    public static final String INSERT_COMMON_TIMER_SQL = "INSERT INTO common_timers (minutes) VALUES ";
    public static final String MIN_ID = "minutes";
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS common_timers (_id INTEGER PRIMARY KEY,minutes INTEGER);";
    public static final String TABLE_NAME = "common_timers";
}
