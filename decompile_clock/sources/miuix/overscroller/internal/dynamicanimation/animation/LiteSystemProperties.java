package miuix.overscroller.internal.dynamicanimation.animation;

import android.util.Log;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes3.dex */
class LiteSystemProperties {
    private static final String TAG = "LiteSystemProperties";
    private static Class pClassSystemProperties;
    private static Method pGet;
    private static Method pGetInt;

    LiteSystemProperties() {
    }

    static {
        try {
            pClassSystemProperties = Class.forName("android.os.SystemProperties");
        } catch (Exception unused) {
            pClassSystemProperties = null;
        }
        Class cls = pClassSystemProperties;
        if (cls != null) {
            try {
                Method method = cls.getMethod("get", String.class, String.class);
                pGet = method;
                method.setAccessible(true);
            } catch (Exception unused2) {
            }
            try {
                Method method2 = pClassSystemProperties.getMethod("getInt", String.class, Integer.TYPE);
                pGetInt = method2;
                method2.setAccessible(true);
            } catch (Exception unused3) {
            }
        }
    }

    public static String get(String str, String str2) {
        Method method = pGet;
        if (method != null) {
            try {
                return (String) method.invoke(null, str, str2);
            } catch (Exception e) {
                Log.e(TAG, "key: " + str + " detail:" + e);
            }
        }
        return str2;
    }

    public static String get(String str) {
        return get(str, "");
    }

    public static int getInt(String str, int i) {
        Method method = pGetInt;
        if (method != null) {
            try {
                return ((Integer) method.invoke(null, str, Integer.valueOf(i))).intValue();
            } catch (Exception e) {
                Log.e(TAG, "key: " + str + " detail:" + e);
            }
        }
        return i;
    }
}
