package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorVisitor;
import kotlin.reflect.jvm.internal.impl.descriptors.InvalidModuleExceptionKt;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleCapability;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProvider;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.platform.TargetPlatform;
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNotNull;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;

/* JADX INFO: compiled from: ModuleDescriptorImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ModuleDescriptorImpl extends DeclarationDescriptorImpl implements ModuleDescriptor {
    private final KotlinBuiltIns builtIns;
    private final Map<ModuleCapability<?>, Object> capabilities;
    private ModuleDependencies dependencies;
    private boolean isValid;
    private PackageFragmentProvider packageFragmentProviderForModuleContent;
    private final Lazy packageFragmentProviderForWholeModuleWithDependencies$delegate;
    private final PackageViewDescriptorFactory packageViewDescriptorFactory;
    private final MemoizedFunctionToNotNull<FqName, PackageViewDescriptor> packages;
    private final TargetPlatform platform;
    private final Name stableName;
    private final StorageManager storageManager;

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public ModuleDescriptorImpl(Name moduleName, StorageManager storageManager, KotlinBuiltIns builtIns, TargetPlatform targetPlatform) {
        this(moduleName, storageManager, builtIns, targetPlatform, null, null, 48, null);
        Intrinsics.checkNotNullParameter(moduleName, "moduleName");
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(builtIns, "builtIns");
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public <R, D> R accept(DeclarationDescriptorVisitor<R, D> declarationDescriptorVisitor, D d) {
        return (R) ModuleDescriptor.DefaultImpls.accept(this, declarationDescriptorVisitor, d);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public DeclarationDescriptor getContainingDeclaration() {
        return ModuleDescriptor.DefaultImpls.getContainingDeclaration(this);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public KotlinBuiltIns getBuiltIns() {
        return this.builtIns;
    }

    public /* synthetic */ ModuleDescriptorImpl(Name name, StorageManager storageManager, KotlinBuiltIns kotlinBuiltIns, TargetPlatform targetPlatform, Map map, Name name2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(name, storageManager, kotlinBuiltIns, (i & 8) != 0 ? null : targetPlatform, (i & 16) != 0 ? MapsKt.emptyMap() : map, (i & 32) != 0 ? null : name2);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ModuleDescriptorImpl(Name moduleName, StorageManager storageManager, KotlinBuiltIns builtIns, TargetPlatform targetPlatform, Map<ModuleCapability<?>, ? extends Object> capabilities, Name name) {
        super(Annotations.Companion.getEMPTY(), moduleName);
        Intrinsics.checkNotNullParameter(moduleName, "moduleName");
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(builtIns, "builtIns");
        Intrinsics.checkNotNullParameter(capabilities, "capabilities");
        this.storageManager = storageManager;
        this.builtIns = builtIns;
        this.platform = targetPlatform;
        this.stableName = name;
        if (!moduleName.isSpecial()) {
            throw new IllegalArgumentException("Module name must be special: " + moduleName);
        }
        this.capabilities = capabilities;
        PackageViewDescriptorFactory.Default r2 = (PackageViewDescriptorFactory) getCapability(PackageViewDescriptorFactory.Companion.getCAPABILITY());
        this.packageViewDescriptorFactory = r2 == null ? PackageViewDescriptorFactory.Default.INSTANCE : r2;
        this.isValid = true;
        this.packages = storageManager.createMemoizedFunction(new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDescriptorImpl$$Lambda$0
            private final ModuleDescriptorImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return ModuleDescriptorImpl.packages$lambda$0(this.arg$0, (FqName) obj);
            }
        });
        this.packageFragmentProviderForWholeModuleWithDependencies$delegate = LazyKt.lazy(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDescriptorImpl$$Lambda$1
            private final ModuleDescriptorImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return ModuleDescriptorImpl.packageFragmentProviderForWholeModuleWithDependencies_delegate$lambda$10(this.arg$0);
            }
        });
    }

    public boolean isValid() {
        return this.isValid;
    }

    public void assertValid() {
        if (isValid()) {
            return;
        }
        InvalidModuleExceptionKt.moduleInvalidated(this);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final PackageViewDescriptor packages$lambda$0(ModuleDescriptorImpl this$0, FqName fqName) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        return this$0.packageViewDescriptorFactory.compute(this$0, fqName, this$0.storageManager);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public List<ModuleDescriptor> getExpectedByModules() {
        ModuleDependencies moduleDependencies = this.dependencies;
        if (moduleDependencies != null) {
            return moduleDependencies.getDirectExpectedByDependencies();
        }
        throw new AssertionError("Dependencies of module " + getId() + " were not set");
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public PackageViewDescriptor getPackage(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        assertValid();
        return this.packages.invoke(fqName);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public Collection<FqName> getSubPackagesOf(FqName fqName, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        assertValid();
        return getPackageFragmentProvider().getSubPackagesOf(fqName, nameFilter);
    }

    private final CompositePackageFragmentProvider getPackageFragmentProviderForWholeModuleWithDependencies() {
        return (CompositePackageFragmentProvider) this.packageFragmentProviderForWholeModuleWithDependencies$delegate.getValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final CompositePackageFragmentProvider packageFragmentProviderForWholeModuleWithDependencies_delegate$lambda$10(ModuleDescriptorImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        ModuleDependencies moduleDependencies = this$0.dependencies;
        if (moduleDependencies != null) {
            List<ModuleDescriptorImpl> allDependencies = moduleDependencies.getAllDependencies();
            this$0.assertValid();
            allDependencies.contains(this$0);
            List<ModuleDescriptorImpl> list = allDependencies;
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                ((ModuleDescriptorImpl) it.next()).isInitialized();
            }
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
            Iterator<T> it2 = list.iterator();
            while (it2.hasNext()) {
                PackageFragmentProvider packageFragmentProvider = ((ModuleDescriptorImpl) it2.next()).packageFragmentProviderForModuleContent;
                Intrinsics.checkNotNull(packageFragmentProvider);
                arrayList.add(packageFragmentProvider);
            }
            return new CompositePackageFragmentProvider(arrayList, "CompositeProvider@ModuleDescriptor for " + this$0.getName());
        }
        throw new AssertionError("Dependencies of module " + this$0.getId() + " were not set before querying module content");
    }

    private final boolean isInitialized() {
        return this.packageFragmentProviderForModuleContent != null;
    }

    public final void setDependencies(ModuleDependencies dependencies) {
        Intrinsics.checkNotNullParameter(dependencies, "dependencies");
        this.dependencies = dependencies;
    }

    public final void setDependencies(ModuleDescriptorImpl... descriptors) {
        Intrinsics.checkNotNullParameter(descriptors, "descriptors");
        setDependencies(ArraysKt.toList(descriptors));
    }

    public final void setDependencies(List<ModuleDescriptorImpl> descriptors) {
        Intrinsics.checkNotNullParameter(descriptors, "descriptors");
        setDependencies(descriptors, SetsKt.emptySet());
    }

    public final void setDependencies(List<ModuleDescriptorImpl> descriptors, Set<ModuleDescriptorImpl> friends) {
        Intrinsics.checkNotNullParameter(descriptors, "descriptors");
        Intrinsics.checkNotNullParameter(friends, "friends");
        setDependencies(new ModuleDependenciesImpl(descriptors, friends, CollectionsKt.emptyList(), SetsKt.emptySet()));
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public boolean shouldSeeInternalsOf(ModuleDescriptor targetModule) {
        Intrinsics.checkNotNullParameter(targetModule, "targetModule");
        if (Intrinsics.areEqual(this, targetModule)) {
            return true;
        }
        ModuleDependencies moduleDependencies = this.dependencies;
        Intrinsics.checkNotNull(moduleDependencies);
        return CollectionsKt.contains(moduleDependencies.getModulesWhoseInternalsAreVisible(), targetModule) || getExpectedByModules().contains(targetModule) || targetModule.getExpectedByModules().contains(this);
    }

    private final String getId() {
        String string = getName().toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public final void initialize(PackageFragmentProvider providerForModuleContent) {
        Intrinsics.checkNotNullParameter(providerForModuleContent, "providerForModuleContent");
        isInitialized();
        this.packageFragmentProviderForModuleContent = providerForModuleContent;
    }

    public final PackageFragmentProvider getPackageFragmentProvider() {
        assertValid();
        return getPackageFragmentProviderForWholeModuleWithDependencies();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public <T> T getCapability(ModuleCapability<T> capability) {
        Intrinsics.checkNotNullParameter(capability, "capability");
        T t = (T) this.capabilities.get(capability);
        if (t == null) {
            return null;
        }
        return t;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.DeclarationDescriptorImpl
    public String toString() {
        Class<?> cls;
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString());
        if (!isValid()) {
            sb.append(" !isValid");
        }
        sb.append(" packageFragmentProvider: ");
        PackageFragmentProvider packageFragmentProvider = this.packageFragmentProviderForModuleContent;
        sb.append((packageFragmentProvider == null || (cls = packageFragmentProvider.getClass()) == null) ? null : cls.getSimpleName());
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
