package com.android.deskclock.alarm.bedtime;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.util.Log;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class BedtimeProvider extends ContentProvider {
    private static final String AUTHORITY = "com.android.deskclock.bedtimeProvider";
    private static final int BEDTIME = 1;
    private static final UriMatcher sURLMatcher;
    private Context mContext;

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sURLMatcher = uriMatcher;
        uriMatcher.addURI(AUTHORITY, "bedtime", 1);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        if (sURLMatcher.match(uri) == 1) {
            return "vnd.android.cursor.dir/bedtime";
        }
        throw new IllegalArgumentException("Unknown URI");
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mContext = FBEUtil.createDeviceProtectedStorageContext(getContext());
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        if (sURLMatcher.match(uri) == 1) {
            long jClearIdentity = clearIdentity();
            Cursor cursorQueryBedtime = BedtimeUtil.queryBedtime(this.mContext);
            restorCallingIdentity(jClearIdentity);
            return cursorQueryBedtime;
        }
        throw new IllegalArgumentException("Unknown URL " + uri);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public static long clearIdentity() {
        if (Util.isVersionS()) {
            return Binder.clearCallingIdentity();
        }
        return -1L;
    }

    public static void restorCallingIdentity(long j) {
        if (Util.isVersionS()) {
            try {
                Binder.restoreCallingIdentity(j);
            } catch (Throwable th) {
                Log.e("DC:BedtimeProvider", "restorCallingIdentity, error: " + th);
            }
        }
    }
}
