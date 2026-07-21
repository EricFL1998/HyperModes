package kotlin.reflect.jvm.internal.impl.types.checker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.jvm.internal.impl.resolve.calls.inference.CapturedTypeConstructorImpl;
import kotlin.reflect.jvm.internal.impl.resolve.constants.IntegerValueTypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.AbstractTypePreparator;
import kotlin.reflect.jvm.internal.impl.types.FlexibleType;
import kotlin.reflect.jvm.internal.impl.types.IntersectionTypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.KotlinTypeFactory;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.TypeUtils;
import kotlin.reflect.jvm.internal.impl.types.TypeWithEnhancementKt;
import kotlin.reflect.jvm.internal.impl.types.UnwrappedType;
import kotlin.reflect.jvm.internal.impl.types.Variance;
import kotlin.reflect.jvm.internal.impl.types.model.CaptureStatus;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: KotlinTypePreparator.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class KotlinTypePreparator extends AbstractTypePreparator {
    private final SimpleType transformToNewType(SimpleType simpleType) {
        KotlinType type;
        TypeConstructor constructor = simpleType.getConstructor();
        IntersectionTypeConstructor alternative = null;
        unwrappedTypeUnwrap = null;
        UnwrappedType unwrappedTypeUnwrap = null;
        if (constructor instanceof CapturedTypeConstructorImpl) {
            CapturedTypeConstructorImpl capturedTypeConstructorImpl = (CapturedTypeConstructorImpl) constructor;
            TypeProjection projection = capturedTypeConstructorImpl.getProjection();
            if (projection.getProjectionKind() != Variance.IN_VARIANCE) {
                projection = null;
            }
            if (projection != null && (type = projection.getType()) != null) {
                unwrappedTypeUnwrap = type.unwrap();
            }
            UnwrappedType unwrappedType = unwrappedTypeUnwrap;
            if (capturedTypeConstructorImpl.getNewTypeConstructor() == null) {
                TypeProjection projection2 = capturedTypeConstructorImpl.getProjection();
                Collection<KotlinType> collectionMo1492getSupertypes = capturedTypeConstructorImpl.mo1492getSupertypes();
                ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(collectionMo1492getSupertypes, 10));
                Iterator<T> it = collectionMo1492getSupertypes.iterator();
                while (it.hasNext()) {
                    arrayList.add(((KotlinType) it.next()).unwrap());
                }
                capturedTypeConstructorImpl.setNewTypeConstructor(new NewCapturedTypeConstructor(projection2, arrayList, null, 4, null));
            }
            CaptureStatus captureStatus = CaptureStatus.FOR_SUBTYPING;
            NewCapturedTypeConstructor newTypeConstructor = capturedTypeConstructorImpl.getNewTypeConstructor();
            Intrinsics.checkNotNull(newTypeConstructor);
            return new NewCapturedType(captureStatus, newTypeConstructor, unwrappedType, simpleType.getAttributes(), simpleType.isMarkedNullable(), false, 32, null);
        }
        boolean z = false;
        if (constructor instanceof IntegerValueTypeConstructor) {
            Collection<KotlinType> collectionMo1492getSupertypes2 = ((IntegerValueTypeConstructor) constructor).mo1492getSupertypes();
            ArrayList arrayList2 = new ArrayList(CollectionsKt.collectionSizeOrDefault(collectionMo1492getSupertypes2, 10));
            Iterator<T> it2 = collectionMo1492getSupertypes2.iterator();
            while (it2.hasNext()) {
                arrayList2.add(TypeUtils.makeNullableAsSpecified((KotlinType) it2.next(), simpleType.isMarkedNullable()));
            }
            return KotlinTypeFactory.simpleTypeWithNonTrivialMemberScope(simpleType.getAttributes(), new IntersectionTypeConstructor(arrayList2), CollectionsKt.emptyList(), false, simpleType.getMemberScope());
        }
        if (!(constructor instanceof IntersectionTypeConstructor) || !simpleType.isMarkedNullable()) {
            return simpleType;
        }
        IntersectionTypeConstructor intersectionTypeConstructor = (IntersectionTypeConstructor) constructor;
        Collection<KotlinType> collectionMo1492getSupertypes3 = intersectionTypeConstructor.mo1492getSupertypes();
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(collectionMo1492getSupertypes3, 10));
        Iterator<T> it3 = collectionMo1492getSupertypes3.iterator();
        while (it3.hasNext()) {
            arrayList3.add(TypeUtilsKt.makeNullable((KotlinType) it3.next()));
            z = true;
        }
        ArrayList arrayList4 = arrayList3;
        if (z) {
            KotlinType alternativeType = intersectionTypeConstructor.getAlternativeType();
            alternative = new IntersectionTypeConstructor(arrayList4).setAlternative(alternativeType != null ? TypeUtilsKt.makeNullable(alternativeType) : null);
        }
        if (alternative != null) {
            intersectionTypeConstructor = alternative;
        }
        return intersectionTypeConstructor.createType();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.AbstractTypePreparator
    public UnwrappedType prepareType(KotlinTypeMarker type) {
        SimpleType simpleTypeFlexibleType;
        Intrinsics.checkNotNullParameter(type, "type");
        if (!(type instanceof KotlinType)) {
            throw new IllegalArgumentException("Failed requirement.".toString());
        }
        UnwrappedType unwrappedTypeUnwrap = ((KotlinType) type).unwrap();
        if (unwrappedTypeUnwrap instanceof SimpleType) {
            simpleTypeFlexibleType = transformToNewType((SimpleType) unwrappedTypeUnwrap);
        } else {
            if (!(unwrappedTypeUnwrap instanceof FlexibleType)) {
                throw new NoWhenBranchMatchedException();
            }
            FlexibleType flexibleType = (FlexibleType) unwrappedTypeUnwrap;
            SimpleType simpleTypeTransformToNewType = transformToNewType(flexibleType.getLowerBound());
            SimpleType simpleTypeTransformToNewType2 = transformToNewType(flexibleType.getUpperBound());
            simpleTypeFlexibleType = (simpleTypeTransformToNewType == flexibleType.getLowerBound() && simpleTypeTransformToNewType2 == flexibleType.getUpperBound()) ? unwrappedTypeUnwrap : KotlinTypeFactory.flexibleType(simpleTypeTransformToNewType, simpleTypeTransformToNewType2);
        }
        return TypeWithEnhancementKt.inheritEnhancement(simpleTypeFlexibleType, unwrappedTypeUnwrap, new AnonymousClass1(this));
    }

    /* JADX INFO: renamed from: kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypePreparator$prepareType$1, reason: invalid class name */
    /* JADX INFO: compiled from: KotlinTypePreparator.kt */
    /* synthetic */ class AnonymousClass1 extends FunctionReference implements Function1<KotlinTypeMarker, UnwrappedType> {
        AnonymousClass1(Object obj) {
            super(1, obj);
        }

        @Override // kotlin.jvm.internal.CallableReference, kotlin.reflect.KCallable
        public final String getName() {
            return "prepareType";
        }

        @Override // kotlin.jvm.internal.CallableReference
        public final KDeclarationContainer getOwner() {
            return Reflection.getOrCreateKotlinClass(KotlinTypePreparator.class);
        }

        @Override // kotlin.jvm.internal.CallableReference
        public final String getSignature() {
            return "prepareType(Lorg/jetbrains/kotlin/types/model/KotlinTypeMarker;)Lorg/jetbrains/kotlin/types/UnwrappedType;";
        }

        @Override // kotlin.jvm.functions.Function1
        public final UnwrappedType invoke(KotlinTypeMarker p0) {
            Intrinsics.checkNotNullParameter(p0, "p0");
            return ((KotlinTypePreparator) this.receiver).prepareType(p0);
        }
    }

    /* JADX INFO: compiled from: KotlinTypePreparator.kt */
    public static final class Default extends KotlinTypePreparator {
        public static final Default INSTANCE = new Default();

        private Default() {
        }
    }
}
