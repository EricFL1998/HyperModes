package kotlin.reflect.jvm.internal.impl.types.error;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.DefaultBuiltIns;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptorVisitor;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleCapability;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageViewDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: ErrorModuleDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ErrorModuleDescriptor implements ModuleDescriptor {
    public static final ErrorModuleDescriptor INSTANCE = new ErrorModuleDescriptor();
    private static final List<ModuleDescriptor> allDependencyModules;
    private static final Set<ModuleDescriptor> allExpectedByModules;
    private static final Lazy builtIns$delegate;
    private static final List<ModuleDescriptor> expectedByModules;
    private static final Name stableName;

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public <R, D> R accept(DeclarationDescriptorVisitor<R, D> visitor, D d) {
        Intrinsics.checkNotNullParameter(visitor, "visitor");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public <T> T getCapability(ModuleCapability<T> capability) {
        Intrinsics.checkNotNullParameter(capability, "capability");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public DeclarationDescriptor getContainingDeclaration() {
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public boolean shouldSeeInternalsOf(ModuleDescriptor targetModule) {
        Intrinsics.checkNotNullParameter(targetModule, "targetModule");
        return false;
    }

    private ErrorModuleDescriptor() {
    }

    static {
        Name nameSpecial = Name.special(ErrorEntity.ERROR_MODULE.getDebugText());
        Intrinsics.checkNotNullExpressionValue(nameSpecial, "special(...)");
        stableName = nameSpecial;
        allDependencyModules = CollectionsKt.emptyList();
        expectedByModules = CollectionsKt.emptyList();
        allExpectedByModules = SetsKt.emptySet();
        builtIns$delegate = LazyKt.lazy(new Function0() { // from class: kotlin.reflect.jvm.internal.impl.types.error.ErrorModuleDescriptor$$Lambda$0
            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return ErrorModuleDescriptor.builtIns_delegate$lambda$0();
            }
        });
    }

    public Name getStableName() {
        return stableName;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public List<ModuleDescriptor> getExpectedByModules() {
        return expectedByModules;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated
    public Annotations getAnnotations() {
        return Annotations.Companion.getEMPTY();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final DefaultBuiltIns builtIns_delegate$lambda$0() {
        return DefaultBuiltIns.Companion.getInstance();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public KotlinBuiltIns getBuiltIns() {
        return (KotlinBuiltIns) builtIns$delegate.getValue();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public Collection<FqName> getSubPackagesOf(FqName fqName, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.Named
    public Name getName() {
        return getStableName();
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor
    public PackageViewDescriptor getPackage(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        throw new IllegalStateException("Should not be called!");
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor
    public DeclarationDescriptor getOriginal() {
        return this;
    }
}
