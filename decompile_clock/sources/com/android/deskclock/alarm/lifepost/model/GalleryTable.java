package com.android.deskclock.alarm.lifepost.model;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class GalleryTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/gallery");
    public static final String[] PROJECTION = {"_id", "image", Columns.TEXT, "color", "expire", "type", Columns.ACTION_TYPE, Columns.ACTION_URL, Columns.MARKET_URL, Columns.PKG_NAME};
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS gallery (_id INTEGER PRIMARY KEY AUTOINCREMENT,image TEXT,text TEXT,color TEXT,expire INTEGER,type INTEGER,action_type INTEGER,action_url TEXT,marketUrl TEXT,pkg_name TEXT);";
    public static final String TABLE_NAME = "gallery";

    public static class Columns {
        public static final String ACTION_TYPE = "action_type";
        public static final String ACTION_URL = "action_url";
        public static final String COLOR = "color";
        public static final String EXPIRE = "expire";
        public static final String IMAGE = "image";
        public static final String MARKET_URL = "marketUrl";
        public static final String PKG_NAME = "pkg_name";
        public static final String TEXT = "text";
        public static final String TYPE = "type";
        public static final String _ID = "_id";
    }
}
