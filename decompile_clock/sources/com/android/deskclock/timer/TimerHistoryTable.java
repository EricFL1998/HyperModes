package com.android.deskclock.timer;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class TimerHistoryTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/timer_history");
    public static final String DESC_ID = "description";
    public static final String INSERT_COMMON_TIMER_SQL = "INSERT INTO timer_history (seconds,description) VALUES ";
    public static final String SECOND_ID = "seconds";
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS timer_history (_id INTEGER PRIMARY KEY,seconds INTEGER,description TEXT);";
    public static final String TABLE_NAME = "timer_history";
}
