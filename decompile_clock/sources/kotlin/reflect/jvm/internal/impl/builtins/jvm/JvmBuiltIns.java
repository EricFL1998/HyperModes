package kotlin.reflect.jvm.internal.impl.builtins.jvm;

import java.util.List;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.ModuleDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.AdditionalClassPartsProvider;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.ClassDescriptorFactory;
import kotlin.reflect.jvm.internal.impl.descriptors.deserialization.PlatformDependentDeclarationFilter;
import kotlin.reflect.jvm.internal.impl.descriptors.impl.ModuleDescriptorImpl;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageKt;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;

/* JADX INFO: compiled from: JvmBuiltIns.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JvmBuiltIns extends KotlinBuiltIns {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(JvmBuiltIns.class), "customizer", "getCustomizer()Lorg/jetbrains/kotlin/builtins/jvm/JvmBuiltInsCustomizer;"))};
    private final NotNullLazyValue customizer$delegate;
    private final Kind kind;
    private Function0<Settings> settingsComputation;

    /* JADX INFO: compiled from: JvmBuiltIns.kt */
    public enum Kind {
        FROM_DEPENDENCIES,
        FROM_CLASS_LOADER,
        FALLBACK;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    }

    /* JADX INFO: compiled from: JvmBuiltIns.kt */
    public /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[Kind.values().length];
            try {
                iArr[Kind.FROM_DEPENDENCIES.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[Kind.FROM_CLASS_LOADER.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[Kind.FALLBACK.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public JvmBuiltIns(final StorageManager storageManager, Kind kind) {
        super(storageManager);
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(kind, "kind");
        this.kind = kind;
        this.customizer$delegate = storageManager.createLazyValue(new Function0(this, storageManager) { // from class: kotlin.reflect.jvm.internal.impl.builtins.jvm.JvmBuiltIns$$Lambda$0
            private final JvmBuiltIns arg$0;
            private final StorageManager arg$1;

            {
                this.arg$0 = this;
                this.arg$1 = storageManager;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return JvmBuiltIns.customizer_delegate$lambda$5(this.arg$0, this.arg$1);
            }
        });
        int i = WhenMappings.$EnumSwitchMapping$0[kind.ordinal()];
        if (i != 1) {
            if (i == 2) {
                createBuiltInsModule(false);
            } else {
                if (i != 3) {
                    throw new NoWhenBranchMatchedException();
                }
                createBuiltInsModule(true);
            }
        }
    }

    /* JADX INFO: compiled from: JvmBuiltIns.kt */
    public static final class Settings {
        private final boolean isAdditionalBuiltInsFeatureSupported;
        private final ModuleDescriptor ownerModuleDescriptor;

        public Settings(ModuleDescriptor ownerModuleDescriptor, boolean z) {
            Intrinsics.checkNotNullParameter(ownerModuleDescriptor, "ownerModuleDescriptor");
            this.ownerModuleDescriptor = ownerModuleDescriptor;
            this.isAdditionalBuiltInsFeatureSupported = z;
        }

        public final ModuleDescriptor getOwnerModuleDescriptor() {
            return this.ownerModuleDescriptor;
        }

        public final boolean isAdditionalBuiltInsFeatureSupported() {
            return this.isAdditionalBuiltInsFeatureSupported;
        }
    }

    public final void setPostponedSettingsComputation(Function0<Settings> computation) {
        Intrinsics.checkNotNullParameter(computation, "computation");
        this.settingsComputation = computation;
    }

    public final void initialize(final ModuleDescriptor moduleDescriptor, final boolean z) {
        Intrinsics.checkNotNullParameter(moduleDescriptor, "moduleDescriptor");
        setPostponedSettingsComputation(new Function0(moduleDescriptor, z) { // from class: kotlin.reflect.jvm.internal.impl.builtins.jvm.JvmBuiltIns$$Lambda$1
            private final ModuleDescriptor arg$0;
            private final boolean arg$1;

            {
                this.arg$0 = moduleDescriptor;
                this.arg$1 = z;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return JvmBuiltIns.initialize$lambda$1(this.arg$0, this.arg$1);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Settings initialize$lambda$1(ModuleDescriptor moduleDescriptor, boolean z) {
        Intrinsics.checkNotNullParameter(moduleDescriptor, "$moduleDescriptor");
        return new Settings(moduleDescriptor, z);
    }

    public final JvmBuiltInsCustomizer getCustomizer() {
        return (JvmBuiltInsCustomizer) StorageKt.getValue(this.customizer$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final JvmBuiltInsCustomizer customizer_delegate$lambda$5(final JvmBuiltIns this$0, StorageManager storageManager) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Intrinsics.checkNotNullParameter(storageManager, "$storageManager");
        ModuleDescriptorImpl builtInsModule = this$0.getBuiltInsModule();
        Intrinsics.checkNotNullExpressionValue(builtInsModule, "getBuiltInsModule(...)");
        return new JvmBuiltInsCustomizer(builtInsModule, storageManager, new Function0(this$0) { // from class: kotlin.reflect.jvm.internal.impl.builtins.jvm.JvmBuiltIns$$Lambda$2
            private final JvmBuiltIns arg$0;

            {
                this.arg$0 = this$0;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return JvmBuiltIns.customizer_delegate$lambda$5$lambda$4(this.arg$0);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Settings customizer_delegate$lambda$5$lambda$4(JvmBuiltIns this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        Function0<Settings> function0 = this$0.settingsComputation;
        if (function0 != null) {
            Settings settingsInvoke = function0.invoke();
            this$0.settingsComputation = null;
            return settingsInvoke;
        }
        throw new AssertionError("JvmBuiltins instance has not been initialized properly");
    }

    @Override // kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
    protected PlatformDependentDeclarationFilter getPlatformDependentDeclarationFilter() {
        return getCustomizer();
    }

    @Override // kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
    protected AdditionalClassPartsProvider getAdditionalClassPartsProvider() {
        return getCustomizer();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns
    public List<ClassDescriptorFactory> getClassDescriptorFactories() {
        Iterable<ClassDescriptorFactory> classDescriptorFactories = super.getClassDescriptorFactories();
        Intrinsics.checkNotNullExpressionValue(classDescriptorFactories, "getClassDescriptorFactories(...)");
        StorageManager storageManager = getStorageManager();
        Intrinsics.checkNotNullExpressionValue(storageManager, "getStorageManager(...)");
        ModuleDescriptorImpl builtInsModule = getBuiltInsModule();
        Intrinsics.checkNotNullExpressionValue(builtInsModule, "getBuiltInsModule(...)");
        return CollectionsKt.plus(classDescriptorFactories, new JvmBuiltInClassDescriptorFactory(storageManager, builtInsModule, null, 4, null));
    }
}
