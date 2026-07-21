package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;

/* JADX INFO: compiled from: ErasureProjectionComputer.kt */
/* JADX INFO: loaded from: classes2.dex */
public class ErasureProjectionComputer {
    public static /* synthetic */ TypeProjection computeProjection$default(ErasureProjectionComputer erasureProjectionComputer, TypeParameterDescriptor typeParameterDescriptor, ErasureTypeAttributes erasureTypeAttributes, TypeParameterUpperBoundEraser typeParameterUpperBoundEraser, KotlinType kotlinType, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: computeProjection");
        }
        if ((i & 8) != 0) {
            kotlinType = typeParameterUpperBoundEraser.getErasedUpperBound(typeParameterDescriptor, erasureTypeAttributes);
        }
        return erasureProjectionComputer.computeProjection(typeParameterDescriptor, erasureTypeAttributes, typeParameterUpperBoundEraser, kotlinType);
    }

    public TypeProjection computeProjection(TypeParameterDescriptor parameter, ErasureTypeAttributes typeAttr, TypeParameterUpperBoundEraser typeParameterUpperBoundEraser, KotlinType erasedUpperBound) {
        Intrinsics.checkNotNullParameter(parameter, "parameter");
        Intrinsics.checkNotNullParameter(typeAttr, "typeAttr");
        Intrinsics.checkNotNullParameter(typeParameterUpperBoundEraser, "typeParameterUpperBoundEraser");
        Intrinsics.checkNotNullParameter(erasedUpperBound, "erasedUpperBound");
        return new TypeProjectionImpl(Variance.OUT_VARIANCE, erasedUpperBound);
    }
}
