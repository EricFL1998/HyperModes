package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

/* JADX INFO: compiled from: EnumEntriesDeserializationSupport.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface EnumEntriesDeserializationSupport {
    Boolean canSynthesizeEnumEntries();

    /* JADX INFO: compiled from: EnumEntriesDeserializationSupport.kt */
    public static final class Default implements EnumEntriesDeserializationSupport {
        public static final Default INSTANCE = new Default();

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.EnumEntriesDeserializationSupport
        public Boolean canSynthesizeEnumEntries() {
            return null;
        }

        private Default() {
        }
    }
}
