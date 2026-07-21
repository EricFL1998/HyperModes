package kotlin.reflect.jvm.internal.impl.types.checker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.DynamicTypesKt;
import kotlin.reflect.jvm.internal.impl.types.FlexibleType;
import kotlin.reflect.jvm.internal.impl.types.FlexibleTypesKt;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeKt;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.UnwrappedType;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorTypeKind;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;

/* JADX INFO: compiled from: IntersectionType.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class IntersectionTypeKt {
    public static final UnwrappedType intersectTypes(List<? extends UnwrappedType> types) {
        SimpleType lowerBound;
        Intrinsics.checkNotNullParameter(types, "types");
        int size = types.size();
        if (size == 0) {
            throw new IllegalStateException("Expected some types".toString());
        }
        if (size == 1) {
            return (UnwrappedType) CollectionsKt.single((List) types);
        }
        List<? extends UnwrappedType> list = types;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        boolean z = false;
        boolean z2 = false;
        for (UnwrappedType unwrappedType : list) {
            z = z || KotlinTypeKt.isError(unwrappedType);
            if (unwrappedType instanceof SimpleType) {
                lowerBound = (SimpleType) unwrappedType;
            } else {
                if (!(unwrappedType instanceof FlexibleType)) {
                    throw new NoWhenBranchMatchedException();
                }
                if (DynamicTypesKt.isDynamic(unwrappedType)) {
                    return unwrappedType;
                }
                lowerBound = ((FlexibleType) unwrappedType).getLowerBound();
                z2 = true;
            }
            arrayList.add(lowerBound);
        }
        ArrayList arrayList2 = arrayList;
        if (z) {
            return ErrorUtils.createErrorType(ErrorTypeKind.INTERSECTION_OF_ERROR_TYPES, types.toString());
        }
        if (!z2) {
            return TypeIntersector.INSTANCE.intersectTypes$descriptors(arrayList2);
        }
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList3.add(FlexibleTypesKt.upperIfFlexible((UnwrappedType) it.next()));
        }
        return KotlinTypeFactory.flexibleType(TypeIntersector.INSTANCE.intersectTypes$descriptors(arrayList2), TypeIntersector.INSTANCE.intersectTypes$descriptors(arrayList3));
    }
}
