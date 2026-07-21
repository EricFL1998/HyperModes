package kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeAliasDescriptor;
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.protobuf.AbstractMessageLite;
import kotlin.reflect.jvm.internal.impl.protobuf.MessageLite;
import kotlin.reflect.jvm.internal.impl.protobuf.Parser;
import kotlin.reflect.jvm.internal.impl.resolve.MemberComparator;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.DescriptorKindFilter;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationContext;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.MemberDeserializer;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.NameResolverUtilKt;
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNotNull;
import kotlin.reflect.jvm.internal.impl.storage.MemoizedFunctionToNullable;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.NullableLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageKt;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.sequences.SequencesKt;

/* JADX INFO: compiled from: DeserializedMemberScope.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class DeserializedMemberScope extends MemberScopeImpl {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(DeserializedMemberScope.class), "classNames", "getClassNames$deserialization()Ljava/util/Set;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(DeserializedMemberScope.class), "classifierNamesLazy", "getClassifierNamesLazy()Ljava/util/Set;"))};
    private final DeserializationContext c;
    private final NotNullLazyValue classNames$delegate;
    private final NullableLazyValue classifierNamesLazy$delegate;
    private final Implementation impl;

    /* JADX INFO: compiled from: DeserializedMemberScope.kt */
    private interface Implementation {
        void addFunctionsAndPropertiesTo(Collection<DeclarationDescriptor> collection, DescriptorKindFilter descriptorKindFilter, Function1<? super Name, Boolean> function1, LookupLocation lookupLocation);

        Collection<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation lookupLocation);

        Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation lookupLocation);

        Set<Name> getFunctionNames();

        TypeAliasDescriptor getTypeAliasByName(Name name);

        Set<Name> getTypeAliasNames();

        Set<Name> getVariableNames();
    }

    protected abstract void addEnumEntryDescriptors(Collection<DeclarationDescriptor> collection, Function1<? super Name, Boolean> function1);

    protected void computeNonDeclaredFunctions(Name name, List<SimpleFunctionDescriptor> functions) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(functions, "functions");
    }

    protected void computeNonDeclaredProperties(Name name, List<PropertyDescriptor> descriptors) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(descriptors, "descriptors");
    }

    protected abstract ClassId createClassId(Name name);

    protected abstract Set<Name> getNonDeclaredClassifierNames();

    protected abstract Set<Name> getNonDeclaredFunctionNames();

    protected abstract Set<Name> getNonDeclaredVariableNames();

    protected boolean isDeclaredFunctionAvailable(SimpleFunctionDescriptor function) {
        Intrinsics.checkNotNullParameter(function, "function");
        return true;
    }

    protected final DeserializationContext getC() {
        return this.c;
    }

    protected DeserializedMemberScope(DeserializationContext c, List<ProtoBuf.Function> functionList, List<ProtoBuf.Property> propertyList, List<ProtoBuf.TypeAlias> typeAliasList, final Function0<? extends Collection<Name>> classNames) {
        Intrinsics.checkNotNullParameter(c, "c");
        Intrinsics.checkNotNullParameter(functionList, "functionList");
        Intrinsics.checkNotNullParameter(propertyList, "propertyList");
        Intrinsics.checkNotNullParameter(typeAliasList, "typeAliasList");
        Intrinsics.checkNotNullParameter(classNames, "classNames");
        this.c = c;
        this.impl = createImplementation(functionList, propertyList, typeAliasList);
        this.classNames$delegate = c.getStorageManager().createLazyValue(new Function0(classNames) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$$Lambda$0
            private final Function0 arg$0;

            {
                this.arg$0 = classNames;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return DeserializedMemberScope.classNames_delegate$lambda$0(this.arg$0);
            }
        });
        this.classifierNamesLazy$delegate = c.getStorageManager().createNullableLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$$Lambda$1
            private final DeserializedMemberScope arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return DeserializedMemberScope.classifierNamesLazy_delegate$lambda$1(this.arg$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Set classNames_delegate$lambda$0(Function0 classNames) {
        Intrinsics.checkNotNullParameter(classNames, "$classNames");
        return CollectionsKt.toSet((Iterable) classNames.invoke());
    }

    public final Set<Name> getClassNames$deserialization() {
        return (Set) StorageKt.getValue(this.classNames$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
    }

    private final Set<Name> getClassifierNamesLazy() {
        return (Set) StorageKt.getValue(this.classifierNamesLazy$delegate, this, (KProperty<?>) $$delegatedProperties[1]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Set classifierNamesLazy_delegate$lambda$1(DeserializedMemberScope this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Set<Name> nonDeclaredClassifierNames = this$0.getNonDeclaredClassifierNames();
        if (nonDeclaredClassifierNames == null) {
            return null;
        }
        return SetsKt.plus(SetsKt.plus((Set) this$0.getClassNames$deserialization(), (Iterable) this$0.impl.getTypeAliasNames()), (Iterable) nonDeclaredClassifierNames);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Set<Name> getFunctionNames() {
        return this.impl.getFunctionNames();
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Set<Name> getVariableNames() {
        return this.impl.getVariableNames();
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Set<Name> getClassifierNames() {
        return getClassifierNamesLazy();
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public Collection<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        return this.impl.getContributedFunctions(name, location);
    }

    private final TypeAliasDescriptor getTypeAliasByName(Name name) {
        return this.impl.getTypeAliasByName(name);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        return this.impl.getContributedVariables(name, location);
    }

    protected final Collection<DeclarationDescriptor> computeDescriptors(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> nameFilter, LookupLocation location) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        Intrinsics.checkNotNullParameter(location, "location");
        ArrayList arrayList = new ArrayList(0);
        if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getSINGLETON_CLASSIFIERS_MASK())) {
            addEnumEntryDescriptors(arrayList, nameFilter);
        }
        ArrayList arrayList2 = arrayList;
        this.impl.addFunctionsAndPropertiesTo(arrayList2, kindFilter, nameFilter, location);
        if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getCLASSIFIERS_MASK())) {
            for (Name name : getClassNames$deserialization()) {
                if (nameFilter.invoke(name).booleanValue()) {
                    kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(arrayList2, deserializeClass(name));
                }
            }
        }
        if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getTYPE_ALIASES_MASK())) {
            for (Name name2 : this.impl.getTypeAliasNames()) {
                if (nameFilter.invoke(name2).booleanValue()) {
                    kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.addIfNotNull(arrayList2, this.impl.getTypeAliasByName(name2));
                }
            }
        }
        return kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.compact(arrayList);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    /* JADX INFO: renamed from: getContributedClassifier */
    public ClassifierDescriptor mo1493getContributedClassifier(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        if (hasClass(name)) {
            return deserializeClass(name);
        }
        if (this.impl.getTypeAliasNames().contains(name)) {
            return getTypeAliasByName(name);
        }
        return null;
    }

    private final ClassDescriptor deserializeClass(Name name) {
        return this.c.getComponents().deserializeClass(createClassId(name));
    }

    protected boolean hasClass(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return getClassNames$deserialization().contains(name);
    }

    private final Implementation createImplementation(List<ProtoBuf.Function> list, List<ProtoBuf.Property> list2, List<ProtoBuf.TypeAlias> list3) {
        if (this.c.getComponents().getConfiguration().getPreserveDeclarationsOrdering()) {
            return new NoReorderImplementation(this, list, list2, list3);
        }
        return new OptimizedImplementation(this, list, list2, list3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: DeserializedMemberScope.kt */
    final class OptimizedImplementation implements Implementation {
        static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(OptimizedImplementation.class), "functionNames", "getFunctionNames()Ljava/util/Set;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(OptimizedImplementation.class), "variableNames", "getVariableNames()Ljava/util/Set;"))};
        private final NotNullLazyValue functionNames$delegate;
        private final Map<Name, byte[]> functionProtosBytes;
        private final MemoizedFunctionToNotNull<Name, Collection<SimpleFunctionDescriptor>> functions;
        private final MemoizedFunctionToNotNull<Name, Collection<PropertyDescriptor>> properties;
        private final Map<Name, byte[]> propertyProtosBytes;
        final /* synthetic */ DeserializedMemberScope this$0;
        private final MemoizedFunctionToNullable<Name, TypeAliasDescriptor> typeAliasByName;
        private final Map<Name, byte[]> typeAliasBytes;
        private final NotNullLazyValue variableNames$delegate;

        public OptimizedImplementation(DeserializedMemberScope deserializedMemberScope, List<ProtoBuf.Function> functionList, List<ProtoBuf.Property> propertyList, List<ProtoBuf.TypeAlias> typeAliasList) throws IOException {
            Map<Name, byte[]> mapEmptyMap;
            Intrinsics.checkNotNullParameter(functionList, "functionList");
            Intrinsics.checkNotNullParameter(propertyList, "propertyList");
            Intrinsics.checkNotNullParameter(typeAliasList, "typeAliasList");
            this.this$0 = deserializedMemberScope;
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (Object obj : functionList) {
                Name name = NameResolverUtilKt.getName(deserializedMemberScope.getC().getNameResolver(), ((ProtoBuf.Function) ((MessageLite) obj)).getName());
                Object obj2 = linkedHashMap.get(name);
                if (obj2 == null) {
                    obj2 = (List) new ArrayList();
                    linkedHashMap.put(name, obj2);
                }
                ((List) obj2).add(obj);
            }
            this.functionProtosBytes = packToByteArray(linkedHashMap);
            DeserializedMemberScope deserializedMemberScope2 = this.this$0;
            LinkedHashMap linkedHashMap2 = new LinkedHashMap();
            for (Object obj3 : propertyList) {
                Name name2 = NameResolverUtilKt.getName(deserializedMemberScope2.getC().getNameResolver(), ((ProtoBuf.Property) ((MessageLite) obj3)).getName());
                Object obj4 = linkedHashMap2.get(name2);
                if (obj4 == null) {
                    obj4 = (List) new ArrayList();
                    linkedHashMap2.put(name2, obj4);
                }
                ((List) obj4).add(obj3);
            }
            this.propertyProtosBytes = packToByteArray(linkedHashMap2);
            if (!this.this$0.getC().getComponents().getConfiguration().getTypeAliasesAllowed()) {
                mapEmptyMap = MapsKt.emptyMap();
            } else {
                DeserializedMemberScope deserializedMemberScope3 = this.this$0;
                LinkedHashMap linkedHashMap3 = new LinkedHashMap();
                for (Object obj5 : typeAliasList) {
                    Name name3 = NameResolverUtilKt.getName(deserializedMemberScope3.getC().getNameResolver(), ((ProtoBuf.TypeAlias) ((MessageLite) obj5)).getName());
                    Object obj6 = linkedHashMap3.get(name3);
                    if (obj6 == null) {
                        obj6 = (List) new ArrayList();
                        linkedHashMap3.put(name3, obj6);
                    }
                    ((List) obj6).add(obj5);
                }
                mapEmptyMap = packToByteArray(linkedHashMap3);
            }
            this.typeAliasBytes = mapEmptyMap;
            this.functions = this.this$0.getC().getStorageManager().createMemoizedFunction(new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$OptimizedImplementation$$Lambda$0
                private final DeserializedMemberScope.OptimizedImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj7) {
                    return DeserializedMemberScope.OptimizedImplementation.functions$lambda$5(this.arg$0, (Name) obj7);
                }
            });
            this.properties = this.this$0.getC().getStorageManager().createMemoizedFunction(new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$OptimizedImplementation$$Lambda$1
                private final DeserializedMemberScope.OptimizedImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj7) {
                    return DeserializedMemberScope.OptimizedImplementation.properties$lambda$6(this.arg$0, (Name) obj7);
                }
            });
            this.typeAliasByName = this.this$0.getC().getStorageManager().createMemoizedFunctionWithNullableValues(new Function1(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$OptimizedImplementation$$Lambda$2
                private final DeserializedMemberScope.OptimizedImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function1
                public Object invoke(Object obj7) {
                    return DeserializedMemberScope.OptimizedImplementation.typeAliasByName$lambda$7(this.arg$0, (Name) obj7);
                }
            });
            StorageManager storageManager = this.this$0.getC().getStorageManager();
            final DeserializedMemberScope deserializedMemberScope4 = this.this$0;
            this.functionNames$delegate = storageManager.createLazyValue(new Function0(this, deserializedMemberScope4) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$OptimizedImplementation$$Lambda$3
                private final DeserializedMemberScope.OptimizedImplementation arg$0;
                private final DeserializedMemberScope arg$1;

                {
                    this.arg$0 = this;
                    this.arg$1 = deserializedMemberScope4;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.OptimizedImplementation.functionNames_delegate$lambda$8(this.arg$0, this.arg$1);
                }
            });
            StorageManager storageManager2 = this.this$0.getC().getStorageManager();
            final DeserializedMemberScope deserializedMemberScope5 = this.this$0;
            this.variableNames$delegate = storageManager2.createLazyValue(new Function0(this, deserializedMemberScope5) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$OptimizedImplementation$$Lambda$4
                private final DeserializedMemberScope.OptimizedImplementation arg$0;
                private final DeserializedMemberScope arg$1;

                {
                    this.arg$0 = this;
                    this.arg$1 = deserializedMemberScope5;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.OptimizedImplementation.variableNames_delegate$lambda$9(this.arg$0, this.arg$1);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Collection functions$lambda$5(OptimizedImplementation this$0, Name it) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(it, "it");
            return this$0.computeFunctions(it);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Collection properties$lambda$6(OptimizedImplementation this$0, Name it) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(it, "it");
            return this$0.computeProperties(it);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final TypeAliasDescriptor typeAliasByName$lambda$7(OptimizedImplementation this$0, Name it) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(it, "it");
            return this$0.createTypeAlias(it);
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Set<Name> getFunctionNames() {
            return (Set) StorageKt.getValue(this.functionNames$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Set functionNames_delegate$lambda$8(OptimizedImplementation this$0, DeserializedMemberScope this$1) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            return SetsKt.plus((Set) this$0.functionProtosBytes.keySet(), (Iterable) this$1.getNonDeclaredFunctionNames());
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Set<Name> getVariableNames() {
            return (Set) StorageKt.getValue(this.variableNames$delegate, this, (KProperty<?>) $$delegatedProperties[1]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Set variableNames_delegate$lambda$9(OptimizedImplementation this$0, DeserializedMemberScope this$1) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            return SetsKt.plus((Set) this$0.propertyProtosBytes.keySet(), (Iterable) this$1.getNonDeclaredVariableNames());
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Set<Name> getTypeAliasNames() {
            return this.typeAliasBytes.keySet();
        }

        /* JADX WARN: Code duplicated, block: B:7:0x002e  */
        private final Collection<SimpleFunctionDescriptor> computeFunctions(Name name) {
            List<ProtoBuf.Function> listEmptyList;
            Map<Name, byte[]> map = this.functionProtosBytes;
            Parser<ProtoBuf.Function> PARSER = ProtoBuf.Function.PARSER;
            Intrinsics.checkNotNullExpressionValue(PARSER, "PARSER");
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            byte[] bArr = map.get(name);
            if (bArr != null) {
                List list = SequencesKt.toList(SequencesKt.generateSequence(new DeserializedMemberScope$OptimizedImplementation$computeDescriptors$1$1(PARSER, new ByteArrayInputStream(bArr), this.this$0)));
                if (list != null) {
                    listEmptyList = list;
                } else {
                    listEmptyList = CollectionsKt.emptyList();
                }
            } else {
                listEmptyList = CollectionsKt.emptyList();
            }
            ArrayList arrayList = new ArrayList(listEmptyList.size());
            for (ProtoBuf.Function function : listEmptyList) {
                MemberDeserializer memberDeserializer = deserializedMemberScope.getC().getMemberDeserializer();
                Intrinsics.checkNotNull(function);
                SimpleFunctionDescriptor simpleFunctionDescriptorLoadFunction = memberDeserializer.loadFunction(function);
                if (!deserializedMemberScope.isDeclaredFunctionAvailable(simpleFunctionDescriptorLoadFunction)) {
                    simpleFunctionDescriptorLoadFunction = null;
                }
                if (simpleFunctionDescriptorLoadFunction != null) {
                    arrayList.add(simpleFunctionDescriptorLoadFunction);
                }
            }
            ArrayList arrayList2 = arrayList;
            deserializedMemberScope.computeNonDeclaredFunctions(name, arrayList2);
            return kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.compact(arrayList2);
        }

        /* JADX WARN: Code duplicated, block: B:7:0x002e  */
        private final Collection<PropertyDescriptor> computeProperties(Name name) {
            List<ProtoBuf.Property> listEmptyList;
            Map<Name, byte[]> map = this.propertyProtosBytes;
            Parser<ProtoBuf.Property> PARSER = ProtoBuf.Property.PARSER;
            Intrinsics.checkNotNullExpressionValue(PARSER, "PARSER");
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            byte[] bArr = map.get(name);
            if (bArr != null) {
                List list = SequencesKt.toList(SequencesKt.generateSequence(new DeserializedMemberScope$OptimizedImplementation$computeDescriptors$1$1(PARSER, new ByteArrayInputStream(bArr), this.this$0)));
                if (list != null) {
                    listEmptyList = list;
                } else {
                    listEmptyList = CollectionsKt.emptyList();
                }
            } else {
                listEmptyList = CollectionsKt.emptyList();
            }
            ArrayList arrayList = new ArrayList(listEmptyList.size());
            for (ProtoBuf.Property property : listEmptyList) {
                MemberDeserializer memberDeserializer = deserializedMemberScope.getC().getMemberDeserializer();
                Intrinsics.checkNotNull(property);
                PropertyDescriptor propertyDescriptorLoadProperty = memberDeserializer.loadProperty(property);
                if (propertyDescriptorLoadProperty != null) {
                    arrayList.add(propertyDescriptorLoadProperty);
                }
            }
            ArrayList arrayList2 = arrayList;
            deserializedMemberScope.computeNonDeclaredProperties(name, arrayList2);
            return kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.compact(arrayList2);
        }

        private final TypeAliasDescriptor createTypeAlias(Name name) {
            ProtoBuf.TypeAlias delimitedFrom;
            byte[] bArr = this.typeAliasBytes.get(name);
            if (bArr == null || (delimitedFrom = ProtoBuf.TypeAlias.parseDelimitedFrom(new ByteArrayInputStream(bArr), this.this$0.getC().getComponents().getExtensionRegistryLite())) == null) {
                return null;
            }
            return this.this$0.getC().getMemberDeserializer().loadTypeAlias(delimitedFrom);
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Collection<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation location) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(location, "location");
            return !getFunctionNames().contains(name) ? CollectionsKt.emptyList() : this.functions.invoke(name);
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public TypeAliasDescriptor getTypeAliasByName(Name name) {
            Intrinsics.checkNotNullParameter(name, "name");
            return this.typeAliasByName.invoke(name);
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation location) {
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(location, "location");
            return !getVariableNames().contains(name) ? CollectionsKt.emptyList() : this.properties.invoke(name);
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public void addFunctionsAndPropertiesTo(Collection<DeclarationDescriptor> result, DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> nameFilter, LookupLocation location) {
            Intrinsics.checkNotNullParameter(result, "result");
            Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
            Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
            Intrinsics.checkNotNullParameter(location, "location");
            if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getVARIABLES_MASK())) {
                Set<Name> variableNames = getVariableNames();
                ArrayList arrayList = new ArrayList();
                for (Name name : variableNames) {
                    if (nameFilter.invoke(name).booleanValue()) {
                        arrayList.addAll(getContributedVariables(name, location));
                    }
                }
                MemberComparator.NameAndTypeMemberComparator INSTANCE = MemberComparator.NameAndTypeMemberComparator.INSTANCE;
                Intrinsics.checkNotNullExpressionValue(INSTANCE, "INSTANCE");
                CollectionsKt.sortWith(arrayList, INSTANCE);
                result.addAll(arrayList);
            }
            if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getFUNCTIONS_MASK())) {
                Set<Name> functionNames = getFunctionNames();
                ArrayList arrayList2 = new ArrayList();
                for (Name name2 : functionNames) {
                    if (nameFilter.invoke(name2).booleanValue()) {
                        arrayList2.addAll(getContributedFunctions(name2, location));
                    }
                }
                MemberComparator.NameAndTypeMemberComparator INSTANCE2 = MemberComparator.NameAndTypeMemberComparator.INSTANCE;
                Intrinsics.checkNotNullExpressionValue(INSTANCE2, "INSTANCE");
                CollectionsKt.sortWith(arrayList2, INSTANCE2);
                result.addAll(arrayList2);
            }
        }

        private final Map<Name, byte[]> packToByteArray(Map<Name, ? extends Collection<? extends AbstractMessageLite>> map) throws IOException {
            LinkedHashMap linkedHashMap = new LinkedHashMap(MapsKt.mapCapacity(map.size()));
            Iterator<T> it = map.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                Object key = entry.getKey();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                Iterable iterable = (Iterable) entry.getValue();
                ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10));
                Iterator it2 = iterable.iterator();
                while (it2.hasNext()) {
                    ((AbstractMessageLite) it2.next()).writeDelimitedTo(byteArrayOutputStream);
                    arrayList.add(Unit.INSTANCE);
                }
                linkedHashMap.put(key, byteArrayOutputStream.toByteArray());
            }
            return linkedHashMap;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: DeserializedMemberScope.kt */
    final class NoReorderImplementation implements Implementation {
        static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "declaredFunctions", "getDeclaredFunctions()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "declaredProperties", "getDeclaredProperties()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "allTypeAliases", "getAllTypeAliases()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "allFunctions", "getAllFunctions()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "allProperties", "getAllProperties()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "typeAliasesByName", "getTypeAliasesByName()Ljava/util/Map;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "functionsByName", "getFunctionsByName()Ljava/util/Map;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "propertiesByName", "getPropertiesByName()Ljava/util/Map;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "functionNames", "getFunctionNames()Ljava/util/Set;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(NoReorderImplementation.class), "variableNames", "getVariableNames()Ljava/util/Set;"))};
        private final NotNullLazyValue allFunctions$delegate;
        private final NotNullLazyValue allProperties$delegate;
        private final NotNullLazyValue allTypeAliases$delegate;
        private final NotNullLazyValue declaredFunctions$delegate;
        private final NotNullLazyValue declaredProperties$delegate;
        private final List<ProtoBuf.Function> functionList;
        private final NotNullLazyValue functionNames$delegate;
        private final NotNullLazyValue functionsByName$delegate;
        private final NotNullLazyValue propertiesByName$delegate;
        private final List<ProtoBuf.Property> propertyList;
        final /* synthetic */ DeserializedMemberScope this$0;
        private final List<ProtoBuf.TypeAlias> typeAliasList;
        private final NotNullLazyValue typeAliasesByName$delegate;
        private final NotNullLazyValue variableNames$delegate;

        public NoReorderImplementation(final DeserializedMemberScope deserializedMemberScope, List<ProtoBuf.Function> functionList, List<ProtoBuf.Property> propertyList, List<ProtoBuf.TypeAlias> typeAliasList) {
            Intrinsics.checkNotNullParameter(functionList, "functionList");
            Intrinsics.checkNotNullParameter(propertyList, "propertyList");
            Intrinsics.checkNotNullParameter(typeAliasList, "typeAliasList");
            this.this$0 = deserializedMemberScope;
            this.functionList = functionList;
            this.propertyList = propertyList;
            this.typeAliasList = deserializedMemberScope.getC().getComponents().getConfiguration().getTypeAliasesAllowed() ? typeAliasList : CollectionsKt.emptyList();
            this.declaredFunctions$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$0
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.declaredFunctions_delegate$lambda$0(this.arg$0);
                }
            });
            this.declaredProperties$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$1
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.declaredProperties_delegate$lambda$1(this.arg$0);
                }
            });
            this.allTypeAliases$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$2
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.allTypeAliases_delegate$lambda$2(this.arg$0);
                }
            });
            this.allFunctions$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$3
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.allFunctions_delegate$lambda$3(this.arg$0);
                }
            });
            this.allProperties$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$4
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.allProperties_delegate$lambda$4(this.arg$0);
                }
            });
            this.typeAliasesByName$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$5
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.typeAliasesByName_delegate$lambda$6(this.arg$0);
                }
            });
            this.functionsByName$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$6
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.functionsByName_delegate$lambda$8(this.arg$0);
                }
            });
            this.propertiesByName$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$7
                private final DeserializedMemberScope.NoReorderImplementation arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.propertiesByName_delegate$lambda$10(this.arg$0);
                }
            });
            this.functionNames$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this, deserializedMemberScope) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$8
                private final DeserializedMemberScope.NoReorderImplementation arg$0;
                private final DeserializedMemberScope arg$1;

                {
                    this.arg$0 = this;
                    this.arg$1 = deserializedMemberScope;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.functionNames_delegate$lambda$12(this.arg$0, this.arg$1);
                }
            });
            this.variableNames$delegate = deserializedMemberScope.getC().getStorageManager().createLazyValue(new Function0(this, deserializedMemberScope) { // from class: kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope$NoReorderImplementation$$Lambda$9
                private final DeserializedMemberScope.NoReorderImplementation arg$0;
                private final DeserializedMemberScope arg$1;

                {
                    this.arg$0 = this;
                    this.arg$1 = deserializedMemberScope;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return DeserializedMemberScope.NoReorderImplementation.variableNames_delegate$lambda$14(this.arg$0, this.arg$1);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final List declaredFunctions_delegate$lambda$0(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return this$0.computeFunctions();
        }

        private final List<SimpleFunctionDescriptor> getDeclaredFunctions() {
            return (List) StorageKt.getValue(this.declaredFunctions$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final List declaredProperties_delegate$lambda$1(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return this$0.computeProperties();
        }

        private final List<PropertyDescriptor> getDeclaredProperties() {
            return (List) StorageKt.getValue(this.declaredProperties$delegate, this, (KProperty<?>) $$delegatedProperties[1]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final List allTypeAliases_delegate$lambda$2(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return this$0.computeTypeAliases();
        }

        private final List<TypeAliasDescriptor> getAllTypeAliases() {
            return (List) StorageKt.getValue(this.allTypeAliases$delegate, this, (KProperty<?>) $$delegatedProperties[2]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final List allFunctions_delegate$lambda$3(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return CollectionsKt.plus((Collection) this$0.getDeclaredFunctions(), (Iterable) this$0.computeAllNonDeclaredFunctions());
        }

        private final List<SimpleFunctionDescriptor> getAllFunctions() {
            return (List) StorageKt.getValue(this.allFunctions$delegate, this, (KProperty<?>) $$delegatedProperties[3]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final List allProperties_delegate$lambda$4(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return CollectionsKt.plus((Collection) this$0.getDeclaredProperties(), (Iterable) this$0.computeAllNonDeclaredProperties());
        }

        private final List<PropertyDescriptor> getAllProperties() {
            return (List) StorageKt.getValue(this.allProperties$delegate, this, (KProperty<?>) $$delegatedProperties[4]);
        }

        private final Map<Name, TypeAliasDescriptor> getTypeAliasesByName() {
            return (Map) StorageKt.getValue(this.typeAliasesByName$delegate, this, (KProperty<?>) $$delegatedProperties[5]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Map typeAliasesByName_delegate$lambda$6(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            List<TypeAliasDescriptor> allTypeAliases = this$0.getAllTypeAliases();
            LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(allTypeAliases, 10)), 16));
            for (Object obj : allTypeAliases) {
                Name name = ((TypeAliasDescriptor) obj).getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                linkedHashMap.put(name, obj);
            }
            return linkedHashMap;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Map functionsByName_delegate$lambda$8(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            List<SimpleFunctionDescriptor> allFunctions = this$0.getAllFunctions();
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (Object obj : allFunctions) {
                Name name = ((SimpleFunctionDescriptor) obj).getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                Object obj2 = linkedHashMap.get(name);
                if (obj2 == null) {
                    obj2 = (List) new ArrayList();
                    linkedHashMap.put(name, obj2);
                }
                ((List) obj2).add(obj);
            }
            return linkedHashMap;
        }

        private final Map<Name, Collection<SimpleFunctionDescriptor>> getFunctionsByName() {
            return (Map) StorageKt.getValue(this.functionsByName$delegate, this, (KProperty<?>) $$delegatedProperties[6]);
        }

        private final Map<Name, Collection<PropertyDescriptor>> getPropertiesByName() {
            return (Map) StorageKt.getValue(this.propertiesByName$delegate, this, (KProperty<?>) $$delegatedProperties[7]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Map propertiesByName_delegate$lambda$10(NoReorderImplementation this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            List<PropertyDescriptor> allProperties = this$0.getAllProperties();
            LinkedHashMap linkedHashMap = new LinkedHashMap();
            for (Object obj : allProperties) {
                Name name = ((PropertyDescriptor) obj).getName();
                Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                Object obj2 = linkedHashMap.get(name);
                if (obj2 == null) {
                    obj2 = (List) new ArrayList();
                    linkedHashMap.put(name, obj2);
                }
                ((List) obj2).add(obj);
            }
            return linkedHashMap;
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Set<Name> getFunctionNames() {
            return (Set) StorageKt.getValue(this.functionNames$delegate, this, (KProperty<?>) $$delegatedProperties[8]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Set functionNames_delegate$lambda$12(NoReorderImplementation this$0, DeserializedMemberScope this$1) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            List<ProtoBuf.Function> list = this$0.functionList;
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            DeserializedMemberScope deserializedMemberScope = this$0.this$0;
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                linkedHashSet.add(NameResolverUtilKt.getName(deserializedMemberScope.getC().getNameResolver(), ((ProtoBuf.Function) ((MessageLite) it.next())).getName()));
            }
            return SetsKt.plus((Set) linkedHashSet, (Iterable) this$1.getNonDeclaredFunctionNames());
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Set<Name> getVariableNames() {
            return (Set) StorageKt.getValue(this.variableNames$delegate, this, (KProperty<?>) $$delegatedProperties[9]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Set variableNames_delegate$lambda$14(NoReorderImplementation this$0, DeserializedMemberScope this$1) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            List<ProtoBuf.Property> list = this$0.propertyList;
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            DeserializedMemberScope deserializedMemberScope = this$0.this$0;
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                linkedHashSet.add(NameResolverUtilKt.getName(deserializedMemberScope.getC().getNameResolver(), ((ProtoBuf.Property) ((MessageLite) it.next())).getName()));
            }
            return SetsKt.plus((Set) linkedHashSet, (Iterable) this$1.getNonDeclaredVariableNames());
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Set<Name> getTypeAliasNames() {
            List<ProtoBuf.TypeAlias> list = this.typeAliasList;
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                linkedHashSet.add(NameResolverUtilKt.getName(deserializedMemberScope.getC().getNameResolver(), ((ProtoBuf.TypeAlias) ((MessageLite) it.next())).getName()));
            }
            return linkedHashSet;
        }

        private final List<SimpleFunctionDescriptor> computeFunctions() {
            List<ProtoBuf.Function> list = this.functionList;
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            ArrayList arrayList = new ArrayList();
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                SimpleFunctionDescriptor simpleFunctionDescriptorLoadFunction = deserializedMemberScope.getC().getMemberDeserializer().loadFunction((ProtoBuf.Function) ((MessageLite) it.next()));
                if (!deserializedMemberScope.isDeclaredFunctionAvailable(simpleFunctionDescriptorLoadFunction)) {
                    simpleFunctionDescriptorLoadFunction = null;
                }
                SimpleFunctionDescriptor simpleFunctionDescriptor = simpleFunctionDescriptorLoadFunction;
                if (simpleFunctionDescriptor != null) {
                    arrayList.add(simpleFunctionDescriptor);
                }
            }
            return arrayList;
        }

        private final List<PropertyDescriptor> computeProperties() {
            List<ProtoBuf.Property> list = this.propertyList;
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            ArrayList arrayList = new ArrayList();
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                PropertyDescriptor propertyDescriptorLoadProperty = deserializedMemberScope.getC().getMemberDeserializer().loadProperty((ProtoBuf.Property) ((MessageLite) it.next()));
                if (propertyDescriptorLoadProperty != null) {
                    arrayList.add(propertyDescriptorLoadProperty);
                }
            }
            return arrayList;
        }

        private final List<TypeAliasDescriptor> computeTypeAliases() {
            List<ProtoBuf.TypeAlias> list = this.typeAliasList;
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            ArrayList arrayList = new ArrayList();
            Iterator<T> it = list.iterator();
            while (it.hasNext()) {
                TypeAliasDescriptor typeAliasDescriptorLoadTypeAlias = deserializedMemberScope.getC().getMemberDeserializer().loadTypeAlias((ProtoBuf.TypeAlias) ((MessageLite) it.next()));
                if (typeAliasDescriptorLoadTypeAlias != null) {
                    arrayList.add(typeAliasDescriptorLoadTypeAlias);
                }
            }
            return arrayList;
        }

        private final List<SimpleFunctionDescriptor> computeAllNonDeclaredFunctions() {
            Set<Name> nonDeclaredFunctionNames = this.this$0.getNonDeclaredFunctionNames();
            ArrayList arrayList = new ArrayList();
            Iterator<T> it = nonDeclaredFunctionNames.iterator();
            while (it.hasNext()) {
                CollectionsKt.addAll(arrayList, computeNonDeclaredFunctionsForName((Name) it.next()));
            }
            return arrayList;
        }

        private final List<PropertyDescriptor> computeAllNonDeclaredProperties() {
            Set<Name> nonDeclaredVariableNames = this.this$0.getNonDeclaredVariableNames();
            ArrayList arrayList = new ArrayList();
            Iterator<T> it = nonDeclaredVariableNames.iterator();
            while (it.hasNext()) {
                CollectionsKt.addAll(arrayList, computeNonDeclaredPropertiesForName((Name) it.next()));
            }
            return arrayList;
        }

        private final List<SimpleFunctionDescriptor> computeNonDeclaredFunctionsForName(Name name) {
            List<SimpleFunctionDescriptor> declaredFunctions = getDeclaredFunctions();
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            ArrayList arrayList = new ArrayList();
            for (Object obj : declaredFunctions) {
                if (Intrinsics.areEqual(((DeclarationDescriptor) obj).getName(), name)) {
                    arrayList.add(obj);
                }
            }
            ArrayList arrayList2 = arrayList;
            int size = arrayList2.size();
            deserializedMemberScope.computeNonDeclaredFunctions(name, arrayList2);
            return arrayList2.subList(size, arrayList2.size());
        }

        private final List<PropertyDescriptor> computeNonDeclaredPropertiesForName(Name name) {
            List<PropertyDescriptor> declaredProperties = getDeclaredProperties();
            DeserializedMemberScope deserializedMemberScope = this.this$0;
            ArrayList arrayList = new ArrayList();
            for (Object obj : declaredProperties) {
                if (Intrinsics.areEqual(((DeclarationDescriptor) obj).getName(), name)) {
                    arrayList.add(obj);
                }
            }
            ArrayList arrayList2 = arrayList;
            int size = arrayList2.size();
            deserializedMemberScope.computeNonDeclaredProperties(name, arrayList2);
            return arrayList2.subList(size, arrayList2.size());
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Collection<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation location) {
            Collection<SimpleFunctionDescriptor> collection;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(location, "location");
            return (getFunctionNames().contains(name) && (collection = getFunctionsByName().get(name)) != null) ? collection : CollectionsKt.emptyList();
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public TypeAliasDescriptor getTypeAliasByName(Name name) {
            Intrinsics.checkNotNullParameter(name, "name");
            return getTypeAliasesByName().get(name);
        }

        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation location) {
            Collection<PropertyDescriptor> collection;
            Intrinsics.checkNotNullParameter(name, "name");
            Intrinsics.checkNotNullParameter(location, "location");
            return (getVariableNames().contains(name) && (collection = getPropertiesByName().get(name)) != null) ? collection : CollectionsKt.emptyList();
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedMemberScope.Implementation
        public void addFunctionsAndPropertiesTo(Collection<DeclarationDescriptor> result, DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> nameFilter, LookupLocation location) {
            Intrinsics.checkNotNullParameter(result, "result");
            Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
            Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
            Intrinsics.checkNotNullParameter(location, "location");
            if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getVARIABLES_MASK())) {
                for (Object obj : getAllProperties()) {
                    Name name = ((PropertyDescriptor) obj).getName();
                    Intrinsics.checkNotNullExpressionValue(name, "getName(...)");
                    if (nameFilter.invoke(name).booleanValue()) {
                        result.add(obj);
                    }
                }
            }
            if (kindFilter.acceptsKinds(DescriptorKindFilter.Companion.getFUNCTIONS_MASK())) {
                for (Object obj2 : getAllFunctions()) {
                    Name name2 = ((SimpleFunctionDescriptor) obj2).getName();
                    Intrinsics.checkNotNullExpressionValue(name2, "getName(...)");
                    if (nameFilter.invoke(name2).booleanValue()) {
                        result.add(obj2);
                    }
                }
            }
        }
    }
}
