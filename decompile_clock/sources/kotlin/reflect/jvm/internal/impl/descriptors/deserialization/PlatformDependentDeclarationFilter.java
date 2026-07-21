package kotlin.reflect.jvm.internal.impl.descriptors.deserialization;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;

/* JADX INFO: compiled from: PlatformDependentDeclarationFilter.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface PlatformDependentDeclarationFilter {
    boolean isFunctionAvailable(ClassDescriptor classDescriptor, SimpleFunctionDescriptor simpleFunctionDescriptor);

    /* JADX INFO: compiled from: PlatformDependentDeclarationFilter.kt */
    public static final class All implements PlatformDependentDeclarationFilter {
        public static final All INSTANCE = new All();

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter
        public boolean isFunctionAvailable(ClassDescriptor classDescriptor, SimpleFunctionDescriptor functionDescriptor) {
            Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
            Intrinsics.checkNotNullParameter(functionDescriptor, "functionDescriptor");
            return true;
        }

        private All() {
        }
    }

    /* JADX INFO: compiled from: PlatformDependentDeclarationFilter.kt */
    public static final class NoPlatformDependent implements PlatformDependentDeclarationFilter {
        public static final NoPlatformDependent INSTANCE = new NoPlatformDependent();

        private NoPlatformDependent() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter
        public boolean isFunctionAvailable(ClassDescriptor classDescriptor, SimpleFunctionDescriptor functionDescriptor) {
            Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
            Intrinsics.checkNotNullParameter(functionDescriptor, "functionDescriptor");
            return !functionDescriptor.getAnnotations().hasAnnotation(PlatformDependentDeclarationFilterKt.getPLATFORM_DEPENDENT_ANNOTATION_FQ_NAME());
        }
    }
}
