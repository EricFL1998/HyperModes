package kotlin;

import com.android.deskclock.settings.SearchContract;
import kotlin.jvm.JvmInline;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.UIntRange;
import kotlin.ranges.URangesKt;

/* JADX INFO: compiled from: UByte.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u000f\n\u0000\n\u0002\u0010\u0005\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b-\n\u0002\u0018\u0002\n\u0002\b\u000f\n\u0002\u0010\n\n\u0002\b\u0006\n\u0002\u0010\t\n\u0002\b\u000b\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087@\u0018\u0000 s2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001sB\u0011\b\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0000H\u0097\n¢\u0006\u0004\b\u000b\u0010\fJ\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\rH\u0087\n¢\u0006\u0004\b\u000e\u0010\u000fJ\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0010H\u0087\n¢\u0006\u0004\b\u0011\u0010\u0012J\u0018\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0013H\u0087\n¢\u0006\u0004\b\u0014\u0010\u0015J\u0018\u0010\u0016\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b\u0017\u0010\fJ\u0018\u0010\u0016\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\rH\u0087\n¢\u0006\u0004\b\u0018\u0010\u000fJ\u0018\u0010\u0016\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\n¢\u0006\u0004\b\u0019\u0010\u0012J\u0018\u0010\u0016\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\n¢\u0006\u0004\b\u001a\u0010\u001bJ\u0018\u0010\u001c\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b\u001d\u0010\fJ\u0018\u0010\u001c\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\rH\u0087\n¢\u0006\u0004\b\u001e\u0010\u000fJ\u0018\u0010\u001c\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\n¢\u0006\u0004\b\u001f\u0010\u0012J\u0018\u0010\u001c\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\n¢\u0006\u0004\b \u0010\u001bJ\u0018\u0010!\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b\"\u0010\fJ\u0018\u0010!\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\rH\u0087\n¢\u0006\u0004\b#\u0010\u000fJ\u0018\u0010!\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\n¢\u0006\u0004\b$\u0010\u0012J\u0018\u0010!\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\n¢\u0006\u0004\b%\u0010\u001bJ\u0018\u0010&\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b'\u0010\fJ\u0018\u0010&\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\rH\u0087\n¢\u0006\u0004\b(\u0010\u000fJ\u0018\u0010&\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\n¢\u0006\u0004\b)\u0010\u0012J\u0018\u0010&\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\n¢\u0006\u0004\b*\u0010\u001bJ\u0018\u0010+\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b,\u0010\fJ\u0018\u0010+\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\rH\u0087\n¢\u0006\u0004\b-\u0010\u000fJ\u0018\u0010+\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\n¢\u0006\u0004\b.\u0010\u0012J\u0018\u0010+\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\n¢\u0006\u0004\b/\u0010\u001bJ\u0018\u00100\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0000H\u0087\b¢\u0006\u0004\b1\u0010\fJ\u0018\u00100\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\rH\u0087\b¢\u0006\u0004\b2\u0010\u000fJ\u0018\u00100\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\b¢\u0006\u0004\b3\u0010\u0012J\u0018\u00100\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\b¢\u0006\u0004\b4\u0010\u001bJ\u0018\u00105\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0000H\u0087\b¢\u0006\u0004\b6\u00107J\u0018\u00105\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\rH\u0087\b¢\u0006\u0004\b8\u00109J\u0018\u00105\u001a\u00020\u00102\u0006\u0010\n\u001a\u00020\u0010H\u0087\b¢\u0006\u0004\b:\u0010\u0012J\u0018\u00105\u001a\u00020\u00132\u0006\u0010\n\u001a\u00020\u0013H\u0087\b¢\u0006\u0004\b;\u0010\u001bJ\u0010\u0010<\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b=\u0010\u0005J\u0010\u0010>\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\b?\u0010\u0005J\u0018\u0010@\u001a\u00020A2\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\bB\u0010CJ\u0018\u0010D\u001a\u00020A2\u0006\u0010\n\u001a\u00020\u0000H\u0087\n¢\u0006\u0004\bE\u0010CJ\u0018\u0010F\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0000H\u0087\f¢\u0006\u0004\bG\u00107J\u0018\u0010H\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0000H\u0087\f¢\u0006\u0004\bI\u00107J\u0018\u0010J\u001a\u00020\u00002\u0006\u0010\n\u001a\u00020\u0000H\u0087\f¢\u0006\u0004\bK\u00107J\u0010\u0010L\u001a\u00020\u0000H\u0087\b¢\u0006\u0004\bM\u0010\u0005J\u0010\u0010N\u001a\u00020\u0003H\u0087\b¢\u0006\u0004\bO\u0010\u0005J\u0010\u0010P\u001a\u00020QH\u0087\b¢\u0006\u0004\bR\u0010SJ\u0010\u0010T\u001a\u00020\tH\u0087\b¢\u0006\u0004\bU\u0010VJ\u0010\u0010W\u001a\u00020XH\u0087\b¢\u0006\u0004\bY\u0010ZJ\u0010\u0010[\u001a\u00020\u0000H\u0087\b¢\u0006\u0004\b\\\u0010\u0005J\u0010\u0010]\u001a\u00020\rH\u0087\b¢\u0006\u0004\b^\u0010SJ\u0010\u0010_\u001a\u00020\u0010H\u0087\b¢\u0006\u0004\b`\u0010VJ\u0010\u0010a\u001a\u00020\u0013H\u0087\b¢\u0006\u0004\bb\u0010ZJ\u0010\u0010c\u001a\u00020dH\u0087\b¢\u0006\u0004\be\u0010fJ\u0010\u0010g\u001a\u00020hH\u0087\b¢\u0006\u0004\bi\u0010jJ\u000f\u0010k\u001a\u00020lH\u0016¢\u0006\u0004\bm\u0010nJ\u0013\u0010o\u001a\u00020p2\b\u0010\n\u001a\u0004\u0018\u00010qHÖ\u0003J\t\u0010r\u001a\u00020\tHÖ\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0000X\u0081\u0004¢\u0006\b\n\u0000\u0012\u0004\b\u0006\u0010\u0007\u0088\u0001\u0002\u0092\u0001\u00020\u0003¨\u0006t"}, d2 = {"Lkotlin/UByte;", "", "data", "", "constructor-impl", "(B)B", "getData$annotations", "()V", "compareTo", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "compareTo-7apg3OU", "(BB)I", "Lkotlin/UShort;", "compareTo-xj2QHRw", "(BS)I", "Lkotlin/UInt;", "compareTo-WZ4Q5Ns", "(BI)I", "Lkotlin/ULong;", "compareTo-VKZWuLQ", "(BJ)I", "plus", "plus-7apg3OU", "plus-xj2QHRw", "plus-WZ4Q5Ns", "plus-VKZWuLQ", "(BJ)J", "minus", "minus-7apg3OU", "minus-xj2QHRw", "minus-WZ4Q5Ns", "minus-VKZWuLQ", "times", "times-7apg3OU", "times-xj2QHRw", "times-WZ4Q5Ns", "times-VKZWuLQ", "div", "div-7apg3OU", "div-xj2QHRw", "div-WZ4Q5Ns", "div-VKZWuLQ", "rem", "rem-7apg3OU", "rem-xj2QHRw", "rem-WZ4Q5Ns", "rem-VKZWuLQ", "floorDiv", "floorDiv-7apg3OU", "floorDiv-xj2QHRw", "floorDiv-WZ4Q5Ns", "floorDiv-VKZWuLQ", "mod", "mod-7apg3OU", "(BB)B", "mod-xj2QHRw", "(BS)S", "mod-WZ4Q5Ns", "mod-VKZWuLQ", "inc", "inc-w2LRezQ", "dec", "dec-w2LRezQ", "rangeTo", "Lkotlin/ranges/UIntRange;", "rangeTo-7apg3OU", "(BB)Lkotlin/ranges/UIntRange;", "rangeUntil", "rangeUntil-7apg3OU", "and", "and-7apg3OU", "or", "or-7apg3OU", "xor", "xor-7apg3OU", "inv", "inv-w2LRezQ", "toByte", "toByte-impl", "toShort", "", "toShort-impl", "(B)S", "toInt", "toInt-impl", "(B)I", "toLong", "", "toLong-impl", "(B)J", "toUByte", "toUByte-w2LRezQ", "toUShort", "toUShort-Mh2AYeg", "toUInt", "toUInt-pVg5ArA", "toULong", "toULong-s-VKNKU", "toFloat", "", "toFloat-impl", "(B)F", "toDouble", "", "toDouble-impl", "(B)D", "toString", "", "toString-impl", "(B)Ljava/lang/String;", "equals", "", "", "hashCode", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
@JvmInline
public final class UByte implements Comparable<UByte> {
    public static final byte MAX_VALUE = -1;
    public static final byte MIN_VALUE = 0;
    public static final int SIZE_BITS = 8;
    public static final int SIZE_BYTES = 1;
    private final byte data;

    /* JADX INFO: renamed from: box-impl, reason: not valid java name */
    public static final /* synthetic */ UByte m216boximpl(byte b) {
        return new UByte(b);
    }

    /* JADX INFO: renamed from: constructor-impl, reason: not valid java name */
    public static byte m222constructorimpl(byte b) {
        return b;
    }

    /* JADX INFO: renamed from: equals-impl, reason: not valid java name */
    public static boolean m228equalsimpl(byte b, Object obj) {
        return (obj instanceof UByte) && b == ((UByte) obj).getData();
    }

    /* JADX INFO: renamed from: equals-impl0, reason: not valid java name */
    public static final boolean m229equalsimpl0(byte b, byte b2) {
        return b == b2;
    }

    public static /* synthetic */ void getData$annotations() {
    }

    /* JADX INFO: renamed from: hashCode-impl, reason: not valid java name */
    public static int m234hashCodeimpl(byte b) {
        return Byte.hashCode(b);
    }

    /* JADX INFO: renamed from: toByte-impl, reason: not valid java name */
    private static final byte m260toByteimpl(byte b) {
        return b;
    }

    /* JADX INFO: renamed from: toInt-impl, reason: not valid java name */
    private static final int m263toIntimpl(byte b) {
        return b & 255;
    }

    /* JADX INFO: renamed from: toLong-impl, reason: not valid java name */
    private static final long m264toLongimpl(byte b) {
        return ((long) b) & 255;
    }

    /* JADX INFO: renamed from: toShort-impl, reason: not valid java name */
    private static final short m265toShortimpl(byte b) {
        return (short) (b & 255);
    }

    /* JADX INFO: renamed from: toUByte-w2LRezQ, reason: not valid java name */
    private static final byte m267toUBytew2LRezQ(byte b) {
        return b;
    }

    public boolean equals(Object other) {
        return m228equalsimpl(this.data, other);
    }

    public int hashCode() {
        return m234hashCodeimpl(this.data);
    }

    /* JADX INFO: renamed from: unbox-impl, reason: not valid java name and from getter */
    public final /* synthetic */ byte getData() {
        return this.data;
    }

    @Override // java.lang.Comparable
    public /* bridge */ /* synthetic */ int compareTo(UByte uByte) {
        return Intrinsics.compare(getData() & 255, uByte.getData() & 255);
    }

    private /* synthetic */ UByte(byte b) {
        this.data = b;
    }

    /* JADX INFO: renamed from: compareTo-7apg3OU, reason: not valid java name */
    private int m217compareTo7apg3OU(byte b) {
        return Intrinsics.compare(getData() & 255, b & 255);
    }

    /* JADX INFO: renamed from: compareTo-7apg3OU, reason: not valid java name */
    private static int m218compareTo7apg3OU(byte b, byte b2) {
        return Intrinsics.compare(b & 255, b2 & 255);
    }

    /* JADX INFO: renamed from: compareTo-xj2QHRw, reason: not valid java name */
    private static final int m221compareToxj2QHRw(byte b, short s) {
        return Intrinsics.compare(b & 255, s & UShort.MAX_VALUE);
    }

    /* JADX INFO: renamed from: compareTo-WZ4Q5Ns, reason: not valid java name */
    private static final int m220compareToWZ4Q5Ns(byte b, int i) {
        return Integer.compareUnsigned(UInt.m299constructorimpl(b & 255), i);
    }

    /* JADX INFO: renamed from: compareTo-VKZWuLQ, reason: not valid java name */
    private static final int m219compareToVKZWuLQ(byte b, long j) {
        return Long.compareUnsigned(ULong.m378constructorimpl(((long) b) & 255), j);
    }

    /* JADX INFO: renamed from: plus-7apg3OU, reason: not valid java name */
    private static final int m246plus7apg3OU(byte b, byte b2) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) + UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: plus-xj2QHRw, reason: not valid java name */
    private static final int m249plusxj2QHRw(byte b, short s) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) + UInt.m299constructorimpl(s & UShort.MAX_VALUE));
    }

    /* JADX INFO: renamed from: plus-WZ4Q5Ns, reason: not valid java name */
    private static final int m248plusWZ4Q5Ns(byte b, int i) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) + i);
    }

    /* JADX INFO: renamed from: plus-VKZWuLQ, reason: not valid java name */
    private static final long m247plusVKZWuLQ(byte b, long j) {
        return ULong.m378constructorimpl(ULong.m378constructorimpl(((long) b) & 255) + j);
    }

    /* JADX INFO: renamed from: minus-7apg3OU, reason: not valid java name */
    private static final int m237minus7apg3OU(byte b, byte b2) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) - UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: minus-xj2QHRw, reason: not valid java name */
    private static final int m240minusxj2QHRw(byte b, short s) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) - UInt.m299constructorimpl(s & UShort.MAX_VALUE));
    }

    /* JADX INFO: renamed from: minus-WZ4Q5Ns, reason: not valid java name */
    private static final int m239minusWZ4Q5Ns(byte b, int i) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) - i);
    }

    /* JADX INFO: renamed from: minus-VKZWuLQ, reason: not valid java name */
    private static final long m238minusVKZWuLQ(byte b, long j) {
        return ULong.m378constructorimpl(ULong.m378constructorimpl(((long) b) & 255) - j);
    }

    /* JADX INFO: renamed from: times-7apg3OU, reason: not valid java name */
    private static final int m256times7apg3OU(byte b, byte b2) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) * UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: times-xj2QHRw, reason: not valid java name */
    private static final int m259timesxj2QHRw(byte b, short s) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) * UInt.m299constructorimpl(s & UShort.MAX_VALUE));
    }

    /* JADX INFO: renamed from: times-WZ4Q5Ns, reason: not valid java name */
    private static final int m258timesWZ4Q5Ns(byte b, int i) {
        return UInt.m299constructorimpl(UInt.m299constructorimpl(b & 255) * i);
    }

    /* JADX INFO: renamed from: times-VKZWuLQ, reason: not valid java name */
    private static final long m257timesVKZWuLQ(byte b, long j) {
        return ULong.m378constructorimpl(ULong.m378constructorimpl(((long) b) & 255) * j);
    }

    /* JADX INFO: renamed from: div-7apg3OU, reason: not valid java name */
    private static final int m224div7apg3OU(byte b, byte b2) {
        return Integer.divideUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: div-xj2QHRw, reason: not valid java name */
    private static final int m227divxj2QHRw(byte b, short s) {
        return Integer.divideUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(s & UShort.MAX_VALUE));
    }

    /* JADX INFO: renamed from: div-WZ4Q5Ns, reason: not valid java name */
    private static final int m226divWZ4Q5Ns(byte b, int i) {
        return Integer.divideUnsigned(UInt.m299constructorimpl(b & 255), i);
    }

    /* JADX INFO: renamed from: div-VKZWuLQ, reason: not valid java name */
    private static final long m225divVKZWuLQ(byte b, long j) {
        return Long.divideUnsigned(ULong.m378constructorimpl(((long) b) & 255), j);
    }

    /* JADX INFO: renamed from: rem-7apg3OU, reason: not valid java name */
    private static final int m252rem7apg3OU(byte b, byte b2) {
        return Integer.remainderUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: rem-xj2QHRw, reason: not valid java name */
    private static final int m255remxj2QHRw(byte b, short s) {
        return Integer.remainderUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(s & UShort.MAX_VALUE));
    }

    /* JADX INFO: renamed from: rem-WZ4Q5Ns, reason: not valid java name */
    private static final int m254remWZ4Q5Ns(byte b, int i) {
        return Integer.remainderUnsigned(UInt.m299constructorimpl(b & 255), i);
    }

    /* JADX INFO: renamed from: rem-VKZWuLQ, reason: not valid java name */
    private static final long m253remVKZWuLQ(byte b, long j) {
        return Long.remainderUnsigned(ULong.m378constructorimpl(((long) b) & 255), j);
    }

    /* JADX INFO: renamed from: floorDiv-7apg3OU, reason: not valid java name */
    private static final int m230floorDiv7apg3OU(byte b, byte b2) {
        return Integer.divideUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: floorDiv-xj2QHRw, reason: not valid java name */
    private static final int m233floorDivxj2QHRw(byte b, short s) {
        return Integer.divideUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(s & UShort.MAX_VALUE));
    }

    /* JADX INFO: renamed from: floorDiv-WZ4Q5Ns, reason: not valid java name */
    private static final int m232floorDivWZ4Q5Ns(byte b, int i) {
        return Integer.divideUnsigned(UInt.m299constructorimpl(b & 255), i);
    }

    /* JADX INFO: renamed from: floorDiv-VKZWuLQ, reason: not valid java name */
    private static final long m231floorDivVKZWuLQ(byte b, long j) {
        return Long.divideUnsigned(ULong.m378constructorimpl(((long) b) & 255), j);
    }

    /* JADX INFO: renamed from: mod-7apg3OU, reason: not valid java name */
    private static final byte m241mod7apg3OU(byte b, byte b2) {
        return m222constructorimpl((byte) Integer.remainderUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(b2 & 255)));
    }

    /* JADX INFO: renamed from: mod-xj2QHRw, reason: not valid java name */
    private static final short m244modxj2QHRw(byte b, short s) {
        return UShort.m485constructorimpl((short) Integer.remainderUnsigned(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(s & UShort.MAX_VALUE)));
    }

    /* JADX INFO: renamed from: mod-WZ4Q5Ns, reason: not valid java name */
    private static final int m243modWZ4Q5Ns(byte b, int i) {
        return Integer.remainderUnsigned(UInt.m299constructorimpl(b & 255), i);
    }

    /* JADX INFO: renamed from: mod-VKZWuLQ, reason: not valid java name */
    private static final long m242modVKZWuLQ(byte b, long j) {
        return Long.remainderUnsigned(ULong.m378constructorimpl(((long) b) & 255), j);
    }

    /* JADX INFO: renamed from: inc-w2LRezQ, reason: not valid java name */
    private static final byte m235incw2LRezQ(byte b) {
        return m222constructorimpl((byte) (b + 1));
    }

    /* JADX INFO: renamed from: dec-w2LRezQ, reason: not valid java name */
    private static final byte m223decw2LRezQ(byte b) {
        return m222constructorimpl((byte) (b - 1));
    }

    /* JADX INFO: renamed from: rangeTo-7apg3OU, reason: not valid java name */
    private static final UIntRange m250rangeTo7apg3OU(byte b, byte b2) {
        return new UIntRange(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(b2 & 255), null);
    }

    /* JADX INFO: renamed from: rangeUntil-7apg3OU, reason: not valid java name */
    private static final UIntRange m251rangeUntil7apg3OU(byte b, byte b2) {
        return URangesKt.m1480untilJ1ME1BU(UInt.m299constructorimpl(b & 255), UInt.m299constructorimpl(b2 & 255));
    }

    /* JADX INFO: renamed from: and-7apg3OU, reason: not valid java name */
    private static final byte m215and7apg3OU(byte b, byte b2) {
        return m222constructorimpl((byte) (b & b2));
    }

    /* JADX INFO: renamed from: or-7apg3OU, reason: not valid java name */
    private static final byte m245or7apg3OU(byte b, byte b2) {
        return m222constructorimpl((byte) (b | b2));
    }

    /* JADX INFO: renamed from: xor-7apg3OU, reason: not valid java name */
    private static final byte m271xor7apg3OU(byte b, byte b2) {
        return m222constructorimpl((byte) (b ^ b2));
    }

    /* JADX INFO: renamed from: inv-w2LRezQ, reason: not valid java name */
    private static final byte m236invw2LRezQ(byte b) {
        return m222constructorimpl((byte) (~b));
    }

    /* JADX INFO: renamed from: toUShort-Mh2AYeg, reason: not valid java name */
    private static final short m270toUShortMh2AYeg(byte b) {
        return UShort.m485constructorimpl((short) (b & 255));
    }

    /* JADX INFO: renamed from: toUInt-pVg5ArA, reason: not valid java name */
    private static final int m268toUIntpVg5ArA(byte b) {
        return UInt.m299constructorimpl(b & 255);
    }

    /* JADX INFO: renamed from: toULong-s-VKNKU, reason: not valid java name */
    private static final long m269toULongsVKNKU(byte b) {
        return ULong.m378constructorimpl(((long) b) & 255);
    }

    /* JADX INFO: renamed from: toFloat-impl, reason: not valid java name */
    private static final float m262toFloatimpl(byte b) {
        return (float) UnsignedKt.uintToDouble(b & 255);
    }

    /* JADX INFO: renamed from: toDouble-impl, reason: not valid java name */
    private static final double m261toDoubleimpl(byte b) {
        return UnsignedKt.uintToDouble(b & 255);
    }

    /* JADX INFO: renamed from: toString-impl, reason: not valid java name */
    public static String m266toStringimpl(byte b) {
        return String.valueOf(b & 255);
    }

    public String toString() {
        return m266toStringimpl(this.data);
    }
}
