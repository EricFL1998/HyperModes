package kotlin.reflect.jvm.internal.impl.utils;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.BinaryVersion;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmMetadataVersion;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationConfiguration;

/* JADX INFO: compiled from: deserializationHelpers.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DeserializationHelpersKt {
    public static final JvmMetadataVersion jvmMetadataVersionOrDefault(DeserializationConfiguration deserializationConfiguration) {
        Intrinsics.checkNotNullParameter(deserializationConfiguration, "<this>");
        BinaryVersion binaryVersion = deserializationConfiguration.getBinaryVersion();
        JvmMetadataVersion jvmMetadataVersion = binaryVersion instanceof JvmMetadataVersion ? (JvmMetadataVersion) binaryVersion : null;
        return jvmMetadataVersion == null ? JvmMetadataVersion.INSTANCE : jvmMetadataVersion;
    }
}
