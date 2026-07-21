package com.android.deskclock.timer;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class CommonTimerTableNew {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/common_timers_new");
    public static final String DESC_ID = "description";
    public static final String INSERT_COMMON_TIMER_SQL = "INSERT INTO common_timers_new (seconds,description) VALUES ";
    public static final String SECOND_ID = "seconds";
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS common_timers_new (_id INTEGER PRIMARY KEY,seconds INTEGER,description TEXT);";
    public static final String TABLE_NAME = "common_timers_new";
}
