package kotlin.reflect.jvm.internal.impl.load.java.components;

import java.util.Map;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames;
import kotlin.reflect.jvm.internal.impl.load.java.lazy.LazyJavaResolverContext;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotation;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationArgument;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaArrayAnnotationArgument;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaEnumValueAnnotationArgument;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.reflect.jvm.internal.impl.resolve.constants.ConstantValue;
import kotlin.reflect.jvm.internal.impl.storage.NotNullLazyValue;
import kotlin.reflect.jvm.internal.impl.storage.StorageKt;

/* JADX INFO: compiled from: JavaAnnotationMapper.kt */
/* JADX INFO: loaded from: classes2.dex */
public final class JavaTargetAnnotationDescriptor extends JavaAnnotationDescriptor {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(JavaTargetAnnotationDescriptor.class), "allValueArguments", "getAllValueArguments()Ljava/util/Map;"))};
    private final NotNullLazyValue allValueArguments$delegate;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public JavaTargetAnnotationDescriptor(JavaAnnotation annotation, LazyJavaResolverContext c) {
        super(c, annotation, StandardNames.FqNames.target);
        Intrinsics.checkNotNullParameter(annotation, "annotation");
        Intrinsics.checkNotNullParameter(c, "c");
        this.allValueArguments$delegate = c.getStorageManager().createLazyValue(new Function0(this) { // from class: kotlin.reflect.jvm.internal.impl.load.java.components.JavaTargetAnnotationDescriptor$$Lambda$0
            private final JavaTargetAnnotationDescriptor arg$0;

            {
                this.arg$0 = this;
            }

            @Override // kotlin.jvm.functions.Function0
            public Object invoke() {
                return JavaTargetAnnotationDescriptor.allValueArguments_delegate$lambda$1(this.arg$0);
            }
        });
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.components.JavaAnnotationDescriptor, kotlin.reflect.jvm.internal.impl.descriptors.annotations.AnnotationDescriptor
    public Map<Name, ConstantValue<Object>> getAllValueArguments() {
        return (Map) StorageKt.getValue(this.allValueArguments$delegate, this, (KProperty<?>) $$delegatedProperties[0]);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final Map allValueArguments_delegate$lambda$1(JavaTargetAnnotationDescriptor this$0) {
        ConstantValue<?> constantValueMapJavaTargetArguments$descriptors_jvm;
        Intrinsics.checkNotNullParameter(this$0, "this$0");
        JavaAnnotationArgument firstArgument = this$0.getFirstArgument();
        if (firstArgument instanceof JavaArrayAnnotationArgument) {
            constantValueMapJavaTargetArguments$descriptors_jvm = JavaAnnotationTargetMapper.INSTANCE.mapJavaTargetArguments$descriptors_jvm(((JavaArrayAnnotationArgument) this$0.getFirstArgument()).getElements());
        } else {
            constantValueMapJavaTargetArguments$descriptors_jvm = firstArgument instanceof JavaEnumValueAnnotationArgument ? JavaAnnotationTargetMapper.INSTANCE.mapJavaTargetArguments$descriptors_jvm(CollectionsKt.listOf(this$0.getFirstArgument())) : null;
        }
        Map mapMapOf = constantValueMapJavaTargetArguments$descriptors_jvm != null ? MapsKt.mapOf(TuplesKt.to(JavaAnnotationMapper.INSTANCE.getTARGET_ANNOTATION_ALLOWED_TARGETS$descriptors_jvm(), constantValueMapJavaTargetArguments$descriptors_jvm)) : null;
        return mapMapOf == null ? MapsKt.emptyMap() : mapMapOf;
    }
}
