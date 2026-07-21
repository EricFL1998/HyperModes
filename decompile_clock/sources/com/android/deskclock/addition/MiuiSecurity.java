package com.android.deskclock.addition;

import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class MiuiSecurity {
    private static final String PACKAGE_NAME = "com.lbe.security.miui";
    private static final int SYSTEM_PERMISSION_DECLARE_MIN_VERSION_CODE = 111;
    private static final String TAG = "DC:MiuiSecurity";
    private static int VERSION_CODE = -1;

    private static int getVersionCode() {
        if (VERSION_CODE == -1) {
            VERSION_CODE = Util.getPackageCode(PACKAGE_NAME);
        }
        return VERSION_CODE;
    }

    public static boolean supportSystemPermissionDeclare() {
        return !Util.isInternational() && getVersionCode() >= 111;
    }
}
