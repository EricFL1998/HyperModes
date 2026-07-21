package kotlin.reflect.jvm.internal.impl.resolve.jvm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.PropertyDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaResolverContext;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: SyntheticJavaPartsProvider.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class CompositeSyntheticJavaPartsProvider implements SyntheticJavaPartsProvider {
    private final List<SyntheticJavaPartsProvider> inner;

    /* JADX WARN: Multi-variable type inference failed */
    public CompositeSyntheticJavaPartsProvider(List<? extends SyntheticJavaPartsProvider> inner) {
        Intrinsics.checkNotNullParameter(inner, "inner");
        this.inner = inner;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public List<Name> getMethodNames(ClassDescriptor thisDescriptor, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(c, "c");
        List<SyntheticJavaPartsProvider> list = this.inner;
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(arrayList, ((SyntheticJavaPartsProvider) it.next()).getMethodNames(thisDescriptor, c));
        }
        return arrayList;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public void generateMethods(ClassDescriptor thisDescriptor, Name name, Collection<SimpleFunctionDescriptor> result, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(c, "c");
        Iterator<T> it = this.inner.iterator();
        while (it.hasNext()) {
            ((SyntheticJavaPartsProvider) it.next()).generateMethods(thisDescriptor, name, result, c);
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public List<Name> getStaticFunctionNames(ClassDescriptor thisDescriptor, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(c, "c");
        List<SyntheticJavaPartsProvider> list = this.inner;
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(arrayList, ((SyntheticJavaPartsProvider) it.next()).getStaticFunctionNames(thisDescriptor, c));
        }
        return arrayList;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public void generateStaticFunctions(ClassDescriptor thisDescriptor, Name name, Collection<SimpleFunctionDescriptor> result, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(c, "c");
        Iterator<T> it = this.inner.iterator();
        while (it.hasNext()) {
            ((SyntheticJavaPartsProvider) it.next()).generateStaticFunctions(thisDescriptor, name, result, c);
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public void generateConstructors(ClassDescriptor thisDescriptor, List<ClassConstructorDescriptor> result, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(c, "c");
        Iterator<T> it = this.inner.iterator();
        while (it.hasNext()) {
            ((SyntheticJavaPartsProvider) it.next()).generateConstructors(thisDescriptor, result, c);
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public List<Name> getNestedClassNames(ClassDescriptor thisDescriptor, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(c, "c");
        List<SyntheticJavaPartsProvider> list = this.inner;
        ArrayList arrayList = new ArrayList();
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(arrayList, ((SyntheticJavaPartsProvider) it.next()).getNestedClassNames(thisDescriptor, c));
        }
        return arrayList;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public void generateNestedClass(ClassDescriptor thisDescriptor, Name name, List<ClassDescriptor> result, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(c, "c");
        Iterator<T> it = this.inner.iterator();
        while (it.hasNext()) {
            ((SyntheticJavaPartsProvider) it.next()).generateNestedClass(thisDescriptor, name, result, c);
        }
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.jvm.SyntheticJavaPartsProvider
    public PropertyDescriptorImpl modifyField(ClassDescriptor thisDescriptor, PropertyDescriptorImpl propertyDescriptor, LazyJavaResolverContext c) {
        Intrinsics.checkNotNullParameter(thisDescriptor, "thisDescriptor");
        Intrinsics.checkNotNullParameter(propertyDescriptor, "propertyDescriptor");
        Intrinsics.checkNotNullParameter(c, "c");
        Iterator<T> it = this.inner.iterator();
        while (it.hasNext()) {
            propertyDescriptor = ((SyntheticJavaPartsProvider) it.next()).modifyField(thisDescriptor, propertyDescriptor, c);
        }
        return propertyDescriptor;
    }
}
