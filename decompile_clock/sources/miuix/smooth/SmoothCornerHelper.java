package miuix.smooth;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import miuix.core.util.SystemProperties;
import miuix.reflect.ReflectionHelper;

/* JADX INFO: loaded from: classes3.dex */
public class SmoothCornerHelper {
    public static Boolean FORCE_USE_SMOOTH;
    public static boolean IS_SUPPORT_SMOOTH_CORNER;
    private static Boolean sEnableAppSmoothCorner;

    static {
        boolean z = Boolean.parseBoolean(SystemProperties.get("persist.sys.support_view_smoothcorner", "false"));
        IS_SUPPORT_SMOOTH_CORNER = z;
        if (z) {
            return;
        }
        Log.d("SmoothCornerHelper", "this device is not support system smooth corner");
    }

    public static void init(Context context) {
        if (IS_SUPPORT_SMOOTH_CORNER) {
            isEnableAppSmoothCorner(context);
        }
    }

    public static boolean isEnableAppSmoothCorner(Context context) {
        if (sEnableAppSmoothCorner == null) {
            try {
                Boolean bool = (Boolean) ReflectionHelper.invokeObject(ApplicationInfo.class, context.getApplicationInfo(), "getGlobalSmoothCornerEnabled", new Class[0], new Object[0]);
                sEnableAppSmoothCorner = bool;
                if (bool == null) {
                    sEnableAppSmoothCorner = false;
                }
            } catch (Exception e) {
                sEnableAppSmoothCorner = false;
                Log.d("SmoothCornerHelper", "isEnableAppSmoothCorner fail " + e);
            }
        }
        return sEnableAppSmoothCorner.booleanValue();
    }

    public static void setViewSmoothCornerEnable(View view, boolean z) {
        if (IS_SUPPORT_SMOOTH_CORNER && !isEnableAppSmoothCorner(view.getContext())) {
            setViewSmoothCornerEnableByReflect(view, z);
        }
    }

    public static void setViewSmoothCornerForceEnable(View view, boolean z) {
        if (IS_SUPPORT_SMOOTH_CORNER) {
            setViewSmoothCornerEnableByReflect(view, z);
        }
    }

    private static void setViewSmoothCornerEnableByReflect(View view, boolean z) {
        try {
            ReflectionHelper.invoke(View.class, view, "setSmoothCornerEnabled", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.d("SmoothCornerHelper", "setViewSmoothCornerEnable fail " + e);
        }
    }

    public static void setDrawableSmoothCornerEnable(Drawable drawable, boolean z) {
        if (IS_SUPPORT_SMOOTH_CORNER) {
            Boolean bool = sEnableAppSmoothCorner;
            if (bool == null || !bool.booleanValue()) {
                setDrawableSmoothCornerEnableByReflect(drawable, z);
            }
        }
    }

    public static void setDrawableSmoothCornerForceEnable(Drawable drawable, boolean z) {
        if (IS_SUPPORT_SMOOTH_CORNER) {
            setDrawableSmoothCornerEnableByReflect(drawable, z);
        }
    }

    private static void setDrawableSmoothCornerEnableByReflect(Drawable drawable, boolean z) {
        try {
            ReflectionHelper.invoke(Drawable.class, drawable, "setSmoothCornerEnabled", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        } catch (Exception e) {
            Log.d("SmoothCornerHelper", "setDrawableSmoothCornerEnable fail " + e);
        }
    }
}
