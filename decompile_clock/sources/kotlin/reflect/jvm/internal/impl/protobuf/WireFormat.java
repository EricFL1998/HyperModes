package kotlin.reflect.jvm.internal.impl.protobuf;

/* JADX INFO: loaded from: classes2.dex */
public final class WireFormat {
    static final int MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
    static final int MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
    static final int MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
    static final int MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);

    public static int getTagFieldNumber(int i) {
        return i >>> 3;
    }

    static int getTagWireType(int i) {
        return i & 7;
    }

    static int makeTag(int i, int i2) {
        return (i << 3) | i2;
    }

    public enum JavaType {
        INT(0),
        LONG(0L),
        FLOAT(Float.valueOf(0.0f)),
        DOUBLE(Double.valueOf(0.0d)),
        BOOLEAN(false),
        STRING(""),
        BYTE_STRING(ByteString.EMPTY),
        ENUM(null),
        MESSAGE(null);

        private final Object defaultDefault;

        JavaType(Object obj) {
            this.defaultDefault = obj;
        }
    }

    /* JADX WARN: Enum visitor error
    jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'STRING' uses external variables
    	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:485)
    	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:422)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:351)
    	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:284)
    	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:153)
    	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:102)
     */
    /* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
    public static class FieldType {
        private static final /* synthetic */ FieldType[] $VALUES;
        public static final FieldType BOOL;
        public static final FieldType BYTES;
        public static final FieldType DOUBLE;
        public static final FieldType ENUM;
        public static final FieldType FIXED32;
        public static final FieldType FIXED64;
        public static final FieldType FLOAT;
        public static final FieldType GROUP;
        public static final FieldType INT32;
        public static final FieldType INT64;
        public static final FieldType MESSAGE;
        public static final FieldType SFIXED32;
        public static final FieldType SFIXED64;
        public static final FieldType SINT32;
        public static final FieldType SINT64;
        public static final FieldType STRING;
        public static final FieldType UINT32;
        public static final FieldType UINT64;
        private final JavaType javaType;
        private final int wireType;

        public boolean isPackable() {
            return true;
        }

        public static FieldType valueOf(String str) {
            return (FieldType) Enum.valueOf(FieldType.class, str);
        }

        public static FieldType[] values() {
            return (FieldType[]) $VALUES.clone();
        }

        static {
            FieldType fieldType = new FieldType("DOUBLE", 0, JavaType.DOUBLE, 1);
            DOUBLE = fieldType;
            FieldType fieldType2 = new FieldType("FLOAT", 1, JavaType.FLOAT, 5);
            FLOAT = fieldType2;
            int i = 2;
            FieldType fieldType3 = new FieldType("INT64", 2, JavaType.LONG, 0);
            INT64 = fieldType3;
            FieldType fieldType4 = new FieldType("UINT64", 3, JavaType.LONG, 0);
            UINT64 = fieldType4;
            FieldType fieldType5 = new FieldType("INT32", 4, JavaType.INT, 0);
            INT32 = fieldType5;
            FieldType fieldType6 = new FieldType("FIXED64", 5, JavaType.LONG, 1);
            FIXED64 = fieldType6;
            FieldType fieldType7 = new FieldType("FIXED32", 6, JavaType.INT, 5);
            FIXED32 = fieldType7;
            FieldType fieldType8 = new FieldType("BOOL", 7, JavaType.BOOLEAN, 0);
            BOOL = fieldType8;
            FieldType fieldType9 = new FieldType("STRING", 8, JavaType.STRING, i) { // from class: kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType.1
                @Override // kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType
                public boolean isPackable() {
                    return false;
                }
            };
            STRING = fieldType9;
            FieldType fieldType10 = new FieldType("GROUP", 9, JavaType.MESSAGE, 3) { // from class: kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType.2
                @Override // kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType
                public boolean isPackable() {
                    return false;
                }
            };
            GROUP = fieldType10;
            FieldType fieldType11 = new FieldType("MESSAGE", 10, JavaType.MESSAGE, i) { // from class: kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType.3
                @Override // kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType
                public boolean isPackable() {
                    return false;
                }
            };
            MESSAGE = fieldType11;
            FieldType fieldType12 = new FieldType("BYTES", 11, JavaType.BYTE_STRING, i) { // from class: kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType.4
                @Override // kotlin.reflect.jvm.internal.impl.protobuf.WireFormat.FieldType
                public boolean isPackable() {
                    return false;
                }
            };
            BYTES = fieldType12;
            FieldType fieldType13 = new FieldType("UINT32", 12, JavaType.INT, 0);
            UINT32 = fieldType13;
            FieldType fieldType14 = new FieldType("ENUM", 13, JavaType.ENUM, 0);
            ENUM = fieldType14;
            FieldType fieldType15 = new FieldType("SFIXED32", 14, JavaType.INT, 5);
            SFIXED32 = fieldType15;
            FieldType fieldType16 = new FieldType("SFIXED64", 15, JavaType.LONG, 1);
            SFIXED64 = fieldType16;
            FieldType fieldType17 = new FieldType("SINT32", 16, JavaType.INT, 0);
            SINT32 = fieldType17;
            FieldType fieldType18 = new FieldType("SINT64", 17, JavaType.LONG, 0);
            SINT64 = fieldType18;
            $VALUES = new FieldType[]{fieldType, fieldType2, fieldType3, fieldType4, fieldType5, fieldType6, fieldType7, fieldType8, fieldType9, fieldType10, fieldType11, fieldType12, fieldType13, fieldType14, fieldType15, fieldType16, fieldType17, fieldType18};
        }

        private FieldType(String str, int i, JavaType javaType, int i2) {
            super(str, i);
            this.javaType = javaType;
            this.wireType = i2;
        }

        public JavaType getJavaType() {
            return this.javaType;
        }

        public int getWireType() {
            return this.wireType;
        }
    }
}
