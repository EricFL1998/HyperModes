package kotlin.reflect.jvm.internal.impl.resolve;

import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;

/* JADX INFO: compiled from: ResolutionAnchorProvider.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ResolutionAnchorProvider {
    ModuleDescriptor getResolutionAnchor(ModuleDescriptor moduleDescriptor);
}
