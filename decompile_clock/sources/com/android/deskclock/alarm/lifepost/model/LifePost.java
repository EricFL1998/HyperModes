package com.android.deskclock.alarm.lifepost.model;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.loader.content.CursorLoader;

/* JADX INFO: loaded from: classes.dex */
public class LifePost {
    public static final int ID_COLUMN_INDEX = 0;
    public static final int PERCENTAGE_COLUMN_INDEX = 2;
    public static final int WAKE_UP_TIME_COLUMN_INDEX = 1;
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock.lifepost/lifepost");
    public static final String[] PROJECTION = {"_id", Columns.WAKE_UP_TIME, Columns.PERCENTAGE};

    public static class Columns implements BaseColumns {
        public static final String PERCENTAGE = "percentage";
        public static final String WAKE_UP_TIME = "wake_up_time";
        public static final String _ID = "_id";
    }

    public static CursorLoader getLifePostLoader(Context context) {
        return new CursorLoader(context, CONTENT_URI, PROJECTION, null, null, "_id DESC");
    }
}
