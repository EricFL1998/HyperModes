package com.xiaomi.onetrack.b;

import android.text.TextUtils;
import com.xiaomi.onetrack.util.p;
import com.xiaomi.onetrack.util.q;

/* JADX INFO: loaded from: classes2.dex */
public class n {
    private static String a = "ConfigProvider";
    private static volatile boolean b = false;
    private static volatile boolean c = true;

    public static boolean a() {
        try {
            String[] strArrB = com.xiaomi.onetrack.d.f.a().b();
            return (!TextUtils.isEmpty(strArrB[0]) && !TextUtils.isEmpty(strArrB[1])) && !q.a(a);
        } catch (Exception e) {
            p.a(a, "ConfigProvider.available", e);
            return false;
        }
    }

    public static int a(int i) {
        int iIntValue;
        if (p.b) {
            p.a(a, "debug upload mode, send events immediately");
            return 0;
        }
        try {
            iIntValue = e.c().get(Integer.valueOf(i + 1)).intValue();
        } catch (Exception unused) {
            iIntValue = 60000;
        }
        p.a(a, "getUploadInterval " + iIntValue);
        return iIntValue;
    }

    public static synchronized void a(boolean z) {
        b = z;
    }

    public static synchronized boolean b() {
        return b;
    }

    public static void b(boolean z) {
        c = z;
    }

    public static boolean c() {
        return c;
    }
}
