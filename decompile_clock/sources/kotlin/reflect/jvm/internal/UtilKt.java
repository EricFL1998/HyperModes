package kotlin.reflect.jvm.internal;

import com.xiaomi.onetrack.api.as;
import com.xiaomi.onetrack.api.g;
import com.xiaomi.settingsdk.backup.SettingsBackupConsts;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.JvmClassMappingKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference;
import kotlin.jvm.internal.RepeatableContainer;
import kotlin.reflect.KCallable;
import kotlin.reflect.KType;
import kotlin.reflect.KVisibility;
import kotlin.reflect.jvm.internal.calls.AnnotationConstructorCallerKt;
import kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType;
import kotlin.reflect.jvm.internal.impl.builtins.jvm.JavaToKotlinClassMap;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ReceiverParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectAnnotationSource;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectJavaClassFinderKt;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectKotlinClass;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.RuntimeModuleData;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.RuntimeSourceElementFactory;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaAnnotation;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaClass;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaElement;
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinJvmBinaryClass;
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinJvmBinarySourceElement;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.BinaryVersion;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.VersionRequirementTable;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.protobuf.MessageLite;
import kotlin.reflect.jvm.internal.impl.resolve.InlineClassesUtilsKt;
import kotlin.reflect.jvm.internal.impl.resolve.constants.AnnotationValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ArrayValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.EnumValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ErrorValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.KClassValue;
import kotlin.reflect.jvm.internal.impl.resolve.constants.NullValue;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationComponents;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationContext;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.MemberDeserializer;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: util.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000Ú\u0001\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u001b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u0012\u0010\u0004\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u0005*\u00020\u0006H\u0000\u001a(\u0010\u0007\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\rH\u0002\u001a.\u0010\u0007\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u00052\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\rH\u0002\u001a\u0014\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0005*\u0006\u0012\u0002\b\u00030\u0005H\u0000\u001a\u000e\u0010\u0012\u001a\u0004\u0018\u00010\u0013*\u00020\u0014H\u0000\u001a\u0012\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016*\u00020\u0018H\u0000\u001a\u0018\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00170\u0016*\b\u0012\u0004\u0012\u00020\u00170\u0016H\u0002\u001a\u000e\u0010\u001a\u001a\u0004\u0018\u00010\u0017*\u00020\u001bH\u0002\u001a\u001a\u0010\u001c\u001a\u0004\u0018\u00010\u001d*\u0006\u0012\u0002\b\u00030\u001e2\u0006\u0010\b\u001a\u00020\tH\u0002\u001a\u0016\u0010\u001f\u001a\u0004\u0018\u00010\u001d*\u00020 2\u0006\u0010\b\u001a\u00020\tH\u0002\u001a%\u0010!\u001a\u0002H\"\"\u0004\b\u0000\u0010\"2\f\u0010#\u001a\b\u0012\u0004\u0012\u0002H\"0$H\u0080\bø\u0001\u0000¢\u0006\u0002\u0010%\u001a\u0010\u0010&\u001a\u0004\u0018\u00010'*\u0004\u0018\u00010\u001dH\u0000\u001a\u0014\u0010(\u001a\b\u0012\u0002\b\u0003\u0018\u00010)*\u0004\u0018\u00010\u001dH\u0000\u001a\u0014\u0010*\u001a\b\u0012\u0002\b\u0003\u0018\u00010+*\u0004\u0018\u00010\u001dH\u0000\u001al\u00101\u001a\u0002H2\"\b\b\u0000\u00103*\u000204\"\b\b\u0001\u00102*\u00020.2\n\u00105\u001a\u0006\u0012\u0002\b\u00030\u00052\u0006\u00106\u001a\u0002H32\u0006\u00107\u001a\u0002082\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020<2\u001d\u0010=\u001a\u0019\u0012\u0004\u0012\u00020?\u0012\u0004\u0012\u0002H3\u0012\u0004\u0012\u0002H20>¢\u0006\u0002\b@H\u0000¢\u0006\u0002\u0010A\u001a\u0012\u0010H\u001a\u0004\u0018\u00010\u001d2\u0006\u0010I\u001a\u00020JH\u0000\u001a;\u0010K\u001a\u0004\u0018\u00010L*\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010M\u001a\u00020\u000f2\u001a\u0010N\u001a\u000e\u0012\n\b\u0001\u0012\u0006\u0012\u0002\b\u00030\u00050O\"\u0006\u0012\u0002\b\u00030\u0005H\u0000¢\u0006\u0002\u0010P\u001a\u001a\u0010Q\u001a\u0004\u0018\u00010R*\u0006\u0012\u0002\b\u00030\u00052\u0006\u0010M\u001a\u00020\u000fH\u0000\"\u0014\u0010\u0000\u001a\u00020\u0001X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u001a\u0010,\u001a\u0004\u0018\u00010-*\u00020.8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\b/\u00100\"\u0018\u0010B\u001a\u00020C*\u00020D8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\bB\u0010E\"\u0018\u0010F\u001a\u00020C*\u00020D8@X\u0080\u0004¢\u0006\u0006\u001a\u0004\bG\u0010E\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006S"}, d2 = {"JVM_STATIC", "Lkotlin/reflect/jvm/internal/impl/name/FqName;", "getJVM_STATIC", "()Lorg/jetbrains/kotlin/name/FqName;", "toJavaClass", "Ljava/lang/Class;", "Lkotlin/reflect/jvm/internal/impl/descriptors/ClassDescriptor;", "loadClass", "classLoader", "Ljava/lang/ClassLoader;", "kotlinClassId", "Lkotlin/reflect/jvm/internal/impl/name/ClassId;", "arrayDimensions", "", SettingsBackupConsts.EXTRA_PACKAGE_NAME, "", "className", "createArrayType", "toKVisibility", "Lkotlin/reflect/KVisibility;", "Lkotlin/reflect/jvm/internal/impl/descriptors/DescriptorVisibility;", "computeAnnotations", "", "", "Lkotlin/reflect/jvm/internal/impl/descriptors/annotations/Annotated;", "unwrapRepeatableAnnotations", "toAnnotationInstance", "Lkotlin/reflect/jvm/internal/impl/descriptors/annotations/AnnotationDescriptor;", "toRuntimeValue", "", "Lkotlin/reflect/jvm/internal/impl/resolve/constants/ConstantValue;", "arrayToRuntimeValue", "Lkotlin/reflect/jvm/internal/impl/resolve/constants/ArrayValue;", "reflectionCall", "R", "block", "Lkotlin/Function0;", "(Lkotlin/jvm/functions/Function0;)Ljava/lang/Object;", "asKFunctionImpl", "Lkotlin/reflect/jvm/internal/KFunctionImpl;", "asKPropertyImpl", "Lkotlin/reflect/jvm/internal/KPropertyImpl;", "asKCallableImpl", "Lkotlin/reflect/jvm/internal/KCallableImpl;", "instanceReceiverParameter", "Lkotlin/reflect/jvm/internal/impl/descriptors/ReceiverParameterDescriptor;", "Lkotlin/reflect/jvm/internal/impl/descriptors/CallableDescriptor;", "getInstanceReceiverParameter", "(Lorg/jetbrains/kotlin/descriptors/CallableDescriptor;)Lorg/jetbrains/kotlin/descriptors/ReceiverParameterDescriptor;", "deserializeToDescriptor", "D", "M", "Lkotlin/reflect/jvm/internal/impl/protobuf/MessageLite;", "moduleAnchor", "proto", "nameResolver", "Lkotlin/reflect/jvm/internal/impl/metadata/deserialization/NameResolver;", "typeTable", "Lkotlin/reflect/jvm/internal/impl/metadata/deserialization/TypeTable;", "metadataVersion", "Lkotlin/reflect/jvm/internal/impl/metadata/deserialization/BinaryVersion;", "createDescriptor", "Lkotlin/Function2;", "Lkotlin/reflect/jvm/internal/impl/serialization/deserialization/MemberDeserializer;", "Lkotlin/ExtensionFunctionType;", "(Ljava/lang/Class;Lorg/jetbrains/kotlin/protobuf/MessageLite;Lorg/jetbrains/kotlin/metadata/deserialization/NameResolver;Lorg/jetbrains/kotlin/metadata/deserialization/TypeTable;Lorg/jetbrains/kotlin/metadata/deserialization/BinaryVersion;Lkotlin/jvm/functions/Function2;)Lorg/jetbrains/kotlin/descriptors/CallableDescriptor;", "isInlineClassType", "", "Lkotlin/reflect/KType;", "(Lkotlin/reflect/KType;)Z", "needsMultiFieldValueClassFlattening", "getNeedsMultiFieldValueClassFlattening", "defaultPrimitiveValue", "type", "Ljava/lang/reflect/Type;", "getDeclaredMethodOrNull", "Ljava/lang/reflect/Method;", as.a, "parameterTypes", "", "(Ljava/lang/Class;Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;", "getDeclaredFieldOrNull", "Ljava/lang/reflect/Field;", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class UtilKt {
    private static final FqName JVM_STATIC = new FqName("kotlin.jvm.JvmStatic");

    /* JADX INFO: compiled from: util.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[PrimitiveType.values().length];
            try {
                iArr[PrimitiveType.BOOLEAN.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[PrimitiveType.CHAR.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[PrimitiveType.BYTE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[PrimitiveType.SHORT.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr[PrimitiveType.INT.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr[PrimitiveType.FLOAT.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                iArr[PrimitiveType.LONG.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                iArr[PrimitiveType.DOUBLE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public static final FqName getJVM_STATIC() {
        return JVM_STATIC;
    }

    public static final Class<?> toJavaClass(ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(classDescriptor, "<this>");
        SourceElement source = classDescriptor.getSource();
        Intrinsics.checkNotNullExpressionValue(source, "getSource(...)");
        if (source instanceof KotlinJvmBinarySourceElement) {
            KotlinJvmBinaryClass binaryClass = ((KotlinJvmBinarySourceElement) source).getBinaryClass();
            Intrinsics.checkNotNull(binaryClass, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.runtime.components.ReflectKotlinClass");
            return ((ReflectKotlinClass) binaryClass).getKlass();
        }
        if (source instanceof RuntimeSourceElementFactory.RuntimeSourceElement) {
            ReflectJavaElement javaElement = ((RuntimeSourceElementFactory.RuntimeSourceElement) source).getJavaElement();
            Intrinsics.checkNotNull(javaElement, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.runtime.structure.ReflectJavaClass");
            return ((ReflectJavaClass) javaElement).getElement();
        }
        ClassId classId = DescriptorUtilsKt.getClassId(classDescriptor);
        if (classId == null) {
            return null;
        }
        return loadClass(ReflectClassUtilKt.getSafeClassLoader(classDescriptor.getClass()), classId, 0);
    }

    static /* synthetic */ Class loadClass$default(ClassLoader classLoader, ClassId classId, int i, int i2, Object obj) {
        if ((i2 & 4) != 0) {
            i = 0;
        }
        return loadClass(classLoader, classId, i);
    }

    private static final Class<?> loadClass(ClassLoader classLoader, ClassId classId, int i) {
        JavaToKotlinClassMap javaToKotlinClassMap = JavaToKotlinClassMap.INSTANCE;
        FqNameUnsafe unsafe = classId.asSingleFqName().toUnsafe();
        Intrinsics.checkNotNullExpressionValue(unsafe, "toUnsafe(...)");
        ClassId classIdMapKotlinToJava = javaToKotlinClassMap.mapKotlinToJava(unsafe);
        if (classIdMapKotlinToJava != null) {
            classId = classIdMapKotlinToJava;
        }
        String strAsString = classId.getPackageFqName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        String strAsString2 = classId.getRelativeClassName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString2, "asString(...)");
        return loadClass(classLoader, strAsString, strAsString2, i);
    }

    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    private static final Class<?> loadClass(ClassLoader classLoader, String str, String str2, int i) {
        if (Intrinsics.areEqual(str, "kotlin")) {
            switch (str2.hashCode()) {
                case -901856463:
                    if (str2.equals("BooleanArray")) {
                        return boolean[].class;
                    }
                    break;
                case -763279523:
                    if (str2.equals("ShortArray")) {
                        return short[].class;
                    }
                    break;
                case -755911549:
                    if (str2.equals("CharArray")) {
                        return char[].class;
                    }
                    break;
                case -74930671:
                    if (str2.equals("ByteArray")) {
                        return byte[].class;
                    }
                    break;
                case 22374632:
                    if (str2.equals("DoubleArray")) {
                        return double[].class;
                    }
                    break;
                case 63537721:
                    if (str2.equals("Array")) {
                        return Object[].class;
                    }
                    break;
                case 601811914:
                    if (str2.equals("IntArray")) {
                        return int[].class;
                    }
                    break;
                case 948852093:
                    if (str2.equals("FloatArray")) {
                        return float[].class;
                    }
                    break;
                case 2104330525:
                    if (str2.equals("LongArray")) {
                        return long[].class;
                    }
                    break;
            }
        }
        StringBuilder sb = new StringBuilder();
        if (i > 0) {
            for (int i2 = 0; i2 < i; i2++) {
                sb.append("[");
            }
            sb.append("L");
        }
        if (str.length() > 0) {
            sb.append(str + '.');
        }
        sb.append(StringsKt.replace$default(str2, '.', Typography.dollar, false, 4, (Object) null));
        if (i > 0) {
            sb.append(";");
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return ReflectJavaClassFinderKt.tryLoadClass(classLoader, string);
    }

    public static final Class<?> createArrayType(Class<?> cls) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        return Array.newInstance(cls, 0).getClass();
    }

    public static final KVisibility toKVisibility(DescriptorVisibility descriptorVisibility) {
        Intrinsics.checkNotNullParameter(descriptorVisibility, "<this>");
        if (Intrinsics.areEqual(descriptorVisibility, DescriptorVisibilities.PUBLIC)) {
            return KVisibility.PUBLIC;
        }
        if (Intrinsics.areEqual(descriptorVisibility, DescriptorVisibilities.PROTECTED)) {
            return KVisibility.PROTECTED;
        }
        if (Intrinsics.areEqual(descriptorVisibility, DescriptorVisibilities.INTERNAL)) {
            return KVisibility.INTERNAL;
        }
        if (Intrinsics.areEqual(descriptorVisibility, DescriptorVisibilities.PRIVATE) || Intrinsics.areEqual(descriptorVisibility, DescriptorVisibilities.PRIVATE_TO_THIS)) {
            return KVisibility.PRIVATE;
        }
        return null;
    }

    public static final List<Annotation> computeAnnotations(Annotated annotated) {
        Annotation annotationInstance;
        Intrinsics.checkNotNullParameter(annotated, "<this>");
        Annotations annotations = annotated.getAnnotations();
        ArrayList arrayList = new ArrayList();
        for (AnnotationDescriptor annotationDescriptor : annotations) {
            SourceElement source = annotationDescriptor.getSource();
            if (source instanceof ReflectAnnotationSource) {
                annotationInstance = ((ReflectAnnotationSource) source).getAnnotation();
            } else if (source instanceof RuntimeSourceElementFactory.RuntimeSourceElement) {
                ReflectJavaElement javaElement = ((RuntimeSourceElementFactory.RuntimeSourceElement) source).getJavaElement();
                ReflectJavaAnnotation reflectJavaAnnotation = javaElement instanceof ReflectJavaAnnotation ? (ReflectJavaAnnotation) javaElement : null;
                annotationInstance = reflectJavaAnnotation != null ? reflectJavaAnnotation.getAnnotation() : null;
            } else {
                annotationInstance = toAnnotationInstance(annotationDescriptor);
            }
            if (annotationInstance != null) {
                arrayList.add(annotationInstance);
            }
        }
        return unwrapRepeatableAnnotations(arrayList);
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static final List<Annotation> unwrapRepeatableAnnotations(List<? extends Annotation> list) throws IllegalAccessException, InvocationTargetException {
        List listListOf;
        List<? extends Annotation> list2 = list;
        if ((list2 instanceof Collection) && list2.isEmpty()) {
            return list;
        }
        Iterator it = list2.iterator();
        while (it.hasNext()) {
            if (Intrinsics.areEqual(JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass((Annotation) it.next())).getSimpleName(), "Container")) {
                ArrayList arrayList = new ArrayList();
                for (Annotation annotation : list2) {
                    Class javaClass = JvmClassMappingKt.getJavaClass(JvmClassMappingKt.getAnnotationClass(annotation));
                    if (Intrinsics.areEqual(javaClass.getSimpleName(), "Container") && javaClass.getAnnotation(RepeatableContainer.class) != null) {
                        Object objInvoke = javaClass.getDeclaredMethod(g.p, new Class[0]).invoke(annotation, new Object[0]);
                        Intrinsics.checkNotNull(objInvoke, "null cannot be cast to non-null type kotlin.Array<out kotlin.Annotation>");
                        listListOf = ArraysKt.asList((Annotation[]) objInvoke);
                    } else {
                        listListOf = CollectionsKt.listOf(annotation);
                    }
                    CollectionsKt.addAll(arrayList, listListOf);
                }
                return arrayList;
            }
        }
        return list;
    }

    private static final Annotation toAnnotationInstance(AnnotationDescriptor annotationDescriptor) {
        ClassDescriptor annotationClass = DescriptorUtilsKt.getAnnotationClass(annotationDescriptor);
        Class<?> javaClass = annotationClass != null ? toJavaClass(annotationClass) : null;
        if (!(javaClass instanceof Class)) {
            javaClass = null;
        }
        if (javaClass == null) {
            return null;
        }
        Set<Map.Entry<Name, ConstantValue<?>>> setEntrySet = annotationDescriptor.getAllValueArguments().entrySet();
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = setEntrySet.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Name name = (Name) entry.getKey();
            ConstantValue constantValue = (ConstantValue) entry.getValue();
            ClassLoader classLoader = javaClass.getClassLoader();
            Intrinsics.checkNotNullExpressionValue(classLoader, "getClassLoader(...)");
            Object runtimeValue = toRuntimeValue(constantValue, classLoader);
            Pair pair = runtimeValue != null ? TuplesKt.to(name.asString(), runtimeValue) : null;
            if (pair != null) {
                arrayList.add(pair);
            }
        }
        return (Annotation) AnnotationConstructorCallerKt.createAnnotationInstance$default(javaClass, MapsKt.toMap(arrayList), null, 4, null);
    }

    private static final Object toRuntimeValue(ConstantValue<?> constantValue, ClassLoader classLoader) {
        if (constantValue instanceof AnnotationValue) {
            return toAnnotationInstance(((AnnotationValue) constantValue).getValue());
        }
        if (constantValue instanceof ArrayValue) {
            return arrayToRuntimeValue((ArrayValue) constantValue, classLoader);
        }
        if (constantValue instanceof EnumValue) {
            Pair<? extends ClassId, ? extends Name> value = ((EnumValue) constantValue).getValue();
            ClassId classIdComponent1 = value.component1();
            Name nameComponent2 = value.component2();
            Class clsLoadClass$default = loadClass$default(classLoader, classIdComponent1, 0, 4, null);
            if (clsLoadClass$default != null) {
                return Util.getEnumConstantByName(clsLoadClass$default, nameComponent2.asString());
            }
        } else if (constantValue instanceof KClassValue) {
            KClassValue.Value value2 = ((KClassValue) constantValue).getValue();
            if (value2 instanceof KClassValue.Value.NormalClass) {
                KClassValue.Value.NormalClass normalClass = (KClassValue.Value.NormalClass) value2;
                return loadClass(classLoader, normalClass.getClassId(), normalClass.getArrayDimensions());
            }
            if (!(value2 instanceof KClassValue.Value.LocalClass)) {
                throw new NoWhenBranchMatchedException();
            }
            ClassifierDescriptor classifierDescriptorMo1491getDeclarationDescriptor = ((KClassValue.Value.LocalClass) value2).getType().getConstructor().mo1491getDeclarationDescriptor();
            ClassDescriptor classDescriptor = classifierDescriptorMo1491getDeclarationDescriptor instanceof ClassDescriptor ? (ClassDescriptor) classifierDescriptorMo1491getDeclarationDescriptor : null;
            if (classDescriptor != null) {
                return toJavaClass(classDescriptor);
            }
        } else if (!(constantValue instanceof ErrorValue) && !(constantValue instanceof NullValue)) {
            return constantValue.getValue();
        }
        return null;
    }

    /* JADX WARN: Incorrect type for immutable var: ssa=boolean[], code=short[], for r7v13, types: [boolean[]] */
    /* JADX WARN: Incorrect type for immutable var: ssa=byte[], code=short[], for r7v15, types: [byte[]] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r7v10, types: [java.lang.Object[]] */
    /* JADX WARN: Type inference failed for: r7v11, types: [java.lang.Class[]] */
    /* JADX WARN: Type inference failed for: r7v12, types: [java.lang.String[]] */
    /* JADX WARN: Type inference failed for: r7v17, types: [int[]] */
    /* JADX WARN: Type inference failed for: r7v18, types: [float[]] */
    /* JADX WARN: Type inference failed for: r7v19, types: [long[]] */
    /* JADX WARN: Type inference failed for: r7v21, types: [double[]] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    private static final java.lang.Object arrayToRuntimeValue(kotlin.reflect.jvm.internal.impl.resolve.constants.ArrayValue r6, java.lang.ClassLoader r7) {
        /*
            Method dump skipped, instruction units count: 604
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: kotlin.reflect.jvm.internal.UtilKt.arrayToRuntimeValue(kotlin.reflect.jvm.internal.impl.resolve.constants.ArrayValue, java.lang.ClassLoader):java.lang.Object");
    }

    public static final KFunctionImpl asKFunctionImpl(Object obj) {
        KFunctionImpl kFunctionImpl = obj instanceof KFunctionImpl ? (KFunctionImpl) obj : null;
        if (kFunctionImpl != null) {
            return kFunctionImpl;
        }
        FunctionReference functionReference = obj instanceof FunctionReference ? (FunctionReference) obj : null;
        KCallable kCallableCompute = functionReference != null ? functionReference.compute() : null;
        if (kCallableCompute instanceof KFunctionImpl) {
            return (KFunctionImpl) kCallableCompute;
        }
        return null;
    }

    public static final KPropertyImpl<?> asKPropertyImpl(Object obj) {
        KPropertyImpl<?> kPropertyImpl = obj instanceof KPropertyImpl ? (KPropertyImpl) obj : null;
        if (kPropertyImpl != null) {
            return kPropertyImpl;
        }
        PropertyReference propertyReference = obj instanceof PropertyReference ? (PropertyReference) obj : null;
        KCallable kCallableCompute = propertyReference != null ? propertyReference.compute() : null;
        if (kCallableCompute instanceof KPropertyImpl) {
            return (KPropertyImpl) kCallableCompute;
        }
        return null;
    }

    public static final KCallableImpl<?> asKCallableImpl(Object obj) {
        KCallableImpl<?> kCallableImpl = obj instanceof KCallableImpl ? (KCallableImpl) obj : null;
        if (kCallableImpl != null) {
            return kCallableImpl;
        }
        KFunctionImpl kFunctionImplAsKFunctionImpl = asKFunctionImpl(obj);
        return kFunctionImplAsKFunctionImpl != null ? kFunctionImplAsKFunctionImpl : asKPropertyImpl(obj);
    }

    public static final ReceiverParameterDescriptor getInstanceReceiverParameter(CallableDescriptor callableDescriptor) {
        Intrinsics.checkNotNullParameter(callableDescriptor, "<this>");
        if (callableDescriptor.getDispatchReceiverParameter() == null) {
            return null;
        }
        DeclarationDescriptor containingDeclaration = callableDescriptor.getContainingDeclaration();
        Intrinsics.checkNotNull(containingDeclaration, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.ClassDescriptor");
        return ((ClassDescriptor) containingDeclaration).getThisAsReceiverParameter();
    }

    public static final <M extends MessageLite, D extends CallableDescriptor> D deserializeToDescriptor(Class<?> moduleAnchor, M proto, NameResolver nameResolver, TypeTable typeTable, BinaryVersion metadataVersion, Function2<? super MemberDeserializer, ? super M, ? extends D> createDescriptor) {
        List<ProtoBuf.TypeParameter> typeParameterList;
        Intrinsics.checkNotNullParameter(moduleAnchor, "moduleAnchor");
        Intrinsics.checkNotNullParameter(proto, "proto");
        Intrinsics.checkNotNullParameter(nameResolver, "nameResolver");
        Intrinsics.checkNotNullParameter(typeTable, "typeTable");
        Intrinsics.checkNotNullParameter(metadataVersion, "metadataVersion");
        Intrinsics.checkNotNullParameter(createDescriptor, "createDescriptor");
        RuntimeModuleData orCreateModule = ModuleByClassLoaderKt.getOrCreateModule(moduleAnchor);
        if (proto instanceof ProtoBuf.Function) {
            typeParameterList = ((ProtoBuf.Function) proto).getTypeParameterList();
        } else {
            if (!(proto instanceof ProtoBuf.Property)) {
                throw new IllegalStateException(("Unsupported message: " + proto).toString());
            }
            typeParameterList = ((ProtoBuf.Property) proto).getTypeParameterList();
        }
        List<ProtoBuf.TypeParameter> list = typeParameterList;
        DeserializationComponents deserialization = orCreateModule.getDeserialization();
        ModuleDescriptor module = orCreateModule.getModule();
        VersionRequirementTable empty = VersionRequirementTable.Companion.getEMPTY();
        Intrinsics.checkNotNull(list);
        return createDescriptor.invoke(new MemberDeserializer(new DeserializationContext(deserialization, nameResolver, module, typeTable, empty, metadataVersion, null, null, list)), proto);
    }

    public static final boolean isInlineClassType(KType kType) {
        KotlinType type;
        Intrinsics.checkNotNullParameter(kType, "<this>");
        KTypeImpl kTypeImpl = kType instanceof KTypeImpl ? (KTypeImpl) kType : null;
        return (kTypeImpl == null || (type = kTypeImpl.getType()) == null || !InlineClassesUtilsKt.isInlineClassType(type)) ? false : true;
    }

    public static final boolean getNeedsMultiFieldValueClassFlattening(KType kType) {
        KotlinType type;
        Intrinsics.checkNotNullParameter(kType, "<this>");
        KTypeImpl kTypeImpl = kType instanceof KTypeImpl ? (KTypeImpl) kType : null;
        return (kTypeImpl == null || (type = kTypeImpl.getType()) == null || !InlineClassesUtilsKt.needsMfvcFlattening(type)) ? false : true;
    }

    public static final Object defaultPrimitiveValue(Type type) {
        Intrinsics.checkNotNullParameter(type, "type");
        if (type instanceof Class) {
            Class cls = (Class) type;
            if (cls.isPrimitive()) {
                if (Intrinsics.areEqual(cls, Boolean.TYPE)) {
                    return false;
                }
                if (Intrinsics.areEqual(cls, Character.TYPE)) {
                    return (char) 0;
                }
                if (Intrinsics.areEqual(cls, Byte.TYPE)) {
                    return (byte) 0;
                }
                if (Intrinsics.areEqual(cls, Short.TYPE)) {
                    return (short) 0;
                }
                if (Intrinsics.areEqual(cls, Integer.TYPE)) {
                    return 0;
                }
                if (Intrinsics.areEqual(cls, Float.TYPE)) {
                    return Float.valueOf(0.0f);
                }
                if (Intrinsics.areEqual(cls, Long.TYPE)) {
                    return 0L;
                }
                if (Intrinsics.areEqual(cls, Double.TYPE)) {
                    return Double.valueOf(0.0d);
                }
                if (Intrinsics.areEqual(cls, Void.TYPE)) {
                    throw new IllegalStateException("Parameter with void type is illegal");
                }
                throw new UnsupportedOperationException("Unknown primitive: " + type);
            }
        }
        return null;
    }

    public static final Method getDeclaredMethodOrNull(Class<?> cls, String name, Class<?>... parameterTypes) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(parameterTypes, "parameterTypes");
        try {
            return cls.getDeclaredMethod(name, (Class[]) Arrays.copyOf(parameterTypes, parameterTypes.length));
        } catch (NoSuchMethodException unused) {
            return null;
        }
    }

    public static final Field getDeclaredFieldOrNull(Class<?> cls, String name) {
        Intrinsics.checkNotNullParameter(cls, "<this>");
        Intrinsics.checkNotNullParameter(name, "name");
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException unused) {
            return null;
        }
    }
}
