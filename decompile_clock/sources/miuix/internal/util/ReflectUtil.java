package miuix.internal.util;

import android.util.Log;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes2.dex */
public class ReflectUtil {
    private static final String TAG = "ReflectUtil";

    public static Object callObjectMethod(Object obj, String str, Class<?>[] clsArr, Object... objArr) {
        if (obj == null) {
            return null;
        }
        try {
            return obj.getClass().getDeclaredMethod(str, clsArr).invoke(obj, objArr);
        } catch (Exception e) {
            Log.e(TAG, "Failed to call method:" + str, e);
            return null;
        }
    }

    public static <T> T callStaticObjectMethod(Class<?> cls, Class<T> cls2, String str, Class<?>[] clsArr, Object... objArr) {
        if (cls == null) {
            return null;
        }
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return (T) declaredMethod.invoke(null, objArr);
        } catch (Exception e) {
            Log.e(TAG, "Failed to call static method:" + str, e);
            return null;
        }
    }

    public static Class<?> getClass(String str) {
        try {
            return Class.forName(str);
        } catch (Exception e) {
            Log.e(TAG, "Cant find class " + str, e);
            return null;
        }
    }
}
