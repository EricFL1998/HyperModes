package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.BinaryVersion;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.VersionRequirementTable;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.VersionSpecificBehaviorKt;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedContainerSource;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.text.Typography;

/* JADX INFO: compiled from: context.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DeserializationContext {
    private final DeserializationComponents components;
    private final DeserializedContainerSource containerSource;
    private final DeclarationDescriptor containingDeclaration;
    private final MemberDeserializer memberDeserializer;
    private final BinaryVersion metadataVersion;
    private final NameResolver nameResolver;
    private final TypeDeserializer typeDeserializer;
    private final TypeTable typeTable;
    private final VersionRequirementTable versionRequirementTable;

    public DeserializationContext(DeserializationComponents components, NameResolver nameResolver, DeclarationDescriptor containingDeclaration, TypeTable typeTable, VersionRequirementTable versionRequirementTable, BinaryVersion metadataVersion, DeserializedContainerSource deserializedContainerSource, TypeDeserializer typeDeserializer, List<ProtoBuf.TypeParameter> typeParameters) {
        String presentableString;
        Intrinsics.checkNotNullParameter(components, "components");
        Intrinsics.checkNotNullParameter(nameResolver, "nameResolver");
        Intrinsics.checkNotNullParameter(containingDeclaration, "containingDeclaration");
        Intrinsics.checkNotNullParameter(typeTable, "typeTable");
        Intrinsics.checkNotNullParameter(versionRequirementTable, "versionRequirementTable");
        Intrinsics.checkNotNullParameter(metadataVersion, "metadataVersion");
        Intrinsics.checkNotNullParameter(typeParameters, "typeParameters");
        this.components = components;
        this.nameResolver = nameResolver;
        this.containingDeclaration = containingDeclaration;
        this.typeTable = typeTable;
        this.versionRequirementTable = versionRequirementTable;
        this.metadataVersion = metadataVersion;
        this.containerSource = deserializedContainerSource;
        this.typeDeserializer = new TypeDeserializer(this, typeDeserializer, typeParameters, "Deserializer for \"" + containingDeclaration.getName() + Typography.quote, (deserializedContainerSource == null || (presentableString = deserializedContainerSource.getPresentableString()) == null) ? "[container not found]" : presentableString);
        this.memberDeserializer = new MemberDeserializer(this);
    }

    public final DeserializationComponents getComponents() {
        return this.components;
    }

    public final NameResolver getNameResolver() {
        return this.nameResolver;
    }

    public final DeclarationDescriptor getContainingDeclaration() {
        return this.containingDeclaration;
    }

    public final TypeTable getTypeTable() {
        return this.typeTable;
    }

    public final VersionRequirementTable getVersionRequirementTable() {
        return this.versionRequirementTable;
    }

    public final DeserializedContainerSource getContainerSource() {
        return this.containerSource;
    }

    public final TypeDeserializer getTypeDeserializer() {
        return this.typeDeserializer;
    }

    public final MemberDeserializer getMemberDeserializer() {
        return this.memberDeserializer;
    }

    public final StorageManager getStorageManager() {
        return this.components.getStorageManager();
    }

    public static /* synthetic */ DeserializationContext childContext$default(DeserializationContext deserializationContext, DeclarationDescriptor declarationDescriptor, List list, NameResolver nameResolver, TypeTable typeTable, VersionRequirementTable versionRequirementTable, BinaryVersion binaryVersion, int i, Object obj) {
        if ((i & 4) != 0) {
            nameResolver = deserializationContext.nameResolver;
        }
        NameResolver nameResolver2 = nameResolver;
        if ((i & 8) != 0) {
            typeTable = deserializationContext.typeTable;
        }
        TypeTable typeTable2 = typeTable;
        if ((i & 16) != 0) {
            versionRequirementTable = deserializationContext.versionRequirementTable;
        }
        VersionRequirementTable versionRequirementTable2 = versionRequirementTable;
        if ((i & 32) != 0) {
            binaryVersion = deserializationContext.metadataVersion;
        }
        return deserializationContext.childContext(declarationDescriptor, list, nameResolver2, typeTable2, versionRequirementTable2, binaryVersion);
    }

    public final DeserializationContext childContext(DeclarationDescriptor descriptor, List<ProtoBuf.TypeParameter> typeParameterProtos, NameResolver nameResolver, TypeTable typeTable, VersionRequirementTable versionRequirementTable, BinaryVersion metadataVersion) {
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        Intrinsics.checkNotNullParameter(typeParameterProtos, "typeParameterProtos");
        Intrinsics.checkNotNullParameter(nameResolver, "nameResolver");
        Intrinsics.checkNotNullParameter(typeTable, "typeTable");
        VersionRequirementTable versionRequirementTable2 = versionRequirementTable;
        Intrinsics.checkNotNullParameter(versionRequirementTable2, "versionRequirementTable");
        Intrinsics.checkNotNullParameter(metadataVersion, "metadataVersion");
        DeserializationComponents deserializationComponents = this.components;
        if (!VersionSpecificBehaviorKt.isVersionRequirementTableWrittenCorrectly(metadataVersion)) {
            versionRequirementTable2 = this.versionRequirementTable;
        }
        return new DeserializationContext(deserializationComponents, nameResolver, descriptor, typeTable, versionRequirementTable2, metadataVersion, this.containerSource, this.typeDeserializer, typeParameterProtos);
    }
}
