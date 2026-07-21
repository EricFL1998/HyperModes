package kotlin.reflect.jvm.internal.impl.descriptors.runtime.components;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.text.StringsKt;
import kotlin.text.Typography;

/* JADX INFO: compiled from: ReflectKotlinClassFinder.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectKotlinClassFinderKt {
    /* JADX INFO: Access modifiers changed from: private */
    public static final String toRuntimeFqName(ClassId classId) {
        String strAsString = classId.getRelativeClassName().asString();
        Intrinsics.checkNotNullExpressionValue(strAsString, "asString(...)");
        String strReplace$default = StringsKt.replace$default(strAsString, '.', Typography.dollar, false, 4, (Object) null);
        return classId.getPackageFqName().isRoot() ? strReplace$default : classId.getPackageFqName() + '.' + strReplace$default;
    }
}
