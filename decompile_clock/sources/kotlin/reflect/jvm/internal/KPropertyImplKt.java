package kotlin.reflect.jvm.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.calls.Caller;
import kotlin.reflect.jvm.internal.calls.CallerImpl;
import kotlin.reflect.jvm.internal.calls.InternalUnderlyingValOfInlineClass;
import kotlin.reflect.jvm.internal.calls.ThrowingCaller;
import kotlin.reflect.jvm.internal.calls.ValueClassAwareCallerKt;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.JvmProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmProtoBufUtil;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedPropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.types.TypeUtils;

/* JADX INFO: compiled from: KPropertyImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000 \n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a \u0010\u0005\u001a\u0006\u0012\u0002\b\u00030\u0006*\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00022\u0006\u0010\u0007\u001a\u00020\bH\u0002\u001a\f\u0010\t\u001a\u00020\b*\u00020\nH\u0002\"\"\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\u00028@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b\u0003\u0010\u0004¨\u0006\u000b"}, d2 = {"boundReceiver", "", "Lkotlin/reflect/jvm/internal/KPropertyImpl$Accessor;", "getBoundReceiver", "(Lkotlin/reflect/jvm/internal/KPropertyImpl$Accessor;)Ljava/lang/Object;", "computeCallerForAccessor", "Lkotlin/reflect/jvm/internal/calls/Caller;", "isGetter", "", "isJvmFieldPropertyInCompanionObject", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class KPropertyImplKt {
    public static final Object getBoundReceiver(KPropertyImpl.Accessor<?, ?> accessor) {
        Intrinsics.checkNotNullParameter(accessor, "<this>");
        return accessor.getProperty().getBoundReceiver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:16:0x004c  */
    public static final Caller<?> computeCallerForAccessor(KPropertyImpl.Accessor<?, ?> accessor, boolean z) {
        JvmFunctionSignature.KotlinFunction setterSignature;
        Method setterMethod;
        Caller boundInstance;
        JvmProtoBuf.JvmMethodSignature setter;
        Method inlineClassUnboxMethod;
        if (KDeclarationContainerImpl.INSTANCE.getLOCAL_PROPERTY_SIGNATURE$kotlin_reflection().matches(accessor.getProperty().getSignature())) {
            return ThrowingCaller.INSTANCE;
        }
        JvmPropertySignature jvmPropertySignatureMapPropertySignature = RuntimeTypeMapper.INSTANCE.mapPropertySignature(accessor.getProperty().getDescriptor());
        if (jvmPropertySignatureMapPropertySignature instanceof JvmPropertySignature.KotlinProperty) {
            JvmPropertySignature.KotlinProperty kotlinProperty = (JvmPropertySignature.KotlinProperty) jvmPropertySignatureMapPropertySignature;
            JvmProtoBuf.JvmPropertySignature signature = kotlinProperty.getSignature();
            if (z) {
                if (signature.hasGetter()) {
                    setter = signature.getGetter();
                } else {
                    setter = null;
                }
            } else if (signature.hasSetter()) {
                setter = signature.getSetter();
            } else {
                setter = null;
            }
            Method methodFindMethodBySignature = setter != null ? accessor.getProperty().getContainer().findMethodBySignature(kotlinProperty.getNameResolver().getString(setter.getName()), kotlinProperty.getNameResolver().getString(setter.getDesc())) : null;
            if (methodFindMethodBySignature == null) {
                if (InlineClassesUtilsKt.isUnderlyingPropertyOfInlineClass(accessor.getProperty().getDescriptor()) && Intrinsics.areEqual(accessor.getProperty().getDescriptor().getVisibility(), DescriptorVisibilities.INTERNAL)) {
                    Class<?> inlineClass = ValueClassAwareCallerKt.toInlineClass(accessor.getProperty().getDescriptor().getContainingDeclaration());
                    if (inlineClass == null || (inlineClassUnboxMethod = ValueClassAwareCallerKt.getInlineClassUnboxMethod(inlineClass, accessor.getProperty().getDescriptor())) == null) {
                        throw new KotlinReflectionInternalError("Underlying property of inline class " + accessor.getProperty() + " should have a field");
                    }
                    boundInstance = accessor.isBound() ? new InternalUnderlyingValOfInlineClass.Bound(inlineClassUnboxMethod, getBoundReceiver(accessor)) : new InternalUnderlyingValOfInlineClass.Unbound(inlineClassUnboxMethod);
                } else {
                    Field javaField = accessor.getProperty().getJavaField();
                    if (javaField == null) {
                        throw new KotlinReflectionInternalError("No accessors or field is found for property " + accessor.getProperty());
                    }
                    boundInstance = computeCallerForAccessor$computeFieldCaller(accessor, z, javaField);
                }
            } else if (!Modifier.isStatic(methodFindMethodBySignature.getModifiers())) {
                boundInstance = accessor.isBound() ? new CallerImpl.Method.BoundInstance(methodFindMethodBySignature, getBoundReceiver(accessor)) : new CallerImpl.Method.Instance(methodFindMethodBySignature);
            } else if (computeCallerForAccessor$isJvmStaticProperty(accessor)) {
                boundInstance = accessor.isBound() ? new CallerImpl.Method.BoundJvmStaticInObject(methodFindMethodBySignature) : new CallerImpl.Method.JvmStaticInObject(methodFindMethodBySignature);
            } else {
                boundInstance = accessor.isBound() ? new CallerImpl.Method.BoundStatic(methodFindMethodBySignature, getBoundReceiver(accessor)) : new CallerImpl.Method.Static(methodFindMethodBySignature);
            }
        } else if (jvmPropertySignatureMapPropertySignature instanceof JvmPropertySignature.JavaField) {
            boundInstance = computeCallerForAccessor$computeFieldCaller(accessor, z, ((JvmPropertySignature.JavaField) jvmPropertySignatureMapPropertySignature).getField());
        } else if (jvmPropertySignatureMapPropertySignature instanceof JvmPropertySignature.JavaMethodProperty) {
            if (z) {
                setterMethod = ((JvmPropertySignature.JavaMethodProperty) jvmPropertySignatureMapPropertySignature).getGetterMethod();
            } else {
                JvmPropertySignature.JavaMethodProperty javaMethodProperty = (JvmPropertySignature.JavaMethodProperty) jvmPropertySignatureMapPropertySignature;
                setterMethod = javaMethodProperty.getSetterMethod();
                if (setterMethod == null) {
                    throw new KotlinReflectionInternalError("No source found for setter of Java method property: " + javaMethodProperty.getGetterMethod());
                }
            }
            boundInstance = accessor.isBound() ? new CallerImpl.Method.BoundInstance(setterMethod, getBoundReceiver(accessor)) : new CallerImpl.Method.Instance(setterMethod);
        } else {
            if (!(jvmPropertySignatureMapPropertySignature instanceof JvmPropertySignature.MappedKotlinProperty)) {
                throw new NoWhenBranchMatchedException();
            }
            if (z) {
                setterSignature = ((JvmPropertySignature.MappedKotlinProperty) jvmPropertySignatureMapPropertySignature).getGetterSignature();
            } else {
                setterSignature = ((JvmPropertySignature.MappedKotlinProperty) jvmPropertySignatureMapPropertySignature).getSetterSignature();
                if (setterSignature == null) {
                    throw new KotlinReflectionInternalError("No setter found for property " + accessor.getProperty());
                }
            }
            Method methodFindMethodBySignature2 = accessor.getProperty().getContainer().findMethodBySignature(setterSignature.getMethodName(), setterSignature.getMethodDesc());
            if (methodFindMethodBySignature2 == null) {
                throw new KotlinReflectionInternalError("No accessor found for property " + accessor.getProperty());
            }
            Modifier.isStatic(methodFindMethodBySignature2.getModifiers());
            return accessor.isBound() ? new CallerImpl.Method.BoundInstance(methodFindMethodBySignature2, getBoundReceiver(accessor)) : new CallerImpl.Method.Instance(methodFindMethodBySignature2);
        }
        return ValueClassAwareCallerKt.createValueClassAwareCallerIfNeeded$default(boundInstance, accessor.getDescriptor(), false, 2, null);
    }

    private static final boolean computeCallerForAccessor$isJvmStaticProperty(KPropertyImpl.Accessor<?, ?> accessor) {
        return accessor.getProperty().getDescriptor().getAnnotations().hasAnnotation(UtilKt.getJVM_STATIC());
    }

    private static final boolean computeCallerForAccessor$isNotNullProperty(KPropertyImpl.Accessor<?, ?> accessor) {
        return !TypeUtils.isNullableType(accessor.getProperty().getDescriptor().getType());
    }

    private static final CallerImpl<Field> computeCallerForAccessor$computeFieldCaller(KPropertyImpl.Accessor<?, ?> accessor, boolean z, Field field) {
        if (isJvmFieldPropertyInCompanionObject(accessor.getProperty().getDescriptor()) || !Modifier.isStatic(field.getModifiers())) {
            if (z) {
                return accessor.isBound() ? new CallerImpl.FieldGetter.BoundInstance(field, getBoundReceiver(accessor)) : new CallerImpl.FieldGetter.Instance(field);
            }
            return accessor.isBound() ? new CallerImpl.FieldSetter.BoundInstance(field, computeCallerForAccessor$isNotNullProperty(accessor), getBoundReceiver(accessor)) : new CallerImpl.FieldSetter.Instance(field, computeCallerForAccessor$isNotNullProperty(accessor));
        }
        if (computeCallerForAccessor$isJvmStaticProperty(accessor)) {
            if (z) {
                return accessor.isBound() ? new CallerImpl.FieldGetter.BoundJvmStaticInObject(field) : new CallerImpl.FieldGetter.JvmStaticInObject(field);
            }
            return accessor.isBound() ? new CallerImpl.FieldSetter.BoundJvmStaticInObject(field, computeCallerForAccessor$isNotNullProperty(accessor)) : new CallerImpl.FieldSetter.JvmStaticInObject(field, computeCallerForAccessor$isNotNullProperty(accessor));
        }
        if (z) {
            return new CallerImpl.FieldGetter.Static(field);
        }
        return new CallerImpl.FieldSetter.Static(field, computeCallerForAccessor$isNotNullProperty(accessor));
    }

    private static final boolean isJvmFieldPropertyInCompanionObject(PropertyDescriptor propertyDescriptor) {
        DeclarationDescriptor containingDeclaration = propertyDescriptor.getContainingDeclaration();
        Intrinsics.checkNotNullExpressionValue(containingDeclaration, "getContainingDeclaration(...)");
        if (!DescriptorUtils.isCompanionObject(containingDeclaration)) {
            return false;
        }
        DeclarationDescriptor containingDeclaration2 = containingDeclaration.getContainingDeclaration();
        return !(DescriptorUtils.isInterface(containingDeclaration2) || DescriptorUtils.isAnnotationClass(containingDeclaration2)) || ((propertyDescriptor instanceof DeserializedPropertyDescriptor) && JvmProtoBufUtil.isMovedFromInterfaceCompanion(((DeserializedPropertyDescriptor) propertyDescriptor).getProto()));
    }
}
