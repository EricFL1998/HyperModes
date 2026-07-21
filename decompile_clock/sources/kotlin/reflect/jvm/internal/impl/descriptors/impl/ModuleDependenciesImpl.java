package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: ModuleDescriptorImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ModuleDependenciesImpl implements ModuleDependencies {
    private final List<ModuleDescriptorImpl> allDependencies;
    private final Set<ModuleDescriptorImpl> allExpectedByDependencies;
    private final List<ModuleDescriptorImpl> directExpectedByDependencies;
    private final Set<ModuleDescriptorImpl> modulesWhoseInternalsAreVisible;

    public ModuleDependenciesImpl(List<ModuleDescriptorImpl> allDependencies, Set<ModuleDescriptorImpl> modulesWhoseInternalsAreVisible, List<ModuleDescriptorImpl> directExpectedByDependencies, Set<ModuleDescriptorImpl> allExpectedByDependencies) {
        Intrinsics.checkNotNullParameter(allDependencies, "allDependencies");
        Intrinsics.checkNotNullParameter(modulesWhoseInternalsAreVisible, "modulesWhoseInternalsAreVisible");
        Intrinsics.checkNotNullParameter(directExpectedByDependencies, "directExpectedByDependencies");
        Intrinsics.checkNotNullParameter(allExpectedByDependencies, "allExpectedByDependencies");
        this.allDependencies = allDependencies;
        this.modulesWhoseInternalsAreVisible = modulesWhoseInternalsAreVisible;
        this.directExpectedByDependencies = directExpectedByDependencies;
        this.allExpectedByDependencies = allExpectedByDependencies;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDependencies
    public List<ModuleDescriptorImpl> getAllDependencies() {
        return this.allDependencies;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDependencies
    public Set<ModuleDescriptorImpl> getModulesWhoseInternalsAreVisible() {
        return this.modulesWhoseInternalsAreVisible;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDependencies
    public List<ModuleDescriptorImpl> getDirectExpectedByDependencies() {
        return this.directExpectedByDependencies;
    }
}
