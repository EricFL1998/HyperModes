package com.android.deskclock.worldclock;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import androidx.loader.content.CursorLoader;

/* JADX INFO: loaded from: classes.dex */
public class WorldClock {
    public static final int CITY_ID_COLUMN_INDEX = 1;
    public static final int ID_COLUMN_INDEX = 0;
    public String cityId;
    public int id;
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/worldclock");
    public static final String[] PROJECTION = {"_id", Columns.CITY_ID};

    public static class Columns implements BaseColumns {
        public static final String CITY_ID = "cityid_new";
        public static final int CITY_ID_INDEX = 1;
        public static final int ID_INDEX = 0;
    }

    public static CursorLoader getWorldClockLoader(Context context) {
        return new CursorLoader(context, CONTENT_URI, PROJECTION, "cityid_new is not null", null, null);
    }

    public WorldClock(Cursor cursor) {
        this.id = cursor.getInt(0);
        this.cityId = cursor.getString(1);
    }
}
