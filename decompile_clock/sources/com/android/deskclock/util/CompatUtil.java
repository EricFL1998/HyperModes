package com.android.deskclock.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.UserHandle;
import com.android.deskclock.DeskClockApp;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class CompatUtil {
    private static final String TAG = "DC:CompatUtil";
    private static int sUserId;

    private CompatUtil() {
    }

    public static int getCurrentUser() {
        try {
            Method method = ActivityManager.class.getMethod("getCurrentUser", new Class[0]);
            method.setAccessible(true);
            return ((Integer) method.invoke(ActivityManager.class, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e(TAG, "getCurrentUser exception is " + e);
            return Integer.MIN_VALUE;
        }
    }

    public static int getUserId(Context context) {
        try {
            Method method = Context.class.getMethod("getUserId", new Class[0]);
            method.setAccessible(true);
            return ((Integer) method.invoke(context, new Object[0])).intValue();
        } catch (Exception e) {
            Log.e(TAG, "getUserId exception is " + e);
            return Integer.MIN_VALUE;
        }
    }

    public static boolean startActivityAsUser(Context context, Intent intent, UserHandle userHandle) {
        try {
            Method method = Context.class.getMethod("startActivityAsUser", Intent.class, UserHandle.class);
            method.setAccessible(true);
            method.invoke(context, intent, userHandle);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "startActivityAsUser exception is " + e);
            return false;
        }
    }

    public static Object getMyUserId() {
        try {
            return Integer.valueOf(DeskClockApp.getAppDEContext().getPackageManager().getPackageInfo(DeskClockApp.getAppDEContext().getPackageName(), 0).applicationInfo.uid);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
