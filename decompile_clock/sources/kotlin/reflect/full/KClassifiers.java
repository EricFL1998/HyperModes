package kotlin.reflect.full;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KClassifier;
import kotlin.reflect.KType;
import kotlin.reflect.KTypeProjection;
import kotlin.reflect.KVariance;
import kotlin.reflect.jvm.internal.KClassifierImpl;
import kotlin.reflect.jvm.internal.KTypeImpl;
import kotlin.reflect.jvm.internal.KotlinReflectionInternalError;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.StarProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.TypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionBase;
import kotlin.reflect.jvm.internal.impl.types.TypeProjectionImpl;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;

/* JADX INFO: compiled from: KClassifiers.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00006\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u001b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a6\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u000e\b\u0002\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\b\b\u0002\u0010\u0006\u001a\u00020\u00072\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\t0\u0004H\u0007\u001a.\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u00042\u0006\u0010\u0006\u001a\u00020\u0007H\u0002\"\u001e\u0010\u0010\u001a\u00020\u0001*\u00020\u00028FX\u0087\u0004¢\u0006\f\u0012\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015"}, d2 = {"createType", "Lkotlin/reflect/KType;", "Lkotlin/reflect/KClassifier;", "arguments", "", "Lkotlin/reflect/KTypeProjection;", "nullable", "", "annotations", "", "createKotlinType", "Lkotlin/reflect/jvm/internal/impl/types/SimpleType;", "attributes", "Lkotlin/reflect/jvm/internal/impl/types/TypeAttributes;", "typeConstructor", "Lkotlin/reflect/jvm/internal/impl/types/TypeConstructor;", "starProjectedType", "getStarProjectedType$annotations", "(Lkotlin/reflect/KClassifier;)V", "getStarProjectedType", "(Lkotlin/reflect/KClassifier;)Lkotlin/reflect/KType;", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class KClassifiers {

    /* JADX INFO: compiled from: KClassifiers.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[KVariance.values().length];
            try {
                iArr[KVariance.INVARIANT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[KVariance.IN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[KVariance.OUT.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public static /* synthetic */ void getStarProjectedType$annotations(KClassifier kClassifier) {
    }

    public static /* synthetic */ KType createType$default(KClassifier kClassifier, List list, boolean z, List list2, int i, Object obj) {
        if ((i & 1) != 0) {
            list = CollectionsKt.emptyList();
        }
        if ((i & 2) != 0) {
            z = false;
        }
        if ((i & 4) != 0) {
            list2 = CollectionsKt.emptyList();
        }
        return createType(kClassifier, list, z, list2);
    }

    public static final KType createType(KClassifier kClassifier, List<KTypeProjection> arguments, boolean z, List<? extends Annotation> annotations) {
        ClassifierDescriptor descriptor;
        Intrinsics.checkNotNullParameter(kClassifier, "<this>");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        KClassifierImpl kClassifierImpl = kClassifier instanceof KClassifierImpl ? (KClassifierImpl) kClassifier : null;
        if (kClassifierImpl == null || (descriptor = kClassifierImpl.getDescriptor()) == null) {
            throw new KotlinReflectionInternalError("Cannot create type for an unsupported classifier: " + kClassifier + " (" + kClassifier.getClass() + ')');
        }
        TypeConstructor typeConstructor = descriptor.getTypeConstructor();
        Intrinsics.checkNotNullExpressionValue(typeConstructor, "getTypeConstructor(...)");
        List<TypeParameterDescriptor> parameters = typeConstructor.getParameters();
        Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
        if (parameters.size() != arguments.size()) {
            throw new IllegalArgumentException("Class declares " + parameters.size() + " type parameters, but " + arguments.size() + " were provided.");
        }
        return new KTypeImpl(createKotlinType(annotations.isEmpty() ? TypeAttributes.Companion.getEmpty() : TypeAttributes.Companion.getEmpty(), typeConstructor, arguments, z), null, 2, null);
    }

    private static final SimpleType createKotlinType(TypeAttributes typeAttributes, TypeConstructor typeConstructor, List<KTypeProjection> list, boolean z) {
        TypeProjectionBase starProjectionImpl;
        List<TypeParameterDescriptor> parameters = typeConstructor.getParameters();
        Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
        List<KTypeProjection> list2 = list;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list2, 10));
        int i = 0;
        for (Object obj : list2) {
            int i2 = i + 1;
            if (i < 0) {
                CollectionsKt.throwIndexOverflow();
            }
            KTypeProjection kTypeProjection = (KTypeProjection) obj;
            KTypeImpl kTypeImpl = (KTypeImpl) kTypeProjection.getType();
            KotlinType type = kTypeImpl != null ? kTypeImpl.getType() : null;
            KVariance variance = kTypeProjection.getVariance();
            int i3 = variance == null ? -1 : WhenMappings.$EnumSwitchMapping$0[variance.ordinal()];
            if (i3 == -1) {
                TypeParameterDescriptor typeParameterDescriptor = parameters.get(i);
                Intrinsics.checkNotNullExpressionValue(typeParameterDescriptor, "get(...)");
                starProjectionImpl = new StarProjectionImpl(typeParameterDescriptor);
            } else if (i3 == 1) {
                Variance variance2 = Variance.INVARIANT;
                Intrinsics.checkNotNull(type);
                starProjectionImpl = new TypeProjectionImpl(variance2, type);
            } else if (i3 == 2) {
                Variance variance3 = Variance.IN_VARIANCE;
                Intrinsics.checkNotNull(type);
                starProjectionImpl = new TypeProjectionImpl(variance3, type);
            } else {
                if (i3 != 3) {
                    throw new NoWhenBranchMatchedException();
                }
                Variance variance4 = Variance.OUT_VARIANCE;
                Intrinsics.checkNotNull(type);
                starProjectionImpl = new TypeProjectionImpl(variance4, type);
            }
            arrayList.add(starProjectionImpl);
            i = i2;
        }
        return KotlinTypeFactory.simpleType$default(typeAttributes, typeConstructor, arrayList, z, (KotlinTypeRefiner) null, 16, (Object) null);
    }

    public static final KType getStarProjectedType(KClassifier kClassifier) {
        ClassifierDescriptor descriptor;
        Intrinsics.checkNotNullParameter(kClassifier, "<this>");
        KClassifierImpl kClassifierImpl = kClassifier instanceof KClassifierImpl ? (KClassifierImpl) kClassifier : null;
        if (kClassifierImpl == null || (descriptor = kClassifierImpl.getDescriptor()) == null) {
            return createType$default(kClassifier, null, false, null, 7, null);
        }
        List<TypeParameterDescriptor> parameters = descriptor.getTypeConstructor().getParameters();
        Intrinsics.checkNotNullExpressionValue(parameters, "getParameters(...)");
        if (parameters.isEmpty()) {
            return createType$default(kClassifier, null, false, null, 7, null);
        }
        List<TypeParameterDescriptor> list = parameters;
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        for (TypeParameterDescriptor typeParameterDescriptor : list) {
            arrayList.add(KTypeProjection.INSTANCE.getSTAR());
        }
        return createType$default(kClassifier, arrayList, false, null, 6, null);
    }
}
