package kotlin.reflect.jvm.internal.impl.resolve;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleCapability;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;

/* JADX INFO: compiled from: StdlibClassFinder.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class StdlibClassFinderKt {
    private static final ModuleCapability<StdlibClassFinder> STDLIB_CLASS_FINDER_CAPABILITY = new ModuleCapability<>("StdlibClassFinder");

    public static final StdlibClassFinder getStdlibClassFinder(ModuleDescriptor moduleDescriptor) {
        Intrinsics.checkNotNullParameter(moduleDescriptor, "<this>");
        StdlibClassFinder stdlibClassFinder = (StdlibClassFinder) moduleDescriptor.getCapability(STDLIB_CLASS_FINDER_CAPABILITY);
        return stdlibClassFinder == null ? CliStdlibClassFinderImpl.INSTANCE : stdlibClassFinder;
    }
}
