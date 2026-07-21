package kotlin.reflect.jvm.internal.impl.descriptors.runtime.components;

import java.util.Set;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaClass;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaPackage;
import kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaClass;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaPackage;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: ReflectJavaClassFinder.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectJavaClassFinder implements JavaClassFinder {
    private final ClassLoader classLoader;

    @Override // kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder
    public Set<String> knownClassNamesInPackage(FqName packageFqName) {
        Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
        return null;
    }

    public ReflectJavaClassFinder(ClassLoader classLoader) {
        Intrinsics.checkNotNullParameter(classLoader, "classLoader");
        this.classLoader = classLoader;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder
    public JavaClass findClass(JavaClassFinder.Request request) {
        Intrinsics.checkNotNullParameter(request, "request");
        ClassId classId = request.getClassId();
        FqName packageFqName = classId.getPackageFqName();
        String strAsString = classId.getRelativeClassName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        String strReplace$default = StringsKt.replace$default(strAsString, '.', Typography.dollar, false, 4, (Object) null);
        if (!packageFqName.isRoot()) {
            strReplace$default = packageFqName.asString() + '.' + strReplace$default;
        }
        Class<?> clsTryLoadClass = ReflectJavaClassFinderKt.tryLoadClass(this.classLoader, strReplace$default);
        if (clsTryLoadClass != null) {
            return new ReflectJavaClass(clsTryLoadClass);
        }
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.JavaClassFinder
    public JavaPackage findPackage(FqName fqName, boolean z) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        return new ReflectJavaPackage(fqName);
    }
}
