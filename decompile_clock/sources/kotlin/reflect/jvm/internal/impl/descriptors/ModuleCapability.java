package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ModuleCapability.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ModuleCapability<T> {
    private final String name;

    public ModuleCapability(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        this.name = name;
    }

    public String toString() {
        return this.name;
    }
}
