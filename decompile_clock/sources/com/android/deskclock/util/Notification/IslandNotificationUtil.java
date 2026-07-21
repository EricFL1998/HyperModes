package com.android.deskclock.util.Notification;

import android.content.pm.PackageInfo;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class IslandNotificationUtil {
    private static final String TAG = "DC:IslandNotificationUtil";

    public static boolean isAddTimerTotal() {
        try {
            PackageInfo packageInfo = DeskClockApp.getAppDEContext().getPackageManager().getPackageInfo("miui.systemui.plugin", 0);
            return packageInfo != null && packageInfo.versionCode >= 170022800;
        } catch (Exception e) {
            Log.e(TAG, "isAddTimerTotal", e);
        }
    }
}
