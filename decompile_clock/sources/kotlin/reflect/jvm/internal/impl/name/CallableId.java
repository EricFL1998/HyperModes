package kotlin.reflect.jvm.internal.impl.name;

import com.android.deskclock.R2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: CallableId.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class CallableId {
    public static final Companion Companion = new Companion(null);
    private static final Name LOCAL_NAME;
    private static final FqName PACKAGE_FQ_NAME_FOR_LOCAL;
    private final Name callableName;
    private final ClassId classId;
    private final FqName className;
    private final FqName packageName;
    private final FqName pathToLocal;

    private CallableId(FqName fqName, FqName fqName2, Name name, ClassId classId, FqName fqName3) {
        this.packageName = fqName;
        this.className = fqName2;
        this.callableName = name;
        this.classId = classId;
        this.pathToLocal = fqName3;
    }

    /* JADX INFO: compiled from: CallableId.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    static {
        Name name = SpecialNames.LOCAL;
        LOCAL_NAME = name;
        FqName fqName = FqName.topLevel(name);
        Intrinsics.checkNotNullExpressionValue(fqName, "topLevel(...)");
        PACKAGE_FQ_NAME_FOR_LOCAL = fqName;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public CallableId(FqName packageName, Name callableName) {
        this(packageName, null, callableName, null, null);
        Intrinsics.checkNotNullParameter(packageName, "packageName");
        Intrinsics.checkNotNullParameter(callableName, "callableName");
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof CallableId) {
            CallableId callableId = (CallableId) obj;
            if (Intrinsics.areEqual(this.packageName, callableId.packageName) && Intrinsics.areEqual(this.className, callableId.className) && Intrinsics.areEqual(this.callableName, callableId.callableName)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int iHashCode = (R2.attr.checkWidgetOnAlphaDrawableBackgroundColor + this.packageName.hashCode()) * 31;
        FqName fqName = this.className;
        return ((iHashCode + (fqName != null ? fqName.hashCode() : 0)) * 31) + this.callableName.hashCode();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        String strAsString = this.packageName.asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        sb.append(StringsKt.replace$default(strAsString, '.', '/', false, 4, (Object) null));
        sb.append("/");
        FqName fqName = this.className;
        if (fqName != null) {
            sb.append(fqName);
            sb.append(".");
        }
        sb.append(this.callableName);
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
