package kotlin.reflect.jvm.internal.impl.metadata.deserialization;

import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.protobuf.Internal;

/* JADX INFO: loaded from: classes2.dex */
public class Flags {
    public static final FlagField<ProtoBuf.Class.Kind> CLASS_KIND;
    public static final BooleanFlagField DECLARES_DEFAULT_VALUE;
    public static final BooleanFlagField DEFINITELY_NOT_NULL_TYPE;
    public static final BooleanFlagField HAS_ANNOTATIONS;
    public static final BooleanFlagField HAS_CONSTANT;
    public static final BooleanFlagField HAS_ENUM_ENTRIES;
    public static final BooleanFlagField HAS_GETTER;
    public static final BooleanFlagField HAS_SETTER;
    public static final BooleanFlagField IS_CONST;
    public static final BooleanFlagField IS_CONSTRUCTOR_WITH_NON_STABLE_PARAMETER_NAMES;
    public static final BooleanFlagField IS_CROSSINLINE;
    public static final BooleanFlagField IS_DATA;
    public static final BooleanFlagField IS_DELEGATED;
    public static final BooleanFlagField IS_EXPECT_CLASS;
    public static final BooleanFlagField IS_EXPECT_FUNCTION;
    public static final BooleanFlagField IS_EXPECT_PROPERTY;
    public static final BooleanFlagField IS_EXTERNAL_ACCESSOR;
    public static final BooleanFlagField IS_EXTERNAL_CLASS;
    public static final BooleanFlagField IS_EXTERNAL_FUNCTION;
    public static final BooleanFlagField IS_EXTERNAL_PROPERTY;
    public static final BooleanFlagField IS_FUNCTION_WITH_NON_STABLE_PARAMETER_NAMES;
    public static final BooleanFlagField IS_FUN_INTERFACE;
    public static final BooleanFlagField IS_INFIX;
    public static final BooleanFlagField IS_INLINE;
    public static final BooleanFlagField IS_INLINE_ACCESSOR;
    public static final BooleanFlagField IS_INNER;
    public static final BooleanFlagField IS_LATEINIT;
    public static final BooleanFlagField IS_NEGATED;
    public static final BooleanFlagField IS_NOINLINE;
    public static final BooleanFlagField IS_NOT_DEFAULT;
    public static final BooleanFlagField IS_NULL_CHECK_PREDICATE;
    public static final BooleanFlagField IS_OPERATOR;
    public static final BooleanFlagField IS_SECONDARY;
    public static final BooleanFlagField IS_SUSPEND;
    public static final BooleanFlagField IS_TAILREC;
    public static final BooleanFlagField IS_UNSIGNED;
    public static final BooleanFlagField IS_VALUE_CLASS;
    public static final BooleanFlagField IS_VAR;
    public static final FlagField<ProtoBuf.MemberKind> MEMBER_KIND;
    public static final FlagField<ProtoBuf.Modality> MODALITY;
    public static final BooleanFlagField SUSPEND_TYPE;
    public static final FlagField<ProtoBuf.Visibility> VISIBILITY;

    /* JADX WARN: Code duplicated, block: B:16:0x0022  */
    /* JADX WARN: Code duplicated, block: B:18:0x002c  */
    private static /* synthetic */ void $$$reportNull$$$0(int i) {
        Object[] objArr = new Object[3];
        if (i == 1) {
            objArr[0] = "modality";
        } else if (i == 2) {
            objArr[0] = "kind";
        } else if (i == 5) {
            objArr[0] = "modality";
        } else if (i == 6) {
            objArr[0] = "memberKind";
        } else if (i == 8) {
            objArr[0] = "modality";
        } else if (i == 9) {
            objArr[0] = "memberKind";
        } else if (i != 11) {
            objArr[0] = "visibility";
        } else {
            objArr[0] = "modality";
        }
        objArr[1] = "kotlin/reflect/jvm/internal/impl/metadata/deserialization/Flags";
        switch (i) {
            case 3:
                objArr[2] = "getConstructorFlags";
                break;
            case 4:
            case 5:
            case 6:
                objArr[2] = "getFunctionFlags";
                break;
            case 7:
            case 8:
            case 9:
                objArr[2] = "getPropertyFlags";
                break;
            case 10:
            case 11:
                objArr[2] = "getAccessorFlags";
                break;
            default:
                objArr[2] = "getClassFlags";
                break;
        }
        throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", objArr));
    }

    static {
        BooleanFlagField booleanFlagFieldBooleanFirst = FlagField.booleanFirst();
        SUSPEND_TYPE = booleanFlagFieldBooleanFirst;
        DEFINITELY_NOT_NULL_TYPE = FlagField.booleanAfter(booleanFlagFieldBooleanFirst);
        BooleanFlagField booleanFlagFieldBooleanFirst2 = FlagField.booleanFirst();
        HAS_ANNOTATIONS = booleanFlagFieldBooleanFirst2;
        FlagField<ProtoBuf.Visibility> flagFieldAfter = FlagField.after(booleanFlagFieldBooleanFirst2, ProtoBuf.Visibility.values());
        VISIBILITY = flagFieldAfter;
        FlagField<ProtoBuf.Modality> flagFieldAfter2 = FlagField.after(flagFieldAfter, ProtoBuf.Modality.values());
        MODALITY = flagFieldAfter2;
        FlagField<ProtoBuf.Class.Kind> flagFieldAfter3 = FlagField.after(flagFieldAfter2, ProtoBuf.Class.Kind.values());
        CLASS_KIND = flagFieldAfter3;
        BooleanFlagField booleanFlagFieldBooleanAfter = FlagField.booleanAfter(flagFieldAfter3);
        IS_INNER = booleanFlagFieldBooleanAfter;
        BooleanFlagField booleanFlagFieldBooleanAfter2 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter);
        IS_DATA = booleanFlagFieldBooleanAfter2;
        BooleanFlagField booleanFlagFieldBooleanAfter3 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter2);
        IS_EXTERNAL_CLASS = booleanFlagFieldBooleanAfter3;
        BooleanFlagField booleanFlagFieldBooleanAfter4 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter3);
        IS_EXPECT_CLASS = booleanFlagFieldBooleanAfter4;
        BooleanFlagField booleanFlagFieldBooleanAfter5 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter4);
        IS_VALUE_CLASS = booleanFlagFieldBooleanAfter5;
        BooleanFlagField booleanFlagFieldBooleanAfter6 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter5);
        IS_FUN_INTERFACE = booleanFlagFieldBooleanAfter6;
        HAS_ENUM_ENTRIES = FlagField.booleanAfter(booleanFlagFieldBooleanAfter6);
        BooleanFlagField booleanFlagFieldBooleanAfter7 = FlagField.booleanAfter(flagFieldAfter);
        IS_SECONDARY = booleanFlagFieldBooleanAfter7;
        IS_CONSTRUCTOR_WITH_NON_STABLE_PARAMETER_NAMES = FlagField.booleanAfter(booleanFlagFieldBooleanAfter7);
        FlagField<ProtoBuf.MemberKind> flagFieldAfter4 = FlagField.after(flagFieldAfter2, ProtoBuf.MemberKind.values());
        MEMBER_KIND = flagFieldAfter4;
        BooleanFlagField booleanFlagFieldBooleanAfter8 = FlagField.booleanAfter(flagFieldAfter4);
        IS_OPERATOR = booleanFlagFieldBooleanAfter8;
        BooleanFlagField booleanFlagFieldBooleanAfter9 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter8);
        IS_INFIX = booleanFlagFieldBooleanAfter9;
        BooleanFlagField booleanFlagFieldBooleanAfter10 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter9);
        IS_INLINE = booleanFlagFieldBooleanAfter10;
        BooleanFlagField booleanFlagFieldBooleanAfter11 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter10);
        IS_TAILREC = booleanFlagFieldBooleanAfter11;
        BooleanFlagField booleanFlagFieldBooleanAfter12 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter11);
        IS_EXTERNAL_FUNCTION = booleanFlagFieldBooleanAfter12;
        BooleanFlagField booleanFlagFieldBooleanAfter13 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter12);
        IS_SUSPEND = booleanFlagFieldBooleanAfter13;
        BooleanFlagField booleanFlagFieldBooleanAfter14 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter13);
        IS_EXPECT_FUNCTION = booleanFlagFieldBooleanAfter14;
        IS_FUNCTION_WITH_NON_STABLE_PARAMETER_NAMES = FlagField.booleanAfter(booleanFlagFieldBooleanAfter14);
        BooleanFlagField booleanFlagFieldBooleanAfter15 = FlagField.booleanAfter(flagFieldAfter4);
        IS_VAR = booleanFlagFieldBooleanAfter15;
        BooleanFlagField booleanFlagFieldBooleanAfter16 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter15);
        HAS_GETTER = booleanFlagFieldBooleanAfter16;
        BooleanFlagField booleanFlagFieldBooleanAfter17 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter16);
        HAS_SETTER = booleanFlagFieldBooleanAfter17;
        BooleanFlagField booleanFlagFieldBooleanAfter18 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter17);
        IS_CONST = booleanFlagFieldBooleanAfter18;
        BooleanFlagField booleanFlagFieldBooleanAfter19 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter18);
        IS_LATEINIT = booleanFlagFieldBooleanAfter19;
        BooleanFlagField booleanFlagFieldBooleanAfter20 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter19);
        HAS_CONSTANT = booleanFlagFieldBooleanAfter20;
        BooleanFlagField booleanFlagFieldBooleanAfter21 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter20);
        IS_EXTERNAL_PROPERTY = booleanFlagFieldBooleanAfter21;
        BooleanFlagField booleanFlagFieldBooleanAfter22 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter21);
        IS_DELEGATED = booleanFlagFieldBooleanAfter22;
        IS_EXPECT_PROPERTY = FlagField.booleanAfter(booleanFlagFieldBooleanAfter22);
        BooleanFlagField booleanFlagFieldBooleanAfter23 = FlagField.booleanAfter(booleanFlagFieldBooleanFirst2);
        DECLARES_DEFAULT_VALUE = booleanFlagFieldBooleanAfter23;
        BooleanFlagField booleanFlagFieldBooleanAfter24 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter23);
        IS_CROSSINLINE = booleanFlagFieldBooleanAfter24;
        IS_NOINLINE = FlagField.booleanAfter(booleanFlagFieldBooleanAfter24);
        BooleanFlagField booleanFlagFieldBooleanAfter25 = FlagField.booleanAfter(flagFieldAfter2);
        IS_NOT_DEFAULT = booleanFlagFieldBooleanAfter25;
        BooleanFlagField booleanFlagFieldBooleanAfter26 = FlagField.booleanAfter(booleanFlagFieldBooleanAfter25);
        IS_EXTERNAL_ACCESSOR = booleanFlagFieldBooleanAfter26;
        IS_INLINE_ACCESSOR = FlagField.booleanAfter(booleanFlagFieldBooleanAfter26);
        BooleanFlagField booleanFlagFieldBooleanFirst3 = FlagField.booleanFirst();
        IS_NEGATED = booleanFlagFieldBooleanFirst3;
        IS_NULL_CHECK_PREDICATE = FlagField.booleanAfter(booleanFlagFieldBooleanFirst3);
        IS_UNSIGNED = FlagField.booleanFirst();
    }

    public static int getAccessorFlags(boolean z, ProtoBuf.Visibility visibility, ProtoBuf.Modality modality, boolean z2, boolean z3, boolean z4) {
        if (visibility == null) {
            $$$reportNull$$$0(10);
        }
        if (modality == null) {
            $$$reportNull$$$0(11);
        }
        return HAS_ANNOTATIONS.toFlags(Boolean.valueOf(z)) | MODALITY.toFlags(modality) | VISIBILITY.toFlags(visibility) | IS_NOT_DEFAULT.toFlags(Boolean.valueOf(z2)) | IS_EXTERNAL_ACCESSOR.toFlags(Boolean.valueOf(z3)) | IS_INLINE_ACCESSOR.toFlags(Boolean.valueOf(z4));
    }

    public static abstract class FlagField<E> {
        public final int bitWidth;
        public final int offset;

        public abstract E get(int i);

        public abstract int toFlags(E e);

        /* JADX WARN: Incorrect types in method signature: <E::Lkotlin/reflect/jvm/internal/impl/protobuf/Internal$EnumLite;>(Lkotlin/reflect/jvm/internal/impl/metadata/deserialization/Flags$FlagField<*>;[TE;)Lkotlin/reflect/jvm/internal/impl/metadata/deserialization/Flags$FlagField<TE;>; */
        public static FlagField after(FlagField flagField, Internal.EnumLite[] enumLiteArr) {
            return new EnumLiteFlagField(flagField.offset + flagField.bitWidth, enumLiteArr);
        }

        public static BooleanFlagField booleanFirst() {
            return new BooleanFlagField(0);
        }

        public static BooleanFlagField booleanAfter(FlagField<?> flagField) {
            return new BooleanFlagField(flagField.offset + flagField.bitWidth);
        }

        private FlagField(int i, int i2) {
            this.offset = i;
            this.bitWidth = i2;
        }
    }

    public static class BooleanFlagField extends FlagField<Boolean> {
        private static /* synthetic */ void $$$reportNull$$$0(int i) {
            throw new IllegalStateException(String.format("@NotNull method %s.%s must not return null", "kotlin/reflect/jvm/internal/impl/metadata/deserialization/Flags$BooleanFlagField", "get"));
        }

        public BooleanFlagField(int i) {
            super(i, 1);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags.FlagField
        public Boolean get(int i) {
            Boolean boolValueOf = Boolean.valueOf((i & (1 << this.offset)) != 0);
            if (boolValueOf == null) {
                $$$reportNull$$$0(0);
            }
            return boolValueOf;
        }

        @Override // kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags.FlagField
        public int toFlags(Boolean bool) {
            if (bool.booleanValue()) {
                return 1 << this.offset;
            }
            return 0;
        }
    }

    private static class EnumLiteFlagField<E extends Internal.EnumLite> extends FlagField<E> {
        private final E[] values;

        private static /* synthetic */ void $$$reportNull$$$0(int i) {
            throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", "enumEntries", "kotlin/reflect/jvm/internal/impl/metadata/deserialization/Flags$EnumLiteFlagField", "bitWidth"));
        }

        public EnumLiteFlagField(int i, E[] eArr) {
            super(i, bitWidth(eArr));
            this.values = eArr;
        }

        private static <E> int bitWidth(E[] eArr) {
            if (eArr == null) {
                $$$reportNull$$$0(0);
            }
            int length = eArr.length - 1;
            if (length == 0) {
                return 1;
            }
            for (int i = 31; i >= 0; i--) {
                if (((1 << i) & length) != 0) {
                    return i + 1;
                }
            }
            throw new IllegalStateException("Empty enum: " + eArr.getClass());
        }

        @Override // kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags.FlagField
        public E get(int i) {
            int i2 = (i & (((1 << this.bitWidth) - 1) << this.offset)) >> this.offset;
            for (E e : this.values) {
                if (e.getNumber() == i2) {
                    return e;
                }
            }
            return null;
        }

        @Override // kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags.FlagField
        public int toFlags(E e) {
            return e.getNumber() << this.offset;
        }
    }
}
