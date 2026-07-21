package kotlin.reflect.jvm.internal.impl.types.error;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.builtins.DefaultBuiltIns;
import kotlin.reflect.jvm.internal.impl.builtins.KotlinBuiltIns;
import kotlin.reflect.jvm.internal.impl.descriptors.ClassifierDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.TypeParameterDescriptor;
import kotlin.reflect.jvm.internal.impl.types.KotlinType;
import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.checker.KotlinTypeRefiner;

/* JADX INFO: compiled from: ErrorTypeConstructor.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ErrorTypeConstructor implements TypeConstructor {
    private final String debugText;
    private final String[] formatParams;
    private final ErrorTypeKind kind;

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public boolean isDenotable() {
        return false;
    }

    public ErrorTypeConstructor(ErrorTypeKind kind, String... formatParams) {
        Intrinsics.checkNotNullParameter(kind, "kind");
        Intrinsics.checkNotNullParameter(formatParams, "formatParams");
        this.kind = kind;
        this.formatParams = formatParams;
        String debugText = ErrorEntity.ERROR_TYPE.getDebugText();
        String debugMessage = kind.getDebugMessage();
        Object[] objArrCopyOf = Arrays.copyOf(formatParams, formatParams.length);
        String str = String.format(debugMessage, Arrays.copyOf(objArrCopyOf, objArrCopyOf.length));
        Intrinsics.checkNotNullExpressionValue(str, "format(...)");
        String str2 = String.format(debugText, Arrays.copyOf(new Object[]{str}, 1));
        Intrinsics.checkNotNullExpressionValue(str2, "format(...)");
        this.debugText = str2;
    }

    public final ErrorTypeKind getKind() {
        return this.kind;
    }

    public final String getParam(int i) {
        return this.formatParams[i];
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public List<TypeParameterDescriptor> getParameters() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    /* JADX INFO: renamed from: getSupertypes */
    public Collection<KotlinType> mo1492getSupertypes() {
        return CollectionsKt.emptyList();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    /* JADX INFO: renamed from: getDeclarationDescriptor */
    public ClassifierDescriptor mo1491getDeclarationDescriptor() {
        return ErrorUtils.INSTANCE.getErrorClass();
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public KotlinBuiltIns getBuiltIns() {
        return DefaultBuiltIns.Companion.getInstance();
    }

    public String toString() {
        return this.debugText;
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeConstructor
    public TypeConstructor refine(KotlinTypeRefiner kotlinTypeRefiner) {
        Intrinsics.checkNotNullParameter(kotlinTypeRefiner, "kotlinTypeRefiner");
        return this;
    }
}
