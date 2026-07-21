package kotlin.reflect.jvm.internal.impl.resolve.scopes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableMemberDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.FunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.SimpleFunctionDescriptor;
import kotlin.reflect.jvm.internal.impl.incremental.components.LookupLocation;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.NonReportingOverrideStrategy;
import kotlin.reflect.jvm.internal.impl.resolve.OverridingUtil;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageKt;
import kotlin.reflect.jvm.internal.impl.storage.StorageManager;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.utils.SmartList;

/* JADX INFO: compiled from: GivenFunctionsMemberScope.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class GivenFunctionsMemberScope extends MemberScopeImpl {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(GivenFunctionsMemberScope.class), "allDescriptors", "getAllDescriptors()Ljava/util/List;"))};
    private final NotNullLazyValue allDescriptors$delegate;
    private final ClassDescriptor containingClass;

    protected abstract List<FunctionDescriptor> computeDeclaredFunctions();

    protected final ClassDescriptor getContainingClass() {
        return this.containingClass;
    }

    public GivenFunctionsMemberScope(StorageManager storageManager, ClassDescriptor containingClass) {
        Intrinsics.checkNotNullParameter(storageManager, "storageManager");
        Intrinsics.checkNotNullParameter(containingClass, "containingClass");
        this.containingClass = containingClass;
        this.allDescriptors$delegate = storageManager.createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.resolve.scopes.GivenFunctionsMemberScope$$Lambda$0
            private final GivenFunctionsMemberScope arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return GivenFunctionsMemberScope.allDescriptors_delegate$lambda$0(this.arg$0);
            }
        });
    }

    private final List<DeclarationDescriptor> getAllDescriptors() {
        return (List) StorageKt.getValue(this.allDescriptors$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final List allDescriptors_delegate$lambda$0(GivenFunctionsMemberScope this$0) {
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        List<FunctionDescriptor> listComputeDeclaredFunctions = this$0.computeDeclaredFunctions();
        return CollectionsKt.plus((Collection) listComputeDeclaredFunctions, (Iterable) this$0.createFakeOverrides(listComputeDeclaredFunctions));
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public Collection<DeclarationDescriptor> getContributedDescriptors(DescriptorKindFilter kindFilter, Function1<? super Name, Boolean> nameFilter) {
        Intrinsics.checkNotNullParameter(kindFilter, "kindFilter");
        Intrinsics.checkNotNullParameter(nameFilter, "nameFilter");
        return !kindFilter.acceptsKinds(DescriptorKindFilter.CALLABLES.getKindMask()) ? CollectionsKt.emptyList() : getAllDescriptors();
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope, kotlin.reflect.jvm.internal.impl.resolve.scopes.ResolutionScope
    public Collection<SimpleFunctionDescriptor> getContributedFunctions(Name name, LookupLocation location) {
        SmartList smartListEmptyList;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        List<DeclarationDescriptor> allDescriptors = getAllDescriptors();
        if (allDescriptors.isEmpty()) {
            smartListEmptyList = CollectionsKt.emptyList();
        } else {
            SmartList smartList = new SmartList();
            for (Object obj : allDescriptors) {
                if ((obj instanceof SimpleFunctionDescriptor) && Intrinsics.areEqual(((SimpleFunctionDescriptor) obj).getName(), name)) {
                    smartList.add(obj);
                }
            }
            smartListEmptyList = smartList;
        }
        return smartListEmptyList;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScopeImpl, kotlin.reflect.jvm.internal.impl.resolve.scopes.MemberScope
    public Collection<PropertyDescriptor> getContributedVariables(Name name, LookupLocation location) {
        SmartList smartListEmptyList;
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(location, "location");
        List<DeclarationDescriptor> allDescriptors = getAllDescriptors();
        if (allDescriptors.isEmpty()) {
            smartListEmptyList = CollectionsKt.emptyList();
        } else {
            SmartList smartList = new SmartList();
            for (Object obj : allDescriptors) {
                if ((obj instanceof PropertyDescriptor) && Intrinsics.areEqual(((PropertyDescriptor) obj).getName(), name)) {
                    smartList.add(obj);
                }
            }
            smartListEmptyList = smartList;
        }
        return smartListEmptyList;
    }

    private final List<DeclarationDescriptor> createFakeOverrides(List<? extends FunctionDescriptor> list) {
        ArrayList arrayListEmptyList;
        final ArrayList arrayList = new ArrayList(3);
        Collection<KotlinType> collectionMo1492getSupertypes = this.containingClass.getTypeConstructor().mo1492getSupertypes();
        Intrinsics.checkNotNullExpressionValue(collectionMo1492getSupertypes, "getSupertypes(...)");
        ArrayList arrayList2 = new ArrayList();
        Iterator<T> it = collectionMo1492getSupertypes.iterator();
        while (it.hasNext()) {
            CollectionsKt.addAll(arrayList2, ResolutionScope.DefaultImpls.getContributedDescriptors$default(((KotlinType) it.next()).getMemberScope(), null, null, 3, null));
        }
        ArrayList arrayList3 = new ArrayList();
        for (Object obj : arrayList2) {
            if (obj instanceof CallableMemberDescriptor) {
                arrayList3.add(obj);
            }
        }
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        for (Object obj2 : arrayList3) {
            Name name = ((CallableMemberDescriptor) obj2).getName();
            Object obj3 = linkedHashMap.get(name);
            if (obj3 == null) {
                obj3 = (List) new ArrayList();
                linkedHashMap.put(name, obj3);
            }
            ((List) obj3).add(obj2);
        }
        for (Map.Entry entry : linkedHashMap.entrySet()) {
            Object key = entry.getKey();
            Intrinsics.checkNotNullExpressionValue(key, "component1(...)");
            Name name2 = (Name) key;
            List list2 = (List) entry.getValue();
            LinkedHashMap linkedHashMap2 = new LinkedHashMap();
            for (Object obj4 : list2) {
                Boolean boolValueOf = Boolean.valueOf(((CallableMemberDescriptor) obj4) instanceof FunctionDescriptor);
                Object obj5 = linkedHashMap2.get(boolValueOf);
                if (obj5 == null) {
                    obj5 = (List) new ArrayList();
                    linkedHashMap2.put(boolValueOf, obj5);
                }
                ((List) obj5).add(obj4);
            }
            for (Map.Entry entry2 : linkedHashMap2.entrySet()) {
                boolean zBooleanValue = ((Boolean) entry2.getKey()).booleanValue();
                List list3 = (List) entry2.getValue();
                OverridingUtil overridingUtil = OverridingUtil.DEFAULT;
                List list4 = list3;
                if (!zBooleanValue) {
                    arrayListEmptyList = CollectionsKt.emptyList();
                } else {
                    ArrayList arrayList4 = new ArrayList();
                    for (Object obj6 : list) {
                        if (Intrinsics.areEqual(((FunctionDescriptor) obj6).getName(), name2)) {
                            arrayList4.add(obj6);
                        }
                    }
                    arrayListEmptyList = arrayList4;
                }
                overridingUtil.generateOverridesInFunctionGroup(name2, list4, arrayListEmptyList, this.containingClass, new NonReportingOverrideStrategy() { // from class: kotlin.reflect.jvm.internal.impl.resolve.scopes.GivenFunctionsMemberScope.createFakeOverrides.4
                    @Override // kotlin.reflect.jvm.internal.impl.resolve.OverridingStrategy
                    public void addFakeOverride(CallableMemberDescriptor fakeOverride) {
                        Intrinsics.checkNotNullParameter(fakeOverride, "fakeOverride");
                        OverridingUtil.resolveUnknownVisibilityForMember(fakeOverride, null);
                        arrayList.add(fakeOverride);
                    }

                    @Override // kotlin.reflect.jvm.internal.impl.resolve.NonReportingOverrideStrategy
                    protected void conflict(CallableMemberDescriptor fromSuper, CallableMemberDescriptor fromCurrent) {
                        Intrinsics.checkNotNullParameter(fromSuper, "fromSuper");
                        Intrinsics.checkNotNullParameter(fromCurrent, "fromCurrent");
                        throw new IllegalStateException(("Conflict in scope of " + this.getContainingClass() + ": " + fromSuper + " vs " + fromCurrent).toString());
                    }
                });
            }
        }
        return kotlin.reflect.jvm.internal.impl.utils.CollectionsKt.compact(arrayList);
    }
}
