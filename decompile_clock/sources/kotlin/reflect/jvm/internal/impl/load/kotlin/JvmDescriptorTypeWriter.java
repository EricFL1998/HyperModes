package kotlin.reflect.jvm.internal.impl.load.kotlin;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.name.Name;
import kotlin.text.StringsKt;

/* JADX INFO: compiled from: typeSignatureMapping.kt */
/* JADX INFO: loaded from: classes2.dex */
public class JvmDescriptorTypeWriter<T> {
    private T jvmCurrentType;
    private int jvmCurrentTypeArrayLevel;
    private final JvmTypeFactory<T> jvmTypeFactory;

    public void writeArrayEnd() {
    }

    public void writeArrayType() {
        if (this.jvmCurrentType == null) {
            this.jvmCurrentTypeArrayLevel++;
        }
    }

    public void writeClass(T objectType) {
        Intrinsics.checkNotNullParameter(objectType, "objectType");
        writeJvmTypeAsIs(objectType);
    }

    protected final void writeJvmTypeAsIs(T type) {
        Intrinsics.checkNotNullParameter(type, "type");
        if (this.jvmCurrentType == null) {
            if (this.jvmCurrentTypeArrayLevel > 0) {
                type = this.jvmTypeFactory.createFromString(StringsKt.repeat("[", this.jvmCurrentTypeArrayLevel) + this.jvmTypeFactory.toString(type));
            }
            this.jvmCurrentType = type;
        }
    }

    public void writeTypeVariable(Name name, T type) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(type, "type");
        writeJvmTypeAsIs(type);
    }
}
