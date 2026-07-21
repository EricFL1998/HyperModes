package kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.ClassDescriptorFactory;
import kotlin.reflect.jvm.internal.impl.incremental.UtilsKt;
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation;
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.BinaryVersion;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.VersionRequirementTable;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.DescriptorKindFilter;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationComponents;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationContext;

/* JADX INFO: compiled from: DeserializedPackageMemberScope.kt */
/* JADX INFO: loaded from: classes2.dex */
public class DeserializedPackageMemberScope extends DeserializedMemberScope {
    private final String debugName;
    private final PackageFragmentDescriptor packageDescriptor;
    private final FqName packageFqName;

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope
    protected void addEnumEntryDescriptors(Collection<DeclarationDescriptor> result, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public /* bridge */ /* synthetic */ Collection getContributedDescriptors(DescriptorKindFilter descriptorKindFilter, Function1 function1) {
        return getContributedDescriptors(descriptorKindFilter, (Function1<? super Name, Boolean>) function1);
    }

    public DeserializedPackageMemberScope(PackageFragmentDescriptor packageDescriptor, ProtoBuf.Package proto, NameResolver nameResolver, BinaryVersion metadataVersion, DeserializedContainerSource deserializedContainerSource, DeserializationComponents components, String debugName, Function0<? extends Collection<Name>> classNames) {
        Intrinsics.checkNotNullParameter(packageDescriptor, "packageDescriptor");
        Intrinsics.checkNotNullParameter(proto, "proto");
        Intrinsics.checkNotNullParameter(nameResolver, "nameResolver");
        Intrinsics.checkNotNullParameter(metadataVersion, "metadataVersion");
        Intrinsics.checkNotNullParameter(components, "components");
        Intrinsics.checkNotNullParameter(debugName, "debugName");
        Intrinsics.checkNotNullParameter(classNames, "classNames");
        ProtoBuf.TypeTable typeTable = proto.getTypeTable();
        Intrinsics.checkNotNullExpressionValue(typeTable, "getTypeTable(...)");
        TypeTable typeTable2 = new TypeTable(typeTable);
        VersionRequirementTable.Companion companion = VersionRequirementTable.Companion;
        ProtoBuf.VersionRequirementTable versionRequirementTable = proto.getVersionRequirementTable();
        Intrinsics.checkNotNullExpressionValue(versionRequirementTable, "getVersionRequirementTable(...)");
        DeserializationContext deserializationContextCreateContext = components.createContext(packageDescriptor, nameResolver, typeTable2, companion.create(versionRequirementTable), metadataVersion, deserializedContainerSource);
        List<ProtoBuf.Function> functionList = proto.getFunctionList();
        Intrinsics.checkNotNullExpressionValue(functionList, "getFunctionList(...)");
        List<ProtoBuf.Property> propertyList = proto.getPropertyList();
        Intrinsics.checkNotNullExpressionValue(propertyList, "getPropertyList(...)");
        List<ProtoBuf.TypeAlias> typeAliasList = proto.getTypeAliasList();
        Intrinsics.checkNotNullExpressionValue(typeAliasList, "getTypeAliasList(...)");
        super(deserializationContextCreateContext, functionList, propertyList, typeAliasList, classNames);
        this.packageDescriptor = packageDescriptor;
        this.debugName = debugName;
        this.packageFqName = packageDescriptor.getFqName();
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public List<DeclarationDescriptor> getContributedDescriptors(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        Collection<DeclarationDescriptor> collectionComputeDescriptors = computeDescriptors(kindFilter, nameFilter, NoLookupLocation.WHEN_GET_ALL_DESCRIPTORS);
        Iterable<ClassDescriptorFactory> fictitiousClassDescriptorFactories = getC().getComponents().getFictitiousClassDescriptorFactories();
        ArrayList arrayList = new ArrayList();
        Iterator<ClassDescriptorFactory> it = fictitiousClassDescriptorFactories.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(arrayList, it.next().getAllContributedClassesIfPossible(this.packageFqName));
        }
        return CollectionsKt.plus((Collection) collectionComputeDescriptors, (Iterable) arrayList);
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope
    protected boolean hasClass(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        if (!super.hasClass(name)) {
            Iterable<ClassDescriptorFactory> fictitiousClassDescriptorFactories = getC().getComponents().getFictitiousClassDescriptorFactories();
            if (!(fictitiousClassDescriptorFactories instanceof Collection) || !((Collection) fictitiousClassDescriptorFactories).isEmpty()) {
                Iterator<ClassDescriptorFactory> it = fictitiousClassDescriptorFactories.iterator();
                while (it.hasNext()) {
                    if (it.next().shouldCreateClass(this.packageFqName, name)) {
                    }
                }
            }
            return false;
        }
        return true;
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope
    protected ClassId createClassId(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return new ClassId(this.packageFqName, name);
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    /* JADX INFO: renamed from: getContributedClassifier */
    public ClassifierDescriptor mo1493getContributedClassifier(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        mo1497recordLookup(name, location);
        return super.mo1493getContributedClassifier(name, location);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    /* JADX INFO: renamed from: recordLookup */
    public void mo1497recordLookup(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        UtilsKt.record(getC().getComponents().getLookupTracker(), location, this.packageDescriptor, name);
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope
    protected Set<Name> getNonDeclaredFunctionNames() {
        return SetsKt.emptySet();
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope
    protected Set<Name> getNonDeclaredVariableNames() {
        return SetsKt.emptySet();
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope
    protected Set<Name> getNonDeclaredClassifierNames() {
        return SetsKt.emptySet();
    }

    public String toString() {
        return this.debugName;
    }
}
