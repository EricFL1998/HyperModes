package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorVisitor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProviderKt;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.ChainedMemberScope;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.LazyScopeAdapter;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageKt;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;

/* JADX INFO: compiled from: LazyPackageViewDescriptorImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public class LazyPackageViewDescriptorImpl extends DeclarationDescriptorImpl implements PackageViewDescriptor {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(LazyPackageViewDescriptorImpl.class), "fragments", "getFragments()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(LazyPackageViewDescriptorImpl.class), "empty", "getEmpty()Z"))};
    private final NotNullLazyValue empty$delegate;
    private final FqName fqName;
    private final NotNullLazyValue fragments$delegate;
    private final MemberScope memberScope;
    private final ModuleDescriptorImpl module;

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor
    public ModuleDescriptorImpl getModule() {
        return this.module;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor
    public FqName getFqName() {
        return this.fqName;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public LazyPackageViewDescriptorImpl(ModuleDescriptorImpl module, FqName fqName, StorageManager storageManager) {
        super(Annotations.Companion.getEMPTY(), fqName.shortNameOrSpecial());
        Intrinsics.checkNotNullParameter(module, "module");
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        this.module = module;
        this.fqName = fqName;
        this.fragments$delegate = storageManager.createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.impl.LazyPackageViewDescriptorImpl$$Lambda$0
            private final LazyPackageViewDescriptorImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyPackageViewDescriptorImpl.fragments_delegate$lambda$0(this.arg$0);
            }
        });
        this.empty$delegate = storageManager.createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.impl.LazyPackageViewDescriptorImpl$$Lambda$1
            private final LazyPackageViewDescriptorImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return Boolean.valueOf(LazyPackageViewDescriptorImpl.empty_delegate$lambda$1(this.arg$0));
            }
        });
        this.memberScope = new LazyScopeAdapter(storageManager, new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.impl.LazyPackageViewDescriptorImpl$$Lambda$2
            private final LazyPackageViewDescriptorImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyPackageViewDescriptorImpl.memberScope$lambda$3(this.arg$0);
            }
        });
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor
    public List<PackageFragmentDescriptor> getFragments() {
        return (List) StorageKt.getValue(this.fragments$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List fragments_delegate$lambda$0(LazyPackageViewDescriptorImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return PackageFragmentProviderKt.packageFragments(this$0.getModule().getPackageFragmentProvider(), this$0.getFqName());
    }

    protected final boolean getEmpty() {
        return ((Boolean) StorageKt.getValue(this.empty$delegate, this, (KProperty<?>) $$delegatedProperties[1])).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final boolean empty_delegate$lambda$1(LazyPackageViewDescriptorImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return PackageFragmentProviderKt.isEmpty(this$0.getModule().getPackageFragmentProvider(), this$0.getFqName());
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor
    public boolean isEmpty() {
        return getEmpty();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor
    public MemberScope getMemberScope() {
        return this.memberScope;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final MemberScope memberScope$lambda$3(LazyPackageViewDescriptorImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (this$0.isEmpty()) {
            return MemberScope.Empty.INSTANCE;
        }
        List<PackageFragmentDescriptor> fragments = this$0.getFragments();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(fragments, 10));
        Iterator<T> it = fragments.iterator();
        while (it.hasNext()) {
            arrayList.add(((PackageFragmentDescriptor) it.next()).getMemberScope());
        }
        return ChainedMemberScope.Companion.create("package view scope for " + this$0.getFqName() + " in " + this$0.getModule().getName(), CollectionsKt.plus((Collection<? extends SubpackagesScope>) arrayList, new SubpackagesScope(this$0.getModule(), this$0.getFqName())));
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public PackageViewDescriptor getContainingDeclaration() {
        if (getFqName().isRoot()) {
            return null;
        }
        ModuleDescriptorImpl module = getModule();
        FqName fqNameParent = getFqName().parent();
        Intrinsics.checkNotNullExpressionValue(fqNameParent, "parent(...)");
        return module.getPackage(fqNameParent);
    }

    public boolean equals(Object obj) {
        PackageViewDescriptor packageViewDescriptor = obj instanceof PackageViewDescriptor ? (PackageViewDescriptor) obj : null;
        return packageViewDescriptor != null && Intrinsics.areEqual(getFqName(), packageViewDescriptor.getFqName()) && Intrinsics.areEqual(getModule(), packageViewDescriptor.getModule());
    }

    public int hashCode() {
        return (getModule().hashCode() * 31) + getFqName().hashCode();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public <R, D> R accept(DeclarationDescriptorVisitor<R, D> visitor, D d) {
        Intrinsics.checkNotNullParameter(visitor, "visitor");
        return visitor.visitPackageViewDescriptor(this, d);
    }
}
