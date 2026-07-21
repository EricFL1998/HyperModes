package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: InvalidModuleException.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class InvalidModuleExceptionKt {
    private static final ModuleCapability<InvalidModuleNotifier> INVALID_MODULE_NOTIFIER_CAPABILITY = new ModuleCapability<>("InvalidModuleNotifier");

    public static final void moduleInvalidated(ModuleDescriptor moduleDescriptor) {
        Intrinsics.checkNotNullParameter(moduleDescriptor, "<this>");
        InvalidModuleNotifier invalidModuleNotifier = (InvalidModuleNotifier) moduleDescriptor.getCapability(INVALID_MODULE_NOTIFIER_CAPABILITY);
        if (invalidModuleNotifier != null) {
            invalidModuleNotifier.notifyModuleInvalidated(moduleDescriptor);
            return;
        }
        throw new InvalidModuleException("Accessing invalid module descriptor " + moduleDescriptor);
    }
}
