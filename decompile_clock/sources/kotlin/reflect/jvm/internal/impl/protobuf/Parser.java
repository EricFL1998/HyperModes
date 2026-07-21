package kotlin.reflect.jvm.internal.impl.protobuf;

import java.io.InputStream;

/* JADX INFO: loaded from: classes2.dex */
public interface Parser<MessageType> {
    MessageType parseDelimitedFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException;

    MessageType parseFrom(InputStream inputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException;

    MessageType parseFrom(ByteString byteString, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException;

    MessageType parsePartialFrom(CodedInputStream codedInputStream, ExtensionRegistryLite extensionRegistryLite) throws InvalidProtocolBufferException;
}
