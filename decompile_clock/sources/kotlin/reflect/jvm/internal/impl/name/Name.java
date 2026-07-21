package kotlin.reflect.jvm.internal.impl.name;

import com.xiaomi.onetrack.api.as;

/* JADX INFO: loaded from: classes2.dex */
public final class Name implements Comparable<Name> {
    private final String name;
    private final boolean special;

    private static /* synthetic */ void $$$reportNull$$$0(int i) {
        String str = (i == 1 || i == 2 || i == 3 || i == 4) ? "@NotNull method %s.%s must not return null" : "Argument for @NotNull parameter '%s' of %s.%s must not be null";
        Object[] objArr = new Object[(i == 1 || i == 2 || i == 3 || i == 4) ? 2 : 3];
        if (i == 1 || i == 2 || i == 3 || i == 4) {
            objArr[0] = "kotlin/reflect/jvm/internal/impl/name/Name";
        } else {
            objArr[0] = as.a;
        }
        if (i == 1) {
            objArr[1] = "asString";
        } else if (i == 2) {
            objArr[1] = "getIdentifier";
        } else if (i == 3 || i == 4) {
            objArr[1] = "asStringStripSpecialMarkers";
        } else {
            objArr[1] = "kotlin/reflect/jvm/internal/impl/name/Name";
        }
        switch (i) {
            case 1:
            case 2:
            case 3:
            case 4:
                break;
            case 5:
                objArr[2] = "identifier";
                break;
            case 6:
                objArr[2] = "isValidIdentifier";
                break;
            case 7:
                objArr[2] = "identifierIfValid";
                break;
            case 8:
                objArr[2] = "special";
                break;
            case 9:
                objArr[2] = "guessByFirstCharacter";
                break;
            default:
                objArr[2] = "<init>";
                break;
        }
        String str2 = String.format(str, objArr);
        if (i != 1 && i != 2 && i != 3 && i != 4) {
            throw new IllegalArgumentException(str2);
        }
        throw new IllegalStateException(str2);
    }

    private Name(String str, boolean z) {
        if (str == null) {
            $$$reportNull$$$0(0);
        }
        this.name = str;
        this.special = z;
    }

    public String asString() {
        String str = this.name;
        if (str == null) {
            $$$reportNull$$$0(1);
        }
        return str;
    }

    public String getIdentifier() {
        if (this.special) {
            throw new IllegalStateException("not identifier: " + this);
        }
        String strAsString = asString();
        if (strAsString == null) {
            $$$reportNull$$$0(2);
        }
        return strAsString;
    }

    public boolean isSpecial() {
        return this.special;
    }

    @Override // java.lang.Comparable
    public int compareTo(Name name) {
        return this.name.compareTo(name.name);
    }

    public static Name identifier(String str) {
        if (str == null) {
            $$$reportNull$$$0(5);
        }
        return new Name(str, false);
    }

    public static boolean isValidIdentifier(String str) {
        if (str == null) {
            $$$reportNull$$$0(6);
        }
        if (str.isEmpty() || str.startsWith("<")) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '.' || cCharAt == '/' || cCharAt == '\\') {
                return false;
            }
        }
        return true;
    }

    public static Name special(String str) {
        if (str == null) {
            $$$reportNull$$$0(8);
        }
        if (!str.startsWith("<")) {
            throw new IllegalArgumentException("special name must start with '<': " + str);
        }
        return new Name(str, true);
    }

    public static Name guessByFirstCharacter(String str) {
        if (str == null) {
            $$$reportNull$$$0(9);
        }
        if (str.startsWith("<")) {
            return special(str);
        }
        return identifier(str);
    }

    public String toString() {
        return this.name;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Name)) {
            return false;
        }
        Name name = (Name) obj;
        return this.special == name.special && this.name.equals(name.name);
    }

    public int hashCode() {
        return (this.name.hashCode() * 31) + (this.special ? 1 : 0);
    }
}
