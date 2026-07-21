package kotlin.reflect.jvm.internal.impl.util;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorUtilKt;
import kotlin.reflect.jvm.internal.impl.descriptors.FindClassInModuleKt;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeAliasDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ValueParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRenderer;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ImplicitClassReceiver;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ReceiverValue;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: modifierChecks.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class OperatorChecks extends AbstractModifierChecks {
    public static final OperatorChecks INSTANCE = new OperatorChecks();
    private static final List<Checks> checks = CollectionsKt.listOf((Object[]) new Checks[]{new Checks(OperatorNameConventions.GET, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, new ValueParameterCountCheck.AtLeast(1)}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.SET, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, new ValueParameterCountCheck.AtLeast(2)}, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.util.OperatorChecks$$Lambda$0
        @Override // kotlin.jvm.functions.Function1
        public Object invoke(Object obj) {
            return OperatorChecks.checks$lambda$2((FunctionDescriptor) obj);
        }
    }), new Checks(OperatorNameConventions.GET_VALUE, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE, new ValueParameterCountCheck.AtLeast(2), IsKPropertyCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.SET_VALUE, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE, new ValueParameterCountCheck.AtLeast(3), IsKPropertyCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.PROVIDE_DELEGATE, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE, new ValueParameterCountCheck.Equals(2), IsKPropertyCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.INVOKE, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.CONTAINS, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.SingleValueParameter.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE, ReturnsCheck.ReturnsBoolean.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.ITERATOR, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.NoValueParameters.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.NEXT, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.NoValueParameters.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.HAS_NEXT, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.NoValueParameters.INSTANCE, ReturnsCheck.ReturnsBoolean.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.RANGE_TO, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.SingleValueParameter.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.RANGE_UNTIL, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.SingleValueParameter.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.EQUALS, new Check[]{MemberKindCheck.Member.INSTANCE}, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.util.OperatorChecks$$Lambda$1
        @Override // kotlin.jvm.functions.Function1
        public Object invoke(Object obj) {
            return OperatorChecks.checks$lambda$6((FunctionDescriptor) obj);
        }
    }), new Checks(OperatorNameConventions.COMPARE_TO, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ReturnsCheck.ReturnsInt.INSTANCE, ValueParameterCountCheck.SingleValueParameter.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.BINARY_OPERATION_NAMES, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.SingleValueParameter.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.SIMPLE_UNARY_OPERATION_NAMES, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.NoValueParameters.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(CollectionsKt.listOf((Object[]) new Name[]{OperatorNameConventions.INC, OperatorNameConventions.DEC}), new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE}, new Function1() { // from class: kotlin.reflect.jvm.internal.impl.util.OperatorChecks$$Lambda$2
        @Override // kotlin.jvm.functions.Function1
        public Object invoke(Object obj) {
            return OperatorChecks.checks$lambda$8((FunctionDescriptor) obj);
        }
    }), new Checks(OperatorNameConventions.ASSIGNMENT_OPERATIONS, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ReturnsCheck.ReturnsUnit.INSTANCE, ValueParameterCountCheck.SingleValueParameter.INSTANCE, NoDefaultAndVarargsCheck.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null), new Checks(OperatorNameConventions.COMPONENT_REGEX, new Check[]{MemberKindCheck.MemberOrExtension.INSTANCE, ValueParameterCountCheck.NoValueParameters.INSTANCE}, (Function1) null, 4, (DefaultConstructorMarker) null)});

    private OperatorChecks() {
    }

    @Override // kotlin.reflect.jvm.internal.impl.util.AbstractModifierChecks
    public List<Checks> getChecks$descriptors() {
        return checks;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String checks$lambda$2(FunctionDescriptor Checks) {
        Intrinsics.checkNotNullParameter(Checks, "$this$Checks");
        List<ValueParameterDescriptor> valueParameters = Checks.getValueParameters();
        Intrinsics.checkNotNullExpressionValue(valueParameters, "getValueParameters(...)");
        ValueParameterDescriptor valueParameterDescriptor = (ValueParameterDescriptor) CollectionsKt.lastOrNull((List) valueParameters);
        boolean z = false;
        if (valueParameterDescriptor != null && !DescriptorUtilsKt.declaresOrInheritsDefaultValue(valueParameterDescriptor) && valueParameterDescriptor.getVarargElementType() == null) {
            z = true;
        }
        OperatorChecks operatorChecks = INSTANCE;
        if (z) {
            return null;
        }
        return "last parameter should not have a default value or be a vararg";
    }

    private static final boolean checks$lambda$6$isAny(DeclarationDescriptor declarationDescriptor) {
        return (declarationDescriptor instanceof ClassDescriptor) && KotlinBuiltIns.isAny((ClassDescriptor) declarationDescriptor);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:16:0x0052  */
    /* JADX WARN: Code duplicated, block: B:17:0x0054  */
    public static final String checks$lambda$6(FunctionDescriptor Checks) {
        boolean z;
        Intrinsics.checkNotNullParameter(Checks, "$this$Checks");
        OperatorChecks operatorChecks = INSTANCE;
        DeclarationDescriptor containingDeclaration = Checks.getContainingDeclaration();
        Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
        if (checks$lambda$6$isAny(containingDeclaration)) {
            z = true;
        } else {
            Collection<? extends FunctionDescriptor> overriddenDescriptors = Checks.getOverriddenDescriptors();
            Intrinsics.checkNotNullExpressionValue(overriddenDescriptors, "getOverriddenDescriptors(...)");
            Collection<? extends FunctionDescriptor> collection = overriddenDescriptors;
            if (!collection.isEmpty()) {
                Iterator<T> it = collection.iterator();
                while (true) {
                    if (it.hasNext()) {
                        DeclarationDescriptor containingDeclaration2 = ((FunctionDescriptor) it.next()).getContainingDeclaration();
                        Intrinsics.checkNotNullExpressionValue(containingDeclaration2, "getContainingDeclaration(...)");
                        if (checks$lambda$6$isAny(containingDeclaration2)) {
                        }
                    } else if (DescriptorUtilKt.isTypedEqualsInValueClass(Checks)) {
                        z = false;
                    }
                    z = true;
                }
            } else if (DescriptorUtilKt.isTypedEqualsInValueClass(Checks)) {
                z = false;
            } else {
                z = true;
            }
        }
        if (z) {
            return null;
        }
        StringBuilder sb = new StringBuilder("must override ''equals()'' in Any");
        DeclarationDescriptor containingDeclaration3 = Checks.getContainingDeclaration();
        Intrinsics.checkNotNullExpressionValue(containingDeclaration3, "getContainingDeclaration(...)");
        if (InlineClassesUtilsKt.isValueClass(containingDeclaration3)) {
            DescriptorRenderer descriptorRenderer = DescriptorRenderer.SHORT_NAMES_IN_TYPES;
            DeclarationDescriptor containingDeclaration4 = Checks.getContainingDeclaration();
            Intrinsics.checkNotNull(containingDeclaration4, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.ClassDescriptor");
            SimpleType defaultType = ((ClassDescriptor) containingDeclaration4).getDefaultType();
            Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
            sb.append(" or define ''equals(other: " + descriptorRenderer.renderType(TypeUtilsKt.replaceArgumentsWithStarProjections(defaultType)) + "): Boolean''");
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final String checks$lambda$8(FunctionDescriptor Checks) {
        boolean zIsSubtypeOf;
        Intrinsics.checkNotNullParameter(Checks, "$this$Checks");
        ReceiverParameterDescriptor dispatchReceiverParameter = Checks.getDispatchReceiverParameter();
        if (dispatchReceiverParameter == null) {
            dispatchReceiverParameter = Checks.getExtensionReceiverParameter();
        }
        OperatorChecks operatorChecks = INSTANCE;
        boolean z = false;
        if (dispatchReceiverParameter != null) {
            KotlinType returnType = Checks.getReturnType();
            if (returnType != null) {
                KotlinType type = dispatchReceiverParameter.getType();
                Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
                zIsSubtypeOf = TypeUtilsKt.isSubtypeOf(returnType, type);
            } else {
                zIsSubtypeOf = false;
            }
            if (zIsSubtypeOf || operatorChecks.incDecCheckForExpectClass(Checks, dispatchReceiverParameter)) {
                z = true;
            }
        }
        if (z) {
            return null;
        }
        return "receiver must be a supertype of the return type";
    }

    private final boolean incDecCheckForExpectClass(FunctionDescriptor functionDescriptor, ReceiverParameterDescriptor receiverParameterDescriptor) {
        ClassId classId;
        KotlinType returnType;
        ReceiverValue value = receiverParameterDescriptor.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        if (!(value instanceof ImplicitClassReceiver)) {
            return false;
        }
        ClassDescriptor classDescriptor = ((ImplicitClassReceiver) value).getClassDescriptor();
        if (!classDescriptor.isExpect() || (classId = DescriptorUtilsKt.getClassId(classDescriptor)) == null) {
            return false;
        }
        ClassifierDescriptor classifierDescriptorFindClassifierAcrossModuleDependencies = FindClassInModuleKt.findClassifierAcrossModuleDependencies(DescriptorUtilsKt.getModule(classDescriptor), classId);
        TypeAliasDescriptor typeAliasDescriptor = classifierDescriptorFindClassifierAcrossModuleDependencies instanceof TypeAliasDescriptor ? (TypeAliasDescriptor) classifierDescriptorFindClassifierAcrossModuleDependencies : null;
        if (typeAliasDescriptor == null || (returnType = functionDescriptor.getReturnType()) == null) {
            return false;
        }
        return TypeUtilsKt.isSubtypeOf(returnType, typeAliasDescriptor.getExpandedType());
    }
}
