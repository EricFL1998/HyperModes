package com.android.deskclock.addition;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.deskclock.AdditionUtil;
import com.android.deskclock.Alarm;
import com.android.deskclock.AlarmDatabaseHelper;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.util.FBEUtil;

/* JADX INFO: loaded from: classes.dex */
public class AdditionProvider extends ContentProvider {
    private static final int ALARM = 2;
    private static final String AUTHORITY = "com.android.deskclock.additionProvider";
    private static final int BEDTIME = 1;
    private static String TAG = "DC:AdditionProvider";
    private static final int WAKEALARM = 3;
    private static final UriMatcher sURLMatcher;
    private Context mContext;
    private AlarmDatabaseHelper mOpenHelper;

    @Override // android.content.ContentProvider
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    static {
        UriMatcher uriMatcher = new UriMatcher(-1);
        sURLMatcher = uriMatcher;
        uriMatcher.addURI(AUTHORITY, "bedtime", 1);
        uriMatcher.addURI(AUTHORITY, "alarm", 2);
        uriMatcher.addURI(AUTHORITY, "wakeAlarm", 3);
    }

    @Override // android.content.ContentProvider
    public int delete(Uri uri, String str, String[] strArr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override // android.content.ContentProvider
    public String getType(Uri uri) {
        int iMatch = sURLMatcher.match(uri);
        if (iMatch == 1) {
            return "vnd.android.cursor.dir/bedtime";
        }
        if (iMatch == 2) {
            return "vnd.android.cursor.dir/alarm";
        }
        if (iMatch == 3) {
            return "vnd.android.cursor.dir/wakealarm";
        }
        throw new IllegalArgumentException("Unknown URI");
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        this.mContext = FBEUtil.createDeviceProtectedStorageContext(getContext());
        this.mOpenHelper = new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(getContext()));
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri uri, String[] strArr, String str, String[] strArr2, String str2) {
        String str3;
        String callingPackage = getCallingPackage();
        Log.d(TAG, "query AdditionProvider from: " + callingPackage + ", uri: " + uri);
        if (!AdditionUtil.isPermissionAllow(callingPackage)) {
            Log.d(TAG, "not permission");
            return null;
        }
        int iMatch = sURLMatcher.match(uri);
        if (iMatch == 1) {
            return BedtimeUtil.queryBedtimeForAddition(this.mContext);
        }
        if (iMatch != 2) {
            if (iMatch == 3) {
                return BedtimeUtil.queryWakeAlarmForAddition(getContext());
            }
            throw new IllegalArgumentException("Unknown URL " + uri);
        }
        SQLiteDatabase readableDatabase = this.mOpenHelper.getReadableDatabase();
        SQLiteQueryBuilder sQLiteQueryBuilder = new SQLiteQueryBuilder();
        sQLiteQueryBuilder.setTables("alarms");
        if (TextUtils.isEmpty(str)) {
            str3 = Alarm.Columns.WHERE_NORMAL_ALARM;
        } else {
            str3 = "type=0 AND (" + str + ")";
        }
        return sQLiteQueryBuilder.query(readableDatabase, strArr, str3, strArr2, null, null, str2);
    }

    @Override // android.content.ContentProvider
    public int update(Uri uri, ContentValues contentValues, String str, String[] strArr) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
