package kotlin.reflect.jvm.internal.impl.resolve.jvm;

import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.text.Typography;

/* JADX INFO: loaded from: classes2.dex */
public class JvmClassName {
    private FqName fqName;
    private final String internalName;

    /* JADX WARN: Code duplicated, block: B:7:0x000c  */
    private static /* synthetic */ void $$$reportNull$$$0(int i) {
        String str;
        int i2;
        if (i != 3 && i != 5) {
            switch (i) {
                case 8:
                case 9:
                case 10:
                    str = "@NotNull method %s.%s must not return null";
                    break;
                default:
                    str = "Argument for @NotNull parameter '%s' of %s.%s must not be null";
                    break;
            }
        } else {
            str = "@NotNull method %s.%s must not return null";
        }
        if (i != 3 && i != 5) {
            switch (i) {
                case 8:
                case 9:
                case 10:
                    i2 = 2;
                    break;
                default:
                    i2 = 3;
                    break;
            }
        } else {
            i2 = 2;
        }
        Object[] objArr = new Object[i2];
        switch (i) {
            case 1:
            case 2:
                objArr[0] = "classId";
                break;
            case 3:
            case 5:
            case 8:
            case 9:
            case 10:
                objArr[0] = "kotlin/reflect/jvm/internal/impl/resolve/jvm/JvmClassName";
                break;
            case 4:
            case 6:
                objArr[0] = "fqName";
                break;
            case 7:
            default:
                objArr[0] = "internalName";
                break;
        }
        if (i == 3) {
            objArr[1] = "internalNameByClassId";
        } else if (i != 5) {
            switch (i) {
                case 8:
                    objArr[1] = "getFqNameForClassNameWithoutDollars";
                    break;
                case 9:
                    objArr[1] = "getPackageFqName";
                    break;
                case 10:
                    objArr[1] = "getInternalName";
                    break;
                default:
                    objArr[1] = "kotlin/reflect/jvm/internal/impl/resolve/jvm/JvmClassName";
                    break;
            }
        } else {
            objArr[1] = "byFqNameWithoutInnerClasses";
        }
        switch (i) {
            case 1:
                objArr[2] = "byClassId";
                break;
            case 2:
                objArr[2] = "internalNameByClassId";
                break;
            case 3:
            case 5:
            case 8:
            case 9:
            case 10:
                break;
            case 4:
            case 6:
                objArr[2] = "byFqNameWithoutInnerClasses";
                break;
            case 7:
                objArr[2] = "<init>";
                break;
            default:
                objArr[2] = "byInternalName";
                break;
        }
        String str2 = String.format(str, objArr);
        if (i != 3 && i != 5) {
            switch (i) {
                case 8:
                case 9:
                case 10:
                    break;
                default:
                    throw new IllegalArgumentException(str2);
            }
        }
        throw new IllegalStateException(str2);
    }

    public static JvmClassName byInternalName(String str) {
        if (str == null) {
            $$$reportNull$$$0(0);
        }
        return new JvmClassName(str);
    }

    public static JvmClassName byClassId(ClassId classId) {
        if (classId == null) {
            $$$reportNull$$$0(1);
        }
        return new JvmClassName(internalNameByClassId(classId));
    }

    public static String internalNameByClassId(ClassId classId) {
        if (classId == null) {
            $$$reportNull$$$0(2);
        }
        FqName packageFqName = classId.getPackageFqName();
        String strReplace = classId.getRelativeClassName().asString().replace('.', Typography.dollar);
        if (!packageFqName.isRoot()) {
            strReplace = packageFqName.asString().replace('.', '/') + "/" + strReplace;
        }
        if (strReplace == null) {
            $$$reportNull$$$0(3);
        }
        return strReplace;
    }

    public static JvmClassName byFqNameWithoutInnerClasses(FqName fqName) {
        if (fqName == null) {
            $$$reportNull$$$0(4);
        }
        JvmClassName jvmClassName = new JvmClassName(fqName.asString().replace('.', '/'));
        jvmClassName.fqName = fqName;
        return jvmClassName;
    }

    private JvmClassName(String str) {
        if (str == null) {
            $$$reportNull$$$0(7);
        }
        this.internalName = str;
    }

    public FqName getFqNameForTopLevelClassMaybeWithDollars() {
        return new FqName(this.internalName.replace('/', '.'));
    }

    public FqName getPackageFqName() {
        int iLastIndexOf = this.internalName.lastIndexOf("/");
        if (iLastIndexOf == -1) {
            FqName fqName = FqName.ROOT;
            if (fqName == null) {
                $$$reportNull$$$0(9);
            }
            return fqName;
        }
        return new FqName(this.internalName.substring(0, iLastIndexOf).replace('/', '.'));
    }

    public String getInternalName() {
        String str = this.internalName;
        if (str == null) {
            $$$reportNull$$$0(10);
        }
        return str;
    }

    public String toString() {
        return this.internalName;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        return this.internalName.equals(((JvmClassName) obj).internalName);
    }

    public int hashCode() {
        return this.internalName.hashCode();
    }
}
