package kotlin.reflect.jvm.internal.impl.descriptors.java;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibilities;
import kotlin.reflect.jvm.internal.impl.descriptors.Visibility;

/* JADX INFO: compiled from: JavaVisibilities.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaVisibilities {
    public static final JavaVisibilities INSTANCE = new JavaVisibilities();

    /* JADX INFO: compiled from: JavaVisibilities.kt */
    public static final class PackageVisibility extends Visibility {
        public static final PackageVisibility INSTANCE = new PackageVisibility();

        private PackageVisibility() {
            super("package", false);
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public Integer compareTo(Visibility visibility) {
            Intrinsics.checkNotNullParameter(visibility, "visibility");
            if (this == visibility) {
                return 0;
            }
            return Visibilities.INSTANCE.isPrivate(visibility) ? 1 : -1;
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public Visibility normalize() {
            return Visibilities.Protected.INSTANCE;
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public String getInternalDisplayName() {
            return "public/*package*/";
        }
    }

    private JavaVisibilities() {
    }

    /* JADX INFO: compiled from: JavaVisibilities.kt */
    public static final class ProtectedStaticVisibility extends Visibility {
        public static final ProtectedStaticVisibility INSTANCE = new ProtectedStaticVisibility();

        private ProtectedStaticVisibility() {
            super("protected_static", true);
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public Visibility normalize() {
            return Visibilities.Protected.INSTANCE;
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public String getInternalDisplayName() {
            return "protected/*protected static*/";
        }
    }

    /* JADX INFO: compiled from: JavaVisibilities.kt */
    public static final class ProtectedAndPackage extends Visibility {
        public static final ProtectedAndPackage INSTANCE = new ProtectedAndPackage();

        private ProtectedAndPackage() {
            super("protected_and_package", true);
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public Integer compareTo(Visibility visibility) {
            Intrinsics.checkNotNullParameter(visibility, "visibility");
            if (Intrinsics.areEqual(this, visibility)) {
                return 0;
            }
            if (visibility == Visibilities.Internal.INSTANCE) {
                return null;
            }
            return Integer.valueOf(Visibilities.INSTANCE.isPrivate(visibility) ? 1 : -1);
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public Visibility normalize() {
            return Visibilities.Protected.INSTANCE;
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.Visibility
        public String getInternalDisplayName() {
            return "protected/*protected and package*/";
        }
    }
}
