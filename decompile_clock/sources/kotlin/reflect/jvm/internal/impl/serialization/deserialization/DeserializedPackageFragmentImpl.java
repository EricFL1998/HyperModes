package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SourceElement;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.BinaryVersion;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolverImpl;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedContainerSource;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedPackageMemberScope;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;

/* JADX INFO: compiled from: DeserializedPackageFragmentImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class DeserializedPackageFragmentImpl extends DeserializedPackageFragment {
    private MemberScope _memberScope;
    private ProtoBuf.PackageFragment _proto;
    private final ProtoBasedClassDataFinder classDataFinder;
    private final DeserializedContainerSource containerSource;
    private final BinaryVersion metadataVersion;
    private final NameResolverImpl nameResolver;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public DeserializedPackageFragmentImpl(FqName fqName, StorageManager storageManager, ModuleDescriptor module, ProtoBuf.PackageFragment proto, BinaryVersion metadataVersion, DeserializedContainerSource deserializedContainerSource) {
        super(fqName, storageManager, module);
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(module, "module");
        Intrinsics.checkNotNullParameter(proto, "proto");
        Intrinsics.checkNotNullParameter(metadataVersion, "metadataVersion");
        this.metadataVersion = metadataVersion;
        this.containerSource = deserializedContainerSource;
        ProtoBuf.StringTable strings = proto.getStrings();
        Intrinsics.checkNotNullExpressionValue(strings, "getStrings(...)");
        ProtoBuf.QualifiedNameTable qualifiedNames = proto.getQualifiedNames();
        Intrinsics.checkNotNullExpressionValue(qualifiedNames, "getQualifiedNames(...)");
        NameResolverImpl nameResolverImpl = new NameResolverImpl(strings, qualifiedNames);
        this.nameResolver = nameResolverImpl;
        this.classDataFinder = new ProtoBasedClassDataFinder(proto, nameResolverImpl, metadataVersion, new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializedPackageFragmentImpl$$Lambda$0
            private final DeserializedPackageFragmentImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function1
            public Object invoke(Object obj) {
                return DeserializedPackageFragmentImpl.classDataFinder$lambda$0(this.arg$0, (ClassId) obj);
            }
        });
        this._proto = proto;
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializedPackageFragment
    public ProtoBasedClassDataFinder getClassDataFinder() {
        return this.classDataFinder;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final SourceElement classDataFinder$lambda$0(DeserializedPackageFragmentImpl this$0, ClassId it) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(it, "it");
        DeserializedContainerSource deserializedContainerSource = this$0.containerSource;
        if (deserializedContainerSource != null) {
            return deserializedContainerSource;
        }
        SourceElement NO_SOURCE = SourceElement.NO_SOURCE;
        Intrinsics.checkNotNullExpressionValue(NO_SOURCE, "NO_SOURCE");
        return NO_SOURCE;
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializedPackageFragment
    public void initialize(DeserializationComponents components) {
        Intrinsics.checkNotNullParameter(components, "components");
        ProtoBuf.PackageFragment packageFragment = this._proto;
        if (packageFragment == null) {
            throw new IllegalStateException("Repeated call to DeserializedPackageFragmentImpl::initialize".toString());
        }
        this._proto = null;
        ProtoBuf.Package r4 = packageFragment.getPackage();
        Intrinsics.checkNotNullExpressionValue(r4, "getPackage(...)");
        this._memberScope = new DeserializedPackageMemberScope(this, r4, this.nameResolver, this.metadataVersion, this.containerSource, components, "scope of " + this, new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializedPackageFragmentImpl$$Lambda$1
            private final DeserializedPackageFragmentImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return DeserializedPackageFragmentImpl.initialize$lambda$3(this.arg$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Collection initialize$lambda$3(DeserializedPackageFragmentImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Collection<ClassId> allClassIds = this$0.getClassDataFinder().getAllClassIds();
        ArrayList arrayList = new ArrayList();
        for (Object obj : allClassIds) {
            ClassId classId = (ClassId) obj;
            if (!classId.isNestedClass() && !ClassDeserializer.Companion.getBLACK_LIST().contains(classId)) {
                arrayList.add(obj);
            }
        }
        ArrayList arrayList2 = arrayList;
        ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(arrayList2, 10));
        Iterator it = arrayList2.iterator();
        while (it.hasNext()) {
            arrayList3.add(((ClassId) it.next()).getShortClassName());
        }
        return arrayList3;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor
    public MemberScope getMemberScope() {
        MemberScope memberScope = this._memberScope;
        if (memberScope != null) {
            return memberScope;
        }
        Intrinsics.throwUninitializedPropertyAccessException("_memberScope");
        return null;
    }
}
