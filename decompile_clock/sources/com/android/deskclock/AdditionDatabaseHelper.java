package com.android.deskclock;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.android.deskclock.timer.CommonTimerTableNew;
import com.android.deskclock.timer.TimerHistoryTable;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AdditionDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "addition.db";
    private static final int DATABASE_VERSION = 2;
    private static String TAG = "DC:AdditionDatabaseHelper";
    private Context mContext;

    public AdditionDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, (SQLiteDatabase.CursorFactory) null, 2);
        this.mContext = context;
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        Log.f(TAG, "onCreate additionDatabase version 2");
        sQLiteDatabase.execSQL(CommonTimerTableNew.TABLE_CREATE_SQL);
        sQLiteDatabase.execSQL(TimerHistoryTable.TABLE_CREATE_SQL);
        insertDefaultCommonTimers(sQLiteDatabase);
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.f(TAG, "Upgrading additionDatabase from version " + i + " to " + i2);
        if (i2 == 2) {
            sQLiteDatabase.execSQL(TimerHistoryTable.TABLE_CREATE_SQL);
        }
    }

    @Override // android.database.sqlite.SQLiteOpenHelper
    public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
        Log.f("Downgrading additionDatabase from version " + i + " to " + i2);
    }

    private void insertDefaultCommonTimers(SQLiteDatabase sQLiteDatabase) {
        Log.i("Inserting default common timers");
        sQLiteDatabase.execSQL("INSERT INTO common_timers_new (seconds,description) VALUES (5*60,'" + this.mContext.getString(R.string.timer_desc_use_brush_teeth) + "');");
        sQLiteDatabase.execSQL("INSERT INTO common_timers_new (seconds,description) VALUES (15*60,'" + this.mContext.getString(R.string.timer_desc_use_facial_mask) + "');");
        sQLiteDatabase.execSQL("INSERT INTO common_timers_new (seconds,description) VALUES (30*60,'" + this.mContext.getString(R.string.timer_desc_use_sleep) + "');");
    }
}
