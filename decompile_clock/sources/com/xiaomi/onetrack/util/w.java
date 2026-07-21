package com.xiaomi.onetrack.util;

import android.os.Looper;

/* JADX INFO: loaded from: classes2.dex */
public class w {
    private static final String a = "ProcessUtil";

    public static boolean a() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
