package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ReceiverValue;

/* JADX INFO: compiled from: DescriptorVisibility.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class DescriptorVisibility {
    public abstract Visibility getDelegate();

    public abstract String getInternalDisplayName();

    public abstract boolean isVisible(ReceiverValue receiverValue, DeclarationDescriptorWithVisibility declarationDescriptorWithVisibility, DeclarationDescriptor declarationDescriptor, boolean z);

    public abstract DescriptorVisibility normalize();

    protected DescriptorVisibility() {
    }

    public final boolean isPublicAPI() {
        return getDelegate().isPublicAPI();
    }

    public final Integer compareTo(DescriptorVisibility visibility) {
        Intrinsics.checkNotNullParameter(visibility, "visibility");
        return getDelegate().compareTo(visibility.getDelegate());
    }

    public final String toString() {
        return getDelegate().toString();
    }
}
