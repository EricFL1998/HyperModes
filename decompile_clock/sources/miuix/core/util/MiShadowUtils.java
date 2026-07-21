package miuix.core.util;

import android.os.Build;
import android.util.Log;
import android.view.View;
import miuix.reflect.ReflectionHelper;

/* JADX INFO: loaded from: classes2.dex */
public class MiShadowUtils {
    public static final boolean SUPPORT_MI_SHADOW;
    private static final String TAG = "MiShadowHelper";

    private MiShadowUtils() {
    }

    static {
        boolean z = Boolean.parseBoolean(SystemProperties.get("persist.sys.mi_shadow_supported", "false"));
        SUPPORT_MI_SHADOW = z;
        if (z) {
            return;
        }
        Log.d(TAG, "This device does not support mi shadow!");
    }

    public static void setShadowConfig(View view, MaterialConfig.ShadowConfig shadowConfig) {
        if (view != null && SUPPORT_MI_SHADOW) {
            if (shadowConfig != null) {
                setMiShadow(view, shadowConfig.shadowColor, shadowConfig.shadowOffsetX, shadowConfig.shadowOffsetY, shadowConfig.shadowRadius, shadowConfig.shadowDispersion);
            } else {
                clearMiShadow(view);
            }
        }
    }

    public static void setMiShadow(View view, int i, float f) {
        setMiShadow(view, i, 0.0f, 0.0f, f);
    }

    public static void setMiShadow(View view, int i, float f, float f2, float f3) {
        setMiShadow(view, i, f, f2, f3, 1.0f);
    }

    public static void setMiShadow(View view, int i, float f, float f2, float f3, float f4) {
        if (SUPPORT_MI_SHADOW) {
            try {
                ReflectionHelper.invoke(View.class, view, "setMiShadow", new Class[]{Integer.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE}, Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4));
            } catch (Exception e) {
                Log.e(TAG, "Failed to call setMiShadow", e);
            }
        }
    }

    public static void setMiShadow(View view, int i, float f, float f2, float f3, float f4, boolean z) {
        if (SUPPORT_MI_SHADOW) {
            if (Build.VERSION.SDK_INT <= 34) {
                setMiShadow(view, i, f, f2, f3, f4);
                return;
            }
            try {
                ReflectionHelper.invoke(View.class, view, "setMiShadow", new Class[]{Integer.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Float.TYPE, Boolean.TYPE}, Integer.valueOf(i), Float.valueOf(f), Float.valueOf(f2), Float.valueOf(f3), Float.valueOf(f4), Boolean.valueOf(z));
            } catch (Exception e) {
                Log.e(TAG, "Failed to call setMiShadow", e);
            }
        }
    }

    public static void clearMiShadow(View view) {
        setMiShadow(view, 0, 0.0f, 0.0f, 0.0f);
    }
}
