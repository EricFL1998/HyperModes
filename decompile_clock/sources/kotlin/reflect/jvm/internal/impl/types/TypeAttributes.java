package kotlin.reflect.jvm.internal.impl.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;
import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.impl.util.ArrayMap;
import kotlin.reflect.jvm.internal.impl.util.AttributeArrayOwner;
import kotlin.reflect.jvm.internal.impl.util.TypeRegistry;

/* JADX INFO: compiled from: TypeAttributes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeAttributes extends AttributeArrayOwner<TypeAttribute<?>, TypeAttribute<?>> implements Iterable<TypeAttribute<?>>, KMappedMarker {
    public static final Companion Companion = new Companion(null);
    private static final TypeAttributes Empty = new TypeAttributes((List<? extends TypeAttribute<?>>) CollectionsKt.emptyList());

    public /* synthetic */ TypeAttributes(List list, DefaultConstructorMarker defaultConstructorMarker) {
        this((List<? extends TypeAttribute<?>>) list);
    }

    private TypeAttributes(List<? extends TypeAttribute<?>> list) {
        for (TypeAttribute<?> typeAttribute : list) {
            registerComponent((KClass) typeAttribute.getKey(), typeAttribute);
        }
    }

    /* JADX INFO: compiled from: TypeAttributes.kt */
    public static final class Companion extends TypeRegistry<TypeAttribute<?>, TypeAttribute<?>> {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.util.TypeRegistry
        public int customComputeIfAbsent(ConcurrentHashMap<String, Integer> concurrentHashMap, String key, Function1<? super String, Integer> compute) {
            int iIntValue;
            Intrinsics.checkNotNullParameter(concurrentHashMap, "<this>");
            Intrinsics.checkNotNullParameter(key, "key");
            Intrinsics.checkNotNullParameter(compute, "compute");
            Integer num = concurrentHashMap.get(key);
            if (num != null) {
                return num.intValue();
            }
            synchronized (concurrentHashMap) {
                Integer num2 = concurrentHashMap.get(key);
                if (num2 != null) {
                    iIntValue = num2.intValue();
                } else {
                    Integer numInvoke = compute.invoke(key);
                    concurrentHashMap.putIfAbsent(key, Integer.valueOf(numInvoke.intValue()));
                    iIntValue = numInvoke.intValue();
                }
            }
            return iIntValue;
        }

        public final TypeAttributes getEmpty() {
            return TypeAttributes.Empty;
        }

        public final TypeAttributes create(List<? extends TypeAttribute<?>> attributes) {
            Intrinsics.checkNotNullParameter(attributes, "attributes");
            if (attributes.isEmpty()) {
                return getEmpty();
            }
            return new TypeAttributes(attributes, null);
        }
    }

    private TypeAttributes(TypeAttribute<?> typeAttribute) {
        this((List<? extends TypeAttribute<?>>) CollectionsKt.listOf(typeAttribute));
    }

    public final boolean contains(TypeAttribute<?> attribute) {
        Intrinsics.checkNotNullParameter(attribute, "attribute");
        return getArrayMap().get(Companion.getId((KClass) attribute.getKey())) != null;
    }

    public final TypeAttributes plus(TypeAttribute<?> attribute) {
        Intrinsics.checkNotNullParameter(attribute, "attribute");
        if (contains(attribute)) {
            return this;
        }
        if (isEmpty()) {
            return new TypeAttributes(attribute);
        }
        return Companion.create(CollectionsKt.plus((Collection<? extends TypeAttribute<?>>) CollectionsKt.toList(this), attribute));
    }

    public final TypeAttributes remove(TypeAttribute<?> attribute) {
        Intrinsics.checkNotNullParameter(attribute, "attribute");
        if (isEmpty()) {
            return this;
        }
        ArrayMap<TypeAttribute<?>> arrayMap = getArrayMap();
        ArrayList arrayList = new ArrayList();
        for (TypeAttribute<?> typeAttribute : arrayMap) {
            if (!Intrinsics.areEqual(typeAttribute, attribute)) {
                arrayList.add(typeAttribute);
            }
        }
        ArrayList arrayList2 = arrayList;
        return arrayList2.size() == getArrayMap().getSize() ? this : Companion.create(arrayList2);
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.AbstractArrayMapOwner
    protected TypeRegistry<TypeAttribute<?>, TypeAttribute<?>> getTypeRegistry() {
        return Companion;
    }

    public final TypeAttributes intersect(TypeAttributes other) {
        TypeAttribute typeAttributeIntersect;
        Intrinsics.checkNotNullParameter(other, "other");
        if (isEmpty() && other.isEmpty()) {
            return this;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = Companion.getIndices().iterator();
        while (it.hasNext()) {
            int iIntValue = ((Number) it.next()).intValue();
            TypeAttribute<?> typeAttribute = getArrayMap().get(iIntValue);
            TypeAttribute<?> typeAttribute2 = other.getArrayMap().get(iIntValue);
            if (typeAttribute == null) {
                typeAttributeIntersect = typeAttribute2 != null ? typeAttribute2.intersect(typeAttribute) : null;
            } else {
                typeAttributeIntersect = typeAttribute.intersect(typeAttribute2);
            }
            kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(arrayList, typeAttributeIntersect);
        }
        return Companion.create(arrayList);
    }

    public final TypeAttributes add(TypeAttributes other) {
        TypeAttribute typeAttributeAdd;
        Intrinsics.checkNotNullParameter(other, "other");
        if (isEmpty() && other.isEmpty()) {
            return this;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = Companion.getIndices().iterator();
        while (it.hasNext()) {
            int iIntValue = ((Number) it.next()).intValue();
            TypeAttribute<?> typeAttribute = getArrayMap().get(iIntValue);
            TypeAttribute<?> typeAttribute2 = other.getArrayMap().get(iIntValue);
            if (typeAttribute == null) {
                typeAttributeAdd = typeAttribute2 != null ? typeAttribute2.add(typeAttribute) : null;
            } else {
                typeAttributeAdd = typeAttribute.add(typeAttribute2);
            }
            kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(arrayList, typeAttributeAdd);
        }
        return Companion.create(arrayList);
    }
}
