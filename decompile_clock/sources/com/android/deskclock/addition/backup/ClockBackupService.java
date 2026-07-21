package com.android.deskclock.addition.backup;

import com.xiaomi.settingsdk.backup.CloudBackupServiceBase;
import com.xiaomi.settingsdk.backup.ICloudBackup;

/* JADX INFO: loaded from: classes.dex */
public class ClockBackupService extends CloudBackupServiceBase {
    @Override // com.xiaomi.settingsdk.backup.CloudBackupServiceBase
    protected ICloudBackup getBackupImpl() {
        return new ClocksBackupImpl();
    }
}
