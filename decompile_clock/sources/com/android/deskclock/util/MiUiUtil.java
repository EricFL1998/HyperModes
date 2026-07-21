package com.android.deskclock.util;

import android.app.Application;
import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MiUiUtil {
    private static final String SDK_ENTRANCE_CLASS = "miui.core.SdkManager";
    private static final String SDK_ENTRANCE_FALLBACK_CLASS = "com.miui.internal.core.SdkManager";
    private static String TAG = "DC:MiUiUtil";

    public static void init(Context context) {
        if (initializeSdk(context)) {
            startSdk();
        }
    }

    private static boolean initializeSdk(Context context) {
        try {
            int iIntValue = ((Integer) getSdkEntrance().getMethod("initialize", Application.class, Map.class).invoke(null, context, new HashMap())).intValue();
            Log.d(TAG, "initializeSdk code:" + iIntValue);
            return iIntValue == 0;
        } catch (Throwable th) {
            Log.e(TAG, "initializeSdk error: " + th);
            return false;
        }
    }

    private static void startSdk() {
        try {
            Log.d(TAG, "startSdk code:" + ((Integer) getSdkEntrance().getMethod("start", Map.class).invoke(null, new HashMap())).intValue());
        } catch (Throwable th) {
            Log.e(TAG, "startSdk error: " + th);
        }
    }

    public static Class<?> getSdkEntrance() throws ClassNotFoundException {
        try {
            try {
                return Class.forName(SDK_ENTRANCE_CLASS);
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "getSdkEntrance error, no sdk found: " + e);
                throw e;
            }
        } catch (ClassNotFoundException unused) {
            return Class.forName(SDK_ENTRANCE_FALLBACK_CLASS);
        }
    }
}
