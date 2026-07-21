package kotlin.reflect.jvm.internal.impl.load.java;

import java.util.List;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaClassDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaMethodDescriptor;
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType;
import kotlin.reflect.jvm.internal.impl.load.kotlin.MethodSignatureMappingKt;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.ExternalOverridabilityCondition;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: JavaIncompatibilityRulesOverridabilityCondition.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaIncompatibilityRulesOverridabilityCondition implements ExternalOverridabilityCondition {
    public static final Companion Companion = new Companion(null);

    @Override // kotlin.reflect.jvm.internal.impl.resolve.ExternalOverridabilityCondition
    public ExternalOverridabilityCondition.Result isOverridable(CallableDescriptor superDescriptor, CallableDescriptor subDescriptor, ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(superDescriptor, "superDescriptor");
        Intrinsics.checkNotNullParameter(subDescriptor, "subDescriptor");
        if (isIncompatibleInAccordanceWithBuiltInOverridabilityRules(superDescriptor, subDescriptor, classDescriptor)) {
            return ExternalOverridabilityCondition.Result.INCOMPATIBLE;
        }
        if (Companion.doesJavaOverrideHaveIncompatibleValueParameterKinds(superDescriptor, subDescriptor)) {
            return ExternalOverridabilityCondition.Result.INCOMPATIBLE;
        }
        return ExternalOverridabilityCondition.Result.UNKNOWN;
    }

    private final boolean isIncompatibleInAccordanceWithBuiltInOverridabilityRules(CallableDescriptor callableDescriptor, CallableDescriptor callableDescriptor2, ClassDescriptor classDescriptor) {
        if ((callableDescriptor instanceof CallableMemberDescriptor) && (callableDescriptor2 instanceof FunctionDescriptor) && !KotlinBuiltIns.isBuiltIn(callableDescriptor2)) {
            BuiltinMethodsWithSpecialGenericSignature builtinMethodsWithSpecialGenericSignature = BuiltinMethodsWithSpecialGenericSignature.INSTANCE;
            FunctionDescriptor functionDescriptor = (FunctionDescriptor) callableDescriptor2;
            Name name = functionDescriptor.getName();
            Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
            if (!builtinMethodsWithSpecialGenericSignature.getSameAsBuiltinMethodWithErasedValueParameters(name)) {
                SpecialGenericSignatures.Companion companion = SpecialGenericSignatures.Companion;
                Name name2 = functionDescriptor.getName();
                Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
                if (!companion.getSameAsRenamedInJvmBuiltin(name2)) {
                    return false;
                }
            }
            CallableMemberDescriptor overriddenSpecialBuiltin = SpecialBuiltinMembers.getOverriddenSpecialBuiltin((CallableMemberDescriptor) callableDescriptor);
            boolean z = callableDescriptor instanceof FunctionDescriptor;
            FunctionDescriptor functionDescriptor2 = z ? (FunctionDescriptor) callableDescriptor : null;
            if ((!(functionDescriptor2 != null && functionDescriptor.isHiddenToOvercomeSignatureClash() == functionDescriptor2.isHiddenToOvercomeSignatureClash())) && (overriddenSpecialBuiltin == null || !functionDescriptor.isHiddenToOvercomeSignatureClash())) {
                return true;
            }
            if ((classDescriptor instanceof JavaClassDescriptor) && functionDescriptor.getInitialSignatureDescriptor() == null && overriddenSpecialBuiltin != null && !SpecialBuiltinMembers.hasRealKotlinSuperClassWithOverrideOf(classDescriptor, overriddenSpecialBuiltin)) {
                if ((overriddenSpecialBuiltin instanceof FunctionDescriptor) && z && BuiltinMethodsWithSpecialGenericSignature.getOverriddenBuiltinFunctionWithErasedValueParametersInJava((FunctionDescriptor) overriddenSpecialBuiltin) != null) {
                    String strComputeJvmDescriptor$default = MethodSignatureMappingKt.computeJvmDescriptor$default(functionDescriptor, false, false, 2, null);
                    FunctionDescriptor original = ((FunctionDescriptor) callableDescriptor).getOriginal();
                    Intrinsics.checkNotNullExpressionValue(original, "getOriginal(...)");
                    if (Intrinsics.areEqual(strComputeJvmDescriptor$default, MethodSignatureMappingKt.computeJvmDescriptor$default(original, false, false, 2, null))) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.ExternalOverridabilityCondition
    public ExternalOverridabilityCondition.Contract getContract() {
        return ExternalOverridabilityCondition.Contract.CONFLICTS_ONLY;
    }

    /* JADX INFO: compiled from: JavaIncompatibilityRulesOverridabilityCondition.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean doesJavaOverrideHaveIncompatibleValueParameterKinds(CallableDescriptor superDescriptor, CallableDescriptor subDescriptor) {
            Intrinsics.checkNotNullParameter(superDescriptor, "superDescriptor");
            Intrinsics.checkNotNullParameter(subDescriptor, "subDescriptor");
            if ((subDescriptor instanceof JavaMethodDescriptor) && (superDescriptor instanceof FunctionDescriptor)) {
                JavaMethodDescriptor javaMethodDescriptor = (JavaMethodDescriptor) subDescriptor;
                javaMethodDescriptor.getValueParameters().size();
                FunctionDescriptor functionDescriptor = (FunctionDescriptor) superDescriptor;
                functionDescriptor.getValueParameters().size();
                List<ValueParameterDescriptor> valueParameters = javaMethodDescriptor.getOriginal().getValueParameters();
                Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
                List<ValueParameterDescriptor> valueParameters2 = functionDescriptor.getOriginal().getValueParameters();
                Intrinsics.checkNotNullExpressionValue(valueParameters2, "getValueParameters(...)");
                for (Pair pair : CollectionsKt.zip(valueParameters, valueParameters2)) {
                    ValueParameterDescriptor valueParameterDescriptor = (ValueParameterDescriptor) pair.component1();
                    ValueParameterDescriptor valueParameterDescriptor2 = (ValueParameterDescriptor) pair.component2();
                    Intrinsics.checkNotNull(valueParameterDescriptor);
                    boolean z = mapValueParameterType((FunctionDescriptor) subDescriptor, valueParameterDescriptor) instanceof JvmType.Primitive;
                    Intrinsics.checkNotNull(valueParameterDescriptor2);
                    if (z != (mapValueParameterType(functionDescriptor, valueParameterDescriptor2) instanceof JvmType.Primitive)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private final JvmType mapValueParameterType(FunctionDescriptor functionDescriptor, ValueParameterDescriptor valueParameterDescriptor) {
            if (MethodSignatureMappingKt.forceSingleValueParameterBoxing(functionDescriptor) || isPrimitiveCompareTo(functionDescriptor)) {
                KotlinType type = valueParameterDescriptor.getType();
                Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
                return MethodSignatureMappingKt.mapToJvmType(TypeUtilsKt.makeNullable(type));
            }
            KotlinType type2 = valueParameterDescriptor.getType();
            Intrinsics.checkNotNullExpressionValue(type2, "getType(...)");
            return MethodSignatureMappingKt.mapToJvmType(type2);
        }

        private final boolean isPrimitiveCompareTo(FunctionDescriptor functionDescriptor) {
            if (functionDescriptor.getValueParameters().size() != 1) {
                return false;
            }
            DeclarationDescriptor containingDeclaration = functionDescriptor.getContainingDeclaration();
            ClassDescriptor classDescriptor = containingDeclaration instanceof ClassDescriptor ? (ClassDescriptor) containingDeclaration : null;
            if (classDescriptor == null) {
                return false;
            }
            List<ValueParameterDescriptor> valueParameters = functionDescriptor.getValueParameters();
            Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = ((ValueParameterDescriptor) CollectionsKt.single((List) valueParameters)).getType().getConstructor().mo1491getDeclarationDescriptor();
            ClassDescriptor classDescriptor2 = classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor ? (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor : null;
            return classDescriptor2 != null && KotlinBuiltIns.isPrimitiveClass(classDescriptor) && Intrinsics.areEqual(DescriptorUtilsKt.getFqNameSafe(classDescriptor), DescriptorUtilsKt.getFqNameSafe(classDescriptor2));
        }
    }
}
