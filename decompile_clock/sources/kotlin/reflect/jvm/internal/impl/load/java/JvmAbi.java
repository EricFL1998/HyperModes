package kotlin.reflect.jvm.internal.impl.load.java;

import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.util.capitalizeDecapitalize.CapitalizeDecapitalizeKt;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: JvmAbi.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JvmAbi {
    public static final JvmAbi INSTANCE = new JvmAbi();
    public static final ClassId JVM_FIELD_ANNOTATION_CLASS_ID;
    public static final FqName JVM_FIELD_ANNOTATION_FQ_NAME;
    private static final ClassId REFLECTION_FACTORY_IMPL;
    private static final ClassId REPEATABLE_ANNOTATION_CONTAINER_META_ANNOTATION;

    private JvmAbi() {
    }

    static {
        FqName fqName = new FqName("kotlin.jvm.JvmField");
        JVM_FIELD_ANNOTATION_FQ_NAME = fqName;
        JVM_FIELD_ANNOTATION_CLASS_ID = ClassId.Companion.topLevel(fqName);
        REFLECTION_FACTORY_IMPL = ClassId.Companion.topLevel(new FqName("kotlin.reflect.jvm.internal.ReflectionFactoryImpl"));
        REPEATABLE_ANNOTATION_CONTAINER_META_ANNOTATION = ClassId.Companion.fromString$default(ClassId.Companion, "kotlin/jvm/internal/RepeatableContainer", false, 2, null);
    }

    public final ClassId getREPEATABLE_ANNOTATION_CONTAINER_META_ANNOTATION() {
        return REPEATABLE_ANNOTATION_CONTAINER_META_ANNOTATION;
    }

    @JvmStatic
    public static final boolean isGetterName(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return StringsKt.startsWith$default(name, "get", false, 2, (Object) null) || StringsKt.startsWith$default(name, "is", false, 2, (Object) null);
    }

    @JvmStatic
    public static final boolean isSetterName(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return StringsKt.startsWith$default(name, "set", false, 2, (Object) null);
    }

    @JvmStatic
    public static final String getterName(String propertyName) {
        Intrinsics.checkNotNullParameter(propertyName, "propertyName");
        return startsWithIsPrefix(propertyName) ? propertyName : "get" + CapitalizeDecapitalizeKt.capitalizeAsciiOnly(propertyName);
    }

    @JvmStatic
    public static final String setterName(String propertyName) {
        String strCapitalizeAsciiOnly;
        Intrinsics.checkNotNullParameter(propertyName, "propertyName");
        StringBuilder sb = new StringBuilder("set");
        if (startsWithIsPrefix(propertyName)) {
            strCapitalizeAsciiOnly = propertyName.substring(2);
            Intrinsics.checkNotNullExpressionValue(strCapitalizeAsciiOnly, "substring(...)");
        } else {
            strCapitalizeAsciiOnly = CapitalizeDecapitalizeKt.capitalizeAsciiOnly(propertyName);
        }
        return sb.append(strCapitalizeAsciiOnly).toString();
    }

    @JvmStatic
    public static final boolean startsWithIsPrefix(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        if (!StringsKt.startsWith$default(name, "is", false, 2, (Object) null) || name.length() == 2) {
            return false;
        }
        char cCharAt = name.charAt(2);
        return Intrinsics.compare(97, (int) cCharAt) > 0 || Intrinsics.compare((int) cCharAt, 122) > 0;
    }
}
