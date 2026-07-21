package kotlin.reflect.jvm.internal.impl.load.java.lazy.types;

import java.util.ArrayList;
import java.util.List;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.types.ErasureProjectionComputer;
import kotlin.reflect.jvm.internal.impl.types.FlexibleTypesKt;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeKt;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeParameterUpperBoundEraser;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.TypeSubstitution;
import kotlin.reflect.jvm.internal.impl.types.TypeUsage;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorTypeKind;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;
import kotlin.text.Typography;

/* JADX INFO: compiled from: RawSubstitution.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class RawSubstitution extends TypeSubstitution {
    public static final Companion Companion = new Companion(null);
    private static final JavaTypeAttributes lowerTypeAttr = JavaTypeAttributesKt.toAttributes$default(TypeUsage.COMMON, false, true, null, 5, null).withFlexibility(JavaTypeFlexibility.FLEXIBLE_LOWER_BOUND);
    private static final JavaTypeAttributes upperTypeAttr = JavaTypeAttributesKt.toAttributes$default(TypeUsage.COMMON, false, true, null, 5, null).withFlexibility(JavaTypeFlexibility.FLEXIBLE_UPPER_BOUND);
    private final RawProjectionComputer projectionComputer;
    private final TypeParameterUpperBoundEraser typeParameterUpperBoundEraser;

    public RawSubstitution() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public boolean isEmpty() {
        return false;
    }

    public RawSubstitution(TypeParameterUpperBoundEraser typeParameterUpperBoundEraser) {
        RawProjectionComputer rawProjectionComputer = new RawProjectionComputer();
        this.projectionComputer = rawProjectionComputer;
        this.typeParameterUpperBoundEraser = typeParameterUpperBoundEraser == null ? new TypeParameterUpperBoundEraser(rawProjectionComputer, null, 2, null) : typeParameterUpperBoundEraser;
    }

    public /* synthetic */ RawSubstitution(TypeParameterUpperBoundEraser typeParameterUpperBoundEraser, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : typeParameterUpperBoundEraser);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    /* JADX INFO: renamed from: get */
    public TypeProjectionImpl mo1496get(KotlinType key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return new TypeProjectionImpl(eraseType$default(this, key, null, 2, null));
    }

    static /* synthetic */ KotlinType eraseType$default(RawSubstitution rawSubstitution, KotlinType kotlinType, JavaTypeAttributes javaTypeAttributes, int i, Object obj) {
        if ((i & 2) != 0) {
            javaTypeAttributes = new JavaTypeAttributes(TypeUsage.COMMON, null, false, false, null, null, 62, null);
        }
        return rawSubstitution.eraseType(kotlinType, javaTypeAttributes);
    }

    private final KotlinType eraseType(KotlinType kotlinType, JavaTypeAttributes javaTypeAttributes) {
        RawTypeImpl rawTypeImpl;
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = kotlinType.getConstructor().mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor) {
            return eraseType(this.typeParameterUpperBoundEraser.getErasedUpperBound((TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor, javaTypeAttributes.markIsRaw(true)), javaTypeAttributes);
        }
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor) {
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor2 = FlexibleTypesKt.upperIfFlexible(kotlinType).getConstructor().mo1491getDeclarationDescriptor();
            if (!(classifierDescriptorMo1491getDeclarationDescriptor2 instanceof ClassDescriptor)) {
                throw new IllegalStateException(("For some reason declaration for upper bound is not a class but \"" + classifierDescriptorMo1491getDeclarationDescriptor2 + "\" while for lower it's \"" + classifierDescriptorMo1491getDeclarationDescriptor + Typography.quote).toString());
            }
            Pair<SimpleType, Boolean> pairEraseInflexibleBasedOnClassDescriptor = eraseInflexibleBasedOnClassDescriptor(FlexibleTypesKt.lowerIfFlexible(kotlinType), (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor, lowerTypeAttr);
            SimpleType simpleTypeComponent1 = pairEraseInflexibleBasedOnClassDescriptor.component1();
            boolean zBooleanValue = pairEraseInflexibleBasedOnClassDescriptor.component2().booleanValue();
            Pair<SimpleType, Boolean> pairEraseInflexibleBasedOnClassDescriptor2 = eraseInflexibleBasedOnClassDescriptor(FlexibleTypesKt.upperIfFlexible(kotlinType), (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor2, upperTypeAttr);
            SimpleType simpleTypeComponent2 = pairEraseInflexibleBasedOnClassDescriptor2.component1();
            boolean zBooleanValue2 = pairEraseInflexibleBasedOnClassDescriptor2.component2().booleanValue();
            if (zBooleanValue || zBooleanValue2) {
                rawTypeImpl = new RawTypeImpl(simpleTypeComponent1, simpleTypeComponent2);
            } else {
                rawTypeImpl = KotlinTypeFactory.flexibleType(simpleTypeComponent1, simpleTypeComponent2);
            }
            return rawTypeImpl;
        }
        throw new IllegalStateException(("Unexpected declaration kind: " + classifierDescriptorMo1491getDeclarationDescriptor).toString());
    }

    private final Pair<SimpleType, Boolean> eraseInflexibleBasedOnClassDescriptor(final SimpleType simpleType, final ClassDescriptor classDescriptor, final JavaTypeAttributes javaTypeAttributes) {
        if (simpleType.getConstructor().getParameters().isEmpty()) {
            return TuplesKt.to(simpleType, false);
        }
        SimpleType simpleType2 = simpleType;
        if (KotlinBuiltIns.isArray(simpleType2)) {
            TypeProjection typeProjection = simpleType.getArguments().get(0);
            Variance projectionKind = typeProjection.getProjectionKind();
            KotlinType type = typeProjection.getType();
            Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
            return TuplesKt.to(KotlinTypeFactory.simpleType$default(simpleType.getAttributes(), simpleType.getConstructor(), CollectionsKt.listOf(new TypeProjectionImpl(projectionKind, eraseType(type, javaTypeAttributes))), simpleType.isMarkedNullable(), (KotlinTypeRefiner) null, 16, (Object) null), false);
        }
        if (KotlinTypeKt.isError(simpleType2)) {
            return TuplesKt.to(ErrorUtils.createErrorType(ErrorTypeKind.ERROR_RAW_TYPE, simpleType.getConstructor().toString()), false);
        }
        MemberScope memberScope = classDescriptor.getMemberScope(this);
        Intrinsics.checkNotNullExpressionValue(memberScope, "getMemberScope(...)");
        TypeAttributes attributes = simpleType.getAttributes();
        TypeConstructor typeConstructor = classDescriptor.getTypeConstructor();
        Intrinsics.checkNotNullExpressionValue(typeConstructor, "getTypeConstructor(...)");
        List<TypeParameterDescriptor> parameters = classDescriptor.getTypeConstructor().getParameters();
        Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
        List<TypeParameterDescriptor> list = parameters;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (TypeParameterDescriptor typeParameterDescriptor : list) {
            RawProjectionComputer rawProjectionComputer = this.projectionComputer;
            Intrinsics.checkNotNull(typeParameterDescriptor);
            arrayList.add(ErasureProjectionComputer.computeProjection$default(rawProjectionComputer, typeParameterDescriptor, javaTypeAttributes, this.typeParameterUpperBoundEraser, null, 8, null));
        }
        return TuplesKt.to(KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(attributes, typeConstructor, arrayList, simpleType.isMarkedNullable(), memberScope, new Function1(classDescriptor, this, simpleType, javaTypeAttributes) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.types.RawSubstitution$$Lambda$0
            private final ClassDescriptor arg$0;
            private final RawSubstitution arg$1;
            private final SimpleType arg$2;
            private final JavaTypeAttributes arg$3;

            {
                this.arg$0 = classDescriptor;
                this.arg$1 = this;
                this.arg$2 = simpleType;
                this.arg$3 = javaTypeAttributes;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return RawSubstitution.eraseInflexibleBasedOnClassDescriptor$lambda$2(this.arg$0, this.arg$1, this.arg$2, this.arg$3, (KotlinTypeRefiner) obj);
            }
        }), true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SimpleType eraseInflexibleBasedOnClassDescriptor$lambda$2(ClassDescriptor declaration, RawSubstitution this$0, SimpleType type, JavaTypeAttributes attr, KotlinTypeRefiner kotlinTypeRefiner) {
        ClassDescriptor classDescriptorFindClassAcrossModuleDependencies;
        Intrinsics.checkNotNullParameter(declaration, "$declaration");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(type, "$type");
        Intrinsics.checkNotNullParameter(attr, "$attr");
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        ClassId classId = DescriptorUtilsKt.getClassId(declaration);
        if (classId == null || (classDescriptorFindClassAcrossModuleDependencies = kotlinTypeRefiner.findClassAcrossModuleDependencies(classId)) == null || Intrinsics.areEqual(classDescriptorFindClassAcrossModuleDependencies, declaration)) {
            return null;
        }
        return this$0.eraseInflexibleBasedOnClassDescriptor(type, classDescriptorFindClassAcrossModuleDependencies, attr).getFirst();
    }

    /* JADX INFO: compiled from: RawSubstitution.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
