package com.android.deskclock.addition.backup;

import android.content.Context;
import android.content.Intent;
import com.android.deskclock.AdditionDatabaseHelper;
import com.android.deskclock.AlarmDatabaseHelper;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.util.FBEUtil;
import com.android.deskclock.util.Log;
import com.android.deskclock.util.themeringtone.RingtoneHelper;
import com.xiaomi.settingsdk.backup.ICloudBackup;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import com.xiaomi.settingsdk.backup.data.KeyJsonSettingItem;
import com.xiaomi.settingsdk.backup.data.SettingItem;
import java.io.File;
import org.json.JSONObject;

/* JADX INFO: loaded from: classes.dex */
class ClocksBackupImpl implements ICloudBackup {
    private static final String ALARMS_DB_NAME = "alarms";
    private static final String J_DB = "db";
    private static final String J_SETTINGS = "settings";
    private static final String J_TIMER_DB = "db_common_timer";
    private static final String TAG = "DC:CloudBackup";
    private static final String TIMER_DB_NAME = "common_timers";
    private boolean mSkipBackupRestoreRingtone = false;

    @Override // com.xiaomi.settingsdk.backup.ICloudBackup
    public int getCurrentVersion(Context context) {
        return 2;
    }

    ClocksBackupImpl() {
    }

    @Override // com.xiaomi.settingsdk.backup.ICloudBackup
    public void onBackupSettings(Context context, DataPackage dataPackage) {
        Log.f(TAG, "onBackupSettings start");
        if (isSkipBackupRestoreRingtone()) {
            Log.d(TAG, "isSkipBackupRestoreRingtone: " + isSkipBackupRestoreRingtone());
            try {
                dataPackage.addKeyJson(J_DB, DbTool.db2json(new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(context)).getReadableDatabase(), ALARMS_DB_NAME));
                dataPackage.addKeyJson(J_TIMER_DB, DbTool.db2json(new AdditionDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(context)).getReadableDatabase(), "common_timers"));
            } catch (Exception e) {
                Log.e(TAG, "backup db error: " + e.getMessage());
            }
            try {
                SettingsBackup settingsBackup = new SettingsBackup();
                settingsBackup.loadFromPhone(context);
                dataPackage.addKeyJson("settings", settingsBackup.getSettingsObject());
            } catch (Exception e2) {
                Log.e(TAG, "backup settings error: " + e2.getMessage());
            }
            try {
                File file = new File(FBEUtil.createDeviceProtectedStorageContext(context).getFilesDir().getAbsolutePath() + File.separator + RingtoneHelper.RINGTONE_DIRECTORY);
                if (file.exists()) {
                    File[] fileArrListFiles = file.listFiles();
                    if (fileArrListFiles == null) {
                        return;
                    }
                    for (int i = 0; i < fileArrListFiles.length; i++) {
                        if (fileArrListFiles[i].isFile()) {
                            File file2 = fileArrListFiles[i];
                            dataPackage.addKeyFile(file2.getPath(), file2);
                        }
                    }
                }
            } catch (Exception e3) {
                Log.e(TAG, "backup ringtone error: " + e3.getMessage());
            }
        } else {
            try {
                SettingsBackup settingsBackup2 = new SettingsBackup();
                settingsBackup2.loadFromPhoneGoogle(context);
                dataPackage.addKeyJson("settings", settingsBackup2.getSettingsObject());
            } catch (Exception e4) {
                Log.e(TAG, "backup settings error: " + e4.getMessage());
            }
        }
        Log.f(TAG, "onBackupSettings end");
    }

    @Override // com.xiaomi.settingsdk.backup.ICloudBackup
    public void onRestoreSettings(Context context, DataPackage dataPackage, int i) {
        Log.f(TAG, "onRestoreSettings start packageVersion ：" + i);
        if (getCurrentVersion(context) == i || i == 3) {
            Log.d(TAG, "isSkipBackupRestoreRingtone: " + isSkipBackupRestoreRingtone());
            if (isSkipBackupRestoreRingtone()) {
                SettingItem<?> settingItem = dataPackage.get(J_DB);
                boolean z = settingItem instanceof KeyJsonSettingItem;
                if (z) {
                    try {
                        DbTool.jsonAlarmAndClockdb(new AlarmDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(context)).getWritableDatabase(), ((KeyJsonSettingItem) settingItem).getValue());
                        Intent intent = new Intent(DeskClockTabActivity.ACTION_ALARM_CHANGED);
                        intent.setPackage(BuildConfig.APPLICATION_ID);
                        context.sendBroadcast(intent);
                    } catch (Exception e) {
                        Log.e(TAG, "restore db error: " + e.getMessage());
                    }
                }
                SettingItem<?> settingItem2 = dataPackage.get(J_TIMER_DB);
                if (settingItem2 instanceof KeyJsonSettingItem) {
                    try {
                        DbTool.json2db(new AdditionDatabaseHelper(FBEUtil.createDeviceProtectedStorageContext(context)).getWritableDatabase(), ((KeyJsonSettingItem) settingItem2).getValue());
                        Intent intent2 = new Intent(DeskClockTabActivity.ACTION_ALARM_CHANGED);
                        intent2.setPackage(BuildConfig.APPLICATION_ID);
                        context.sendBroadcast(intent2);
                    } catch (Exception e2) {
                        Log.e(TAG, "restore db error: " + e2.getMessage());
                    }
                }
                SettingItem<?> settingItem3 = dataPackage.get("settings");
                if (z) {
                    try {
                        JSONObject value = ((KeyJsonSettingItem) settingItem3).getValue();
                        SettingsBackup settingsBackup = new SettingsBackup();
                        settingsBackup.setData(value, context);
                        settingsBackup.restoreToPhone(context);
                        Intent intent3 = new Intent(DeskClockTabActivity.ACTION_ALARM_CHANGED);
                        intent3.setPackage(BuildConfig.APPLICATION_ID);
                        context.sendBroadcast(intent3);
                    } catch (Exception e3) {
                        Log.e(TAG, "restore settings error: " + e3.getMessage());
                    }
                }
            } else {
                SettingItem<?> settingItem4 = dataPackage.get("settings");
                if (settingItem4 instanceof KeyJsonSettingItem) {
                    try {
                        JSONObject value2 = ((KeyJsonSettingItem) settingItem4).getValue();
                        SettingsBackup settingsBackup2 = new SettingsBackup();
                        settingsBackup2.setDataGoogle(value2);
                        settingsBackup2.restoreToPhoneGoogle(context);
                        Intent intent4 = new Intent(DeskClockTabActivity.ACTION_ALARM_CHANGED);
                        intent4.setPackage(BuildConfig.APPLICATION_ID);
                        context.sendBroadcast(intent4);
                    } catch (Exception e4) {
                        Log.e(TAG, "restore settings error: " + e4.getMessage());
                    }
                }
            }
        } else if (i == 1) {
            Log.e("packageVersion == 1, cannot restore, current version is " + getCurrentVersion(context));
        }
        Log.f(TAG, "onRestoreSettings end");
    }

    public boolean isSkipBackupRestoreRingtone() {
        return this.mSkipBackupRestoreRingtone;
    }

    public void setSkipBackupRestoreRingtone(boolean z) {
        this.mSkipBackupRestoreRingtone = z;
    }
}
