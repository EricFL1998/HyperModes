package kotlin.reflect.jvm.internal.impl.builtins.functions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: FunctionTypeKindExtractor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FunctionTypeKindExtractor {
    public static final Companion Companion = new Companion(null);
    private static final FunctionTypeKindExtractor Default = new FunctionTypeKindExtractor(CollectionsKt.listOf((Object[]) new FunctionTypeKind[]{FunctionTypeKind.Function.INSTANCE, FunctionTypeKind.SuspendFunction.INSTANCE, FunctionTypeKind.KFunction.INSTANCE, FunctionTypeKind.KSuspendFunction.INSTANCE}));
    private final List<FunctionTypeKind> kinds;
    private final Map<FqName, List<FunctionTypeKind>> knownKindsByPackageFqName;

    /* JADX INFO: compiled from: FunctionTypeKindExtractor.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final FunctionTypeKindExtractor getDefault() {
            return FunctionTypeKindExtractor.Default;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public FunctionTypeKindExtractor(List<? extends FunctionTypeKind> kinds) {
        Intrinsics.checkNotNullParameter(kinds, "kinds");
        this.kinds = kinds;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Object obj : kinds) {
            FqName packageFqName = ((FunctionTypeKind) obj).getPackageFqName();
            Object obj2 = linkedHashMap.get(packageFqName);
            if (obj2 == null) {
                obj2 = (List) new ArrayList();
                linkedHashMap.put(packageFqName, obj2);
            }
            ((List) obj2).add(obj);
        }
        this.knownKindsByPackageFqName = linkedHashMap;
    }

    public final FunctionTypeKind getFunctionalClassKind(FqName packageFqName, String className) {
        Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
        Intrinsics.checkNotNullParameter(className, "className");
        KindWithArity functionalClassKindWithArity = getFunctionalClassKindWithArity(packageFqName, className);
        if (functionalClassKindWithArity != null) {
            return functionalClassKindWithArity.getKind();
        }
        return null;
    }

    public final KindWithArity getFunctionalClassKindWithArity(FqName packageFqName, String className) {
        Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
        Intrinsics.checkNotNullParameter(className, "className");
        List<FunctionTypeKind> list = this.knownKindsByPackageFqName.get(packageFqName);
        if (list == null) {
            return null;
        }
        for (FunctionTypeKind functionTypeKind : list) {
            if (StringsKt.startsWith$default(className, functionTypeKind.getClassNamePrefix(), false, 2, (Object) null)) {
                String strSubstring = className.substring(functionTypeKind.getClassNamePrefix().length());
                Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
                Integer num = toInt(strSubstring);
                if (num != null) {
                    return new KindWithArity(functionTypeKind, num.intValue());
                }
            }
        }
        return null;
    }

    /* JADX INFO: compiled from: FunctionTypeKindExtractor.kt */
    public static final class KindWithArity {
        private final int arity;
        private final FunctionTypeKind kind;

        public final FunctionTypeKind component1() {
            return this.kind;
        }

        public final int component2() {
            return this.arity;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof KindWithArity)) {
                return false;
            }
            KindWithArity kindWithArity = (KindWithArity) obj;
            return Intrinsics.areEqual(this.kind, kindWithArity.kind) && this.arity == kindWithArity.arity;
        }

        public int hashCode() {
            return (this.kind.hashCode() * 31) + Integer.hashCode(this.arity);
        }

        public String toString() {
            return "KindWithArity(kind=" + this.kind + ", arity=" + this.arity + ')';
        }

        public KindWithArity(FunctionTypeKind kind, int i) {
            Intrinsics.checkNotNullParameter(kind, "kind");
            this.kind = kind;
            this.arity = i;
        }

        public final FunctionTypeKind getKind() {
            return this.kind;
        }
    }

    private final Integer toInt(String str) {
        if (str.length() == 0) {
            return null;
        }
        int length = str.length();
        int i = 0;
        for (int i2 = 0; i2 < length; i2++) {
            int iCharAt = str.charAt(i2) - '0';
            if (iCharAt < 0 || iCharAt >= 10) {
                return null;
            }
            i = (i * 10) + iCharAt;
        }
        return Integer.valueOf(i);
    }
}
