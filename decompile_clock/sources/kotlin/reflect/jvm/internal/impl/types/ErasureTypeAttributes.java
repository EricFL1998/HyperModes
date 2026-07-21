package kotlin.reflect.jvm.internal.impl.types;

import java.util.Set;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;

/* JADX INFO: compiled from: ErasureTypeAttributes.kt */
/* JADX INFO: loaded from: classes2.dex */
public class ErasureTypeAttributes {
    private final SimpleType defaultType;
    private final TypeUsage howThisTypeIsUsed;
    private final Set<TypeParameterDescriptor> visitedTypeParameters;

    /* JADX WARN: Multi-variable type inference failed */
    public ErasureTypeAttributes(TypeUsage howThisTypeIsUsed, Set<? extends TypeParameterDescriptor> set, SimpleType simpleType) {
        Intrinsics.checkNotNullParameter(howThisTypeIsUsed, "howThisTypeIsUsed");
        this.howThisTypeIsUsed = howThisTypeIsUsed;
        this.visitedTypeParameters = set;
        this.defaultType = simpleType;
    }

    public TypeUsage getHowThisTypeIsUsed() {
        return this.howThisTypeIsUsed;
    }

    public Set<TypeParameterDescriptor> getVisitedTypeParameters() {
        return this.visitedTypeParameters;
    }

    public SimpleType getDefaultType() {
        return this.defaultType;
    }

    public ErasureTypeAttributes withNewVisitedTypeParameter(TypeParameterDescriptor typeParameter) {
        Set of;
        Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
        TypeUsage howThisTypeIsUsed = getHowThisTypeIsUsed();
        Set<TypeParameterDescriptor> visitedTypeParameters = getVisitedTypeParameters();
        if (visitedTypeParameters == null || (of = SetsKt.plus(visitedTypeParameters, typeParameter)) == null) {
            of = SetsKt.setOf(typeParameter);
        }
        return new ErasureTypeAttributes(howThisTypeIsUsed, of, getDefaultType());
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ErasureTypeAttributes)) {
            return false;
        }
        ErasureTypeAttributes erasureTypeAttributes = (ErasureTypeAttributes) obj;
        return Intrinsics.areEqual(erasureTypeAttributes.getDefaultType(), getDefaultType()) && erasureTypeAttributes.getHowThisTypeIsUsed() == getHowThisTypeIsUsed();
    }

    public int hashCode() {
        SimpleType defaultType = getDefaultType();
        int iHashCode = defaultType != null ? defaultType.hashCode() : 0;
        return iHashCode + (iHashCode * 31) + getHowThisTypeIsUsed().hashCode();
    }
}
