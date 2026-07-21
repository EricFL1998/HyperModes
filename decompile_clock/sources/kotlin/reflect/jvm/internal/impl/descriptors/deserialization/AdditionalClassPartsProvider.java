package kotlin.reflect.jvm.internal.impl.descriptors.deserialization;

import java.util.Collection;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: AdditionalClassPartsProvider.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface AdditionalClassPartsProvider {
    Collection<ClassConstructorDescriptor> getConstructors(ClassDescriptor classDescriptor);

    Collection<SimpleFunctionDescriptor> getFunctions(Name name, ClassDescriptor classDescriptor);

    Collection<Name> getFunctionsNames(ClassDescriptor classDescriptor);

    Collection<KotlinType> getSupertypes(ClassDescriptor classDescriptor);

    /* JADX INFO: compiled from: AdditionalClassPartsProvider.kt */
    public static final class None implements AdditionalClassPartsProvider {
        public static final None INSTANCE = new None();

        private None() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.deserialization.AdditionalClassPartsProvider
        public Collection<KotlinType> getSupertypes(ClassDescriptor classDescriptor) {
            Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
            return CollectionsKt.emptyList();
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.deserialization.AdditionalClassPartsProvider
        public Collection<SimpleFunctionDescriptor> getFunctions(Name name, ClassDescriptor classDescriptor) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
            return CollectionsKt.emptyList();
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.deserialization.AdditionalClassPartsProvider
        public Collection<Name> getFunctionsNames(ClassDescriptor classDescriptor) {
            Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
            return CollectionsKt.emptyList();
        }

        @Override // kotlin.reflect.jvm.internal.impl.descriptors.deserialization.AdditionalClassPartsProvider
        public Collection<ClassConstructorDescriptor> getConstructors(ClassDescriptor classDescriptor) {
            Intrinsics.checkNotNullParameter(classDescriptor, "classDescriptor");
            return CollectionsKt.emptyList();
        }
    }
}
