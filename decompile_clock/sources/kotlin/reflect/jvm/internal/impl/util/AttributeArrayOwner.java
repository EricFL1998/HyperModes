package kotlin.reflect.jvm.internal.impl.util;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AttributeArrayOwner.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class AttributeArrayOwner<K, T> extends AbstractArrayMapOwner<K, T> {
    private ArrayMap<T> arrayMap;

    protected AttributeArrayOwner(ArrayMap<T> arrayMap) {
        Intrinsics.checkNotNullParameter(arrayMap, "arrayMap");
        this.arrayMap = arrayMap;
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.AbstractArrayMapOwner
    protected final ArrayMap<T> getArrayMap() {
        return this.arrayMap;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public AttributeArrayOwner() {
        EmptyArrayMap emptyArrayMap = EmptyArrayMap.INSTANCE;
        Intrinsics.checkNotNull(emptyArrayMap, "null cannot be cast to non-null type org.jetbrains.kotlin.util.ArrayMap<T of org.jetbrains.kotlin.util.AttributeArrayOwner>");
        this(emptyArrayMap);
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Override // kotlin.reflect.jvm.internal.impl.util.AbstractArrayMapOwner
    protected final void registerComponent(String keyQualifiedName, T value) {
        Intrinsics.checkNotNullParameter(keyQualifiedName, "keyQualifiedName");
        Intrinsics.checkNotNullParameter(value, "value");
        int id = getTypeRegistry().getId(keyQualifiedName);
        int size = this.arrayMap.getSize();
        if (size == 0) {
            this.arrayMap = new OneElementArrayMap(value, id);
            return;
        }
        if (size == 1) {
            ArrayMap<T> arrayMap = this.arrayMap;
            Intrinsics.checkNotNull(arrayMap, "null cannot be cast to non-null type org.jetbrains.kotlin.util.OneElementArrayMap<T of org.jetbrains.kotlin.util.AttributeArrayOwner>");
            OneElementArrayMap oneElementArrayMap = (OneElementArrayMap) arrayMap;
            if (oneElementArrayMap.getIndex() == id) {
                this.arrayMap = new OneElementArrayMap(value, id);
                return;
            } else {
                ArrayMapImpl arrayMapImpl = new ArrayMapImpl();
                this.arrayMap = arrayMapImpl;
                arrayMapImpl.set(oneElementArrayMap.getIndex(), oneElementArrayMap.getValue());
            }
        }
        this.arrayMap.set(id, value);
    }
}
