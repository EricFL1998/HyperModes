package kotlin.reflect.jvm.internal.impl.resolve.constants;

import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FindClassInModuleKt;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorTypeKind;
import kotlin.reflect.jvm.internal.impl.types.error.ErrorUtils;

/* JADX INFO: compiled from: constantValues.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class EnumValue extends ConstantValue<Pair<? extends ClassId, ? extends Name>> {
    private final ClassId enumClassId;
    private final Name enumEntryName;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public EnumValue(ClassId enumClassId, Name enumEntryName) {
        super(TuplesKt.to(enumClassId, enumEntryName));
        Intrinsics.checkNotNullParameter(enumClassId, "enumClassId");
        Intrinsics.checkNotNullParameter(enumEntryName, "enumEntryName");
        this.enumClassId = enumClassId;
        this.enumEntryName = enumEntryName;
    }

    public final Name getEnumEntryName() {
        return this.enumEntryName;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue
    public KotlinType getType(ModuleDescriptor module) {
        SimpleType defaultType;
        Intrinsics.checkNotNullParameter(module, "module");
        ClassDescriptor classDescriptorFindClassAcrossModuleDependencies = FindClassInModuleKt.findClassAcrossModuleDependencies(module, this.enumClassId);
        if (classDescriptorFindClassAcrossModuleDependencies != null) {
            if (!DescriptorUtils.isEnumClass(classDescriptorFindClassAcrossModuleDependencies)) {
                classDescriptorFindClassAcrossModuleDependencies = null;
            }
            if (classDescriptorFindClassAcrossModuleDependencies != null && (defaultType = classDescriptorFindClassAcrossModuleDependencies.getDefaultType()) != null) {
                return defaultType;
            }
        }
        return ErrorUtils.createErrorType(ErrorTypeKind.ERROR_ENUM_TYPE, this.enumClassId.toString(), this.enumEntryName.toString());
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue
    public String toString() {
        return new StringBuilder().append(this.enumClassId.getShortClassName()).append('.').append(this.enumEntryName).toString();
    }
}
