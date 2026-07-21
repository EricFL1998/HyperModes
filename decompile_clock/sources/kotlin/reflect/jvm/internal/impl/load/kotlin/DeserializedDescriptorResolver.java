package kotlin.reflect.jvm.internal.impl.load.kotlin;

import java.util.Collection;
import java.util.Set;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PackageFragmentDescriptor;
import kotlin.reflect.jvm.internal.impl.load.kotlin.header.KotlinClassHeader;
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmMetadataVersion;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmNameResolver;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization.JvmProtoBufUtil;
import kotlin.reflect.jvm.internal.impl.protobuf.InvalidProtocolBufferException;
import kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ClassData;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationComponents;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.IncompatibleVersionErrorData;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedContainerAbiStability;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.descriptors.DeserializedPackageMemberScope;
import kotlin.reflect.jvm.internal.impl.utils.DeserializationHelpersKt;

/* JADX INFO: compiled from: DeserializedDescriptorResolver.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DeserializedDescriptorResolver {
    public DeserializationComponents components;
    public static final Companion Companion = new Companion(null);
    private static final Set<KotlinClassHeader.Kind> KOTLIN_CLASS = SetsKt.setOf(KotlinClassHeader.Kind.CLASS);
    private static final Set<KotlinClassHeader.Kind> KOTLIN_FILE_FACADE_OR_MULTIFILE_CLASS_PART = SetsKt.setOf((Object[]) new KotlinClassHeader.Kind[]{KotlinClassHeader.Kind.FILE_FACADE, KotlinClassHeader.Kind.MULTIFILE_CLASS_PART});
    private static final JvmMetadataVersion KOTLIN_1_1_EAP_METADATA_VERSION = new JvmMetadataVersion(1, 1, 2);
    private static final JvmMetadataVersion KOTLIN_1_3_M1_METADATA_VERSION = new JvmMetadataVersion(1, 1, 11);
    private static final JvmMetadataVersion KOTLIN_1_3_RC_METADATA_VERSION = new JvmMetadataVersion(1, 1, 13);

    public final DeserializationComponents getComponents() {
        DeserializationComponents deserializationComponents = this.components;
        if (deserializationComponents != null) {
            return deserializationComponents;
        }
        Intrinsics.throwUninitializedPropertyAccessException("components");
        return null;
    }

    public final void setComponents(DeserializationComponents deserializationComponents) {
        Intrinsics.checkNotNullParameter(deserializationComponents, "<set-?>");
        this.components = deserializationComponents;
    }

    private final JvmMetadataVersion getOwnMetadataVersion() {
        return DeserializationHelpersKt.jvmMetadataVersionOrDefault(getComponents().getConfiguration());
    }

    public final void setComponents(DeserializationComponentsForJava components) {
        Intrinsics.checkNotNullParameter(components, "components");
        setComponents(components.getComponents());
    }

    private final boolean getSkipMetadataVersionCheck() {
        return getComponents().getConfiguration().getSkipMetadataVersionCheck();
    }

    public final ClassDescriptor resolveClass(KotlinJvmBinaryClass kotlinClass) {
        Intrinsics.checkNotNullParameter(kotlinClass, "kotlinClass");
        ClassData classData$descriptors_jvm = readClassData$descriptors_jvm(kotlinClass);
        if (classData$descriptors_jvm == null) {
            return null;
        }
        return getComponents().getClassDeserializer().deserializeClass(kotlinClass.getClassId(), classData$descriptors_jvm);
    }

    public final ClassData readClassData$descriptors_jvm(KotlinJvmBinaryClass kotlinClass) {
        Pair<JvmNameResolver, ProtoBuf.Class> classDataFrom;
        Intrinsics.checkNotNullParameter(kotlinClass, "kotlinClass");
        String[] data = readData(kotlinClass, KOTLIN_CLASS);
        if (data == null) {
            return null;
        }
        String[] strings = kotlinClass.getClassHeader().getStrings();
        try {
            if (strings == null) {
                return null;
            }
            try {
                classDataFrom = JvmProtoBufUtil.readClassDataFrom(data, strings);
            } catch (InvalidProtocolBufferException e) {
                throw new IllegalStateException("Could not read data from " + kotlinClass.getLocation(), e);
            }
        } catch (Throwable th) {
            if (getSkipMetadataVersionCheck() || kotlinClass.getClassHeader().getMetadataVersion().isCompatible(getOwnMetadataVersion())) {
                throw th;
            }
            classDataFrom = null;
        }
        if (classDataFrom == null) {
            return null;
        }
        return new ClassData(classDataFrom.component1(), classDataFrom.component2(), kotlinClass.getClassHeader().getMetadataVersion(), new KotlinJvmBinarySourceElement(kotlinClass, getIncompatibility(kotlinClass), isPreReleaseInvisible(kotlinClass), getAbiStability(kotlinClass)));
    }

    public final MemberScope createKotlinPackagePartScope(PackageFragmentDescriptor descriptor, KotlinJvmBinaryClass kotlinClass) {
        String[] strings;
        Pair<JvmNameResolver, ProtoBuf.Package> packageDataFrom;
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        Intrinsics.checkNotNullParameter(kotlinClass, "kotlinClass");
        String[] data = readData(kotlinClass, KOTLIN_FILE_FACADE_OR_MULTIFILE_CLASS_PART);
        if (data == null || (strings = kotlinClass.getClassHeader().getStrings()) == null) {
            return null;
        }
        try {
            try {
                packageDataFrom = JvmProtoBufUtil.readPackageDataFrom(data, strings);
            } catch (InvalidProtocolBufferException e) {
                throw new IllegalStateException("Could not read data from " + kotlinClass.getLocation(), e);
            }
        } catch (Throwable th) {
            if (getSkipMetadataVersionCheck() || kotlinClass.getClassHeader().getMetadataVersion().isCompatible(getOwnMetadataVersion())) {
                throw th;
            }
            packageDataFrom = null;
        }
        if (packageDataFrom == null) {
            return null;
        }
        JvmNameResolver jvmNameResolverComponent1 = packageDataFrom.component1();
        ProtoBuf.Package packageComponent2 = packageDataFrom.component2();
        JvmNameResolver jvmNameResolver = jvmNameResolverComponent1;
        JvmPackagePartSource jvmPackagePartSource = new JvmPackagePartSource(kotlinClass, packageComponent2, jvmNameResolver, getIncompatibility(kotlinClass), isPreReleaseInvisible(kotlinClass), getAbiStability(kotlinClass));
        return new DeserializedPackageMemberScope(descriptor, packageComponent2, jvmNameResolver, kotlinClass.getClassHeader().getMetadataVersion(), jvmPackagePartSource, getComponents(), "scope for " + jvmPackagePartSource + " in " + descriptor, new Function0() { // from class: kotlin.reflect.jvm.internal.impl.load.kotlin.DeserializedDescriptorResolver$$Lambda$0
            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return DeserializedDescriptorResolver.createKotlinPackagePartScope$lambda$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Collection createKotlinPackagePartScope$lambda$2() {
        return CollectionsKt.emptyList();
    }

    private final IncompatibleVersionErrorData<JvmMetadataVersion> getIncompatibility(KotlinJvmBinaryClass kotlinJvmBinaryClass) {
        if (getSkipMetadataVersionCheck() || kotlinJvmBinaryClass.getClassHeader().getMetadataVersion().isCompatible(getOwnMetadataVersion())) {
            return null;
        }
        return new IncompatibleVersionErrorData<>(kotlinJvmBinaryClass.getClassHeader().getMetadataVersion(), JvmMetadataVersion.INSTANCE, getOwnMetadataVersion(), getOwnMetadataVersion().lastSupportedVersionWithThisLanguageVersion(kotlinJvmBinaryClass.getClassHeader().getMetadataVersion().isStrictSemantics()), kotlinJvmBinaryClass.getLocation(), kotlinJvmBinaryClass.getClassId());
    }

    private final boolean isPreReleaseInvisible(KotlinJvmBinaryClass kotlinJvmBinaryClass) {
        return (getComponents().getConfiguration().getReportErrorsOnPreReleaseDependencies() && (kotlinJvmBinaryClass.getClassHeader().isPreRelease() || Intrinsics.areEqual(kotlinJvmBinaryClass.getClassHeader().getMetadataVersion(), KOTLIN_1_1_EAP_METADATA_VERSION))) || isCompiledWith13M1(kotlinJvmBinaryClass);
    }

    private final boolean isCompiledWith13M1(KotlinJvmBinaryClass kotlinJvmBinaryClass) {
        return !getComponents().getConfiguration().getSkipPrereleaseCheck() && kotlinJvmBinaryClass.getClassHeader().isPreRelease() && Intrinsics.areEqual(kotlinJvmBinaryClass.getClassHeader().getMetadataVersion(), KOTLIN_1_3_M1_METADATA_VERSION);
    }

    private final DeserializedContainerAbiStability getAbiStability(KotlinJvmBinaryClass kotlinJvmBinaryClass) {
        if (getComponents().getConfiguration().getAllowUnstableDependencies()) {
            return DeserializedContainerAbiStability.STABLE;
        }
        return kotlinJvmBinaryClass.getClassHeader().isUnstableJvmIrBinary() ? DeserializedContainerAbiStability.UNSTABLE : DeserializedContainerAbiStability.STABLE;
    }

    private final String[] readData(KotlinJvmBinaryClass kotlinJvmBinaryClass, Set<? extends KotlinClassHeader.Kind> set) {
        KotlinClassHeader classHeader = kotlinJvmBinaryClass.getClassHeader();
        String[] data = classHeader.getData();
        if (data == null) {
            data = classHeader.getIncompatibleData();
        }
        if (data == null || !set.contains(classHeader.getKind())) {
            return null;
        }
        return data;
    }

    /* JADX INFO: compiled from: DeserializedDescriptorResolver.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final JvmMetadataVersion getKOTLIN_1_3_RC_METADATA_VERSION$descriptors_jvm() {
            return DeserializedDescriptorResolver.KOTLIN_1_3_RC_METADATA_VERSION;
        }
    }
}
