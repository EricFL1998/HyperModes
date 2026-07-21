package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: DescriptorVisibility.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class DelegatedDescriptorVisibility extends DescriptorVisibility {
    private final Visibility delegate;

    public DelegatedDescriptorVisibility(Visibility delegate) {
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        this.delegate = delegate;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility
    public Visibility getDelegate() {
        return this.delegate;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility
    public String getInternalDisplayName() {
        return getDelegate().getInternalDisplayName();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DescriptorVisibility
    public DescriptorVisibility normalize() {
        DescriptorVisibility descriptorVisibility = DescriptorVisibilities.toDescriptorVisibility(getDelegate().normalize());
        Intrinsics.checkNotNullExpressionValue(descriptorVisibility, "toDescriptorVisibility(...)");
        return descriptorVisibility;
    }
}
