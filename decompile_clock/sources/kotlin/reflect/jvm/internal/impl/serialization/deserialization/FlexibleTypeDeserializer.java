package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.SimpleType;

/* JADX INFO: compiled from: FlexibleTypeDeserializer.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface FlexibleTypeDeserializer {
    KotlinType create(ProtoBuf.Type type, String str, SimpleType simpleType, SimpleType simpleType2);

    /* JADX INFO: compiled from: FlexibleTypeDeserializer.kt */
    public static final class ThrowException implements FlexibleTypeDeserializer {
        public static final ThrowException INSTANCE = new ThrowException();

        private ThrowException() {
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.FlexibleTypeDeserializer
        public KotlinType create(ProtoBuf.Type proto, String flexibleId, SimpleType lowerBound, SimpleType upperBound) {
            Intrinsics.checkNotNullParameter(proto, "proto");
            Intrinsics.checkNotNullParameter(flexibleId, "flexibleId");
            Intrinsics.checkNotNullParameter(lowerBound, "lowerBound");
            Intrinsics.checkNotNullParameter(upperBound, "upperBound");
            throw new IllegalArgumentException("This method should not be used.");
        }
    }
}
