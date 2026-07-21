package kotlin.reflect.jvm.internal.impl.load.kotlin;

import java.util.Collection;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: methodSignatureMapping.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeMappingConfigurationImpl implements TypeMappingConfiguration<JvmType> {
    public static final TypeMappingConfigurationImpl INSTANCE = new TypeMappingConfigurationImpl();

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.TypeMappingConfiguration
    public String getPredefinedFullInternalNameForClass(ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.TypeMappingConfiguration
    public String getPredefinedInternalNameForClass(ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.TypeMappingConfiguration
    public JvmType getPredefinedTypeForClass(ClassDescriptor classDescriptor) {
        Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.TypeMappingConfiguration
    public KotlinType preprocessType(KotlinType kotlinType) {
        Intrinsics.checkNotNullParameter(kotlinType, "kotlinType");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.TypeMappingConfiguration
    public void processErrorType(KotlinType kotlinType, ClassDescriptor descriptor) {
        Intrinsics.checkNotNullParameter(kotlinType, "kotlinType");
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
    }

    private TypeMappingConfigurationImpl() {
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.kotlin.TypeMappingConfiguration
    public KotlinType commonSupertype(Collection<? extends KotlinType> types) {
        Intrinsics.checkNotNullParameter(types, "types");
        throw new AssertionError("There should be no intersection type in existing descriptors, but found: " + CollectionsKt.joinToString$default(types, null, null, null, 0, null, null, 63, null));
    }
}
