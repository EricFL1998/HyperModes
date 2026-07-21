package miuix.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/* JADX INFO: loaded from: classes3.dex */
public class Reflects {
    private Reflects() {
    }

    public static Method getMethod(String str, String str2, Class<?>... clsArr) {
        return getMethod(forName(str), str2, clsArr);
    }

    public static Method getDeclaredMethod(String str, String str2, Class<?>... clsArr) {
        return getDeclaredMethod(forName(str), str2, clsArr);
    }

    public static Method getMethod(Class<?> cls, String str, Class<?>... clsArr) {
        try {
            Method method = cls.getMethod(str, clsArr);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Method getDeclaredMethod(Class<?> cls, String str, Class<?>... clsArr) {
        try {
            Method declaredMethod = cls.getDeclaredMethod(str, clsArr);
            declaredMethod.setAccessible(true);
            return declaredMethod;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getField(String str, String str2) {
        return getField(forName(str), str2);
    }

    public static Field getDeclaredField(String str, String str2) {
        return getDeclaredField(forName(str), str2);
    }

    public static Field getField(Class<?> cls, String str) {
        try {
            Field field = cls.getField(str);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Field getDeclaredField(Class<?> cls, String str) {
        try {
            Field declaredField = cls.getDeclaredField(str);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object get(Object obj, Field field) {
        try {
            return field.get(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void set(Object obj, Field field, Object obj2) {
        try {
            field.set(obj, obj2);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object invoke(Object obj, Method method, Object... objArr) {
        try {
            return method.invoke(obj, objArr);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static Class<?> forName(String str) {
        try {
            return Class.forName(str);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
