package kotlin.reflect.jvm.internal.impl.types.typesApproximation;

import kotlin.jvm.functions.Function1;
import kotlin.reflect.jvm.internal.impl.renderer.DescriptorRendererOptions;

/* JADX INFO: loaded from: classes2.dex */
class CapturedTypeApproximationKt$$Lambda$0 implements Function1 {
    public static final CapturedTypeApproximationKt$$Lambda$0 INSTANCE = new CapturedTypeApproximationKt$$Lambda$0();

    @Override // kotlin.jvm.functions.Function1
    public Object invoke(Object obj) {
        return CapturedTypeApproximationKt.toTypeProjection$lambda$1$lambda$0((DescriptorRendererOptions) obj);
    }
}
