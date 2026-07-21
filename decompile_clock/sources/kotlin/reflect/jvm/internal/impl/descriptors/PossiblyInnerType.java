package kotlin.reflect.jvm.internal.impl.descriptors;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;

/* JADX INFO: compiled from: typeParameterUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class PossiblyInnerType {
    private final List<TypeProjection> arguments;
    private final ClassifierDescriptorWithTypeParameters classifierDescriptor;
    private final PossiblyInnerType outerType;

    /* JADX WARN: Multi-variable type inference failed */
    public PossiblyInnerType(ClassifierDescriptorWithTypeParameters classifierDescriptor, List<? extends TypeProjection> arguments, PossiblyInnerType possiblyInnerType) {
        Intrinsics.checkNotNullParameter(classifierDescriptor, "classifierDescriptor");
        Intrinsics.checkNotNullParameter(arguments, "arguments");
        this.classifierDescriptor = classifierDescriptor;
        this.arguments = arguments;
        this.outerType = possiblyInnerType;
    }

    public final ClassifierDescriptorWithTypeParameters getClassifierDescriptor() {
        return this.classifierDescriptor;
    }

    public final List<TypeProjection> getArguments() {
        return this.arguments;
    }

    public final PossiblyInnerType getOuterType() {
        return this.outerType;
    }
}
