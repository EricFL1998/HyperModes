package com.xiaomi.onetrack.api;

import android.content.Context;
import android.content.Intent;
import android.os.Process;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    public static final String a = "action";
    private static final String b = "AppActiveBroadcastManager";
    private static volatile a c = null;
    private static final String d = "content://com.miui.analytics.OneTrackProvider/traceId";
    private static final String e = "traceId";
    private static final String f = "pkg";
    private static final String g = "sign";
    private static String j = null;
    private static final String k = "package";
    private static final String l = "installer";
    private static final String m = "userId";
    private static final String n = "miuiActiveId";
    private static final String o = "miuiActiveTime";
    private static final String p = "activeTime";
    private static final String q = "queryTime";
    private static final Set<String> r = new HashSet(Arrays.asList("com.xiaomi.xmsf", "com.xiaomi.market", "com.miui.packageinstaller"));
    private final Context h = com.xiaomi.onetrack.f.a.a();
    private final Context i = com.xiaomi.onetrack.f.a.b();

    public static a a() {
        if (c == null) {
            b();
        }
        return c;
    }

    public static void b() {
        if (c == null) {
            synchronized (a.class) {
                if (c == null) {
                    c = new a();
                }
            }
        }
    }

    private a() {
        j = com.xiaomi.onetrack.f.a.e();
    }

    public boolean c() {
        return r.contains(com.xiaomi.onetrack.f.a.b().getPackageName());
    }

    public String a(Intent intent) {
        Exception e2;
        String str;
        FutureTask futureTask = new FutureTask(new b(this, intent));
        long jCurrentTimeMillis = System.currentTimeMillis();
        com.xiaomi.onetrack.util.i.a(futureTask);
        try {
            str = (String) futureTask.get(5L, TimeUnit.SECONDS);
            try {
                if (com.xiaomi.onetrack.util.p.a) {
                    com.xiaomi.onetrack.util.p.a(b, "packageName:" + j + " _start ------getTraceId:" + str + "  _tid:" + Process.myTid());
                }
            } catch (Exception e3) {
                e2 = e3;
                com.xiaomi.onetrack.util.p.b(b, "getTraceId error: " + e2.toString());
            }
        } catch (Exception e4) {
            e2 = e4;
            str = "";
        }
        com.xiaomi.onetrack.util.p.a(b, "packageName:" + j + " _end ------getTraceId:" + str + "  _tid:" + Process.myTid() + " diffTime:" + (System.currentTimeMillis() - jCurrentTimeMillis));
        return str;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean f() {
        try {
            int i = com.xiaomi.onetrack.f.a.b().getPackageManager().getPackageInfo("com.miui.analytics", 0).versionCode;
            if (i >= 2023010300) {
                return true;
            }
            com.xiaomi.onetrack.util.p.a(b, "not support getTraceId versionCode: " + i);
        } catch (Throwable th) {
            com.xiaomi.onetrack.util.p.b(b, "isSupportEmptyEvent error:" + th.getMessage());
        }
        return false;
    }
}
