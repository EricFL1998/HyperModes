package com.xiaomi.onetrack.util;

import android.util.Log;

/* JADX INFO: loaded from: classes2.dex */
public class ab {
    private static final String a = "SystemProperties";

    public static String a(String str, String str2) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", String.class, String.class).invoke(null, str, str2);
        } catch (Throwable th) {
            Log.e(p.a(a), "get e" + th.getMessage());
            return str2;
        }
    }

    public static String a(String str) {
        return a(str, "");
    }

    public static long a(String str, Long l) {
        try {
            return ((Long) Class.forName("android.os.SystemProperties").getMethod("getLong", String.class, Long.TYPE).invoke(null, str, l)).longValue();
        } catch (Throwable th) {
            Log.e(p.a(a), "getLong e" + th.getMessage());
            return l.longValue();
        }
    }
}
