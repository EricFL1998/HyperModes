package com.android.deskclock.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

/* JADX INFO: loaded from: classes.dex */
public class SleepModeUtil {
    public static final String ACTION_REQUEST_WAKE = "com.miui.powerkeeper_request_wake";
    public static final String EXTRA_REASON = "reason";
    private static final String METHOD_GET_SLEEP_MODE_STATE = "getSleepModeState";
    private static final String METHOD_GET_SLEEP_MODE_STATE_KEY = "isInSleep";
    public static final String PACKAGE_NAME = "com.miui.powerkeeper";
    public static final int REASON_DESK_CLOCK = 1;
    private static final String TAG = "DC:SleepModeUtil";
    private static final String URI_PROVIDER = "content://com.miui.powerkeeper.configure";

    public static void exitSleepMode(Context context) {
        try {
            Intent intent = new Intent(ACTION_REQUEST_WAKE);
            intent.putExtra(EXTRA_REASON, 1);
            intent.setPackage("com.miui.powerkeeper");
            context.sendBroadcast(intent);
            Log.i(TAG, "request exit sleep mode");
        } catch (Exception e) {
            Log.e(TAG, "exitSleepMode error: " + e.getMessage());
        }
    }

    public static boolean inSleepMode(Context context) {
        try {
            boolean z = context.getContentResolver().call(Uri.parse(URI_PROVIDER), METHOD_GET_SLEEP_MODE_STATE, (String) null, (Bundle) null).getBoolean(METHOD_GET_SLEEP_MODE_STATE_KEY);
            Log.i(TAG, "get sleep mode: " + z);
            return z;
        } catch (Exception e) {
            Log.e(TAG, "getSleepMode error: " + e.getMessage());
            return false;
        }
    }
}
