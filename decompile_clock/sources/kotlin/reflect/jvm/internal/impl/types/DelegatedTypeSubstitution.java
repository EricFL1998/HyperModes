package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;

/* JADX INFO: compiled from: TypeSubstitution.kt */
/* JADX INFO: loaded from: classes2.dex */
public class DelegatedTypeSubstitution extends TypeSubstitution {
    private final TypeSubstitution substitution;

    public DelegatedTypeSubstitution(TypeSubstitution substitution) {
        Intrinsics.checkNotNullParameter(substitution, "substitution");
        this.substitution = substitution;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    /* JADX INFO: renamed from: get */
    public TypeProjection mo1496get(KotlinType key) {
        Intrinsics.checkNotNullParameter(key, "key");
        return this.substitution.mo1496get(key);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public KotlinType prepareTopLevelType(KotlinType topLevelType, Variance position) {
        Intrinsics.checkNotNullParameter(topLevelType, "topLevelType");
        Intrinsics.checkNotNullParameter(position, "position");
        return this.substitution.prepareTopLevelType(topLevelType, position);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public boolean isEmpty() {
        return this.substitution.isEmpty();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public boolean approximateCapturedTypes() {
        return this.substitution.approximateCapturedTypes();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public boolean approximateContravariantCapturedTypes() {
        return this.substitution.approximateContravariantCapturedTypes();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeSubstitution
    public Annotations filterAnnotations(Annotations annotations) {
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        return this.substitution.filterAnnotations(annotations);
    }
}
