package kotlin.reflect.jvm.internal.impl.load.java.lazy;

import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass;

/* JADX INFO: compiled from: ModuleClassResolver.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ModuleClassResolver {
    ClassDescriptor resolveClass(JavaClass javaClass);
}
