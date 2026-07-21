package com.android.deskclock.util;

import android.content.Context;
import android.content.Intent;

/* JADX INFO: loaded from: classes.dex */
public class BleUtil {
    private static final String ACTION_ALARM = "miui.deskclock.ACTION_ALARM";
    private static final String EXTRA_TIMES = "times";
    private static final String EXTRA_TYPE = "type";
    private static final String PERMISSION_BLE_IMMEDIATE_ALERT = "miui.permission.BLE_IMMEDIATE_ALERT";
    public static final int TYPE_LONG_VIBRATE = 2;
    public static final int TYPE_SHORT_VIBRATE = 1;
    public static final int TYPE_STOP = 0;

    public static void vibrateMiBracelet(Context context) {
        vibrateMiBracelet(context, 1, 3);
    }

    public static void vibrateMiBracelet(Context context, int i, int i2) {
        Intent intent = new Intent(ACTION_ALARM);
        intent.putExtra(EXTRA_TIMES, i2);
        intent.putExtra("type", i);
        context.sendBroadcast(intent, PERMISSION_BLE_IMMEDIATE_ALERT);
    }
}
