package kotlin.reflect.jvm.internal.impl.resolve;

import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;

/* JADX INFO: compiled from: StdlibClassFinder.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface StdlibClassFinder {
    ClassDescriptor findEnumEntriesClass(ModuleDescriptor moduleDescriptor);
}
