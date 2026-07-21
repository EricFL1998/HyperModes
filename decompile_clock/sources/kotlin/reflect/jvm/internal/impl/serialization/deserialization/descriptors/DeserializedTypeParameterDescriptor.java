package kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.descriptors.SupertypeLoopChecker;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.AbstractLazyTypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.ProtoTypeTableUtilKt;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.descriptorUtil.DescriptorUtilsKt;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationContext;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.NameResolverUtilKt;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ProtoEnumFlags;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.TypeDeserializer;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: DeserializedTypeParameterDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DeserializedTypeParameterDescriptor extends AbstractLazyTypeParameterDescriptor {
    private final DeserializedAnnotations annotations;
    private final DeserializationContext c;
    private final ProtoBuf.TypeParameter proto;

    /* JADX WARN: Illegal instructions before constructor call */
    public DeserializedTypeParameterDescriptor(DeserializationContext c, ProtoBuf.TypeParameter proto, int i) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(proto, "proto");
        StorageManager storageManager = c.getStorageManager();
        DeclarationDescriptor containingDeclaration = c.getContainingDeclaration();
        Annotations empty = Annotations.Companion.getEMPTY();
        Name name = NameResolverUtilKt.getName(c.getNameResolver(), proto.getName());
        ProtoEnumFlags protoEnumFlags = ProtoEnumFlags.INSTANCE;
        ProtoBuf.TypeParameter.Variance variance = proto.getVariance();
        Intrinsics.checkNotNullExpressionValue(variance, "getVariance(...)");
        super(storageManager, containingDeclaration, empty, name, protoEnumFlags.variance(variance), proto.getReified(), i, SourceElement.NO_SOURCE, SupertypeLoopChecker.EMPTY.INSTANCE);
        this.c = c;
        this.proto = proto;
        this.annotations = new DeserializedAnnotations(c.getStorageManager(), new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedTypeParameterDescriptor$$Lambda$0
            private final DeserializedTypeParameterDescriptor arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return DeserializedTypeParameterDescriptor.annotations$lambda$0(this.arg$0);
            }
        });
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotatedImpl, kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated
    public DeserializedAnnotations getAnnotations() {
        return this.annotations;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List annotations$lambda$0(DeserializedTypeParameterDescriptor this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return CollectionsKt.toList(this$0.c.getComponents().getAnnotationAndConstantLoader().loadTypeParameterAnnotations(this$0.proto, this$0.c.getNameResolver()));
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.AbstractTypeParameterDescriptor
    protected List<KotlinType> resolveUpperBounds() {
        List<ProtoBuf.Type> listUpperBounds = ProtoTypeTableUtilKt.upperBounds(this.proto, this.c.getTypeTable());
        if (listUpperBounds.isEmpty()) {
            return CollectionsKt.listOf(DescriptorUtilsKt.getBuiltIns(this).getDefaultBound());
        }
        List<ProtoBuf.Type> list = listUpperBounds;
        TypeDeserializer typeDeserializer = this.c.getTypeDeserializer();
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(list, 10));
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(typeDeserializer.type((ProtoBuf.Type) it.next()));
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.reflect.jvm.internal.impl.descriptors.impl.AbstractTypeParameterDescriptor
    /* JADX INFO: renamed from: reportSupertypeLoopError, reason: merged with bridge method [inline-methods] */
    public Void mo1494reportSupertypeLoopError(KotlinType type) {
        Intrinsics.checkNotNullParameter(type, "type");
        throw new IllegalStateException("There should be no cycles for deserialized type parameters, but found for: " + this);
    }
}
