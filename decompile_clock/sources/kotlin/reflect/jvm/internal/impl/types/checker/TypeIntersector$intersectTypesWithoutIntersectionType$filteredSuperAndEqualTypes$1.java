package kotlin.reflect.jvm.internal.impl.types.checker;

import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: IntersectionType.kt */
/* JADX INFO: loaded from: classes2.dex */
/* synthetic */ class TypeIntersector$intersectTypesWithoutIntersectionType$filteredSuperAndEqualTypes$1 extends FunctionReference implements Function2<KotlinType, KotlinType, Boolean> {
    TypeIntersector$intersectTypesWithoutIntersectionType$filteredSuperAndEqualTypes$1(Object obj) {
        super(2, obj);
    }

    @Override // kotlin.jvm.internal.CallableReference, kotlin.reflect.KCallable
    public final String getName() {
        return "equalTypes";
    }

    @Override // kotlin.jvm.internal.CallableReference
    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(NewKotlinTypeCheckerImpl.class);
    }

    @Override // kotlin.jvm.internal.CallableReference
    public final String getSignature() {
        return "equalTypes(Lorg/jetbrains/kotlin/types/KotlinType;Lorg/jetbrains/kotlin/types/KotlinType;)Z";
    }

    @Override // kotlin.jvm.functions.Function2
    public final Boolean invoke(KotlinType p0, KotlinType p1) {
        Intrinsics.checkNotNullParameter(p0, "p0");
        Intrinsics.checkNotNullParameter(p1, "p1");
        return Boolean.valueOf(((NewKotlinTypeCheckerImpl) this.receiver).equalTypes(p0, p1));
    }
}
