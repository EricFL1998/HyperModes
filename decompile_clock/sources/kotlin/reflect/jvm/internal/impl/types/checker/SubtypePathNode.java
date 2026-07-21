package kotlin.reflect.jvm.internal.impl.types.checker;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;

/* JADX INFO: compiled from: utils.kt */
/* JADX INFO: loaded from: classes2.dex */
final class SubtypePathNode {
    private final SubtypePathNode previous;
    private final KotlinType type;

    public SubtypePathNode(KotlinType type, SubtypePathNode subtypePathNode) {
        Intrinsics.checkNotNullParameter(type, "type");
        this.type = type;
        this.previous = subtypePathNode;
    }

    public final SubtypePathNode getPrevious() {
        return this.previous;
    }

    public final KotlinType getType() {
        return this.type;
    }
}
