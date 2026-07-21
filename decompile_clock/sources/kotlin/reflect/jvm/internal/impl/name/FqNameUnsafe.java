package kotlin.reflect.jvm.internal.impl.name;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.as;
import com.xiaomi.onetrack.util.z;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes2.dex */
public final class FqNameUnsafe {
    private static final Name ROOT_NAME = Name.special("<root>");
    private static final Pattern SPLIT_BY_DOTS = Pattern.compile(z.a);
    private static final Function1<String, Name> STRING_TO_NAME = new Function1<String, Name>() { // from class: kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe.1
        @Override // kotlin.jvm.functions.Function1
        public Name invoke(String str) {
            return Name.guessByFirstCharacter(str);
        }
    };
    private final String fqName;
    private transient FqNameUnsafe parent;
    private transient FqName safe;
    private transient Name shortName;

    private static /* synthetic */ void $$$reportNull$$$0(int i) {
        String str;
        int i2;
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 18:
                str = "@NotNull method %s.%s must not return null";
                break;
            case 9:
            case 15:
            case 16:
            case 17:
            default:
                str = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
                break;
        }
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 18:
                i2 = 2;
                break;
            case 9:
            case 15:
            case 16:
            case 17:
            default:
                i2 = 3;
                break;
        }
        Object[] objArr = new Object[i2];
        if (i != 1) {
            switch (i) {
                case 4:
                case 5:
                case 6:
                case 7:
                case 8:
                case 10:
                case 11:
                case 12:
                case 13:
                case 14:
                case 18:
                    objArr[0] = "kotlin/reflect/jvm/internal/impl/name/FqNameUnsafe";
                    break;
                case 9:
                    objArr[0] = as.a;
                    break;
                case 15:
                    objArr[0] = "segment";
                    break;
                case 16:
                    objArr[0] = SearchContract.SearchResultColumn.COLUMN_OTHER;
                    break;
                case 17:
                    objArr[0] = "shortName";
                    break;
                default:
                    objArr[0] = "fqName";
                    break;
            }
        } else {
            objArr[0] = "safe";
        }
        switch (i) {
            case 4:
                objArr[1] = "asString";
                break;
            case 5:
            case 6:
                objArr[1] = "toSafe";
                break;
            case 7:
            case 8:
                objArr[1] = "parent";
                break;
            case 9:
            case 15:
            case 16:
            case 17:
            default:
                objArr[1] = "kotlin/reflect/jvm/internal/impl/name/FqNameUnsafe";
                break;
            case 10:
            case 11:
                objArr[1] = "shortName";
                break;
            case 12:
            case 13:
                objArr[1] = "shortNameOrSpecial";
                break;
            case 14:
                objArr[1] = "pathSegments";
                break;
            case 18:
                objArr[1] = "toString";
                break;
        }
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 18:
                break;
            case 9:
                objArr[2] = "child";
                break;
            case 15:
            case 16:
                objArr[2] = "startsWith";
                break;
            case 17:
                objArr[2] = "topLevel";
                break;
            default:
                objArr[2] = "<init>";
                break;
        }
        String str2 = String.format(str, objArr);
        switch (i) {
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 10:
            case 11:
            case 12:
            case 13:
            case 14:
            case 18:
                throw new IllegalStateException(str2);
            case 9:
            case 15:
            case 16:
            case 17:
            default:
                throw new IllegalArgumentException(str2);
        }
    }

    FqNameUnsafe(String str, FqName fqName) {
        if (str == null) {
            $$$reportNull$$$0(0);
        }
        if (fqName == null) {
            $$$reportNull$$$0(1);
        }
        this.fqName = str;
        this.safe = fqName;
    }

    public FqNameUnsafe(String str) {
        if (str == null) {
            $$$reportNull$$$0(2);
        }
        this.fqName = str;
    }

    private FqNameUnsafe(String str, FqNameUnsafe fqNameUnsafe, Name name) {
        if (str == null) {
            $$$reportNull$$$0(3);
        }
        this.fqName = str;
        this.parent = fqNameUnsafe;
        this.shortName = name;
    }

    private void compute() {
        int iLastIndexOf = this.fqName.lastIndexOf(46);
        if (iLastIndexOf >= 0) {
            this.shortName = Name.guessByFirstCharacter(this.fqName.substring(iLastIndexOf + 1));
            this.parent = new FqNameUnsafe(this.fqName.substring(0, iLastIndexOf));
        } else {
            this.shortName = Name.guessByFirstCharacter(this.fqName);
            this.parent = FqName.ROOT.toUnsafe();
        }
    }

    public String asString() {
        String str = this.fqName;
        if (str == null) {
            $$$reportNull$$$0(4);
        }
        return str;
    }

    public boolean isSafe() {
        return this.safe != null || asString().indexOf(60) < 0;
    }

    public FqName toSafe() {
        FqName fqName = this.safe;
        if (fqName != null) {
            if (fqName == null) {
                $$$reportNull$$$0(5);
            }
            return fqName;
        }
        FqName fqName2 = new FqName(this);
        this.safe = fqName2;
        return fqName2;
    }

    public boolean isRoot() {
        return this.fqName.isEmpty();
    }

    public FqNameUnsafe parent() {
        FqNameUnsafe fqNameUnsafe = this.parent;
        if (fqNameUnsafe != null) {
            if (fqNameUnsafe == null) {
                $$$reportNull$$$0(7);
            }
            return fqNameUnsafe;
        }
        if (isRoot()) {
            throw new IllegalStateException("root");
        }
        compute();
        FqNameUnsafe fqNameUnsafe2 = this.parent;
        if (fqNameUnsafe2 == null) {
            $$$reportNull$$$0(8);
        }
        return fqNameUnsafe2;
    }

    public FqNameUnsafe child(Name name) {
        String strAsString;
        if (name == null) {
            $$$reportNull$$$0(9);
        }
        if (isRoot()) {
            strAsString = name.asString();
        } else {
            strAsString = this.fqName + "." + name.asString();
        }
        return new FqNameUnsafe(strAsString, this, name);
    }

    public Name shortName() {
        Name name = this.shortName;
        if (name != null) {
            if (name == null) {
                $$$reportNull$$$0(10);
            }
            return name;
        }
        if (isRoot()) {
            throw new IllegalStateException("root");
        }
        compute();
        Name name2 = this.shortName;
        if (name2 == null) {
            $$$reportNull$$$0(11);
        }
        return name2;
    }

    public Name shortNameOrSpecial() {
        if (isRoot()) {
            Name name = ROOT_NAME;
            if (name == null) {
                $$$reportNull$$$0(12);
            }
            return name;
        }
        Name nameShortName = shortName();
        if (nameShortName == null) {
            $$$reportNull$$$0(13);
        }
        return nameShortName;
    }

    public List<Name> pathSegments() {
        List<Name> listEmptyList = isRoot() ? Collections.emptyList() : ArraysKt.map(SPLIT_BY_DOTS.split(this.fqName), STRING_TO_NAME);
        if (listEmptyList == null) {
            $$$reportNull$$$0(14);
        }
        return listEmptyList;
    }

    public boolean startsWith(Name name) {
        if (name == null) {
            $$$reportNull$$$0(15);
        }
        if (isRoot()) {
            return false;
        }
        int iIndexOf = this.fqName.indexOf(46);
        if (iIndexOf == -1) {
            iIndexOf = this.fqName.length();
        }
        String strAsString = name.asString();
        return iIndexOf == strAsString.length() && this.fqName.regionMatches(0, strAsString, 0, iIndexOf);
    }

    public static FqNameUnsafe topLevel(Name name) {
        if (name == null) {
            $$$reportNull$$$0(17);
        }
        return new FqNameUnsafe(name.asString(), FqName.ROOT.toUnsafe(), name);
    }

    public String toString() {
        String strAsString = isRoot() ? ROOT_NAME.asString() : this.fqName;
        if (strAsString == null) {
            $$$reportNull$$$0(18);
        }
        return strAsString;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof FqNameUnsafe) && this.fqName.equals(((FqNameUnsafe) obj).fqName);
    }

    public int hashCode() {
        return this.fqName.hashCode();
    }
}
