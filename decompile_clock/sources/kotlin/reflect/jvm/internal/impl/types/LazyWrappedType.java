package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;
import kotlin.reflect.jvm.internal.impl.types.model.KotlinTypeMarker;

/* JADX INFO: compiled from: SpecialTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class LazyWrappedType extends WrappedType {
    private final Function0<KotlinType> computation;
    private final NotNullLazyValue<KotlinType> lazyValue;
    private final StorageManager storageManager;

    /* JADX WARN: Multi-variable type inference failed */
    public LazyWrappedType(StorageManager storageManager, Function0<? extends KotlinType> computation) {
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(computation, "computation");
        this.storageManager = storageManager;
        this.computation = computation;
        this.lazyValue = storageManager.createLazyValue(computation);
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.WrappedType
    protected KotlinType getDelegate() {
        return this.lazyValue.invoke();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.WrappedType
    public boolean isComputed() {
        return this.lazyValue.isComputed();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.KotlinType
    public LazyWrappedType refine(final KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return new LazyWrappedType(this.storageManager, new Function0(kotlinTypeRefiner, this) { // from class: kotlin.reflect.jvm.internal.impl.types.LazyWrappedType$$Lambda$0
            private final KotlinTypeRefiner arg$0;
            private final LazyWrappedType arg$1;

            {
                this.arg$0 = kotlinTypeRefiner;
                this.arg$1 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyWrappedType.refine$lambda$0(this.arg$0, this.arg$1);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final KotlinType refine$lambda$0(KotlinTypeRefiner kotlinTypeRefiner, LazyWrappedType this$0) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "$kotlinTypeRefiner");
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return kotlinTypeRefiner.refineType((KotlinTypeMarker) this$0.computation.invoke());
    }
}
