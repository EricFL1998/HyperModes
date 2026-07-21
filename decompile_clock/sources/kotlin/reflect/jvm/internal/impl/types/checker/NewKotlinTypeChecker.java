package kotlin.reflect.jvm.internal.impl.types.checker;

import kotlin.reflect.jvm.internal.impl.resolve.OverridingUtil;

/* JADX INFO: compiled from: NewKotlinTypeChecker.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface NewKotlinTypeChecker extends KotlinTypeChecker {
    public static final Companion Companion = Companion.$$INSTANCE;

    KotlinTypeRefiner getKotlinTypeRefiner();

    OverridingUtil getOverridingUtil();

    /* JADX INFO: compiled from: NewKotlinTypeChecker.kt */
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();
        private static final NewKotlinTypeCheckerImpl Default = new NewKotlinTypeCheckerImpl(KotlinTypeRefiner.Default.INSTANCE, null, 2, 0 == true ? 1 : 0);

        private Companion() {
        }

        public final NewKotlinTypeCheckerImpl getDefault() {
            return Default;
        }
    }
}
