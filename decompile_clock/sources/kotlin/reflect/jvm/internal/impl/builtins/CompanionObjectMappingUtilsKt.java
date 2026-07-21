package kotlin.reflect.jvm.internal.impl.builtins;

import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorUtils;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;

/* JADX INFO: compiled from: CompanionObjectMappingUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class CompanionObjectMappingUtilsKt {
    public static final boolean isMappedIntrinsicCompanionObject(CompanionObjectMapping companionObjectMapping, ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(companionObjectMapping, "<this>");
        Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
        if (DescriptorUtils.isCompanionObject(classDescriptor)) {
            Set<ClassId> classIds = companionObjectMapping.getClassIds();
            ClassId classId = DescriptorUtilsKt.getClassId(classDescriptor);
            if (CollectionsKt.contains(classIds, classId != null ? classId.getOuterClassId() : null)) {
                return true;
            }
        }
        return false;
    }
}
