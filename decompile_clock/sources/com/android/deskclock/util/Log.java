package com.android.deskclock.util;

import com.android.deskclock.DeskClockApp;
import com.android.deskclock.util.log.ExLogger;
import com.android.deskclock.util.log.MiuiLogger;

/* JADX INFO: loaded from: classes.dex */
public class Log {
    public static final String TAG = "DC:AlarmClock";

    public static void v(String str) {
        v(TAG, str);
    }

    public static void v(String str, String str2) {
        android.util.Log.v(str, str2);
    }

    public static void d(String str) {
        d(TAG, str);
    }

    public static void d(String str, String str2) {
        android.util.Log.d(str, str2);
    }

    public static void i(String str) {
        i(TAG, str);
    }

    public static void i(String str, String str2) {
        android.util.Log.i(str, str2);
    }

    public static void w(String str) {
        w(TAG, str);
    }

    public static void w(String str, String str2) {
        android.util.Log.w(str, str2);
    }

    public static void e(String str) {
        e(TAG, str);
    }

    public static void e(String str, String str2) {
        android.util.Log.e(str, str2);
    }

    public static void e(String str, Exception exc) {
        e(TAG, str, exc);
    }

    public static void e(String str, String str2, Exception exc) {
        android.util.Log.e(str, str2, exc);
    }

    public static void f(String str) {
        f(TAG, str);
    }

    public static void f(String str, String str2) {
        ExLogger.getInstance().i(str, str2);
        MiuiLogger.info(DeskClockApp.getAppContext(), str, str2);
    }
}
