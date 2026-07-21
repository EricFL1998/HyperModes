package kotlin.reflect.jvm.internal.impl.descriptors.impl;

import kotlin.reflect.jvm.internal.impl.descriptors.ClassConstructorDescriptor;
import kotlin.reflect.jvm.internal.impl.descriptors.ConstructorDescriptor;

/* JADX INFO: compiled from: TypeAliasConstructorDescriptor.kt */
/* JADX INFO: loaded from: classes2.dex */
public interface TypeAliasConstructorDescriptor extends ConstructorDescriptor {
    ClassConstructorDescriptor getUnderlyingConstructorDescriptor();
}
