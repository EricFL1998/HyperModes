package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ReflectJavaRecordComponent.kt */
/* JADX INFO: loaded from: classes2.dex */
final class Java16RecordComponentsLoader {
    public static final Java16RecordComponentsLoader INSTANCE = new Java16RecordComponentsLoader();
    private static Cache _cache;

    /* JADX INFO: compiled from: ReflectJavaRecordComponent.kt */
    public static final class Cache {
        private final Method getAccessor;
        private final Method getType;

        public Cache(Method method, Method method2) {
            this.getType = method;
            this.getAccessor = method2;
        }

        public final Method getGetType() {
            return this.getType;
        }

        public final Method getGetAccessor() {
            return this.getAccessor;
        }
    }

    private Java16RecordComponentsLoader() {
    }

    private final Cache buildCache(Object obj) {
        Class<?> cls = obj.getClass();
        try {
            return new Cache(cls.getMethod("getType", new Class[0]), cls.getMethod("getAccessor", new Class[0]));
        } catch (NoSuchMethodException unused) {
            return new Cache(null, null);
        }
    }

    private final Cache initCache(Object obj) {
        Cache cache = _cache;
        if (cache != null) {
            return cache;
        }
        Cache cacheBuildCache = buildCache(obj);
        _cache = cacheBuildCache;
        return cacheBuildCache;
    }

    public final Class<?> loadGetType(Object recordComponent) throws IllegalAccessException, InvocationTargetException {
        Intrinsics.checkNotNullParameter(recordComponent, "recordComponent");
        Method getType = initCache(recordComponent).getGetType();
        if (getType == null) {
            return null;
        }
        Object objInvoke = getType.invoke(recordComponent, new Object[0]);
        Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type java.lang.Class<*>");
        return (Class) objInvoke;
    }

    public final Method loadGetAccessor(Object recordComponent) throws IllegalAccessException, InvocationTargetException {
        Intrinsics.checkNotNullParameter(recordComponent, "recordComponent");
        Method getAccessor = initCache(recordComponent).getGetAccessor();
        if (getAccessor == null) {
            return null;
        }
        Object objInvoke = getAccessor.invoke(recordComponent, new Object[0]);
        Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type java.lang.reflect.Method");
        return (Method) objInvoke;
    }
}
