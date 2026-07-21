package kotlin.reflect.jvm.internal.impl.builtins.functions;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: FunctionTypeKind.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class FunctionTypeKind {
    private final ClassId annotationOnInvokeClassId;
    private final String classNamePrefix;
    private final boolean isReflectType;
    private final FqName packageFqName;

    public FunctionTypeKind(FqName packageFqName, String classNamePrefix, boolean z, ClassId classId) {
        Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
        Intrinsics.checkNotNullParameter(classNamePrefix, "classNamePrefix");
        this.packageFqName = packageFqName;
        this.classNamePrefix = classNamePrefix;
        this.isReflectType = z;
        this.annotationOnInvokeClassId = classId;
    }

    public final FqName getPackageFqName() {
        return this.packageFqName;
    }

    public final String getClassNamePrefix() {
        return this.classNamePrefix;
    }

    public final Name numberedClassName(int i) {
        Name nameIdentifier = Name.identifier(this.classNamePrefix + i);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        return nameIdentifier;
    }

    public String toString() {
        return this.packageFqName + '.' + this.classNamePrefix + 'N';
    }

    /* JADX INFO: compiled from: FunctionTypeKind.kt */
    public static final class Function extends FunctionTypeKind {
        public static final Function INSTANCE = new Function();

        private Function() {
            super(StandardNames.BUILT_INS_PACKAGE_FQ_NAME, "Function", false, null);
        }
    }

    /* JADX INFO: compiled from: FunctionTypeKind.kt */
    public static final class SuspendFunction extends FunctionTypeKind {
        public static final SuspendFunction INSTANCE = new SuspendFunction();

        private SuspendFunction() {
            super(StandardNames.COROUTINES_PACKAGE_FQ_NAME, "SuspendFunction", false, null);
        }
    }

    /* JADX INFO: compiled from: FunctionTypeKind.kt */
    public static final class KFunction extends FunctionTypeKind {
        public static final KFunction INSTANCE = new KFunction();

        private KFunction() {
            super(StandardNames.KOTLIN_REFLECT_FQ_NAME, "KFunction", true, null);
        }
    }

    /* JADX INFO: compiled from: FunctionTypeKind.kt */
    public static final class KSuspendFunction extends FunctionTypeKind {
        public static final KSuspendFunction INSTANCE = new KSuspendFunction();

        private KSuspendFunction() {
            super(StandardNames.KOTLIN_REFLECT_FQ_NAME, "KSuspendFunction", true, null);
        }
    }
}
