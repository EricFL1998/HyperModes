package com.xiaomi.onetrack.f;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.xiaomi.onetrack.util.j;

/* JADX INFO: loaded from: classes2.dex */
public class a {
    private static Context a = null;
    private static Context b = null;
    private static int c = 0;
    private static String d = null;
    private static String e = null;
    private static long f = 0;
    private static volatile boolean g = false;

    public static void a(Context context) {
        if (g) {
            return;
        }
        synchronized (a.class) {
            if (g) {
                return;
            }
            a = context;
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(a.getPackageName(), 0);
                c = packageInfo.versionCode;
                d = packageInfo.versionName;
                f = packageInfo.lastUpdateTime;
                e = a.getPackageName();
            } catch (PackageManager.NameNotFoundException e2) {
                e2.printStackTrace();
            }
            g = true;
        }
    }

    public static boolean a(ApplicationInfo applicationInfo) {
        return (applicationInfo.flags & 1) != 0;
    }

    public static boolean a(Context context, String str) {
        try {
            return a(a(context, str, 0).applicationInfo);
        } catch (Exception unused) {
            return false;
        }
    }

    public static PackageInfo a(Context context, String str, int i) {
        try {
            return context.getPackageManager().getPackageInfo(str, i);
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    public static boolean b(Context context, String str) {
        PackageInfo packageInfoA = a(context, str, 0);
        return (packageInfoA == null || packageInfoA.applicationInfo == null) ? false : true;
    }

    public static String c(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            return packageManager.getApplicationInfo(str, 0).loadLabel(packageManager).toString();
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
            return "";
        }
    }

    public static Context a() {
        if (j.d(a)) {
            Context context = b;
            if (context != null) {
                return context;
            }
            synchronized (a.class) {
                if (b == null) {
                    b = j.a(a);
                }
            }
            return b;
        }
        return a;
    }

    public static Context b() {
        return a;
    }

    public static String c() {
        return d;
    }

    public static int d() {
        return c;
    }

    public static String e() {
        return e;
    }

    public static long f() {
        return f;
    }
}
