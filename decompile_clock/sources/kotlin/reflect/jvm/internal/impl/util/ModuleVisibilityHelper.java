package kotlin.reflect.jvm.internal.impl.util;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;

/* JADX INFO: compiled from: ModuleVisibilityHelper.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface ModuleVisibilityHelper {
    boolean isInFriendModule(DeclarationDescriptor declarationDescriptor, DeclarationDescriptor declarationDescriptor2);

    /* JADX INFO: compiled from: ModuleVisibilityHelper.kt */
    public static final class EMPTY implements ModuleVisibilityHelper {
        public static final EMPTY INSTANCE = new EMPTY();

        @Override // kotlin.reflect.jvm.internal.impl.util.ModuleVisibilityHelper
        public boolean isInFriendModule(DeclarationDescriptor what, DeclarationDescriptor from) {
            Intrinsics.checkNotNullParameter(what, "what");
            Intrinsics.checkNotNullParameter(from, "from");
            return true;
        }

        private EMPTY() {
        }
    }
}
