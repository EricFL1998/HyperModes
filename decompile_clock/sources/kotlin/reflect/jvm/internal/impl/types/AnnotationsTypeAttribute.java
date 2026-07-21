package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationsKt;

/* JADX INFO: compiled from: AnnotationsTypeAttribute.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AnnotationsTypeAttribute extends TypeAttribute<AnnotationsTypeAttribute> {
    private final Annotations annotations;

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeAttribute
    public KClass<? extends AnnotationsTypeAttribute> getKey() {
        return Reflection.getOrCreateKotlinClass(AnnotationsTypeAttribute.class);
    }

    public AnnotationsTypeAttribute(Annotations annotations) {
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        this.annotations = annotations;
    }

    public final Annotations getAnnotations() {
        return this.annotations;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeAttribute
    public AnnotationsTypeAttribute intersect(AnnotationsTypeAttribute annotationsTypeAttribute) {
        if (Intrinsics.areEqual(annotationsTypeAttribute, this)) {
            return this;
        }
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeAttribute
    public AnnotationsTypeAttribute add(AnnotationsTypeAttribute annotationsTypeAttribute) {
        return annotationsTypeAttribute == null ? this : new AnnotationsTypeAttribute(AnnotationsKt.composeAnnotations(this.annotations, annotationsTypeAttribute.annotations));
    }

    public boolean equals(Object obj) {
        if (obj instanceof AnnotationsTypeAttribute) {
            return Intrinsics.areEqual(((AnnotationsTypeAttribute) obj).annotations, this.annotations);
        }
        return false;
    }

    public int hashCode() {
        return this.annotations.hashCode();
    }
}
