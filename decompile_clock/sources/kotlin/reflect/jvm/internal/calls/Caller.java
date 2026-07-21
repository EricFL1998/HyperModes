package kotlin.reflect.jvm.internal.calls;

import androidx.core.app.NotificationCompat;
import java.lang.reflect.Member;
import java.lang.reflect.Type;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: Caller.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\b`\u0018\u0000*\f\b\u0000\u0010\u0001 \u0001*\u0004\u0018\u00010\u00022\u00020\u0003J\u0019\u0010\u000f\u001a\u00020\u00102\n\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0012H\u0016¢\u0006\u0002\u0010\u0013J\u001b\u0010\u0014\u001a\u0004\u0018\u00010\u00032\n\u0010\u0011\u001a\u0006\u0012\u0002\b\u00030\u0012H&¢\u0006\u0002\u0010\u0015R\u0012\u0010\u0004\u001a\u00028\u0000X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0005\u0010\u0006R\u0012\u0010\u0007\u001a\u00020\bX¦\u0004¢\u0006\u0006\u001a\u0004\b\t\u0010\nR\u0018\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\b0\fX¦\u0004¢\u0006\u0006\u001a\u0004\b\r\u0010\u000e¨\u0006\u0016"}, d2 = {"Lkotlin/reflect/jvm/internal/calls/Caller;", "M", "Ljava/lang/reflect/Member;", "", "member", "getMember", "()Ljava/lang/reflect/Member;", "returnType", "Ljava/lang/reflect/Type;", "getReturnType", "()Ljava/lang/reflect/Type;", "parameterTypes", "", "getParameterTypes", "()Ljava/util/List;", "checkArguments", "", "args", "", "([Ljava/lang/Object;)V", NotificationCompat.CATEGORY_CALL, "([Ljava/lang/Object;)Ljava/lang/Object;", "kotlin-reflection"}, k = 1, mv = {2, 0, 0}, xi = 48)
public interface Caller<M extends Member> {
    Object call(Object[] args);

    /* JADX INFO: renamed from: getMember */
    M mo1483getMember();

    List<Type> getParameterTypes();

    Type getReturnType();

    /* JADX INFO: compiled from: Caller.kt */
    @Metadata(k = 3, mv = {2, 0, 0}, xi = 48)
    public static final class DefaultImpls {
        public static <M extends Member> void checkArguments(Caller<? extends M> caller, Object[] args) {
            Intrinsics.checkNotNullParameter(args, "args");
            if (CallerKt.getArity(caller) != args.length) {
                throw new IllegalArgumentException("Callable expects " + CallerKt.getArity(caller) + " arguments, but " + args.length + " were provided.");
            }
        }
    }
}
