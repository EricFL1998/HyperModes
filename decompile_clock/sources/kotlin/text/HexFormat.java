package kotlin.text;

import com.xiaomi.onetrack.api.g;
import com.xiaomi.onetrack.util.z;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: HexFormat.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u0005\b\u0007\u0018\u0000 \u00152\u00020\u0001:\u0004\u0012\u0013\u0014\u0015B!\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0016"}, d2 = {"Lkotlin/text/HexFormat;", "", "upperCase", "", "bytes", "Lkotlin/text/HexFormat$BytesHexFormat;", "number", "Lkotlin/text/HexFormat$NumberHexFormat;", "<init>", "(ZLkotlin/text/HexFormat$BytesHexFormat;Lkotlin/text/HexFormat$NumberHexFormat;)V", "getUpperCase", "()Z", "getBytes", "()Lkotlin/text/HexFormat$BytesHexFormat;", "getNumber", "()Lkotlin/text/HexFormat$NumberHexFormat;", "toString", "", "BytesHexFormat", "NumberHexFormat", "Builder", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public final class HexFormat {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final HexFormat Default = new HexFormat(false, BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib(), NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib());
    private static final HexFormat UpperCase = new HexFormat(true, BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib(), NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib());
    private final BytesHexFormat bytes;
    private final NumberHexFormat number;
    private final boolean upperCase;

    public HexFormat(boolean z, BytesHexFormat bytes, NumberHexFormat number) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        Intrinsics.checkNotNullParameter(number, "number");
        this.upperCase = z;
        this.bytes = bytes;
        this.number = number;
    }

    public final boolean getUpperCase() {
        return this.upperCase;
    }

    public final BytesHexFormat getBytes() {
        return this.bytes;
    }

    public final NumberHexFormat getNumber() {
        return this.number;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("HexFormat(\n    upperCase = ");
        sb.append(this.upperCase).append(",\n    bytes = BytesHexFormat(\n");
        this.bytes.appendOptionsTo$kotlin_stdlib(sb, "        ").append('\n');
        sb.append("    ),").append('\n');
        sb.append("    number = NumberHexFormat(").append('\n');
        this.number.appendOptionsTo$kotlin_stdlib(sb, "        ").append('\n');
        sb.append("    )").append('\n');
        sb.append(")");
        return sb.toString();
    }

    /* JADX INFO: compiled from: HexFormat.kt */
    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 %2\u00020\u0001:\u0002$%B9\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\u0006¢\u0006\u0004\b\n\u0010\u000bJ\b\u0010\u001c\u001a\u00020\u0006H\u0016J'\u0010\u001d\u001a\u00060\u001ej\u0002`\u001f2\n\u0010 \u001a\u00060\u001ej\u0002`\u001f2\u0006\u0010!\u001a\u00020\u0006H\u0000¢\u0006\u0004\b\"\u0010#R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010R\u0011\u0010\t\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0010R\u0014\u0010\u0014\u001a\u00020\u0015X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0014\u0010\u0018\u001a\u00020\u0015X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0014\u0010\u001a\u001a\u00020\u0015X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0017¨\u0006&"}, d2 = {"Lkotlin/text/HexFormat$BytesHexFormat;", "", "bytesPerLine", "", "bytesPerGroup", "groupSeparator", "", "byteSeparator", "bytePrefix", "byteSuffix", "<init>", "(IILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getBytesPerLine", "()I", "getBytesPerGroup", "getGroupSeparator", "()Ljava/lang/String;", "getByteSeparator", "getBytePrefix", "getByteSuffix", "noLineAndGroupSeparator", "", "getNoLineAndGroupSeparator$kotlin_stdlib", "()Z", "shortByteSeparatorNoPrefixAndSuffix", "getShortByteSeparatorNoPrefixAndSuffix$kotlin_stdlib", "ignoreCase", "getIgnoreCase$kotlin_stdlib", "toString", "appendOptionsTo", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "sb", "indent", "appendOptionsTo$kotlin_stdlib", "(Ljava/lang/StringBuilder;Ljava/lang/String;)Ljava/lang/StringBuilder;", "Builder", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class BytesHexFormat {

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);
        private static final BytesHexFormat Default = new BytesHexFormat(Integer.MAX_VALUE, Integer.MAX_VALUE, "  ", "", "", "");
        private final String bytePrefix;
        private final String byteSeparator;
        private final String byteSuffix;
        private final int bytesPerGroup;
        private final int bytesPerLine;
        private final String groupSeparator;
        private final boolean ignoreCase;
        private final boolean noLineAndGroupSeparator;
        private final boolean shortByteSeparatorNoPrefixAndSuffix;

        public BytesHexFormat(int i, int i2, String groupSeparator, String byteSeparator, String bytePrefix, String byteSuffix) {
            Intrinsics.checkNotNullParameter(groupSeparator, "groupSeparator");
            Intrinsics.checkNotNullParameter(byteSeparator, "byteSeparator");
            Intrinsics.checkNotNullParameter(bytePrefix, "bytePrefix");
            Intrinsics.checkNotNullParameter(byteSuffix, "byteSuffix");
            this.bytesPerLine = i;
            this.bytesPerGroup = i2;
            this.groupSeparator = groupSeparator;
            this.byteSeparator = byteSeparator;
            this.bytePrefix = bytePrefix;
            this.byteSuffix = byteSuffix;
            this.noLineAndGroupSeparator = i == Integer.MAX_VALUE && i2 == Integer.MAX_VALUE;
            this.shortByteSeparatorNoPrefixAndSuffix = bytePrefix.length() == 0 && byteSuffix.length() == 0 && byteSeparator.length() <= 1;
            this.ignoreCase = HexFormatKt.isCaseSensitive(groupSeparator) || HexFormatKt.isCaseSensitive(byteSeparator) || HexFormatKt.isCaseSensitive(bytePrefix) || HexFormatKt.isCaseSensitive(byteSuffix);
        }

        public final int getBytesPerLine() {
            return this.bytesPerLine;
        }

        public final int getBytesPerGroup() {
            return this.bytesPerGroup;
        }

        public final String getGroupSeparator() {
            return this.groupSeparator;
        }

        public final String getByteSeparator() {
            return this.byteSeparator;
        }

        public final String getBytePrefix() {
            return this.bytePrefix;
        }

        public final String getByteSuffix() {
            return this.byteSuffix;
        }

        /* JADX INFO: renamed from: getNoLineAndGroupSeparator$kotlin_stdlib, reason: from getter */
        public final boolean getNoLineAndGroupSeparator() {
            return this.noLineAndGroupSeparator;
        }

        /* JADX INFO: renamed from: getShortByteSeparatorNoPrefixAndSuffix$kotlin_stdlib, reason: from getter */
        public final boolean getShortByteSeparatorNoPrefixAndSuffix() {
            return this.shortByteSeparatorNoPrefixAndSuffix;
        }

        /* JADX INFO: renamed from: getIgnoreCase$kotlin_stdlib, reason: from getter */
        public final boolean getIgnoreCase() {
            return this.ignoreCase;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("BytesHexFormat(\n");
            appendOptionsTo$kotlin_stdlib(sb, "    ").append('\n');
            sb.append(")");
            return sb.toString();
        }

        public final StringBuilder appendOptionsTo$kotlin_stdlib(StringBuilder sb, String indent) {
            Intrinsics.checkNotNullParameter(sb, "sb");
            Intrinsics.checkNotNullParameter(indent, "indent");
            sb.append(indent).append("bytesPerLine = ").append(this.bytesPerLine).append(z.b).append('\n');
            sb.append(indent).append("bytesPerGroup = ").append(this.bytesPerGroup).append(z.b).append('\n');
            sb.append(indent).append("groupSeparator = \"").append(this.groupSeparator).append("\",").append('\n');
            sb.append(indent).append("byteSeparator = \"").append(this.byteSeparator).append("\",").append('\n');
            sb.append(indent).append("bytePrefix = \"").append(this.bytePrefix).append("\",").append('\n');
            sb.append(indent).append("byteSuffix = \"").append(this.byteSuffix).append("\"");
            return sb;
        }

        /* JADX INFO: compiled from: HexFormat.kt */
        @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u000e\n\u0002\b\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\t\b\u0000¢\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u001d\u001a\u00020\u001eH\u0000¢\u0006\u0002\b\u001fR$\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR$\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\b\"\u0004\b\r\u0010\nR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R$\u0010\u0014\u001a\u00020\u000f2\u0006\u0010\u0004\u001a\u00020\u000f@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0011\"\u0004\b\u0016\u0010\u0013R$\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0004\u001a\u00020\u000f@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0011\"\u0004\b\u0019\u0010\u0013R$\u0010\u001a\u001a\u00020\u000f2\u0006\u0010\u0004\u001a\u00020\u000f@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u0011\"\u0004\b\u001c\u0010\u0013¨\u0006 "}, d2 = {"Lkotlin/text/HexFormat$BytesHexFormat$Builder;", "", "<init>", "()V", g.p, "", "bytesPerLine", "getBytesPerLine", "()I", "setBytesPerLine", "(I)V", "bytesPerGroup", "getBytesPerGroup", "setBytesPerGroup", "groupSeparator", "", "getGroupSeparator", "()Ljava/lang/String;", "setGroupSeparator", "(Ljava/lang/String;)V", "byteSeparator", "getByteSeparator", "setByteSeparator", "bytePrefix", "getBytePrefix", "setBytePrefix", "byteSuffix", "getByteSuffix", "setByteSuffix", "build", "Lkotlin/text/HexFormat$BytesHexFormat;", "build$kotlin_stdlib", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        public static final class Builder {
            private int bytesPerLine = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib().getBytesPerLine();
            private int bytesPerGroup = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib().getBytesPerGroup();
            private String groupSeparator = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib().getGroupSeparator();
            private String byteSeparator = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib().getByteSeparator();
            private String bytePrefix = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib().getBytePrefix();
            private String byteSuffix = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib().getByteSuffix();

            public final int getBytesPerLine() {
                return this.bytesPerLine;
            }

            public final void setBytesPerLine(int i) {
                if (i <= 0) {
                    throw new IllegalArgumentException("Non-positive values are prohibited for bytesPerLine, but was " + i);
                }
                this.bytesPerLine = i;
            }

            public final int getBytesPerGroup() {
                return this.bytesPerGroup;
            }

            public final void setBytesPerGroup(int i) {
                if (i <= 0) {
                    throw new IllegalArgumentException("Non-positive values are prohibited for bytesPerGroup, but was " + i);
                }
                this.bytesPerGroup = i;
            }

            public final String getGroupSeparator() {
                return this.groupSeparator;
            }

            public final void setGroupSeparator(String str) {
                Intrinsics.checkNotNullParameter(str, "<set-?>");
                this.groupSeparator = str;
            }

            public final String getByteSeparator() {
                return this.byteSeparator;
            }

            public final void setByteSeparator(String value) {
                Intrinsics.checkNotNullParameter(value, "value");
                String str = value;
                if (StringsKt.contains$default((CharSequence) str, '\n', false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, '\r', false, 2, (Object) null)) {
                    throw new IllegalArgumentException("LF and CR characters are prohibited in byteSeparator, but was " + value);
                }
                this.byteSeparator = value;
            }

            public final String getBytePrefix() {
                return this.bytePrefix;
            }

            public final void setBytePrefix(String value) {
                Intrinsics.checkNotNullParameter(value, "value");
                String str = value;
                if (StringsKt.contains$default((CharSequence) str, '\n', false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, '\r', false, 2, (Object) null)) {
                    throw new IllegalArgumentException("LF and CR characters are prohibited in bytePrefix, but was " + value);
                }
                this.bytePrefix = value;
            }

            public final String getByteSuffix() {
                return this.byteSuffix;
            }

            public final void setByteSuffix(String value) {
                Intrinsics.checkNotNullParameter(value, "value");
                String str = value;
                if (StringsKt.contains$default((CharSequence) str, '\n', false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, '\r', false, 2, (Object) null)) {
                    throw new IllegalArgumentException("LF and CR characters are prohibited in byteSuffix, but was " + value);
                }
                this.byteSuffix = value;
            }

            public final BytesHexFormat build$kotlin_stdlib() {
                return new BytesHexFormat(this.bytesPerLine, this.bytesPerGroup, this.groupSeparator, this.byteSeparator, this.bytePrefix, this.byteSuffix);
            }
        }

        /* JADX INFO: compiled from: HexFormat.kt */
        @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lkotlin/text/HexFormat$BytesHexFormat$Companion;", "", "<init>", "()V", "Default", "Lkotlin/text/HexFormat$BytesHexFormat;", "getDefault$kotlin_stdlib", "()Lkotlin/text/HexFormat$BytesHexFormat;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            public final BytesHexFormat getDefault$kotlin_stdlib() {
                return BytesHexFormat.Default;
            }
        }
    }

    /* JADX INFO: compiled from: HexFormat.kt */
    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\u0018\u0000 #2\u00020\u0001:\u0002\"#B)\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\b\u0010\u001a\u001a\u00020\u0003H\u0016J'\u0010\u001b\u001a\u00060\u001cj\u0002`\u001d2\n\u0010\u001e\u001a\u00060\u001cj\u0002`\u001d2\u0006\u0010\u001f\u001a\u00020\u0003H\u0000¢\u0006\u0004\b \u0010!R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u001c\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\u000e\n\u0000\u0012\u0004\b\u0010\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0014\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000fR\u0014\u0010\u0016\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000fR\u0014\u0010\u0018\u001a\u00020\u0006X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u000f¨\u0006$"}, d2 = {"Lkotlin/text/HexFormat$NumberHexFormat;", "", "prefix", "", "suffix", "removeLeadingZeros", "", "minLength", "", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZI)V", "getPrefix", "()Ljava/lang/String;", "getSuffix", "getRemoveLeadingZeros", "()Z", "getMinLength$annotations", "()V", "getMinLength", "()I", "isDigitsOnly", "isDigitsOnly$kotlin_stdlib", "isDigitsOnlyAndNoPadding", "isDigitsOnlyAndNoPadding$kotlin_stdlib", "ignoreCase", "getIgnoreCase$kotlin_stdlib", "toString", "appendOptionsTo", "Ljava/lang/StringBuilder;", "Lkotlin/text/StringBuilder;", "sb", "indent", "appendOptionsTo$kotlin_stdlib", "(Ljava/lang/StringBuilder;Ljava/lang/String;)Ljava/lang/StringBuilder;", "Builder", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class NumberHexFormat {

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);
        private static final NumberHexFormat Default = new NumberHexFormat("", "", false, 1);
        private final boolean ignoreCase;
        private final boolean isDigitsOnly;
        private final boolean isDigitsOnlyAndNoPadding;
        private final int minLength;
        private final String prefix;
        private final boolean removeLeadingZeros;
        private final String suffix;

        public static /* synthetic */ void getMinLength$annotations() {
        }

        public NumberHexFormat(String prefix, String suffix, boolean z, int i) {
            Intrinsics.checkNotNullParameter(prefix, "prefix");
            Intrinsics.checkNotNullParameter(suffix, "suffix");
            this.prefix = prefix;
            this.suffix = suffix;
            this.removeLeadingZeros = z;
            this.minLength = i;
            boolean z2 = prefix.length() == 0 && suffix.length() == 0;
            this.isDigitsOnly = z2;
            this.isDigitsOnlyAndNoPadding = z2 && i == 1;
            this.ignoreCase = HexFormatKt.isCaseSensitive(prefix) || HexFormatKt.isCaseSensitive(suffix);
        }

        public final String getPrefix() {
            return this.prefix;
        }

        public final String getSuffix() {
            return this.suffix;
        }

        public final boolean getRemoveLeadingZeros() {
            return this.removeLeadingZeros;
        }

        public final int getMinLength() {
            return this.minLength;
        }

        /* JADX INFO: renamed from: isDigitsOnly$kotlin_stdlib, reason: from getter */
        public final boolean getIsDigitsOnly() {
            return this.isDigitsOnly;
        }

        /* JADX INFO: renamed from: isDigitsOnlyAndNoPadding$kotlin_stdlib, reason: from getter */
        public final boolean getIsDigitsOnlyAndNoPadding() {
            return this.isDigitsOnlyAndNoPadding;
        }

        /* JADX INFO: renamed from: getIgnoreCase$kotlin_stdlib, reason: from getter */
        public final boolean getIgnoreCase() {
            return this.ignoreCase;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("NumberHexFormat(\n");
            appendOptionsTo$kotlin_stdlib(sb, "    ").append('\n');
            sb.append(")");
            return sb.toString();
        }

        public final StringBuilder appendOptionsTo$kotlin_stdlib(StringBuilder sb, String indent) {
            Intrinsics.checkNotNullParameter(sb, "sb");
            Intrinsics.checkNotNullParameter(indent, "indent");
            sb.append(indent).append("prefix = \"").append(this.prefix).append("\",").append('\n');
            sb.append(indent).append("suffix = \"").append(this.suffix).append("\",").append('\n');
            sb.append(indent).append("removeLeadingZeros = ").append(this.removeLeadingZeros).append(',').append('\n');
            sb.append(indent).append("minLength = ").append(this.minLength);
            return sb;
        }

        /* JADX INFO: compiled from: HexFormat.kt */
        @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\t\b\u0000¢\u0006\u0004\b\u0002\u0010\u0003J\r\u0010\u001b\u001a\u00020\u001cH\u0000¢\u0006\u0002\b\u001dR$\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR$\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005@FX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\b\"\u0004\b\r\u0010\nR\u001a\u0010\u000e\u001a\u00020\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R,\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0004\u001a\u00020\u00148\u0006@FX\u0087\u000e¢\u0006\u0014\n\u0000\u0012\u0004\b\u0016\u0010\u0003\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001a¨\u0006\u001e"}, d2 = {"Lkotlin/text/HexFormat$NumberHexFormat$Builder;", "", "<init>", "()V", g.p, "", "prefix", "getPrefix", "()Ljava/lang/String;", "setPrefix", "(Ljava/lang/String;)V", "suffix", "getSuffix", "setSuffix", "removeLeadingZeros", "", "getRemoveLeadingZeros", "()Z", "setRemoveLeadingZeros", "(Z)V", "", "minLength", "getMinLength$annotations", "getMinLength", "()I", "setMinLength", "(I)V", "build", "Lkotlin/text/HexFormat$NumberHexFormat;", "build$kotlin_stdlib", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        public static final class Builder {
            private String prefix = NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib().getPrefix();
            private String suffix = NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib().getSuffix();
            private boolean removeLeadingZeros = NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib().getRemoveLeadingZeros();
            private int minLength = NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib().getMinLength();

            public static /* synthetic */ void getMinLength$annotations() {
            }

            public final String getPrefix() {
                return this.prefix;
            }

            public final void setPrefix(String value) {
                Intrinsics.checkNotNullParameter(value, "value");
                String str = value;
                if (StringsKt.contains$default((CharSequence) str, '\n', false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, '\r', false, 2, (Object) null)) {
                    throw new IllegalArgumentException("LF and CR characters are prohibited in prefix, but was " + value);
                }
                this.prefix = value;
            }

            public final String getSuffix() {
                return this.suffix;
            }

            public final void setSuffix(String value) {
                Intrinsics.checkNotNullParameter(value, "value");
                String str = value;
                if (StringsKt.contains$default((CharSequence) str, '\n', false, 2, (Object) null) || StringsKt.contains$default((CharSequence) str, '\r', false, 2, (Object) null)) {
                    throw new IllegalArgumentException("LF and CR characters are prohibited in suffix, but was " + value);
                }
                this.suffix = value;
            }

            public final boolean getRemoveLeadingZeros() {
                return this.removeLeadingZeros;
            }

            public final void setRemoveLeadingZeros(boolean z) {
                this.removeLeadingZeros = z;
            }

            public final int getMinLength() {
                return this.minLength;
            }

            public final void setMinLength(int i) {
                if (i <= 0) {
                    throw new IllegalArgumentException(("Non-positive values are prohibited for minLength, but was " + i).toString());
                }
                this.minLength = i;
            }

            public final NumberHexFormat build$kotlin_stdlib() {
                return new NumberHexFormat(this.prefix, this.suffix, this.removeLeadingZeros, this.minLength);
            }
        }

        /* JADX INFO: compiled from: HexFormat.kt */
        @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0080\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0014\u0010\u0004\u001a\u00020\u0005X\u0080\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lkotlin/text/HexFormat$NumberHexFormat$Companion;", "", "<init>", "()V", "Default", "Lkotlin/text/HexFormat$NumberHexFormat;", "getDefault$kotlin_stdlib", "()Lkotlin/text/HexFormat$NumberHexFormat;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            public final NumberHexFormat getDefault$kotlin_stdlib() {
                return NumberHexFormat.Default;
            }
        }
    }

    /* JADX INFO: compiled from: HexFormat.kt */
    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\t\b\u0001¢\u0006\u0004\b\u0002\u0010\u0003J%\u0010\n\u001a\u00020\u00142\u0017\u0010\u0015\u001a\u0013\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00140\u0016¢\u0006\u0002\b\u0017H\u0087\bø\u0001\u0000J%\u0010\u000f\u001a\u00020\u00142\u0017\u0010\u0015\u001a\u0013\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00140\u0016¢\u0006\u0002\b\u0017H\u0087\bø\u0001\u0000J\b\u0010\u0018\u001a\u00020\u0019H\u0001R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\u000f\u001a\u00020\u00108F¢\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000\u0082\u0002\u0007\n\u0005\b\u009920\u0001¨\u0006\u001a"}, d2 = {"Lkotlin/text/HexFormat$Builder;", "", "<init>", "()V", "upperCase", "", "getUpperCase", "()Z", "setUpperCase", "(Z)V", "bytes", "Lkotlin/text/HexFormat$BytesHexFormat$Builder;", "getBytes", "()Lkotlin/text/HexFormat$BytesHexFormat$Builder;", "_bytes", "number", "Lkotlin/text/HexFormat$NumberHexFormat$Builder;", "getNumber", "()Lkotlin/text/HexFormat$NumberHexFormat$Builder;", "_number", "", "builderAction", "Lkotlin/Function1;", "Lkotlin/ExtensionFunctionType;", "build", "Lkotlin/text/HexFormat;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Builder {
        private BytesHexFormat.Builder _bytes;
        private NumberHexFormat.Builder _number;
        private boolean upperCase = HexFormat.INSTANCE.getDefault().getUpperCase();

        public final boolean getUpperCase() {
            return this.upperCase;
        }

        public final void setUpperCase(boolean z) {
            this.upperCase = z;
        }

        public final BytesHexFormat.Builder getBytes() {
            if (this._bytes == null) {
                this._bytes = new BytesHexFormat.Builder();
            }
            BytesHexFormat.Builder builder = this._bytes;
            Intrinsics.checkNotNull(builder);
            return builder;
        }

        public final NumberHexFormat.Builder getNumber() {
            if (this._number == null) {
                this._number = new NumberHexFormat.Builder();
            }
            NumberHexFormat.Builder builder = this._number;
            Intrinsics.checkNotNull(builder);
            return builder;
        }

        private final void bytes(Function1<? super BytesHexFormat.Builder, Unit> builderAction) {
            Intrinsics.checkNotNullParameter(builderAction, "builderAction");
            builderAction.invoke(getBytes());
        }

        private final void number(Function1<? super NumberHexFormat.Builder, Unit> builderAction) {
            Intrinsics.checkNotNullParameter(builderAction, "builderAction");
            builderAction.invoke(getNumber());
        }

        public final HexFormat build() {
            BytesHexFormat default$kotlin_stdlib;
            NumberHexFormat default$kotlin_stdlib2;
            boolean z = this.upperCase;
            BytesHexFormat.Builder builder = this._bytes;
            if (builder == null || (default$kotlin_stdlib = builder.build$kotlin_stdlib()) == null) {
                default$kotlin_stdlib = BytesHexFormat.INSTANCE.getDefault$kotlin_stdlib();
            }
            NumberHexFormat.Builder builder2 = this._number;
            if (builder2 == null || (default$kotlin_stdlib2 = builder2.build$kotlin_stdlib()) == null) {
                default$kotlin_stdlib2 = NumberHexFormat.INSTANCE.getDefault$kotlin_stdlib();
            }
            return new HexFormat(z, default$kotlin_stdlib, default$kotlin_stdlib2);
        }
    }

    /* JADX INFO: compiled from: HexFormat.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0011\u0010\b\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0007¨\u0006\n"}, d2 = {"Lkotlin/text/HexFormat$Companion;", "", "<init>", "()V", "Default", "Lkotlin/text/HexFormat;", "getDefault", "()Lkotlin/text/HexFormat;", "UpperCase", "getUpperCase", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final HexFormat getDefault() {
            return HexFormat.Default;
        }

        public final HexFormat getUpperCase() {
            return HexFormat.UpperCase;
        }
    }
}
