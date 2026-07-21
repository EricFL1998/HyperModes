package kotlin.reflect.jvm.internal.impl.builtins;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.Modality;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.EmptyPackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.MutableClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.TypeParameterDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.storage.LockBasedStorageManager;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: suspendFunctionTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class SuspendFunctionTypesKt {
    private static final MutableClassDescriptor FAKE_CONTINUATION_CLASS_DESCRIPTOR;

    static {
        MutableClassDescriptor mutableClassDescriptor = new MutableClassDescriptor(new EmptyPackageFragmentDescriptor(ErrorUtils.INSTANCE.getErrorModule(), StandardNames.COROUTINES_PACKAGE_FQ_NAME), ClassKind.INTERFACE, false, false, StandardNames.CONTINUATION_INTERFACE_FQ_NAME.shortName(), SourceElement.NO_SOURCE, LockBasedStorageManager.NO_LOCKS);
        mutableClassDescriptor.setModality(Modality.ABSTRACT);
        mutableClassDescriptor.setVisibility(DescriptorVisibilities.PUBLIC);
        mutableClassDescriptor.setTypeParameterDescriptors(CollectionsKt.listOf(TypeParameterDescriptorImpl.createWithDefaultBound(mutableClassDescriptor, Annotations.Companion.getEMPTY(), false, Variance.IN_VARIANCE, Name.identifier("T"), 0, LockBasedStorageManager.NO_LOCKS)));
        mutableClassDescriptor.createTypeConstructor();
        FAKE_CONTINUATION_CLASS_DESCRIPTOR = mutableClassDescriptor;
    }

    public static final SimpleType transformSuspendFunctionToRuntimeFunctionType(KotlinType suspendFunType) {
        Intrinsics.checkNotNullParameter(suspendFunType, "suspendFunType");
        FunctionTypesKt.isSuspendFunctionType(suspendFunType);
        KotlinBuiltIns builtIns = TypeUtilsKt.getBuiltIns(suspendFunType);
        Annotations annotations = suspendFunType.getAnnotations();
        KotlinType receiverTypeFromFunctionType = FunctionTypesKt.getReceiverTypeFromFunctionType(suspendFunType);
        List<KotlinType> contextReceiverTypesFromFunctionType = FunctionTypesKt.getContextReceiverTypesFromFunctionType(suspendFunType);
        List<TypeProjection> valueParameterTypesFromFunctionType = FunctionTypesKt.getValueParameterTypesFromFunctionType(suspendFunType);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(valueParameterTypesFromFunctionType, 10));
        Iterator<T> it = valueParameterTypesFromFunctionType.iterator();
        while (it.hasNext()) {
            arrayList.add(((TypeProjection) it.next()).getType());
        }
        TypeAttributes empty = TypeAttributes.Companion.getEmpty();
        TypeConstructor typeConstructor = FAKE_CONTINUATION_CLASS_DESCRIPTOR.getTypeConstructor();
        Intrinsics.checkNotNullExpressionValue(typeConstructor, "getTypeConstructor(...)");
        List listPlus = CollectionsKt.plus((Collection<? extends SimpleType>) arrayList, KotlinTypeFactory.simpleType$default(empty, typeConstructor, CollectionsKt.listOf(TypeUtilsKt.asTypeProjection(FunctionTypesKt.getReturnTypeFromFunctionType(suspendFunType))), false, (KotlinTypeRefiner) null, 16, (Object) null));
        SimpleType nullableAnyType = TypeUtilsKt.getBuiltIns(suspendFunType).getNullableAnyType();
        Intrinsics.checkNotNullExpressionValue(nullableAnyType, "getNullableAnyType(...)");
        return FunctionTypesKt.createFunctionType(builtIns, annotations, receiverTypeFromFunctionType, contextReceiverTypesFromFunctionType, listPlus, null, nullableAnyType, (128 & 128) != 0 ? false : false).makeNullableAsSpecified(suspendFunType.isMarkedNullable());
    }
}
