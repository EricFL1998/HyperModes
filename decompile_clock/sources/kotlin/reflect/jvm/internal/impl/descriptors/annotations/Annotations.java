package kotlin.reflect.jvm.internal.impl.descriptors.annotations;

import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.markers.KMappedMarker;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: Annotations.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface Annotations extends Iterable<AnnotationDescriptor>, KMappedMarker {
    public static final Companion Companion = Companion.$$INSTANCE;

    /* JADX INFO: renamed from: findAnnotation */
    AnnotationDescriptor mo1486findAnnotation(FqName fqName);

    boolean hasAnnotation(FqName fqName);

    boolean isEmpty();

    /* JADX INFO: compiled from: Annotations.kt */
    public static final class DefaultImpls {
        public static AnnotationDescriptor findAnnotation(Annotations annotations, FqName fqName) {
            AnnotationDescriptor next;
            Intrinsics.checkNotNullParameter(fqName, "fqName");
            Iterator<AnnotationDescriptor> it = annotations.iterator();
            while (it.hasNext()) {
                next = it.next();
                if (Intrinsics.areEqual(next.getFqName(), fqName)) {
                    return next;
                }
            }
            next = null;
            return next;
        }

        public static boolean hasAnnotation(Annotations annotations, FqName fqName) {
            Intrinsics.checkNotNullParameter(fqName, "fqName");
            return annotations.mo1486findAnnotation(fqName) != null;
        }
    }

    /* JADX INFO: compiled from: Annotations.kt */
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();
        private static final Annotations EMPTY = new Annotations() { // from class: kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations$Companion$EMPTY$1
            public Void findAnnotation(FqName fqName) {
                Intrinsics.checkNotNullParameter(fqName, "fqName");
                return null;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
            public boolean isEmpty() {
                return true;
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
            /* JADX INFO: renamed from: findAnnotation, reason: collision with other method in class */
            public /* bridge */ /* synthetic */ AnnotationDescriptor mo1486findAnnotation(FqName fqName) {
                return (AnnotationDescriptor) findAnnotation(fqName);
            }

            @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations
            public boolean hasAnnotation(FqName fqName) {
                return Annotations.DefaultImpls.hasAnnotation(this, fqName);
            }

            @Override // java.lang.Iterable
            public Iterator<AnnotationDescriptor> iterator() {
                return CollectionsKt.emptyList().iterator();
            }

            public String toString() {
                return "EMPTY";
            }
        };

        private Companion() {
        }

        public final Annotations getEMPTY() {
            return EMPTY;
        }

        public final Annotations create(List<? extends AnnotationDescriptor> annotations) {
            Intrinsics.checkNotNullParameter(annotations, "annotations");
            return annotations.isEmpty() ? EMPTY : new AnnotationsImpl(annotations);
        }
    }
}
