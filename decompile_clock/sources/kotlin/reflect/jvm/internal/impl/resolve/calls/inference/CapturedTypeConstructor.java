package kotlin.reflect.jvm.internal.impl.resolve.calls.inference;

import kotlin.reflect.jvm.internal.impl.types.TypeConstructor;
import kotlin.reflect.jvm.internal.impl.types.TypeProjection;
import kotlin.reflect.jvm.internal.impl.types.model.CapturedTypeConstructorMarker;

/* JADX INFO: compiled from: CapturedTypeConstructor.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface CapturedTypeConstructor extends TypeConstructor, CapturedTypeConstructorMarker {
    TypeProjection getProjection();
}
