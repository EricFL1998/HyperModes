package kotlin.reflect.full;

import com.android.deskclock.settings.SearchContract;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.KType;
import kotlin.reflect.jvm.internal.KTypeImpl;
import kotlin.reflect.jvm.internal.impl.types.typeUtil.TypeUtilsKt;

/* JADX INFO: compiled from: KTypes.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\u001a\u0014\u0010\u0000\u001a\u00020\u0001*\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u001a\u0014\u0010\u0004\u001a\u00020\u0003*\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0007\u001a\u0014\u0010\u0006\u001a\u00020\u0003*\u00020\u00012\u0006\u0010\u0005\u001a\u00020\u0001H\u0007¨\u0006\u0007"}, d2 = {"withNullability", "Lkotlin/reflect/KType;", "nullable", "", "isSubtypeOf", SearchContract.SearchResultColumn.COLUMN_OTHER, "isSupertypeOf", "kotlin-reflection"}, k = 2, mv = {2, 0, 0}, xi = 48)
public final class KTypes {
    public static final KType withNullability(KType kType, boolean z) {
        Intrinsics.checkNotNullParameter(kType, "<this>");
        return ((KTypeImpl) kType).makeNullableAsSpecified$kotlin_reflection(z);
    }

    public static final boolean isSubtypeOf(KType kType, KType other) {
        Intrinsics.checkNotNullParameter(kType, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return TypeUtilsKt.isSubtypeOf(((KTypeImpl) kType).getType(), ((KTypeImpl) other).getType());
    }

    public static final boolean isSupertypeOf(KType kType, KType other) {
        Intrinsics.checkNotNullParameter(kType, "<this>");
        Intrinsics.checkNotNullParameter(other, "other");
        return isSubtypeOf(other, kType);
    }
}
