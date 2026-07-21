package kotlin.reflect.jvm.internal.impl.builtins.jvm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructorSubstitution;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: mappingUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class MappingUtilKt {
    public static final TypeConstructorSubstitution createMappedTypeParametersSubstitution(ClassDescriptor from, ClassDescriptor to) {
        Intrinsics.checkNotNullParameter(from, "from");
        Intrinsics.checkNotNullParameter(to, "to");
        from.getDeclaredTypeParameters().size();
        to.getDeclaredTypeParameters().size();
        TypeConstructorSubstitution.Companion companion = TypeConstructorSubstitution.Companion;
        List<TypeParameterDescriptor> declaredTypeParameters = from.getDeclaredTypeParameters();
        Intrinsics.checkNotNullExpressionValue(declaredTypeParameters, "getDeclaredTypeParameters(...)");
        List<TypeParameterDescriptor> list = declaredTypeParameters;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(((TypeParameterDescriptor) it.next()).getTypeConstructor());
        }
        ArrayList arrayList2 = arrayList;
        List<TypeParameterDescriptor> declaredTypeParameters2 = to.getDeclaredTypeParameters();
        Intrinsics.checkNotNullExpressionValue(declaredTypeParameters2, "getDeclaredTypeParameters(...)");
        List<TypeParameterDescriptor> list2 = declaredTypeParameters2;
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
        Iterator<T> it2 = list2.iterator();
        while (it2.hasNext()) {
            SimpleType defaultType = ((TypeParameterDescriptor) it2.next()).getDefaultType();
            Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
            arrayList3.add(TypeUtilsKt.asTypeProjection(defaultType));
        }
        return TypeConstructorSubstitution.Companion.createByConstructorsMap$default(companion, MapsKt.toMap(CollectionsKt.zip(arrayList2, arrayList3)), false, 2, null);
    }
}
