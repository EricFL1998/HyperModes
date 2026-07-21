package kotlin.reflect.jvm.internal.impl.types.checker;

/* JADX INFO: compiled from: KotlinTypeRefiner.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class TypeRefinementSupport {
    private final boolean isEnabled;

    public final boolean isEnabled() {
        return this.isEnabled;
    }

    /* JADX INFO: compiled from: KotlinTypeRefiner.kt */
    public static final class Enabled extends TypeRefinementSupport {
        private final KotlinTypeRefiner typeRefiner;

        public final KotlinTypeRefiner getTypeRefiner() {
            return this.typeRefiner;
        }
    }
}
