package kotlin.reflect.jvm.internal.impl.load.java.lazy.types;

import java.util.Set;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.TypeUsage;

/* JADX INFO: compiled from: JavaTypeAttributes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaTypeAttributes extends ErasureTypeAttributes {
    private final SimpleType defaultType;
    private final JavaTypeFlexibility flexibility;
    private final TypeUsage howThisTypeIsUsed;
    private final boolean isForAnnotationParameter;
    private final boolean isRaw;
    private final Set<TypeParameterDescriptor> visitedTypeParameters;

    /* JADX WARN: Multi-variable type inference failed */
    public static /* synthetic */ JavaTypeAttributes copy$default(JavaTypeAttributes javaTypeAttributes, TypeUsage typeUsage, JavaTypeFlexibility javaTypeFlexibility, boolean z, boolean z2, Set set, SimpleType simpleType, int i, Object obj) {
        if ((i & 1) != 0) {
            typeUsage = javaTypeAttributes.howThisTypeIsUsed;
        }
        if ((i & 2) != 0) {
            javaTypeFlexibility = javaTypeAttributes.flexibility;
        }
        JavaTypeFlexibility javaTypeFlexibility2 = javaTypeFlexibility;
        if ((i & 4) != 0) {
            z = javaTypeAttributes.isRaw;
        }
        boolean z3 = z;
        if ((i & 8) != 0) {
            z2 = javaTypeAttributes.isForAnnotationParameter;
        }
        boolean z4 = z2;
        if ((i & 16) != 0) {
            set = javaTypeAttributes.visitedTypeParameters;
        }
        Set set2 = set;
        if ((i & 32) != 0) {
            simpleType = javaTypeAttributes.defaultType;
        }
        return javaTypeAttributes.copy(typeUsage, javaTypeFlexibility2, z3, z4, set2, simpleType);
    }

    public final JavaTypeAttributes copy(TypeUsage howThisTypeIsUsed, JavaTypeFlexibility flexibility, boolean z, boolean z2, Set<? extends TypeParameterDescriptor> set, SimpleType simpleType) {
        Intrinsics.checkNotNullParameter(howThisTypeIsUsed, "howThisTypeIsUsed");
        Intrinsics.checkNotNullParameter(flexibility, "flexibility");
        return new JavaTypeAttributes(howThisTypeIsUsed, flexibility, z, z2, set, simpleType);
    }

    public String toString() {
        return "JavaTypeAttributes(howThisTypeIsUsed=" + this.howThisTypeIsUsed + ", flexibility=" + this.flexibility + ", isRaw=" + this.isRaw + ", isForAnnotationParameter=" + this.isForAnnotationParameter + ", visitedTypeParameters=" + this.visitedTypeParameters + ", defaultType=" + this.defaultType + ')';
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes
    public TypeUsage getHowThisTypeIsUsed() {
        return this.howThisTypeIsUsed;
    }

    public /* synthetic */ JavaTypeAttributes(TypeUsage typeUsage, JavaTypeFlexibility javaTypeFlexibility, boolean z, boolean z2, Set set, SimpleType simpleType, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(typeUsage, (i & 2) != 0 ? JavaTypeFlexibility.INFLEXIBLE : javaTypeFlexibility, (i & 4) != 0 ? false : z, (i & 8) != 0 ? false : z2, (i & 16) != 0 ? null : set, (i & 32) != 0 ? null : simpleType);
    }

    public final JavaTypeFlexibility getFlexibility() {
        return this.flexibility;
    }

    public final boolean isRaw() {
        return this.isRaw;
    }

    public final boolean isForAnnotationParameter() {
        return this.isForAnnotationParameter;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes
    public Set<TypeParameterDescriptor> getVisitedTypeParameters() {
        return this.visitedTypeParameters;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes
    public SimpleType getDefaultType() {
        return this.defaultType;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    /* JADX WARN: Multi-variable type inference failed */
    public JavaTypeAttributes(TypeUsage howThisTypeIsUsed, JavaTypeFlexibility flexibility, boolean z, boolean z2, Set<? extends TypeParameterDescriptor> set, SimpleType simpleType) {
        super(howThisTypeIsUsed, set, simpleType);
        Intrinsics.checkNotNullParameter(howThisTypeIsUsed, "howThisTypeIsUsed");
        Intrinsics.checkNotNullParameter(flexibility, "flexibility");
        this.howThisTypeIsUsed = howThisTypeIsUsed;
        this.flexibility = flexibility;
        this.isRaw = z;
        this.isForAnnotationParameter = z2;
        this.visitedTypeParameters = set;
        this.defaultType = simpleType;
    }

    public final JavaTypeAttributes withFlexibility(JavaTypeFlexibility flexibility) {
        Intrinsics.checkNotNullParameter(flexibility, "flexibility");
        return copy$default(this, null, flexibility, false, false, null, null, 61, null);
    }

    public final JavaTypeAttributes markIsRaw(boolean z) {
        return copy$default(this, null, null, z, false, null, null, 59, null);
    }

    public JavaTypeAttributes withDefaultType(SimpleType simpleType) {
        return copy$default(this, null, null, false, false, null, simpleType, 31, null);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes
    public JavaTypeAttributes withNewVisitedTypeParameter(TypeParameterDescriptor typeParameter) {
        Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
        return copy$default(this, null, null, false, false, getVisitedTypeParameters() != null ? SetsKt.plus(getVisitedTypeParameters(), typeParameter) : SetsKt.setOf(typeParameter), null, 47, null);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes
    public boolean equals(Object obj) {
        if (!(obj instanceof JavaTypeAttributes)) {
            return false;
        }
        JavaTypeAttributes javaTypeAttributes = (JavaTypeAttributes) obj;
        return Intrinsics.areEqual(javaTypeAttributes.getDefaultType(), getDefaultType()) && javaTypeAttributes.getHowThisTypeIsUsed() == getHowThisTypeIsUsed() && javaTypeAttributes.flexibility == this.flexibility && javaTypeAttributes.isRaw == this.isRaw && javaTypeAttributes.isForAnnotationParameter == this.isForAnnotationParameter;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.ErasureTypeAttributes
    public int hashCode() {
        SimpleType defaultType = getDefaultType();
        int iHashCode = defaultType != null ? defaultType.hashCode() : 0;
        int iHashCode2 = iHashCode + (iHashCode * 31) + getHowThisTypeIsUsed().hashCode();
        int iHashCode3 = iHashCode2 + (iHashCode2 * 31) + this.flexibility.hashCode();
        int i = iHashCode3 + (iHashCode3 * 31) + (this.isRaw ? 1 : 0);
        return i + (i * 31) + (this.isForAnnotationParameter ? 1 : 0);
    }
}
