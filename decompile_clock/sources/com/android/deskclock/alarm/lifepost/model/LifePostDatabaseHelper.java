package com.android.deskclock.alarm.lifepost.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class LifePostDatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_TABLE_LIFEPOST = "CREATE TABLE lifepost (_id INTEGER PRIMARY KEY,wake_up_time LONG,percentage INTEGER);";
    private static final String DATABASE_NAME = "lifepost.db";
    private static final int DATABASE_VERSION = 2;
    private Context mContext;

    public LifePostDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
        this.mContext = context;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(CREATE_TABLE_LIFEPOST);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.v("Upgrading alarms database from version " + i + " to " + i2);
        if (i == 1) {
            i++;
        }
        if (i != i2) {
            Log.e("Upgrade lifepost database to version " + i2 + " fails");
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        resetDatabase(sQLiteDatabase);
    }

    private void resetDatabase(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS lifepost");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS recommend");
        onCreate(sQLiteDatabase);
    }
}
