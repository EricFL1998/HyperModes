package kotlin.reflect.jvm.internal.impl.builtins.jvm;

import java.io.InputStream;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.functions.BuiltInFictitiousFunctionClassFactory;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.NotFoundClasses;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.AdditionalClassPartsProvider;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.ClassDescriptorFactory;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter;
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupTracker;
import kotlin.reflect.jvm.internal.impl.load.kotlin.KotlinClassFinder;
import kotlin.reflect.jvm.internal.impl.name.FqName;
import kotlin.reflect.jvm.internal.impl.resolve.sam.SamConversionResolver;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.AbstractDeserializedPackageFragmentProvider;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.AnnotationAndConstantLoaderImpl;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ContractDeserializer;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationComponents;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializationConfiguration;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializedClassDataFinder;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.DeserializedPackageFragment;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.ErrorReporter;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.FlexibleTypeDeserializer;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.JvmEnumEntriesDeserializationSupport;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.LocalClassifierTypeSettings;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.BuiltInSerializerProtocol;
import kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.BuiltInsPackageFragmentImpl;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.types.checker.NewKotlinTypeChecker;

/* JADX INFO: compiled from: JvmBuiltInsPackageFragmentProvider.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JvmBuiltInsPackageFragmentProvider extends AbstractDeserializedPackageFragmentProvider {
    public static final Companion Companion = new Companion(null);

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public JvmBuiltInsPackageFragmentProvider(StorageManager storageManager, KotlinClassFinder finder, ModuleDescriptor moduleDescriptor, NotFoundClasses notFoundClasses, AdditionalClassPartsProvider additionalClassPartsProvider, PlatformDependentDeclarationFilter platformDependentDeclarationFilter, DeserializationConfiguration deserializationConfiguration, NewKotlinTypeChecker kotlinTypeChecker, SamConversionResolver samConversionResolver) {
        super(storageManager, finder, moduleDescriptor);
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(finder, "finder");
        Intrinsics.checkNotNullParameter(moduleDescriptor, "moduleDescriptor");
        Intrinsics.checkNotNullParameter(notFoundClasses, "notFoundClasses");
        Intrinsics.checkNotNullParameter(additionalClassPartsProvider, "additionalClassPartsProvider");
        Intrinsics.checkNotNullParameter(platformDependentDeclarationFilter, "platformDependentDeclarationFilter");
        Intrinsics.checkNotNullParameter(deserializationConfiguration, "deserializationConfiguration");
        Intrinsics.checkNotNullParameter(kotlinTypeChecker, "kotlinTypeChecker");
        Intrinsics.checkNotNullParameter(samConversionResolver, "samConversionResolver");
        JvmBuiltInsPackageFragmentProvider jvmBuiltInsPackageFragmentProvider = this;
        DeserializedClassDataFinder deserializedClassDataFinder = new DeserializedClassDataFinder(jvmBuiltInsPackageFragmentProvider);
        AnnotationAndConstantLoaderImpl annotationAndConstantLoaderImpl = new AnnotationAndConstantLoaderImpl(moduleDescriptor, notFoundClasses, BuiltInSerializerProtocol.INSTANCE);
        LocalClassifierTypeSettings.Default r8 = LocalClassifierTypeSettings.Default.INSTANCE;
        ErrorReporter DO_NOTHING = ErrorReporter.DO_NOTHING;
        Intrinsics.checkNotNullExpressionValue(DO_NOTHING, "DO_NOTHING");
        setComponents(new DeserializationComponents(storageManager, moduleDescriptor, deserializationConfiguration, deserializedClassDataFinder, annotationAndConstantLoaderImpl, jvmBuiltInsPackageFragmentProvider, r8, DO_NOTHING, LookupTracker.DO_NOTHING.INSTANCE, FlexibleTypeDeserializer.ThrowException.INSTANCE, CollectionsKt.listOf((Object[]) new ClassDescriptorFactory[]{new BuiltInFictitiousFunctionClassFactory(storageManager, moduleDescriptor), new JvmBuiltInClassDescriptorFactory(storageManager, moduleDescriptor, null, 4, null)}), notFoundClasses, ContractDeserializer.Companion.getDEFAULT(), additionalClassPartsProvider, platformDependentDeclarationFilter, BuiltInSerializerProtocol.INSTANCE.getExtensionRegistry(), kotlinTypeChecker, samConversionResolver, null, JvmEnumEntriesDeserializationSupport.INSTANCE, 262144, null));
    }

    @Override // kotlin.reflect.jvm.internal.impl.serialization.deserialization.AbstractDeserializedPackageFragmentProvider
    protected DeserializedPackageFragment findPackage(FqName fqName) {
        Intrinsics.checkNotNullParameter(fqName, "fqName");
        InputStream inputStreamFindBuiltInsData = getFinder().findBuiltInsData(fqName);
        return inputStreamFindBuiltInsData != null ? BuiltInsPackageFragmentImpl.Companion.create(fqName, getStorageManager(), getModuleDescriptor(), inputStreamFindBuiltInsData, false) : null;
    }

    /* JADX INFO: compiled from: JvmBuiltInsPackageFragmentProvider.kt */
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }
    }
}
