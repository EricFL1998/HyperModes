package kotlin.reflect.jvm.internal.impl.types;

import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;

/* JADX INFO: compiled from: KotlinType.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class UnwrappedType extends KotlinType {
    public /* synthetic */ UnwrappedType(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    public abstract UnwrappedType makeNullableAsSpecified(boolean z);

    @Override // kotlin.reflect.jvm.internal.impl.types.KotlinType
    public abstract UnwrappedType refine(KotlinTypeRefiner kotlinTypeRefiner);

    public abstract UnwrappedType replaceAttributes(TypeAttributes typeAttributes);

    @Override // kotlin.reflect.jvm.internal.impl.types.KotlinType
    public final UnwrappedType unwrap() {
        return this;
    }

    private UnwrappedType() {
        super(null);
    }
}
