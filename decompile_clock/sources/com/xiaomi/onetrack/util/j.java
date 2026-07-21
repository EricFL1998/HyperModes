package com.xiaomi.onetrack.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.UserManager;
import android.os.storage.StorageManager;
import android.preference.PreferenceManager;

/* JADX INFO: loaded from: classes2.dex */
public class j {
    private static final String a = "FbeUtil";

    private static boolean f(Context context) {
        return false;
    }

    private j() {
    }

    public static Context a(Context context) {
        if (e(context)) {
            p.a(a, "getSafeContext return origin ctx");
            return context;
        }
        p.a(a, "getSafeContext , create the safe ctx");
        return context.createDeviceProtectedStorageContext();
    }

    public static boolean a() {
        try {
            return ((Boolean) StorageManager.class.getDeclaredMethod("isFileEncryptedNativeOrEmulated", new Class[0]).invoke(null, new Object[0]).getClass().getDeclaredMethod("isFileEncryptedNativeOrEmulated", Boolean.TYPE).invoke(null, new Object[0])).booleanValue();
        } catch (Exception e) {
            p.b(a, "*** " + e);
            return false;
        }
    }

    public static void a(PreferenceManager preferenceManager) {
        preferenceManager.setStorageDeviceProtected();
    }

    public static boolean b(Context context) {
        try {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService("keyguard");
            return a() && keyguardManager != null && keyguardManager.isKeyguardSecure();
        } catch (Exception e) {
            p.a(a, "FBEDeviceAndSetedUpScreenLock Exception: " + e.getMessage());
            return false;
        }
    }

    public static boolean c(Context context) {
        return b(context) && !e(context);
    }

    public static boolean d(Context context) {
        return !e(context);
    }

    private static boolean e(Context context) {
        try {
            UserManager userManager = (UserManager) context.getSystemService("user");
            return userManager != null && userManager.isUserUnlocked();
        } catch (Exception e) {
            p.a(a, "isUserUnlocked Exception: " + e.getMessage());
            return false;
        }
    }
}
