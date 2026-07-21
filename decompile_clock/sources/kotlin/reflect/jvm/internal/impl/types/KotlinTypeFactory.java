package kotlin.reflect.jvm.internal.impl.types;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeAliasDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleAwareClassDescriptorKt;
import kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerLiteralTypeConstructor;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorScopeKind;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;

/* JADX INFO: compiled from: KotlinTypeFactory.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class KotlinTypeFactory {
    public static final KotlinTypeFactory INSTANCE = new KotlinTypeFactory();
    private static final Function1<KotlinTypeRefiner, SimpleType> EMPTY_REFINED_TYPE_FACTORY = new Function1() { // from class: kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory$EMPTY_REFINED_TYPE_FACTORY$1
        @Override // kotlin.jvm.functions.Function1
        public final Void invoke(KotlinTypeRefiner kotlinTypeRefiner) {
            Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "<unused var>");
            return null;
        }
    };

    @JvmStatic
    public static final SimpleType simpleType(TypeAttributes attributes, TypeConstructor constructor, List<? extends TypeProjection> arguments, boolean z) {
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        return simpleType$default(attributes, constructor, arguments, z, (KotlinTypeRefiner) null, 16, (Object) null);
    }

    private KotlinTypeFactory() {
    }

    private final MemberScope computeMemberScope(TypeConstructor typeConstructor, List<? extends TypeProjection> list, KotlinTypeRefiner kotlinTypeRefiner) {
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = typeConstructor.mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor) {
            return ((TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor).getDefaultType().getMemberScope();
        }
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor) {
            if (kotlinTypeRefiner == null) {
                kotlinTypeRefiner = DescriptorUtilsKt.getKotlinTypeRefiner(DescriptorUtilsKt.getModule(classifierDescriptorMo1491getDeclarationDescriptor));
            }
            if (list.isEmpty()) {
                return ModuleAwareClassDescriptorKt.getRefinedUnsubstitutedMemberScopeIfPossible((ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor, kotlinTypeRefiner);
            }
            return ModuleAwareClassDescriptorKt.getRefinedMemberScopeIfPossible((ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor, TypeConstructorSubstitution.Companion.create(typeConstructor, list), kotlinTypeRefiner);
        }
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeAliasDescriptor) {
            return ErrorUtils.createErrorScope(ErrorScopeKind.SCOPE_FOR_ABBREVIATION_TYPE, true, ((TypeAliasDescriptor) classifierDescriptorMo1491getDeclarationDescriptor).getName().toString());
        }
        if (typeConstructor instanceof IntersectionTypeConstructor) {
            return ((IntersectionTypeConstructor) typeConstructor).createScopeForKotlinType();
        }
        throw new IllegalStateException("Unsupported classifier: " + classifierDescriptorMo1491getDeclarationDescriptor + " for constructor: " + typeConstructor);
    }

    public static /* synthetic */ SimpleType simpleType$default(TypeAttributes typeAttributes, TypeConstructor typeConstructor, List list, boolean z, KotlinTypeRefiner kotlinTypeRefiner, int i, Object obj) {
        if ((i & 16) != 0) {
            kotlinTypeRefiner = null;
        }
        return simpleType(typeAttributes, typeConstructor, (List<? extends TypeProjection>) list, z, kotlinTypeRefiner);
    }

    @JvmStatic
    public static final SimpleType simpleType(final TypeAttributes attributes, final TypeConstructor constructor, final List<? extends TypeProjection> arguments, final boolean z, KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        if (attributes.isEmpty() && arguments.isEmpty() && !z && constructor.mo1491getDeclarationDescriptor() != null) {
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = constructor.mo1491getDeclarationDescriptor();
            Intrinsics.checkNotNull(classifierDescriptorMo1491getDeclarationDescriptor);
            SimpleType defaultType = classifierDescriptorMo1491getDeclarationDescriptor.getDefaultType();
            Intrinsics.checkNotNullExpressionValue(defaultType, "getDefaultType(...)");
            return defaultType;
        }
        return simpleTypeWithNonTrivialMemberScope(attributes, constructor, arguments, z, INSTANCE.computeMemberScope(constructor, arguments, kotlinTypeRefiner), new Function1(constructor, arguments, attributes, z) { // from class: kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory$$Lambda$0
            private final TypeConstructor arg$0;
            private final List arg$1;
            private final TypeAttributes arg$2;
            private final boolean arg$3;

            {
                this.arg$0 = constructor;
                this.arg$1 = arguments;
                this.arg$2 = attributes;
                this.arg$3 = z;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return KotlinTypeFactory.simpleType$lambda$1(this.arg$0, this.arg$1, this.arg$2, this.arg$3, (KotlinTypeRefiner) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SimpleType simpleType$lambda$1(TypeConstructor constructor, List arguments, TypeAttributes attributes, boolean z, KotlinTypeRefiner refiner) {
        Intrinsics.checkNotNullParameter(constructor, "$constructor");
        Intrinsics.checkNotNullParameter(arguments, "$arguments");
        Intrinsics.checkNotNullParameter(attributes, "$attributes");
        Intrinsics.checkNotNullParameter(refiner, "refiner");
        ExpandedTypeOrRefinedConstructor expandedTypeOrRefinedConstructorRefineConstructor = INSTANCE.refineConstructor(constructor, refiner, arguments);
        if (expandedTypeOrRefinedConstructorRefineConstructor == null) {
            return null;
        }
        SimpleType expandedType = expandedTypeOrRefinedConstructorRefineConstructor.getExpandedType();
        if (expandedType != null) {
            return expandedType;
        }
        TypeConstructor refinedConstructor = expandedTypeOrRefinedConstructorRefineConstructor.getRefinedConstructor();
        Intrinsics.checkNotNull(refinedConstructor);
        return simpleType(attributes, refinedConstructor, (List<? extends TypeProjection>) arguments, z, refiner);
    }

    @JvmStatic
    public static final SimpleType computeExpandedType(TypeAliasDescriptor typeAliasDescriptor, List<? extends TypeProjection> arguments) {
        Intrinsics.checkNotNullParameter(typeAliasDescriptor, "<this>");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        return new TypeAliasExpander(TypeAliasExpansionReportStrategy.DO_NOTHING.INSTANCE, false).expand(TypeAliasExpansion.Companion.create(null, typeAliasDescriptor, arguments), TypeAttributes.Companion.getEmpty());
    }

    private final ExpandedTypeOrRefinedConstructor refineConstructor(TypeConstructor typeConstructor, KotlinTypeRefiner kotlinTypeRefiner, List<? extends TypeProjection> list) {
        ClassifierDescriptor classifierDescriptorRefineDescriptor;
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = typeConstructor.mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor == null || (classifierDescriptorRefineDescriptor = kotlinTypeRefiner.refineDescriptor(classifierDescriptorMo1491getDeclarationDescriptor)) == null) {
            return null;
        }
        if (classifierDescriptorRefineDescriptor instanceof TypeAliasDescriptor) {
            return new ExpandedTypeOrRefinedConstructor(computeExpandedType((TypeAliasDescriptor) classifierDescriptorRefineDescriptor, list), null);
        }
        TypeConstructor typeConstructorRefine = classifierDescriptorRefineDescriptor.getTypeConstructor().refine(kotlinTypeRefiner);
        Intrinsics.checkNotNullExpressionValue(typeConstructorRefine, "refine(...)");
        return new ExpandedTypeOrRefinedConstructor(null, typeConstructorRefine);
    }

    /* JADX INFO: compiled from: KotlinTypeFactory.kt */
    private static final class ExpandedTypeOrRefinedConstructor {
        private final SimpleType expandedType;
        private final TypeConstructor refinedConstructor;

        public ExpandedTypeOrRefinedConstructor(SimpleType simpleType, TypeConstructor typeConstructor) {
            this.expandedType = simpleType;
            this.refinedConstructor = typeConstructor;
        }

        public final SimpleType getExpandedType() {
            return this.expandedType;
        }

        public final TypeConstructor getRefinedConstructor() {
            return this.refinedConstructor;
        }
    }

    @JvmStatic
    public static final SimpleType simpleTypeWithNonTrivialMemberScope(final TypeAttributes attributes, final TypeConstructor constructor, final List<? extends TypeProjection> arguments, final boolean z, final MemberScope memberScope) {
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        Intrinsics.checkNotNullParameter(memberScope, "memberScope");
        SimpleTypeImpl simpleTypeImpl = new SimpleTypeImpl(constructor, arguments, z, memberScope, new Function1(constructor, arguments, attributes, z, memberScope) { // from class: kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory$$Lambda$1
            private final TypeConstructor arg$0;
            private final List arg$1;
            private final TypeAttributes arg$2;
            private final boolean arg$3;
            private final MemberScope arg$4;

            {
                this.arg$0 = constructor;
                this.arg$1 = arguments;
                this.arg$2 = attributes;
                this.arg$3 = z;
                this.arg$4 = memberScope;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope$lambda$4(this.arg$0, this.arg$1, this.arg$2, this.arg$3, this.arg$4, (KotlinTypeRefiner) obj);
            }
        });
        if (attributes.isEmpty()) {
            return simpleTypeImpl;
        }
        return new SimpleTypeWithAttributes(simpleTypeImpl, attributes);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SimpleType simpleTypeWithNonTrivialMemberScope$lambda$4(TypeConstructor constructor, List arguments, TypeAttributes attributes, boolean z, MemberScope memberScope, KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(constructor, "$constructor");
        Intrinsics.checkNotNullParameter(arguments, "$arguments");
        Intrinsics.checkNotNullParameter(attributes, "$attributes");
        Intrinsics.checkNotNullParameter(memberScope, "$memberScope");
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        ExpandedTypeOrRefinedConstructor expandedTypeOrRefinedConstructorRefineConstructor = INSTANCE.refineConstructor(constructor, kotlinTypeRefiner, arguments);
        if (expandedTypeOrRefinedConstructorRefineConstructor == null) {
            return null;
        }
        SimpleType expandedType = expandedTypeOrRefinedConstructorRefineConstructor.getExpandedType();
        if (expandedType != null) {
            return expandedType;
        }
        TypeConstructor refinedConstructor = expandedTypeOrRefinedConstructorRefineConstructor.getRefinedConstructor();
        Intrinsics.checkNotNull(refinedConstructor);
        return simpleTypeWithNonTrivialMemberScope(attributes, refinedConstructor, arguments, z, memberScope);
    }

    @JvmStatic
    public static final SimpleType simpleTypeWithNonTrivialMemberScope(TypeAttributes attributes, TypeConstructor constructor, List<? extends TypeProjection> arguments, boolean z, MemberScope memberScope, Function1<? super KotlinTypeRefiner, ? extends SimpleType> refinedTypeFactory) {
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        Intrinsics.checkNotNullParameter(memberScope, "memberScope");
        Intrinsics.checkNotNullParameter(refinedTypeFactory, "refinedTypeFactory");
        SimpleTypeImpl simpleTypeImpl = new SimpleTypeImpl(constructor, arguments, z, memberScope, refinedTypeFactory);
        if (attributes.isEmpty()) {
            return simpleTypeImpl;
        }
        return new SimpleTypeWithAttributes(simpleTypeImpl, attributes);
    }

    @JvmStatic
    public static final SimpleType simpleNotNullType(TypeAttributes attributes, ClassDescriptor descriptor, List<? extends TypeProjection> arguments) {
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        TypeConstructor typeConstructor = descriptor.getTypeConstructor();
        Intrinsics.checkNotNullExpressionValue(typeConstructor, "getTypeConstructor(...)");
        return simpleType$default(attributes, typeConstructor, (List) arguments, false, (KotlinTypeRefiner) null, 16, (Object) null);
    }

    public static /* synthetic */ SimpleType simpleType$default(SimpleType simpleType, TypeAttributes typeAttributes, TypeConstructor typeConstructor, List list, boolean z, int i, Object obj) {
        if ((i & 2) != 0) {
            typeAttributes = simpleType.getAttributes();
        }
        if ((i & 4) != 0) {
            typeConstructor = simpleType.getConstructor();
        }
        if ((i & 8) != 0) {
            list = simpleType.getArguments();
        }
        if ((i & 16) != 0) {
            z = simpleType.isMarkedNullable();
        }
        return simpleType(simpleType, typeAttributes, typeConstructor, (List<? extends TypeProjection>) list, z);
    }

    @JvmStatic
    public static final SimpleType simpleType(SimpleType baseType, TypeAttributes annotations, TypeConstructor constructor, List<? extends TypeProjection> arguments, boolean z) {
        Intrinsics.checkNotNullParameter(baseType, "baseType");
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        return simpleType$default(annotations, constructor, arguments, z, (KotlinTypeRefiner) null, 16, (Object) null);
    }

    @JvmStatic
    public static final UnwrappedType flexibleType(SimpleType lowerBound, SimpleType upperBound) {
        Intrinsics.checkNotNullParameter(lowerBound, "lowerBound");
        Intrinsics.checkNotNullParameter(upperBound, "upperBound");
        return Intrinsics.areEqual(lowerBound, upperBound) ? lowerBound : new FlexibleTypeImpl(lowerBound, upperBound);
    }

    @JvmStatic
    public static final SimpleType integerLiteralType(TypeAttributes attributes, IntegerLiteralTypeConstructor constructor, boolean z) {
        Intrinsics.checkNotNullParameter(attributes, "attributes");
        Intrinsics.checkNotNullParameter(constructor, "constructor");
        return simpleTypeWithNonTrivialMemberScope(attributes, constructor, CollectionsKt.emptyList(), z, ErrorUtils.createErrorScope(ErrorScopeKind.INTEGER_LITERAL_TYPE_SCOPE, true, "unknown integer literal type"));
    }
}
