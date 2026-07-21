package kotlin.reflect.jvm.internal.impl.util;

import kotlin.jvm.internal.Intrinsics;
import kotlin.properties.ReadOnlyProperty;
import kotlin.reflect.KProperty;

/* JADX INFO: compiled from: ArrayMapOwner.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class NullableArrayMapAccessor<K, V, T extends V> extends AbstractArrayMapOwner.AbstractArrayMapAccessor<K, V, T> implements ReadOnlyProperty<AbstractArrayMapOwner<K, V>, V> {
    @Override // kotlin.properties.ReadOnlyProperty
    public /* bridge */ /* synthetic */ Object getValue(Object obj, KProperty kProperty) {
        return getValue((AbstractArrayMapOwner) obj, (KProperty<?>) kProperty);
    }

    public NullableArrayMapAccessor(int i) {
        super(i);
    }

    public T getValue(AbstractArrayMapOwner<K, V> thisRef, KProperty<?> property) {
        Intrinsics.checkNotNullParameter(thisRef, "thisRef");
        Intrinsics.checkNotNullParameter(property, "property");
        return extractValue(thisRef);
    }
}
