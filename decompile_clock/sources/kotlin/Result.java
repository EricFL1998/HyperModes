package kotlin;

import com.android.deskclock.settings.SearchContract;
import com.xiaomi.onetrack.api.g;
import java.io.Serializable;
import kotlin.jvm.JvmInline;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Result.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0003\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0003\b\u0087@\u0018\u0000 \u001e*\u0006\b\u0000\u0010\u0001 \u00012\u00060\u0002j\u0002`\u0003:\u0002\u001e\u001fB\u0013\b\u0001\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0012\u0010\u0010\u001a\u0004\u0018\u00018\u0000H\u0087\b¢\u0006\u0004\b\u0011\u0010\u0007J\u000f\u0010\u0012\u001a\u0004\u0018\u00010\u0013¢\u0006\u0004\b\u0014\u0010\u0015J\u000f\u0010\u0016\u001a\u00020\u0017H\u0016¢\u0006\u0004\b\u0018\u0010\u0019J\u0013\u0010\u001a\u001a\u00020\u000b2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0005HÖ\u0003J\t\u0010\u001c\u001a\u00020\u001dHÖ\u0001R\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0000X\u0081\u0004¢\u0006\b\n\u0000\u0012\u0004\b\b\u0010\tR\u0011\u0010\n\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000b8F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\r\u0088\u0001\u0004\u0092\u0001\u0004\u0018\u00010\u0005¨\u0006 "}, d2 = {"Lkotlin/Result;", "T", "Ljava/io/Serializable;", "Lkotlin/io/Serializable;", g.p, "", "constructor-impl", "(Ljava/lang/Object;)Ljava/lang/Object;", "getValue$annotations", "()V", "isSuccess", "", "isSuccess-impl", "(Ljava/lang/Object;)Z", "isFailure", "isFailure-impl", "getOrNull", "getOrNull-impl", "exceptionOrNull", "", "exceptionOrNull-impl", "(Ljava/lang/Object;)Ljava/lang/Throwable;", "toString", "", "toString-impl", "(Ljava/lang/Object;)Ljava/lang/String;", "equals", SearchContract.SearchResultColumn.COLUMN_OTHER, "hashCode", "", "Companion", "Failure", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
@JvmInline
public final class Result<T> implements Serializable {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private final Object value;

    /* JADX INFO: renamed from: box-impl, reason: not valid java name */
    public static final /* synthetic */ Result m203boximpl(Object obj) {
        return new Result(obj);
    }

    /* JADX INFO: renamed from: constructor-impl, reason: not valid java name */
    public static <T> Object m204constructorimpl(Object obj) {
        return obj;
    }

    /* JADX INFO: renamed from: equals-impl, reason: not valid java name */
    public static boolean m205equalsimpl(Object obj, Object obj2) {
        return (obj2 instanceof Result) && Intrinsics.areEqual(obj, ((Result) obj2).getValue());
    }

    /* JADX INFO: renamed from: equals-impl0, reason: not valid java name */
    public static final boolean m206equalsimpl0(Object obj, Object obj2) {
        return Intrinsics.areEqual(obj, obj2);
    }

    public static /* synthetic */ void getValue$annotations() {
    }

    /* JADX INFO: renamed from: hashCode-impl, reason: not valid java name */
    public static int m209hashCodeimpl(Object obj) {
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public boolean equals(Object other) {
        return m205equalsimpl(this.value, other);
    }

    public int hashCode() {
        return m209hashCodeimpl(this.value);
    }

    /* JADX INFO: renamed from: unbox-impl, reason: not valid java name and from getter */
    public final /* synthetic */ Object getValue() {
        return this.value;
    }

    private /* synthetic */ Result(Object obj) {
        this.value = obj;
    }

    /* JADX INFO: renamed from: isSuccess-impl, reason: not valid java name */
    public static final boolean m211isSuccessimpl(Object obj) {
        return !(obj instanceof Failure);
    }

    /* JADX INFO: renamed from: isFailure-impl, reason: not valid java name */
    public static final boolean m210isFailureimpl(Object obj) {
        return obj instanceof Failure;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX INFO: renamed from: getOrNull-impl, reason: not valid java name */
    private static final T m208getOrNullimpl(Object obj) {
        if (m210isFailureimpl(obj)) {
            return null;
        }
        return obj;
    }

    /* JADX INFO: renamed from: exceptionOrNull-impl, reason: not valid java name */
    public static final Throwable m207exceptionOrNullimpl(Object obj) {
        if (obj instanceof Failure) {
            return ((Failure) obj).exception;
        }
        return null;
    }

    public String toString() {
        return m212toStringimpl(this.value);
    }

    /* JADX INFO: renamed from: toString-impl, reason: not valid java name */
    public static String m212toStringimpl(Object obj) {
        return obj instanceof Failure ? ((Failure) obj).toString() : "Success(" + obj + ')';
    }

    /* JADX INFO: compiled from: Result.kt */
    @Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0003\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\"\u0010\u0004\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0005\"\u0004\b\u0001\u0010\u00062\u0006\u0010\u0007\u001a\u0002H\u0006H\u0087\b¢\u0006\u0002\u0010\bJ\"\u0010\t\u001a\b\u0012\u0004\u0012\u0002H\u00060\u0005\"\u0004\b\u0001\u0010\u00062\u0006\u0010\n\u001a\u00020\u000bH\u0087\b¢\u0006\u0002\u0010\f¨\u0006\r"}, d2 = {"Lkotlin/Result$Companion;", "", "<init>", "()V", "success", "Lkotlin/Result;", "T", g.p, "(Ljava/lang/Object;)Ljava/lang/Object;", "failure", "exception", "", "(Ljava/lang/Throwable;)Ljava/lang/Object;", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        private final <T> Object success(T value) {
            return Result.m204constructorimpl(value);
        }

        private final <T> Object failure(Throwable exception) {
            Intrinsics.checkNotNullParameter(exception, "exception");
            return Result.m204constructorimpl(ResultKt.createFailure(exception));
        }
    }

    /* JADX INFO: compiled from: Result.kt */
    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0000\u0018\u00002\u00060\u0001j\u0002`\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0004\b\u0005\u0010\u0006J\u0013\u0010\u0007\u001a\u00020\b2\b\u0010\t\u001a\u0004\u0018\u00010\nH\u0096\u0002J\b\u0010\u000b\u001a\u00020\fH\u0016J\b\u0010\r\u001a\u00020\u000eH\u0016R\u0010\u0010\u0003\u001a\u00020\u00048\u0006X\u0087\u0004¢\u0006\u0002\n\u0000¨\u0006\u000f"}, d2 = {"Lkotlin/Result$Failure;", "Ljava/io/Serializable;", "Lkotlin/io/Serializable;", "exception", "", "<init>", "(Ljava/lang/Throwable;)V", "equals", "", SearchContract.SearchResultColumn.COLUMN_OTHER, "", "hashCode", "", "toString", "", "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
    public static final class Failure implements Serializable {
        public final Throwable exception;

        public Failure(Throwable exception) {
            Intrinsics.checkNotNullParameter(exception, "exception");
            this.exception = exception;
        }

        public boolean equals(Object other) {
            return (other instanceof Failure) && Intrinsics.areEqual(this.exception, ((Failure) other).exception);
        }

        public int hashCode() {
            return this.exception.hashCode();
        }

        public String toString() {
            return "Failure(" + this.exception + ')';
        }
    }
}
