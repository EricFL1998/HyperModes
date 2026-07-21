package com.android.deskclock.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserManager;
import android.os.storage.StorageManager;
import androidx.preference.PreferenceManager;
import com.android.deskclock.DeskClockApp;
import com.android.deskclock.alarm.AlarmClockFragment;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes.dex */
public class FBEUtil {
    private static final String KEY_MOVE_DATA_FINISH = "key_move_data_finish";
    private static final String[] SHAREDPREF_NAME = {AlarmClockFragment.PREFERENCES, "com.android.deskclock_preferences"};
    private static String mDataBaseName = "alarms.db";

    private FBEUtil() {
    }

    public static Context createDeviceProtectedStorageContext(Context context) {
        if (context == null) {
            context = DeskClockApp.getAppContext();
        }
        Context contextCreateDeviceProtectedStorageContext = context.createDeviceProtectedStorageContext();
        if (contextCreateDeviceProtectedStorageContext != null) {
            return contextCreateDeviceProtectedStorageContext;
        }
        Context appContext = DeskClockApp.getAppContext();
        Log.e("createDeviceProtectedStorageContext error");
        return appContext;
    }

    public static boolean isFileEncryptedNativeOrEmulated() {
        try {
            Method method = StorageManager.class.getMethod("isFileEncryptedNativeOrEmulated", new Class[0]);
            method.setAccessible(true);
            return ((Boolean) method.invoke(StorageManager.class, new Object[0])).booleanValue();
        } catch (Exception unused) {
            return false;
        }
    }

    public static void setStorageDeviceProtectedForFBE(PreferenceManager preferenceManager) {
        preferenceManager.setStorageDeviceProtected();
    }

    public static SharedPreferences getSharedPreferences(Context context, String str, int i) {
        return createDeviceProtectedStorageContext(context).getSharedPreferences(str, i);
    }

    public static SharedPreferences getDefaultSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(createDeviceProtectedStorageContext(context));
    }

    public static boolean isFBEDeviceAndSetedUpScreenLock(Context context) {
        return isFileEncryptedNativeOrEmulated() && ((KeyguardManager) context.getSystemService("keyguard")).isKeyguardSecure();
    }

    public static boolean isLockedUnderFBE(Context context) {
        return isFBEDeviceAndSetedUpScreenLock(context) && isUserLocked(context);
    }

    public static boolean isUserUnlocked(Context context) {
        UserManager userManager = (UserManager) context.getSystemService("user");
        Log.d("isUserUnlocked: " + userManager.isUserUnlocked());
        return userManager.isUserUnlocked();
    }

    public static boolean isUserLocked(Context context) {
        return !((UserManager) context.getSystemService("user")).isUserUnlocked();
    }

    public static void moveData(Context context, Context context2) {
        if (getDefaultSharedPreferences(context2).getBoolean(KEY_MOVE_DATA_FINISH, true)) {
            boolean zMoveDatabaseFrom = context2.moveDatabaseFrom(context, mDataBaseName);
            for (String str : SHAREDPREF_NAME) {
                zMoveDatabaseFrom = zMoveDatabaseFrom && context2.moveSharedPreferencesFrom(context, str);
                Log.d("moveSharedPreferencesFrom and the name is " + str);
            }
            Log.d("Move the DataBaseFile and SharedPrefFile is " + zMoveDatabaseFrom);
            if (zMoveDatabaseFrom) {
                getDefaultSharedPreferences(context2).edit().putBoolean(KEY_MOVE_DATA_FINISH, false).commit();
            }
        }
    }
}
