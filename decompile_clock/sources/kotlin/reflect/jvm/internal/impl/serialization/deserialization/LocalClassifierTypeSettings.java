package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import kotlin.reflect.jvm.internal.impl.types.SimpleType;

/* JADX INFO: compiled from: LocalClassifierTypeSettings.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface LocalClassifierTypeSettings {
    SimpleType getReplacementTypeForLocalClassifiers();

    /* JADX INFO: compiled from: LocalClassifierTypeSettings.kt */
    public static final class Default implements LocalClassifierTypeSettings {
        public static final Default INSTANCE = new Default();

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.LocalClassifierTypeSettings
        public SimpleType getReplacementTypeForLocalClassifiers() {
            return null;
        }

        private Default() {
        }
    }
}
