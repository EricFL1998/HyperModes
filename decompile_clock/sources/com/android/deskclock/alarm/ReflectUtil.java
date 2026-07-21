package com.android.deskclock.alarm;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import miui.os.Build;

/* JADX INFO: loaded from: classes.dex */
public class ReflectUtil {
    private static final String KEY_INCOMING_STRIP_LIGHT_SETTINGS = "settings_strip_light_enable";
    private static final int STRIP_LIGHT_OFF = 1;
    private static final int STRIP_LIGHT_OFF_FOR_O10U = 0;
    private static final int STRIP_LIGHT_ON = 0;
    private static final String TAG = "DC:ReflectUtil";

    public static <T> T callObjectMethod(Object obj, Class<T> cls, String str, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return (T) declaredMethod.invoke(obj, objArr);
    }

    public static Object callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        return obj.getClass().getDeclaredMethod(str, clsArr).invoke(obj, objArr);
    }

    public static Object callStaticObjectMethod(Class<?> cls, String str, Class<?>[] clsArr, Object... objArr) throws IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
        Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(null, objArr);
    }

    public static boolean isSupportBackStrap(Context context) {
        boolean zBooleanValue = false;
        try {
            zBooleanValue = ((Boolean) Class.forName("android.app.ColorLightManager").getMethod("isSupportLedStrip", new Class[0]).invoke(null, new Object[0])).booleanValue();
            Log.d(TAG, "Is LED strip supported: " + zBooleanValue);
            return zBooleanValue;
        } catch (Exception e) {
            Log.e(TAG, "Fail to get status for: isSupportLedStrip");
            e.printStackTrace();
            return zBooleanValue;
        }
    }

    public static boolean isStripLightEnable(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), KEY_INCOMING_STRIP_LIGHT_SETTINGS, !Build.DEVICE.equals("onyx") ? 1 : 0) != 0;
    }
}
