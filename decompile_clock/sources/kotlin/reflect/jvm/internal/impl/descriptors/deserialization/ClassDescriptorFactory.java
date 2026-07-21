package kotlin.reflect.jvm.internal.impl.descriptors.deserialization;

import java.util.Collection;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: ClassDescriptorFactory.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ClassDescriptorFactory {
    ClassDescriptor createClass(ClassId classId);

    Collection<ClassDescriptor> getAllContributedClassesIfPossible(FqName fqName);

    boolean shouldCreateClass(FqName fqName, Name name);
}
