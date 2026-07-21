package kotlin.reflect.jvm.internal.impl.metadata.jvm.deserialization;

import java.util.List;
import java.util.Set;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.metadata.jvm.JvmProtoBuf;

/* JADX INFO: compiled from: JvmNameResolver.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JvmNameResolver extends JvmNameResolverBase {
    private final JvmProtoBuf.StringTableTypes types;

    /* JADX WARN: Illegal instructions before constructor call */
    public JvmNameResolver(JvmProtoBuf.StringTableTypes types, String[] strings) {
        Set set;
        Intrinsics.checkNotNullParameter(types, "types");
        Intrinsics.checkNotNullParameter(strings, "strings");
        List<Integer> localNameList = types.getLocalNameList();
        if (localNameList.isEmpty()) {
            set = SetsKt.emptySet();
        } else {
            Intrinsics.checkNotNull(localNameList);
            set = CollectionsKt.toSet(localNameList);
        }
        List<JvmProtoBuf.StringTableTypes.Record> recordList = types.getRecordList();
        Intrinsics.checkNotNullExpressionValue(recordList, "getRecordList(...)");
        super(strings, set, JvmNameResolverKt.toExpandedRecordsList(recordList));
        this.types = types;
    }
}
