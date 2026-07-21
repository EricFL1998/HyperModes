package kotlin.reflect.jvm.internal.impl.builtins.jvm;

import java.util.Collection;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;

/* JADX INFO: compiled from: JavaToKotlinClassMapper.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaToKotlinClassMapper {
    public static final JavaToKotlinClassMapper INSTANCE = new JavaToKotlinClassMapper();

    private JavaToKotlinClassMapper() {
    }

    public final Collection<ClassDescriptor> mapPlatformClass(FqName fqName, KotlinBuiltIns builtIns) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(builtIns, "builtIns");
        ClassDescriptor classDescriptorMapJavaToKotlin$default = mapJavaToKotlin$default(this, fqName, builtIns, null, 4, null);
        if (classDescriptorMapJavaToKotlin$default == null) {
            return SetsKt.emptySet();
        }
        FqName onlyToMutable = JavaToKotlinClassMap.INSTANCE.readOnlyToMutable(DescriptorUtilsKt.getFqNameUnsafe(classDescriptorMapJavaToKotlin$default));
        return onlyToMutable == null ? SetsKt.setOf(classDescriptorMapJavaToKotlin$default) : CollectionsKt.listOf((Object[]) new ClassDescriptor[]{classDescriptorMapJavaToKotlin$default, builtIns.getBuiltInClassByFqName(onlyToMutable)});
    }

    public static /* synthetic */ ClassDescriptor mapJavaToKotlin$default(JavaToKotlinClassMapper javaToKotlinClassMapper, FqName fqName, KotlinBuiltIns kotlinBuiltIns, Integer num, int i, Object obj) {
        if ((i & 4) != 0) {
            num = null;
        }
        return javaToKotlinClassMapper.mapJavaToKotlin(fqName, kotlinBuiltIns, num);
    }

    public final ClassDescriptor mapJavaToKotlin(FqName fqName, KotlinBuiltIns builtIns, Integer num) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(builtIns, "builtIns");
        ClassId classIdMapJavaToKotlin = (num == null || !Intrinsics.areEqual(fqName, JavaToKotlinClassMap.INSTANCE.getFUNCTION_N_FQ_NAME())) ? JavaToKotlinClassMap.INSTANCE.mapJavaToKotlin(fqName) : StandardNames.getFunctionClassId(num.intValue());
        if (classIdMapJavaToKotlin != null) {
            return builtIns.getBuiltInClassByFqName(classIdMapJavaToKotlin.asSingleFqName());
        }
        return null;
    }

    public final boolean isMutable(ClassDescriptor mutable) {
        Intrinsics.checkNotNullParameter(mutable, "mutable");
        return JavaToKotlinClassMap.INSTANCE.isMutable(DescriptorUtils.getFqName(mutable));
    }

    public final boolean isReadOnly(ClassDescriptor readOnly) {
        Intrinsics.checkNotNullParameter(readOnly, "readOnly");
        return JavaToKotlinClassMap.INSTANCE.isReadOnly(DescriptorUtils.getFqName(readOnly));
    }

    public final ClassDescriptor convertMutableToReadOnly(ClassDescriptor mutable) {
        Intrinsics.checkNotNullParameter(mutable, "mutable");
        ClassDescriptor classDescriptor = mutable;
        FqName fqNameMutableToReadOnly = JavaToKotlinClassMap.INSTANCE.mutableToReadOnly(DescriptorUtils.getFqName(classDescriptor));
        if (fqNameMutableToReadOnly == null) {
            throw new IllegalArgumentException("Given class " + mutable + " is not a mutable collection");
        }
        ClassDescriptor builtInClassByFqName = DescriptorUtilsKt.getBuiltIns(classDescriptor).getBuiltInClassByFqName(fqNameMutableToReadOnly);
        Intrinsics.checkNotNullExpressionValue(builtInClassByFqName, "getBuiltInClassByFqName(...)");
        return builtInClassByFqName;
    }

    public final ClassDescriptor convertReadOnlyToMutable(ClassDescriptor readOnly) {
        Intrinsics.checkNotNullParameter(readOnly, "readOnly");
        ClassDescriptor classDescriptor = readOnly;
        FqName onlyToMutable = JavaToKotlinClassMap.INSTANCE.readOnlyToMutable(DescriptorUtils.getFqName(classDescriptor));
        if (onlyToMutable == null) {
            throw new IllegalArgumentException("Given class " + readOnly + " is not a read-only collection");
        }
        ClassDescriptor builtInClassByFqName = DescriptorUtilsKt.getBuiltIns(classDescriptor).getBuiltInClassByFqName(onlyToMutable);
        Intrinsics.checkNotNullExpressionValue(builtInClassByFqName, "getBuiltInClassByFqName(...)");
        return builtInClassByFqName;
    }
}
