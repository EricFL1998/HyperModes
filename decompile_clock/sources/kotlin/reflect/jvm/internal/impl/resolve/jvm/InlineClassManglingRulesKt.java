package kotlin.reflect.jvm.internal.impl.resolve.jvm;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: inlineClassManglingRules.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class InlineClassManglingRulesKt {
    public static final boolean shouldHideConstructorDueToValueClassTypeValueParameters(CallableMemberDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        ClassConstructorDescriptor classConstructorDescriptor = descriptor instanceof ClassConstructorDescriptor ? (ClassConstructorDescriptor) descriptor : null;
        if (classConstructorDescriptor == null || DescriptorVisibilities.isPrivate(classConstructorDescriptor.getVisibility())) {
            return false;
        }
        ClassDescriptor constructedClass = classConstructorDescriptor.getConstructedClass();
        Intrinsics.checkNotNullExpressionValue(constructedClass, "getConstructedClass(...)");
        if (InlineClassesUtilsKt.isValueClass(constructedClass) || DescriptorUtils.isSealedClass(classConstructorDescriptor.getConstructedClass())) {
            return false;
        }
        List<ValueParameterDescriptor> valueParameters = classConstructorDescriptor.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        List<ValueParameterDescriptor> list = valueParameters;
        if ((list instanceof Collection) && list.isEmpty()) {
            return false;
        }
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            KotlinType type = ((ValueParameterDescriptor) it.next()).getType();
            Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
            if (requiresFunctionNameManglingInParameterTypes(type)) {
                return true;
            }
        }
        return false;
    }

    public static final boolean isValueClassThatRequiresMangling(DeclarationDescriptor declarationDescriptor) {
        Intrinsics.checkNotNullParameter(declarationDescriptor, "<this>");
        return InlineClassesUtilsKt.isValueClass(declarationDescriptor) && !isDontMangleClass((ClassDescriptor) declarationDescriptor);
    }

    public static final boolean isValueClassThatRequiresMangling(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "<this>");
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor == null) {
            return false;
        }
        ClassifierDescriptor classifierDescriptor = classifierDescriptorMo1491getDeclarationDescriptor;
        return (InlineClassesUtilsKt.isInlineClass(classifierDescriptor) && isValueClassThatRequiresMangling(classifierDescriptor)) || InlineClassesUtilsKt.needsMfvcFlattening(kotlinType);
    }

    private static final boolean requiresFunctionNameManglingInParameterTypes(KotlinType kotlinType) {
        return isValueClassThatRequiresMangling(kotlinType) || isTypeParameterWithUpperBoundThatRequiresMangling(kotlinType, true);
    }

    private static final boolean isDontMangleClass(ClassDescriptor classDescriptor) {
        return Intrinsics.areEqual(DescriptorUtilsKt.getFqNameSafe(classDescriptor), StandardNames.RESULT_FQ_NAME);
    }

    private static final boolean isTypeParameterWithUpperBoundThatRequiresMangling(KotlinType kotlinType, boolean z) {
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
        TypeParameterDescriptor typeParameterDescriptor = classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor ? (TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor : null;
        if (typeParameterDescriptor == null) {
            return false;
        }
        return (z || !InlineClassesUtilsKt.isMultiFieldValueClass(typeParameterDescriptor)) && requiresFunctionNameManglingInParameterTypes(TypeUtilsKt.getRepresentativeUpperBound(typeParameterDescriptor));
    }
}
