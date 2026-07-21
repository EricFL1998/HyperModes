package com.android.deskclock.addition.backup;

import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.content.Intent;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import com.android.deskclock.Alarm;
import com.android.deskclock.BuildConfig;
import com.android.deskclock.DeskClockTabActivity;
import com.android.deskclock.compat.ClockCompat;
import com.android.deskclock.util.AlarmHelper;
import com.xiaomi.settingsdk.backup.SettingsBackupHelper;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import miui.app.backup.BackupMeta;
import miui.app.backup.FullBackupAgent;
import miuix.core.util.IOUtils;

/* JADX INFO: loaded from: classes.dex */
public class DeskClockBackupAgent extends FullBackupAgent {
    private static final String KEY_AGENT_SETTINGS = "clock_settings";
    private static final String TAG = "DeskClockBackupAgent";

    protected int getVersion(int i) {
        return 2;
    }

    protected int onDataRestore(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
        int backupMetaVersion;
        try {
            backupMetaVersion = ClockCompat.getBackupMetaVersion(backupMeta);
        } catch (Exception e) {
            Log.e("DC:BackupAgent", "getBackupMetaVersion error: " + e);
            backupMetaVersion = 2;
        }
        Log.e("DC:BackupAgent", "onDataRestore version: " + backupMetaVersion);
        ClocksBackupImpl clocksBackupImpl = new ClocksBackupImpl();
        clocksBackupImpl.setSkipBackupRestoreRingtone(true);
        if (backupMetaVersion == 1) {
            deleteAlarms();
            FileInputStream fileInputStream = null;
            try {
                FileInputStream fileInputStream2 = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    byte[] fully = readFully(fileInputStream2);
                    Parcel parcelObtain = Parcel.obtain();
                    parcelObtain.unmarshall(fully, 0, fully.length);
                    parcelObtain.setDataPosition(0);
                    fileInputStream2.close();
                    readAlarmFromParcel(parcelObtain, backupMetaVersion);
                    parcelObtain.recycle();
                    Intent intent = new Intent(DeskClockTabActivity.ACTION_ALARM_CHANGED);
                    intent.setPackage(BuildConfig.APPLICATION_ID);
                    sendBroadcast(intent);
                    fileInputStream2.close();
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } else if (backupMetaVersion == 2 || backupMetaVersion == 3) {
            SettingsBackupHelper.restoreSettings(getApplicationContext(), parcelFileDescriptor, clocksBackupImpl);
        }
        Log.e("DC:BackupAgent", "onDataRestore end");
        return 0;
    }

    protected int onFullBackup(ParcelFileDescriptor parcelFileDescriptor, int i) throws IOException {
        ClocksBackupImpl clocksBackupImpl = new ClocksBackupImpl();
        clocksBackupImpl.setSkipBackupRestoreRingtone(true);
        Iterator<String> it = SettingsBackupHelper.backupSettings(getApplicationContext(), parcelFileDescriptor, clocksBackupImpl).getFileItems().keySet().iterator();
        while (it.hasNext()) {
            addAttachedFile(it.next());
        }
        return 0;
    }

    protected int onAttachRestore(BackupMeta backupMeta, ParcelFileDescriptor parcelFileDescriptor, String str) throws Throwable {
        FileInputStream fileInputStream;
        File file = new File(str);
        FileOutputStream fileOutputStream = null;
        try {
            if (file.exists()) {
                file.delete();
            }
            File parentFile = file.getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            file.createNewFile();
            FileOutputStream fileOutputStream2 = new FileOutputStream(file);
            try {
                FileInputStream fileInputStream2 = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i = fileInputStream2.read(bArr);
                        if (i > 0) {
                            fileOutputStream2.write(bArr, 0, i);
                        } else {
                            IOUtils.closeQuietly((OutputStream) fileOutputStream2);
                            IOUtils.closeQuietly((InputStream) fileInputStream2);
                            return 0;
                        }
                    }
                } catch (Exception e) {
                    fileOutputStream = fileOutputStream2;
                    fileInputStream = fileInputStream2;
                    e = e;
                    try {
                        Log.e("DC:DeskClockBackupAgent", "onAttachRestore error " + e);
                        IOUtils.closeQuietly((OutputStream) fileOutputStream);
                        IOUtils.closeQuietly((InputStream) fileInputStream);
                        return 1;
                    } catch (Throwable th) {
                        th = th;
                        IOUtils.closeQuietly((OutputStream) fileOutputStream);
                        IOUtils.closeQuietly((InputStream) fileInputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    fileOutputStream = fileOutputStream2;
                    fileInputStream = fileInputStream2;
                    th = th2;
                    IOUtils.closeQuietly((OutputStream) fileOutputStream);
                    IOUtils.closeQuietly((InputStream) fileInputStream);
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
                fileInputStream = null;
                fileOutputStream = fileOutputStream2;
            } catch (Throwable th3) {
                th = th3;
                fileInputStream = null;
                fileOutputStream = fileOutputStream2;
            }
        } catch (Exception e3) {
            e = e3;
            fileInputStream = null;
        } catch (Throwable th4) {
            th = th4;
            fileInputStream = null;
        }
    }

    private void deleteAlarms() {
        getContentResolver().delete(Alarm.Columns.CONTENT_URI, null, null);
    }

    private byte[] readFully(FileInputStream fileInputStream) throws IOException {
        byte[] bArr = new byte[fileInputStream.available()];
        int i = 0;
        while (true) {
            int i2 = fileInputStream.read(bArr, i, bArr.length - i);
            if (i2 <= 0) {
                return bArr;
            }
            i += i2;
            int iAvailable = fileInputStream.available();
            if (iAvailable > bArr.length - i) {
                byte[] bArr2 = new byte[iAvailable + i];
                System.arraycopy(bArr, 0, bArr2, 0, i);
                bArr = bArr2;
            }
        }
    }

    private void readAlarmFromParcel(Parcel parcel, int i) {
        int i2 = (int) parcel.readLong();
        for (int i3 = 0; i3 < i2; i3++) {
            Alarm alarm = new Alarm(parcel, i);
            AlarmHelper.addAlarm(getApplicationContext(), alarm);
            if (alarm.id == 0) {
                return;
            }
        }
    }

    public void onBackup(ParcelFileDescriptor parcelFileDescriptor, BackupDataOutput backupDataOutput, ParcelFileDescriptor parcelFileDescriptor2) throws Throwable {
        Log.i(TAG, "onBackup start");
        try {
            backupAgentWithKey(KEY_AGENT_SETTINGS, backupDataOutput);
        } catch (Exception e) {
            Log.e(TAG, "onBackup error", e);
        }
        Log.i(TAG, "onBackup end");
    }

    public void onRestore(BackupDataInput backupDataInput, int i, ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
        Log.i(TAG, "onRestore start");
        while (backupDataInput.readNextHeader()) {
            try {
                String key = backupDataInput.getKey();
                Log.i(TAG, "onRestore loading" + key);
                int dataSize = backupDataInput.getDataSize();
                byte[] bArr = new byte[dataSize];
                backupDataInput.readEntityData(bArr, 0, dataSize);
                File file = new File(getApplicationContext().getCacheDir(), key + "_restore_temp_file");
                if (!file.exists()) {
                    Log.i(TAG, "onRestore: " + file.createNewFile());
                }
                new FileOutputStream(file).write(bArr);
                restoreData(ParcelFileDescriptor.open(file, 268435456));
                Log.i(TAG, "restore delete tmp" + file.delete());
            } catch (IOException e) {
                Log.e(TAG, "onRestore error" + e);
            }
            Log.i(TAG, "onRestore end");
        }
    }

    private void backupAgentWithKey(String str, BackupDataOutput backupDataOutput) throws Throwable {
        try {
            Log.i(TAG, "backupAgentWithKey: " + str);
            File file = new File(getApplicationContext().getCacheDir(), str + "_backup_temp_file");
            if (!file.exists()) {
                Log.i(TAG, "newFile" + file.createNewFile());
            }
            fullBackup(ParcelFileDescriptor.open(file, 536870912));
            byte[] bArrConvertParcelFileDescriptorToByteArray = convertParcelFileDescriptorToByteArray(ParcelFileDescriptor.open(file, 268435456));
            backupDataOutput.writeEntityHeader(str, bArrConvertParcelFileDescriptorToByteArray.length);
            backupDataOutput.writeEntityData(bArrConvertParcelFileDescriptorToByteArray, bArrConvertParcelFileDescriptorToByteArray.length);
            Log.i(TAG, "backup delete tmp: " + file.delete());
        } catch (IOException e) {
            Log.e(TAG, "onBackup error: ", e);
        }
    }

    private byte[] convertParcelFileDescriptorToByteArray(ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
        ByteArrayOutputStream byteArrayOutputStream;
        FileInputStream fileInputStream = null;
        try {
            try {
                FileInputStream fileInputStream2 = new FileInputStream(parcelFileDescriptor.getFileDescriptor());
                try {
                    try {
                        int iAvailable = fileInputStream2.available();
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            byte[] bArr = new byte[1024];
                            int i = 0;
                            while (true) {
                                int i2 = fileInputStream2.read(bArr);
                                if (i2 == -1) {
                                    break;
                                }
                                i += i2;
                                byteArrayOutputStream.write(bArr, 0, i2);
                                if (iAvailable == i) {
                                    Log.i(TAG, "write end");
                                    break;
                                }
                            }
                            fileInputStream2.close();
                        } catch (IOException e) {
                            e = e;
                            fileInputStream = fileInputStream2;
                            Log.e(TAG, "convertParcelFd", e);
                            if (fileInputStream != null) {
                                fileInputStream.close();
                            }
                        }
                    } catch (IOException e2) {
                        e = e2;
                        byteArrayOutputStream = null;
                    }
                    Log.i(TAG, "convertParcelFileDescriptorToByteArray end");
                    return byteArrayOutputStream == null ? new byte[0] : byteArrayOutputStream.toByteArray();
                } catch (Throwable th) {
                    th = th;
                    fileInputStream = fileInputStream2;
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            } catch (IOException e3) {
                e = e3;
                byteArrayOutputStream = null;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int fullBackup(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
        ClocksBackupImpl clocksBackupImpl = new ClocksBackupImpl();
        clocksBackupImpl.setSkipBackupRestoreRingtone(false);
        Iterator<String> it = SettingsBackupHelper.backupSettings(this, parcelFileDescriptor, clocksBackupImpl).getFileItems().keySet().iterator();
        while (it.hasNext()) {
            addAttachedFile(it.next());
        }
        return 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private int restoreData(ParcelFileDescriptor parcelFileDescriptor) throws Throwable {
        ClocksBackupImpl clocksBackupImpl = new ClocksBackupImpl();
        clocksBackupImpl.setSkipBackupRestoreRingtone(false);
        SettingsBackupHelper.restoreSettings(this, parcelFileDescriptor, clocksBackupImpl);
        return 0;
    }
}
