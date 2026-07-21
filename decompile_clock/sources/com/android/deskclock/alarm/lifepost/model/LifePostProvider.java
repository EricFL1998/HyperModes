package com.android.deskclock.alarm.lifepost.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class LifePostProvider extends ContentProvider {
    private static final int LIFEPOST = 1;
    private static final int LIFEPOST_ID = 2;
    private static final UriMatcher sURLMatcher;
    private LifePostDatabaseHelper mOpenHelper;

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sURLMatcher = uriMatcher;
        uriMatcher.addURI("com.android.deskclock.lifepost", "lifepost", 1);
        uriMatcher.addURI("com.android.deskclock.lifepost", "lifepost/#", 2);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mOpenHelper = new LifePostDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(getContext()));
        return true;
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int iMatch = sURLMatcher.match(uri);
        if (iMatch == 1) {
            return "vnd.android.cursor.dir/lifepost";
        }
        if (iMatch == 2) {
            return "vnd.android.cursor.item/lifepost";
        }
        throw new IllegalArgumentException("Unknown URI");
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        if (sURLMatcher.match(uri) == 1) {
            long jInsert = this.mOpenHelper.getWritableDatabase().insert("lifepost", null, contentValues);
            if (jInsert < 0) {
                throw new SQLException("Failed to insert row");
            }
            Uri uriWithAppendedId = ContentUris.withAppendedId(LifePost.CONTENT_URI, jInsert);
            if (uriWithAppendedId != null) {
                getContext().getContentResolver().notifyChange(uri, null);
                getContext().getContentResolver().notifyChange(uriWithAppendedId, null);
            }
            return uriWithAppendedId;
        }
        throw new IllegalArgumentException("Cannot insert into URI: " + uri);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        int iDelete;
        String str2;
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        int iMatch = sURLMatcher.match(uri);
        if (iMatch == 1) {
            iDelete = writableDatabase.delete("lifepost", str, strArr);
        } else if (iMatch == 2) {
            String str3 = uri.getPathSegments().get(1);
            if (TextUtils.isEmpty(str)) {
                str2 = "_id=" + str3;
            } else {
                str2 = "_id=" + str3 + " AND (" + str + ")";
            }
            iDelete = writableDatabase.delete("lifepost", str2, strArr);
        } else {
            throw new IllegalArgumentException("Cannot delete from URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return iDelete;
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        int iMatch = sURLMatcher.match(uri);
        SQLiteDatabase writableDatabase = this.mOpenHelper.getWritableDatabase();
        if (iMatch == 1) {
            int iUpdate = writableDatabase.update("lifepost", contentValues, "_id=" + Long.parseLong(uri.getPathSegments().get(1)), null);
            getContext().getContentResolver().notifyChange(uri, null);
            return iUpdate;
        }
        throw new UnsupportedOperationException("Cannot update URI: " + uri);
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
        int iMatch = sURLMatcher.match(uri);
        if (iMatch == 1) {
            sQLiteQueryBuilder.setTables("lifepost");
        } else if (iMatch == 2) {
            sQLiteQueryBuilder.setTables("lifepost");
            sQLiteQueryBuilder.appendWhere("_id=");
            sQLiteQueryBuilder.appendWhere(uri.getPathSegments().get(1));
        } else {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
        Cursor cursorQuery = sQLiteQueryBuilder.query(readableDatabase, strArr, str, strArr2, null, null, str2);
        if (cursorQuery == null) {
            Log.v("PostLifeDatabaseHelper.query: failed");
        } else {
            cursorQuery.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursorQuery;
    }
}
