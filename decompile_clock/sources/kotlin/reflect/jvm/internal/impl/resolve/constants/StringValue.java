package kotlin.reflect.jvm.internal.impl.resolve.constants;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.text.Typography;

/* JADX INFO: compiled from: constantValues.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class StringValue extends ConstantValue<String> {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public StringValue(String value) {
        super(value);
        Intrinsics.checkNotNullParameter(value, "value");
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue
    public SimpleType getType(ModuleDescriptor module) {
        Intrinsics.checkNotNullParameter(module, "module");
        SimpleType stringType = module.getBuiltIns().getStringType();
        Intrinsics.checkNotNullExpressionValue(stringType, "getStringType(...)");
        return stringType;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue
    public String toString() {
        return "\"" + getValue() + Typography.quote;
    }
}
