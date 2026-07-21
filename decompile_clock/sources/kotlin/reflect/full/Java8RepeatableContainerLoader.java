package kotlin.reflect.full;

import com.xiaomi.onetrack.api.g;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: KAnnotatedElements.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0002\b\u0003\bÂ\u0002\u0018\u00002\u00020\u0001:\u0001\u000fB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u0005H\u0002J \u0010\u000b\u001a\f\u0012\u0006\b\u0001\u0012\u00020\r\u0018\u00010\f2\u000e\u0010\u000e\u001a\n\u0012\u0006\b\u0001\u0012\u00020\r0\fR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\u0010"}, d2 = {"Lkotlin/reflect/full/Java8RepeatableContainerLoader;", "", "<init>", "()V", "cache", "Lkotlin/reflect/full/Java8RepeatableContainerLoader$Cache;", "getCache", "()Lkotlin/reflect/full/Java8RepeatableContainerLoader$Cache;", "setCache", "(Lkotlin/reflect/full/Java8RepeatableContainerLoader$Cache;)V", "buildCache", "loadRepeatableContainer", "Ljava/lang/Class;", "", "klass", "Cache", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
final class Java8RepeatableContainerLoader {
    public static final Java8RepeatableContainerLoader INSTANCE = new Java8RepeatableContainerLoader();
    private static Cache cache;

    private Java8RepeatableContainerLoader() {
    }

    /* JADX INFO: compiled from: KAnnotatedElements.kt */
    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u00002\u00020\u0001B#\u0012\u0010\u0010\u0002\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bR\u001b\u0010\u0002\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\r"}, d2 = {"Lkotlin/reflect/full/Java8RepeatableContainerLoader$Cache;", "", "repeatableClass", "Ljava/lang/Class;", "", "valueMethod", "Ljava/lang/reflect/Method;", "<init>", "(Ljava/lang/Class;Ljava/lang/reflect/Method;)V", "getRepeatableClass", "()Ljava/lang/Class;", "getValueMethod", "()Ljava/lang/reflect/Method;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    public static final class Cache {
        private final Class<? extends Annotation> repeatableClass;
        private final Method valueMethod;

        public Cache(Class<? extends Annotation> cls, Method method) {
            this.repeatableClass = cls;
            this.valueMethod = method;
        }

        public final Class<? extends Annotation> getRepeatableClass() {
            return this.repeatableClass;
        }

        public final Method getValueMethod() {
            return this.valueMethod;
        }
    }

    private final Cache buildCache() {
        try {
            Class<?> cls = Class.forName("java.lang.annotation.Repeatable");
            Intrinsics.checkNotNull(cls, "null cannot be cast to non-null type java.lang.Class<out kotlin.Annotation>");
            return new Cache(cls, cls.getMethod(g.p, new Class[0]));
        } catch (ClassNotFoundException unused) {
            return new Cache(null, null);
        }
    }

    public final Class<? extends Annotation> loadRepeatableContainer(Class<? extends Annotation> klass) throws IllegalAccessException, InvocationTargetException {
        Annotation annotation;
        Method valueMethod;
        Intrinsics.checkNotNullParameter(klass, "klass");
        Cache cacheBuildCache = cache;
        if (cacheBuildCache == null) {
            synchronized (this) {
                Java8RepeatableContainerLoader java8RepeatableContainerLoader = INSTANCE;
                Cache cache2 = cache;
                if (cache2 == null) {
                    cacheBuildCache = java8RepeatableContainerLoader.buildCache();
                    cache = cacheBuildCache;
                } else {
                    cacheBuildCache = cache2;
                }
            }
        }
        Class repeatableClass = cacheBuildCache.getRepeatableClass();
        if (repeatableClass == null || (annotation = klass.getAnnotation(repeatableClass)) == null || (valueMethod = cacheBuildCache.getValueMethod()) == null) {
            return null;
        }
        Object objInvoke = valueMethod.invoke(annotation, new Object[0]);
        Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type java.lang.Class<out kotlin.Annotation>");
        return (Class) objInvoke;
    }
}
