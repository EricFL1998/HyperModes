package kotlin.reflect.jvm.internal;

import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.calls.ValueClassAwareCallerKt;
import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.builtins.jvm.CloneableClassScope;
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyGetterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertySetterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaClass;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaConstructor;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaField;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaMethod;
import kotlin.reflect.jvm.internal.impl.load.java.JvmAbi;
import kotlin.reflect.jvm.internal.impl.load.java.SpecialBuiltinMembers;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaMethodDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.descriptors.JavaPropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.sources.JavaSourceElement;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaElement;
import kotlin.reflect.jvm.internal.impl.load.kotlin.MethodSignatureMappingKt;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.ProtoBufUtilKt;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.JvmProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmMemberSignature;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmProtoBufUtil;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.protobuf.GeneratedMessageLite;
import kotlin.reflect.jvm.internal.impl.protobuf.MessageLite;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorFactory;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.jvm.JvmPrimitiveType;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedCallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedPropertyDescriptor;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: RuntimeTypeMapper.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000R\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\tH\u0002J\u0010\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0010\u001a\u00020\tH\u0002J\u0010\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0015H\u0002J\u0012\u0010\u0016\u001a\u00020\u00052\n\u0010\u0017\u001a\u0006\u0012\u0002\b\u00030\u0018R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0019\u001a\u0004\u0018\u00010\u001a*\u0006\u0012\u0002\b\u00030\u00188BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001c¨\u0006\u001d"}, d2 = {"Lkotlin/reflect/jvm/internal/RuntimeTypeMapper;", "", "<init>", "()V", "JAVA_LANG_VOID", "Lkotlin/reflect/jvm/internal/impl/name/ClassId;", "mapSignature", "Lkotlin/reflect/jvm/internal/JvmFunctionSignature;", "possiblySubstitutedFunction", "Lkotlin/reflect/jvm/internal/impl/descriptors/FunctionDescriptor;", "mapPropertySignature", "Lkotlin/reflect/jvm/internal/JvmPropertySignature;", "possiblyOverriddenProperty", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", "isKnownBuiltInFunction", "", "descriptor", "mapJvmFunctionSignature", "Lkotlin/reflect/jvm/internal/JvmFunctionSignature$KotlinFunction;", "mapName", "", "Lkotlin/reflect/jvm/internal/impl/descriptors/CallableMemberDescriptor;", "mapJvmClassToKotlinClassId", "klass", "Ljava/lang/Class;", "primitiveType", "Lkotlin/reflect/jvm/internal/impl/builtins/PrimitiveType;", "getPrimitiveType", "(Ljava/lang/Class;)Lorg/jetbrains/kotlin/builtins/PrimitiveType;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class RuntimeTypeMapper {
    public static final RuntimeTypeMapper INSTANCE = new RuntimeTypeMapper();
    private static final ClassId JAVA_LANG_VOID = ClassId.Companion.topLevel(new FqName("java.lang.Void"));

    private RuntimeTypeMapper() {
    }

    public final JvmFunctionSignature mapSignature(FunctionDescriptor possiblySubstitutedFunction) {
        Method member;
        JvmMemberSignature.Method jvmConstructorSignature;
        JvmMemberSignature.Method jvmMethodSignature;
        Intrinsics.checkNotNullParameter(possiblySubstitutedFunction, "possiblySubstitutedFunction");
        FunctionDescriptor original = ((FunctionDescriptor) DescriptorUtils.unwrapFakeOverride(possiblySubstitutedFunction)).getOriginal();
        Intrinsics.checkNotNullExpressionValue(original, "getOriginal(...)");
        if (original instanceof DeserializedCallableMemberDescriptor) {
            DeserializedMemberDescriptor deserializedMemberDescriptor = (DeserializedMemberDescriptor) original;
            MessageLite proto = deserializedMemberDescriptor.getProto();
            if ((proto instanceof ProtoBuf.Function) && (jvmMethodSignature = JvmProtoBufUtil.INSTANCE.getJvmMethodSignature((ProtoBuf.Function) proto, deserializedMemberDescriptor.getNameResolver(), deserializedMemberDescriptor.getTypeTable())) != null) {
                return new JvmFunctionSignature.KotlinFunction(jvmMethodSignature);
            }
            if ((proto instanceof ProtoBuf.Constructor) && (jvmConstructorSignature = JvmProtoBufUtil.INSTANCE.getJvmConstructorSignature((ProtoBuf.Constructor) proto, deserializedMemberDescriptor.getNameResolver(), deserializedMemberDescriptor.getTypeTable())) != null) {
                DeclarationDescriptor containingDeclaration = possiblySubstitutedFunction.getContainingDeclaration();
                Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
                if (InlineClassesUtilsKt.isInlineClass(containingDeclaration)) {
                    return new JvmFunctionSignature.KotlinFunction(jvmConstructorSignature);
                }
                DeclarationDescriptor containingDeclaration2 = possiblySubstitutedFunction.getContainingDeclaration();
                Intrinsics.checkNotNullExpressionValue(containingDeclaration2, "getContainingDeclaration(...)");
                if (InlineClassesUtilsKt.isMultiFieldValueClass(containingDeclaration2)) {
                    ConstructorDescriptor constructorDescriptor = (ConstructorDescriptor) possiblySubstitutedFunction;
                    if (constructorDescriptor.isPrimary()) {
                        if (!Intrinsics.areEqual(jvmConstructorSignature.getName(), "constructor-impl") || !StringsKt.endsWith$default(jvmConstructorSignature.getDesc(), ")V", false, 2, (Object) null)) {
                            throw new IllegalArgumentException(("Invalid signature: " + jvmConstructorSignature).toString());
                        }
                    } else {
                        if (!Intrinsics.areEqual(jvmConstructorSignature.getName(), "constructor-impl")) {
                            throw new IllegalArgumentException(("Invalid signature: " + jvmConstructorSignature).toString());
                        }
                        ClassDescriptor constructedClass = constructorDescriptor.getConstructedClass();
                        Intrinsics.checkNotNullExpressionValue(constructedClass, "getConstructedClass(...)");
                        String jvmDescriptor = ValueClassAwareCallerKt.toJvmDescriptor(constructedClass);
                        if (StringsKt.endsWith$default(jvmConstructorSignature.getDesc(), ")V", false, 2, (Object) null)) {
                            jvmConstructorSignature = JvmMemberSignature.Method.copy$default(jvmConstructorSignature, null, StringsKt.removeSuffix(jvmConstructorSignature.getDesc(), (CharSequence) "V") + jvmDescriptor, 1, null);
                        } else if (!StringsKt.endsWith$default(jvmConstructorSignature.getDesc(), jvmDescriptor, false, 2, (Object) null)) {
                            throw new IllegalArgumentException(("Invalid signature: " + jvmConstructorSignature).toString());
                        }
                    }
                    return new JvmFunctionSignature.KotlinFunction(jvmConstructorSignature);
                }
                return new JvmFunctionSignature.KotlinConstructor(jvmConstructorSignature);
            }
            return mapJvmFunctionSignature(original);
        }
        if (original instanceof JavaMethodDescriptor) {
            SourceElement source = ((JavaMethodDescriptor) original).getSource();
            JavaSourceElement javaSourceElement = source instanceof JavaSourceElement ? (JavaSourceElement) source : null;
            JavaElement javaElement = javaSourceElement != null ? javaSourceElement.getJavaElement() : null;
            ReflectJavaMethod reflectJavaMethod = javaElement instanceof ReflectJavaMethod ? (ReflectJavaMethod) javaElement : null;
            if (reflectJavaMethod == null || (member = reflectJavaMethod.getMember()) == null) {
                throw new KotlinReflectionInternalError("Incorrect resolution sequence for Java method " + original);
            }
            return new JvmFunctionSignature.JavaMethod(member);
        }
        if (original instanceof JavaClassConstructorDescriptor) {
            SourceElement source2 = ((JavaClassConstructorDescriptor) original).getSource();
            JavaSourceElement javaSourceElement2 = source2 instanceof JavaSourceElement ? (JavaSourceElement) source2 : null;
            JavaElement javaElement2 = javaSourceElement2 != null ? javaSourceElement2.getJavaElement() : null;
            if (javaElement2 instanceof ReflectJavaConstructor) {
                return new JvmFunctionSignature.JavaConstructor(((ReflectJavaConstructor) javaElement2).getMember());
            }
            if (javaElement2 instanceof ReflectJavaClass) {
                ReflectJavaClass reflectJavaClass = (ReflectJavaClass) javaElement2;
                if (reflectJavaClass.isAnnotationType()) {
                    return new JvmFunctionSignature.FakeJavaAnnotationConstructor(reflectJavaClass.getElement());
                }
            }
            throw new KotlinReflectionInternalError("Incorrect resolution sequence for Java constructor " + original + " (" + javaElement2 + ')');
        }
        if (isKnownBuiltInFunction(original)) {
            return mapJvmFunctionSignature(original);
        }
        throw new KotlinReflectionInternalError("Unknown origin of " + original + " (" + original.getClass() + ')');
    }

    public final JvmPropertySignature mapPropertySignature(PropertyDescriptor possiblyOverriddenProperty) {
        Intrinsics.checkNotNullParameter(possiblyOverriddenProperty, "possiblyOverriddenProperty");
        PropertyDescriptor original = ((PropertyDescriptor) DescriptorUtils.unwrapFakeOverride(possiblyOverriddenProperty)).getOriginal();
        Intrinsics.checkNotNullExpressionValue(original, "getOriginal(...)");
        if (original instanceof DeserializedPropertyDescriptor) {
            DeserializedPropertyDescriptor deserializedPropertyDescriptor = (DeserializedPropertyDescriptor) original;
            ProtoBuf.Property proto = deserializedPropertyDescriptor.getProto();
            GeneratedMessageLite.GeneratedExtension<ProtoBuf.Property, JvmProtoBuf.JvmPropertySignature> propertySignature = JvmProtoBuf.propertySignature;
            Intrinsics.checkNotNullExpressionValue(propertySignature, "propertySignature");
            JvmProtoBuf.JvmPropertySignature jvmPropertySignature = (JvmProtoBuf.JvmPropertySignature) ProtoBufUtilKt.getExtensionOrNull(proto, propertySignature);
            if (jvmPropertySignature != null) {
                return new JvmPropertySignature.KotlinProperty(original, proto, jvmPropertySignature, deserializedPropertyDescriptor.getNameResolver(), deserializedPropertyDescriptor.getTypeTable());
            }
        } else if (original instanceof JavaPropertyDescriptor) {
            JavaPropertyDescriptor javaPropertyDescriptor = (JavaPropertyDescriptor) original;
            SourceElement source = javaPropertyDescriptor.getSource();
            JavaSourceElement javaSourceElement = source instanceof JavaSourceElement ? (JavaSourceElement) source : null;
            JavaElement javaElement = javaSourceElement != null ? javaSourceElement.getJavaElement() : null;
            if (javaElement instanceof ReflectJavaField) {
                return new JvmPropertySignature.JavaField(((ReflectJavaField) javaElement).getMember());
            }
            if (javaElement instanceof ReflectJavaMethod) {
                Method member = ((ReflectJavaMethod) javaElement).getMember();
                PropertySetterDescriptor setter = javaPropertyDescriptor.getSetter();
                SourceElement source2 = setter != null ? setter.getSource() : null;
                JavaSourceElement javaSourceElement2 = source2 instanceof JavaSourceElement ? (JavaSourceElement) source2 : null;
                JavaElement javaElement2 = javaSourceElement2 != null ? javaSourceElement2.getJavaElement() : null;
                ReflectJavaMethod reflectJavaMethod = javaElement2 instanceof ReflectJavaMethod ? (ReflectJavaMethod) javaElement2 : null;
                return new JvmPropertySignature.JavaMethodProperty(member, reflectJavaMethod != null ? reflectJavaMethod.getMember() : null);
            }
            throw new KotlinReflectionInternalError("Incorrect resolution sequence for Java field " + original + " (source = " + javaElement + ')');
        }
        PropertyGetterDescriptor getter = original.getGetter();
        Intrinsics.checkNotNull(getter);
        JvmFunctionSignature.KotlinFunction kotlinFunctionMapJvmFunctionSignature = mapJvmFunctionSignature(getter);
        PropertySetterDescriptor setter2 = original.getSetter();
        return new JvmPropertySignature.MappedKotlinProperty(kotlinFunctionMapJvmFunctionSignature, setter2 != null ? mapJvmFunctionSignature(setter2) : null);
    }

    private final boolean isKnownBuiltInFunction(FunctionDescriptor descriptor) {
        if (DescriptorFactory.isEnumValueOfMethod(descriptor) || DescriptorFactory.isEnumValuesMethod(descriptor)) {
            return true;
        }
        return Intrinsics.areEqual(descriptor.getName(), CloneableClassScope.Companion.getCLONE_NAME()) && descriptor.getValueParameters().isEmpty();
    }

    private final JvmFunctionSignature.KotlinFunction mapJvmFunctionSignature(FunctionDescriptor descriptor) {
        return new JvmFunctionSignature.KotlinFunction(new JvmMemberSignature.Method(mapName(descriptor), MethodSignatureMappingKt.computeJvmDescriptor$default(descriptor, false, false, 1, null)));
    }

    private final String mapName(CallableMemberDescriptor descriptor) {
        String jvmMethodNameIfSpecial = SpecialBuiltinMembers.getJvmMethodNameIfSpecial(descriptor);
        if (jvmMethodNameIfSpecial != null) {
            return jvmMethodNameIfSpecial;
        }
        if (descriptor instanceof PropertyGetterDescriptor) {
            String strAsString = DescriptorUtilsKt.getPropertyIfAccessor(descriptor).getName().asString();
            Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
            return JvmAbi.getterName(strAsString);
        }
        if (descriptor instanceof PropertySetterDescriptor) {
            String strAsString2 = DescriptorUtilsKt.getPropertyIfAccessor(descriptor).getName().asString();
            Intrinsics.checkNotNullExpressionValue(strAsString2, "asString(...)");
            return JvmAbi.setterName(strAsString2);
        }
        String strAsString3 = descriptor.getName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString3, "asString(...)");
        return strAsString3;
    }

    public final ClassId mapJvmClassToKotlinClassId(Class<?> klass) {
        ClassId classIdMapJavaToKotlin;
        Intrinsics.checkNotNullParameter(klass, "klass");
        if (klass.isArray()) {
            Class<?> componentType = klass.getComponentType();
            Intrinsics.checkNotNullExpressionValue(componentType, "getComponentType(...)");
            PrimitiveType primitiveType = getPrimitiveType(componentType);
            if (primitiveType != null) {
                return new ClassId(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, primitiveType.getArrayTypeName());
            }
            ClassId.Companion companion = ClassId.Companion;
            FqName safe = StandardNames.FqNames.array.toSafe();
            Intrinsics.checkNotNullExpressionValue(safe, "toSafe(...)");
            return companion.topLevel(safe);
        }
        if (Intrinsics.areEqual(klass, Void.TYPE)) {
            return JAVA_LANG_VOID;
        }
        PrimitiveType primitiveType2 = getPrimitiveType(klass);
        if (primitiveType2 != null) {
            return new ClassId(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, primitiveType2.getTypeName());
        }
        ClassId classId = ReflectClassUtilKt.getClassId(klass);
        return (classId.isLocal() || (classIdMapJavaToKotlin = JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(classId.asSingleFqName())) == null) ? classId : classIdMapJavaToKotlin;
    }

    private final PrimitiveType getPrimitiveType(Class<?> cls) {
        if (cls.isPrimitive()) {
            return JvmPrimitiveType.get(cls.getSimpleName()).getPrimitiveType();
        }
        return null;
    }
}
