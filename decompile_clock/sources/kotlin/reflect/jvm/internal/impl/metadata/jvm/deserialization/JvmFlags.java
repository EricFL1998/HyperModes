package kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization;

import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags;

/* JADX INFO: compiled from: JvmFlags.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JvmFlags {
    private static final Flags.BooleanFlagField IS_COMPILED_IN_COMPATIBILITY_MODE;
    private static final Flags.BooleanFlagField IS_COMPILED_IN_JVM_DEFAULT_MODE;
    public static final JvmFlags INSTANCE = new JvmFlags();
    private static final Flags.BooleanFlagField IS_MOVED_FROM_INTERFACE_COMPANION = Flags.FlagField.booleanFirst();

    private JvmFlags() {
    }

    static {
        Flags.BooleanFlagField booleanFlagFieldBooleanFirst = Flags.FlagField.booleanFirst();
        IS_COMPILED_IN_JVM_DEFAULT_MODE = booleanFlagFieldBooleanFirst;
        IS_COMPILED_IN_COMPATIBILITY_MODE = Flags.FlagField.booleanAfter(booleanFlagFieldBooleanFirst);
    }

    public final Flags.BooleanFlagField getIS_MOVED_FROM_INTERFACE_COMPANION() {
        return IS_MOVED_FROM_INTERFACE_COMPANION;
    }
}
