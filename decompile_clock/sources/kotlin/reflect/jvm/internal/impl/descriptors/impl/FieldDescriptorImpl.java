package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.descriptors.FieldDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.PropertyDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotatedImpl;
import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotations;

/* JADX INFO: compiled from: FieldDescriptorImpl.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class FieldDescriptorImpl extends AnnotatedImpl implements FieldDescriptor {
    private final PropertyDescriptor correspondingProperty;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public FieldDescriptorImpl(Annotations annotations, PropertyDescriptor correspondingProperty) {
        super(annotations);
        Intrinsics.checkNotNullParameter(annotations, "annotations");
        Intrinsics.checkNotNullParameter(correspondingProperty, "correspondingProperty");
        this.correspondingProperty = correspondingProperty;
    }
}
