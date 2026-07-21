package kotlin.reflect.full;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

/* JADX INFO: compiled from: exceptions.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000\u0010\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0007\u0018\u00002\u00060\u0002j\u0002`\u0001B\u0019\u0012\u0010\b\u0002\u0010\u0003\u001a\n\u0018\u00010\u0002j\u0004\u0018\u0001`\u0001¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"}, d2 = {"Lkotlin/reflect/full/NoSuchPropertyException;", "Lkotlin/Exception;", "Ljava/lang/Exception;", "cause", "<init>", "(Ljava/lang/Exception;)V", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public final class NoSuchPropertyException extends Exception {
    public NoSuchPropertyException() {
        this(null, 1, 0 == true ? 1 : 0);
    }

    public NoSuchPropertyException(Exception exc) {
        super(exc);
    }

    public /* synthetic */ NoSuchPropertyException(Exception exc, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? null : exc);
    }
}
