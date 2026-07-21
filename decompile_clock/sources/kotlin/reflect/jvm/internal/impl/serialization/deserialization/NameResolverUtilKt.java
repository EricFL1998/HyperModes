package kotlin.reflect.jvm.internal.impl.serialization.deserialization;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.NameResolver;
import kotlin.reflect.jvm.internal.impl.name.ClassId;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: NameResolverUtil.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class NameResolverUtilKt {
    public static final ClassId getClassId(NameResolver nameResolver, int i) {
        Intrinsics.checkNotNullParameter(nameResolver, "<this>");
        return ClassId.Companion.fromString(nameResolver.getQualifiedClassName(i), nameResolver.isLocalClassName(i));
    }

    public static final Name getName(NameResolver nameResolver, int i) {
        Intrinsics.checkNotNullParameter(nameResolver, "<this>");
        Name nameGuessByFirstCharacter = Name.guessByFirstCharacter(nameResolver.getString(i));
        Intrinsics.checkNotNullExpressionValue(nameGuessByFirstCharacter, "guessByFirstCharacter(...)");
        return nameGuessByFirstCharacter;
    }
}
