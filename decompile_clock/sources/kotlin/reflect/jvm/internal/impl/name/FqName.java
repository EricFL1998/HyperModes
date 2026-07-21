package kotlin.reflect.jvm.internal.impl.name;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.as;
import java.util.List;

/* JADX INFO: loaded from: classes2.dex */
public final class FqName {
    public static final FqName ROOT = new FqName("");
    private final FqNameUnsafe fqName;
    private transient FqName parent;

    private static /* synthetic */ void $$$reportNull$$$0(int i) {
        String str;
        int i2;
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
                str = "@NotNull method %s.%s must not return null";
                break;
            case 8:
            default:
                str = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
                break;
        }
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
                i2 = 2;
                break;
            case 8:
            default:
                i2 = 3;
                break;
        }
        Object[] objArr = new Object[i2];
        switch (i) {
            case 1:
            case 2:
            case 3:
                objArr[0] = "fqName";
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
                objArr[0] = "kotlin/reflect/jvm/internal/impl/name/FqName";
                break;
            case 8:
                objArr[0] = as.a;
                break;
            case 12:
                objArr[0] = "segment";
                break;
            case 13:
                objArr[0] = SearchContract.SearchResultColumn.COLUMN_OTHER;
                break;
            case 14:
                objArr[0] = "shortName";
                break;
            default:
                objArr[0] = "names";
                break;
        }
        switch (i) {
            case 4:
                objArr[1] = "asString";
                break;
            case 5:
                objArr[1] = "toUnsafe";
                break;
            case 6:
            case 7:
                objArr[1] = "parent";
                break;
            case 8:
            default:
                objArr[1] = "kotlin/reflect/jvm/internal/impl/name/FqName";
                break;
            case 9:
                objArr[1] = "shortName";
                break;
            case 10:
                objArr[1] = "shortNameOrSpecial";
                break;
            case 11:
                objArr[1] = "pathSegments";
                break;
        }
        switch (i) {
            case 1:
            case 2:
            case 3:
                objArr[2] = "<init>";
                break;
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
                break;
            case 8:
                objArr[2] = "child";
                break;
            case 12:
            case 13:
                objArr[2] = "startsWith";
                break;
            case 14:
                objArr[2] = "topLevel";
                break;
            default:
                objArr[2] = "fromSegments";
                break;
        }
        String str2 = String.format(str, objArr);
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 9:
            case 10:
            case 11:
                throw new IllegalStateException(str2);
            case 8:
            default:
                throw new IllegalArgumentException(str2);
        }
    }

    public FqName(String str) {
        if (str == null) {
            $$$reportNull$$$0(1);
        }
        this.fqName = new FqNameUnsafe(str, this);
    }

    public FqName(FqNameUnsafe fqNameUnsafe) {
        if (fqNameUnsafe == null) {
            $$$reportNull$$$0(2);
        }
        this.fqName = fqNameUnsafe;
    }

    private FqName(FqNameUnsafe fqNameUnsafe, FqName fqName) {
        if (fqNameUnsafe == null) {
            $$$reportNull$$$0(3);
        }
        this.fqName = fqNameUnsafe;
        this.parent = fqName;
    }

    public String asString() {
        String strAsString = this.fqName.asString();
        if (strAsString == null) {
            $$$reportNull$$$0(4);
        }
        return strAsString;
    }

    public FqNameUnsafe toUnsafe() {
        FqNameUnsafe fqNameUnsafe = this.fqName;
        if (fqNameUnsafe == null) {
            $$$reportNull$$$0(5);
        }
        return fqNameUnsafe;
    }

    public boolean isRoot() {
        return this.fqName.isRoot();
    }

    public FqName parent() {
        FqName fqName = this.parent;
        if (fqName != null) {
            if (fqName == null) {
                $$$reportNull$$$0(6);
            }
            return fqName;
        }
        if (isRoot()) {
            throw new IllegalStateException("root");
        }
        FqName fqName2 = new FqName(this.fqName.parent());
        this.parent = fqName2;
        return fqName2;
    }

    public FqName child(Name name) {
        if (name == null) {
            $$$reportNull$$$0(8);
        }
        return new FqName(this.fqName.child(name), this);
    }

    public Name shortName() {
        Name nameShortName = this.fqName.shortName();
        if (nameShortName == null) {
            $$$reportNull$$$0(9);
        }
        return nameShortName;
    }

    public Name shortNameOrSpecial() {
        Name nameShortNameOrSpecial = this.fqName.shortNameOrSpecial();
        if (nameShortNameOrSpecial == null) {
            $$$reportNull$$$0(10);
        }
        return nameShortNameOrSpecial;
    }

    public List<Name> pathSegments() {
        List<Name> listPathSegments = this.fqName.pathSegments();
        if (listPathSegments == null) {
            $$$reportNull$$$0(11);
        }
        return listPathSegments;
    }

    public boolean startsWith(Name name) {
        if (name == null) {
            $$$reportNull$$$0(12);
        }
        return this.fqName.startsWith(name);
    }

    public static FqName topLevel(Name name) {
        if (name == null) {
            $$$reportNull$$$0(14);
        }
        return new FqName(FqNameUnsafe.topLevel(name));
    }

    public String toString() {
        return this.fqName.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof FqName) && this.fqName.equals(((FqName) obj).fqName);
    }

    public int hashCode() {
        return this.fqName.hashCode();
    }
}
