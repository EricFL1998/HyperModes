package com.android.deskclock;

import android.content.Context;
import android.os.PowerManager;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class AlarmAlertWakeLock {
    private static PowerManager.WakeLock sCpuWakeLock;

    public static PowerManager.WakeLock createPartialWakeLock(Context context) {
        return createPartialWakeLock(context, Log.TAG);
    }

    public static PowerManager.WakeLock createPartialWakeLock(Context context, String str) {
        return ((PowerManager) context.getSystemService("power")).newWakeLock(1, str);
    }

    public static void acquireCpuWakeLock(Context context) {
        if (sCpuWakeLock != null) {
            return;
        }
        PowerManager.WakeLock wakeLockCreatePartialWakeLock = createPartialWakeLock(context);
        sCpuWakeLock = wakeLockCreatePartialWakeLock;
        wakeLockCreatePartialWakeLock.acquire();
    }

    public static void releaseCpuLock() {
        PowerManager.WakeLock wakeLock = sCpuWakeLock;
        if (wakeLock != null) {
            wakeLock.release();
            sCpuWakeLock = null;
        }
    }
}
