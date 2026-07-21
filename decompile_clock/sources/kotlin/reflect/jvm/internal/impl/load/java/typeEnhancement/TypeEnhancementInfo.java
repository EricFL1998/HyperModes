package kotlin.reflect.jvm.internal.impl.load.java.typeEnhancement;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: predefinedEnhancementInfo.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class TypeEnhancementInfo {
    private final Map<Integer, JavaTypeQualifiers> map;

    public TypeEnhancementInfo(Map<Integer, JavaTypeQualifiers> map) {
        Intrinsics.checkNotNullParameter(map, "map");
        this.map = map;
    }

    public final Map<Integer, JavaTypeQualifiers> getMap() {
        return this.map;
    }
}
