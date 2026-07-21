package kotlin.reflect.jvm.internal;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: CacheByClass.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\b\u0003\u0018\u0000*\u0004\b\u0000\u0010\u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u001f\u0012\u0016\u0010\u0003\u001a\u0012\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u0005\u0012\u0004\u0012\u00028\u00000\u0004¢\u0006\u0004\b\u0006\u0010\u0007J\u0019\u0010\n\u001a\u00028\u00002\n\u0010\u000b\u001a\u0006\u0012\u0002\b\u00030\u0005H\u0016¢\u0006\u0002\u0010\fJ\b\u0010\r\u001a\u00020\u000eH\u0016R\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00028\u00000\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lkotlin/reflect/jvm/internal/ClassValueCache;", "V", "Lkotlin/reflect/jvm/internal/CacheByClass;", "compute", "Lkotlin/Function1;", "Ljava/lang/Class;", "<init>", "(Lkotlin/jvm/functions/Function1;)V", "classValue", "Lkotlin/reflect/jvm/internal/ComputableClassValue;", "get", "key", "(Ljava/lang/Class;)Ljava/lang/Object;", "clear", "", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
final class ClassValueCache<V> extends CacheByClass<V> {
    private volatile ComputableClassValue<V> classValue;

    public ClassValueCache(Function1<? super Class<?>, ? extends V> compute) {
        Intrinsics.checkNotNullParameter(compute, "compute");
        this.classValue = new ComputableClassValue<>(compute);
    }

    @Override // kotlin.reflect.jvm.internal.CacheByClass
    public V get(Class<?> key) {
        Intrinsics.checkNotNullParameter(key, "key");
        ComputableClassValue<V> computableClassValue = this.classValue;
        V v = computableClassValue.get(key).get();
        if (v != null) {
            return v;
        }
        computableClassValue.remove(key);
        V v2 = computableClassValue.get(key).get();
        return v2 != null ? v2 : computableClassValue.compute.invoke(key);
    }

    @Override // kotlin.reflect.jvm.internal.CacheByClass
    public void clear() {
        this.classValue = this.classValue.createNewCopy();
    }
}
