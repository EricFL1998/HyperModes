package com.android.deskclock.util.log;

import android.content.Context;
import miuix.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class MiuiLogger {
    private static final String TAG = "DC:MiuiLogger";

    public static void info(Context context, String str, String str2) {
        if (context != null) {
            try {
                Log.getAsyncFileLogger(context).info(str, str2);
            } catch (Exception e) {
                android.util.Log.e(TAG, "MiuiLog error is " + e);
            }
        }
    }
}
