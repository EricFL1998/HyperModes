package kotlin.reflect.jvm.internal.impl.load.kotlin;

import kotlin.Unit;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.FunctionTypesKt;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.builtins.SuspendFunctionTypesKt;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyGetterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeAliasDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.name.SpecialNames;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.types.ExpandedTypeUtilsKt;
import kotlin.reflect.jvm.internal.impl.types.IntersectionTypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.TypeUtils;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.SimpleClassicTypeSystemContext;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;
import kotlin.reflect.jvm.internal.impl.utils.FunctionsKt;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: descriptorBasedTypeSignatureMapping.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DescriptorBasedTypeSignatureMappingKt {
    public static /* synthetic */ Object mapType$default(KotlinType kotlinType, JvmTypeFactory jvmTypeFactory, TypeMappingMode typeMappingMode, TypeMappingConfiguration typeMappingConfiguration, JvmDescriptorTypeWriter jvmDescriptorTypeWriter, Function3 function3, int i, Object obj) {
        if ((i & 32) != 0) {
            function3 = FunctionsKt.getDO_NOTHING_3();
        }
        return mapType(kotlinType, jvmTypeFactory, typeMappingMode, typeMappingConfiguration, jvmDescriptorTypeWriter, function3);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v10, types: [T, java.lang.Object] */
    public static final <T> T mapType(KotlinType kotlinType, JvmTypeFactory<T> factory, TypeMappingMode mode, TypeMappingConfiguration<? extends T> typeMappingConfiguration, JvmDescriptorTypeWriter<T> jvmDescriptorTypeWriter, Function3<? super KotlinType, ? super T, ? super TypeMappingMode, Unit> writeGenericType) {
        T t;
        KotlinType kotlinType2;
        Object objMapType;
        Intrinsics.checkNotNullParameter(kotlinType, "kotlinType");
        Intrinsics.checkNotNullParameter(factory, "factory");
        Intrinsics.checkNotNullParameter(mode, "mode");
        Intrinsics.checkNotNullParameter(typeMappingConfiguration, "typeMappingConfiguration");
        Intrinsics.checkNotNullParameter(writeGenericType, "writeGenericType");
        KotlinType kotlinTypePreprocessType = typeMappingConfiguration.preprocessType(kotlinType);
        if (kotlinTypePreprocessType != null) {
            return (T) mapType(kotlinTypePreprocessType, factory, mode, typeMappingConfiguration, jvmDescriptorTypeWriter, writeGenericType);
        }
        if (FunctionTypesKt.isSuspendFunctionType(kotlinType)) {
            return (T) mapType(SuspendFunctionTypesKt.transformSuspendFunctionToRuntimeFunctionType(kotlinType), factory, mode, typeMappingConfiguration, jvmDescriptorTypeWriter, writeGenericType);
        }
        KotlinType kotlinType3 = kotlinType;
        Object objMapBuiltInType = TypeSignatureMappingKt.mapBuiltInType(SimpleClassicTypeSystemContext.INSTANCE, kotlinType3, factory, mode);
        if (objMapBuiltInType != null) {
            ?? r9 = (Object) TypeSignatureMappingKt.boxTypeIfNeeded(factory, objMapBuiltInType, mode.getNeedPrimitiveBoxing());
            writeGenericType.invoke(kotlinType, r9, mode);
            return r9;
        }
        TypeConstructor constructor = kotlinType.getConstructor();
        if (constructor instanceof IntersectionTypeConstructor) {
            IntersectionTypeConstructor intersectionTypeConstructor = (IntersectionTypeConstructor) constructor;
            KotlinType alternativeType = intersectionTypeConstructor.getAlternativeType();
            if (alternativeType == null) {
                alternativeType = typeMappingConfiguration.commonSupertype(intersectionTypeConstructor.mo1492getSupertypes());
            }
            return (T) mapType(TypeUtilsKt.replaceArgumentsWithStarProjections(alternativeType), factory, mode, typeMappingConfiguration, jvmDescriptorTypeWriter, writeGenericType);
        }
        ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = constructor.mo1491getDeclarationDescriptor();
        if (classifierDescriptorMo1491getDeclarationDescriptor == null) {
            throw new UnsupportedOperationException("no descriptor for type constructor of " + kotlinType);
        }
        ClassifierDescriptor classifierDescriptor = classifierDescriptorMo1491getDeclarationDescriptor;
        if (ErrorUtils.isError(classifierDescriptor)) {
            T t2 = (T) factory.createObjectType("error/NonExistentClass");
            typeMappingConfiguration.processErrorType(kotlinType, (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor);
            if (jvmDescriptorTypeWriter != 0) {
                jvmDescriptorTypeWriter.writeClass(t2);
            }
            return t2;
        }
        boolean z = classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor;
        if (z && KotlinBuiltIns.isArray(kotlinType)) {
            if (kotlinType.getArguments().size() != 1) {
                throw new UnsupportedOperationException("arrays must have one type argument");
            }
            TypeProjection typeProjection = kotlinType.getArguments().get(0);
            KotlinType type = typeProjection.getType();
            Intrinsics.checkNotNullExpressionValue(type, "getType(...)");
            if (typeProjection.getProjectionKind() == Variance.IN_VARIANCE) {
                objMapType = factory.createObjectType("java/lang/Object");
                if (jvmDescriptorTypeWriter != 0) {
                    jvmDescriptorTypeWriter.writeArrayType();
                    jvmDescriptorTypeWriter.writeClass(objMapType);
                    jvmDescriptorTypeWriter.writeArrayEnd();
                }
            } else {
                if (jvmDescriptorTypeWriter != 0) {
                    jvmDescriptorTypeWriter.writeArrayType();
                }
                Variance projectionKind = typeProjection.getProjectionKind();
                Intrinsics.checkNotNullExpressionValue(projectionKind, "getProjectionKind(...)");
                objMapType = mapType(type, factory, mode.toGenericArgumentMode(projectionKind, true), typeMappingConfiguration, jvmDescriptorTypeWriter, writeGenericType);
                if (jvmDescriptorTypeWriter != 0) {
                    jvmDescriptorTypeWriter.writeArrayEnd();
                }
            }
            return (T) factory.createFromString("[" + factory.toString(objMapType));
        }
        if (z) {
            if (InlineClassesUtilsKt.isInlineClass(classifierDescriptor) && !mode.getNeedInlineClassWrapping() && (kotlinType2 = (KotlinType) ExpandedTypeUtilsKt.computeExpandedTypeForInlineClass(SimpleClassicTypeSystemContext.INSTANCE, kotlinType3)) != null) {
                return (T) mapType(kotlinType2, factory, mode.wrapInlineClassesMode(), typeMappingConfiguration, jvmDescriptorTypeWriter, writeGenericType);
            }
            if (mode.isForAnnotationParameter() && KotlinBuiltIns.isKClass((ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor)) {
                t = (Object) factory.getJavaLangClassType();
            } else {
                ClassDescriptor classDescriptor = (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor;
                ClassDescriptor original = classDescriptor.getOriginal();
                Intrinsics.checkNotNullExpressionValue(original, "getOriginal(...)");
                T predefinedTypeForClass = typeMappingConfiguration.getPredefinedTypeForClass(original);
                if (predefinedTypeForClass == null) {
                    if (classDescriptor.getKind() == ClassKind.ENUM_ENTRY) {
                        DeclarationDescriptor containingDeclaration = classDescriptor.getContainingDeclaration();
                        Intrinsics.checkNotNull(containingDeclaration, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.ClassDescriptor");
                        classDescriptor = (ClassDescriptor) containingDeclaration;
                    }
                    ClassDescriptor original2 = classDescriptor.getOriginal();
                    Intrinsics.checkNotNullExpressionValue(original2, "getOriginal(...)");
                    t = (Object) factory.createObjectType(computeInternalName(original2, typeMappingConfiguration));
                } else {
                    t = (Object) predefinedTypeForClass;
                }
            }
            writeGenericType.invoke(kotlinType, t, mode);
            return t;
        }
        if (classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeParameterDescriptor) {
            KotlinType representativeUpperBound = TypeUtilsKt.getRepresentativeUpperBound((TypeParameterDescriptor) classifierDescriptorMo1491getDeclarationDescriptor);
            if (kotlinType.isMarkedNullable()) {
                representativeUpperBound = TypeUtilsKt.makeNullable(representativeUpperBound);
            }
            T t3 = (T) mapType(representativeUpperBound, factory, mode, typeMappingConfiguration, null, FunctionsKt.getDO_NOTHING_3());
            if (jvmDescriptorTypeWriter != 0) {
                Name name = classifierDescriptorMo1491getDeclarationDescriptor.getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                jvmDescriptorTypeWriter.writeTypeVariable(name, t3);
            }
            return t3;
        }
        if ((classifierDescriptorMo1491getDeclarationDescriptor instanceof TypeAliasDescriptor) && mode.getMapTypeAliases()) {
            return (T) mapType(((TypeAliasDescriptor) classifierDescriptorMo1491getDeclarationDescriptor).getExpandedType(), factory, mode, typeMappingConfiguration, jvmDescriptorTypeWriter, writeGenericType);
        }
        throw new UnsupportedOperationException("Unknown type " + kotlinType);
    }

    public static final boolean hasVoidReturnType(CallableDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        if (descriptor instanceof ConstructorDescriptor) {
            return true;
        }
        KotlinType returnType = descriptor.getReturnType();
        Intrinsics.checkNotNull(returnType);
        if (KotlinBuiltIns.isUnit(returnType)) {
            KotlinType returnType2 = descriptor.getReturnType();
            Intrinsics.checkNotNull(returnType2);
            if (!TypeUtils.isNullableType(returnType2) && !(descriptor instanceof PropertyGetterDescriptor)) {
                return true;
            }
        }
        return false;
    }

    public static /* synthetic */ String computeInternalName$default(ClassDescriptor classDescriptor, TypeMappingConfiguration typeMappingConfiguration, int i, Object obj) {
        if ((i & 2) != 0) {
            typeMappingConfiguration = TypeMappingConfigurationImpl.INSTANCE;
        }
        return computeInternalName(classDescriptor, typeMappingConfiguration);
    }

    public static final String computeInternalName(ClassDescriptor klass, TypeMappingConfiguration<?> typeMappingConfiguration) {
        Intrinsics.checkNotNullParameter(klass, "klass");
        Intrinsics.checkNotNullParameter(typeMappingConfiguration, "typeMappingConfiguration");
        String predefinedFullInternalNameForClass = typeMappingConfiguration.getPredefinedFullInternalNameForClass(klass);
        if (predefinedFullInternalNameForClass != null) {
            return predefinedFullInternalNameForClass;
        }
        DeclarationDescriptor containingDeclaration = klass.getContainingDeclaration();
        Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
        String identifier = SpecialNames.safeIdentifier(klass.getName()).getIdentifier();
        Intrinsics.checkNotNullExpressionValue(identifier, "getIdentifier(...)");
        if (containingDeclaration instanceof PackageFragmentDescriptor) {
            FqName fqName = ((PackageFragmentDescriptor) containingDeclaration).getFqName();
            if (fqName.isRoot()) {
                return identifier;
            }
            StringBuilder sb = new StringBuilder();
            String strAsString = fqName.asString();
            Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
            return sb.append(StringsKt.replace$default(strAsString, '.', '/', false, 4, (Object) null)).append('/').append(identifier).toString();
        }
        ClassDescriptor classDescriptor = containingDeclaration instanceof ClassDescriptor ? (ClassDescriptor) containingDeclaration : null;
        if (classDescriptor == null) {
            throw new IllegalArgumentException("Unexpected container: " + containingDeclaration + " for " + klass);
        }
        String predefinedInternalNameForClass = typeMappingConfiguration.getPredefinedInternalNameForClass(classDescriptor);
        if (predefinedInternalNameForClass == null) {
            predefinedInternalNameForClass = computeInternalName(classDescriptor, typeMappingConfiguration);
        }
        return predefinedInternalNameForClass + Typography.dollar + identifier;
    }
}
