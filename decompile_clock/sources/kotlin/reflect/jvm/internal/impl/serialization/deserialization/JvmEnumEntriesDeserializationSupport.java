package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

/* JADX INFO: compiled from: EnumEntriesDeserializationSupport.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JvmEnumEntriesDeserializationSupport implements EnumEntriesDeserializationSupport {
    public static final JvmEnumEntriesDeserializationSupport INSTANCE = new JvmEnumEntriesDeserializationSupport();

    private JvmEnumEntriesDeserializationSupport() {
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.EnumEntriesDeserializationSupport
    public Boolean canSynthesizeEnumEntries() {
        return true;
    }
}
