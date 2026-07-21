package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField;

/* JADX INFO: compiled from: ReflectJavaField.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class ReflectJavaField extends ReflectJavaMember implements JavaField {
    private final Field member;

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField
    public boolean getHasConstantNotNullInitializer() {
        return false;
    }

    public ReflectJavaField(Field member) {
        Intrinsics.checkNotNullParameter(member, "member");
        this.member = member;
    }

    @Override // kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure.ReflectJavaMember
    public Field getMember() {
        return this.member;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField
    public boolean isEnumEntry() {
        return getMember().isEnumConstant();
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaField
    public ReflectJavaType getType() {
        ReflectJavaType.Factory factory = ReflectJavaType.Factory;
        Type genericType = getMember().getGenericType();
        Intrinsics.checkNotNullExpressionValue(genericType, "getGenericType(...)");
        return factory.create(genericType);
    }
}
