package kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.CallableDescriptor;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: ContextReceiver.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ContextReceiver extends AbstractReceiverValue implements ImplicitContextReceiver {
    private final Name customLabelName;
    private final CallableDescriptor declarationDescriptor;

    public CallableDescriptor getDeclarationDescriptor() {
        return this.declarationDescriptor;
    }

    @Override // kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers.ImplicitContextReceiver
    public Name getCustomLabelName() {
        return this.customLabelName;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public ContextReceiver(CallableDescriptor declarationDescriptor, KotlinType receiverType, Name name, ReceiverValue receiverValue) {
        super(receiverType, receiverValue);
        Intrinsics.checkNotNullParameter(declarationDescriptor, "declarationDescriptor");
        Intrinsics.checkNotNullParameter(receiverType, "receiverType");
        this.declarationDescriptor = declarationDescriptor;
        this.customLabelName = name;
    }

    public String toString() {
        return "Cxt { " + getDeclarationDescriptor() + " }";
    }
}
