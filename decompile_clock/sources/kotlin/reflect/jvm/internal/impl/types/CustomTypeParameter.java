package kotlin.reflect.jvm.internal.impl.types;

/* JADX INFO: compiled from: TypeCapabilities.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface CustomTypeParameter {
    boolean isTypeParameter();

    KotlinType substitutionResult(KotlinType kotlinType);
}
