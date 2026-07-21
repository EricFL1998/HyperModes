package kotlin.reflect.jvm.internal.impl.utils.addToStdlib;

import java.util.concurrent.ConcurrentHashMap;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: compiled from: addToStdlib.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class AddToStdlibKt {
    private static final ConcurrentHashMap<Function0<?>, Object> constantMap = new ConcurrentHashMap<>();

    public static /* synthetic */ Void shouldNotBeCalled$default(String str, int i, Object obj) {
        if ((i & 1) != 0) {
            str = "should not be called";
        }
        return shouldNotBeCalled(str);
    }

    public static final Void shouldNotBeCalled(String message) {
        Intrinsics.checkNotNullParameter(message, "message");
        throw new IllegalStateException(message.toString());
    }
}
