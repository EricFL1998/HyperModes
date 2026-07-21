package kotlin.reflect.jvm.internal.impl.types.model;

import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.Variance;

/* JADX INFO: compiled from: TypeSystemContext.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeSystemContextKt {

    /* JADX INFO: compiled from: TypeSystemContext.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Variance.values().length];
            try {
                iArr[Variance.INVARIANT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Variance.IN_VARIANCE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[Variance.OUT_VARIANCE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public static final TypeVariance convertVariance(Variance variance) {
        Intrinsics.checkNotNullParameter(variance, "<this>");
        int i = WhenMappings.$EnumSwitchMapping$0[variance.ordinal()];
        if (i == 1) {
            return TypeVariance.INV;
        }
        if (i == 2) {
            return TypeVariance.IN;
        }
        if (i != 3) {
            throw new NoWhenBranchMatchedException();
        }
        return TypeVariance.OUT;
    }
}
