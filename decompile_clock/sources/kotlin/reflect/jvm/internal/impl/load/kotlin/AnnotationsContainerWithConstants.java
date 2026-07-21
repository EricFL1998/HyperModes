package kotlin.reflect.jvm.internal.impl.load.kotlin;

import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: AbstractBinaryClassAnnotationLoader.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AnnotationsContainerWithConstants<A, C> extends AbstractBinaryClassAnnotationLoader.AnnotationsContainer<A> {
    private final Map<MemberSignature, C> annotationParametersDefaultValues;
    private final Map<MemberSignature, List<A>> memberAnnotations;
    private final Map<MemberSignature, C> propertyConstants;

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.AbstractBinaryClassAnnotationLoader.AnnotationsContainer
    public Map<MemberSignature, List<A>> getMemberAnnotations() {
        return this.memberAnnotations;
    }

    public final Map<MemberSignature, C> getPropertyConstants() {
        return this.propertyConstants;
    }

    public final Map<MemberSignature, C> getAnnotationParametersDefaultValues() {
        return this.annotationParametersDefaultValues;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public AnnotationsContainerWithConstants(Map<MemberSignature, ? extends List<? extends A>> memberAnnotations, Map<MemberSignature, ? extends C> propertyConstants, Map<MemberSignature, ? extends C> annotationParametersDefaultValues) {
        Intrinsics.checkNotNullParameter(memberAnnotations, "memberAnnotations");
        Intrinsics.checkNotNullParameter(propertyConstants, "propertyConstants");
        Intrinsics.checkNotNullParameter(annotationParametersDefaultValues, "annotationParametersDefaultValues");
        this.memberAnnotations = memberAnnotations;
        this.propertyConstants = propertyConstants;
        this.annotationParametersDefaultValues = annotationParametersDefaultValues;
    }
}
