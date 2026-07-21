package kotlin.reflect.jvm.internal.impl.resolve.scopes.receivers;

import kotlin.reflect.jvm.internal.impl.descriptors.ClassDescriptor;

/* JADX INFO: compiled from: ImplicitClassReceiver.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ThisClassReceiver extends ReceiverValue {
    ClassDescriptor getClassDescriptor();
}
