package kotlin.reflect.jvm.internal;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.as;
import java.util.Collection;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Triple;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KCallable;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.components.ReflectKotlinClass;
import kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectClassUtilKt;
import kotlin.reflect.jvm.internal.impl.incremental.components.NoLookupLocation;
import kotlin.reflect.jvm.internal.impl.load.kotlin.header.KotlinClassHeader;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.ProtoBufUtilKt;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.TypeTable;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.JvmProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmMetadataVersion;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmNameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmProtoBufUtil;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.protobuf.GeneratedMessageLite;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: KPackageImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000j\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0000\u0018\u00002\u00020\u0001:\u0001)B\u0013\u0012\n\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u001a0\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u0016\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00122\u0006\u0010\u001b\u001a\u00020\u001cH\u0016J\u0012\u0010\u001f\u001a\u0004\u0018\u00010\u001a2\u0006\u0010 \u001a\u00020!H\u0016J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010%H\u0096\u0002J\b\u0010&\u001a\u00020!H\u0016J\b\u0010'\u001a\u00020(H\u0016R\u0018\u0010\u0002\u001a\u0006\u0012\u0002\b\u00030\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0018\u0010\b\u001a\f\u0012\b\u0012\u00060\nR\u00020\u00000\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u0018\u0010\u000b\u001a\u0006\u0012\u0002\b\u00030\u00038TX\u0094\u0004¢\u0006\u0006\u001a\u0004\b\f\u0010\u0007R\u0014\u0010\r\u001a\u00020\u000e8BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u001e\u0010\u0011\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00130\u00128VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0015R\u001a\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u00128VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0015¨\u0006*"}, d2 = {"Lkotlin/reflect/jvm/internal/KPackageImpl;", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", "jClass", "Ljava/lang/Class;", "<init>", "(Ljava/lang/Class;)V", "getJClass", "()Ljava/lang/Class;", "data", "Lkotlin/Lazy;", "Lkotlin/reflect/jvm/internal/KPackageImpl$Data;", "methodOwner", "getMethodOwner", "scope", "Lkotlin/reflect/jvm/internal/impl/resolve/scopes/MemberScope;", "getScope", "()Lorg/jetbrains/kotlin/resolve/scopes/MemberScope;", "members", "", "Lkotlin/reflect/KCallable;", "getMembers", "()Ljava/util/Collection;", "constructorDescriptors", "Lkotlin/reflect/jvm/internal/impl/descriptors/ConstructorDescriptor;", "getConstructorDescriptors", "getProperties", "Lkotlin/reflect/jvm/internal/impl/descriptors/PropertyDescriptor;", as.a, "Lkotlin/reflect/jvm/internal/impl/name/Name;", "getFunctions", "Lkotlin/reflect/jvm/internal/impl/descriptors/FunctionDescriptor;", "getLocalProperty", "index", "", "equals", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "", "hashCode", "toString", "", "Data", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class KPackageImpl extends KDeclarationContainerImpl {
    private final Lazy<Data> data;
    private final Class<?> jClass;

    @Override // kotlin.jvm.internal.ClassBasedDeclarationContainer
    public Class<?> getJClass() {
        return this.jClass;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: compiled from: KPackageImpl.kt */
    @Metadata(d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u001e\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0082\u0004\u0018\u00002\u00060\u0001R\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004R\u001d\u0010\u0005\u001a\u0004\u0018\u00010\u00068BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR\u001b\u0010\u000b\u001a\u00020\f8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000f\u0010\n\u001a\u0004\b\r\u0010\u000eR!\u0010\u0010\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u00118FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0014\u0010\u0015\u001a\u0004\b\u0012\u0010\u0013R/\u0010\u0016\u001a\u0016\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u001a\u0018\u00010\u00178FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u001d\u0010\u0015\u001a\u0004\b\u001b\u0010\u001cR%\u0010\u001e\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030 0\u001f8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b#\u0010\n\u001a\u0004\b!\u0010\"¨\u0006$"}, d2 = {"Lkotlin/reflect/jvm/internal/KPackageImpl$Data;", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl$Data;", "Lkotlin/reflect/jvm/internal/KDeclarationContainerImpl;", "<init>", "(Lkotlin/reflect/jvm/internal/KPackageImpl;)V", "kotlinClass", "Lkotlin/reflect/jvm/internal/impl/descriptors/runtime/components/ReflectKotlinClass;", "getKotlinClass", "()Lorg/jetbrains/kotlin/descriptors/runtime/components/ReflectKotlinClass;", "kotlinClass$delegate", "Lkotlin/reflect/jvm/internal/ReflectProperties$LazySoftVal;", "scope", "Lkotlin/reflect/jvm/internal/impl/resolve/scopes/MemberScope;", "getScope", "()Lorg/jetbrains/kotlin/resolve/scopes/MemberScope;", "scope$delegate", "multifileFacade", "Ljava/lang/Class;", "getMultifileFacade", "()Ljava/lang/Class;", "multifileFacade$delegate", "Lkotlin/Lazy;", "metadata", "Lkotlin/Triple;", "Lkotlin/reflect/jvm/internal/impl/metadata/jvm/deserialization/JvmNameResolver;", "Lkotlin/reflect/jvm/internal/impl/metadata/ProtoBuf$Package;", "Lkotlin/reflect/jvm/internal/impl/metadata/jvm/deserialization/JvmMetadataVersion;", "getMetadata", "()Lkotlin/Triple;", "metadata$delegate", "members", "", "Lkotlin/reflect/jvm/internal/KCallableImpl;", "getMembers", "()Ljava/util/Collection;", "members$delegate", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
    final class Data extends KDeclarationContainerImpl.Data {
        static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(Data.class), "kotlinClass", "getKotlinClass()Lorg/jetbrains/kotlin/descriptors/runtime/components/ReflectKotlinClass;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(Data.class), "scope", "getScope()Lorg/jetbrains/kotlin/resolve/scopes/MemberScope;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(Data.class), "members", "getMembers()Ljava/util/Collection;"))};

        /* JADX INFO: renamed from: kotlinClass$delegate, reason: from kotlin metadata */
        private final ReflectProperties.LazySoftVal kotlinClass;

        /* JADX INFO: renamed from: members$delegate, reason: from kotlin metadata */
        private final ReflectProperties.LazySoftVal members;

        /* JADX INFO: renamed from: metadata$delegate, reason: from kotlin metadata */
        private final Lazy metadata;

        /* JADX INFO: renamed from: multifileFacade$delegate, reason: from kotlin metadata */
        private final Lazy multifileFacade;

        /* JADX INFO: renamed from: scope$delegate, reason: from kotlin metadata */
        private final ReflectProperties.LazySoftVal scope;

        public Data() {
            super();
            this.kotlinClass = ReflectProperties.lazySoft(new Function0(KPackageImpl.this) { // from class: kotlin.reflect.jvm.internal.KPackageImpl$Data$$Lambda$0
                private final KPackageImpl arg$0;

                {
                    this.arg$0 = kPackageImpl;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KPackageImpl.Data.kotlinClass_delegate$lambda$0(this.arg$0);
                }
            });
            this.scope = ReflectProperties.lazySoft(new Function0(this) { // from class: kotlin.reflect.jvm.internal.KPackageImpl$Data$$Lambda$1
                private final KPackageImpl.Data arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KPackageImpl.Data.scope_delegate$lambda$1(this.arg$0);
                }
            });
            this.multifileFacade = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this, KPackageImpl.this) { // from class: kotlin.reflect.jvm.internal.KPackageImpl$Data$$Lambda$2
                private final KPackageImpl.Data arg$0;
                private final KPackageImpl arg$1;

                {
                    this.arg$0 = this;
                    this.arg$1 = kPackageImpl;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KPackageImpl.Data.multifileFacade_delegate$lambda$2(this.arg$0, this.arg$1);
                }
            });
            this.metadata = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KPackageImpl$Data$$Lambda$3
                private final KPackageImpl.Data arg$0;

                {
                    this.arg$0 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KPackageImpl.Data.metadata_delegate$lambda$4(this.arg$0);
                }
            });
            this.members = ReflectProperties.lazySoft(new Function0(KPackageImpl.this, this) { // from class: kotlin.reflect.jvm.internal.KPackageImpl$Data$$Lambda$4
                private final KPackageImpl arg$0;
                private final KPackageImpl.Data arg$1;

                {
                    this.arg$0 = kPackageImpl;
                    this.arg$1 = this;
                }

                @Override // kotlin.jvm.functions.Function0
                public Object invoke() {
                    return KPackageImpl.Data.members_delegate$lambda$5(this.arg$0, this.arg$1);
                }
            });
        }

        /* JADX WARN: Multi-variable type inference failed */
        private final ReflectKotlinClass getKotlinClass() {
            return (ReflectKotlinClass) this.kotlinClass.getValue(this, $$delegatedProperties[0]);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final ReflectKotlinClass kotlinClass_delegate$lambda$0(KPackageImpl this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            return ReflectKotlinClass.Factory.create(this$0.getJClass());
        }

        public final MemberScope getScope() {
            T value = this.scope.getValue(this, $$delegatedProperties[1]);
            Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
            return (MemberScope) value;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final MemberScope scope_delegate$lambda$1(Data this$0) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            ReflectKotlinClass kotlinClass = this$0.getKotlinClass();
            if (kotlinClass != null) {
                return this$0.getModuleData().getPackagePartScopeCache().getPackagePartScope(kotlinClass);
            }
            return MemberScope.Empty.INSTANCE;
        }

        public final Class<?> getMultifileFacade() {
            return (Class) this.multifileFacade.getValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Class multifileFacade_delegate$lambda$2(Data this$0, KPackageImpl this$1) {
            KotlinClassHeader classHeader;
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            ReflectKotlinClass kotlinClass = this$0.getKotlinClass();
            String multifileClassName = (kotlinClass == null || (classHeader = kotlinClass.getClassHeader()) == null) ? null : classHeader.getMultifileClassName();
            if (multifileClassName == null || multifileClassName.length() <= 0) {
                return null;
            }
            return this$1.getJClass().getClassLoader().loadClass(StringsKt.replace$default(multifileClassName, '/', '.', false, 4, (Object) null));
        }

        public final Triple<JvmNameResolver, ProtoBuf.Package, JvmMetadataVersion> getMetadata() {
            return (Triple) this.metadata.getValue();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Triple metadata_delegate$lambda$4(Data this$0) {
            KotlinClassHeader classHeader;
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            ReflectKotlinClass kotlinClass = this$0.getKotlinClass();
            if (kotlinClass == null || (classHeader = kotlinClass.getClassHeader()) == null) {
                return null;
            }
            String[] data = classHeader.getData();
            String[] strings = classHeader.getStrings();
            if (data == null || strings == null) {
                return null;
            }
            Pair<JvmNameResolver, ProtoBuf.Package> packageDataFrom = JvmProtoBufUtil.readPackageDataFrom(data, strings);
            return new Triple(packageDataFrom.component1(), packageDataFrom.component2(), classHeader.getMetadataVersion());
        }

        public final Collection<KCallableImpl<?>> getMembers() {
            T value = this.members.getValue(this, $$delegatedProperties[2]);
            Intrinsics.checkNotNullExpressionValue(value, "getValue(...)");
            return (Collection) value;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static final Collection members_delegate$lambda$5(KPackageImpl this$0, Data this$1) {
            Intrinsics.checkNotNullParameter(this$0, "this$0");
            Intrinsics.checkNotNullParameter(this$1, "this$1");
            return this$0.getMembers(this$1.getScope(), KDeclarationContainerImpl.MemberBelonginess.DECLARED);
        }
    }

    public KPackageImpl(Class<?> jClass) {
        Intrinsics.checkNotNullParameter(jClass, "jClass");
        this.jClass = jClass;
        this.data = LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0(this) { // from class: kotlin.reflect.jvm.internal.KPackageImpl$$Lambda$0
            private final KPackageImpl arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return KPackageImpl.data$lambda$0(this.arg$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Data data$lambda$0(KPackageImpl this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return this$0.new Data();
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    protected Class<?> getMethodOwner() {
        Class<?> multifileFacade = this.data.getValue().getMultifileFacade();
        return multifileFacade == null ? getJClass() : multifileFacade;
    }

    private final MemberScope getScope() {
        return this.data.getValue().getScope();
    }

    @Override // kotlin.reflect.KDeclarationContainer
    public Collection<KCallable<?>> getMembers() {
        return this.data.getValue().getMembers();
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public Collection<ConstructorDescriptor> getConstructorDescriptors() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public Collection<PropertyDescriptor> getProperties(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return getScope().getContributedVariables(name, NoLookupLocation.FROM_REFLECTION);
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public Collection<FunctionDescriptor> getFunctions(Name name) {
        Intrinsics.checkNotNullParameter(name, "name");
        return getScope().getContributedFunctions(name, NoLookupLocation.FROM_REFLECTION);
    }

    @Override // kotlin.reflect.jvm.internal.KDeclarationContainerImpl
    public PropertyDescriptor getLocalProperty(int index) {
        Triple<JvmNameResolver, ProtoBuf.Package, JvmMetadataVersion> metadata = this.data.getValue().getMetadata();
        if (metadata == null) {
            return null;
        }
        JvmNameResolver jvmNameResolverComponent1 = metadata.component1();
        ProtoBuf.Package packageComponent2 = metadata.component2();
        JvmMetadataVersion jvmMetadataVersionComponent3 = metadata.component3();
        GeneratedMessageLite.GeneratedExtension<ProtoBuf.Package, List<ProtoBuf.Property>> packageLocalVariable = JvmProtoBuf.packageLocalVariable;
        Intrinsics.checkNotNullExpressionValue(packageLocalVariable, "packageLocalVariable");
        ProtoBuf.Property property = (ProtoBuf.Property) ProtoBufUtilKt.getExtensionOrNull(packageComponent2, packageLocalVariable, index);
        if (property == null) {
            return null;
        }
        ProtoBuf.TypeTable typeTable = packageComponent2.getTypeTable();
        Intrinsics.checkNotNullExpressionValue(typeTable, "getTypeTable(...)");
        return (PropertyDescriptor) UtilKt.deserializeToDescriptor(getJClass(), property, jvmNameResolverComponent1, new TypeTable(typeTable), jvmMetadataVersionComponent3, KPackageImpl$getLocalProperty$1$1$1.INSTANCE);
    }

    public boolean equals(Object other) {
        return (other instanceof KPackageImpl) && Intrinsics.areEqual(getJClass(), ((KPackageImpl) other).getJClass());
    }

    public int hashCode() {
        return getJClass().hashCode();
    }

    public String toString() {
        return "file class " + ReflectClassUtilKt.getClassId(getJClass()).asSingleFqName();
    }
}
