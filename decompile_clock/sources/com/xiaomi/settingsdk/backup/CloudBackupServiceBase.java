package com.xiaomi.settingsdk.backup;

import android.app.IntentService;
import android.content.Intent;
import android.os.BadParcelableException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.util.Log;
import com.xiaomi.settingsdk.backup.data.DataPackage;
import com.xiaomi.settingsdk.backup.data.SettingItem;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CloudBackupServiceBase extends IntentService {
    public static final String ACTION_CLOUD_BACKUP_SETTINGS = "miui.action.CLOUD_BACKUP_SETTINGS";
    public static final String ACTION_CLOUD_RESTORE_SETTINGS = "miui.action.CLOUD_RESTORE_SETTINGS";
    private static final int CODE_SETTINGS_SERVICE_RESULT_FAILED = 1;
    private static final int CODE_SETTINGS_SERVICE_RESULT_OK = 0;
    public static final String KEY_RESULT_RECEIVER = "result_receiver";
    private static final String TAG = "SettingsBackup";
    private static final ExecutorService sSettingsExecutor = Executors.newSingleThreadExecutor();
    private final IBackupRestoreSettings.Stub mBackupRestoreSettingsBinder;

    protected abstract ICloudBackup getBackupImpl();

    public CloudBackupServiceBase() {
        super("SettingsBackup");
        this.mBackupRestoreSettingsBinder = new IBackupRestoreSettings.Stub() { // from class: com.xiaomi.settingsdk.backup.CloudBackupServiceBase.1
            @Override // com.xiaomi.settingsdk.backup.IBackupRestoreSettings
            public void handleSettingsIntent(Intent intent) throws RemoteException {
                CloudBackupServiceBase.sSettingsExecutor.submit(CloudBackupServiceBase.this.new SettingsIntentRunner(intent, null));
            }
        };
    }

    private class SettingsIntentRunner implements Runnable {
        private final Intent mIntent;
        private final Integer mStartId;

        public SettingsIntentRunner(Intent intent, Integer num) {
            this.mIntent = intent;
            this.mStartId = num;
        }

        @Override // java.lang.Runnable
        public void run() {
            CloudBackupServiceBase.this.handleIntent(this.mIntent, this.mStartId);
        }
    }

    private String prependPackageName(String str) {
        return getPackageName() + ": " + str;
    }

    protected static void dumpDataPackage(DataPackage dataPackage) {
        for (Map.Entry<String, SettingItem<?>> entry : dataPackage.getDataItems().entrySet()) {
            Log.d("SettingsBackup", "key: " + entry.getKey() + ", value: " + entry.getValue().getValue());
        }
    }

    @Override // android.app.IntentService
    protected void onHandleIntent(Intent intent) {
        Log.d("SettingsBackup", "@Deprecated :: onHandleIntent(" + intent + ")");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleIntent(Intent intent, Integer num) {
        boolean zRestoreSettings;
        Log.d("SettingsBackup", "new_version_check_backup");
        if (intent == null) {
            if (num != null) {
                stopSelf(num.intValue());
                return;
            }
            return;
        }
        Log.d("SettingsBackup", prependPackageName("myPid: " + Process.myPid()));
        Log.d("SettingsBackup", prependPackageName("intent: " + intent));
        Log.d("SettingsBackup", prependPackageName("extras: " + intent.getExtras()));
        String action = intent.getAction();
        ResultReceiver resultReceiver = (ResultReceiver) intent.getParcelableExtra("result_receiver");
        if ("miui.action.CLOUD_BACKUP_SETTINGS".equals(action)) {
            if (resultReceiver != null) {
                Bundle bundleBackupSettings = backupSettings();
                if (bundleBackupSettings == null) {
                    Log.e("SettingsBackup", prependPackageName("bundle result is null after backupSettings"));
                }
                resultReceiver.send(0, bundleBackupSettings);
            }
        } else if ("miui.action.CLOUD_RESTORE_SETTINGS".equals(action) && resultReceiver != null) {
            IBinder binder = intent.getExtras().getBinder(DataPackage.KEY_DATA_PACKAGE);
            Parcel parcelObtain = Parcel.obtain();
            Parcel parcelObtain2 = Parcel.obtain();
            try {
                try {
                    binder.transact(2, parcelObtain, parcelObtain2, 0);
                    zRestoreSettings = restoreSettings((DataPackage) parcelObtain2.readParcelable(getClass().getClassLoader()), intent.getIntExtra("version", -1));
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                } catch (BadParcelableException e) {
                    Log.e("SettingsBackup", "BadParcelableException when read readParcelable", e);
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                    zRestoreSettings = false;
                } catch (RemoteException e2) {
                    Log.e("SettingsBackup", "RemoteException in onHandleIntent()", e2);
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                    zRestoreSettings = false;
                } catch (ClassCastException unused) {
                    Log.e("SettingsBackup", "ClassCastException when cast DataPackage");
                    parcelObtain.recycle();
                    parcelObtain2.recycle();
                    zRestoreSettings = false;
                }
                if (zRestoreSettings) {
                    resultReceiver.send(0, new Bundle());
                } else {
                    resultReceiver.send(1, null);
                }
                Log.d("SettingsBackup", prependPackageName("r.send()" + Thread.currentThread()));
            } catch (Throwable th) {
                parcelObtain.recycle();
                parcelObtain2.recycle();
                throw th;
            }
        }
        if (num != null) {
            stopSelf(num.intValue());
        }
    }

    @Override // android.app.IntentService, android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        sSettingsExecutor.submit(new SettingsIntentRunner(intent, Integer.valueOf(i2)));
        return 2;
    }

    @Override // android.app.IntentService, android.app.Service
    public IBinder onBind(Intent intent) {
        return this.mBackupRestoreSettingsBinder;
    }

    private boolean restoreSettings(DataPackage dataPackage, int i) {
        Log.d("SettingsBackup", prependPackageName("SettingsBackupServiceBase:restoreSettings"));
        ICloudBackup iCloudBackupCheckAndGetBackuper = checkAndGetBackuper();
        int currentVersion = iCloudBackupCheckAndGetBackuper.getCurrentVersion(getApplicationContext());
        if (i > currentVersion) {
            Log.w("SettingsBackup", "drop restore data because dataVersion is higher than currentAppVersion, dataVersion: " + i + ", currentAppVersion: " + currentVersion);
            return false;
        }
        iCloudBackupCheckAndGetBackuper.onRestoreSettings(getApplicationContext(), dataPackage, i);
        return true;
    }

    private Bundle backupSettings() {
        Log.d("SettingsBackup", prependPackageName("SettingsBackupServiceBase:backupSettings"));
        ICloudBackup iCloudBackupCheckAndGetBackuper = checkAndGetBackuper();
        DataPackage dataPackage = new DataPackage();
        iCloudBackupCheckAndGetBackuper.onBackupSettings(getApplicationContext(), dataPackage);
        Bundle bundle = new Bundle();
        dataPackage.appendToWrappedBundle(bundle);
        bundle.putInt("version", iCloudBackupCheckAndGetBackuper.getCurrentVersion(getApplicationContext()));
        return bundle;
    }

    private ICloudBackup checkAndGetBackuper() {
        ICloudBackup backupImpl = getBackupImpl();
        if (backupImpl != null) {
            return backupImpl;
        }
        throw new IllegalArgumentException("backuper must not be null");
    }
}
