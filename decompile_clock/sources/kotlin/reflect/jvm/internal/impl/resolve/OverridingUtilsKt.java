package kotlin.reflect.jvm.internal.impl.resolve;

import android.Manifest;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.utils.SmartSet;

/* JADX INFO: compiled from: overridingUtils.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class OverridingUtilsKt {
    /* JADX WARN: Multi-variable type inference failed */
    public static final <H> Collection<H> selectMostSpecificInEachOverridableGroup(Collection<? extends H> collection, Function1<? super H, ? extends CallableDescriptor> descriptorByHandle) {
        Intrinsics.checkNotNullParameter(collection, "<this>");
        Intrinsics.checkNotNullParameter(descriptorByHandle, "descriptorByHandle");
        if (collection.size() <= 1) {
            return collection;
        }
        LinkedList linkedList = new LinkedList(collection);
        SmartSet smartSetCreate = SmartSet.Companion.create();
        while (true) {
            LinkedList linkedList2 = linkedList;
            if (!linkedList2.isEmpty()) {
                Object objFirst = CollectionsKt.first((List<? extends Object>) linkedList);
                final SmartSet smartSetCreate2 = SmartSet.Companion.create();
                Collection<Manifest> collectionExtractMembersOverridableInBothWays = OverridingUtil.extractMembersOverridableInBothWays(objFirst, linkedList2, descriptorByHandle, new Function1(smartSetCreate2) { // from class: kotlin.reflect.jvm.internal.impl.resolve.OverridingUtilsKt$$Lambda$1
                    private final SmartSet arg$0;

                    {
                        this.arg$0 = smartSetCreate2;
                    }

                    @Override // kotlin.jvm.functions.Function1
                    public Object invoke(Object obj) {
                        return OverridingUtilsKt.selectMostSpecificInEachOverridableGroup$lambda$2(this.arg$0, obj);
                    }
                });
                Intrinsics.checkNotNullExpressionValue(collectionExtractMembersOverridableInBothWays, "extractMembersOverridableInBothWays(...)");
                if (collectionExtractMembersOverridableInBothWays.size() == 1 && smartSetCreate2.isEmpty()) {
                    Object objSingle = CollectionsKt.single(collectionExtractMembersOverridableInBothWays);
                    Intrinsics.checkNotNullExpressionValue(objSingle, "single(...)");
                    smartSetCreate.add(objSingle);
                } else {
                    Manifest manifest = (Object) OverridingUtil.selectMostSpecificMember(collectionExtractMembersOverridableInBothWays, descriptorByHandle);
                    CallableDescriptor callableDescriptorInvoke = descriptorByHandle.invoke(manifest);
                    for (Manifest manifest2 : collectionExtractMembersOverridableInBothWays) {
                        Intrinsics.checkNotNull(manifest2);
                        if (!OverridingUtil.isMoreSpecific(callableDescriptorInvoke, descriptorByHandle.invoke(manifest2))) {
                            smartSetCreate2.add(manifest2);
                        }
                    }
                    SmartSet smartSet = smartSetCreate2;
                    if (!smartSet.isEmpty()) {
                        smartSetCreate.addAll(smartSet);
                    }
                    smartSetCreate.add(manifest);
                }
            } else {
                return smartSetCreate;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Unit selectMostSpecificInEachOverridableGroup$lambda$2(SmartSet conflictedHandles, Object obj) {
        Intrinsics.checkNotNullParameter(conflictedHandles, "$conflictedHandles");
        Intrinsics.checkNotNull(obj);
        conflictedHandles.add(obj);
        return Unit.INSTANCE;
    }
}
