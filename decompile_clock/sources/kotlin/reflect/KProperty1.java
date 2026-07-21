package kotlin.reflect;

import kotlin.Metadata;
import kotlin.jvm.functions.Function1;

/* JADX INFO: compiled from: KProperty.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\bf\u0018\u0000*\u0004\b\u0000\u0010\u0001*\u0006\b\u0001\u0010\u0002 \u00012\b\u0012\u0004\u0012\u0002H\u00020\u00032\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0004:\u0001\u0010J\u0015\u0010\u0005\u001a\u00028\u00012\u0006\u0010\u0006\u001a\u00028\u0000H&¢\u0006\u0002\u0010\u0007J\u0017\u0010\b\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0006\u001a\u00028\u0000H'¢\u0006\u0002\u0010\u0007R$\u0010\n\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\u000bX¦\u0004¢\u0006\f\u0012\u0004\b\f\u0010\r\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0011"}, d2 = {"Lkotlin/reflect/KProperty1;", "T", "V", "Lkotlin/reflect/KProperty;", "Lkotlin/Function1;", "get", "receiver", "(Ljava/lang/Object;)Ljava/lang/Object;", "getDelegate", "", "getter", "Lkotlin/reflect/KProperty1$Getter;", "getGetter$annotations", "()V", "getGetter", "()Lkotlin/reflect/KProperty1$Getter;", "Getter", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public interface KProperty1<T, V> extends KProperty<V>, Function1<T, V> {

    /* JADX INFO: compiled from: KProperty.kt */
    @Metadata(k = 3, mv = {2, 1, 0}, xi = 48)
    public static final class DefaultImpls {
        public static /* synthetic */ void getGetter$annotations() {
        }
    }

    /* JADX INFO: compiled from: KProperty.kt */
    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bf\u0018\u0000*\u0004\b\u0002\u0010\u0001*\u0006\b\u0003\u0010\u0002 \u00012\b\u0012\u0004\u0012\u0002H\u00020\u00032\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00020\u0004¨\u0006\u0005"}, d2 = {"Lkotlin/reflect/KProperty1$Getter;", "T", "V", "Lkotlin/reflect/KProperty$Getter;", "Lkotlin/Function1;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public interface Getter<T, V> extends KProperty.Getter<V>, Function1<T, V> {
    }

    V get(T receiver);

    Object getDelegate(T receiver);

    @Override // kotlin.reflect.KProperty
    Getter<T, V> getGetter();
}
