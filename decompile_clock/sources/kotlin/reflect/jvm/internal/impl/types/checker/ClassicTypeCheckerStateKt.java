package kotlin.reflect.jvm.internal.impl.types.checker;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.TypeCheckerState;

/* JADX INFO: compiled from: ClassicTypeCheckerState.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ClassicTypeCheckerStateKt {
    public static /* synthetic */ TypeCheckerState createClassicTypeCheckerState$default(boolean z, boolean z2, ClassicTypeSystemContext classicTypeSystemContext, KotlinTypePreparator kotlinTypePreparator, KotlinTypeRefiner kotlinTypeRefiner, int i, Object obj) {
        if ((i & 2) != 0) {
            z2 = true;
        }
        if ((i & 4) != 0) {
            classicTypeSystemContext = SimpleClassicTypeSystemContext.INSTANCE;
        }
        if ((i & 8) != 0) {
            kotlinTypePreparator = KotlinTypePreparator.Default.INSTANCE;
        }
        if ((i & 16) != 0) {
            kotlinTypeRefiner = KotlinTypeRefiner.Default.INSTANCE;
        }
        return createClassicTypeCheckerState(z, z2, classicTypeSystemContext, kotlinTypePreparator, kotlinTypeRefiner);
    }

    public static final TypeCheckerState createClassicTypeCheckerState(boolean z, boolean z2, ClassicTypeSystemContext typeSystemContext, KotlinTypePreparator kotlinTypePreparator, KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(typeSystemContext, "typeSystemContext");
        Intrinsics.checkNotNullParameter(kotlinTypePreparator, "kotlinTypePreparator");
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return new TypeCheckerState(z, z2, true, typeSystemContext, kotlinTypePreparator, kotlinTypeRefiner);
    }
}
