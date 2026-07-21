package kotlin.reflect.jvm.internal;

import kotlin.Metadata;

/* JADX INFO: compiled from: CacheByClass.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\b \u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0019\u0010\u0005\u001a\u00028\u00002\n\u0010\u0006\u001a\u0006\u0012\u0002\b\u00030\u0007H&¢\u0006\u0002\u0010\bJ\b\u0010\t\u001a\u00020\nH&¨\u0006\u000b"}, d2 = {"Lkotlin/reflect/jvm/internal/CacheByClass;", "V", "", "<init>", "()V", "get", "key", "Ljava/lang/Class;", "(Ljava/lang/Class;)Ljava/lang/Object;", "clear", "", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public abstract class CacheByClass<V> {
    public abstract void clear();

    public abstract V get(Class<?> key);
}
