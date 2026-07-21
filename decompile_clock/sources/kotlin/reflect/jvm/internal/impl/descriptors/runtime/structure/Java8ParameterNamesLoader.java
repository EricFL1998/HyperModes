package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ReflectJavaMember.kt */
/* JADX INFO: loaded from: classes2.dex */
final class Java8ParameterNamesLoader {
    public static final Java8ParameterNamesLoader INSTANCE = new Java8ParameterNamesLoader();
    private static Cache cache;

    /* JADX INFO: compiled from: ReflectJavaMember.kt */
    public static final class Cache {
        private final Method getName;
        private final Method getParameters;

        public Cache(Method method, Method method2) {
            this.getParameters = method;
            this.getName = method2;
        }

        public final Method getGetName() {
            return this.getName;
        }

        public final Method getGetParameters() {
            return this.getParameters;
        }
    }

    private Java8ParameterNamesLoader() {
    }

    public final Cache buildCache(Member member) {
        Intrinsics.checkNotNullParameter(member, "member");
        Class<?> cls = member.getClass();
        try {
            return new Cache(cls.getMethod("getParameters", new Class[0]), ReflectClassUtilKt.getSafeClassLoader(cls).loadClass("java.lang.reflect.Parameter").getMethod("getName", new Class[0]));
        } catch (NoSuchMethodException unused) {
            return new Cache(null, null);
        }
    }

    public final List<String> loadParameterNames(Member member) throws IllegalAccessException, InvocationTargetException {
        Method getName;
        Intrinsics.checkNotNullParameter(member, "member");
        Cache cacheBuildCache = cache;
        if (cacheBuildCache == null) {
            synchronized (this) {
                Java8ParameterNamesLoader java8ParameterNamesLoader = INSTANCE;
                Cache cache2 = cache;
                if (cache2 == null) {
                    cacheBuildCache = java8ParameterNamesLoader.buildCache(member);
                    cache = cacheBuildCache;
                } else {
                    cacheBuildCache = cache2;
                }
            }
        }
        Method getParameters = cacheBuildCache.getGetParameters();
        if (getParameters == null || (getName = cacheBuildCache.getGetName()) == null) {
            return null;
        }
        Object objInvoke = getParameters.invoke(member, new Object[0]);
        Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.Array<*>");
        Object[] objArr = (Object[]) objInvoke;
        ArrayList arrayList = new ArrayList(objArr.length);
        for (Object obj : objArr) {
            Object objInvoke2 = getName.invoke(obj, new Object[0]);
            Intrinsics.checkNotNull(objInvoke2, "null cannot be cast to non-null type kotlin.String");
            arrayList.add((String) objInvoke2);
        }
        return arrayList;
    }
}
