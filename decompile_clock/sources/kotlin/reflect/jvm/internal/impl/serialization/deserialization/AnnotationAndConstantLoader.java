package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: AnnotationAndConstantLoader.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface AnnotationAndConstantLoader<A, C> extends AnnotationLoader<A> {
    C loadAnnotationDefaultValue(ProtoContainer protoContainer, ProtoBuf.Property property, KotlinType kotlinType);

    C loadPropertyConstant(ProtoContainer protoContainer, ProtoBuf.Property property, KotlinType kotlinType);
}
