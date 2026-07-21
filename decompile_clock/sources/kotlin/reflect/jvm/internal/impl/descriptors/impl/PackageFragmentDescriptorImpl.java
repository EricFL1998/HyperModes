package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorVisitor;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.FqName;

/* JADX INFO: compiled from: PackageFragmentDescriptorImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class PackageFragmentDescriptorImpl extends DeclarationDescriptorNonRootImpl implements PackageFragmentDescriptor {
    private final String debugString;
    private final FqName fqName;

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor
    public final FqName getFqName() {
        return this.fqName;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public PackageFragmentDescriptorImpl(ModuleDescriptor module, FqName fqName) {
        super(module, Annotations.Companion.getEMPTY(), fqName.shortNameOrSpecial(), SourceElement.NO_SOURCE);
        Intrinsics.checkNotNullParameter(module, "module");
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        this.fqName = fqName;
        this.debugString = "package " + fqName + " of " + module;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public <R, D> R accept(DeclarationDescriptorVisitor<R, D> visitor, D d) {
        Intrinsics.checkNotNullParameter(visitor, "visitor");
        return visitor.visitPackageFragmentDescriptor(this, d);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorNonRootImpl, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public ModuleDescriptor getContainingDeclaration() {
        DeclarationDescriptor containingDeclaration = super.getContainingDeclaration();
        Intrinsics.checkNotNull(containingDeclaration, "null cannot be cast to non-null type org.jetbrains.kotlin.descriptors.ModuleDescriptor");
        return (ModuleDescriptor) containingDeclaration;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorNonRootImpl, kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorWithSource
    public SourceElement getSource() {
        SourceElement NO_SOURCE = SourceElement.NO_SOURCE;
        Intrinsics.checkNotNullExpressionValue(NO_SOURCE, "NO_SOURCE");
        return NO_SOURCE;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorImpl
    public String toString() {
        return this.debugString;
    }
}
