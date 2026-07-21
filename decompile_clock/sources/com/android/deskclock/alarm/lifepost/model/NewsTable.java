package com.android.deskclock.alarm.lifepost.model;

import android.net.Uri;

/* JADX INFO: loaded from: classes.dex */
public class NewsTable {
    public static final Uri CONTENT_URI = Uri.parse("content://com.android.deskclock/news");
    public static final String[] PROJECTION = {"_id", "type", Columns.TEMPLATE, Columns.SHOW, "expire", "title", Columns.DESC, "image", Columns.LINK_TYPE, Columns.LINK_URL, Columns.LINK_APP, Columns.LINK_PKG};
    public static final String TABLE_CREATE_SQL = "CREATE TABLE IF NOT EXISTS news (_id INTEGER PRIMARY KEY AUTOINCREMENT,type TEXT,template TEXT,show INTEGER,expire INTEGER,title TEXT,desc TEXT,image TEXT,link_type TEXT,link_url TEXT,link_app TEXT,link_pkg TEXT);";
    public static final String TABLE_NAME = "news";

    public static class Columns {
        public static final String DESC = "desc";
        public static final String EXPIRE = "expire";
        public static final String IMAGE = "image";
        public static final String LINK_APP = "link_app";
        public static final String LINK_PKG = "link_pkg";
        public static final String LINK_TYPE = "link_type";
        public static final String LINK_URL = "link_url";
        public static final String SHOW = "show";
        public static final String TEMPLATE = "template";
        public static final String TITLE = "title";
        public static final String TYPE = "type";
        public static final String _ID = "_id";
    }
}
