package com.android.deskclock.addition;

import android.util.Log;
import com.android.deskclock.util.Util;

/* JADX INFO: loaded from: classes.dex */
public class MiuiMusic {
    public static final int MUSIC_RADIO_VERSION_CODE = 10350;
    private static final String PACKAGE_NAME = "com.miui.player";
    private static final String TAG = "DC:MiuiMusic";
    private static int VERSION_CODE = -1;

    private static int getVersionCode() {
        VERSION_CODE = Util.getPackageCode("com.miui.player");
        Log.d(TAG, "getVersionCode: " + VERSION_CODE);
        return VERSION_CODE;
    }

    public static boolean supportNews() {
        return getVersionCode() >= 10350;
    }
}
