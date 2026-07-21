package kotlin.reflect.jvm.internal;

import com.xiaomi.onetrack.api.as;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KCallable;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: EmptyContainerForLocal.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0001\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00110\t2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0016\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\t2\u0006\u0010\u0012\u001a\u00020\u0013H\u0016J\u0012\u0010\u0016\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\b\u0010\u0019\u001a\u00020\u001aH\u0002R\u0018\u0010\u0004\u001a\u0006\u0012\u0002\b\u00030\u00058VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u001e\u0010\b\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\n0\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000b\u0010\fR\u001a\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\f¨\u0006\u001b"}, d2 = {"Lkotlin/reflect/jvm/internal/EmptyContainerForLocal;", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", "<init>", "()V", "jClass", "Ljava/lang/Class;", "getJClass", "()Ljava/lang/Class;", "members", "", "Lkotlin/reflect/KCallable;", "getMembers", "()Ljava/util/Collection;", "constructorDescriptors", "Lkotlin/reflect/jvm/internal/impl/descriptors/ConstructorDescriptor;", "getConstructorDescriptors", "getProperties", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", as.a, "Lkotlin/reflect/jvm/internal/impl/name/Name;", "getFunctions", "Lkotlin/reflect/jvm/internal/impl/descriptors/FunctionDescriptor;", "getLocalProperty", "index", "", "fail", "", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class EmptyContainerForLocal extends KDeclarationContainerImpl {
    public static final EmptyContainerForLocal INSTANCE = new EmptyContainerForLocal();

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public PropertyDescriptor getLocalProperty(int index) {
        return null;
    }

    private EmptyContainerForLocal() {
    }

    @Override // kotlin.jvm.internal.ClassBasedDeclarationContainer
    public Class<?> getJClass() {
        fail();
        throw null;
    }

    @Override // kotlin.reflect.KDeclarationContainer
    public Collection<KCallable<?>> getMembers() {
        fail();
        throw null;
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public Collection<ConstructorDescriptor> getConstructorDescriptors() {
        fail();
        throw null;
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public Collection<PropertyDescriptor> getProperties(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        fail();
        throw null;
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public Collection<FunctionDescriptor> getFunctions(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        fail();
        throw null;
    }

    private final Void fail() {
        throw new KotlinReflectionInternalError("Introspecting local functions, lambdas, anonymous functions, local variables and typealiases is not yet fully supported in Kotlin reflection");
    }
}
