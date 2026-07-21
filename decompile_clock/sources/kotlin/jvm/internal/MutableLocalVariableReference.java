package kotlin.jvm.internal;

import com.xiaomi.onetrack.api.g;
import kotlin.KotlinNothingValueException;
import kotlin.Metadata;
import kotlin.reflect.KDeclarationContainer;

/* JADX INFO: compiled from: localVariableReferences.kt */
/* JADX INFO: loaded from: classes2.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0017\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J\n\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0016J\u0012\u0010\b\u001a\u00020\t2\b\u0010\n\u001a\u0004\u0018\u00010\u0007H\u0016¨\u0006\u000b"}, d2 = {"Lkotlin/jvm/internal/MutableLocalVariableReference;", "Lkotlin/jvm/internal/MutablePropertyReference0;", "<init>", "()V", "getOwner", "Lkotlin/reflect/KDeclarationContainer;", "get", "", "set", "", g.p, "kotlin-stdlib"}, k = 1, mv = {2, 1, 0}, xi = 48)
public class MutableLocalVariableReference extends MutablePropertyReference0 {
    @Override // kotlin.jvm.internal.CallableReference
    public KDeclarationContainer getOwner() {
        LocalVariableReferencesKt.notSupportedError();
        throw new KotlinNothingValueException();
    }

    @Override // kotlin.reflect.KProperty0
    public Object get() {
        LocalVariableReferencesKt.notSupportedError();
        throw new KotlinNothingValueException();
    }

    @Override // kotlin.reflect.KMutableProperty0
    public void set(Object value) {
        LocalVariableReferencesKt.notSupportedError();
        throw new KotlinNothingValueException();
    }
}
