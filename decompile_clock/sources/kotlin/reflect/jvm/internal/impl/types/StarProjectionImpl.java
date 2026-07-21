package kotlin.reflect.jvm.internal.impl.types;

import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;

/* JADX INFO: compiled from: StarProjectionImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class StarProjectionImpl extends TypeProjectionBase {
    private final Lazy _type$delegate;
    private final TypeParameterDescriptor typeParameter;

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeProjection
    public boolean isStarProjection() {
        return true;
    }

    public StarProjectionImpl(TypeParameterDescriptor typeParameter) {
        Intrinsics.checkNotNullParameter(typeParameter, "typeParameter");
        this.typeParameter = typeParameter;
        this._type$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.types.StarProjectionImpl$$Lambda$0
            private final StarProjectionImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return StarProjectionImpl._type_delegate$lambda$0(this.arg$0);
            }
        });
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeProjection
    public Variance getProjectionKind() {
        return Variance.OUT_VARIANCE;
    }

    private final KotlinType get_type() {
        return (KotlinType) this._type$delegate.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final KotlinType _type_delegate$lambda$0(StarProjectionImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return StarProjectionImplKt.starProjectionType(this$0.typeParameter);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeProjection
    public KotlinType getType() {
        return get_type();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeProjection
    public TypeProjection refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return this;
    }
}
