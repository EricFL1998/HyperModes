package kotlin.reflect.jvm.internal.impl.load.java.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ValueParameterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaStaticClassScope;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: util.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class UtilKt {
    public static final List<ValueParameterDescriptor> copyValueParameters(Collection<? extends KotlinType> newValueParameterTypes, Collection<? extends ValueParameterDescriptor> oldValueParameters, CallableDescriptor newOwner) {
        Intrinsics.checkNotNullParameter(newValueParameterTypes, "newValueParameterTypes");
        Intrinsics.checkNotNullParameter(oldValueParameters, "oldValueParameters");
        Intrinsics.checkNotNullParameter(newOwner, "newOwner");
        newValueParameterTypes.size();
        oldValueParameters.size();
        List<Pair> listZip = CollectionsKt.zip(newValueParameterTypes, oldValueParameters);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(listZip, 10));
        for (Pair pair : listZip) {
            KotlinType kotlinType = (KotlinType) pair.component1();
            ValueParameterDescriptor valueParameterDescriptor = (ValueParameterDescriptor) pair.component2();
            int index = valueParameterDescriptor.getIndex();
            Annotations annotations = valueParameterDescriptor.getAnnotations();
            Name name = valueParameterDescriptor.getName();
            Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
            boolean zDeclaresDefaultValue = valueParameterDescriptor.declaresDefaultValue();
            boolean zIsCrossinline = valueParameterDescriptor.isCrossinline();
            boolean zIsNoinline = valueParameterDescriptor.isNoinline();
            KotlinType arrayElementType = valueParameterDescriptor.getVarargElementType() != null ? DescriptorUtilsKt.getModule(newOwner).getBuiltIns().getArrayElementType(kotlinType) : null;
            SourceElement source = valueParameterDescriptor.getSource();
            Intrinsics.checkNotNullExpressionValue(source, "getSource(...)");
            arrayList.add(new ValueParameterDescriptorImpl(newOwner, null, index, annotations, name, kotlinType, zDeclaresDefaultValue, zIsCrossinline, zIsNoinline, arrayElementType, source));
        }
        return arrayList;
    }

    public static final LazyJavaStaticClassScope getParentJavaStaticClassScope(ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(classDescriptor, "<this>");
        ClassDescriptor superClassNotAny = DescriptorUtilsKt.getSuperClassNotAny(classDescriptor);
        if (superClassNotAny == null) {
            return null;
        }
        MemberScope staticScope = superClassNotAny.getStaticScope();
        LazyJavaStaticClassScope lazyJavaStaticClassScope = staticScope instanceof LazyJavaStaticClassScope ? (LazyJavaStaticClassScope) staticScope : null;
        return lazyJavaStaticClassScope == null ? getParentJavaStaticClassScope(superClassNotAny) : lazyJavaStaticClassScope;
    }
}
