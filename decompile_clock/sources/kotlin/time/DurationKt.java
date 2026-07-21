package kotlin.time;

import com.xiaomi.onetrack.api.g;
import java.util.Collection;
import java.util.Iterator;
import kotlin.Metadata;
import kotlin.collections.IntIterator;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: Duration.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000:\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0002\b\t\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0010\f\n\u0002\b\u0015\u001a\u0019\u0010\u0000\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u0003\u001a\u00020\u0004H\u0007¢\u0006\u0002\u0010\u0005\u001a\u0019\u0010\u0000\u001a\u00020\u0001*\u00020\u00062\u0006\u0010\u0003\u001a\u00020\u0004H\u0007¢\u0006\u0002\u0010\u0007\u001a\u0019\u0010\u0000\u001a\u00020\u0001*\u00020\b2\u0006\u0010\u0003\u001a\u00020\u0004H\u0007¢\u0006\u0002\u0010\t\u001a\u001c\u0010\n\u001a\u00020\u0001*\u00020\u00022\u0006\u0010\u000b\u001a\u00020\u0001H\u0087\n¢\u0006\u0004\b\f\u0010\r\u001a\u001c\u0010\n\u001a\u00020\u0001*\u00020\b2\u0006\u0010\u000b\u001a\u00020\u0001H\u0087\n¢\u0006\u0004\b\u000e\u0010\u000f\u001a\u001d\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0002¢\u0006\u0002\u0010\u0015\u001a\u0010\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0011\u001a\u00020\u0012H\u0002\u001a)\u0010\u0017\u001a\u00020\u0012*\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u00022\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u00140\u001aH\u0082\b\u001a)\u0010\u001c\u001a\u00020\u0002*\u00020\u00122\u0006\u0010\u0018\u001a\u00020\u00022\u0012\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u001b\u0012\u0004\u0012\u00020\u00140\u001aH\u0082\b\u001a\u0010\u0010!\u001a\u00020\u00062\u0006\u0010\"\u001a\u00020\u0006H\u0002\u001a\u0010\u0010#\u001a\u00020\u00062\u0006\u0010$\u001a\u00020\u0006H\u0002\u001a\u0015\u0010%\u001a\u00020\u00012\u0006\u0010&\u001a\u00020\u0006H\u0002¢\u0006\u0002\u0010'\u001a\u0015\u0010(\u001a\u00020\u00012\u0006\u0010)\u001a\u00020\u0006H\u0002¢\u0006\u0002\u0010'\u001a\u001d\u0010*\u001a\u00020\u00012\u0006\u0010+\u001a\u00020\u00062\u0006\u0010,\u001a\u00020\u0002H\u0002¢\u0006\u0002\u0010-\u001a\u0015\u0010.\u001a\u00020\u00012\u0006\u0010\"\u001a\u00020\u0006H\u0002¢\u0006\u0002\u0010'\u001a\u0015\u0010/\u001a\u00020\u00012\u0006\u0010$\u001a\u00020\u0006H\u0002¢\u0006\u0002\u0010'\"\u000e\u0010\u001d\u001a\u00020\u0002X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u001e\u001a\u00020\u0006X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010\u001f\u001a\u00020\u0006X\u0080T¢\u0006\u0002\n\u0000\"\u000e\u0010 \u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"toDuration", "Lkotlin/time/Duration;", "", "unit", "Lkotlin/time/DurationUnit;", "(ILkotlin/time/DurationUnit;)J", "", "(JLkotlin/time/DurationUnit;)J", "", "(DLkotlin/time/DurationUnit;)J", "times", "duration", "times-mvk6XK0", "(IJ)J", "times-kIfJnKk", "(DJ)J", "parseDuration", g.p, "", "strictIso", "", "(Ljava/lang/String;Z)J", "parseOverLongIsoComponent", "substringWhile", "startIndex", "predicate", "Lkotlin/Function1;", "", "skipWhile", "NANOS_IN_MILLIS", "MAX_NANOS", "MAX_MILLIS", "MAX_NANOS_IN_MILLIS", "nanosToMillis", "nanos", "millisToNanos", "millis", "durationOfNanos", "normalNanos", "(J)J", "durationOfMillis", "normalMillis", "durationOf", "normalValue", "unitDiscriminator", "(JI)J", "durationOfNanosNormalized", "durationOfMillisNormalized", "kotlin-stdlib"}, k = 2, mv = {2, 1, 0}, xi = 48)
public final class DurationKt {
    public static final long MAX_MILLIS = 4611686018427387903L;
    public static final long MAX_NANOS = 4611686018426999999L;
    private static final long MAX_NANOS_IN_MILLIS = 4611686018426L;
    public static final int NANOS_IN_MILLIS = 1000000;

    /* JADX INFO: Access modifiers changed from: private */
    public static final long millisToNanos(long j) {
        return j * ((long) 1000000);
    }

    public static final long toDuration(int i, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        if (unit.compareTo(DurationUnit.SECONDS) <= 0) {
            return durationOfNanos(DurationUnitKt.convertDurationUnitOverflow(i, unit, DurationUnit.NANOSECONDS));
        }
        return toDuration(i, unit);
    }

    public static final long toDuration(long j, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        long jConvertDurationUnitOverflow = DurationUnitKt.convertDurationUnitOverflow(MAX_NANOS, DurationUnit.NANOSECONDS, unit);
        if ((-jConvertDurationUnitOverflow) <= j && j <= jConvertDurationUnitOverflow) {
            return durationOfNanos(DurationUnitKt.convertDurationUnitOverflow(j, unit, DurationUnit.NANOSECONDS));
        }
        return durationOfMillis(RangesKt.coerceIn(DurationUnitKt.convertDurationUnit(j, unit, DurationUnit.MILLISECONDS), -4611686018427387903L, MAX_MILLIS));
    }

    public static final long toDuration(double d, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        double dConvertDurationUnit = DurationUnitKt.convertDurationUnit(d, unit, DurationUnit.NANOSECONDS);
        if (!(!Double.isNaN(dConvertDurationUnit))) {
            throw new IllegalArgumentException("Duration value cannot be NaN.".toString());
        }
        long jRoundToLong = MathKt.roundToLong(dConvertDurationUnit);
        if (-4611686018426999999L <= jRoundToLong && jRoundToLong < 4611686018427000000L) {
            return durationOfNanos(jRoundToLong);
        }
        return durationOfMillisNormalized(MathKt.roundToLong(DurationUnitKt.convertDurationUnit(d, unit, DurationUnit.MILLISECONDS)));
    }

    /* JADX INFO: renamed from: times-mvk6XK0, reason: not valid java name */
    private static final long m1661timesmvk6XK0(int i, long j) {
        return Duration.m1595timesUwyO8pc(j, i);
    }

    /* JADX INFO: renamed from: times-kIfJnKk, reason: not valid java name */
    private static final long m1660timeskIfJnKk(double d, long j) {
        return Duration.m1594timesUwyO8pc(j, d);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long parseDuration(String str, boolean z) {
        boolean z2;
        char cCharAt;
        char cCharAt2;
        boolean z3;
        int length = str.length();
        if (length == 0) {
            throw new IllegalArgumentException("The string is empty");
        }
        long jM1655getZEROUwyO8pc = Duration.INSTANCE.m1655getZEROUwyO8pc();
        char cCharAt3 = str.charAt(0);
        int length2 = (cCharAt3 == '+' || cCharAt3 == '-') ? 1 : 0;
        boolean z4 = length2 > 0;
        Object obj = null;
        boolean z5 = z4 && StringsKt.startsWith$default((CharSequence) str, '-', false, 2, (Object) null);
        if (length <= length2) {
            throw new IllegalArgumentException("No components");
        }
        char c = ':';
        char c2 = '0';
        if (str.charAt(length2) == 'P') {
            int i = length2 + 1;
            if (i == length) {
                throw new IllegalArgumentException();
            }
            DurationUnit durationUnit = null;
            boolean z6 = false;
            while (i < length) {
                if (str.charAt(i) != 'T') {
                    int i2 = i;
                    while (true) {
                        if (i2 >= str.length()) {
                            z3 = z5;
                            break;
                        }
                        char cCharAt4 = str.charAt(i2);
                        if (c2 > cCharAt4 || cCharAt4 >= c) {
                            z3 = z5;
                            if (!StringsKt.contains$default((CharSequence) "+-.", cCharAt4, false, 2, obj)) {
                                break;
                            }
                        } else {
                            z3 = z5;
                        }
                        i2++;
                        z5 = z3;
                        c = ':';
                        c2 = '0';
                    }
                    Intrinsics.checkNotNull(str, "null cannot be cast to non-null type java.lang.String");
                    String strSubstring = str.substring(i, i2);
                    Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
                    String str2 = strSubstring;
                    if (str2.length() == 0) {
                        throw new IllegalArgumentException();
                    }
                    int length3 = i + strSubstring.length();
                    String str3 = str;
                    if (length3 < 0 || length3 >= str3.length()) {
                        throw new IllegalArgumentException("Missing unit for value " + strSubstring);
                    }
                    char cCharAt5 = str3.charAt(length3);
                    i = length3 + 1;
                    DurationUnit durationUnitDurationUnitByIsoChar = DurationUnitKt.durationUnitByIsoChar(cCharAt5, z6);
                    if (durationUnit != null && durationUnit.compareTo(durationUnitDurationUnitByIsoChar) <= 0) {
                        throw new IllegalArgumentException("Unexpected order of duration components");
                    }
                    int iIndexOf$default = StringsKt.indexOf$default((CharSequence) str2, '.', 0, false, 6, (Object) null);
                    if (durationUnitDurationUnitByIsoChar == DurationUnit.SECONDS && iIndexOf$default > 0) {
                        Intrinsics.checkNotNull(strSubstring, "null cannot be cast to non-null type java.lang.String");
                        String strSubstring2 = strSubstring.substring(0, iIndexOf$default);
                        Intrinsics.checkNotNullExpressionValue(strSubstring2, "substring(...)");
                        long jM1593plusLRDsOJo = Duration.m1593plusLRDsOJo(jM1655getZEROUwyO8pc, toDuration(parseOverLongIsoComponent(strSubstring2), durationUnitDurationUnitByIsoChar));
                        Intrinsics.checkNotNull(strSubstring, "null cannot be cast to non-null type java.lang.String");
                        String strSubstring3 = strSubstring.substring(iIndexOf$default);
                        Intrinsics.checkNotNullExpressionValue(strSubstring3, "substring(...)");
                        jM1655getZEROUwyO8pc = Duration.m1593plusLRDsOJo(jM1593plusLRDsOJo, toDuration(Double.parseDouble(strSubstring3), durationUnitDurationUnitByIsoChar));
                    } else {
                        jM1655getZEROUwyO8pc = Duration.m1593plusLRDsOJo(jM1655getZEROUwyO8pc, toDuration(parseOverLongIsoComponent(strSubstring), durationUnitDurationUnitByIsoChar));
                    }
                    durationUnit = durationUnitDurationUnitByIsoChar;
                    z5 = z3;
                    c = ':';
                    c2 = '0';
                    obj = null;
                } else {
                    if (z6 || (i = i + 1) == length) {
                        throw new IllegalArgumentException();
                    }
                    z6 = true;
                }
            }
            z2 = z5;
        } else {
            z2 = z5;
            if (z) {
                throw new IllegalArgumentException();
            }
            String str4 = "Unexpected order of duration components";
            if (StringsKt.regionMatches(str, length2, "Infinity", 0, Math.max(length - length2, 8), true)) {
                jM1655getZEROUwyO8pc = Duration.INSTANCE.m1653getINFINITEUwyO8pc();
            } else {
                boolean z7 = !z4;
                if (z4 && str.charAt(length2) == '(' && StringsKt.last(str) == ')') {
                    length2++;
                    length--;
                    if (length2 == length) {
                        throw new IllegalArgumentException("No components");
                    }
                    z7 = true;
                }
                DurationUnit durationUnit2 = null;
                boolean z8 = false;
                while (length2 < length) {
                    if (z8 && z7) {
                        while (length2 < str.length() && str.charAt(length2) == ' ') {
                            length2++;
                        }
                    }
                    int i3 = length2;
                    while (i3 < str.length() && (('0' <= (cCharAt2 = str.charAt(i3)) && cCharAt2 < ':') || cCharAt2 == '.')) {
                        i3++;
                    }
                    Intrinsics.checkNotNull(str, "null cannot be cast to non-null type java.lang.String");
                    String strSubstring4 = str.substring(length2, i3);
                    Intrinsics.checkNotNullExpressionValue(strSubstring4, "substring(...)");
                    String str5 = strSubstring4;
                    if (str5.length() == 0) {
                        throw new IllegalArgumentException();
                    }
                    int length4 = length2 + strSubstring4.length();
                    int i4 = length4;
                    while (i4 < str.length() && 'a' <= (cCharAt = str.charAt(i4)) && cCharAt < '{') {
                        i4++;
                    }
                    Intrinsics.checkNotNull(str, "null cannot be cast to non-null type java.lang.String");
                    String strSubstring5 = str.substring(length4, i4);
                    Intrinsics.checkNotNullExpressionValue(strSubstring5, "substring(...)");
                    length2 = length4 + strSubstring5.length();
                    DurationUnit durationUnitDurationUnitByShortName = DurationUnitKt.durationUnitByShortName(strSubstring5);
                    if (durationUnit2 != null && durationUnit2.compareTo(durationUnitDurationUnitByShortName) <= 0) {
                        throw new IllegalArgumentException(str4);
                    }
                    String str6 = str4;
                    int iIndexOf$default2 = StringsKt.indexOf$default((CharSequence) str5, '.', 0, false, 6, (Object) null);
                    if (iIndexOf$default2 > 0) {
                        Intrinsics.checkNotNull(strSubstring4, "null cannot be cast to non-null type java.lang.String");
                        String strSubstring6 = strSubstring4.substring(0, iIndexOf$default2);
                        Intrinsics.checkNotNullExpressionValue(strSubstring6, "substring(...)");
                        long jM1593plusLRDsOJo2 = Duration.m1593plusLRDsOJo(jM1655getZEROUwyO8pc, toDuration(Long.parseLong(strSubstring6), durationUnitDurationUnitByShortName));
                        Intrinsics.checkNotNull(strSubstring4, "null cannot be cast to non-null type java.lang.String");
                        String strSubstring7 = strSubstring4.substring(iIndexOf$default2);
                        Intrinsics.checkNotNullExpressionValue(strSubstring7, "substring(...)");
                        jM1655getZEROUwyO8pc = Duration.m1593plusLRDsOJo(jM1593plusLRDsOJo2, toDuration(Double.parseDouble(strSubstring7), durationUnitDurationUnitByShortName));
                        if (length2 < length) {
                            throw new IllegalArgumentException("Fractional component must be last");
                        }
                    } else {
                        jM1655getZEROUwyO8pc = Duration.m1593plusLRDsOJo(jM1655getZEROUwyO8pc, toDuration(Long.parseLong(strSubstring4), durationUnitDurationUnitByShortName));
                    }
                    str4 = str6;
                    durationUnit2 = durationUnitDurationUnitByShortName;
                    z8 = true;
                }
            }
        }
        return z2 ? Duration.m1608unaryMinusUwyO8pc(jM1655getZEROUwyO8pc) : jM1655getZEROUwyO8pc;
    }

    private static final long parseOverLongIsoComponent(String str) {
        int length = str.length();
        int i = (length <= 0 || !StringsKt.contains$default((CharSequence) "+-", str.charAt(0), false, 2, (Object) null)) ? 0 : 1;
        if (length - i > 16) {
            Iterable intRange = new IntRange(i, StringsKt.getLastIndex(str));
            if (!(intRange instanceof Collection) || !((Collection) intRange).isEmpty()) {
                Iterator it = intRange.iterator();
                while (true) {
                    if (it.hasNext()) {
                        char cCharAt = str.charAt(((IntIterator) it).nextInt());
                        if ('0' > cCharAt || cCharAt >= ':') {
                        }
                    }
                }
            }
            return str.charAt(0) == '-' ? Long.MIN_VALUE : Long.MAX_VALUE;
        }
        if (StringsKt.startsWith$default(str, "+", false, 2, (Object) null)) {
            str = StringsKt.drop(str, 1);
        }
        return Long.parseLong(str);
    }

    private static final int skipWhile(String str, int i, Function1<? super Character, Boolean> function1) {
        while (i < str.length() && function1.invoke(Character.valueOf(str.charAt(i))).booleanValue()) {
            i++;
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long nanosToMillis(long j) {
        return j / ((long) 1000000);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfNanos(long j) {
        return Duration.m1564constructorimpl(j << 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfMillis(long j) {
        return Duration.m1564constructorimpl((j << 1) + 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOf(long j, int i) {
        return Duration.m1564constructorimpl((j << 1) + ((long) i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfNanosNormalized(long j) {
        if (-4611686018426999999L <= j && j < 4611686018427000000L) {
            return durationOfNanos(j);
        }
        return durationOfMillis(nanosToMillis(j));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final long durationOfMillisNormalized(long j) {
        if (-4611686018426L <= j && j < 4611686018427L) {
            return durationOfNanos(millisToNanos(j));
        }
        return durationOfMillis(RangesKt.coerceIn(j, -4611686018427387903L, MAX_MILLIS));
    }

    private static final String substringWhile(String str, int i, Function1<? super Character, Boolean> function1) {
        int i2 = i;
        while (i2 < str.length() && function1.invoke(Character.valueOf(str.charAt(i2))).booleanValue()) {
            i2++;
        }
        Intrinsics.checkNotNull(str, "null cannot be cast to non-null type java.lang.String");
        String strSubstring = str.substring(i, i2);
        Intrinsics.checkNotNullExpressionValue(strSubstring, "substring(...)");
        return strSubstring;
    }
}
