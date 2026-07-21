package miuix.view;

import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import miui.util.HapticFeedbackUtil;

/* JADX INFO: loaded from: classes3.dex */
public class PlatformConstants {
    public static final int VERSION;
    public static double romHapticVersion = 1.0d;

    static {
        int iCheckVersion;
        try {
            Class<?> cls = Class.forName("miui.util.HapticFeedbackUtil");
            Class<?> cls2 = Class.forName("miui.view.MiuiHapticFeedbackConstants");
            if (cls.getMethod("isSupportLinearMotorVibrate", Integer.TYPE) != null) {
                Field declaredField = cls2.getDeclaredField("FLAG_MIUI_HAPTIC_VERSION");
                iCheckVersion = declaredField != null ? declaredField.getInt(null) : 0;
            } else {
                iCheckVersion = checkVersion();
            }
        } catch (ClassNotFoundException e) {
            e = e;
            Log.w("HapticCompat", "MIUI Haptic Implementation not found.", e);
            iCheckVersion = -1;
        } catch (IllegalAccessException e2) {
            e = e2;
            Log.w("HapticCompat", "error when getting FLAG_MIUI_HAPTIC_VERSION.", e);
            iCheckVersion = checkVersion();
        } catch (NoClassDefFoundError e3) {
            e = e3;
            Log.w("HapticCompat", "MIUI Haptic Implementation not found.", e);
            iCheckVersion = -1;
        } catch (NoSuchFieldException e4) {
            e = e4;
            Log.w("HapticCompat", "error when getting FLAG_MIUI_HAPTIC_VERSION.", e);
            iCheckVersion = checkVersion();
        } catch (NoSuchMethodException e5) {
            e = e5;
            Log.w("HapticCompat", "MIUI Haptic Implementation not found.", e);
            iCheckVersion = -1;
        }
        VERSION = iCheckVersion;
        Log.i("HapticCompat", String.format("Platform version: %d.", Integer.valueOf(iCheckVersion)));
        try {
            Class<?> cls3 = Class.forName("miui.util.HapticFeedbackUtil");
            romHapticVersion = ((Double) cls3.getDeclaredMethod("getCurVersion", new Class[0]).invoke(cls3, new Object[0])).doubleValue();
        } catch (ClassNotFoundException e6) {
            e = e6;
            Log.w("HapticCompat", "MIUI Haptic Implementation not found.", e);
        } catch (IllegalAccessException unused) {
            Log.w("HapticCompat", "have no access to the definition of getCurVersion()");
        } catch (NoSuchMethodException e7) {
            e = e7;
            Log.w("HapticCompat", "MIUI Haptic Implementation not found.", e);
        } catch (InvocationTargetException unused2) {
            Log.w("HapticCompat", "method getCurVersion() called using Reflection failed");
        }
        Log.i("HapticCompat", "Rom haptic version: " + romHapticVersion);
    }

    static int checkVersion() {
        if (HapticFeedbackUtil.isSupportLinearMotorVibrate(268435471)) {
            return 5;
        }
        if (HapticFeedbackUtil.isSupportLinearMotorVibrate(268435470)) {
            return 4;
        }
        if (HapticFeedbackUtil.isSupportLinearMotorVibrate(268435469)) {
            return 3;
        }
        if (HapticFeedbackUtil.isSupportLinearMotorVibrate(268435468)) {
            return 2;
        }
        return HapticFeedbackUtil.isSupportLinearMotorVibrate(268435465) ? 1 : 0;
    }
}
