package kotlin.reflect.jvm.internal.impl.resolve.jvm;

import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation;
import kotlin.reflect.jvm.internal.impl.load.java.components.JavaResolverCache;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaPackageFragmentProvider;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.descriptors.LazyJavaPackageFragment;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass;
import kotlin.reflect.jvm.internal.impl.load.java.structure.LightClassOriginKind;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;

/* JADX INFO: compiled from: JavaDescriptorResolver.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaDescriptorResolver {
    private final JavaResolverCache javaResolverCache;
    private final LazyJavaPackageFragmentProvider packageFragmentProvider;

    public JavaDescriptorResolver(LazyJavaPackageFragmentProvider packageFragmentProvider, JavaResolverCache javaResolverCache) {
        Intrinsics.checkNotNullParameter(packageFragmentProvider, "packageFragmentProvider");
        Intrinsics.checkNotNullParameter(javaResolverCache, "javaResolverCache");
        this.packageFragmentProvider = packageFragmentProvider;
        this.javaResolverCache = javaResolverCache;
    }

    public final LazyJavaPackageFragmentProvider getPackageFragmentProvider() {
        return this.packageFragmentProvider;
    }

    public final ClassDescriptor resolveClass(JavaClass javaClass) {
        Intrinsics.checkNotNullParameter(javaClass, "javaClass");
        FqName fqName = javaClass.getFqName();
        if (fqName != null && javaClass.getLightClassOriginKind() == LightClassOriginKind.SOURCE) {
            return this.javaResolverCache.getClassResolvedFromSource(fqName);
        }
        JavaClass outerClass = javaClass.getOuterClass();
        if (outerClass != null) {
            ClassDescriptor classDescriptorResolveClass = resolveClass(outerClass);
            MemberScope unsubstitutedInnerClassesScope = classDescriptorResolveClass != null ? classDescriptorResolveClass.getUnsubstitutedInnerClassesScope() : null;
            ClassifierDescriptor contributedClassifier = unsubstitutedInnerClassesScope != null ? unsubstitutedInnerClassesScope.mo1493getContributedClassifier(javaClass.getName(), NoLookupLocation.FROM_JAVA_LOADER) : null;
            if (contributedClassifier instanceof ClassDescriptor) {
                return (ClassDescriptor) contributedClassifier;
            }
            return null;
        }
        if (fqName == null) {
            return null;
        }
        LazyJavaPackageFragmentProvider lazyJavaPackageFragmentProvider = this.packageFragmentProvider;
        FqName fqNameParent = fqName.parent();
        Intrinsics.checkNotNullExpressionValue(fqNameParent, "parent(...)");
        LazyJavaPackageFragment lazyJavaPackageFragment = (LazyJavaPackageFragment) CollectionsKt.firstOrNull((List) lazyJavaPackageFragmentProvider.getPackageFragments(fqNameParent));
        if (lazyJavaPackageFragment != null) {
            return lazyJavaPackageFragment.findClassifierByJavaClass$descriptors_jvm(javaClass);
        }
        return null;
    }
}
