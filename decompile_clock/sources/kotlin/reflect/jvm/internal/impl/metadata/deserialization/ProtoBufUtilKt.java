package kotlin.reflect.jvm.internal.impl.metadata.deserialization;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.protobuf.GeneratedMessageLite;

/* JADX INFO: compiled from: ProtoBufUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ProtoBufUtilKt {
    /* JADX WARN: Multi-variable type inference failed */
    public static final <M extends GeneratedMessageLite.ExtendableMessage<M>, T> T getExtensionOrNull(GeneratedMessageLite.ExtendableMessage<M> extendableMessage, GeneratedMessageLite.GeneratedExtension<M, T> extension) {
        Intrinsics.checkNotNullParameter(extendableMessage, "<this>");
        Intrinsics.checkNotNullParameter(extension, "extension");
        if (extendableMessage.hasExtension(extension)) {
            return (T) extendableMessage.getExtension(extension);
        }
        return null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public static final <M extends GeneratedMessageLite.ExtendableMessage<M>, T> T getExtensionOrNull(GeneratedMessageLite.ExtendableMessage<M> extendableMessage, GeneratedMessageLite.GeneratedExtension<M, List<T>> extension, int i) {
        Intrinsics.checkNotNullParameter(extendableMessage, "<this>");
        Intrinsics.checkNotNullParameter(extension, "extension");
        if (i < extendableMessage.getExtensionCount(extension)) {
            return (T) extendableMessage.getExtension(extension, i);
        }
        return null;
    }
}
