package miuix.animation.utils;

import android.util.ArrayMap;
import android.util.Log;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class FieldManager {
    static final String GET = "get";
    static final String SET = "set";
    Map<String, MethodInfo> mMethodMap = new ArrayMap();
    Map<String, FieldInfo> mFieldMap = new ArrayMap();

    static class MethodInfo {
        Method method;

        MethodInfo() {
        }
    }

    static class FieldInfo {
        Field field;

        FieldInfo() {
        }
    }

    public synchronized <T> T getField(Object obj, String str, Class<T> cls) {
        if (obj != null && str != null) {
            if (str.length() != 0) {
                MethodInfo method = this.mMethodMap.get(str);
                if (method == null) {
                    method = getMethod(obj, getMethodName(str, GET), this.mMethodMap, new Class[0]);
                }
                if (method.method != null) {
                    return (T) retToClz(invokeMethod(obj, method.method, new Object[0]), cls);
                }
                FieldInfo field = this.mFieldMap.get(str);
                if (field == null) {
                    field = getField(obj, str, cls, this.mFieldMap);
                }
                if (field.field == null) {
                    return null;
                }
                return (T) getValueByField(obj, field.field);
            }
        }
        return null;
    }

    public synchronized <T> boolean setField(Object obj, String str, Class<T> cls, T t) {
        if (obj != null && str != null) {
            if (str.length() != 0) {
                MethodInfo method = this.mMethodMap.get(str);
                if (method == null) {
                    method = getMethod(obj, getMethodName(str, SET), this.mMethodMap, cls);
                }
                if (method.method != null) {
                    invokeMethod(obj, method.method, t);
                    return true;
                }
                FieldInfo field = this.mFieldMap.get(str);
                if (field == null) {
                    field = getField(obj, str, cls, this.mFieldMap);
                }
                if (field.field == null) {
                    return false;
                }
                setValueByField(obj, field.field, t);
                return true;
            }
        }
        return false;
    }

    static <T> T retToClz(Object obj, Class<T> cls) {
        if (!(obj instanceof Number)) {
            return null;
        }
        Number number = (Number) obj;
        if (cls == Float.class || cls == Float.TYPE) {
            return (T) Float.valueOf(number.floatValue());
        }
        if (cls == Integer.class || cls == Integer.TYPE) {
            return (T) Integer.valueOf(number.intValue());
        }
        throw new IllegalArgumentException("getPropertyValue, clz must be float or int instead of " + cls);
    }

    static String getMethodName(String str, String str2) {
        return str2 + Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    static <T> T getValueByField(Object obj, Field field) {
        try {
            return (T) field.get(obj);
        } catch (Exception unused) {
            return null;
        }
    }

    static <T> void setValueByField(Object obj, Field field, T t) {
        try {
            field.set(obj, t);
        } catch (Exception unused) {
        }
    }

    static MethodInfo getMethod(Object obj, String str, Map<String, MethodInfo> map, Class<?>... clsArr) {
        MethodInfo methodInfo = map.get(str);
        if (methodInfo != null) {
            return methodInfo;
        }
        MethodInfo methodInfo2 = new MethodInfo();
        methodInfo2.method = getMethod(obj, str, clsArr);
        map.put(str, methodInfo2);
        return methodInfo2;
    }

    static Method getMethod(Object obj, String str, Class<?>... clsArr) {
        try {
            try {
                Method declaredMethod = obj.getClass().getDeclaredMethod(str, clsArr);
                declaredMethod.setAccessible(true);
                return declaredMethod;
            } catch (NoSuchMethodException unused) {
                return null;
            }
        } catch (NoSuchMethodException unused2) {
            return obj.getClass().getMethod(str, clsArr);
        }
    }

    static FieldInfo getField(Object obj, String str, Class<?> cls, Map<String, FieldInfo> map) {
        FieldInfo fieldInfo = map.get(str);
        if (fieldInfo != null) {
            return fieldInfo;
        }
        FieldInfo fieldInfo2 = new FieldInfo();
        fieldInfo2.field = getFieldByType(obj, str, cls);
        map.put(str, fieldInfo2);
        return fieldInfo2;
    }

    static Field getFieldByType(Object obj, String str, Class<?> cls) {
        Field field;
        try {
            field = obj.getClass().getDeclaredField(str);
            try {
                field.setAccessible(true);
            } catch (NoSuchFieldException unused) {
                try {
                    field = obj.getClass().getField(str);
                } catch (NoSuchFieldException unused2) {
                }
            }
        } catch (NoSuchFieldException unused3) {
            field = null;
        }
        if (field == null || field.getType() == cls) {
            return field;
        }
        return null;
    }

    static <T> T invokeMethod(Object obj, Method method, Object... objArr) {
        if (method == null) {
            return null;
        }
        try {
            return (T) method.invoke(obj, objArr);
        } catch (Exception e) {
            Log.d(CommonUtils.TAG, "ValueProperty.invokeMethod failed, " + method.getName(), e);
            return null;
        }
    }
}
