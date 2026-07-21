package kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors;

import kotlin.reflect.jvm.internal.impl.descriptors.DeserializedDescriptor;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.protobuf.MessageLite;

/* JADX INFO: compiled from: DeserializedMemberDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface DeserializedMemberDescriptor extends DeserializedDescriptor, DescriptorWithContainerSource {
    DeserializedContainerSource getContainerSource();

    NameResolver getNameResolver();

    MessageLite getProto();

    TypeTable getTypeTable();
}
