package kotlin.reflect.jvm.internal.impl.load.java;

import kotlin.collections.MapsKt;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: JavaNullabilityAnnotationSettings.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface NullabilityAnnotationStates<T> {
    public static final Companion Companion = Companion.$$INSTANCE;

    T get(FqName fqName);

    /* JADX INFO: compiled from: JavaNullabilityAnnotationSettings.kt */
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();
        private static final NullabilityAnnotationStates EMPTY = new NullabilityAnnotationStatesImpl(MapsKt.emptyMap());

        private Companion() {
        }

        public final NullabilityAnnotationStates getEMPTY() {
            return EMPTY;
        }
    }
}
