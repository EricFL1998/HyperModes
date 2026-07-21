package com.android.deskclock.util;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.provider.Settings;
import java.lang.reflect.Method;
import miuix.core.util.SystemProperties;

/* JADX INFO: loaded from: classes.dex */
public class GestureLineUtil {
    private static final String HIDE_GESTURE_LINE = "hide_gesture_line";
    private static final String USE_GESTURE_VERSION_THREE = "use_gesture_version_three";

    private static boolean isEnableGestureLine(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), HIDE_GESTURE_LINE, 0) == 0;
    }

    private static boolean isSupportGestureLine(Context context) {
        try {
            Method declaredMethod = Class.forName("android.provider.MiuiSettings$Global").getDeclaredMethod("getBoolean", ContentResolver.class, String.class);
            declaredMethod.setAccessible(true);
            return ((Boolean) declaredMethod.invoke(null, context.getContentResolver(), USE_GESTURE_VERSION_THREE)).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getGestureLineHeight(Context context) {
        int navigationBarHeightFromProp;
        try {
            navigationBarHeightFromProp = (isInFullWindowGestureMode(context) && isMiuiXIISdkSupported() && isSupportGestureLine(context) && isEnableGestureLine(context)) ? getNavigationBarHeightFromProp(context) : 0;
        } catch (Exception e) {
            android.util.Log.e("DC:GestureLineUtil", "getGestureLineHeight error: " + e);
        }
        int i = navigationBarHeightFromProp >= 0 ? navigationBarHeightFromProp : 0;
        android.util.Log.d("GestureLineUtil", "getGestureLineHeight = " + i);
        return i;
    }

    public static int getNavigationBarHeight(Context context) {
        int navigationBarHeightFromProp;
        if (isInFullWindowGestureMode(context)) {
            navigationBarHeightFromProp = (isMiuiXIISdkSupported() && isSupportGestureLine(context) && isEnableGestureLine(context)) ? getNavigationBarHeightFromProp(context) : 0;
        } else {
            navigationBarHeightFromProp = getNavigationBarHeightFromProp(context);
        }
        int i = navigationBarHeightFromProp >= 0 ? navigationBarHeightFromProp : 0;
        android.util.Log.d("GestureLineUtil", "getNavigationBarHeight = " + i);
        return i;
    }

    private static boolean isInFullWindowGestureMode(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "force_fsg_nav_bar", 0) != 0;
    }

    private static boolean isMiuiXIISdkSupported() {
        return SystemProperties.getInt("ro.miui.ui.version.code", 0) >= 10;
    }

    private static int getNavigationBarHeightFromProp(Context context) {
        Resources resources;
        int identifier;
        if (hasNavigationBar(context) && (identifier = (resources = context.getResources()).getIdentifier("navigation_bar_height", "dimen", "android")) > 0) {
            return resources.getDimensionPixelSize(identifier);
        }
        return 0;
    }

    private static boolean hasNavigationBar(Context context) {
        if (Build.VERSION.SDK_INT > 28) {
            try {
                Object objInvoke = Class.forName("android.view.WindowManagerGlobal").getMethod("getWindowManagerService", new Class[0]).invoke(null, new Object[0]);
                return ((Boolean) objInvoke.getClass().getMethod("hasNavigationBar", Integer.TYPE).invoke(objInvoke, context.getClass().getMethod("getDisplayId", new Class[0]).invoke(context, new Object[0]))).booleanValue();
            } catch (Exception e) {
                android.util.Log.d("DisplayUtils", "hasNavigationBar Q", e);
            }
        } else {
            try {
                Object objInvoke2 = Class.forName("android.view.WindowManagerGlobal").getMethod("getWindowManagerService", new Class[0]).invoke(null, new Object[0]);
                return ((Boolean) objInvoke2.getClass().getMethod("hasNavigationBar", new Class[0]).invoke(objInvoke2, new Object[0])).booleanValue();
            } catch (Exception e2) {
                android.util.Log.d("DisplayUtils", "hasNavigationBar", e2);
            }
        }
        return false;
    }
}
