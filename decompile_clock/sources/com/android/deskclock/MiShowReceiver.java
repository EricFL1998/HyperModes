package com.android.deskclock;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import com.android.deskclock.addition.MiuiSdk;
import com.android.deskclock.alarm.bedtime.BedtimeUtil;
import com.android.deskclock.alarm.bedtime.SleepAlarmTable;
import com.android.deskclock.alarm.bedtime.ZenModeUtil;
import com.android.deskclock.stopwatch.StopWatchService;
import com.android.deskclock.timer.TimerDao;
import com.android.deskclock.timer.TimerHistoryTable;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.worldclock.WorldClock;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
public class MiShowReceiver extends BroadcastReceiver {
    private static final String ACTION_MISHOW_PRESET_DATA = "com.xiaomi.mihomemanager.getPresetData";
    public static final String ACTION_MISHOW_RESET_DATA = "com.xiaomi.mihomemanager.resetAppDataOrSetting";
    private static final String METHOD_GET_FILE_DESCRIPTOR = "method_get_file_descriptor";
    private static final String PRESET_DATA_AUTHORITY = "com.xiaomi.mihomemanager.presetdataprovider";
    private static final String TAG = "DC:MiShowReceiver";

    @Override // android.content.BroadcastReceiver
    public void onReceive(final Context context, final Intent intent) {
        Log.d(TAG, "onReceive: " + intent.getAction());
        AsyncHandler.post(new Runnable() { // from class: com.android.deskclock.MiShowReceiver.1
            @Override // java.lang.Runnable
            public void run() {
                if (MiShowReceiver.ACTION_MISHOW_PRESET_DATA.equals(intent.getAction())) {
                    MiShowReceiver.this.handlePresetDataRequest(context);
                } else if (MiShowReceiver.ACTION_MISHOW_RESET_DATA.equals(intent.getAction())) {
                    MiShowReceiver.this.deleteAllAlarms();
                    MiShowReceiver.this.deleteAllWorldClock();
                    MiShowReceiver.this.deleteAllStopwatches();
                    MiShowReceiver.this.deleteAllTimers();
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePresetDataRequest(Context context) {
        Bundle bundleCall = context.getContentResolver().call(new Uri.Builder().scheme("content").authority(PRESET_DATA_AUTHORITY).build(), METHOD_GET_FILE_DESCRIPTOR, context.getPackageName(), (Bundle) null);
        if (bundleCall == null) {
            return;
        }
        processFileDescriptors(bundleCall, context);
    }

    private void processFileDescriptors(Bundle bundle, Context context) {
        ArrayList parcelableArrayList = Build.VERSION.SDK_INT >= 33 ? bundle.getParcelableArrayList("fileDescriptorList", ParcelFileDescriptor.class) : null;
        ArrayList<String> stringArrayList = bundle.getStringArrayList("fileNameList");
        if (parcelableArrayList == null || stringArrayList == null || parcelableArrayList.size() != stringArrayList.size()) {
            Log.e(TAG, "Invalid file descriptor or file name list");
            return;
        }
        Log.d(TAG, "fileDescriptorList.size(): " + parcelableArrayList.size());
        int i = 0;
        while (i < parcelableArrayList.size()) {
            ParcelFileDescriptor parcelFileDescriptor = (ParcelFileDescriptor) parcelableArrayList.get(i);
            String str = i < stringArrayList.size() ? stringArrayList.get(i) : null;
            if (parcelFileDescriptor == null || str == null) {
                Log.w(TAG, "Skipping null file descriptor or file name");
            } else {
                try {
                    AssetFileDescriptor assetFileDescriptor = new AssetFileDescriptor(parcelFileDescriptor, 0L, -1L);
                    try {
                        FileInputStream fileInputStreamCreateInputStream = assetFileDescriptor.createInputStream();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStreamCreateInputStream));
                            try {
                                if (str.equals("worldclock.txt")) {
                                    while (true) {
                                        String line = bufferedReader.readLine();
                                        if (line == null) {
                                            break;
                                        }
                                        boolean zIsCityExists = isCityExists(DeskClockApp.getAppDEContext(), line);
                                        Log.d(TAG, "cityId: " + line + "isCityExists: " + zIsCityExists);
                                        if (!zIsCityExists) {
                                            insertCityZoneToDB(line);
                                        }
                                    }
                                } else if (!str.equals("alarmdata.json")) {
                                    Log.w(TAG, "Unsupported file type: " + str);
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    while (true) {
                                        String line2 = bufferedReader.readLine();
                                        if (line2 == null) {
                                            break;
                                        } else {
                                            sb.append(line2);
                                        }
                                    }
                                    if (sb.length() <= 0) {
                                        Log.w(TAG, "Empty JSON file: " + str);
                                    } else {
                                        processAlarmData(new JSONObject(sb.toString()), context);
                                    }
                                }
                                bufferedReader.close();
                                if (fileInputStreamCreateInputStream != null) {
                                    fileInputStreamCreateInputStream.close();
                                }
                                assetFileDescriptor.close();
                            } catch (Throwable th) {
                                try {
                                    bufferedReader.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                                throw th;
                            }
                        } catch (Throwable th3) {
                            if (fileInputStreamCreateInputStream != null) {
                                try {
                                    fileInputStreamCreateInputStream.close();
                                } catch (Throwable th4) {
                                    th3.addSuppressed(th4);
                                }
                            }
                            throw th3;
                        }
                    } catch (Throwable th5) {
                        try {
                            assetFileDescriptor.close();
                        } catch (Throwable th6) {
                            th5.addSuppressed(th6);
                        }
                        throw th5;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Error processing file: " + str, e);
                } catch (JSONException e2) {
                    Log.e(TAG, "Error processing jsonObject: " + str, e2);
                }
            }
            i++;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteAllWorldClock() {
        DeskClockApp.getAppDEContext().getContentResolver().delete(WorldClock.CONTENT_URI, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteAllAlarms() {
        DeskClockApp.getAppDEContext().getContentResolver().delete(Alarm.Columns.CONTENT_URI, null, null);
        if (MiuiSdk.isSupportSleep()) {
            handleSleepRelatedTasks();
            BedtimeUtil.setBedTimeCompleted(DeskClockApp.getAppDEContext(), false);
            BedtimeUtil.setBedtimeOpenState(DeskClockApp.getAppDEContext(), false);
            DeskClockApp.getAppDEContext().getContentResolver().delete(SleepAlarmTable.CONTENT_URI, null, null);
        }
    }

    private void handleSleepRelatedTasks() {
        if ("".equals(ZenModeUtil.getLocalZenRuleId(DeskClockApp.getAppDEContext()))) {
            ZenModeUtil.clearZenMode(DeskClockApp.getAppDEContext());
        }
        if (BedtimeUtil.hasTransferWakeAlarmToSp(DeskClockApp.getAppDEContext())) {
            return;
        }
        BedtimeUtil.transferWakeAlarmToSp(DeskClockApp.getAppDEContext());
    }

    private void processAlarmData(JSONObject jSONObject, Context context) {
        if (jSONObject == null || !jSONObject.has("alarms")) {
            Log.w(TAG, "Invalid or empty alarm data JSON");
            return;
        }
        try {
            JSONArray jSONArray = jSONObject.getJSONArray("alarms");
            AlarmDatabaseHelper alarmDatabaseHelper = new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(context));
            if (jSONArray != null && jSONArray.length() != 0) {
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                    if (jSONObject2 != null) {
                        String string = jSONObject2.getString("hour");
                        String string2 = jSONObject2.getString("minutes");
                        String string3 = jSONObject2.getString("daysofweek");
                        String string4 = jSONObject2.getString("enabled");
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("enabled", string4);
                        contentValues.put("hour", string);
                        contentValues.put("minutes", string2);
                        contentValues.put("daysofweek", string3);
                        if (!isAlarmExists(alarmDatabaseHelper.getReadableDatabase(), Integer.parseInt(string), Integer.parseInt(string2), Integer.parseInt(string3), Integer.parseInt(string4))) {
                            DeskClockApp.getAppDEContext().getContentResolver().insert(Alarm.Columns.CONTENT_URI, contentValues);
                        }
                    }
                }
                return;
            }
            Log.w(TAG, "No alarms data found in JSON");
        } catch (JSONException e) {
            Log.e(TAG, "Failed to process alarm data: " + e);
        }
    }

    private boolean isAlarmExists(SQLiteDatabase sQLiteDatabase, int i, int i2, int i3, int i4) {
        Cursor cursorRawQuery = sQLiteDatabase.rawQuery("SELECT 1 FROM alarms WHERE hour = ? AND minutes = ? AND daysofweek = ? AND enabled = ?", new String[]{String.valueOf(i), String.valueOf(i2), String.valueOf(i3), String.valueOf(i4)});
        boolean z = cursorRawQuery.getCount() > 0;
        cursorRawQuery.close();
        return z;
    }

    private boolean isCityExists(Context context, String str) {
        boolean zMoveToFirst = false;
        Cursor cursorQuery = context.getContentResolver().query(WorldClock.CONTENT_URI, WorldClock.PROJECTION, "cityid_new=?", new String[]{str}, null);
        if (cursorQuery != null) {
            try {
                zMoveToFirst = cursorQuery.moveToFirst();
            } finally {
                cursorQuery.close();
            }
        }
        return zMoveToFirst;
    }

    private void insertCityZoneToDB(String str) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(WorldClock.Columns.CITY_ID, str);
        DeskClockApp.getAppDEContext().getContentResolver().insert(WorldClock.CONTENT_URI, contentValues);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteAllStopwatches() {
        try {
            Context appDEContext = DeskClockApp.getAppDEContext();
            Intent intent = new Intent(StopWatchService.ACTION_STOPWATCH_RESET);
            intent.setPackage(appDEContext.getPackageName());
            appDEContext.sendBroadcast(intent);
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete all stopwatches", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deleteAllTimers() {
        try {
            TimerDao.deleteTimer(DeskClockApp.getAppDEContext());
            DeskClockApp.getAppDEContext().getContentResolver().delete(TimerHistoryTable.CONTENT_URI, null, null);
        } catch (Exception e) {
            Log.e(TAG, "Failed to delete all timers", e);
        }
    }
}
