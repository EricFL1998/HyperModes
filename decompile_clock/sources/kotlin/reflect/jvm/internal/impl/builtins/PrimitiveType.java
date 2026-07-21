package kotlin.reflect.jvm.internal.impl.builtins;

import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.collections.SetsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Can't remove SSA var: r4v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType, still in use, count: 1, list:
  (r4v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType) from 0x0076: FILLED_NEW_ARRAY 
  (r4v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
  (r5v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
  (r6v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
  (r7v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
  (r8v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
  (r9v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
  (r10v0 kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType)
 A[WRAPPED] (LINE:32) elemType: kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType
	at jadx.core.utils.InsnRemover.removeSsaVar(InsnRemover.java:164)
	at jadx.core.utils.InsnRemover.unbindResult(InsnRemover.java:129)
	at jadx.core.utils.InsnRemover.lambda$unbindInsns$1(InsnRemover.java:101)
	at java.base/java.util.ArrayList.forEach(Unknown Source)
	at jadx.core.utils.InsnRemover.unbindInsns(InsnRemover.java:100)
	at jadx.core.utils.InsnRemover.removeAllAndUnbind(InsnRemover.java:257)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:187)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:102)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX INFO: compiled from: PrimitiveType.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class PrimitiveType {
    BOOLEAN("Boolean"),
    CHAR("Char"),
    BYTE("Byte"),
    SHORT("Short"),
    INT("Int"),
    FLOAT("Float"),
    LONG("Long"),
    DOUBLE("Double");

    private final Lazy arrayTypeFqName$delegate;
    private final Name arrayTypeName;
    private final Lazy typeFqName$delegate;
    private final Name typeName;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    public static final Companion Companion = new Companion(null);
    public static final Set<PrimitiveType> NUMBER_TYPES = SetsKt.setOf((Object[]) new PrimitiveType[]{new PrimitiveType("Char"), new PrimitiveType("Byte"), new PrimitiveType("Short"), new PrimitiveType("Int"), new PrimitiveType("Float"), new PrimitiveType("Long"), new PrimitiveType("Double")});

    private PrimitiveType(String str) {
        super(str, i);
        Name nameIdentifier = Name.identifier(str);
        Intrinsics.checkNotNullExpressionValue(nameIdentifier, "identifier(...)");
        this.typeName = nameIdentifier;
        Name nameIdentifier2 = Name.identifier(str + "Array");
        Intrinsics.checkNotNullExpressionValue(nameIdentifier2, "identifier(...)");
        this.arrayTypeName = nameIdentifier2;
        this.typeFqName$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType$$Lambda$0
            private final PrimitiveType arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return PrimitiveType.typeFqName_delegate$lambda$0(this.arg$0);
            }
        });
        this.arrayTypeFqName$delegate = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.builtins.PrimitiveType$$Lambda$1
            private final PrimitiveType arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return PrimitiveType.arrayTypeFqName_delegate$lambda$1(this.arg$0);
            }
        });
    }

    static {
    }

    public final Name getTypeName() {
        return this.typeName;
    }

    public final Name getArrayTypeName() {
        return this.arrayTypeName;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final FqName typeFqName_delegate$lambda$0(PrimitiveType this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return StandardNames.BUILT_INS_PACKAGE_FQ_NAME.child(this$0.typeName);
    }

    public final FqName getTypeFqName() {
        Object value = this.typeFqName$delegate.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (FqName) value;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final FqName arrayTypeFqName_delegate$lambda$1(PrimitiveType this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return StandardNames.BUILT_INS_PACKAGE_FQ_NAME.child(this$0.arrayTypeName);
    }

    public final FqName getArrayTypeFqName() {
        Object value = this.arrayTypeFqName$delegate.getValue();
        Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
        return (FqName) value;
    }

    /* JADX INFO: compiled from: PrimitiveType.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }

    public static PrimitiveType valueOf(String str) {
        return (PrimitiveType) Enum.valueOf(PrimitiveType.class, str);
    }

    public static PrimitiveType[] values() {
        return (PrimitiveType[]) $VALUES.clone();
    }
}
