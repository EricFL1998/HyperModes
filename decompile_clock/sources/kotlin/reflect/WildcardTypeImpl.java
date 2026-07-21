package kotlin.reflect;

import com.android.deskclock.settings.SearchContract;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: TypesJVM.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0003\u0018\u0000 \u00152\u00020\u00012\u00020\u0002:\u0001\u0015B\u001b\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004¢\u0006\u0004\b\u0006\u0010\u0007J\u0013\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00040\tH\u0016¢\u0006\u0002\u0010\nJ\u0013\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00040\tH\u0016¢\u0006\u0002\u0010\nJ\b\u0010\f\u001a\u00020\rH\u0016J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0096\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\rH\u0016R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0016"}, d2 = {"Lkotlin/reflect/WildcardTypeImpl;", "Ljava/lang/reflect/WildcardType;", "Lkotlin/reflect/TypeImpl;", "upperBound", "Ljava/lang/reflect/Type;", "lowerBound", "<init>", "(Ljava/lang/reflect/Type;Ljava/lang/reflect/Type;)V", "getUpperBounds", "", "()[Ljava/lang/reflect/Type;", "getLowerBounds", "getTypeName", "", "equals", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "", "hashCode", "", "toString", "Companion", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
final class WildcardTypeImpl implements WildcardType, TypeImpl {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final WildcardTypeImpl STAR = new WildcardTypeImpl(null, null);
    private final Type lowerBound;
    private final Type upperBound;

    public WildcardTypeImpl(Type type, Type type2) {
        this.upperBound = type;
        this.lowerBound = type2;
    }

    @Override // java.lang.reflect.WildcardType
    public Type[] getUpperBounds() {
        Type[] typeArr = new Type[1];
        Class cls = this.upperBound;
        if (cls == null) {
        }
        typeArr[0] = cls;
        return typeArr;
    }

    @Override // java.lang.reflect.WildcardType
    public Type[] getLowerBounds() {
        Type type = this.lowerBound;
        return type == null ? new Type[0] : new Type[]{type};
    }

    @Override // java.lang.reflect.Type, kotlin.reflect.TypeImpl
    public String getTypeName() {
        if (this.lowerBound != null) {
            return "? super " + TypesJVMKt.typeToString(this.lowerBound);
        }
        Type type = this.upperBound;
        if (type != null && !Intrinsics.areEqual(type, Object.class)) {
            return "? extends " + TypesJVMKt.typeToString(this.upperBound);
        }
        return "?";
    }

    public boolean equals(Object other) {
        if (other instanceof WildcardType) {
            WildcardType wildcardType = (WildcardType) other;
            if (Arrays.equals(getUpperBounds(), wildcardType.getUpperBounds()) && Arrays.equals(getLowerBounds(), wildcardType.getLowerBounds())) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return Arrays.hashCode(getUpperBounds()) ^ Arrays.hashCode(getLowerBounds());
    }

    public String toString() {
        return getTypeName();
    }

    /* JADX INFO: compiled from: TypesJVM.kt */
    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lkotlin/reflect/WildcardTypeImpl$Companion;", "", "<init>", "()V", "STAR", "Lkotlin/reflect/WildcardTypeImpl;", "getSTAR", "()Lkotlin/reflect/WildcardTypeImpl;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final WildcardTypeImpl getSTAR() {
            return WildcardTypeImpl.STAR;
        }
    }
}
