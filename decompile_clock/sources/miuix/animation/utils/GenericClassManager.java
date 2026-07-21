package miuix.animation.utils;

import android.util.ArrayMap;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import miuix.animation.property.FloatProperty;
import miuix.animation.property.IIntValueProperty;

/* JADX INFO: loaded from: classes2.dex */
public class GenericClassManager {
    Map<Object, Class<?>> mClassMap = new ArrayMap();

    public Class<?> get(FloatProperty floatProperty) {
        if (!this.mClassMap.containsKey(floatProperty)) {
            Class<?> genericClass = getGenericClass(floatProperty);
            this.mClassMap.put(floatProperty, genericClass);
            return genericClass;
        }
        return this.mClassMap.get(floatProperty);
    }

    public Class<?> get(IIntValueProperty iIntValueProperty) {
        if (!this.mClassMap.containsKey(iIntValueProperty)) {
            Class<?> genericClass = getGenericClass(iIntValueProperty);
            this.mClassMap.put(iIntValueProperty, genericClass);
            return genericClass;
        }
        return this.mClassMap.get(iIntValueProperty);
    }

    public static Class<?> getGenericClass(FloatProperty floatProperty) {
        Type genericSuperclass = floatProperty.getClass().getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        if (actualTypeArguments.length <= 0) {
            return null;
        }
        Type type = actualTypeArguments[0];
        if (type instanceof Class) {
            return (Class) type;
        }
        return null;
    }

    public static Class<?> getGenericClass(IIntValueProperty iIntValueProperty) {
        Type genericSuperclass = iIntValueProperty.getClass().getGenericSuperclass();
        if (!(genericSuperclass instanceof ParameterizedType)) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        if (actualTypeArguments.length <= 0) {
            return null;
        }
        Type type = actualTypeArguments[0];
        if (type instanceof Class) {
            return (Class) type;
        }
        return null;
    }
}
