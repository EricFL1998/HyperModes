package kotlin.reflect.jvm.internal.impl.load.java.lazy;

import java.util.Collection;
import java.util.List;
import kotlin.Deprecated;
import kotlin.LazyKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProviderOptimized;
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder$$Util;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageFragment;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.storage.CacheWithNotNullValues;

/* JADX INFO: compiled from: LazyJavaPackageFragmentProvider.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class LazyJavaPackageFragmentProvider implements PackageFragmentProviderOptimized {
    private final LazyJavaResolverContext c;
    private final CacheWithNotNullValues<FqName, LazyJavaPackageFragment> packageFragments;

    public LazyJavaPackageFragmentProvider(JavaResolverComponents components) {
        Intrinsics.checkNotNullParameter(components, "components");
        LazyJavaResolverContext lazyJavaResolverContext = new LazyJavaResolverContext(components, TypeParameterResolver.EMPTY.INSTANCE, LazyKt.lazyOf(null));
        this.c = lazyJavaResolverContext;
        this.packageFragments = lazyJavaResolverContext.getStorageManager().createCacheWithNotNullValues();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProvider
    public /* bridge */ /* synthetic */ Collection getSubPackagesOf(FqName fqName, Function1 function1) {
        return getSubPackagesOf(fqName, (Function1<? super Name, Boolean>) function1);
    }

    private final LazyJavaPackageFragment getPackageFragment(FqName fqName) {
        final JavaPackage javaPackageFindPackage$default = JavaClassFinder$$Util.findPackage$default(this.c.getComponents().getFinder(), fqName, false, 2, null);
        if (javaPackageFindPackage$default == null) {
            return null;
        }
        return this.packageFragments.computeIfAbsent(fqName, new Function0(this, javaPackageFindPackage$default) { // from class: kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaPackageFragmentProvider$$Lambda$0
            private final LazyJavaPackageFragmentProvider arg$0;
            private final JavaPackage arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = javaPackageFindPackage$default;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return LazyJavaPackageFragmentProvider.getPackageFragment$lambda$0(this.arg$0, this.arg$1);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final LazyJavaPackageFragment getPackageFragment$lambda$0(LazyJavaPackageFragmentProvider this$0, JavaPackage jPackage) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(jPackage, "$jPackage");
        return new LazyJavaPackageFragment(this$0.c, jPackage);
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProvider
    @Deprecated(message = "for usages use #packageFragments(FqName) at final point, for impl use #collectPackageFragments(FqName, MutableCollection<PackageFragmentDescriptor>)")
    public List<LazyJavaPackageFragment> getPackageFragments(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        return CollectionsKt.listOfNotNull(getPackageFragment(fqName));
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProviderOptimized
    public void collectPackageFragments(FqName fqName, Collection<PackageFragmentDescriptor> packageFragments) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(packageFragments, "packageFragments");
        kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(packageFragments, getPackageFragment(fqName));
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProviderOptimized
    public boolean isEmpty(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        return JavaClassFinder$$Util.findPackage$default(this.c.getComponents().getFinder(), fqName, false, 2, null) == null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentProvider
    public List<FqName> getSubPackagesOf(FqName fqName, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        LazyJavaPackageFragment packageFragment = getPackageFragment(fqName);
        List<FqName> subPackageFqNames$descriptors_jvm = packageFragment != null ? packageFragment.getSubPackageFqNames$descriptors_jvm() : null;
        return subPackageFqNames$descriptors_jvm == null ? CollectionsKt.emptyList() : subPackageFqNames$descriptors_jvm;
    }

    public String toString() {
        return "LazyJavaPackageFragmentProvider of module " + this.c.getComponents().getModule();
    }
}
