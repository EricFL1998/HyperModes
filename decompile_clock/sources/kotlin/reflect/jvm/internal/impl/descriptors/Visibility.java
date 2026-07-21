package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Visibility.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class Visibility {
    private final boolean isPublicAPI;
    private final String name;

    public Visibility normalize() {
        return this;
    }

    protected Visibility(String name, boolean z) {
        Intrinsics.checkNotNullParameter(name, "name");
        this.name = name;
        this.isPublicAPI = z;
    }

    public final boolean isPublicAPI() {
        return this.isPublicAPI;
    }

    public String getInternalDisplayName() {
        return this.name;
    }

    public Integer compareTo(Visibility visibility) {
        Intrinsics.checkNotNullParameter(visibility, "visibility");
        return Visibilities.INSTANCE.compareLocal$compiler_common(this, visibility);
    }

    public final String toString() {
        return getInternalDisplayName();
    }
}
