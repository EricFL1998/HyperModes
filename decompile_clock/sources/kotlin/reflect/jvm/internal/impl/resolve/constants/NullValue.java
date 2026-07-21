package kotlin.reflect.jvm.internal.impl.resolve.constants;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;

/* JADX INFO: compiled from: constantValues.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class NullValue extends ConstantValue<Void> {
    public NullValue() {
        super(null);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue
    public SimpleType getType(ModuleDescriptor module) {
        Intrinsics.checkNotNullParameter(module, "module");
        SimpleType nullableNothingType = module.getBuiltIns().getNullableNothingType();
        Intrinsics.checkNotNullExpressionValue(nullableNothingType, "getNullableNothingType(...)");
        return nullableNothingType;
    }
}
