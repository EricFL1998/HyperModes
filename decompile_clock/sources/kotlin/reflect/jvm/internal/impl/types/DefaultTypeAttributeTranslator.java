package kotlin.reflect.jvm.internal.impl.types;

import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.DeclarationDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;

/* JADX INFO: compiled from: TypeAttributeTranslator.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class DefaultTypeAttributeTranslator implements TypeAttributeTranslator {
    public static final DefaultTypeAttributeTranslator INSTANCE = new DefaultTypeAttributeTranslator();

    private DefaultTypeAttributeTranslator() {
    }

    @Override // kotlin.reflect.jvm.internal.impl.types.TypeAttributeTranslator
    public TypeAttributes toAttributes(Annotations annotations, TypeConstructor typeConstructor, DeclarationDescriptor declarationDescriptor) {
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        if (annotations.isEmpty()) {
            return TypeAttributes.Companion.getEmpty();
        }
        return TypeAttributes.Companion.create(CollectionsKt.listOf(new AnnotationsTypeAttribute(annotations)));
    }
}
