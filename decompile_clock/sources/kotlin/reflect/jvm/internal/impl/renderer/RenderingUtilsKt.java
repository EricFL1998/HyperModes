package kotlin.reflect.jvm.internal.impl.renderer;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.FqNameUnsafe;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: RenderingUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class RenderingUtilsKt {
    public static final String render(Name name) {
        Intrinsics.checkNotNullParameter(name, "<this>");
        if (!shouldBeEscaped(name)) {
            String strAsString = name.asString();
            Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
            return strAsString;
        }
        StringBuilder sb = new StringBuilder();
        String strAsString2 = name.asString();
        Intrinsics.checkNotNullExpressionValue(strAsString2, "asString(...)");
        return sb.append("`" + strAsString2).append('`').toString();
    }

    private static final boolean shouldBeEscaped(Name name) {
        String strAsString = name.asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        if (!KeywordStringsGenerated.KEYWORDS.contains(strAsString)) {
            String str = strAsString;
            for (int i = 0; i < str.length(); i++) {
                char cCharAt = str.charAt(i);
                if (Character.isLetterOrDigit(cCharAt) || cCharAt == '_') {
                }
            }
            if (str.length() != 0 && Character.isJavaIdentifierStart(strAsString.codePointAt(0))) {
                return false;
            }
        }
        return true;
    }

    public static final String render(FqNameUnsafe fqNameUnsafe) {
        Intrinsics.checkNotNullParameter(fqNameUnsafe, "<this>");
        List<Name> listPathSegments = fqNameUnsafe.pathSegments();
        Intrinsics.checkNotNullExpressionValue(listPathSegments, "pathSegments(...)");
        return renderFqName(listPathSegments);
    }

    public static final String renderFqName(List<Name> pathSegments) {
        Intrinsics.checkNotNullParameter(pathSegments, "pathSegments");
        StringBuilder sb = new StringBuilder();
        for (Name name : pathSegments) {
            if (sb.length() > 0) {
                sb.append(".");
            }
            sb.append(render(name));
        }
        String string = sb.toString();
        Intrinsics.checkNotNullExpressionValue(string, "toString(...)");
        return string;
    }

    public static final String replacePrefixesInTypeRepresentations(String lowerRendered, String lowerPrefix, String upperRendered, String upperPrefix, String foldedPrefix) {
        Intrinsics.checkNotNullParameter(lowerRendered, "lowerRendered");
        Intrinsics.checkNotNullParameter(lowerPrefix, "lowerPrefix");
        Intrinsics.checkNotNullParameter(upperRendered, "upperRendered");
        Intrinsics.checkNotNullParameter(upperPrefix, "upperPrefix");
        Intrinsics.checkNotNullParameter(foldedPrefix, "foldedPrefix");
        if (StringsKt.startsWith$default(lowerRendered, lowerPrefix, false, 2, (Object) null) && StringsKt.startsWith$default(upperRendered, upperPrefix, false, 2, (Object) null)) {
            String strSubstring = lowerRendered.substring(lowerPrefix.length());
            Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
            String strSubstring2 = upperRendered.substring(upperPrefix.length());
            Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
            String str = foldedPrefix + strSubstring;
            if (Intrinsics.areEqual(strSubstring, strSubstring2)) {
                return str;
            }
            if (typeStringsDifferOnlyInNullability(strSubstring, strSubstring2)) {
                return str + '!';
            }
        }
        return null;
    }

    public static final boolean typeStringsDifferOnlyInNullability(String lower, String upper) {
        Intrinsics.checkNotNullParameter(lower, "lower");
        Intrinsics.checkNotNullParameter(upper, "upper");
        return Intrinsics.areEqual(lower, StringsKt.replace$default(upper, "?", "", false, 4, (Object) null)) || (StringsKt.endsWith$default(upper, "?", false, 2, (Object) null) && Intrinsics.areEqual(new StringBuilder().append(lower).append('?').toString(), upper)) || Intrinsics.areEqual(new StringBuilder("(").append(lower).append(")?").toString(), upper);
    }
}
