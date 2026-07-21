package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.reflect.Field;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.FunctionReference;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;

/* JADX INFO: compiled from: ReflectJavaClass.kt */
/* JADX INFO: loaded from: classes2.dex */
/* synthetic */ class ReflectJavaClass$fields$2 extends FunctionReference implements Function1<Field, ReflectJavaField> {
    public static final ReflectJavaClass$fields$2 INSTANCE = new ReflectJavaClass$fields$2();

    ReflectJavaClass$fields$2() {
        super(1);
    }

    @Override // kotlin.jvm.internal.CallableReference, kotlin.reflect.KCallable
    public final String getName() {
        return "<init>";
    }

    @Override // kotlin.jvm.internal.CallableReference
    public final KDeclarationContainer getOwner() {
        return Reflection.getOrCreateKotlinClass(ReflectJavaField.class);
    }

    @Override // kotlin.jvm.internal.CallableReference
    public final String getSignature() {
        return "<init>(Ljava/lang/reflect/Field;)V";
    }

    @Override // kotlin.jvm.functions.Function1
    public final ReflectJavaField invoke(Field p0) {
        Intrinsics.checkNotNullParameter(p0, "p0");
        return new ReflectJavaField(p0);
    }
}
