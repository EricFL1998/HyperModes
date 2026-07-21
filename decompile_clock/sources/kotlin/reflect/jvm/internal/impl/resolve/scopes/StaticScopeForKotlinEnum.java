package kotlin.reflect.jvm.internal.impl.resolve.scopes;

import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassKind;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.DescriptorFactory;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageKt;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.utils.SmartList;

/* JADX INFO: compiled from: StaticScopeForKotlinEnum.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class StaticScopeForKotlinEnum extends MemberScopeImpl {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(StaticScopeForKotlinEnum.class), "functions", "getFunctions()Ljava/util/List;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(StaticScopeForKotlinEnum.class), "properties", "getProperties()Ljava/util/List;"))};
    private final ClassDescriptor containingClass;
    private final boolean enumEntriesCanBeUsed;
    private final NotNullLazyValue functions$delegate;
    private final NotNullLazyValue properties$delegate;

    public Void getContributedClassifier(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        return null;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    /* JADX INFO: renamed from: getContributedClassifier, reason: collision with other method in class */
    public /* bridge */ /* synthetic */ ClassifierDescriptor mo1493getContributedClassifier(Name name, LookupLocation lookupLocation) {
        return (ClassifierDescriptor) getContributedClassifier(name, lookupLocation);
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public /* bridge */ /* synthetic */ Collection getContributedDescriptors(DescriptorKindFilter descriptorKindFilter, Function1 function1) {
        return getContributedDescriptors(descriptorKindFilter, (Function1<? super Name, Boolean>) function1);
    }

    public StaticScopeForKotlinEnum(StorageManager storageManager, ClassDescriptor containingClass, boolean z) {
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(containingClass, "containingClass");
        this.containingClass = containingClass;
        this.enumEntriesCanBeUsed = z;
        containingClass.getKind();
        ClassKind classKind = ClassKind.ENUM_CLASS;
        this.functions$delegate = storageManager.createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.resolve.scopes.StaticScopeForKotlinEnum$$Lambda$0
            private final StaticScopeForKotlinEnum arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return StaticScopeForKotlinEnum.functions_delegate$lambda$1(this.arg$0);
            }
        });
        this.properties$delegate = storageManager.createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.resolve.scopes.StaticScopeForKotlinEnum$$Lambda$1
            private final StaticScopeForKotlinEnum arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return StaticScopeForKotlinEnum.properties_delegate$lambda$2(this.arg$0);
            }
        });
    }

    private final List<SimpleFunctionDescriptor> getFunctions() {
        return (List) StorageKt.getValue(this.functions$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List functions_delegate$lambda$1(StaticScopeForKotlinEnum this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        return CollectionsKt.listOf((Object[]) new SimpleFunctionDescriptor[]{DescriptorFactory.createEnumValueOfMethod(this$0.containingClass), DescriptorFactory.createEnumValuesMethod(this$0.containingClass)});
    }

    private final List<PropertyDescriptor> getProperties() {
        return (List) StorageKt.getValue(this.properties$delegate, this, (KProperty<?>) $$delegatedProperties[1]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List properties_delegate$lambda$2(StaticScopeForKotlinEnum this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        if (this$0.enumEntriesCanBeUsed) {
            return CollectionsKt.listOfNotNull(DescriptorFactory.createEnumEntriesProperty(this$0.containingClass));
        }
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public List<CallableMemberDescriptor> getContributedDescriptors(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        return CollectionsKt.plus((Collection) getFunctions(), (Iterable) getProperties());
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public SmartList<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        List<SimpleFunctionDescriptor> functions = getFunctions();
        SmartList smartList = new SmartList();
        for (Object obj : functions) {
            if (Intrinsics.areEqual(((SimpleFunctionDescriptor) obj).getName(), name)) {
                smartList.add(obj);
            }
        }
        return smartList;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation location) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        List<PropertyDescriptor> properties = getProperties();
        SmartList smartList = new SmartList();
        for (Object obj : properties) {
            if (Intrinsics.areEqual(((PropertyDescriptor) obj).getName(), name)) {
                smartList.add(obj);
            }
        }
        return smartList;
    }
}
