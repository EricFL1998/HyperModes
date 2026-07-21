package com.android.deskclock.alarm.lifepost.model;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class NewsHelper {
    private static final String TAG = "DC:NewsHelper";

    public static int insertNews(Context context, News news) {
        if (news == null) {
            return -1;
        }
        try {
            Uri uriInsert = context.getContentResolver().insert(NewsTable.CONTENT_URI, transferToContentValues(news));
            if (uriInsert != null) {
                return (int) ContentUris.parseId(uriInsert);
            }
            return -1;
        } catch (Exception e) {
            Log.e(TAG, "insertNews error: ", e);
            return -1;
        }
    }

    public static void deleteNews(Context context) {
        try {
            context.getContentResolver().delete(NewsTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.e(TAG, "deleteNews error: ", e);
        }
    }

    /* JADX WARN: Code duplicated, block: B:17:0x004e A[DONT_GENERATE, PHI: r2
  0x004e: PHI (r2v3 android.database.Cursor) = (r2v1 android.database.Cursor), (r2v4 android.database.Cursor) binds: [B:16:0x004c, B:10:0x003f] A[DONT_GENERATE, DONT_INLINE]] */
    public static List<News> getNewsByType(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        Cursor cursorQuery = null;
        try {
            try {
                cursorQuery = context.getContentResolver().query(NewsTable.CONTENT_URI, NewsTable.PROJECTION, "type=\"" + str + "\"", null, null);
                if (cursorQuery != null) {
                    cursorQuery.moveToFirst();
                    while (!cursorQuery.isAfterLast()) {
                        arrayList.add(createFromCursor(cursorQuery));
                        cursorQuery.moveToNext();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "getNewsByType error: ", e);
            }
            return arrayList;
        } finally {
            if (cursorQuery != null) {
                cursorQuery.close();
            }
        }
    }

    public static void updateNewsPic(Context context, int i, String str) {
        try {
            context.getContentResolver().update(ContentUris.withAppendedId(NewsTable.CONTENT_URI, i), transferToContentValues(str), null, null);
        } catch (Exception e) {
            Log.e(TAG, "updateNewsPic error: ", e);
        }
    }

    private static News createFromCursor(Cursor cursor) {
        News news = new News();
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex("_id");
            if (columnIndex != -1) {
                news.setId(cursor.getInt(columnIndex));
            }
            int columnIndex2 = cursor.getColumnIndex("type");
            if (columnIndex2 != -1) {
                news.setType(cursor.getString(columnIndex2));
            }
            int columnIndex3 = cursor.getColumnIndex(NewsTable.Columns.SHOW);
            if (columnIndex3 != -1) {
                news.setShow(cursor.getInt(columnIndex3) == 1);
            }
            int columnIndex4 = cursor.getColumnIndex("expire");
            if (columnIndex4 != -1) {
                news.setExpire(cursor.getLong(columnIndex4));
            }
            int columnIndex5 = cursor.getColumnIndex("title");
            if (columnIndex5 != -1) {
                news.setTitle(cursor.getString(columnIndex5));
            }
            int columnIndex6 = cursor.getColumnIndex(NewsTable.Columns.DESC);
            if (columnIndex6 != -1) {
                news.setDesc(cursor.getString(columnIndex6));
            }
            int columnIndex7 = cursor.getColumnIndex("image");
            if (columnIndex7 != -1) {
                news.setPic(cursor.getString(columnIndex7));
            }
            int columnIndex8 = cursor.getColumnIndex(NewsTable.Columns.LINK_TYPE);
            if (columnIndex8 != -1) {
                news.setUrlType(cursor.getString(columnIndex8));
            }
            int columnIndex9 = cursor.getColumnIndex(NewsTable.Columns.LINK_URL);
            if (columnIndex9 != -1) {
                news.setUrl(cursor.getString(columnIndex9));
            }
            int columnIndex10 = cursor.getColumnIndex(NewsTable.Columns.LINK_APP);
            if (columnIndex10 != -1) {
                news.setUrlApp(cursor.getString(columnIndex10));
            }
            int columnIndex11 = cursor.getColumnIndex(NewsTable.Columns.LINK_PKG);
            if (columnIndex11 != -1) {
                news.setPkgName(cursor.getString(columnIndex11));
            }
        }
        return news;
    }

    private static ContentValues transferToContentValues(News news) {
        ContentValues contentValues = new ContentValues();
        if (news != null) {
            contentValues.put("type", news.getType());
            contentValues.put(NewsTable.Columns.SHOW, Boolean.valueOf(news.isShow()));
            contentValues.put("expire", Long.valueOf(news.getExpire()));
            contentValues.put("title", news.getTitle());
            contentValues.put(NewsTable.Columns.DESC, news.getDesc());
            contentValues.put("image", news.getPic());
            contentValues.put(NewsTable.Columns.LINK_TYPE, news.getUrlType());
            contentValues.put(NewsTable.Columns.LINK_URL, news.getUrl());
            contentValues.put(NewsTable.Columns.LINK_APP, news.getUrlApp());
            contentValues.put(NewsTable.Columns.LINK_PKG, news.getPkgName());
        }
        return contentValues;
    }

    private static ContentValues transferToContentValues(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("image", str);
        return contentValues;
    }
}
