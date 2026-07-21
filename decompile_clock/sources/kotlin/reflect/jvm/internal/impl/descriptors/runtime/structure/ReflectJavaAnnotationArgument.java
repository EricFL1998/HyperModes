package kotlin.reflect.jvm.internal.impl.descriptors.runtime.structure;

import java.lang.annotation.Annotation;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationArgument;
import kotlin.reflect.jvm.internal.impl.name.Name;

/* JADX INFO: compiled from: ReflectJavaAnnotationArguments.kt */
/* JADX INFO: loaded from: classes2.dex */
public abstract class ReflectJavaAnnotationArgument implements JavaAnnotationArgument {
    public static final Factory Factory = new Factory(null);
    private final Name name;

    public /* synthetic */ ReflectJavaAnnotationArgument(Name name, DefaultConstructorMarker defaultConstructorMarker) {
        this(name);
    }

    private ReflectJavaAnnotationArgument(Name name) {
        this.name = name;
    }

    @Override // kotlin.reflect.jvm.internal.impl.load.java.structure.JavaAnnotationArgument
    public Name getName() {
        return this.name;
    }

    /* JADX INFO: compiled from: ReflectJavaAnnotationArguments.kt */
    public static final class Factory {
        public /* synthetic */ Factory(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Factory() {
        }

        public final ReflectJavaAnnotationArgument create(Object value, Name name) {
            Intrinsics.checkNotNullParameter(value, "value");
            if (ReflectClassUtilKt.isEnumClassOrSpecializedEnumEntryClass(value.getClass())) {
                return new ReflectJavaEnumValueAnnotationArgument(name, (Enum) value);
            }
            if (value instanceof Annotation) {
                return new ReflectJavaAnnotationAsAnnotationArgument(name, (Annotation) value);
            }
            if (value instanceof Object[]) {
                return new ReflectJavaArrayAnnotationArgument(name, (Object[]) value);
            }
            return value instanceof Class ? new ReflectJavaClassObjectAnnotationArgument(name, (Class) value) : new ReflectJavaLiteralAnnotationArgument(name, value);
        }
    }
}
