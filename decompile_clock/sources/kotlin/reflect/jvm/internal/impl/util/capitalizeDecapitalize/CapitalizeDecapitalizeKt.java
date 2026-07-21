package kotlin.reflect.jvm.internal.impl.util.capitalizeDecapitalize;

import java.util.Iterator;
import java.util.Locale;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: capitalizeDecapitalize.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class CapitalizeDecapitalizeKt {
    public static final String decapitalizeSmartForCompiler(String str, boolean z) {
        Integer next;
        Intrinsics.checkNotNullParameter(str, "<this>");
        String str2 = str;
        if (str2.length() == 0 || !isUpperCaseCharAt(str, 0, z)) {
            return str;
        }
        if (str.length() == 1 || !isUpperCaseCharAt(str, 1, z)) {
            if (z) {
                return decapitalizeAsciiOnly(str);
            }
            if (str2.length() <= 0) {
                return str;
            }
            char lowerCase = Character.toLowerCase(str.charAt(0));
            String strSubstring = str.substring(1);
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            return lowerCase + strSubstring;
        }
        Iterator<Integer> it = StringsKt.getIndices(str2).iterator();
        do {
            if (!it.hasNext()) {
                next = null;
                break;
            }
            next = it.next();
        } while (!(!isUpperCaseCharAt(str, next.intValue(), z)));
        Integer num = next;
        if (num == null) {
            return toLowerCase(str, z);
        }
        int iIntValue = num.intValue() - 1;
        StringBuilder sb = new StringBuilder();
        String strSubstring2 = str.substring(0, iIntValue);
        Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
        StringBuilder sbAppend = sb.append(toLowerCase(strSubstring2, z));
        String strSubstring3 = str.substring(iIntValue);
        Intrinsics.checkNotNullExpressionValue(strSubstring3, "substring(...)");
        return sbAppend.append(strSubstring3).toString();
    }

    private static final boolean isUpperCaseCharAt(String str, int i, boolean z) {
        char cCharAt = str.charAt(i);
        if (z) {
            return 'A' <= cCharAt && cCharAt < '[';
        }
        return Character.isUpperCase(cCharAt);
    }

    private static final String toLowerCase(String str, boolean z) {
        if (z) {
            return toLowerCaseAsciiOnly(str);
        }
        String lowerCase = str.toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue(lowerCase, "toLowerCase(...)");
        return lowerCase;
    }

    public static final String capitalizeAsciiOnly(String str) {
        char cCharAt;
        Intrinsics.checkNotNullParameter(str, "<this>");
        String str2 = str;
        if (str2.length() == 0 || 'a' > (cCharAt = str.charAt(0)) || cCharAt >= '{') {
            return str;
        }
        StringBuilder sb = new StringBuilder(str.length());
        sb.append(Character.toUpperCase(cCharAt));
        sb.append((CharSequence) str2, 1, str.length());
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public static final String decapitalizeAsciiOnly(String str) {
        char cCharAt;
        Intrinsics.checkNotNullParameter(str, "<this>");
        if (str.length() == 0 || 'A' > (cCharAt = str.charAt(0)) || cCharAt >= '[') {
            return str;
        }
        char lowerCase = Character.toLowerCase(cCharAt);
        String strSubstring = str.substring(1);
        Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
        return lowerCase + strSubstring;
    }

    public static final String toLowerCaseAsciiOnly(String str) {
        Intrinsics.checkNotNullParameter(str, "<this>");
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if ('A' <= cCharAt && cCharAt < '[') {
                cCharAt = Character.toLowerCase(cCharAt);
            }
            sb.append(cCharAt);
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }
}
