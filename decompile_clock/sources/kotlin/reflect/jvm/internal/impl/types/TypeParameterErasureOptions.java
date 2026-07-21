package kotlin.reflect.jvm.internal.impl.types;

/* JADX INFO: compiled from: TypeParameterErasureOptions.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeParameterErasureOptions {
    private final boolean intersectUpperBounds;
    private final boolean leaveNonTypeParameterTypes;

    public TypeParameterErasureOptions(boolean z, boolean z2) {
        this.leaveNonTypeParameterTypes = z;
        this.intersectUpperBounds = z2;
    }

    public final boolean getLeaveNonTypeParameterTypes() {
        return this.leaveNonTypeParameterTypes;
    }

    public final boolean getIntersectUpperBounds() {
        return this.intersectUpperBounds;
    }
}
