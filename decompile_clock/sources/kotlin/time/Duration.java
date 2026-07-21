package kotlin.time;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.micloudsdk.utils.MiCloudRuntimeConstants;
import com.xiaomi.onetrack.api.a;
import com.xiaomi.onetrack.api.as;
import com.xiaomi.onetrack.api.g;
import kotlin.Metadata;
import kotlin.comparisons.ComparisonsKt;
import kotlin.jvm.JvmInline;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.functions.Function4;
import kotlin.jvm.functions.Function5;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.math.MathKt;
import kotlin.ranges.LongRange;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: Duration.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000r\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u0006\n\u0002\b\u0019\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b%\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087@\u0018\u0000 \u0089\u00012\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0002\u0089\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\f\u001a\u00020\rH\u0002¢\u0006\u0004\b\u000e\u0010\u000fJ\u000f\u0010\u0010\u001a\u00020\rH\u0002¢\u0006\u0004\b\u0011\u0010\u000fJ\u0010\u0010\u0016\u001a\u00020\u0000H\u0086\u0002¢\u0006\u0004\b\u0017\u0010\u0005J\u0018\u0010\u0018\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u0000H\u0086\u0002¢\u0006\u0004\b\u001a\u0010\u001bJ\u001f\u0010\u001c\u001a\u00020\u00002\u0006\u0010\u001d\u001a\u00020\u00032\u0006\u0010\u001e\u001a\u00020\u0003H\u0002¢\u0006\u0004\b\u001f\u0010 J\u0018\u0010!\u001a\u00020\u00002\u0006\u0010\u0019\u001a\u00020\u0000H\u0086\u0002¢\u0006\u0004\b\"\u0010\u001bJ\u0018\u0010#\u001a\u00020\u00002\u0006\u0010$\u001a\u00020\tH\u0086\u0002¢\u0006\u0004\b%\u0010&J\u0018\u0010#\u001a\u00020\u00002\u0006\u0010$\u001a\u00020'H\u0086\u0002¢\u0006\u0004\b%\u0010(J\u0018\u0010)\u001a\u00020\u00002\u0006\u0010$\u001a\u00020\tH\u0086\u0002¢\u0006\u0004\b*\u0010&J\u0018\u0010)\u001a\u00020\u00002\u0006\u0010$\u001a\u00020'H\u0086\u0002¢\u0006\u0004\b*\u0010(J\u0018\u0010)\u001a\u00020'2\u0006\u0010\u0019\u001a\u00020\u0000H\u0086\u0002¢\u0006\u0004\b+\u0010,J\u0017\u0010-\u001a\u00020\u00002\u0006\u0010.\u001a\u00020\u0013H\u0000¢\u0006\u0004\b/\u00100J\r\u00101\u001a\u00020\r¢\u0006\u0004\b2\u0010\u000fJ\r\u00103\u001a\u00020\r¢\u0006\u0004\b4\u0010\u000fJ\r\u00105\u001a\u00020\r¢\u0006\u0004\b6\u0010\u000fJ\r\u00107\u001a\u00020\r¢\u0006\u0004\b8\u0010\u000fJ\u0018\u0010;\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u0000H\u0096\u0002¢\u0006\u0004\b<\u0010=J\u009d\u0001\u0010>\u001a\u0002H?\"\u0004\b\u0000\u0010?2u\u0010@\u001aq\u0012\u0013\u0012\u00110\u0003¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(D\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(E\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(F\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(G\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(H\u0012\u0004\u0012\u0002H?0AH\u0086\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0004\bI\u0010JJ\u0088\u0001\u0010>\u001a\u0002H?\"\u0004\b\u0000\u0010?2`\u0010@\u001a\\\u0012\u0013\u0012\u00110\u0003¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(E\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(F\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(G\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(H\u0012\u0004\u0012\u0002H?0KH\u0086\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0004\bI\u0010LJs\u0010>\u001a\u0002H?\"\u0004\b\u0000\u0010?2K\u0010@\u001aG\u0012\u0013\u0012\u00110\u0003¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(F\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(G\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(H\u0012\u0004\u0012\u0002H?0MH\u0086\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0004\bI\u0010NJ^\u0010>\u001a\u0002H?\"\u0004\b\u0000\u0010?26\u0010@\u001a2\u0012\u0013\u0012\u00110\u0003¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(G\u0012\u0013\u0012\u00110\t¢\u0006\f\bB\u0012\b\bC\u0012\u0004\b\b(H\u0012\u0004\u0012\u0002H?0OH\u0086\bø\u0001\u0000\u0082\u0002\n\n\b\b\u0001\u0012\u0002\u0010\u0001 \u0001¢\u0006\u0004\bI\u0010PJ\u0015\u0010^\u001a\u00020'2\u0006\u0010.\u001a\u00020\u0013¢\u0006\u0004\b_\u0010`J\u0015\u0010a\u001a\u00020\u00032\u0006\u0010.\u001a\u00020\u0013¢\u0006\u0004\bb\u00100J\u0015\u0010c\u001a\u00020\t2\u0006\u0010.\u001a\u00020\u0013¢\u0006\u0004\bd\u0010eJ\u000f\u0010t\u001a\u00020uH\u0016¢\u0006\u0004\bv\u0010wJA\u0010x\u001a\u00020y*\u00060zj\u0002`{2\u0006\u0010|\u001a\u00020\t2\u0006\u0010}\u001a\u00020\t2\u0006\u0010~\u001a\u00020\t2\u0006\u0010.\u001a\u00020u2\u0006\u0010\u007f\u001a\u00020\rH\u0002¢\u0006\u0006\b\u0080\u0001\u0010\u0081\u0001J!\u0010t\u001a\u00020u2\u0006\u0010.\u001a\u00020\u00132\t\b\u0002\u0010\u0082\u0001\u001a\u00020\t¢\u0006\u0005\bv\u0010\u0083\u0001J\u000f\u0010\u0084\u0001\u001a\u00020u¢\u0006\u0005\b\u0085\u0001\u0010wJ\u0015\u0010\u0086\u0001\u001a\u00020\r2\t\u0010\u0019\u001a\u0005\u0018\u00010\u0087\u0001HÖ\u0003J\n\u0010\u0088\u0001\u001a\u00020\tHÖ\u0001R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u00020\u00038BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0007\u0010\u0005R\u0015\u0010\b\u001a\u00020\t8Â\u0002X\u0082\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\u0012\u001a\u00020\u00138BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u0011\u00109\u001a\u00020\u00008F¢\u0006\u0006\u001a\u0004\b:\u0010\u0005R\u001a\u0010Q\u001a\u00020\t8@X\u0081\u0004¢\u0006\f\u0012\u0004\bR\u0010S\u001a\u0004\bT\u0010\u000bR\u001a\u0010U\u001a\u00020\t8@X\u0081\u0004¢\u0006\f\u0012\u0004\bV\u0010S\u001a\u0004\bW\u0010\u000bR\u001a\u0010X\u001a\u00020\t8@X\u0081\u0004¢\u0006\f\u0012\u0004\bY\u0010S\u001a\u0004\bZ\u0010\u000bR\u001a\u0010[\u001a\u00020\t8@X\u0081\u0004¢\u0006\f\u0012\u0004\b\\\u0010S\u001a\u0004\b]\u0010\u000bR\u0011\u0010f\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bg\u0010\u0005R\u0011\u0010h\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bi\u0010\u0005R\u0011\u0010j\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bk\u0010\u0005R\u0011\u0010l\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bm\u0010\u0005R\u0011\u0010n\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bo\u0010\u0005R\u0011\u0010p\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bq\u0010\u0005R\u0011\u0010r\u001a\u00020\u00038F¢\u0006\u0006\u001a\u0004\bs\u0010\u0005\u0088\u0001\u0002\u0092\u0001\u00020\u0003\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u008a\u0001"}, d2 = {"Lkotlin/time/Duration;", "", "rawValue", "", "constructor-impl", "(J)J", g.p, "getValue-impl", "unitDiscriminator", "", "getUnitDiscriminator-impl", "(J)I", "isInNanos", "", "isInNanos-impl", "(J)Z", "isInMillis", "isInMillis-impl", "storageUnit", "Lkotlin/time/DurationUnit;", "getStorageUnit-impl", "(J)Lkotlin/time/DurationUnit;", "unaryMinus", "unaryMinus-UwyO8pc", "plus", SearchContract.SearchResultColumn.COLUMN_OTHER, "plus-LRDsOJo", "(JJ)J", "addValuesMixedRanges", "thisMillis", "otherNanos", "addValuesMixedRanges-UwyO8pc", "(JJJ)J", "minus", "minus-LRDsOJo", "times", "scale", "times-UwyO8pc", "(JI)J", "", "(JD)J", "div", "div-UwyO8pc", "div-LRDsOJo", "(JJ)D", "truncateTo", "unit", "truncateTo-UwyO8pc$kotlin_stdlib", "(JLkotlin/time/DurationUnit;)J", "isNegative", "isNegative-impl", "isPositive", "isPositive-impl", "isInfinite", "isInfinite-impl", "isFinite", "isFinite-impl", "absoluteValue", "getAbsoluteValue-UwyO8pc", "compareTo", "compareTo-LRDsOJo", "(JJ)I", "toComponents", "T", a.a, "Lkotlin/Function5;", "Lkotlin/ParameterName;", as.a, "days", "hours", "minutes", "seconds", "nanoseconds", "toComponents-impl", "(JLkotlin/jvm/functions/Function5;)Ljava/lang/Object;", "Lkotlin/Function4;", "(JLkotlin/jvm/functions/Function4;)Ljava/lang/Object;", "Lkotlin/Function3;", "(JLkotlin/jvm/functions/Function3;)Ljava/lang/Object;", "Lkotlin/Function2;", "(JLkotlin/jvm/functions/Function2;)Ljava/lang/Object;", "hoursComponent", "getHoursComponent$annotations", "()V", "getHoursComponent-impl", "minutesComponent", "getMinutesComponent$annotations", "getMinutesComponent-impl", "secondsComponent", "getSecondsComponent$annotations", "getSecondsComponent-impl", "nanosecondsComponent", "getNanosecondsComponent$annotations", "getNanosecondsComponent-impl", "toDouble", "toDouble-impl", "(JLkotlin/time/DurationUnit;)D", "toLong", "toLong-impl", "toInt", "toInt-impl", "(JLkotlin/time/DurationUnit;)I", "inWholeDays", "getInWholeDays-impl", "inWholeHours", "getInWholeHours-impl", "inWholeMinutes", "getInWholeMinutes-impl", "inWholeSeconds", "getInWholeSeconds-impl", "inWholeMilliseconds", "getInWholeMilliseconds-impl", "inWholeMicroseconds", "getInWholeMicroseconds-impl", "inWholeNanoseconds", "getInWholeNanoseconds-impl", "toString", "", "toString-impl", "(J)Ljava/lang/String;", "appendFractional", "", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "whole", "fractional", "fractionalSize", "isoZeroes", "appendFractional-impl", "(JLjava/lang/StringBuilder;IIILjava/lang/String;Z)V", "decimals", "(JLkotlin/time/DurationUnit;I)Ljava/lang/String;", "toIsoString", "toIsoString-impl", "equals", "", "hashCode", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
@JvmInline
public final class Duration implements Comparable<Duration> {
    private final long rawValue;

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final long ZERO = m1564constructorimpl(0);
    private static final long INFINITE = DurationKt.durationOfMillis(DurationKt.MAX_MILLIS);
    private static final long NEG_INFINITE = DurationKt.durationOfMillis(-4611686018427387903L);

    /* JADX INFO: renamed from: box-impl, reason: not valid java name */
    public static final /* synthetic */ Duration m1562boximpl(long j) {
        return new Duration(j);
    }

    /* JADX INFO: renamed from: equals-impl, reason: not valid java name */
    public static boolean m1568equalsimpl(long j, Object obj) {
        return (obj instanceof Duration) && j == ((Duration) obj).getRawValue();
    }

    /* JADX INFO: renamed from: equals-impl0, reason: not valid java name */
    public static final boolean m1569equalsimpl0(long j, long j2) {
        return j == j2;
    }

    public static /* synthetic */ void getHoursComponent$annotations() {
    }

    public static /* synthetic */ void getMinutesComponent$annotations() {
    }

    public static /* synthetic */ void getNanosecondsComponent$annotations() {
    }

    public static /* synthetic */ void getSecondsComponent$annotations() {
    }

    /* JADX INFO: renamed from: getUnitDiscriminator-impl, reason: not valid java name */
    private static final int m1583getUnitDiscriminatorimpl(long j) {
        return ((int) j) & 1;
    }

    /* JADX INFO: renamed from: getValue-impl, reason: not valid java name */
    private static final long m1584getValueimpl(long j) {
        return j >> 1;
    }

    /* JADX INFO: renamed from: hashCode-impl, reason: not valid java name */
    public static int m1585hashCodeimpl(long j) {
        return Long.hashCode(j);
    }

    /* JADX INFO: renamed from: isInMillis-impl, reason: not valid java name */
    private static final boolean m1587isInMillisimpl(long j) {
        return (((int) j) & 1) == 1;
    }

    /* JADX INFO: renamed from: isInNanos-impl, reason: not valid java name */
    private static final boolean m1588isInNanosimpl(long j) {
        return (((int) j) & 1) == 0;
    }

    /* JADX INFO: renamed from: isNegative-impl, reason: not valid java name */
    public static final boolean m1590isNegativeimpl(long j) {
        return j < 0;
    }

    /* JADX INFO: renamed from: isPositive-impl, reason: not valid java name */
    public static final boolean m1591isPositiveimpl(long j) {
        return j > 0;
    }

    public boolean equals(Object other) {
        return m1568equalsimpl(this.rawValue, other);
    }

    public int hashCode() {
        return m1585hashCodeimpl(this.rawValue);
    }

    /* JADX INFO: renamed from: unbox-impl, reason: not valid java name and from getter */
    public final /* synthetic */ long getRawValue() {
        return this.rawValue;
    }

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(Duration duration) {
        return m1609compareToLRDsOJo(duration.getRawValue());
    }

    private /* synthetic */ Duration(long j) {
        this.rawValue = j;
    }

    /* JADX INFO: renamed from: getStorageUnit-impl, reason: not valid java name */
    private static final DurationUnit m1582getStorageUnitimpl(long j) {
        return m1588isInNanosimpl(j) ? DurationUnit.NANOSECONDS : DurationUnit.MILLISECONDS;
    }

    /* JADX INFO: renamed from: constructor-impl, reason: not valid java name */
    public static long m1564constructorimpl(long j) {
        if (DurationJvmKt.getDurationAssertionsEnabled()) {
            if (m1588isInNanosimpl(j)) {
                long jM1584getValueimpl = m1584getValueimpl(j);
                if (-4611686018426999999L > jM1584getValueimpl || jM1584getValueimpl >= 4611686018427000000L) {
                    throw new AssertionError(m1584getValueimpl(j) + " ns is out of nanoseconds range");
                }
            } else {
                long jM1584getValueimpl2 = m1584getValueimpl(j);
                if (-4611686018427387903L > jM1584getValueimpl2 || jM1584getValueimpl2 >= 4611686018427387904L) {
                    throw new AssertionError(m1584getValueimpl(j) + " ms is out of milliseconds range");
                }
                long jM1584getValueimpl3 = m1584getValueimpl(j);
                if (-4611686018426L <= jM1584getValueimpl3 && jM1584getValueimpl3 < 4611686018427L) {
                    throw new AssertionError(m1584getValueimpl(j) + " ms is denormalized");
                }
            }
        }
        return j;
    }

    /* JADX INFO: compiled from: Duration.kt */
    @Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0017\n\u0002\u0010\u000e\n\u0002\b\t\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J \u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0007J\u0015\u00100\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u000201¢\u0006\u0004\b2\u00103J\u0015\u00104\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u000201¢\u0006\u0004\b5\u00103J\u0015\u00106\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u000201¢\u0006\u0002\b7J\u0015\u00108\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u000f\u001a\u000201¢\u0006\u0002\b9R\u0013\u0010\u0004\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\u0006\u0010\u0007R\u0013\u0010\t\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\n\u0010\u0007R\u0016\u0010\u000b\u001a\u00020\u0005X\u0080\u0004¢\u0006\n\n\u0002\u0010\b\u001a\u0004\b\f\u0010\u0007R\u001f\u0010\u0013\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\u0015\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018R\u001f\u0010\u0013\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\u0015\u0010\u001a\u001a\u0004\b\u0017\u0010\u001bR\u001f\u0010\u0013\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\u0015\u0010\u001c\u001a\u0004\b\u0017\u0010\u001dR\u001f\u0010\u001e\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\u001f\u0010\u0016\u001a\u0004\b \u0010\u0018R\u001f\u0010\u001e\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\u001f\u0010\u001a\u001a\u0004\b \u0010\u001bR\u001f\u0010\u001e\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\u001f\u0010\u001c\u001a\u0004\b \u0010\u001dR\u001f\u0010!\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\"\u0010\u0016\u001a\u0004\b#\u0010\u0018R\u001f\u0010!\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\"\u0010\u001a\u001a\u0004\b#\u0010\u001bR\u001f\u0010!\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b\"\u0010\u001c\u001a\u0004\b#\u0010\u001dR\u001f\u0010$\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b%\u0010\u0016\u001a\u0004\b&\u0010\u0018R\u001f\u0010$\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b%\u0010\u001a\u001a\u0004\b&\u0010\u001bR\u001f\u0010$\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b%\u0010\u001c\u001a\u0004\b&\u0010\u001dR\u001f\u0010'\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b(\u0010\u0016\u001a\u0004\b)\u0010\u0018R\u001f\u0010'\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b(\u0010\u001a\u001a\u0004\b)\u0010\u001bR\u001f\u0010'\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b(\u0010\u001c\u001a\u0004\b)\u0010\u001dR\u001f\u0010*\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b+\u0010\u0016\u001a\u0004\b,\u0010\u0018R\u001f\u0010*\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b+\u0010\u001a\u001a\u0004\b,\u0010\u001bR\u001f\u0010*\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b+\u0010\u001c\u001a\u0004\b,\u0010\u001dR\u001f\u0010-\u001a\u00020\u0005*\u00020\u00148Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b.\u0010\u0016\u001a\u0004\b/\u0010\u0018R\u001f\u0010-\u001a\u00020\u0005*\u00020\u00198Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b.\u0010\u001a\u001a\u0004\b/\u0010\u001bR\u001f\u0010-\u001a\u00020\u0005*\u00020\u000e8Æ\u0002X\u0087\u0004¢\u0006\f\u0012\u0004\b.\u0010\u001c\u001a\u0004\b/\u0010\u001d¨\u0006:"}, d2 = {"Lkotlin/time/Duration$Companion;", "", "<init>", "()V", "ZERO", "Lkotlin/time/Duration;", "getZERO-UwyO8pc", "()J", "J", "INFINITE", "getINFINITE-UwyO8pc", "NEG_INFINITE", "getNEG_INFINITE-UwyO8pc$kotlin_stdlib", "convert", "", g.p, "sourceUnit", "Lkotlin/time/DurationUnit;", "targetUnit", "nanoseconds", "", "getNanoseconds-UwyO8pc$annotations", "(I)V", "getNanoseconds-UwyO8pc", "(I)J", "", "(J)V", "(J)J", "(D)V", "(D)J", "microseconds", "getMicroseconds-UwyO8pc$annotations", "getMicroseconds-UwyO8pc", "milliseconds", "getMilliseconds-UwyO8pc$annotations", "getMilliseconds-UwyO8pc", "seconds", "getSeconds-UwyO8pc$annotations", "getSeconds-UwyO8pc", "minutes", "getMinutes-UwyO8pc$annotations", "getMinutes-UwyO8pc", "hours", "getHours-UwyO8pc$annotations", "getHours-UwyO8pc", "days", "getDays-UwyO8pc$annotations", "getDays-UwyO8pc", "parse", "", "parse-UwyO8pc", "(Ljava/lang/String;)J", "parseIsoString", "parseIsoString-UwyO8pc", "parseOrNull", "parseOrNull-FghU774", "parseIsoStringOrNull", "parseIsoStringOrNull-FghU774", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* JADX INFO: renamed from: getDays-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1614getDaysUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getDays-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1615getDaysUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getDays-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1616getDaysUwyO8pc$annotations(long j) {
        }

        /* JADX INFO: renamed from: getHours-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1620getHoursUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getHours-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1621getHoursUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getHours-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1622getHoursUwyO8pc$annotations(long j) {
        }

        /* JADX INFO: renamed from: getMicroseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1626getMicrosecondsUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getMicroseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1627getMicrosecondsUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getMicroseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1628getMicrosecondsUwyO8pc$annotations(long j) {
        }

        /* JADX INFO: renamed from: getMilliseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1632getMillisecondsUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getMilliseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1633getMillisecondsUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getMilliseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1634getMillisecondsUwyO8pc$annotations(long j) {
        }

        /* JADX INFO: renamed from: getMinutes-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1638getMinutesUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getMinutes-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1639getMinutesUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getMinutes-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1640getMinutesUwyO8pc$annotations(long j) {
        }

        /* JADX INFO: renamed from: getNanoseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1644getNanosecondsUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getNanoseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1645getNanosecondsUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getNanoseconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1646getNanosecondsUwyO8pc$annotations(long j) {
        }

        /* JADX INFO: renamed from: getSeconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1650getSecondsUwyO8pc$annotations(double d) {
        }

        /* JADX INFO: renamed from: getSeconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1651getSecondsUwyO8pc$annotations(int i) {
        }

        /* JADX INFO: renamed from: getSeconds-UwyO8pc$annotations, reason: not valid java name */
        public static /* synthetic */ void m1652getSecondsUwyO8pc$annotations(long j) {
        }

        private Companion() {
        }

        /* JADX INFO: renamed from: getZERO-UwyO8pc, reason: not valid java name */
        public final long m1655getZEROUwyO8pc() {
            return Duration.ZERO;
        }

        /* JADX INFO: renamed from: getINFINITE-UwyO8pc, reason: not valid java name */
        public final long m1653getINFINITEUwyO8pc() {
            return Duration.INFINITE;
        }

        /* JADX INFO: renamed from: getNEG_INFINITE-UwyO8pc$kotlin_stdlib, reason: not valid java name */
        public final long m1654getNEG_INFINITEUwyO8pc$kotlin_stdlib() {
            return Duration.NEG_INFINITE;
        }

        public final double convert(double value, DurationUnit sourceUnit, DurationUnit targetUnit) {
            Intrinsics.checkNotNullParameter(sourceUnit, "sourceUnit");
            Intrinsics.checkNotNullParameter(targetUnit, "targetUnit");
            return DurationUnitKt.convertDurationUnit(value, sourceUnit, targetUnit);
        }

        /* JADX INFO: renamed from: getNanoseconds-UwyO8pc, reason: not valid java name */
        private final long m1642getNanosecondsUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.NANOSECONDS);
        }

        /* JADX INFO: renamed from: getNanoseconds-UwyO8pc, reason: not valid java name */
        private final long m1643getNanosecondsUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.NANOSECONDS);
        }

        /* JADX INFO: renamed from: getNanoseconds-UwyO8pc, reason: not valid java name */
        private final long m1641getNanosecondsUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.NANOSECONDS);
        }

        /* JADX INFO: renamed from: getMicroseconds-UwyO8pc, reason: not valid java name */
        private final long m1624getMicrosecondsUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.MICROSECONDS);
        }

        /* JADX INFO: renamed from: getMicroseconds-UwyO8pc, reason: not valid java name */
        private final long m1625getMicrosecondsUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.MICROSECONDS);
        }

        /* JADX INFO: renamed from: getMicroseconds-UwyO8pc, reason: not valid java name */
        private final long m1623getMicrosecondsUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.MICROSECONDS);
        }

        /* JADX INFO: renamed from: getMilliseconds-UwyO8pc, reason: not valid java name */
        private final long m1630getMillisecondsUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.MILLISECONDS);
        }

        /* JADX INFO: renamed from: getMilliseconds-UwyO8pc, reason: not valid java name */
        private final long m1631getMillisecondsUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.MILLISECONDS);
        }

        /* JADX INFO: renamed from: getMilliseconds-UwyO8pc, reason: not valid java name */
        private final long m1629getMillisecondsUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.MILLISECONDS);
        }

        /* JADX INFO: renamed from: getSeconds-UwyO8pc, reason: not valid java name */
        private final long m1648getSecondsUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.SECONDS);
        }

        /* JADX INFO: renamed from: getSeconds-UwyO8pc, reason: not valid java name */
        private final long m1649getSecondsUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.SECONDS);
        }

        /* JADX INFO: renamed from: getSeconds-UwyO8pc, reason: not valid java name */
        private final long m1647getSecondsUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.SECONDS);
        }

        /* JADX INFO: renamed from: getMinutes-UwyO8pc, reason: not valid java name */
        private final long m1636getMinutesUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.MINUTES);
        }

        /* JADX INFO: renamed from: getMinutes-UwyO8pc, reason: not valid java name */
        private final long m1637getMinutesUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.MINUTES);
        }

        /* JADX INFO: renamed from: getMinutes-UwyO8pc, reason: not valid java name */
        private final long m1635getMinutesUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.MINUTES);
        }

        /* JADX INFO: renamed from: getHours-UwyO8pc, reason: not valid java name */
        private final long m1618getHoursUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.HOURS);
        }

        /* JADX INFO: renamed from: getHours-UwyO8pc, reason: not valid java name */
        private final long m1619getHoursUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.HOURS);
        }

        /* JADX INFO: renamed from: getHours-UwyO8pc, reason: not valid java name */
        private final long m1617getHoursUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.HOURS);
        }

        /* JADX INFO: renamed from: getDays-UwyO8pc, reason: not valid java name */
        private final long m1612getDaysUwyO8pc(int i) {
            return DurationKt.toDuration(i, DurationUnit.DAYS);
        }

        /* JADX INFO: renamed from: getDays-UwyO8pc, reason: not valid java name */
        private final long m1613getDaysUwyO8pc(long j) {
            return DurationKt.toDuration(j, DurationUnit.DAYS);
        }

        /* JADX INFO: renamed from: getDays-UwyO8pc, reason: not valid java name */
        private final long m1611getDaysUwyO8pc(double d) {
            return DurationKt.toDuration(d, DurationUnit.DAYS);
        }

        /* JADX INFO: renamed from: parse-UwyO8pc, reason: not valid java name */
        public final long m1656parseUwyO8pc(String value) {
            Intrinsics.checkNotNullParameter(value, "value");
            try {
                return DurationKt.parseDuration(value, false);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid duration string format: '" + value + "'.", e);
            }
        }

        /* JADX INFO: renamed from: parseIsoString-UwyO8pc, reason: not valid java name */
        public final long m1657parseIsoStringUwyO8pc(String value) {
            Intrinsics.checkNotNullParameter(value, "value");
            try {
                return DurationKt.parseDuration(value, true);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid ISO duration string format: '" + value + "'.", e);
            }
        }

        /* JADX INFO: renamed from: parseOrNull-FghU774, reason: not valid java name */
        public final Duration m1659parseOrNullFghU774(String value) {
            Intrinsics.checkNotNullParameter(value, "value");
            try {
                return Duration.m1562boximpl(DurationKt.parseDuration(value, false));
            } catch (IllegalArgumentException unused) {
                return null;
            }
        }

        /* JADX INFO: renamed from: parseIsoStringOrNull-FghU774, reason: not valid java name */
        public final Duration m1658parseIsoStringOrNullFghU774(String value) {
            Intrinsics.checkNotNullParameter(value, "value");
            try {
                return Duration.m1562boximpl(DurationKt.parseDuration(value, true));
            } catch (IllegalArgumentException unused) {
                return null;
            }
        }
    }

    /* JADX INFO: renamed from: unaryMinus-UwyO8pc, reason: not valid java name */
    public static final long m1608unaryMinusUwyO8pc(long j) {
        return DurationKt.durationOf(-m1584getValueimpl(j), ((int) j) & 1);
    }

    /* JADX INFO: renamed from: plus-LRDsOJo, reason: not valid java name */
    public static final long m1593plusLRDsOJo(long j, long j2) {
        if (m1589isInfiniteimpl(j)) {
            if (m1586isFiniteimpl(j2) || (j2 ^ j) >= 0) {
                return j;
            }
            throw new IllegalArgumentException("Summing infinite durations of different signs yields an undefined result.");
        }
        if (m1589isInfiniteimpl(j2)) {
            return j2;
        }
        if ((((int) j) & 1) == (((int) j2) & 1)) {
            long jM1584getValueimpl = m1584getValueimpl(j) + m1584getValueimpl(j2);
            return m1588isInNanosimpl(j) ? DurationKt.durationOfNanosNormalized(jM1584getValueimpl) : DurationKt.durationOfMillisNormalized(jM1584getValueimpl);
        }
        if (m1587isInMillisimpl(j)) {
            return m1560addValuesMixedRangesUwyO8pc(j, m1584getValueimpl(j), m1584getValueimpl(j2));
        }
        return m1560addValuesMixedRangesUwyO8pc(j, m1584getValueimpl(j2), m1584getValueimpl(j));
    }

    /* JADX INFO: renamed from: addValuesMixedRanges-UwyO8pc, reason: not valid java name */
    private static final long m1560addValuesMixedRangesUwyO8pc(long j, long j2, long j3) {
        long jNanosToMillis = DurationKt.nanosToMillis(j3);
        long j4 = j2 + jNanosToMillis;
        if (-4611686018426L > j4 || j4 >= 4611686018427L) {
            return DurationKt.durationOfMillis(RangesKt.coerceIn(j4, -4611686018427387903L, DurationKt.MAX_MILLIS));
        }
        return DurationKt.durationOfNanos(DurationKt.millisToNanos(j4) + (j3 - DurationKt.millisToNanos(jNanosToMillis)));
    }

    /* JADX INFO: renamed from: minus-LRDsOJo, reason: not valid java name */
    public static final long m1592minusLRDsOJo(long j, long j2) {
        return m1593plusLRDsOJo(j, m1608unaryMinusUwyO8pc(j2));
    }

    /* JADX INFO: renamed from: times-UwyO8pc, reason: not valid java name */
    public static final long m1595timesUwyO8pc(long j, int i) {
        if (m1589isInfiniteimpl(j)) {
            if (i != 0) {
                return i > 0 ? j : m1608unaryMinusUwyO8pc(j);
            }
            throw new IllegalArgumentException("Multiplying infinite duration by zero yields an undefined result.");
        }
        if (i == 0) {
            return ZERO;
        }
        long jM1584getValueimpl = m1584getValueimpl(j);
        long j2 = i;
        long j3 = jM1584getValueimpl * j2;
        if (!m1588isInNanosimpl(j)) {
            if (j3 / j2 == jM1584getValueimpl) {
                return DurationKt.durationOfMillis(RangesKt.coerceIn(j3, new LongRange(-4611686018427387903L, DurationKt.MAX_MILLIS)));
            }
            return MathKt.getSign(jM1584getValueimpl) * MathKt.getSign(i) > 0 ? INFINITE : NEG_INFINITE;
        }
        if (-2147483647L <= jM1584getValueimpl && jM1584getValueimpl < 2147483648L) {
            return DurationKt.durationOfNanos(j3);
        }
        if (j3 / j2 == jM1584getValueimpl) {
            return DurationKt.durationOfNanosNormalized(j3);
        }
        long jNanosToMillis = DurationKt.nanosToMillis(jM1584getValueimpl);
        long j4 = jNanosToMillis * j2;
        long jNanosToMillis2 = DurationKt.nanosToMillis((jM1584getValueimpl - DurationKt.millisToNanos(jNanosToMillis)) * j2) + j4;
        if (j4 / j2 != jNanosToMillis || (jNanosToMillis2 ^ j4) < 0) {
            return MathKt.getSign(jM1584getValueimpl) * MathKt.getSign(i) > 0 ? INFINITE : NEG_INFINITE;
        }
        return DurationKt.durationOfMillis(RangesKt.coerceIn(jNanosToMillis2, new LongRange(-4611686018427387903L, DurationKt.MAX_MILLIS)));
    }

    /* JADX INFO: renamed from: times-UwyO8pc, reason: not valid java name */
    public static final long m1594timesUwyO8pc(long j, double d) {
        int iRoundToInt = MathKt.roundToInt(d);
        if (iRoundToInt == d) {
            return m1595timesUwyO8pc(j, iRoundToInt);
        }
        DurationUnit durationUnitM1582getStorageUnitimpl = m1582getStorageUnitimpl(j);
        return DurationKt.toDuration(m1600toDoubleimpl(j, durationUnitM1582getStorageUnitimpl) * d, durationUnitM1582getStorageUnitimpl);
    }

    /* JADX INFO: renamed from: div-UwyO8pc, reason: not valid java name */
    public static final long m1567divUwyO8pc(long j, int i) {
        if (i == 0) {
            if (m1591isPositiveimpl(j)) {
                return INFINITE;
            }
            if (m1590isNegativeimpl(j)) {
                return NEG_INFINITE;
            }
            throw new IllegalArgumentException("Dividing zero duration by zero yields an undefined result.");
        }
        if (m1588isInNanosimpl(j)) {
            return DurationKt.durationOfNanos(m1584getValueimpl(j) / ((long) i));
        }
        if (m1589isInfiniteimpl(j)) {
            return m1595timesUwyO8pc(j, MathKt.getSign(i));
        }
        long j2 = i;
        long jM1584getValueimpl = m1584getValueimpl(j) / j2;
        if (-4611686018426L > jM1584getValueimpl || jM1584getValueimpl >= 4611686018427L) {
            return DurationKt.durationOfMillis(jM1584getValueimpl);
        }
        return DurationKt.durationOfNanos(DurationKt.millisToNanos(jM1584getValueimpl) + (DurationKt.millisToNanos(m1584getValueimpl(j) - (jM1584getValueimpl * j2)) / j2));
    }

    /* JADX INFO: renamed from: div-UwyO8pc, reason: not valid java name */
    public static final long m1566divUwyO8pc(long j, double d) {
        int iRoundToInt = MathKt.roundToInt(d);
        if (iRoundToInt == d && iRoundToInt != 0) {
            return m1567divUwyO8pc(j, iRoundToInt);
        }
        DurationUnit durationUnitM1582getStorageUnitimpl = m1582getStorageUnitimpl(j);
        return DurationKt.toDuration(m1600toDoubleimpl(j, durationUnitM1582getStorageUnitimpl) / d, durationUnitM1582getStorageUnitimpl);
    }

    /* JADX INFO: renamed from: div-LRDsOJo, reason: not valid java name */
    public static final double m1565divLRDsOJo(long j, long j2) {
        DurationUnit durationUnit = (DurationUnit) ComparisonsKt.maxOf(m1582getStorageUnitimpl(j), m1582getStorageUnitimpl(j2));
        return m1600toDoubleimpl(j, durationUnit) / m1600toDoubleimpl(j2, durationUnit);
    }

    /* JADX INFO: renamed from: truncateTo-UwyO8pc$kotlin_stdlib, reason: not valid java name */
    public static final long m1607truncateToUwyO8pc$kotlin_stdlib(long j, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        DurationUnit durationUnitM1582getStorageUnitimpl = m1582getStorageUnitimpl(j);
        if (unit.compareTo(durationUnitM1582getStorageUnitimpl) <= 0 || m1589isInfiniteimpl(j)) {
            return j;
        }
        return DurationKt.toDuration(m1584getValueimpl(j) - (m1584getValueimpl(j) % DurationUnitKt.convertDurationUnit(1L, unit, durationUnitM1582getStorageUnitimpl)), durationUnitM1582getStorageUnitimpl);
    }

    /* JADX INFO: renamed from: isInfinite-impl, reason: not valid java name */
    public static final boolean m1589isInfiniteimpl(long j) {
        return j == INFINITE || j == NEG_INFINITE;
    }

    /* JADX INFO: renamed from: isFinite-impl, reason: not valid java name */
    public static final boolean m1586isFiniteimpl(long j) {
        return !m1589isInfiniteimpl(j);
    }

    /* JADX INFO: renamed from: getAbsoluteValue-UwyO8pc, reason: not valid java name */
    public static final long m1570getAbsoluteValueUwyO8pc(long j) {
        return m1590isNegativeimpl(j) ? m1608unaryMinusUwyO8pc(j) : j;
    }

    /* JADX INFO: renamed from: compareTo-LRDsOJo, reason: not valid java name */
    public int m1609compareToLRDsOJo(long j) {
        return m1563compareToLRDsOJo(this.rawValue, j);
    }

    /* JADX INFO: renamed from: compareTo-LRDsOJo, reason: not valid java name */
    public static int m1563compareToLRDsOJo(long j, long j2) {
        long j3 = j ^ j2;
        if (j3 < 0 || (((int) j3) & 1) == 0) {
            return Intrinsics.compare(j, j2);
        }
        int i = (((int) j) & 1) - (((int) j2) & 1);
        return m1590isNegativeimpl(j) ? -i : i;
    }

    /* JADX INFO: renamed from: toComponents-impl, reason: not valid java name */
    public static final <T> T m1599toComponentsimpl(long j, Function5<? super Long, ? super Integer, ? super Integer, ? super Integer, ? super Integer, ? extends T> action) {
        Intrinsics.checkNotNullParameter(action, "action");
        return action.invoke(Long.valueOf(m1572getInWholeDaysimpl(j)), Integer.valueOf(m1571getHoursComponentimpl(j)), Integer.valueOf(m1579getMinutesComponentimpl(j)), Integer.valueOf(m1581getSecondsComponentimpl(j)), Integer.valueOf(m1580getNanosecondsComponentimpl(j)));
    }

    /* JADX INFO: renamed from: toComponents-impl, reason: not valid java name */
    public static final <T> T m1598toComponentsimpl(long j, Function4<? super Long, ? super Integer, ? super Integer, ? super Integer, ? extends T> action) {
        Intrinsics.checkNotNullParameter(action, "action");
        return action.invoke(Long.valueOf(m1573getInWholeHoursimpl(j)), Integer.valueOf(m1579getMinutesComponentimpl(j)), Integer.valueOf(m1581getSecondsComponentimpl(j)), Integer.valueOf(m1580getNanosecondsComponentimpl(j)));
    }

    /* JADX INFO: renamed from: toComponents-impl, reason: not valid java name */
    public static final <T> T m1597toComponentsimpl(long j, Function3<? super Long, ? super Integer, ? super Integer, ? extends T> action) {
        Intrinsics.checkNotNullParameter(action, "action");
        return action.invoke(Long.valueOf(m1576getInWholeMinutesimpl(j)), Integer.valueOf(m1581getSecondsComponentimpl(j)), Integer.valueOf(m1580getNanosecondsComponentimpl(j)));
    }

    /* JADX INFO: renamed from: toComponents-impl, reason: not valid java name */
    public static final <T> T m1596toComponentsimpl(long j, Function2<? super Long, ? super Integer, ? extends T> action) {
        Intrinsics.checkNotNullParameter(action, "action");
        return action.invoke(Long.valueOf(m1578getInWholeSecondsimpl(j)), Integer.valueOf(m1580getNanosecondsComponentimpl(j)));
    }

    /* JADX INFO: renamed from: getHoursComponent-impl, reason: not valid java name */
    public static final int m1571getHoursComponentimpl(long j) {
        if (m1589isInfiniteimpl(j)) {
            return 0;
        }
        return (int) (m1573getInWholeHoursimpl(j) % ((long) 24));
    }

    /* JADX INFO: renamed from: getMinutesComponent-impl, reason: not valid java name */
    public static final int m1579getMinutesComponentimpl(long j) {
        if (m1589isInfiniteimpl(j)) {
            return 0;
        }
        return (int) (m1576getInWholeMinutesimpl(j) % ((long) 60));
    }

    /* JADX INFO: renamed from: getSecondsComponent-impl, reason: not valid java name */
    public static final int m1581getSecondsComponentimpl(long j) {
        if (m1589isInfiniteimpl(j)) {
            return 0;
        }
        return (int) (m1578getInWholeSecondsimpl(j) % ((long) 60));
    }

    /* JADX INFO: renamed from: getNanosecondsComponent-impl, reason: not valid java name */
    public static final int m1580getNanosecondsComponentimpl(long j) {
        long jM1584getValueimpl;
        if (m1589isInfiniteimpl(j)) {
            return 0;
        }
        if (m1587isInMillisimpl(j)) {
            jM1584getValueimpl = DurationKt.millisToNanos(m1584getValueimpl(j) % ((long) 1000));
        } else {
            jM1584getValueimpl = m1584getValueimpl(j) % ((long) 1000000000);
        }
        return (int) jM1584getValueimpl;
    }

    /* JADX INFO: renamed from: toDouble-impl, reason: not valid java name */
    public static final double m1600toDoubleimpl(long j, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        if (j == INFINITE) {
            return Double.POSITIVE_INFINITY;
        }
        if (j == NEG_INFINITE) {
            return Double.NEGATIVE_INFINITY;
        }
        return DurationUnitKt.convertDurationUnit(m1584getValueimpl(j), m1582getStorageUnitimpl(j), unit);
    }

    /* JADX INFO: renamed from: toLong-impl, reason: not valid java name */
    public static final long m1603toLongimpl(long j, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        if (j == INFINITE) {
            return Long.MAX_VALUE;
        }
        if (j == NEG_INFINITE) {
            return Long.MIN_VALUE;
        }
        return DurationUnitKt.convertDurationUnit(m1584getValueimpl(j), m1582getStorageUnitimpl(j), unit);
    }

    /* JADX INFO: renamed from: toInt-impl, reason: not valid java name */
    public static final int m1601toIntimpl(long j, DurationUnit unit) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        return (int) RangesKt.coerceIn(m1603toLongimpl(j, unit), -2147483648L, 2147483647L);
    }

    /* JADX INFO: renamed from: getInWholeDays-impl, reason: not valid java name */
    public static final long m1572getInWholeDaysimpl(long j) {
        return m1603toLongimpl(j, DurationUnit.DAYS);
    }

    /* JADX INFO: renamed from: getInWholeHours-impl, reason: not valid java name */
    public static final long m1573getInWholeHoursimpl(long j) {
        return m1603toLongimpl(j, DurationUnit.HOURS);
    }

    /* JADX INFO: renamed from: getInWholeMinutes-impl, reason: not valid java name */
    public static final long m1576getInWholeMinutesimpl(long j) {
        return m1603toLongimpl(j, DurationUnit.MINUTES);
    }

    /* JADX INFO: renamed from: getInWholeSeconds-impl, reason: not valid java name */
    public static final long m1578getInWholeSecondsimpl(long j) {
        return m1603toLongimpl(j, DurationUnit.SECONDS);
    }

    /* JADX INFO: renamed from: getInWholeMilliseconds-impl, reason: not valid java name */
    public static final long m1575getInWholeMillisecondsimpl(long j) {
        return (m1587isInMillisimpl(j) && m1586isFiniteimpl(j)) ? m1584getValueimpl(j) : m1603toLongimpl(j, DurationUnit.MILLISECONDS);
    }

    /* JADX INFO: renamed from: getInWholeMicroseconds-impl, reason: not valid java name */
    public static final long m1574getInWholeMicrosecondsimpl(long j) {
        return m1603toLongimpl(j, DurationUnit.MICROSECONDS);
    }

    /* JADX INFO: renamed from: getInWholeNanoseconds-impl, reason: not valid java name */
    public static final long m1577getInWholeNanosecondsimpl(long j) {
        long jM1584getValueimpl = m1584getValueimpl(j);
        if (m1588isInNanosimpl(j)) {
            return jM1584getValueimpl;
        }
        if (jM1584getValueimpl > 9223372036854L) {
            return Long.MAX_VALUE;
        }
        if (jM1584getValueimpl < -9223372036854L) {
            return Long.MIN_VALUE;
        }
        return DurationKt.millisToNanos(jM1584getValueimpl);
    }

    public String toString() {
        return m1604toStringimpl(this.rawValue);
    }

    /* JADX INFO: renamed from: toString-impl, reason: not valid java name */
    public static String m1604toStringimpl(long j) {
        if (j == 0) {
            return "0s";
        }
        if (j == INFINITE) {
            return "Infinity";
        }
        if (j == NEG_INFINITE) {
            return "-Infinity";
        }
        boolean zM1590isNegativeimpl = m1590isNegativeimpl(j);
        StringBuilder sb = new StringBuilder();
        if (zM1590isNegativeimpl) {
            sb.append('-');
        }
        long jM1570getAbsoluteValueUwyO8pc = m1570getAbsoluteValueUwyO8pc(j);
        long jM1572getInWholeDaysimpl = m1572getInWholeDaysimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1571getHoursComponentimpl = m1571getHoursComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1579getMinutesComponentimpl = m1579getMinutesComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1581getSecondsComponentimpl = m1581getSecondsComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1580getNanosecondsComponentimpl = m1580getNanosecondsComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        int i = 0;
        boolean z = jM1572getInWholeDaysimpl != 0;
        boolean z2 = iM1571getHoursComponentimpl != 0;
        boolean z3 = iM1579getMinutesComponentimpl != 0;
        boolean z4 = (iM1581getSecondsComponentimpl == 0 && iM1580getNanosecondsComponentimpl == 0) ? false : true;
        if (z) {
            sb.append(jM1572getInWholeDaysimpl).append('d');
            i = 1;
        }
        if (z2 || (z && (z3 || z4))) {
            int i2 = i + 1;
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(iM1571getHoursComponentimpl).append('h');
            i = i2;
        }
        if (z3 || (z4 && (z2 || z))) {
            int i3 = i + 1;
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(iM1579getMinutesComponentimpl).append('m');
            i = i3;
        }
        if (z4) {
            int i4 = i + 1;
            if (i > 0) {
                sb.append(' ');
            }
            if (iM1581getSecondsComponentimpl != 0 || z || z2 || z3) {
                m1561appendFractionalimpl(j, sb, iM1581getSecondsComponentimpl, iM1580getNanosecondsComponentimpl, 9, MiCloudRuntimeConstants.PUSH.WATERMARK_TYPE.SUBSCRIPTION, false);
            } else if (iM1580getNanosecondsComponentimpl >= 1000000) {
                m1561appendFractionalimpl(j, sb, iM1580getNanosecondsComponentimpl / 1000000, iM1580getNanosecondsComponentimpl % 1000000, 6, "ms", false);
            } else if (iM1580getNanosecondsComponentimpl >= 1000) {
                m1561appendFractionalimpl(j, sb, iM1580getNanosecondsComponentimpl / 1000, iM1580getNanosecondsComponentimpl % 1000, 3, "us", false);
            } else {
                sb.append(iM1580getNanosecondsComponentimpl).append("ns");
            }
            i = i4;
        }
        if (zM1590isNegativeimpl && i > 1) {
            sb.insert(1, '(').append(')');
        }
        return sb.toString();
    }

    /* JADX INFO: renamed from: appendFractional-impl, reason: not valid java name */
    private static final void m1561appendFractionalimpl(long j, StringBuilder sb, int i, int i2, int i3, String str, boolean z) {
        sb.append(i);
        if (i2 != 0) {
            sb.append('.');
            String strPadStart = StringsKt.padStart(String.valueOf(i2), i3, '0');
            int i4 = -1;
            int length = strPadStart.length() - 1;
            if (length >= 0) {
                while (true) {
                    int i5 = length - 1;
                    if (strPadStart.charAt(length) != '0') {
                        i4 = length;
                        break;
                    } else if (i5 < 0) {
                        break;
                    } else {
                        length = i5;
                    }
                }
            }
            int i6 = i4 + 1;
            if (z || i6 >= 3) {
                Intrinsics.checkNotNullExpressionValue(sb.append((CharSequence) strPadStart, 0, ((i4 + 3) / 3) * 3), "append(...)");
            } else {
                Intrinsics.checkNotNullExpressionValue(sb.append((CharSequence) strPadStart, 0, i6), "append(...)");
            }
        }
        sb.append(str);
    }

    /* JADX INFO: renamed from: toString-impl$default, reason: not valid java name */
    public static /* synthetic */ String m1606toStringimpl$default(long j, DurationUnit durationUnit, int i, int i2, Object obj) {
        if ((i2 & 2) != 0) {
            i = 0;
        }
        return m1605toStringimpl(j, durationUnit, i);
    }

    /* JADX INFO: renamed from: toString-impl, reason: not valid java name */
    public static final String m1605toStringimpl(long j, DurationUnit unit, int i) {
        Intrinsics.checkNotNullParameter(unit, "unit");
        if (i < 0) {
            throw new IllegalArgumentException(("decimals must be not negative, but was " + i).toString());
        }
        double dM1600toDoubleimpl = m1600toDoubleimpl(j, unit);
        return Double.isInfinite(dM1600toDoubleimpl) ? String.valueOf(dM1600toDoubleimpl) : DurationJvmKt.formatToExactDecimals(dM1600toDoubleimpl, RangesKt.coerceAtMost(i, 12)) + DurationUnitKt.shortName(unit);
    }

    /* JADX INFO: renamed from: toIsoString-impl, reason: not valid java name */
    public static final String m1602toIsoStringimpl(long j) {
        StringBuilder sb = new StringBuilder();
        if (m1590isNegativeimpl(j)) {
            sb.append('-');
        }
        sb.append("PT");
        long jM1570getAbsoluteValueUwyO8pc = m1570getAbsoluteValueUwyO8pc(j);
        long jM1573getInWholeHoursimpl = m1573getInWholeHoursimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1579getMinutesComponentimpl = m1579getMinutesComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1581getSecondsComponentimpl = m1581getSecondsComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        int iM1580getNanosecondsComponentimpl = m1580getNanosecondsComponentimpl(jM1570getAbsoluteValueUwyO8pc);
        if (m1589isInfiniteimpl(j)) {
            jM1573getInWholeHoursimpl = 9999999999999L;
        }
        boolean z = true;
        boolean z2 = jM1573getInWholeHoursimpl != 0;
        boolean z3 = (iM1581getSecondsComponentimpl == 0 && iM1580getNanosecondsComponentimpl == 0) ? false : true;
        if (iM1579getMinutesComponentimpl == 0 && (!z3 || !z2)) {
            z = false;
        }
        if (z2) {
            sb.append(jM1573getInWholeHoursimpl).append('H');
        }
        if (z) {
            sb.append(iM1579getMinutesComponentimpl).append('M');
        }
        if (z3 || (!z2 && !z)) {
            m1561appendFractionalimpl(j, sb, iM1581getSecondsComponentimpl, iM1580getNanosecondsComponentimpl, 9, "S", true);
        }
        return sb.toString();
    }
}
