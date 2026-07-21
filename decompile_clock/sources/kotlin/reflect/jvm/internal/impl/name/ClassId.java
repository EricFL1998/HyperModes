package kotlin.reflect.jvm.internal.impl.name;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: ClassId.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ClassId {
    public static final Companion Companion = new Companion(null);
    private final boolean isLocal;
    private final FqName packageFqName;
    private final FqName relativeClassName;

    @JvmStatic
    public static final ClassId topLevel(FqName fqName) {
        return Companion.topLevel(fqName);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ClassId)) {
            return false;
        }
        ClassId classId = (ClassId) obj;
        return Intrinsics.areEqual(this.packageFqName, classId.packageFqName) && Intrinsics.areEqual(this.relativeClassName, classId.relativeClassName) && this.isLocal == classId.isLocal;
    }

    public int hashCode() {
        return (((this.packageFqName.hashCode() * 31) + this.relativeClassName.hashCode()) * 31) + Boolean.hashCode(this.isLocal);
    }

    public ClassId(FqName packageFqName, FqName relativeClassName, boolean z) {
        Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
        Intrinsics.checkNotNullParameter(relativeClassName, "relativeClassName");
        this.packageFqName = packageFqName;
        this.relativeClassName = relativeClassName;
        this.isLocal = z;
        relativeClassName.isRoot();
    }

    public final FqName getPackageFqName() {
        return this.packageFqName;
    }

    public final FqName getRelativeClassName() {
        return this.relativeClassName;
    }

    public final boolean isLocal() {
        return this.isLocal;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public ClassId(FqName packageFqName, Name topLevelName) {
        Intrinsics.checkNotNullParameter(packageFqName, "packageFqName");
        Intrinsics.checkNotNullParameter(topLevelName, "topLevelName");
        FqName fqName = FqName.topLevel(topLevelName);
        Intrinsics.checkNotNullExpressionValue(fqName, "topLevel(...)");
        this(packageFqName, fqName, false);
    }

    public final Name getShortClassName() {
        Name nameShortName = this.relativeClassName.shortName();
        Intrinsics.checkNotNullExpressionValue(nameShortName, "shortName(...)");
        return nameShortName;
    }

    public final ClassId getOuterClassId() {
        FqName fqNameParent = this.relativeClassName.parent();
        Intrinsics.checkNotNullExpressionValue(fqNameParent, "parent(...)");
        if (!fqNameParent.isRoot()) {
            return new ClassId(this.packageFqName, fqNameParent, this.isLocal);
        }
        return null;
    }

    public final boolean isNestedClass() {
        return !this.relativeClassName.parent().isRoot();
    }

    public final ClassId createNestedClassId(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        FqName fqName = this.packageFqName;
        FqName fqNameChild = this.relativeClassName.child(name);
        Intrinsics.checkNotNullExpressionValue(fqNameChild, "child(...)");
        return new ClassId(fqName, fqNameChild, this.isLocal);
    }

    public final FqName asSingleFqName() {
        return this.packageFqName.isRoot() ? this.relativeClassName : new FqName(this.packageFqName.asString() + '.' + this.relativeClassName.asString());
    }

    private static final String asString$escapeSlashes(FqName fqName) {
        String strAsString = fqName.asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        return StringsKt.contains$default((CharSequence) strAsString, '/', false, 2, (Object) null) ? "`" + strAsString + '`' : strAsString;
    }

    public final String asString() {
        if (this.packageFqName.isRoot()) {
            return asString$escapeSlashes(this.relativeClassName);
        }
        StringBuilder sb = new StringBuilder();
        String strAsString = this.packageFqName.asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        sb.append(StringsKt.replace$default(strAsString, '.', '/', false, 4, (Object) null));
        sb.append("/");
        sb.append(asString$escapeSlashes(this.relativeClassName));
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public String toString() {
        return this.packageFqName.isRoot() ? "/" + asString() : asString();
    }

    /* JADX INFO: compiled from: ClassId.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final ClassId topLevel(FqName topLevelFqName) {
            Intrinsics.checkNotNullParameter(topLevelFqName, "topLevelFqName");
            FqName fqNameParent = topLevelFqName.parent();
            Intrinsics.checkNotNullExpressionValue(fqNameParent, "parent(...)");
            Name nameShortName = topLevelFqName.shortName();
            Intrinsics.checkNotNullExpressionValue(nameShortName, "shortName(...)");
            return new ClassId(fqNameParent, nameShortName);
        }

        public static /* synthetic */ ClassId fromString$default(Companion companion, String str, boolean z, int i, Object obj) {
            if ((i & 2) != 0) {
                z = false;
            }
            return companion.fromString(str, z);
        }

        @JvmStatic
        public final ClassId fromString(String string, boolean z) {
            String strReplace$default;
            String str;
            Intrinsics.checkNotNullParameter(string, "string");
            String str2 = string;
            int iIndexOf$default = StringsKt.indexOf$default((CharSequence) str2, '`', 0, false, 6, (Object) null);
            if (iIndexOf$default == -1) {
                iIndexOf$default = string.length();
            }
            int iLastIndexOf$default = StringsKt.lastIndexOf$default((CharSequence) str2, "/", iIndexOf$default, false, 4, (Object) null);
            if (iLastIndexOf$default == -1) {
                strReplace$default = StringsKt.replace$default(string, "`", "", false, 4, (Object) null);
                str = "";
            } else {
                String strSubstring = string.substring(0, iLastIndexOf$default);
                Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
                String strReplace$default2 = StringsKt.replace$default(strSubstring, '/', '.', false, 4, (Object) null);
                String strSubstring2 = string.substring(iLastIndexOf$default + 1);
                Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
                strReplace$default = StringsKt.replace$default(strSubstring2, "`", "", false, 4, (Object) null);
                str = strReplace$default2;
            }
            return new ClassId(new FqName(str), new FqName(strReplace$default), z);
        }
    }
}
